package com.axiom.app.domain.analytics

/**
 * WP-206 — pure offline-drain orchestrator (§20/§21/§24). The @HiltWorker CoroutineWorker is a
 * thin Android shell that supplies these three ports and calls [drain]; ALL reliability + consent
 * re-check logic lives here so it is provable on the JVM with hand-written fakes (mirrors WP-205's
 * AtomicCompletionWriteSequence pattern).
 *
 * Contract enforced by [drain]:
 *  - UNKNOWN  → upload 0, no delete (NO_CONSENT). Hard Cap §39.
 *  - DECLINED → purge queued analytics, upload 0 (PURGED_DECLINED). §13/§26.
 *  - GRANTED  → upload each pending analytics row; DELETE a row ONLY after a confirmed successful
 *               upload; a failed upload keeps the row PENDING and stops (RETRY_LATER) so ordering
 *               and at-least-once hold. Consent is re-read BEFORE every upload so a mid-drain
 *               revocation halts further network egress (§16).
 *
 * Delivery is AT-LEAST-ONCE (Decision C): local UNIQUE(idempotencyKey) + single unique worker +
 * delete-after-success. Backend dedup on the key is UNVERIFIED, so exactly-once is NOT claimed;
 * the "server accepted → client dies before delete" window is a documented network-boundary
 * duplicate, not a correctness bug.
 */
object AnalyticsDispatchEngine {

    suspend fun drain(
        consent: AnalyticsConsentSource,
        store: AnalyticsEventStore,
        uploader: AnalyticsUploader
    ): DrainOutcome {
        when (consent.current()) {
            AnalyticsConsentState.DECLINED -> {
                store.purgeAnalytics()
                return DrainOutcome.PURGED_DECLINED
            }
            AnalyticsConsentState.UNKNOWN -> return DrainOutcome.NO_CONSENT
            AnalyticsConsentState.GRANTED -> Unit // fall through to drain loop
        }

        for (event in store.pendingAnalytics()) {
            // Re-check consent BEFORE every upload so a mid-drain revocation halts egress (§16).
            if (consent.current() != AnalyticsConsentState.GRANTED) {
                return DrainOutcome.NO_CONSENT
            }
            if (uploader.upload(event)) {
                store.delete(event.eventId) // delete ONLY after confirmed success (§21)
            } else {
                return DrainOutcome.RETRY_LATER // offline/failure keeps the row PENDING
            }
        }
        return DrainOutcome.DRAINED
    }
}

/** Immutable snapshot of one queued analytics row handed to the uploader. Carries no raw text. */
data class QueuedAnalyticsEvent(
    val eventId: String,
    val idempotencyKey: String,
    val eventType: String,
    val properties: Map<String, String>
)

/** Consent port — re-readable so the worker can re-check before every upload. */
interface AnalyticsConsentSource {
    suspend fun current(): AnalyticsConsentState
}

/** Local durable queue port (backed by event_queue in production). Analytics rows only. */
interface AnalyticsEventStore {
    suspend fun pendingAnalytics(): List<QueuedAnalyticsEvent>
    suspend fun delete(eventId: String)
    /** Delete every queued analytics row (DECLINED purge). Returns rows removed. */
    suspend fun purgeAnalytics(): Int
}

/** Network egress port — the SINGLE place analytics leave the device. Returns confirmed success. */
interface AnalyticsUploader {
    suspend fun upload(event: QueuedAnalyticsEvent): Boolean
}

enum class DrainOutcome {
    /** UNKNOWN — nothing uploaded, nothing deleted. */
    NO_CONSENT,
    /** DECLINED — queued analytics purged, nothing uploaded. */
    PURGED_DECLINED,
    /** GRANTED — all pending analytics uploaded and deleted. */
    DRAINED,
    /** GRANTED — an upload failed (offline); remaining rows kept PENDING for a later run. */
    RETRY_LATER
}
