package com.axiom.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.HomeUiState
import com.axiom.app.ui.HomeViewModel
import com.axiom.app.ui.AxiomViewModel
import com.axiom.app.ui.MissionsViewModel
import com.axiom.app.ui.VitalsViewModel
import com.axiom.app.ui.theme.*

/**
 * Product Owner Primary State Home content architecture.
 *
 * Implements the binding visual hierarchy and canonical Module Disposition:
 * - LAYER 1 — NOW: AXIOM Brand Header, Compact Hunter Profile + Gold Level/XP Ring,
 *   Next Meaningful Mission (single dominant primary CTA [testTag] = "home_primary_cta").
 * - LAYER 2 — TODAY: Compact 4-Card Status Row bound strictly to real G3 Home/Mission state.
 *   (Zero AX-015 Vitals, zero direct Daily Check-in, zero fabricated DailyOutcomes).
 * - LAYER 3 — XION & PROGRESS: Visually subordinate Xion Insight advisory card,
 *   and G3-authorized Progress card (Goal Progress > XP).
 * - LAYER 4 — SECONDARY / PROGRESSIVE DISCLOSURE: Collapsible section containing ONLY
 *   the two explicit Product Owner secondary exceptions:
 *   1. AX-015 Daily Check-in
 *   2. AX-022 Weekly Analytics
 *   and real System Feed messages if present.
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
    val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale
    val bottomPadding = if (fontScale > 1.3f) 110.dp else 96.dp

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                )
            }

            // ─────────────────────────────────────────────────────────────────
            // LAYER 2 — TODAY (Compact 4-Card Status Row — G3 Data Truth)
            // ─────────────────────────────────────────────────────────────────
            item(key = "today_status") {
                TodayStatusRow(state = state)
            }

            // ─────────────────────────────────────────────────────────────────
            // LAYER 3 — XION & PROGRESS (Subordinate Insight & Goal Progress)
            // ─────────────────────────────────────────────────────────────────
            item(key = "xion_insight") {
                XionInsightCard(
                    nextBestAction = state.nextBestAction
                )
            }

            item(key = "home_progress") {
                HomeProgressCard(
                    hunter = state.hunter,
                    activeMissionsCount = state.activeMissionsCount
                )
            }

            // ─────────────────────────────────────────────────────────────────
            // LAYER 4 — SECONDARY / PROGRESSIVE DISCLOSURE (Other Missions, Operational Tracks, System Feed)
            // ─────────────────────────────────────────────────────────────────
            item(key = "secondary_surfaces") {
                SecondarySurfacesSection(initiallyExpanded = false) {
                    if (state.topMissions.size > 1) {
                        ActiveMissionStrip(
                            topMissions = state.topMissions.drop(1),
                            dungeons = state.dungeons,
                            isRestMode = false,
                            onNavigateToMissionDetail = { id -> onNavigate(Screen.MissionDetail(id).route) },
                            onCompleteMission = { id -> missionsViewModel.completeMission(id, null) },
                            onDeleteMission = { id -> missionsViewModel.deleteMission(id) }
                        )
                    }

                    // Operational Tracks renders ONLY the two PO-approved secondary links:
                    // - Daily Check-in (AX-015)
                    // - Weekly Analytics (AX-022)
                    OperationalTracksSection(
                        onNavigate = onNavigate
                    )

                    if (state.recentFeed.isNotEmpty()) {
                        SystemFeedSection(recentFeed = state.recentFeed)
                    }
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
