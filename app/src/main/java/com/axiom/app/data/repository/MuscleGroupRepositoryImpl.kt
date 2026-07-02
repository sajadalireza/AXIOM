package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.MuscleGroupDao
import com.axiom.app.data.local.entity.MuscleGroupEntity
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.domain.repository.MuscleGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleGroupRepositoryImpl @Inject constructor(
    private val muscleGroupDao: MuscleGroupDao
) : MuscleGroupRepository {
    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> =
        muscleGroupDao.getAllMuscleGroupsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getMuscleGroupById(id: String): MuscleGroup? = withContext(Dispatchers.IO) {
        muscleGroupDao.getMuscleGroupById(id)?.toDomain()
    }

    override suspend fun insertMuscleGroup(muscle: MuscleGroup) = withContext(Dispatchers.IO) {
        muscleGroupDao.insertMuscleGroup(MuscleGroupEntity.fromDomain(muscle))
    }

    override suspend fun insertMuscleGroups(muscles: List<MuscleGroup>) = withContext(Dispatchers.IO) {
        muscleGroupDao.insertMuscleGroups(muscles.map { MuscleGroupEntity.fromDomain(it) })
    }

    override suspend fun updateMuscleGroup(muscle: MuscleGroup) = withContext(Dispatchers.IO) {
        muscleGroupDao.updateMuscleGroup(MuscleGroupEntity.fromDomain(muscle))
    }
}
