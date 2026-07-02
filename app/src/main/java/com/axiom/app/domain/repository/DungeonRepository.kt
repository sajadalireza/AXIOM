package com.axiom.app.domain.repository

import com.axiom.app.domain.model.Dungeon
import kotlinx.coroutines.flow.Flow

interface DungeonRepository {
    fun getAllDungeons(): Flow<List<Dungeon>>
    suspend fun getDungeonById(id: String): Dungeon?
    suspend fun insertDungeon(dungeon: Dungeon)
    suspend fun updateDungeon(dungeon: Dungeon)
}
