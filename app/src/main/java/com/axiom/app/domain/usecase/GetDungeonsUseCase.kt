package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.repository.DungeonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDungeonsUseCase @Inject constructor(
    private val repository: DungeonRepository
) {
    operator fun invoke(): Flow<List<Dungeon>> {
        return repository.getAllDungeons()
    }
}
