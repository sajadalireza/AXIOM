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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun StreakShieldUsedOverlay(
    savedStreak: Int,
    remainingShields: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    var overlayAlphaState by remember { mutableStateOf(0f) }
    var numberScaleState by remember { mutableStateOf(0f) }
    var text1ProgState by remember { mutableStateOf(0) }
    var text2ProgState by remember { mutableStateOf(0) }
    var text3ProgState by remember { mutableStateOf(0) }
    var canDismissState by remember { mutableStateOf(false) }

    val text1 = "[ SHIELD DEPLOYED ]"
    val text2 = "Streak Shield activated."
    val text3 = "$savedStreak-day streak saved. $remainingShields shield(s) left."

    val infiniteTransition = rememberInfiniteTransition(label = "streak_shield_blink")
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
        // Background VoidBlack 90% overlay
        animate(0f, 0.90f, animationSpec = tween(300)) { v, _ ->
            overlayAlphaState = v
        }

        // Giant shield scale
        animate(
            0f, 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) { v, _ ->
            numberScaleState = v
        }

        // Phase 2: Typewriter lines
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

        // Hold 2s → dismiss on tap
        delay(2000)
        canDismissState = true
    }

    val mappedScale = numberScaleState * if (numberScaleState < 0.9f) 1.15f else (1.15f - (numberScaleState - 0.9f) * 1.5f).coerceAtLeast(1.0f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack.copy(alpha = overlayAlphaState))
            .clickable(enabled = canDismissState) {
                onDismiss()
            }
            .testTag("streak_shield_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Icon
            Text(
                text = "⬡",
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 96.sp,
                color = colors.rareBlue,
                modifier = Modifier
                    .scale(mappedScale)
                    .testTag("ceremony_glyph")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Typewriter Label
            if (text1ProgState > 0) {
                Text(
                    text = text1.take(text1ProgState),
                    fontFamily = JetBrainsMono,
                    fontSize = 15.sp,
                    color = colors.rareBlue,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_title")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Message
            if (text2ProgState > 0) {
                Text(
                    text = text2.take(text2ProgState),
                    fontFamily = Inter,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("ceremony_subtitle")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Remaining info
            if (text3ProgState > 0) {
                Text(
                    text = text3.take(text3ProgState),
                    fontFamily = JetBrainsMono,
                    fontSize = 15.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("remaining_shields_text")
                )
            }
        }

        // Tap to continue
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
