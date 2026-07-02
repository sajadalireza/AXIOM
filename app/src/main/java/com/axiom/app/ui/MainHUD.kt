package com.axiom.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun MainHUD(
    viewModel: AxiomViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state is AxiomUiState.Success) {
        val success = state as AxiomUiState.Success
        MainHUDContent(
            hunter = success.hunter,
            streak = success.streak,
            modifier = modifier
        )
    }
}

@Composable
fun MainHUDContent(
    hunter: Hunter,
    streak: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else (-40).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "hud_offset"
    )

    val colors = LocalAxiomColors.current
    val resolvedRankColor = Color(hunter.rankColor.toLong())

    Row(
        modifier = modifier
            .offset(y = offsetY)
            .fillMaxWidth()
            .height(40.dp)
            .background(colors.shadowSurface)
            .border(width = 1.dp, color = colors.borderFaint)
            .padding(horizontal = 12.dp)
            .testTag("main_hud_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Level in HudS FiraCode (e.g. ⚔ Lv.23)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "⚔ Lv.${hunter.level}",
                style = HudS,
                fontFamily = FiraCode,
                color = colors.systemGreen,
                fontWeight = FontWeight.Bold
            )
        }

        // 2. XP bar (custom, 120dp width) with overshoot animation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val progressPercent = if (hunter.xpToNextLevel > 0) {
                hunter.currentXP.toFloat() / hunter.xpToNextLevel.toFloat()
            } else {
                0f
            }

            // Custom Easing with Overshoot tension of 1.3f (approx 5% overshoot at t=0.7)
            val overshootEasing = remember {
                Easing { t ->
                    val tMinusOne = t - 1f
                    val tension = 1.3f
                    tMinusOne * tMinusOne * ((tension + 1f) * tMinusOne + tension) + 1f
                }
            }

            val animatedProgress by animateFloatAsState(
                targetValue = progressPercent.coerceIn(0f, 1f),
                animationSpec = tween(durationMillis = 400, easing = overshootEasing),
                label = "hud_xp_progress"
            )

            CompactXPBar(
                progress = animatedProgress,
                rankColor = resolvedRankColor,
                modifier = Modifier
                    .width(120.dp)
                    .height(8.dp)
            )

            Text(
                text = "${hunter.currentXP}/${hunter.xpToNextLevel} XP",
                style = HudSmall,
                fontFamily = FiraCode,
                color = colors.textSecondary
            )
        }

        // 3. Streak count with flame icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🔥 ${streak}d",
                style = HudS,
                fontFamily = FiraCode,
                color = LegendaryGold,
                fontWeight = FontWeight.Bold
            )
        }

        // 4. Rank badge [RANK: B]
        Box(
            modifier = Modifier
                .border(1.dp, resolvedRankColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            val displayRank = hunter.rankLabel.replace("-Rank", "").trim()
            Text(
                text = "RANK: $displayRank",
                style = HudSmall,
                fontFamily = FiraCode,
                color = resolvedRankColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CompactXPBar(
    progress: Float,
    rankColor: Color,
    modifier: Modifier = Modifier
) {
    val borderFaint = LocalAxiomColors.current.borderFaint

    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val height = size.height
        val numSegments = 10
        val spacingPx = 2.dp.toPx()
        val segmentWidth = (totalWidth - spacingPx * (numSegments - 1)) / numSegments

        val filledSegmentsFloat = progress * numSegments
        val fullyFilledCount = filledSegmentsFloat.toInt()
        val partialFillFraction = filledSegmentsFloat - fullyFilledCount
        val isRtl = layoutDirection == LayoutDirection.Rtl

        for (i in 0 until numSegments) {
            val startX = if (isRtl) totalWidth - (i + 1) * (segmentWidth + spacingPx) + spacingPx else i * (segmentWidth + spacingPx)
            val rectWidth = segmentWidth

            // Determine filled amount of this segment
            val segmentFill = when {
                i < fullyFilledCount -> 1.0f
                i == fullyFilledCount -> partialFillFraction
                else -> 0.0f
            }

            // Draw unfilled segment background first
            drawRoundRect(
                color = borderFaint,
                topLeft = Offset(startX, 0f),
                size = androidx.compose.ui.geometry.Size(rectWidth, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx(), 1.dp.toPx())
            )

            // Draw filled segment part if segmentFill > 0f
            if (segmentFill > 0f) {
                val filledWidth = rectWidth * segmentFill
                val drawTopLeftX = if (isRtl) startX + segmentWidth - filledWidth else startX
                val brush = Brush.horizontalGradient(
                    colors = if (isRtl) listOf(rankColor.copy(alpha = 0.7f), rankColor) else listOf(rankColor, rankColor.copy(alpha = 0.7f)),
                    startX = if (isRtl) startX + segmentWidth else startX,
                    endX = if (isRtl) startX + segmentWidth - filledWidth else startX + filledWidth
                )

                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(drawTopLeftX, 0f),
                    size = androidx.compose.ui.geometry.Size(filledWidth, height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx(), 1.dp.toPx())
                )
            }
        }
    }
}
