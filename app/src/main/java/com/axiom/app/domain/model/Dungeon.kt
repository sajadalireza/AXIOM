package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Dungeon(
    val id: String,
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
    val isCompleted: Boolean
        get() = isBossDefeated || completedStages >= totalStages

    val progressPercent: Float
        get() = if (totalStages > 0) completedStages.toFloat() / totalStages.toFloat() else 0f

    val isOnBossStage: Boolean
        get() = completedStages == totalStages - 1 && !isBossDefeated
}
