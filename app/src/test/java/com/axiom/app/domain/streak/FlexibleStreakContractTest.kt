package com.axiom.app.domain.streak

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class FlexibleStreakContractTest {

    private val baseMonday = LocalDate.of(2026, 9, 14) // Monday
    private val baseTuesday = LocalDate.of(2026, 9, 15) // Tuesday
    private val baseFriday = LocalDate.of(2026, 9, 18) // Friday
    private val baseSaturday = LocalDate.of(2026, 9, 19) // Saturday
    private val baseSunday = LocalDate.of(2026, 9, 20) // Sunday
    private val nextMonday = LocalDate.of(2026, 9, 21) // Next Monday

    @Test
    fun consecutiveScheduledDays_evaluatesAsActive() {
        val cadence = StreakCadence(StreakCadenceType.EVERYDAY)
        val pauseState = StreakPauseState()
        val recoveryState = StreakRecoveryState()

        val (result, _) = FlexibleStreakEngine.evaluateStreak(
            now = baseTuesday,
            nowMillis = 10000L,
            lastActivityDate = baseMonday,
            currentStreak = 5,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )

        assertTrue("Consecutive scheduled day must be Active", result is StreakEvaluationResult.Active)
        assertEquals(5, (result as StreakEvaluationResult.Active).streak)
    }

    @Test
    fun weekdaysOnly_preservesStreakOverWeekendWithoutBreak() {
        val cadence = StreakCadence(StreakCadenceType.WEEKDAYS_ONLY)
        val pauseState = StreakPauseState()
        val recoveryState = StreakRecoveryState()

        // Evaluated on Saturday after Friday completion
        val (satResult, _) = FlexibleStreakEngine.evaluateStreak(
            now = baseSaturday,
            nowMillis = 10000L,
            lastActivityDate = baseFriday,
            currentStreak = 10,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )
        assertTrue("Saturday must be RestDay", satResult is StreakEvaluationResult.RestDay)
        assertEquals(10, (satResult as StreakEvaluationResult.RestDay).streak)

        // Evaluated on Sunday after Friday completion
        val (sunResult, _) = FlexibleStreakEngine.evaluateStreak(
            now = baseSunday,
            nowMillis = 20000L,
            lastActivityDate = baseFriday,
            currentStreak = 10,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )
        assertTrue("Sunday must be RestDay", sunResult is StreakEvaluationResult.RestDay)
        assertEquals(10, (sunResult as StreakEvaluationResult.RestDay).streak)

        // Evaluated on Monday after Friday completion (no weekdays missed!)
        val (monResult, _) = FlexibleStreakEngine.evaluateStreak(
            now = nextMonday,
            nowMillis = 30000L,
            lastActivityDate = baseFriday,
            currentStreak = 10,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )
        assertTrue("Monday must be Active awaiting today's completion", monResult is StreakEvaluationResult.Active)
        assertEquals(10, (monResult as StreakEvaluationResult.Active).streak)
    }

    @Test
    fun customCadence_respectsConfiguredRestDays() {
        // Mon, Wed, Fri cadence
        val cadence = StreakCadence(
            type = StreakCadenceType.CUSTOM,
            scheduledDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        )
        val pauseState = StreakPauseState()
        val recoveryState = StreakRecoveryState()

        val wednesday = LocalDate.of(2026, 9, 16)

        // Tuesday evaluation after Monday completion
        val (tueResult, _) = FlexibleStreakEngine.evaluateStreak(
            now = baseTuesday,
            nowMillis = 10000L,
            lastActivityDate = baseMonday,
            currentStreak = 3,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )
        assertTrue("Tuesday is unscheduled rest day", tueResult is StreakEvaluationResult.RestDay)

        // Wednesday evaluation after Monday completion (no scheduled day missed between Mon and Wed!)
        val (wedResult, _) = FlexibleStreakEngine.evaluateStreak(
            now = wednesday,
            nowMillis = 20000L,
            lastActivityDate = baseMonday,
            currentStreak = 3,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )
        assertTrue("Wednesday is active scheduled day", wedResult is StreakEvaluationResult.Active)
    }

    @Test
    fun proactivePause_preservesStreakForDeclaredDuration() {
        val cadence = StreakCadence(StreakCadenceType.EVERYDAY)
        val resumeDate = baseMonday.plusDays(5)
        val pauseState = StreakPauseState(isPaused = true, pausedUntil = resumeDate)
        val recoveryState = StreakRecoveryState()

        val (result, _) = FlexibleStreakEngine.evaluateStreak(
            now = baseMonday.plusDays(2),
            nowMillis = 10000L,
            lastActivityDate = baseMonday.minusDays(1),
            currentStreak = 12,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )

        assertTrue("Proactive pause must preserve streak", result is StreakEvaluationResult.Paused)
        assertEquals(12, (result as StreakEvaluationResult.Paused).streak)
        assertEquals(resumeDate, result.resumeDate)
    }

    @Test
    fun reactiveShield_absorbsMissedScheduledDay() {
        val cadence = StreakCadence(StreakCadenceType.EVERYDAY)
        val pauseState = StreakPauseState()
        val recoveryState = StreakRecoveryState()

        // Missed day: last activity was Monday, now is Wednesday (Tuesday was missed)
        val wednesday = LocalDate.of(2026, 9, 16)
        val (result, _) = FlexibleStreakEngine.evaluateStreak(
            now = wednesday,
            nowMillis = 10000L,
            lastActivityDate = baseMonday,
            currentStreak = 7,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 1,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )

        assertTrue("Available shield must absorb miss", result is StreakEvaluationResult.ShieldUsed)
        assertEquals(7, (result as StreakEvaluationResult.ShieldUsed).streak)
        assertEquals(0, result.remainingShields)
    }

    @Test
    fun unshieldedMiss_enters48HourGraceRecoveryWindow() {
        val cadence = StreakCadence(StreakCadenceType.EVERYDAY)
        val pauseState = StreakPauseState()
        val recoveryState = StreakRecoveryState()

        val wednesday = LocalDate.of(2026, 9, 16)
        val nowMillis = 1000000L

        val (result, newRecState) = FlexibleStreakEngine.evaluateStreak(
            now = wednesday,
            nowMillis = nowMillis,
            lastActivityDate = baseMonday,
            currentStreak = 14,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )

        assertTrue("Unshielded miss must enter GraceRecovery", result is StreakEvaluationResult.GraceRecoveryOffered)
        val recovery = result as StreakEvaluationResult.GraceRecoveryOffered
        assertEquals(14, recovery.frozenStreak)
        val expectedDeadline = nowMillis + (48 * 3600 * 1000L)
        assertEquals(expectedDeadline, recovery.deadlineMillis)
        assertEquals(RecoveryStatus.PENDING, newRecState.status)
        assertEquals(14, newRecState.frozenStreak)
    }

    @Test
    fun completingRecoveryMission_restoresAndIncrementsStreak() {
        val pendingState = StreakRecoveryState(
            status = RecoveryStatus.PENDING,
            frozenStreak = 14,
            deadlineMillis = 5000000L,
            recoveryMissionId = "recovery_core_action"
        )

        val today = LocalDate.of(2026, 9, 17)
        val (result, repairedState) = FlexibleStreakEngine.onRecoveryMissionCompleted(pendingState, today)

        assertEquals(15, result.restoredStreak)
        assertEquals(RecoveryStatus.REPAIRED, repairedState.status)
        assertEquals(0, repairedState.frozenStreak)
        assertEquals(today, repairedState.lastRecoveryDate)
    }

    @Test
    fun expiredGraceWindow_cleanlyBreaksStreak() {
        val deadline = 2000000L
        val expiredNowMillis = 2500000L // 500 seconds after deadline
        val pendingState = StreakRecoveryState(
            status = RecoveryStatus.PENDING,
            frozenStreak = 14,
            deadlineMillis = deadline,
            recoveryMissionId = "recovery_core_action"
        )

        val (result, updatedState) = FlexibleStreakEngine.evaluateStreak(
            now = LocalDate.of(2026, 9, 18),
            nowMillis = expiredNowMillis,
            lastActivityDate = baseMonday,
            currentStreak = 14,
            cadence = StreakCadence(StreakCadenceType.EVERYDAY),
            pauseState = StreakPauseState(),
            availableShields = 0,
            recoveryState = pendingState,
            isStreakTrackingEnabled = true
        )

        assertTrue("Expired grace window must break streak", result is StreakEvaluationResult.Broken)
        assertEquals(14, (result as StreakEvaluationResult.Broken).previousStreak)
        assertEquals(RecoveryStatus.EXPIRED, updatedState.status)
        assertEquals(0, updatedState.frozenStreak)
    }

    @Test
    fun antiPredatory_recoveryCooldownEnforced() {
        val cadence = StreakCadence(StreakCadenceType.EVERYDAY)
        val pauseState = StreakPauseState()

        // Recovered 5 days ago (cooldown is 14 days)
        val recentRecoveryDate = LocalDate.of(2026, 9, 10)
        val recoveryState = StreakRecoveryState(
            status = RecoveryStatus.REPAIRED,
            frozenStreak = 0,
            lastRecoveryDate = recentRecoveryDate
        )

        val wednesday = LocalDate.of(2026, 9, 16)
        val (result, updatedState) = FlexibleStreakEngine.evaluateStreak(
            now = wednesday,
            nowMillis = 100000L,
            lastActivityDate = baseMonday,
            currentStreak = 4,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = 0,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = true
        )

        assertTrue("Miss during cooldown must break without offering second recovery", result is StreakEvaluationResult.Broken)
        assertEquals(RecoveryStatus.NONE, updatedState.status)
    }

    @Test
    fun optOut_suppressesStreakCompletely() {
        val (result, _) = FlexibleStreakEngine.evaluateStreak(
            now = baseMonday,
            nowMillis = 10000L,
            lastActivityDate = baseMonday.minusDays(5),
            currentStreak = 20,
            cadence = StreakCadence(StreakCadenceType.EVERYDAY),
            pauseState = StreakPauseState(),
            availableShields = 0,
            recoveryState = StreakRecoveryState(),
            isStreakTrackingEnabled = false
        )

        assertTrue("Opted-out tracking must return OptedOut", result is StreakEvaluationResult.OptedOut)
    }
}
