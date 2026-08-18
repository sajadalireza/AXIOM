package com.axiom.app.domain.firstwin

/**
 * WP-207 RED — forward-only First-Win session lifecycle orchestration.
 * Durable receipt/schedule facts gate visible progress; status writes use compare-and-set.
 */
class FirstWinLifecycleController(
    private val factsReader: FirstWinFactsReader,
    private val lifecycleStore: FirstWinLifecycleStore,
) {
    suspend fun ensureSession(hunterId: String, nowMillis: Long): String =
        TODO("WP-207 RED")

    suspend fun markRewardSeen(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome = TODO("WP-207 RED")

    suspend fun markHandoffWithSchedule(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome = TODO("WP-207 RED")

    suspend fun markHandoffWithoutSchedule(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome = TODO("WP-207 RED")

    suspend fun markCompleted(
        setupComplete: Boolean,
        sessionId: String,
        nowMillis: Long,
    ): FirstWinLifecycleOutcome = TODO("WP-207 RED")
}
