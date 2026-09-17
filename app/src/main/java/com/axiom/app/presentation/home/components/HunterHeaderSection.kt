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
import androidx.compose.ui.graphics.lerp
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
    val avatarInitial = displayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "A"
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // AXIOM Brand Atmospheric Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "▲",
                        fontSize = 13.sp,
                        color = colors.systemGreen
                    )
                    Text(
                        text = "A X I O M",
                        fontFamily = Outfit,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        letterSpacing = 4.sp
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = stringResource(R.string.axiom_tagline),
                    fontFamily = Outfit,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    letterSpacing = 0.3.sp
                )
            }
        }

        // Identity Row: [Portrait Anchor] [Name + Rank] [Circular Gold Level Ring + XP Below]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Circular Avatar Profile Target (>=48dp touch area)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateToProfile() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    lerp(colors.dimSurface, colors.systemGreen, 0.10f),
                                    colors.dimSurface
                                )
                            )
                        )
                        .border(1.5.dp, colors.legendaryGold.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarInitial,
                        fontFamily = Outfit,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.legendaryGold
                    )
                }
            }

            // Name & Subordinate Rank Capsule
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = displayName,
                    fontFamily = Outfit,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Refined Subordinate Rank Capsule
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.systemGreen.copy(alpha = 0.12f))
                            .border(0.75.dp, colors.systemGreen.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = rankText,
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.systemGreen,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Circular Gold Level/XP Indicator with progress arc and centered XP
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val indicatorSize = if (fontScale > 1.3f) 50.dp else 46.dp
                Box(
                    modifier = Modifier
                        .size(indicatorSize)
                        .semantics {
                            contentDescription = levelProgressCd
                        },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(indicatorSize)) {
                        val strokeWidth = 2.5.dp.toPx()
                        drawCircle(
                            color = colors.borderFaint.copy(alpha = 0.4f),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.home_hunter_level_label),
                            fontFamily = JetBrainsMono,
                            fontSize = if (isFa) 8.sp else 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.legendaryGold
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Text(
                            text = levelDisplay,
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                // Truthful XP Counter centered cleanly below the Level indicator
                val currentXpDisplay = if (isFa) com.axiom.app.core.localization.AxiomDateFormatter.toPersianDigits(hunter.currentXP.toString()) else hunter.currentXP.toString()
                val nextXpDisplay = if (isFa) com.axiom.app.core.localization.AxiomDateFormatter.toPersianDigits(hunter.xpToNextLevel.toString()) else hunter.xpToNextLevel.toString()
                Text(
                    text = stringResource(R.string.home_hunter_xp_format, currentXpDisplay, nextXpDisplay),
                    fontFamily = JetBrainsMono,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary
                )
            }
        }
    }
}
