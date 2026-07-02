package com.axiom.app.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.axiom.app.R
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

enum class CardSize { FULL, COMPACT, MINI }

@Composable
fun MissionXpBadge(xpReward: Int, isLegendary: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(DimSurface)
            .border(1.dp, SystemGreen.copy(alpha = 0.35f), RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp, vertical = 1.2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (isLegendary) {
                Text(
                    text = "⬡",
                    fontFamily = JetBrainsMono,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = LegendaryGold
                )
            }
            Text(
                text = stringResource(R.string.mission_xp_reward, xpReward),
                fontFamily = JetBrainsMono,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = SystemGreen
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MissionCardBase(
    mission: Mission,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    content: @Composable BoxScope.(rColor: Color, glowEnabled: Boolean, pulseAlpha: Float, instantGateRemainingMs: Long) -> Unit
) {
    var showBurst by remember { mutableStateOf(false) }

    val currentMillis by produceState(initialValue = System.currentTimeMillis(), keys = arrayOf(mission.id)) {
        while (true) {
            delay(1000)
            value = System.currentTimeMillis()
        }
    }

    val instantGateElapsed = currentMillis - mission.createdAt
    val instantGateRemainingMs = (3600000L - instantGateElapsed).coerceAtLeast(0L)
    val isInstantGateActive = mission.isInstantGate && instantGateRemainingMs > 0L

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_press_scale"
    )

    val rColor = if (mission.isInstantGate) LegendaryGold else Color(mission.rarityColor)
    val isFlashing = showBurst
    val isLegendaryOrEpic = mission.rarity.uppercase() in listOf("LEGENDARY", "EPIC", "DEPTH", "SHIELD", "WEALTH_ENGINE") || mission.isInstantGate
    val glowEnabled = isFlashing || isLegendaryOrEpic

    // InstantGate Urgency Pulse logic
    val remainingSeconds = (instantGateRemainingMs / 1000).toInt()
    val isTimerUrgent = isInstantGateActive && remainingSeconds in 1..299
    var pulseTarget by remember { mutableStateOf(1f) }
    LaunchedEffect(isTimerUrgent) {
        if (isTimerUrgent) {
            while (true) {
                pulseTarget = if (pulseTarget == 1f) 0.4f else 1f
                delay(500)
            }
        } else {
            pulseTarget = 1f
        }
    }
    val pulseAlpha by animateFloatAsState(
        targetValue = pulseTarget,
        animationSpec = tween(500, easing = LinearEasing),
        label = "instant_gate_countdown_pulse"
    )

    HolographicCard(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .rarityGlowPulse(rColor, enabled = glowEnabled)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        accentColor = rColor,
        glowEnabled = glowEnabled
    ) {
        content(rColor, glowEnabled, pulseAlpha, instantGateRemainingMs)

        if (showBurst) {
            MissionParticleBurst(
                trigger = showBurst,
                rarityColor = rColor,
                isLegendary = mission.rarity.uppercase() in listOf("LEGENDARY", "DEPTH", "WEALTH_ENGINE"),
                onComplete = { showBurst = false },
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

enum class MissionVisualState {
    AVAILABLE,
    IN_PROGRESS,
    OVERDUE,
    COMPLETED,
    LOCKED
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MissionCard(
    mission: Mission,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardSize: CardSize = CardSize.FULL,
    onComplete: (Float?) -> Unit = {},
    onDefer: () -> Unit = {},
    onDelete: () -> Unit = {},
    onFocusClick: () -> Unit = {},
    isTimerActive: Boolean = false,
    isTimerActiveForMe: Boolean = false,
    dungeonName: String? = null,
    stageLabel: String? = null
) {
    var showContextMenu by remember { mutableStateOf(false) }

    val visualState = remember(mission.status, isTimerActiveForMe, mission.createdAt, mission.estimatedHours) {
        val deadlineMs = (mission.estimatedHours * 60 * 60 * 1000).toLong()
        val isOverdue = mission.status.uppercase() != "COMPLETED" && 
                (mission.createdAt + deadlineMs < System.currentTimeMillis())
        
        when {
            mission.status.uppercase() == "LOCKED" -> MissionVisualState.LOCKED
            mission.status.uppercase() == "COMPLETED" -> MissionVisualState.COMPLETED
            isOverdue -> MissionVisualState.OVERDUE
            isTimerActiveForMe || mission.status.uppercase() == "IN_PROGRESS" -> MissionVisualState.IN_PROGRESS
            else -> MissionVisualState.AVAILABLE
        }
    }

    var isCompleting by remember { mutableStateOf(false) }
    var showParticleBurst by remember { mutableStateOf(false) }
    var xpFloatY by remember { mutableStateOf(0f) }
    var xpAlpha by remember { mutableStateOf(1f) }

    val compScale by animateFloatAsState(
        targetValue = if (isCompleting) 0.95f else 1.0f,
        animationSpec = tween(100),
        label = "complete_scale"
    )

    val compOffsetY by animateFloatAsState(
        targetValue = if (isCompleting) -40f else 0f,
        animationSpec = tween(400, easing = EaseOutQuad),
        label = "complete_offset_y"
    )

    val compAlpha by animateFloatAsState(
        targetValue = if (isCompleting) 0f else if (visualState == MissionVisualState.COMPLETED) 0.6f else 1.0f,
        animationSpec = tween(400, easing = EaseInQuad),
        label = "complete_alpha",
        finishedListener = {
            if (it == 0f) {
                onComplete(mission.estimatedHours)
            }
        }
    )

    LaunchedEffect(isCompleting) {
        if (isCompleting) {
            showParticleBurst = true
            val anim = androidx.compose.animation.core.Animatable(0f)
            anim.animateTo(
                targetValue = -120f,
                animationSpec = tween(1000, easing = EaseOutQuad)
            ) {
                xpFloatY = value
                xpAlpha = 1f - (value / -120f)
            }
        }
    }

    Box(modifier = modifier.wrapContentSize()) {
        val animationModifier = Modifier
            .graphicsLayer {
                scaleX = compScale
                scaleY = compScale
                translationY = compOffsetY
                alpha = compAlpha
            }
            .fillMaxWidth()

        val leftBorderColor = when (visualState) {
            MissionVisualState.AVAILABLE -> SystemGreen
            MissionVisualState.IN_PROGRESS -> SystemGlint
            MissionVisualState.OVERDUE -> PenaltyRed
            MissionVisualState.COMPLETED -> SystemGreen.copy(alpha = 0.5f)
            MissionVisualState.LOCKED -> CommonGray.copy(alpha = 0.5f)
        }

        MissionCardBase(
            mission = mission,
            onClick = {
                if (visualState != MissionVisualState.LOCKED) {
                    onClick()
                }
            },
            modifier = animationModifier,
            onLongClick = { 
                if (visualState != MissionVisualState.LOCKED) {
                    showContextMenu = true 
                }
            }
        ) { rColor, glowEnabled, pulseAlpha, instantGateRemainingMs ->
        when (cardSize) {
            CardSize.FULL -> {
                val twentyFourHours = 24L * 60 * 60 * 1000
                val deadlineMs = (mission.estimatedHours * 60 * 60 * 1000).toLong()
                val deadlineRemaining = mission.createdAt + deadlineMs - System.currentTimeMillis()
                val isWithin24Hours = deadlineRemaining in 1..twentyFourHours
                val remainingHours = (deadlineRemaining / (1000f * 60 * 60)).coerceAtLeast(0f)
                val deadlineText = if (deadlineRemaining < 0) "OVERDUE" else "DUE IN ${String.format(java.util.Locale.US, "%.1f", remainingHours)}H"

                val dotAlphaAnim = if (isWithin24Hours || deadlineRemaining < 0) {
                    val infiniteTransition = rememberInfiniteTransition(label = "deadline_dot_pulse")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 0.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = EaseInOutCubic),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "deadline_dot_alpha"
                    )
                    alpha
                } else {
                    1.0f
                }

                val isLegendary = mission.rarity.uppercase() in listOf("LEGENDARY", "DEPTH", "WEALTH_ENGINE")
                val letterSpacingAnim = if (isLegendary) {
                    val infiniteTransition = rememberInfiniteTransition(label = "legendary_shimmer")
                    val spacing by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 0.5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = EaseInOutCubic),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "letter_spacing"
                    )
                    spacing.sp
                } else {
                    0.sp
                }

                val sweepTransition = rememberInfiniteTransition(label = "gold_diagonal_sweep")
                val sweepProgress by sweepTransition.animateFloat(
                    initialValue = -1f,
                    targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "sweep_progress"
                )

                val legendaryModifier = if (isLegendary) {
                    Modifier.drawBehind {
                        val w = size.width
                        val h = size.height
                        val xOffset = w * sweepProgress
                        val goldBrush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                LegendaryGold.copy(alpha = 0.03f),
                                LegendaryGold.copy(alpha = 0.08f),
                                LegendaryGold.copy(alpha = 0.03f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(xOffset - w * 0.3f, 0f),
                            end = androidx.compose.ui.geometry.Offset(xOffset + w * 0.3f, h)
                        )
                        drawRect(brush = goldBrush)
                    }
                } else {
                    Modifier
                }

                val statusColor = when (mission.status.uppercase()) {
                    "COMPLETED" -> SystemGreen
                    "DEFERRED" -> TextDim
                    else -> RareBlue
                }

                val density = LocalDensity.current
                val HexagonalShape = remember(density) {
                    GenericShape { size, _ ->
                        val cut = with(density) { 6.dp.toPx() }
                        moveTo(cut, 0f)
                        lineTo(size.width - cut, 0f)
                        lineTo(size.width, cut)
                        lineTo(size.width, size.height - cut)
                        lineTo(size.width - cut, size.height)
                        lineTo(cut, size.height)
                        lineTo(0f, size.height - cut)
                        lineTo(0f, cut)
                        close()
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(leftBorderColor.copy(alpha = 0.12f))
                            .drawBehind {
                                drawLine(
                                    color = leftBorderColor.copy(alpha = 0.4f),
                                    start = androidx.compose.ui.geometry.Offset(0f, size.height),
                                    end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RarityBadge(rarity = mission.rarity, fontSize = 8.sp)

                                if (mission.isInstantGate) {
                                    val totalSecs = (instantGateRemainingMs / 1000).toInt()
                                    val mins = totalSecs / 60
                                    val secs = totalSecs % 60
                                    val timerText = if (instantGateRemainingMs > 0L) {
                                        String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
                                    } else {
                                        "EXPIRED"
                                    }
                                    val badgeColor = if (instantGateRemainingMs > 0L) LegendaryGold else TextDim
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(badgeColor.copy(alpha = 0.12f))
                                            .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = "⚡ INSTANT",
                                                fontFamily = JetBrainsMono,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeColor
                                            )
                                            if (instantGateRemainingMs > 0L) {
                                                Text(
                                                    text = timerText,
                                                    fontFamily = JetBrainsMono,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SystemGreen,
                                                    modifier = Modifier.graphicsLayer { alpha = pulseAlpha }
                                                )
                                            }
                                        }
                                    }
                                }

                                MissionXpBadge(xpReward = mission.xpReward, isLegendary = isLegendary)

                                val stateBadgeText = when (visualState) {
                                    MissionVisualState.LOCKED -> "LOCKED 🔒"
                                    MissionVisualState.COMPLETED -> "COMPLETED ✓"
                                    MissionVisualState.OVERDUE -> "OVERDUE ⚠️"
                                    MissionVisualState.IN_PROGRESS -> "IN FOCUS ⚡"
                                    else -> null
                                }
                                val stateBadgeColor = when (visualState) {
                                    MissionVisualState.LOCKED -> TextDim
                                    MissionVisualState.COMPLETED -> SystemGreen
                                    MissionVisualState.OVERDUE -> PenaltyRed
                                    MissionVisualState.IN_PROGRESS -> SystemGlint
                                    else -> null
                                }

                                if (stateBadgeText != null && stateBadgeColor != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(stateBadgeColor.copy(alpha = 0.15f))
                                            .border(1.dp, stateBadgeColor.copy(alpha = 0.35f), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.2.dp)
                                    ) {
                                        Text(
                                            text = stateBadgeText,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 9.sp,
                                            color = stateBadgeColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(DimSurface)
                                        .border(1.dp, BorderFaint, RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = mission.skillName.uppercase(),
                                        fontFamily = JetBrainsMono,
                                        fontSize = 9.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (visualState == MissionVisualState.AVAILABLE) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        if (isWithin24Hours || deadlineRemaining < 0) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .graphicsLayer { alpha = dotAlphaAnim }
                                                    .background(PenaltyRed, androidx.compose.foundation.shape.CircleShape)
                                            )
                                        }
                                        Text(
                                            text = deadlineText,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 9.sp,
                                            color = if (deadlineRemaining < 0) PenaltyRed else TextDim,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val indicatorTransition = rememberInfiniteTransition(label = "indicator_energy")
                        val energyProgress by indicatorTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "energy_pulse_y_ratio"
                        )

                        androidx.compose.foundation.Canvas(
                            modifier = Modifier
                                .width(if (visualState == MissionVisualState.IN_PROGRESS) 5.dp else 4.dp)
                                .fillMaxHeight()
                        ) {
                            val h = size.height
                            val w = size.width

                            if (visualState != MissionVisualState.IN_PROGRESS) {
                                drawRect(
                                    color = leftBorderColor,
                                    size = this.size
                                )
                            } else {
                                drawRect(
                                    color = leftBorderColor.copy(alpha = 0.2f),
                                    size = this.size
                                )

                                val width2 = 3.5.dp.toPx()
                                drawRect(
                                    color = leftBorderColor.copy(alpha = 0.5f),
                                    topLeft = androidx.compose.ui.geometry.Offset((w - width2) / 2f, 0f),
                                    size = androidx.compose.ui.geometry.Size(width2, h)
                                )

                                val width3 = 2.5.dp.toPx()
                                drawRect(
                                    color = leftBorderColor,
                                    topLeft = androidx.compose.ui.geometry.Offset((w - width3) / 2f, 0f),
                                    size = androidx.compose.ui.geometry.Size(width3, h)
                                )

                                val pulseH = 12.dp.toPx()
                                val maxOffset = (h - pulseH).coerceAtLeast(0f)
                                val animatedY = maxOffset * energyProgress

                                drawRect(
                                    color = leftBorderColor.copy(alpha = 0.8f),
                                    topLeft = androidx.compose.ui.geometry.Offset(0f, animatedY),
                                    size = androidx.compose.ui.geometry.Size(w, pulseH)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = mission.title,
                                    fontFamily = Inter,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = if (visualState == MissionVisualState.LOCKED) TextDim else if (visualState == MissionVisualState.COMPLETED) TextSecondary else TextPrimary,
                                    textDecoration = if (visualState == MissionVisualState.COMPLETED) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                    letterSpacing = letterSpacingAnim,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(legendaryModifier)
                                )

                                val scanLineProgress = remember { Animatable(0f) }
                                LaunchedEffect(mission.id) {
                                    scanLineProgress.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(600)
                                    )
                                }

                                androidx.compose.foundation.Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                ) {
                                    drawLine(
                                        color = leftBorderColor.copy(alpha = 0.6f),
                                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(this.size.width * scanLineProgress.value, 0f),
                                        strokeWidth = 2.dp.toPx()
                                    )
                                }
                            }

                            if (mission.description.isNotBlank()) {
                                Text(
                                    text = mission.description,
                                    fontFamily = Inter,
                                    fontSize = 12.sp,
                                    color = if (visualState == MissionVisualState.LOCKED || visualState == MissionVisualState.COMPLETED) TextDim.copy(alpha = 0.6f) else TextDim,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(DimSurface)
                                            .border(1.dp, rColor.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        androidx.compose.foundation.Canvas(modifier = Modifier.size(4.dp)) {
                                            drawCircle(color = rColor)
                                        }
                                        Text(
                                            text = String.format(java.util.Locale.US, "%.1f", mission.powerScore),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = rColor
                                        )
                                    }

                                    Text(
                                        text = stringResource(R.string.mission_estimated_hours, mission.estimatedHours.toInt()),
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        color = TextDim
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (visualState == MissionVisualState.IN_PROGRESS) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(12.dp),
                                                strokeWidth = 1.5.dp,
                                                color = SystemGlint
                                            )
                                        }
                                        Text(
                                            text = "◉ ${mission.track.uppercase()}",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            color = if (visualState == MissionVisualState.IN_PROGRESS) SystemGlint else TextSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (!dungeonName.isNullOrBlank()) {
                                        Text(
                                            text = "⚔ RAID: ${dungeonName.uppercase()}",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            color = RareBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            if (visualState != MissionVisualState.COMPLETED && visualState != MissionVisualState.LOCKED) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = onFocusClick,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isTimerActiveForMe) SystemGreen.copy(alpha = 0.2f) else SystemGreen,
                                            contentColor = if (isTimerActiveForMe) SystemGreen else VoidBlack
                                        ),
                                        shape = HexagonalShape,
                                        modifier = Modifier
                                            .weight(1.2f)
                                            .height(36.dp),
                                        enabled = !isTimerActive || isTimerActiveForMe
                                    ) {
                                        val isFa = java.util.Locale.getDefault().language == "fa"
                                        val leftButtonText = if (isFa) {
                                            if (isTimerActiveForMe) "⚡ در حال تمرکز" else "▶ تمرکز"
                                        } else {
                                            if (isTimerActiveForMe) "⚡ FOCUSING" else "▶ FOCUS"
                                        }
                                        Text(
                                            text = leftButtonText,
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            isCompleting = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SystemGreen,
                                            contentColor = VoidBlack
                                        ),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier
                                            .size(width = 44.dp, height = 36.dp)
                                            .testTag("btn_complete_mission_${mission.id}"),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = "✓",
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = onDefer,
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = TextSecondary
                                        ),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, BorderFaint),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                    ) {
                                        val isFa = java.util.Locale.getDefault().language == "fa"
                                        val rightButtonText = if (mission.status.uppercase() == "ACTIVE") {
                                            stringResource(R.string.mission_defer)
                                        } else {
                                            if (isFa) "ادامه" else "RESUME"
                                        }
                                        Text(
                                            text = rightButtonText,
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CardSize.COMPACT -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(rColor)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = mission.title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                fontFamily = Inter,
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            val subtitleText = buildString {
                                if (mission.isInstantGate) {
                                    append("⚡ INSTANT GATE | ")
                                }
                                append(mission.track.uppercase())
                                if (!dungeonName.isNullOrBlank()) {
                                    append(" | ⚔ RAID: ${dungeonName.uppercase()}")
                                    if (!stageLabel.isNullOrBlank()) {
                                        append(" | STAGE: ${stageLabel.uppercase()}")
                                    }
                                }
                            }
                            Text(
                                text = subtitleText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMono,
                                color = if (mission.isInstantGate) LegendaryGold else TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(DimSurface)
                                    .border(1.dp, rColor.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PS ${String.format(java.util.Locale.US, "%.1f", mission.powerScore)}",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rColor
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(DimSurface)
                                    .border(1.dp, BorderFaint, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = mission.skillName.uppercase(),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
            CardSize.MINI -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 2.dp, height = 16.dp)
                                .background(rColor)
                        )
                        Text(
                            text = mission.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = Inter,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(DimSurface)
                            .border(0.5.dp, rColor.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "PS ${String.format(java.util.Locale.US, "%.1f", mission.powerScore)}",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = rColor
                        )
                    }
                }
            }
        }
    }

        if (showParticleBurst) {
            MissionParticleBurst(
                trigger = showParticleBurst,
                rarityColor = leftBorderColor,
                isLegendary = mission.rarity.uppercase() in listOf("LEGENDARY", "DEPTH", "WEALTH_ENGINE"),
                onComplete = { showParticleBurst = false },
                modifier = Modifier.align(Alignment.Center)
            )
            
            Text(
                text = "+${mission.xpReward} XP",
                color = LegendaryGold,
                fontFamily = JetBrainsMono,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationY = xpFloatY
                        alpha = xpAlpha
                    }
            )
        }
    }

    DropdownMenu(
        expanded = showContextMenu,
        onDismissRequest = { showContextMenu = false },
        modifier = Modifier.background(ShadowSurface)
    ) {
        val isFa = java.util.Locale.getDefault().language == "fa"
        if (mission.status.uppercase() != "COMPLETED") {
            val focusLabel = if (isFa) "شروع پروتکل تمرکز" else "Start Focus Protocol"
            DropdownMenuItem(
                text = { Text(focusLabel, color = SystemGreen, fontFamily = JetBrainsMono) },
                onClick = {
                    showContextMenu = false
                    onFocusClick()
                },
                enabled = !isTimerActive || isTimerActiveForMe
            )
            val deferRestoreLabel = if (mission.status.uppercase() == "ACTIVE") {
                if (isFa) "به تعویق" else "Defer"
            } else {
                if (isFa) "فعال‌سازی" else "Activate"
            }
            DropdownMenuItem(
                text = { Text(deferRestoreLabel, color = TextSecondary, fontFamily = JetBrainsMono) },
                onClick = {
                    showContextMenu = false
                    onDefer()
                }
            )
        }
        DropdownMenuItem(
            text = { Text(if (isFa) "حذف" else "Delete", color = PenaltyRed, fontFamily = JetBrainsMono) },
            onClick = {
                showContextMenu = false
                onDelete()
            }
        )
    }
}

@Preview
@Composable
fun MissionCardFullPreview() {
    AwakenTheme {
        MissionCard(
            mission = Mission(
                id = "test_id",
                title = "Defeat the Hobgoblin Shaman",
                track = "strength",
                rarity = "EPIC",
                skillId = "strength_shaman",
                skillName = "Rage Strike",
                xpReward = 120,
                powerScore = 45.5f,
                status = "ACTIVE",
                dungeonId = null,
                estimatedHours = 2f,
                actualHours = null,
                createdAt = System.currentTimeMillis() - 120000L,
                completedAt = null,
                rarityColor = 0xFFFF007FL,
                isInstantGate = false,
                description = "Locate and eliminate the shaman leading the raid squad before reinforcements arrive."
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
fun MissionCardCompactPreview() {
    AwakenTheme {
        MissionCard(
            mission = Mission(
                id = "test_id",
                title = "Defeat the Hobgoblin Shaman",
                track = "strength",
                rarity = "EPIC",
                skillId = "strength_shaman",
                skillName = "Rage Strike",
                xpReward = 120,
                powerScore = 45.5f,
                status = "ACTIVE",
                dungeonId = null,
                estimatedHours = 2f,
                actualHours = null,
                createdAt = System.currentTimeMillis() - 120000L,
                completedAt = null,
                rarityColor = 0xFFFF007FL,
                isInstantGate = false,
                description = "Locate and eliminate the shaman leading the raid squad."
            ),
            onClick = {},
            cardSize = CardSize.COMPACT
        )
    }
}

@Preview
@Composable
fun MissionCardMiniPreview() {
    AwakenTheme {
        MissionCard(
            mission = Mission(
                id = "test_id",
                title = "Defeat the Hobgoblin Shaman",
                track = "strength",
                rarity = "EPIC",
                skillId = "strength_shaman",
                skillName = "Rage Strike",
                xpReward = 120,
                powerScore = 45.5f,
                status = "ACTIVE",
                dungeonId = null,
                estimatedHours = 2f,
                actualHours = null,
                createdAt = System.currentTimeMillis() - 120000L,
                completedAt = null,
                rarityColor = 0xFFFF007FL,
                isInstantGate = false,
                description = "Locate and eliminate the shaman leading the raid squad."
            ),
            onClick = {},
            cardSize = CardSize.MINI
        )
    }
}
