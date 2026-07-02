package com.axiom.app.data.repository

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val preferences: AxiomPreferences
) : StreakRepository {
    override val streakFlow: Flow<Int> = preferences.streakFlow
    override val lastCompleteTimestampFlow: Flow<Long> = preferences.lastCompleteTimestampFlow

    override suspend fun checkOffDailyProtocol(): Boolean {
        return preferences.checkOffDailyProtocol()
    }
}
