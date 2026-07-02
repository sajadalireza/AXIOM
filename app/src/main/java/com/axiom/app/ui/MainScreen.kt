package com.axiom.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.axiom.app.domain.model.WorkoutTemplate
import com.axiom.app.ui.theme.LegendaryGold
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.axiom.app.navigation.AwakenNavGraph
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.components.AwakenBottomNavBar
import com.axiom.app.ui.components.CompanionXionWidget
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import com.axiom.app.ui.components.xion.XionMood
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.axiom.app.ui.theme.VoidBlack
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.PenaltyRed
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import com.axiom.app.presentation.ceremony.CeremonyHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val safeNavigate: (String) -> Unit = remember(navController) {
        { route ->
            val currentDestRoute = navController.currentBackStackEntry?.destination?.route
            if (currentDestRoute != route) {
                try {
                    navController.navigate(route) {
                        val startDest = navController.graph.startDestinationRoute ?: Screen.Home.route
                        val targetPopUp = if (startDest == Screen.Splash.route || startDest == Screen.Setup.route) {
                            Screen.Home.route
                        } else {
                            startDest
                        }
                        popUpTo(targetPopUp) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                } catch (e: Exception) {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            }
        }
    }

    val focusManager = viewModel.focusProtocolManager
    val isTimerActive by focusManager.isTimerActive.collectAsStateWithLifecycle()
    val timerSeconds by focusManager.timerSecondsRemaining.collectAsStateWithLifecycle()
    val activeTitle by focusManager.activeFocusTitle.collectAsStateWithLifecycle()
    val pendingWorkoutMins by focusManager.pendingWorkoutMinutes.collectAsStateWithLifecycle()

    val pendingDailyCount by viewModel.pendingDailyCheckinCount.collectAsStateWithLifecycle()
    val overdueWeeklyCount by viewModel.overdueWeeklyReviewCount.collectAsStateWithLifecycle()
    val newMessagesCount by viewModel.newSystemMessagesCount.collectAsStateWithLifecycle()

    val tabs = remember {
        listOf(
            Screen.Missions,
            Screen.BodyMap,
            Screen.Home,
            Screen.ShadowArmy,
            Screen.Profile
        )
    }

    val selectedIndex = remember(currentRoute, tabs) {
        val index = tabs.indexOfFirst { tab ->
            currentRoute == tab.route ||
            (tab == Screen.Missions && (currentRoute == Screen.Missions.route || currentRoute == Screen.Dungeons.route || currentRoute?.startsWith("dungeon") == true)) ||
            (tab == Screen.BodyMap && (currentRoute == Screen.BodyMap.route || currentRoute == Screen.DailyCheckin.route)) ||
            (tab == Screen.Home && currentRoute == Screen.Home.route) ||
            (tab == Screen.ShadowArmy && (currentRoute == Screen.ShadowArmy.route || currentRoute?.startsWith("shadow") == true)) ||
            (tab == Screen.Profile && (currentRoute == Screen.Profile.route || currentRoute == Screen.CharacterStats.route || currentRoute == Screen.SkillTree.route))
        }
        if (index != -1) index else 2 // default to Center HOME tab
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Secure App-Level Focus Protocol Observer (CRITICAL GOAL ACHIEVEMENT)
    DisposableEffect(lifecycleOwner, isTimerActive) {
        val observer = LifecycleEventObserver { _, event ->
            if (isTimerActive && (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_PAUSE)) {
                val powerManager = context.getSystemService(android.content.Context.POWER_SERVICE) as? android.os.PowerManager
                val isInteractive = powerManager?.isInteractive ?: true
                if (isInteractive) {
                    focusManager.pauseOrAbortFocusProtocol(isBreach = true)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Compose-friendly way to request POST_NOTIFICATIONS on Android 13+ (API 33+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle result if needed (e.g. log status)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val shouldShowBottomBar = when (currentRoute) {
        Screen.Home.route,
        Screen.Missions.route,
        Screen.Dungeons.route,
        Screen.ShadowArmy.route,
        Screen.SystemVoice.route,
        Screen.Leagues.route,
        Screen.DailyCheckin.route,
        Screen.BodyMap.route,
        Screen.SkillTree.route -> true
        else -> false
    }

    var dragAmountTotal by remember { mutableStateOf(0f) }
    val swipeModifier = if (shouldShowBottomBar) {
        Modifier.pointerInput(currentRoute) {
            detectHorizontalDragGestures(
                onDragStart = { dragAmountTotal = 0f },
                onDragEnd = {
                    val threshold = 150f
                    if (dragAmountTotal > threshold) {
                        // Swipe right -> Go to previous tab
                        val prevIndex = (selectedIndex - 1 + 5) % 5
                        safeNavigate(tabs[prevIndex].route)
                    } else if (dragAmountTotal < -threshold) {
                        // Swipe left -> Go to next tab
                        val nextIndex = (selectedIndex + 1) % 5
                        safeNavigate(tabs[nextIndex].route)
                    }
                }
            ) { change, dragAmount ->
                change.consume()
                dragAmountTotal += dragAmount
            }
        }
    } else {
        Modifier
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                Column {
                    if (isTimerActive) {
                        FocusActiveChip(
                            missionTitle = activeTitle ?: if (java.util.Locale.getDefault().language == "fa") "هدف پروتکل فعال" else "Active Protocol Target",
                            timerSeconds = timerSeconds,
                            onTap = {
                                safeNavigate(Screen.Leagues.route)
                            },
                            onStop = {
                                focusManager.pauseOrAbortFocusProtocol(isBreach = false)
                            }
                        )
                    }
                    AwakenBottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { screen ->
                            safeNavigate(screen.route)
                        },
                        pendingCheckinCount = pendingDailyCount,
                        overdueWeeklyReviewCount = overdueWeeklyCount,
                        newSystemMessagesCount = newMessagesCount
                    )
                }
            }
        },
        containerColor = LocalAxiomColors.current.voidBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(swipeModifier)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (shouldShowBottomBar) {
                    val axiomViewModel: AxiomViewModel = hiltViewModel()
                    MainHUD(viewModel = axiomViewModel)
                }
                Box(modifier = Modifier.weight(1f)) {
                    AwakenNavGraph(
                        navController = navController,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // CeremonyHost overlay for Phase 7
            CeremonyHost(
                onNavigateToMissions = {
                    safeNavigate(Screen.Missions.route)
                }
            )

            val isSplash = currentRoute == Screen.Splash.route
            val shouldShowXionWidget = shouldShowBottomBar || isSplash

            if (shouldShowXionWidget) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val avatarScale = remember { Animatable(0f) }
                    var splashMood by remember { mutableStateOf(XionMood.GLITCHED) }

                    LaunchedEffect(currentRoute) {
                        if (isSplash) {
                            avatarScale.snapTo(0f)
                            splashMood = XionMood.GLITCHED
                            delay(1200)

                            launch {
                                splashMood = XionMood.GLITCHED
                                delay(150)
                                splashMood = XionMood.THINKING
                                delay(300)
                                splashMood = XionMood.EXCITED
                                delay(150)
                                splashMood = XionMood.HAPPY
                            }

                            avatarScale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        } else {
                            avatarScale.snapTo(1f)
                        }
                    }

                    val avatarSize by animateDpAsState(
                        targetValue = if (isSplash) 160.dp else 62.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "avatar_size"
                    )

                    val targetX = if (isSplash) {
                        (maxWidth - avatarSize) / 2
                    } else {
                        maxWidth - avatarSize - 16.dp
                    }

                    val targetY = if (isSplash) {
                        (maxHeight - avatarSize) / 2 + 60.dp
                    } else {
                        maxHeight - avatarSize - (if (isTimerActive) 80.dp else 24.dp)
                    }

                    val animatedX by animateDpAsState(
                        targetValue = targetX,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "avatar_x"
                    )

                    val animatedY by animateDpAsState(
                        targetValue = targetY,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "avatar_y"
                    )

                    CompanionXionWidget(
                        onNavigate = { route ->
                            safeNavigate(route)
                        },
                        isSplashMode = isSplash,
                        splashScale = avatarScale.value,
                        splashMood = splashMood,
                        modifier = Modifier
                            .size(avatarSize)
                            .offset(x = animatedX, y = animatedY)
                    )
                }
            }

            pendingWorkoutMins?.let { mins ->
                WorkoutPromptDialog(
                    durationMinutes = mins,
                    onSelectTemplate = { template ->
                        focusManager.submitWorkoutTemplate(template)
                    },
                    onDismiss = {
                        focusManager.dismissWorkoutPrompt()
                    }
                )
            }

            val secondarySheetRoutes = remember {
                setOf(
                    Screen.SystemVoice.route,
                    Screen.WeeklyReview.route,
                    Screen.DecisionFilter.route,
                    Screen.Premium.route
                )
            }

            if (currentRoute in secondarySheetRoutes) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val colors = LocalAxiomColors.current
                ModalBottomSheet(
                    onDismissRequest = {
                        navController.popBackStack()
                    },
                    sheetState = sheetState,
                    containerColor = colors.voidBlack,
                    dragHandle = {
                        BottomSheetDefaults.DragHandle(color = colors.systemGreen)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .fillMaxWidth()
                    ) {
                        when (currentRoute) {
                            Screen.SystemVoice.route -> {
                                com.axiom.app.presentation.systemvoice.SystemVoiceScreen(
                                    onNavigate = { route ->
                                        navController.popBackStack()
                                        safeNavigate(route)
                                    }
                                )
                            }
                            Screen.WeeklyReview.route -> {
                                com.axiom.app.presentation.review.WeeklyReviewScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            Screen.DecisionFilter.route -> {
                                com.axiom.app.presentation.decisionfilter.DecisionFilterScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            Screen.Premium.route -> {
                                com.axiom.app.presentation.premium.PremiumScreen(
                                    onBack = { navController.popBackStack() }
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
fun FocusActiveChip(
    missionTitle: String,
    timerSeconds: Int,
    onTap: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val isFa = java.util.Locale.getDefault().language == "fa"
    val formattedTime = String.format("%02d:%02d", timerSeconds / 60, timerSeconds % 60)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(colors.voidBlack.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
            .border(1.dp, colors.systemGreen.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .clickable { onTap() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isFa) "⏱ تمرکز فعال: " else "⏱ FOCUS ACTIVE: ",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.systemGreen
            )
            Text(
                text = missionTitle,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = formattedTime,
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.systemGreen
            )
            androidx.compose.material3.IconButton(
                onClick = { onStop() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Stop Timer",
                    tint = PenaltyRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutPromptDialog(
    durationMinutes: Int,
    onSelectTemplate: (WorkoutTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalAxiomColors.current
    val context = LocalContext.current
    val isFa = java.util.Locale.getDefault().language == "fa"
    val penaltyRed = PenaltyRed

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.voidBlack, RoundedCornerShape(12.dp))
                .border(1.dp, LegendaryGold, RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(LegendaryGold.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape)
                        .border(1.dp, LegendaryGold, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 24.sp,
                        color = LegendaryGold
                    )
                }

                Text(
                    text = if (isFa) "پروتکل تمرین تکمیل شد" else "WORKOUT PROTOCOL COMPLETED",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = LegendaryGold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isFa) {
                        "شما یک جلسه تمرکز $durationMinutes دقیقه‌ای را با موفقیت پشت سر گذاشتید. آیا این جلسه تمرین فیزیکی بود؟ برای به‌روزرسانی شاخص عضلات، نوع پروتکل را انتخاب کنید:"
                    } else {
                        "You successfully completed a $durationMinutes-minute focus session. Was this physical training? Select a protocol template to update your muscle recovery core:"
                    },
                    fontFamily = com.axiom.app.ui.theme.Inter,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                val templates = listOf(
                    WorkoutTemplate.Push,
                    WorkoutTemplate.Pull,
                    WorkoutTemplate.Legs,
                    WorkoutTemplate.FullBody,
                    WorkoutTemplate.RunCardio
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    templates.forEach { template ->
                        val resId = remember(template.nameKey) {
                            context.resources.getIdentifier(template.nameKey, "string", context.packageName)
                        }
                        val displayName = if (resId != 0) stringResource(resId) else template.nameKey

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .background(colors.shadowSurface, RoundedCornerShape(6.dp))
                                .border(1.dp, colors.borderFaint, RoundedCornerShape(6.dp))
                                .clickable { onSelectTemplate(template) }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayName.uppercase(),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "▶",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    color = LegendaryGold
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.voidBlack),
                    border = androidx.compose.foundation.BorderStroke(1.dp, penaltyRed.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text(
                        text = if (isFa) "انصراف / رد شدن" else "CANCEL / SKIP",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = penaltyRed
                    )
                }
            }
        }
    }
}
