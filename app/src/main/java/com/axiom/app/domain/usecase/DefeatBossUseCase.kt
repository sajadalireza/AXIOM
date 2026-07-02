package com.axiom.app.domain.usecase

import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.domain.repository.DungeonRepository
import com.axiom.app.domain.repository.HunterRepository
import javax.inject.Inject

class DefeatBossUseCase @Inject constructor(
    private val dungeonRepository: DungeonRepository,
    private val hunterRepository: HunterRepository
) {
    suspend operator fun invoke(dungeonId: String): Pair<String, Int>? {
        val dungeon = dungeonRepository.getDungeonById(dungeonId) ?: return null
        if (dungeon.isBossDefeated) return null

        val updated = dungeon.copy(
            completedStages = dungeon.totalStages,
            isBossDefeated = true,
            completedAt = System.currentTimeMillis()
        )
        dungeonRepository.updateDungeon(updated)

        // Award bonus XP to hunter
        val hunter = hunterRepository.getDirectHunterProfile()
        val bonusXP = when (dungeon.rarity.lowercase().trim()) {
            "depth", "legendary" -> 500
            "shield", "epic" -> 300
            "critical", "rare" -> 150
            "compound" -> 120
            else -> 100
        }

        if (hunter != null) {
            var newHunterXP = hunter.currentXP + bonusXP
            var newHunterLevel = hunter.level
            var nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            val totalXP = hunter.totalXP + bonusXP

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

            val hunterRank = XPEngine.calculateHunterRank(newHunterLevel)
            val hunterRankWithSuffix = if (hunterRank.endsWith("-Rank")) hunterRank else "$hunterRank-Rank"
            val hunterRankColor = XPEngine.getRankColor(hunterRank)
            val hunterRankGlyph = XPEngine.getGlyphForRank(hunterRank)

            val updatedHunter = hunter.copy(
                level = newHunterLevel,
                rankLabel = hunterRankWithSuffix,
                totalXP = totalXP,
                currentXP = newHunterXP,
                xpToNextLevel = nextLevelXP,
                progressPercent = if (newHunterLevel >= 100) 1.0f else newHunterXP.toFloat() / nextLevelXP.toFloat(),
                rankColor = hunterRankColor,
                rankGlyph = hunterRankGlyph
            )
            hunterRepository.updateHunterProfile(updatedHunter)
        }

        return Pair(dungeon.name, bonusXP)
    }
}
