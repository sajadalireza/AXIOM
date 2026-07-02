package com.axiom.app.presentation.missions

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.domain.model.Mission
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.MissionsUiState
import com.axiom.app.ui.MissionsViewModel
import com.axiom.app.ui.components.SwipeableMissionCard
import com.axiom.app.ui.components.MissionCard
import com.axiom.app.ui.components.AxiomEmptyState
import com.axiom.app.ui.components.XPFloatAnimation
import com.axiom.app.ui.components.ScanlineOverlay
import com.axiom.app.ui.components.ScreenHelpButton
import com.axiom.app.ui.components.SystemToast
import com.axiom.app.ui.theme.*

import com.axiom.app.presentation.missions.AIMissionGeneratorSheet
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border

@Composable
fun MissionsScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEmbedded: Boolean = false,
    viewModel: MissionsViewModel = hiltViewModel()
) {
    val state by viewModel.missionsState.collectAsStateWithLifecycle()
    val toastState by viewModel.toastMessage.collectAsStateWithLifecycle()
    val xpFloatEvent = viewModel.xpFloatEvent
    val colors = LocalAxiomColors.current

    val isTimerActive by viewModel.isFocusTimerActive.collectAsStateWithLifecycle()
    val activeFocusMission by viewModel.activeFocusMission.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Active, 1 = Pending, 2 = Completed
    var showAISheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isEmbedded) 4.dp else 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header
            val activeCount = when (val s = state) {
                is MissionsUiState.Success -> s.activeMissions.size
                else -> 0
            }
            if (!isEmbedded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.missions_title),
                        fontFamily = JetBrainsMono,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    
                    ScreenHelpButton(stringResId = R.string.glossary_missions)
                }
                Text(
                    text = stringResource(R.string.missions_active_protocols, activeCount),
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = colors.textDim
                )
            }

            var showGlossary by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                showGlossary = !viewModel.isMissionsBriefingShown()
            }
            if (showGlossary) {
                Spacer(modifier = Modifier.height(10.dp))
                com.axiom.app.ui.components.GlossaryBriefingCard(
                    stringResId = com.axiom.app.R.string.glossary_missions,
                    onDismiss = {
                        showGlossary = false
                        viewModel.markMissionsBriefingShown()
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Holographic System Status Card (Diagnostics)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.systemGreen.copy(alpha = 0.03f))
                    .border(1.dp, colors.systemGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isFa = java.util.Locale.getDefault().language == "fa"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isFa) "پروتکل‌های فعال" else "ACTIVE PROTOCOLS",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = colors.textDim,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format("%02d", activeCount),
                            fontFamily = JetBrainsMono,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.systemGreen
                        )
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(BorderFaint))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val pendingCount = when (val s = state) {
                            is MissionsUiState.Success -> s.pendingMissions.size
                            else -> 0
                        }
                        Text(
                            text = if (isFa) "منطقه عایق" else "STASIS ZONE",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = colors.textDim,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format("%02d", pendingCount),
                            fontFamily = JetBrainsMono,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = RareBlue
                        )
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(BorderFaint))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val completedCount = when (val s = state) {
                            is MissionsUiState.Success -> s.completedMissions.size
                            else -> 0
                        }
                        Text(
                            text = if (isFa) "بخش‌های پاکسازی‌شده" else "CLEARED SECTORS",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = colors.textDim,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format("%02d", completedCount),
                            fontFamily = JetBrainsMono,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegendaryGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Tab Bar inside capsule
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DimSurface)
                    .border(1.dp, BorderFaint, RoundedCornerShape(6.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isFa = java.util.Locale.getDefault().language == "fa"
                    val tabs = if (isFa) {
                        listOf("فعال", "معلق", "تکمیل شده")
                    } else {
                        listOf("ACTIVE", "PENDING", "COMPLETED")
                    }
                    val originalTabs = listOf("ACTIVE", "PENDING", "COMPLETED")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        val originalTitle = originalTabs[index]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isSelected) colors.systemGreen.copy(alpha = 0.08f) else Color.Transparent)
                                .clickable { selectedTab = index }
                                .testTag("tab_$originalTitle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = title,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) colors.systemGreen else colors.textDim
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(2.dp)
                                            .background(colors.systemGreen)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            when (val s = state) {
                is MissionsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.systemGreen)
                    }
                }
                is MissionsUiState.Success -> {
                    val listToShow = when (selectedTab) {
                        0 -> s.activeMissions
                        1 -> s.pendingMissions
                        else -> s.completedMissions
                    }

                    if (listToShow.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val isFa = java.util.Locale.getDefault().language == "fa"
                            when (selectedTab) {
                                0 -> {
                                    AxiomEmptyState(
                                        icon = "",
                                        title = if (isFa) "[ مأموریت فعالی وجود ندارد ]" else "[ NO ACTIVE MISSIONS ]",
                                        subtitle = if (isFa) "هر هدف مفیدی که باید امروز انجام دهید یک مأموریت است.\nبرای افزودن اولین مأموریت خود، روی + بزنید." else "Every task you need to do today is a Mission.\nTap + to add your first one.",
                                        ctaLabel = if (isFa) "[ افزودن اولین مأموریت ]" else "[ ADD FIRST MISSION ]",
                                        onCtaClick = { onNavigate(Screen.AddMission.route) },
                                        iconRes = R.drawable.ic_nav_missions
                                    )
                                }
                                1 -> {
                                    AxiomEmptyState(
                                        icon = "",
                                        title = if (isFa) "[ مأموریت معلقی وجود ندارد ]" else "[ NO PENDING MISSIONS ]",
                                        subtitle = if (isFa) "مأموریت خود را به تعویق بیندازید تا به اینجا منتقل شود. مأموریت تا زمانی که آماده باشید، در این بخش باقی می‌ماند." else "Defer a Mission to move it here. It stays until you're ready.",
                                        iconRes = R.drawable.ic_nav_home
                                    )
                                }
                                else -> {
                                    AxiomEmptyState(
                                        icon = "",
                                        title = if (isFa) "[ مأموریت تکمیل‌شده‌ای وجود ندارد ]" else "[ NO COMPLETED MISSIONS ]",
                                        subtitle = if (isFa) "برای کسب امتیاز فعالیت (XP) و ایجاد زنجیره روزهای متوالی، اهدافتان را به انجام برسانید." else "Complete a Mission to earn XP and build your streak.",
                                        iconRes = R.drawable.ic_nav_system
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(listToShow, key = { it.id }) { mission ->
                                val dungeonName = mission.dungeonId?.let { linkId ->
                                    s.dungeons.find { it.id == linkId }?.name
                                }
                                SwipeableMissionCard(
                                    mission = mission,
                                    onComplete = { viewModel.completeMission(mission.id, null) },
                                    onDelete = { viewModel.deleteMission(mission.id) }
                                ) {
                                    MissionCard(
                                        mission = mission,
                                        onComplete = { actualHours ->
                                            viewModel.completeMission(mission.id, actualHours)
                                        },
                                        onDefer = {
                                            viewModel.deferMission(mission.id)
                                        },
                                        onDelete = {
                                            viewModel.deleteMission(mission.id)
                                        },
                                        onClick = {
                                            onNavigate(Screen.MissionDetail(mission.id).route)
                                        },
                                        onFocusClick = {
                                            viewModel.startFocusProtocol(mission, 25)
                                        },
                                        isTimerActive = isTimerActive,
                                        isTimerActiveForMe = activeFocusMission?.id == mission.id,
                                        dungeonName = dungeonName
                                    )
                                }
                            }
                        }
                    }
                }
                is MissionsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(s.message, color = PenaltyRed, fontFamily = JetBrainsMono, fontSize = 14.sp)
                    }
                }
            }
        }

        // Floating action button bottom aligned
        val fabInteractionSource = remember { MutableInteractionSource() }
        val isFabPressed by fabInteractionSource.collectIsPressedAsState()
        val fabScale by animateFloatAsState(
            targetValue = if (isFabPressed) 0.96f else 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessHigh
            ),
            label = "press_scale"
        )
        FloatingActionButton(
            onClick           = { onNavigate(Screen.AddMission.route) },
            containerColor    = colors.systemGreen,
            contentColor      = colors.voidBlack,
            shape             = CircleShape,
            interactionSource = fabInteractionSource,
            modifier          = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
                .scale(fabScale)
                .testTag("fab_add_mission")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Mission")
        }

        XPFloatAnimation(
            xpEventFlow = xpFloatEvent,
            onAnimationComplete = { viewModel.clearXpFloatEvent() }
        )

        ScanlineOverlay()

        if (showAISheet && com.axiom.app.core.FeatureFlags.AI_FEATURES_ENABLED) {
            AIMissionGeneratorSheet(onDismiss = { showAISheet = false })
        }

        // Toast overlay — placed at Box level so it overlays everything without zIndex hacks
        SystemToast(
            message   = toastState?.first,
            isGold    = toastState?.second ?: false,
            onDismiss = { viewModel.clearToast() },
            modifier  = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )
    }
}
