package com.axiom.app.data.analytics

import android.util.Log
import com.axiom.app.BuildConfig
import com.axiom.app.domain.analytics.AnalyticsUploader
import com.axiom.app.domain.analytics.QueuedAnalyticsEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WP-206 — the SINGLE analytics network egress (§11: ANALYTICS NETWORK EGRESS AUTHORITIES = 1).
 * Posts one validated [QueuedAnalyticsEvent] to Supabase `analytics_events`. Called ONLY by the
 * drain worker via [com.axiom.app.domain.analytics.AnalyticsDispatchEngine]; no other code path
 * uploads analytics (the old AnalyticsLogger direct-POST is removed).
 *
 * Delivery is AT-LEAST-ONCE: [upload] returns true ONLY on a confirmed 2xx so the caller deletes
 * the row after success. Backend key-dedup is UNVERIFIED, so exactly-once is not claimed; a
 * "server accepted → process dies before delete" retry is a documented duplicate boundary.
 * Returns false (row kept PENDING) on any non-2xx, timeout, offline, or unconfigured backend.
 */
@Singleton
class SupabaseAnalyticsUploader @Inject constructor() : AnalyticsUploader {

    override suspend fun upload(event: QueuedAnalyticsEvent): Boolean = withContext(Dispatchers.IO) {
        val rawUrl = BuildConfig.SUPABASE_URL
        val rawKey = BuildConfig.SUPABASE_KEY
        if (rawUrl.isBlank() || rawKey.isBlank() || rawUrl.contains("your-project")) {
            return@withContext false // no destination — retain locally (no silent delete)
        }

        runCatching {
            val conn = (URL("$rawUrl/rest/v1/analytics_events").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("apikey", rawKey)
                setRequestProperty("Authorization", "Bearer $rawKey")
                setRequestProperty("Prefer", "return=minimal")
                connectTimeout = 3000
                readTimeout = 3000
                doOutput = true
            }
            val body = JSONObject().apply {
                put("event_name", event.eventType)
                put("app_version", BuildConfig.VERSION_NAME)
                // Idempotency key travels in properties (backend dedup UNVERIFIED — best-effort).
                put("properties", JSONObject(event.properties as Map<*, *>).put("idempotency_key", event.idempotencyKey))
            }
            OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
            val code = conn.responseCode
            conn.disconnect()
            code in 200..299
        }.getOrElse {
            Log.e("AnalyticsUploader", "upload failed for ${event.eventType}", it)
            false
        }
    }
}
