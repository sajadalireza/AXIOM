package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.ui.theme.*
import kotlin.math.sin
import kotlin.math.PI

private data class FlameSpark(
    val initialXMult: Float,
    val speedY: Float,
    val size: Float,
    val phaseOffset: Float
)

@Composable
fun StreakFlameWidget(
    streakDays: Int,
    multiplier: Float,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmber = streakDays == 0

    // Infinite transitions for flame flickering & dancing
    val infiniteTransition = rememberInfiniteTransition(label = "flame_dance_infinite")
    
    // Vertical scaling of the flame
    val scaleY by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale_y"
    )

    // Sideways flicker/distortion
    val flickerX by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_flicker_x"
    )

    // Upward ember particles timing
    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flame_particle_time"
    )

    // Fixed set of sparks to avoid recomposition allocations
    val sparks = remember {
        java.util.Random(42).let { rand ->
            List(12) {
                FlameSpark(
                    initialXMult = 0.25f + rand.nextFloat() * 0.5f,
                    speedY = 0.4f + rand.nextFloat() * 0.6f,
                    size = 3f + rand.nextFloat() * 4f,
                    phaseOffset = rand.nextFloat() * 2f * PI.toFloat()
                )
            }
        }
    }

    // Outer flame color profile based on state and streak length
    val flameColorOuter = when {
        isEmber -> Color(0xFF2C3E50) // Cold blue gray
        streakDays in 1..7 -> Color(0xFF00838F) // Cyan (darker)
        streakDays in 8..30 -> Color(0xFFE65100) // Orange-red
        else -> Color(0xFFD500F9) // Vivid purple/magenta
    }

    // Inner flame color profile
    val flameColorInner = when {
        isEmber -> Color(0xFF34495E) // Dim gray
        streakDays in 1..7 -> Color(0xFF00E5FF) // Cyan (vivid)
        streakDays in 8..30 -> Color(0xFFFFD700) // Gold
        else -> Color(0xFFFFEB3B) // Yellow / multi-color high glow
    }

    val flameTitle = when {
        isEmber -> "EMBER PROTOCOL: INACTIVE"
        streakDays in 1..7 -> "STREAK PROTOCOL: RECRUIT"
        streakDays in 8..30 -> "STREAK PROTOCOL: OPERATOR"
        else -> "STREAK PROTOCOL: ARCHITECT"
    }

    val flameQuote = if (isEmber) {
        stringResource(R.string.streak_flame_frozen)
    } else {
        stringResource(R.string.streak_flame_burning)
    }

    val colors = LocalAxiomColors.current

    val swayOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_sway_offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ShadowSurface)
            .border(
                1.dp,
                if (isEmber) colors.borderFaint else when {
                    streakDays in 1..7 -> Color(0xFF00E5FF).copy(alpha = 0.35f)
                    streakDays in 8..30 -> LegendaryGold.copy(alpha = 0.45f)
                    else -> Color(0xFFD500F9).copy(alpha = 0.6f)
                },
                RoundedCornerShape(8.dp)
            )
            .semantics {
                contentDescription = "Current streak: $streakDays days"
            }
            .padding(14.dp)
            .testTag("streak_flame_widget")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left block: Canvas with Animated 2D Fire Flame
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 100.dp)
                    .blur(if (isEmber) 0.dp else 0.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val swayPx = swayOffset.dp.toPx()

                    // Draw Background Aura/Glow if burning
                    if (!isEmber) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    flameColorInner.copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                center = Offset(w / 2f, h * 0.7f),
                                radius = w * 0.85f
                            )
                        )
                    }

                    // -- Level 3 Particle Halo (31+ Days) --
                    if (streakDays >= 31) {
                        // Soft pulsing halo background
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFD500F9).copy(alpha = 0.25f),
                                    Color(0xFF00E5FF).copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                center = Offset(w / 2f, h * 0.55f),
                                radius = w * 0.9f
                            )
                        )
                        
                        // Orbiting particles
                        val pCount = 8
                        for (i in 0 until pCount) {
                            val angle = (particleTime * 2 * PI + (i * 2 * PI / pCount)).toFloat()
                            val rx = w * 0.45f
                            val ry = h * 0.3f
                            val px = (w / 2f) + rx * kotlin.math.cos(angle)
                            val py = (h * 0.5f) + ry * sin(angle)
                            
                            val pColor = when (i % 3) {
                                0 -> Color(0xFF00E5FF) // Cyan
                                1 -> Color(0xFFFFEA00) // Gold
                                else -> Color(0xFFD500F9) // Purple
                            }
                            drawCircle(
                                color = pColor.copy(alpha = 0.75f),
                                radius = 3.dp.toPx() * (0.6f + 0.4f * sin(angle * 2)),
                                center = Offset(px, py)
                            )
                        }
                    }

                    // -- Spark Particles drawing loop --
                    sparks.forEach { spark ->
                        val rawY = (1.0f - ((particleTime * spark.speedY) % 1.0f))
                        val sparkY = h * (0.1f + rawY * 0.75f)
                        val driftX = sin((particleTime * 2f * PI.toFloat() + spark.phaseOffset).toDouble()).toFloat() * (w * 0.12f)
                        val sparkX = (w * spark.initialXMult) + driftX
                        val alpha = (rawY * 1.1f).coerceIn(0f, 1f)

                        drawCircle(
                            color = (if (isEmber) flameColorOuter else flameColorInner).copy(alpha = alpha * 0.7f),
                            radius = spark.size * (0.4f + rawY * 0.6f),
                            center = Offset(sparkX, sparkY)
                        )
                    }

                    // Flame Intensity Scale Factor
                    val intensityScale = when {
                        streakDays == 0 -> 0.5f
                        streakDays in 1..7 -> 0.65f
                        streakDays in 8..30 -> 0.85f
                        else -> 1.05f
                    }

                    // Draw outer and inner flame inside the transform block
                    drawContext.canvas.save()
                    drawContext.transform.scale(intensityScale, intensityScale, Offset(w / 2f, h * 0.8f))

                    // -- Outer Flame Path --
                    val outerPath = Path().apply {
                        val topX = (w / 2f) + (if (isEmber) 0f else swayPx) + flickerX
                        val topY = (h * 0.15f) + (1f - intensityScale) * (h * 0.15f)
                        moveTo(topX, topY)

                        // Left curve
                        cubicTo(
                            x1 = w * 0.12f, y1 = h * 0.40f * intensityScale,
                            x2 = w * 0.15f, y2 = h * 0.82f,
                            x3 = w * 0.50f, y3 = h * 0.88f
                        )

                        // Right curve
                        cubicTo(
                            x1 = w * 0.85f, y1 = h * 0.82f,
                            x2 = w * 0.88f, y2 = h * 0.40f * intensityScale,
                            x3 = topX, y3 = topY
                        )
                        close()
                    }

                    drawPath(
                        path = outerPath,
                        brush = Brush.verticalGradient(
                            colors = if (streakDays >= 31) {
                                listOf(
                                    Color(0xFFD500F9),
                                    Color(0xFFF50057)
                                )
                            } else {
                                listOf(
                                    flameColorOuter.copy(alpha = 0.9f),
                                    flameColorOuter.copy(alpha = 0.4f)
                                )
                            }
                        )
                    )

                    // -- Inner Core Flame Path --
                    val innerPath = Path().apply {
                        val innerScaleY = intensityScale * 1.05f
                        val topInnerX = (w / 2f) + (if (isEmber) 0f else (swayPx * 0.5f)) + (flickerX * 0.5f)
                        val topInnerY = (h * 0.38f) + (1f - innerScaleY) * (h * 0.12f)
                        moveTo(topInnerX, topInnerY)

                        // Left curve
                        cubicTo(
                            x1 = w * 0.22f, y1 = h * 0.52f * innerScaleY,
                            x2 = w * 0.28f, y2 = h * 0.80f,
                            x3 = w * 0.50f, y3 = h * 0.85f
                        )

                        // Right curve
                        cubicTo(
                            x1 = w * 0.72f, y1 = h * 0.80f,
                            x2 = w * 0.78f, y2 = h * 0.52f * innerScaleY,
                            x3 = topInnerX, y3 = topInnerY
                        )
                        close()
                    }

                    drawPath(
                        path = innerPath,
                        brush = Brush.verticalGradient(
                            colors = if (streakDays >= 31) {
                                listOf(
                                    Color(0xFFFFEA00),
                                    Color(0xFF00E5FF)
                                )
                            } else {
                                listOf(
                                    flameColorInner,
                                    flameColorOuter.copy(alpha = 0.8f)
                                )
                            }
                        )
                    )

                    // Draw base anchor core glow indicator
                    drawCircle(
                        color = (if (isEmber) flameColorOuter else Color.White).copy(alpha = 0.35f),
                        radius = w * 0.08f,
                        center = Offset(w / 2f, h * 0.78f)
                    )

                    drawContext.canvas.restore()
                }

                // Streak number rendered in monospace, inside the base of the flame in white
                if (streakDays > 0) {
                    Text(
                        text = streakDays.toString(),
                        style = HudM.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FiraCode
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 22.dp)
                    )
                }
            }

            // Right block: Info, counts and Persian motivational messaging
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = flameTitle,
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEmber) colors.textDim else LegendaryGold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = streakDays.toString(),
                        fontFamily = JetBrainsMono,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isEmber) colors.textSecondary else LegendaryGold
                    )
                    Text(
                        text = stringResource(R.string.streak_days_label),
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = flameQuote,
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )

                if (multiplier > 1.0f) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .border(0.5.dp, LegendaryGold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                            .background(LegendaryGold.copy(alpha = 0.08f))
                            .clickable { onNavigateToPremium() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚡ SECURED MULTIPLIER: ${multiplier}x",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegendaryGold
                        )
                    }
                }
            }
        }
    }
}
