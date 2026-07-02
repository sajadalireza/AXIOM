package com.axiom.app.core

import android.util.Log
import com.axiom.app.BuildConfig
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Minimal fire-and-forget event log, posted to a Supabase `analytics_events` table.
 * Mirrors CrashReporter's own posting pattern. Requires a table with columns
 * (event_name text, app_version text, properties jsonb, created_at timestamptz
 * default now()) to exist in the configured Supabase project — silently no-ops
 * (never crashes the caller) if credentials are missing or the table doesn't exist yet.
 *
 * This exists to answer the one question the app currently has zero way to answer:
 * did a shipped feature actually work for a real user, not just "did it compile."
 */
object AnalyticsLogger {
    private const val TAG = "AnalyticsLogger"

    fun log(eventName: String, properties: Map<String, Any?> = emptyMap()) {
        Thread {
            runCatching { send(eventName, properties) }
                .onFailure { Log.e(TAG, "Failed to log event '$eventName'", it) }
        }.start()
    }

    private fun send(eventName: String, properties: Map<String, Any?>) {
        val rawUrl = BuildConfig.SUPABASE_URL
        val rawKey = BuildConfig.SUPABASE_KEY
        if (rawUrl.isBlank() || rawKey.isBlank() || rawUrl.contains("your-project")) {
            return
        }

        val url = URL("$rawUrl/rest/v1/analytics_events")
        val conn = url.openConnection() as HttpURLConnection

        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("apikey", rawKey)
        conn.setRequestProperty("Authorization", "Bearer $rawKey")
        conn.setRequestProperty("Prefer", "return=minimal")
        conn.connectTimeout = 3000
        conn.readTimeout = 3000
        conn.doOutput = true

        val body = JSONObject().apply {
            put("event_name", eventName)
            put("app_version", BuildConfig.VERSION_NAME)
            put("properties", JSONObject(properties.mapValues { it.value?.toString() }))
        }

        OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
        conn.responseCode
        conn.disconnect()
    }
}
