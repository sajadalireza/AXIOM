package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.repository.DungeonRepository
import java.util.UUID
import javax.inject.Inject

class CreateDungeonUseCase @Inject constructor(
    private val repository: DungeonRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        rarity: String,
        totalStages: Int,
        stageDescriptions: String = ""
    ) {
        val dungeonId = UUID.randomUUID().toString()
        val dungeon = Dungeon(
            id = dungeonId,
            name = name,
            description = description,
            rarity = rarity,
            totalStages = totalStages,
            completedStages = 0,
            isBossDefeated = false,
            createdAt = System.currentTimeMillis(),
            completedAt = null,
            stageDescriptions = stageDescriptions
        )
        repository.insertDungeon(dungeon)
    }
}
