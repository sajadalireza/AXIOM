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

private data class RecoveryOption(
    val titleEn: String,
    val titleFa: String,
    val descEn: String,
    val descFa: String,
    val rewardText: String,
    val recoveryHintEn: String,
    val recoveryHintFa: String,
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

    val recoveryOptions = remember {
        listOf(
            RecoveryOption(
                titleEn = "Momentum Re-anchor: Core Goal Action",
                titleFa = "[ لنگر مجدد شتاب: اقدام محوری هدف ]",
                descEn = "Execute one concrete action advancing your active primary Goal.",
                descFa = "یک اقدام ملموس برای پیشبرد هدف اصلی خود انجام دهید و ریتم خود را بازیابی کنید.",
                rewardText = "+15 XP",
                recoveryHintEn = "Focuses attention on the next immediate win.",
                recoveryHintFa = "تمرکز مجدد روی پیروزی ملموس بعدی.",
                xpToAdd = 15
            ),
            RecoveryOption(
                titleEn = "Deep Focus Re-calibration: 15-min Session",
                titleFa = "[ تنظیم مجدد تمرکز: چرخه ۱۵ دقیقه‌ای ]",
                descEn = "Perform an uninterrupted 15-minute deep focus session on high-priority work.",
                descFa = "یک جلسه تمرکز عمیق ۱۵ دقیقه‌ای بدون حواس‌پرتی روی کار دارای اولویت بالا انجام دهید.",
                rewardText = "+15 XP",
                recoveryHintEn = "Rebuilds working memory and deep work capacity.",
                recoveryHintFa = "بازسازی ظرفیت کار عمیق و حافظه کاری.",
                xpToAdd = 15
            ),
            RecoveryOption(
                titleEn = "Reflection & Alignment: Log Lesson & Intention",
                titleFa = "[ بازاندیشی و هم‌راستایی: ثبت درس و قصد ]",
                descEn = "Record what caused the pause and define one constructive boundary.",
                descFa = "علت وقفه را بدون سرزنش بررسی کرده و یک حد مرزی سازنده برای فردا ثبت کنید.",
                rewardText = "+10 XP",
                recoveryHintEn = "Turns interruption into permanent learning.",
                recoveryHintFa = "تبدیل وقفه به داده و یادگیری ماندگار.",
                xpToAdd = 10
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "streak_warning_blink")
    val warningBlinkAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
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

        delay(1500)
        if (currentScreen == 1) {
            currentScreen = 2
        }

        delay(1500)
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
                    // SCREEN 1: DIGNITY & CADENCE CHECK-IN
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = if (isFa) "⚡ توقف موقت ریتم ⚡" else "⚡ CADENCE INTERRUPTED ⚡",
                        style = SystemMsg.copy(
                            color = colors.statusWarning,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier.alpha(warningBlinkAlpha)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "⏳⬡",
                        fontSize = 72.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.neonGlow(colors.statusWarning, intensity = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = if (isFa) "ریتم متوقف شد" else "RHYTHM PAUSED",
                        style = DisplayL.copy(
                            color = colors.statusWarning,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.neonGlow(colors.statusWarning, intensity = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$lostStreak",
                        style = HudXL.copy(
                            color = colors.statusWarning,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 80.sp
                        ),
                        modifier = Modifier.neonGlow(colors.statusWarning, intensity = 0.2f)
                    )

                    Text(
                        text = if (isFa) "روز پیشرفت ساخته‌شده" else "DAYS ACHIEVED",
                        style = HudS.copy(color = colors.textSecondary),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = if (isFa) "برای بررسی گزینه‌های بازیابی ضربه بزنید" else "TAP TO SCAN FOR RECOVERY STATUS",
                        style = SystemMsg.copy(color = colors.textDim),
                        modifier = Modifier.alpha(warningBlinkAlpha)
                    )
                }

                2 -> {
                    // SCREEN 2: SHIELD INVENTORY & MITIGATION OPTIONS
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = if (isFa) "در حال بررسی وضعیت بازیابی..." else "SCANNING RECOVERY STATUS...",
                        style = SystemMsg.copy(
                            color = if (shieldCount > 0) colors.systemGlint else colors.commonGray,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.alpha(warningBlinkAlpha)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = if (isFa) "سپر شناختی موجود است؟" else "SHIELD AVAILABLE?",
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
                            text = if (isFa) "سپر زنجیره می‌تواند ریتم شما را حفظ کند.\nآیا مایلید آن را فعال کنید؟" else "A STREAK SHIELD CAN PRESERVE YOUR CADENCE.\nDO YOU WISH TO ACTIVATE IT NOW?",
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
                                text = if (isFa) "بله، فعال‌سازی سپر" else "YES, ACTIVATE SHIELD",
                                style = HudS.copy(fontWeight = FontWeight.Bold, color = colors.voidBlack)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { currentScreen = 3 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isFa) "خیر، ورود به پنجره بازیابی" else "NO, PROCEED TO RECOVERY",
                                style = HudS.copy(color = colors.textDim)
                            )
                        }
                    } else {
                        Text(
                            text = if (isFa) "سپری در رجیستر یافت نشد.\nپنجره ۴۸ ساعته بازیابی برای بازسازی ریتم فعال شد." else "NO COGNITIVE SHIELDS DETECTED.\n48-HOUR GRACE RECOVERY WINDOW ACTIVATED.",
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
                                text = if (isFa) "ورود به مأموریت‌های بازیابی" else "PROCEED TO RECOVERY MISSIONS",
                                style = HudS.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                3 -> {
                    // SCREEN 3: MEANINGFUL RECOVERY & EMOTIONAL STRENGTH
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isFa) "مهارت‌ها و دستاوردهای شما دائمی هستند.\nریتم خود را با یک اقدام واقعی بازسازی کنید." else "WARRIOR, YOUR CAPABILITIES ARE PERMANENT.\nRESTORE YOUR CADENCE WITH REAL EFFORT.",
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
                        text = if (isFa) "یک مأموریت بازیابی انتخاب کنید تا ریتم شما بازسازی شود:" else "SELECT A GOAL-ALIGNED RECOVERY MISSION TO RESTORE YOUR CADENCE:",
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
                        recoveryOptions.forEachIndexed { index, option ->
                            val isSelected = selectedOptionIndex == index
                            val cardBorderColor = if (isSelected) colors.systemGlint else colors.borderFaint
                            val cardBg = if (isSelected) colors.systemGlint.copy(alpha = 0.12f) else colors.shadowSurface

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
                                        color = if (isSelected) colors.systemGlint else colors.textPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(colors.systemGlint.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                                            .border(0.5.dp, colors.systemGlint, RoundedCornerShape(2.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = option.rewardText,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 9.sp,
                                            color = colors.systemGlint,
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
                                        color = colors.systemGlint.copy(alpha = 0.7f)
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
                                    text = "⬡ " + (if (isFa) option.recoveryHintFa else option.recoveryHintEn),
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
                            val opt = recoveryOptions[selectedOptionIndex]
                            onAcceptPenalty(opt.titleEn, opt.xpToAdd)
                            onDismissAndNavigateToMissions()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.systemGlint,
                            contentColor = colors.voidBlack
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .neonGlow(colors.systemGlint, intensity = 0.3f)
                    ) {
                        Text(
                            text = if (isFa) "پذیرش مأموریت و بازیابی ریتم" else "ACCEPT RECOVERY MISSION & RESTORE",
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
