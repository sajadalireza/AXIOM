package com.axiom.app.data.remote

import com.axiom.app.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class SupabaseActivationCode(
    @Json(name = "code") val code: String,
    @Json(name = "is_used") val isUsed: Boolean = false,
    @Json(name = "used_by") val usedBy: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseUpdateBody(
    @Json(name = "is_used") val isUsed: Boolean,
    @Json(name = "used_by") val usedBy: String?
)

@JsonClass(generateAdapter = true)
data class SupabaseLoginBody(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SupabaseIdTokenBody(
    @Json(name = "provider") val provider: String = "google",
    @Json(name = "token") val token: String
)

@JsonClass(generateAdapter = true)
data class SupabaseLoginResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Int? = null,
    @Json(name = "user") val user: SupabaseUser
)

@JsonClass(generateAdapter = true)
data class SupabaseUser(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String?,
    @Json(name = "is_anonymous") val isAnonymous: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class UpgradeAccountBody(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SubmitScoreBody(
    @Json(name = "p_mission_rarity") val missionRarity: String,
    @Json(name = "p_xp_amount") val xpAmount: Int,
    @Json(name = "p_hunter_name") val hunterName: String,
    @Json(name = "p_hunter_rank") val hunterRank: String
)

@JsonClass(generateAdapter = true)
data class LeagueScoreRow(
    @Json(name = "user_id") val userId: String,
    @Json(name = "total_score") val totalScore: Long,
    @Json(name = "hunter_name") val hunterName: String,
    @Json(name = "hunter_rank") val hunterRank: String
)

@JsonClass(generateAdapter = true)
data class LeagueWaitlistBody(
    @Json(name = "user_email") val userEmail: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAnonymousBody(
    @Json(name = "data") val data: Map<String, String> = emptyMap()
)

interface SupabaseService {
    @GET("rest/v1/activation_codes")
    suspend fun getActivationCode(
        @Query("code") codeFilter: String, // format: eq.CODE
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): List<SupabaseActivationCode>

    @PATCH("rest/v1/activation_codes")
    suspend fun updateActivationCode(
        @Query("code") codeFilter: String, // format: eq.CODE
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body body: SupabaseUpdateBody
    ): List<SupabaseActivationCode>

    @POST("auth/v1/token")
    suspend fun loginWithEmailPassword(
        @Query("grant_type") grantType: String = "password",
        @Header("apikey") apiKey: String,
        @Body body: SupabaseLoginBody
    ): SupabaseLoginResponse

    @POST("auth/v1/signup")
    suspend fun signUpWithEmailPassword(
        @Header("apikey") apiKey: String,
        @Body body: SupabaseLoginBody
    ): SupabaseLoginResponse

    /**
     * Anonymous sign-in — POST /auth/v1/signup with an empty body.
     * This is GoTrue's documented mechanism for anonymous sessions:
     * it returns a real access_token + user.id with no email/password
     * required, so a brand-new install can talk to Supabase (League
     * submissions, etc.) immediately without any user action.
     */
    @POST("auth/v1/signup")
    suspend fun signInAnonymously(
        @Header("apikey") apiKey: String,
        @Body body: SupabaseAnonymousBody = SupabaseAnonymousBody()
    ): SupabaseLoginResponse

    /**
     * Converts the CURRENT anonymous session into a real email account
     * in place — same user_id, same league history. Must be called with
     * the anonymous session's own bearer token (not the apikey).
     */
    @PUT("auth/v1/user")
    suspend fun upgradeAnonymousToEmail(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body body: UpgradeAccountBody
    ): retrofit2.Response<Unit>

    @POST("auth/v1/token")
    suspend fun loginWithIdToken(
        @Query("grant_type") grantType: String = "id_token",
        @Header("apikey") apiKey: String,
        @Body body: SupabaseIdTokenBody
    ): SupabaseLoginResponse

    /** Verify a stored access_token is still valid (not expired/revoked). */
    @GET("auth/v1/user")
    suspend fun getUserProfile(
        @Header("apikey")       apiKey    : String,
        @Header("Authorization") authHeader: String
    ): SupabaseUser

    @POST("rest/v1/rpc/submit_mission_score")
    suspend fun submitMissionScore(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body body: SubmitScoreBody
    ): retrofit2.Response<Unit>

    @GET("rest/v1/rpc/get_current_season_leaderboard")
    suspend fun getLeaderboard(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String
    ): List<LeagueScoreRow>

    @POST("rest/v1/league_waitlist")
    suspend fun preRegisterLeague(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body body: LeagueWaitlistBody
    ): retrofit2.Response<Unit>

    @GET("rest/v1/user_progress")
    suspend fun getUserProgress(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("user_id") userIdFilter: String
    ): List<UserProgressRow>

    @POST("rest/v1/user_progress")
    suspend fun upsertUserProgress(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body body: UserProgressRow
    ): retrofit2.Response<Unit>

    @POST("functions/v1/extract-blueprint")
    suspend fun extractBlueprint(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body body: ExtractBlueprintRequest
    ): ExtractBlueprintResponse
}

@JsonClass(generateAdapter = true)
data class ExtractBlueprintRequest(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class ExtractedTrack(
    @Json(name = "name") val name: String,
    @Json(name = "color") val color: String,
    @Json(name = "icon") val icon: String,
    @Json(name = "description") val description: String
)

@JsonClass(generateAdapter = true)
data class ExtractedScheduleBlock(
    @Json(name = "startTime") val startTime: String,
    @Json(name = "title") val title: String,
    @Json(name = "actionDescription") val actionDescription: String,
    @Json(name = "tag") val tag: String,
    @Json(name = "isNonNegotiable") val isNonNegotiable: Boolean
)

@JsonClass(generateAdapter = true)
data class ExtractedKPI(
    @Json(name = "name") val name: String,
    @Json(name = "targetValue") val targetValue: Float,
    @Json(name = "targetUnit") val targetUnit: String,
    @Json(name = "measurementHint") val measurementHint: String,
    @Json(name = "redFlagAction") val redFlagAction: String
)

@JsonClass(generateAdapter = true)
data class ExtractedIronRule(
    @Json(name = "ruleText") val ruleText: String
)

@JsonClass(generateAdapter = true)
data class ExtractedTruth(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class ExtractedAffirmation(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class ExtractedMilestone(
    @Json(name = "label") val label: String,
    @Json(name = "targetDate") val targetDate: String
)

@JsonClass(generateAdapter = true)
data class ExtractedRelationship(
    @Json(name = "label") val label: String,
    @Json(name = "category") val category: String,
    @Json(name = "preparedTalkingPoint") val preparedTalkingPoint: String
)

@JsonClass(generateAdapter = true)
data class ExtractBlueprintResponse(
    @Json(name = "codename") val codename: String? = null,
    @Json(name = "oneLineThesis") val oneLineThesis: String? = null,
    @Json(name = "rareProfileDescription") val rareProfileDescription: String? = null,
    @Json(name = "tracks") val tracks: List<ExtractedTrack>? = null,
    @Json(name = "scheduleBlocks") val scheduleBlocks: List<ExtractedScheduleBlock>? = null,
    @Json(name = "customKPIs") val customKPIs: List<ExtractedKPI>? = null,
    @Json(name = "ironRules") val ironRules: List<ExtractedIronRule>? = null,
    @Json(name = "hardTruths") val hardTruths: List<ExtractedTruth>? = null,
    @Json(name = "affirmations") val affirmations: List<ExtractedAffirmation>? = null,
    @Json(name = "majorMilestone") val majorMilestone: ExtractedMilestone? = null,
    @Json(name = "keyRelationships") val keyRelationships: List<ExtractedRelationship>? = null
)

@JsonClass(generateAdapter = true)
data class UserProgressRow(
    @Json(name = "user_id") val userId: String,
    @Json(name = "progress_data") val progressData: String
)

object SupabaseClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // WP-104 SEC-104-004: never log request/response bodies or credential headers.
        // BASIC = method/url/status only; release stays NONE. Redactors are belt-and-
        // suspenders so the anon key can never surface even if the level is raised.
        redactHeader("Authorization")
        redactHeader("apikey")
        level = if (com.axiom.app.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
        else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val serviceCache = mutableMapOf<String, SupabaseService>()

    fun createService(baseUrl: String): SupabaseService {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return serviceCache.getOrPut(formattedUrl) {
            Retrofit.Builder()
                .baseUrl(formattedUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SupabaseService::class.java)
        }
    }
}
