package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.core.sound.AwakenSound
import com.axiom.app.core.sound.SoundEngine
import com.axiom.app.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

data class XPFloatEvent(
    val xpValue: Int,
    val shadowMultiplier: Float
)

@Composable
fun XPFloatAnimation(
    xpEventFlow: StateFlow<XPFloatEvent?>,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val xpEvent by xpEventFlow.collectAsStateWithLifecycle()
    val activeFloats = remember { mutableStateListOf<ActiveFloat>() }
    var spawnCount by remember { mutableStateOf(0) }

    LaunchedEffect(xpEvent) {
        xpEvent?.let { valXP ->
            // Play ping sound
            SoundEngine.play(AwakenSound.XP_PING)
            
            // Stagger horizontal positions to avoid overlap
            val staggerIndex = spawnCount % 3
            val offsetDp = when (staggerIndex) {
                0 -> 0f
                1 -> -70f
                2 -> 70f
                else -> 0f
            }
            spawnCount++

            activeFloats.add(
                ActiveFloat(
                    id = System.nanoTime(),
                    xpValue = valXP.xpValue,
                    shadowMultiplier = valXP.shadowMultiplier,
                    horizontalOffset = offsetDp
                )
            )

            // Instantly notify completion to clear the state and allow consecutive events
            onAnimationComplete()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        activeFloats.forEach { floatItem ->
            key(floatItem.id) {
                val progress = remember { Animatable(0f) }
                
                LaunchedEffect(floatItem.id) {
                    progress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                    )
                    activeFloats.remove(floatItem)
                }

                val p = progress.value

                // Easing for upward float (EaseOutQuad)
                val smoothP = p * (2f - p)
                val offsetY = (-80).dp * smoothP

                // Pop effect: 1.3 -> 1.0 in first 200ms
                val scale = if (p <= 0.2f) {
                    1.3f - 0.3f * (p / 0.2f)
                } else {
                    1.0f
                }

                // Fade out: last 300ms (0.7 -> 1.0)
                val alpha = if (p >= 0.7f) {
                    (1.0f - (p - 0.7f) / 0.3f).coerceIn(0f, 1f)
                } else {
                    1.0f
                }

                Box(
                    modifier = Modifier
                        .offset(x = floatItem.horizontalOffset.dp, y = offsetY)
                        .scale(scale)
                        .alpha(alpha)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(
                                color = LocalAxiomColors.current.voidBlack.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = SystemGreen.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+${floatItem.xpValue} XP",
                            fontSize = 14.sp,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            color = SystemGreen
                        )
                        if (floatItem.shadowMultiplier > 1.0f) {
                            val formattedMult = String.format(java.util.Locale.US, "%.2f", floatItem.shadowMultiplier)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "SHADOW BONUS ×$formattedMult",
                                fontSize = 10.sp,
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                color = LegendaryGold
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ActiveFloat(
    val id: Long,
    val xpValue: Int,
    val shadowMultiplier: Float,
    val horizontalOffset: Float
)
