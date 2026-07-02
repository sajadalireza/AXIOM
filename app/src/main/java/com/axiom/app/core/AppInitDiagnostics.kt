package com.axiom.app.core

import android.content.Context
import android.os.Build
import android.util.Log
import com.axiom.app.BuildConfig
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.remote.SupabaseClient
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.SkillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale

/**
 * Diagnostic utility to track and log the application's startup sequence.
 * Tracks Supabase loading, user auth status, local database health, and crashes.
 */
object AppInitDiagnostics {
    private const val TAG = "AppInitDiagnostics"
    private const val LOG_FILE_NAME = "init_diagnostics.txt"

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val logList = Collections.synchronizedList(mutableListOf<String>())
    private var logFile: File? = null

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    // Tracks individual step statuses
    val supabaseConfigStatus = MutableStateFlow<StepStatus>(StepStatus.PENDING)
    val supabaseClientStatus = MutableStateFlow<StepStatus>(StepStatus.PENDING)
    val userAuthStatus = MutableStateFlow<StepStatus>(StepStatus.PENDING)
    val dbFetchStatus = MutableStateFlow<StepStatus>(StepStatus.PENDING)

    enum class StepStatus {
        PENDING, RUNNING, SUCCESS, FAILED
    }

    fun init(context: Context) {
        logFile = File(context.cacheDir, LOG_FILE_NAME)
        // Clean old diagnostics file on start to keep it fresh
        try {
            if (logFile?.exists() == true) {
                logFile?.delete()
            }
            logFile?.createNewFile()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset diagnostics log file", e)
        }

        log("SYSTEM", "========================================")
        log("SYSTEM", "   WARRIOR DIAGNOSTIC LOGGER INITIALIZED")
        log("SYSTEM", "========================================")
        log("SYSTEM", "Device: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})")
        log("SYSTEM", "App Version: ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})")
    }

