package com.axiom.app.presentation.ceremony

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import com.axiom.app.core.sound.SoundEngine
import com.axiom.app.R
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

private data class BossParticle(
    val xOffset: Float,
    val yOffset: Float,
    val radius: Float,
    val color: Color,
    val speedX: Float,
    val speedY: Float
)

@Composable
fun BossDefeatedCeremony(
    bossName: String,
    bonusXP: Long,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stepState by remember { mutableStateOf(0) } // 0 = entrance/shake, 1 = text typing, 2 = idle
    var showBurst by remember { mutableStateOf(false) }
    var typedBossName by remember { mutableStateOf("") }
    var typedXpText by remember { mutableStateOf("") }

    val accessibilityManager = androidx.compose.ui.platform.LocalAccessibilityManager.current
    val reduceMotion = accessibilityManager?.isReducedMotionEnabled == true

    val infiniteTransition = rememberInfiniteTransition(label = "boss_defeated_infinite")

    // Camera rumble / shake coordinate offsets (first 700ms)
    val shakeTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shake_time"
    )

    val shakeOffsetX = if (stepState == 0 && !reduceMotion) {
        (sin(shakeTime * Math.PI.toFloat()) * 12).toInt()
    } else 0

    val shakeOffsetY = if (stepState == 0 && !reduceMotion) {
        (sin((shakeTime + 0.5f) * Math.PI.toFloat()) * 12).toInt()
    } else 0

    // Background vignette breathing glow
    val breathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vignette_breathing"
    )

    // Tap to dismiss blinking label
    val tapBlinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tap_blink"
    )

    // Crimson core scaling pulse
    val glyphScale by animateFloatAsState(
        targetValue = if (stepState >= 1 || reduceMotion) 1f else 0.4f,
        animationSpec = if (reduceMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "glyph_scale"
    )

    // Particle splash setup
    val density = LocalDensity.current
    val particles = remember {
        val colors = listOf(PenaltyRed, PenaltyRed.copy(alpha = 0.7f), LegendaryGold, VoidBlack)
        List(40) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = 200f + Random.nextFloat() * 300f
            BossParticle(
                xOffset = 0f,
                yOffset = 0f,
                radius = 3f + Random.nextFloat() * 5f,
                color = colors[Random.nextInt(colors.size)],
                speedX = (speed * sin(angle)),
                speedY = (speed * sin(angle + 1.2f))
            )
        }
    }

    val particleProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        SoundEngine.play(R.raw.boss_defeated)
        if (reduceMotion) {
            stepState = 2
            typedBossName = bossName.uppercase()
            typedXpText = "+$bonusXP BONUS XP"
        } else {
            // Step 0: Rumble & Heavy Impact
            delay(600)
            stepState = 1
            showBurst = true
            
            // Trigger explosive particle splash
            particleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, easing = EaseOutQuad)
            )

            // Step 1: Typewriter animation for boss name
            val rawBossName = bossName.uppercase()
            for (i in 1..rawBossName.length) {
                typedBossName = rawBossName.take(i)
                delay(40)
            }

            // Typewriter bonus XP reward
            val rewardText = "+$bonusXP BONUS XP"
            for (i in 1..rewardText.length) {
                typedXpText = rewardText.take(i)
                delay(50)
            }

            stepState = 2
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack.copy(alpha = 0.92f))
            .clickable(enabled = stepState == 2) { onDismiss() }
            .offset { IntOffset(shakeOffsetX, shakeOffsetY) },
        contentAlignment = Alignment.Center
    ) {
        // Red Ambient Dark Sweep Background Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = PenaltyRed,
                radius = size.maxDimension * 0.45f,
                center = center,
                alpha = 0.12f * breathingAlpha
            )
            // Outer crimson ring
            drawCircle(
                color = PenaltyRed.copy(alpha = 0.04f * breathingAlpha),
                radius = size.maxDimension * 0.7f,
                center = center,
                style = Stroke(width = 8.dp.toPx())
            )
        }

        // Exploding crimson particle splash wave
        if (showBurst) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                particles.forEach { p ->
                    val dx = p.speedX * particleProgress.value
                    val dy = p.speedY * particleProgress.value
                    drawCircle(
                        color = p.color.copy(alpha = (1f - particleProgress.value).coerceIn(0f, 1f)),
                        radius = p.radius,
                        center = Offset(cx + dx, cy + dy)
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // High-Fantasy crossed-swords or boss skull icon glyph
            Text(
                text = "☠",
                color = PenaltyRed,
                fontFamily = Fraunces,
                fontWeight = FontWeight.Black,
                fontSize = 72.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(glyphScale)
                    .alpha(if (stepState >= 1) 1f else 0.2f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Text display "BOSS DEFEATED" in display bold
            Text(
                text = "[ BOSS DEFEATED ]",
                color = PenaltyRed,
                fontFamily = Fraunces,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(if (stepState >= 1) 1f else 0.4f)
                    .testTag("txt_boss_defeated_header")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle typing out boss name
            Text(
                text = typedBossName,
                color = TextPrimary,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                minLines = 1,
                modifier = Modifier.testTag("txt_boss_name")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Typing out "+X BONUS XP"
            if (typedXpText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .background(LegendaryGold.copy(alpha = 0.1f), androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                        .border(1.dp, LegendaryGold.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = typedXpText,
                        color = LegendaryGold,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Blinking TAP TO CONTINUE footer
            if (stepState == 2) {
                Text(
                    text = "[ TAP TO CONCLUDE OPERATIVE ]",
                    color = TextDim,
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .alpha(tapBlinkAlpha)
                        .testTag("txt_tap_to_continue")
                )
            }
        }
    }
}

@Preview
@Composable
fun BossDefeatedCeremonyPreview() {
    AwakenTheme {
        BossDefeatedCeremony(
            bossName = "Igris The Blood-Red Commander",
            bonusXP = 1250,
            onDismiss = {}
        )
    }
}

private val androidx.compose.ui.platform.AccessibilityManager.isReducedMotionEnabled: Boolean
    @Composable
    get() {
        val context = androidx.compose.ui.platform.LocalContext.current
        return try {
            android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.TRANSITION_ANIMATION_SCALE,
                1f
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }

