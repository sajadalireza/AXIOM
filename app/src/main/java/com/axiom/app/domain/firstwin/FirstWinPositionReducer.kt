package com.axiom.app.domain.firstwin

/**
 * WP-207 — pure, deterministic First-Win position derivation (GREEN).
 *
 * Derives the resumable [FirstWinPosition] from typed durable facts, fail-closed,
 * in this precedence:
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
 * no coroutine timing — the same facts always yield the same position.
 */
object FirstWinPositionReducer {
    fun reduce(facts: FirstWinFacts): FirstWinPosition {
        // 1. Terminal — the ONLY home condition. Durable Room proof dominates a stale setup flag.
        if (facts.sessionStatus == FirstWinSessionStatus.COMPLETED) return FirstWinPosition.HOME

        // 2. Setup prerequisite dominates all other non-terminal states.
        if (!facts.setupComplete) return FirstWinPosition.SETUP

        // 3. Fresh eligible entry — the session row is created on entering/continuing Area.
        if (!facts.sessionExists) return FirstWinPosition.AREA

        // 4. Assigned but no mission yet — pre-mission draft loss returns to Area.
        if (!facts.primaryMissionExists) return FirstWinPosition.AREA

        // 5. Mission exists but completion not committed — Do, regardless of advanced status.
        if (!facts.completionReceiptExists) return FirstWinPosition.DO

        // 6-9. Completion committed — position by lifecycle status (fail-closed).
        return when (facts.sessionStatus) {
            null, FirstWinSessionStatus.ACTIVE -> FirstWinPosition.REWARD
            FirstWinSessionStatus.REWARD_SEEN -> FirstWinPosition.NEXT
            FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE ->
                if (facts.nextScheduleExists) FirstWinPosition.HANDOFF else FirstWinPosition.NEXT
            FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE -> FirstWinPosition.HANDOFF
            FirstWinSessionStatus.COMPLETED -> FirstWinPosition.HOME // unreachable (rule 1), exhaustive
        }
    }
}
