package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.repository.HunterRepository

/** WP-207 RED — minimal Hunter bootstrap with no legacy starter-content side effects. */
class EnsureFirstWinHunterUseCase(
    private val hunterRepository: HunterRepository,
) {
    suspend operator fun invoke(): Hunter = TODO("WP-207 RED")
}
