package com.axiom.app.domain.repository

import com.axiom.app.data.local.entity.DailyHabitLogEntity
import kotlinx.coroutines.flow.Flow

interface DailyHabitLogRepository {
    fun getLogByDate(date: String): Flow<DailyHabitLogEntity?>
    suspend fun getLogByDateDirect(date: String): DailyHabitLogEntity?
    fun getLogsForLast7Days(startDate: String): Flow<List<DailyHabitLogEntity>>
    suspend fun insertLog(log: DailyHabitLogEntity)
    suspend fun updateLog(log: DailyHabitLogEntity)
}
