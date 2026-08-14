package com.axiom.app.domain.analytics

/**
 * WP-206 — pure consent decision engine (Decision A, §6). Deterministic, side-effect-free:
 * `ConsentState → routing`. Contains NO network, DataStore, Room, or clock access so the whole
 * UNKNOWN=0 / DECLINED=0 / GRANTED-eligible contract is provable on the JVM with no fakes.
 *
 * The Android layers (gateway, worker, uploader) MUST route every analytics decision through
 * this object — there is no second place where consent is interpreted.
 */
object ConsentDecisionEngine {

    /** Per-event routing verdict. Total function of [state]. */
    fun decide(state: AnalyticsConsentState): QueueDecision = when (state) {
        AnalyticsConsentState.UNKNOWN -> QueueDecision.HOLD
        AnalyticsConsentState.GRANTED -> QueueDecision.SEND_ELIGIBLE
        AnalyticsConsentState.DECLINED -> QueueDecision.DELETE
    }

    /**
     * May an ordinary analytics event be written to the local queue at all?
     * DECLINED → false (do-not-collect, Decision D); UNKNOWN/GRANTED → true (UNKNOWN retains
     * locally without uploading).
     */
    fun shouldEnqueue(state: AnalyticsConsentState): Boolean =
        state != AnalyticsConsentState.DECLINED

    /** May queued analytics be uploaded over the network? GRANTED only (§12/§14). */
    fun shouldUpload(state: AnalyticsConsentState): Boolean =
        state == AnalyticsConsentState.GRANTED

    /** Must already-queued analytics be purged now? DECLINED only (§13/§26). */
    fun shouldPurge(state: AnalyticsConsentState): Boolean =
        state == AnalyticsConsentState.DECLINED
}
