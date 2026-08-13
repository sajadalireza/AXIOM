package com.axiom.app.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.R
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.ui.components.xion.XionLivingEyeAvatar
import com.axiom.app.ui.components.xion.XionMood
import com.axiom.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val hunterRepository: HunterRepository,
    private val ensureAnonymousSessionUseCase: com.axiom.app.domain.usecase.EnsureAnonymousSessionUseCase,
    private val preferences: com.axiom.app.data.local.AxiomPreferences
) : ViewModel() {
    /**
     * Resolves the launch destination from authoritative startup flags.
     *
     * WP-201 (RED): no startup-readiness gate — reads state as soon as called,
     * so it can observe pre-bootstrap state and misroute depending on timing.
     */
    suspend fun resolveDestination(): LaunchDestination {
        val resolver = LaunchRouteResolver(
            awaitStartupReady = {},
            readState = {
                LaunchInputs(
                    setupComplete = preferences.setupCompleteFlow.first(),
                    firstMissionDone = preferences.firstMissionDoneFlow.first(),
                    blueprintSetupComplete = preferences.blueprintSetupCompleteFlow.first()
                )
            }
        )
        return resolver.resolve()
    }
    fun ensureAnonymousSessionInBackground() {
        viewModelScope.launch {
            try {
                ensureAnonymousSessionUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToSetup: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    var axiomOsTextProgress by remember { mutableStateOf(0) }
    var warriorTextProgress by remember { mutableStateOf(0) }
    var subtitleTextProgress by remember { mutableStateOf(0) }
    var showScanlineSweep by remember { mutableStateOf(false) }
    var scanlineProgress by remember { mutableStateOf(0f) }

    // Verification lines states
    var verificationLine1Progress by remember { mutableStateOf(0) }
    var verificationLine2Progress by remember { mutableStateOf(0) }
    var verificationLine3Progress by remember { mutableStateOf(0) }

    var exitAlpha by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        // Fire-and-forget — does not block splash timing or navigation.
        viewModel.ensureAnonymousSessionInBackground()

        // 0ms: Stage 1 - Start Scanline Sweep
        showScanlineSweep = true
        launch {
            animate(0f, 1f, animationSpec = tween(300, easing = EaseOutQuad)) { v, _ ->
                scanlineProgress = v
            }
            showScanlineSweep = false
        }

        // 300ms: Stage 2 - Type "AXIOM OS v5.1"
        delay(300)
        val axiomOsStr = "AXIOM OS v5.1"
        for (i in 1..axiomOsStr.length) {
            axiomOsTextProgress = i
            delay(400L / axiomOsStr.length)
        }

        // 700ms: Stage 3 - Type "WARRIOR" and system verification simultaneously
        val warriorStr = "WARRIOR"
        launch {
            for (i in 1..warriorStr.length) {
                warriorTextProgress = i
                delay(500L / warriorStr.length)
            }
        }

        launch {
            val line1 = "> LOADING HUNTER PROFILE..."
            for (i in 1..line1.length) {
                verificationLine1Progress = i
                delay(150L / line1.length)
            }
            val line2 = "> CHECKING STREAK STATUS..."
            for (i in 1..line2.length) {
                verificationLine2Progress = i
                delay(150L / line2.length)
            }
            val line3 = "> SYSTEM READY ✓"
            for (i in 1..line3.length) {
                verificationLine3Progress = i
                delay(150L / line3.length)
            }
        }

        // 1200ms: Stage 4 - Avatar materializes below (avatar handled in MainScreen coordinates, so we just wait)
        delay(500) // delay to reach 1200ms
        delay(600) // delay during materialization (1200 to 1800ms)

        // 1800ms: Stage 5 - Type "AWAKENING PROTOCOL INITIALIZED"
        val subtitleStr = "AWAKENING PROTOCOL INITIALIZED"
        for (i in 1..subtitleStr.length) {
            subtitleTextProgress = i
            delay(600L / subtitleStr.length)
        }

        // 2400ms: Stage 6 - Hold then fade out to destination
        delay(200)
        animate(1f, 0f, animationSpec = tween(300)) { v, _ ->
            exitAlpha = v
        }

        coroutineScope.launch {
            try {
                when (viewModel.resolveDestination()) {
                    LaunchDestination.SETUP -> onNavigateToSetup()
                    LaunchDestination.HOME -> onNavigateToHome()
                    LaunchDestination.ONBOARDING,
                    LaunchDestination.BLUEPRINT_WIZARD -> onNavigateToOnboarding()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onNavigateToSetup()
            }
        }
    }

    var showDiagnostics by remember { mutableStateOf(false) }

    if (showDiagnostics) {
        com.axiom.app.ui.components.DiagnosticsHUD(
            onDismissRequest = { showDiagnostics = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(exitAlpha)
            .background(LocalAxiomColors.current.voidBlack)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Floating Diagnostics Button
        androidx.compose.material3.IconButton(
            onClick = { showDiagnostics = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
                .size(48.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "System Diagnostics",
                tint = SystemGreen.copy(alpha = 0.6f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // AXIOM OS Header
            Box(modifier = Modifier.height(24.dp), contentAlignment = Alignment.Center) {
                if (axiomOsTextProgress > 0) {
                    Text(
                        text = "AXIOM OS v5.1".take(axiomOsTextProgress),
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SystemGreen,
                        modifier = Modifier.testTag("splash_axiom_os")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verification lines container
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .width(220.dp)
                    .height(60.dp)
            ) {
                if (verificationLine1Progress > 0) {
                    Text(
                        text = "> LOADING HUNTER PROFILE...".take(verificationLine1Progress),
                        fontFamily = FiraCode,
                        fontSize = 10.sp,
                        color = SystemGreen.copy(alpha = 0.8f)
                    )
                }
                if (verificationLine2Progress > 0) {
                    Text(
                        text = "> CHECKING STREAK STATUS...".take(verificationLine2Progress),
                        fontFamily = FiraCode,
                        fontSize = 10.sp,
                        color = SystemGreen.copy(alpha = 0.8f)
                    )
                }
                if (verificationLine3Progress > 0) {
                    Text(
                        text = "> SYSTEM READY ✓".take(verificationLine3Progress),
                        fontFamily = FiraCode,
                        fontSize = 10.sp,
                        color = SystemGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // WARRIOR Title
            Box(modifier = Modifier.height(56.dp), contentAlignment = Alignment.Center) {
                if (warriorTextProgress > 0) {
                    Text(
                        text = "WARRIOR".take(warriorTextProgress),
                        style = DisplayXL,
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.testTag("splash_axiom_title")
                    )
                }
            }

            // Spaced-out visual landing zone for the flying avatar
            Spacer(modifier = Modifier.height(180.dp))

            // Subtitle
            Box(modifier = Modifier.height(36.dp), contentAlignment = Alignment.Center) {
                if (subtitleTextProgress > 0) {
                    Text(
                        text = "AWAKENING PROTOCOL INITIALIZED".take(subtitleTextProgress),
                        style = SystemMsg,
                        color = SystemGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("splash_axiom_subtitle")
                    )
                }
            }
        }

        // Custom Scanner Sweep pass overlay
        if (showScanlineSweep) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val y = size.height * scanlineProgress
                drawLine(
                    color = SystemGreen.copy(alpha = 0.8f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 3.dp.toPx()
                )
                drawRect(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            SystemGreen.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        startY = maxOf(0f, y - 100.dp.toPx()),
                        endY = y
                    ),
                    topLeft = Offset(0f, maxOf(0f, y - 100.dp.toPx())),
                    size = androidx.compose.ui.geometry.Size(size.width, minOf(y, 100.dp.toPx()))
                )
            }
        }
    }
}
