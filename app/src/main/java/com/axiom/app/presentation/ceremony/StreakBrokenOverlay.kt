package com.axiom.app.presentation.ceremony

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import com.axiom.app.ui.components.neonGlow
import com.axiom.app.ui.components.StreakShieldIndicator
import kotlinx.coroutines.delay

private data class PenaltyOption(
    val titleEn: String,
    val titleFa: String,
    val descEn: String,
    val descFa: String,
    val rewardText: String,
    val penaltyTextEn: String,
    val penaltyTextFa: String,
    val xpToAdd: Int
)

@Composable
fun StreakBrokenOverlay(
    lostStreak: Int,
    shieldCount: Int,
    onUseShield: () -> Unit,
    onAcceptPenalty: (title: String, xp: Int) -> Unit,
    onDismissAndNavigateToMissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(1) }
    var selectedOptionIndex by remember { mutableStateOf(0) }

    var overlayAlphaState by remember { mutableStateOf(0f) }
    var contentAlphaState by remember { mutableStateOf(0f) }

    val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"

    val penaltyOptions = remember {
        listOf(
            PenaltyOption(
                titleEn = "Protocol 404: System Calibration",
                titleFa = "[ پروتکل ۴۰۴: کالیبره‌سازی زیرآرایه‌ها ]",
                descEn = "Perform manual calibration logs on memory buffers and review system indices.",
                descFa = "بررسی پارامترهای توجه و هم‌ترازی مجدد ریجسترهای حافظه سیستم برای هم‌گام‌سازی مجدد جریان اطلاعات.",
                rewardText = "+10 XP",
                penaltyTextEn = "Negative temporal lag added temporarily.",
                penaltyTextFa = "جریمه داستانی: افزوده شدن تاخیر زمانی موقت در گزارش تحلیل‌ها.",
                xpToAdd = 10
            ),
            PenaltyOption(
                titleEn = "Gravity Chamber: 5G Discipline",
                titleFa = "[ اتاق گرانش: شرطی‌سازی توان منفی ]",
                descEn = "Perform 100 physical repetitions or a high-resistance deep focus cycle.",
                descFa = "جبران نقض پروتکل دوره گذشته با انجام ۱۰۰ تکرار فیزیکی یا جلسه تمرکز عمیق با مقاومت شبیه‌سازی شده.",
                rewardText = "+15 XP",
                penaltyTextEn = "High pressure conditioning. Temporary performance reduction.",
                penaltyTextFa = "جریمه داستانی: فشار جاذبه مصنوعی سنگین بر نرخ سرعت رشد اولیه.",
                xpToAdd = 15
            ),
            PenaltyOption(
                titleEn = "Data Purification: Chrono-Logs",
                titleFa = "[ پاکسازی داده‌ها: بازرسی بسته‌های زمانی ]",
                descEn = "Manually extract code fragments from corrupted telemetry records.",
                descFa = "کاوش در قطعات زمانی از دست رفته روزهای گذشته برای استخراج و بازسازی فاز کدهای معیوب تله‌متری.",
                rewardText = "+5 XP",
                penaltyTextEn = "Requires tedious static telemetry checks.",
                penaltyTextFa = "جریمه داستانی: نیاز به بررسی‌های خسته‌کننده تله‌متری ایستا.",
                xpToAdd = 5
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "streak_warning_blink")
    val warningBlinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    // Auto-advance logic: Screen 1 -> Screen 2 -> Screen 3 (if no shield)
    LaunchedEffect(Unit) {
        animate(0f, 0.98f, animationSpec = tween(300)) { v, _ ->
            overlayAlphaState = v
        }
        animate(0f, 1f, animationSpec = tween(400)) { v, _ ->
            contentAlphaState = v
        }

        // 1.5 seconds on Screen 1
        delay(1500)
        if (currentScreen == 1) {
            currentScreen = 2
        }

        // 1.5 seconds on Screen 2
        delay(1500)
        // If they don't have a shield, auto-advance to Screen 3. If they have one, pause to let them choose.
        if (currentScreen == 2 && shieldCount <= 0) {
            currentScreen = 3
        }
    }

    val advanceScreen = {
        if (currentScreen < 3) {
            currentScreen++
        }
    }

    val colors = LocalAxiomColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack.copy(alpha = overlayAlphaState))
            .clickable { advanceScreen() }
            .testTag("streak_broken_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .alpha(contentAlphaState)
        ) {
            when (currentScreen) {
                1 -> {
                    // SCREEN 1: THE CRACKED LOSS
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "⚡ SYSTEM BREACH DETECTED ⚡",
                        style = SystemMsg.copy(
                            color = colors.penaltyRed,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier.alpha(warningBlinkAlpha)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "💔🔥",
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.neonGlow(colors.penaltyRed, intensity = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "STREAK BROKEN",
                        style = DisplayL.copy(
                            color = colors.penaltyRed,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.neonGlow(colors.penaltyRed, intensity = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$lostStreak",
                        style = HudXL.copy(
                            color = colors.penaltyRed,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 80.sp
                        ),
                        modifier = Modifier.neonGlow(colors.penaltyRed, intensity = 0.2f)
                    )

                    Text(
                        text = "DAYS LOST",
                        style = HudS.copy(color = colors.textSecondary),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "TAP TO SCAN FOR MITIGATION STATUS",
                        style = SystemMsg.copy(color = colors.textDim),
                        modifier = Modifier.alpha(warningBlinkAlpha)
                    )
                }

                2 -> {
                    // SCREEN 2: SHIELD INVENTORY & MITIGATION OPTIONS
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "SCANNING SYSTEM REGISTER...",
                        style = SystemMsg.copy(
                            color = if (shieldCount > 0) colors.systemGlint else colors.commonGray,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.alpha(warningBlinkAlpha)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "SHIELD AVAILABLE?",
                        style = DisplayL.copy(
                            color = if (shieldCount > 0) colors.systemGlint else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hexagonal Indicator Badge
                    StreakShieldIndicator(
                        shieldCount = shieldCount,
                        modifier = Modifier
                            .background(colors.shadowSurface, RoundedCornerShape(8.dp))
                            .border(1.dp, if (shieldCount > 0) colors.systemGlint.copy(alpha = 0.25f) else colors.borderFaint, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (shieldCount > 0) {
                        Text(
                            text = "A STREAK SHIELD CAN PREVENT LOSS.\nDO YOU WISH TO ACTIVATE IT NOW?",
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Button(
                            onClick = { onUseShield() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.systemGlint,
                                contentColor = colors.voidBlack
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .neonGlow(colors.systemGlint, intensity = 0.3f)
                        ) {
                            Text(
                                text = "YES, ACTIVATE SHIELD",
                                style = HudS.copy(fontWeight = FontWeight.Bold, color = colors.voidBlack)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { currentScreen = 3 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "NO, ACCEPT PROTOCOL RESET",
                                style = HudS.copy(color = colors.textDim)
                            )
                        }
                    } else {
                        Text(
                            text = "NO COGNITIVE SHIELDS DETECTED.\nTHE CHRONO-LOGS MUST RESET.",
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            color = colors.commonGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        Button(
                            onClick = { currentScreen = 3 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.dimSurface,
                                contentColor = colors.textPrimary
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "PROCEED TO PROTOCOL CALIBRATION",
                                style = HudS.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                3 -> {
                    // SCREEN 3: CALIBRATION DISCIPLINE & EMOTIONAL RECOVERY
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "WARRIOR, YOU RESTART FROM DAY 1.\nBUT YOUR STRENGTH REMAINS.",
                        fontFamily = Fraunces,
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "REBUILD YOUR MOMENTUM BY ACCEPTING A SYSTEM CALIBRATION PROTOCOL:",
                        style = HudS.copy(color = colors.textSecondary, fontSize = 10.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mission Cards Container
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        penaltyOptions.forEachIndexed { index, option ->
                            val isSelected = selectedOptionIndex == index
                            val cardBorderColor = if (isSelected) colors.penaltyRed else colors.borderFaint
                            val cardBg = if (isSelected) colors.penaltyRed.copy(alpha = 0.12f) else colors.shadowSurface

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardBg, RoundedCornerShape(4.dp))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.5.dp,
                                        color = cardBorderColor,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable { selectedOptionIndex = index }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isFa) option.titleFa else option.titleEn,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        color = if (isSelected) colors.penaltyRed else colors.textPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(colors.penaltyRed.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                                            .border(0.5.dp, colors.penaltyRed, RoundedCornerShape(2.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = option.rewardText,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 9.sp,
                                            color = colors.penaltyRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                if (isFa) {
                                    Text(
                                        text = option.titleEn,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 9.sp,
                                        color = colors.penaltyRed.copy(alpha = 0.7f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isFa) option.descFa else option.descEn,
                                    fontFamily = Inter,
                                    fontSize = 11.sp,
                                    color = colors.textSecondary,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚠ " + (if (isFa) option.penaltyTextFa else option.penaltyTextEn),
                                    fontFamily = Inter,
                                    fontSize = 9.sp,
                                    color = colors.textDim,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Button(
                        onClick = {
                            val opt = penaltyOptions[selectedOptionIndex]
                            onAcceptPenalty(opt.titleEn, opt.xpToAdd)
                            onDismissAndNavigateToMissions()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.penaltyRed,
                            contentColor = colors.voidBlack
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .neonGlow(colors.penaltyRed, intensity = 0.3f)
                    ) {
                        Text(
                            text = if (isFa) "پذیرش جریمه و کالیبراسیون" else "ACCEPT CALIBRATION & RESTART",
                            style = HudS.copy(fontWeight = FontWeight.Bold, color = colors.voidBlack)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = onDismissAndNavigateToMissions,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isFa) "بستن و نادیده گرفتن" else "CLOSE & DISMISS",
                            style = HudS.copy(color = colors.textDim)
                        )
                    }
                }
            }
        }
    }
}
