package com.axiom.app.domain.firstwin

/**
 * WP-207 — deterministic id builders for the First-Win durable anchors (GREEN).
 *
 * Contract:
 *  - The same [sessionId] always yields the same id (replay-safe / idempotent).
 *  - Different sessions yield different ids.
 *  - The primary-mission id differs from the next-schedule id for the same session.
 *  - Each id is non-blank and carries the [sessionId] so rows are reconcilable to
 *    their First-Win session (no opaque payload).
 *  - A blank [sessionId] is rejected ([IllegalArgumentException]) — never a silent id.
 *
 * These ids are the typed idempotency/correlation keys used against the existing
 * `missions` / `schedule_blocks` tables (both already INSERT with REPLACE), so no
 * schema change is required.
 */
object FirstWinIds {
    private const val PREFIX = "fw"

    fun primaryMissionId(sessionId: String): String {
        require(sessionId.isNotBlank()) { "sessionId must not be blank" }
        return "$PREFIX:$sessionId:mission"
    }

    fun nextScheduleId(sessionId: String): String {
        require(sessionId.isNotBlank()) { "sessionId must not be blank" }
        return "$PREFIX:$sessionId:next"
    }
}
