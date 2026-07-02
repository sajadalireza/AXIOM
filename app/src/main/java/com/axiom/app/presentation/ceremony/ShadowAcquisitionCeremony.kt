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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

private data class ShadowParticle(
    val startX: Float, // random edge location ratio
    val startY: Float,
    val delayOffset: Float,
    val pxSize: Float
)

private fun getTargetOffset(index: Int, total: Int, shapeType: Int, cx: Float, cy: Float, r: Float): Offset {
    return when (shapeType) {
        0 -> {
            // Triangle: 3 segments
            val segmentSize = total / 3
            when {
                index < segmentSize -> {
                    val t = index.toFloat() / segmentSize
                    val x1 = cx - r * 0.866f
                    val y1 = cy + r * 0.5f
                    val x2 = cx
                    val y2 = cy - r
                    Offset(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t)
                }
                index < 2 * segmentSize -> {
                    val t = (index - segmentSize).toFloat() / segmentSize
                    val x1 = cx
                    val y1 = cy - r
                    val x2 = cx + r * 0.866f
                    val y2 = cy + r * 0.5f
                    Offset(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t)
                }
                else -> {
                    val remSize = total - 2 * segmentSize
                    val t = (index - 2 * segmentSize).toFloat() / remSize
                    val x1 = cx + r * 0.866f
                    val y1 = cy + r * 0.5f
                    val x2 = cx - r * 0.866f
                    val y2 = cy + r * 0.5f
                    Offset(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t)
                }
            }
        }
        1 -> {
            // Hexagon: 6 segments
            val segmentSize = total / 6
            val segIdx = (index / segmentSize).coerceIn(0, 5)
            val t = (index % segmentSize).toFloat() / segmentSize
            
            val angle1 = segIdx * Math.PI / 3.0
            val angle2 = (segIdx + 1) * Math.PI / 3.0
            
            val x1 = cx + r * Math.cos(angle1).toFloat()
            val y1 = cy + r * Math.sin(angle1).toFloat()
            val x2 = cx + r * Math.cos(angle2).toFloat()
            val y2 = cy + r * Math.sin(angle2).toFloat()
            
            Offset(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t)
        }
        else -> {
            // Cross: 2 segments
            val half = total / 2
            if (index < half) {
                val t = index.toFloat() / half
                Offset(cx, cy - r + (r * 2f) * t)
            } else {
                val t = (index - half).toFloat() / (total - half)
                Offset(cx - r + (r * 2f) * t, cy)
            }
        }
    }
}

