package com.axiom.app.presentation.profile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.CharacterStatsUiState
import com.axiom.app.ui.CharacterStatsViewModel
import com.axiom.app.ui.theme.*
import androidx.compose.ui.res.painterResource
import com.axiom.app.R

@Composable
fun StatsTabContent(
    hunter: Hunter,
    skills: List<com.axiom.app.domain.model.Skill>,
    shadowCount: Int,
    currentStreak: Int,
    longestStreak: Int,
    onNavigateToSkillTree: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterStatsViewModel = hiltViewModel()
) {
    val state by viewModel.statsState.collectAsStateWithLifecycle()
    val colors = LocalAxiomColors.current
    val rawColor = rankColorMap[hunter.rankLabel] ?: Color(hunter.rankColor)
    val rankColor = if (hunter.rankLabel.contains("s", ignoreCase = true)) LegendaryGold else rawColor

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "CHARACTER ATTRIBUTES",
            style = HudM,
            color = rankColor,
            letterSpacing = 1.sp
        )

        when (val s = state) {
            is CharacterStatsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SystemGreen)
                }
            }
            is CharacterStatsUiState.Success -> {
                val stats = s.stats

                // Render 7 stats Focus, Consistency, Strength, Intelligence, Finance, Social, Discipline
                CharacterStatBar("Focus", stats.focus, rankColor)
                CharacterStatBar("Consistency", (stats.focus + stats.execution) / 2, rankColor)
                CharacterStatBar("Strength", stats.fitness, rankColor)
                CharacterStatBar("Intelligence", stats.knowledge, rankColor)
                CharacterStatBar("Finance", stats.business, rankColor)
                CharacterStatBar("Social", stats.creativity, rankColor)
                CharacterStatBar("Discipline", stats.execution, rankColor)
            }
            is CharacterStatsUiState.Error -> {
                Text("Error loading stats.", style = LabelS, color = PenaltyRed)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Streak & Shadow Record Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Shadow Army Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("SHADOW ARMY", style = LabelS, color = TextDim)
                    Text("$shadowCount", style = HudL, color = SystemGreen)
                    Text("ACTIVE AGENTS", style = LabelS, color = TextDim)
                }
            }

            // Streak Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("STREAK RECORD", style = LabelS, color = TextDim)
                    Text("$currentStreak / $longestStreak", style = HudM, color = SystemGreen)
                    Text("DAYS CURRENT/MAX", style = LabelS, color = TextDim)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Skill Level Breakdown
        Text(
            text = "SKILL LEVEL BREAKDOWN",
            style = HudM,
            color = rankColor,
            letterSpacing = 1.sp
        )

        if (skills.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                border = BorderStroke(1.dp, BorderFaint)
            ) {
                Text(
                    text = "No skills unlocked in your skill tree yet.",
                    style = LabelS,
                    color = TextDim,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                skills.sortedByDescending { it.level }.forEach { skill ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                        border = BorderStroke(1.dp, BorderFaint)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(skill.name, style = LabelL, color = TextPrimary)
                                Text(skill.category.uppercase(), style = LabelS, color = TextDim)
                            }
                            Text("LV. ${skill.level}", style = HudS, color = SystemGreen)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToSkillTree,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, SystemGreen)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_nav_skills),
                contentDescription = null,
                tint = SystemGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("VIEW SKILL TREE", fontFamily = FiraCode, color = SystemGreen)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun CharacterStatBar(
    label: String,
    value: Int,
    rankColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = (value.coerceIn(0, 100).toFloat() / 100f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stat_$label"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = LabelL,
                color = TextPrimary
            )
            Text(
                text = "$value/100",
                style = HudS,
                color = rankColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BorderFaint.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedValue)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                rankColor.copy(alpha = 0.4f),
                                rankColor
                            )
                        )
                    )
            )
        }
    }
}
