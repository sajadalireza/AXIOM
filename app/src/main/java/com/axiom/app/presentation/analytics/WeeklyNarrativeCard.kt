package com.axiom.app.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.Fraunces
import com.axiom.app.ui.theme.Outfit
import com.axiom.app.ui.theme.HudXL
import com.axiom.app.ui.theme.LegendaryGold
import com.axiom.app.ui.theme.SystemGreen
import com.axiom.app.ui.theme.SystemGlint
import com.axiom.app.ui.theme.TextPrimary

@Composable
fun WeeklyNarrativeCard(
    debriefTitle: String,
    aiSummaryText: String,
    performanceScore: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Header
            Text(
                text = debriefTitle.uppercase(),
                color = LegendaryGold,
                fontFamily = Fraunces,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Circular Performance Score Gauge
            CircularPerformanceGauge(
                score = performanceScore,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // AI Narrative Summary Text
            Text(
                text = aiSummaryText,
                color = TextPrimary,
                fontFamily = Outfit,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun CircularPerformanceGauge(
    score: Int,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(160.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 12.dp.toPx()
            val sizeMin = size.minDimension
            val rectSize = sizeMin - strokeWidthPx
            val topLeft = Offset((size.width - rectSize) / 2f, (size.height - rectSize) / 2f)
            
            // Draw background track arc
            drawArc(
                color = Color.Gray.copy(alpha = 0.15f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(rectSize, rectSize),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round
                )
            )
            
            // Draw active arc with color gradient
            val sweepAngle = (score / 100f) * 270f
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to SystemGreen,
                    0.5f to SystemGlint,
                    1.0f to LegendaryGold
                ),
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(rectSize, rectSize),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round
                )
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$score",
                style = HudXL,
                color = SystemGreen,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "WEEKLY SCORE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }
    }
}
