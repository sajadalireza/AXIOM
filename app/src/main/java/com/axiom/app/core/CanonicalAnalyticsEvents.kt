package com.axiom.app.core

/**
 * Canonical Analytics Event Dictionary and Alias Bridge.
 *
 * Per AXIOM Canonical Vocabulary (CANONICAL_VOCABULARY.md):
 * - Uses lower_snake_case names.
 * - Starts with canonical domain nouns: goal_*, mission_*, project_*, skill_*.
 * - Keeps Goal progress, Mission completion, Project progress, and Skill progression distinct.
 * - Prohibits private user free-text, reflection content, or mission titles in properties.
 */
object CanonicalAnalyticsEvents {

    // Goal Events
    const val GOAL_CREATED = "goal_created"
    const val GOAL_UPDATED = "goal_updated"
    const val GOAL_PROGRESS_UPDATED = "goal_progress_updated"
    const val GOAL_COMPLETED = "goal_completed"

    // Mission Events
    const val MISSION_CREATED = "mission_created"
    const val MISSION_SCHEDULED = "mission_scheduled"
    const val MISSION_STARTED = "mission_started"
    const val MISSION_COMPLETED = "mission_completed"
    const val MISSION_DEFERRED = "mission_deferred"
    const val MISSION_EVIDENCE_RECORDED = "mission_evidence_recorded"
    const val MISSION_REFLECTION_RECORDED = "mission_reflection_recorded"

    // Project Events (Canonical replacement for legacy Dungeon events)
    const val PROJECT_CREATED = "project_created"
    const val PROJECT_STARTED = "project_started"
    const val PROJECT_PROGRESS_UPDATED = "project_progress_updated"
    const val PROJECT_COMPLETED = "project_completed"
    const val PROJECT_ARCHIVED = "project_archived"

    // Skill Events
    const val SKILL_CREATED = "skill_created"
    const val SKILL_PRACTICE_RECORDED = "skill_practice_recorded"
    const val SKILL_PROGRESS_UPDATED = "skill_progress_updated"
    const val SKILL_UNLOCKED = "skill_unlocked"
    const val SKILL_MASTERY_TIER_CHANGED = "skill_mastery_tier_changed"

    // Purpose Events
    const val PURPOSE_DEFINED = "purpose_defined"
    const val PURPOSE_UPDATED = "purpose_updated"

    // Funnel & First-Win Lifecycle Events (Strict separation of assignment vs. exposure)
    const val FIRST_WIN_ASSIGNED = "first_win_assigned"
    const val FIRST_WIN_EXPOSED = "first_win_exposed"
    const val FIRST_WIN_COMPLETED = "first_win_completed"
    const val ONBOARDING_STARTED = "onboarding_started"
    const val ONBOARDING_COMPLETED = "onboarding_completed"
    const val WEEKLY_REVIEW_EXPOSED = "weekly_review_exposed"
    const val WEEKLY_REVIEW_COMPLETED = "weekly_review_completed"

    // Experimentation Events (Separation of assignment vs. exposure)
    const val EXPERIMENT_ASSIGNED = "experiment_assigned"
    const val EXPERIMENT_EXPOSED = "experiment_exposed"

    // WMPU (Weekly Meaningful Progress Unit) Event
    const val WMPU_ACHIEVED = "wmpu_achieved"

    // Operational Integrity & Error Boundary Events
    const val OPERATIONAL_ERROR = "operational_error"
    const val INTEGRITY_HEARTBEAT = "integrity_heartbeat"

    /**
     * Map of legacy event names to their canonical equivalents.
     */
    val LEGACY_EVENT_ALIASES: Map<String, String> = mapOf(
        "dungeon_created" to PROJECT_CREATED,
        "dungeon_started" to PROJECT_STARTED,
        "dungeon_completed" to PROJECT_COMPLETED,
        "task_created" to MISSION_CREATED,
        "task_completed" to MISSION_COMPLETED,
        "instant_gate_started" to MISSION_STARTED,
        "instant_gate_completed" to MISSION_COMPLETED,
        "FIRST_WIN_COMPLETION" to FIRST_WIN_COMPLETED
    )

    /**
     * Resolves an event name to its canonical counterpart if an alias exists.
     */
    fun resolveCanonicalName(eventName: String): String {
        return LEGACY_EVENT_ALIASES[eventName] ?: eventName
    }
}
