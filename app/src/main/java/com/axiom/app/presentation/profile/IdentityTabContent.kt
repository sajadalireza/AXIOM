package com.axiom.app.presentation.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.theme.*
import java.util.Calendar

@Composable
fun IdentityTabContent(
    hunter: Hunter,
    completedMissions: List<Mission>,
    activePersona: String,
    onEditThesis: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val rawColor = rankColorMap[hunter.rankLabel] ?: Color(hunter.rankColor)
    val rankColor = if (hunter.rankLabel.contains("s", ignoreCase = true)) LegendaryGold else rawColor

    var thesisExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Full-width rank glyph at 120sp, centered, animated with slow rotation (0.1 RPM)
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotationAngle"
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer { rotationZ = rotationAngle },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = hunter.rankGlyph.ifEmpty { "⬟" },
                color = rankColor,
                fontSize = 120.sp,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }

        // Rank label in DisplayXL Fraunces
        Text(
            text = hunter.rankLabel,
            style = DisplayXL,
            color = rankColor,
            textAlign = TextAlign.Center
        )

        // Hunter Name
        Text(
            text = hunter.name.uppercase(),
            style = TitleL,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        // Active Persona
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "ACTIVE PERSONA",
                style = LabelS,
                color = TextDim,
                letterSpacing = 1.sp
            )
            Text(
                text = activePersona,
                style = HudM,
                color = SystemGreen
            )
        }

        // Personal thesis in BodyM Outfit Italic, TextSecondary, 2-line max with expand button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderFaint, RoundedCornerShape(8.dp))
                .background(colors.shadowSurface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERSONAL THESIS",
                    style = LabelS,
                    color = rankColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "EDIT",
                    style = HudS,
                    color = SystemGreen,
                    modifier = Modifier
                        .clickable(onClick = onEditThesis)
                        .padding(vertical = 4.dp)
                )
            }

            Text(
                text = hunter.personalThesis.ifEmpty { "No personal thesis defined yet — tap EDIT to set your north star." },
                style = TextStyle(
                    fontFamily = Outfit,
                    fontSize = 14.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp
                ),
                color = TextSecondary,
                maxLines = if (thesisExpanded) Int.MAX_VALUE else 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            if (hunter.personalThesis.isNotEmpty()) {
                Text(
                    text = if (thesisExpanded) "COLLAPSE" else "EXPAND THESIS",
                    style = HudS,
                    color = SystemGreen,
                    modifier = Modifier
                        .clickable { thesisExpanded = !thesisExpanded }
                        .padding(vertical = 4.dp)
                )
            }
        }

        // 5-day XP sparkline chart below
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderFaint, RoundedCornerShape(8.dp))
                .background(colors.shadowSurface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "5-DAY XP MOMENTUM",
                style = LabelS,
                color = rankColor,
                letterSpacing = 1.sp
            )

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val last5DaysXP = (0..4).map { dayOffset ->
                val dayStart = today.timeInMillis - dayOffset * 24 * 60 * 60 * 1000L
                val dayEnd = dayStart + 24 * 60 * 60 * 1000L
                completedMissions.filter { mission ->
                    val completedTime = mission.completedAt ?: 0L
                    completedTime in dayStart until dayEnd
                }.sumOf { it.xpReward }.toFloat()
            }.reversed()

            val hasActivity = last5DaysXP.any { it > 0f }
            val displayPoints = if (hasActivity) last5DaysXP else listOf(0f, 0f, 0f, 0f, 0f)

            if (!hasActivity) {
                Text(
                    text = "No recent mission completions. Complete daily missions to see progress sparkline.",
                    style = LabelS,
                    color = TextDim,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )
            }

            // Draw sparkline
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                val width = size.width
                val height = size.height
                val padding = 16.dp.toPx()

                val minVal = displayPoints.minOrNull() ?: 0f
                val maxVal = displayPoints.maxOrNull() ?: 10f
                val delta = if (maxVal == minVal) 10f else (maxVal - minVal)

                val path = Path()
                displayPoints.forEachIndexed { index, val_ ->
                    val x = padding + (index.toFloat() / 4f) * (width - 2 * padding)
                    val y = height - padding - ((val_ - minVal) / delta) * (height - 2 * padding)
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                // Smooth line
                drawPath(
                    path = path,
                    color = rankColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Glowing points
                displayPoints.forEachIndexed { index, val_ ->
                    val x = padding + (index.toFloat() / 4f) * (width - 2 * padding)
                    val y = height - padding - ((val_ - minVal) / delta) * (height - 2 * padding)
                    drawCircle(
                        color = rankColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = rankColor.copy(alpha = 0.3f),
                        radius = 8.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("4D AGO", style = LabelS, color = TextDim)
                Text("3D AGO", style = LabelS, color = TextDim)
                Text("2D AGO", style = LabelS, color = TextDim)
                Text("YESTERDAY", style = LabelS, color = TextDim)
                Text("TODAY", style = LabelS, color = TextDim)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
