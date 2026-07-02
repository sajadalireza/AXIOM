package com.axiom.app.domain.repository

import com.axiom.app.domain.model.Mission
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    fun getAllMissions(): Flow<List<Mission>>
    fun getActiveMissions(): Flow<List<Mission>>
    suspend fun getMissionById(id: String): Mission?
    suspend fun insertMission(mission: Mission)
    suspend fun updateMission(mission: Mission)
    suspend fun deleteMission(mission: Mission)
}
