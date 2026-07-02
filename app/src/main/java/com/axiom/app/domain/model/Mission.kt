package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Mission(
    val id: String,
    val title: String,
    val track: String,
    val rarity: String,
    val skillId: String,
    val skillName: String,
    val xpReward: Int,
    val powerScore: Float,
    val status: String,
    val dungeonId: String?,
    val estimatedHours: Float,
    val actualHours: Float?,
    val createdAt: Long,
    val completedAt: Long?,
    val rarityColor: Long,
    val isInstantGate: Boolean = false,
    val description: String = "",
    val trackId: String? = null,
    val scheduleBlockId: String? = null,
    val qualityScore: Double = 1.0,
    val effectiveHours: Double = 0.0
)
