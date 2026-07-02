package com.axiom.app.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.local.dao.WeeklyReviewDao
import com.axiom.app.data.local.entity.WeeklyReviewEntity
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WeeklyReviewViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val preferences: AxiomPreferences,
    private val weeklyReviewDao: WeeklyReviewDao
) : ViewModel() {

    val completedMissionsThisWeek: StateFlow<List<Mission>> = missionRepository.getAllMissions()
        .map { missions ->
            val sevenDaysAgo = System.currentTimeMillis() - 7 * 86400000L
            missions.filter {
                it.status == "COMPLETED" && (it.completedAt ?: 0L) >= sevenDaysAgo
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lastReviewTimestamp: StateFlow<Long> = preferences.lastReviewTimestampFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun createEvidenceSummary(completed: List<Mission>): String {
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
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val reviewEntity = WeeklyReviewEntity(
                id = UUID.randomUUID().toString(),
                timestamp = now,
                step1Summary = summary,
                step2WrongAssumption = wrongAssumption,
                step3CriticFeedback = criticFeedback,
                step4DecisionType = decisionType,
                step5JournalText = journalText
            )
            weeklyReviewDao.insertReview(reviewEntity)
            preferences.setLastReviewTimestamp(now)
            onComplete()
        }
    }
}
