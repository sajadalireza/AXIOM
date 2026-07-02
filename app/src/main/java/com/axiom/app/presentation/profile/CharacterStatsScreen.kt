package com.axiom.app.presentation.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.ui.CharacterStatsUiState
import com.axiom.app.ui.CharacterStatsViewModel
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.theme.*

@Composable
fun CharacterStatsScreen(
    modifier: Modifier = Modifier,
    viewModel: CharacterStatsViewModel = hiltViewModel()
) {
    val state by viewModel.statsState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with left accent bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 20.dp, height = 3.dp)
                        .background(SystemGreen)
                )
                Text(
                    text = stringResource(R.string.stats_title),
                    fontFamily = JetBrainsMono,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SystemGreen
                )
            }
            Text(
                text = stringResource(R.string.stats_interface),
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = SystemGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            when (val s = state) {
                is CharacterStatsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SystemGreen)
                    }
                }
                is CharacterStatsUiState.Success -> {
                    val points = s.pointsAvailable

                    // Available points HU HolographicCard panel
                    HolographicCard(
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = if (points > 0) SystemGreen else BorderFaint,
                        glowEnabled = points > 0
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.stats_available_points),
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        color = TextDim,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (points > 0) {
                                        Text(
                                            text = stringResource(R.string.stats_alert),
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            color = LegendaryGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = stringResource(R.string.stats_earn_credits),
                                    fontFamily = Inter,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            // CounterAnimation (animateIntAsState)
                            val animatedPoints by animateIntAsState(
                                targetValue = points,
                                animationSpec = tween(1000, easing = EaseOutCubic),
                                label = "points_counter"
                            )

                            Text(
                                text = "+$animatedPoints",
                                fontFamily = JetBrainsMono,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (points > 0) SystemGreen else TextDim,
                                modifier = Modifier.testTag("points_available")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val normalizedMap = remember(s.stats) {
                        val maxVal = 100f
                        mapOf(
                            "STR" to (s.stats.fitness.toFloat() / maxVal).coerceIn(0f, 1f),
                            "INT" to (s.stats.knowledge.toFloat() / maxVal).coerceIn(0f, 1f),
                            "VIT" to (s.stats.focus.toFloat() / maxVal).coerceIn(0f, 1f),
                            "AGI" to (s.stats.execution.toFloat() / maxVal).coerceIn(0f, 1f),
                            "PER" to (s.stats.business.toFloat() / maxVal).coerceIn(0f, 1f),
                            "LUK" to (s.stats.creativity.toFloat() / maxVal).coerceIn(0f, 1f)
                        )
                    }

                    // Radar Chart centered
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        StatRadarChart(stats = normalizedMap)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats scrollable section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        StatAllocateRow(
                            name = stringResource(R.string.stat_execution_title),
                            statKey = "execution",
                            value = s.stats.execution,
                            description = stringResource(R.string.stat_execution_desc),
                            icon = Icons.Default.Speed,
                            glowColor = SystemGreen,
                            pointsLeft = points,
                            onAllocate = { viewModel.allocateStat("execution") }
                        )

                        StatAllocateRow(
                            name = stringResource(R.string.stat_focus_title),
                            statKey = "focus",
                            value = s.stats.focus,
                            description = stringResource(R.string.stat_focus_desc),
                            icon = Icons.Default.Favorite,
                            glowColor = RareBlue,
                            pointsLeft = points,
                            onAllocate = { viewModel.allocateStat("focus") }
                        )

                        StatAllocateRow(
                            name = stringResource(R.string.stat_knowledge_title),
                            statKey = "knowledge",
                            value = s.stats.knowledge,
                            description = stringResource(R.string.stat_knowledge_desc),
                            icon = Icons.Default.Psychology,
                            glowColor = EpicPurple,
                            pointsLeft = points,
                            onAllocate = { viewModel.allocateStat("knowledge") }
                        )

                        StatAllocateRow(
                            name = stringResource(R.string.stat_business_title),
                            statKey = "business",
                            value = s.stats.business,
                            description = stringResource(R.string.stat_business_desc),
                            icon = Icons.Default.Visibility,
                            glowColor = LegendaryGold,
                            pointsLeft = points,
                            onAllocate = { viewModel.allocateStat("business") }
                        )

                        StatAllocateRow(
                            name = stringResource(R.string.stat_fitness_title),
                            statKey = "fitness",
                            value = s.stats.fitness,
                            description = stringResource(R.string.stat_fitness_desc),
                            icon = Icons.Default.FitnessCenter,
                            glowColor = UncommonTeal,
                            pointsLeft = points,
                            onAllocate = { viewModel.allocateStat("fitness") }
                        )

                        StatAllocateRow(
                            name = stringResource(R.string.stat_creativity_title),
                            statKey = "creativity",
                            value = s.stats.creativity,
                            description = stringResource(R.string.stat_creativity_desc),
                            icon = Icons.Default.Star,
                            glowColor = RareBlue,
                            pointsLeft = points,
                            onAllocate = { viewModel.allocateStat("creativity") }
                        )

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
                is CharacterStatsUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.stats_error, s.message ?: ""),
                            color = PenaltyRed,
                            fontFamily = JetBrainsMono
                        )
                    }
                }
            }
        }
    }
}
