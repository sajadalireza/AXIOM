package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.DungeonDao
import com.axiom.app.data.local.entity.DungeonEntity
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.repository.DungeonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DungeonRepositoryImpl @Inject constructor(
    private val dungeonDao: DungeonDao
) : DungeonRepository {
    override fun getAllDungeons(): Flow<List<Dungeon>> =
        dungeonDao.getAllDungeonsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getDungeonById(id: String): Dungeon? = withContext(Dispatchers.IO) {
        dungeonDao.getDungeonById(id)?.toDomain()
    }

    override suspend fun insertDungeon(dungeon: Dungeon) = withContext(Dispatchers.IO) {
        dungeonDao.insertDungeon(DungeonEntity.fromDomain(dungeon))
    }

    override suspend fun updateDungeon(dungeon: Dungeon) = withContext(Dispatchers.IO) {
        dungeonDao.updateDungeon(DungeonEntity.fromDomain(dungeon))
    }
}
