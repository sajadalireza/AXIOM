package com.axiom.app.presentation.leagues

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.RivalHunter
import com.axiom.app.ui.theme.*

@Composable
fun ZoneDividerItem(
    title: String,
    description: String,
    color: Color,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 0.5.sp
            )
            Text(
                text = description,
                fontFamily = Inter,
                fontSize = 8.sp,
                color = color.copy(alpha = 0.7f),
                lineHeight = 11.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Canvas(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
        ) {
            drawLine(
                color = color.copy(alpha = 0.4f),
                start = androidx.compose.ui.geometry.Offset(0f, size.height / 2f),
                end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2f),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun RivalHunterRow(
    rank: Int,
    rival: RivalHunter,
    axiomColors: AxiomColorScheme,
    isUser: Boolean,
    zoneColor: Color,
    isPreregistered: Boolean = false
) {
    val rankBackground = when {
        isUser -> Brush.horizontalGradient(listOf(axiomColors.systemGreen, axiomColors.systemGreen.copy(alpha = 0.8f)))
        rank == 1 -> Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500)))
        rank == 2 -> Brush.horizontalGradient(listOf(Color(0xFFE5E4E2), Color(0xFFB0C4DE)))
        rank == 3 -> Brush.horizontalGradient(listOf(Color(0xFFFF8C00), Color(0xFFCD7F32)))
        else -> null
    }

    val isFa = java.util.Locale.getDefault().language == "fa"
    val translatedStatus = remember(rival.status, isFa) {
        if (!isFa) rival.status else {
            when {
                rival.status == "IDLE (RESTING)" -> "در حال استراحت"
                rival.status == "IDLE" -> "در حال استراحت"
                rival.status == "PATROLLING GATES" -> "گشت‌زنی در دروازه‌ها"
                rival.status == "RECALIBRATING CORE" -> "بازتنظیم هسته"
                rival.status == "PREPARING SUPPLIES" -> "آماده‌سازی تجهیزات"
                rival.status == "READY FOR COMBAT" -> "آماده برای نبرد"
                rival.status.startsWith("LIVE FOCUS ON:") -> {
                    val missionName = rival.status.removePrefix("LIVE FOCUS ON:").trim()
                    val transMission = when (missionName) {
                        "DUST STORM GATES" -> "دروازه‌های طوفان شن"
                        "MONARCH GATES" -> "دروازه‌های پادشاهی"
                        "MONARCH LABS" -> "آزمایشگاه‌های باستانی"
                        else -> missionName
                    }
                    "تمرکز روی: $transMission"
                }
                else -> rival.status
            }
        }
    }
    val translatedRype = remember(rival.rype, isFa) {
        if (!isFa) rival.rype else {
            when (rival.rype) {
                "Shadow Lord" -> "ارباب سایه‌ها"
                "Goliath Power" -> "قدرت جالوت"
                "Sword Dance" -> "رقص شمشیر"
                "Beast Form" -> "حالت هیولا"
                "Iron Arrow" -> "تیر آهنی"
                "Shield Barrier" -> "دیوار محافظ"
                "Awakened Protocol" -> "پروتکل بیدار"
                "Global Challenger" -> "رقیب جهانی"
                else -> rival.rype
            }
        }
    }

    val rowBackground = if (isUser) {
        Brush.horizontalGradient(
            colors = listOf(axiomColors.systemGreen.copy(alpha = 0.12f), Color.Transparent)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(axiomColors.dimSurface, axiomColors.dimSurface.copy(alpha = 0.7f))
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isUser) axiomColors.systemGreen else BorderFaint,
                shape = RoundedCornerShape(6.dp)
            )
            .background(
                brush = rowBackground,
                shape = RoundedCornerShape(6.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical indicator strip on the far left for Zone identification
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .background(zoneColor)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Rank Badge & Rank-Delta chip row
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            val rankModifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .then(
                    if (rankBackground != null) {
                        Modifier.background(rankBackground)
                    } else {
                        Modifier.background(TextPrimary.copy(alpha = 0.05f))
                    }
                )

            Box(
                modifier = rankModifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isUser || rank <= 3) axiomColors.voidBlack else axiomColors.textPrimary
                )
            }

            if (rival.rankDelta != 0) {
                Spacer(modifier = Modifier.height(2.dp))
                val climb = rival.rankDelta > 0
                Box(
                    modifier = Modifier
                        .background(
                            if (climb) SystemGreen.copy(alpha = 0.15f) else PenaltyRed.copy(alpha = 0.15f),
                            RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (climb) "▲ ${rival.rankDelta}" else "▼ ${-rival.rankDelta}",
                        fontFamily = JetBrainsMono,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (climb) SystemGreen else PenaltyRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Name & Rype Description
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = rival.name,
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) axiomColors.systemGreen else axiomColors.textPrimary
                )
                Box(
                    modifier = Modifier
                        .background(
                            TextPrimary.copy(alpha = 0.08f),
                            RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = rival.rankLabel,
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        color = axiomColors.textSecondary
                    )
                }
                if (isUser && isPreregistered) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, axiomColors.systemGreen.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .background(axiomColors.systemGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (isFa) "◈ اولویت" else "◈ PRIORITY",
                            fontFamily = JetBrainsMono,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = axiomColors.systemGreen
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isLiveFocusStatus = rival.status.contains("LIVE FOCUS") || rival.status.contains("COMPLETING")
                
                if (isLiveFocusStatus) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rival")
                    val alphaPulse by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse_alpha"
                    )
                    
                    Canvas(modifier = Modifier.size(6.dp)) {
                        drawCircle(color = SystemGreen, alpha = alphaPulse)
                    }
                }
                
                Text(
                    text = "$translatedRype • $translatedStatus",
                    fontFamily = Inter,
                    fontSize = 10.sp,
                    color = if (isLiveFocusStatus) {
                        axiomColors.systemGreen.copy(alpha = 0.9f)
                    } else {
                        axiomColors.textDim
                    }
                )
            }
            if (rival.isGhost) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "⟳ SIMULATED — Ghost Protocol, not a real hunter",
                    fontFamily = Inter,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = axiomColors.legendaryGold.copy(alpha = 0.7f)
                )
            }
        }

        // LP count & delta chips
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (rival.pointsDelta > 0) {
                    Text(
                        text = "+${rival.pointsDelta}",
                        fontFamily = JetBrainsMono,
                        fontSize = 9.sp,
                        color = SystemGreen,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                Text(
                    text = "${rival.points} LP",
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isUser) axiomColors.systemGreen else axiomColors.textPrimary
                )
            }
            Text(
                text = if (isFa) "امتیاز" else "POINTS",
                fontFamily = JetBrainsMono,
                fontSize = 8.sp,
                color = axiomColors.textDim
            )
        }
    }
}
