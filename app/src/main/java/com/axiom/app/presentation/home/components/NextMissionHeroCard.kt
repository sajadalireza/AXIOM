package com.axiom.app.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

/**
 * Visual and operational center of gravity on the Home screen.
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
            .testTag("next_mission_hero_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.systemGreen.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Category Label & Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors.systemGreen)
                    )
                    Text(
                        text = stringResource(R.string.home_hero_label),
                        fontFamily = FiraCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.systemGreen,
                        letterSpacing = 1.5.sp
                    )
                }

                if (mission?.isTimedMission == true) {
                    Text(
                        text = "TIMED SPRINT",
                        fontFamily = FiraCode,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.legendaryGold,
                        modifier = Modifier
                            .background(colors.legendaryGold.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .border(1.dp, colors.legendaryGold.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (mission != null) {
                // Mission Title
                Text(
                    text = mission.title,
                    fontFamily = Inter,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 24.sp,
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.dimSurface)
                            .border(1.dp, colors.borderFaint, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
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
                            fontFamily = FiraCode,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Execution Context details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val durationMin = (mission.estimatedHours * 60).toInt()
                    Text(
                        text = "⏱ $durationMin min",
                        fontFamily = FiraCode,
                        fontSize = 12.sp,
                        color = colors.textDim
                    )
                    Text(
                        text = "⚡ +${mission.xpReward} XP",
                        fontFamily = FiraCode,
                        fontSize = 12.sp,
                        color = colors.legendaryGold
                    )
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
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_hero_start_mission),
                        fontFamily = FiraCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Subordinate Secondary Link
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_hero_view_all_missions),
                        fontFamily = FiraCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textDim,
                        modifier = Modifier
                            .clickable { onViewAllMissions() }
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }
            } else {
                // Empty state: Guide to commit to the next mission
                Text(
                    text = stringResource(R.string.home_hero_no_mission_desc),
                    fontFamily = Inter,
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
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_hero_commit_mission),
                        fontFamily = FiraCode,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
