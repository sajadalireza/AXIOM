package com.axiom.app.presentation.home.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.core.sound.AwakenSound
import com.axiom.app.core.sound.SoundEngine
import com.axiom.app.ui.AxiomViewModel
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun FridayReviewSection(
    axiomViewModel: AxiomViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val context = LocalContext.current
    val isFa = java.util.Locale.getDefault().language == "fa"

    val sharedPrefs = remember { context.getSharedPreferences("warrior_settings", Context.MODE_PRIVATE) }
    var reviewActiveStep by remember { mutableStateOf(sharedPrefs.getInt("friday_review_active_step", 0)) }
    val scope = rememberCoroutineScope()

    val isFriday = remember {
        val cal = Calendar.getInstance()
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
    }

    Card(
        modifier = modifier.fillMaxWidth().testTag("friday_review_wizard_card"),
        colors = CardDefaults.cardColors(containerColor = colors.dimSurface),
        border = BorderStroke(
            width = if (isFriday) 2.dp else 1.dp,
            color = if (isFriday) colors.legendaryGold else colors.borderFaint
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFriday) {
                        if (isFa) "◈ پروتکل ارزیابی جمعه (اجباری و فعال)" else "◈ FRIDAY REVIEW ACTIVE (MANDATORY)"
                    } else {
                        if (isFa) "◈ پروتکل ارزیابی جمعه" else "◈ FRIDAY REVIEW PROTOCOL"
                    },
                    fontFamily = FiraCode,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isFriday) colors.legendaryGold else colors.textPrimary,
                    letterSpacing = 1.sp
                )

                if (isFriday) {
                    Box(
                        modifier = Modifier
                            .background(colors.systemGreen.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isFa) "فرمت جمعه" else "FRIDAY PROTOCOL",
                            fontFamily = FiraCode,
                            fontSize = 8.sp,
                            color = colors.systemGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            when (reviewActiveStep) {
                0 -> { // Not started
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isFa) {
                                "پروتکل زمان‌بندی‌شده ممیزی خروجی‌ها و تصحیح مسیر مالی و برنامه‌ریزی هفته بعد."
                            } else {
                                "A strict, offline 3-stage protocol to eliminate self-delusion, auditing weekly outputs and aligning the incoming wealth agenda."
                            },
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )

                        Button(
                            onClick = {
                                reviewActiveStep = 1
                                sharedPrefs.edit().putInt("friday_review_active_step", 1).apply()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFriday) colors.legendaryGold.copy(alpha = 0.2f) else colors.systemGreen.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, if (isFriday) colors.legendaryGold else colors.systemGreen),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(
                                text = if (isFa) "شروع فرآیند ۳ مرحله‌ای ارزیابی" else "INITIALIZE 3-STAGE PROTOCOL",
                                fontFamily = FiraCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFriday) colors.legendaryGold else colors.systemGreen
                            )
                        }
                    }
                }
                1 -> { // Step 1: Output Audit
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isFa) "مرحله ۱ از ۳: ممیزی دقیق خروجی‌ها (Output Audit)" else "STAGE 1 OF 3: Output Audit",
                            fontFamily = FiraCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.systemGreen
                        )
                        Text(
                            text = if (isFa) {
                                "تمام نتایج کار خود را در این هفته با صداقت کامل بسنجید. کارهای بیهوده را یادداشت کرده و حذف کنید تا انکار و خودفریبی کاملا از بین برود."
                            } else {
                                "Critically examine your actual outputs this week. Honestly record non-work hours, strip away all deniability, and audit what actually got shipped."
                            },
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    reviewActiveStep = 0
                                    sharedPrefs.edit().putInt("friday_review_active_step", 0).apply()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = BorderStroke(1.dp, colors.borderFaint),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(if (isFa) "لغو" else "CANCEL", fontFamily = FiraCode, fontSize = 10.sp, color = colors.textSecondary)
                            }

                            Button(
                                onClick = {
                                    reviewActiveStep = 2
                                    sharedPrefs.edit().putInt("friday_review_active_step", 2).apply()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, colors.systemGreen),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text(if (isFa) "تایید و مرحله بعد ▸" else "CONFIRM & NEXT ▸", fontFamily = FiraCode, fontSize = 10.sp, color = colors.systemGreen)
                            }
                        }
                    }
                }
                2 -> { // Step 2: Income Diagnostic
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isFa) "مرحله ۲ از ۳: عارضه‌یابی مالی (Income Diagnostic)" else "STAGE 2 OF 3: Income Diagnostic",
                            fontFamily = FiraCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.systemGreen
                        )
                        Text(
                            text = if (isFa) {
                                "تراز مالی و مبالغ تراکنش‌ها را بررسی نمایید. نشتی‌های مالی، اهرم‌های هدررفت سرمایه و مسیرهای رشد درآمد را مشخص کنید."
                            } else {
                                "Check transaction records and financial balances. Audit financial leaks, analyze capital allocations, and verify passive revenue models."
                            },
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    reviewActiveStep = 1
                                    sharedPrefs.edit().putInt("friday_review_active_step", 1).apply()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = BorderStroke(1.dp, colors.borderFaint),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(if (isFa) "◂ قبل" else "◂ BACK", fontFamily = FiraCode, fontSize = 10.sp, color = colors.textSecondary)
                            }

                            Button(
                                onClick = {
                                    reviewActiveStep = 3
                                    sharedPrefs.edit().putInt("friday_review_active_step", 3).apply()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, colors.systemGreen),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text(if (isFa) "تایید و مرحله بعد ▸" else "CONFIRM & NEXT ▸", fontFamily = FiraCode, fontSize = 10.sp, color = colors.systemGreen)
                            }
                        }
                    }
                }
                3 -> { // Step 3: Next 3 Outcomes
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isFa) "مرحله ۳ از ۳: تعیین اهداف هفته نو (Next 3 Outcomes)" else "STAGE 3 OF 3: Next 3 Outcomes",
                            fontFamily = FiraCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.systemGreen
                        )
                        Text(
                            text = if (isFa) {
                                "سه نتیجه محوری و خارق‌العاده را برای هفته پیش‌رو مشخص و پیش‌نویس کنید. کارهای بیهوده را حذف کرده و روی ارزش‌های واقعی زوم کنید."
                            } else {
                                "Define your 3 high-impact outcomes for the upcoming execution week. Lock your focus and schedule the exact blocks of time needed."
                            },
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    reviewActiveStep = 2
                                    sharedPrefs.edit().putInt("friday_review_active_step", 2).apply()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = BorderStroke(1.dp, colors.borderFaint),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(if (isFa) "◂ قبل" else "◂ BACK", fontFamily = FiraCode, fontSize = 10.sp, color = colors.textSecondary)
                            }

                            Button(
                                onClick = {
                                    reviewActiveStep = 4
                                    sharedPrefs.edit().putInt("friday_review_active_step", 4).apply()
                                    scope.launch {
                                        axiomViewModel.preferences.setLastReviewTimestamp(System.currentTimeMillis())
                                        SoundEngine.play(AwakenSound.SYSTEM_ALERT)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen.copy(alpha = 0.25f)),
                                border = BorderStroke(1.dp, colors.systemGreen),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text(if (isFa) "ثبت نهایی و اتمام ارزیابی ✓" else "COMMIT PROTOCOL ✓", fontFamily = FiraCode, fontSize = 10.sp, color = colors.systemGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                4 -> { // Completed
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isFa) "✓ ارزیابی و ترازبندی هفته با موفقیت ثبت شد" else "✓ WEEKLY ALIGNMENT COMMITTED",
                            fontFamily = FiraCode,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.systemGreen
                        )
                        Text(
                            text = if (isFa) {
                                "پروتکل با موفقیت اجرا شد. خودفریبی به صفر رسید. ذهن کالیبره شده و آماده هجوم به پیشگاه شنبه است."
                            } else {
                                "The 3-stage strict audit protocol has been fully recorded. Negating delusion. Your mind is aligned and primed to conquer the new week."
                            },
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )

                        Button(
                            onClick = {
                                reviewActiveStep = 0
                                sharedPrefs.edit().putInt("friday_review_active_step", 0).apply()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, colors.borderFaint),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Text(if (isFa) "ارزیابی دوباره" else "RUN PROTOCOL AGAIN", fontFamily = FiraCode, fontSize = 10.sp, color = colors.textSecondary)
                        }
                    }
                }
            }
        }
    }
}
