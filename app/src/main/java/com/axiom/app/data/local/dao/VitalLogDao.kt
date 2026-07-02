package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.VitalLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalLogDao {
    @Query("SELECT * FROM vital_logs WHERE date = :date AND type = :type LIMIT 1")
    fun getVitalLogFlow(date: Long, type: String): Flow<VitalLogEntity?>

    @Query("SELECT * FROM vital_logs WHERE date = :date AND type = :type LIMIT 1")
    suspend fun getVitalLog(date: Long, type: String): VitalLogEntity?

    @Query("SELECT * FROM vital_logs WHERE type = :type AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getTrendFlow(type: String, startDate: Long, endDate: Long): Flow<List<VitalLogEntity>>

    @Query("SELECT * FROM vital_logs WHERE type = :type AND date >= :startDate ORDER BY date ASC")
    suspend fun getRecentLogs(type: String, startDate: Long): List<VitalLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalLog(vitalLog: VitalLogEntity)

    @Query("DELETE FROM vital_logs WHERE date = :date AND type = :type")
    suspend fun deleteVitalLog(date: Long, type: String)
}
