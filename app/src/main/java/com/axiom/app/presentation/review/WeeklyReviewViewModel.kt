package com.axiom.app.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.core.AnalyticsLogger
import com.axiom.app.core.CanonicalAnalyticsEvents
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.local.dao.WeeklyReviewDao
import com.axiom.app.data.local.entity.WeeklyReviewEntity
import com.axiom.app.domain.analytics.WmpuCalculationEngine
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.review.ObstacleCategory
import com.axiom.app.domain.review.ReviewProgressSnapshot
import com.axiom.app.domain.review.WeeklyReviewEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WeeklyReviewViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val preferences: AxiomPreferences,
    private val weeklyReviewDao: WeeklyReviewDao,
    private val hunterRepository: HunterRepository,
    private val feedRepository: SystemFeedRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            val ring = preferences.releaseRingFlow.first().name
            val cycleWeek = WmpuCalculationEngine.currentCycleWeek()
            AnalyticsLogger.log(
                CanonicalAnalyticsEvents.WEEKLY_REVIEW_EXPOSED,
                mapOf(
                    "cycle_week" to cycleWeek,
                    "cohort_ring" to ring
                )
            )
        }
    }

    val completedMissionsThisWeek: StateFlow<List<Mission>> = missionRepository.getAllMissions()
        .map { missions ->
            val sevenDaysAgo = System.currentTimeMillis() - 7 * 86400000L
            missions.filter {
                it.status == "COMPLETED" && (it.completedAt ?: 0L) >= sevenDaysAgo
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val progressSnapshot: StateFlow<ReviewProgressSnapshot> = combine(
        missionRepository.getAllMissions(),
        preferences.streakFlow
    ) { missions, streak ->
        WeeklyReviewEngine.buildProgressSnapshot(missions, streak)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ReviewProgressSnapshot(0, 0, 0, 0f, false, 0)
    )

    val lastReviewTimestamp: StateFlow<Long> = preferences.lastReviewTimestampFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun createEvidenceSummary(completed: List<Mission>, snapshot: ReviewProgressSnapshot? = null): String {
        val map = mutableMapOf<String, Float>()
        var incomeSessions = 0
        var problemDiscussions = 0

        for (mission in completed) {
            val eff = if (mission.effectiveHours > 0.0) mission.effectiveHours else (mission.actualHours?.toDouble() ?: mission.estimatedHours.toDouble())
            map[mission.skillName] = (map[mission.skillName] ?: 0f) + eff.toFloat()

            // Safe checks using track or skillName
            val trackLower = mission.track.lowercase()
            val skillLower = mission.skillName.lowercase()
            if (trackLower.contains("income") || trackLower.contains("commercial") || trackLower.contains("intelligence")) {
                incomeSessions++
            }
            if (skillLower.contains("problem discovery") || skillLower.contains("problem-discovery")) {
                problemDiscussions++
            }
        }

        val sb = StringBuilder()
        sb.append("=== TRUTH-GROUNDED PROGRESS AUDIT ===\n")
        if (snapshot != null) {
            val wmpuStatus = if (snapshot.wmpuAchieved) "ACHIEVED (+1 WMPU)" else "PENDING (Need >=1 Goal mission)"
            sb.append("Weekly Meaningful Progress Unit (WMPU): $wmpuStatus\n")
            sb.append("Goal-Contributing Missions: ${snapshot.goalContributingMissions} / ${snapshot.totalMissionsCompleted}\n")
            sb.append("Active Streak Cadence: ${snapshot.activeStreak} days\n\n")
        }

        sb.append("Total Effective Hours per Discipline:\n")
        if (map.isEmpty()) {
            sb.append("  No hours logged.\n")
        } else {
            map.forEach { (discipline, hours) ->
                sb.append("  - $discipline: ${String.format("%.1f", hours)} hrs\n")
            }
        }
        sb.append("\nIncome-related sessions logged: $incomeSessions\n")
        sb.append("Problem-discovery conversations count: $problemDiscussions")

        return sb.toString()
    }

    fun submitReview(
        summary: String,
        wrongAssumption: String,
        criticFeedback: String,
        decisionType: String,
        journalText: String,
        obstacleCategory: ObstacleCategory? = null,
        usefulnessRating: Int = 5,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val allMissions = missionRepository.getAllMissions().first()
            val currentStreak = preferences.streakFlow.first()
            val snapshot = WeeklyReviewEngine.buildProgressSnapshot(allMissions, currentStreak, now)

            val evaluation = WeeklyReviewEngine.evaluateReview(
                snapshot = snapshot,
                obstacleCategory = obstacleCategory,
                commitmentOutcome = decisionType,
                usefulnessRating = usefulnessRating
            )

            val formattedAssumption = if (obstacleCategory != null && wrongAssumption.isNotBlank()) {
                "[${obstacleCategory.titleEn}] $wrongAssumption"
            } else {
                obstacleCategory?.titleEn ?: wrongAssumption
            }

            val reviewEntity = WeeklyReviewEntity(
                id = UUID.randomUUID().toString(),
                timestamp = now,
                step1Summary = summary,
                step2WrongAssumption = formattedAssumption,
                step3CriticFeedback = criticFeedback,
                step4DecisionType = decisionType,
                step5JournalText = journalText
            )
            weeklyReviewDao.insertReview(reviewEntity)
            preferences.setLastReviewTimestamp(now)
            preferences.setNextWeekOutcome1(decisionType)
            if (journalText.isNotBlank()) {
                preferences.setNextWeekOutcome2(journalText)
            }

            // Award flat review XP (50 XP) to hunter without reward inflation
            val hunter = hunterRepository.getDirectHunterProfile()
            if (hunter != null) {
                var newHunterXP = hunter.currentXP + evaluation.reviewXpAward
                var newHunterLevel = hunter.level
                var nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
                while (newHunterXP >= nextLevelXP && newHunterLevel < 100) {
                    newHunterXP -= nextLevelXP
                    newHunterLevel++
                    nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
                }
                hunterRepository.updateHunterProfile(
                    hunter.copy(currentXP = newHunterXP, level = newHunterLevel)
                )
            }

            val ring = preferences.releaseRingFlow.first().name
            val cycleWeek = snapshot.cycleWeek
            AnalyticsLogger.log(
                CanonicalAnalyticsEvents.WEEKLY_REVIEW_COMPLETED,
                mapOf(
                    "cycle_week" to cycleWeek,
                    "actions_taken" to "1",
                    "cohort_ring" to ring,
                    "wmpu_achieved" to snapshot.wmpuAchieved.toString(),
                    "usefulness_rating" to usefulnessRating.toString(),
                    "effective_hours_bracket" to WeeklyReviewEngine.categorizeHoursBracket(snapshot.totalEffectiveHours)
                )
            )

            feedRepository.emitMessage(
                SystemMessage(
                    id = UUID.randomUUID().toString(),
                    message = "⬡ Weekly review completed. Cycle week $cycleWeek committed (+${evaluation.reviewXpAward} XP).",
                    timestamp = now
                )
            )

            onComplete()
        }
    }
}
