package com.axiom.app.presentation.dungeon

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.ui.DungeonViewModel
import com.axiom.app.ui.DungeonsUiState
import com.axiom.app.ui.components.AwakenTopBar
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.components.SystemToast
import com.axiom.app.ui.components.VoidParticleField
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DungeonDetailScreen(
    dungeonId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DungeonViewModel = hiltViewModel()
) {
    val state by viewModel.dungeonsState.collectAsStateWithLifecycle()

    // Screen-level state
    var isFlashingGreen by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isListExpanded by remember { mutableStateOf(true) }

    // Entry animation states
    var showEntryOverlay by remember { mutableStateOf(true) }
    var entryOverlayScale by remember { mutableStateOf(0f) }
    var entryOverlayAlpha by remember { mutableStateOf(1f) }
    var typedBossName by remember { mutableStateOf("") }

    // Resolve current dungeon to extract details for the entry animation
    val currentDungeon = remember(state) {
        if (state is DungeonsUiState.Success) {
            (state as DungeonsUiState.Success).dungeons.firstOrNull { it.id == dungeonId }
        } else {
            null
        }
    }

    val cleanRarity = remember(currentDungeon) {
        currentDungeon?.rarity?.lowercase()?.replace("-rank", "")?.trim() ?: "normal"
    }

    val rarityColor = when (cleanRarity) {
        "mythic" -> Color(0xFFD500F9)
        "legendary" -> LegendaryGold
        "epic" -> EpicPurple
        "rare" -> RareBlue
        "uncommon" -> UncommonTeal
        else -> CommonGray
    }

    val bossName = remember(cleanRarity) {
        when (cleanRarity) {
            "mythic" -> "Kargalgan the Shadow Monarch"
            "legendary" -> "Baruka the Ice Elf Lord"
            "epic" -> "Ignis the Blood Red"
            "rare" -> "Kasaka the Blue Poison Fang"
            else -> "Raid Alpha Beast"
        }
    }

    // ─── Entry Animation Execution ───
    val entryScale by animateFloatAsState(
        targetValue = entryOverlayScale,
        animationSpec = tween(durationMillis = 600, easing = EaseOutBack),
        label = "entry_scale_anim"
    )

    val entryAlpha by animateFloatAsState(
        targetValue = entryOverlayAlpha,
        animationSpec = tween(durationMillis = 400),
        label = "entry_alpha_anim"
    )

    LaunchedEffect(Unit) {
        // Expand red void (0 -> 1) over 600ms
        entryOverlayScale = 1f
        delay(200)

        // Type boss name character-by-character
        typedBossName = ""
        for (i in 0..bossName.length) {
            typedBossName = bossName.substring(0, i)
            delay(30)
        }

        // Keep displayed briefly for drama, then fade out
        delay(600)
        entryOverlayAlpha = 0f
        delay(400)
        showEntryOverlay = false
    }

    // ─── Stage Completion Event Listener ───
    LaunchedEffect(Unit) {
        viewModel.stageCompletionEvent.collectLatest { event ->
            when (event) {
                is DungeonViewModel.StageCompletionEvent.MidStageCleared -> {
                    // Flash screen green
                    isFlashingGreen = true
                    delay(80)
                    isFlashingGreen = false

                    // Show stage cleared toast
                    toastMessage = "STAGE CLEARED"
                }
                is DungeonViewModel.StageCompletionEvent.BossDefeated -> {
                    // Flash screen green
                    isFlashingGreen = true
                    delay(80)
                    isFlashingGreen = false

                    // Overlay ceremony handled by CeremonyHost, notify locally
                    toastMessage = "BOSS DEFEATED!"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AwakenTopBar(
                title = stringResource(R.string.dungeon_raid_title),
                onBackClick = onBack
            )
        },
        containerColor = LocalAxiomColors.current.voidBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Layer 1: Stars background
            VoidParticleField(modifier = Modifier.fillMaxSize())

            // Layer 2: Main Arena Layout
            when (val s = state) {
                is DungeonsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SystemGreen)
                    }
                }
                is DungeonsUiState.Success -> {
                    val dungeon = s.dungeons.firstOrNull { it.id == dungeonId }

                    if (dungeon == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.dungeon_not_found),
                                color = PenaltyRed,
                                fontFamily = FiraCode
                            )
                        }
                    } else {
                        // Progress bar calculations
                        val targetProgress = if (dungeon.totalStages > 0) {
                            dungeon.completedStages.toFloat() / dungeon.totalStages.toFloat()
                        } else {
                            0f
                        }
                        val animatedProgress by animateFloatAsState(
                            targetValue = targetProgress,
                            animationSpec = tween(700, easing = EaseOutCubic),
                            label = "dungeon_progress_bar_fill"
                        )

                        val parsedStageNames = remember(dungeon.stageDescriptions) {
                            dungeon.stageDescriptions
                                .split("||")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                        }

                        val currentStageDesc = remember(dungeon.completedStages, parsedStageNames) {
                            parsedStageNames.getOrNull(dungeon.completedStages)
                                ?: "Proceed with high-focus protocols to complete this stage."
                        }

                        val isBossStage = dungeon.completedStages == dungeon.totalStages - 1

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // ─── HERO HEADER ───
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                EpicPurple.copy(alpha = 0.25f),
                                                LocalAxiomColors.current.voidBlack
                                            )
                                        )
                                    )
                                    .border(1.dp, EpicPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = bossName.uppercase(),
                                                fontFamily = FiraCode,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = dungeon.name,
                                                fontFamily = Inter,
                                                fontSize = 13.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    rarityColor.copy(alpha = 0.15f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .border(1.dp, rarityColor, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = dungeon.rarity.uppercase(),
                                                fontFamily = FiraCode,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = rarityColor
                                            )
                                        }
                                    }

                                    // Block stylized progress bar
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "RAID PROGRESS",
                                                fontFamily = FiraCode,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDim
                                            )
                                            Text(
                                                text = "Stage ${dungeon.completedStages} of ${dungeon.totalStages}",
                                                fontFamily = FiraCode,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SystemGreen
                                            )
                                        }

                                        // Progress Track
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(BorderFaint)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(animatedProgress)
                                                    .fillMaxHeight()
                                                    .background(
                                                        brush = Brush.horizontalGradient(
                                                            colors = listOf(SystemGreen, UncommonTeal)
                                                        )
                                                    )
                                            )
                                        }
                                    }
                                }
                            }

                            // ─── ACTIVE MISSION CARD ───
                            HolographicCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = if (isBossStage) PenaltyRed else rarityColor,
                                glowEnabled = isBossStage
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = if (isBossStage) "⚠️ ACTIVE BOSS ENCOUNTER" else "ACTIVE MISSION",
                                        fontFamily = FiraCode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isBossStage) PenaltyRed else rarityColor
                                    )

                                    Text(
                                        text = currentStageDesc,
                                        fontFamily = Inter,
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            // ─── PRIMARY CTA ───
                            if (!dungeon.isCompleted) {
                                Button(
                                    onClick = {
                                        viewModel.completeStage(dungeon.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isBossStage) PenaltyRed else SystemGreen,
                                        contentColor = VoidBlack
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("btn_complete_stage")
                                ) {
                                    Text(
                                        text = if (isBossStage) "⚔️ DEFEAT BOSS ⚔️" else "⚡ COMPLETE STAGE ⚡",
                                        fontFamily = FiraCode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            LegendaryGold.copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(1.dp, LegendaryGold, RoundedCornerShape(8.dp))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🏆 DUNGEON CONQUERED",
                                        fontFamily = FiraCode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LegendaryGold
                                    )
                                }
                            }

                            // ─── COLLAPSIBLE STAGE LIST ───
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DimSurface, RoundedCornerShape(8.dp))
                                    .border(1.dp, BorderFaint, RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isListExpanded = !isListExpanded }
                                    .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ALL STAGES",
                                        fontFamily = FiraCode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (isListExpanded) "▲ COLLAPSE" else "▼ EXPAND",
                                        fontFamily = FiraCode,
                                        fontSize = 10.sp,
                                        color = TextDim
                                    )
                                }

                                AnimatedVisibility(visible = isListExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        for (i in 0 until dungeon.totalStages) {
                                            val isCompleted = i < dungeon.completedStages
                                            val isCurrent = i == dungeon.completedStages
                                            val isFuture = i > dungeon.completedStages

                                            val customName = parsedStageNames.getOrNull(i)?.takeIf { it.isNotBlank() }
                                            val stageTitle = when {
                                                i == dungeon.totalStages - 1 -> "STAGE ${i + 1}: BOSS FIGHT ⚔"
                                                customName != null -> "STAGE ${i + 1}: ${customName.uppercase()}"
                                                else -> "STAGE ${i + 1}"
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        if (isCurrent) rarityColor.copy(alpha = 0.08f) else Color.Transparent,
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    if (isCompleted) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Completed",
                                                            tint = SystemGreen,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    } else if (isCurrent) {
                                                        Text(
                                                            text = "→",
                                                            fontFamily = FiraCode,
                                                            fontWeight = FontWeight.Bold,
                                                            color = rarityColor,
                                                            fontSize = 16.sp
                                                        )
                                                    } else {
                                                        Icon(
                                                            imageVector = Icons.Default.Lock,
                                                            contentDescription = "Locked",
                                                            tint = TextDim,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }

                                                    Text(
                                                        text = stageTitle,
                                                        fontFamily = FiraCode,
                                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                        fontSize = 12.sp,
                                                        color = if (isCompleted) TextDim else if (isCurrent) TextPrimary else TextSecondary
                                                    )
                                                }

                                                if (isCompleted) {
                                                    Text(
                                                        text = "CLEARED",
                                                        fontFamily = FiraCode,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SystemGreen
                                                    )
                                                } else if (isCurrent) {
                                                    Text(
                                                        text = "ACTIVE",
                                                        fontFamily = FiraCode,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = rarityColor
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is DungeonsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: " + s.message,
                            color = PenaltyRed,
                            fontFamily = FiraCode
                        )
                    }
                }
            }

            // Layer 3: Screen flash effect
            if (isFlashingGreen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SystemGreen.copy(alpha = 0.35f))
                )
            }

            // Layer 4: System Toast Notification
            SystemToast(
                message = toastMessage,
                onDismiss = { toastMessage = null }
            )

            // Layer 5: Entry Dramatic Animation Overlay
            if (showEntryOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(entryAlpha)
                        .background(LocalAxiomColors.current.voidBlack)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .scale(entryScale)
                            .size(240.dp)
                            .clip(RoundedCornerShape(120.dp))
                            .background(Color(0xFF220000).copy(alpha = 0.85f))
                            .border(2.dp, Color(0xFF8B0000), RoundedCornerShape(120.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "⚠️ RAID ALERT ⚠️",
                                fontFamily = FiraCode,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PenaltyRed,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = typedBossName.uppercase(),
                                fontFamily = FiraCode,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LegendaryGold,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
