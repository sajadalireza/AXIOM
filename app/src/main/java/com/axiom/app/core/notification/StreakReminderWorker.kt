package com.axiom.app.core.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.axiom.app.data.local.AxiomPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class StreakReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val preferences: AxiomPreferences
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val streak = preferences.streakFlow.first()

        val mood = when {
            streak == 0 -> XionAlertMood.SYSTEM_WARNING
            streak < 7  -> XionAlertMood.CALM_BRIEFING
            else        -> XionAlertMood.INSPIRED_BOOST
        }

        val contentTitle = when {
            streak == 0 -> "[ SYSTEM ] MISSION AWAITS"
            streak < 7  -> "[ SYSTEM ] STREAK ACTIVE — DAY $streak"
            else        -> "[ SYSTEM ] DOMINANCE STREAK — $streak DAYS"
        }

        val contentText = when {
            streak == 0 -> "Complete a mission today. The void does not forgive inactivity."
            streak < 7  -> "Your momentum continues. Do not break the chain."
            else        -> "Elite performance sustained. Report for duty."
        }

        // Reuses the mascot-face notification generator (previously built but never
        // called) instead of a bare NotificationCompat.Builder, while keeping this
        // worker's own streak-day-specific copy rather than sendXionNotification's
        // generic per-mood defaults.
        AxiomNotificationManager.sendXionNotification(
            context = applicationContext,
            mood = mood,
            title = contentTitle,
            message = contentText
        )

        return Result.success()
    }
}
