package com.axiom.app.domain.firstwin

/** Schema-neutral write port for the existing Room v18 first_win_session row. */
interface FirstWinLifecycleStore {
    suspend fun ensureActiveSession(sessionId: String, nowMillis: Long)
    suspend fun compareAndSetStatus(
        sessionId: String,
        expected: FirstWinSessionStatus,
        target: FirstWinSessionStatus,
        nowMillis: Long,
    ): Boolean
}

enum class FirstWinLifecycleOutcome {
    APPLIED,
    ALREADY_APPLIED,
    PRECONDITION_FAILED,
    STATE_CONFLICT,
}
