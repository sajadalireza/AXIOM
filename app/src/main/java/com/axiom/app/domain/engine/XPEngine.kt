package com.axiom.app.domain.engine

import com.axiom.app.domain.model.*
import java.util.UUID

object XPEngine {

    private fun getRarityMultiplier(rarity: String): Float {
        return when (rarity.uppercase().replace("-RANK", "").trim()) {
            "DEPTH", "LEGENDARY"        -> 5.0f
            "SHIELD", "EPIC"            -> 3.0f
            "CRITICAL", "RARE"          -> 2.0f
            "COMPOUND", "UNCOMMON"      -> 1.5f
            "FOUNDATION", "COMMON"      -> 1.0f
            "WEALTH_ENGINE"             -> 4.5f
            "REVIEW"                    -> 2.5f
            "PROTECTED"                 -> 2.0f
            "BATCH"                     -> 1.2f
            "REST"                      -> 0.8f
            else                        -> 1.0f
        }
    }

    fun getShadowXPMultiplier(shadowCount: Int, skillCategory: String,
                              shadows: List<Shadow>): Float {
        val relevantShadows = shadows.filter {
            it.skillCategory == skillCategory
        }
        return 1.0f + (relevantShadows.size * 0.05f).coerceAtMost(0.50f)
    }

    fun calculateXPResult(
        mission: Mission,
        hunter: Hunter,
        skill: Skill,
        streakMultiplier: Float,
        shadows: List<Shadow> = emptyList()
    ): XPResult {
        val shadowMultiplier = getShadowXPMultiplier(shadows.size, skill.category, shadows)
        val rarityMultiplier = getRarityMultiplier(mission.rarity)
        
        val isInstantGateActive = mission.isInstantGate && (System.currentTimeMillis() - mission.createdAt <= 3600000L)
        val instantGateMultiplier = if (isInstantGateActive) 3.0f else 1.0f

        val hunterXPGained = (mission.xpReward * rarityMultiplier * streakMultiplier * shadowMultiplier * instantGateMultiplier).toInt()
        val skillXPGainedVal = (mission.xpReward.toLong() * instantGateMultiplier).toLong()

        val oldLevel = skill.level
        var testXP = skill.currentXP + skillXPGainedVal
        var testLevel = oldLevel
        var nextRankXP = (testLevel * 100L)
        while (testXP >= nextRankXP) {
            testXP -= nextRankXP
            testLevel++
            nextRankXP = (testLevel * 100L)
        }
        val isLeveledUp = testLevel > oldLevel

        val oldRank = skill.rankLabel.replace("-Rank", "")
        val newRank = calculateSkillRank(skill.currentXP + skillXPGainedVal)
        val shadowUnlocked = if (!isShadowCandidate(oldRank) && isShadowCandidate(newRank)) {
            Shadow(
                id = UUID.randomUUID().toString(),
                name = "${skill.name}'s Operative",
                skillId = skill.id,
                rankLabel = "$newRank-Rank",
                acquiredAt = System.currentTimeMillis(),
                skillCategory = skill.category
            )
        } else {
            null
        }

        var testHunterXP = hunter.currentXP + hunterXPGained
        var testHunterLevel = hunter.level
        var nextLevelXP = xpNeededForLevel(testHunterLevel).toInt()
        while (testHunterXP >= nextLevelXP && testHunterLevel < 100) {
            testHunterXP -= nextLevelXP
            testHunterLevel++
            nextLevelXP = xpNeededForLevel(testHunterLevel).toInt()
        }
        if (testHunterLevel >= 100) {
            testHunterLevel = 100
            testHunterXP = 0
        }
        val leveledUp = testHunterLevel > hunter.level
        val newLevelVal = if (leveledUp) testHunterLevel else null

        val oldHunterRank = calculateHunterRank(hunter.level)
        val newHunterRank = calculateHunterRank(testHunterLevel)
        val rankChanged = oldHunterRank != newHunterRank
        val newRankVal = if (rankChanged) "$newHunterRank-Rank" else null

        return XPResult(
            missionId = mission.id,
            hunterXPGained = hunterXPGained,
            skillXPGained = mapOf(skill.id to skillXPGainedVal),
            skillLeveledUp = mapOf(skill.id to isLeveledUp),
            shadowUnlocked = shadowUnlocked,
            leveledUp = leveledUp,
            newLevel = newLevelVal,
            rankChanged = rankChanged,
            newRank = newRankVal,
            shadowMultiplier = shadowMultiplier
        )
    }

