package com.axiom.app.presentation.missions

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.domain.engine.ROIEngine
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.ui.MissionsUiState
import com.axiom.app.ui.MissionsViewModel
import com.axiom.app.ui.components.VoidParticleField
import com.axiom.app.ui.components.AnimatedScanlineOverlay
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMissionScreen(
    onBack: () -> Unit,
    prefilledSkillId: String? = null,
    onMissionCreated: () -> Unit = onBack,
    modifier: Modifier = Modifier,
    viewModel: MissionsViewModel = hiltViewModel()
) {
    val state by viewModel.missionsState.collectAsStateWithLifecycle()

    var currentStep by remember { mutableStateOf(1) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }
    var selectedTrack by remember { mutableStateOf("Wealth") }
    var estimatedHoursStr by remember { mutableStateOf("") }

    // Sliders
    var marketDemand by remember { mutableStateOf(5f) }
    var leverage by remember { mutableStateOf(5f) }
    var complexity by remember { mutableStateOf(5f) }
    var selectedRarityState by remember { mutableStateOf<String?>(null) }

    var selectedDungeon by remember { mutableStateOf<Dungeon?>(null) }
    var isDungeonDropdownExpanded by remember { mutableStateOf(false) }
    var isInstantGate by remember { mutableStateOf(false) }

    // Deliberate Practice Log-as-completed fields
    var logAsCompleted by remember { mutableStateOf(false) }
    var goalSet by remember { mutableStateOf(true) }
    var gotFeedback by remember { mutableStateOf(true) }
    var pushedComfortZone by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack)
    ) {
        // BACKGROUND: VoidParticleField at very low opacity (50% alpha) throughout the screen
        VoidParticleField(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.add_mission_new_transmission),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = TextPrimary
                    )
                )
            },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            when (val s = state) {
                is MissionsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SystemGreen)
                    }
                }
                is MissionsUiState.Success -> {
                    // Initialize fallback skill selection if any
                    LaunchedEffect(s.skills) {
                        if (selectedSkill == null && s.skills.isNotEmpty()) {
                            selectedSkill = if (prefilledSkillId != null) {
                                s.skills.firstOrNull { it.id == prefilledSkillId }
                                    ?: s.skills.firstOrNull()
                            } else {
                                s.skills.firstOrNull()
                            }
                        }
                    }

                    val estHours = estimatedHoursStr.toFloatOrNull() ?: 2.0f
                    val currentPowerScore = ROIEngine.calculatePowerScore(
                        marketDemand = marketDemand,
                        leverage = leverage,
                        complexity = complexity,
                        estimatedHours = estHours
                    )
                    val liveRarity = selectedRarityState ?: ROIEngine.classifyRarity(currentPowerScore)
                    val baseXP = (currentPowerScore * 20f).toInt().coerceAtLeast(25)
                    val suggestion = ROIEngine.toMissionSuggestion(currentPowerScore, baseXP)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. HEADER BOX WITH SCANLINES
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ShadowSurface)
                                .border(1.dp, BorderFaint, RoundedCornerShape(6.dp))
                                .padding(16.dp)
                        ) {
                            AnimatedScanlineOverlay(modifier = Modifier.matchParentSize())

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = stringResource(R.string.add_mission_terminal_title),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SystemGreen
                                )
                                Text(
                                    text = stringResource(R.string.add_mission_define),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDim
                                )
                            }
                        }

                        // CYBER STEP INDICATOR (TAB-LIKE SELECTORS)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val stepsInfo = listOf(
                                    "01 DETAILS" to 1,
                                    "02 CATEGORY" to 2,
                                    "03 ROI ENGINE" to 3,
                                    "04 DEPLOY" to 4
                                )

                                stepsInfo.forEach { (label, stepIdx) ->
                                    val isActive = currentStep == stepIdx
                                    val isCompleted = stepIdx < currentStep

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isActive) SystemGreen.copy(alpha = 0.12f)
                                                else if (isCompleted) SystemGreen.copy(alpha = 0.04f)
                                                else ShadowSurface
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isActive) SystemGreen else if (isCompleted) SystemGreen.copy(alpha = 0.4f) else BorderFaint,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { currentStep = stepIdx }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isCompleted) "$label ✓" else label,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) SystemGreen else if (isCompleted) SystemGreen.copy(alpha = 0.7f) else TextDim
                                        )
                                    }
                                }
                            }

                            // Stepper bar indicator
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(BorderFaint),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (i in 1..4) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(
                                                if (i <= currentStep) SystemGreen else Color.Transparent
                                            )
                                    )
                                }
                            }
                        }

                        // STEP COMPOSABLES
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            when (currentStep) {
                                1 -> {
                                    AddMissionStep1Details(
                                        title = title,
                                        onTitleChange = { title = it },
                                        description = description,
                                        onDescriptionChange = { description = it }
                                    )
                                }
                                2 -> {
                                    AddMissionStep2Category(
                                        skills = s.skills,
                                        selectedSkill = selectedSkill,
                                        onSkillSelect = { selectedSkill = it },
                                        selectedTrack = selectedTrack,
                                        onTrackSelect = { selectedTrack = it }
                                    )
                                }
                                3 -> {
                                    AddMissionStep3ROI(
                                        estimatedHoursStr = estimatedHoursStr,
                                        onEstimatedHoursChange = { estimatedHoursStr = it },
                                        marketDemand = marketDemand,
                                        onMarketDemandChange = { marketDemand = it },
                                        leverage = leverage,
                                        onLeverageChange = { leverage = it },
                                        complexity = complexity,
                                        onComplexityChange = { complexity = it },
                                        selectedRarityState = selectedRarityState,
                                        onRaritySelect = { selectedRarityState = it },
                                        liveRarity = liveRarity,
                                        currentPowerScore = currentPowerScore,
                                        estimatedXP = suggestion.estimatedXP,
                                        successChance = suggestion.successChance
                                    )
                                }
                                4 -> {
                                    val isFormValid = title.isNotBlank() && selectedSkill != null
                                    val scope = rememberCoroutineScope()

                                    AddMissionStep4Protocols(
                                        dungeons = s.dungeons,
                                        selectedDungeon = selectedDungeon,
                                        onDungeonSelect = { selectedDungeon = it },
                                        isDungeonDropdownExpanded = isDungeonDropdownExpanded,
                                        onDropdownExpandedChange = { isDungeonDropdownExpanded = it },
                                        isInstantGate = isInstantGate,
                                        onInstantGateChange = { isInstantGate = it },
                                        logAsCompleted = logAsCompleted,
                                        onLogAsCompletedChange = { logAsCompleted = it },
                                        goalSet = goalSet,
                                        onGoalSetChange = { goalSet = it },
                                        gotFeedback = gotFeedback,
                                        onGotFeedbackChange = { gotFeedback = it },
                                        pushedComfortZone = pushedComfortZone,
                                        onPushedComfortZoneChange = { pushedComfortZone = it },
                                        isFormValid = isFormValid,
                                        onDeployClick = {
                                            scope.launch {
                                                val skill = selectedSkill ?: return@launch
                                                val hours = estimatedHoursStr.toFloatOrNull() ?: 2.0f
                                                if (logAsCompleted) {
                                                    viewModel.addAndCompleteMission(
                                                        title = title,
                                                        track = selectedTrack,
                                                        skillId = skill.id,
                                                        estimatedHours = hours,
                                                        marketDemand = marketDemand,
                                                        leverage = leverage,
                                                        complexity = complexity,
                                                        dungeonId = selectedDungeon?.id,
                                                        isInstantGate = isInstantGate,
                                                        customRarity = liveRarity,
                                                        description = description,
                                                        actualHours = hours,
                                                        goalSet = goalSet,
                                                        gotFeedback = gotFeedback,
                                                        pushedComfortZone = pushedComfortZone
                                                    )
                                                } else {
                                                    viewModel.addMission(
                                                        title = title,
                                                        track = selectedTrack,
                                                        skillId = skill.id,
                                                        estimatedHours = hours,
                                                        marketDemand = marketDemand,
                                                        leverage = leverage,
                                                        complexity = complexity,
                                                        dungeonId = selectedDungeon?.id,
                                                        isInstantGate = isInstantGate,
                                                        customRarity = liveRarity,
                                                        description = description
                                                    )
                                                }
                                                onMissionCreated()
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // STEP NAVIGATION BUTTONS (PREV / NEXT)
                        if (currentStep < 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Previous Button
                                OutlinedButton(
                                    onClick = { if (currentStep > 1) currentStep-- },
                                    enabled = currentStep > 1,
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, if (currentStep > 1) SystemGreen else BorderFaint),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (currentStep > 1) SystemGreen else TextDim
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Text(
                                        text = "PREVIOUS",
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Next Button
                                Button(
                                    onClick = { if (currentStep < 4) currentStep++ },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SystemGreen,
                                        contentColor = VoidBlack
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                ) {
                                    Text(
                                        text = "NEXT",
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
                is MissionsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: " + s.message, color = PenaltyRed, fontFamily = JetBrainsMono)
                    }
                }
            }
        }
    }
}
