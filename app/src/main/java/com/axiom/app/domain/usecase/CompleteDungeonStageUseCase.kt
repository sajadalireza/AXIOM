package com.axiom.app.domain.usecase

import com.axiom.app.domain.repository.DungeonRepository
import javax.inject.Inject

class CompleteDungeonStageUseCase @Inject constructor(
    private val repository: DungeonRepository
) {
    suspend operator fun invoke(dungeonId: String) {
        val dungeon = repository.getDungeonById(dungeonId) ?: return
        val nextCompleted = (dungeon.completedStages + 1).coerceAtMost(dungeon.totalStages)
        val isBossDefeatedNow = nextCompleted == dungeon.totalStages

        val updated = dungeon.copy(
            completedStages = nextCompleted,
            isBossDefeated = isBossDefeatedNow,
            completedAt = if (isBossDefeatedNow) System.currentTimeMillis() else null
        )
        repository.updateDungeon(updated)
    }
}
