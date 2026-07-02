package com.axiom.app.ui.components.xion

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.axiom.app.domain.model.WarriorPersona
import com.axiom.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XionInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isChatLoading: Boolean,
    selectedPersona: WarriorPersona,
    onPersonaSelected: (WarriorPersona) -> Unit,
    systemColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalAxiomColors.current
    var showPersonaSheet by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    
    // Pulse animation for recording state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse"
    )

    // Speech Recognizer setup
    val speechRecognizer = remember {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                SpeechRecognizer.createSpeechRecognizer(context)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    val recognitionListener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                val msg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Voice not available"
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val speechText = matches[0]
                    if (value.isBlank()) {
                        onValueChange(speechText)
                    } else {
                        onValueChange("$value $speechText")
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    LaunchedEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(recognitionListener)
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (speechRecognizer != null) {
                isListening = true
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }
                try {
                    speechRecognizer.startListening(intent)
                } catch (e: Exception) {
                    isListening = false
                    Toast.makeText(context, "Voice not available", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Voice not available", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Audio Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun startVoiceInput() {
        if (isListening) {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                // ignore
            }
            isListening = false
        } else {
            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                if (speechRecognizer != null) {
                    isListening = true
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    }
                    try {
                        speechRecognizer.startListening(intent)
                    } catch (e: Exception) {
                        isListening = false
                        Toast.makeText(context, "Voice not available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Voice not available", Toast.LENGTH_SHORT).show()
                }
            } else {
                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Main input container row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Input Button (pulse red when recording)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isListening) PenaltyRed.copy(alpha = 0.25f) else colors.dimSurface)
                    .border(1.dp, if (isListening) PenaltyRed else colors.borderFaint, CircleShape)
                    .clickable { startVoiceInput() }
                    .scale(if (isListening) pulseScale else 1f)
                    .testTag("voice_input_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListening) PenaltyRed else systemColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Input TextField
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontFamily = FiraCode, fontSize = 11.sp, color = colors.textPrimary),
                cursorBrush = SolidColor(systemColor),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (value.isNotBlank() && !isChatLoading) {
                        onSend()
                    }
                }),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.dimSurface)
                    .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .testTag("chat_text_input"),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            text = "Transmit system command...",
                            fontFamily = FiraCode,
                            fontSize = 11.sp,
                            color = colors.textDim
                        )
                    }
                    inner()
                }
            )

            // Send Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (value.isNotBlank() && !isChatLoading) systemColor.copy(alpha = 0.2f) else colors.shadowSurface)
                    .border(1.dp, if (value.isNotBlank() && !isChatLoading) systemColor else colors.borderFaint, CircleShape)
                    .clickable(enabled = value.isNotBlank() && !isChatLoading) {
                        onSend()
                    }
                    .testTag("send_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (value.isNotBlank() && !isChatLoading) systemColor else colors.textDim,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Persona indicator row (pill tag)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.epicPurple.copy(alpha = 0.15f))
                    .border(1.dp, colors.epicPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { showPersonaSheet = true }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("persona_pill_tag"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Persona",
                        tint = colors.epicPurple,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = selectedPersona.personaName.uppercase(),
                        fontFamily = FiraCode,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.epicPurple
                    )
                }
            }
        }
    }

    // Persona Selection Sheet (ModalBottomSheet)
    if (showPersonaSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPersonaSheet = false },
            containerColor = colors.voidBlack,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(colors.borderFaint)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "SELECT OPERATIONS CO-PILOT",
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colors.epicPurple,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.borderFaint)
                        .padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(WarriorPersona.values()) { persona ->
                        val isSelected = persona == selectedPersona
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) colors.epicPurple.copy(alpha = 0.15f) else Color.Transparent)
                                .border(1.dp, if (isSelected) colors.epicPurple else colors.borderFaint, RoundedCornerShape(8.dp))
                                .clickable {
                                    onPersonaSelected(persona)
                                    showPersonaSheet = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = persona.personaName.uppercase(),
                                    fontFamily = FiraCode,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isSelected) colors.epicPurple else colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = persona.role,
                                    fontFamily = FiraCode,
                                    fontSize = 9.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
