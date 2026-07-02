package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.domain.repository.SkillRepository
import java.util.UUID
import javax.inject.Inject

class CreateMissionUseCase @Inject constructor(
    private val missionRepository: MissionRepository,
    private val skillRepository: SkillRepository
) {
    suspend operator fun invoke(
        title: String,
        track: String,
        rarity: String,
        skillId: String,
        xpReward: Int,
        powerScore: Float,
        estimatedHours: Float,
        dungeonId: String? = null,
        isInstantGate: Boolean = false,
        description: String = ""
    ): String {
        require(estimatedHours > 0f) { "Estimated hours must be greater than 0" }
        val skill = skillRepository.getSkillById(skillId)
        val skillName = skill?.name ?: "Unknown Skill"
        val missionId = UUID.randomUUID().toString()

        val rarityColor = if (isInstantGate) {
            0xFFEF9F27 // Crimson/Legendary gold neon
        } else {
            when (rarity.lowercase()) {
                "normal", "common" -> 0xFF8A8AA0 // CommonGray
                "uncommon" -> 0xFF1D9E75 // UncommonTeal
                "rare" -> 0xFF378ADD // RareBlue
                "epic" -> 0xFF7F77DD // EpicPurple
                "legendary" -> 0xFFEF9F27 // LegendaryGold
                "mythic" -> 0xFFF44336
                else -> 0xFF9E9E9E
            }
        }

        val mission = Mission(
            id = missionId,
            title = title,
            track = track,
            rarity = if (isInstantGate) "LEGENDARY" else rarity,
            skillId = skillId,
            skillName = skillName,
            xpReward = xpReward,
            powerScore = powerScore,
            status = "ACTIVE",
            dungeonId = dungeonId,
            estimatedHours = estimatedHours,
            actualHours = null,
            createdAt = System.currentTimeMillis(),
            completedAt = null,
            rarityColor = rarityColor,
            isInstantGate = isInstantGate,
            description = description
        )

        missionRepository.insertMission(mission)
        return missionId
    }

    suspend operator fun invoke(
        title: String,
        estimatedHours: Float,
        skillId: String?,
        rarity: String
    ): String {
        val resolvedSkillId = skillId ?: "skill_health"
        val skill = skillRepository.getSkillById(resolvedSkillId)
        val track = skill?.category ?: "Health"
        val powerScore = 1.5f
        val xpReward = 30
        val resolvedRarity = if (rarity == "COMMON") "Normal" else rarity

        return invoke(
            title = title,
            track = track,
            rarity = resolvedRarity,
            skillId = resolvedSkillId,
            xpReward = xpReward,
            powerScore = powerScore,
            estimatedHours = estimatedHours
        )
    }
}
