package com.axiom.app.presentation.ceremony

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CheckpointClearedCeremony(
    campaignName: String,
    bonusXP: Long,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var whiteFlashAlphaState by remember { mutableStateOf(1f) }
    var swordYOffsetState by remember { mutableStateOf(-200) }
    var swordScaleState by remember { mutableStateOf(0.3f) }
    var text1ProgState by remember { mutableStateOf(0) }
    var text2ProgState by remember { mutableStateOf(0) }
    var text3ProgState by remember { mutableStateOf(0) }
    var goldPulseAlphaState by remember { mutableStateOf(0f) }
    var canDismissState by remember { mutableStateOf(false) }

    val text1 = "[ CHECKPOINT CLEARED ]"
    val text2 = campaignName
    val text3 = "+$bonusXP BONUS XP"

    val infiniteTransition = rememberInfiniteTransition(label = "checkpoint_cleared_tap")
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
        // Phase 1 (0–200ms): WHITE FLASH - 100% white overlay fades to 0. SoundEngine play on flash.
        com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.BOSS_DEFEATED)
        animate(1f, 0f, animationSpec = tween(200)) { v, _ ->
            whiteFlashAlphaState = v
        }

        // Phase 2 (200–700ms): "⚔" drops from top of screen.
        // TranslateY from -200dp -> center, scale 0.3 -> 1.0 (500ms duration with EaseOutBounce)
        launch {
            animate(-200f, 0f, animationSpec = tween(500, easing = EaseOutBounce)) { v, _ ->
                swordYOffsetState = v.toInt()
            }
        }
        launch {
            animate(0.3f, 1.0f, animationSpec = tween(500, easing = EaseOutBounce)) { v, _ ->
                swordScaleState = v
            }
        }

        // Wait for Phase 2 to finish
        delay(500)

        // Phase 3: Typewriter
        for (i in 1..text1.length) {
            text1ProgState = i
            delay(40)
        }
        for (i in 1..text2.length) {
            text2ProgState = i
            delay(30)
        }
        for (i in 1..text3.length) {
            text3ProgState = i
            delay(40)
        }

        // Phase 4: Screen tint pulses LegendaryGold 10% -> 0% -> 10% -> 0% (2 pulses, 800ms)
        // Let's model 2 pulses over 800ms.
        animate(0f, 0.1f, animationSpec = tween(200)) { v, _ -> goldPulseAlphaState = v }
        animate(0.1f, 0f, animationSpec = tween(200)) { v, _ -> goldPulseAlphaState = v }
        animate(0f, 0.1f, animationSpec = tween(200)) { v, _ -> goldPulseAlphaState = v }
        animate(0.1f, 0f, animationSpec = tween(200)) { v, _ -> goldPulseAlphaState = v }

        // Phase 5: Hold 2s -> dismiss on tap
        delay(2000)
        canDismissState = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack.copy(alpha = 0.92f))
            .background(LegendaryGold.copy(alpha = goldPulseAlphaState))
            .clickable(enabled = canDismissState) {
                onDismiss()
            }
            .testTag("checkpoint_cleared_ceremony_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Drops sword glyph from top
            Text(
                text = "⚔",
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 96.sp,
                color = LegendaryGold,
                modifier = Modifier
                    .offset { IntOffset(0, swordYOffsetState.dp.roundToPx()) }
                    .scale(swordScaleState)
                    .testTag("ceremony_glyph")
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Typewriter Text 1
            if (text1ProgState > 0) {
                Text(
                    text = text1.take(text1ProgState),
                    fontFamily = JetBrainsMono,
                    fontSize = 16.sp,
                    color = LegendaryGold,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_title")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Typewriter Campaign Name
            if (text2ProgState > 0) {
                Text(
                    text = text2.take(text2ProgState).uppercase(),
                    fontFamily = Inter,
                    fontSize = 22.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_subtitle")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Typewriter Bonus XP
            if (text3ProgState > 0) {
                Text(
                    text = text3.take(text3ProgState),
                    fontFamily = JetBrainsMono,
                    fontSize = 24.sp,
                    color = SystemGreen,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("bonus_xp_text")
                )
            }
        }

        // White Flash overlay for Phase 1 impact
        if (whiteFlashAlphaState > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = whiteFlashAlphaState))
            )
        }

        // Tap prompt at bottom
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
