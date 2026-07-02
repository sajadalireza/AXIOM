package com.axiom.app.presentation.ceremony

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.components.neonGlow
import com.axiom.app.ui.components.rarityGlowPulse
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MissionCompleteMini(
    missionTitle: String,
    rarity: com.axiom.app.domain.model.LeverageTag,
    xpGained: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tier = when (rarity) {
        com.axiom.app.domain.model.LeverageTag.DEPTH, com.axiom.app.domain.model.LeverageTag.SHIELD, com.axiom.app.domain.model.LeverageTag.WEALTH_ENGINE -> 3
        com.axiom.app.domain.model.LeverageTag.CRITICAL, com.axiom.app.domain.model.LeverageTag.REVIEW, com.axiom.app.domain.model.LeverageTag.PROTECTED -> 2
        else -> 1
    }
    val color = rarity.getColor()

    when (tier) {
        // TIER 1 — slim banner, auto-dismiss 1200ms
        1 -> {
            var offsetY by remember { mutableStateOf(80f) }
            val animOffset by animateFloatAsState(offsetY, tween(300, easing = FastOutSlowInEasing), label = "t1")
            LaunchedEffect(Unit) {
                offsetY = 0f
                delay(1200)
                offsetY = 80f
                delay(300)
                onDismiss()
            }
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .offset(y = animOffset.dp)
                    .background(ShadowSurface)
                    .border(1.dp, color.copy(alpha = 0.6f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    "[ PROTOCOL RESOLVED ] +$xpGained XP",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // TIER 2 — taller banner with glow, auto-dismiss 2000ms
        2 -> {
            var offsetY by remember { mutableStateOf(100f) }
            val animOffset by animateFloatAsState(offsetY, tween(300, easing = FastOutSlowInEasing), label = "t2")
            LaunchedEffect(Unit) {
                offsetY = 0f
                delay(2000)
                offsetY = 100f
                delay(300)
                onDismiss()
            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .offset(y = animOffset.dp)
                    .background(ShadowSurface)
                    .border(1.dp, color.copy(alpha = 0.8f))
                    .rarityGlowPulse(color, enabled = true)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "RARE PROTOCOL RESOLVED",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    missionTitle,
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "+$xpGained XP",
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    color = SystemGlint,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // TIER 3 — full card, manual dismiss
        else -> {
            var scale by remember { mutableStateOf(0.8f) }
            var alpha by remember { mutableStateOf(0f) }
            val animScale by animateFloatAsState(scale, spring(dampingRatio = Spring.DampingRatioLowBouncy), label = "t3s")
            val animAlpha by animateFloatAsState(alpha, tween(400), label = "t3a")
            LaunchedEffect(Unit) {
                scale = 1f
                alpha = 1f
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalAxiomColors.current.voidBlack.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth(0.9f)
                        .graphicsLayer {
                            scaleX = animScale
                            scaleY = animScale
                            this.alpha = animAlpha
                        }
                        .background(LocalAxiomColors.current.voidBlack)
                        .border(1.5.dp, color)
                        .neonGlow(color, intensity = 0.4f)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        rarity.name,
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = color,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        missionTitle,
                        fontFamily = Inter,
                        fontSize = 18.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "+$xpGained XP",
                        fontFamily = JetBrainsMono,
                        fontSize = 20.sp,
                        color = SystemGlint,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, SystemGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SystemGreen)
                    ) {
                        Text(
                            "[ ACKNOWLEDGED ]",
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
