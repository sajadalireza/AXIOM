package com.axiom.app.presentation.shadow

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.axiom.app.R
import com.axiom.app.ui.ShadowArmyUiState
import com.axiom.app.ui.ShadowViewModel
import com.axiom.app.ui.components.*
import com.axiom.app.ui.theme.*
import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.model.Mission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShadowArmyScreen(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {},
    viewModel: ShadowViewModel = hiltViewModel()
) {
    val state by viewModel.shadowsState.collectAsStateWithLifecycle()
    val colors = LocalAxiomColors.current

    var showAriseDialog by remember { mutableStateOf(false) }
    var selectedSkillToArise by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedShadowForDetail by remember { mutableStateOf<Shadow?>(null) }
    var sortBy by remember { mutableStateOf(SortOption.POWER) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val s = state) {
                is ShadowArmyUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LegendaryGold)
                    }
                }
                is ShadowArmyUiState.Success -> {
                    if (s.hunterLevel < 1 && !s.isDevBypass) {
                        CyberLockedFeaturePanel(
                            title = stringResource(R.string.shadow_locked_title),
                            lockedMessage = stringResource(R.string.shadow_locked_message),
                            requirementLabel = stringResource(R.string.shadow_locked_requirement),
                            currentProgressLabel = stringResource(R.string.shadow_locked_progress, s.hunterLevel),
                            progress = s.hunterLevel.toFloat() / 5f,
                            actionLabel = stringResource(R.string.shadow_locked_action),
                            onActionClick = { onNavigate(com.axiom.app.navigation.Screen.Missions.route) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        val count = s.shadows.size

                        var showGlossary by remember { mutableStateOf(false) }
                        val scope = rememberCoroutineScope()
                        LaunchedEffect(Unit) {
                            val shown = viewModel.preferences.briefingShadowFlow.first()
                            showGlossary = !shown
                        }
                        if (showGlossary) {
                            GlossaryBriefingCard(
                                stringResId = R.string.glossary_shadow_army,
                                onDismiss = {
                                    showGlossary = false
                                    scope.launch {
                                        viewModel.preferences.setBriefingShown("shadow")
                                    }
                                }
                            )
                        }

                        // 1. HERO BANNER
                        ShadowHeroBanner(count = count)

                        // 2. SEGMENTED SORT CONTROL
                        SortSegmentedControl(
                            selectedOption = sortBy,
                            onOptionSelected = { sortBy = it }
                        )

                        // 3. PULSING ARISE BUTTON SECTION
                        if (s.candidates.isNotEmpty()) {
                            AriseSection(
                                candidates = s.candidates,
                                onAriseClick = { id, name ->
                                    selectedSkillToArise = Pair(id, name)
                                    showAriseDialog = true
                                }
                            )
                        }

                        // 4. MAIN ARMY STAGGERED GRID OR EMPTY STATE
                        if (s.shadows.isEmpty()) {
                            AxiomEmptyState(
                                icon = "",
                                title = "[ VIRTUAL TEAM DIRECTORY DORMANT ]",
                                subtitle = "Operatives represent specialized AI personas assisting you.\nMaster your skill checkpoints to enlist active Operatives.",
                                iconRes = R.drawable.ic_nav_shadows,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        } else {
                            val sortedShadows = remember(s.shadows, sortBy) {
                                when (sortBy) {
                                    SortOption.POWER -> s.shadows.sortedByDescending { it.powerLevel }
                                    SortOption.DATE -> s.shadows.sortedByDescending { it.acquiredAt }
                                    SortOption.CATEGORY -> s.shadows.sortedBy { it.skillCategory.uppercase() }
                                }
                            }

                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalItemSpacing = 12.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(sortedShadows, key = { it.id }) { shadow ->
                                    ShadowCard(
                                        shadow = shadow,
                                        onClick = { selectedShadowForDetail = shadow }
                                    )
                                }
                            }
                        }
                    }
                }
                is ShadowArmyUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.shadow_system_error, s.message ?: ""),
                            color = colors.systemGreen,
                            fontFamily = JetBrainsMono
                        )
                    }
                }
            }
        }

        // 5. DEPLOYMENT DIALOG (VIRTUAL TEAM COMMAND)
        if (showAriseDialog && selectedSkillToArise != null) {
            var shadowName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = {
                    showAriseDialog = false
                    selectedSkillToArise = null
                },
                title = {
                    Text(
                        text = stringResource(R.string.shadow_extract_title),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.systemGreen
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = stringResource(R.string.shadow_extract_desc, selectedSkillToArise?.second?.uppercase() ?: ""),
                            fontFamily = Inter,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )

                        Text(
                            text = "DESIGNATED TEAM ROLES:",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textDim
                        )

                        val personas = listOf(
                            "Research Scientist",
                            "ML Engineer",
                            "Startup Advisor",
                            "English Coach",
                            "Market Intelligence",
                            "Publishing Coach",
                            "Accountability Partner",
                            "Ruthless Critic"
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            personas.forEach { persona ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (shadowName == persona) colors.systemGreen.copy(alpha = 0.2f) else colors.voidBlack)
                                        .border(
                                            width = 1.dp,
                                            color = if (shadowName == persona) colors.systemGreen else colors.borderFaint,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clickable { shadowName = persona }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = persona,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (shadowName == persona) colors.systemGreen else colors.textPrimary
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = shadowName,
                            onValueChange = { shadowName = it },
                            placeholder = { Text("E.g. ML Engineer", color = colors.textDim) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.systemGreen,
                                unfocusedBorderColor = colors.borderFaint,
                                focusedTextColor = colors.textPrimary,
                                unfocusedTextColor = colors.textPrimary
                            ),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                imeAction = androidx.compose.ui.text.input.ImeAction.Done
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("field_shadow_army_arise_name")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val skillId = selectedSkillToArise?.first
                            if (skillId != null && shadowName.isNotBlank()) {
                                viewModel.ariseShadow(skillId, shadowName)
                                showAriseDialog = false
                                selectedSkillToArise = null
                            }
                        },
                        enabled = shadowName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.systemGreen,
                            contentColor = colors.voidBlack
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.testTag("btn_confirm_arise_army")
                    ) {
                        Text(
                            text = stringResource(R.string.shadow_arise),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAriseDialog = false
                            selectedSkillToArise = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colors.textDim
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.shadow_cancel),
                            fontFamily = JetBrainsMono
                        )
                    }
                },
                containerColor = colors.dimSurface,
                shape = RoundedCornerShape(8.dp)
            )
        }

        // 6. BOTTOM SHEET DETAILS FOR ACTIVE SHADOW
        if (selectedShadowForDetail != null) {
            val shadow = selectedShadowForDetail!!
            val successState = state as? ShadowArmyUiState.Success
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { selectedShadowForDetail = null },
                sheetState = sheetState,
                containerColor = colors.dimSurface,
                scrimColor = Color.Black.copy(alpha = 0.7f)
            ) {
                ShadowDetailSheetContent(
                    shadow = shadow,
                    missions = successState?.missions ?: emptyList(),
                    onDismiss = { selectedShadowForDetail = null }
                )
            }
        }
    }
}

