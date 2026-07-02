package com.axiom.app.presentation.skilltree

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.axiom.app.ui.SkillTreeViewModel
import com.axiom.app.ui.SkillTreeUiState
import com.axiom.app.ui.components.ScanlineOverlay
import com.axiom.app.ui.components.ScreenHelpButton
import com.axiom.app.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillTreeScreen(
    onBack: () -> Unit,
    onNavigateToAddMission: (skillId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SkillTreeViewModel = hiltViewModel(),
    showTopBar: Boolean = true
) {
    val colors = LocalAxiomColors.current
    val uiState by viewModel.skillsState.collectAsStateWithLifecycle()
    var showAddSkillDialog by remember { mutableStateOf(false) }

    val allSkills = remember(uiState) {
        if (uiState is SkillTreeUiState.Success) {
            (uiState as SkillTreeUiState.Success).skills
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(com.axiom.app.R.string.skill_protocol_awakening),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.textPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.textPrimary
                            )
                        }
                    },
                    actions = {
                        ScreenHelpButton(
                            stringResId = com.axiom.app.R.string.glossary_skill_tree,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.voidBlack,
                        titleContentColor = colors.textPrimary
                    )
                )
            }
        },
        containerColor = colors.voidBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showTopBar) innerPadding else androidx.compose.foundation.layout.PaddingValues(0.dp))
        ) {
            // 1. SkillTreeCanvas occupies the entire viewport for fully immersive navigation
            SkillTreeCanvas(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
                onCenterHubClick = { showAddSkillDialog = true }
            )

            // 1b. PASSIVE BOOST SLOT OVERLAY (Holographic Gamer HUD Theme)
            val equippedPassiveSkillId by viewModel.equippedPassiveSkillId.collectAsStateWithLifecycle()
            val equippedPassiveSkill = remember(allSkills, equippedPassiveSkillId) {
                allSkills.firstOrNull { it.id == equippedPassiveSkillId }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .widthIn(max = 240.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.voidBlack.copy(alpha = 0.85f))
                    .border(
                        BorderStroke(
                            1.dp,
                            if (equippedPassiveSkill != null) LegendaryGold.copy(alpha = 0.6f) else BorderFaint.copy(alpha = 0.4f)
                        ),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(com.axiom.app.R.string.passive_boost_slot),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = LegendaryGold,
                            letterSpacing = 1.sp
                        )
                        if (equippedPassiveSkill != null) {
                            IconButton(
                                onClick = { viewModel.equipPassiveSkill(null) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_clear_passive_slot")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear passive slot",
                                    tint = PenaltyRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (equippedPassiveSkill != null) {
                        val rankColor = when (val rank = equippedPassiveSkill.rankLabel.replace("-Rank", "").trim()) {
                            "S", "S+", "Legendary" -> LegendaryGold
                            "A" -> EpicPurple
                            else -> SystemGreen
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(rankColor.copy(alpha = 0.2f))
                                    .border(1.dp, rankColor, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = equippedPassiveSkill.rankLabel.take(2).uppercase(),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rankColor
                                )
                            }

                            Text(
                                text = com.axiom.app.ui.LocalizationUtils.getLocalizedSkillName(equippedPassiveSkill.name, androidx.compose.ui.platform.LocalContext.current).uppercase(),
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                        }

                        // Passive Bonus Display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(SystemGreen.copy(alpha = 0.1f))
                                .border(1.dp, SystemGreen.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(com.axiom.app.R.string.passive_multiplier_bonus),
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = SystemGlint
                            )
                        }
                    } else {
                        // Empty slot state
                        com.axiom.app.ui.components.AxiomEmptyState(
                            icon = "⬡",
                            title = "[ CONDUIT EMPTY ]",
                            subtitle = "Equip specialized B-Class+ skill module",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            var showGlossary by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            LaunchedEffect(Unit) {
                val shown = viewModel.preferences.briefingSkillTreeFlow.first()
                showGlossary = !shown
            }
            if (showGlossary) {
                com.axiom.app.ui.components.GlossaryBriefingCard(
                    stringResId = com.axiom.app.R.string.glossary_skill_tree,
                    onDismiss = {
                        showGlossary = false
                        scope.launch {
                            viewModel.preferences.setBriefingShown("skill_tree")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                )
            }

            // 2. SkillDetailPanel opens as a ModalBottomSheet
            SkillDetailPanel(
                viewModel = viewModel,
                onNavigateToAddMission = onNavigateToAddMission
            )

            if (showAddSkillDialog) {
                var skillName by remember { mutableStateOf("") }
                var selectedCategory by remember { mutableStateOf("Programming") }
                var selectedParentId by remember { mutableStateOf<String?>(null) }
                var showParentDropdown by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = { showAddSkillDialog = false },
                    title = {
                        Text(
                            text = stringResource(com.axiom.app.R.string.skill_add_routine),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LegendaryGold
                        )
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            OutlinedTextField(
                                value = skillName,
                                onValueChange = { skillName = it },
                                label = { Text("Routine Name", color = TextDim) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LegendaryGold,
                                    unfocusedBorderColor = BorderFaint,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("field_add_skill_name")
                            )

                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = { selectedCategory = it },
                                label = { Text("Sector / Category", color = TextDim) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LegendaryGold,
                                    unfocusedBorderColor = BorderFaint,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("field_add_skill_category")
                            )

                            Text(
                                text = stringResource(com.axiom.app.R.string.skill_highlight_suggestions),
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TextDim
                            )

                            val defaultCategories = listOf("Programming", "Business", "Knowledge", "Health", "Creativity")
                            val customCategories = allSkills.map { it.category.trim() }.filter { it.isNotBlank() }.distinct()
                            val categories = (defaultCategories + customCategories).distinctBy { it.lowercase().trim() }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                categories.forEach { cat ->
                                    val isSelected = selectedCategory.lowercase().trim() == cat.lowercase().trim()
                                    val borderCol = if (isSelected) LegendaryGold else BorderFaint
                                    val bgCol = if (isSelected) LegendaryGold.copy(alpha = 0.15f) else Color.Transparent
                                    val textCol = if (isSelected) LegendaryGold else TextSecondary

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(bgCol)
                                            .border(BorderStroke(1.dp, borderCol), RoundedCornerShape(4.dp))
                                            .clickable { selectedCategory = cat }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat.uppercase(),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textCol
                                        )
                                    }
                                }
                            }

                            Text(
                                text = stringResource(com.axiom.app.R.string.skill_parent_label),
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TextDim
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                val selectedParentName = if (selectedParentId == null) {
                                    "None (Root Node)"
                                } else {
                                    allSkills.firstOrNull { it.id == selectedParentId }?.name ?: "None"
                                }

                                OutlinedCard(
                                    onClick = { showParentDropdown = true },
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = LocalAxiomColors.current.voidBlack,
                                    ),
                                    border = BorderStroke(1.dp, BorderFaint),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedParentName,
                                            fontFamily = Inter,
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "▼",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 10.sp,
                                            color = LegendaryGold
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showParentDropdown,
                                    onDismissRequest = { showParentDropdown = false },
                                    modifier = Modifier.fillMaxWidth(0.9f).background(DimSurface).border(1.dp, BorderFaint)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("None (Root Node)", fontFamily = Inter, fontSize = 13.sp, color = TextPrimary) },
                                        onClick = {
                                            selectedParentId = null
                                            showParentDropdown = false
                                        }
                                    )
                                    Divider(color = BorderFaint)
                                    allSkills.forEach { s ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${s.name} [${s.category.uppercase()}]",
                                                    fontFamily = Inter,
                                                    fontSize = 13.sp,
                                                    color = TextPrimary
                                                )
                                            },
                                            onClick = {
                                                selectedParentId = s.id
                                                showParentDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (skillName.isNotBlank()) {
                                    viewModel.createSkill(skillName, selectedCategory, selectedParentId)
                                    showAddSkillDialog = false
                                }
                            },
                            enabled = skillName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LegendaryGold,
                                contentColor = VoidBlack
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.testTag("btn_register_node")
                        ) {
                            Text(
                                text = stringResource(com.axiom.app.R.string.skill_register_node),
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showAddSkillDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = TextDim
                            )
                        ) {
                            Text(
                                text = "CANCEL",
                                fontFamily = JetBrainsMono
                            )
                        }
                    },
                    containerColor = DimSurface,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            ScanlineOverlay()
        }
    }
}
