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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class CeremonyParticle(
    val angle: Float,
    val speedPx: Float,
    val cosAngle: Float,
    val sinAngle: Float,
    val color: Color,
    val radiusPx: Float,
    val elongation: Float
)

@Composable
fun LevelUpCeremony(
    newLevel: Int,
    hunterName: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var overlayAlphaState by remember { mutableStateOf(0f) }
    var beamProgressState by remember { mutableStateOf(0f) }
    var glyphScaleState by remember { mutableStateOf(0f) }
    var text1ProgState by remember { mutableStateOf(0) }
    var text2ProgState by remember { mutableStateOf(0) }
    var xpProgressState by remember { mutableStateOf(0f) }
    var canDismissState by remember { mutableStateOf(false) }
    var isDismissingState by remember { mutableStateOf(false) }

    val accessibilityManager = androidx.compose.ui.platform.LocalAccessibilityManager.current
    val reduceMotion = accessibilityManager?.isReducedMotionEnabled == true

    val text1 = "[ LEVEL UP ]"
    val text2 = "Hunter Level $newLevel"

    val rankLabel = remember(newLevel) { XPEngine.calculateHunterRank(newLevel) }
    val glyph = remember(rankLabel) { XPEngine.getGlyphForRank(rankLabel) }
    val rankColorVal = remember(rankLabel) { XPEngine.getRankColor(rankLabel) }
    val rankColor = remember(rankColorVal) { Color(rankColorVal.toInt()) }

    // Blinking [ TAP TO CONTINUE ]
    val infiniteTransition = rememberInfiniteTransition(label = "tap_blink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    // 1. PARTICLE EXPLOSION STATE & TRIGGERS
    val isGlyphFullyVisible = remember { derivedStateOf { glyphScaleState >= 0.95f } }
    val particleAnim = remember { Animatable(0f) }
    val density = LocalDensity.current
    val particles = remember(isGlyphFullyVisible.value, density, reduceMotion) {
        if (isGlyphFullyVisible.value && !reduceMotion) {
            val colorsList = listOf(rankColor, SystemGlint, LegendaryGold)
            with(density) {
                List(24) { index ->
                    val angle = Random.nextFloat() * 360f
                    val angleRad = Math.toRadians(angle.toDouble())
                    val speedDp = 150f + Random.nextFloat() * 150f // 150 to 300 dp over 600ms
                    val sizeDp = 4f + Random.nextFloat() * 4f // 4 to 8 dp
                    val elongation = 1.5f + Random.nextFloat() * 1.5f // slightly elongated
                    CeremonyParticle(
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

    LaunchedEffect(isGlyphFullyVisible.value) {
        if (isGlyphFullyVisible.value) {
            particleAnim.snapTo(0f)
            particleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = LinearEasing)
            )
        }
    }

    // 3. GLYPH CIRCLE ENHANCEMENT ROTATORS
    val ringTransition = rememberInfiniteTransition(label = "ring_rotation")
    val rotation1 by ringTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing), // 20 deg/sec = 18s total
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation1"
    )
    val rotation2 by ringTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing), // -12 deg/sec = 30s total
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation2"
    )
    val rotation3 by ringTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing), // 8 deg/sec = 45s total
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation3"
    )

    val ringAlpha by animateFloatAsState(
        targetValue = if (isGlyphFullyVisible.value) 1f else 0f,
        animationSpec = tween(if (reduceMotion) 0 else 200, easing = LinearEasing),
        label = "ring_alpha"
    )

    // 5. RANK TEXT EMBELLISHMENT SWEETER SHIMMER
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer_sweep")
    val shimmerProgress by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    LaunchedEffect(Unit) {
        if (reduceMotion) {
            overlayAlphaState = 0.95f
            beamProgressState = 1f
            glyphScaleState = 1f
            text1ProgState = text1.length
            text2ProgState = text2.length
            xpProgressState = 1f
            canDismissState = true
            com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.LEVEL_UP)
        } else {
            // Phase 1 (0–200ms): VoidBlack overlay fades to 95% alpha.
            animate(0f, 0.95f, animationSpec = tween(200)) { v, _ ->
                overlayAlphaState = v
            }

            // Phase 2 (200–600ms): Vertical light beam rises from bottom. 400ms duration
            animate(0f, 1f, animationSpec = tween(400, easing = EaseInOut)) { v, _ ->
                beamProgressState = v
            }

            // Phase 3 (600–900ms): Hunter glyph appears at screen center. 300ms duration
            com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.LEVEL_UP)
            animate(
                0f, 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) { v, _ ->
                glyphScaleState = v
            }

            // Phase 4 (900ms+): Typewriter below glyph
            for (i in 1..text1.length) {
                text1ProgState = i
                delay(40)
            }
            for (i in 1..text2.length) {
                text2ProgState = i
                delay(30)
            }

            // Phase 5: XP bar sweeps left->right at overlay bottom (500ms EaseOutCubic)
            animate(0f, 1f, animationSpec = tween(500, easing = EaseOutCubic)) { v, _ ->
                xpProgressState = v
            }

            // Phase 6: Tap to continue active
            canDismissState = true
        }
    }

    val finalAlpha = if (isDismissingState) overlayAlphaState else 0.95f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack.copy(alpha = finalAlpha))
            .clickable(enabled = canDismissState && !isDismissingState) {
                isDismissingState = true
                onDismiss()
            }
            .testTag("level_up_ceremony_overlay"),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        // 4. SCREEN EDGE GLOW:
        // Add a Box filling the screen with radial gradient blooming colored atmosphere
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(overlayAlphaState)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(rankColor.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(screenWidthPx / 2f, screenHeightPx / 2f),
                        radius = screenWidthPx * 0.8f
                    )
                )
        )

        // 2. LIGHT BEAM ENHANCEMENT (A multi-faceted "divine light column" vertical beam gradient assembly)
        // Main Center Beam (40dp wide, vertical transparent -> rankColor.copy(0.15f) -> transparent)
        Box(
            modifier = Modifier
                .fillMaxHeight(beamProgressState)
                .width(40.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, rankColor.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // Center spine bright line
        Box(
            modifier = Modifier
                .fillMaxHeight(beamProgressState)
                .width(2.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, rankColor.copy(alpha = 0.40f), Color.Transparent)
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // Symmetrical parallel beams at ±15dp and ±30dp offsets with reduced opacities (30% of center opacity -> 0.045f)
        listOf(-30.dp, -15.dp, 15.dp, 30.dp).forEach { offsetValue ->
            Box(
                modifier = Modifier
                    .fillMaxHeight(beamProgressState)
                    .width(16.dp)
                    .offset(x = offsetValue)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, rankColor.copy(alpha = 0.045f), Color.Transparent)
                        )
                    )
                    .align(Alignment.BottomCenter)
            )
        }

        // Main Center Content Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // 3. GLYPH CIRCLE ENHANCEMENT
            // Wrap glyph in a centered sizing Box containing the 3 rotating concentric rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                // Concentric sci-fi rings graphic canvas rotating at exact specified speeds
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerPx = Offset(size.width / 2f, size.height / 2f)

                    // Ring 1 (72dp radius): rankColor.copy(alpha=0.3f), 2dp stroke, rotates continuously at 20deg/sec
                    withTransform({
                        rotate(if (reduceMotion) 0f else rotation1, centerPx)
                    }) {
                        drawCircle(
                            color = rankColor.copy(alpha = 0.3f * ringAlpha),
                            radius = 72.dp.toPx(),
                            center = centerPx,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 15f), 0f)
                            )
                        )
                    }

                    // Ring 2 (96dp radius): rankColor.copy(alpha=0.15f), 1dp stroke, rotates at -12deg/sec
                    withTransform({
                        rotate(if (reduceMotion) 0f else rotation2, centerPx)
                    }) {
                        drawCircle(
                            color = rankColor.copy(alpha = 0.15f * ringAlpha),
                            radius = 96.dp.toPx(),
                            center = centerPx,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 30f), 0f)
                            )
                        )
                    }

                    // Ring 3 (120dp radius): rankColor.copy(alpha=0.08f), 1dp stroke, rotates at 8deg/sec
                    withTransform({
                        rotate(if (reduceMotion) 0f else rotation3, centerPx)
                    }) {
                        drawCircle(
                            color = rankColor.copy(alpha = 0.08f * ringAlpha),
                            radius = 120.dp.toPx(),
                            center = centerPx,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(60f, 20f), 0f)
                            )
                        )
                    }
                }

                // Inner glyph itself scale animated
                if (glyphScaleState > 0f) {
                    Text(
                        text = glyph,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 96.sp,
                        color = rankColor,
                        modifier = Modifier
                            .scale(glyphScaleState)
                            .testTag("ceremony_glyph")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Typewriter Text 1
            if (text1ProgState > 0) {
                Text(
                    text = text1.take(text1ProgState),
                    fontFamily = JetBrainsMono,
                    fontSize = 16.sp,
                    color = LocalAxiomColors.current.systemGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("ceremony_title")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Typewriter Text 2
            if (text2ProgState > 0) {
                Text(
                    text = text2.take(text2ProgState),
                    fontFamily = Inter,
                    fontSize = 32.sp,
                    color = LocalAxiomColors.current.textPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("ceremony_subtitle")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. RANK TEXT ENHANCEMENT: Explicit display of rank label with the sweep shimmer
            if (text2ProgState == text2.length) {
                Text(
                    text = stringResource(R.string.ceremony_rank_hunter, rankLabel),
                    fontFamily = JetBrainsMono,
                    fontSize = 20.sp,
                    color = rankColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .testTag("ceremony_rank_label")
                        .drawWithContent {
                            drawContent()
                            
                            val widthPx = size.width
                            val heightPx = size.height
                            val stripWidth = 20.dp.toPx()
                            
                            // Translate highlight from -stripWidth to widthPx + stripWidth based on shimmerProgress
                            val translationX = -stripWidth + (widthPx + 2 * stripWidth) * shimmerProgress

                            drawRect(
                                color = Color.White.copy(alpha = 0.4f),
                                topLeft = Offset(translationX, 0f),
                                size = Size(stripWidth, heightPx),
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                )
            }
        }

        // 1. PARTICLE EXPLOSION CANVAS
        if (isGlyphFullyVisible.value && particleAnim.value < 1.0f) {
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
                        // Drawing aligned at local Offset.Zero center due to translate
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

        // Phase 5: XP bar sweeps at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(DimSurface)
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(xpProgressState)
                    .fillMaxHeight()
                    .background(SystemGreen)
            )
        }

        // Phase 6: Blink Tap to Continue
        if (canDismissState) {
            Text(
                text = stringResource(R.string.ceremony_tap_continue),
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = LocalAxiomColors.current.textDim,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    // graphicsLayer defers the read to the draw phase — blinkAlpha ticks
                    // indefinitely for as long as the ceremony is undismissed, and a
                    // plain .alpha() value read would force recomposition every tick.
                    .graphicsLayer { alpha = blinkAlpha }
                    .testTag("ceremony_tap_continue")
            )
        }
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

