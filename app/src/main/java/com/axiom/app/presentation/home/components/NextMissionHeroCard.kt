package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.theme.*
import kotlin.math.roundToInt

/**
 * Visual and operational center of gravity on the Home screen.
 * Matches binding PO visual target 01_home_target.jpg.
 *
 * Enforces the AXIOM Product Constitution Section 3.5 invariant:
 * "هر Screen فقط یک Primary CTA دارد." (Every Screen has exactly one Primary CTA).
 *
 * Displays the Next Meaningful Mission with visible connection to Goal / Project progress
 * and hosts the single dominant Primary CTA ([testTag] = "home_primary_cta").
 */
@Composable
fun NextMissionHeroCard(
    mission: Mission?,
    dungeons: List<Dungeon> = emptyList(),
    onPrimaryAction: (missionId: String?) -> Unit,
    onViewAllMissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .testTag("next_mission_hero_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = BorderStroke(1.dp, colors.borderFaint),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Category Label ("ACTIVE MISSION" or "NEXT MEANINGFUL MISSION")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (mission != null) {
                        stringResource(R.string.home_hero_active_label)
                    } else {
                        stringResource(R.string.home_hero_label)
                    },
                    fontFamily = Outfit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.systemGreen,
                    letterSpacing = 1.sp
                )

                if (mission?.isTimedMission == true) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.legendaryGold.copy(alpha = 0.12f))
                            .border(1.dp, colors.legendaryGold.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_hero_timed_sprint),
                            fontFamily = Outfit,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.legendaryGold
                        )
                    }
                }
            }

            if (mission != null) {
                // Mission Title
                Text(
                    text = mission.title,
                    fontFamily = Outfit,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    lineHeight = 30.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Visible connection to Goal / Project
                val connectedDungeon = mission.dungeonId?.let { id -> dungeons.find { it.id == id } }
                val projectOrGoalTitle = connectedDungeon?.name ?: mission.track.takeIf { it != "DEFAULT" }

                if (projectOrGoalTitle != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.dimSurface)
                            .border(1.dp, colors.borderFaint, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "◈",
                            fontSize = 12.sp,
                            color = colors.legendaryGold
                        )
                        Text(
                            text = stringResource(R.string.home_hero_advances_goal, projectOrGoalTitle),
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Execution Context details: Canonical Duration & XP Reward
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val durationText = when {
                        mission.estimatedHours <= 0f -> stringResource(R.string.home_hero_duration_unspecified)
                        mission.estimatedHours < 1.0f -> {
                            val totalMinutes = (mission.estimatedHours * 60).roundToInt()
                            if (totalMinutes > 0) {
                                stringResource(R.string.home_hero_duration_minutes, totalMinutes)
                            } else {
                                stringResource(R.string.home_hero_duration_unspecified)
                            }
                        }
                        else -> {
                            val totalMinutes = (mission.estimatedHours * 60).roundToInt()
                            val hours = totalMinutes / 60
                            val remainingMins = totalMinutes % 60
                            if (remainingMins == 0) {
                                stringResource(R.string.home_hero_duration_hours, hours)
                            } else {
                                stringResource(R.string.home_hero_duration_hours_mins, hours, remainingMins)
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "⏱",
                            fontSize = 14.sp
                        )
                        Text(
                            text = durationText,
                            fontFamily = Outfit,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.systemGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_hero_xp_reward_format, mission.xpReward),
                            fontFamily = Outfit,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.systemGreen
                        )
                    }
                }

                // THE SINGLE DOMINANT PRIMARY CTA
                Button(
                    onClick = { onPrimaryAction(mission.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("home_primary_cta"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.systemGreen,
                        contentColor = colors.voidBlack
                    ),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text(
                        text = if (mission.status == "ACTIVE") {
                            stringResource(R.string.home_hero_continue_mission)
                        } else {
                            stringResource(R.string.home_hero_start_mission)
                        },
                        fontFamily = Outfit,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Subordinate Secondary Link (Direction-safe in RTL and LTR)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onViewAllMissions() }
                            .padding(vertical = 6.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_hero_view_all_missions),
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textDim
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = colors.textDim,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            } else {
                // Empty state: Guide to commit to the next mission
                Text(
                    text = stringResource(R.string.home_hero_no_mission_desc),
                    fontFamily = Outfit,
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    lineHeight = 20.sp
                )

                // THE SINGLE DOMINANT PRIMARY CTA FOR EMPTY STATE
                Button(
                    onClick = { onPrimaryAction(null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("home_primary_cta"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.systemGreen,
                        contentColor = colors.voidBlack
                    ),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_hero_commit_mission),
                        fontFamily = Outfit,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
