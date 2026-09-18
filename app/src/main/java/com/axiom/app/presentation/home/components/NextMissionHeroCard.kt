package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.theme.*
import kotlin.math.roundToInt

/**
 * Visual and operational center of gravity on the Home screen.
 * Matches binding PO visual target 01_home_primary_state.png.
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
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val isLargeFont = LocalDensity.current.fontScale > 1.3f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("next_mission_hero_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface.copy(alpha = 0.86f)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    colors.systemGreen.copy(alpha = 0.45f),
                    colors.borderFaint.copy(alpha = 0.25f)
                )
            )
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            // Header: Category Label ("ACTIVE MISSION" or "NEXT MEANINGFUL MISSION")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(colors.systemGreen)
                            .border(1.dp, colors.systemGreen.copy(alpha = 0.4f), CircleShape)
                    )
                    Text(
                        text = stringResource(R.string.home_hero_label),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.systemGreen,
                        letterSpacing = 1.sp
                    )
                }

                if (mission?.isTimedMission == true) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.legendaryGold.copy(alpha = 0.12f))
                            .border(1.dp, colors.legendaryGold.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_hero_timed_sprint),
                            fontFamily = Outfit,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.legendaryGold
                        )
                    }
                }
            }

            if (mission != null) {
                val connectedDungeon = mission.dungeonId?.let { id -> dungeons.find { it.id == id } }
                val projectOrGoalTitle = connectedDungeon?.name ?: mission.track.takeIf { it != "DEFAULT" }

                // Main Mission Body: [Icon Container] [Title + Subtitle] [Chevron]
                val titleMaxLines = if (isLargeFont) 4 else 2
                val goalMaxLines = if (isLargeFont) 3 else 1
                if (isLargeFont) {
                    // Accessibility layout: the title and goal connection own the full card width so
                    // essential mission context stays readable instead of being ellipsized away.
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MissionIconBlock(size = 40.dp, iconSize = 20.dp)
                        MissionTitleBlock(
                            mission = mission,
                            projectOrGoalTitle = projectOrGoalTitle,
                            titleMaxLines = titleMaxLines,
                            goalMaxLines = goalMaxLines
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MissionIconBlock(size = 52.dp, iconSize = 24.dp)
                        MissionTitleBlock(
                            mission = mission,
                            projectOrGoalTitle = projectOrGoalTitle,
                            titleMaxLines = titleMaxLines,
                            goalMaxLines = goalMaxLines,
                            modifier = Modifier.weight(1f)
                        )
                        // Circular Detail Chevron (Decorative only; 52.dp button is the single dominant CTA)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.dimSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Metadata Row: Duration | Track | XP Reward
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "⏱", fontSize = 12.sp)
                        Text(
                            text = durationText,
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary
                        )
                    }

                    val validTrack = mission.track.takeIf { it.isNotBlank() && it != "DEFAULT" }
                    if (validTrack != null) {
                        Text(text = "|", color = colors.borderFaint, fontSize = 11.sp)

                        // The truthful track value may wrap at high font scale; it is never sliced.
                        Text(
                            text = validTrack,
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textSecondary,
                            maxLines = if (isLargeFont) 2 else 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(text = "|", color = colors.borderFaint, fontSize = 11.sp)

                    Text(
                        text = stringResource(R.string.home_hero_xp_reward_format, mission.xpReward),
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.legendaryGold,
                        maxLines = 1
                    )
                }

                // THE SINGLE DOMINANT PRIMARY CTA — Glowing Emerald Pill Button
                Button(
                    onClick = { onPrimaryAction(mission.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("home_primary_cta"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = colors.voidBlack
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        colors.uncommonTeal,
                                        colors.systemGreen,
                                        lerp(colors.systemGreen, colors.shadowSurface, 0.12f)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                color = colors.voidBlack,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = colors.voidBlack,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

            } else {
                // Empty state: Guide to commit to the next mission
                Text(
                    text = stringResource(R.string.home_hero_no_mission_desc),
                    fontFamily = Outfit,
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )

                // THE SINGLE DOMINANT PRIMARY CTA FOR EMPTY STATE
                Button(
                    onClick = { onPrimaryAction(null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("home_primary_cta"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = colors.voidBlack
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        colors.uncommonTeal,
                                        colors.systemGreen,
                                        lerp(colors.systemGreen, colors.shadowSurface, 0.12f)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.home_hero_commit_mission),
                                fontFamily = Outfit,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.voidBlack,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = colors.voidBlack,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionIconBlock(size: Dp, iconSize: Dp) {
    val colors = LocalAxiomColors.current
    val shape = RoundedCornerShape(size * 0.27f)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(colors.systemGreen.copy(alpha = 0.12f))
            .border(1.dp, colors.systemGreen.copy(alpha = 0.35f), shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_nav_missions),
            contentDescription = null,
            tint = colors.systemGreen,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun MissionTitleBlock(
    mission: Mission,
    projectOrGoalTitle: String?,
    titleMaxLines: Int,
    goalMaxLines: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = mission.title,
            fontFamily = Outfit,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            lineHeight = 22.sp,
            maxLines = titleMaxLines,
            overflow = TextOverflow.Ellipsis
        )

        if (projectOrGoalTitle != null) {
            Text(
                text = stringResource(R.string.home_hero_advances_goal, projectOrGoalTitle),
                fontFamily = Outfit,
                fontSize = 11.sp,
                color = colors.textSecondary,
                fontWeight = FontWeight.Medium,
                maxLines = goalMaxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
