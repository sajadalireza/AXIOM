package com.axiom.app.domain.usecase

import com.axiom.app.core.AnalyticsLogger
import com.axiom.app.core.CanonicalAnalyticsEvents
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.streak.FlexibleStreakEngine
import com.axiom.app.domain.streak.RecoveryStatus
import com.axiom.app.domain.streak.StreakCadence
import com.axiom.app.domain.streak.StreakRecoveryMission
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRecoveryUseCase @Inject constructor(
    private val preferences: AxiomPreferences,
    private val createMissionUseCase: CreateMissionUseCase,
    private val feedRepository: SystemFeedRepository
) {

    /**
     * Proactively pauses the streak for 1 to 14 days without penalty or decay.
     */
    suspend fun pauseStreak(days: Long) {
        val clamped = days.coerceIn(1L, FlexibleStreakEngine.MAX_PAUSE_DAYS)
        preferences.pauseStreak(clamped)
        val resumeDate = LocalDate.now().plusDays(clamped)
        AnalyticsLogger.log(
            CanonicalAnalyticsEvents.STREAK_PAUSED,
            mapOf(
                "days_paused" to clamped.toInt(),
                "resume_date" to resumeDate.toString()
            )
        )
        feedRepository.emitMessage(
            SystemMessage(
                id = UUID.randomUUID().toString(),
                message = "⬡ Streak cadence paused for $clamped days. Resumes on $resumeDate without penalty.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /**
     * Resumes a paused streak immediately.
     */
    suspend fun resumeStreak(reason: String = "user_manual_resume") {
        preferences.resumeStreak()
        AnalyticsLogger.log(
            CanonicalAnalyticsEvents.STREAK_RESUMED,
            mapOf("reason" to reason)
        )
        feedRepository.emitMessage(
            SystemMessage(
                id = UUID.randomUUID().toString(),
                message = "⬡ Streak cadence resumed. Ready for active execution.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /**
     * Configures the active scheduled cadence (e.g. EVERYDAY, WEEKDAYS_ONLY, or CUSTOM).
     */
    suspend fun updateCadence(cadence: StreakCadence) {
        preferences.setStreakCadence(cadence)
    }

    /**
     * Allows the user to toggle streak gamification entirely (opt-out).
     */
    suspend fun setStreakTrackingEnabled(enabled: Boolean) {
        preferences.setStreakTrackingEnabled(enabled)
        AnalyticsLogger.log(
            CanonicalAnalyticsEvents.STREAK_OPT_OUT_CHANGED,
            mapOf("opt_out_state" to (!enabled).toString())
        )
    }

    /**
     * Accepts and creates a real-world recovery mission to restore a paused cadence.
     */
    suspend fun acceptRecoveryMission(mission: StreakRecoveryMission, goalId: String? = null) {
        val recoveryState = preferences.streakRecoveryStateFlow.first()
        val isFa = java.util.Locale.getDefault().language == "fa"
        val title = if (isFa) mission.titleFa else mission.titleEn
        val description = if (isFa) mission.descriptionFa else mission.descriptionEn

        createMissionUseCase(
            title = title,
            track = "Recovery Protocol",
            rarity = "COMMON",
            skillId = "",
            xpReward = mission.xpReward,
            powerScore = 0.5f,
            estimatedHours = 0.5f,
            dungeonId = goalId
        )

        preferences.setStreakRecoveryState(
            recoveryState.copy(recoveryMissionId = mission.id)
        )
    }
}
