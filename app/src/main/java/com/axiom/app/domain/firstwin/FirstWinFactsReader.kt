package com.axiom.app.domain.firstwin

import javax.inject.Inject

/**
 * WP-207 — reconstructs resumable First-Win facts from durable storage only.
 *
 * Unknown persisted status values are intentionally mapped to null so the existing
 * [FirstWinPositionReducer] falls back to the earliest safe position instead of
 * crashing or trusting an unrecognised lifecycle label. A missing session is an
 * authority boundary: orphaned downstream rows are ignored rather than fabricated
 * into a First-Win journey.
 */
class FirstWinFactsReader @Inject constructor(
    private val store: FirstWinStateStore,
) {
    suspend fun read(setupComplete: Boolean, sessionId: String): FirstWinFacts {
        require(sessionId.isNotBlank()) { "sessionId must not be blank" }

        val session = store.getSession(sessionId)
            ?: return FirstWinFacts(
                setupComplete = setupComplete,
                sessionExists = false,
                sessionStatus = null,
                primaryMissionExists = false,
                completionReceiptExists = false,
                nextScheduleExists = false,
            )

        val status = runCatching { FirstWinSessionStatus.valueOf(session.rawStatus) }.getOrNull()
        return FirstWinFacts(
            setupComplete = setupComplete,
            sessionExists = true,
            sessionStatus = status,
            primaryMissionExists = store.missionExists(FirstWinIds.primaryMissionId(sessionId)),
            completionReceiptExists = store.completionReceiptExists(sessionId),
            nextScheduleExists = store.scheduleExists(FirstWinIds.nextScheduleId(sessionId)),
        )
    }
}
