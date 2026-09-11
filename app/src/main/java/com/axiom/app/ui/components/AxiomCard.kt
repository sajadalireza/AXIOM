package com.axiom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.axiom.app.ui.theme.AxiomBorder
import com.axiom.app.ui.theme.AxiomRadius
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.LocalAxiomReducedMotion
import com.axiom.app.ui.theme.SoloLevelingBeveledShape
import com.axiom.app.ui.theme.soloLevelingCard

/**
 * Canonical Cyber-Fantasy Card Container for AXIOM.
 *
 * Consolidates fragmented card and panel implementations across critical screens
 * into a single unified component path (E2.5 / G3-P5).
 *
 * Supports:
 * - Clean Cyber rounded cards ([showBevel] = false)
 * - Aggressive Solo Leveling beveled rune cards ([showBevel] = true)
 * - Optional glow pulse on hover/activation ([glowEnabled] = true)
 * - Optional scanlines overlay ([showScanlines] = true)
 * - Dynamic font scaling resilience and minimum touch targets
 */
@Composable
fun AxiomCard(
    modifier: Modifier = Modifier,
    accentColor: Color = LocalAxiomColors.current.borderFaint,
    glowEnabled: Boolean = false,
    backgroundColor: Color = LocalAxiomColors.current.shadowSurface,
    borderColor: Color = if (glowEnabled) accentColor else LocalAxiomColors.current.borderFaint,
    borderWidth: Dp = if (glowEnabled) AxiomBorder.medium else AxiomBorder.thin,
    shape: Shape = RoundedCornerShape(AxiomRadius.l),
    showBevel: Boolean = false,
    showScanlines: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val reducedMotion = LocalAxiomReducedMotion.current
    val effectiveGlow = if (reducedMotion) false else glowEnabled
    val effectiveScanlines = if (reducedMotion) false else showScanlines

    val effectiveShape = if (showBevel) {
        SoloLevelingBeveledShape(bevel = 16f, showSideNotches = true)
    } else {
        shape
    }

    val effectiveBorderColor = if (effectiveGlow) accentColor else borderColor
    val effectiveBorderWidth = if (effectiveGlow) AxiomBorder.medium else borderWidth

    val baseModifier = if (showBevel) {
        modifier
            .soloLevelingCard(
                accentColor = effectiveBorderColor,
                bevel = 16f,
                borderWidth = effectiveBorderWidth.value,
                glowRadius = if (effectiveGlow) 8f else 0f,
                showSideNotches = true,
                backgroundColor = backgroundColor
            )
            .clip(effectiveShape)
    } else {
        modifier
            .clip(effectiveShape)
            .background(backgroundColor)
            .border(effectiveBorderWidth, effectiveBorderColor, effectiveShape)
    }

    val interactiveModifier = if (onClick != null) {
        baseModifier.clickable(onClick = onClick)
    } else {
        baseModifier
    }

    Box(modifier = interactiveModifier) {
        if (effectiveScanlines) {
            AnimatedScanlineOverlay(modifier = Modifier.matchParentSize())
        }
        content()
    }
}
