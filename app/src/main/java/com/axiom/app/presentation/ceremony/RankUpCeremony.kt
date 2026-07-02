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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RankUpCeremony(
    oldRank: String,
    newRank: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    var overlayAlphaState by remember { mutableStateOf(0f) }
    var oldGlyphAlphaState by remember { mutableStateOf(0f) }
    var burstProgressState by remember { mutableStateOf(0f) }
    var newGlyphScaleState by remember { mutableStateOf(0f) }
    var text1ProgState by remember { mutableStateOf(0) }
    var text2ProgState by remember { mutableStateOf(0) }
    var canDismissState by remember { mutableStateOf(false) }

    val accessibilityManager = androidx.compose.ui.platform.LocalAccessibilityManager.current
    val reduceMotion = accessibilityManager?.isReducedMotionEnabled == true

    val cleanOldRank = oldRank.replace("-Rank", "").trim()
    val cleanNewRank = newRank.replace("-Rank", "").trim()

    val oldGlyph = remember(cleanOldRank) { XPEngine.getGlyphForRank(cleanOldRank) }
    val newGlyph = remember(cleanNewRank) { XPEngine.getGlyphForRank(cleanNewRank) }

    val oldRankColorVal = remember(cleanOldRank) { XPEngine.getRankColor(cleanOldRank) }
    val oldRankColor = remember(oldRankColorVal) { Color(oldRankColorVal.toInt()) }

    val newRankColorVal = remember(cleanNewRank) { XPEngine.getRankColor(cleanNewRank) }
    val newRankColor = remember(newRankColorVal) { Color(newRankColorVal.toInt()) }

    val isSClass = cleanNewRank.startsWith("S", ignoreCase = true)

    val paint = remember(newRankColor, density) {
        Paint().asFrameworkPaint().apply {
            this.color = newRankColor.toArgb()
            this.maskFilter = android.graphics.BlurMaskFilter(
                24f * density.density,
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }
    }

    val paintText = remember(density) {
        android.text.TextPaint().apply {
            textSize = with(density) { 96.sp.toPx() }
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rank_tap_blink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    LaunchedEffect(Unit) {
        com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.RANK_UP)
        if (reduceMotion) {
            overlayAlphaState = 1f
            oldGlyphAlphaState = 0f
            burstProgressState = 0f
            newGlyphScaleState = 1f
            text1ProgState = "[ RANK INCREASED ]".length
            text2ProgState = "$oldRank  →  $newRank".length
            canDismissState = true
        } else {
            // Phase 1 (0–300ms): Screen goes 100% BLACK
            animate(0f, 1f, animationSpec = tween(300)) { v, _ ->
                overlayAlphaState = v
            }

            // Phase 2 (300–600ms): Old rank glyph fades in then out
            animate(0f, 1f, animationSpec = tween(150)) { v, _ ->
                oldGlyphAlphaState = v
            }
            animate(1f, 0f, animationSpec = tween(150)) { v, _ ->
                oldGlyphAlphaState = v
            }

            // Phase 3 (600–1000ms): Horizontal light burst - 4 lines extend from center
            animate(0f, 1f, animationSpec = tween(400, easing = EaseOutQuad)) { v, _ ->
                burstProgressState = v
            }

            // Phase 4 (1000–1400ms): New rank glyph SLAMS to center
            animate(0f, 1f, animationSpec = tween(400, easing = EaseOutBack)) { v, _ ->
                newGlyphScaleState = v
            }

            // Phase 5: Typewriter
            val text1 = "[ RANK INCREASED ]"
            val text2 = "$oldRank  →  $newRank"

            for (i in 1..text1.length) {
                text1ProgState = i
                delay(50)
            }
            for (i in 1..text2.length) {
                text2ProgState = i
                delay(40)
            }

            // Phase 6: Hold 2.5s
            delay(2500)
            canDismissState = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack.copy(alpha = overlayAlphaState))
            .drawBehind {
                if (newGlyphScaleState > 0f) {
                    drawRect(newRankColor.copy(alpha = 0.08f * newGlyphScaleState))
                }
            }
            .clickable(enabled = canDismissState) {
                onDismiss()
            }
            .testTag("rank_up_ceremony_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Phase 2: Old Glyph
        if (oldGlyphAlphaState > 0f) {
            Text(
                text = oldGlyph,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 80.sp,
                color = oldRankColor,
                modifier = Modifier
                    .alpha(oldGlyphAlphaState)
                    .testTag("ceremony_old_glyph")
            )
        }

        // Phase 3: Horizontal burst lines (4 lines extend from center)
        if (burstProgressState > 0f && burstProgressState < 1f) {
            val strokeW = with(density) { 2.dp.toPx() }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val maxLen = size.width * 0.7f

                val len = maxLen * burstProgressState

                // Left line
                drawLine(oldRankColor, Offset(cx, cy), Offset(cx - len, cy), strokeW)
                // Right line
                drawLine(oldRankColor, Offset(cx, cy), Offset(cx + len, cy), strokeW)
                // Top line
                drawLine(oldRankColor, Offset(cx, cy), Offset(cx, cy - len), strokeW)
                // Bottom line
                drawLine(oldRankColor, Offset(cx, cy), Offset(cx, cy + len), strokeW)
            }
        }

        // Phase 4: New Glyph with spring scale and optional Blur shadow for S-Class
        if (newGlyphScaleState > 0f) {
            val glyphModifier = if (isSClass) {
                Modifier.drawBehind {
                    drawIntoCanvas { canvas ->
                        val text = newGlyph
                        val textWidth = paintText.measureText(text)
                        val textHeight = paintText.fontMetrics.descent - paintText.fontMetrics.ascent
                        val x = (size.width - textWidth) / 2f
                        val y = (size.height + textHeight) / 2f - paintText.fontMetrics.descent
                        canvas.nativeCanvas.drawText(text, x, y, paint)
                    }
                }
            } else {
                Modifier
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = newGlyph,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 96.sp,
                    color = newRankColor,
                    modifier = glyphModifier
                        .scale(newGlyphScaleState)
                        .testTag("ceremony_new_glyph")
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Phase 5: Typewriter Rank label
                if (text1ProgState > 0) {
                    val fullText1 = "[ RANK INCREASED ]"
                    Text(
                        text = fullText1.take(text1ProgState),
                        fontFamily = JetBrainsMono,
                        fontSize = 16.sp,
                        color = newRankColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("ceremony_title")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (text2ProgState > 0) {
                    val fullText2 = "$oldRank  →  $newRank"
                    Text(
                        text = fullText2.take(text2ProgState),
                        fontFamily = Inter,
                        fontSize = 28.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("ceremony_subtitle")
                    )
                }
            }
        }

        // Phase 6: Tap to continue blinks
        if (canDismissState) {
            Text(
                text = "[ TAP TO CONTINUE ]",
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

