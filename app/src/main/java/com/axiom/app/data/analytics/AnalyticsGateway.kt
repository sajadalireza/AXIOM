package com.axiom.app.data.analytics

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.entity.EventQueueEntity
import com.axiom.app.domain.analytics.AnalyticsConsentState
import com.axiom.app.domain.analytics.AnalyticsPayloadPolicy
import com.axiom.app.domain.analytics.ConsentDecisionEngine
import com.axiom.app.domain.analytics.PayloadValidation
import com.axiom.app.data.local.AxiomPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WP-206 — the SINGLE analytics enqueue + scheduling authority (§11). Every analytics event flows
 * through [track]; nothing else writes an analytics row or schedules the drain worker. Network
 * egress itself lives ONLY in [AnalyticsDrainWorker] → [SupabaseAnalyticsUploader]; this class does
 * no network I/O.
 *
 * Consent is enforced HERE at the front door AND re-checked in the drain loop (defence in depth):
 *  - UNKNOWN  → [ConsentDecisionEngine.shouldEnqueue] true so the row is retained locally, but
 *               [ConsentDecisionEngine.shouldUpload] false so NO drain is scheduled (upload = 0).
 *  - GRANTED  → enqueue + schedule a network-constrained drain.
 *  - DECLINED → do NOT enqueue new rows; [updateConsent] triggers a purge of already-queued rows.
 *
 * Payloads are validated by [AnalyticsPayloadPolicy] BEFORE persistence: a Rejected payload
 * (unknown event / sensitive key / non-allowlisted key) is DROPPED, never written — so raw
 * sensitive text (reflection, mission title, notes, tokens) can never reach the local queue,
 * let alone the network (§G/§40).
 */
@Singleton
class AnalyticsGateway @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: AxiomPreferences,
    private val database: AxiomDatabase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Enqueue one analytics event iff consent allows AND the payload passes the allowlist. */
    fun track(eventName: String, properties: Map<String, Any?> = emptyMap()) {
        scope.launch {
            val state = preferences.analyticsConsentStateOnce()
            if (!ConsentDecisionEngine.shouldEnqueue(state)) return@launch // DECLINED → collect nothing

            val validation = AnalyticsPayloadPolicy.validate(eventName, properties.mapValues { it.value?.toString() ?: "" })
            val clean = when (validation) {
                is PayloadValidation.Accepted -> validation.clean
                is PayloadValidation.Rejected -> return@launch // drop unclassified/sensitive — never persist
            }

            database.eventQueueDao().insert(
                EventQueueEntity(
                    eventId = UUID.randomUUID().toString(),
                    idempotencyKey = "$eventName:${UUID.randomUUID()}",
                    eventType = eventName,
                    payload = JSONObject(clean as Map<*, *>).toString(),
                    status = "PENDING",
                    createdAt = System.currentTimeMillis()
                )
            )

            // Only schedule egress while GRANTED. UNKNOWN retains locally with upload = 0 (§39).
            if (ConsentDecisionEngine.shouldUpload(state)) scheduleDrain()
        }
    }

    /**
     * Reversible consent (§C). Persist the new state, then act on it:
     *  - DECLINED → purge queued analytics LOCALLY & IMMEDIATELY (no network needed — a delete must
     *    not wait on a CONNECTED constraint), and schedule NO egress.
     *  - GRANTED → schedule a drain to flush the retained backlog.
     *  - UNKNOWN → do nothing (rows stay PENDING, upload = 0).
     */
    fun updateConsent(state: AnalyticsConsentState) {
        scope.launch {
            preferences.setAnalyticsConsent(state)
            when (state) {
                AnalyticsConsentState.DECLINED ->
                    database.eventQueueDao().purgeByTypes(AnalyticsPayloadPolicy.ANALYTICS_EVENT_TYPES.toList())
                AnalyticsConsentState.GRANTED -> scheduleDrain()
                AnalyticsConsentState.UNKNOWN -> Unit
            }
        }
    }

    /** Kick the drain worker (used at app start and after a consent change). */
    fun requestDrain() = scheduleDrain()

    private fun scheduleDrain() {
        val request = OneTimeWorkRequestBuilder<AnalyticsDrainWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP, // single unique worker (§C at-least-once precondition)
            request
        )
    }

    companion object {
        const val UNIQUE_WORK_NAME = "wp206_analytics_drain"
    }
}
