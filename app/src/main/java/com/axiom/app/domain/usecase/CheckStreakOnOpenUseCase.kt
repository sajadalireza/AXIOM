package com.axiom.app.domain.usecase

import com.axiom.app.core.AnalyticsLogger
import com.axiom.app.core.CanonicalAnalyticsEvents
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.streak.FlexibleStreakEngine
import com.axiom.app.domain.streak.RecoveryStatus
import com.axiom.app.domain.streak.StreakEvaluationResult
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class CheckStreakOnOpenUseCase @Inject constructor(
    private val preferences: AxiomPreferences,
    private val ceremonyEngine: CeremonyEngine,
    private val feedRepository: SystemFeedRepository
) {
    suspend operator fun invoke() {
        preferences.resetWeeklyIfNeeded()

        // Opt-out guard
        val isTrackingEnabled = preferences.streakTrackingEnabledFlow.first()
        if (!isTrackingEnabled) return

        val currentStreak = preferences.streakFlow.first()
        preferences.setWeeklyStreakBest(currentStreak)
        val lastComplete = preferences.lastCompleteTimestampFlow.first()
        val nowMillis = System.currentTimeMillis()
        val nowDate = LocalDate.now()
        val lastActivityDate = if (lastComplete > 0L) {
            Instant.ofEpochMilli(lastComplete).atZone(ZoneId.systemDefault()).toLocalDate()
        } else null

        val cadence = preferences.streakCadenceFlow.first()
        val pauseState = preferences.streakPauseStateFlow.first()
        val availableShields = preferences.streakFreezeFlow.first()
        val recoveryState = preferences.streakRecoveryStateFlow.first()

        val (evalResult, updatedRecoveryState) = FlexibleStreakEngine.evaluateStreak(
            now = nowDate,
            nowMillis = nowMillis,
            lastActivityDate = lastActivityDate,
            currentStreak = currentStreak,
            cadence = cadence,
            pauseState = pauseState,
            availableShields = availableShields,
            recoveryState = recoveryState,
            isStreakTrackingEnabled = isTrackingEnabled
        )

        preferences.setStreakRecoveryState(updatedRecoveryState)

        when (evalResult) {
            is StreakEvaluationResult.OptedOut,
            is StreakEvaluationResult.RestDay,
            is StreakEvaluationResult.Paused -> {
                // Streak preserved without penalty
                return
            }
            is StreakEvaluationResult.ShieldUsed -> {
                preferences.consumeStreakFreeze()
                ceremonyEngine.emit(CeremonyEvent.StreakShieldUsed(currentStreak, evalResult.remainingShields))
                AnalyticsLogger.log(
                    "streak_shield_used",
                    mapOf("streak_length" to currentStreak)
                )
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = "⬡ Streak Shield activated. $currentStreak-day streak preserved.",
                        timestamp = nowMillis
                    )
                )
                return
            }
            is StreakEvaluationResult.GraceRecoveryOffered -> {
                ceremonyEngine.emit(CeremonyEvent.StreakBroken(evalResult.frozenStreak))
                AnalyticsLogger.log(
                    CanonicalAnalyticsEvents.STREAK_RECOVERY_OFFERED,
                    mapOf(
                        "streak_length" to evalResult.frozenStreak,
                        "cadence" to cadence.type.name
                    )
                )
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = "⬡ Cadence interrupted. 48-hour recovery window active. Complete a recovery mission to restore your streak.",
                        timestamp = nowMillis
                    )
                )
                return
            }
            is StreakEvaluationResult.Broken -> {
                preferences.setStreak(0)
                if (recoveryState.status == RecoveryStatus.PENDING) {
                    AnalyticsLogger.log(
                        CanonicalAnalyticsEvents.STREAK_RECOVERY_EXPIRED,
                        mapOf("streak_length" to evalResult.previousStreak)
                    )
                }
                if (evalResult.previousStreak >= 1) {
                    ceremonyEngine.emit(CeremonyEvent.StreakBroken(evalResult.previousStreak))
                    AnalyticsLogger.log(
                        "streak_broken",
                        mapOf("streak_length" to evalResult.previousStreak)
                    )
                }
                return
            }
            is StreakEvaluationResult.Repaired -> {
                return
            }
            is StreakEvaluationResult.Active -> {
                checkMilestones(currentStreak, nowMillis)
            }
        }
    }

    private suspend fun checkMilestones(currentStreak: Int, now: Long) {
        val milestones = listOf(7, 14, 21, 30, 60, 90, 180, 365)
        val lastShown = preferences.lastShownStreakMilestoneFlow.first()
        val newMilestone = milestones
            .filter { it <= currentStreak && it > lastShown }
            .maxOrNull()

        if (newMilestone != null) {
            preferences.setLastShownStreakMilestone(newMilestone)
            val label = when (newMilestone) {
                7    -> "CONSECRATION PROTOCOL"
                14   -> "DOMINANCE PROTOCOL"
                21   -> "RESONANCE PROTOCOL"
                30   -> "ASCENSION PROTOCOL"
                60   -> "TRANSCENDENCE PROTOCOL"
                90   -> "IMMORTAL PROTOCOL"
                180  -> "OVERLORD PROTOCOL"
                else -> "CHRONOS PROTOCOL"
            }
            ceremonyEngine.emit(CeremonyEvent.StreakMilestone(currentStreak, label))
            preferences.awardStreakFreeze()

            if (newMilestone >= 7) {
                val isFa = java.util.Locale.getDefault().language == "fa"
                val labelFa = when (newMilestone) {
                    7    -> "پروتکل تعهد"
                    14   -> "پروتکل تسلط"
                    21   -> "پروتکل هماهنگی"
                    30   -> "پروتکل صعود"
                    60   -> "پروتکل تعالی"
                    90   -> "پروتکل جاودانگی"
                    180  -> "پروتکل فرمانروا"
                    else -> "پروتکل زمان"
                }
                val finalMsg = if (isFa) {
                    "⬡ پروتکل زنجیره $labelFa به دست آمد. زنجیره $currentStreak روزه شما هم‌اکنون دارایی باارزشی تحت حفاظت سیستم است."
                } else {
                    "⬡ Streak Protocol $label achieved. Your $currentStreak-day streak is now an asset worth protecting."
                }
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = finalMsg,
                        timestamp = now
                    )
                )
            }
        }
    }
}

