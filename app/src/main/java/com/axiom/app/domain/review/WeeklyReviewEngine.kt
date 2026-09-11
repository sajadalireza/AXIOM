package com.axiom.app.domain.review

import com.axiom.app.domain.analytics.WmpuCalculationEngine
import com.axiom.app.domain.model.Mission

object WeeklyReviewEngine {
    const val FLAT_REVIEW_XP = 50

    fun buildProgressSnapshot(
        missions: List<Mission>,
        currentStreak: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): ReviewProgressSnapshot {
        val cycleWeek = WmpuCalculationEngine.currentCycleWeek(nowMillis)
        val sevenDaysAgo = nowMillis - 7 * 86400000L

        val completedThisCycle = missions.filter {
            it.status == "COMPLETED" && (it.completedAt ?: 0L) >= sevenDaysAgo
        }

        val goalContributing = completedThisCycle.count { !it.goalId.isNullOrBlank() }

        val totalEffectiveHours = completedThisCycle.sumOf { mission ->
            if (mission.effectiveHours > 0.0) mission.effectiveHours
            else (mission.actualHours?.toDouble() ?: mission.estimatedHours.toDouble())
        }.toFloat()

        val wmpuEval = WmpuCalculationEngine.evaluate(
            missions = missions,
            activeGoalsCount = if (goalContributing > 0) 1 else 0,
            nowMillis = nowMillis
        )

        return ReviewProgressSnapshot(
            cycleWeek = cycleWeek,
            totalMissionsCompleted = completedThisCycle.size,
            goalContributingMissions = goalContributing,
            totalEffectiveHours = totalEffectiveHours,
            wmpuAchieved = wmpuEval.isAchieved,
            activeStreak = currentStreak
        )
    }

    fun evaluateReview(
        snapshot: ReviewProgressSnapshot,
        obstacleCategory: ObstacleCategory?,
        commitmentOutcome: String,
        usefulnessRating: Int
    ): WeeklyReviewEvaluation {
        var completenessPoints = 0
        if (snapshot.totalMissionsCompleted >= 0) completenessPoints += 25
        if (obstacleCategory != null) completenessPoints += 25
        if (commitmentOutcome.isNotBlank()) completenessPoints += 25
        if (usefulnessRating in 1..5) completenessPoints += 25

        val completeness = completenessPoints / 100f

        // Progress correlation: higher if review is tied to active Goal contributions and WMPU
        val correlation = when {
            snapshot.wmpuAchieved -> 1.0f
            snapshot.goalContributingMissions > 0 -> 0.8f
            snapshot.totalMissionsCompleted > 0 -> 0.6f
            else -> 0.4f
        }

        return WeeklyReviewEvaluation(
            completenessScore = completeness,
            progressCorrelationScore = correlation,
            isWmpuAchieved = snapshot.wmpuAchieved,
            reviewXpAward = FLAT_REVIEW_XP
        )
    }

    fun categorizeHoursBracket(hours: Float): String {
        return when {
            hours < 2f -> "0-2h"
            hours < 5f -> "2-5h"
            hours < 10f -> "5-10h"
            hours < 20f -> "10-20h"
            else -> "20h+"
        }
    }
}
