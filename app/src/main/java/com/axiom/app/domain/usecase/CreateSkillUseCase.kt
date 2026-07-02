package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.SkillRepository
import java.util.UUID
import javax.inject.Inject

class CreateSkillUseCase @Inject constructor(
    private val repository: SkillRepository
) {
    suspend operator fun invoke(
        name: String,
        category: String,
        parentId: String? = null,
        trackId: String? = null
    ) {
        val skillId = UUID.randomUUID().toString()
        val defaultSkill = Skill(
            id = skillId,
            name = name,
            category = category,
            currentXP = 0L,
            level = 1,
            rankLabel = "E-Rank",
            parentId = parentId,
            isUnlocked = true,
            xpToNextRank = 100L,
            rankProgressPercent = 0.0f,
            isShadowCandidate = false,
            rankColor = 0xFF9E9E9E,
            trackId = trackId
        )
        repository.insertSkill(defaultSkill)
    }
}
