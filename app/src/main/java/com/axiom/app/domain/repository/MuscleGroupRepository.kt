package com.axiom.app.domain.repository

import com.axiom.app.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

interface MuscleGroupRepository {
    fun getAllMuscleGroups(): Flow<List<MuscleGroup>>
    suspend fun getMuscleGroupById(id: String): MuscleGroup?
    suspend fun insertMuscleGroup(muscle: MuscleGroup)
    suspend fun insertMuscleGroups(muscles: List<MuscleGroup>)
    suspend fun updateMuscleGroup(muscle: MuscleGroup)
}
