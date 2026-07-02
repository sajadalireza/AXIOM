package com.axiom.app.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// AXIOM STATIC DESIGN TOKENS
// Exposes dark tokens statically for backward compatibility
// ═══════════════════════════════════════════════════════════════

// ─── Backgrounds ────────────────────────────────────────────────
val VoidBlack: Color = AxiomDarkColors.voidBlack
val ShadowSurface: Color = AxiomDarkColors.shadowSurface
val DimSurface: Color = AxiomDarkColors.dimSurface
val BorderFaint: Color = AxiomDarkColors.borderFaint

// ─── Accent ─────────────────────────────────────────────────────
val SystemGreen: Color = AxiomDarkColors.systemGreen
val SystemGlint: Color = AxiomDarkColors.systemGlint

// ─── Rank / Rarity ──────────────────────────────────────────────
val LegendaryGold: Color = AxiomDarkColors.legendaryGold
val EpicPurple: Color = AxiomDarkColors.epicPurple
val RareBlue: Color = AxiomDarkColors.rareBlue
val UncommonTeal: Color = AxiomDarkColors.uncommonTeal
val CommonGray: Color = AxiomDarkColors.commonGray

// ─── Status ─────────────────────────────────────────────────────
val PenaltyRed: Color = AxiomDarkColors.penaltyRed

// ─── Text ───────────────────────────────────────────────────────
val TextPrimary: Color = AxiomDarkColors.textPrimary
val TextSecondary: Color = AxiomDarkColors.textSecondary
val TextDim: Color = AxiomDarkColors.textDim

// ═══════════════════════════════════════════════════════════════
// SEMANTIC MAPS FOR DYNAMIC LOOKUP
// ═══════════════════════════════════════════════════════════════

/**
 * Maps mission rarity strings to their display color.
 * Keys are uppercase rarity labels as stored in Room.
 */
val rarityColorMap: Map<String, Color> = mapOf(
    "LEGENDARY" to LegendaryGold,
    "EPIC"      to EpicPurple,
    "RARE"      to RareBlue,
    "UNCOMMON"  to UncommonTeal,
    "COMMON"    to CommonGray
)

/**
 * Maps hunter/skill rank labels to their display color.
 * Supports both bare rank ("S") and suffixed ("S-Rank") keys.
 */
val rankColorMap: Map<String, Color> = mapOf(
    "S"      to LegendaryGold,
    "A"      to EpicPurple,
    "B"      to RareBlue,
    "C"      to UncommonTeal,
    "D"      to CommonGray,
    "E"      to CommonGray,
    "S-Rank" to LegendaryGold,
    "A-Rank" to EpicPurple,
    "B-Rank" to RareBlue,
    "C-Rank" to UncommonTeal,
    "D-Rank" to CommonGray,
    "E-Rank" to CommonGray
)
