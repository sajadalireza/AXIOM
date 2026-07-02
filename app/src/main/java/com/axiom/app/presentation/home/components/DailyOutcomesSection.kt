package com.axiom.app.presentation.home.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*

@Composable
fun DailyOutcomesSection(
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val context = LocalContext.current
    val isFa = java.util.Locale.getDefault().language == "fa"

    val sharedPrefs = remember { context.getSharedPreferences("warrior_settings", Context.MODE_PRIVATE) }

    // State
    val outcome1 = remember { mutableStateOf(sharedPrefs.getString("outcome_1", if (isFa) "کامل کردن ساختار بیسیک عملیاتی" else "Complete core deployment setup") ?: "") }
    val outcome2 = remember { mutableStateOf(sharedPrefs.getString("outcome_2", if (isFa) "بررسی پایداری و پایگاه‌داده پروژه" else "Verify initial database migration") ?: "") }
    val outcome3 = remember { mutableStateOf(sharedPrefs.getString("outcome_3", if (isFa) "برنامه‌ریزی دقیق مأموریت‌های فردا" else "Update daily calibration schedule") ?: "") }

    val checked1 = remember { mutableStateOf(sharedPrefs.getBoolean("outcome_checked_1", false)) }
    val checked2 = remember { mutableStateOf(sharedPrefs.getBoolean("outcome_checked_2", false)) }
    val checked3 = remember { mutableStateOf(sharedPrefs.getBoolean("outcome_checked_3", false)) }

    var editingOutcomeIndex by remember { mutableStateOf<Int?>(null) }
    var editingText by remember { mutableStateOf("") }

    Card(
        modifier = modifier.fillMaxWidth().testTag("today_outcomes_card"),
        colors = CardDefaults.cardColors(containerColor = colors.dimSurface),
        border = BorderStroke(1.dp, colors.systemGreen.copy(alpha = 0.4f)),
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
                    text = if (isFa) "◈ سه دستاورد محوری امروز" else "◈ TODAY'S 3 OUTCOMES",
                    fontFamily = FiraCode,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.systemGreen,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isFa) "پروتکل روزانه" else "DAILY CALIBRATION",
                    fontFamily = FiraCode,
                    fontSize = 8.sp,
                    color = colors.textDim
                )
            }

            Text(
                text = if (isFa) "برای ویرایش روی متن هر دستاورد ضربه بزنید. کارها را پس از اتمام تیک بزنید." else "Tap text to customize any of today's key outcomes. Check when done.",
                fontFamily = Inter,
                fontSize = 10.sp,
                color = colors.textSecondary
            )

            // Row 1
            OutcomeRow(
                checked = checked1.value,
                onCheckedChange = {
                    checked1.value = it
                    sharedPrefs.edit().putBoolean("outcome_checked_1", it).apply()
                },
                text = outcome1.value,
                onTextClick = {
                    editingOutcomeIndex = 1
                    editingText = outcome1.value
                }
            )

            // Row 2
            OutcomeRow(
                checked = checked2.value,
                onCheckedChange = {
                    checked2.value = it
                    sharedPrefs.edit().putBoolean("outcome_checked_2", it).apply()
                },
                text = outcome2.value,
                onTextClick = {
                    editingOutcomeIndex = 2
                    editingText = outcome2.value
                }
            )

            // Row 3
            OutcomeRow(
                checked = checked3.value,
                onCheckedChange = {
                    checked3.value = it
                    sharedPrefs.edit().putBoolean("outcome_checked_3", it).apply()
                },
                text = outcome3.value,
                onTextClick = {
                    editingOutcomeIndex = 3
                    editingText = outcome3.value
                }
            )
        }
    }

    // Dialog overlay for editing
    if (editingOutcomeIndex != null) {
        val index = editingOutcomeIndex!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { editingOutcomeIndex = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.dimSurface),
                border = BorderStroke(1.dp, colors.systemGreen),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(16.dp).testTag("edit_outcome_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isFa) "✍ ویرایش دستاورد محوری امروز" else "✍ UPDATE DAILY KEY OUTCOME",
                        fontFamily = FiraCode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.systemGreen
                    )

                    OutlinedTextField(
                        value = editingText,
                        onValueChange = { editingText = it },
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Inter, fontSize = 13.sp, color = colors.textPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("outcome_edit_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.systemGreen,
                            unfocusedBorderColor = colors.borderFaint,
                            cursorColor = colors.systemGreen
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { editingOutcomeIndex = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text(if (isFa) "لغو" else "CANCEL", fontFamily = FiraCode, fontSize = 11.sp, color = colors.textSecondary)
                        }

                        Button(
                            onClick = {
                                if (editingText.isNotBlank()) {
                                    when (index) {
                                        1 -> {
                                            outcome1.value = editingText
                                            sharedPrefs.edit().putString("outcome_1", editingText).apply()
                                        }
                                        2 -> {
                                            outcome2.value = editingText
                                            sharedPrefs.edit().putString("outcome_2", editingText).apply()
                                        }
                                        3 -> {
                                            outcome3.value = editingText
                                            sharedPrefs.edit().putString("outcome_3", editingText).apply()
                                        }
                                    }
                                }
                                editingOutcomeIndex = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, colors.systemGreen),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(if (isFa) "ذخیره" else "SAVE", fontFamily = FiraCode, fontSize = 11.sp, color = colors.systemGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OutcomeRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    onTextClick: () -> Unit
) {
    val colors = LocalAxiomColors.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.systemGreen,
                uncheckedColor = colors.borderFaint
            )
        )
        Text(
            text = text,
            fontFamily = Inter,
            fontSize = 13.sp,
            color = if (checked) colors.textDim else colors.textPrimary,
            style = androidx.compose.ui.text.TextStyle(
                textDecoration = if (checked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            ),
            modifier = Modifier
                .weight(1f)
                .clickable { onTextClick() }
        )
        Text(
            text = "✍",
            fontSize = 12.sp,
            color = colors.textDim,
            modifier = Modifier.clickable { onTextClick() }
        )
    }
}
