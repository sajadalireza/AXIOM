package com.axiom.app.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.theme.Fraunces
import com.axiom.app.ui.theme.LegendaryGold
import com.axiom.app.ui.theme.VoidBlack
import com.axiom.app.ui.theme.PenaltyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyAnalyticsScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {},
    viewModel: WeeklyAnalyticsViewModel = hiltViewModel()
) {
    val completedMissions by viewModel.completedMissions.collectAsStateWithLifecycle()
    val habits by viewModel.last7DaysHabits.collectAsStateWithLifecycle()
    val kpis by viewModel.customKPIs.collectAsStateWithLifecycle()
    val progress by viewModel.kpiProgress.collectAsStateWithLifecycle()
    val missStreaks by viewModel.kpiMissStreaks.collectAsStateWithLifecycle()
    val streak by viewModel.streakFlow.collectAsStateWithLifecycle()
    val aiSummary by viewModel.aiSummary.collectAsStateWithLifecycle()
    val lastReviewTimestamp by viewModel.lastReviewTimestampFlow.collectAsStateWithLifecycle(initialValue = 0L)

    val limit = System.currentTimeMillis() - 7 * 86400000L
    val missionsLast7Days = completedMissions.count { (it.completedAt ?: 0L) >= limit }
    val performanceScore = viewModel.calculatePerformanceScore(missionsLast7Days, streak, habits)
    val (wins, missPair) = viewModel.getWeeklyWinsAndMisses(completedMissions, habits, kpis, progress, missStreaks, streak)
    
    val isReviewOverdue = lastReviewTimestamp == 0L || (System.currentTimeMillis() - lastReviewTimestamp) > 7 * 86400000L

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OPERATIONAL ANALYTICS", color = LegendaryGold, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = Fraunces) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LegendaryGold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidBlack)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { WeeklyNarrativeCard(viewModel.getWeekDebriefTitle(), aiSummary, performanceScore) }
            item { ActivityHeatmapChart(viewModel.getLast7DaysLabels(), viewModel.getMissionsCountPerDay(completedMissions)) }
            item { MissionCompletionSparkline(viewModel.getCompletedMissionsLast4Weeks(completedMissions)) }
            item { XpGainBarChart(viewModel.getLast7DaysLabels(), viewModel.getXpGainedPerDay(completedMissions)) }
            item { StreakConsistencyHeatRing(streak) }
            item { WinsAndMissesSection(wins, missPair.first, missPair.second) }
            
            if (isReviewOverdue) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("weekly_review_overdue_card"),
                        colors = CardDefaults.cardColors(containerColor = PenaltyRed.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PenaltyRed)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = "Overdue", tint = PenaltyRed)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("YOUR WEEKLY REVIEW IS OVERDUE", color = PenaltyRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Take 5 minutes to realign your tactical protocols and strip away any lingering self-delusion.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigate(Screen.WeeklyReview.route) },
                                colors = ButtonDefaults.buttonColors(containerColor = PenaltyRed, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("BEGIN WEEKLY REVIEW PROTOCOL", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
