package com.axiom.app.presentation.missions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.theme.*

@Composable
fun AddMissionStep4Protocols(
    dungeons: List<Dungeon>,
    selectedDungeon: Dungeon?,
    onDungeonSelect: (Dungeon?) -> Unit,
    isDungeonDropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    isInstantGate: Boolean,
    onInstantGateChange: (Boolean) -> Unit,
    logAsCompleted: Boolean,
    onLogAsCompletedChange: (Boolean) -> Unit,
    goalSet: Boolean,
    onGoalSetChange: (Boolean) -> Unit,
    gotFeedback: Boolean,
    onGotFeedbackChange: (Boolean) -> Unit,
    pushedComfortZone: Boolean,
    onPushedComfortZoneChange: (Boolean) -> Unit,
    isFormValid: Boolean,
    onDeployClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val qualityPercent = ((if (goalSet) 1 else 0) + (if (gotFeedback) 1 else 0) + (if (pushedComfortZone) 1 else 0)) * 100 / 3

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // OPTIONAL DUNGEON ASSIGNMENT
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.add_mission_assign_dungeon),
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                    .background(ShadowSurface)
                    .clickable { onDropdownExpandedChange(true) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDungeon?.name ?: "[ NO ACTIVE RAID ]",
                        fontFamily = Inter,
                        color = if (selectedDungeon != null) TextPrimary else TextDim,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = TextSecondary
                    )
                }
                DropdownMenu(
                    expanded = isDungeonDropdownExpanded,
                    onDismissRequest = { onDropdownExpandedChange(false) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(ShadowSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("[ NO ACTIVE RAID ]", fontFamily = Inter, color = TextDim) },
                        onClick = {
                            onDungeonSelect(null)
                            onDropdownExpandedChange(false)
                        }
                    )
                    dungeons.filter { !it.isCompleted }.forEach { dungeon ->
                        DropdownMenuItem(
                            text = { Text(dungeon.name, fontFamily = Inter, color = TextPrimary) },
                            onClick = {
                                onDungeonSelect(dungeon)
                                onDropdownExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }

        // INSTANT GATE PROTOCOL (TIMED RAIDS)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isInstantGate) LegendaryGold.copy(alpha = 0.08f) else ShadowSurface)
                    .border(
                        width = 1.dp,
                        color = if (isInstantGate) LegendaryGold else BorderFaint,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onInstantGateChange(!isInstantGate) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = stringResource(R.string.instant_gate_protocol_title),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = if (isInstantGate) LegendaryGold else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.instant_gate_protocol_desc),
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }
                Switch(
                    checked = isInstantGate,
                    onCheckedChange = onInstantGateChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LegendaryGold,
                        checkedTrackColor = LegendaryGold.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DimSurface
                    )
                )
            }
        }

        // DELIBERATE PRACTICE LOG-AS-COMPLETED SESSION
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (logAsCompleted) SystemGreen.copy(alpha = 0.08f) else ShadowSurface)
                    .border(
                        width = 1.dp,
                        color = if (logAsCompleted) SystemGreen else BorderFaint,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onLogAsCompletedChange(!logAsCompleted) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "LOG AS COMPLETED SESSION",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = if (logAsCompleted) SystemGreen else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Directly commit completed deliberate practice hours to Discipline mastery.",
                        fontFamily = Inter,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }
                Switch(
                    checked = logAsCompleted,
                    onCheckedChange = onLogAsCompletedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SystemGreen,
                        checkedTrackColor = SystemGreen.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DimSurface
                    )
                )
            }
        }

        if (logAsCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShadowSurface)
                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "DELIBERATE PRACTICE QUESTIONS",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen
                    )

                    // Question 1
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Did you have a specific goal for this session?",
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (goalSet) "Yes (Highly targeted practice)" else "No (Generic practice)",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = if (goalSet) SystemGreen else TextDim
                            )
                            Switch(
                                checked = goalSet,
                                onCheckedChange = onGoalSetChange,
                                colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen)
                            )
                        }
                    }

                    // Question 2
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Did you get feedback or measure your result?",
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (gotFeedback) "Yes (Obtained metric/external review)" else "No (No feedback channel)",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = if (gotFeedback) SystemGreen else TextDim
                            )
                            Switch(
                                checked = gotFeedback,
                                onCheckedChange = onGotFeedbackChange,
                                colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen)
                            )
                        }
                    }

                    // Question 3
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Did this push you outside your comfort zone?",
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (pushedComfortZone) "Yes (Stretching boundaries)" else "No (Routine/Comfort zone)",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = if (pushedComfortZone) SystemGreen else TextDim
                            )
                            Switch(
                                checked = pushedComfortZone,
                                onCheckedChange = onPushedComfortZoneChange,
                                colors = SwitchDefaults.colors(checkedThumbColor = SystemGreen)
                            )
                        }
                    }

                    HorizontalDivider(color = BorderFaint)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SESSION QUALITY PREVIEW",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Quality: $qualityPercent%",
                            fontFamily = JetBrainsMono,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegendaryGold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // DEPLOY MISSION SUBMIT BUTTON (styled inside holographic card)
        val submitHaptic = LocalHapticFeedback.current

        HolographicCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("btn_accept_mission")
                .let {
                    if (isFormValid) {
                        it.clickable {
                            submitHaptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDeployClick()
                        }
                    } else {
                        it
                    }
                },
            accentColor = if (isFormValid) SystemGreen else BorderFaint,
            glowEnabled = isFormValid
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isFormValid) SystemGreen else DimSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.add_mission_accept),
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isFormValid) VoidBlack else TextSecondary
                )
            }
        }
    }
}
