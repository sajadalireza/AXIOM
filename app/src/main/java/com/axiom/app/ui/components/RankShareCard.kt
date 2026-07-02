package com.axiom.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RankShareCard(
    hunter: Hunter,
    modifier: Modifier = Modifier,
    missionsComplete: Int = 0,
    shadowArmySize: Int = 0,
    dayStreak: Int = 0
) {
    val rawColor = rankColorMap[hunter.rankLabel] ?: Color(hunter.rankColor)
    val rankColor = if (hunter.rankLabel.contains("s", ignoreCase = true)) LegendaryGold else rawColor
    
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    Box(
        modifier = modifier
            .aspectRatio(4f / 5f)
            .fillMaxWidth()
            .background(VoidBlack)
            .border(1.dp, BorderFaint, RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        // Diagonal gradient overlay in rank color at 20% alpha
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        rankColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )
        }

        // Large rank glyph watermark at 30% opacity in the center
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = hunter.rankGlyph,
                color = rankColor.copy(alpha = 0.3f),
                fontSize = 160.sp,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }

        // Content layout
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WARRIOR REGISTRY",
                    color = rankColor,
                    fontSize = 12.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "RANK: ${hunter.rankLabel}",
                    color = TextDim,
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMono
                )
            }

            // Middle Section: Name, Level, Rank
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = hunter.name.uppercase(),
                    color = TextPrimary,
                    style = DisplayXL,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "LEVEL ${hunter.level} ${hunter.rankLabel} HUNTER",
                    color = rankColor,
                    style = LabelL,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            // Stats Grid: Level, Total XP, Missions Complete, Shadow Army size, Day streak
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderFaint)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("LEVEL", style = LabelS, color = TextDim)
                        Text("${hunter.level}", style = HudM, color = TextPrimary)
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text("MISSIONS", style = LabelS, color = TextDim)
                        Text("$missionsComplete", style = HudM, color = TextPrimary)
                    }
                    
                    // Middle Column
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("TOTAL XP", style = LabelS, color = TextDim)
                        Text("${hunter.totalXP}", style = HudM, color = TextPrimary)
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text("SHADOW ARMY", style = LabelS, color = TextDim)
                        Text("$shadowArmySize", style = HudM, color = TextPrimary)
                    }

                    // Right Column
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("DAY STREAK", style = LabelS, color = TextDim)
                        Text("$dayStreak DAYS", style = HudM, color = TextPrimary)
                    }
                }
            }

            // Bottom Section: WARRIOR logo bottom-left, date bottom-right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "⬟ WARRIOR",
                    color = SystemGreen,
                    style = HudM,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = currentDate,
                    color = TextDim,
                    style = LabelS
                )
            }
        }
    }
}
