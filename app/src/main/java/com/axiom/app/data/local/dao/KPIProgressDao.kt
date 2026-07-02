package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.KPIProgressEntity
import com.axiom.app.data.local.entity.KPIMissStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KPIProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: KPIProgressEntity)

    @Query("SELECT * FROM kpi_progress WHERE kpiId = :kpiId")
    fun getProgressForKPI(kpiId: String): Flow<List<KPIProgressEntity>>

    @Query("SELECT * FROM kpi_progress")
    fun getAllProgress(): Flow<List<KPIProgressEntity>>

    @Query("DELETE FROM kpi_progress WHERE kpiId = :kpiId")
    suspend fun deleteProgressForKPI(kpiId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissStreak(streak: KPIMissStreakEntity)

    @Query("SELECT * FROM kpi_miss_streaks WHERE kpiId = :kpiId")
    fun getMissStreakForKPI(kpiId: String): Flow<KPIMissStreakEntity?>

    @Query("SELECT * FROM kpi_miss_streaks WHERE kpiId = :kpiId")
    suspend fun getDirectMissStreakForKPI(kpiId: String): KPIMissStreakEntity?

    @Query("SELECT * FROM kpi_miss_streaks")
    fun getAllMissStreaks(): Flow<List<KPIMissStreakEntity>>
}
