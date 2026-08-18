package com.axiom.app.domain.firstwin

/**
 * WP-207 RED — pure, deterministic First-Win position derivation.
 *
 * Contract (pinned by [FirstWinPositionReducerTest]) — derive the resumable
 * [FirstWinPosition] from typed durable facts, fail-closed, in this precedence:
 *
 *   1. sessionStatus == COMPLETED                        -> HOME
 *        (the ONLY home condition; durable Room proof dominates a stale setup flag)
 *   2. !setupComplete                                    -> SETUP
 *   3. !sessionExists                                    -> AREA
 *        (fresh eligible entry; the session row is created on entering/continuing Area)
 *   4. !primaryMissionExists                             -> AREA
 *   5. !completionReceiptExists                          -> DO
 *        (regardless of an inconsistent advanced sessionStatus)
 *   6. receipt exists && status ACTIVE or null           -> REWARD
 *   7. receipt exists && status REWARD_SEEN              -> NEXT
 *   8. receipt exists && status HANDOFF_WITH_SCHEDULE
 *        + nextScheduleExists                            -> HANDOFF
 *        (schedule row missing -> fall back NEXT)
 *   9. receipt exists && status HANDOFF_WITHOUT_SCHEDULE -> HANDOFF
 *
 * Fail-closed: the authoritative facts (mission / completion_receipt / schedule_blocks)
 * dominate the mirrored [FirstWinSessionStatus]. An advanced status without a committed
 * receipt falls back to DO (mission exists) or AREA (no mission). Pure: no clock, no I/O,
 * no coroutine timing. NOT YET IMPLEMENTED (RED): throws [NotImplementedError] until GREEN.
 */
object FirstWinPositionReducer {
    fun reduce(facts: FirstWinFacts): FirstWinPosition =
        TODO("WP-207 RED")
}
