package com.axiom.app.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.axiom.app.data.SeedDataHelper
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.usecase.InitializeAxiomUseCase
import com.axiom.app.ui.components.*
import com.axiom.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val initializeAxiomUseCase: InitializeAxiomUseCase,
    private val seedDataHelper: SeedDataHelper,
    private val preferences: AxiomPreferences
) : ViewModel() {
    suspend fun beginAwakening(name: String) {
        initializeAxiomUseCase(customName = name.trim())
        seedDataHelper.seedSkillsIfNeeded()
        seedDataHelper.seedMuscleGroupsIfNeeded()
        preferences.setStreak(0)
    }
}

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val isFa = java.util.Locale.getDefault().language == "fa"
    val coroutineScope = rememberCoroutineScope()
    var nameState by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showErrorBlink by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(1) }

    // Red error warning blink
    val errorTransition = rememberInfiniteTransition(label = "error_blink")
    val errorBlinkAlpha by errorTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "error_alpha"
    )

    // Reset/Setup typing animation for step 1
    var displayedText1 by remember { mutableStateOf("") }
    var displayedText2 by remember { mutableStateOf("") }
    var displayedText3 by remember { mutableStateOf("") }
    var colorIndex by remember { mutableStateOf(0) }
    var showContinue1 by remember { mutableStateOf(false) }

    LaunchedEffect(currentStep) {
        if (currentStep == 1) {
            displayedText1 = ""
            displayedText2 = ""
            displayedText3 = ""
            showContinue1 = false
            
            val t1 = if (isFa) "[ پیام سیستم ]" else "[ SYSTEM MESSAGE ]"
            for (i in 0..t1.length) {
                displayedText1 = t1.substring(0, i)
                delay(50)
            }
            delay(1500)
            val t2 = if (isFa) "موجود ناشناس شناسایی شد" else "UNKNOWN ENTITY DETECTED"
            for (i in 0..t2.length) {
                displayedText2 = t2.substring(0, i)
                delay(50)
            }
            delay(2000)
            val t3 = if (isFa) "در حال اجرای پروتکل بیداری..." else "INITIATING AWAKENING PROTOCOL..."
            for (i in 0..t3.length) {
                displayedText3 = t3.substring(0, i)
                delay(50)
            }
        }
    }

    LaunchedEffect(currentStep) {
        if (currentStep == 1) {
            colorIndex = 0
            while (true) {
                delay(2000)
                colorIndex = (colorIndex + 1) % 4
            }
        }
    }

    LaunchedEffect(currentStep) {
        if (currentStep == 1) {
            delay(4000)
            showContinue1 = true
        }
    }

    // Step 3 animation states
    var displayedAwakenedName by remember { mutableStateOf("") }
    var showRankGlyph by remember { mutableStateOf(false) }
    var progressValue by remember { mutableStateOf(0f) }
    var countdownSecs by remember { mutableStateOf(3) }
    var showBeginProtocolButton by remember { mutableStateOf(false) }

    LaunchedEffect(currentStep) {
        if (currentStep == 3) {
            displayedAwakenedName = ""
            showRankGlyph = false
            progressValue = 0f
            countdownSecs = 3
            showBeginProtocolButton = false

            delay(500)
            
            val nameToType = nameState.trim()
            for (i in 0..nameToType.length) {
                displayedAwakenedName = nameToType.substring(0, i)
                delay(50)
            }
            delay(1000)
            
            showRankGlyph = true
            delay(1000)
            
            // Progress animation from 0% to 3%
            val duration = 1000L
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < duration) {
                val elapsed = System.currentTimeMillis() - startTime
                val fraction = elapsed.toFloat() / duration
                progressValue = fraction * 0.03f
                delay(16)
            }
            progressValue = 0.03f
            delay(1000)
            
            showBeginProtocolButton = true
            while (countdownSecs > 0) {
                delay(1000)
                countdownSecs--
            }
            
            // Auto complete awakening
            if (!isSubmitting) {
                isSubmitting = true
                viewModel.beginAwakening(nameState)
                onNavigateToHome()
            }
        }
    }

    val colors = listOf(CommonGray, RareBlue, EpicPurple, LegendaryGold)
    val targetColor = colors[colorIndex]
    val glyphColor by animateColorAsState(targetColor, animationSpec = tween(1000), label = "glyph_color")

    val glyphPulseTransition = rememberInfiniteTransition(label = "step1_glyph_pulse")
    val glyphScale by glyphPulseTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glyph_pulse_scale"
    )
    val glyphAlpha by glyphPulseTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glyph_pulse_alpha"
    )

    fun handleSubmit() {
        if (nameState.trim().isEmpty()) {
            coroutineScope.launch {
                showErrorBlink = true
                delay(1500)
                showErrorBlink = false
            }
            return
        }

        if (isSubmitting) return
        isSubmitting = true

        coroutineScope.launch {
            viewModel.beginAwakening(nameState)
            onNavigateToHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack)
            .testTag("onboarding_screen"),
        contentAlignment = Alignment.Center
    ) {
        if (currentStep == 1 || currentStep == 2) {
            // Background Layer 1: Stars Field
            VoidParticleField(modifier = Modifier.fillMaxSize())
            
            // Background Layer 2: Hex patterns & Animated scanline overlay
            AnimatedScanlineOverlay(modifier = Modifier.fillMaxSize())
        }

        when (currentStep) {
            1 -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1.2f))

                    // Text line 1: System Message
                    Text(
                        text = displayedText1,
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(24.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text line 2: Unknown entity detected
                    Text(
                        text = displayedText2,
                        fontFamily = Inter,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(72.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text line 3: Protocol initiation
                    Text(
                        text = displayedText3,
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextDim,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(20.dp)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Pulse glyph
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .alpha(glyphAlpha)
                            .scale(glyphScale),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⬡",
                            fontFamily = JetBrainsMono,
                            fontSize = 72.sp,
                            color = glyphColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.weight(1.5f))

                    // Continue button
                    AnimatedVisibility(
                        visible = showContinue1,
                        enter = fadeIn(animationSpec = tween(800)),
                        exit = fadeOut(animationSpec = tween(500)),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        HolographicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable { currentStep = 2 },
                            accentColor = SystemGreen,
                            glowEnabled = true
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.onboarding_continue),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SystemGreen,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            2 -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.onboarding_register_identity),
                        fontFamily = JetBrainsMono,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen,
                        letterSpacing = 2.sp,
                        modifier = Modifier.testTag("onboarding_header")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.onboarding_identify_desc),
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = TextDim,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Custom Styled TextField
                    var isFocused by remember { mutableStateOf(false) }
                    val borderThickness by animateDpAsState(if (isFocused) 2.dp else 1.dp, label = "border_thickness")
                    val borderColor by animateColorAsState(if (isFocused) SystemGreen else BorderFaint, label = "border_color")

                    Column(
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_hunter_designation),
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDim,
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(DimSurface, RoundedCornerShape(6.dp))
                                .let {
                                    if (isFocused) {
                                        it.rarityGlowPulse(idColor = SystemGreen, enabled = true)
                                    } else {
                                        it
                                    }
                                }
                                .border(
                                    width = borderThickness,
                                    color = borderColor,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = nameState,
                                onValueChange = { if (it.length <= 16) nameState = it },
                                textStyle = TextStyle(
                                    fontFamily = JetBrainsMono,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                ),
                                cursorBrush = SolidColor(SystemGreen),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (nameState.trim().isNotEmpty()) {
                                        currentStep = 3
                                    } else {
                                        handleSubmit()
                                    }
                                }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .testTag("username_input"),
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (nameState.isEmpty()) {
                                            Text(
                                                text = stringResource(R.string.onboarding_enter_code_name),
                                                fontFamily = JetBrainsMono,
                                                fontSize = 18.sp,
                                                color = TextDim.copy(alpha = 0.5f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.onboarding_legend_begins),
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            color = TextDim,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Error warning
                    Box(modifier = Modifier.height(24.dp)) {
                        if (showErrorBlink) {
                            Text(
                                text = stringResource(R.string.onboarding_error_unique),
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = PenaltyRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .alpha(errorBlinkAlpha)
                                    .testTag("onboarding_error")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Awakening Button
                    val submitInteractionSource = remember { MutableInteractionSource() }
                    val isSubmitPressed by submitInteractionSource.collectIsPressedAsState()
                    val submitScale by animateFloatAsState(
                        targetValue = if (isSubmitPressed) 0.96f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "press_scale"
                    )

                    Button(
                        onClick = {
                            if (nameState.trim().isNotEmpty()) {
                                currentStep = 3
                            } else {
                                handleSubmit()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = SystemGreen
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SystemGreen),
                        shape = RoundedCornerShape(4.dp),
                        interactionSource = submitInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(48.dp)
                            .scale(submitScale)
                            .testTag("submit_button")
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_begin_awakening),
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            3 -> {
                // Step 3 UI (Pitch black background in place of star fields to represent solid black void ceremonies)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VoidBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        // Typed identity name
                        Text(
                            text = displayedAwakenedName,
                            fontFamily = JetBrainsMono,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Rank info & Progress Bar
                        AnimatedVisibility(
                            visible = showRankGlyph,
                            enter = fadeIn(animationSpec = tween(1000)) + expandVertically(animationSpec = tween(1000)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "E — ◆",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CommonGray,
                                    letterSpacing = 3.sp
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = stringResource(R.string.onboarding_e_rank),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CommonGray.copy(alpha = 0.8f),
                                    letterSpacing = 1.5.sp
                                )

                                Spacer(modifier = Modifier.height(40.dp))

                                // Progress bar to represent starting alignment XP
                                CyberProgressBar(
                                    progress = progressValue,
                                    color = SystemGreen,
                                    trackColor = BorderFaint,
                                    modifier = Modifier.fillMaxWidth(0.8f),
                                    showGlow = true,
                                    animated = false // Animated manually in LaunchedEffect
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(R.string.onboarding_awakening_progress, (progressValue * 100).toInt()),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    color = TextDim,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Countdown action button
                        AnimatedVisibility(
                            visible = showBeginProtocolButton,
                            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(500)
                            )
                        ) {
                            Button(
                                onClick = {
                                    if (!isSubmitting) {
                                        isSubmitting = true
                                        coroutineScope.launch {
                                            viewModel.beginAwakening(nameState)
                                            onNavigateToHome()
                                        }
                                    }
                                },
                                enabled = !isSubmitting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = SystemGreen
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SystemGreen),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = if (isSubmitting) {
                                        if (isFa) "[ در حال تدوین پروتکل... ]" else "[ COMPILING PROTOCOL... ]"
                                    } else {
                                        if (isFa) "[ شروع پروتکل ($countdownSecs) ]" else "[ BEGIN PROTOCOL ($countdownSecs) ]"
                                    },
                                    fontFamily = JetBrainsMono,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 2.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}
