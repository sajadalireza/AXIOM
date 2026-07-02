package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.theme.*

@Composable
fun HunterStatusPanel(
    hunter: Hunter,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val isFa = java.util.Locale.getDefault().language == "fa"
    // 1. Level-up trigger scale animation
    var levelUpTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(hunter.level) {
        if (hunter.level > 1) {
            levelUpTrigger = true
            kotlinx.coroutines.delay(800)
            levelUpTrigger = false
        }
    }

    val glyphScale by animateFloatAsState(
        targetValue = if (levelUpTrigger) 1.12f else 1.0f,
        animationSpec = tween(durationMillis = 400, easing = EaseOutCubic),
        label = "glyph_scale"
    )

    // 2. XP progress indicator
    val xpProgress by animateFloatAsState(
        targetValue = if (hunter.level >= 100) 1.0f else hunter.progressPercent.coerceIn(0.0f, 1.0f),
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "xp_progress_anim"
    )

    // 3. Streak pulsed color/alpha
    val pulseAlpha by if (streak >= 30) {
        val infiniteTransition = rememberInfiniteTransition(label = "streak_pulse_alpha")
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    // Determine streak color
    val streakColor = when {
        streak >= 30 -> LegendaryGold
        streak >= 14 -> RareBlue
        streak >= 7 -> UncommonTeal
        else -> TextDim
    }

    // Determine rank color
    val resolvedRankColor = Color(hunter.rankColor.toInt())

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(ShadowSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left 30% - Glyph
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .scale(glyphScale),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = hunter.rankGlyph,
                style = HudXL.copy(fontSize = 44.sp),
                fontWeight = FontWeight.Bold,
                color = resolvedRankColor
            )
        }

        // Right 70% - Data
        Column(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Row: Name and Rank/Level
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hunter.name,
                    style = TitleL,
                    color = TextPrimary
                )

                Text(
                    text = "[ ${hunter.rankLabel} ] Lv.${hunter.level}",
                    style = HudS.copy(fontWeight = FontWeight.Bold),
                    color = resolvedRankColor
                )
            }

            // XP Bar + Numbers Row
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(DimSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(xpProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(SystemGreen, SystemGlint)
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (hunter.level >= 100) (if (isFa) "رتبـه S بـه دست آمـد" else "S-CLASS ACHIEVED") else "${hunter.currentXP}/${hunter.xpToNextLevel} XP",
                        style = HudS,
                        color = if (hunter.level >= 100) LegendaryGold else TextDim,
                        fontWeight = if (hunter.level >= 100) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // Streak Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val streakText = if (isFa) "◈ $streak روز پیاپی" else "◈ $streak DAY STREAK"
                val protocolText = if (isFa) {
                    when {
                        streak >= 30 -> " [ پروتکل صعود ]"
                        streak >= 14 -> " [ پروتکل اورکلاک ]"
                        streak >= 7 -> " [ پروتکل پایدار ]"
                        else -> " [ پروتکل غیرفعال ]"
                    }
                } else {
                    when {
                        streak >= 30 -> " [ ASCENSION PROTOCOL ]"
                        streak >= 14 -> " [ OVERCHARGE PROTOCOL ]"
                        streak >= 7 -> " [ STABLE PROTOCOL ]"
                        else -> " [ INACTIVE PROTOCOL ]"
                    }
                }

                Text(
                    text = streakText + protocolText,
                    style = HudS,
                    color = streakColor.copy(alpha = pulseAlpha),
                    fontWeight = FontWeight.Normal
                )

                val multiplier = 1.0f + (streak * 0.05f)
                val multText = "×${String.format(java.util.Locale.US, "%.2f", multiplier)} XP"
                Text(
                    text = multText,
                    style = HudS,
                    color = if (multiplier > 1.0f) SystemGreen else TextDim,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
