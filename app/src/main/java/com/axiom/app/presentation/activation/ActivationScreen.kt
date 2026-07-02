package com.axiom.app.presentation.activation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationScreen(
    onActivationSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isFa = java.util.Locale.getDefault().language == "fa"

    // Multi-mode configuration (1: Log In, 2: Activation Code)
    var activationMode by remember { mutableStateOf(1) }
    var enteredActivationCode by remember { mutableStateOf("") }
    val codeInteractionSource = remember { MutableInteractionSource() }
    val isCodeFocused by codeInteractionSource.collectIsFocusedAsState()

    // Bouncy animation state for the submit button
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isButtonPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_bounce_scale"
    )

    // Interaction sources for input field glow effects
    val emailInteractionSource = remember { MutableInteractionSource() }
    val isEmailFocused by emailInteractionSource.collectIsFocusedAsState()

    val passwordInteractionSource = remember { MutableInteractionSource() }
    val isPasswordFocused by passwordInteractionSource.collectIsFocusedAsState()

    // Handle navigation callback after successful activation delay
    LaunchedEffect(uiState.isActivated, uiState.isLoading) {
        if (uiState.isActivated && !uiState.isLoading) {
            kotlinx.coroutines.delay(1000)
            onActivationSuccess()
        }
    }

    // Trigger subtle haptic feedback on error occurrence
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(isButtonPressed) {
        if (isButtonPressed) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LocalAxiomColors.current.voidBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "AXIOM",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TextPrimary,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isFa) "درگاه بیداری شناختی هانتر" else "HUNTER AWAKENING COGNITIVE GATEWAY",
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SystemGreen,
                    letterSpacing = 1.5.sp
                )
            }

            // Central console + Input block
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Welcome / Status Dashboard
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .background(ShadowSurface, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, BorderFaint, shape = RoundedCornerShape(8.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = SystemGreen,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isFa) "در حال رمزگشایی کانال‌های امنیتی..." else "DECRYPTING SECURITY CHANNELS...",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SystemGreen,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "اتصال پیوند شناختی تأیید شده به پایگاه داده اکسیوم." else "Connecting authenticated cognitive link to Supabase.",
                                fontFamily = Inter,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (activationMode == 2) {
                                    if (isFa) "پروتکل فعال‌سازی با لایسنس" else "COGNITIVE KEY ACTIVATION"
                                } else {
                                    if (isFa) "ترمینال فعال‌سازی سیستم" else "GATEWAY INITIATION TERMINAL"
                                },
                                fontFamily = JetBrainsMono,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SystemGreen,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (activationMode == 2) {
                                    if (isFa) "کد فعال‌سازی لایسنس خود را وارد نمایید تا دستگاه فوراً ثبت شود."
                                    else "Enter your cognitive license activation key to authorize this device."
                                } else {
                                    if (isFa) "جهت بازیابی اطلاعات و رتبه شکارچی خود وارد شوید"
                                    else "Provide credentials to reconnect with your current hunter rank."
                                },
                                fontFamily = Inter,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Terminal 2-Way Mode Selector Tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(ShadowSurface, shape = RoundedCornerShape(4.dp))
                        .border(1.dp, SystemGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (activationMode == 1) SystemGreen.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable(enabled = !uiState.isLoading && !uiState.isActivated) { 
                                activationMode = 1
                                viewModel.setSignUpMode(false) 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFa) "ورود" else "LOG IN",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activationMode == 1) SystemGreen else TextDim,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .background(if (activationMode == 2) SystemGreen.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable(enabled = !uiState.isLoading && !uiState.isActivated) { 
                                activationMode = 2
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFa) "کد فعال‌سازی" else "ACTIVATION KEY",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activationMode == 2) SystemGreen else TextDim,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Email input field (Always present for identifying stats/codes)
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    textStyle = TextStyle(fontFamily = Inter, fontSize = 16.sp, color = TextPrimary),
                    label = { 
                        Text(
                            text = if (isFa) "ایمیل شکارچی" else "Hunter Email", 
                            fontFamily = Inter,
                            fontSize = 13.sp
                        ) 
                    },
                    placeholder = { Text("hunter@axiom.com", fontFamily = Inter, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = "Email", tint = SystemGreen) },
                    singleLine = true,
                    enabled = !uiState.isLoading && !uiState.isActivated,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = if (activationMode == 2) ImeAction.Done else ImeAction.Next
                    ),
                    interactionSource = emailInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 58.dp)
                        .glowOnFocus(isEmailFocused)
                        .testTag("activation_email_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SystemGreen,
                        unfocusedBorderColor = TextDim.copy(alpha = 0.5f),
                        focusedLabelColor = SystemGreen,
                        unfocusedLabelColor = TextDim
                    )
                )

                if (activationMode == 2) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Activation Code input field
                    OutlinedTextField(
                        value = enteredActivationCode,
                        onValueChange = { enteredActivationCode = it },
                        textStyle = TextStyle(fontFamily = JetBrainsMono, fontSize = 16.sp, color = TextPrimary),
                        label = { 
                            Text(
                                text = if (isFa) "کد فعال‌سازی" else "Activation Code", 
                                fontFamily = Inter,
                                fontSize = 13.sp
                            ) 
                        },
                        placeholder = { Text("AWAKEN-XXXX-XXXX", fontFamily = JetBrainsMono, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Activation Code", tint = SystemGreen) },
                        singleLine = true,
                        enabled = !uiState.isLoading && !uiState.isActivated,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        interactionSource = codeInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 58.dp)
                            .glowOnFocus(isCodeFocused)
                            .testTag("activation_code_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SystemGreen,
                            unfocusedBorderColor = TextDim.copy(alpha = 0.5f),
                            focusedLabelColor = SystemGreen,
                            unfocusedLabelColor = TextDim
                        )
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Password input field
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        textStyle = TextStyle(fontFamily = Inter, fontSize = 16.sp, color = TextPrimary),
                        label = { Text(if (isFa) "رمز عبور سیستم" else "System Password", fontFamily = Inter, fontSize = 13.sp) },
                        placeholder = { Text("••••••••", fontFamily = Inter, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Password", tint = SystemGreen) },
                        trailingIcon = {
                            val image = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (isPasswordVisible) "Hide password" else "Show password"

                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = description, tint = SystemGreen)
                            }
                        },
                        singleLine = true,
                        enabled = !uiState.isLoading && !uiState.isActivated,
                        visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        interactionSource = passwordInteractionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 58.dp)
                            .glowOnFocus(isPasswordFocused)
                            .testTag("activation_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SystemGreen,
                            unfocusedBorderColor = TextDim.copy(alpha = 0.5f),
                            focusedLabelColor = SystemGreen,
                            unfocusedLabelColor = TextDim
                        )
                    )
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error info",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontFamily = Inter
                        )
                    }
                }
            }

            // Action Button triggers
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isSubmitEnabled = if (activationMode == 2) {
                    !uiState.isLoading && !uiState.isActivated && enteredActivationCode.isNotBlank()
                } else {
                    !uiState.isLoading && !uiState.isActivated && 
                        uiState.email.isNotBlank() && uiState.password.isNotBlank()
                }

                Button(
                    onClick = { 
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (activationMode == 2) {
                            viewModel.triggerActivationWithCode(
                                code = enteredActivationCode,
                                email = uiState.email
                            )
                        } else {
                            viewModel.triggerActivation() 
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SystemGreen,
                        contentColor = VoidBlack
                    ),
                    shape = RoundedCornerShape(6.dp),
                    enabled = isSubmitEnabled,
                    interactionSource = buttonInteractionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .graphicsLayer(
                            scaleX = buttonScale,
                            scaleY = buttonScale
                        )
                        .testTag("activation_submit_button")
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = VoidBlack,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (uiState.isActivated) {
                                if (isFa) "ارتباط شناختی برقرار شد" else "COGNITIVE AWAKENING GRANTED"
                            } else if (activationMode == 2) {
                                if (isFa) "فعال‌سازی پروتکل" else "ACTIVATE PROTOCOL"
                            } else {
                                if (isFa) "ورود به سیستم" else "RE-AWAKEN SOUL"
                            },
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Secondary instructions
                Text(
                    text = if (isFa) "درگاه اکسیوم برای ثبت لول و مأموریت‌ها به ورود امن نیاز دارد. در صورت عدم تنظیم پایگاه داده، حالت شبیه‌ساز فعال خواهد شد."
                    else "Axiom gateway requires secure authentication to index and record your level progress. If Supabase is unconfigured, debug mode allows live simulated access.",
                    fontSize = 10.sp,
                    fontFamily = Inter,
                    color = TextDim,
                    lineHeight = 15.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
fun Modifier.glowOnFocus(
    isFocused: Boolean,
    glowColor: Color = SystemGreen,
    borderRadius: Dp = 8.dp
): Modifier {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.15f else 0.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "glow_alpha"
    )
    
    val glowSpread by animateFloatAsState(
        targetValue = if (isFocused) 10.dp.value else 0.dp.value,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "glow_spread"
    )

    if (glowAlpha <= 0f) return this

    return this.drawBehind {
        val maxGlowPadding = glowSpread.dp.toPx()
        val glowLayers = 3
        for (i in 1..glowLayers) {
            val fraction = i.toFloat() / glowLayers
            val padding = maxGlowPadding * fraction
            val alpha = glowAlpha * (1f - fraction * 0.6f)
            drawRoundRect(
                color = glowColor,
                alpha = alpha,
                topLeft = androidx.compose.ui.geometry.Offset(-padding, -padding),
                size = androidx.compose.ui.geometry.Size(size.width + padding * 2, size.height + padding * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(borderRadius.toPx() + padding),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}
