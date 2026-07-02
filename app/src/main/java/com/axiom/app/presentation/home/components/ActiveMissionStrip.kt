package com.axiom.app.presentation.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.components.MissionCardCompact
import com.axiom.app.ui.components.SwipeableMissionCard
import com.axiom.app.ui.theme.*

@Composable
fun ActiveMissionStrip(
    topMissions: List<Mission>,
    dungeons: List<Dungeon>,
    isRestMode: Boolean,
    onNavigateToMissionDetail: (String) -> Unit,
    onCompleteMission: (String) -> Unit,
    onDeleteMission: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header
        Text(
            text = stringResource(R.string.home_active_missions).uppercase(),
            fontFamily = FiraCode,
            fontSize = 11.sp,
            color = colors.textDim,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        if (topMissions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.dimSurface)
                    .border(1.dp, colors.borderFaint, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.home_awaiting_mission),
                    fontFamily = FiraCode,
                    fontSize = 13.sp,
                    color = colors.systemGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Take top 3 active missions
            val sortedMissions = remember(topMissions, isRestMode) {
                val base = if (isRestMode) {
                    topMissions.sortedBy { it.dungeonId == null }
                } else {
                    topMissions
                }
                base.take(3)
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                sortedMissions.forEach { mission ->
                    val dungeon = mission.dungeonId?.let { linkId ->
                        dungeons.find { it.id == linkId }
                    }
                    val dungeonName = dungeon?.name
                    val stageLabel = dungeon?.let { d ->
                        val parsedNames = d.stageDescriptions.split("||").map { it.trim() }
                        parsedNames.getOrNull(d.completedStages)?.takeIf { it.isNotBlank() }
                            ?: if (d.completedStages == d.totalStages - 1) "BOSS FIGHT" else "STAGE ${d.completedStages + 1}"
                    }
                    val isEssential = mission.dungeonId != null
                    val missionAlpha = if (isRestMode && !isEssential) 0.4f else 1f

                    // Swipable card wrapper
                    SwipeableMissionCard(
                        mission = mission,
                        onComplete = { onCompleteMission(mission.id) },
                        onDelete = { onDeleteMission(mission.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.voidBlack) // Prevent background overlap
                                .graphicsLayer { alpha = missionAlpha },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Compact card occupies the left weight
                            MissionCardCompact(
                                mission = mission,
                                onClick = { onNavigateToMissionDetail(mission.id) },
                                dungeonName = dungeonName,
                                stageLabel = stageLabel,
                                modifier = Modifier.weight(1f)
                            )

                            // One-Tap Quick-Complete action button
                            IconButton(
                                onClick = { onCompleteMission(mission.id) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(colors.dimSurface)
                                    .border(1.dp, colors.systemGreen.copy(alpha = 0.4f), CircleShape)
                                    .testTag("one_tap_complete_${mission.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Quick Complete",
                                    tint = colors.systemGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
