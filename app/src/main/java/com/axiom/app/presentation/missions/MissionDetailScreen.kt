package com.axiom.app.presentation.missions

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.MissionsUiState
import com.axiom.app.ui.MissionsViewModel
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.components.AwakenTopBar
import com.axiom.app.ui.components.RarityBadge
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.components.CyberParticleBurst
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailScreen(
    missionId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MissionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.missionsState.collectAsStateWithLifecycle()
    val mission = remember(uiState, missionId) {
        if (uiState is MissionsUiState.Success) {
            val s = uiState as MissionsUiState.Success
            val allMissions = s.activeMissions + s.completedMissions + s.pendingMissions
            allMissions.find { it.id == missionId }
        } else {
            null
        }
    }

    val colors = LocalAxiomColors.current

    Scaffold(
        topBar = {
            AwakenTopBar(
                title = mission?.title ?: stringResource(R.string.missions_protocol_title),
                onBackClick = onBack
            )
        },
        containerColor = colors.voidBlack,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.voidBlack),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is MissionsUiState.Loading -> {
                    CircularProgressIndicator(color = SystemGreen)
                }
                is MissionsUiState.Error -> {
                    Text(
                        text = state.message, 
                        color = PenaltyRed, 
                        fontFamily = JetBrainsMono,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is MissionsUiState.Success -> {
                    if (mission == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.missions_not_found), 
                                color = PenaltyRed, 
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = ShadowSurface)
                            ) {
                                Text("RETURN TO HOME", color = TextPrimary, fontFamily = JetBrainsMono)
                            }
                        }
                    } else {
                        MissionDetailContent(
                            mission = mission,
                            viewModel = viewModel,
                            onComplete = {
                                onBack()
                            },
                            onDelete = {
                                viewModel.deleteMission(mission.id)
                                onBack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MissionDetailContent(
    mission: Mission,
    viewModel: MissionsViewModel,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isCompleted = mission.status.uppercase() == "COMPLETED"
    
    val colors = LocalAxiomColors.current
    val rarityColor = if (mission.isInstantGate) LegendaryGold else (rarityColorMap[mission.rarity.uppercase()] ?: CommonGray)

    var triggerParticleBurst by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    // Bouncy animation state for Complete button
    val completeInteractionSource = remember { MutableInteractionSource() }
    val isCompletePressed by completeInteractionSource.collectIsPressedAsState()
    val completeScale by animateFloatAsState(
        targetValue = if (isCompletePressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "complete_bounce_scale"
    )

    // Bouncy animation state for Delete button
    val deleteInteractionSource = remember { MutableInteractionSource() }
    val isDeletePressed by deleteInteractionSource.collectIsPressedAsState()
    val deleteScale by animateFloatAsState(
        targetValue = if (isDeletePressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "delete_bounce_scale"
    )

    LaunchedEffect(isCompletePressed) {
        if (isCompletePressed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(isDeletePressed) {
        if (isDeletePressed) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val currentMillis by produceState(initialValue = System.currentTimeMillis(), keys = arrayOf(mission.id)) {
        while (true) {
            delay(1000)
            value = System.currentTimeMillis()
        }
    }
    val instantGateElapsed = currentMillis - mission.createdAt
    val instantGateRemainingMs = (3600000L - instantGateElapsed).coerceAtLeast(0L)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // 1. Holographic Card for Title & Core Info
        HolographicCard(
            accentColor = rarityColor,
            glowEnabled = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ShadowSurface)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RarityBadge(rarity = mission.rarity)
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
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeColor.copy(alpha = 0.12f))
                                    .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "⚡ INSTANT GATE",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor
                                    )
                                    if (instantGateRemainingMs > 0L) {
                                        Text(
                                            text = timerText,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SystemGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isCompleted) SystemGreen.copy(alpha = 0.15f) else Color(0x33505068))
                            .border(1.dp, if (isCompleted) SystemGreen else TextDim, RoundedCornerShape(4.dp))
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (isCompleted) "RESOLVED" else "ACTIVE",
                            color = if (isCompleted) SystemGreen else TextSecondary,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = mission.title,
                    fontFamily = Inter,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = TextPrimary
                )

                if (mission.description.isNotBlank()) {
                    Text(
                        text = mission.description,
                        fontFamily = Inter,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                HorizontalDivider(color = BorderFaint, thickness = 1.dp)

                // Detail Specs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TARGET SKILL", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                        Text(mission.skillName, fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("XP REWARD", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            "+${mission.xpReward} XP", 
                            fontFamily = JetBrainsMono, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 16.sp, 
                            color = if (mission.rarity.uppercase() == "LEGENDARY") LegendaryGold else SystemGreen
                        )
                    }
                }
            }
        }

        // 2. Metrics & Parameters (Power Score Details)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ShadowSurface),
            border = BorderStroke(1.dp, BorderFaint)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.missions_metric_analytics),
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                HorizontalDivider(color = BorderFaint, thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("POWER SCORE", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = String.format(Locale.US, "%.2f ★", mission.powerScore),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = rarityColor
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("ESTIMATED HOURS", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = String.format(Locale.US, "%.1f Hrs", mission.estimatedHours),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }
                }

                if (isCompleted && mission.actualHours != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ACTUAL HOURS INVESTED", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = String.format(Locale.US, "%.1f Hrs", mission.actualHours),
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = SystemGreen
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("RESOLUTION RATIO", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                            val ratio = if (mission.actualHours > 0) (mission.estimatedHours / mission.actualHours) else 1.0f
                            val ratioColor = if (ratio >= 1.0f) SystemGreen else PenaltyRed
                            Text(
                                text = String.format(Locale.US, "%.1fx Efficiency", ratio),
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = ratioColor
                            )
                        }
                    }
                }

                if (mission.createdAt > 0) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("REGISTERED AT", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = sdf.format(Date(mission.createdAt)),
                                fontFamily = JetBrainsMono,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        if (mission.completedAt != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("COMPLETED AT", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = sdf.format(Date(mission.completedAt)),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 13.sp,
                                    color = SystemGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Execution Protocols (Complete Panel)
        if (!isCompleted) {
            val focusManager = viewModel.focusProtocolManager
            val isTimerActive by focusManager.isTimerActive.collectAsStateWithLifecycle()
            val activeFocusTitle by focusManager.activeFocusTitle.collectAsStateWithLifecycle()
            val activeFocusMission by focusManager.activeFocusMission.collectAsStateWithLifecycle()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.focus_enforcement_title),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = colors.systemGreen,
                        letterSpacing = 1.sp
                    )

                    HorizontalDivider(color = BorderFaint, thickness = 1.dp)

                    if (isTimerActive) {
                        if (activeFocusMission?.id == mission.id) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.focus_neural_active),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.systemGreen,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.focus_stay_warning),
                                    fontFamily = Inter,
                                    fontSize = 10.sp,
                                    color = colors.textDim,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.focus_another_locked),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PenaltyRed,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.focus_active_target, activeFocusTitle ?: ""),
                                    fontFamily = Inter,
                                    fontSize = 10.sp,
                                    color = colors.textDim,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        var selectedDurationIndex by remember { mutableStateOf(2) } // Default to 25 Mins
                        val durations = listOf(1, 10, 25, 50)

                        Text(
                            text = stringResource(R.string.focus_select_duration),
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            color = colors.textDim
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            durations.forEachIndexed { index, duration ->
                                val isSelected = selectedDurationIndex == index
                                val isDemo = duration == 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) colors.systemGreen else BorderFaint,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .background(
                                            if (isSelected) colors.systemGreen.copy(alpha = 0.12f) else colors.voidBlack
                                        )
                                        .clickable { selectedDurationIndex = index }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isDemo) "1 MIN" else "${duration}M",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) colors.systemGreen else colors.textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val duration = durations[selectedDurationIndex]
                                focusManager.startFocusProtocol(mission, duration)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_start_focus_protocol")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Start Focus", tint = VoidBlack)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.focus_start_btn),
                                    fontFamily = JetBrainsMono,
                                    fontWeight = FontWeight.Bold,
                                    color = VoidBlack,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3.5. Resolve Practice Session (Deliberate Practice Completion Panel)
        if (!isCompleted) {
            var actualHoursStr by remember { mutableStateOf(String.format(Locale.US, "%.1f", mission.estimatedHours)) }
            var goalSet by remember { mutableStateOf(true) }
            var gotFeedback by remember { mutableStateOf(true) }
            var pushedComfortZone by remember { mutableStateOf(true) }
            val qualityPercent = ((if (goalSet) 1 else 0) + (if (gotFeedback) 1 else 0) + (if (pushedComfortZone) 1 else 0)) * 100 / 3

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "RESOLVE PRACTICE SESSION",
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SystemGreen,
                        letterSpacing = 1.sp
                    )

                    HorizontalDivider(color = BorderFaint, thickness = 1.dp)

                    // Actual hours spent
                    TerminalTextField(
                        value = actualHoursStr,
                        onValueChange = { actualHoursStr = it },
                        label = "ACTUAL HOURS INVESTED",
                        placeholder = { Text("E.g. 2.0", color = TextDim, fontFamily = JetBrainsMono, fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "DELIBERATE PRACTICE INTEGRITY",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = TextDim,
                        fontWeight = FontWeight.Bold
                    )

                    // Toggle 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Targeted Goal", fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Did you have a specific goal for this session?", fontFamily = Inter, fontSize = 10.sp, color = TextDim)
                        }
                        Switch(
                            checked = goalSet,
                            onCheckedChange = { goalSet = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen)
                        )
                    }

                    // Toggle 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Measurable Feedback", fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Did you get feedback or measure your result?", fontFamily = Inter, fontSize = 10.sp, color = TextDim)
                        }
                        Switch(
                            checked = gotFeedback,
                            onCheckedChange = { gotFeedback = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen)
                        )
                    }

                    // Toggle 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text("Comfort Zone Stretch", fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Did this push you outside your comfort zone?", fontFamily = Inter, fontSize = 10.sp, color = TextDim)
                        }
                        Switch(
                            checked = pushedComfortZone,
                            onCheckedChange = { pushedComfortZone = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen)
                        )
                    }

                    HorizontalDivider(color = BorderFaint, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SESSION QUALITY SCORE",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Quality: $qualityPercent%",
                            fontFamily = JetBrainsMono,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegendaryGold
                        )
                    }

                    val finalHours = actualHoursStr.toFloatOrNull() ?: mission.estimatedHours
                    Button(
                        onClick = {
                            viewModel.completeMission(
                                id = mission.id,
                                actualHours = finalHours,
                                goalSet = goalSet,
                                gotFeedback = gotFeedback,
                                pushedComfortZone = pushedComfortZone
                            )
                            onComplete()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SystemGreen),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_resolve_practice_session")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Resolve", tint = VoidBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COMPLETE PRACTICE SESSION",
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                color = VoidBlack,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Delete protocol action
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ShadowSurface),
            border = BorderStroke(1.dp, BorderFaint)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.missions_danger),
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PenaltyRed,
                    letterSpacing = 1.sp
                )

                HorizontalDivider(color = BorderFaint, thickness = 1.dp)

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, PenaltyRed),
                    shape = RoundedCornerShape(4.dp),
                    interactionSource = deleteInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .graphicsLayer(
                            scaleX = deleteScale,
                            scaleY = deleteScale
                        )
                        .testTag("btn_terminate_mission")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Terminate", tint = PenaltyRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.missions_terminate), 
                            fontFamily = JetBrainsMono, 
                            fontWeight = FontWeight.Bold, 
                            color = PenaltyRed,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
}
