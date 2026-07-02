package com.axiom.app.data.repository

import com.axiom.app.BuildConfig
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.remote.SupabaseClient
import com.axiom.app.domain.repository.ActivationRepository
import com.axiom.app.domain.repository.ActivationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivationRepositoryImpl @Inject constructor(
    private val preferences: AxiomPreferences
) : ActivationRepository {

    override fun isActivated(): Flow<Boolean> = preferences.activatedFlow

    private fun sanitizeUrl(url: String?): String {
        val trimmed = url?.trim()?.removeSurrounding("\"")?.removeSurrounding("'")?.trim() ?: return ""
        if (trimmed.isEmpty() || trimmed == "https://your-project.supabase.co") return ""
        
        // If it is just a project ref id (like bfhyvesbetydmurrbqxd)
        if (!trimmed.contains(".") && !trimmed.contains("/")) {
            return "https://$trimmed.supabase.co"
        }
        
        // If it lacks https scheme (like bfhyvesbetydmurrbqxd.supabase.co)
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

    override fun hasSupabaseCredentials(): Boolean {
        val url = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val key = sanitizeKey(BuildConfig.SUPABASE_KEY)
        return url.isNotBlank() && key.isNotBlank()
    }

    override suspend fun ensureAnonymousSession() {
        // Session already exists (anonymous or upgraded) — nothing to do.
        val existingToken = preferences.supabaseAccessTokenFlow.first()
        if (!existingToken.isNullOrBlank()) return

        // No Supabase project configured — skip entirely, non-fatal.
        if (!hasSupabaseCredentials()) return

        try {
            val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
            val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)
            val service = SupabaseClient.createService(cleanUrl)
            val response = service.signInAnonymously(apiKey = cleanKey)

            val token = response.accessToken
            if (!token.isNullOrBlank()) {
                preferences.setSupabaseSession(
                    accessToken = token,
                    userId = response.user.id,
                    isAnonymous = true
                )
            }
        } catch (e: Exception) {
            // Network/server error — non-fatal. League submission will
            // simply no-op later if no session exists; core app is
            // entirely unaffected.
            e.printStackTrace()
        }
    }

    override suspend fun upgradeAnonymousToEmail(email: String, password: String): ActivationResult {
        if (email.trim().isBlank() || password.trim().isBlank()) {
            return ActivationResult.Error("Please enter both email and password.")
        }

        val existingToken = preferences.supabaseAccessTokenFlow.first()
        val isAnonymous = preferences.isAnonymousUserFlow.first()

        // No anonymous session to upgrade (e.g. ensureAnonymousSession()
        // never succeeded) — fall back to a normal sign-up so the user
        // is never blocked.
        if (existingToken.isNullOrBlank() || !isAnonymous) {
            return signUpWithEmailPassword(email, password)
        }

        if (!hasSupabaseCredentials()) {
            if (com.axiom.app.BuildConfig.DEBUG) {
                preferences.setActivated(true, "USER-AUTH-TOKEN:MOCK-TOKEN-DEBUG")
                preferences.setUserEmail(email)
                return ActivationResult.Success
            }
            return ActivationResult.Error("Supabase credentials are not configured in your Secrets.")
        }

        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val response = service.upgradeAnonymousToEmail(
                apiKey = cleanKey,
                bearerToken = "Bearer $existingToken",
                body = com.axiom.app.data.remote.UpgradeAccountBody(
                    email = email.trim(),
                    password = password.trim()
                )
            )

            if (response.isSuccessful) {
                val userId = preferences.supabaseUserIdFlow.first().orEmpty()
                preferences.setSupabaseSession(
                    accessToken = existingToken,
                    userId = userId,
                    isAnonymous = false
                )
                preferences.setActivated(true, "USER-AUTH-TOKEN:$existingToken")
                preferences.setUserEmail(email)
                ActivationResult.Success
            } else {
                val errorMessage = when (response.code()) {
                    400, 422 -> "Email might already be registered, or password is too weak."
                    401 -> "Your session expired. Please restart the app and try again."
                    else -> "Failed to link email (HTTP ${response.code()})."
                }
                ActivationResult.Error(errorMessage)
            }
        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            ActivationResult.Error("Failed to link email (HTTP ${e.code()}): ${e.message()}")
        } catch (e: Exception) {
            e.printStackTrace()
            ActivationResult.Error("Connection error: ${e.message ?: "Unknown network failure"}")
        }
    }

    override suspend fun loginWithEmailPassword(email: String, password: String): ActivationResult {
        if (email.trim().isBlank() || password.trim().isBlank()) {
            return ActivationResult.Error("Please enter both email and password.")
        }

        if (!hasSupabaseCredentials()) {
            if (com.axiom.app.BuildConfig.DEBUG) {
                val compositeCode = "USER-AUTH-TOKEN:MOCK-TOKEN-DEBUG"
                preferences.setActivated(true, compositeCode)
                preferences.setSupabaseAccessToken("MOCK-TOKEN-DEBUG")
                preferences.setUserEmail(email)
                return ActivationResult.Success
            }
            return ActivationResult.Error(
                "Supabase credentials are not configured in your Secrets.\n" +
                "To test locally, use the demo activation code: AWAKEN-DEMO-2026"
            )
        }

        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val response = service.loginWithEmailPassword(
                apiKey = cleanKey,
                body = com.axiom.app.data.remote.SupabaseLoginBody(
                    email = email.trim(),
                    password = password.trim()
                )
            )

            // Store the access_token — NOT the password. The token is a JWT that
            // expires and cannot be reversed to recover credentials.
            val token = response.accessToken ?: ""
            val compositeCode = "USER-AUTH-TOKEN:$token"
            preferences.setActivated(true, compositeCode)
            preferences.setSupabaseAccessToken(token)
            preferences.setUserEmail(email)
            ActivationResult.Success

        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            val errorMessage = when (e.code()) {
                400, 401 -> "Invalid email or password.\n" +
                           "• Make sure this user exists in your Supabase Auth dashboard and matches the password."
                403 -> "Access denied (HTTP 403).\n" +
                           "• This user account may be banned or disabled in your Supabase Auth dashboard."
                else -> "Authentication failed (HTTP ${e.code()}): ${e.message()}"
            }
            ActivationResult.Error(errorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ActivationResult.Error("Connection error: ${e.message ?: "Unknown network failure"}")
        }
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String): ActivationResult {
        if (email.trim().isBlank() || password.trim().isBlank()) {
            return ActivationResult.Error("Please enter both email and password.")
        }

        if (!hasSupabaseCredentials()) {
            if (com.axiom.app.BuildConfig.DEBUG) {
                val compositeCode = "USER-AUTH-TOKEN:MOCK-TOKEN-DEBUG"
                preferences.setActivated(true, compositeCode)
                preferences.setSupabaseAccessToken("MOCK-TOKEN-DEBUG")
                preferences.setUserEmail(email)
                return ActivationResult.Success
            }
            return ActivationResult.Error(
                "Supabase credentials are not configured in your Secrets.\n" +
                "To test locally, use the demo activation code: AWAKEN-DEMO-2026"
            )
        }

        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val response = service.signUpWithEmailPassword(
                apiKey = cleanKey,
                body = com.axiom.app.data.remote.SupabaseLoginBody(
                    email = email.trim(),
                    password = password.trim()
                )
            )

            val token = response.accessToken
            if (token != null && token.isNotBlank()) {
                val compositeCode = "USER-AUTH-TOKEN:$token"
                preferences.setActivated(true, compositeCode)
                preferences.setSupabaseAccessToken(token)
                preferences.setUserEmail(email)
                ActivationResult.Success
            } else {
                ActivationResult.Error("Verification email sent! Please check your inbox and verify your email, then login.")
            }

        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            val errorMessage = when (e.code()) {
                400 -> "Registration failed (HTTP 400).\n• Email might already be registered, or password is too weak."
                422 -> "Invalid email format or password policy violation (HTTP 422)."
                else -> "Registration failed (HTTP ${e.code()}): ${e.message()}"
            }
            ActivationResult.Error(errorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ActivationResult.Error("Connection error: ${e.message ?: "Unknown network failure"}")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): ActivationResult {
        if (idToken.isBlank()) {
            return ActivationResult.Error("Invalid Google ID Token.")
        }

        // Bypassing real network request for mock tokens to avoid Supabase API token format failure
        if (idToken.startsWith("MOCK-GOOGLE-") && com.axiom.app.BuildConfig.DEBUG) {
            val compositeCode = "USER-AUTH-TOKEN:MOCK-GOOGLE-TOKEN-DEBUG"
            preferences.setActivated(true, compositeCode)
            preferences.setSupabaseAccessToken("MOCK-GOOGLE-TOKEN-DEBUG")
            preferences.setUserEmail("google-hunter-demo@axiom.com")
            return ActivationResult.Success
        }

        if (!hasSupabaseCredentials()) {
            if (com.axiom.app.BuildConfig.DEBUG) {
                val compositeCode = "USER-AUTH-TOKEN:MOCK-GOOGLE-TOKEN-DEBUG"
                preferences.setActivated(true, compositeCode)
                preferences.setSupabaseAccessToken("MOCK-GOOGLE-TOKEN-DEBUG")
                preferences.setUserEmail("google-hunter-demo@axiom.com")
                return ActivationResult.Success
            }
            return ActivationResult.Error("Supabase credentials are not configured in your Secrets.")
        }

        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val response = service.loginWithIdToken(
                apiKey = cleanKey,
                body = com.axiom.app.data.remote.SupabaseIdTokenBody(
                    provider = "google",
                    token = idToken
                )
            )

            val token = response.accessToken
            if (token != null && token.isNotBlank()) {
                val compositeCode = "USER-AUTH-TOKEN:$token"
                preferences.setActivated(true, compositeCode)
                preferences.setSupabaseAccessToken(token)
                val userEmail = response.user.email ?: "google-hunter@axiom.com"
                preferences.setUserEmail(userEmail)
                ActivationResult.Success
            } else {
                ActivationResult.Error("Google Sign-In succeeded, but Supabase did not return an access token.")
            }

        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            val errorMessage = "Google Sign-In failed on Supabase (HTTP ${e.code()}): ${e.message()}"
            ActivationResult.Error(errorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ActivationResult.Error("Connection error during Google Sign-In: ${e.message ?: "Unknown network failure"}")
        }
    }

    override suspend fun reVerifyOnBoot(): Boolean {
        if (!hasSupabaseCredentials()) return true // No credentials, skip online check
        
        val savedCode = preferences.activationCodeFlow.first() ?: return false
        if (com.axiom.app.BuildConfig.DEBUG && (savedCode == "LOCAL-DEMO-KEY" || savedCode == "AWAKEN-DEMO-2026")) {
            return true // Demo key always valid in debug mode
        }

        // Access token path (current format — replaces old Base64 credential storage)
        if (savedCode.startsWith("USER-AUTH-TOKEN:")) {
            val token = savedCode.substringAfter("USER-AUTH-TOKEN:")
            if (token.isBlank()) { preferences.setActivated(false, ""); return false }
            return try {
                val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
                val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)
                val service  = SupabaseClient.createService(cleanUrl)
                val profile = service.getUserProfile(
                    apiKey     = cleanKey,
                    authHeader = "Bearer $token"
                )
                profile.email?.let { email ->
                    preferences.setUserEmail(email)
                }
                true // Token still valid
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is retrofit2.HttpException && e.code() == 401) {
                    // Token expired or revoked — deactivate
                    preferences.setActivated(false, "")
                    false
                } else {
                    // Network/Timeout error — allow offline session
                    true
                }
            }
        }

        return try {
            val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
            val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)
            val service = SupabaseClient.createService(cleanUrl)
            val apiKey = cleanKey
            val authHeader = "Bearer $apiKey"

            val codes = service.getActivationCode(
                codeFilter = "eq.$savedCode",
                apiKey = apiKey,
                authHeader = authHeader
            )

            if (codes.isEmpty()) {
                // Code deleted from Database -> DEACTIVATE locally!
                preferences.setActivated(false, "")
                false
            } else {
                val matchingCode = codes.first()
                if (!matchingCode.isUsed) {
                    // Code reset or set unused -> DEACTIVATE locally!
                    preferences.setActivated(false, "")
                    false
                } else {
                    true // Still valid and used!
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If network fails (offline), assume true to allow offline use
            true
        }
    }

    override suspend fun preRegisterLeague(email: String): ActivationResult {
        if (email.trim().isBlank()) {
            return ActivationResult.Error("Email address is blank.")
        }

        if (!hasSupabaseCredentials()) {
            if (com.axiom.app.BuildConfig.DEBUG) {
                // Mock success in debug mode when Supabase is not configured
                return ActivationResult.Success
            }
            return ActivationResult.Error("Supabase credentials are not configured in your Secrets.")
        }

        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val response = service.preRegisterLeague(
                apiKey = cleanKey,
                authHeader = "Bearer $cleanKey",
                body = com.axiom.app.data.remote.LeagueWaitlistBody(userEmail = email.trim())
            )

            if (response.isSuccessful) {
                ActivationResult.Success
            } else {
                val errorCode = response.code()
                val errorMessage = when (errorCode) {
                    401 -> "Supabase Key is invalid or unauthorized (HTTP 401)."
                    403 -> "Permission denied (HTTP 403).\n" +
                           "• Row Level Security (RLS) might be blocking this transaction.\n" +
                           "• Ensure the 'league_waitlist' table has a policy allowing anonymous INSERT."
                    404 -> "Table not found (HTTP 404).\n" +
                           "• Ensure your Supabase database has a table named exactly 'league_waitlist' with columns: user_email (text) and created_at (timestamptz)."
                    else -> "Supabase server error (HTTP $errorCode): ${response.message()}"
                }
                ActivationResult.Error(errorMessage)
            }
        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            val errorMessage = when (e.code()) {
                401 -> "Supabase Key is invalid or unauthorized (HTTP 401)."
                403 -> "Permission denied (HTTP 403).\n" +
                       "• Row Level Security (RLS) might be blocking this transaction.\n" +
                       "• Ensure the 'league_waitlist' table has a policy allowing anonymous INSERT."
                404 -> "Table not found (HTTP 404).\n" +
                       "• Ensure your Supabase database has a table named exactly 'league_waitlist' with columns: user_email (text) and created_at (timestamptz)."
                else -> "Supabase server error (HTTP ${e.code()}): ${e.message()}"
            }
            ActivationResult.Error(errorMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            ActivationResult.Error("Connection error: ${e.message ?: "Unknown network failure"}")
        }
    }
}
