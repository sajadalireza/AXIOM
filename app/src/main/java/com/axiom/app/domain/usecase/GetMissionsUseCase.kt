package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMissionsUseCase @Inject constructor(
    private val repository: MissionRepository
) {
    operator fun invoke(activeOnly: Boolean = false): Flow<List<Mission>> {
        return if (activeOnly) {
            repository.getActiveMissions()
        } else {
            repository.getAllMissions()
        }
    }
}
