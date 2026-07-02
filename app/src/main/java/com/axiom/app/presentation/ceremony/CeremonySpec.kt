package com.axiom.app.presentation.ceremony

import androidx.compose.ui.graphics.Color

enum class ParticleStyle { BURST, RAIN, RING, NONE }

data class CeremonySpec(
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val soundResId: Int?,
    val particleStyle: ParticleStyle,
    val durationMs: Int = 3000,
    val dismissible: Boolean = true
)
