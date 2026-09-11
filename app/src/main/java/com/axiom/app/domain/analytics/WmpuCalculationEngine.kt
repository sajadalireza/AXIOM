package com.axiom.app.domain.analytics

import com.axiom.app.domain.model.Mission
import java.util.Calendar
import java.util.TimeZone

/**
 * Pure calculation engine for Weekly Meaningful Progress Units (WMPU).
 *
 * Core Principle: "Goal progress outranks XP."
 * A vanity task or unaligned XP grind does NOT constitute meaningful progress.
 * A user achieves a WMPU if and only if they complete at least one mission tied to an
 * active Goal during the rolling 7-day window / current cycle week.
 */
object WmpuCalculationEngine {

    const val ROLLING_WINDOW_DAYS = 7L
    const val MILLIS_PER_DAY = 86_400_000L

    /**
     * Represents the outcome of a WMPU evaluation.
     */
    data class WmpuEvaluation(
        val cycleWeek: Int,
        val isAchieved: Boolean,
        val meaningfulMissionsCount: Int,
        val activeGoalsCount: Int
    )

    /**
     * Determines whether a specific mission qualifies as meaningful for WMPU.
     * Criteria: Mission must be completed, must have a non-blank goalId, and completed within the window.
     */
    fun isMeaningfulMission(
        mission: Mission,
        windowStartMillis: Long,
        windowEndMillis: Long = Long.MAX_VALUE
    ): Boolean {
        val completedAt = mission.completedAt ?: return false
        if (completedAt < windowStartMillis || completedAt > windowEndMillis) return false
        val hasActiveGoal = !mission.goalId.isNullOrBlank()
        return hasActiveGoal
    }

    /**
     * Evaluates WMPU status across a collection of missions and active goal count.
     */
    fun evaluate(
        missions: List<Mission>,
        activeGoalsCount: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): WmpuEvaluation {
        val cycleWeek = currentCycleWeek(nowMillis)
        val windowStart = nowMillis - (ROLLING_WINDOW_DAYS * MILLIS_PER_DAY)

        val meaningfulCount = missions.count { mission ->
            isMeaningfulMission(mission, windowStartMillis = windowStart, windowEndMillis = nowMillis)
        }

        val achieved = meaningfulCount >= 1 && activeGoalsCount >= 1

        return WmpuEvaluation(
            cycleWeek = cycleWeek,
            isAchieved = achieved,
            meaningfulMissionsCount = meaningfulCount,
            activeGoalsCount = activeGoalsCount
        )
    }

    /**
     * Determines if a new WMPU achievement event should be emitted.
     * Prevents duplicate emissions within the same cycle week.
     */
    fun shouldEmitWmpuEvent(
        evaluation: WmpuEvaluation,
        lastRecordedCycleWeek: Int?
    ): Boolean {
        if (!evaluation.isAchieved) return false
        if (lastRecordedCycleWeek == null) return true
        return evaluation.cycleWeek > lastRecordedCycleWeek
    }

    /**
     * Computes unambiguous cycle week identifier (YYYYWW).
     */
    fun currentCycleWeek(timestampMillis: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = timestampMillis
        }
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return year * 100 + week
    }
}
