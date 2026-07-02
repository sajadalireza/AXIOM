package com.axiom.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    val streakFlow: Flow<Int>
    val lastCompleteTimestampFlow: Flow<Long>
    suspend fun checkOffDailyProtocol(): Boolean
}
