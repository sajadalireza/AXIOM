package com.axiom.app.presentation.skilltree

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.ui.SkillTreeViewModel
import com.axiom.app.ui.SkillTreeUiState
import com.axiom.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailPanel(
    viewModel: SkillTreeViewModel,
    onNavigateToAddMission: (skillId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.skillsState.collectAsStateWithLifecycle()
    val selectedSkillId by viewModel.selectedSkillId.collectAsStateWithLifecycle()
    
    val skillsList = when (val s = uiState) {
        is SkillTreeUiState.Success -> s.skills
        else -> emptyList()
    }
    
    val selectedSkill = skillsList.firstOrNull { it.id == selectedSkillId }
    
    if (selectedSkill != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.clearSelection() },
            containerColor = LocalAxiomColors.current.voidBlack,
            contentColor = TextPrimary,
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Skill Name in DisplayM
                Text(
                    text = selectedSkill.name.uppercase(),
                    style = DisplayM,
                    color = SystemGreen
                )
                
                // Description in BodyMedium
                val desc = "An awakened ${selectedSkill.category} routine of ${selectedSkill.name}. Dedicate focus sessions and complete corresponding missions to overclock this node."
                Text(
                    text = desc,
                    style = BodyMedium,
                    color = TextSecondary
                )
                
                // Level Progress Bar and XP cost
                val xpToNext = selectedSkill.xpToNextRank
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LEVEL ${selectedSkill.level}  •  ${selectedSkill.rankLabel}",
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "XP TO NEXT RANK: $xpToNext XP",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = LegendaryGold
                    )
                }
                
                LinearProgressIndicator(
                    progress = { selectedSkill.rankProgressPercent.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = SystemGreen,
                    trackColor = BorderFaint
                )
                
                // "Unlock" / "Level Up" button in SystemGreen
                Button(
                    onClick = {
                        viewModel.upgradeOrUnlockSkill(selectedSkill.id)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SystemGreen,
                        contentColor = LocalAxiomColors.current.voidBlack
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    val btnText = if (selectedSkill.isUnlocked) "LEVEL UP" else "UNLOCK"
                    Text(text = btnText, fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                }
                
                // Connected skills listed as small pill chips
                val connectedSkills = skillsList.filter { it.parentId == selectedSkill.id || it.id == selectedSkill.parentId }
                if (connectedSkills.isNotEmpty()) {
                    Text(
                        text = "CONNECTED SKILLS",
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
                        connectedSkills.forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BorderFaint.copy(alpha = 0.15f))
                                    .border(1.dp, BorderFaint, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.selectSkill(skill.id) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = skill.name.uppercase(),
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
