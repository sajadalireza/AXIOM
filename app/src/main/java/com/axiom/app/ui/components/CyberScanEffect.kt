package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiom.app.ui.theme.AwakenTheme
import com.axiom.app.ui.theme.LocalAxiomColors

enum class ScanMode { AMBIENT, SWEEP, BOOT }

@Composable
fun CyberScanEffect(
    modifier: Modifier = Modifier,
    mode: ScanMode = ScanMode.AMBIENT,
    customColor: Color? = null
) {
    val colors = LocalAxiomColors.current
    val accentColor = customColor ?: colors.systemGreen

    val infiniteTransition = rememberInfiniteTransition(label = "cyber_scan_effect_infinite")

    // Progress for scanning sweeps
    val progress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (mode == ScanMode.BOOT) 2500 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanner_progress1"
    )

    val breathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_breathing"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_glow_pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Grid matrix (drawn for AMBIENT and BOOT)
        if (mode == ScanMode.AMBIENT || mode == ScanMode.BOOT) {
            val gridSpacing = 40.dp.toPx()
            val gridLineColor = accentColor.copy(alpha = 0.02f)
            
            // Vertical grid lines
            var x = 0f
            while (x < w) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }
            
            // Horizontal grid lines
            var y = 0f
            while (y < h) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }

            // Tech Blueprint Dots / Matrix Points
            val dotColor = accentColor.copy(alpha = 0.05f * breathingAlpha)
            val pointsList = mutableListOf<Offset>()
            var dx = 0f
            while (dx < w) {
                var dy = 0f
                while (dy < h) {
                    pointsList.add(Offset(dx, dy))
                    dy += gridSpacing
                }
                dx += gridSpacing
            }
            drawPoints(
                points = pointsList,
                pointMode = PointMode.Points,
                color = dotColor,
                strokeWidth = 2.dp.toPx()
            )
        }

        // 2. Corner reticle frames
        if (mode == ScanMode.AMBIENT || mode == ScanMode.SWEEP) {
            val cornerSize = 24.dp.toPx()
            val cornerColor = accentColor.copy(alpha = 0.15f * glowPulse)
            val strokeW = 1.5.dp.toPx()

            // Top-Left
            drawLine(cornerColor, Offset(16.dp.toPx(), 16.dp.toPx()), Offset(16.dp.toPx() + cornerSize, 16.dp.toPx()), strokeW)
            drawLine(cornerColor, Offset(16.dp.toPx(), 16.dp.toPx()), Offset(16.dp.toPx(), 16.dp.toPx() + cornerSize), strokeW)

            // Top-Right
            drawLine(cornerColor, Offset(w - 16.dp.toPx(), 16.dp.toPx()), Offset(w - 16.dp.toPx() - cornerSize, 16.dp.toPx()), strokeW)
            drawLine(cornerColor, Offset(w - 16.dp.toPx(), 16.dp.toPx()), Offset(w - 16.dp.toPx(), 16.dp.toPx() + cornerSize), strokeW)

            // Bottom-Left
            drawLine(cornerColor, Offset(16.dp.toPx(), h - 16.dp.toPx()), Offset(16.dp.toPx() + cornerSize, h - 16.dp.toPx()), strokeW)
            drawLine(cornerColor, Offset(16.dp.toPx(), h - 16.dp.toPx()), Offset(16.dp.toPx(), h - 16.dp.toPx() - cornerSize), strokeW)

            // Bottom-Right
            drawLine(cornerColor, Offset(w - 16.dp.toPx(), h - 16.dp.toPx()), Offset(w - 16.dp.toPx() - cornerSize, h - 16.dp.toPx()), strokeW)
            drawLine(cornerColor, Offset(w - 16.dp.toPx(), h - 16.dp.toPx()), Offset(w - 16.dp.toPx(), h - 16.dp.toPx() - cornerSize), strokeW)
        }

        // 3. Procedural Sweep Lasers
        val ySweep = h * progress1

        // Horizontal laser lines and glow
        if (mode != ScanMode.AMBIENT) {
            // Sweeping laser glow
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        accentColor.copy(alpha = 0.05f * glowPulse),
                        accentColor.copy(alpha = 0.12f * glowPulse),
                        Color.Transparent
                    ),
                    startY = (ySweep - 80.dp.toPx()).coerceAtLeast(0f),
                    endY = (ySweep + 20.dp.toPx()).coerceAtMost(h)
                ),
                topLeft = Offset(0f, (ySweep - 80.dp.toPx()).coerceAtLeast(0f)),
                size = Size(w, 100.dp.toPx())
            )
        }

        // Sweep Main line
        drawLine(
            color = accentColor.copy(alpha = 0.6f * breathingAlpha),
            start = Offset(0f, ySweep),
            end = Offset(w, ySweep),
            strokeWidth = 2.dp.toPx()
        )

        // Micro status bar indicators
        if (mode == ScanMode.BOOT || mode == ScanMode.SWEEP) {
            drawLine(
                color = accentColor.copy(alpha = 0.8f),
                start = Offset(w * 0.1f, ySweep + 4.dp.toPx()),
                end = Offset(w * 0.3f, ySweep + 4.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Preview
@Composable
fun CyberScanEffectAmbientPreview() {
    AwakenTheme {
        CyberScanEffect(mode = ScanMode.AMBIENT)
    }
}

@Preview
@Composable
fun CyberScanEffectSweepPreview() {
    AwakenTheme {
        CyberScanEffect(mode = ScanMode.SWEEP)
    }
}
