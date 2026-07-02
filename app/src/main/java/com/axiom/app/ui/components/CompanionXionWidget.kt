package com.axiom.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.ui.XionViewModel
import com.axiom.app.ui.components.xion.XionDialogContent
import com.axiom.app.ui.components.xion.XionLivingEyeAvatar
import com.axiom.app.ui.components.xion.XionMood
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CompanionXionWidget(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSplashMode: Boolean = false,
    splashScale: Float = 1f,
    splashMood: XionMood = XionMood.IDLE,
    viewModel: XionViewModel = hiltViewModel()
) {
    val colors  = LocalAxiomColors.current
    val haptic  = LocalHapticFeedback.current
    var showCompanionDialog by remember { mutableStateOf(false) }
    var activeXionCustomColor by remember { mutableStateOf<Color?>(null) }
    val systemColor = activeXionCustomColor ?: colors.systemGreen

    val configuration  = androidx.compose.ui.platform.LocalConfiguration.current
    val density        = androidx.compose.ui.platform.LocalDensity.current
    val screenWidthPx  = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    // Floating orb pulse
    val infiniteT = rememberInfiniteTransition(label = "xion_pulse")
    val pulseScale by infiniteT.animateFloat(
        0.95f, 1.05f,
        infiniteRepeatable(tween(1400, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "xion_scale"
    )
    val translationY by infiniteT.animateFloat(
        -4f, 4f,
        infiniteRepeatable(tween(1200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "xion_float"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue  = if (isPressed) 0.85f else 1f,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessMedium),
        label = "xion_click_scale"
    )
    LaunchedEffect(isPressed) { if (isPressed && !isSplashMode) haptic.performHapticFeedback(HapticFeedbackType.LongPress) }

    var showSpeechBubble   by remember { mutableStateOf(false) }
    var bubbleFullText     by remember { mutableStateOf("") }
    var bubbleDisplayedText by remember { mutableStateOf("") }

    val snapshot       by viewModel.snapshot.collectAsStateWithLifecycle()
    val contextBubble  by viewModel.contextualBubble.collectAsStateWithLifecycle()
    val activeEvent    by viewModel.activeEvent.collectAsStateWithLifecycle()
    var isShowingEventBubble by remember { mutableStateOf(false) }

    // Derive mood for floating orb from snapshot
    val orbMood = remember(snapshot) {
        when {
            snapshot.inactiveDays >= 3                                  -> XionMood.WARNING
            snapshot.streakDays >= 30                                   -> XionMood.EXCITED
            snapshot.completedTodayCount > 0                            -> XionMood.HAPPY
            snapshot.streakDays == 0 && snapshot.hunterName != "HUNTER" -> XionMood.SAD
            else                                                        -> XionMood.IDLE
        }
    }

    // Idle bubble loop
    LaunchedEffect(isSplashMode) {
        if (isSplashMode) return@LaunchedEffect
        viewModel.refresh()
        delay(3000)
        while (true) {
            if (!isShowingEventBubble) {
                bubbleFullText      = contextBubble
                showSpeechBubble    = true
                bubbleDisplayedText = ""
                for (char in bubbleFullText) { bubbleDisplayedText += char; delay(28) }
                delay(6000)
                showSpeechBubble = false
                delay(12000)
                viewModel.refresh()
            } else {
                delay(500)
            }
        }
    }

    // Event bubble
    LaunchedEffect(activeEvent, isSplashMode) {
        if (isSplashMode) return@LaunchedEffect
        val event = activeEvent ?: return@LaunchedEffect
        isShowingEventBubble = true
        bubbleFullText       = viewModel.bubbleForEvent(event)
        showSpeechBubble     = true
        bubbleDisplayedText  = ""
        for (char in bubbleFullText) { bubbleDisplayedText += char; delay(22) }
        delay(4000)
        showSpeechBubble     = false
        delay(300)
        isShowingEventBubble = false
    }

    val dragModifier = if (isSplashMode) {
        Modifier
    } else {
        Modifier
            .offset { androidx.compose.ui.unit.IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
            .pointerInput(screenWidthPx, screenHeightPx) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val minX = -screenWidthPx + with(density) { 210.dp.toPx() }
                    val minY = -screenHeightPx + with(density) { 190.dp.toPx() }
                    dragOffset = Offset(
                        (dragOffset.x + dragAmount.x).coerceIn(minX, 0f),
                        (dragOffset.y + dragAmount.y).coerceIn(minY, 0f)
                    )
                }
            }
    }

    Column(
        modifier = modifier.then(dragModifier),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Speech bubble
        AnimatedVisibility(
            visible = showSpeechBubble && !isSplashMode,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit  = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .widthIn(max = 190.dp)
                    .background(colors.voidBlack.copy(alpha = 0.95f),
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp))
                    .border(1.dp, systemColor,
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(bubbleDisplayedText, color = colors.textSecondary,
                    fontFamily = JetBrainsMono, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }

        // Floating orb
        Box(
            modifier = Modifier
                .then(if (isSplashMode) Modifier else Modifier.offset(y = translationY.dp))
                .graphicsLayer {
                    val finalScale = if (isSplashMode) splashScale else animatedScale
                    scaleX = finalScale
                    scaleY = finalScale
                }
        ) {
            if (!isSplashMode) {
                Box(
                    modifier = Modifier.size(62.dp).scale(pulseScale)
                        .background(systemColor.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, systemColor.copy(alpha = 0.35f), CircleShape)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .border(if (isSplashMode) 3.dp else 2.dp, systemColor, CircleShape)
                    .then(
                        if (isSplashMode) Modifier else Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            showCompanionDialog = true
                        }
                    )
                    .testTag(if (isSplashMode) "splash_xion_logo_visor" else "floating_companion_xion")
            ) {
                XionLivingEyeAvatar(
                    modifier    = Modifier.fillMaxSize(),
                    systemColor = systemColor,
                    mood        = if (isSplashMode) splashMood else orbMood
                )
                if (!isSplashMode) {
                    Box(
                        modifier = Modifier.size(8.dp).align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .background(systemColor, CircleShape)
                            .border(1.dp, colors.voidBlack, CircleShape)
                    )
                }
            }
        }
    }

    if (showCompanionDialog) {
        Dialog(onDismissRequest = { showCompanionDialog = false }) {
            XionDialogContent(
                onDismiss         = { showCompanionDialog = false },
                onNavigate        = { route -> showCompanionDialog = false; onNavigate(route) },
                activeCustomColor = activeXionCustomColor,
                onColorChange     = { activeXionCustomColor = it },
                viewModel         = viewModel
            )
        }
    }
}
