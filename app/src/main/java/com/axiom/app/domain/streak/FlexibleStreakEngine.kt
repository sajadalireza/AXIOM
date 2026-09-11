package com.axiom.app.domain.streak

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object FlexibleStreakEngine {
    const val RECOVERY_WINDOW_HOURS = 48L
    const val RECOVERY_COOLDOWN_DAYS = 14L
    const val MAX_PAUSE_DAYS = 14L

    val defaultRecoveryMissions = listOf(
        StreakRecoveryMission(
            id = "recovery_core_action",
            titleEn = "Momentum Re-anchor: Core Goal Action",
            titleFa = "لنگر مجدد شتاب: اقدام محوری هدف",
            descriptionEn = "Execute one concrete action advancing your active primary Goal.",
            descriptionFa = "یک اقدام ملموس برای پیشبرد هدف اصلی خود انجام دهید.",
            xpReward = 15
        ),
        StreakRecoveryMission(
            id = "recovery_deep_focus",
            titleEn = "Deep Focus Re-calibration: 15-min Session",
            titleFa = "تنظیم مجدد تمرکز عمیق: جلسه ۱۵ دقیقه‌ای",
            descriptionEn = "Complete an uninterrupted 15-minute deep focus cycle.",
            descriptionFa = "یک چرخه تمرکز عمیق ۱۵ دقیقه‌ای بدون وقفه را تکمیل کنید.",
            xpReward = 15
        ),
        StreakRecoveryMission(
            id = "recovery_reflection",
            titleEn = "Reflection & Alignment: Log Lesson & Intention",
            titleFa = "بازاندیشی و هم‌راستایی: ثبت درس و قصد",
            descriptionEn = "Record what caused the cadence pause and your next step.",
            descriptionFa = "علت وقفه در ریتم و اقدام بعدی خود را ثبت کنید.",
            xpReward = 10
        )
    )

    fun findRecoveryMission(id: String?): StreakRecoveryMission {
        return defaultRecoveryMissions.firstOrNull { it.id == id } ?: defaultRecoveryMissions.first()
    }

    /**
     * Pure evaluation of streak state.
     * Evaluates schedule cadence, proactive pause, emergency shields, and grace recovery windows.
     */
    fun evaluateStreak(
        now: LocalDate,
        nowMillis: Long,
        lastActivityDate: LocalDate?,
        currentStreak: Int,
        cadence: StreakCadence,
        pauseState: StreakPauseState,
        availableShields: Int,
        recoveryState: StreakRecoveryState,
        isStreakTrackingEnabled: Boolean
    ): Pair<StreakEvaluationResult, StreakRecoveryState> {
        // Opt-out guard: user autonomy outranks gamification
        if (!isStreakTrackingEnabled) {
            return StreakEvaluationResult.OptedOut to recoveryState
        }

        // Proactive pause: declared rest/illness/vacation window
        if (pauseState.isEffectivelyPaused(now)) {
            return StreakEvaluationResult.Paused(currentStreak, pauseState.pausedUntil!!) to recoveryState
        }

        // Currently in grace recovery: check whether window is active or expired
        if (recoveryState.status == RecoveryStatus.PENDING) {
            return if (recoveryState.isWithinGrace(nowMillis)) {
                val mission = findRecoveryMission(recoveryState.recoveryMissionId)
                StreakEvaluationResult.GraceRecoveryOffered(
                    frozenStreak = recoveryState.frozenStreak,
                    deadlineMillis = recoveryState.deadlineMillis,
                    recoveryMission = mission
                ) to recoveryState
            } else {
                // Grace window expired without recovery
                StreakEvaluationResult.Broken(recoveryState.frozenStreak) to recoveryState.copy(
                    status = RecoveryStatus.EXPIRED,
                    frozenStreak = 0
                )
            }
        }

        // Fresh or unstarted streak
        if (lastActivityDate == null || currentStreak <= 0) {
            return if (!cadence.isScheduled(now)) {
                StreakEvaluationResult.RestDay(0) to recoveryState
            } else {
                StreakEvaluationResult.Active(0) to recoveryState
            }
        }

        // Completed today
        if (lastActivityDate == now) {
            return StreakEvaluationResult.Active(currentStreak) to recoveryState
        }

        // Find the last scheduled day strictly before today
        val lastScheduled = cadence.lastScheduledDayBefore(now)

        // If the user's last activity was on or after that last scheduled day,
        // no scheduled day has been missed!
        if (!lastActivityDate.isBefore(lastScheduled)) {
            return if (!cadence.isScheduled(now)) {
                StreakEvaluationResult.RestDay(currentStreak) to recoveryState
            } else {
                StreakEvaluationResult.Active(currentStreak) to recoveryState
            }
        }

        // A scheduled day was missed!
        // First defense: reactive shield
        if (availableShields > 0) {
            return StreakEvaluationResult.ShieldUsed(
                streak = currentStreak,
                remainingShields = availableShields - 1
            ) to recoveryState
        }

        // Second defense: grace recovery window
        val canOfferRecovery = recoveryState.lastRecoveryDate == null ||
            ChronoUnit.DAYS.between(recoveryState.lastRecoveryDate, now) >= RECOVERY_COOLDOWN_DAYS

        if (canOfferRecovery && currentStreak >= 1) {
            val deadline = nowMillis + (RECOVERY_WINDOW_HOURS * 3600 * 1000L)
            val mission = defaultRecoveryMissions.first()
            val newRecoveryState = StreakRecoveryState(
                status = RecoveryStatus.PENDING,
                frozenStreak = currentStreak,
                deadlineMillis = deadline,
                recoveryMissionId = mission.id,
                lastRecoveryDate = recoveryState.lastRecoveryDate
            )
            return StreakEvaluationResult.GraceRecoveryOffered(
                frozenStreak = currentStreak,
                deadlineMillis = deadline,
                recoveryMission = mission
            ) to newRecoveryState
        }

        // If recovery cannot be offered (e.g. on cooldown or streak < 1), streak breaks cleanly
        return StreakEvaluationResult.Broken(currentStreak) to recoveryState.copy(
            status = RecoveryStatus.NONE,
            frozenStreak = 0
        )
    }

    /**
     * Resolves recovery mission completion, restoring and advancing the frozen streak.
     */
    fun onRecoveryMissionCompleted(
        recoveryState: StreakRecoveryState,
        today: LocalDate
    ): Pair<StreakEvaluationResult.Repaired, StreakRecoveryState> {
        val restoredStreak = recoveryState.frozenStreak + 1
        val updatedState = recoveryState.copy(
            status = RecoveryStatus.REPAIRED,
            frozenStreak = 0,
            deadlineMillis = 0L,
            lastRecoveryDate = today
        )
        return StreakEvaluationResult.Repaired(restoredStreak) to updatedState
    }
}
