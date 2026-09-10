package com.axiom.app.domain.firstwin

/** Raw persisted First-Win session row projected into the domain read seam. */
data class FirstWinSessionRecord(
    val sessionId: String,
    val rawStatus: String,
)

/**
 * WP-207 narrow read port over existing Room v18 First-Win durable facts.
 *
 * This port intentionally exposes no writes. Mission / receipt / schedule existence
 * are queried by their stable correlation ids; no local UI state can satisfy them.
 */
interface FirstWinStateStore {
    suspend fun getSession(sessionId: String): FirstWinSessionRecord?
    suspend fun missionExists(missionId: String): Boolean
    suspend fun completionReceiptExists(sessionId: String): Boolean
    suspend fun scheduleExists(scheduleId: String): Boolean
}