@Composable
fun ShadowAcquisitionCeremony(
    skillName: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val shapeType = remember(skillName) { kotlin.math.abs(skillName.hashCode()) % 3 }

    // Generate 45 particles starting from outer boundaries
    val particles = remember(density) {
        List(45) { i ->
            // Put start positions along screen edges/random
            val side = i % 4
            val startX = when (side) {
                0 -> 0f
                1 -> 1f
                else -> ((i * 31) % 100) / 100f
            }
            val startY = when (side) {
                2 -> 0f
                3 -> 1f
                else -> ((i * 47) % 100) / 100f
            }
            val pSizeDp = 4 + ((i * 7) % 5)
            ShadowParticle(
                startX = startX,
                startY = startY,
                delayOffset = ((i * 13) % 100) / 100f,
                pxSize = with(density) { pSizeDp.dp.toPx() }
            )
        }
    }

    var overlayAlphaState by remember { mutableStateOf(0f) }
    var convergeProgressState by remember { mutableStateOf(0f) }
    var bowTypewriterText by remember { mutableStateOf("") }
    var nameProgState by remember { mutableStateOf(0) }
    var showSubtitleState by remember { mutableStateOf(false) }
    var canDismissState by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "ceremony_ticker")
    val waveTicker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_ticker"
    )

    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    LaunchedEffect(Unit) {
        // Play Sound immediately
        com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.SHADOW_MANIFEST)

        // Screen fades to near-black over 500ms
        animate(0f, 0.95f, animationSpec = tween(500)) { v, _ ->
            overlayAlphaState = v
        }

        // Particle converge animation: particles fly in from edges over 1500ms
        animate(0f, 1f, animationSpec = tween(1500, easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f))) { v, _ ->
            convergeProgressState = v
        }

        // Typewriter "A NEW SHADOW BOWS TO YOU" over 1500ms
        val fullBowText = "A NEW SHADOW BOWS TO YOU"
        val delayPerChar = 1500 / fullBowText.length
        for (i in 1..fullBowText.length) {
            bowTypewriterText = fullBowText.take(i)
            delay(delayPerChar.toLong())
        }

        // Display actual shadow name letter by letter
        for (i in 1..skillName.length) {
            nameProgState = i
            delay(50)
        }

        showSubtitleState = true
        delay(1500)
        canDismissState = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack.copy(alpha = overlayAlphaState))
            .clickable(enabled = canDismissState) { onDismiss() }
            .testTag("shadow_acquisition_ceremony_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Particles Converge canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val cx = width / 2f
            val cy = height * 0.4f
            val targetRadius = minOf(width, height) * 0.25f

            particles.forEachIndexed { i, particle ->
                val t = ((convergeProgressState - particle.delayOffset * 0.3f) / 0.7f).coerceIn(0f, 1f)
                val sX = width * particle.startX
                val sY = height * particle.startY
                val target = getTargetOffset(i, particles.size, shapeType, cx, cy, targetRadius)
                
                val easeT = CubicBezierEasing(0.15f, 0.85f, 0.15f, 1f).transform(t)
                val curX = sX + (target.x - sX) * easeT
                val curY = sY + (target.y - sY) * easeT

                val pAlpha = if (t < 1f) t.coerceIn(0.1f, 1.0f) else (1.0f - (waveTicker * 0.25f))
                val pxSize = particle.pxSize

                drawContext.canvas.save()
                drawContext.canvas.translate(curX, curY)
                drawContext.canvas.rotate(45f + t * 180f)
                drawRect(
                    color = EpicPurple,
                    topLeft = Offset(-pxSize / 2f, -pxSize / 2f),
                    size = androidx.compose.ui.geometry.Size(pxSize, pxSize),
                    alpha = pAlpha * 0.75f
                )
                drawContext.canvas.restore()
            }

            // Once fully converged, draw solid sigil line on canvas with stroke
            if (convergeProgressState >= 0.95f) {
                val path = Path()
                when (shapeType) {
                    0 -> {
                        // Triangle
                        path.moveTo(cx, cy - targetRadius)
                        path.lineTo(cx + targetRadius * 0.866f, cy + targetRadius * 0.5f)
                        path.lineTo(cx - targetRadius * 0.866f, cy + targetRadius * 0.5f)
                        path.close()
                    }
                    1 -> {
                        // Hexagon
                        for (i in 0 until 6) {
                            val angle = i * Math.PI / 3.0
                            val x = cx + targetRadius * Math.cos(angle).toFloat()
                            val y = cy + targetRadius * Math.sin(angle).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                    }
                    else -> {
                        // Cross lines
                        drawLine(
                            color = EpicPurple,
                            start = Offset(cx, cy - targetRadius),
                            end = Offset(cx, cy + targetRadius),
                            strokeWidth = 3.dp.toPx()
                        )
                        drawLine(
                            color = EpicPurple,
                            start = Offset(cx - targetRadius, cy),
                            end = Offset(cx + targetRadius, cy),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }
                if (shapeType != 2) {
                    drawPath(
                        path = path,
                        color = EpicPurple,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }

        // Glowing outer rings as background
        if (convergeProgressState >= 0.8f) {
            val ringRadiusState = (convergeProgressState - 0.8f) / 0.2f
            val ringRadiusPx = with(density) { (80 * ringRadiusState).dp.toPx() }
            Canvas(modifier = Modifier.size(240.dp)) {
                drawCircle(
                    color = EpicPurple,
                    radius = ringRadiusPx,
                    style = Stroke(width = 1.dp.toPx()),
                    alpha = 0.25f
                )
            }
        }

        // Overlay Column: Typewritten Messages & Labels
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // "A NEW SHADOW BOWS TO YOU" in SystemGreen FiraCode
            if (bowTypewriterText.isNotEmpty()) {
                Text(
                    text = bowTypewriterText,
                    style = SystemMsg,
                    fontFamily = FiraCode,
                    color = LocalAxiomColors.current.systemGreen,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_intro_text")
                )
            }

            // Shadow Name in EpicPurple DisplayL
            if (nameProgState > 0) {
                Text(
                    text = skillName.take(nameProgState).uppercase(),
                    style = DisplayL,
                    color = EpicPurple,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_title")
                )
            }

            // Bound Subtitle Descriptor
            if (showSubtitleState) {
                Text(
                    text = stringResource(R.string.ceremony_shadow_bound).uppercase(),
                    style = LabelS,
                    fontFamily = FiraCode,
                    color = LocalAxiomColors.current.textDim,
                    letterSpacing = 0.1.em,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_subtitle")
                )
            }
        }

        // Tap to dismissal prompt
        if (canDismissState) {
            Text(
                text = stringResource(R.string.ceremony_tap_continue),
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = LocalAxiomColors.current.textDim,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .alpha(blinkAlpha)
                    .testTag("ceremony_tap_continue")
            )
        }
    }
}
