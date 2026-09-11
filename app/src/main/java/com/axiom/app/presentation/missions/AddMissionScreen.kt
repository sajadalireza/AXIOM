package com.axiom.app.presentation.missions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.domain.engine.ROIEngine
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.EvidenceLevel
import com.axiom.app.domain.model.MissionAuthoringPayload
import com.axiom.app.domain.model.ScheduleSlot
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.template.MissionTemplate
import com.axiom.app.domain.template.MissionTemplatePack
import com.axiom.app.ui.MissionsUiState
import com.axiom.app.ui.MissionsViewModel
import com.axiom.app.ui.components.AnimatedScanlineOverlay
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.components.VoidParticleField
import com.axiom.app.ui.getLocalizedSkillName
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

    // Core Quick Create Fields
    var title by remember { mutableStateOf("") }
    var doneCondition by remember { mutableStateOf("") }
    var contextTrigger by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableIntStateOf(45) }
    var selectedScheduleSlot by remember { mutableStateOf<ScheduleSlot>(ScheduleSlot.Immediate) }
    var selectedEvidenceLevel by remember { mutableStateOf(EvidenceLevel.NONE) }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }
    var selectedTrack by remember { mutableStateOf("Capability") }

    // Advanced & Fallback Path State (Accordion)
    var isAdvancedExpanded by remember { mutableStateOf(false) }
    var marketDemand by remember { mutableFloatStateOf(5f) }
    var leverage by remember { mutableFloatStateOf(5f) }
    var complexity by remember { mutableFloatStateOf(5f) }
    var selectedRarityState by remember { mutableStateOf<String?>(null) }
    var selectedDungeon by remember { mutableStateOf<Dungeon?>(null) }
    var isDungeonDropdownExpanded by remember { mutableStateOf(false) }
    var isTimedMission by remember { mutableStateOf(false) }
    var additionalNotes by remember { mutableStateOf("") }

    // Deliberate Practice Log-as-completed fields
    var logAsCompleted by remember { mutableStateOf(false) }
    var sessionGoalSet by remember { mutableStateOf(true) }
    var sessionGotFeedback by remember { mutableStateOf(true) }
    var sessionPushedComfortZone by remember { mutableStateOf(true) }

    // Template Pack (G5-P3: E4.3)
    var showTemplateSheet by remember { mutableStateOf(false) }
    var activeTemplateId by remember { mutableStateOf<String?>(null) }
    var activeTemplateTitle by remember { mutableStateOf<String?>(null) }
    var templateWasCustomized by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack)
    ) {
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
                    // Initialize fallback skill selection
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

                    val estHours = (durationMinutes / 60f).coerceAtLeast(0.25f)
                    val currentPowerScore = ROIEngine.calculatePowerScore(
                        marketDemand = marketDemand,
                        leverage = leverage,
                        complexity = complexity,
                        estimatedHours = estHours
                    )
                    val liveRarity = selectedRarityState ?: ROIEngine.classifyRarity(currentPowerScore)
                    val baseXP = (currentPowerScore * 20f).toInt().coerceAtLeast(25)
                    val suggestion = ROIEngine.toMissionSuggestion(currentPowerScore, baseXP)

                    val isFormValid = title.isNotBlank() && doneCondition.isNotBlank() && selectedSkill != null

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. TERMINAL HEADER CARD WITH QUICK CREATE BADGE
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ShadowSurface)
                                .border(1.dp, BorderFaint, RoundedCornerShape(6.dp))
                                .padding(16.dp)
                        ) {
                            AnimatedScanlineOverlay(modifier = Modifier.matchParentSize())

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.add_mission_terminal_title),
                                        fontFamily = JetBrainsMono,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SystemGreen
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SystemGreen.copy(alpha = 0.15f))
                                            .border(1.dp, SystemGreen.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.add_mission_quick_create_badge),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SystemGreen
                                        )
                                    }
                                }
                                Text(
                                    text = stringResource(R.string.add_mission_define),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDim
                                )
                            }
                        }

                        // TEMPLATE PACK SELECTOR / ACTIVE BANNER (G5-P3: E4.3)
                        if (activeTemplateId != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LegendaryGold.copy(alpha = 0.12f))
                                    .border(1.dp, LegendaryGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TEMPLATE APPLIED",
                                        color = LegendaryGold,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = activeTemplateTitle ?: "Solopreneur Template",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        activeTemplateId = null
                                        activeTemplateTitle = null
                                        templateWasCustomized = false
                                    },
                                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                ) {
                                    Text("Clear", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = { showTemplateSheet = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .testTag("btn_browse_templates"),
                                border = BorderStroke(1.dp, LegendaryGold.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LegendaryGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "⚡ BROWSE TEMPLATES (SOFTWARE / SOLOPRENEUR)",
                                    fontFamily = JetBrainsMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // 2. MISSION TITLE INPUT
                        TerminalTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                if (activeTemplateId != null) templateWasCustomized = true
                            },
                            label = stringResource(R.string.add_mission_title_label),
                            placeholder = {
                                Text(
                                    stringResource(R.string.add_mission_title_placeholder),
                                    color = TextDim,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_mission_title")
                        )

                        // 3. DONE CONDITION INPUT (Canonical requirement)
                        TerminalTextField(
                            value = doneCondition,
                            onValueChange = {
                                doneCondition = it
                                if (activeTemplateId != null) templateWasCustomized = true
                            },
                            label = stringResource(R.string.add_mission_done_condition_label),
                            placeholder = {
                                Text(
                                    stringResource(R.string.add_mission_done_condition_placeholder),
                                    color = TextDim,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_mission_done_condition")
                        )

                        // 4. DURATION & SCHEDULE TIMING (Resolving G2-S3-02)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.add_mission_duration_label),
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = TextDim,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(15, 25, 45, 60, 90).forEach { mins ->
                                    val isSelected = durationMinutes == mins
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) SystemGreen.copy(alpha = 0.15f) else ShadowSurface)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) SystemGreen else BorderFaint,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { durationMinutes = mins }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                            .testTag("duration_chip_$mins"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.add_mission_duration_min, mins),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SystemGreen else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // SCHEDULE TIMING CHIPS
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.add_mission_schedule_label),
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = TextDim,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val slots = listOf(
                                    ScheduleSlot.Immediate to stringResource(R.string.add_mission_schedule_now) to "schedule_chip_now",
                                    ScheduleSlot.Morning to stringResource(R.string.add_mission_schedule_morning) to "schedule_chip_morning",
                                    ScheduleSlot.Afternoon to stringResource(R.string.add_mission_schedule_afternoon) to "schedule_chip_afternoon",
                                    ScheduleSlot.Evening to stringResource(R.string.add_mission_schedule_evening) to "schedule_chip_evening"
                                )
                                slots.forEach { (slotAndLabel, tag) ->
                                    val (slot, label) = slotAndLabel
                                    val isSelected = selectedScheduleSlot == slot
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) SystemGreen.copy(alpha = 0.15f) else ShadowSurface)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) SystemGreen else BorderFaint,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { selectedScheduleSlot = slot }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                            .testTag(tag),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SystemGreen else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // 5. EXECUTION CONTEXT / TRIGGER (Optional)
                        TerminalTextField(
                            value = contextTrigger,
                            onValueChange = { contextTrigger = it },
                            label = stringResource(R.string.add_mission_context_label),
                            placeholder = {
                                Text(
                                    stringResource(R.string.add_mission_context_placeholder),
                                    color = TextDim,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_mission_context")
                        )

                        // 6. EVIDENCE LEVEL SELECTOR
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.add_mission_evidence_label),
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = TextDim,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val evidenceOptions = listOf(
                                    EvidenceLevel.NONE to stringResource(R.string.add_mission_evidence_none) to "evidence_chip_none",
                                    EvidenceLevel.BINARY_CHECKLIST to stringResource(R.string.add_mission_evidence_checklist) to "evidence_chip_checklist",
                                    EvidenceLevel.METRIC_OR_NOTE to stringResource(R.string.add_mission_evidence_metric) to "evidence_chip_metric"
                                )
                                evidenceOptions.forEach { (pair, tag) ->
                                    val (evLevel, label) = pair
                                    val isSelected = selectedEvidenceLevel == evLevel
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) SystemGreen.copy(alpha = 0.15f) else ShadowSurface)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) SystemGreen else BorderFaint,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { selectedEvidenceLevel = evLevel }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                            .testTag(tag),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SystemGreen else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // 7. LINKED SKILL & TRACK
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.add_mission_linked_skill),
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = TextDim,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                s.skills.forEach { skill ->
                                    val isSelected = selectedSkill?.id == skill.id
                                    HolographicCard(
                                        modifier = Modifier
                                            .widthIn(min = 120.dp)
                                            .heightIn(min = 44.dp)
                                            .clickable { selectedSkill = skill }
                                            .testTag("skill_chip_${skill.id}"),
                                        accentColor = if (isSelected) SystemGreen else BorderFaint,
                                        glowEnabled = isSelected
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = getLocalizedSkillName(skill.name).uppercase(),
                                                fontFamily = JetBrainsMono,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) SystemGreen else TextDim,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // TRACK ALIGNMENT
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = stringResource(R.string.add_mission_alignment),
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = TextDim,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Capability", "Wealth", "Discovery", "Network").forEach { tr ->
                                    val isSelected = selectedTrack == tr
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 36.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) SystemGreen.copy(alpha = 0.15f) else ShadowSurface)
                                            .border(
                                                border = BorderStroke(
                                                    width = 1.dp,
                                                    color = if (isSelected) SystemGreen else BorderFaint
                                                ),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { selectedTrack = tr }
                                            .padding(horizontal = 4.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tr.uppercase(),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) SystemGreen else TextSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // 8. NON-BLOCKING ADVANCED ROI & SETTINGS ACCORDION
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ShadowSurface)
                                .border(1.dp, BorderFaint, RoundedCornerShape(6.dp))
                                .padding(12.dp)
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isAdvancedExpanded = !isAdvancedExpanded }
                                    .testTag("btn_toggle_advanced"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAdvancedExpanded)
                                        stringResource(R.string.add_mission_advanced_toggle_collapse)
                                    else
                                        stringResource(R.string.add_mission_advanced_toggle_expand),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAdvancedExpanded) SystemGreen else TextDim
                                )
                                Icon(
                                    imageVector = if (isAdvancedExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Toggle Advanced",
                                    tint = if (isAdvancedExpanded) SystemGreen else TextDim
                                )
                            }

                            AnimatedVisibility(visible = isAdvancedExpanded) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    HorizontalDivider(color = BorderFaint)

                                    // Target Rarity Chips
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = stringResource(R.string.add_mission_target_rarity),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            color = TextDim,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY").forEach { rarity ->
                                                val isColorSelected = liveRarity.uppercase() == rarity
                                                val color = rarityColorMap[rarity] ?: CommonGray
                                                HolographicCard(
                                                    modifier = Modifier
                                                        .width(100.dp)
                                                        .height(38.dp)
                                                        .clickable {
                                                            selectedRarityState = rarity
                                                            when (rarity) {
                                                                "COMMON" -> { marketDemand = 1f; leverage = 1f; complexity = 1f }
                                                                "UNCOMMON" -> { marketDemand = 3f; leverage = 3f; complexity = 3f }
                                                                "RARE" -> { marketDemand = 5f; leverage = 5f; complexity = 5f }
                                                                "EPIC" -> { marketDemand = 8f; leverage = 8f; complexity = 8f }
                                                                "LEGENDARY" -> { marketDemand = 10f; leverage = 10f; complexity = 10f }
                                                            }
                                                        }
                                                        .testTag("rarity_chip_$rarity"),
                                                    accentColor = if (isColorSelected) color else BorderFaint,
                                                    glowEnabled = isColorSelected
                                                ) {
                                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = rarity,
                                                            fontFamily = JetBrainsMono,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isColorSelected) color else TextDim
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Sliders: Market Demand, Leverage, Complexity
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("ROI ENGINE SLIDERS", fontFamily = JetBrainsMono, fontSize = 11.sp, color = TextDim, fontWeight = FontWeight.Bold)

                                        Column {
                                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                                Text(stringResource(R.string.add_mission_market_value), fontFamily = JetBrainsMono, fontSize = 10.sp, color = TextSecondary)
                                                Text("${marketDemand.toInt()}/10", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SystemGreen)
                                            }
                                            Slider(
                                                value = marketDemand,
                                                onValueChange = { marketDemand = it },
                                                valueRange = 1f..10f,
                                                steps = 8,
                                                colors = SliderDefaults.colors(thumbColor = SystemGreen, activeTrackColor = SystemGreen)
                                            )
                                        }

                                        Column {
                                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                                Text(stringResource(R.string.add_mission_growth), fontFamily = JetBrainsMono, fontSize = 10.sp, color = TextSecondary)
                                                Text("${leverage.toInt()}/10", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SystemGreen)
                                            }
                                            Slider(
                                                value = leverage,
                                                onValueChange = { leverage = it },
                                                valueRange = 1f..10f,
                                                steps = 8,
                                                colors = SliderDefaults.colors(thumbColor = SystemGreen, activeTrackColor = SystemGreen)
                                            )
                                        }

                                        Column {
                                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                                Text(stringResource(R.string.add_mission_mental_load), fontFamily = JetBrainsMono, fontSize = 10.sp, color = TextSecondary)
                                                Text("${complexity.toInt()}/10", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SystemGreen)
                                            }
                                            Slider(
                                                value = complexity,
                                                onValueChange = { complexity = it },
                                                valueRange = 1f..10f,
                                                steps = 8,
                                                colors = SliderDefaults.colors(thumbColor = SystemGreen, activeTrackColor = SystemGreen)
                                            )
                                        }
                                    }

                                    // Optional Project / Dungeon dropdown
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = stringResource(R.string.add_mission_assign_dungeon),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            color = TextDim,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(min = 48.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                                                .background(DimSurface)
                                                .clickable { isDungeonDropdownExpanded = true }
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = selectedDungeon?.name ?: "[ NO PROJECT ASSIGNED ]",
                                                    fontFamily = Inter,
                                                    color = if (selectedDungeon != null) TextPrimary else TextDim,
                                                    fontSize = 13.sp
                                                )
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand", tint = TextSecondary)
                                            }
                                            DropdownMenu(
                                                expanded = isDungeonDropdownExpanded,
                                                onDismissRequest = { isDungeonDropdownExpanded = false },
                                                modifier = Modifier.fillMaxWidth(0.85f).background(ShadowSurface)
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("[ NO PROJECT ASSIGNED ]", fontFamily = Inter, color = TextDim) },
                                                    onClick = {
                                                        selectedDungeon = null
                                                        isDungeonDropdownExpanded = false
                                                    }
                                                )
                                                s.dungeons.filter { !it.isCompleted }.forEach { dungeon ->
                                                    DropdownMenuItem(
                                                        text = { Text(dungeon.name, fontFamily = Inter, color = TextPrimary) },
                                                        onClick = {
                                                            selectedDungeon = dungeon
                                                            isDungeonDropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Timed Mission Switch
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isTimedMission) LegendaryGold.copy(alpha = 0.08f) else DimSurface)
                                            .border(1.dp, if (isTimedMission) LegendaryGold else BorderFaint, RoundedCornerShape(4.dp))
                                            .clickable { isTimedMission = !isTimedMission }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(
                                                text = stringResource(R.string.instant_gate_protocol_title),
                                                fontFamily = JetBrainsMono,
                                                fontSize = 11.sp,
                                                color = if (isTimedMission) LegendaryGold else TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = stringResource(R.string.instant_gate_protocol_desc),
                                                fontFamily = Inter,
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        Switch(
                                            checked = isTimedMission,
                                            onCheckedChange = { isTimedMission = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = LegendaryGold,
                                                checkedTrackColor = LegendaryGold.copy(alpha = 0.3f)
                                            )
                                        )
                                    }

                                    // Deliberate Practice Log As Completed Switch
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (logAsCompleted) SystemGreen.copy(alpha = 0.08f) else DimSurface)
                                            .border(1.dp, if (logAsCompleted) SystemGreen else BorderFaint, RoundedCornerShape(4.dp))
                                            .clickable { logAsCompleted = !logAsCompleted }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(
                                                text = "LOG AS COMPLETED SESSION",
                                                fontFamily = JetBrainsMono,
                                                fontSize = 11.sp,
                                                color = if (logAsCompleted) SystemGreen else TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Immediately records completed practice to the Progress Ledger.",
                                                fontFamily = Inter,
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        Switch(
                                            checked = logAsCompleted,
                                            onCheckedChange = { logAsCompleted = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = SystemGreen,
                                                checkedTrackColor = SystemGreen.copy(alpha = 0.3f)
                                            )
                                        )
                                    }

                                    if (logAsCompleted) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(DimSurface)
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("PRACTICE SETTINGS", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SystemGreen, fontWeight = FontWeight.Bold)

                                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                                Text("Session target defined?", fontFamily = Inter, fontSize = 11.sp, color = TextPrimary)
                                                Switch(checked = sessionGoalSet, onCheckedChange = { sessionGoalSet = it }, colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen))
                                            }
                                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                                Text("Feedback obtained?", fontFamily = Inter, fontSize = 11.sp, color = TextPrimary)
                                                Switch(checked = sessionGotFeedback, onCheckedChange = { sessionGotFeedback = it }, colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen))
                                            }
                                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                                Text("Pushed comfort zone?", fontFamily = Inter, fontSize = 11.sp, color = TextPrimary)
                                                Switch(checked = sessionPushedComfortZone, onCheckedChange = { sessionPushedComfortZone = it }, colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen))
                                            }
                                        }
                                    }

                                    // Additional Notes
                                    TerminalTextField(
                                        value = additionalNotes,
                                        onValueChange = { additionalNotes = it },
                                        label = "ADDITIONAL NOTES",
                                        placeholder = { Text("Supplementary context or links", color = TextDim, fontFamily = JetBrainsMono, fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // 9. SINGLE DOMINANT PRIMARY CTA (Product Constitution Section 3.5)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HolographicCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 52.dp)
                                    .testTag("btn_accept_mission")
                                    .let {
                                        if (isFormValid) {
                                            it.clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                val skill = selectedSkill ?: return@clickable
                                                val payload = MissionAuthoringPayload(
                                                    title = title,
                                                    doneCondition = doneCondition,
                                                    contextTrigger = contextTrigger,
                                                    durationMinutes = durationMinutes,
                                                    evidenceLevel = selectedEvidenceLevel,
                                                    scheduleSlot = selectedScheduleSlot,
                                                    skillId = skill.id,
                                                    track = selectedTrack,
                                                    dungeonId = selectedDungeon?.id,
                                                    isTimedMission = isTimedMission,
                                                    notes = additionalNotes,
                                                    marketDemand = marketDemand,
                                                    leverage = leverage,
                                                    complexity = complexity,
                                                    customRarity = liveRarity,
                                                    logAsCompleted = logAsCompleted,
                                                    sessionGoalSet = sessionGoalSet,
                                                    sessionGotFeedback = sessionGotFeedback,
                                                    sessionPushedComfortZone = sessionPushedComfortZone
                                                )
                                                viewModel.createMissionFromPayload(
                                                    payload = payload,
                                                    templateId = activeTemplateId,
                                                    wasCustomized = templateWasCustomized
                                                ) {
                                                    onMissionCreated()
                                                }
                                            }
                                        } else {
                                            it
                                        }
                                    },
                                accentColor = if (isFormValid) SystemGreen else BorderFaint,
                                glowEnabled = isFormValid
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 52.dp)
                                        .background(if (isFormValid) SystemGreen else DimSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.add_mission_primary_cta),
                                        fontFamily = JetBrainsMono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isFormValid) VoidBlack else TextSecondary
                                    )
                                }
                            }

                            if (!isFormValid) {
                                Text(
                                    text = stringResource(R.string.add_mission_validation_hint),
                                    fontFamily = Inter,
                                    fontSize = 11.sp,
                                    color = TextDim,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
                is MissionsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: " + s.message, color = PenaltyRed, fontFamily = JetBrainsMono)
                    }
                }
            }
        }

        if (showTemplateSheet) {
            MissionTemplateSelectorSheet(
                onDismiss = { showTemplateSheet = false },
                onSelectTemplate = { template ->
                    title = template.titleEn
                    doneCondition = template.doneConditionEn
                    contextTrigger = template.contextTriggerEn
                    durationMinutes = template.defaultDurationMinutes
                    activeTemplateId = template.id
                    activeTemplateTitle = template.titleEn
                    templateWasCustomized = false
                    viewModel.recordTemplateExposed(template.id)
                    val s = state
                    if (s is MissionsUiState.Success) {
                        val matchedSkill = s.skills.firstOrNull { 
                            it.name.equals(template.recommendedSkillName, ignoreCase = true) 
                        }
                        if (matchedSkill != null) {
                            selectedSkill = matchedSkill
                        }
                    }
                    showTemplateSheet = false
                }
            )
        }
    }
}
