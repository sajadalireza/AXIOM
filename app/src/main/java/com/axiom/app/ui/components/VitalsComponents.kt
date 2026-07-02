package com.axiom.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.axiom.app.R
import com.axiom.app.ui.theme.*
import com.axiom.app.ui.VitalsViewModel

@Composable
fun BurnoutOverlay(
    viewModel: VitalsViewModel,
    modifier: Modifier = Modifier
) {
    val burnoutActive by viewModel.burnoutActive.collectAsState()
    val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"

    if (burnoutActive) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LocalAxiomColors.current.voidBlack.copy(alpha = 0.95f))
                .background(PenaltyRed.copy(alpha = 0.12f))
                .testTag("burnout_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(24.dp)
            ) {
                Text(
                    text = "◆",
                    fontFamily = JetBrainsMono,
                    fontSize = 48.sp,
                    color = PenaltyRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.neonGlow(PenaltyRed, intensity = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isFa) "هشدار فرسودگی شدید سیستم" else "CRITICAL BURNOUT WARNING",
                    fontFamily = JetBrainsMono,
                    fontSize = 18.sp,
                    color = PenaltyRed,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.neonGlow(PenaltyRed, intensity = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isFa) {
                        "[ فرماندهی ] آستانه فرسودگی شغلی رد شد. توصیه می‌شود مأموریت‌های غیرضروری را به مدت ۴۸ ساعت تعلیق کنید."
                    } else {
                        "[ COMMAND ] Burnout threshold crossed. Recommend 48h of non-essential work suspension."
                    },
                    fontFamily = Inter,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.acknowledgeBurnout() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PenaltyRed,
                        contentColor = VoidBlack
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .neonGlow(PenaltyRed, intensity = 0.3f)
                        .testTag("burnout_ack_button")
                ) {
                    Text(
                        text = if (isFa) "تایید و ثبت فرمان" else "ACKNOWLEDGE COMMAND",
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EnergyPromptOverlay(
    viewModel: VitalsViewModel,
    modifier: Modifier = Modifier
) {
    val showPrompt by viewModel.showEnergyPrompt.collectAsState()
    val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"
    var selectedScore by remember { mutableStateOf(5) }

    if (showPrompt) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LocalAxiomColors.current.voidBlack.copy(alpha = 0.85f))
                .testTag("energy_prompt_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = LocalAxiomColors.current.voidBlack,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, LegendaryGold),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⎔",
                        fontFamily = JetBrainsMono,
                        fontSize = 32.sp,
                        color = LegendaryGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.neonGlow(LegendaryGold, intensity = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isFa) "ثبت سطح انرژی امروز" else "DAILY ENERGY RATING",
                        fontFamily = JetBrainsMono,
                        fontSize = 16.sp,
                        color = LegendaryGold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isFa) {
                            "میزان سطح انرژی امروز خود را وارد کنید (۱ تا ۱۰):"
                        } else {
                            "Rate today's energy rating (1-10):"
                        },
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Huge Score Display
                    Text(
                        text = selectedScore.toString(),
                        fontFamily = JetBrainsMono,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = LegendaryGold,
                        modifier = Modifier.neonGlow(LegendaryGold, intensity = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = selectedScore.toFloat(),
                        onValueChange = { selectedScore = it.toInt().coerceIn(1, 10) },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = LegendaryGold,
                            activeTrackColor = LegendaryGold,
                            inactiveTrackColor = BorderFaint
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.dismissEnergyPrompt() },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.5.dp, BorderFaint),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDim),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text(
                                text = if (isFa) "بعداً" else "LATER",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.logEnergyAndSavePrompt(selectedScore) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LegendaryGold,
                                contentColor = VoidBlack
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .weight(2.5f)
                                .height(44.dp)
                                .neonGlow(LegendaryGold, intensity = 0.2f)
                                .testTag("energy_submit_button")
                        ) {
                            Text(
                                text = if (isFa) "ثبت سطح انرژی" else "SUBMIT ENERGY LEVEL",
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VitalsRow(
    viewModel: VitalsViewModel,
    modifier: Modifier = Modifier
) {
    val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"
    val todayWater by viewModel.todayWater.collectAsState()
    val todaySleep by viewModel.todaySleep.collectAsState()
    val todayTeethAm by viewModel.todayTeethAm.collectAsState()
    val todayTeethPm by viewModel.todayTeethPm.collectAsState()
    val todayEnergy by viewModel.todayEnergy.collectAsState()

    val waterTarget by viewModel.waterTarget.collectAsState()
    val sleepTarget by viewModel.sleepTarget.collectAsState()
    val energyFloor by viewModel.energyFloor.collectAsState()

    var showWaterDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }
    var showEnergyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isFa) "پروتکل‌های حیاتی بدنی" else "UNIVERSAL VITALS",
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            color = TextDim,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Water
            Box(modifier = Modifier.weight(1f)) {
                VitalCard(
                    title = if (isFa) "آب" else "WATER",
                    value = "${todayWater.toInt()}ml",
                    subValue = "/ ${waterTarget.toInt()}ml",
                    borderColor = if (todayWater >= waterTarget) SystemGreen else BorderFaint,
                    onClick = { showWaterDialog = true }
                )
            }

            // 2. Sleep
            Box(modifier = Modifier.weight(1f)) {
                val sleepColor = when {
                    todaySleep >= sleepTarget -> SystemGreen
                    todaySleep >= (sleepTarget - 1.0f) -> LegendaryGold
                    else -> PenaltyRed
                }
                VitalCard(
                    title = if (isFa) "خواب" else "SLEEP",
                    value = String.format("%.1fh", todaySleep),
                    subValue = "/ ${sleepTarget}h",
                    borderColor = if (todaySleep > 0) sleepColor else BorderFaint,
                    onClick = { showSleepDialog = true }
                )
            }

            // 3. Teeth (Direct toggle Checkboxes)
            Box(modifier = Modifier.weight(1f)) {
                VitalTeethCard(
                    amChecked = todayTeethAm,
                    pmChecked = todayTeethPm,
                    onToggleAm = { viewModel.toggleTeeth(am = true) },
                    onTogglePm = { viewModel.toggleTeeth(am = false) }
                )
            }

            // 4. Energy
            Box(modifier = Modifier.weight(1f)) {
                val hasEnergy = todayEnergy != null
                val energyBorderColor = if (hasEnergy) {
                    if ((todayEnergy ?: 0) >= energyFloor) SystemGreen else PenaltyRed
                } else {
                    BorderFaint
                }
                VitalCard(
                    title = if (isFa) "انرژی" else "ENERGY",
                    value = todayEnergy?.toString() ?: "-",
                    subValue = "/ 10",
                    borderColor = energyBorderColor,
                    onClick = { showEnergyDialog = true }
                )
            }
        }
    }

    if (showWaterDialog) {
        WaterLogDialog(
            currentWater = todayWater,
            targetWater = waterTarget,
            onDismiss = { showWaterDialog = false },
            onLog = { ml ->
                viewModel.logWater(ml)
                showWaterDialog = false
            }
        )
    }

    if (showSleepDialog) {
        SleepLogDialog(
            currentSleep = todaySleep,
            targetSleep = sleepTarget,
            onDismiss = { showSleepDialog = false },
            onLog = { hours ->
                viewModel.logSleep(hours)
                showSleepDialog = false
            }
        )
    }

    if (showEnergyDialog) {
        EnergyLogDialog(
            currentEnergy = todayEnergy,
            onDismiss = { showEnergyDialog = false },
            onLog = { score ->
                viewModel.logEnergyAndSavePrompt(score)
                showEnergyDialog = false
            }
        )
    }
}

