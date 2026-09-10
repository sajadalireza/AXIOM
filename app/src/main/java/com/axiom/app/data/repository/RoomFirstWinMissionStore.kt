package com.axiom.app.data.repository

import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.entity.MissionEntity
import com.axiom.app.domain.firstwin.FirstWinMissionStore
import com.axiom.app.domain.model.Mission
import javax.inject.Inject
import javax.inject.Singleton

/** Room-v18 idempotent First-Win Mission writer. Existing rows are never replaced. */
@Singleton
class RoomFirstWinMissionStore @Inject constructor(
    private val database: AxiomDatabase,
) : FirstWinMissionStore {
    override suspend fun getById(missionId: String): Mission? =
        database.missionDao().getMissionById(missionId)?.toDomain()

    override suspend fun insertIfAbsent(mission: Mission): Mission {
        database.missionDao().insertMissionIfAbsent(MissionEntity.fromDomain(mission))
        return database.missionDao().getMissionById(mission.id)?.toDomain()
            ?: error("First-Win mission insert/read-back failed for ${mission.id}")
    }
}
