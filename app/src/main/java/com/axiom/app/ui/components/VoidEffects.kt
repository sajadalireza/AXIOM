package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import com.axiom.app.ui.theme.*

// 1. VoidParticleField Background Composable
private data class VoidParticle(
    val fractionX: Float,
    val seedOffset: Float,
    val speed: Float,
    val radiusDp: Float,
    val baseColor: Color,
    val baseAlpha: Float
)

@Composable
fun VoidParticleField(
    modifier           : Modifier = Modifier,
    particleMultiplier : Float    = 1f
) {
    val count     = (60 * particleMultiplier.coerceIn(1f, 3f)).toInt()
    val particles = remember(particleMultiplier) {
        val random = java.util.Random(1337)
        List(count) {
            VoidParticle(
                fractionX = random.nextFloat(),
                seedOffset = random.nextFloat(),
                speed = 0.02f + random.nextFloat() * 0.08f, // Slow, peaceful upward drift speed
                radiusDp = 1f + random.nextFloat() * 2f, // 1 to 3 dp radius
                baseColor = if (random.nextBoolean()) BorderFaint else TextDim,
                baseAlpha = 0.2f + random.nextFloat() * 0.3f // 0.2 to 0.5 alpha
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "void_stars_infinite")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "master_time_void"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Starfield base black void depth
        drawRect(color = VoidBlack)

        particles.forEach { particle ->
            // Seeded x offset
            val x = particle.fractionX * size.width

            // Slowly drift upward: decrease y with time, loop wrap positive
            val rawY = (particle.seedOffset - time * particle.speed) % 1.0f
            val normalizedY = if (rawY < 0f) rawY + 1.0f else rawY
            val y = normalizedY * size.height

            drawCircle(
                color = particle.baseColor.copy(alpha = particle.baseAlpha),
                radius = particle.radiusDp.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

// 2. NeonGlowBorder Modifier Extension
fun Modifier.neonGlow(
    color: Color,
    glowRadius: Dp = 8.dp,
    intensity: Float = 0.7f
): Modifier = this.drawBehind {
    val cornerRadius = 6.dp.toPx()

    // Base background layer
    drawRoundRect(
        color = color.copy(alpha = (intensity * 0.3f).coerceIn(0f, 1f)),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )

    // Outermost blur layer (4dp pad)
    drawRoundRect(
        color = color.copy(alpha = (0.08f * intensity).coerceIn(0f, 1f)),
        cornerRadius = CornerRadius(cornerRadius + 4.dp.toPx(), cornerRadius + 4.dp.toPx()),
        style = Stroke(width = 4.dp.toPx())
    )

    // Middle blur layer (2dp pad)
    drawRoundRect(
        color = color.copy(alpha = (0.15f * intensity).coerceIn(0f, 1f)),
        cornerRadius = CornerRadius(cornerRadius + 2.dp.toPx(), cornerRadius + 2.dp.toPx()),
        style = Stroke(width = 2.dp.toPx())
    )

    // Inner sharp edge highlight (1dp pad)
    drawRoundRect(
        color = color.copy(alpha = (0.4f * intensity).coerceIn(0f, 1f)),
        cornerRadius = CornerRadius(cornerRadius + 1.dp.toPx(), cornerRadius + 1.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )
}

// 3. AnimatedScanlineOverlay Composable
@Composable
fun AnimatedScanlineOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_scanline_infinite")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline_progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw subtle tiled hexagonal grid
        val hexRadius = 24.dp.toPx()
        val w = 1.7320508f * hexRadius
        val h = 2f * hexRadius

        val cols = (size.width / w).toInt() + 2
        val rows = (size.height / (1.5f * hexRadius)).toInt() + 2

        val path = Path()
        for (row in -1..rows) {
            for (col in -1..cols) {
                val cx = col * w + (if (row % 2 != 0) w / 2f else 0f)
                val cy = row * 1.5f * hexRadius

                path.reset()
                path.moveTo(cx, cy - hexRadius)
                path.lineTo(cx + w / 2f, cy - hexRadius / 2f)
                path.lineTo(cx + w / 2f, cy + hexRadius / 2f)
                path.lineTo(cx, cy + hexRadius)
                path.lineTo(cx - w / 2f, cy + hexRadius / 2f)
                path.lineTo(cx - w / 2f, cy - hexRadius / 2f)
                path.close()

                drawPath(
                    path = path,
                    color = BorderFaint.copy(alpha = 0.03f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Draw 4 horizontal scan lines moving at staggered downward speeds
        val lineHeights = 1.dp.toPx()
        val scanlineColor = SystemGreen.copy(alpha = 0.04f)

        // Line 1 speed factor = 1.0, seedOffset = 0.1
        val y1 = ((0.1f + progress * 1.0f) % 1.0f) * size.height
        // Line 2 speed factor = 1.4, seedOffset = 0.4
        val y2 = ((0.4f + progress * 1.4f) % 1.0f) * size.height
        // Line 3 speed factor = 0.7, seedOffset = 0.6
        val y3 = ((0.6f + progress * 0.7f) % 1.0f) * size.height
        // Line 4 speed factor = 2.1, seedOffset = 0.8
        val y4 = ((0.8f + progress * 2.1f) % 1.0f) * size.height

        drawLine(color = scanlineColor, start = Offset(0f, y1), end = Offset(size.width, y1), strokeWidth = lineHeights)
        drawLine(color = scanlineColor, start = Offset(0f, y2), end = Offset(size.width, y2), strokeWidth = lineHeights)
        drawLine(color = scanlineColor, start = Offset(0f, y3), end = Offset(size.width, y3), strokeWidth = lineHeights)
        drawLine(color = scanlineColor, start = Offset(0f, y4), end = Offset(size.width, y4), strokeWidth = lineHeights)
    }
}

// 4. RarityGlowPulse Modifier Composable
fun Modifier.rarityGlowPulse(
    idColor: Color,
    enabled: Boolean = true
): Modifier = this.composed {
    val intensity = if (enabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "rarity_pulse_infinite")
        val pulsedValue by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulsed_value"
        )
        pulsedValue
    } else {
        0.3f
    }
    neonGlow(color = idColor, intensity = intensity)
}

// 5. HolographicCard Composable Container
@Composable
fun HolographicCard(
    modifier: Modifier = Modifier,
    accentColor: Color = SystemGreen,
    glowEnabled: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer_sweep_infinite")
    val shimmerProgress by shimmerTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    // Using Solo Leveling high-fantasy container shape (clipped so internally elements don't bleed out of bevels)
    val cardModifier = modifier
        .soloLevelingCard(
            accentColor = if (glowEnabled) accentColor else BorderFaint,
            bevel = 16f,
            borderWidth = 1.5f,
            glowRadius = if (glowEnabled) 8f else 0f,
            showSideNotches = true,
            backgroundColor = ShadowSurface
        )
        .let {
            if (glowEnabled) {
                it.rarityGlowPulse(idColor = accentColor, enabled = true)
            } else {
                it
            }
        }
        .clip(SoloLevelingBeveledShape(bevel = 16f, showSideNotches = true))

    Box(modifier = cardModifier) {
        // Shimmer Sweep layer (drawn within the beveled container)
        Canvas(modifier = Modifier.matchParentSize()) {
            val progress = shimmerProgress
            val xOffset = size.width * progress
            val gradientWidth = size.width * 0.4f

            val brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    accentColor.copy(alpha = 0.04f),
                    accentColor.copy(alpha = 0.08f),
                    accentColor.copy(alpha = 0.04f),
                    Color.Transparent
                ),
                start = Offset(xOffset - gradientWidth, 0f),
                end = Offset(xOffset + gradientWidth, size.height)
            )

            // Draw beveled shimmer background path matching the Solo Leveling geometry
            val pathBevel = Path().apply {
                val bevelSize = 16f
                val w = size.width
                val h = size.height
                moveTo(0f, bevelSize)
                lineTo(bevelSize, 0f)
                lineTo(w - bevelSize, 0f)
                lineTo(w, bevelSize)
                
                val midY = h / 2f
                val notchSize = bevelSize * 0.4f
                lineTo(w, midY - notchSize)
                lineTo(w - notchSize, midY)
                lineTo(w, midY + notchSize)
                
                lineTo(w, h - bevelSize)
                lineTo(w - bevelSize, h)
                lineTo(bevelSize, h)
                lineTo(0f, h - bevelSize)
                
                lineTo(0f, midY + notchSize)
                lineTo(notchSize, midY)
                lineTo(0f, midY - notchSize)
                close()
            }
            drawPath(
                path = pathBevel,
                brush = brush
            )
        }

        // Inner layout container for content (with 0dp margins, caller matches styling)
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

// 6. CyberProgressBar Composable UI Component
@Composable
fun CyberProgressBar(
    progress: Float,
    color: Color = SystemGreen,
    trackColor: Color = BorderFaint,
    modifier: Modifier = Modifier,
    showGlow: Boolean = true,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) tween(1000, easing = EaseOutCubic) else snap(),
        label = "cyber_progress_tween"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // High-fantasy angled segmented display drawn on a custom Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@Canvas
            
            // 1. Draw track bar with sharp beveled ends (continuous outline)
            val trackBevel = 6f
            val pathTrack = Path().apply {
                moveTo(0f, trackBevel)
                lineTo(trackBevel, 0f)
                lineTo(w - trackBevel, 0f)
                lineTo(w, trackBevel)
                lineTo(w, h - trackBevel)
                lineTo(w - trackBevel, h)
                lineTo(trackBevel, h)
                lineTo(0f, h - trackBevel)
                close()
            }
            drawPath(path = pathTrack, color = trackColor.copy(alpha = 0.15f))
            drawPath(path = pathTrack, color = trackColor.copy(alpha = 0.4f), style = Stroke(width = 1f))
            
            // 2. Draw progress fill as beautiful, slanted parallel active block segments
            val numSegments = 16
            val spacing = 4.dp.toPx()
            val segmentWidth = (w - (numSegments - 1) * spacing) / numSegments
            
            val activeSegmentsCount = (numSegments * animatedProgress).toInt()
            val fractionalSegmentProgress = (numSegments * animatedProgress) - activeSegmentsCount
            
            for (i in 0 until numSegments) {
                val startX = i * (segmentWidth + spacing)
                val isFullyActive = i < activeSegmentsCount
                val isFractional = i == activeSegmentsCount && fractionalSegmentProgress > 0f
                
                if (isFullyActive || isFractional) {
                    val segProgressWidth = if (isFullyActive) segmentWidth else segmentWidth * fractionalSegmentProgress
                    if (segProgressWidth <= 0f) continue
                    
                    val slantOffset = 3.dp.toPx() // 45-degree slant offset
                    val pathSegment = Path().apply {
                        moveTo(startX + slantOffset, 0f)
                        lineTo(startX + segProgressWidth + slantOffset, 0f)
                        lineTo(startX + segProgressWidth, h)
                        lineTo(startX, h)
                        close()
                    }
                    
                    // Outer glow behind segment
                    if (showGlow) {
                        drawPath(
                            path = pathSegment,
                            color = color.copy(alpha = 0.22f),
                            style = Stroke(width = 5.dp.toPx())
                        )
                    }
                    
                    // Fill segment with vibrant gradient
                    drawPath(
                        path = pathSegment,
                        brush = Brush.verticalGradient(
                            colors = listOf(color, color.copy(alpha = 0.65f))
                        )
                    )
                }
            }
            
            // 3. Draw runic side-wings (outward pointed triangles) representing system rank bounds
            val wingSize = 5.dp.toPx()
            val leftWing = Path().apply {
                moveTo(-spacing / 2f, h / 2f)
                lineTo(-spacing / 2f - wingSize, h / 2f - wingSize)
                lineTo(-spacing / 2f - wingSize, h / 2f + wingSize)
                close()
            }
            drawPath(path = leftWing, color = color.copy(alpha = if (animatedProgress > 0) 1f else 0.4f))
            
            val rightWing = Path().apply {
                moveTo(w + spacing / 2f, h / 2f)
                lineTo(w + spacing / 2f + wingSize, h / 2f - wingSize)
                lineTo(w + spacing / 2f + wingSize, h / 2f + wingSize)
                close()
            }
            drawPath(path = rightWing, color = color.copy(alpha = if (animatedProgress >= 1f) 1f else 0.4f))
        }
    }
}

@Composable
fun MissionParticleBurst(
    trigger: Boolean,
    rarityColor: Color,
    isLegendary: Boolean = false,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return
    val particleCount = if (isLegendary) 16 else 12
    val travelDp     = if (isLegendary) 56.dp else 48.dp
    val scope        = rememberCoroutineScope()
    val progresses   = remember(trigger) { List(particleCount) { Animatable(0f) } }
    val alphas       = remember(trigger) { List(particleCount) { Animatable(1f) } }

    LaunchedEffect(trigger) {
        if (!trigger) return@LaunchedEffect
        val jobs1 = progresses.map { anim ->
            scope.launch { anim.animateTo(1f, tween(300, easing = FastOutSlowInEasing)) }
        }
        val jobs2 = alphas.map { anim ->
            scope.launch {
                kotlinx.coroutines.delay(300)
                anim.animateTo(0f, tween(300, easing = LinearEasing))
            }
        }
        (jobs1 + jobs2).forEach { it.join() }
        onComplete()
    }

    val travelPx       = with(LocalDensity.current) { travelDp.toPx() }
    val particleRadius = with(LocalDensity.current) { 3.dp.toPx() }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val step = (2.0 * kotlin.math.PI / particleCount).toFloat()
        progresses.forEachIndexed { i, prog ->
            val angle = i * step
            val dx = kotlin.math.cos(angle.toDouble()).toFloat() * travelPx * prog.value
            val dy = kotlin.math.sin(angle.toDouble()).toFloat() * travelPx * prog.value
            val color = if (isLegendary && i % 4 == 0) LegendaryGold else rarityColor
            drawCircle(
                color  = color.copy(alpha = alphas[i].value),
                radius = particleRadius,
                center = androidx.compose.ui.geometry.Offset(cx + dx, cy + dy)
            )
        }
    }
}

