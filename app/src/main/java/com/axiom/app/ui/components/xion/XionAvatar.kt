package com.axiom.app.ui.components.xion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

enum class XionMood {
    IDLE,       // normal blink, calm
    HAPPY,      // arched smile eyes
    THINKING,   // side-shifted pupils, raised brow
    GLITCHED,   // jitter + RGB-split overlay
    SAD,        // drooping eyes + pixel tears
    EXCITED,    // big pupils + pulsing glow ring
    WARNING     // red pupils + furrowed brows
}

@Composable
fun XionLivingEyeAvatar(
    modifier: Modifier = Modifier,
    systemColor: Color,
    isActiveSpeaking: Boolean = false,
    textTickerKey: Int = 0,
    onClick: (() -> Unit)? = null,
    mood: XionMood = XionMood.IDLE
) {
    val colors = LocalAxiomColors.current
    var blinkProgress  by remember { mutableStateOf(0f) }
    var gazeX          by remember { mutableStateOf(0.dp) }
    var gazeY          by remember { mutableStateOf(0.dp) }
    var reactionScale  by remember { mutableStateOf(1f) }

    val tearDropOffset by animateFloatAsState(
        targetValue   = if (mood == XionMood.SAD) 1f else 0f,
        animationSpec = tween(800, easing = LinearEasing),
        label         = "tear"
    )
    val moodInfinite = rememberInfiniteTransition(label = "mood_anim")
    val warningBlink by moodInfinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(300), RepeatMode.Reverse),
        label = "warn_blink"
    )
    val excitedGlow by moodInfinite.animateFloat(
        0.85f, 1.35f,
        infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "excited_glow"
    )

    // Blink loop (IDLE only)
    LaunchedEffect(Unit) {
        while (true) {
            delay((3000..6000).random().toLong())
            if (mood == XionMood.IDLE) {
                animate(0f, 1f, animationSpec = tween(120, easing = FastOutSlowInEasing)) { v, _ -> blinkProgress = v }
                animate(1f, 0f, animationSpec = tween(150, easing = LinearOutSlowInEasing)) { v, _ -> blinkProgress = v }
            }
        }
    }

    // Gaze shift
    LaunchedEffect(Unit) {
        while (true) {
            delay((2500..5000).random().toLong())
            val tx = (-4..4).random().dp; val ty = (-3..3).random().dp
            val cx = gazeX.value; val cy = gazeY.value
            animate(0f, 1f, animationSpec = tween(400, easing = FastOutSlowInEasing)) { p, _ ->
                gazeX = (cx + (tx.value - cx) * p).dp
                gazeY = (cy + (ty.value - cy) * p).dp
            }
        }
    }

    val breathT = rememberInfiniteTransition(label = "breathing")
    val breathY  by breathT.animateFloat(-3f, 3f, infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse), label = "bY")
    val breathSc by breathT.animateFloat(0.98f, 1.02f, infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse), label = "bSc")

    LaunchedEffect(isActiveSpeaking, textTickerKey) {
        if (isActiveSpeaking) {
            repeat(4) { reactionScale = 1.06f; delay(80); reactionScale = 0.94f; delay(80) }
            reactionScale = 1f
        }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick
            ) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = gazeX * reactionScale, y = (gazeY + breathY.dp) * reactionScale)
                .scale(reactionScale * breathSc)
        ) {
            val c = center

            // Aura
            drawCircle(
                brush  = Brush.radialGradient(listOf(systemColor.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(c.x, c.y + size.height * 0.35f), radius = size.width * 0.5f),
                radius = size.width * 0.45f,
                center = Offset(c.x, c.y + size.height * 0.35f)
            )

            // Cyber ears
            val leftEar = androidx.compose.ui.graphics.Path().apply {
                moveTo(c.x - size.width * 0.3f, c.y - size.height * 0.1f)
                lineTo(c.x - size.width * 0.42f, c.y - size.height * 0.28f)
                lineTo(c.x - size.width * 0.35f, c.y - size.height * 0.05f); close()
            }
            val rightEar = androidx.compose.ui.graphics.Path().apply {
                moveTo(c.x + size.width * 0.3f, c.y - size.height * 0.1f)
                lineTo(c.x + size.width * 0.42f, c.y - size.height * 0.28f)
                lineTo(c.x + size.width * 0.35f, c.y - size.height * 0.05f); close()
            }
            drawPath(leftEar, systemColor); drawPath(rightEar, systemColor)

            // Visor
            val vW = size.width * 0.70f; val vH = size.height * 0.44f
            val vL = c.x - vW / 2f;     val vT = c.y - vH / 2f
            val cr = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx(), 14.dp.toPx())
            drawRoundRect(color = colors.voidBlack, topLeft = Offset(vL, vT), size = androidx.compose.ui.geometry.Size(vW, vH), cornerRadius = cr)
            
            val strokeW = if (isActiveSpeaking) 3.5.dp.toPx() else 2.dp.toPx()
            drawRoundRect(color = systemColor, topLeft = Offset(vL, vT), size = androidx.compose.ui.geometry.Size(vW, vH), cornerRadius = cr,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW))

            if (isActiveSpeaking) {
                val rippleCount = 3
                for (i in 0 until rippleCount) {
                    val progress = ((System.currentTimeMillis() + i * 200) % 650) / 650f
                    val rW = vW + progress * 16.dp.toPx()
                    val rH = vH + progress * 10.dp.toPx()
                    val rL = c.x - rW / 2f
                    val rT = c.y - rH / 2f
                    drawRoundRect(
                        color = systemColor.copy(alpha = 0.35f * (1f - progress)),
                        topLeft = Offset(rL, rT),
                        size = androidx.compose.ui.geometry.Size(rW, rH),
                        cornerRadius = cr,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.0.dp.toPx())
                    )
                }
            }

            // Eye positions
            val lX = c.x - vW * 0.22f; val rX = c.x + vW * 0.22f
            val eY = c.y + 2.dp.toPx(); val lr = 7.dp.toPx()
            val warningRed = PenaltyRed

            when (mood) {
                XionMood.HAPPY -> {
                    val happyArc = { cx: Float ->
                        androidx.compose.ui.graphics.Path().apply {
                            arcTo(androidx.compose.ui.geometry.Rect(cx - lr, eY - lr, cx + lr, eY + lr), 180f, 180f, true)
                        }
                    }
                    drawPath(happyArc(lX), systemColor, style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                    drawPath(happyArc(rX), systemColor, style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                    drawLine(systemColor, Offset(lX - 8.dp.toPx(), eY - lr - 6.dp.toPx()), Offset(lX + 6.dp.toPx(), eY - lr - 9.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(systemColor, Offset(rX - 6.dp.toPx(), eY - lr - 9.dp.toPx()), Offset(rX + 8.dp.toPx(), eY - lr - 6.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
                XionMood.SAD -> {
                    val sadArc = { cx: Float ->
                        androidx.compose.ui.graphics.Path().apply {
                            arcTo(androidx.compose.ui.geometry.Rect(cx - lr, eY - lr * 0.5f, cx + lr, eY + lr * 1.5f), 0f, -180f, true)
                        }
                    }
                    drawPath(sadArc(lX), systemColor, style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                    drawPath(sadArc(rX), systemColor, style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                    drawLine(systemColor, Offset(lX - 8.dp.toPx(), eY - lr - 8.dp.toPx()), Offset(lX + 6.dp.toPx(), eY - lr - 3.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(systemColor, Offset(rX - 6.dp.toPx(), eY - lr - 3.dp.toPx()), Offset(rX + 8.dp.toPx(), eY - lr - 8.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
                XionMood.THINKING -> {
                    val lookX = -2.dp.toPx()
                    drawCircle(systemColor, lr, Offset(lX + lookX, eY))
                    drawCircle(systemColor, lr, Offset(rX + lookX, eY))
                    drawCircle(Color.White.copy(alpha = 0.9f), 2.2.dp.toPx(), Offset(lX + lookX - 2.dp.toPx(), eY - 2.dp.toPx()))
                    drawCircle(Color.White.copy(alpha = 0.9f), 2.2.dp.toPx(), Offset(rX + lookX - 2.dp.toPx(), eY - 2.dp.toPx()))
                    drawLine(systemColor, Offset(lX - 8.dp.toPx(), eY - lr - 10.dp.toPx()), Offset(lX + 6.dp.toPx(), eY - lr - 10.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(systemColor, Offset(rX - 6.dp.toPx(), eY - lr - 12.dp.toPx()), Offset(rX + 8.dp.toPx(), eY - lr - 15.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
                XionMood.GLITCHED -> {
                    val j = ((-3..3).random()).dp.toPx()
                    drawRect(systemColor, Offset(lX - lr * 1.2f + j, eY - lr * 0.4f), androidx.compose.ui.geometry.Size(lr * 2.4f, lr * 0.8f))
                    drawRect(systemColor, Offset(rX - lr * 1.2f - j, eY - lr * 0.4f), androidx.compose.ui.geometry.Size(lr * 2.4f, lr * 0.8f))
                    drawRect(systemColor.copy(alpha = 0.7f), Offset(center.x - size.width * 0.25f, center.y + size.height * 0.15f), androidx.compose.ui.geometry.Size(4.dp.toPx(), 6.dp.toPx()))
                    drawRect(systemColor.copy(alpha = 0.7f), Offset(center.x - size.width * 0.25f + 6.dp.toPx(), center.y + size.height * 0.15f - 2.dp.toPx()), androidx.compose.ui.geometry.Size(4.dp.toPx(), 8.dp.toPx()))
                }
                XionMood.EXCITED -> {
                    drawCircle(systemColor, lr * 1.35f, Offset(lX, eY))
                    drawCircle(systemColor, lr * 1.35f, Offset(rX, eY))
                    drawCircle(Color.White.copy(alpha = 1f), 3.dp.toPx(), Offset(lX - 3.dp.toPx(), eY - 3.dp.toPx()))
                    drawCircle(Color.White.copy(alpha = 1f), 3.dp.toPx(), Offset(rX - 3.dp.toPx(), eY - 3.dp.toPx()))
                    drawLine(systemColor, Offset(lX - 8.dp.toPx(), eY - lr - 10.dp.toPx()), Offset(lX + 6.dp.toPx(), eY - lr - 15.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(systemColor, Offset(rX - 6.dp.toPx(), eY - lr - 15.dp.toPx()), Offset(rX + 8.dp.toPx(), eY - lr - 10.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
                XionMood.WARNING -> {
                    val eyeAlpha = 0.4f + 0.6f * warningBlink
                    drawCircle(warningRed.copy(alpha = eyeAlpha), lr * 1.1f, Offset(lX, eY))
                    drawCircle(warningRed.copy(alpha = eyeAlpha), lr * 1.1f, Offset(rX, eY))
                    drawCircle(Color.White.copy(alpha = 0.7f * eyeAlpha), 1.8.dp.toPx(), Offset(lX - 2.dp.toPx(), eY - 2.dp.toPx()))
                    drawCircle(Color.White.copy(alpha = 0.7f * eyeAlpha), 1.8.dp.toPx(), Offset(rX - 2.dp.toPx(), eY - 2.dp.toPx()))
                    drawLine(warningRed, Offset(lX - 8.dp.toPx(), eY - lr - 4.dp.toPx()), Offset(lX + 6.dp.toPx(), eY - lr - 10.dp.toPx()), 2.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(warningRed, Offset(rX - 6.dp.toPx(), eY - lr - 10.dp.toPx()), Offset(rX + 8.dp.toPx(), eY - lr - 4.dp.toPx()), 2.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
                else -> {
                    if (blinkProgress > 0.4f) {
                        drawLine(systemColor, Offset(lX - lr, eY), Offset(lX + lr, eY), 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(systemColor, Offset(rX - lr, eY), Offset(rX + lr, eY), 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    } else {
                        drawCircle(systemColor, lr, Offset(lX, eY)); drawCircle(systemColor, lr, Offset(rX, eY))
                        drawCircle(Color.White.copy(alpha = 0.95f), 2.2.dp.toPx(), Offset(lX - 2.dp.toPx(), eY - 2.dp.toPx()))
                        drawCircle(Color.White.copy(alpha = 0.95f), 2.2.dp.toPx(), Offset(rX - 2.dp.toPx(), eY - 2.dp.toPx()))
                    }
                    drawLine(systemColor, Offset(lX - 8.dp.toPx(), eY - lr - 6.dp.toPx()), Offset(lX + 6.dp.toPx(), eY - lr - 6.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(systemColor, Offset(rX - 6.dp.toPx(), eY - lr - 6.dp.toPx()), Offset(rX + 8.dp.toPx(), eY - lr - 6.dp.toPx()), 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
            }
        }

        if (mood == XionMood.SAD && tearDropOffset > 0.1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val tearX = size.width * 0.42f
                val tearY = size.height * 0.62f + size.height * 0.25f * tearDropOffset
                drawCircle(systemColor.copy(alpha = 0.7f * tearDropOffset), 3.dp.toPx(), Offset(tearX, tearY))
                drawCircle(systemColor.copy(alpha = 0.5f * tearDropOffset), 2.dp.toPx(), Offset(tearX + size.width * 0.18f, tearY - 8.dp.toPx()))
            }
        }

        if (mood == XionMood.GLITCHED) {
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { translationX = 4f; alpha = 0.35f }.background(Color.Red.copy(alpha = 0.22f), CircleShape))
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { translationX = -4f; alpha = 0.35f }.background(Color.Cyan.copy(alpha = 0.22f), CircleShape))
        }

        if (mood == XionMood.EXCITED) {
            Box(modifier = Modifier.fillMaxSize().scale(excitedGlow).border(2.dp, systemColor.copy(alpha = 0.55f), CircleShape))
        }
    }
}
