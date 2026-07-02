package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.data.local.entity.DailyHabitLogEntity
import com.axiom.app.ui.theme.*

@Composable
fun DailyHabitNudgeSection(
    log: DailyHabitLogEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val isFa = java.util.Locale.getDefault().language == "fa"

    val water = log?.waterGlasses ?: 0
    val isSleepLogged = log?.sleepHours != null
    val sleepText = if (isSleepLogged) "${log?.sleepHours}h" else (if (isFa) "ثبت‌نشده" else "Pending")

    var teethCount = 0
    if (log?.teethMorning == true) teethCount++
    if (log?.teethEvening == true) teethCount++

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("daily_habit_nudge_widget"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = BorderStroke(1.dp, LegendaryGold.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFa) "پروتکل‌های حیاتی روزانه" else "DAILY HABIT BUFFER",
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LegendaryGold
                )

                Text(
                    text = if (isFa) "به‌روزرسانی >" else "UPDATE CHECKIN >",
                    fontFamily = FiraCode,
                    fontSize = 10.sp,
                    color = LegendaryGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (isFa) "حفظ پایداری بیولوژیکی عملکرد تمرکزی شما را بهینه نگه می‌دارد." else "Optimizing biological buffers ensures peak throughput.",
                fontFamily = Inter,
                fontSize = 11.sp,
                color = colors.textDim
            )

            HorizontalDivider(color = BorderFaint.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Water Item
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isFa) "آب" else "WATER",
                        fontFamily = FiraCode,
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$water / 8 gl",
                        fontFamily = FiraCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (water >= 8) colors.systemGreen else LegendaryGold
                    )
                }

                // Sleep Item
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isFa) "خواب" else "SLEEP",
                        fontFamily = FiraCode,
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sleepText,
                        fontFamily = FiraCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSleepLogged) colors.systemGreen else colors.penaltyRed
                    )
                }

                // Teeth Item
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isFa) "دندان" else "TEETH",
                        fontFamily = FiraCode,
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$teethCount / 2",
                        fontFamily = FiraCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (teethCount >= 2) colors.systemGreen else LegendaryGold
                    )
                }
            }
        }
    }
}
