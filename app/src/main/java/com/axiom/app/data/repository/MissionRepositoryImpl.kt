package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.MissionDao
import com.axiom.app.data.local.entity.MissionEntity
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.MissionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepositoryImpl @Inject constructor(
    private val missionDao: MissionDao
) : MissionRepository {
    override fun getAllMissions(): Flow<List<Mission>> =
        missionDao.getAllMissionsFlow().map { list -> list.map { it.toDomain() } }

    override fun getActiveMissions(): Flow<List<Mission>> =
        missionDao.getActiveMissionsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getMissionById(id: String): Mission? = withContext(Dispatchers.IO) {
        missionDao.getMissionById(id)?.toDomain()
    }

    override suspend fun insertMission(mission: Mission) = withContext(Dispatchers.IO) {
        missionDao.insertMission(MissionEntity.fromDomain(mission))
    }

    override suspend fun updateMission(mission: Mission) = withContext(Dispatchers.IO) {
        missionDao.updateMission(MissionEntity.fromDomain(mission))
    }

    override suspend fun deleteMission(mission: Mission) = withContext(Dispatchers.IO) {
        missionDao.deleteMission(MissionEntity.fromDomain(mission))
    }
}
