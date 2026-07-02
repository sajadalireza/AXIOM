package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.BorderFaint
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.Inter
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.PenaltyRed

@Composable
fun ActiveFocusEngineLayout(
    title: String,
    subtitle: String,
    seconds: Int,
    isFastSyncEnabled: Boolean,
    isPaused: Boolean = false,
    onPauseToggle: () -> Unit = {},
    onAbort: () -> Unit,
    modifier: Modifier = Modifier
) {
    val axiomColors = LocalAxiomColors.current
    val isFa = java.util.Locale.getDefault().language == "fa"
    val totalSeconds = if (isFastSyncEnabled) 10 else 25 * 60 // standard size representation
    val progress = if (totalSeconds > 0) seconds.toFloat() / totalSeconds else 0f

    val mins = seconds / 60
    val secs = seconds % 60
    val formattedTime = String.format("%02d:%02d", mins, secs)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(
                            rememberInfiniteTransition(label = "").animateFloat(
                                initialValue = 0.6f,
                                targetValue = 1.2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = ""
                            ).value
                        )
                        .background(if (isPaused) axiomColors.textDim else axiomColors.systemGreen, CircleShape)
                )
                Text(
                    text = if (isPaused) {
                        if (isFa) "تلمتری شناختی: متوقف شده" else "COGNITIVE TELEMETRY: PAUSED"
                    } else {
                        if (isFa) "قفل شناختی عصبی: فعال" else "NEURAL COGNITIVE LOCK: ACTIVE"
                    },
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPaused) axiomColors.textDim else axiomColors.systemGreen
                )
            }
            if (isFastSyncEnabled) {
                Box(
                    modifier = Modifier
                        .background(axiomColors.legendaryGold.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                        .border(1.dp, axiomColors.legendaryGold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "FAST SYNC (DEMO)",
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        color = axiomColors.legendaryGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large high-fidelity visual progress circle
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background track
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    style = Stroke(width = 4.dp.toPx())
                )
                // Glowing sweeping active track
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = if (isPaused) {
                            listOf(axiomColors.textDim, Color.White.copy(alpha = 0.1f), axiomColors.textDim)
                        } else {
                            listOf(axiomColors.systemGreen, Color(0xFF00BFFF), axiomColors.systemGreen)
                        }
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 6.dp.toPx())
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formattedTime,
                    fontFamily = JetBrainsMono,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPaused) axiomColors.textDim else axiomColors.textPrimary
                )
                Text(
                    text = if (isFa) "زمان باقی‌مانده" else "REMAINING",
                    fontFamily = JetBrainsMono,
                    fontSize = 9.sp,
                    color = axiomColors.textDim
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected mission/dungeon information
        Text(
            text = if (isFa) "دروازه هدف مأموریت" else "TARGET GATEWAY",
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            color = axiomColors.textDim
        )
        Text(
            text = title,
            fontFamily = JetBrainsMono,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = axiomColors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.OfflineBolt,
                contentDescription = null,
                tint = if (isPaused) axiomColors.textDim else axiomColors.systemGreen,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = subtitle,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = if (isPaused) axiomColors.textDim else axiomColors.systemGreen
            )
        }

        // Live Telemetry status feed
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                .background(axiomColors.voidBlack)
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CrisisAlert,
                contentDescription = null,
                tint = PenaltyRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isFa) "قانون حیاتی: خروج از برنامه ارتباط عصبی تمرکز را قطع میکند." else "CRITICAL LAW: Leaving this app breaks concentration neural link.",
                fontFamily = Inter,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = axiomColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pause / Resume Toggle
            Button(
                onClick = onPauseToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPaused) axiomColors.systemGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                    contentColor = if (isPaused) axiomColors.systemGreen else axiomColors.textPrimary
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(1.dp, if (isPaused) axiomColors.systemGreen else BorderFaint, RoundedCornerShape(4.dp))
            ) {
                Text(
                    text = if (isPaused) {
                        if (isFa) "▶ ادامه" else "▶ RESUME"
                    } else {
                        if (isFa) "⏸ توقف" else "⏸ PAUSE"
                    },
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Emergency stop / abort button
            Button(
                onClick = onAbort,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PenaltyRed.copy(alpha = 0.15f),
                    contentColor = PenaltyRed
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1.4f)
                    .height(44.dp)
                    .border(1.dp, PenaltyRed, RoundedCornerShape(4.dp))
                    .testTag("abort_focus_btn")
            ) {
                Text(
                    text = if (isFa) "خروج اضطراری" else "EMERGENCY DISCHARGE",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}
