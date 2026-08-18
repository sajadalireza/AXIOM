package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.SkillRepository
import javax.inject.Inject

/** Creates exactly one real, local micro-Mission for a First-Win session. */
class CreateFirstWinMissionUseCase @Inject constructor(
    private val missionStore: FirstWinMissionStore,
    private val skillRepository: SkillRepository,
) {
    suspend operator fun invoke(
        sessionId: String,
        area: FirstWinArea,
        actionTitle: String,
    ): Mission {
        require(sessionId.isNotBlank()) { "sessionId must not be blank" }
        val title = actionTitle.trim()
        require(title.length >= 3) { "actionTitle must contain at least 3 characters" }

        val skill = skillRepository.getSkillById(area.skillId)
            ?: error("Required neutral First-Win skill is missing: ${area.skillId}")

        val mission = Mission(
            id = FirstWinIds.primaryMissionId(sessionId),
            title = title,
            track = area.name,
            rarity = "Normal",
            skillId = skill.id,
            skillName = skill.name,
            xpReward = 10,
            powerScore = 1.0f,
            status = "ACTIVE",
            dungeonId = null,
            estimatedHours = 2f / 60f,
            actualHours = null,
            createdAt = System.currentTimeMillis(),
            completedAt = null,
            rarityColor = 0xFF8A8AA0,
            isInstantGate = false,
            description = "",
            trackId = skill.trackId,
            scheduleBlockId = null,
            qualityScore = 1.0,
            effectiveHours = 0.0,
        )
        return missionStore.insertIfAbsent(mission)
    }
}
