package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.MuscleGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuscleGroupDao {
    @Query("SELECT * FROM muscle_groups")
    fun getAllMuscleGroupsFlow(): Flow<List<MuscleGroupEntity>>

    @Query("SELECT * FROM muscle_groups WHERE id = :id LIMIT 1")
    suspend fun getMuscleGroupById(id: String): MuscleGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuscleGroup(muscle: MuscleGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMuscleGroups(muscles: List<MuscleGroupEntity>)

    @Update
    suspend fun updateMuscleGroup(muscle: MuscleGroupEntity)
}
