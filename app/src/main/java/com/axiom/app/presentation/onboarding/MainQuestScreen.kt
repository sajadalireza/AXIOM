package com.axiom.app.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.ui.components.ScannerSweep
import com.axiom.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainQuestViewModel @Inject constructor(
    private val hunterRepository: HunterRepository
) : ViewModel() {
    private val _profile = MutableStateFlow<Hunter?>(null)
    val profile: StateFlow<Hunter?> = _profile

    init {
        viewModelScope.launch {
            hunterRepository.getHunterProfile().collect {
                _profile.value = it
            }
        }
    }

    fun savePersonalThesis(quest: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            val current = hunterRepository.getDirectHunterProfile()
            if (current != null) {
                hunterRepository.updateHunterProfile(current.copy(personalThesis = quest.trim()))
            }
            onFinished()
        }
    }
}

@Composable
fun MainQuestScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainQuestViewModel = hiltViewModel()
) {
    var questState by remember { mutableStateOf("") }
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    LaunchedEffect(profile) {
        profile?.let {
            if (questState.isEmpty() && it.personalThesis.isNotEmpty()) {
                questState = it.personalThesis
            }
        }
    }

    var displayedHeader by remember { mutableStateOf("") }
    val fullHeader = "[ DEFINE YOUR LIFE PROTOCOL ]"

    LaunchedEffect(Unit) {
        for (i in 0..fullHeader.length) {
            displayedHeader = fullHeader.substring(0, i)
            delay(50)
        }
    }

    // Interactive button press scale
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

    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack)
            .testTag("main_quest_screen")
    ) {
        // Immersive active scanner sweep overlay
        ScannerSweep(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header typewriter
            Text(
                text = displayedHeader,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = SystemGreen,
                modifier = Modifier.testTag("main_quest_header")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle
            Text(
                text = stringResource(R.string.main_quest_reason),
                fontFamily = Inter,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                modifier = Modifier.testTag("main_quest_subtitle")
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Centered elegant large input box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(ShadowSurface, RoundedCornerShape(6.dp))
                    .border(1.dp, BorderFaint, RoundedCornerShape(6.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = questState,
                    onValueChange = { questState = it },
                    textStyle = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    cursorBrush = SolidColor(SystemGreen),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("main_quest_input"),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (questState.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.main_quest_example),
                                    fontFamily = Inter,
                                    fontSize = 18.sp,
                                    color = TextDim,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Confirm Button
            val isFormValid = questState.trim().isNotEmpty()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isFormValid) LocalAxiomColors.current.voidBlack else LocalAxiomColors.current.shadowSurface)
                    .border(1.dp, if (isFormValid) SystemGreen else BorderFaint, RoundedCornerShape(4.dp))
                    .clickable(
                        enabled = isFormValid,
                        interactionSource = interactionSource,
                        indication = LocalIndication.current
                    ) {
                        viewModel.savePersonalThesis(questState, onFinished)
                    }
                    .testTag("btn_lock_protocol"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.main_quest_lock_in),
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isFormValid) SystemGreen else TextDim
                )
            }
        }
    }
}
