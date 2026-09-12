package com.axiom.app.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.R
import com.axiom.app.core.startup.StartupReadiness
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
    private val ensureAnonymousSessionUseCase: com.axiom.app.domain.usecase.EnsureAnonymousSessionUseCase,
    private val preferences: com.axiom.app.data.local.AxiomPreferences,
    private val hunterRepository: com.axiom.app.domain.repository.HunterRepository,
    private val startupReadiness: StartupReadiness,
    private val firstWinFactsReader: com.axiom.app.domain.firstwin.FirstWinFactsReader,
    private val firstWinControlPlane: com.axiom.app.domain.firstwin.control.FirstWinControlPlane,
) : ViewModel() {
    /**
     * Resolves the launch destination from authoritative startup facts.
     *
     * WP-201: awaits startup readiness BEFORE reading any state, so the route is
     * independent of Splash animation / seeding coroutine timing.
     * WP-202: the returned [LaunchDestination] is the single navigation authority
     * handed verbatim to the one-shot Splash exit (no NavGraph recomputation).
     * WP-203: routing consults the four-fact [EligibilityStateMachine] — the Hunter
     * entity is a prerequisite fact, never completion evidence.
     * WP-207: the resulting eligibility decision is then combined with the durable
     * First-Win session lifecycle.
     * WP-208: FirstWinControlPlane gates treatment activity with sticky variant
     * assignment, eligibility versioning, and local/remote kill boundaries.
     */
    suspend fun resolveDestination(): LaunchDestination {
        // WP-201 gate: block until startup work is done, THEN read facts.
        startupReadiness.await()
        val hunter = hunterRepository.getDirectHunterProfile()
        val snapshot = EligibilitySnapshot(
            setupComplete = preferences.setupCompleteFlow.first(),
            hunterExists = hunter != null,
            firstMissionDone = preferences.firstMissionDoneFlow.first(),
            blueprintSetupComplete = preferences.blueprintSetupCompleteFlow.first(),
        )
        val eligibility = EligibilityStateMachine.evaluate(snapshot)

        // Durable First-Win state is hunter-scoped by a deterministic correlation id.
        // Unknown persisted status remains fail-closed through FirstWinFactsReader:
        // sessionExists=true + sessionStatus=null resumes FIRST_WIN rather than
        // trusting an unrecognized lifecycle label or silently routing HOME.
        val firstWinFacts = hunter?.let {
            firstWinFactsReader.read(
                setupComplete = snapshot.setupComplete,
                sessionId = com.axiom.app.domain.firstwin.FirstWinIds.sessionId(it.id),
            )
        }

        val isTreatmentActive = firstWinControlPlane.isTreatmentActive()

        return FirstWinLaunchPolicy.resolve(
            eligibility = eligibility,
            firstWinSessionStatus = firstWinFacts?.sessionStatus,
            firstWinSessionExists = firstWinFacts?.sessionExists ?: false,
            isTreatmentActive = isTreatmentActive,
        )
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
    onDestinationResolved: (LaunchDestination) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var resolvedDestination by remember { mutableStateOf<LaunchDestination?>(null) }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.ensureAnonymousSessionInBackground()
        val dest = try {
            viewModel.resolveDestination()
        } catch (e: Exception) {
            e.printStackTrace()
            LaunchDestination.SETUP
        }
        resolvedDestination = dest
        isReady = true

        // For established returning users (HOME), seamlessly transition on launch
        if (dest == LaunchDestination.HOME) {
            delay(1200)
            onDestinationResolved(dest)
        }
    }

    val colors = LocalAxiomColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
            .testTag("splash_screen")
    ) {
        // Upper Artwork: Stars, AXIOM Wordmark, and Sunrise Horizon
        Image(
            painter = painterResource(R.drawable.bg_launch_horizon),
            contentDescription = "AXIOM Horizon",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        // Bottom Content: Tagline and Begin CTA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            // Tagline: "Small steps. Real progress."
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.launch_tagline_1),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Light,
                    fontSize = 28.sp,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = stringResource(R.string.launch_tagline_2),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Light,
                    fontSize = 28.sp,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
            }

            // Primary CTA: "Begin  →"
            com.axiom.app.ui.components.AxiomPillButton(
                text = stringResource(R.string.launch_btn_begin),
                onClick = {
                    val dest = resolvedDestination ?: LaunchDestination.SETUP
                    onDestinationResolved(dest)
                },
                enabled = isReady,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}
