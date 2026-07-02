package com.axiom.app.presentation.ceremony

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class MilestoneParticle(
    val angle: Float,
    val speedPx: Float,
    val cosAngle: Float,
    val sinAngle: Float,
    val color: Color,
    val radiusPx: Float,
    val elongation: Float
)

@Composable
fun StreakMilestoneOverlay(
    streakDays: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var overlayAlphaState by remember { mutableStateOf(0f) }
    var beamProgressState by remember { mutableStateOf(0f) }
    var numberScaleState by remember { mutableStateOf(0f) }
    var text1ProgState by remember { mutableStateOf(0) }
    var text2ProgState by remember { mutableStateOf(0) }
    var text3ProgState by remember { mutableStateOf(0) }
    var canDismissState by remember { mutableStateOf(false) }

    val is30Day = streakDays == 30

    val (label, multiplier) = remember(streakDays) {
        when (streakDays) {
            7 -> "CONSECRATION PROTOCOL" to "1.5"
            14 -> "DOMINANCE PROTOCOL" to "2.0"
            21 -> "RESONANCE PROTOCOL" to "2.5"
            30 -> "ASCENSION PROTOCOL" to "3.0"
            60 -> "TRANSCENDENCE PROTOCOL" to "4.0"
            90 -> "IMMORTAL PROTOCOL" to "5.0"
            180 -> "OVERLORD PROTOCOL" to "6.0"
            365 -> "CHRONOS PROTOCOL" to "8.0"
            else -> "MILESTONE" to "1.2"
        }
    }

    val milestoneMessage = remember(streakDays) {
        when (streakDays) {
            7 -> "CONSECRATION PHASE UNLOCKED: 7-day consistency calibration active."
            14 -> "DOMINANCE LEVEL REACHED: 14-day streak locks down cognitive registers."
            21 -> "RESONANCE ENGAGED: 21-day streak aligns system and spirit."
            30 -> "ULTIMATE ASCENSION: 30-day streak achieves total temporal harmony."
            60 -> "TRANSCENDENCE ACTIVE: 60-day path of continuous boundary limits."
            90 -> "IMMORTAL CALIBRATION: 90 days of unbroken operational power."
            180 -> "OVERLORD PROTOCOL: 180 days. You control the timeline."
            365 -> "CHRONOS COMPLETE: 365 days. A monument of perfect discipline."
            else -> "$streakDays days of continuous progress achieved."
        }
    }

    val text1 = "[ PROTOCOL $label ]"
    val text2 = milestoneMessage
    val text3 = "XP multiplier: ×$multiplier"

    val infiniteTransition = rememberInfiniteTransition(label = "streak_milestone_blink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    // Concentric sci-fi rings graphic rotations
    val ringTransition = rememberInfiniteTransition(label = "ring_rotation")
    val rotation1 by ringTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation1"
    )
    val rotation2 by ringTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation2"
    )

    // Particle Explosion for Day 30
    val isNumberFullyVisible = remember { derivedStateOf { numberScaleState >= 0.95f } }
    val particleAnim = remember { Animatable(0f) }
    val density = LocalDensity.current
    val particles = remember(isNumberFullyVisible.value, density) {
        if (isNumberFullyVisible.value && is30Day) {
            val colorsList = listOf(LegendaryGold, SystemGreen, SystemGlint)
            with(density) {
                List(28) { index ->
                    val angle = Random.nextFloat() * 360f
                    val angleRad = Math.toRadians(angle.toDouble())
                    val speedDp = 160f + Random.nextFloat() * 180f
                    val sizeDp = 5f + Random.nextFloat() * 5f
                    val elongation = 1.4f + Random.nextFloat() * 1.6f
                    MilestoneParticle(
                        angle = angle,
                        speedPx = speedDp.dp.toPx(),
                        cosAngle = kotlin.math.cos(angleRad).toFloat(),
                        sinAngle = kotlin.math.sin(angleRad).toFloat(),
                        color = colorsList[index % colorsList.size],
                        radiusPx = sizeDp.dp.toPx() / 2f,
                        elongation = elongation
                    )
                }
            }
        } else {
            emptyList()
        }
    }

    LaunchedEffect(isNumberFullyVisible.value) {
        if (isNumberFullyVisible.value && is30Day) {
            particleAnim.snapTo(0f)
            particleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(700, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(Unit) {
        // VoidBlack background fade
        animate(0f, 0.92f, animationSpec = tween(250)) { v, _ ->
            overlayAlphaState = v
        }

        // Parallel light beam rises
        animate(0f, 1f, animationSpec = tween(450, easing = EaseInOut)) { v, _ ->
            beamProgressState = v
        }

        // Streak number spring pop
        animate(
            0f, 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) { v, _ ->
            numberScaleState = v
        }

        // Typewriter lines
        for (i in 1..text1.length) {
            text1ProgState = i
            delay(35)
        }
        for (i in 1..text2.length) {
            text2ProgState = i
            delay(20)
        }
        for (i in 1..text3.length) {
            text3ProgState = i
            delay(30)
        }

        delay(1500)
        canDismissState = true
    }

    val mappedScale = numberScaleState * if (numberScaleState < 0.9f) 1.15f else (1.15f - (numberScaleState - 0.9f) * 1.5f).coerceAtLeast(1.0f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack.copy(alpha = overlayAlphaState))
            .clickable(enabled = canDismissState) {
                onDismiss()
            }
            .testTag("streak_milestone_overlay"),
        contentAlignment = Alignment.Center
    ) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        // Screen Edge Gold Glow Atmosphere (Day 30 exclusive)
        if (is30Day) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(overlayAlphaState)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(LegendaryGold.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(screenWidthPx / 2f, screenHeightPx / 2f),
                            radius = screenWidthPx * 0.9f
                        )
                    )
            )

            // Divine Light Beam Column
            Box(
                modifier = Modifier
                    .fillMaxHeight(beamProgressState)
                    .width(48.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, LegendaryGold.copy(alpha = 0.18f), Color.Transparent)
                        )
                    )
                    .align(Alignment.BottomCenter)
            )

            // Center spine bright line
            Box(
                modifier = Modifier
                    .fillMaxHeight(beamProgressState)
                    .width(3.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, LegendaryGold.copy(alpha = 0.45f), Color.Transparent)
                        )
                    )
                    .align(Alignment.BottomCenter)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Day Number with optional Rings Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(if (is30Day) 240.dp else 160.dp)
            ) {
                if (is30Day) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerPx = Offset(size.width / 2f, size.height / 2f)

                        // Outer ring rotating at 20deg/sec
                        withTransform({
                            rotate(rotation1, centerPx)
                        }) {
                            drawCircle(
                                color = LegendaryGold.copy(alpha = 0.35f),
                                radius = 70.dp.toPx(),
                                center = centerPx,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(45f, 15f), 0f)
                                )
                            )
                        }

                        // Inner ring rotating at -12deg/sec
                        withTransform({
                            rotate(rotation2, centerPx)
                        }) {
                            drawCircle(
                                color = SystemGreen.copy(alpha = 0.25f),
                                radius = 90.dp.toPx(),
                                center = centerPx,
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 30f), 0f)
                                )
                            )
                        }
                    }
                }

                // Day Number
                Text(
                    text = "$streakDays",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (is30Day) 110.sp else 96.sp,
                    color = if (is30Day) LegendaryGold else SystemGreen,
                    modifier = Modifier
                        .scale(mappedScale)
                        .testTag("ceremony_glyph")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Typewriter Label
            if (text1ProgState > 0) {
                Text(
                    text = text1.take(text1ProgState),
                    fontFamily = JetBrainsMono,
                    fontSize = 15.sp,
                    color = if (is30Day) LegendaryGold else SystemGreen,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_title")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Achieve message
            if (text2ProgState > 0) {
                Text(
                    text = text2.take(text2ProgState),
                    fontFamily = Inter,
                    fontSize = 17.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_subtitle")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // XP Multiplier multiplier text
            if (text3ProgState > 0) {
                Text(
                    text = text3.take(text3ProgState),
                    fontFamily = JetBrainsMono,
                    fontSize = 18.sp,
                    color = LegendaryGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("multiplier_text")
                )
            }
        }

        // Particle Explosion Simulation (Day 30 exclusive)
        if (is30Day && isNumberFullyVisible.value && particleAnim.value < 1.0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerPx = Offset(size.width / 2f, size.height / 2f)
                val elapsed = particleAnim.value
                val alpha = (1f - elapsed).coerceIn(0f, 1f)

                particles.forEach { particle ->
                    val travelPx = particle.speedPx * elapsed
                    val px = centerPx.x + travelPx * particle.cosAngle
                    val py = centerPx.y + travelPx * particle.sinAngle

                    withTransform({
                        translate(px, py)
                        rotate(particle.angle)
                        scale(particle.elongation, 1f)
                    }) {
                        drawCircle(
                            color = particle.color,
                            radius = particle.radiusPx,
                            center = Offset.Zero,
                            alpha = alpha
                        )
                    }
                }
            }
        }

        // Tap back instructions
        if (canDismissState) {
            Text(
                text = stringResource(R.string.ceremony_tap_continue),
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = TextDim,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .alpha(blinkAlpha)
                    .testTag("ceremony_tap_continue")
            )
        }
    }
}
