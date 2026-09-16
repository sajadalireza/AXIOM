package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.core.localization.AxiomDateFormatter
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.theme.*

/**
 * Layer 3 — PROGRESS: Truthful G3 execution and hunter level progression card.
 *
 * Requirements:
 * - Subordinate to the Next Meaningful Mission CTA.
 * - Labels progression truthfully as Level Progress (XP progression), never fabricated Goal Progress.
 * - Does NOT expose AX-030 Streak/Weekly Challenges or AX-021 Weekly Review (owned by G5).
 * - Binds to real hunter progress and active mission commitments.
 * - Responsive to 200% font scale without text truncation.
 */
@Composable
fun HomeProgressCard(
    hunter: Hunter,
    activeMissionsCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val progress = hunter.progressPercent.coerceIn(0f, 1f)
    val isFa = java.util.Locale.getDefault().language == "fa"
    val fontScale = LocalDensity.current.fontScale

    val levelStr = if (isFa) AxiomDateFormatter.toPersianDigits(hunter.level.toString()) else hunter.level.toString()
    val percentInt = (progress * 100).toInt()
    val percentStr = if (isFa) AxiomDateFormatter.toPersianDigits(percentInt.toString()) else percentInt.toString()
    val activeMissionsStr = if (isFa) AxiomDateFormatter.toPersianDigits(activeMissionsCount.toString()) else activeMissionsCount.toString()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("home_progress_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = BorderStroke(1.dp, colors.borderFaint),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: "PROGRESS" + Level badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_progress_header),
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.legendaryGold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = stringResource(R.string.home_progress_level_format, levelStr),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }

            // Metrics row: Level Progress percent + Active missions count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = stringResource(R.string.home_progress_level_label),
                        fontFamily = Outfit,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "$percentStr%",
                        fontFamily = Outfit,
                        fontSize = if (fontScale > 1.3f) 20.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = stringResource(R.string.home_progress_missions_label),
                        fontFamily = Outfit,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = stringResource(R.string.home_progress_missions_active, activeMissionsStr),
                        fontFamily = Outfit,
                        fontSize = if (fontScale > 1.3f) 13.sp else 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.systemGreen
                    )
                }
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.dimSurface)
            ) {
                if (progress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colors.systemGreen, colors.legendaryGold)
                                )
                            )
                    )
                }
            }
        }
    }
}
