package com.axiom.app.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import com.axiom.app.ui.FirstMissionViewModel
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.components.VoidParticleField
import com.axiom.app.ui.components.XPFloatAnimation
import com.axiom.app.ui.theme.*

@Composable
fun FirstMissionScreen(
    onMissionCreatedAndCompleted: () -> Unit,
    viewModel: FirstMissionViewModel = hiltViewModel()
) {
    val done by viewModel.done.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf("") }
    val isButtonEnabled = title.trim().length >= 3 && !loading

    LaunchedEffect(done) {
        if (done) {
            onMissionCreatedAndCompleted()
        }
    }

    val c = LocalAxiomColors.current
    val isFa = java.util.Locale.getDefault().language == "fa"
    var showTutorial by remember { mutableStateOf(true) }
    var tutorialStep by remember { mutableStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(c.voidBlack),
        contentAlignment = Alignment.Center
    ) {
        // Ambient particle background
        VoidParticleField(modifier = Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.l),
            modifier = Modifier
                .padding(Spacing.xl)
                .fillMaxWidth()
        ) {
            // Header protocol with info/help button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.first_mission_header),
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    color = c.systemGreen,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "[ ? ]",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = c.textDim,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            tutorialStep = 1
                            showTutorial = true
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Body text
            Text(
                text = stringResource(R.string.first_mission_title),
                fontFamily = Inter,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = c.textPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.first_mission_desc),
                fontFamily = Inter,
                fontSize = 15.sp,
                color = c.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            // Text Field for first mission title
            TerminalTextField(
                value = title,
                onValueChange = { title = it },
                label = stringResource(R.string.first_mission_label),
                placeholder = {
                    Text(
                        text = stringResource(R.string.first_mission_placeholder),
                        fontFamily = JetBrainsMono,
                        fontSize = 14.sp,
                        color = c.textDim
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.m))

            // Interactions
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed && isButtonEnabled) 0.96f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "button_scale"
            )

            val buttonBgColor = if (isButtonEnabled) c.systemGreen else c.borderFaint

            Box(
                modifier = Modifier
                    .scale(scale)
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(buttonBgColor)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = isButtonEnabled
                    ) {
                        viewModel.createAndStart(title.trim())
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.first_mission_btn_open),
                    fontFamily = JetBrainsMono,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isButtonEnabled) c.voidBlack else c.textDim
                )
            }
        }

        // Animated lightweight Tutorial Overlay
        if (showTutorial) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(c.voidBlack.copy(alpha = 0.82f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // Absorb taps on background to prevent interacting with controls behind
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(c.dimSurface)
                        .border(1.dp, c.systemGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header progress indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isFa) "[ پروتکل بیداری ]" else "[ AWAKENING TUTORIAL ]",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = c.systemGreen
                        )
                        
                        Text(
                            text = "$tutorialStep / 3",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = c.textDim
                        )
                    }

                    // Content Title
                    Text(
                        text = when (tutorialStep) {
                            1 -> if (isFa) "اولین مأموریت شما" else "REGISTER YOUR FIRST QUEST"
                            2 -> if (isFa) "شاخص بیداری چیست؟" else "THE AWAKENING INDEX (AI)"
                            else -> if (isFa) "دروازه شناختی را بگشایید" else "UNLEASH THE COGNITIVE LINK"
                        },
                        fontFamily = Inter,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    // Step description
                    Text(
                        text = when (tutorialStep) {
                            1 -> if (isFa) {
                                "خوش آمدی هانتر. بیداری تو نیازمند ایجاد اولین پیوند شناختی در دنیای فیزیکی است. برای شروع، باید یک کوئست یا هدف واقعی برای امروز خود انتخاب کنی."
                            } else {
                                "Greetings, Hunter. Your journey of self-mastery begins here. To unlock your full cognitive potential, you must register a real-world quest."
                            }
                            2 -> if (isFa) {
                                "شاخص بیداری (Awakening Index)، پایداری، میزان تمرکز و نرخ موفقیتهای مأموریت‌های روزانه تو را در قالب الگو‌های هوش بیداری به شکل نمودار ارزیابی میکند تا پیوند ذهن و کار را هماهنگ نگاه دارد."
                            } else {
                                "The Awakening Index calculates your daily completion rates, focus protocols, and total consistency. Improving this Index levels up your cognitive rank in real time."
                            }
                            else -> if (isFa) {
                                "هدف امروز خود را بنویس (حداقل ۳ کاراکتر؛ مثلاً '۳۰ دقیقه یادگیری جدید'). با کلیک روی 'باز کردن دروازه'، سیستم بلافاصله مأموریت را ثبت و تکمیل کرده و اولین پاداش XP واقعی را فعال می‌کند!"
                            } else {
                                "Write down one task you will accomplish today (minimum 3 characters, e.g. 'Read 5 pages') and tap OPEN THE GATE. The SYSTEM will immediately compile it, awarding you real XP!"
                            }
                        },
                        fontFamily = Inter,
                        fontSize = 13.sp,
                        color = c.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    // Cyber Punk step diamonds
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..3).forEach { step ->
                            val isActive = step == tutorialStep
                            Text(
                                text = if (isActive) "◆" else "◇",
                                fontFamily = JetBrainsMono,
                                fontSize = 14.sp,
                                color = if (isActive) c.systemGreen else c.textDim
                            )
                        }
                    }

                    // Lower Nav Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Skip text button
                        Text(
                            text = if (isFa) "[ رد کردن ]" else "[ SKIP ]",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = c.textDim,
                            modifier = Modifier
                                .clickable { showTutorial = false }
                                .padding(8.dp)
                        )

                        // Next button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(c.systemGreen.copy(alpha = 0.15f))
                                .border(1.dp, c.systemGreen, RoundedCornerShape(4.dp))
                                .clickable {
                                    if (tutorialStep < 3) {
                                        tutorialStep++
                                    } else {
                                        showTutorial = false
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (tutorialStep < 3) {
                                    if (isFa) "بعدی ➔" else "NEXT ➔"
                                } else {
                                    if (isFa) "فهمیدم" else "GOT IT"
                                },
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.systemGreen
                            )
                        }
                    }
                }
            }
        }

        // Overlay XPFloatAnimation to give that amazing floating reward feedback
        XPFloatAnimation(
            xpEventFlow = viewModel.xpFloatEvent,
            onAnimationComplete = { viewModel.onXPAnimationComplete() },
            modifier = Modifier.fillMaxSize()
        )
    }
}
