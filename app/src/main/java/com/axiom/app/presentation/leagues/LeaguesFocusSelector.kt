package com.axiom.app.presentation.leagues

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.BuildConfig
import com.axiom.app.R
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.LeaguesViewModel
import com.axiom.app.ui.theme.*

@Composable
fun InactiveFocusLayout(
    activeMissions: List<Mission>,
    isFastSyncEnabled: Boolean,
    viewModel: LeaguesViewModel,
    axiomColors: AxiomColorScheme,
    onNavigate: (String) -> Unit
) {
    var selectedMissionIndex by remember { mutableStateOf(0) }
    val isFa = java.util.Locale.getDefault().language == "fa"
    val durations = remember {
        if (BuildConfig.DEBUG) listOf(1, 10, 25, 50) else listOf(10, 25, 50)
    }
    var selectedDurationIndex by remember { 
        mutableStateOf(if (BuildConfig.DEBUG) 2 else 1) // Default to 25 Min
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isFa) "◈ پروتکل شناختی تمرکز تاکتیکی" else "◈ TACTICAL FOCUS COGNITIVE PROTOCOL",
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = axiomColors.systemGreen
            )

            // Fast sync demo toggle - only in Debug Mode
            if (BuildConfig.DEBUG) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.toggleFastTimeSync() }
                ) {
                    Text(
                        text = "DEBUG: FAST TIMING",
                        fontFamily = JetBrainsMono,
                        fontSize = 9.sp,
                        color = if (isFastSyncEnabled) axiomColors.systemGreen else axiomColors.textDim
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = isFastSyncEnabled,
                        onCheckedChange = { viewModel.toggleFastTimeSync() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = axiomColors.systemGreen,
                            checkedTrackColor = axiomColors.systemGreen.copy(alpha = 0.3f),
                            uncheckedThumbColor = axiomColors.textDim,
                            uncheckedTrackColor = TextPrimary.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.scale(0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (activeMissions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                    .background(axiomColors.voidBlack)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FactCheck,
                        contentDescription = null,
                        tint = axiomColors.textDim,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.league_no_active_gates),
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = axiomColors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.league_add_mission_hint),
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        color = axiomColors.textDim,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigate(com.axiom.app.navigation.Screen.Missions.route) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = axiomColors.systemGreen
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = if (isFa) "[ تسخیر درگاه‌های فعال ]" else "[ CONQUER ACTIVE GATES ]",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = axiomColors.voidBlack
                        )
                    }
                }
            }
        } else {
            Text(
                text = if (isFa) "پروتکل مأموریت بیدار فعال را جهت همگام‌سازی انتخاب کنید:" else "SELECT ACTIVE GATE PROTOCOL TO SINK WITH:",
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = axiomColors.textDim
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Box for selected mission
            val currentMissionSelection = activeMissions.getOrNull(selectedMissionIndex) ?: activeMissions.first()
            
            // Selector dropdown / row of active missions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                    .background(axiomColors.voidBlack)
                    .clickable {
                        selectedMissionIndex = (selectedMissionIndex + 1) % activeMissions.size
                    }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterFrames,
                            contentDescription = null,
                            tint = axiomColors.systemGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = currentMissionSelection.title,
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = axiomColors.textPrimary
                            )
                            Text(
                                text = if (isFa) "طبقه‌بندی کمیابی: ${currentMissionSelection.rarity} | جایزه: ${currentMissionSelection.xpReward}+ تجربه" else "Classified Rarity: ${currentMissionSelection.rarity} | Reward: +${currentMissionSelection.xpReward} XP",
                                fontFamily = JetBrainsMono,
                                fontSize = 9.sp,
                                color = axiomColors.textDim
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Switch gate selection",
                            tint = axiomColors.systemGreen.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isFa) "چرخش مأموریت" else "CYCLE GATES",
                            fontFamily = JetBrainsMono,
                            fontSize = 7.sp,
                            color = axiomColors.textDim
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isFa) "زمان دوره‌ها (به دقیقه):" else "DURATION LIMITS (MINUTES):",
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = axiomColors.textDim
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Duration selector buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                durations.forEachIndexed { index, duration ->
                    val isSelected = selectedDurationIndex == index
                    val isDemo = duration == 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) axiomColors.systemGreen else BorderFaint,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .background(
                                if (isSelected) axiomColors.systemGreen.copy(alpha = 0.12f) else axiomColors.voidBlack
                            )
                            .clickable { selectedDurationIndex = index }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDemo) (if (isFa) "۱ دقیقه (تست)" else "1 MIN (TEST)") else (if (isFa) "$duration دقیقه" else "${duration}M"),
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) axiomColors.systemGreen else axiomColors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    val duration = durations[selectedDurationIndex]
                    viewModel.startFocusProtocol(currentMissionSelection, duration)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = axiomColors.systemGreen
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("initiate_focus_btn")
            ) {
                Text(
                    text = if (isFa) "[ ورود به سلول تمرکز حواس عصبی ]" else "[ ENTER NEURAL CONCENTRATION CHAMBER ]",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = axiomColors.voidBlack,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
