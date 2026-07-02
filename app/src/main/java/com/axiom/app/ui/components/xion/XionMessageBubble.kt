package com.axiom.app.ui.components.xion

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun XionMessageBubble(
    text: String,
    isUser: Boolean,
    timestamp: Long,
    isStreaming: Boolean = false,
    systemColor: Color = LocalAxiomColors.current.systemGreen,
    xionMood: XionMood = XionMood.IDLE,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (isUser) {
            // User Message
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(colors.shadowSurface, RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                    .drawBehindLeftBorder(colors.systemGreen, 2.dp)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = text,
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    lineHeight = 18.sp
                )
            }
        } else {
            // Xion Message
            // Render 40dp circular avatar above the message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
            ) {
                XionLivingEyeAvatar(
                    modifier = Modifier.size(40.dp),
                    systemColor = colors.epicPurple,
                    mood = xionMood
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "XION TRANSMISSION",
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = colors.epicPurple,
                    letterSpacing = 0.1.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(colors.voidBlack, RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                    .drawBehindLeftBorder(colors.epicPurple, 2.dp)
                    .border(1.dp, colors.borderFaint, RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        fontFamily = FiraCode,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        lineHeight = 16.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isStreaming) {
                        BlinkingCursor(cursorColor = colors.epicPurple)
                    }
                }
            }
        }

        // Timestamp below each message, in LabelS TextDim, right-aligned
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(top = 4.dp, end = 4.dp, start = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = formatTime(timestamp),
                style = LabelS,
                color = colors.textDim
            )
        }
    }
}

@Composable
fun BlinkingCursor(cursorColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )
    Text(
        text = " █",
        color = cursorColor.copy(alpha = alpha),
        fontFamily = FiraCode,
        fontSize = 11.sp
    )
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// Extension modifier to draw a clean left border using drawBehind
private fun Modifier.drawBehindLeftBorder(color: Color, widthDp: androidx.compose.ui.unit.Dp): Modifier {
    return this.drawBehind {
        val strokeWidth = widthDp.toPx()
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(strokeWidth / 2f, 0f),
            end = androidx.compose.ui.geometry.Offset(strokeWidth / 2f, size.height),
            strokeWidth = strokeWidth
        )
    }
}
