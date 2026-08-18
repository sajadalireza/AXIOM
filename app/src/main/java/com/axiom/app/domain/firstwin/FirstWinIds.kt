package com.axiom.app.domain.firstwin

/**
 * WP-207 RED — deterministic id builders for the First-Win durable anchors.
 *
 * Contract (pinned by [FirstWinIdsTest]):
 *  - The same [sessionId] always yields the same id (replay-safe / idempotent).
 *  - Different sessions yield different ids.
 *  - The primary-mission id differs from the next-schedule id for the same session.
 *  - Each id is non-blank and carries the [sessionId] so rows are reconcilable to
 *    their First-Win session (no opaque payload).
 *
 * These ids are the typed idempotency/correlation keys used against the existing
 * `missions` / `schedule_blocks` tables (both already INSERT with REPLACE), so no
 * schema change is required. NOT YET IMPLEMENTED (RED): throws [NotImplementedError]
 * until GREEN.
 */
object FirstWinIds {
    fun primaryMissionId(sessionId: String): String =
        TODO("WP-207 RED: FirstWinIds.primaryMissionId not implemented")

    fun nextScheduleId(sessionId: String): String =
        TODO("WP-207 RED: FirstWinIds.nextScheduleId not implemented")
}
