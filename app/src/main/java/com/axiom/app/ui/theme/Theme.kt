package com.axiom.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

enum class ThemeMode { SYSTEM, LIGHT, DARK }

private fun materialSchemeFor(colors: AxiomColorScheme, dark: Boolean) =
    if (dark) {
        darkColorScheme(
            primary = colors.legendaryGold,
            secondary = colors.systemGreen,
            tertiary = colors.epicPurple,
            background = colors.voidBlack,
            surface = colors.shadowSurface,
            surfaceVariant = colors.dimSurface,
            error = colors.penaltyRed,
            onPrimary = colors.voidBlack,
            onSecondary = colors.voidBlack,
            onTertiary = colors.voidBlack,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            onSurfaceVariant = colors.textSecondary,
            onError = colors.textPrimary,
            outline = colors.borderFaint,
        )
    } else {
        lightColorScheme(
            primary = colors.legendaryGold,
            secondary = colors.systemGreen,
            tertiary = colors.epicPurple,
            background = colors.voidBlack,
            surface = colors.shadowSurface,
            surfaceVariant = colors.dimSurface,
            error = colors.penaltyRed,
            onPrimary = colors.shadowSurface,
            onSecondary = colors.shadowSurface,
            onTertiary = colors.shadowSurface,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            onSurfaceVariant = colors.textSecondary,
            onError = colors.textPrimary,
            outline = colors.borderFaint,
        )
    }

@Composable
fun AwakenTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val axiomColors = if (darkTheme) AxiomDarkColors else AxiomLightColors

    CompositionLocalProvider(LocalAxiomColors provides axiomColors) {
        MaterialTheme(
            colorScheme = materialSchemeFor(axiomColors, darkTheme),
            typography = Typography,
            shapes = AwakenShapes,
            content = content
        )
    }
}
