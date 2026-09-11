package com.axiom.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════
// AXIOM CANONICAL CYBER-FANTASY DESIGN TOKENS
// Unified, strict 4dp-base spacing and geometry system
// ═══════════════════════════════════════════════════════════════

/**
 * Strict 4dp-base spacing scale for layout padding, gutters, and arrangement gaps.
 */
object AxiomSpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val s: Dp = 8.dp
    val sm: Dp = 12.dp
    val m: Dp = 16.dp
    val l: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
    val xxxl: Dp = 64.dp
}

/**
 * Canonical corner radii for cards, buttons, badges, and modals.
 */
object AxiomRadius {
    val none: Dp = 0.dp
    val xs: Dp = 2.dp
    val s: Dp = 4.dp
    val m: Dp = 8.dp
    val l: Dp = 12.dp
    val xl: Dp = 16.dp
    val xxl: Dp = 24.dp
    val round: Dp = 999.dp
}

/**
 * Canonical border stroke widths for cyber containers and holographic boundaries.
 */
object AxiomBorder {
    val none: Dp = 0.dp
    val hairline: Dp = 0.5.dp
    val thin: Dp = 1.dp
    val medium: Dp = 1.5.dp
    val thick: Dp = 2.dp
    val heavy: Dp = 3.dp
}

/**
 * Elevation offsets for layering and holographic shadows.
 */
object AxiomElevation {
    val none: Dp = 0.dp
    val tiny: Dp = 1.dp
    val low: Dp = 2.dp
    val medium: Dp = 4.dp
    val high: Dp = 8.dp
}

/**
 * Standardized animation duration presets (in milliseconds).
 */
object AxiomMotion {
    const val fastMs: Int = 150
    const val normalMs: Int = 300
    const val slowMs: Int = 500
    const val pulseMs: Int = 1200
    const val shimmerMs: Int = 3000
}

// ═══════════════════════════════════════════════════════════════
// BACKWARD COMPATIBILITY ALIASES (Deprecated — use Axiom* equivalents)
// ═══════════════════════════════════════════════════════════════

@Deprecated("Use AxiomSpacing or AxiomRadius instead per G3-P5 Design System Consolidation", ReplaceWith("AxiomSpacing"))
object AwakenTokens {
    val SpaceXS: Dp = AxiomSpacing.xs
    val SpaceS: Dp = AxiomSpacing.s
    val SpaceM: Dp = AxiomSpacing.m
    val SpaceL: Dp = AxiomSpacing.l
    val SpaceXL: Dp = AxiomSpacing.xl
    val SpaceXXL: Dp = AxiomSpacing.xxl
    val RadiusS: Dp = AxiomRadius.m
    val RadiusM: Dp = AxiomRadius.l
    val RadiusL: Dp = AxiomRadius.xxl
    val ElevationS: Dp = AxiomElevation.low
    val ElevationM: Dp = AxiomElevation.high
    val AnimFastMs: Int = AxiomMotion.fastMs
    val AnimNormalMs: Int = AxiomMotion.normalMs
    val AnimSlowMs: Int = AxiomMotion.slowMs
    val CardBorderWidth: Dp = AxiomBorder.medium
    val NavBarHeight: Dp = 72.dp
}

@Deprecated("Use AxiomSpacing instead per G3-P5 Design System Consolidation", ReplaceWith("AxiomSpacing"))
object Spacing {
    val xs: Dp = AxiomSpacing.xs
    val s: Dp = AxiomSpacing.s
    val m: Dp = AxiomSpacing.m
    val l: Dp = AxiomSpacing.l
    val xl: Dp = AxiomSpacing.xl
    val xxl: Dp = AxiomSpacing.xxl
}

@Deprecated("Use AxiomBorder instead per G3-P5 Design System Consolidation", ReplaceWith("AxiomBorder"))
object Border {
    val Thin: Dp = AxiomBorder.thin
    val Medium: Dp = AxiomBorder.thick
    val Thick: Dp = AxiomBorder.heavy
}

@Deprecated("Use AxiomElevation instead per G3-P5 Design System Consolidation", ReplaceWith("AxiomElevation"))
object Elevation {
    val None: Dp = AxiomElevation.none
    val Tiny: Dp = AxiomElevation.tiny
    val Low: Dp = AxiomElevation.low
    val Medium: Dp = AxiomElevation.medium
    val High: Dp = AxiomElevation.high
}

@Deprecated("Use AxiomMotion instead per G3-P5 Design System Consolidation", ReplaceWith("AxiomMotion"))
object Anim {
    const val ShortDuration: Int = AxiomMotion.fastMs
    const val MediumDuration: Int = AxiomMotion.normalMs
    const val LongDuration: Int = AxiomMotion.slowMs
}
