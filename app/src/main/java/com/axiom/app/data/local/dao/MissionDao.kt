package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.MissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY createdAt DESC")
    fun getAllMissionsFlow(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveMissionsFlow(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions WHERE id = :id LIMIT 1")
    suspend fun getMissionById(id: String): MissionEntity?

    @Query("SELECT * FROM missions WHERE scheduleBlockId = :scheduleBlockId")
    suspend fun getMissionsByScheduleBlockId(scheduleBlockId: String): List<MissionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissionIfAbsent(mission: MissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Delete
    suspend fun deleteMission(mission: MissionEntity)
}
