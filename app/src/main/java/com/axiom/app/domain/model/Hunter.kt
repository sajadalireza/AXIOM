package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Hunter(
    val id: String,
    val name: String,
    val level: Int,
    val rankLabel: String,
    val totalXP: Long,
    val currentXP: Int,
    val xpToNextLevel: Int,
    val progressPercent: Float,
    val rankColor: Long,
    val rankGlyph: String,
    val personalThesis: String = ""
)
