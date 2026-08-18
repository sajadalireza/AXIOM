package com.axiom.app.domain.firstwin

/**
 * WP-207 — typed durable facts that fully determine the resumable First-Win position.
 *
 * All inputs are typed booleans / a closed status enum — no strings, no opaque
 * payloads. These mirror durable state that already exists in Room v18 + DataStore:
 *
 *  - [setupComplete]            DataStore `setup_complete`.
 *  - [sessionExists]            presence of a `first_win_session` row.
 *  - [sessionStatus]            mirrored `first_win_session.status` lifecycle label (null when absent).
 *  - [primaryMissionExists]     presence of the First-Win mission row (`missions`).
 *  - [completionReceiptExists]  presence of the completion receipt (`completion_receipt`, WP-205 atomic proof).
 *  - [nextScheduleExists]       presence of the scheduled next action (`schedule_blocks`).
 *
 * Mission / receipt / schedule are authoritative; [sessionStatus] is corroborating only
 * (it can never fabricate completion by itself).
 */
data class FirstWinFacts(
    val setupComplete: Boolean,
    val sessionExists: Boolean,
    val sessionStatus: FirstWinSessionStatus?,
    val primaryMissionExists: Boolean,
    val completionReceiptExists: Boolean,
    val nextScheduleExists: Boolean,
)
