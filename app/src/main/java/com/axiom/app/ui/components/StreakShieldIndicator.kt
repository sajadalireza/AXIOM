package com.axiom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.LocalAxiomColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StreakShieldIndicator(
    shieldCount: Int,
    modifier: Modifier = Modifier
) {
    val c = LocalAxiomColors.current
    val hasShield = shieldCount > 0
    val color = if (hasShield) c.systemGlint else c.commonGray
    val roman = if (hasShield) shieldCount.toRomanNumeral() else "0"

    Row(
        modifier = modifier.testTag("streak_shield_indicator"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Hexagonal Badge
        Box(
            modifier = Modifier
                .size(24.dp)
                .testTag("shield_hexagon_badge"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sizePx = size.minDimension
                val r = sizePx / 2f
                val cx = size.width / 2f
                val cy = size.height / 2f
                
                val path = Path().apply {
                    for (i in 0 until 6) {
                        // Pointy-topped hexagon angles
                        val angle = (i * Math.PI / 3 - Math.PI / 6).toFloat()
                        val x = cx + r * cos(angle)
                        val y = cy + r * sin(angle)
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                
                // Draw hexagon fill
                drawPath(
                    path = path,
                    color = color.copy(alpha = 0.12f),
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
                // Draw hexagon outline
                drawPath(
                    path = path,
                    color = color,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
            }
            
            Text(
                text = roman,
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Text(
            text = if (hasShield) "STREAK SHIELD ×$shieldCount" else "STREAK SHIELD DEPLETED",
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

private fun Int.toRomanNumeral(): String {
    return when (this) {
        1 -> "I"
        2 -> "II"
        3 -> "III"
        4 -> "IV"
        5 -> "V"
        else -> this.toString()
    }
}
