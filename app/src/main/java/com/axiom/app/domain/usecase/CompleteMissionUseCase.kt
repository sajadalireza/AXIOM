package com.axiom.app.domain.usecase

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.core.ai.SystemVoiceEngine
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.ShadowRepository
import com.axiom.app.domain.repository.DungeonRepository
import com.axiom.app.domain.repository.LeagueRepository
import com.axiom.app.domain.repository.AtomicCompletionCommand
import com.axiom.app.domain.repository.AtomicCompletionRepository
import com.axiom.app.domain.completion.AtomicCompletionOutcome
import com.axiom.app.domain.model.XPResult
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * WP-205 — the single completion authority. Structured as three phases:
 *
 *  - PHASE A (pure compute + DataStore reads): resolve all awarded values — XP, level/rank,
 *    skill progression, anomaly roll, shadow, dungeon — WITHOUT touching Room. Reward economics
 *    are byte-for-byte unchanged from the pre-WP-205 path; only WHEN/HOW they're persisted moves.
 *  - PHASE B (atomic): hand the fully-resolved [AtomicCompletionCommand] to
 *    [AtomicCompletionRepository], which commits every core Room write inside ONE
 *    `database.withTransaction` — all-or-nothing, idempotent by receipt key.
 *  - PHASE C (post-commit best-effort): mirror the canonical Room streak back to DataStore,
 *    flip first-mission-done, bump weekly counters, emit ceremonies / system feed / AI reaction /
 *    league submission / analytics. None of these run inside the transaction; a failure here
 *    never invalidates the durable Room commit and a retry never re-awards (receipt gate).
 */
