package com.axiom.app.domain.firstwin

import javax.inject.Inject

/**
 * WP-207 — forward-only First-Win session lifecycle orchestration.
 * Durable receipt/schedule facts gate visible progress; status writes use compare-and-set.
 */
class FirstWinLifecycleController @Inject constructor(
    private val factsReader: FirstWinFactsReader,
    private val lifecycleStore: FirstWinLifecycleStore,
) {
    suspend fun ensureSession(hunterId: String, nowMillis: Long): String {
        val sessionId = FirstWinIds.sessionId(hunterId)
        lifecycleStore.ensureActiveSession(sessionId, nowMillis)
        return sessionId
    }

    suspend fun markRewardSeen(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome {
        val facts = factsReader.read(setupComplete, sessionId)
        if (!facts.sessionExists || !facts.completionReceiptExists) {
            return FirstWinLifecycleOutcome.PRECONDITION_FAILED
        }
        if (facts.sessionStatus == FirstWinSessionStatus.REWARD_SEEN) {
            return FirstWinLifecycleOutcome.ALREADY_APPLIED
        }
        if (facts.sessionStatus != FirstWinSessionStatus.ACTIVE) {
            return FirstWinLifecycleOutcome.STATE_CONFLICT
        }
        return transition(
            setupComplete, sessionId, FirstWinSessionStatus.ACTIVE,
            FirstWinSessionStatus.REWARD_SEEN, nowMillis,
        )
    }

    suspend fun markHandoffWithSchedule(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome {
        val facts = factsReader.read(setupComplete, sessionId)
        if (!facts.sessionExists || !facts.completionReceiptExists || !facts.nextScheduleExists) {
            return FirstWinLifecycleOutcome.PRECONDITION_FAILED
        }
        if (facts.sessionStatus == FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE) {
            return FirstWinLifecycleOutcome.ALREADY_APPLIED
        }
        if (facts.sessionStatus != FirstWinSessionStatus.REWARD_SEEN) {
            return FirstWinLifecycleOutcome.STATE_CONFLICT
        }
        return transition(
            setupComplete, sessionId, FirstWinSessionStatus.REWARD_SEEN,
            FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE, nowMillis,
        )
    }

    suspend fun markHandoffWithoutSchedule(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome {
        val facts = factsReader.read(setupComplete, sessionId)
        if (!facts.sessionExists || !facts.completionReceiptExists) {
            return FirstWinLifecycleOutcome.PRECONDITION_FAILED
        }
        if (facts.sessionStatus == FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE) {
            return FirstWinLifecycleOutcome.ALREADY_APPLIED
        }
        if (facts.sessionStatus != FirstWinSessionStatus.REWARD_SEEN) {
            return FirstWinLifecycleOutcome.STATE_CONFLICT
        }
        return transition(
            setupComplete, sessionId, FirstWinSessionStatus.REWARD_SEEN,
            FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE, nowMillis,
        )
    }

    suspend fun markCompleted(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome {
        val facts = factsReader.read(setupComplete, sessionId)
        if (!facts.sessionExists) return FirstWinLifecycleOutcome.PRECONDITION_FAILED
        if (facts.sessionStatus == FirstWinSessionStatus.COMPLETED) {
            return FirstWinLifecycleOutcome.ALREADY_APPLIED
        }

        val expected = when (facts.sessionStatus) {
            FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE -> {
                if (!facts.completionReceiptExists || !facts.nextScheduleExists) {
                    return FirstWinLifecycleOutcome.PRECONDITION_FAILED
                }
                FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE
            }
            FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE -> {
                if (!facts.completionReceiptExists) {
                    return FirstWinLifecycleOutcome.PRECONDITION_FAILED
                }
                FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE
            }
            else -> return FirstWinLifecycleOutcome.STATE_CONFLICT
        }

        return transition(
            setupComplete, sessionId, expected, FirstWinSessionStatus.COMPLETED, nowMillis,
        )
    }

    private suspend fun transition(
        setupComplete: Boolean,
        sessionId: String,
        expected: FirstWinSessionStatus,
        target: FirstWinSessionStatus,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome {
        if (lifecycleStore.compareAndSetStatus(sessionId, expected, target, nowMillis)) {
            return FirstWinLifecycleOutcome.APPLIED
        }

        // Race-safe idempotency: if another tap/process already applied the same target,
        // report success-equivalent ALREADY_APPLIED. Any other observed state is a conflict.
        val after = factsReader.read(setupComplete, sessionId)
        return if (after.sessionStatus == target) {
            FirstWinLifecycleOutcome.ALREADY_APPLIED
        } else {
            FirstWinLifecycleOutcome.STATE_CONFLICT
        }
    }
}
