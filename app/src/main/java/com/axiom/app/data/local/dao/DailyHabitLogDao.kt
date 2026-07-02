package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.DailyHabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyHabitLogDao {
    @Query("SELECT * FROM daily_habit_logs WHERE date = :date LIMIT 1")
    fun getLogByDate(date: String): Flow<DailyHabitLogEntity?>

    @Query("SELECT * FROM daily_habit_logs WHERE date = :date LIMIT 1")
    suspend fun getLogByDateDirect(date: String): DailyHabitLogEntity?

    @Query("SELECT * FROM daily_habit_logs WHERE date >= :startDate ORDER BY date ASC")
    fun getLogsForLast7Days(startDate: String): Flow<List<DailyHabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyHabitLogEntity)

    @Update
    suspend fun updateLog(log: DailyHabitLogEntity)
}
