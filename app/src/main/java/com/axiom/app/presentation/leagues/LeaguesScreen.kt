package com.axiom.app.presentation.leagues

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.ui.LeaguesUiState
import com.axiom.app.ui.LeaguesViewModel
import com.axiom.app.ui.theme.*

@Composable
fun LeaguesScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeaguesViewModel = hiltViewModel()
) {
    val axiomColors = LocalAxiomColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeMission by viewModel.activeFocusMission.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSecondsRemaining.collectAsStateWithLifecycle()
    val isTimerActive by viewModel.isTimerActive.collectAsStateWithLifecycle()
    val isTimerPaused by viewModel.isTimerPaused.collectAsStateWithLifecycle()
    val isBreachDetected by viewModel.isBreachDetected.collectAsStateWithLifecycle()
    val isFastSyncEnabled by viewModel.fastTimeSyncEnabled.collectAsStateWithLifecycle()
    val hardModeEnabled by viewModel.hardModeEnabledFlow.collectAsStateWithLifecycle(initialValue = false)
    val daysSince by viewModel.daysSinceFirstLaunchFlow.collectAsStateWithLifecycle(initialValue = 0)

    val isPreregistering by viewModel.isPreregistering.collectAsStateWithLifecycle()
    val preregisterError by viewModel.preregisterError.collectAsStateWithLifecycle()

    // Division Rank Up Ceremony States (Priority 4)
    var previousTier by remember { mutableStateOf<String?>(null) }
    var showRankUpDialog by remember { mutableStateOf(false) }
    var rankUpOldTier by remember { mutableStateOf("") }
    var rankUpNewTier by remember { mutableStateOf("") }

    // Neural Calibration Countdown State (Priority 5)
    var countdownSeconds by remember { mutableStateOf(5) }

    LaunchedEffect(isBreachDetected) {
        if (isBreachDetected) {
            countdownSeconds = 5
            while (countdownSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                countdownSeconds--
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(axiomColors.voidBlack)
            .statusBarsPadding()
    ) {
        // Track and show league tier rank up automatically when LP increases and crosses a boundary
        val successState = uiState as? LeaguesUiState.Success
        if (successState != null) {
            val points = successState.userLP
            val currentTier = when {
                points < 200 -> "BRONZE"
                points < 600 -> "SILVER"
                points < 1200 -> "GOLD"
                else -> "SHADOW"
            }

            LaunchedEffect(points) {
                val prev = previousTier
                if (prev != null && prev != currentTier) {
                    val tierValues = mapOf("BRONZE" to 1, "SILVER" to 2, "GOLD" to 3, "SHADOW" to 4)
                    val oldVal = tierValues[prev] ?: 1
                    val newVal = tierValues[currentTier] ?: 1
                    if (newVal > oldVal) {
                        rankUpOldTier = when (prev) {
                            "BRONZE" -> "BRONZE SHIELD PROTOCOL"
                            "SILVER" -> "SILVER CRITERION DIVISION"
                            "GOLD" -> "GOLD ASCENSION GATEWAY"
                            else -> "SHADOW MONARCH DOMINION (S-RANK)"
                        }
                        rankUpNewTier = when (currentTier) {
                            "BRONZE" -> "BRONZE SHIELD PROTOCOL"
                            "SILVER" -> "SILVER CRITERION DIVISION"
                            "GOLD" -> "GOLD ASCENSION GATEWAY"
                            else -> "SHADOW MONARCH DOMINION (S-RANK)"
                        }
                        showRankUpDialog = true
                    }
                }
                previousTier = currentTier
            }
        }

        when (val state = uiState) {
            is LeaguesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = axiomColors.systemGreen)
                }
            }
            is LeaguesUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Sync Error",
                        tint = PenaltyRed,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.leagues_sync_failure, state.message),
                        fontFamily = JetBrainsMono,
                        fontSize = 14.sp,
                        color = axiomColors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is LeaguesUiState.Success -> {
                val developerForceEnableLeagues = true
                if (developerForceEnableLeagues) {
                    MainLeaguesLayout(
                        state = state,
                        activeMission = activeMission,
                        timerSeconds = timerSeconds,
                        isTimerActive = isTimerActive,
                        isTimerPaused = isTimerPaused,
                        isFastSyncEnabled = isFastSyncEnabled,
                        viewModel = viewModel,
                        onNavigate = onNavigate,
                        axiomColors = axiomColors
                    )
                } else {
                    // Beautiful Theme-friendly "Coming Soon" / Under Construction Screen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, axiomColors.borderFaint, RoundedCornerShape(12.dp))
                                .background(axiomColors.shadowSurface, RoundedCornerShape(12.dp))
                                .padding(24.dp)
                        ) {
                            // Hologram style rotating indicator
                            val infiniteTransition = rememberInfiniteTransition(label = "coming_soon_rotation")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(8000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "rotation"
                            )

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(2.dp, axiomColors.legendaryGold, RoundedCornerShape(36.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassEmpty,
                                    contentDescription = "Under Construction",
                                    tint = axiomColors.legendaryGold,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .rotate(rotation)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = stringResource(R.string.leagues_coming_soon),
                                fontFamily = JetBrainsMono,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = axiomColors.legendaryGold,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.2f)
                                    .height(2.dp)
                                    .background(axiomColors.legendaryGold.copy(alpha = 0.3f))
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(R.string.leagues_coming_soon_desc),
                                fontFamily = Inter,
                                fontSize = 12.sp,
                                color = axiomColors.textSecondary,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            val isPreregistered = state.isPreregistered

                            if (!isPreregistered) {
                                if (preregisterError != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .background(com.axiom.app.ui.theme.PenaltyRed.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .border(1.dp, com.axiom.app.ui.theme.PenaltyRed.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = preregisterError ?: "Registration failed",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = com.axiom.app.ui.theme.PenaltyRed,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.preRegisterForLeagues() },
                                    enabled = !isPreregistering,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPreregistering) axiomColors.legendaryGold.copy(alpha = 0.5f) else axiomColors.legendaryGold,
                                        contentColor = axiomColors.voidBlack
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("leagues_preregister_btn")
                                ) {
                                    if (isPreregistering) {
                                        CircularProgressIndicator(
                                            color = axiomColors.voidBlack,
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(R.string.leagues_preregister_btn),
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(axiomColors.systemGreen.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                        .border(1.dp, axiomColors.systemGreen.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.leagues_preregister_success),
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = axiomColors.systemGreen,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Full Screen Neural Disruption Breach Alarm Overlay (Priority 5)
        AnimatedVisibility(
            visible = isBreachDetected,
            enter = fadeIn() + expandIn(),
            exit = fadeOut() + shrinkOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(axiomColors.voidBlack.copy(alpha = 0.97f))
                    .border(2.dp, PenaltyRed)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "flashing")
                val alertAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(400, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Dangerous,
                        contentDescription = "Disruption Alert",
                        tint = PenaltyRed.copy(alpha = alertAlpha),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (hardModeEnabled) "NEURAL LINK SEVERED" else "PROTOCOL INTERRUPTED",
                        fontFamily = JetBrainsMono,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PenaltyRed,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (hardModeEnabled) {
                            "Protocol failed. −15 LP"
                        } else {
                            "No penalty this time.\nEnable Hard Mode in Settings for the full experience."
                        },
                        fontFamily = Inter,
                        fontSize = 13.sp,
                        color = axiomColors.textPrimary,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Cooldown countdown visual bar
                    Column(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.leagues_restore_progress),
                            fontFamily = JetBrainsMono,
                            fontSize = 8.sp,
                            color = axiomColors.textPrimary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(axiomColors.textPrimary.copy(alpha = 0.1f), RoundedCornerShape(3.dp))
                        ) {
                            val ratio = (5 - countdownSeconds).toFloat() / 5f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio)
                                    .fillMaxHeight()
                                    .background(if (countdownSeconds > 0) PenaltyRed else axiomColors.systemGreen, RoundedCornerShape(3.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .background(PenaltyRed.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(1.dp, PenaltyRed.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = when {
                                hardModeEnabled -> "PENALTY: -15 LP deducted from current league scores."
                                daysSince >= 7 -> "PENALTY: -5 LP deducted from current league scores."
                                else -> "PENALTY: No LP deducted (Training Protocol active)."
                            },
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PenaltyRed
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = { if (countdownSeconds <= 0) viewModel.confirmBreachDismissed() },
                        enabled = countdownSeconds <= 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (countdownSeconds <= 0) PenaltyRed else axiomColors.textPrimary.copy(alpha = 0.1f),
                            contentColor = if (countdownSeconds <= 0) axiomColors.textPrimary else axiomColors.textPrimary.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(50.dp)
                            .testTag("dismiss_breach_btn")
                    ) {
                        Text(
                            text = if (countdownSeconds > 0) {
                                "[ RE-CALIBRATING LINK: ${countdownSeconds}S ]"
                            } else {
                                "[ ACKNOWLEDGE & RE-CALIBRATE ]"
                            },
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Full Screen League Tier Rank Up Ceremony (Priority 4)
        AnimatedVisibility(
            visible = showRankUpDialog,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 1.1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(axiomColors.voidBlack.copy(alpha = 0.95f))
                    .border(2.dp, axiomColors.legendaryGold)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val infiniteRot = rememberInfiniteTransition(label = "rot")
                val rotationAngle by infiniteRot.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(25000, easing = LinearEasing)
                    ),
                    label = "rotation"
                )

                Canvas(modifier = Modifier.size(300.dp)) {
                    drawContext.canvas.save()
                    rotate(rotationAngle) {
                        for (i in 0..7) {
                            val angleRad = Math.toRadians((i * 45).toDouble())
                            val x = (120 * Math.cos(angleRad)).toFloat() + center.x
                            val y = (120 * Math.sin(angleRad)).toFloat() + center.y
                            drawCircle(
                                color = axiomColors.legendaryGold.copy(alpha = 0.3f),
                                radius = 10f,
                                center = androidx.compose.ui.geometry.Offset(x, y)
                            )
                        }
                    }
                    drawContext.canvas.restore()
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.leagues_division_matrix),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = axiomColors.legendaryGold,
                        letterSpacing = 2.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = stringResource(R.string.leagues_rank_ascended),
                        fontFamily = JetBrainsMono,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = axiomColors.textPrimary,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .border(1.dp, axiomColors.textPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .background(axiomColors.textPrimary.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.leagues_prev),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    color = axiomColors.textDim
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = rankUpOldTier.take(12) + "...",
                                fontFamily = JetBrainsMono,
                                fontSize = 9.sp,
                                color = axiomColors.textDim,
                                textAlign = TextAlign.Center
                              )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Rank up",
                            tint = axiomColors.legendaryGold,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .border(2.dp, axiomColors.legendaryGold, RoundedCornerShape(8.dp))
                                    .background(axiomColors.legendaryGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "New rank",
                                    tint = axiomColors.legendaryGold,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = rankUpNewTier,
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = axiomColors.legendaryGold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = stringResource(R.string.league_rank_up_alert),
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = axiomColors.textPrimary,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .background(axiomColors.legendaryGold.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .border(1.dp, axiomColors.legendaryGold.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.leagues_ascension_reward),
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = axiomColors.legendaryGold
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { showRankUpDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = axiomColors.legendaryGold,
                            contentColor = axiomColors.voidBlack
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.leagues_secure_link),
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
