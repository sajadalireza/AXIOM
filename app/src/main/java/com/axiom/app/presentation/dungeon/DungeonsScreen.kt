package com.axiom.app.presentation.dungeon

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.DungeonViewModel
import com.axiom.app.ui.DungeonsUiState
import com.axiom.app.ui.components.DungeonCard
import com.axiom.app.ui.components.AxiomEmptyState
import com.axiom.app.ui.components.ScanlineOverlay
import com.axiom.app.ui.components.ScreenHelpButton
import com.axiom.app.ui.theme.*

@Composable
fun DungeonsScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEmbedded: Boolean = false,
    viewModel: DungeonViewModel = hiltViewModel()
) {
    val isFa = java.util.Locale.getDefault().language == "fa"
    val state by viewModel.dungeonsState.collectAsStateWithLifecycle()
    val colors = LocalAxiomColors.current

    var isCompletedSectionExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        colors.voidBlack,
                        EpicPurple.copy(alpha = 0.08f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isEmbedded) 4.dp else 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header
            val activeRaidsCount = when (val s = state) {
                is DungeonsUiState.Success -> s.dungeons.count { !it.isCompleted }
                else -> 0
            }

            if (!isEmbedded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dungeons_title),
                        fontFamily = JetBrainsMono,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    
                    ScreenHelpButton(stringResId = R.string.glossary_dungeons)
                }
                Text(
                    text = stringResource(R.string.dungeons_active_raids, activeRaidsCount),
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = colors.textDim
                )
            }

            var showGlossary by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            LaunchedEffect(Unit) {
                val shown = viewModel.preferences.briefingDungeonsFlow.first()
                showGlossary = !shown
            }
            if (showGlossary) {
                Spacer(modifier = Modifier.height(10.dp))
                com.axiom.app.ui.components.GlossaryBriefingCard(
                    stringResId = com.axiom.app.R.string.glossary_dungeons,
                    onDismiss = {
                        showGlossary = false
                        scope.launch {
                            viewModel.preferences.setBriefingShown("dungeons")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val s = state) {
                is DungeonsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.systemGreen)
                    }
                }
                is DungeonsUiState.Success -> {
                    if (s.hunterLevel < 1 && !s.isDevBypass) {
                        com.axiom.app.ui.components.CyberLockedFeaturePanel(
                            title = stringResource(R.string.dungeons_locked_title),
                            lockedMessage = stringResource(R.string.dungeons_locked_message),
                            requirementLabel = stringResource(R.string.dungeons_locked_requirement),
                            currentProgressLabel = stringResource(R.string.dungeons_locked_progress, s.hunterLevel),
                            progress = s.hunterLevel.toFloat() / 3f,
                            actionLabel = stringResource(R.string.dungeons_locked_action),
                            onActionClick = { onNavigate(Screen.Missions.route) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        val activeDungeons = s.dungeons.filter { !it.isCompleted }
                        val completedDungeons = s.dungeons.filter { it.isCompleted }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            if (activeDungeons.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AxiomEmptyState(
                                            icon = "🏰",
                                            title = if (isFa) "[ دانجن فعالی وجود ندارد ]" else "[ NO ACTIVE DUNGEONS ]",
                                            subtitle = if (isFa) "دانجن نشان‌دهنده یک پروژه چند مرحله‌ای است. یک هدف بزرگ را به مراحل مختلف تقسیم کنید.\nبرای ایجاد اولین دانجن خود روی + ضربه بزنید." else "A Dungeon is a multi-stage project. Break a big goal into stages.\nTap + to create your first Dungeon.",
                                            ctaLabel = if (isFa) "[ ایجاد دانجن ]" else "[ CREATE DUNGEON ]",
                                            onCtaClick = { onNavigate(Screen.CreateDungeon.route) }
                                        )
                                    }
                                }
                            } else {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(SystemGreen, androidx.compose.foundation.shape.CircleShape)
                                        )
                                        Text(
                                            text = "ACTIVE DUNGEONS",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textDim,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                                items(activeDungeons, key = { it.id }) { dungeon ->
                                    val interactionSource = remember { MutableInteractionSource() }
                                    val isPressed by interactionSource.collectIsPressedAsState()
                                    val scale by animateFloatAsState(
                                        targetValue = if (isPressed) 0.96f else 1.0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessHigh
                                        ),
                                        label = "press_scale"
                                    )
                                    DungeonCard(
                                        dungeon = dungeon,
                                        onEnter = {
                                            onNavigate(Screen.DungeonDetail(dungeon.id).route)
                                        },
                                        modifier = Modifier
                                            .scale(scale)
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = androidx.compose.foundation.LocalIndication.current
                                            ) {
                                                onNavigate(Screen.DungeonDetail(dungeon.id).route)
                                            }
                                    )
                                }
                            }

                            // Completed dungeons section collapsed/expanded at bottom
                            if (completedDungeons.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isCompletedSectionExpanded = !isCompletedSectionExpanded }
                                            .background(colors.dimSurface)
                                            .padding(12.dp)
                                            .testTag("completed_dungeons_header"),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "🏆",
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = stringResource(R.string.dungeons_conquered_count, completedDungeons.size),
                                                fontFamily = JetBrainsMono,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textSecondary
                                            )
                                        }
                                        Icon(
                                            imageVector = if (isCompletedSectionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expand/Collapse",
                                            tint = colors.textDim
                                        )
                                    }
                                }

                                if (isCompletedSectionExpanded) {
                                    items(completedDungeons, key = { it.id }) { dungeon ->
                                        val interactionSource = remember { MutableInteractionSource() }
                                        val isPressed by interactionSource.collectIsPressedAsState()
                                        val scale by animateFloatAsState(
                                            targetValue = if (isPressed) 0.96f else 1.0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessHigh
                                            ),
                                            label = "press_scale"
                                        )
                                        DungeonCard(
                                            dungeon = dungeon,
                                            onEnter = {
                                                onNavigate(Screen.DungeonDetail(dungeon.id).route)
                                            },
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .scale(scale)
                                                .clickable(
                                                    interactionSource = interactionSource,
                                                    indication = androidx.compose.foundation.LocalIndication.current
                                                ) {
                                                    onNavigate(Screen.DungeonDetail(dungeon.id).route)
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is DungeonsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(s.message, color = PenaltyRed, fontFamily = JetBrainsMono)
                    }
                }
            }
        }

        // FAB to Create Dungeon
        val isFabVisible = when (val s = state) {
            is DungeonsUiState.Success -> s.hunterLevel >= 3 || s.isDevBypass
            else -> false
        }
        if (isFabVisible) {
            FloatingActionButton(
                onClick = { onNavigate(Screen.CreateDungeon.route) },
                containerColor = colors.systemGreen,
                contentColor = colors.voidBlack,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("fab_create_dungeon")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Dungeon")
            }
        }

        ScanlineOverlay()
    }
}
