package com.axiom.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.axiom.app.core.CrashReporter
import com.axiom.app.core.notification.AxiomNotificationManager
import com.axiom.app.core.sound.SoundEngine
import com.axiom.app.data.local.AxiomPreferences
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AwakenApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var preferences: AxiomPreferences

    override fun onCreate() {
        super.onCreate()
        
        // Always initialize diagnostics and crash reporter first so we can catch any subsequent errors
        try {
            com.axiom.app.core.AppInitDiagnostics.init(this)
        } catch (t: Throwable) {
            android.util.Log.e("AwakenApplication", "Failed to initialize AppInitDiagnostics", t)
        }

        try {
            CrashReporter.init()
        } catch (t: Throwable) {
            com.axiom.app.core.AppInitDiagnostics.logException(t, "CRASH_REPORTER_INIT")
        }

        com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "Application onCreate starting...")

        // Manually initialize WorkManager to ensure it uses the injected Hilt workerFactory safely
        try {
            if (::workerFactory.isInitialized) {
                val config = Configuration.Builder()
                    .setWorkerFactory(workerFactory)
                    .build()
                WorkManager.initialize(this, config)
                com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "WorkManager initialized successfully.")
            } else {
                com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "workerFactory not initialized yet (normal for some tests/flows).")
            }
        } catch (e: Exception) {
            com.axiom.app.core.AppInitDiagnostics.logException(e, "WORKMANAGER_INIT")
        }

        // Initialize SoundEngine
        try {
            SoundEngine.init(this)
            com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "SoundEngine initialized successfully.")
        } catch (t: Throwable) {
            com.axiom.app.core.AppInitDiagnostics.logException(t, "SOUND_ENGINE_INIT")
        }

        // Initialize notification channel
        try {
            AxiomNotificationManager.createNotificationChannel(this)
            com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "Notification channels created.")
        } catch (t: Throwable) {
            com.axiom.app.core.AppInitDiagnostics.logException(t, "NOTIFICATION_CHANNEL_INIT")
        }

        // Schedule notifications — only once onboarding is actually done. Otherwise a
        // user who installs and doesn't finish onboarding the same day can still get
        // a "streak == 0" fear-copy notification that evening, before they've formed
        // any relationship with the app at all.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // WP-104 SEC-104-003: migrate any legacy plaintext Gemini key into the
                // encrypted keystore and remove the plaintext. Idempotent + fail-safe.
                preferences.migrateGeminiKeyIfNeeded()
            } catch (t: Throwable) {
                com.axiom.app.core.AppInitDiagnostics.logException(t, "GEMINI_KEY_MIGRATION")
            }
            try {
                if (preferences.firstMissionDoneFlow.first()) {
                    AxiomNotificationManager.scheduleStreakReminder(this@AwakenApplication, hour = 21, minute = 0)
                    com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "Streak reminders scheduled.")
                } else {
                    com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "Streak reminders skipped (onboarding not complete yet).")
                }
            } catch (t: Throwable) {
                com.axiom.app.core.AppInitDiagnostics.logException(t, "NOTIFICATION_SCHEDULE_INIT")
            }
        }
        
        com.axiom.app.core.AppInitDiagnostics.log("STARTUP_INIT", "Application onCreate finished.")
    }

    override val workManagerConfiguration: Configuration
        get() = if (::workerFactory.isInitialized) {
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        } else {
            Configuration.Builder().build()
        }
}
