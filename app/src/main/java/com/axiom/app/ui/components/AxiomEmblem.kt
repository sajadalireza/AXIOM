package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiom.app.ui.theme.RareBlue
import com.axiom.app.ui.theme.SystemGreen

/**
 * AXIOM Cybernetic Crest Emblem.
 *
 * A high-fidelity animated vector insignia representing the Hunter Awakening System.
 * Renders an angular, faceted delta with a plasma slash and breathing neon glow.
 */
@Composable
fun AxiomEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    pulse: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AxiomEmblemPulse")
    val glowAlpha by if (pulse) {
        infiniteTransition.animateFloat(
            initialValue = 0.45f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0.85f) }
    }

    val glowScale by if (pulse) {
        infiniteTransition.animateFloat(
            initialValue = 0.97f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowScale"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
    }

    val neonGradient = Brush.linearGradient(
        colors = listOf(
            SystemGreen,
            RareBlue,
            Color(0xFF34D399)
        ),
        start = Offset(0f, 0f),
        end = Offset(200f, 200f)
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f

        // Outer Delta Path
        val deltaPath = Path().apply {
            moveTo(cx, h * 0.10f)
            lineTo(w * 0.86f, h * 0.88f)
            lineTo(w * 0.58f, h * 0.88f)
            lineTo(cx, h * 0.60f)
            lineTo(w * 0.42f, h * 0.88f)
            lineTo(w * 0.14f, h * 0.88f)
            close()
        }

        // Plasma Strike / Cyber Slash Path
        val slashPath = Path().apply {
            moveTo(w * 0.08f, h * 0.62f)
            lineTo(w * 0.94f, h * 0.42f)
        }

        // Inner Core Crystal
        val corePath = Path().apply {
            moveTo(cx, h * 0.32f)
            lineTo(cx + w * 0.10f, h * 0.46f)
            lineTo(cx, h * 0.58f)
            lineTo(cx - w * 0.10f, h * 0.46f)
            close()
        }

        // 1. Ambient Glow Halo (Thick diffuse stroke)
        drawPath(
            path = deltaPath,
            color = SystemGreen.copy(alpha = glowAlpha * 0.25f),
            style = Stroke(
                width = 12.dp.toPx() * glowScale,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 2. Secondary Glow Halo
        drawPath(
            path = deltaPath,
            color = RareBlue.copy(alpha = glowAlpha * 0.40f),
            style = Stroke(
                width = 6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 3. Crisp Primary Stroke
        drawPath(
            path = deltaPath,
            brush = neonGradient,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 4. Draw Slash
        drawPath(
            path = slashPath,
            color = Color(0xFF6EE7B7).copy(alpha = glowAlpha),
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // 5. Draw Core Crystal Fill & Stroke
        drawPath(
            path = corePath,
            color = SystemGreen.copy(alpha = 0.25f)
        )
        drawPath(
            path = corePath,
            color = Color.White.copy(alpha = glowAlpha * 0.9f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
