package com.axiom.app.core

import android.os.Build
import com.axiom.app.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object CrashReporter {

    fun init() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { 
                AppInitDiagnostics.log("CRITICAL_UNCAUGHT_CRASH", "Uncaught exception in thread '${thread.name}' (ID: ${thread.id})")
                AppInitDiagnostics.logException(throwable, "UNCAUGHT_CRASH")
                Thread {
                    runCatching { send(throwable) }
                }.start()
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun send(throwable: Throwable) {
        val rawUrl = BuildConfig.SUPABASE_URL
        val rawKey = BuildConfig.SUPABASE_KEY
        if (rawUrl.isBlank() || rawKey.isBlank() || rawUrl.contains("your-project")) {
            return
        }
        val url = java.net.URL("$rawUrl/rest/v1/crash_reports")
        val conn = url.openConnection() as java.net.HttpURLConnection

        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("apikey", BuildConfig.SUPABASE_KEY)
        conn.setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
        conn.connectTimeout = 3000
        conn.readTimeout = 3000
        conn.doOutput = true

        val body = JSONObject().apply {
            put("app_version", BuildConfig.VERSION_NAME)
            put("android_ver", Build.VERSION.RELEASE)
            put("device", "${Build.MANUFACTURER} ${Build.MODEL}")
            put("error", throwable.toString())
            put("stacktrace", throwable.stackTraceToString())
        }

        OutputStreamWriter(conn.outputStream).use { it.write(body.toString()) }
        conn.responseCode
        conn.disconnect()
    }
}
