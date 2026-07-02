package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import com.axiom.app.ui.theme.*

private data class MilestoneData(
    val nextRank        : String,
    val levelsRemaining : Int,
    val xpLabel         : String,
    val accentColor     : Color,
    val showPremiumHint : Boolean = false
)

private fun computeMilestone(rankLabel: String, currentLevel: Int): MilestoneData {
    val rank = rankLabel.replace("-Rank", "").trim()
    return when (rank) {
        "E"  -> MilestoneData("D-RANK",   (11  - currentLevel).coerceAtLeast(0), "LV.11 REQUIRED",  CommonGray,    showPremiumHint = true)
        "D"  -> MilestoneData("C-RANK",   (26  - currentLevel).coerceAtLeast(0), "LV.26 REQUIRED",  UncommonTeal,  showPremiumHint = true)
        "C"  -> MilestoneData("B-RANK",   (46  - currentLevel).coerceAtLeast(0), "LV.46 REQUIRED",  RareBlue,      showPremiumHint = true)
        "B"  -> MilestoneData("A-RANK",   (71  - currentLevel).coerceAtLeast(0), "LV.71 REQUIRED",  EpicPurple)
        "A"  -> MilestoneData("S-RANK",   (100 - currentLevel).coerceAtLeast(0), "LV.100 REQUIRED", LegendaryGold)
        else -> MilestoneData("S-CLASS",  0,                                     "MAXIMUM ACHIEVED", LegendaryGold)
    }
}

@Composable
fun NextMilestoneBar(
    currentLevel    : Int,
    rankLabel       : String,
    currentXP       : Int,
    xpToNextLevel   : Int,
    progressPercent : Float,
    onPremiumHintTap: () -> Unit = {},
    modifier        : Modifier = Modifier
) {
    val milestone = remember(rankLabel, currentLevel) { computeMilestone(rankLabel, currentLevel) }
    val rank      = rankLabel.replace("-Rank", "").trim()

    if (rank == "S") {
        Box(
            modifier          = modifier.fillMaxWidth().background(ShadowSurface).padding(16.dp, 10.dp),
            contentAlignment  = Alignment.Center
        ) {
            Text("[ S-CLASS ] MAXIMUM POWER ACHIEVED",
                fontFamily = JetBrainsMono, fontSize = 11.sp,
                color = LegendaryGold, fontWeight = FontWeight.Bold)
        }
        return
    }

    Column(
        modifier = modifier.fillMaxWidth().background(ShadowSurface).padding(16.dp, 10.dp)
    ) {
        Row(
            modifier            = Modifier.fillMaxWidth(),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("NEXT: ${milestone.nextRank}",
                    fontFamily = JetBrainsMono, fontSize = 11.sp,
                    color = milestone.accentColor, fontWeight = FontWeight.Bold)
                Text(milestone.xpLabel,
                    fontFamily = JetBrainsMono, fontSize = 10.sp, color = TextDim)
            }
            Box(modifier = Modifier.weight(1f).padding(horizontal = 12.dp).height(12.dp)) {
                XPProgressBar(
                    currentXP = currentXP,
                    maxXP = xpToNextLevel,
                    rankColor = milestone.accentColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text("${milestone.levelsRemaining} LVL",
                fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
        }

        if (com.axiom.app.core.FeatureFlags.PREMIUM_PURCHASE_ENABLED && milestone.showPremiumHint) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable { onPremiumHintTap() }
                    .border(0.5.dp, BorderFaint, RoundedCornerShape(3.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🔒", fontSize = 11.sp)
                Text("Streak Shield · AXIOM PREMIUM",
                    fontFamily = JetBrainsMono, fontSize = 10.sp,
                    color = LegendaryGold.copy(alpha = 0.7f))
                Spacer(Modifier.weight(1f))
                Text("→", fontFamily = JetBrainsMono, fontSize = 10.sp, color = TextDim)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(thickness = 0.5.dp, color = BorderFaint)
    }
}

@Composable
fun XPProgressBar(
    currentXP: Int,
    maxXP: Int,
    rankColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (maxXP > 0) (currentXP.toFloat() / maxXP.toFloat()).coerceIn(0f, 1f) else 0f
    
    val infiniteTransition = rememberInfiniteTransition(label = "leading_segment_shimmer")
    val shimmerVal by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_val"
    )

    val borderFaint = LocalAxiomColors.current.borderFaint

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .semantics {
                contentDescription = "XP progress: $currentXP of $maxXP"
            }
            .testTag("xp_progress_bar")
    ) {
        val totalWidth = size.width
        val height = size.height
        val numSegments = 10
        val spacingPx = 3.dp.toPx()
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
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            // Draw filled segment part if segmentFill > 0f
            if (segmentFill > 0f) {
                val filledWidth = rectWidth * segmentFill
                val drawTopLeftX = if (isRtl) startX + segmentWidth - filledWidth else startX
                
                // Segment gradient
                val brush = Brush.horizontalGradient(
                    colors = if (isRtl) listOf(rankColor.copy(alpha = 0.7f), rankColor) else listOf(rankColor, rankColor.copy(alpha = 0.7f)),
                    startX = if (isRtl) startX + segmentWidth else startX,
                    endX = if (isRtl) startX + segmentWidth - filledWidth else startX + filledWidth
                )

                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(drawTopLeftX, 0f),
                    size = androidx.compose.ui.geometry.Size(filledWidth, height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )

                // Leading segment shimmer
                val isLeading = i == fullyFilledCount || (i == fullyFilledCount - 1 && partialFillFraction == 0f)
                if (isLeading) {
                    val shimmerStartX = if (isRtl) (startX + segmentWidth) - filledWidth * shimmerVal else startX + filledWidth * shimmerVal
                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.5f), Color.Transparent),
                        start = Offset(shimmerStartX, 0f),
                        end = if (isRtl) Offset(shimmerStartX - filledWidth * 0.5f, height) else Offset(shimmerStartX + filledWidth * 0.5f, height)
                    )
                    drawRoundRect(
                        brush = shimmerBrush,
                        topLeft = Offset(drawTopLeftX, 0f),
                        size = androidx.compose.ui.geometry.Size(filledWidth, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }
        }

        // Milestone notch at 50% and 100%
        // 50% is between segment 4 and 5
        val notchX50 = 5 * (segmentWidth + spacingPx) - spacingPx / 2f
        val notchX100 = totalWidth

        val notchHalfHeight = 4.dp.toPx()
        val notchWidth = 2.dp.toPx()

        // 50% notch: vertical tick
        val notch50Color = if (progress >= 0.5f) rankColor else borderFaint
        drawLine(
            color = notch50Color,
            start = Offset(notchX50, -notchHalfHeight),
            end = Offset(notchX50, height + notchHalfHeight),
            strokeWidth = notchWidth
        )

        // 100% notch: vertical tick at the very end
        val notch100Color = if (progress >= 1.0f) rankColor else borderFaint
        drawLine(
            color = notch100Color,
            start = Offset(notchX100, -notchHalfHeight),
            end = Offset(notchX100, height + notchHalfHeight),
            strokeWidth = notchWidth
        )
    }
}
