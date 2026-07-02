package com.axiom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.WeeklyChallenge
import com.axiom.app.ui.theme.*

@Composable
fun WeeklyChallengeCard(
    challenges: List<WeeklyChallenge>,
    allClaimed: Boolean,
    onClaimBonus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = LocalAxiomColors.current
    val allComplete = challenges.all { it.isCompleted }
    val isFa = java.util.Locale.getDefault().language == "fa"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .soloLevelingCard(
                accentColor = if (allComplete && !allClaimed) c.legendaryGold else c.borderFaint,
                bevel = 12f,
                borderWidth = 1f,
                glowRadius = if (allComplete && !allClaimed) 6f else 0f,
                showSideNotches = false,
                backgroundColor = c.dimSurface
            )
            .clip(SoloLevelingBeveledShape(bevel = 12f, showSideNotches = false))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (isFa) "[ پروتکل هفتگی ]" else "[ WEEKLY PROTOCOL ]",
                fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = if (allComplete) c.legendaryGold else c.systemGreen
            )
            Text(
                "${challenges.count { it.isCompleted }}/${challenges.size}",
                fontFamily = JetBrainsMono, fontSize = 11.sp, color = c.textDim
            )
        }

        challenges.forEach { ch ->
            val progress = (ch.currentValue.toFloat() / ch.targetValue).coerceIn(0f, 1f)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        ch.title, fontFamily = JetBrainsMono, fontSize = 11.sp,
                        color = if (ch.isCompleted) c.systemGreen else c.textSecondary,
                        fontWeight = if (ch.isCompleted) FontWeight.Bold else FontWeight.Normal
                    )
                    Text("${ch.currentValue}/${ch.targetValue}", fontFamily = JetBrainsMono, fontSize = 10.sp, color = c.textDim)
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(2.dp).clip(RoundedCornerShape(1.dp)),
                    color = if (ch.isCompleted) c.systemGreen else c.systemGreen.copy(alpha = 0.5f),
                    trackColor = c.borderFaint
                )
            }
        }

        if (allComplete && !allClaimed) {
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(c.legendaryGold.copy(alpha = 0.15f))
                    .border(1.dp, c.legendaryGold, RoundedCornerShape(4.dp))
                    .clickable { onClaimBonus() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isFa) "[ دریافت پاداش  +150 XP ]" else "[ CLAIM BONUS  +150 XP ]",
                    fontFamily = JetBrainsMono, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, color = c.legendaryGold
                )
            }
        } else if (allClaimed) {
            Text(
                if (isFa) "✓ پروتکل هفتگی تکمیل شد" else "✓ WEEKLY PROTOCOL COMPLETE",
                fontFamily = JetBrainsMono, fontSize = 11.sp,
                color = c.systemGreen.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
        }
    }
}