    fun calculateSkillRank(xp: Long): String {
        return when {
            xp >= 12000 -> "ARCHITECT"
            xp >= 7000  -> "STRATEGIST"
            xp >= 3500  -> "SPECIALIST"
            xp >= 1500  -> "OPERATOR"
            xp >= 500   -> "BUILDER"
            else        -> "RECRUIT"
        }
    }

    fun calculateHunterRank(level: Int): String {
        return when {
            level >= 100 -> "ARCHITECT"
            level >= 71  -> "STRATEGIST"
            level >= 46  -> "SPECIALIST"
            level >= 26  -> "OPERATOR"
            level >= 11  -> "BUILDER"
            else         -> "RECRUIT"
        }
    }

    fun isShadowCandidate(rankLabel: String): Boolean {
        val clean = rankLabel.uppercase().replace("-RANK", "").trim()
        return clean == "B" || clean == "A" || clean == "S" ||
               clean == "SPECIALIST" || clean == "STRATEGIST" || clean == "ARCHITECT"
    }

    fun getRankColor(rankLabel: String): Long {
        return when (rankLabel.uppercase().replace("-RANK", "").trim()) {
            "S", "ARCHITECT"  -> 0xFFFFD700L // Gold
            "A", "STRATEGIST" -> 0xFF9C27B0L // Purple
            "B", "SPECIALIST" -> 0xFF00BCD4L // Teal
            "C", "OPERATOR"   -> 0xFF2196F3L // Blue
            "D", "BUILDER"    -> 0xFFD4A843L // Gold/Orange
            "E", "RECRUIT"    -> 0xFF9E9E9EL // Grey
            else              -> 0xFF2A2A2AL
        }
    }

    fun getGlyphForRank(rankLabel: String): String {
        return when (rankLabel.uppercase().replace("-RANK", "").trim()) {
            "S", "ARCHITECT"  -> "★"
            "A", "STRATEGIST" -> "◈"
            "B", "SPECIALIST" -> "✦"
            "C", "OPERATOR"   -> "❖"
            "D", "BUILDER"    -> "▲"
            "E", "RECRUIT"    -> "▼"
            else              -> "◈"
        }
    }

    fun xpNeededForLevel(level: Int): Long {
        return 100L + (level - 1) * 50L
    }

    fun calculateQualityScore(goalSet: Boolean, gotFeedback: Boolean, pushedComfortZone: Boolean): Double {
        var yesCount = 0
        if (goalSet) yesCount++
        if (gotFeedback) yesCount++
        if (pushedComfortZone) yesCount++
        return yesCount / 3.0
    }

    fun calculateEffectiveHours(rawHours: Double, quality: Double): Double {
        return rawHours * quality
    }

    fun getMasteryTier(totalEffectiveHours: Double): MasteryTier {
        return MasteryTier.fromHours(totalEffectiveHours)
    }

    fun getProgressToNextTier(totalEffectiveHours: Double): Float {
        val currentTier = MasteryTier.fromHours(totalEffectiveHours)
        val currentTierMin = currentTier.minHours
        val nextTier = MasteryTier.values().sortedBy { it.minHours }.find { it.minHours > currentTierMin } ?: return 1.0f
        val nextTierMin = nextTier.minHours
        val range = nextTierMin - currentTierMin
        if (range <= 0.0) return 1.0f
        val progress = (totalEffectiveHours - currentTierMin) / range
        return progress.coerceIn(0.0, 1.0).toFloat()
    }
}
