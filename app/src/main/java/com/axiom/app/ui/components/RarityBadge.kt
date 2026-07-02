package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*

@Composable
fun RarityBadge(
    rarity: String,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 9.sp
) {
    val cleanRarity = rarity.lowercase().replace("-rank", "").trim()
    val color = when (cleanRarity) {
        "mythic" -> PenaltyRed
        "legendary" -> LegendaryGold
        "epic" -> EpicPurple
        "rare" -> RareBlue
        "uncommon" -> UncommonTeal
        else -> CommonGray
    }

    val isLegendary = cleanRarity == "legendary"

    val transition = rememberInfiniteTransition(label = "badge_shimmer")
    val shimmerProgress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    val backgroundModifier = Modifier.drawBehind {
        val w = size.width
        val h = size.height
        if (isLegendary) {
            val xOffset = w * shimmerProgress
            val brush = Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.45f),
                    color.copy(alpha = 0.15f)
                ),
                start = androidx.compose.ui.geometry.Offset(xOffset - w * 0.4f, 0f),
                end = androidx.compose.ui.geometry.Offset(xOffset + w * 0.4f, h)
            )
            drawRect(brush = brush)
        } else {
            val brush = Brush.linearGradient(
                colors = listOf(
                    color.copy(alpha = 0.15f),
                    color.copy(alpha = 0.05f)
                ),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(w, h)
            )
            drawRect(brush = brush)
        }
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .then(backgroundModifier)
            .border(1.dp, color.copy(alpha = 0.35f), CircleShape)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = rarity.uppercase(),
            color = if (isLegendary) Color.White else color,
            fontFamily = JetBrainsMono,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
