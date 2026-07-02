package com.axiom.app.presentation.profile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.BorderFaint
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.SystemGreen
import com.axiom.app.ui.theme.TextDim

@Composable
fun StatRadarChart(stats: Map<String, Float>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val labels = remember { listOf("STR", "INT", "VIT", "AGI", "PER", "LUK") }

    // Animate the value polygon from 0 to actual values using animateFloatAsState with spring
    val animatedValues = labels.map { label ->
        val target = stats[label] ?: 0f
        animateFloatAsState(
            targetValue = target,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "radar_anim_$label"
        )
    }

    Canvas(
        modifier = modifier
            .size(200.dp)
            .testTag("stat_radar_chart")
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.width / 2f * 0.7f // leave room for labels at tips

        // 1. Background grid: 5 concentric hexagons at 20%, 40%, 60%, 80%, 100% scale
        val gridColor = BorderFaint.copy(alpha = 0.4f)
        val strokeWidthPx = 1.dp.toPx()

        for (scaleIdx in 1..5) {
            val scale = scaleIdx * 0.2f
            val path = Path()
            for (i in 0 until 6) {
                val angleRad = Math.toRadians((i * 60.0) - 90.0)
                val x = center.x + (maxRadius * scale * kotlin.math.cos(angleRad)).toFloat()
                val y = center.y + (maxRadius * scale * kotlin.math.sin(angleRad)).toFloat()
                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()
            drawPath(
                path = path,
                color = gridColor,
                style = Stroke(width = strokeWidthPx)
            )
        }

        // 2. Axis lines: 6 lines from center to each vertex, same color
        for (i in 0 until 6) {
            val angleRad = Math.toRadians((i * 60.0) - 90.0)
            val vertexX = center.x + (maxRadius * kotlin.math.cos(angleRad)).toFloat()
            val vertexY = center.y + (maxRadius * kotlin.math.sin(angleRad)).toFloat()
            drawLine(
                color = gridColor,
                start = center,
                end = Offset(vertexX, vertexY),
                strokeWidth = strokeWidthPx
            )
        }

        // 3. Value polygon: filled with SystemGreen.copy(0.15f), bordered with SystemGreen, 2dp stroke
        val valuePath = Path()
        var hasValidPoints = false
        for (i in 0 until 6) {
            val animValue = animatedValues[i].value
            val angleRad = Math.toRadians((i * 60.0) - 90.0)
            val r = maxRadius * animValue
            val x = center.x + (r * kotlin.math.cos(angleRad)).toFloat()
            val y = center.y + (r * kotlin.math.sin(angleRad)).toFloat()

            if (i == 0) {
                valuePath.moveTo(x, y)
                hasValidPoints = true
            } else {
                valuePath.lineTo(x, y)
            }
        }
        if (hasValidPoints) {
            valuePath.close()
            drawPath(
                path = valuePath,
                color = SystemGreen.copy(alpha = 0.15f)
            )
            drawPath(
                path = valuePath,
                color = SystemGreen,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // 4. Each stat vertex has a small label (9sp JetBrainsMono TextDim) at the tip
        for (i in 0 until 6) {
            val label = labels[i]
            val angleRad = Math.toRadians((i * 60.0) - 90.0)

            // Push labels slightly outside the 100% vertex bounds
            val labelSpacing = 12.dp.toPx()
            val tipX = center.x + ((maxRadius + labelSpacing) * kotlin.math.cos(angleRad)).toFloat()
            val tipY = center.y + ((maxRadius + labelSpacing) * kotlin.math.sin(angleRad)).toFloat()

            val textLayoutResult = textMeasurer.measure(
                text = label,
                style = TextStyle(
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = TextDim
                )
            )

            val textWidth = textLayoutResult.size.width
            val textHeight = textLayoutResult.size.height

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    tipX - textWidth / 2f,
                    tipY - textHeight / 2f
                )
            )
        }
    }
}
