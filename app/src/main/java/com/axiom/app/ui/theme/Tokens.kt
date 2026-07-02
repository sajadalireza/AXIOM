package com.axiom.app.ui.theme

import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════
// NEW CENTRALIZED DESIGN TOKENS
// ═══════════════════════════════════════════════════════════════

object AwakenTokens {
    val SpaceXS = 4.dp
    val SpaceS = 8.dp
    val SpaceM = 16.dp
    val SpaceL = 24.dp
    val SpaceXL = 32.dp
    val SpaceXXL = 48.dp
    val RadiusS = 6.dp
    val RadiusM = 12.dp
    val RadiusL = 20.dp
    val ElevationS = 2.dp
    val ElevationM = 8.dp
    val AnimFastMs = 150
    val AnimNormalMs = 300
    val AnimSlowMs = 500
    val CardBorderWidth = 1.2.dp
    val NavBarHeight = 72.dp
}

// ═══════════════════════════════════════════════════════════════
// BACKWARD COMPATIBLE DESIGN TOKENS (Do not delete)
// ═══════════════════════════════════════════════════════════════

object Spacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 16.dp
    val l = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object Border {
    val Thin = 1.dp
    val Medium = 2.dp
    val Thick = 3.dp
}

object Elevation {
    val None = 0.dp
    val Tiny = 1.dp
    val Low = 2.dp
    val Medium = 4.dp
    val High = 8.dp
}

object Anim {
    const val ShortDuration = 150
    const val MediumDuration = 300
    const val LongDuration = 500
}
