package com.axiom.app.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Stable
data class AxiomColorScheme(
    val voidBlack: Color,
    val shadowSurface: Color,
    val dimSurface: Color,
    val borderFaint: Color,
    val systemGreen: Color,
    val systemGlint: Color,
    val legendaryGold: Color,
    val epicPurple: Color,
    val rareBlue: Color,
    val uncommonTeal: Color,
    val commonGray: Color,
    val penaltyRed: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDim: Color,
    // WARRIOR Leverage Tags
    val leverageFoundation: Color,
    val leverageCompound: Color,
    val leverageCritical: Color,
    val leverageShield: Color,
    val leverageDepth: Color,
    val leverageBatch: Color,
    val leverageWealthEngine: Color,
    val leverageReview: Color,
    val leverageProtected: Color,
    val leverageRest: Color
)

val AxiomDarkColors = AxiomColorScheme(
    voidBlack     = Color(0xFF060807),
    shadowSurface = Color(0xFF141413),
    dimSurface    = Color(0xFF1C1C1A),
    borderFaint   = Color(0xFF2A2A28),
    systemGreen   = Color(0xFF1FAE80),
    systemGlint   = Color(0xFFF0C860),
    legendaryGold = Color(0xFFF0C860),
    epicPurple    = Color(0xFF9B6FE0),
    rareBlue      = Color(0xFF5189C9),
    uncommonTeal  = Color(0xFF3FA06A),
    commonGray    = Color(0xFF6E6658),
    penaltyRed    = Color(0xFFC04040),
    textPrimary   = Color(0xFFF0EBE2),
    textSecondary = Color(0xFFA09880),
    textDim       = Color(0xFF5A5448),
    // Dark Leverage Tags
    leverageFoundation = Color(0xFF6E6E85),
    leverageCompound   = Color(0xFF188C68),
    leverageCritical   = Color(0xFF2569B8),
    leverageShield     = Color(0xFF6258C4),
    leverageDepth      = Color(0xFFC97F0E),
    leverageBatch      = Color(0xFF8A8AA0),
    leverageWealthEngine = Color(0xFFEF9F27),
    leverageReview     = Color(0xFF7F77DD),
    leverageProtected  = Color(0xFFFF5252),
    leverageRest       = Color(0xFF4CAF50)
)

// First-pass light palette — refined visually in Phase 4.
// Keeps the same cool, slightly violet-tinted undertone as VoidBlack,
// inverted to a soft off-white instead of pure white.
val AxiomLightColors = AxiomColorScheme(
    voidBlack     = Color(0xFFF5F1E8),
    shadowSurface = Color(0xFFFFFFFF),
    dimSurface    = Color(0xFFEBE5D8),
    borderFaint   = Color(0xFFD8D0BC),
    systemGreen   = Color(0xFF157F5A),
    systemGlint   = Color(0xFFC4992E),
    legendaryGold = Color(0xFFA87F28),
    epicPurple    = Color(0xFF6A4AA8),
    rareBlue      = Color(0xFF2D5478),
    uncommonTeal  = Color(0xFF2D6E48),
    commonGray    = Color(0xFF6B6354),
    penaltyRed    = Color(0xFF9C3030),
    textPrimary   = Color(0xFF1A1812),
    textSecondary = Color(0xFF5A5448),
    textDim       = Color(0xFF8A8270),
    // Light Leverage Tags
    leverageFoundation = Color(0xFF5A5A6E),
    leverageCompound   = Color(0xFF116A4E),
    leverageCritical   = Color(0xFF1C4F8F),
    leverageShield     = Color(0xFF4B439C),
    leverageDepth      = Color(0xFF9C610A),
    leverageBatch      = Color(0xFF6B6B7D),
    leverageWealthEngine = Color(0xFFB8781B),
    leverageReview     = Color(0xFF5B54AD),
    leverageProtected  = Color(0xFFC62828),
    leverageRest       = Color(0xFF2E7D32)
)

val LocalAxiomColors = staticCompositionLocalOf { AxiomDarkColors }
