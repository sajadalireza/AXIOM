package com.axiom.app.domain.analytics

/**
 * WP-206 — analytics event catalog + payload allowlist/blacklist (§7/§8/§9).
 *
 * PRIMARY guarantee is the per-event ALLOWLIST: only explicitly classified, typed fields may be
 * carried by an analytics event. The SENSITIVE_KEYS blacklist is defense-in-depth (§9) — a second
 * independent gate that rejects known free-text / identity / secret aliases even if a future event
 * were mis-allowlisted. Any key that is not allowlisted for its event, or that matches the
 * blacklist, is REJECTED (fail-closed). Unknown event names are rejected.
 */
object AnalyticsPayloadPolicy {

    /** Analytics/telemetry event types governed by WP-206 consent (used for the purge predicate, §26). */
    val ANALYTICS_EVENT_TYPES: Set<String> = setOf(
        "mission_completed",
        "onboarding_completed",
        "ai_call",
        "streak_shield_used",
        "streak_broken",
        "FIRST_WIN_COMPLETION",
        "first_win_assigned",
        "first_win_exposed",
        "first_win_completed",
        "onboarding_started",
        "mission_created",
        "mission_started",
        "wmpu_achieved",
        "weekly_review_exposed",
        "weekly_review_completed",
        "experiment_assigned",
        "experiment_exposed",
        "operational_error",
        "integrity_heartbeat",
        "streak_paused",
        "streak_resumed",
        "streak_recovery_offered",
        "streak_recovery_completed",
        "streak_recovery_expired",
        "streak_opt_out_changed"
    )

    /** Per-event allowlist. A property key MUST appear here (case-sensitive) to be carried. */
    val ALLOWLIST: Map<String, Set<String>> = mapOf(
        "mission_completed" to setOf("rarity", "xp_gained", "leveled_up", "has_goal", "contributes_to_wmpu", "mission_id"),
        "onboarding_completed" to setOf("cohort_ring", "duration_seconds"),
        "ai_call" to setOf("method", "success", "reason"),
        "streak_shield_used" to setOf("streak_length"),
        "streak_broken" to setOf("streak_length"),
        "FIRST_WIN_COMPLETION" to setOf("missionId", "hunterXpAwarded"),
        "first_win_assigned" to setOf("treatment_id", "template_id", "cohort_ring"),
        "first_win_exposed" to setOf("treatment_id", "template_id", "screen_name", "cohort_ring"),
        "first_win_completed" to setOf("treatment_id", "template_id", "duration_seconds", "cohort_ring"),
        "onboarding_started" to setOf("cohort_ring"),
        "mission_created" to setOf("mission_id", "has_goal", "has_kpi", "is_custom"),
        "mission_started" to setOf("mission_id", "has_goal"),
        "wmpu_achieved" to setOf("cycle_week", "meaningful_mission_count", "goal_count", "cohort_ring"),
        "weekly_review_exposed" to setOf("cycle_week", "cohort_ring"),
        "weekly_review_completed" to setOf("cycle_week", "actions_taken", "cohort_ring", "wmpu_achieved", "usefulness_rating", "effective_hours_bracket"),
        "experiment_assigned" to setOf("experiment_id", "variant_id", "cohort_ring"),
        "experiment_exposed" to setOf("experiment_id", "variant_id", "screen_name", "cohort_ring"),
        "operational_error" to setOf("component", "error_category", "error_code", "status"),
        "integrity_heartbeat" to setOf("schema_version", "pending_events_count", "cohort_ring", "status"),
        "streak_paused" to setOf("days_paused", "resume_date", "cohort_ring"),
        "streak_resumed" to setOf("reason", "cohort_ring"),
        "streak_recovery_offered" to setOf("streak_length", "cadence", "cohort_ring"),
        "streak_recovery_completed" to setOf("streak_length", "recovery_mission_id", "cohort_ring"),
        "streak_recovery_expired" to setOf("streak_length", "cohort_ring"),
        "streak_opt_out_changed" to setOf("opt_out_state", "cohort_ring")
    )

    /**
     * Defense-in-depth blacklist (§9). Compared case-insensitively against every property key.
     * Covers free text, journal/reflection, mission title, goal title, identity, health/finance, and secret/token aliases.
     */
    val SENSITIVE_KEYS: Set<String> = setOf(
        "title", "missiontitle", "mission_title", "goaltitle", "goal_title",
        "reflection", "reflectiontext", "reflection_text",
        "note", "notes", "journal", "journaltext", "journal_text",
        "prompt", "usertext", "user_text", "freetext", "free_text",
        "description", "message", "body", "content", "text",
        "answer", "question",
        "health", "healthdata", "health_data", "medical",
        "finance", "financial", "salary", "bank", "income",
        "email", "phone", "name", "huntername", "hunter_name", "username", "user_name",
        "token", "apikey", "api_key", "password", "secret", "authorization", "bearer"
    )

    /** Validate a (name, properties) pair. Fail-closed. */
    fun validate(eventName: String, properties: Map<String, Any?>): PayloadValidation {
        val allowed = ALLOWLIST[eventName]
            ?: return PayloadValidation.Rejected("unknown_event", eventName)
        for ((key, _) in properties) {
            // Defense-in-depth blacklist first (§9) — case-insensitive.
            if (key.lowercase() in SENSITIVE_KEYS) {
                return PayloadValidation.Rejected("sensitive_key", key)
            }
            // Primary allowlist gate (§8) — case-sensitive, fail-closed.
            if (key !in allowed) {
                return PayloadValidation.Rejected("not_allowlisted", key)
            }
        }
        return PayloadValidation.Accepted(properties.mapValues { it.value?.toString() ?: "" })
    }
}

/** Result of [AnalyticsPayloadPolicy.validate]. */
sealed interface PayloadValidation {
    /** Every key allowlisted and non-sensitive. [clean] carries stringified values only. */
    data class Accepted(val clean: Map<String, String>) : PayloadValidation

    /** At least one key was unknown-event / non-allowlisted / sensitive. */
    data class Rejected(val reason: String, val offendingKey: String) : PayloadValidation
}
