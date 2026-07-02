package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.DailyHabitLogDao
import com.axiom.app.data.local.entity.DailyHabitLogEntity
import com.axiom.app.domain.repository.DailyHabitLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyHabitLogRepositoryImpl @Inject constructor(
    private val dailyHabitLogDao: DailyHabitLogDao
) : DailyHabitLogRepository {
    override fun getLogByDate(date: String): Flow<DailyHabitLogEntity?> =
        dailyHabitLogDao.getLogByDate(date)

    override suspend fun getLogByDateDirect(date: String): DailyHabitLogEntity? = withContext(Dispatchers.IO) {
        dailyHabitLogDao.getLogByDateDirect(date)
    }

    override fun getLogsForLast7Days(startDate: String): Flow<List<DailyHabitLogEntity>> =
        dailyHabitLogDao.getLogsForLast7Days(startDate)

    override suspend fun insertLog(log: DailyHabitLogEntity) = withContext(Dispatchers.IO) {
        dailyHabitLogDao.insertLog(log)
    }

    override suspend fun updateLog(log: DailyHabitLogEntity) = withContext(Dispatchers.IO) {
        dailyHabitLogDao.updateLog(log)
    }
}
