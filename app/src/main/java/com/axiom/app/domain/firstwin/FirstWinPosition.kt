package com.axiom.app.domain.firstwin

/**
 * WP-207 — First-Win state vocabulary (RED contract scaffold, types only).
 *
 * [FirstWinPosition] is the resumable position derived from typed durable facts by
 * [FirstWinPositionReducer]. SETUP -> AREA -> DO -> REWARD -> NEXT -> HANDOFF mirror
 * the vertical-slice flow; HOME is the terminal post-handoff destination. A position
 * is always DERIVED from typed durable facts — never stored as a string.
 */
enum class FirstWinPosition {
    SETUP,
    AREA,
    DO,
    REWARD,
    NEXT,
    HANDOFF,
    HOME,
}

/**
 * WP-207 — closed lifecycle vocabulary for the `first_win_session.status` column.
 *
 * Exactly five values, no structured/opaque payload (Director constraint 3):
 *  - [ACTIVE]                     in-flow, before the reward is acknowledged.
 *  - [REWARD_SEEN]                completion committed and reward acknowledged.
 *  - [HANDOFF_WITH_SCHEDULE]      next action scheduled; awaiting handoff to Home (activation).
 *  - [HANDOFF_WITHOUT_SCHEDULE]   finished without scheduling; awaiting handoff to Home (first value only).
 *  - [COMPLETED]                  terminal; the journey is complete and routes to Home.
 *
 * This is a mirrored lifecycle label only. Mission / completion_receipt /
 * schedule_blocks remain the authoritative durable facts; an advanced status can
 * never fabricate completion or advance the derived position by itself.
 */
enum class FirstWinSessionStatus {
    ACTIVE,
    REWARD_SEEN,
    HANDOFF_WITH_SCHEDULE,
    HANDOFF_WITHOUT_SCHEDULE,
    COMPLETED,
}
