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

    val defaultName = stringResource(R.string.home_hunter_default_name)
    val displayName = hunter.name.ifBlank { defaultName }
    val rankText = hunter.rankLabel.ifBlank { "RECRUIT" }
    val headerContentDesc = stringResource(
        R.string.home_hunter_profile_cd,
        displayName,
        rankText,
        hunter.level
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hunter_header_banner")
            .semantics {
                contentDescription = headerContentDesc
            },
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Identity Row: [Avatar] [Name + Rank] [XP Counter]
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.dimSurface)
                        .border(1.dp, colors.borderFaint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_nav_habits),
                        contentDescription = stringResource(R.string.home_hunter_profile_icon_cd),
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Name & Rank Pill
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = displayName,
                    fontFamily = Outfit,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

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
            }

            // XP Counter Text
            Text(
                text = stringResource(R.string.home_hunter_xp_format, hunter.currentXP, hunter.xpToNextLevel),
                fontFamily = Outfit,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary
            )
        }

        // Sleek horizontal XP progress bar directly below the row
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
                                listOf(colors.systemGreen, colors.systemGreen.copy(alpha = 0.8f))
                            )
                        )
                )
            }
        }
    }
}
