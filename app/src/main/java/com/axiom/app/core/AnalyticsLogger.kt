package com.axiom.app.core

import android.util.Log
import com.axiom.app.data.analytics.AnalyticsGateway

/**
 * WP-206 — [AnalyticsLogger] is now a THIN FAÇADE over [AnalyticsGateway], not a network client.
 *
 * Historically this object POSTed directly to Supabase `analytics_events` on a raw [Thread] with
 * NO consent check — a privacy defect. That direct egress is REMOVED. Every call site
 * (`AnalyticsLogger.log(...)`, unchanged signature so no caller edits are needed) now forwards to
 * the single consent-governed [AnalyticsGateway], which:
 *   1. checks three-state consent (UNKNOWN/GRANTED/DECLINED),
 *   2. validates the payload against the allowlist/blacklist,
 *   3. enqueues a durable row, and
 *   4. schedules the drain worker (the ONLY analytics network egress).
 *
 * Fail-closed: if the gateway has not been bound yet (before Application init), the call is a
 * no-op — it NEVER performs network I/O directly. [bind] is called once from AwakenApplication.
 */
object AnalyticsLogger {
    private const val TAG = "AnalyticsLogger"

    @Volatile
    private var gateway: AnalyticsGateway? = null

    /** Bind the single gateway at app startup. Idempotent. */
    fun bind(gateway: AnalyticsGateway) {
        this.gateway = gateway
    }

    fun log(eventName: String, properties: Map<String, Any?> = emptyMap()) {
        val g = gateway
        if (g == null) {
            Log.w(TAG, "AnalyticsGateway not bound; dropping '$eventName' (fail-closed, no direct egress)")
            return
        }
        g.track(eventName, properties)
    }
}
