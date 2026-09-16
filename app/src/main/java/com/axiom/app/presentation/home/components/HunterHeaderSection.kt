package com.axiom.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.theme.*

/**
 * Layer 1 NOW: Compact Hunter Identity Header matching 01_home_target.jpg.
 *
 * Features:
 * - Avatar profile link (>=48dp touch target)
 * - Hunter name + clean canonical Rank Capsule (e.g. [ RECRUIT ] or [ E-Rank ])
 * - Subordinate XP counter and sleek horizontal progress bar
 * - Truthful data presentation and WCAG-compliant localization
 */
@Composable
fun HunterHeaderSection(
    hunter: Hunter,
    streakDays: Int,
    streakMultiplier: Float,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val progress = hunter.progressPercent.coerceIn(0f, 1f)
    val isFa = java.util.Locale.getDefault().language == "fa"
    val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale

    val defaultName = stringResource(R.string.home_hunter_default_name)
    val defaultRank = stringResource(R.string.home_hunter_default_rank)
    val displayName = hunter.name.ifBlank { defaultName }
    val rankText = hunter.rankLabel.ifBlank { defaultRank }
    val levelDisplay = if (isFa) com.axiom.app.core.localization.AxiomDateFormatter.toPersianDigits(hunter.level.toString()) else hunter.level.toString()
    val progressPercentInt = (progress * 100).toInt()
    val progressDisplay = if (isFa) com.axiom.app.core.localization.AxiomDateFormatter.toPersianDigits(progressPercentInt.toString()) else progressPercentInt.toString()
    val headerContentDesc = stringResource(
        R.string.home_hunter_profile_cd,
        displayName,
        rankText,
        levelDisplay
    )
    val levelProgressCd = stringResource(
        R.string.home_hunter_level_cd,
        levelDisplay,
        progressDisplay
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hunter_header_banner")
            .semantics {
                contentDescription = headerContentDesc
            },
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AXIOM Brand Atmospheric Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "▲",
                    fontSize = 11.sp,
                    color = colors.systemGreen
                )
                Text(
                    text = "A X I O M",
                    fontFamily = Outfit,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    letterSpacing = 4.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.axiom_tagline),
                fontFamily = Outfit,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary,
                letterSpacing = 0.5.sp
            )
        }

        // Identity Row: [Avatar] [Name + Rank] [Circular Gold Level Arc]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Circular Avatar Profile Target (>=48dp touch area)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateToProfile() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(colors.dimSurface)
                        .border(1.dp, colors.borderFaint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_nav_habits),
                        contentDescription = stringResource(R.string.home_hunter_profile_icon_cd),
                        tint = colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Name & Rank Pill
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = displayName,
                    fontFamily = Outfit,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Canonical Rank Capsule Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.systemGreen.copy(alpha = 0.15f))
                            .border(1.dp, colors.systemGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = rankText,
                            fontFamily = Outfit,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.systemGreen,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // XP Counter Text
                    val currentXpDisplay = if (isFa) com.axiom.app.core.localization.AxiomDateFormatter.toPersianDigits(hunter.currentXP.toString()) else hunter.currentXP.toString()
                    val nextXpDisplay = if (isFa) com.axiom.app.core.localization.AxiomDateFormatter.toPersianDigits(hunter.xpToNextLevel.toString()) else hunter.xpToNextLevel.toString()
                    Text(
                        text = stringResource(R.string.home_hunter_xp_format, currentXpDisplay, nextXpDisplay),
                        fontFamily = Outfit,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.textSecondary
                    )
                }
            }

            // Circular Gold Level/XP Indicator with progress arc
            val indicatorSize = if (fontScale > 1.3f) 52.dp else 46.dp
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .semantics {
                        contentDescription = levelProgressCd
                    },
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(indicatorSize)) {
                    val strokeWidth = 3.dp.toPx()
                    drawCircle(
                        color = colors.borderFaint,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = colors.legendaryGold,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_hunter_level_label),
                        fontFamily = JetBrainsMono,
                        fontSize = if (isFa) 7.sp else 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.legendaryGold
                    )
                    Text(
                        text = levelDisplay,
                        fontFamily = Outfit,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // Sleek horizontal XP progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.dimSurface)
        ) {
            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
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
