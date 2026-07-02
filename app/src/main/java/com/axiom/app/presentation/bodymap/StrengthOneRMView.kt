package com.axiom.app.presentation.bodymap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*
import java.util.Locale

@Composable
fun StrengthOneRMView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var weightString by remember { mutableStateOf("80") }
        var selectedReps by remember { mutableStateOf(5) }

        val calculated1RM = remember(weightString, selectedReps) {
            val w = weightString.toFloatOrNull() ?: 0f
            if (w <= 0f) 0f else {
                val epley = w * (1f + selectedReps * 0.0333f)
                val brzycki = w / (1.0278f - 0.0278f * selectedReps)
                (epley + brzycki) / 2f
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ShadowSurface),
            border = BorderStroke(1.dp, BorderFaint)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "کالکیولیتور تخمین رکورد (1RM ESTIMATOR)",
                    color = LegendaryGold,
                    fontSize = 12.sp,
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("وزنه‌ی آزمایشی جابه‌جا شده (کیلوگرم):", color = TextSecondary, fontSize = 11.sp)
                    OutlinedTextField(
                        value = weightString,
                        onValueChange = { weightString = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LegendaryGold,
                            unfocusedBorderColor = BorderFaint,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("تعداد تکرار جابه‌جا شده: ($selectedReps تکرار)", color = TextSecondary, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..6).forEach { rep ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedReps == rep) LegendaryGold else BorderFaint)
                                    .clickable { selectedReps = rep }
                                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$rep",
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedReps == rep) VoidBlack else TextPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (7..12).forEach { rep ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedReps == rep) LegendaryGold else BorderFaint)
                                    .clickable { selectedReps = rep }
                                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$rep",
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedReps == rep) VoidBlack else TextPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        if (calculated1RM > 0f) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                border = BorderStroke(1.dp, LegendaryGold)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("تخمین رکورد جابجایی تکرار بیشینه شما (Estimated 1RM)", color = TextDim, fontSize = 11.sp)
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.1f", calculated1RM)} کیلوگرم",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LegendaryGold,
                        fontFamily = JetBrainsMono
                    )

                    HorizontalDivider(color = BorderFaint, modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "دسته‌بندی دامنه‌ها و زون‌های تمرینی کالیبر",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ZoneRow(
                            label = "قدرت نسبی محض (۱ تا ۵ تکرار - ۹۰٪)",
                            value = "${String.format(Locale.getDefault(), "%.1f", calculated1RM * 0.9f)}kg"
                        )
                        ZoneRow(
                            label = "هایپرتروفی کُلی / عضله‌سازی (۶ تا ۱۲ تکرار - ۸۰٪)",
                            value = "${String.format(Locale.getDefault(), "%.1f", calculated1RM * 0.8f)}kg"
                        )
                        ZoneRow(
                            label = "سوزاندن و استقامت عضلات (۱۵+ تکرار - ۶۵٪)",
                            value = "${String.format(Locale.getDefault(), "%.1f", calculated1RM * 0.65f)}kg"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ZoneRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = LegendaryGold, fontWeight = FontWeight.Bold, fontFamily = JetBrainsMono, fontSize = 12.sp)
    }
}
