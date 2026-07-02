package com.axiom.app.presentation.bodymap

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.LocalizationUtils
import com.axiom.app.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleStatusPanel(
    selectedMuscle: MuscleGroup,
    onDismissRequest: () -> Unit,
    onLogTraining: (muscleId: String, hours: Float, goalSet: Boolean, feedback: Boolean, pushed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = ShadowSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = BorderFaint) },
        modifier = modifier
    ) {
        var hoursTrainedStr by remember { mutableStateOf("1.0") }
        var goalSet by remember { mutableStateOf(true) }
        var gotFeedback by remember { mutableStateOf(false) }
        var pushedComfortZone by remember { mutableStateOf(false) }

        val freshnessVal = selectedMuscle.freshnessPercent
        val isFa = Locale.getDefault().language == "fa"

        // Theme color based on recovery status
        val (themeColor, recoverySpeedLabel) = when {
            freshnessVal >= 90 -> Color(0xFF00FF7F) to (if (isFa) "کامل" else "FULL")
            freshnessVal >= 60 -> Color(0xFFFF8F00) to (if (isFa) "سبک" else "Light")
            else -> Color(0xFFFF3D00) to (if (isFa) "استراحت نهایی" else "Rest Required")
        }

        val statusLabel = when {
            freshnessVal >= 90 -> if (isFa) "آماده تمرین سنگین" else "Ready for Heavy"
            freshnessVal >= 60 -> if (isFa) "آماده کار بی هوازی سبک" else "Ready for Light"
            else -> if (isFa) "نیاز به ریکاوری عمیق" else "Under Recovery"
        }

        val fullRecoveryTime = when {
            freshnessVal >= 90 -> if (isFa) "آماده" else "Fully Ready"
            freshnessVal >= 60 -> "1d 4h"
            else -> "2d 8h"
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Header styled like the "Recovery" Mockup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = LocalizationUtils.getLocalizedSkillName(selectedMuscle.displayName, context),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$freshnessVal% ${if (isFa) "ریکاوری شده" else "Loaded"}",
                            color = themeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = JetBrainsMono
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(TextDim)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusLabel,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Close trigger
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(BorderFaint.copy(alpha = 0.3f))
                        .size(34.dp)
                ) {
                    Text("✕", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Filled progress bar indicating recovery state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(BorderFaint.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(freshnessVal / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(themeColor.copy(alpha = 0.7f), themeColor)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 2x2 Grid details for HIGH-FI METRICS
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Card 1
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, BorderFaint)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isFa) "آماده برای" else "READY FOR",
                                color = TextDim,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = recoverySpeedLabel,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Card 2
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, BorderFaint)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isFa) "ریکاوری کامل" else "FULL RECOVERY",
                                color = TextDim,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fullRecoveryTime,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = JetBrainsMono
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Card 3
                    val dateStr = remember(selectedMuscle.lastTrainedTimestamp) {
                        selectedMuscle.lastTrainedTimestamp?.let {
                            val diff = System.currentTimeMillis() - it
                            val hours = diff / (1000 * 60 * 60)
                            if (hours < 1) {
                                if (isFa) "همین الان" else "Just now"
                            } else if (hours < 24) {
                                if (isFa) "${hours} ساعت پیش" else "${hours}h ago"
                            } else {
                                val days = hours / 24
                                if (isFa) "${days} روز پیش" else "${days}d ago"
                            }
                        } ?: (if (isFa) "ثبت نشده" else "Never")
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, BorderFaint)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isFa) "آخرین تمرین" else "LAST TRAINED",
                                color = TextDim,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dateStr,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Card 4
                    val frequencyStr = if (selectedMuscle.lastTrainedTimestamp != null) "2X this week" else "0X this week"
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, BorderFaint)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isFa) "تکرار هفتگی" else "FREQUENCY",
                                color = TextDim,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = frequencyStr,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = JetBrainsMono
                            )
                        }
                    }
                }
            }

            // Volume Section & Mini Bar Chart
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isFa) "حجم تمرین هفتگی" else "VOLUME THIS WEEK",
                                color = TextDim,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (selectedMuscle.lastTrainedTimestamp != null) "4,960 lbs" else "0 lbs",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                fontFamily = JetBrainsMono
                            )
                            Text(
                                text = if (selectedMuscle.lastTrainedTimestamp != null) "7 sets this week" else "0 sets this week",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        Text(
                            text = "-34% vs 4-wk avg",
                            color = Color(0xFFFF5252),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JetBrainsMono
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "LAST 6 WEEKS",
                        color = TextDim,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    val volumesList = if (selectedMuscle.lastTrainedTimestamp != null) {
                        listOf(2800f, 3200f, 1500f, 4200f, 3100f, 4960f)
                    } else {
                        listOf(0f, 0f, 0f, 0f, 0f, 0f)
                    }
                    RecoveryBarChart(
                        volumes = volumesList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                    )
                }
            }

            // Recommended Exercises
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isFa) "تمرین‌های برتر پیشنهادی این هفته" else "TOP EXERCISES RECOMMENDED",
                    color = LegendaryGold,
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                val recommended = remember(selectedMuscle.id) {
                    getRecommendedExercises(selectedMuscle.id)
                }

                recommended.forEach { exerciseName ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                        border = BorderStroke(1.dp, BorderFaint)
                    ) {
                        Row(
                            modifier = Modifier.padding(11.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(themeColor.copy(alpha = 0.15f))
                                    .border(1.dp, themeColor, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✔", color = themeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Column {
                                Text(
                                    text = exerciseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "3 SETS  |  12 REPS  |  DYNAMIC WEIGHT",
                                    fontSize = 10.sp,
                                    color = TextDim,
                                    fontFamily = JetBrainsMono
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = BorderFaint, modifier = Modifier.padding(vertical = 4.dp))

            // Log New Workout Form
            Text(
                text = if (isFa) "ثبت جلسه تمرینی جدید" else "LOG REHABILITATION WORKOUT",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = LegendaryGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isFa) "مدت زمان تمرین عضلانی (ساعت):" else "Duration Trained (Hours):",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = hoursTrainedStr,
                    onValueChange = { hoursTrainedStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LegendaryGold,
                        unfocusedBorderColor = BorderFaint,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_hours_trained")
                )
            }

            // Checklist
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = if (isFa) "هدف‌گذاری واضح" else "Clear Intent Goal Set",
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isFa) "آیا هر ست با تمرکز کامل انجام شد؟" else "Was each set performed with ultra precision?",
                            fontFamily = Inter,
                            fontSize = 9.sp,
                            color = TextDim
                        )
                    }
                    Switch(
                        checked = goalSet,
                        onCheckedChange = { goalSet = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen),
                        modifier = Modifier.testTag("switch_goal_set")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = if (isFa) "دریافت بازخورد" else "Feedback Integration",
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isFa) "آیا انقباض کامل عضلانی را ثبت کردید؟" else "Did you track full squeeze on peak contractions?",
                            fontFamily = Inter,
                            fontSize = 9.sp,
                            color = TextDim
                        )
                    }
                    Switch(
                        checked = gotFeedback,
                        onCheckedChange = { gotFeedback = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen),
                        modifier = Modifier.testTag("switch_feedback")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = if (isFa) "خروج از محدوده امن بدنی" else "Pushing Physical Limits",
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isFa) "نزدیک شدن به ناتوانی کلی (Failure) برای ریکاوری" else "Training near absolute failure threshold?",
                            fontFamily = Inter,
                            fontSize = 9.sp,
                            color = TextDim
                        )
                    }
                    Switch(
                        checked = pushedComfortZone,
                        onCheckedChange = { pushedComfortZone = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen),
                        modifier = Modifier.testTag("switch_stretch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Log action submit button
            Button(
                onClick = {
                    val hrs = hoursTrainedStr.toFloatOrNull() ?: 1.0f
                    onLogTraining(selectedMuscle.id, hrs, goalSet, gotFeedback, pushedComfortZone)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SystemGreen),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_log_training_submit")
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Log", tint = VoidBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFa) "تایید و ثبت نهایی در ماتریکس کله عضلانی" else "SUBMIT SESSION TO MATRIX DATABASE",
                        color = VoidBlack,
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
fun RecoveryBarChart(
    volumes: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val colWidth = 24.dp.toPx()
        val spacing = (w - (colWidth * 6)) / 5
        val maxVolume = (volumes.maxOrNull() ?: 1f).coerceAtLeast(100f)

        volumes.forEachIndexed { i, vol ->
            val colHeight = if (maxVolume > 0) (vol / maxVolume) * h else 10f
            val x = i * (colWidth + spacing)
            val y = h - colHeight

            // Highlight latest week
            val isCurrent = i == 5
            val brush = if (isCurrent) {
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF00FF7F), Color(0xFF00B0FF))
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(TextSecondary.copy(alpha = 0.5f), TextSecondary.copy(alpha = 0.15f))
                )
            }

            val path = Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = x,
                        top = y.coerceAtLeast(0f),
                        right = x + colWidth,
                        bottom = h,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                )
            }
            drawPath(path = path, brush = brush)
        }
    }
}

fun getRecommendedExercises(muscleId: String): List<String> = when (muscleId.lowercase()) {
    "chest" -> listOf("Barbell Bench Press", "Dumbbell Flyes", "Incline Bench Press")
    "back" -> listOf("Dumbbell Row", "Deadlifts", "Lat Pulldowns")
    "core" -> listOf("Plank Hold", "Ab Rollouts", "Hanging Leg Raises")
    "legs" -> listOf("Barbell Back Squats", "Leg Extensions", "Romanian Deadlifts")
    "shoulders" -> listOf("Military Overhead Press", "Lateral Raises", "Rear Delt Flyes")
    "biceps" -> listOf("Hammer Curls", "Barbell Bicep Curls", "Preacher Curl")
    "triceps" -> listOf("Tricep Pushdowns", "Skullcrushers", "Dips")
    "forearms" -> listOf("Reverse Curls", "Wrist Roller", "Farmer Carries")
    else -> listOf("Pushups", "Pullups", "Bodyweight Squats")
}

