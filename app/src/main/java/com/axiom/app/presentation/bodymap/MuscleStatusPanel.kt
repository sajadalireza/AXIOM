package com.axiom.app.presentation.bodymap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.theme.AxiomBorder
import com.axiom.app.ui.theme.AxiomRadius
import com.axiom.app.ui.theme.AxiomSpacing
import com.axiom.app.ui.theme.FiraCode
import com.axiom.app.ui.theme.Inter
import com.axiom.app.ui.theme.LocalAxiomColors
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
    val colors = LocalAxiomColors.current
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = colors.shadowSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.borderFaint) },
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
            freshnessVal >= 80 -> colors.systemGreen to (if (isFa) "کامل" else "FULL")
            freshnessVal >= 40 -> colors.legendaryGold to (if (isFa) "سبک" else "LIGHT")
            else -> colors.penaltyRed to (if (isFa) "استراحت نهایی" else "REST REQUIRED")
        }

        val statusLabel = when {
            freshnessVal >= 80 -> stringResource(R.string.bodymap_ready_heavy)
            freshnessVal >= 40 -> stringResource(R.string.bodymap_ready_light)
            else -> stringResource(R.string.bodymap_under_recovery)
        }

        val fullRecoveryTime = when {
            freshnessVal >= 80 -> stringResource(R.string.bodymap_fully_ready)
            freshnessVal >= 40 -> "1d 4h"
            else -> "2d 8h"
        }

        val localizedMuscleName = getLocalizedMuscleName(
            muscleId = selectedMuscle.id,
            fallbackDisplayName = selectedMuscle.displayName,
            context = context
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AxiomSpacing.l)
                .padding(bottom = AxiomSpacing.xxl)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AxiomSpacing.m)
        ) {
            // Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = localizedMuscleName,
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(AxiomSpacing.xxs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$freshnessVal% ${if (isFa) "ریکاوری" else "Recovery"}",
                            color = themeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FiraCode
                        )
                        Spacer(modifier = Modifier.width(AxiomSpacing.xs))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(RoundedCornerShape(AxiomRadius.xs))
                                .background(colors.textDim)
                        )
                        Spacer(modifier = Modifier.width(AxiomSpacing.xs))
                        Text(
                            text = statusLabel,
                            color = colors.textSecondary,
                            fontFamily = Inter,
                            fontSize = 11.sp
                        )
                    }
                }

                // Accessible Close button (>= 48dp touch target)
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clip(RoundedCornerShape(AxiomRadius.round))
                        .background(colors.borderFaint.copy(alpha = 0.3f))
                        .semantics {
                            contentDescription = context.getString(R.string.bodymap_close)
                        }
                        .testTag("btn_close_muscle_panel")
                ) {
                    Text("✕", color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Recovery Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(AxiomRadius.xs))
                    .background(colors.borderFaint.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(freshnessVal / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(AxiomRadius.xs))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(themeColor.copy(alpha = 0.7f), themeColor)
                            )
                        )
                )
            }

            // Metrics 2x2 Grid
            Column(verticalArrangement = Arrangement.spacedBy(AxiomSpacing.s)) {
                Row(horizontalArrangement = Arrangement.spacedBy(AxiomSpacing.s)) {
                    // Card 1: Ready For
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = colors.voidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(AxiomBorder.hairline, colors.borderFaint),
                        shape = RoundedCornerShape(AxiomRadius.m)
                    ) {
                        Column(modifier = Modifier.padding(AxiomSpacing.s)) {
                            Text(
                                text = if (isFa) "آماده برای" else "READY FOR",
                                color = colors.textDim,
                                fontSize = 9.sp,
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(AxiomSpacing.xxs))
                            Text(
                                text = recoverySpeedLabel,
                                color = colors.textPrimary,
                                fontSize = 15.sp,
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Card 2: Full Recovery
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = colors.voidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(AxiomBorder.hairline, colors.borderFaint),
                        shape = RoundedCornerShape(AxiomRadius.m)
                    ) {
                        Column(modifier = Modifier.padding(AxiomSpacing.s)) {
                            Text(
                                text = if (isFa) "ریکاوری کامل" else "FULL RECOVERY",
                                color = colors.textDim,
                                fontSize = 9.sp,
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(AxiomSpacing.xxs))
                            Text(
                                text = fullRecoveryTime,
                                color = colors.textPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FiraCode
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(AxiomSpacing.s)) {
                    // Card 3: Last Trained
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
                        colors = CardDefaults.cardColors(containerColor = colors.voidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(AxiomBorder.hairline, colors.borderFaint),
                        shape = RoundedCornerShape(AxiomRadius.m)
                    ) {
                        Column(modifier = Modifier.padding(AxiomSpacing.s)) {
                            Text(
                                text = if (isFa) "آخرین تمرین" else "LAST TRAINED",
                                color = colors.textDim,
                                fontSize = 9.sp,
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(AxiomSpacing.xxs))
                            Text(
                                text = dateStr,
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Card 4: Strength Score
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = colors.voidBlack.copy(alpha = 0.3f)),
                        border = BorderStroke(AxiomBorder.hairline, colors.borderFaint),
                        shape = RoundedCornerShape(AxiomRadius.m)
                    ) {
                        Column(modifier = Modifier.padding(AxiomSpacing.s)) {
                            Text(
                                text = if (isFa) "امتیاز قدرت" else "STRENGTH SCORE",
                                color = colors.textDim,
                                fontSize = 9.sp,
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(AxiomSpacing.xxs))
                            Text(
                                text = "${selectedMuscle.strengthScore} / 100",
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FiraCode
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = colors.borderFaint, modifier = Modifier.padding(vertical = AxiomSpacing.xxs))

            // Log New Workout Form
            Text(
                text = stringResource(R.string.bodymap_log_workout_title),
                fontFamily = FiraCode,
                fontSize = 11.sp,
                color = colors.legendaryGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(AxiomSpacing.xxs)) {
                Text(
                    text = stringResource(R.string.bodymap_log_hours_trained),
                    fontFamily = FiraCode,
                    fontSize = 10.sp,
                    color = colors.textSecondary
                )
                OutlinedTextField(
                    value = hoursTrainedStr,
                    onValueChange = { hoursTrainedStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.legendaryGold,
                        unfocusedBorderColor = colors.borderFaint,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 48.dp)
                        .testTag("input_hours_trained")
                )
            }

            // Quality Checklist with accessible targets
            Column(verticalArrangement = Arrangement.spacedBy(AxiomSpacing.s)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = AxiomSpacing.s)) {
                        Text(
                            text = stringResource(R.string.bodymap_log_goal_set),
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isFa) "آیا هر ست با تمرکز کامل انجام شد؟" else "Was each set performed with focused intent?",
                            fontFamily = Inter,
                            fontSize = 9.sp,
                            color = colors.textDim
                        )
                    }
                    Switch(
                        checked = goalSet,
                        onCheckedChange = { goalSet = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = colors.systemGreen),
                        modifier = Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .testTag("switch_goal_set")
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = AxiomSpacing.s)) {
                        Text(
                            text = stringResource(R.string.bodymap_log_got_feedback),
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isFa) "آیا انقباض کامل عضلانی را ثبت کردید؟" else "Did you verify form and peak contraction?",
                            fontFamily = Inter,
                            fontSize = 9.sp,
                            color = colors.textDim
                        )
                    }
                    Switch(
                        checked = gotFeedback,
                        onCheckedChange = { gotFeedback = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = colors.systemGreen),
                        modifier = Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .testTag("switch_feedback")
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = AxiomSpacing.s)) {
                        Text(
                            text = stringResource(R.string.bodymap_log_pushed_comfort),
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isFa) "نزدیک شدن به ناتوانی کلی برای تطابق عضلانی" else "Trained within 1-2 reps of muscular reserve?",
                            fontFamily = Inter,
                            fontSize = 9.sp,
                            color = colors.textDim
                        )
                    }
                    Switch(
                        checked = pushedComfortZone,
                        onCheckedChange = { pushedComfortZone = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = colors.systemGreen),
                        modifier = Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .testTag("switch_stretch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(AxiomSpacing.xs))

            // Submit Button (>= 48dp touch target)
            Button(
                onClick = {
                    val hrs = hoursTrainedStr.toFloatOrNull() ?: 1.0f
                    onLogTraining(selectedMuscle.id, hrs, goalSet, gotFeedback, pushedComfortZone)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen),
                shape = RoundedCornerShape(AxiomRadius.m),
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 48.dp)
                    .testTag("btn_log_training_submit")
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = colors.voidBlack)
                    Spacer(modifier = Modifier.width(AxiomSpacing.s))
                    Text(
                        text = stringResource(R.string.bodymap_log_btn_save),
                        color = colors.voidBlack,
                        fontFamily = FiraCode,
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
    val colors = LocalAxiomColors.current
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

            val isCurrent = i == 5
            val brush = if (isCurrent) {
                Brush.verticalGradient(
                    colors = listOf(colors.systemGreen, colors.rareBlue)
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(colors.textSecondary.copy(alpha = 0.5f), colors.textSecondary.copy(alpha = 0.15f))
                )
            }

            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = x,
                        top = y.coerceAtLeast(0f),
                        right = x + colWidth,
                        bottom = h,
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                )
            }
            drawPath(path = path, brush = brush)
        }
    }
}
