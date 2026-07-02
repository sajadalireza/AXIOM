package com.axiom.app.domain.usecase

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.core.ai.SystemVoiceEngine
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.ShadowRepository
import com.axiom.app.domain.repository.DungeonRepository
import com.axiom.app.domain.repository.LeagueRepository
import com.axiom.app.domain.model.XPResult
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import kotlinx.coroutines.flow.first
import javax.inject.Inject

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
    private val ceremonyEngine: CeremonyEngine
) {
    suspend operator fun invoke(
        missionId: String,
        actualHours: Float? = null,
        goalSet: Boolean = true,
        gotFeedback: Boolean = true,
        pushedComfortZone: Boolean = true
    ): XPResult? {
        val mission = missionRepository.getMissionById(missionId) ?: return null
        if (mission.status == "COMPLETED") return null

        var result: XPResult? = null

        val rawHours = actualHours?.toDouble() ?: mission.estimatedHours.toDouble()
        val quality = XPEngine.calculateQualityScore(goalSet, gotFeedback, pushedComfortZone)
        val effectiveHours = XPEngine.calculateEffectiveHours(rawHours, quality)

        // 1. Mark Mission as Completed
        val completedMission = mission.copy(
            status = "COMPLETED",
            actualHours = rawHours.toFloat(),
            completedAt = System.currentTimeMillis(),
            qualityScore = quality,
            effectiveHours = effectiveHours
        )
        missionRepository.updateMission(completedMission)

        // 2. Award Skill & Hunter XP
        val skill   = skillRepository.getSkillById(mission.skillId)
        val hunter  = hunterRepository.getDirectHunterProfile()

        if (skill != null && hunter != null) {
            val streakVal = preferences.streakFlow.first()
            val streakMultiplier = when {
                streakVal < 7  -> 1.0f
                streakVal < 14 -> 1.15f
                streakVal < 30 -> 1.30f
                else           -> 1.50f
            }

            // Use engine to calculate
            val shadows = shadowRepository.getAllShadows().first()
            val xpResult = XPEngine.calculateXPResult(
                mission = mission,
                hunter = hunter,
                skill = skill,
                streakMultiplier = streakMultiplier,
                shadows = shadows
            )
            result = xpResult

            // --- System Anomaly (variable-reward mechanic) ---
            // Weighted roll on every non-REST mission completion. Capped at
            // MAX_DAILY_SYSTEM_ANOMALIES actual triggers/day via AxiomPreferences
            // so it stays a delight surprise, not a farmable grind loop.
            var anomalyTier: String? = null
            var anomalyBonusXP = 0
            if (mission.rarity.uppercase() != "REST") {
                val roll = kotlin.random.Random.nextFloat()
                val rolledTier = when {
                    roll < 0.005f -> "CRITICAL"   // 0.5%
                    roll < 0.04f  -> "MAJOR"       // 3.5%
                    roll < 0.16f  -> "MINOR"       // 12%
                    else -> null                    // 84% — no anomaly
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

            // Update Skill
            val skillGainedXP = xpResult.skillXPGained[skill.id] ?: mission.xpReward.toLong()
            var newSkillXP = skill.currentXP + skillGainedXP
            var newSkillLevel = skill.level
            var nextRankXP = (newSkillLevel * 100L)
            while (newSkillXP >= nextRankXP) {
                newSkillXP -= nextRankXP
                newSkillLevel++
                nextRankXP = (newSkillLevel * 100L)
            }

            // --- Mastery & Deliberate Practice Calculation ---
            val oldSkillTier = XPEngine.getMasteryTier(skill.totalEffectiveHours)
            val updatedRawHours = skill.totalRawHours + rawHours
            val updatedEffectiveHours = skill.totalEffectiveHours + effectiveHours
            val newSkillTier = XPEngine.getMasteryTier(updatedEffectiveHours)

            val tierChanged = oldSkillTier != newSkillTier
            if (tierChanged) {
                ceremonyEngine.emit(
                    CeremonyEvent.RankUp(
                        oldRank = oldSkillTier.label,
                        newRank = newSkillTier.label
                    )
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
            skillRepository.updateSkill(updatedSkill)

            // If parent skill reaches Practitioner (>= 50 effective hours), auto-unlock child skills!
            if (updatedSkill.parentId == null && updatedSkill.totalEffectiveHours >= 50.0) {
                val allSkills = skillRepository.getAllSkills().first()
                val lockedChildren = allSkills.filter { it.parentId == updatedSkill.id && !it.isUnlocked }
                for (child in lockedChildren) {
                    val unlockedChild = child.copy(isUnlocked = true)
                    skillRepository.updateSkill(unlockedChild)

                    val isFa = preferences.languageFlow.first() == "fa"
                    val childMsg = if (isFa) {
                        "◈ [ پروتکل سیستم ] ◈ زیربخش '${child.name.uppercase()}' بیدار شد — والد به سطح Practitioner رسید."
                    } else {
                        "◈ [ SYSTEM PROTOCOL ] ◈ SUB-DISCIPLINE '${child.name.uppercase()}' UNLOCKED — Parent has achieved Practitioner tier."
                    }
                    systemFeedRepository.emitMessage(
                        SystemMessage(
                            id = java.util.UUID.randomUUID().toString(),
                            message = childMsg,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            if (wasLocked) {
                val isFa = preferences.languageFlow.first() == "fa"
                val unlockedMsg = if (isFa) {
                    "◈ [ پروتکل سیستم ] ◈ گره '${skill.name.uppercase()}' بیدار شد — اولین امتیاز تمرین مأموریت دریافت گردید."
                } else {
                    "◈ [ SYSTEM PROTOCOL ] ◈ NODE '${skill.name.uppercase()}' AWAKENED — first mission XP received."
                }
                systemFeedRepository.emitMessage(
                    SystemMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        message = unlockedMsg,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            // Update Hunter
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
            val hunterRankColor = XPEngine.getRankColor(hunterRankLabel)
            val hunterRankGlyph = XPEngine.getGlyphForRank(hunterRankLabel)

            val updatedHunter = hunter.copy(
                level = newHunterLevel,
                rankLabel = hunterRankLabelWithSuffix,
                totalXP = hunter.totalXP + totalHunterXPGained,
                currentXP = newHunterXP,
                xpToNextLevel = nextLevelXP,
                progressPercent = if (newHunterLevel >= 100) 1.0f else newHunterXP.toFloat() / nextLevelXP.toFloat(),
                rankColor = hunterRankColor,
                rankGlyph = hunterRankGlyph
            )
            hunterRepository.updateHunterProfile(updatedHunter)

            if (anomalyTier != null) {
                ceremonyEngine.emit(CeremonyEvent.SystemAnomaly(tier = anomalyTier, bonusXP = anomalyBonusXP))
            }

            // If shadow was unlocked, insert it!
            if (xpResult.shadowUnlocked != null) {
                shadowRepository.insertShadow(xpResult.shadowUnlocked)
            }

            preferences.incrementWeeklyMissions()
            if (mission.rarity.uppercase() in listOf("RARE", "EPIC", "LEGENDARY", "CRITICAL", "SHIELD", "DEPTH")) {
                preferences.incrementWeeklyRare()
            }

        } else if (skill == null && hunter != null) {
            // Orphaned mission — linked skill was deleted.
            // Award base mission XP directly to the hunter so effort is never lost.
            val baseXP = mission.xpReward.toLong()
            var newHunterXP    = hunter.currentXP + baseXP.toInt()
            var newHunterLevel = hunter.level
            var nextLevelXP    = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            while (newHunterXP >= nextLevelXP && newHunterLevel < 100) {
                newHunterXP   -= nextLevelXP
                newHunterLevel++
                nextLevelXP    = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            }
            if (newHunterLevel >= 100) { newHunterLevel = 100; newHunterXP = 0 }

            val hunterRankLabel = XPEngine.calculateHunterRank(newHunterLevel)
            val hunterRankSuffix = if (hunterRankLabel.endsWith("-Rank")) hunterRankLabel else "$hunterRankLabel-Rank"
            hunterRepository.updateHunterProfile(
                hunter.copy(
                    level          = newHunterLevel,
                    rankLabel      = hunterRankSuffix,
                    totalXP        = hunter.totalXP + baseXP,
                    currentXP      = newHunterXP,
                    xpToNextLevel  = nextLevelXP,
                    progressPercent = if (newHunterLevel >= 100) 1f else newHunterXP.toFloat() / nextLevelXP,
                    rankColor      = XPEngine.getRankColor(hunterRankLabel),
                    rankGlyph      = XPEngine.getGlyphForRank(hunterRankLabel)
                )
            )
            result = com.axiom.app.domain.model.XPResult(
                missionId        = missionId,
                hunterXPGained   = baseXP.toInt(),
                skillXPGained    = emptyMap(),
                skillLeveledUp   = emptyMap(),
                leveledUp        = newHunterLevel > hunter.level,
                newLevel         = if (newHunterLevel > hunter.level) newHunterLevel else null,
                rankChanged      = hunterRankSuffix != hunter.rankLabel,
                newRank          = if (hunterRankSuffix != hunter.rankLabel) hunterRankSuffix else null,
                shadowUnlocked   = null,
                shadowMultiplier = 1f
            )
        }

        // 4. Update Dungeon Stages
        if (mission.dungeonId != null) {
            val dungeon = dungeonRepository.getDungeonById(mission.dungeonId)
            if (dungeon != null) {
                val newCompletedStages = (dungeon.completedStages + 1).coerceAtMost(dungeon.totalStages)
                val isBossDefeatedNow = newCompletedStages == dungeon.totalStages
                val dungeonCompletedAt = if (isBossDefeatedNow) System.currentTimeMillis() else null

                val updatedDungeon = dungeon.copy(
                    completedStages = newCompletedStages,
                    isBossDefeated = isBossDefeatedNow,
                    completedAt = dungeonCompletedAt
                )
                dungeonRepository.updateDungeon(updatedDungeon)
            }
        }

        if (result != null && hunter != null) {
            try {
                val currentStreak = preferences.streakFlow.first()
                val reaction = systemVoiceEngine.generateCompletionReaction(
                    hunter    = hunter,
                    streakDays = currentStreak,
                    mission   = completedMission,
                    xpGained  = result.hunterXPGained.toInt()
                )
                systemFeedRepository.emitMessage(
                    SystemMessage(
                        id        = java.util.UUID.randomUUID().toString(),
                        message   = reaction,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                // AI reaction is non-critical — ignore any issues
            }

            // Non-blocking fire-and-forget submission of the mission completion to League
            try {
                // Award local league points so local matches server-side score perfectly
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

