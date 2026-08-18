package com.axiom.app.data.repository

import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.entity.ScheduleBlockEntity
import com.axiom.app.domain.firstwin.FirstWinScheduleStore
import com.axiom.app.domain.model.ScheduleBlock
import javax.inject.Inject
import javax.inject.Singleton

/** Existing Room-v18 schedule_blocks adapter with insert-once semantics for First-Win. */
@Singleton
class RoomFirstWinScheduleStore @Inject constructor(
    private val database: AxiomDatabase,
) : FirstWinScheduleStore {
    override suspend fun insertIfAbsent(block: ScheduleBlock): ScheduleBlock {
        database.warriorBlueprintDao().insertScheduleBlockIfAbsent(ScheduleBlockEntity.fromDomain(block))
        return database.warriorBlueprintDao().getScheduleBlockById(block.id)?.toDomain()
            ?: error("First-Win schedule insert/read-back failed for ${block.id}")
    }
}
