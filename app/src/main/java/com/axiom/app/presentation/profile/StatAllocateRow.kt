package com.axiom.app.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.components.CyberProgressBar
import com.axiom.app.ui.theme.*

@Composable
fun StatAllocateRow(
    name: String,
    statKey: String,
    value: Int,
    description: String,
    icon: ImageVector,
    glowColor: Color,
    pointsLeft: Int,
    onAllocate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelPerTier = 10f
    val progress = (value % levelPerTier) / levelPerTier

    HolographicCard(
        modifier = modifier.fillMaxWidth(),
        accentColor = glowColor,
        glowEnabled = pointsLeft > 0
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon and Stat information
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(glowColor.copy(alpha = 0.08f))
                        .border(1.dp, glowColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = glowColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            fontFamily = JetBrainsMono,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = stringResource(R.string.stats_level_value, value),
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = glowColor
                        )
                    }

                    // CyberProgressBar showing progress to next tier
                    CyberProgressBar(
                        progress = progress,
                        color = glowColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    )

                    Text(
                        text = description,
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Allocation "+" button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (pointsLeft > 0) glowColor.copy(alpha = 0.15f) else DimSurface)
                    .border(
                        1.dp,
                        if (pointsLeft > 0) glowColor else BorderFaint,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable(enabled = pointsLeft > 0) { onAllocate() }
                    .testTag("btn_allocate_$statKey"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontFamily = JetBrainsMono,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (pointsLeft > 0) glowColor else TextDim
                )
            }
        }
    }
}
