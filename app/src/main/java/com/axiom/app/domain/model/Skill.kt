package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Skill(
    val id: String,
    val name: String,
    val category: String,
    val currentXP: Long,
    val level: Int,
    val rankLabel: String,
    val parentId: String?,
    val isUnlocked: Boolean,
    val xpToNextRank: Long,
    val rankProgressPercent: Float,
    val isShadowCandidate: Boolean,
    val rankColor: Long,
    val trackId: String? = null,
    val totalRawHours: Double = 0.0,
    val totalEffectiveHours: Double = 0.0
)
