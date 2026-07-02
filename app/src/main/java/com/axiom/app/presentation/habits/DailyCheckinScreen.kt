package com.axiom.app.presentation.habits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckinScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DailyCheckinViewModel = hiltViewModel(),
    showTopBar: Boolean = true
) {
    val log by viewModel.habitLog.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var hoursText by remember { mutableStateOf("") }
    LaunchedEffect(log?.sleepHours) {
        val sleepHrsVal = log?.sleepHours
        if (sleepHrsVal != null) {
            // Keep text in sync if parsing differs
            if (hoursText.toFloatOrNull() != sleepHrsVal) {
                hoursText = sleepHrsVal.toString()
            }
        } else {
            if (hoursText.isNotEmpty() && hoursText.toFloatOrNull() == null) {
                // leave user input alone
            } else {
                hoursText = ""
            }
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (Locale.getDefault().language == "fa") "بررسی روزانه هابیت‌ها" else "DAILY HABIT CHECK-IN",
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = LegendaryGold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VoidBlack,
                        titleContentColor = LegendaryGold,
                        navigationIconContentColor = LegendaryGold
                    )
                )
            }
        },
        containerColor = VoidBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showTopBar) innerPadding else PaddingValues(top = 8.dp))
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Header Indicator
            Text(
                text = "BIOLOGICAL INTEGRITY METRICS",
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = LegendaryGold,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = LegendaryGold
                    )
                    Text(
                        text = "Every transaction is securely and instantly committed as you interact. No finalize buttons required.",
                        fontFamily = Inter,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // 1. WATER GLASSES TRACKER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_water_tracker"),
                colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "H2O CORDRANT",
                            fontFamily = JetBrainsMono,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegendaryGold
                        )
                        Text(
                            text = "${log?.waterGlasses ?: 0} / 8 GLASSES",
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            color = SystemGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Hydration level dictates physical system throughput. Tap glasses to logs consumed index.",
                        fontFamily = Inter,
                        fontSize = 11.sp,
                        color = TextDim
                    )

                    // 8 interactive glasses circles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentGlasses = log?.waterGlasses ?: 0
                        (1..8).forEach { index ->
                            val filled = index <= currentGlasses
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (filled) SystemGreen else Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (filled) SystemGreen else BorderFaint,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        // If tap the same glass that is currently active, toggle down by 1 or clear
                                        val newTarget = if (index == currentGlasses) index - 1 else index
                                        viewModel.updateWater(newTarget)
                                    }
                                    .testTag("water_circle_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$index",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (filled) VoidBlack else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. SLEEP ENGINE TRACKER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_sleep_tracker"),
                colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "REST STATS MODEL",
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LegendaryGold
                    )

                    // Input for sleep hours
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "HOURS SLEPT",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = TextDim
                        )
                        OutlinedTextField(
                            value = hoursText,
                            onValueChange = {
                                hoursText = it
                                val parsed = it.toFloatOrNull()
                                viewModel.updateSleep(parsed, log?.sleepQuality)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("e.g. 7.5", color = TextDim) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LegendaryGold,
                                unfocusedBorderColor = BorderFaint,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_sleep_hours")
                        )
                    }

                    // Interactive Custom Stars 1-5
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "REGENERATION QUALITY (1 - 5 STAR)",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = TextDim
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val activeQuality = log?.sleepQuality ?: 0
                            (1..5).forEach { rate ->
                                val active = rate <= activeQuality
                                Text(
                                    text = if (active) "★" else "☆",
                                    fontSize = 28.sp,
                                    color = if (active) LegendaryGold else TextDim,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.updateSleep(log?.sleepHours, rate)
                                        }
                                        .testTag("sleep_star_$rate")
                                )
                            }
                        }
                    }
                }
            }

            // 3. TEETH HYGIENE TRACKER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_teeth_tracker"),
                colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "HYGIENE PROTOCOLS",
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LegendaryGold
                    )

                    // Morning Brushing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Morning Cleansing", fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Prevent cavity formation upon waking up.", fontFamily = Inter, fontSize = 11.sp, color = TextDim)
                        }
                        Switch(
                            checked = log?.teethMorning ?: false,
                            onCheckedChange = {
                                viewModel.updateTeeth(morning = it, evening = log?.teethEvening ?: false)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen),
                            modifier = Modifier.testTag("switch_teeth_morning")
                        )
                    }

                    HorizontalDivider(color = BorderFaint)

                    // Evening Brushing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Evening Cleansing", fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Purify and prepare for regeneration cycle.", fontFamily = Inter, fontSize = 11.sp, color = TextDim)
                        }
                        Switch(
                            checked = log?.teethEvening ?: false,
                            onCheckedChange = {
                                viewModel.updateTeeth(morning = log?.teethMorning ?: false, evening = it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen),
                            modifier = Modifier.testTag("switch_teeth_evening")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