@Composable
private fun VitalCard(
    title: String,
    value: String,
    subValue: String,
    borderColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = ShadowSurface,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontFamily = JetBrainsMono,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = TextDim
            )
            Text(
                text = value,
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subValue,
                fontFamily = JetBrainsMono,
                fontSize = 8.sp,
                color = TextDim
            )
        }
    }
}

@Composable
private fun VitalTeethCard(
    amChecked: Boolean,
    pmChecked: Boolean,
    onToggleAm: () -> Unit,
    onTogglePm: () -> Unit
) {
    val teethBothComplete = amChecked && pmChecked
    val borderColor = if (teethBothComplete) SystemGreen else BorderFaint

    Card(
        colors = CardDefaults.cardColors(
            containerColor = ShadowSurface,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TEETH",
                fontFamily = JetBrainsMono,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = TextDim
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AM Box
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(if (amChecked) SystemGreen.copy(alpha = 0.2f) else Color.Transparent)
                        .border(0.5.dp, if (amChecked) SystemGreen else TextDim, RoundedCornerShape(2.dp))
                        .clickable(onClick = onToggleAm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AM",
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (amChecked) SystemGreen else TextDim
                    )
                }

                // PM Box
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(if (pmChecked) SystemGreen.copy(alpha = 0.2f) else Color.Transparent)
                        .border(0.5.dp, if (pmChecked) SystemGreen else TextDim, RoundedCornerShape(2.dp))
                        .clickable(onClick = onTogglePm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PM",
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (pmChecked) SystemGreen else TextDim
                    )
                }
            }

            Text(
                text = if (teethBothComplete) "COMPLETE" else "PENDING",
                fontFamily = JetBrainsMono,
                fontSize = 7.sp,
                color = if (teethBothComplete) SystemGreen else TextSecondary
            )
        }
    }
}

