package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.repository.ShadowRepository
import java.util.UUID
import javax.inject.Inject

class AriseShadowUseCase @Inject constructor(
    private val skillRepository: SkillRepository,
    private val shadowRepository: ShadowRepository
) {
    suspend operator fun invoke(skillId: String, shadowName: String): Shadow? {
        val skill = skillRepository.getSkillById(skillId) ?: return null
        if (!skill.isShadowCandidate) return null

        // 1. Create and insert Shadow
        val shadowId = UUID.randomUUID().toString()
        val shadow = Shadow(
            id = shadowId,
            name = shadowName,
            skillId = skillId,
            rankLabel = skill.rankLabel,
            acquiredAt = System.currentTimeMillis(),
            skillCategory = skill.category
        )
        shadowRepository.insertShadow(shadow)

        // 2. Set isShadowCandidate to false so we don't arises it again
        val updatedSkill = skill.copy(
            isShadowCandidate = false
        )
        skillRepository.updateSkill(updatedSkill)

        return shadow
    }
}