@Composable
fun ShadowHeroBanner(
    count: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E1430), // Very dark EpicPurple
                Color(0xFF0C0714)  // Void deep black-purple
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(gradientBrush)
            .border(1.dp, Color(0xFF7F77DD).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .testTag("shadow_hero_banner")
    ) {
        AnimatedScanlineOverlay(modifier = Modifier.matchParentSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "YOUR SHADOW ARMY",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7F77DD),
                letterSpacing = 0.15.em
            )

            Text(
                text = "═══════════════",
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                color = Color(0xFF7F77DD).copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$count SHADOWS BOUND",
                style = HudXL,
                fontSize = 32.sp,
                color = LegendaryGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("shadow_bound_count_title")
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "\"The Weak become Strength.\"",
                fontFamily = Fraunces,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = colors.textDim,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SortSegmentedControl(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(colors.voidBlack)
            .border(1.dp, colors.borderFaint, RoundedCornerShape(6.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SortOption.values().forEach { option ->
            val isSelected = option == selectedOption
            val label = when (option) {
                SortOption.POWER -> "POWER"
                SortOption.DATE -> "DATE"
                SortOption.CATEGORY -> "CATEGORY"
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) colors.systemGreen.copy(alpha = 0.15f) else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) colors.systemGreen else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) colors.systemGreen else colors.textDim
                )
            }
        }
    }
}

