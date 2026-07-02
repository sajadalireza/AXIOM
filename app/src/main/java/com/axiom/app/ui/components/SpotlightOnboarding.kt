package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.LocalAxiomColors

@Composable
fun SpotlightOnboarding(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(1) }
    val colors = LocalAxiomColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack.copy(alpha = 0.78f))
            .clickable(enabled = true, onClickLabel = "Skip onboarding step") {
                // Clicking outside or background can also advance or we just keep it click-safe to avoid accidental dismisses.
            }
    ) {
        // --- STEP HIGHLIGHT CIRCLE (Fixed Positions) ---
        val alignment = when (currentStep) {
            1 -> Alignment.TopCenter
            2 -> Alignment.BottomCenter
            else -> Alignment.Center
        }

        val paddingModifier = when (currentStep) {
            1 -> Modifier.padding(top = 100.dp) // Hunter rank/level card area
            2 -> Modifier.padding(bottom = 70.dp) // Bottom nav bar area
            else -> Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(paddingModifier),
            contentAlignment = alignment
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .background(colors.systemGreen.copy(alpha = 0.10f), shape = CircleShape)
                    .border(1.5.dp, colors.systemGreen.copy(alpha = 0.40f), shape = CircleShape)
            )
        }

        // --- ONBOARDING CARD CONTAINER ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp) // Ensure it is above 80dp from bottom
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colors.dimSurface)
                .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                .clickable(enabled = false) {} // Prevent click-through
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isFa = java.util.Locale.getDefault().language == "fa"
            val titleText = when (currentStep) {
                1 -> if (isFa) "[ وضعیت شما ]" else "[ YOUR STATUS ]"
                2 -> if (isFa) "[ مأموریت شما ]" else "[ YOUR MISSION ]"
                else -> if (isFa) "[ شروع پروتکل ]" else "[ BEGIN PROTOCOL ]"
            }

            val bodyText = when (currentStep) {
                1 -> if (isFa) {
                    "رتبه (RANK) و سطح (LEVEL) خود را از اینجا پیگیری کنید. برای ارتقای رتبه از رتبه E به رتبه S، هر روز نسبت به انجام مأموریت‌ها اقدام نمایید."
                } else {
                    "Track your RANK and LEVEL here. Complete missions every day to rank up from E — S."
                }
                2 -> if (isFa) {
                    "مأموریت‌ها (MISSIONS) کارهایی هستند که در زندگی واقعی انجام می‌دهید. تکمیل هر هدف امتیاز فعالیت (XP) به همراه دارد و زنجیره روز‌های متوالی شما را می‌سازد."
                } else {
                    "MISSIONS are tasks you do in real life. Every checked-off task earns XP and builds your streak."
                }
                else -> if (isFa) {
                    "اولین مأموریت خود را همین حالا اضافه کنید. هر کار و هدفی — چه کوچک و چه بزرگ — ارزشمند است. روی علامت + کلیک کنید."
                } else {
                    "Add your first Mission now. Any task — big or small — counts. Tap + on the Missions tab."
                }
            }

            val buttonText = if (currentStep < 3) {
                if (isFa) "[ بعدی ]" else "[ NEXT → ]"
            } else {
                if (isFa) "[ شروع بیداری ]" else "[ BEGIN HUNT ]"
            }

            Text(
                text = titleText,
                color = colors.systemGreen,
                fontFamily = JetBrainsMono,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = bodyText,
                color = colors.textSecondary,
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, colors.systemGreen, RoundedCornerShape(4.dp))
                    .clickable {
                        if (currentStep < 3) {
                            currentStep++
                        } else {
                            onFinish()
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonText,
                    color = colors.systemGreen,
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
