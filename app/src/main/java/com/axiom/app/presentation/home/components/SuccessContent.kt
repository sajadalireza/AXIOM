package com.axiom.app.presentation.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.WeeklyChallenge
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.HomeUiState
import com.axiom.app.ui.HomeViewModel
import com.axiom.app.ui.AxiomViewModel
import com.axiom.app.ui.MissionsViewModel
import com.axiom.app.ui.VitalsViewModel
import com.axiom.app.ui.theme.*
import java.util.Calendar

@Composable
fun SuccessContent(
    state: HomeUiState.Success,
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel,
    axiomViewModel: AxiomViewModel,
    vitalsViewModel: VitalsViewModel,
    missionsViewModel: MissionsViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val context = LocalContext.current
    val isFa = java.util.Locale.getDefault().language == "fa"

    val weekly by axiomViewModel.weeklyProgress.collectAsStateWithLifecycle()
    val muscles by viewModel.muscleGroups.collectAsStateWithLifecycle()
    val todayHabitLog by viewModel.todayHabitLog.collectAsStateWithLifecycle()

    val lastReviewTimestamp by viewModel.lastReviewTimestampFlow.collectAsStateWithLifecycle(initialValue = 0L)
    val vehicleProgramStartDate by viewModel.vehicleProgramStartDateFlow.collectAsStateWithLifecycle(initialValue = 0L)

    val actualStartDate = remember(vehicleProgramStartDate) { if (vehicleProgramStartDate == 0L) System.currentTimeMillis() else vehicleProgramStartDate }

    val isReviewOverdue = remember(lastReviewTimestamp) {
        val now = System.currentTimeMillis()
        lastReviewTimestamp == 0L || (now - lastReviewTimestamp) >= 7 * 86400000L
    }

    val datePickerDialog = remember(actualStartDate) {
        val calendar = Calendar.getInstance().apply { timeInMillis = actualStartDate }
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                viewModel.setVehicleProgramStartDate(selectedCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val challenges = remember(weekly, isFa) {
        if (isFa) {
            listOf(
                WeeklyChallenge("w_missions", "سهمیه مأموریت", "این هفته ۵ مأموریت را با موفقیت کامل کنید", 5, weekly.missionsDone, weekly.missionsDone >= 5),
                WeeklyChallenge("w_streak", "پروتکل استمرار", "زنجیره ۳ روزه متوالی مأموریت‌ها را حفظ کنید", 3, weekly.streakBest, weekly.streakBest >= 3),
                WeeklyChallenge("w_rare", "آماده‌سازی مأموریت کمیاب", "۱ مأموریت با سطح کمیاب یا بالاتر را تکمیل کنید", 1, weekly.rareDone, weekly.rareDone >= 1)
            )
        } else {
            listOf(
                WeeklyChallenge("w_missions", "MISSION QUOTA", "Complete 5 missions this week", 5, weekly.missionsDone, weekly.missionsDone >= 5),
                WeeklyChallenge("w_streak", "CONSISTENCY PROTOCOL", "Maintain a 3-day streak", 3, weekly.streakBest, weekly.streakBest >= 3),
                WeeklyChallenge("w_rare", "RARE EXTRACTION", "Complete 1 RARE+ mission", 1, weekly.rareDone, weekly.rareDone >= 1)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Zone 1: Identity
            item(key = "identity") {
                HunterHeaderSection(
                    hunter = state.hunter,
                    streakDays = state.streakDays,
                    streakMultiplier = state.streakMultiplier,
                    onNavigateToProfile = { onNavigate(Screen.Profile.route) },
                    onNavigateToPremium = { onNavigate(Screen.Premium.route) }
                )
            }

            // Zone 2: Urgency
            item(key = "urgency") {
                CountdownBannerSection(
                    programStartDate = actualStartDate,
                    onEditProgramStart = { datePickerDialog.show() }
                )
            }

            item(key = "quick_launch") {
                OperationalTracksSection(
                    onNavigate = onNavigate
                )
            }

            if (isReviewOverdue) {
                item(key = "review_overdue") {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigate(Screen.WeeklyReview.route) }.testTag("weekly_review_overdue_banner"),
                        colors = CardDefaults.cardColors(containerColor = colors.legendaryGold.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.legendaryGold.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("⚠️", fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("WEEKLY REVIEW RITUAL OVERDUE", fontFamily = FiraCode, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = colors.legendaryGold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Run the 6-Step Evaluation Protocol to strip away denial and commit weekly alignment.", fontFamily = Inter, fontSize = 11.sp, color = colors.textSecondary, lineHeight = 15.sp)
                            }
                            Text("[ START ]", fontFamily = FiraCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.legendaryGold, modifier = Modifier.background(colors.legendaryGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // Zone 3: Action
            item(key = "action") {
                ActiveMissionStrip(
                    topMissions = state.topMissions,
                    dungeons = state.dungeons,
                    isRestMode = false,
                    onNavigateToMissionDetail = { id -> onNavigate(Screen.MissionDetail(id).route) },
                    onCompleteMission = { id -> missionsViewModel.completeMission(id, null) },
                    onDeleteMission = { id -> missionsViewModel.deleteMission(id) }
                )
            }

            item(key = "daily_outcomes") { DailyOutcomesSection() }

            state.nextBestAction?.let { action ->
                item(key = "next_best_action") {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(colors.shadowSurface).border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp)).clickable { state.nextBestActionRoute?.let { onNavigate(it) } }) {
                        Box(modifier = Modifier.align(Alignment.CenterStart).width(3.dp).fillMaxHeight().background(colors.systemGreen))
                        Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 12.dp, top = 10.dp, bottom = 10.dp)) {
                            Text(text = stringResource(R.string.home_next_action), fontFamily = FiraCode, fontSize = 9.sp, color = colors.systemGreen, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(Modifier.height(3.dp))
                            Text(text = action, fontFamily = Inter, fontSize = 13.sp, color = colors.textPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Zone 4: Progress
            item(key = "weekly_challenge") {
                WeeklyChallengeSection(
                    challenges = challenges,
                    allClaimed = weekly.allClaimed,
                    onClaimBonus = { axiomViewModel.claimWeeklyBonus() }
                )
            }

            item(key = "daily_habit_nudge") {
                DailyHabitNudgeSection(
                    log = todayHabitLog,
                    onClick = { onNavigate(Screen.DailyCheckin.route) }
                )
            }

            item(key = "vitals_row") {
                com.axiom.app.ui.components.VitalsRow(
                    viewModel = vitalsViewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Zone 5: Recovery
            item(key = "body_status") {
                BodyStatusSection(
                    muscles = muscles,
                    onNavigateToBodyMap = { onNavigate(Screen.BodyMap.route) }
                )
            }

            // Zone 6: Intel
            item(key = "system_feed") { SystemFeedSection(recentFeed = state.recentFeed) }
        }

        // Floating HomeActionBar fixed to bottom center of the container
        HomeActionBar(
            onFocusClick = { onNavigate(Screen.Missions.route) },
            onCheckInClick = { onNavigate(Screen.DailyCheckin.route) },
            onAddMissionClick = { onNavigate(Screen.AddMission.route) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}

@Composable
fun LoadingShimmerScreen() {
    val colors = LocalAxiomColors.current
    Box(modifier = Modifier.fillMaxSize().background(colors.voidBlack)) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = colors.systemGreen)
    }
}

@Composable
fun ErrorScreen(message: String) {
    val colors = LocalAxiomColors.current
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.border(1.dp, colors.penaltyRed, RoundedCornerShape(4.dp)).background(colors.shadowSurface).padding(24.dp)) {
            Text(text = stringResource(R.string.home_system_error), fontFamily = FiraCode, fontSize = 15.sp, color = colors.penaltyRed, fontWeight = FontWeight.Bold)
            Text(text = message, fontFamily = Inter, fontSize = 13.sp, color = colors.textSecondary, textAlign = TextAlign.Center)
        }
    }
}
