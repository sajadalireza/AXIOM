package com.axiom.app.presentation.missions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.axiom.app.domain.model.Skill
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.theme.*
import com.axiom.app.ui.getLocalizedSkillName

@Composable
fun AddMissionStep2Category(
    skills: List<Skill>,
    selectedSkill: Skill?,
    onSkillSelect: (Skill) -> Unit,
    selectedTrack: String,
    onTrackSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tracks = listOf("Wealth", "Capability", "Discovery", "Network")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // LINKED SKILL SELECTOR
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.add_mission_linked_skill),
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.forEach { skill ->
                    val isSelected = selectedSkill?.id == skill.id
                    HolographicCard(
                        modifier = Modifier
                            .widthIn(min = 120.dp)
                            .height(44.dp)
                            .clickable { onSkillSelect(skill) }
                            .testTag("skill_chip_${skill.id}"),
                        accentColor = if (isSelected) SystemGreen else BorderFaint,
                        glowEnabled = isSelected
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getLocalizedSkillName(skill.name).uppercase(),
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) SystemGreen else TextDim,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // ALIGNMENT TRACK SELECTOR
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.add_mission_alignment),
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tracks.forEach { tr ->
                    val isSelected = selectedTrack == tr
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) SystemGreen.copy(alpha = 0.15f) else ShadowSurface)
                            .border(
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) SystemGreen else BorderFaint
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onTrackSelect(tr) }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tr.uppercase(),
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) SystemGreen else TextSecondary
                        )
                    }
                }
            }
        }
    }
}
