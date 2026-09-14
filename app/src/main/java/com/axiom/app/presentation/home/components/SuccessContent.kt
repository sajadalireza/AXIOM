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

/**
 * High-fidelity, calm premium Home content architecture.
 *
 * Implements the 4-layer hierarchy specified in WP-UIUX-02:
 * - LAYER 1 — NOW: Compact Hunter identity, Next Meaningful Mission (single primary CTA), active mission state.
 * - LAYER 2 — TODAY: Daily Outcomes, Next Best Action, Daily Habit Nudge, Vitals Row, Program Countdown.
 * - LAYER 3 — PROGRESS: Momentum & Streak 7-day timeline, Weekly Review Overdue, Weekly Challenges, Body Recovery Status.
 * - LAYER 4 — SECONDARY / PROGRESSIVE DISCLOSURE: Operational Tracks, System Feed inside collapsible section.
 *
 * Preserves 100% of existing Home capabilities with zero data/domain mutations.
 */
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

    val actualStartDate = remember(vehicleProgramStartDate) {
        if (vehicleProgramStartDate == 0L) System.currentTimeMillis() else vehicleProgramStartDate
    }

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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─────────────────────────────────────────────────────────────────
            // LAYER 1 — NOW (Identity, Center of Gravity, Primary Action)
            // ─────────────────────────────────────────────────────────────────
            item(key = "identity") {
                HunterHeaderSection(
                    hunter = state.hunter,
                    streakDays = state.streakDays,
                    streakMultiplier = state.streakMultiplier,
                    onNavigateToProfile = { onNavigate(Screen.Profile.route) }
                )
            }

            item(key = "next_meaningful_mission") {
                val primaryMission = state.topMissions.firstOrNull()
                NextMissionHeroCard(
                    mission = primaryMission,
                    dungeons = state.dungeons,
                    onPrimaryAction = { missionId ->
                        if (missionId != null) {
                            onNavigate(Screen.MissionDetail(missionId).route)
                        } else {
                            onNavigate(Screen.AddMission.route)
                        }
                    },
                    onViewAllMissions = {
                        onNavigate(Screen.Missions.route)
                    }
                )
            }

            if (state.topMissions.size > 1) {
                item(key = "other_missions") {
                    ActiveMissionStrip(
                        topMissions = state.topMissions.drop(1),
                        dungeons = state.dungeons,
                        isRestMode = false,
                        onNavigateToMissionDetail = { id -> onNavigate(Screen.MissionDetail(id).route) },
                        onCompleteMission = { id -> missionsViewModel.completeMission(id, null) },
                        onDeleteMission = { id -> missionsViewModel.deleteMission(id) }
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────────
            // LAYER 2 — TODAY (Daily Outcomes, Next Best Action, Habits, Vitals)
            // ─────────────────────────────────────────────────────────────────
            item(key = "daily_outcomes") {
                DailyOutcomesSection()
            }

            state.nextBestAction?.let { action ->
                item(key = "next_best_action") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.shadowSurface)
                            .border(1.dp, colors.borderFaint, RoundedCornerShape(16.dp))
                            .clickable { state.nextBestActionRoute?.let { onNavigate(it) } }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(36.dp)
                                    .background(colors.systemGreen, RoundedCornerShape(1.5.dp))
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.home_next_action),
                                    fontFamily = Outfit,
                                    fontSize = 11.sp,
                                    color = colors.systemGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    text = action,
                                    fontFamily = Outfit,
                                    fontSize = 14.sp,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            item(key = "daily_habits") {
                DailyHabitNudgeSection(
                    log = todayHabitLog,
                    onClick = { onNavigate(Screen.DailyCheckin.route) }
                )
            }

            item(key = "vitals") {
                com.axiom.app.ui.components.VitalsRow(
                    viewModel = vitalsViewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (vehicleProgramStartDate != 0L) {
                item(key = "countdown_banner") {
                    CountdownBannerSection(
                        programStartDate = actualStartDate,
                        onEditProgramStart = { datePickerDialog.show() }
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────────
            // LAYER 3 — PROGRESS (Streak 7-Day Timeline, Challenges, Body Status)
            // ─────────────────────────────────────────────────────────────────
            item(key = "momentum_streak") {
                MomentumStreakSection(
                    streakDays = state.streakDays
                )
            }

            if (isReviewOverdue) {
                item(key = "weekly_review_overdue") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.WeeklyReview.route) }
                            .testTag("weekly_review_overdue_banner"),
                        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.legendaryGold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("⚠️", fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.home_weekly_review_overdue_title),
                                    fontFamily = Outfit,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.legendaryGold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.home_weekly_review_overdue_desc),
                                    fontFamily = Outfit,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                            Text(
                                text = stringResource(R.string.home_weekly_review_start),
                                fontFamily = Outfit,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.legendaryGold,
                                modifier = Modifier
                                    .background(colors.legendaryGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            item(key = "weekly_challenges") {
                WeeklyChallengeSection(
                    challenges = challenges,
                    allClaimed = weekly.allClaimed,
                    onClaimBonus = { axiomViewModel.claimWeeklyBonus() }
                )
            }

            item(key = "body_status") {
                BodyStatusSection(
                    muscles = muscles,
                    onNavigateToBodyMap = { onNavigate(Screen.BodyMap.route) }
                )
            }

            // ─────────────────────────────────────────────────────────────────
            // LAYER 4 — SECONDARY / PROGRESSIVE DISCLOSURE (Operational Tracks, System Feed)
            // ─────────────────────────────────────────────────────────────────
            item(key = "secondary_surfaces") {
                SecondarySurfacesSection(initiallyExpanded = false) {
                    if (vehicleProgramStartDate == 0L) {
                        CountdownBannerSection(
                            programStartDate = actualStartDate,
                            onEditProgramStart = { datePickerDialog.show() }
                        )
                    }

                    OperationalTracksSection(
                        onNavigate = onNavigate
                    )

                    SystemFeedSection(recentFeed = state.recentFeed)
                }
            }
        }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .border(1.dp, colors.penaltyRed, RoundedCornerShape(12.dp))
                .background(colors.shadowSurface)
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.home_system_error),
                fontFamily = Outfit,
                fontSize = 15.sp,
                color = colors.penaltyRed,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                fontFamily = Outfit,
                fontSize = 13.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