@Composable
fun ShadowDetailSheetContent(
    shadow: Shadow,
    missions: List<Mission>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val rarityColor = Color(shadow.rarityColor.toInt())
    val linkedMissions = remember(shadow.skillId, missions) {
        missions.filter { it.skillId == shadow.skillId }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.dimSurface)
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShadowSigil(
                name = shadow.name,
                color = rarityColor,
                modifier = Modifier.size(54.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shadow.name.uppercase(),
                    style = TitleL,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RANK: ${shadow.rankLabel.replace("-Rank", "").uppercase()}",
                        style = LabelS,
                        color = rarityColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "•",
                        style = LabelS,
                        color = colors.textDim
                    )
                    Text(
                        text = "LV.${shadow.level}",
                        style = LabelS,
                        color = colors.systemGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Divider(color = colors.borderFaint)

        // Story Section
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "OPERATIVE ORIGIN & STORY",
                style = HudSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = rarityColor
            )

            Text(
                text = getShadowStory(shadow),
                style = LabelL,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Linked Missions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "LINKED MISSIONS (${linkedMissions.size})",
                style = HudSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.systemGreen
            )

            if (linkedMissions.isEmpty()) {
                Text(
                    text = "No active or completed missions associated with this Operative's specialty yet.",
                    style = LabelS,
                    color = colors.textDim,
                    fontStyle = FontStyle.Italic
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    linkedMissions.forEach { mission ->
                        val isCompleted = mission.completedAt != null
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.voidBlack)
                                .border(0.5.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mission.title,
                                    style = LabelL,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Rarity: ${mission.rarity}",
                                    style = LabelS,
                                    color = colors.textDim
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isCompleted) colors.systemGreen.copy(alpha = 0.1f) else Color(0xFFEF9F27).copy(alpha = 0.1f))
                                    .border(
                                        width = 0.5.dp,
                                        color = if (isCompleted) colors.systemGreen else Color(0xFFEF9F27),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isCompleted) "COMPLETED" else "ACTIVE",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) colors.systemGreen else Color(0xFFEF9F27)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AriseSection(
    candidates: List<com.axiom.app.domain.model.Skill>,
    onAriseClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    
    val infiniteTransition = rememberInfiniteTransition(label = "arise_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.systemGreen.copy(alpha = pulseAlpha))
            .border(1.2.dp, colors.systemGreen, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚠️ DETECTED SHADOW CANDIDATES",
                style = HudSmall,
                color = colors.systemGreen,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Your achievements have unlocked latent virtual team operatives. Extract and bind them to your command:",
            style = LabelS,
            color = colors.textPrimary
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            candidates.forEach { skill ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.voidBlack)
                        .border(0.5.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = skill.name.uppercase(),
                            style = LabelL,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Focus: ${skill.category.uppercase()} • Rank: ${skill.rankLabel}",
                            style = LabelS,
                            color = colors.textDim
                        )
                    }

                    Button(
                        onClick = { onAriseClick(skill.id, skill.name) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.systemGreen,
                            contentColor = colors.voidBlack
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.testTag("btn_arise_skill_${skill.id}")
                    ) {
                        Text(
                            text = "ARISE",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