@Composable
fun WaterLogDialog(
    currentWater: Float,
    targetWater: Float,
    onDismiss: () -> Unit,
    onLog: (Float) -> Unit
) {
    var customAmount by remember { mutableStateOf("") }
    val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderFaint),
            colors = CardDefaults.cardColors(containerColor = LocalAxiomColors.current.voidBlack)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isFa) "آبگیری روزانه" else "WATER INTAKE",
                    fontFamily = JetBrainsMono,
                    fontSize = 16.sp,
                    color = SystemGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isFa) {
                        "سطح کنونی: ${currentWater.toInt()} میلی‌لیتر از ${targetWater.toInt()} میلی‌لیتر هدف"
                    } else {
                        "Logged: ${currentWater.toInt()} / ${targetWater.toInt()} ml"
                    },
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onLog(250f) },
                        colors = ButtonDefaults.buttonColors(containerColor = ShadowSurface, contentColor = TextPrimary),
                        modifier = Modifier.weight(1f).border(0.5.dp, BorderFaint, RoundedCornerShape(4.dp))
                    ) {
                        Text("+250ml", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onLog(500f) },
                        colors = ButtonDefaults.buttonColors(containerColor = ShadowSurface, contentColor = TextPrimary),
                        modifier = Modifier.weight(1f).border(0.5.dp, BorderFaint, RoundedCornerShape(4.dp))
                    ) {
                        Text("+500ml", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val customLabel = if (isFa) "مقدار سفارشی (میلی‌لیتر)" else "Custom amount (ml)"
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(customLabel, fontSize = 11.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = JetBrainsMono),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SystemGreen,
                        unfocusedBorderColor = BorderFaint
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isFa) "انصراف" else "CANCEL", color = TextDim, fontFamily = JetBrainsMono)
                    }

                    Button(
                        onClick = {
                            val ml = customAmount.toFloatOrNull() ?: 0f
                            if (ml > 0f) onLog(ml)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SystemGreen, contentColor = VoidBlack),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isFa) "ثبت" else "SAVE", fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SleepLogDialog(
    currentSleep: Float,
    targetSleep: Float,
    onDismiss: () -> Unit,
    onLog: (Float) -> Unit
) {
    var sleepHours by remember { mutableStateOf("") }
    val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderFaint),
            colors = CardDefaults.cardColors(containerColor = LocalAxiomColors.current.voidBlack)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isFa) "ثبت خواب" else "SLEEP LOG",
                    fontFamily = JetBrainsMono,
                    fontSize = 16.sp,
                    color = SystemGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isFa) {
                        "خواب امروز: ${currentSleep} ساعت (هدف: ${targetSleep} ساعت)"
                    } else {
                        "Logged sleep: ${currentSleep}h (Target: ${targetSleep}h)"
                    },
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = sleepHours,
                    onValueChange = { sleepHours = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(if (isFa) "مدت خواب (ساعت)" else "Sleep hours", fontSize = 11.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = JetBrainsMono),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SystemGreen,
                        unfocusedBorderColor = BorderFaint
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isFa) "انصراف" else "CANCEL", color = TextDim, fontFamily = JetBrainsMono)
                    }

                    Button(
                        onClick = {
                            val hours = sleepHours.toFloatOrNull() ?: 0f
                            if (hours > 0f) onLog(hours)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SystemGreen, contentColor = VoidBlack),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isFa) "ثبت" else "SAVE", fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EnergyLogDialog(
    currentEnergy: Int?,
    onDismiss: () -> Unit,
    onLog: (Int) -> Unit
) {
    var selectedScore by remember { mutableStateOf(currentEnergy ?: 5) }
    val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BorderFaint),
            colors = CardDefaults.cardColors(containerColor = LocalAxiomColors.current.voidBlack)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isFa) "سطح انرژی" else "ENERGY RATING",
                    fontFamily = JetBrainsMono,
                    fontSize = 16.sp,
                    color = LegendaryGold,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = selectedScore.toString(),
                    fontFamily = JetBrainsMono,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = LegendaryGold,
                    modifier = Modifier.neonGlow(LegendaryGold, intensity = 0.3f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = selectedScore.toFloat(),
                    onValueChange = { selectedScore = it.toInt().coerceIn(1, 10) },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = LegendaryGold,
                        activeTrackColor = LegendaryGold,
                        inactiveTrackColor = BorderFaint
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isFa) "انصراف" else "CANCEL", color = TextDim, fontFamily = JetBrainsMono)
                    }

                    Button(
                        onClick = { onLog(selectedScore) },
                        colors = ButtonDefaults.buttonColors(containerColor = LegendaryGold, contentColor = VoidBlack),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text(if (isFa) "ثبت" else "SAVE", fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
