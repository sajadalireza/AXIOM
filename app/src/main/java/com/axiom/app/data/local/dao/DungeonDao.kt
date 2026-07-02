package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.DungeonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DungeonDao {
    @Query("SELECT * FROM dungeons ORDER BY createdAt DESC")
    fun getAllDungeonsFlow(): Flow<List<DungeonEntity>>

    @Query("SELECT * FROM dungeons WHERE id = :id LIMIT 1")
    suspend fun getDungeonById(id: String): DungeonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDungeon(dungeon: DungeonEntity)

    @Update
    suspend fun updateDungeon(dungeon: DungeonEntity)
}
