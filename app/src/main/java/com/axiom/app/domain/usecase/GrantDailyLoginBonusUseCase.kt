package com.axiom.app.domain.usecase

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.repository.HunterRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GrantDailyLoginBonusUseCase @Inject constructor(
    private val preferences: AxiomPreferences,
    private val hunterRepository: HunterRepository
) {
    companion object { const val BONUS_XP = 10 }

    /** Returns true if the bonus was newly granted this call. */
    suspend operator fun invoke(): Boolean {
        val isFirstToday = preferences.checkAndMarkDailyLogin()
        if (isFirstToday) {
            val hunter = hunterRepository.getHunterProfile().first() ?: return false
            hunterRepository.updateHunterProfile(
                hunter.copy(
                    totalXP = hunter.totalXP + BONUS_XP,
                    currentXP = hunter.currentXP + BONUS_XP
                )
            )
        }
        return isFirstToday
    }
}
