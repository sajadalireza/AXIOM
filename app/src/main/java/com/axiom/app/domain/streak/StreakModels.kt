package com.axiom.app.domain.streak

import java.time.DayOfWeek
import java.time.LocalDate

enum class StreakCadenceType {
    EVERYDAY,
    WEEKDAYS_ONLY,
    CUSTOM
}

data class StreakCadence(
    val type: StreakCadenceType = StreakCadenceType.EVERYDAY,
    val scheduledDays: Set<DayOfWeek> = DayOfWeek.values().toSet()
) {
    fun isScheduled(date: LocalDate): Boolean {
        return when (type) {
            StreakCadenceType.EVERYDAY -> true
            StreakCadenceType.WEEKDAYS_ONLY -> date.dayOfWeek in setOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )
            StreakCadenceType.CUSTOM -> date.dayOfWeek in scheduledDays
        }
    }

    /**
     * Finds the most recent scheduled day strictly before [date].
     */
    fun lastScheduledDayBefore(date: LocalDate): LocalDate {
        var cursor = date.minusDays(1)
        var count = 0
        while (count < 14) {
            if (isScheduled(cursor)) return cursor
            cursor = cursor.minusDays(1)
            count++
        }
        return date.minusDays(1)
    }

    /**
     * Checks if any scheduled day between (exclusive) [fromDate] and (exclusive) [toDate] was missed.
     */
    fun hasMissedScheduledDayBetween(fromDate: LocalDate, toDate: LocalDate): Boolean {
        if (!toDate.isAfter(fromDate.plusDays(1))) return false
        var cursor = fromDate.plusDays(1)
        while (cursor.isBefore(toDate)) {
            if (isScheduled(cursor)) {
                return true
            }
            cursor = cursor.plusDays(1)
        }
        return false
    }
}

data class StreakPauseState(
    val isPaused: Boolean = false,
    val pausedUntil: LocalDate? = null
) {
    fun isEffectivelyPaused(onDate: LocalDate): Boolean {
        return isPaused && pausedUntil != null && !onDate.isAfter(pausedUntil)
    }
}

enum class RecoveryStatus {
    NONE,
    PENDING,
    REPAIRED,
    EXPIRED
}

data class StreakRecoveryState(
    val status: RecoveryStatus = RecoveryStatus.NONE,
    val frozenStreak: Int = 0,
    val deadlineMillis: Long = 0L,
    val recoveryMissionId: String? = null,
    val lastRecoveryDate: LocalDate? = null
) {
    fun isWithinGrace(nowMillis: Long): Boolean =
        status == RecoveryStatus.PENDING && nowMillis <= deadlineMillis
}

data class StreakRecoveryMission(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val xpReward: Int = 15
)

sealed interface StreakEvaluationResult {
    data class Active(
        val streak: Int,
        val isMilestone: Boolean = false,
        val milestone: Int? = null,
        val milestoneLabel: String? = null
    ) : StreakEvaluationResult

    data class RestDay(val streak: Int) : StreakEvaluationResult

    data class Paused(val streak: Int, val resumeDate: LocalDate) : StreakEvaluationResult

    data class ShieldUsed(val streak: Int, val remainingShields: Int) : StreakEvaluationResult

    data class GraceRecoveryOffered(
        val frozenStreak: Int,
        val deadlineMillis: Long,
        val recoveryMission: StreakRecoveryMission
    ) : StreakEvaluationResult

    data class Repaired(val restoredStreak: Int) : StreakEvaluationResult

    data class Broken(val previousStreak: Int) : StreakEvaluationResult

    object OptedOut : StreakEvaluationResult
}
