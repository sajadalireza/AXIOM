@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.axiom.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import com.axiom.app.R

// Outfit and Fraunces ship from Google Fonts as variable fonts only (no static
// per-weight files exist upstream anymore) — one font file covers every weight
// via the 'wght' variation axis, selected per Font() entry below.
val Inter = FontFamily(
    Font(R.font.outfit_variable, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400)))
)

val FiraCode = FontFamily(
    Font(R.font.fira_code_regular, FontWeight.Normal),
    Font(R.font.fira_code_medium, FontWeight.Medium)
)

val JetBrainsMono = FiraCode

val Fraunces = FontFamily(
    Font(R.font.fraunces_variable, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.fraunces_variable, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
    Font(R.font.fraunces_variable, FontWeight.Black, variationSettings = FontVariation.Settings(FontVariation.weight(900))),
    Font(R.font.fraunces_italic_variable, FontWeight.Normal, style = FontStyle.Italic, variationSettings = FontVariation.Settings(FontVariation.weight(400)))
)

val Outfit = FontFamily(
    Font(R.font.outfit_variable, FontWeight.Light, variationSettings = FontVariation.Settings(FontVariation.weight(300))),
    Font(R.font.outfit_variable, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.outfit_variable, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.outfit_variable, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600)))
)

// ─────────────────────────────────────────────
// NEW ELEVATED TYPOGRAPHY SYSTEM
// ─────────────────────────────────────────────

val DisplayXL = TextStyle(
    fontFamily = Fraunces,
    fontWeight = FontWeight.Black,
    fontSize = 34.sp,
    lineHeight = 39.1.sp
)

val DisplayL = TextStyle(
    fontFamily = Fraunces,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 32.2.sp
)

val DisplayM = TextStyle(
    fontFamily = Fraunces,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 25.3.sp
)

val TitleL = TextStyle(
    fontFamily = Outfit,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp
)

val TitleM = TextStyle(
    fontFamily = Outfit,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp
)

val LabelL = TextStyle(
    fontFamily = Outfit,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp
)

val LabelS = TextStyle(
    fontFamily = Outfit,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

val BodyL = TextStyle(
    fontFamily = Outfit,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp
)

val HudXL = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Bold,
    fontSize = 40.sp,
    lineHeight = 46.sp
)

val HudL = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 32.sp
)

val HudM = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 22.sp
)

val HudS = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 16.sp
)

val SystemMsg = TextStyle(
    fontFamily = JetBrainsMono,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 16.sp
)

// ─────────────────────────────────────────────
// BACKWARD COMPATIBILITY ALIASES
// ─────────────────────────────────────────────

val DisplayLarge   = DisplayL
val DisplayMedium  = DisplayM
val TitleLarge      = TitleL
val TitleMedium     = TitleM
val BodyMedium      = LabelL
val BodySmall       = LabelS

val HudXLarge       = HudXL
val HudLarge        = HudL
val HudMedium       = HudM
val HudSmall        = HudS

val HUDStyleLarge   = HudL
val HUDStyleMedium  = HudM
val HUDStyleSmall   = HudS

// ─────────────────────────────────────────────
// MATERIAL 3 TYPOGRAPHY MAPPING
// ─────────────────────────────────────────────

val Typography = Typography(
    displayLarge  = DisplayL,
    displayMedium = DisplayM,
    titleLarge    = TitleL,
    titleMedium   = TitleM,
    bodyLarge     = BodyL,
    bodyMedium    = LabelL,
    bodySmall     = LabelS,
    labelLarge    = LabelL,
    labelMedium   = LabelS,
    labelSmall    = LabelS
)
