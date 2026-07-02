package com.axiom.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * A beautiful, aggressive, high-fantasy Solo Leveling 'System Terminal' Shape
 * featuring 45-degree corner bevels and customizable runic center-indent notches.
 */
class SoloLevelingBeveledShape(
    val bevel: Float = 24f,
    val showSideNotches: Boolean = true
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@apply

            val isRtl = layoutDirection == LayoutDirection.Rtl
            // Symmetrical shape remains balanced in RTL/LTR, but we explicitly account for RTL
            val startBevel = if (isRtl) w - bevel else bevel
            val endBevel = if (isRtl) bevel else w - bevel

            // Top-Left/Right bevel start depending on layout direction
            moveTo(0f, bevel)
            lineTo(bevel, 0f)

            // Top edge to Top-Right
            lineTo(w - bevel, 0f)
            lineTo(w, bevel)

            // Right edge with system notch cut-in (mirrored in RTL)
            if (showSideNotches && h > bevel * 3) {
                val midY = h / 2f
                val notchSize = bevel * 0.4f
                lineTo(w, midY - notchSize)
                lineTo(w - notchSize, midY)
                lineTo(w, midY + notchSize)
            }

            // Right edge to Bottom-Right
            lineTo(w, h - bevel)
            lineTo(w - bevel, h)

            // Bottom edge to Bottom-Left
            lineTo(bevel, h)
            lineTo(0f, h - bevel)

            // Left edge with system notch cut-in (mirrored in RTL)
            if (showSideNotches && h > bevel * 3) {
                val midY = h / 2f
                val notchSize = bevel * 0.4f
                lineTo(0f, midY + notchSize)
                lineTo(notchSize, midY)
                lineTo(0f, midY - notchSize)
            }

            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Renders an immersive, glowing Solo Leveling-inspired container background,
 * with beveled corners, multi-layered energy glows, and sci-fi structural tick lines.
 */
fun Modifier.soloLevelingCard(
    accentColor: Color,
    bevel: Float = 24f,
    borderWidth: Float = 2f,
    glowRadius: Float = 8f,
    showSideNotches: Boolean = true,
    backgroundColor: Color = Color(0xFF0F111A) // Deep Void Black background
) = this.drawBehind {
    val w = size.width
    val h = size.height
    if (w <= 0f || h <= 0f) return@drawBehind

    val isRtl = layoutDirection == LayoutDirection.Rtl

    // Compute active bevel size safely
    val activeBevel = bevel.coerceAtMost(h / 3f).coerceAtMost(w / 3f)

    // Build the primary high-fantasy geometric shape path with RTL support
    val path = Path().apply {
        moveTo(0f, activeBevel)
        lineTo(activeBevel, 0f)
        lineTo(w - activeBevel, 0f)
        lineTo(w, activeBevel)

        if (showSideNotches && h > activeBevel * 3) {
            val midY = h / 2f
            val notchSize = activeBevel * 0.4f
            lineTo(w, midY - notchSize)
            lineTo(w - notchSize, midY)
            lineTo(w, midY + notchSize)
        }

        lineTo(w, h - activeBevel)
        lineTo(w - activeBevel, h)
        lineTo(activeBevel, h)
        lineTo(0f, h - activeBevel)

        if (showSideNotches && h > activeBevel * 3) {
            val midY = h / 2f
            val notchSize = activeBevel * 0.4f
            lineTo(0f, midY + notchSize)
            lineTo(notchSize, midY)
            lineTo(0f, midY - notchSize)
        }
        close()
    }

    // 1. Draw cosmic void background
    drawPath(
        path = path,
        color = backgroundColor
    )

    // 2. Multi-Pass Neon Glow Bloom (Layered stroke alphas to prevent Canvas CPU lag)
    if (glowRadius > 0) {
        val glowLevels = listOf(0.04f, 0.08f, 0.12f)
        glowLevels.forEachIndexed { index, alpha ->
            val scalePadding = (index + 1) * (glowRadius / 3f)
            drawPath(
                path = path,
                color = accentColor.copy(alpha = alpha),
                style = Stroke(width = borderWidth + scalePadding)
            )
        }
    }

    // 3. Draw high-fantasy border stroke
    drawPath(
        path = path,
        color = accentColor,
        style = Stroke(width = borderWidth)
    )

    // 4. Render 'System Terminal' structural tick marks at the beveled joints (Runic Marks)
    val ext = 5f
    // Top-Left corner ticks
    drawLine(color = accentColor, start = Offset(0f, activeBevel), end = Offset(-ext, activeBevel + ext), strokeWidth = 2f)
    drawLine(color = accentColor, start = Offset(activeBevel, 0f), end = Offset(activeBevel + ext, -ext), strokeWidth = 2f)

    // Top-Right corner ticks
    drawLine(color = accentColor, start = Offset(w - activeBevel, 0f), end = Offset(w - activeBevel - ext, -ext), strokeWidth = 2f)
    drawLine(color = accentColor, start = Offset(w, activeBevel), end = Offset(w + ext, activeBevel + ext), strokeWidth = 2f)

    // Bottom-Left corner ticks
    drawLine(color = accentColor, start = Offset(0f, h - activeBevel), end = Offset(-ext, h - activeBevel - ext), strokeWidth = 2f)
    drawLine(color = accentColor, start = Offset(activeBevel, h), end = Offset(activeBevel + ext, h + ext), strokeWidth = 2f)

    // Bottom-Right corner ticks
    drawLine(color = accentColor, start = Offset(w - activeBevel, h), end = Offset(w - activeBevel - ext, h + ext), strokeWidth = 2f)
    drawLine(color = accentColor, start = Offset(w, h - activeBevel), end = Offset(w + ext, h - activeBevel - ext), strokeWidth = 2f)
}
