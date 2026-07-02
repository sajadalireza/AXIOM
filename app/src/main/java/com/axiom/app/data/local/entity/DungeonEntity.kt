package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.Dungeon

@Entity(tableName = "dungeons")
data class DungeonEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val rarity: String,
    val totalStages: Int,
    val completedStages: Int,
    val isBossDefeated: Boolean,
    val createdAt: Long,
    val completedAt: Long?,
    val stageDescriptions: String = ""
) {
    fun toDomain(): Dungeon = Dungeon(
        id = id,
        name = name,
        description = description,
        rarity = rarity,
        totalStages = totalStages,
        completedStages = completedStages,
        isBossDefeated = isBossDefeated,
        createdAt = createdAt,
        completedAt = completedAt,
        stageDescriptions = stageDescriptions
    )

    companion object {
        fun fromDomain(domain: Dungeon): DungeonEntity = DungeonEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            rarity = domain.rarity,
            totalStages = domain.totalStages,
            completedStages = domain.completedStages,
            isBossDefeated = domain.isBossDefeated,
            createdAt = domain.createdAt,
            completedAt = domain.completedAt,
            stageDescriptions = domain.stageDescriptions
        )
    }
}
