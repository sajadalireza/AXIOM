package com.axiom.app.data.repository

import com.axiom.app.BuildConfig
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.remote.LeagueScoreRow
import com.axiom.app.data.remote.SubmitScoreBody
import com.axiom.app.data.remote.SupabaseClient
import com.axiom.app.domain.repository.LeagueRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeagueRepositoryImpl @Inject constructor(
    private val preferences: AxiomPreferences
) : LeagueRepository {

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

    private fun hasSupabaseCredentials(): Boolean {
        val url = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val key = sanitizeKey(BuildConfig.SUPABASE_KEY)
        return url.isNotBlank() && key.isNotBlank()
    }

    override suspend fun submitScore(
        rarity: String,
        xp: Int,
        hunterName: String,
        hunterRank: String
    ): Boolean {
        if (!hasSupabaseCredentials()) return false
        val token = preferences.supabaseAccessTokenFlow.first() ?: return false
        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val formatRarity = rarity.uppercase().trim()
            val body = SubmitScoreBody(
                missionRarity = formatRarity,
                xpAmount = xp,
                hunterName = hunterName,
                hunterRank = hunterRank
            )
            val authHeader = "Bearer $token"
            val response = service.submitMissionScore(
                apiKey = cleanKey,
                bearerToken = authHeader,
                body = body
            )
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getLeaderboard(): List<LeagueScoreRow> {
        if (!hasSupabaseCredentials()) return emptyList()
        val token = preferences.supabaseAccessTokenFlow.first() ?: return emptyList()
        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val authHeader = "Bearer $token"
            service.getLeaderboard(
                apiKey = cleanKey,
                bearerToken = authHeader
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
