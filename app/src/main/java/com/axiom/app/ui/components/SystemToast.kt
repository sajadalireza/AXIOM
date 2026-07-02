package com.axiom.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.axiom.app.core.sound.AwakenSound
import com.axiom.app.core.sound.SoundEngine
import com.axiom.app.ui.theme.BorderFaint
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.LegendaryGold
import com.axiom.app.ui.theme.ShadowSurface
import com.axiom.app.ui.theme.ShapeMedium
import com.axiom.app.ui.theme.SystemGreen
import com.axiom.app.ui.theme.Spacing
import com.axiom.app.ui.theme.SystemMsg
import com.axiom.app.ui.theme.TextPrimary
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════
// SYSTEM TOAST — top-of-screen notification banner
//
// Appears at the very top of the content area (above all screens,
// below status bar). Slides DOWN 400 ms + fades in. Auto-dismisses
// after 2.5 s. Plays SYSTEM_ALERT sound on every appearance.
//
// Usage in MainScreen / CeremonyHost level:
//
//   var toastMessage by remember { mutableStateOf<String?>(null) }
//   var toastIsGold   by remember { mutableStateOf(false) }
//
//   SystemToast(
//       message   = toastMessage,
//       isGold    = toastIsGold,
//       onDismiss = { toastMessage = null }
//   )
//
// Trigger from ViewModel via a StateFlow<ToastEvent?>.
// ═══════════════════════════════════════════════════════════════

private const val SLIDE_IN_MS  = 400
private const val SLIDE_OUT_MS = 300
private const val AUTO_DISMISS_MS = 2500L

/**
 * Top-of-screen system notification banner.
 *
 * @param message   Text to display after the "[ SYSTEM ]" prefix.
 *                  When null, the toast is hidden (exit animation plays).
 * @param isGold    If true, renders with [LegendaryGold] border/text
 *                  (legendary/rank events). Defaults to [SystemGreen]
 *                  (XP/progress events).
 * @param onDismiss Called after the 2.5-second auto-dismiss delay,
 *                  or whenever the host wants to hide the toast early.
 * @param modifier  Applied to the outermost AnimatedVisibility container.
 */
@Composable
fun SystemToast(
    message: String?,
    isGold: Boolean = false,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor: Color = if (isGold) LegendaryGold else SystemGreen

    // ─── Sound + auto-dismiss ────────────────────────────────────
    LaunchedEffect(message) {
        if (message != null) {
            SoundEngine.play(AwakenSound.SYSTEM_ALERT)
            delay(AUTO_DISMISS_MS)
            onDismiss()
        }
    }

    // ─── Slide down from above the screen ───────────────────────
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = SLIDE_IN_MS,
                easing         = LinearOutSlowInEasing   // decelerate — ease-out
            ),
            initialOffsetY = { fullHeight -> -fullHeight }
        ) + fadeIn(
            animationSpec = tween(durationMillis = SLIDE_IN_MS)
        ),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = SLIDE_OUT_MS),
            targetOffsetY = { fullHeight -> -fullHeight }
        ) + fadeOut(
            animationSpec = tween(durationMillis = SLIDE_OUT_MS)
        ),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .zIndex(10f)            // render above screen content, below ceremonies
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = Spacing.m, vertical = Spacing.s)
                .background(color = ShadowSurface, shape = ShapeMedium)
                .border(width = 1.dp, color = accentColor, shape = ShapeMedium),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Spacing.m,
                    vertical   = 12.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "[ SYSTEM ]" prefix — always in accent color
                Text(
                    text  = "[ SYSTEM ]",
                    style = SystemMsg.copy(
                        color      = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 12.sp
                    )
                )

                // Message body — same mono font, slightly lighter
                Text(
                    text     = "  ${message ?: ""}",
                    style    = SystemMsg.copy(
                        color    = TextPrimary,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
