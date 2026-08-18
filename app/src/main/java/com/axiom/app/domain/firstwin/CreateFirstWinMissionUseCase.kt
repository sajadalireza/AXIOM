package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.SkillRepository

/** WP-207 RED — creates exactly one real micro-Mission for a First-Win session. */
class CreateFirstWinMissionUseCase(
    private val missionStore: FirstWinMissionStore,
    private val skillRepository: SkillRepository,
) {
    suspend operator fun invoke(
        sessionId: String,
        area: FirstWinArea,
        actionTitle: String,
    ): Mission = TODO("WP-207 RED")
}
