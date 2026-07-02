package com.axiom.app.presentation.bodymap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.LocalizationUtils
import com.axiom.app.ui.theme.*

@Composable
fun CaliberInsightsView(
    muscles: List<MuscleGroup>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val overallScore = remember(muscles) {
            if (muscles.isEmpty()) 0 else muscles.map { it.strengthScore }.average().toInt()
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ShadowSurface),
            border = BorderStroke(1.dp, BorderFaint)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "امتیاز کلی کالیبر قدرت بدنی",
                    color = LegendaryGold,
                    fontSize = 12.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = BorderFaint,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 10f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )

                        val sweep = (overallScore / 100f) * 270f
                        drawArc(
                            color = LegendaryGold,
                            startAngle = 135f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$overallScore",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "قدرت کل",
                            fontSize = 11.sp,
                            color = TextDim,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                val ratingText = when {
                    overallScore >= 85 -> "کالیبر نخبگان (Elite Tier)"
                    overallScore >= 65 -> "حرفه‌ای پیشرفته (Advanced Strength)"
                    overallScore >= 45 -> "سطح متوسط کالیبره (Intermediate Balanced)"
                    else -> "نوآموز تمرکزی (Apprentice Developer)"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "", tint = LegendaryGold, modifier = Modifier.size(14.dp))
                    Text(text = ratingText, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            text = "شاخص‌های تقارن و تعادل کالیبر (EQUILIBRIUM)",
            color = TextDim,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        val pushAvg = remember(muscles) {
            val m = muscles.filter { it.id in listOf("chest", "shoulders", "triceps") }
            if (m.isEmpty()) 50f else m.map { it.strengthScore }.average().toFloat()
        }
        val pullAvg = remember(muscles) {
            val m = muscles.filter { it.id in listOf("back", "biceps") }
            if (m.isEmpty()) 50f else m.map { it.strengthScore }.average().toFloat()
        }
        val absPushDiff = pushAvg - pullAvg

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ShadowSurface),
            border = BorderStroke(1.dp, BorderFaint)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("کالیبراسیون فشار در برابر کشش (Push vs Pull)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = when {
                            absPushDiff > 8 -> "غلبه عضلات جلو (Push bias)"
                            absPushDiff < -8 -> "غلبه عضلات پشت (Pull bias)"
                            else -> "تعادل کامل (Symmetric)"
                        },
                        fontSize = 11.sp,
                        color = LegendaryGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                val pushPercent = (pushAvg / (pushAvg + pullAvg).coerceAtLeast(1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BorderFaint)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(pushPercent)
                                .background(SystemGreen)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f - pushPercent)
                                .background(LegendaryGold)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("عضلات فشار دهنده (سینه/سرشانه): ${pushAvg.toInt()}", fontSize = 10.sp, color = TextDim)
                    Text("عضلات کشنده (پشت/جلوبازو): ${pullAvg.toInt()}", fontSize = 10.sp, color = TextDim)
                }
            }
        }

        val upperAvg = remember(muscles) {
            val m = muscles.filter { it.id in listOf("chest", "back", "shoulders", "biceps", "triceps", "forearms") }
            if (m.isEmpty()) 50f else m.map { it.strengthScore }.average().toFloat()
        }
        val lowerAvg = remember(muscles) {
            val m = muscles.filter { it.id == "legs" }
            if (m.isEmpty()) 50f else m.map { it.strengthScore }.average().toFloat()
        }
        val absUpperLowerDiff = upperAvg - lowerAvg

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ShadowSurface),
            border = BorderStroke(1.dp, BorderFaint)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("بالاتنه در برابر پایین‌تنه (Upper vs Lower)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = when {
                            absUpperLowerDiff > 10 -> "بالاتنه قوی‌تر"
                            absUpperLowerDiff < -10 -> "پایین‌تنه قوی‌تر"
                            else -> "تعادل یکنواخت (Symmetric)"
                        },
                        fontSize = 11.sp,
                        color = LegendaryGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                val upperPercent = (upperAvg / (upperAvg + lowerAvg).coerceAtLeast(1f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BorderFaint)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(upperPercent)
                                .background(SystemGreen)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f - upperPercent)
                                .background(LegendaryGold)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("میانگین بالاتنه: ${upperAvg.toInt()}", fontSize = 10.sp, color = TextDim)
                    Text("میانگین پایین‌تنه: ${lowerAvg.toInt()}", fontSize = 10.sp, color = TextDim)
                }
            }
        }

        val weakestMuscle = remember(muscles) {
            muscles.minByOrNull { it.strengthScore }
        }

        if (weakestMuscle != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x11FF5555)),
                border = BorderStroke(1.dp, Color(0x33FF5555))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "⚠️ اولویت کالیبراسیون و ضعف عضلانی",
                        color = Color(0xFFFF5555),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ضعیف‌ترین بخش فعلی بدنی شما عضله ${LocalizationUtils.getLocalizedSkillName(weakestMuscle.displayName, context)} با امتیاز ${weakestMuscle.strengthScore} است. پیشنهاد کالیبر تمرکز سریع روی حرکاتی مانند اسکات، ددلیفت یا پرس اختصاصی برای پر کردن خلاء قدرت تفکیک می‌باشد.",
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
