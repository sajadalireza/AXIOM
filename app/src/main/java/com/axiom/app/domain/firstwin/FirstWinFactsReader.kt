package com.axiom.app.domain.firstwin

/**
 * WP-207 RED — reconstructs resumable First-Win facts from durable storage only.
 *
 * Unknown persisted status values are intentionally mapped to null so the existing
 * [FirstWinPositionReducer] falls back to the earliest safe position instead of
 * crashing or trusting an unrecognised lifecycle label.
 */
class FirstWinFactsReader(
    private val store: FirstWinStateStore,
) {
    suspend fun read(setupComplete: Boolean, sessionId: String): FirstWinFacts =
        TODO("WP-207 RED")
}
