package com.axiom.app.presentation.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.core.localization.AxiomDateFormatter
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CountdownBannerSection(
    programStartDate: Long,
    onEditProgramStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    // Per-second ticker scoped locally to this banner so only the countdown
    // recomposes each second — not the entire Home dashboard.
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val target180Day = programStartDate + (180L * 24 * 60 * 60 * 1000L)
    val remainingMillis = target180Day - currentTime
    val daysRemaining = if (remainingMillis > 0) remainingMillis / (24 * 60 * 60 * 1000L) else 0L
    val hoursRemaining = if (remainingMillis > 0) (remainingMillis % (24 * 60 * 60 * 1000L)) / (60 * 60 * 1000L) else 0L
    val minutesRemaining = if (remainingMillis > 0) (remainingMillis % (60 * 60 * 1000L)) / (60 * 1000L) else 0L

    val isFa = java.util.Locale.getDefault().language == "fa"
    val reducedMotion = LocalAxiomReducedMotion.current

    // Pulsing alpha for the blinking indicator dot
    val infiniteTransition = rememberInfiniteTransition(label = "countdown_dot_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )
    val effectiveDotAlpha = if (reducedMotion) 1.0f else dotAlpha

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vehicle_countdown_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = androidx.compose.foundation.BorderStroke(com.axiom.app.ui.theme.AxiomBorder.thin, colors.borderFaint),
        shape = RoundedCornerShape(com.axiom.app.ui.theme.AxiomRadius.l)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row with STATUS indicator, Title, and Edit Start button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    // Blinking gold indicator dot
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(
                                color = colors.legendaryGold.copy(alpha = effectiveDotAlpha),
                                shape = RoundedCornerShape(2.5.dp)
                            )
                    )
                    Text(
                        text = if (isFa) "شمارش معکوس تصمیم‌گیری" else "DECISION COUNTDOWN",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.legendaryGold,
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = if (isFa) "تغییر تاریخ" else "EDIT START",
                    fontFamily = JetBrainsMono,
                    fontSize = 9.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(colors.legendaryGold.copy(alpha = 0.08f), RoundedCornerShape(com.axiom.app.ui.theme.AxiomRadius.s))
                        .clickable { onEditProgramStart() }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            // Segmented Flip-Counter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val daysStr = if (isFa) AxiomDateFormatter.toPersianDigits(daysRemaining.toString().padStart(3, '0')) else daysRemaining.toString().padStart(3, '0')
                val hoursStr = if (isFa) AxiomDateFormatter.toPersianDigits(hoursRemaining.toString().padStart(2, '0')) else hoursRemaining.toString().padStart(2, '0')
                val minutesStr = if (isFa) AxiomDateFormatter.toPersianDigits(minutesRemaining.toString().padStart(2, '0')) else minutesRemaining.toString().padStart(2, '0')

                DigitBlock(
                    value = daysStr,
                    label = if (isFa) "روز" else "DAYS",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = ":",
                    fontFamily = JetBrainsMono,
                    fontSize = 22.sp,
                    color = colors.legendaryGold.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                DigitBlock(
                    value = hoursStr,
                    label = if (isFa) "ساعت" else "HOURS",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = ":",
                    fontFamily = JetBrainsMono,
                    fontSize = 22.sp,
                    color = colors.legendaryGold.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                DigitBlock(
                    value = minutesStr,
                    label = if (isFa) "دقیقه" else "MINUTES",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DigitBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dark Card block with gold border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(AxiomRadius.m))
                .background(colors.voidBlack)
                .border(AxiomBorder.thin, colors.legendaryGold.copy(alpha = 0.35f), RoundedCornerShape(AxiomRadius.m))
                .padding(vertical = 4.dp, horizontal = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontFamily = JetBrainsMono,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1
            )
        }
        // Label using JetBrainsMono below the block
        Text(
            text = label,
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textDim,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}