    fun log(section: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val formattedLog = "[$timestamp] [$section] $message"
        
        Log.d(TAG, formattedLog)
        logList.add(formattedLog)
        _logs.value = logList.toList()

        // Append to local diagnostics file asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                logFile?.appendText(formattedLog + "\n")
            } catch (e: Exception) {
                // Ignore file logging exceptions
            }
        }
    }

    fun logException(throwable: Throwable, context: String = "UNCAUGHT_CRASH") {
        val stackTrace = throwable.stackTraceToString()
        log("CRITICAL_$context", "Exception encountered: ${throwable.localizedMessage ?: throwable.toString()}")
        log("CRITICAL_$context", "Stacktrace:\n$stackTrace")
    }

    fun getDiagnosticsFileContent(): String {
        return try {
            if (logFile?.exists() == true) {
                logFile?.readText().orEmpty()
            } else {
                "No local diagnostics file found."
            }
        } catch (e: Exception) {
            "Failed to read diagnostics file: ${e.message}"
        }
    }

    /**
     * Executes the comprehensive startup check sequence
     */
    fun runStartupCheckSequence(
        context: Context,
        preferences: AxiomPreferences,
        hunterRepository: HunterRepository,
        skillRepository: SkillRepository
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            log("DIAGNOSTICS", "🚀 Starting application initialization diagnostics...")

            // 1. Loading Supabase Configuration
            try {
                supabaseConfigStatus.value = StepStatus.RUNNING
                log("SUPABASE_CONFIG", "Reading build configurations...")
                
                val rawUrl = BuildConfig.SUPABASE_URL
                val rawKey = BuildConfig.SUPABASE_KEY
                
                log("SUPABASE_CONFIG", "Raw URL from config: '${if (rawUrl.isNullOrBlank()) "EMPTY" else rawUrl}'")
                log("SUPABASE_CONFIG", "Raw Key from config: '${if (rawKey.isNullOrBlank()) "EMPTY" else if (rawKey.length > 20) rawKey.take(10) + "..." + rawKey.takeLast(10) else "SET_BUT_SHORT"}'")

                val cleanUrl = sanitizeUrl(rawUrl)
                val cleanKey = sanitizeKey(rawKey)

                if (cleanUrl.isBlank() || cleanKey.isBlank()) {
                    log("SUPABASE_CONFIG", "⚠️ WARNING: Supabase credentials are empty or placeholder. Cloud features will operate in demo/fallback mode.")
                    supabaseConfigStatus.value = StepStatus.FAILED
                } else {
                    log("SUPABASE_CONFIG", "Sanitized URL: $cleanUrl")
                    log("SUPABASE_CONFIG", "Supabase configurations validated successfully.")
                    supabaseConfigStatus.value = StepStatus.SUCCESS
                }
            } catch (e: Exception) {
                logException(e, "SUPABASE_CONFIG")
                supabaseConfigStatus.value = StepStatus.FAILED
            }

            // 2. Loading Supabase Client
            try {
                supabaseClientStatus.value = StepStatus.RUNNING
                val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
                if (cleanUrl.isBlank()) {
                    log("SUPABASE_CLIENT", "Skipping client instantiation (no valid URL configured).")
                    supabaseClientStatus.value = StepStatus.FAILED
                } else {
                    log("SUPABASE_CLIENT", "Instantiating Supabase service gateway...")
                    val service = SupabaseClient.createService(cleanUrl)
                    log("SUPABASE_CLIENT", "Supabase Client Service successfully built via Retrofit.")
                    supabaseClientStatus.value = StepStatus.SUCCESS
                }
            } catch (e: Exception) {
                logException(e, "SUPABASE_CLIENT")
                supabaseClientStatus.value = StepStatus.FAILED
            }

            // 3. Loading User Authentication Status
            try {
                userAuthStatus.value = StepStatus.RUNNING
                log("USER_AUTH", "Reading Datastore user preference parameters...")
                
                val isSetupComplete = preferences.setupCompleteFlow.first()
                val isActivated = preferences.activatedFlow.first()
                val activationCode = preferences.activationCodeFlow.first().orEmpty()
                val userEmail = preferences.userEmailFlow.first().orEmpty()
                val accessToken = preferences.supabaseAccessTokenFlow.first().orEmpty()
                val userId = preferences.supabaseUserIdFlow.first().orEmpty()
                val isAnonymous = preferences.isAnonymousUserFlow.first()

                log("USER_AUTH", "Preferences - Setup Complete: $isSetupComplete")
                log("USER_AUTH", "Preferences - Activated State: $isActivated")
                log("USER_AUTH", "Preferences - Email: '${if (userEmail.isEmpty()) "NONE" else userEmail}'")
                log("USER_AUTH", "Preferences - Anonymous User: $isAnonymous")
                log("USER_AUTH", "Preferences - Supabase User ID: '${if (userId.isEmpty()) "NONE" else userId}'")
                log("USER_AUTH", "Preferences - Supabase Token Length: ${accessToken.length}")
                log("USER_AUTH", "Preferences - Activation Code: '${if (activationCode.isEmpty()) "NONE" else if (activationCode.length > 15) activationCode.take(8) + "..." else activationCode}'")

                if (isActivated) {
                    log("USER_AUTH", "User is Authenticated/Activated.")
                } else {
                    log("USER_AUTH", "User is Unauthenticated/Guest.")
                }
                userAuthStatus.value = StepStatus.SUCCESS
            } catch (e: Exception) {
                logException(e, "USER_AUTH")
                userAuthStatus.value = StepStatus.FAILED
            }

            // 4. Initial Local Data Fetch
            try {
                dbFetchStatus.value = StepStatus.RUNNING
                log("LOCAL_DB", "Probing SQLite local Room database...")

                val hunterProfile = hunterRepository.getDirectHunterProfile()
                if (hunterProfile == null) {
                    log("LOCAL_DB", "Hunter profile is empty (onboarding pending).")
                } else {
                    log("LOCAL_DB", "Hunter profile found: ID=${hunterProfile.id}, Name='${hunterProfile.name}', Level=${hunterProfile.level}, Rank='${hunterProfile.rankLabel}'")
                }

                log("LOCAL_DB", "Retrieving skill list...")
                val skills = skillRepository.getAllSkills().first()
                log("LOCAL_DB", "Successfully fetched ${skills.size} skills from SkillTree database.")

                log("LOCAL_DB", "Local database integrity verified successfully.")
                dbFetchStatus.value = StepStatus.SUCCESS
            } catch (e: Exception) {
                logException(e, "LOCAL_DB")
                dbFetchStatus.value = StepStatus.FAILED
            }

            log("DIAGNOSTICS", "✅ Application initialization diagnostics complete.")
        }
    }

    private fun sanitizeUrl(url: String?): String {
        val trimmed = url?.trim()?.removeSurrounding("\"")?.removeSurrounding("'")?.trim() ?: return ""
        if (trimmed.isEmpty() || trimmed == "https://your-project.supabase.co") return ""
        if (!trimmed.contains(".") && !trimmed.contains("/")) {
            return "https://$trimmed.supabase.co"
        }
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://$trimmed"
        }
        return trimmed
    }

    private fun sanitizeKey(key: String?): String {
        val trimmed = key?.trim()?.removeSurrounding("\"")?.removeSurrounding("'")?.trim() ?: return ""
        if (trimmed == "your_supabase_anon_public_key") return ""
        return trimmed
    }
}
