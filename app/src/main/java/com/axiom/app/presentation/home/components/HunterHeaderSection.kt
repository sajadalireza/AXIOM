package com.axiom.app.presentation.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.components.StreakFlameWidget
import com.axiom.app.ui.theme.*

@Composable
fun HunterHeaderSection(
    hunter: Hunter,
    streakDays: Int,
    streakMultiplier: Float,
    onNavigateToProfile: () -> Unit,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val resolvedRankColor = Color(hunter.rankColor.toInt())

    // Shimmer animation for XP progress bar
    val infiniteTransition = rememberInfiniteTransition(label = "xp_shimmer_transition")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Column(modifier = modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF18211D), Color(0xFF131312))
                )
            )
            .border(1.dp, Color(0xFF2A3530), RoundedCornerShape(16.dp))
            .clickable { onNavigateToProfile() }
            .testTag("hunter_header_banner")
            .padding(18.dp)
    ) {
        // Rank glyph watermark in the background at 5% opacity
        Text(
            text = hunter.rankLabel.takeIf { it.isNotEmpty() } ?: "S",
            fontFamily = JetBrainsMono,
            fontSize = 130.sp,
            fontWeight = FontWeight.Bold,
            color = colors.legendaryGold.copy(alpha = 0.05f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = (-24).dp)
        )

        // Main content Column
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            // Personal thesis (as a nice subtitle)
            val questText = hunter.personalThesis.ifEmpty { "Define Personal Thesis..." }
            Text(
                text = "▸ $questText",
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = Color(0xFF8C9A8F),
                style = androidx.compose.ui.text.TextStyle(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                maxLines = 1,
                modifier = Modifier.testTag("home_personal_thesis_label")
            )

            // Name in DisplayM (Fraunces font)
            Text(
                text = hunter.name.uppercase(),
                style = DisplayM, // Fraunces display font
                fontWeight = FontWeight.Black,
                color = colors.textPrimary,
                maxLines = 1,
                letterSpacing = 0.5.sp
            )

            // Rank label and level badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${hunter.rankLabel.uppercase()}-RANK",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = colors.legendaryGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(colors.legendaryGold.copy(alpha = 0.1f), RoundedCornerShape(5.dp))
                        .border(1.dp, colors.legendaryGold.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                )
                Text(
                    text = "LEVEL ${hunter.level}",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "·  HUNTER",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = colors.textDim
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // XP Progression block
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "XP PROGRESSION",
                        fontFamily = JetBrainsMono,
                        fontSize = 9.sp,
                        color = colors.textDim,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${hunter.currentXP} / ${hunter.xpToNextLevel}",
                        fontFamily = JetBrainsMono,
                        fontSize = 9.sp,
                        color = colors.textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Custom gradient XP bar with trailing shimmer animation
                val progress = hunter.progressPercent.coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF0C0E0D))
                        .border(1.dp, Color(0xFF243029), RoundedCornerShape(5.dp))
                ) {
                    if (progress > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(colors.systemGreen, colors.legendaryGold)
                                    )
                                )
                                .drawBehind {
                                    val brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            colors.textPrimary.copy(alpha = 0.45f),
                                            Color.Transparent
                                        ),
                                        start = androidx.compose.ui.geometry.Offset(shimmerOffset, 0f),
                                        end = androidx.compose.ui.geometry.Offset(shimmerOffset + 50f, size.height)
                                    )
                                    drawRect(brush = brush)
                                }
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    StreakFlameWidget(
        streakDays = streakDays,
        multiplier = streakMultiplier,
        onNavigateToPremium = onNavigateToPremium,
        modifier = Modifier.fillMaxWidth()
    )
    }
}
