package com.axiom.app.data.analytics

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.analytics.AnalyticsConsentSource
import com.axiom.app.domain.analytics.AnalyticsConsentState
import com.axiom.app.domain.analytics.AnalyticsDispatchEngine
import com.axiom.app.domain.analytics.AnalyticsEventStore
import com.axiom.app.domain.analytics.AnalyticsUploader
import com.axiom.app.domain.analytics.DrainOutcome
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WP-206 — thin Android shell around [AnalyticsDispatchEngine.drain] (§20/§21/§24). Holds NO
 * reliability or consent logic itself: it supplies the three ports (consent / store / uploader)
 * and maps the pure [DrainOutcome] to a WorkManager [Result]. Enqueued uniquely by
 * [AnalyticsGateway] so at most one drain runs at a time (at-least-once precondition, Decision C).
 *
 * Consent is re-read here (a fresh [AnalyticsPreferencesConsentSource] over DataStore) AND again
 * before every upload inside the engine, so a revocation between scheduling and running — or
 * mid-drain — halts egress.
 */
@HiltWorker
class AnalyticsDrainWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val preferences: AxiomPreferences,
    private val store: AnalyticsEventStore,
    private val uploader: AnalyticsUploader
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val consent = AnalyticsPreferencesConsentSource(preferences)
        return when (AnalyticsDispatchEngine.drain(consent, store, uploader)) {
            // Offline / upload failure: rows stay PENDING → ask WorkManager to retry with backoff.
            DrainOutcome.RETRY_LATER -> Result.retry()
            // DRAINED / PURGED_DECLINED / NO_CONSENT are all terminal for this run.
            DrainOutcome.DRAINED,
            DrainOutcome.PURGED_DECLINED,
            DrainOutcome.NO_CONSENT -> Result.success()
        }
    }
}

/** DataStore-backed consent port; re-readable so the engine can re-check before every upload (§16). */
class AnalyticsPreferencesConsentSource(
    private val preferences: AxiomPreferences
) : AnalyticsConsentSource {
    override suspend fun current(): AnalyticsConsentState = preferences.analyticsConsentStateOnce()
}
