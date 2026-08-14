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
        "FIRST_WIN_COMPLETION"
    )

    /** Per-event allowlist. A property key MUST appear here (case-sensitive) to be carried. */
    val ALLOWLIST: Map<String, Set<String>> = mapOf(
        "mission_completed" to setOf("rarity", "xp_gained", "leveled_up"),
        "onboarding_completed" to emptySet(),
        "ai_call" to setOf("method", "success", "reason"),
        "streak_shield_used" to setOf("streak_length"),
        "streak_broken" to setOf("streak_length"),
        "FIRST_WIN_COMPLETION" to setOf("missionId", "hunterXpAwarded")
    )

    /**
     * Defense-in-depth blacklist (§9). Compared case-insensitively against every property key.
     * Covers free text, journal/reflection, mission title, identity, and secret/token aliases.
     */
    val SENSITIVE_KEYS: Set<String> = setOf(
        "title", "missiontitle", "mission_title",
        "reflection", "reflectiontext", "reflection_text",
        "note", "notes", "journal", "journaltext", "journal_text",
        "prompt", "usertext", "user_text", "freetext", "free_text",
        "description", "message", "body", "content", "text",
        "answer", "question",
        "email", "phone", "name", "huntername", "hunter_name", "username", "user_name",
        "token", "apikey", "api_key", "password", "secret", "authorization", "bearer"
    )

    /** Validate a (name, properties) pair. Fail-closed. */
    fun validate(eventName: String, properties: Map<String, Any?>): PayloadValidation =
        TODO("WP-206 GREEN")
}

/** Result of [AnalyticsPayloadPolicy.validate]. */
sealed interface PayloadValidation {
    /** Every key allowlisted and non-sensitive. [clean] carries stringified values only. */
    data class Accepted(val clean: Map<String, String>) : PayloadValidation

    /** At least one key was unknown-event / non-allowlisted / sensitive. */
    data class Rejected(val reason: String, val offendingKey: String) : PayloadValidation
}
