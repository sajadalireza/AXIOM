package com.axiom.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.axiom.app.domain.model.CharacterStats
import com.axiom.app.domain.analytics.AnalyticsConsentState
import com.axiom.app.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "axiom_prefs")

@Singleton
open class AxiomPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val STREAK = intPreferencesKey("streak")
        private val STREAK_FREEZE = intPreferencesKey("streak_freeze")
        private val LONGEST_STREAK = intPreferencesKey("longest_streak")
        private val LAST_COMPLETE_TIMESTAMP = longPreferencesKey("last_complete_timestamp")
        private val STAT_EXECUTION = intPreferencesKey("stat_execution")
        private val STAT_FOCUS = intPreferencesKey("stat_focus")
        private val STAT_KNOWLEDGE = intPreferencesKey("stat_knowledge")
        private val STAT_BUSINESS = intPreferencesKey("stat_business")
        private val STAT_FITNESS = intPreferencesKey("stat_fitness")
        private val STAT_CREATIVITY = intPreferencesKey("stat_creativity")
        private val SKILL_TREE_SEEDED = booleanPreferencesKey("skill_tree_seeded")
        private val MUSCLE_GROUPS_SEEDED = booleanPreferencesKey("muscle_groups_seeded")
        private val ALIREZA_PROFILE_SEEDED = booleanPreferencesKey("alireza_profile_seeded")
        private val AWAKENING_SHOWN = booleanPreferencesKey("awakening_shown")
        private val BRIEFING_HOME = booleanPreferencesKey("briefing_home")
        private val BRIEFING_MISSIONS = booleanPreferencesKey("briefing_missions")
        private val BRIEFING_DUNGEONS = booleanPreferencesKey("briefing_dungeons")
        private val BRIEFING_SKILL_TREE = booleanPreferencesKey("briefing_skill_tree")
        private val BRIEFING_SHADOW = booleanPreferencesKey("briefing_shadow")
        private val BRIEFING_SYSTEM_VOICE = booleanPreferencesKey("briefing_system_voice")
        // Legacy plaintext key (WP-104 SEC-104-003): migrated into the encrypted
        // GeminiKeyStore and then removed. Retained only as a migration source.
        private val GEMINI_API_KEY  = stringPreferencesKey("gemini_api_key")
        // Reactive presence flag for the encrypted Gemini key (value lives in GeminiKeyStore).
        private val GEMINI_KEY_PRESENT = booleanPreferencesKey("gemini_key_present")
        private val DAILY_BRIEFING  = stringPreferencesKey("daily_briefing_text")
        private val BRIEFING_DATE   = stringPreferencesKey("daily_briefing_date")
        private val BRIEFING_LANG   = stringPreferencesKey("daily_briefing_lang")
        private val THEME_MODE      = stringPreferencesKey("theme_mode")
        private val LANGUAGE        = stringPreferencesKey("language")
        private val SETUP_COMPLETE  = booleanPreferencesKey("setup_complete")
        private val ONBOARDING_SPOTLIGHT_DONE = booleanPreferencesKey("onboarding_spotlight_done")
        // Tracks whether the one-time "Save Your Progress" nudge dialog
        // (shown to anonymous users after a few days) has already fired.
        private val SAVE_PROGRESS_NUDGE_SHOWN = booleanPreferencesKey("save_progress_nudge_shown")
        private val LAST_SHOWN_STREAK_MILESTONE = intPreferencesKey("last_shown_streak_milestone")
        private val SYSTEM_ANOMALY_COUNT = intPreferencesKey("system_anomaly_count")
        private val SYSTEM_ANOMALY_COUNT_DATE = stringPreferencesKey("system_anomaly_count_date")
        private const val MAX_DAILY_SYSTEM_ANOMALIES = 3
        private val SYSTEM_VOICE_MODE = stringPreferencesKey("system_voice_mode")
        private val ACTIVATED = booleanPreferencesKey("activated")
        private val ACTIVATION_CODE = stringPreferencesKey("activation_code")
        // WP-206 — three-state analytics consent (default UNKNOWN) + last-change timestamp.
        private val ANALYTICS_CONSENT_STATE = stringPreferencesKey("analytics_consent_state")
        private val ANALYTICS_CONSENT_UPDATED_AT = longPreferencesKey("analytics_consent_updated_at")
        private val IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val PREMIUM_PLAN = stringPreferencesKey("premium_plan")
        private val EQUIPPED_PASSIVE_SKILL_ID = stringPreferencesKey("equipped_passive_skill_id")
        // PRIORITY 3: tracks when the user first opened the app, for progressive disclosure
        private val FIRST_LAUNCH_TIMESTAMP = longPreferencesKey("first_launch_timestamp")
        private val DEV_BYPASS = booleanPreferencesKey("dev_bypass")
        private val LEAGUE_POINTS = intPreferencesKey("league_points")
        private val LEAGUE_PREREGISTERED = booleanPreferencesKey("league_preregistered")
        private val SUPABASE_ACCESS_TOKEN = stringPreferencesKey("supabase_access_token")
        private val SUPABASE_USER_ID = stringPreferencesKey("supabase_user_id")
        private val IS_ANONYMOUS_USER = booleanPreferencesKey("is_anonymous_user")
        private val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
        private val FIRST_MISSION_DONE = booleanPreferencesKey("first_mission_done")
        private val HARD_MODE_ENABLED = booleanPreferencesKey("hard_mode_enabled")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val LAST_LOGIN_DATE = stringPreferencesKey("last_login_date")
        private val WEEKLY_KEY         = stringPreferencesKey("weekly_key")
        private val WEEKLY_MISSIONS    = intPreferencesKey("weekly_missions_done")
        private val WEEKLY_STREAK_BEST = intPreferencesKey("weekly_streak_best")
        private val WEEKLY_RARE_DONE   = intPreferencesKey("weekly_rare_done")
        private val WEEKLY_ALL_CLAIMED = booleanPreferencesKey("weekly_all_claimed")

        private val FOCUS_ACTIVE = booleanPreferencesKey("focus_active")
        private val FOCUS_START_TIME = longPreferencesKey("focus_start_time")
        private val FOCUS_END_TIME = longPreferencesKey("focus_end_time")
        private val FOCUS_TITLE = stringPreferencesKey("focus_title")
        private val FOCUS_MISSION_ID = stringPreferencesKey("focus_mission_id")
        private val FOCUS_DUNGEON_ID = stringPreferencesKey("focus_dungeon_id")
        private val FOCUS_IS_BOSS = booleanPreferencesKey("focus_is_boss")
        private val FOCUS_PAUSED = booleanPreferencesKey("focus_paused")
        private val FOCUS_PAUSED_REMAINING_SECONDS = intPreferencesKey("focus_paused_remaining_seconds")
        private val BLUEPRINT_SETUP_COMPLETE = booleanPreferencesKey("blueprint_setup_complete")
        private val FINANCIAL_MODULE_ENABLED = booleanPreferencesKey("financial_module_enabled")
        
        private val LAST_ENERGY_PROMPT_TIMESTAMP = longPreferencesKey("last_energy_prompt_timestamp")
        private val BURNOUT_FLAG_ACTIVE = booleanPreferencesKey("burnout_flag_active")
        private val BURNOUT_FLAG_SET_AT = longPreferencesKey("burnout_flag_set_at")
        private val WATER_TARGET = floatPreferencesKey("water_target_ml")
        private val SLEEP_TARGET = floatPreferencesKey("sleep_target_hours")
        private val ENERGY_FLOOR = intPreferencesKey("energy_floor")
        
        private val REVIEW_DAY_OF_WEEK = intPreferencesKey("review_day_of_week")
        private val LAST_REVIEW_COMPLETED_WEEK = stringPreferencesKey("last_review_completed_week")
        private val NEXT_WEEK_OUTCOME_1 = stringPreferencesKey("next_week_outcome_1")
        private val NEXT_WEEK_OUTCOME_2 = stringPreferencesKey("next_week_outcome_2")
        private val NEXT_WEEK_OUTCOME_3 = stringPreferencesKey("next_week_outcome_3")
        private val LAST_WEEK_OUTCOME_1 = stringPreferencesKey("last_week_outcome_1")
        private val LAST_WEEK_OUTCOME_2 = stringPreferencesKey("last_week_outcome_2")
        private val LAST_WEEK_OUTCOME_3 = stringPreferencesKey("last_week_outcome_3")
        
        private val LAST_REVIEW_TIMESTAMP = longPreferencesKey("last_review_timestamp")
        private val VEHICLE_PROGRAM_START_DATE = longPreferencesKey("vehicle_program_start_date")
        
        private val LAST_COMMAND_VOICE_SHOWN_DATE = stringPreferencesKey("last_command_voice_shown_date")
        private val LAST_SHOWN_AFFIRMATION_INDEX = intPreferencesKey("last_shown_affirmation_index")
    }

    open val lastCommandVoiceShownDateFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LAST_COMMAND_VOICE_SHOWN_DATE] ?: ""
    }

    open suspend fun setLastCommandVoiceShownDate(value: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_COMMAND_VOICE_SHOWN_DATE] = value
        }
    }

    open val lastShownAffirmationIndexFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[LAST_SHOWN_AFFIRMATION_INDEX] ?: 0
    }

    open suspend fun setLastShownAffirmationIndex(value: Int) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SHOWN_AFFIRMATION_INDEX] = value
        }
    }

    val blueprintSetupCompleteFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BLUEPRINT_SETUP_COMPLETE] ?: false
    }

    suspend fun setBlueprintSetupComplete(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BLUEPRINT_SETUP_COMPLETE] = value
        }
    }

    open val financialModuleEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[FINANCIAL_MODULE_ENABLED] ?: false
    }

    open suspend fun setFinancialModuleEnabled(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[FINANCIAL_MODULE_ENABLED] = value
        }
    }

    open val lastEnergyPromptTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_ENERGY_PROMPT_TIMESTAMP] ?: 0L
    }

    open suspend fun setLastEnergyPromptTimestamp(value: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_ENERGY_PROMPT_TIMESTAMP] = value
        }
    }

    open val burnoutFlagActiveFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BURNOUT_FLAG_ACTIVE] ?: false
    }

    open suspend fun setBurnoutFlagActive(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BURNOUT_FLAG_ACTIVE] = value
            if (value) {
                prefs[BURNOUT_FLAG_SET_AT] = System.currentTimeMillis()
            }
        }
    }

    open val burnoutFlagSetAtFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[BURNOUT_FLAG_SET_AT] ?: 0L
    }

    open suspend fun setBurnoutFlagSetAt(value: Long) {
        context.dataStore.edit { prefs ->
            prefs[BURNOUT_FLAG_SET_AT] = value
        }
    }

    open val waterTargetFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[WATER_TARGET] ?: 2500f
    }

    open suspend fun setWaterTarget(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[WATER_TARGET] = value
        }
    }

    open val sleepTargetFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[SLEEP_TARGET] ?: 7.5f
    }

    open suspend fun setSleepTarget(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[SLEEP_TARGET] = value
        }
    }

    open val energyFloorFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[ENERGY_FLOOR] ?: 6
    }

    open suspend fun setEnergyFloor(value: Int) {
        context.dataStore.edit { prefs ->
            prefs[ENERGY_FLOOR] = value
        }
    }

    open val reviewDayOfWeekFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[REVIEW_DAY_OF_WEEK] ?: 5
    }

    open suspend fun setReviewDayOfWeek(value: Int) {
        context.dataStore.edit { prefs ->
            prefs[REVIEW_DAY_OF_WEEK] = value
        }
    }

    open val lastReviewCompletedWeekFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LAST_REVIEW_COMPLETED_WEEK] ?: ""
    }

    open suspend fun setLastReviewCompletedWeek(value: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_REVIEW_COMPLETED_WEEK] = value
        }
    }

    open val nextWeekOutcome1Flow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[NEXT_WEEK_OUTCOME_1] ?: ""
    }

    open suspend fun setNextWeekOutcome1(value: String) {
        context.dataStore.edit { prefs ->
            prefs[NEXT_WEEK_OUTCOME_1] = value
        }
    }

    open val nextWeekOutcome2Flow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[NEXT_WEEK_OUTCOME_2] ?: ""
    }

    open suspend fun setNextWeekOutcome2(value: String) {
        context.dataStore.edit { prefs ->
            prefs[NEXT_WEEK_OUTCOME_2] = value
        }
    }

    open val nextWeekOutcome3Flow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[NEXT_WEEK_OUTCOME_3] ?: ""
    }

    open suspend fun setNextWeekOutcome3(value: String) {
        context.dataStore.edit { prefs ->
            prefs[NEXT_WEEK_OUTCOME_3] = value
        }
    }

    open val lastWeekOutcome1Flow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LAST_WEEK_OUTCOME_1] ?: ""
    }

    open suspend fun setLastWeekOutcome1(value: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_WEEK_OUTCOME_1] = value
        }
    }

    open val lastWeekOutcome2Flow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LAST_WEEK_OUTCOME_2] ?: ""
    }

    open suspend fun setLastWeekOutcome2(value: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_WEEK_OUTCOME_2] = value
        }
    }

    open val lastWeekOutcome3Flow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LAST_WEEK_OUTCOME_3] ?: ""
    }

    open suspend fun setLastWeekOutcome3(value: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_WEEK_OUTCOME_3] = value
        }
    }

    open val lastReviewTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_REVIEW_TIMESTAMP] ?: 0L
    }

    open suspend fun setLastReviewTimestamp(value: Long) {
        context.dataStore.edit { prefs ->
            prefs[LAST_REVIEW_TIMESTAMP] = value
        }
    }

    open val vehicleProgramStartDateFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[VEHICLE_PROGRAM_START_DATE] ?: 0L
    }

    open suspend fun setVehicleProgramStartDate(value: Long) {
        context.dataStore.edit { prefs ->
            prefs[VEHICLE_PROGRAM_START_DATE] = value
        }
    }

    val userEmailFlow: Flow<String?> = context.dataStore.data
        .map { it[USER_EMAIL] }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { it[USER_EMAIL] = email }
    }

    val hardModeEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { it[HARD_MODE_ENABLED] ?: false }

    suspend fun setHardModeEnabled(value: Boolean) {
        context.dataStore.edit { it[HARD_MODE_ENABLED] = value }
    }

    val firstMissionDoneFlow: Flow<Boolean> = context.dataStore.data.map { it[FIRST_MISSION_DONE] ?: false }

    suspend fun setFirstMissionDone(value: Boolean) {
        context.dataStore.edit { it[FIRST_MISSION_DONE] = value }
    }

    val profileImageUriFlow: Flow<String?> = context.dataStore.data.map { it[PROFILE_IMAGE_URI] }

    suspend fun setProfileImageUri(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri == null) {
                prefs.remove(PROFILE_IMAGE_URI)
            } else {
                prefs[PROFILE_IMAGE_URI] = uri
            }
        }
    }

    val supabaseAccessTokenFlow: Flow<String?> = context.dataStore.data
        .map { it[SUPABASE_ACCESS_TOKEN] }

    suspend fun setSupabaseAccessToken(token: String) {
        context.dataStore.edit { it[SUPABASE_ACCESS_TOKEN] = token }
    }

    val supabaseUserIdFlow: Flow<String?> = context.dataStore.data
        .map { it[SUPABASE_USER_ID] }

    // Defaults to true: an account is only "not anonymous" once we
    // explicitly mark it as upgraded/logged-in with a real email.
    val isAnonymousUserFlow: Flow<Boolean> = context.dataStore.data
        .map { it[IS_ANONYMOUS_USER] ?: true }

    suspend fun setSupabaseSession(accessToken: String, userId: String, isAnonymous: Boolean) {
        context.dataStore.edit {
            it[SUPABASE_ACCESS_TOKEN] = accessToken
            it[SUPABASE_USER_ID] = userId
            it[IS_ANONYMOUS_USER] = isAnonymous
        }
    }

    val leaguePointsFlow: Flow<Int> = context.dataStore.data.map { it[LEAGUE_POINTS] ?: 0 }

    suspend fun addLeaguePoints(points: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[LEAGUE_POINTS] ?: 0
            prefs[LEAGUE_POINTS] = (current + points).coerceAtLeast(0)
        }
    }

    val leaguePreregisteredFlow: Flow<Boolean> = context.dataStore.data.map { it[LEAGUE_PREREGISTERED] ?: false }

    suspend fun setLeaguePreregistered(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[LEAGUE_PREREGISTERED] = value
        }
    }

    val devBypassFlow: Flow<Boolean> = context.dataStore.data.map { it[DEV_BYPASS] ?: false }

    suspend fun setDevBypass(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DEV_BYPASS] = value
        }
    }

    val activatedFlow: Flow<Boolean> = context.dataStore.data.map { it[ACTIVATED] ?: false }

    // PRIORITY 3: number of days since first launch (0 = day 1). Caps at 999.
    val daysSinceFirstLaunchFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        val first = prefs[FIRST_LAUNCH_TIMESTAMP] ?: 0L
        if (first == 0L) 0
        else ((System.currentTimeMillis() - first) / 86_400_000L).toInt().coerceIn(0, 999)
    }

    suspend fun recordFirstLaunchIfNeeded() {
        val current = context.dataStore.data.map { it[FIRST_LAUNCH_TIMESTAMP] ?: 0L }.first()
        if (current == 0L) {
            context.dataStore.edit { it[FIRST_LAUNCH_TIMESTAMP] = System.currentTimeMillis() }
        }
    }

    // ANONYMOUS UPGRADE NUDGE: whether the one-time proactive dialog
    // (Home screen) asking an anonymous user to link an email has
    // already been shown. Defaults to false (not yet shown). Set to
    // true the moment it's shown — whether the user acts on it or
    // dismisses it — so it never repeats.
    val saveProgressNudgeShownFlow: Flow<Boolean> = context.dataStore.data
        .map { it[SAVE_PROGRESS_NUDGE_SHOWN] ?: false }

    suspend fun setSaveProgressNudgeShown() {
        context.dataStore.edit { it[SAVE_PROGRESS_NUDGE_SHOWN] = true }
    }

    // Skill Tree is unlocked after the user has been in the app for 2+ days (day 3 access) or if dev bypass is active
    val skillTreeUnlockedFlow: Flow<Boolean> = combine(
        daysSinceFirstLaunchFlow,
        devBypassFlow
    ) { days, bypass ->
        bypass || days >= 2
    }

    suspend fun setActivated(value: Boolean, code: String = "") {
        context.dataStore.edit { prefs ->
            prefs[ACTIVATED] = value
            prefs[ACTIVATION_CODE] = code
        }
    }

    val isPremiumFlow: Flow<Boolean> = context.dataStore.data.map { it[IS_PREMIUM] ?: false }

    val premiumPlanFlow: Flow<String?> = context.dataStore.data.map { it[PREMIUM_PLAN] }

    suspend fun setPremium(value: Boolean, plan: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[IS_PREMIUM] = value
            if (plan != null) {
                prefs[PREMIUM_PLAN] = plan
            } else if (!value) {
                prefs.remove(PREMIUM_PLAN)
            }
        }
    }

    val activationCodeFlow: Flow<String?> = context.dataStore.data.map { it[ACTIVATION_CODE] }

    val onboardingSpotlightDoneFlow: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_SPOTLIGHT_DONE] ?: false }

    suspend fun setOnboardingSpotlightDone() {
        context.dataStore.edit { it[ONBOARDING_SPOTLIGHT_DONE] = true }
    }

    val lastShownStreakMilestoneFlow: Flow<Int> = context.dataStore.data.map {
        it[LAST_SHOWN_STREAK_MILESTONE] ?: 0
    }
    suspend fun setLastShownStreakMilestone(milestone: Int) {
        context.dataStore.edit { it[LAST_SHOWN_STREAK_MILESTONE] = milestone }
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        try {
            ThemeMode.valueOf(prefs[THEME_MODE] ?: "SYSTEM")
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode.name
        }
    }

    // ---- WP-206 analytics consent (§5/§15) ----
    // Canonical state is DataStore-authoritative: current state + last-change timestamp +
    // deterministic transitions (§4 ruling: no append-only history needed). Reversible — the
    // setter simply overwrites state and stamps the change time.
    val analyticsConsentStateFlow: Flow<AnalyticsConsentState> = context.dataStore.data.map { prefs ->
        try {
            AnalyticsConsentState.valueOf(prefs[ANALYTICS_CONSENT_STATE] ?: AnalyticsConsentState.UNKNOWN.name)
        } catch (e: Exception) {
            AnalyticsConsentState.UNKNOWN
        }
    }

    val analyticsConsentUpdatedAtFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[ANALYTICS_CONSENT_UPDATED_AT] ?: 0L
    }

    /** Reversible consent write (§15). Persists the new state and the change timestamp atomically. */
    suspend fun setAnalyticsConsent(state: AnalyticsConsentState, nowMillis: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            prefs[ANALYTICS_CONSENT_STATE] = state.name
            prefs[ANALYTICS_CONSENT_UPDATED_AT] = nowMillis
        }
    }

    /** One-shot consent read for the drain worker / atomic-completion snapshot. */
    suspend fun analyticsConsentStateOnce(): AnalyticsConsentState = analyticsConsentStateFlow.first()


    // Language (also saved to SharedPreferences via MainActivity for attachBaseContext)
    val languageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE] ?: "en"
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE] = lang
        }
    }

    val systemVoiceModeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SYSTEM_VOICE_MODE] ?: "COLD"
    }

    suspend fun setSystemVoiceMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[SYSTEM_VOICE_MODE] = mode
        }
    }

    // First-launch setup
    val setupCompleteFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SETUP_COMPLETE] ?: false
    }

    suspend fun setSetupComplete() {
        context.dataStore.edit { prefs ->
            prefs[SETUP_COMPLETE] = true
        }
    }

    val awakeningShownFlow: Flow<Boolean> =
        context.dataStore.data.map { it[AWAKENING_SHOWN] ?: false }

    val briefingHomeFlow: Flow<Boolean> =
        context.dataStore.data.map { it[BRIEFING_HOME] ?: false }

    val briefingMissionsFlow: Flow<Boolean> =
        context.dataStore.data.map { it[BRIEFING_MISSIONS] ?: false }

    val briefingDungeonsFlow: Flow<Boolean> =
        context.dataStore.data.map { it[BRIEFING_DUNGEONS] ?: false }

    val briefingSkillTreeFlow: Flow<Boolean> =
        context.dataStore.data.map { it[BRIEFING_SKILL_TREE] ?: false }

    val briefingShadowFlow: Flow<Boolean> =
        context.dataStore.data.map { it[BRIEFING_SHADOW] ?: false }

    val briefingSystemVoiceFlow: Flow<Boolean> =
        context.dataStore.data.map { it[BRIEFING_SYSTEM_VOICE] ?: false }

    suspend fun setAwakeningShown() {
        context.dataStore.edit { it[AWAKENING_SHOWN] = true }
    }

    suspend fun setBriefingShown(screen: String) {
        context.dataStore.edit { prefs ->
            when (screen) {
                "home"         -> prefs[BRIEFING_HOME] = true
                "missions"     -> prefs[BRIEFING_MISSIONS] = true
                "dungeons"     -> prefs[BRIEFING_DUNGEONS] = true
                "skill_tree"   -> prefs[BRIEFING_SKILL_TREE] = true
                "shadow"       -> prefs[BRIEFING_SHADOW] = true
                "system_voice" -> prefs[BRIEFING_SYSTEM_VOICE] = true
            }
        }
    }

    val skillTreeSeededFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SKILL_TREE_SEEDED] ?: false
    }

    suspend fun setSkillTreeSeeded(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SKILL_TREE_SEEDED] = value
        }
    }

    val alirezaProfileSeededFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ALIREZA_PROFILE_SEEDED] ?: false
    }

    suspend fun setAlirezaProfileSeeded(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALIREZA_PROFILE_SEEDED] = value
        }
    }

    val muscleGroupsSeededFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MUSCLE_GROUPS_SEEDED] ?: false
    }

    suspend fun setMuscleGroupsSeeded(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MUSCLE_GROUPS_SEEDED] = value
        }
    }

    val streakFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[STREAK] ?: 0
    }

    val streakFreezeFlow: Flow<Int> = context.dataStore.data.map { it[STREAK_FREEZE] ?: 0 }

    suspend fun awardStreakFreeze() {
        context.dataStore.edit { prefs ->
            val current = prefs[STREAK_FREEZE] ?: 0
            if (current < 2) prefs[STREAK_FREEZE] = current + 1
        }
    }

    /** Returns true if a freeze was available and consumed. */
    suspend fun consumeStreakFreeze(): Boolean {
        var consumed = false
        context.dataStore.edit { prefs ->
            val current = prefs[STREAK_FREEZE] ?: 0
            if (current > 0) {
                prefs[STREAK_FREEZE] = current - 1
                consumed = true
            }
        }
        return consumed
    }

    /**
     * Atomically checks whether a "System Anomaly" variable-reward bonus may
     * still trigger today (resets the counter on a new calendar day) and, if
     * so, consumes one of the day's limited slots. Returns true only when the
     * caller may proceed to award the anomaly bonus.
     */
    suspend fun tryConsumeSystemAnomalySlot(): Boolean {
        val today = java.time.LocalDate.now().toString()
        var allowed = false
        context.dataStore.edit { prefs ->
            val storedDate = prefs[SYSTEM_ANOMALY_COUNT_DATE]
            val current = if (storedDate == today) (prefs[SYSTEM_ANOMALY_COUNT] ?: 0) else 0
            if (current < MAX_DAILY_SYSTEM_ANOMALIES) {
                prefs[SYSTEM_ANOMALY_COUNT_DATE] = today
                prefs[SYSTEM_ANOMALY_COUNT] = current + 1
                allowed = true
            }
        }
        return allowed
    }

    /** Returns true only the first time this is called on a given calendar day. */
    suspend fun checkAndMarkDailyLogin(): Boolean {
        val today = java.time.LocalDate.now().toString()
        var isFirst = false
        context.dataStore.edit { prefs ->
            if (prefs[LAST_LOGIN_DATE] != today) {
                prefs[LAST_LOGIN_DATE] = today
                isFirst = true
            }
        }
        return isFirst
    }

    fun currentWeekKey(): String =
        java.time.LocalDate.now()
            .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
            .toString()

    suspend fun resetWeeklyIfNeeded() {
        val key = currentWeekKey()
        context.dataStore.edit { prefs ->
            if (prefs[WEEKLY_KEY] != key) {
                prefs[WEEKLY_KEY] = key
                prefs[WEEKLY_MISSIONS] = 0
                prefs[WEEKLY_STREAK_BEST] = 0
                prefs[WEEKLY_RARE_DONE] = 0
                prefs[WEEKLY_ALL_CLAIMED] = false
            }
        }
    }

    suspend fun incrementWeeklyMissions() {
        context.dataStore.edit { it[WEEKLY_MISSIONS] = (it[WEEKLY_MISSIONS] ?: 0) + 1 }
    }

    suspend fun incrementWeeklyRare() {
        context.dataStore.edit { it[WEEKLY_RARE_DONE] = (it[WEEKLY_RARE_DONE] ?: 0) + 1 }
    }

    suspend fun setWeeklyStreakBest(streak: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[WEEKLY_STREAK_BEST] ?: 0
            if (streak > current) prefs[WEEKLY_STREAK_BEST] = streak
        }
    }

    suspend fun claimWeeklyBonus(): Boolean {
        var claimed = false
        context.dataStore.edit { prefs ->
            if (prefs[WEEKLY_ALL_CLAIMED] != true) {
                prefs[WEEKLY_ALL_CLAIMED] = true
                claimed = true
            }
        }
        return claimed
    }

    val weeklyProgressFlow: Flow<com.axiom.app.domain.model.WeeklyProgress> =
        context.dataStore.data.map { prefs ->
            com.axiom.app.domain.model.WeeklyProgress(
                missionsDone = prefs[WEEKLY_MISSIONS] ?: 0,
                streakBest   = prefs[WEEKLY_STREAK_BEST] ?: 0,
                rareDone     = prefs[WEEKLY_RARE_DONE] ?: 0,
                allClaimed   = prefs[WEEKLY_ALL_CLAIMED] ?: false
            )
        }

    val longestStreakFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[LONGEST_STREAK] ?: 0
    }

    val lastCompleteTimestampFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_COMPLETE_TIMESTAMP] ?: 0L
    }

    val statsFlow: Flow<CharacterStats> = context.dataStore.data.map { prefs ->
        CharacterStats(
            execution = prefs[STAT_EXECUTION] ?: 10,
            focus = prefs[STAT_FOCUS] ?: 10,
            knowledge = prefs[STAT_KNOWLEDGE] ?: 10,
            business = prefs[STAT_BUSINESS] ?: 10,
            fitness = prefs[STAT_FITNESS] ?: 10,
            creativity = prefs[STAT_CREATIVITY] ?: 10
        )
    }

    suspend fun increaseStat(statName: String) {
        context.dataStore.edit { prefs ->
            when (statName.lowercase()) {
                "execution" -> {
                    val current = prefs[STAT_EXECUTION] ?: 10
                    prefs[STAT_EXECUTION] = current + 1
                }
                "focus" -> {
                    val current = prefs[STAT_FOCUS] ?: 10
                    prefs[STAT_FOCUS] = current + 1
                }
                "knowledge" -> {
                    val current = prefs[STAT_KNOWLEDGE] ?: 10
                    prefs[STAT_KNOWLEDGE] = current + 1
                }
                "business" -> {
                    val current = prefs[STAT_BUSINESS] ?: 10
                    prefs[STAT_BUSINESS] = current + 1
                }
                "fitness" -> {
                    val current = prefs[STAT_FITNESS] ?: 10
                    prefs[STAT_FITNESS] = current + 1
                }
                "creativity" -> {
                    val current = prefs[STAT_CREATIVITY] ?: 10
                    prefs[STAT_CREATIVITY] = current + 1
                }
            }
        }
    }

    suspend fun restoreStatsAndStreak(
        streakValue: Int,
        longestStreakValue: Int,
        lastComplete: Long,
        executionValue: Int,
        focusValue: Int,
        knowledgeValue: Int,
        businessValue: Int,
        fitnessValue: Int,
        creativityValue: Int
    ) {
        context.dataStore.edit { prefs ->
            prefs[STREAK] = streakValue
            prefs[LONGEST_STREAK] = longestStreakValue
            prefs[LAST_COMPLETE_TIMESTAMP] = lastComplete
            prefs[STAT_EXECUTION] = executionValue
            prefs[STAT_FOCUS] = focusValue
            prefs[STAT_KNOWLEDGE] = knowledgeValue
            prefs[STAT_BUSINESS] = businessValue
            prefs[STAT_FITNESS] = fitnessValue
            prefs[STAT_CREATIVITY] = creativityValue
        }
    }

    suspend fun setStreak(value: Int) {
        context.dataStore.edit { prefs ->
            prefs[STREAK] = value
            val longest = prefs[LONGEST_STREAK] ?: 0
            if (value > longest) {
                prefs[LONGEST_STREAK] = value
            }
        }
    }

    /**
     * WP-205 §8/§10 — post-commit COMPATIBILITY MIRROR of the canonical Room streak.
     *
     * Room [com.axiom.app.data.local.entity.StreakEntity] is the durable authority; this
     * copies the already-committed resulting streak into DataStore for legacy readers. It does
     * NOT recompute the daily rule (that ran inside the atomic transaction) — it only reflects
     * truth. A failure here leaves the Room commit valid (§10); a later retry re-derives the
     * same values from Room and never re-awards. Never writes a lower current streak, so a
     * stale mirror can only be corrected upward, never zeroed.
     */
    suspend fun mirrorCompletionStreak(streak: Int, longest: Int, lastCompleteMillis: Long) {
        context.dataStore.edit { prefs ->
            val existing = prefs[STREAK] ?: 0
            prefs[STREAK] = maxOf(existing, streak)
            val existingLongest = prefs[LONGEST_STREAK] ?: 0
            prefs[LONGEST_STREAK] = maxOf(existingLongest, longest)
            if (lastCompleteMillis > 0L) {
                prefs[LAST_COMPLETE_TIMESTAMP] = lastCompleteMillis
            }
        }
    }

    suspend fun checkOffDailyProtocol(): Boolean {
        var streakUpdated = false
        context.dataStore.edit { prefs ->
            val lastComplete = prefs[LAST_COMPLETE_TIMESTAMP] ?: 0L
            val currentStreak = prefs[STREAK] ?: 0
            val now = System.currentTimeMillis()

            if (isSameDay(lastComplete, now)) {
                // Already completed today
                return@edit
            }

            val newStreak = if (isYesterday(lastComplete, now)) {
                currentStreak + 1
            } else {
                1
            }

            prefs[STREAK] = newStreak
            val longest = prefs[LONGEST_STREAK] ?: 0
            if (newStreak > longest) {
                prefs[LONGEST_STREAK] = newStreak
            }
            prefs[LAST_COMPLETE_TIMESTAMP] = now
            streakUpdated = true
        }
        return streakUpdated
    }

    fun isSameDay(t1: Long, t2: Long): Boolean {
        if (t1 == 0L || t2 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(t1: Long, t2: Long): Boolean {
        if (t1 == 0L || t2 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        cal1.add(Calendar.DAY_OF_YEAR, 1)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    // WP-104 SEC-104-003: AndroidKeyStore-backed AES/GCM secure store for the BYO Gemini key.
    private val geminiKeyStore: com.axiom.app.core.security.GeminiKeyStore =
        com.axiom.app.core.security.AndroidGeminiKeyStore(context)

    // Read-only: prefers the encrypted store; falls back to any not-yet-migrated legacy
    // plaintext so an existing key is never lost before migration runs. No writes here.
    val geminiApiKeyFlow: Flow<String?> = context.dataStore.data
        .map { prefs ->
            val legacy = prefs[GEMINI_API_KEY]?.takeIf { k -> k.isNotBlank() }
            when {
                geminiKeyStore.hasKey() -> geminiKeyStore.retrieve()
                legacy != null -> legacy
                else -> null
            }
        }

    val dailyBriefingFlow: Flow<String?> = context.dataStore.data
        .map { it[DAILY_BRIEFING] }

    suspend fun setGeminiApiKey(key: String) {
        // Never persist plaintext: encrypt via keystore, drop any legacy plaintext.
        geminiKeyStore.store(key.trim())
        context.dataStore.edit {
            it.remove(GEMINI_API_KEY)
            it[GEMINI_KEY_PRESENT] = true
        }
    }

    suspend fun clearGeminiApiKey() {
        geminiKeyStore.clear()
        context.dataStore.edit {
            it.remove(GEMINI_API_KEY)
            it[GEMINI_KEY_PRESENT] = false
        }
    }

    /**
     * WP-104 SEC-104-003 one-time migration of a legacy plaintext Gemini key into the
     * encrypted store. Idempotent and fail-safe: the legacy plaintext is removed ONLY
     * after the encrypted write is verified, so a failure never destroys the only key.
     */
    suspend fun migrateGeminiKeyIfNeeded() {
        val legacy = context.dataStore.data.first()[GEMINI_API_KEY]?.takeIf { it.isNotBlank() }
            ?: return
        if (geminiKeyStore.hasKey()) {
            // Already migrated (or a newer key exists): just drop the stale plaintext.
            context.dataStore.edit { it.remove(GEMINI_API_KEY) }
            return
        }
        runCatching {
            geminiKeyStore.store(legacy)
            check(geminiKeyStore.retrieve() == legacy) { "verify failed" }
        }.onSuccess {
            context.dataStore.edit {
                it.remove(GEMINI_API_KEY)
                it[GEMINI_KEY_PRESENT] = true
            }
        }
        // onFailure: legacy plaintext intentionally preserved (fail-safe, never logged).
    }

    suspend fun saveDailyBriefing(text: String) {
        val today = java.time.LocalDate.now().toString()
        val lang  = languageFlow.first()
        context.dataStore.edit {
            it[DAILY_BRIEFING] = text
            it[BRIEFING_DATE]  = today
            it[BRIEFING_LANG]  = lang
        }
    }

    suspend fun isBriefingFreshToday(): Boolean {
        val today = java.time.LocalDate.now().toString()
        val lang  = languageFlow.first()
        val prefs = context.dataStore.data.first()
        return prefs[BRIEFING_DATE] == today && prefs[BRIEFING_LANG] == lang
    }

    val equippedPassiveSkillIdFlow: Flow<String?> = context.dataStore.data.map { it[EQUIPPED_PASSIVE_SKILL_ID] }

    suspend fun setEquippedPassiveSkillId(skillId: String?) {
        context.dataStore.edit { prefs ->
            if (skillId == null) {
                prefs.remove(EQUIPPED_PASSIVE_SKILL_ID)
            } else {
                prefs[EQUIPPED_PASSIVE_SKILL_ID] = skillId
            }
        }
    }

    private val masteryPrefs by lazy {
        context.getSharedPreferences("axiom_skill_mastery", Context.MODE_PRIVATE)
    }

    fun getMasteryPointsAllocated(skillId: String, type: String): Int {
        return masteryPrefs.getInt("mastery_${skillId}_${type}", 0)
    }

    fun allocateMasteryPoint(skillId: String, type: String, maxPoints: Int): Boolean {
        val overdrive = getMasteryPointsAllocated(skillId, "overdrive")
        val fortitude = getMasteryPointsAllocated(skillId, "fortitude")
        val affinity = getMasteryPointsAllocated(skillId, "affinity")
        val currentTotal = overdrive + fortitude + affinity
        if (currentTotal < maxPoints) {
            val currentVal = masteryPrefs.getInt("mastery_${skillId}_${type}", 0)
            masteryPrefs.edit().putInt("mastery_${skillId}_${type}", currentVal + 1).apply()
            return true
        }
        return false
    }

    fun refundMasteryPoint(skillId: String, type: String): Boolean {
        val currentVal = masteryPrefs.getInt("mastery_${skillId}_${type}", 0)
        if (currentVal > 0) {
            masteryPrefs.edit().putInt("mastery_${skillId}_${type}", currentVal - 1).apply()
            return true
        }
        return false
    }

    fun getSkillPrestige(skillId: String): Int {
        return masteryPrefs.getInt("prestige_${skillId}", 0)
    }

    fun incrementSkillPrestige(skillId: String) {
        val current = getSkillPrestige(skillId)
        masteryPrefs.edit().putInt("prestige_${skillId}", current + 1).apply()
        masteryPrefs.edit()
            .putInt("mastery_${skillId}_overdrive", 0)
            .putInt("mastery_${skillId}_fortitude", 0)
            .putInt("mastery_${skillId}_affinity", 0)
            .apply()
    }

    val activeTimerStateFlow: Flow<ActiveTimerState> = context.dataStore.data.map { prefs ->
        ActiveTimerState(
            isActive = prefs[FOCUS_ACTIVE] ?: false,
            startTimeMillis = prefs[FOCUS_START_TIME] ?: 0L,
            endTimeMillis = prefs[FOCUS_END_TIME] ?: 0L,
            title = prefs[FOCUS_TITLE] ?: "",
            missionId = prefs[FOCUS_MISSION_ID] ?: "",
            dungeonId = prefs[FOCUS_DUNGEON_ID] ?: "",
            isBoss = prefs[FOCUS_IS_BOSS] ?: false,
            isPaused = prefs[FOCUS_PAUSED] ?: false,
            pausedRemainingSeconds = prefs[FOCUS_PAUSED_REMAINING_SECONDS] ?: 0
        )
    }

    suspend fun saveActiveTimerState(state: ActiveTimerState) {
        context.dataStore.edit { prefs ->
            prefs[FOCUS_ACTIVE] = state.isActive
            prefs[FOCUS_START_TIME] = state.startTimeMillis
            prefs[FOCUS_END_TIME] = state.endTimeMillis
            prefs[FOCUS_TITLE] = state.title
            prefs[FOCUS_MISSION_ID] = state.missionId
            prefs[FOCUS_DUNGEON_ID] = state.dungeonId
            prefs[FOCUS_IS_BOSS] = state.isBoss
            prefs[FOCUS_PAUSED] = state.isPaused
            prefs[FOCUS_PAUSED_REMAINING_SECONDS] = state.pausedRemainingSeconds
        }
    }

    suspend fun clearActiveTimerState() {
        context.dataStore.edit { prefs ->
            prefs[FOCUS_ACTIVE] = false
            prefs.remove(FOCUS_START_TIME)
            prefs.remove(FOCUS_END_TIME)
            prefs.remove(FOCUS_TITLE)
            prefs.remove(FOCUS_MISSION_ID)
            prefs.remove(FOCUS_DUNGEON_ID)
            prefs.remove(FOCUS_IS_BOSS)
            prefs.remove(FOCUS_PAUSED)
            prefs.remove(FOCUS_PAUSED_REMAINING_SECONDS)
        }
    }
}

data class ActiveTimerState(
    val isActive: Boolean = false,
    val startTimeMillis: Long = 0L,
    val endTimeMillis: Long = 0L,
    val title: String = "",
    val missionId: String = "",
    val dungeonId: String = "",
    val isBoss: Boolean = false,
    val isPaused: Boolean = false,
    val pausedRemainingSeconds: Int = 0
)
