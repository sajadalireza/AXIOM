package com.axiom.app.data.repository

import com.axiom.app.BuildConfig
import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.local.entity.*
import com.axiom.app.data.remote.SupabaseClient
import com.axiom.app.data.remote.UserProgressRow
import com.axiom.app.domain.repository.CloudSyncRepository
import androidx.room.withTransaction
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@JsonClass(generateAdapter = true)
data class CloudProgressData(
    val streak: Int,
    val longestStreak: Int,
    val lastCompleteTimestamp: Long,
    val execution: Int,
    val focus: Int,
    val knowledge: Int,
    val business: Int,
    val fitness: Int,
    val creativity: Int,
    val hardModeEnabled: Boolean,
    val leaguePoints: Int,
    val equippedPassiveSkillId: String?,
    val hunter: List<HunterEntity>?,
    val skills: List<SkillEntity>?,
    val missions: List<MissionEntity>?,
    val dungeons: List<DungeonEntity>?,
    val shadows: List<ShadowEntity>?,
    val streaks: List<StreakEntity>?,
    val systemFeed: List<SystemFeedEntity>?
)

@Singleton
class CloudSyncRepositoryImpl @Inject constructor(
    private val database: AxiomDatabase,
    private val preferences: AxiomPreferences
) : CloudSyncRepository {

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

    override suspend fun backupProgress(): Boolean {
        if (!hasSupabaseCredentials()) return false
        val userId = preferences.supabaseUserIdFlow.first() ?: return false
        val token = preferences.supabaseAccessTokenFlow.first() ?: return false

        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val streak = preferences.streakFlow.first()
            val longestStreak = preferences.longestStreakFlow.first()
            val lastCompleteTimestamp = preferences.lastCompleteTimestampFlow.first()
            val stats = preferences.statsFlow.first()
            val hardModeEnabled = preferences.hardModeEnabledFlow.first()
            val leaguePoints = preferences.leaguePointsFlow.first()
            val equippedPassiveSkillId = preferences.equippedPassiveSkillIdFlow.first()

            val hunterProfile = database.hunterDao().getProfile()
            val hunter = if (hunterProfile != null) listOf(hunterProfile) else emptyList()
            val skills = database.skillDao().getAllSkillsFlow().first()
            val missions = database.missionDao().getAllMissionsFlow().first()
            val dungeons = database.dungeonDao().getAllDungeonsFlow().first()
            val shadows = database.shadowDao().getAllShadowsFlow().first()
            val streakEntity = database.streakDao().getStreak().first()
            val streaks = if (streakEntity != null) listOf(streakEntity) else emptyList()
            val systemFeed = database.systemFeedDao().getFeed(500).first()

            val progressData = CloudProgressData(
                streak = streak,
                longestStreak = longestStreak,
                lastCompleteTimestamp = lastCompleteTimestamp,
                execution = stats.execution,
                focus = stats.focus,
                knowledge = stats.knowledge,
                business = stats.business,
                fitness = stats.fitness,
                creativity = stats.creativity,
                hardModeEnabled = hardModeEnabled,
                leaguePoints = leaguePoints,
                equippedPassiveSkillId = equippedPassiveSkillId,
                hunter = hunter,
                skills = skills,
                missions = missions,
                dungeons = dungeons,
                shadows = shadows,
                streaks = streaks,
                systemFeed = systemFeed
            )

            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(CloudProgressData::class.java)
            val jsonString = adapter.toJson(progressData)

            val service = SupabaseClient.createService(cleanUrl)
            val body = UserProgressRow(userId = userId, progressData = jsonString)
            val response = service.upsertUserProgress(
                apiKey = cleanKey,
                bearerToken = "Bearer $token",
                body = body
            )
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun restoreProgress(): Boolean {
        if (!hasSupabaseCredentials()) return false
        val userId = preferences.supabaseUserIdFlow.first() ?: return false
        val token = preferences.supabaseAccessTokenFlow.first() ?: return false

        val cleanUrl = sanitizeUrl(BuildConfig.SUPABASE_URL)
        val cleanKey = sanitizeKey(BuildConfig.SUPABASE_KEY)

        return try {
            val service = SupabaseClient.createService(cleanUrl)
            val list = service.getUserProgress(
                apiKey = cleanKey,
                bearerToken = "Bearer $token",
                userIdFilter = "eq.$userId"
            )

            val row = list.firstOrNull() ?: return false
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(CloudProgressData::class.java)
            val data = adapter.fromJson(row.progressData) ?: return false

            // Clean existing database and re-populate atomically — a partial
            // failure mid-restore must not leave the user with a wiped local DB.
            database.withTransaction {
                database.clearAllTables()

                data.hunter?.firstOrNull()?.let { database.hunterDao().updateProfile(it) }
                data.skills?.forEach { database.skillDao().insertSkill(it) }
                data.missions?.forEach { database.missionDao().insertMission(it) }
                data.dungeons?.forEach { database.dungeonDao().insertDungeon(it) }
                data.shadows?.forEach { database.shadowDao().insertShadow(it) }
                data.streaks?.forEach { database.streakDao().insertStreak(it) }
                data.systemFeed?.forEach { database.systemFeedDao().insertMessage(it) }
            }

            // Restore preferences
            preferences.restoreStatsAndStreak(
                streakValue = data.streak,
                longestStreakValue = data.longestStreak,
                lastComplete = data.lastCompleteTimestamp,
                executionValue = data.execution,
                focusValue = data.focus,
                knowledgeValue = data.knowledge,
                businessValue = data.business,
                fitnessValue = data.fitness,
                creativityValue = data.creativity
            )

            preferences.setHardModeEnabled(data.hardModeEnabled)
            data.equippedPassiveSkillId?.let { preferences.setEquippedPassiveSkillId(it) }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
