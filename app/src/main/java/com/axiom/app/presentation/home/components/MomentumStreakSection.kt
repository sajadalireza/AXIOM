package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.core.localization.AxiomDateFormatter
import com.axiom.app.ui.theme.*

/**
 * Layer 3 Progress Surface: Momentum & Streak 7-day dot timeline.
 * Matches binding PO visual target 01_home_target.jpg.
 */
@Composable
fun MomentumStreakSection(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val isFa = java.util.Locale.getDefault().language == "fa"
    val streakTitle = stringResource(R.string.home_momentum_and_streak)
    val streakCountText = stringResource(R.string.home_streak_day_count, streakDays)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("momentum_streak_card")
            .semantics {
                contentDescription = "$streakTitle: $streakCountText"
            },
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = BorderStroke(1.dp, colors.borderFaint),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: "MOMENTUM & STREAK"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = streakTitle,
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary,
                    letterSpacing = 1.sp
                )
            }

            // Streak Metric: Flame + Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🔥",
                    fontSize = 22.sp
                )
                Text(
                    text = streakCountText,
                    fontFamily = Outfit,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 28.sp
                )
            }

            // 7-Day Dot Timeline
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Connecting track line behind dots
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-11).dp)
                        .height(2.dp)
                        .background(colors.dimSurface)
                )

                // 7 day indicator nodes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (day in 1..7) {
                        val isDayComplete = day <= streakDays
                        val isCurrentDay = day == streakDays
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isCurrentDay) 14.dp else 10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrentDay -> colors.legendaryGold
                                            isDayComplete -> colors.systemGreen
                                            else -> colors.dimSurface
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCurrentDay) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(colors.shadowSurface)
                                    )
                                }
                            }

                            Text(
                                text = if (isFa) AxiomDateFormatter.toPersianDigits(day.toString()) else day.toString(),
                                fontFamily = Outfit,
                                fontSize = 11.sp,
                                fontWeight = if (isDayComplete) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isDayComplete) colors.textPrimary else colors.textDim
                            )
                        }
                    }
                }
            }
        }
    }
}