class CompleteMissionUseCase @Inject constructor(
    private val missionRepository: MissionRepository,
    private val skillRepository: SkillRepository,
    private val hunterRepository: HunterRepository,
    private val shadowRepository: ShadowRepository,
    private val dungeonRepository: DungeonRepository,
    private val preferences: AxiomPreferences,
    private val systemVoiceEngine: SystemVoiceEngine,
    private val systemFeedRepository: SystemFeedRepository,
    private val leagueRepository: LeagueRepository,
    private val ceremonyEngine: CeremonyEngine,
    private val atomicCompletionRepository: AtomicCompletionRepository
) {
    suspend operator fun invoke(
        missionId: String,
        actualHours: Float? = null,
        goalSet: Boolean = true,
        gotFeedback: Boolean = true,
        pushedComfortZone: Boolean = true,
        firstWinSessionId: String? = null,
    ): XPResult? {
        val mission = missionRepository.getMissionById(missionId) ?: return null
        if (mission.status == "COMPLETED") return null

        val now = System.currentTimeMillis()

        // ================= PHASE A — pure compute + DataStore reads (NO Room writes) =================
        val rawHours = actualHours?.toDouble() ?: mission.estimatedHours.toDouble()
        val quality = XPEngine.calculateQualityScore(goalSet, gotFeedback, pushedComfortZone)
        val effectiveHours = XPEngine.calculateEffectiveHours(rawHours, quality)

        val completedMission = mission.copy(
            status = "COMPLETED",
            actualHours = rawHours.toFloat(),
            completedAt = now,
            qualityScore = quality,
            effectiveHours = effectiveHours
        )

        val skill = skillRepository.getSkillById(mission.skillId)
        val hunter = hunterRepository.getDirectHunterProfile()

        var result: XPResult? = null
        var updatedHunter: Hunter? = null
        val updatedSkills = mutableListOf<Skill>()
        var unlockedShadow: Shadow? = null
        var updatedDungeon: Dungeon? = null
        val deferredCeremonies = mutableListOf<CeremonyEvent>()
        val deferredMessages = mutableListOf<SystemMessage>()
        var hunterXpAwarded = 0
        var skillXpAwarded = 0L
        var incrementWeekly = false
        var incrementWeeklyRare = false

        if (skill != null && hunter != null) {
            // Multiplier reads the PRE-completion streak (economics unchanged, §3).
            val streakVal = preferences.streakFlow.first()
            val streakMultiplier = when {
                streakVal < 7  -> 1.0f
                streakVal < 14 -> 1.15f
                streakVal < 30 -> 1.30f
                else           -> 1.50f
            }

            val shadows = shadowRepository.getAllShadows().first()
            val xpResult = XPEngine.calculateXPResult(
                mission = mission,
                hunter = hunter,
                skill = skill,
                streakMultiplier = streakMultiplier,
                shadows = shadows
            )

            // --- System Anomaly (variable-reward) — DataStore slot consume hoisted BEFORE txn ---
            var anomalyTier: String? = null
            var anomalyBonusXP = 0
            if (mission.rarity.uppercase() != "REST") {
                val roll = kotlin.random.Random.nextFloat()
                val rolledTier = when {
                    roll < 0.005f -> "CRITICAL"
                    roll < 0.04f  -> "MAJOR"
                    roll < 0.16f  -> "MINOR"
                    else -> null
                }
                if (rolledTier != null && preferences.tryConsumeSystemAnomalySlot()) {
                    anomalyTier = rolledTier
                    val multiplier = when (rolledTier) {
                        "CRITICAL" -> 3.0f
                        "MAJOR"    -> 1.5f
                        else       -> 0.5f
                    }
                    anomalyBonusXP = (xpResult.hunterXPGained * multiplier).toInt().coerceAtLeast(1)
                }
            }
            val totalHunterXPGained = xpResult.hunterXPGained + anomalyBonusXP
            result = xpResult.copy(hunterXPGained = totalHunterXPGained)
            hunterXpAwarded = totalHunterXPGained

            // Skill progression
            val skillGainedXP = xpResult.skillXPGained[skill.id] ?: mission.xpReward.toLong()
            skillXpAwarded = skillGainedXP
            var newSkillXP = skill.currentXP + skillGainedXP
            var newSkillLevel = skill.level
            var nextRankXP = (newSkillLevel * 100L)
            while (newSkillXP >= nextRankXP) {
                newSkillXP -= nextRankXP
                newSkillLevel++
                nextRankXP = (newSkillLevel * 100L)
            }

            val oldSkillTier = XPEngine.getMasteryTier(skill.totalEffectiveHours)
            val updatedRawHours = skill.totalRawHours + rawHours
            val updatedEffectiveHours = skill.totalEffectiveHours + effectiveHours
            val newSkillTier = XPEngine.getMasteryTier(updatedEffectiveHours)

            if (oldSkillTier != newSkillTier) {
                deferredCeremonies.add(
                    CeremonyEvent.RankUp(oldRank = oldSkillTier.label, newRank = newSkillTier.label)
                )
            }

            val wasLocked = !skill.isUnlocked
            val updatedSkill = skill.copy(
                currentXP = newSkillXP,
                level = newSkillLevel,
                rankLabel = newSkillTier.label,
                rankColor = 0xFF1D9E75,
                xpToNextRank = nextRankXP,
                rankProgressPercent = XPEngine.getProgressToNextTier(updatedEffectiveHours),
                isShadowCandidate = false,
                isUnlocked = true,
                totalRawHours = updatedRawHours,
                totalEffectiveHours = updatedEffectiveHours
            )
            updatedSkills.add(updatedSkill)

            val isFa = preferences.languageFlow.first() == "fa"

            // Auto-unlock child skills when parent reaches Practitioner (>= 50 effective hours).
            if (updatedSkill.parentId == null && updatedSkill.totalEffectiveHours >= 50.0) {
                val allSkills = skillRepository.getAllSkills().first()
                val lockedChildren = allSkills.filter { it.parentId == updatedSkill.id && !it.isUnlocked }
                for (child in lockedChildren) {
                    updatedSkills.add(child.copy(isUnlocked = true))
                    val childMsg = if (isFa) {
                        "◈ [ پروتکل سیستم ] ◈ زیربخش '${child.name.uppercase()}' بیدار شد — والد به سطح Practitioner رسید."
                    } else {
                        "◈ [ SYSTEM PROTOCOL ] ◈ SUB-DISCIPLINE '${child.name.uppercase()}' UNLOCKED — Parent has achieved Practitioner tier."
                    }
                    deferredMessages.add(
                        SystemMessage(
                            id = java.util.UUID.randomUUID().toString(),
                            message = childMsg,
                            timestamp = now
                        )
                    )
                }
            }

            if (wasLocked) {
                val unlockedMsg = if (isFa) {
                    "◈ [ پروتکل سیستم ] ◈ گره '${skill.name.uppercase()}' بیدار شد — اولین امتیاز تمرین مأموریت دریافت گردید."
                } else {
                    "◈ [ SYSTEM PROTOCOL ] ◈ NODE '${skill.name.uppercase()}' AWAKENED — first mission XP received."
                }
                deferredMessages.add(
                    SystemMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        message = unlockedMsg,
                        timestamp = now
                    )
                )
            }

            // Hunter progression
            var newHunterXP = hunter.currentXP + totalHunterXPGained
            var newHunterLevel = hunter.level
            var nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            while (newHunterXP >= nextLevelXP && newHunterLevel < 100) {
                newHunterXP -= nextLevelXP
                newHunterLevel++
                nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            }
            if (newHunterLevel >= 100) {
                newHunterLevel = 100
                newHunterXP = 0
                nextLevelXP = XPEngine.xpNeededForLevel(100).toInt()
            }

            val hunterRankLabel = XPEngine.calculateHunterRank(newHunterLevel)
            val hunterRankLabelWithSuffix = if (hunterRankLabel.endsWith("-Rank")) hunterRankLabel else "$hunterRankLabel-Rank"
            updatedHunter = hunter.copy(
                level = newHunterLevel,
                rankLabel = hunterRankLabelWithSuffix,
                totalXP = hunter.totalXP + totalHunterXPGained,
                currentXP = newHunterXP,
                xpToNextLevel = nextLevelXP,
                progressPercent = if (newHunterLevel >= 100) 1.0f else newHunterXP.toFloat() / nextLevelXP.toFloat(),
                rankColor = XPEngine.getRankColor(hunterRankLabel),
                rankGlyph = XPEngine.getGlyphForRank(hunterRankLabel)
            )

            if (anomalyTier != null) {
                deferredCeremonies.add(CeremonyEvent.SystemAnomaly(tier = anomalyTier, bonusXP = anomalyBonusXP))
            }
            if (xpResult.shadowUnlocked != null) {
                unlockedShadow = xpResult.shadowUnlocked
            }

            incrementWeekly = true
            incrementWeeklyRare = mission.rarity.uppercase() in listOf("RARE", "EPIC", "LEGENDARY", "CRITICAL", "SHIELD", "DEPTH")

        } else if (skill == null && hunter != null) {
            // Orphaned mission — linked skill was deleted. Award base XP to the hunter directly.
            val baseXP = mission.xpReward.toLong()
            hunterXpAwarded = baseXP.toInt()
            var newHunterXP = hunter.currentXP + baseXP.toInt()
            var newHunterLevel = hunter.level
            var nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            while (newHunterXP >= nextLevelXP && newHunterLevel < 100) {
                newHunterXP -= nextLevelXP
                newHunterLevel++
                nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            }
            if (newHunterLevel >= 100) { newHunterLevel = 100; newHunterXP = 0 }

            val hunterRankLabel = XPEngine.calculateHunterRank(newHunterLevel)
            val hunterRankSuffix = if (hunterRankLabel.endsWith("-Rank")) hunterRankLabel else "$hunterRankLabel-Rank"
            updatedHunter = hunter.copy(
                level = newHunterLevel,
                rankLabel = hunterRankSuffix,
                totalXP = hunter.totalXP + baseXP,
                currentXP = newHunterXP,
                xpToNextLevel = nextLevelXP,
                progressPercent = if (newHunterLevel >= 100) 1f else newHunterXP.toFloat() / nextLevelXP,
                rankColor = XPEngine.getRankColor(hunterRankLabel),
                rankGlyph = XPEngine.getGlyphForRank(hunterRankLabel)
            )
            result = XPResult(
                missionId = missionId,
                hunterXPGained = baseXP.toInt(),
                skillXPGained = emptyMap(),
                skillLeveledUp = emptyMap(),
                leveledUp = newHunterLevel > hunter.level,
                newLevel = if (newHunterLevel > hunter.level) newHunterLevel else null,
                rankChanged = hunterRankSuffix != hunter.rankLabel,
                newRank = if (hunterRankSuffix != hunter.rankLabel) hunterRankSuffix else null,
                shadowUnlocked = null,
                shadowMultiplier = 1f
            )
        }

        // Dungeon stage advance (compute only).
        if (mission.dungeonId != null) {
            val dungeon = dungeonRepository.getDungeonById(mission.dungeonId)
            if (dungeon != null) {
                val newCompletedStages = (dungeon.completedStages + 1).coerceAtMost(dungeon.totalStages)
                val isBossDefeatedNow = newCompletedStages == dungeon.totalStages
                updatedDungeon = dungeon.copy(
                    completedStages = newCompletedStages,
                    isBossDefeated = isBossDefeatedNow,
                    completedAt = if (isBossDefeatedNow) now else null
                )
            }
        }

        // First-win detection + idempotency key (§6). First-Win uses a hunter-scoped key so a
        // re-onboarded mission still collides; generic completions use a stable mission key so a
        // retry of the SAME mission re-awards nothing while different missions stay independent.
        val isFirstWin = hunter != null && !preferences.firstMissionDoneFlow.first()
        val idempotencyKey = if (isFirstWin) {
            "first-win:first-mission:${hunter!!.id}"
        } else {
            "completion:mission:$missionId"
        }

        // DataStore streak snapshot → reconciled into the canonical Room streak inside the txn (§9).
        val streakBaselineCurrent = preferences.streakFlow.first()
        val streakBaselineLongest = preferences.longestStreakFlow.first()
        val streakLastActivityMillis = preferences.lastCompleteTimestampFlow.first()

        // WP-206 Decision D — immutable consent snapshot read BEFORE the atomic transaction. If
        // DECLINED, the first-win causal analytics row is not enqueued (the ACID write itself is
        // unchanged; consent is never read inside withTransaction).
        val analyticsCollectionAllowed = com.axiom.app.domain.analytics.ConsentDecisionEngine
            .shouldEnqueue(preferences.analyticsConsentStateOnce())

        // ================= PHASE B — single atomic Room transaction =================
        val commit = atomicCompletionRepository.commit(
            AtomicCompletionCommand(
                idempotencyKey = idempotencyKey,
                isFirstWin = isFirstWin,
                completedMission = completedMission,
                updatedHunter = updatedHunter,
                updatedSkills = updatedSkills.toList(),
                unlockedShadow = unlockedShadow,
                updatedDungeon = updatedDungeon,
                streakBaselineCurrent = streakBaselineCurrent,
                streakBaselineLongest = streakBaselineLongest,
                streakLastActivityMillis = streakLastActivityMillis,
                nowMillis = now,
                hunterXpAwarded = hunterXpAwarded,
                skillXpAwarded = skillXpAwarded,
                sessionId = com.axiom.app.domain.firstwin.FirstWinCompletionLink.resolveSessionId(
                    isFirstWin = isFirstWin,
                    candidateSessionId = firstWinSessionId,
                ),
                analyticsCollectionAllowed = analyticsCollectionAllowed
            )
        )

        // Duplicate first-win → nothing was re-awarded and Room is unchanged (§6/§11). Still
        // reconcile the DataStore mirrors (§10/§19): a prior completion committed to Room but its
        // post-commit mirror may have failed, leaving DataStore stale. Re-flipping first-mission-done
        // and mirroring the echoed streak is idempotent and awards nothing — it only heals the mirror.
        if (commit.outcome == AtomicCompletionOutcome.ALREADY_RECORDED) {
            if (isFirstWin) {
                try { preferences.setFirstMissionDone(true) } catch (e: Exception) { }
                try {
                    preferences.mirrorCompletionStreak(
                        streak = commit.resultingStreak,
                        longest = commit.resultingLongestStreak,
                        lastCompleteMillis = commit.lastActivityMillis
                    )
                } catch (e: Exception) { }
            }
            return null
        }

        // ================= PHASE C — post-commit best-effort (NEVER inside the txn) =================
        // Mirror the canonical Room streak into DataStore for legacy readers (§8/§10).
        try {
            preferences.mirrorCompletionStreak(
                streak = commit.resultingStreak,
                longest = commit.resultingLongestStreak,
                lastCompleteMillis = commit.lastActivityMillis
            )
        } catch (e: Exception) { /* mirror failure never invalidates the Room commit */ }

        if (isFirstWin) {
            try { preferences.setFirstMissionDone(true) } catch (e: Exception) { }
        }
        if (incrementWeekly) {
            try {
                preferences.incrementWeeklyMissions()
                if (incrementWeeklyRare) preferences.incrementWeeklyRare()
            } catch (e: Exception) { }
        }

        // Post-commit, best-effort like every other Phase-C side effect: a throw from a ceremony
        // or system-message sink must not discard the already-durable XPResult (§5/§13).
        try { deferredCeremonies.forEach { ceremonyEngine.emit(it) } } catch (e: Exception) { }
        try { deferredMessages.forEach { systemFeedRepository.emitMessage(it) } } catch (e: Exception) { }

        if (result != null && hunter != null) {
            try {
                val currentStreak = commit.resultingStreak
                val reaction = systemVoiceEngine.generateCompletionReaction(
                    hunter = hunter,
                    streakDays = currentStreak,
                    mission = completedMission,
                    xpGained = result.hunterXPGained.toInt()
                )
                systemFeedRepository.emitMessage(
                    SystemMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        message = reaction,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                // AI reaction is non-critical — ignore any issues
            }

            try {
                preferences.addLeaguePoints(result.hunterXPGained)
                val finalHunter = hunterRepository.getDirectHunterProfile() ?: hunter
                leagueRepository.submitScore(
                    rarity = mission.rarity,
                    xp = result.hunterXPGained,
                    hunterName = finalHunter.name,
                    hunterRank = finalHunter.rankLabel
                )
            } catch (e: Exception) {
                // League submission is additive — offline or failure must never block local logic
            }
        }

        if (result != null) {
            com.axiom.app.core.AnalyticsLogger.log(
                "mission_completed",
                mapOf("rarity" to mission.rarity, "xp_gained" to result.hunterXPGained, "leveled_up" to result.leveledUp)
            )
        }

        return result
    }
}
