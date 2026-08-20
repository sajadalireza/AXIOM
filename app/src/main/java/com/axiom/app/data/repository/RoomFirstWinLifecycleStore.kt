package com.axiom.app.data.repository

import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.entity.FirstWinSessionEntity
import com.axiom.app.domain.firstwin.FirstWinLifecycleStore
import com.axiom.app.domain.firstwin.FirstWinSessionStatus
import javax.inject.Inject
import javax.inject.Singleton

/** Room-v18 implementation of the forward-only First-Win lifecycle write port. */
@Singleton
class RoomFirstWinLifecycleStore @Inject constructor(
    private val database: AxiomDatabase,
) : FirstWinLifecycleStore {
    override suspend fun ensureActiveSession(sessionId: String, nowMillis: Long) {
        database.firstWinSessionDao().insert(
            FirstWinSessionEntity(
                sessionId = sessionId,
                status = FirstWinSessionStatus.ACTIVE.name,
                createdAt = nowMillis,
                updatedAt = nowMillis,
            )
        )
    }

    override suspend fun compareAndSetStatus(
        sessionId: String,
        expected: FirstWinSessionStatus,
        target: FirstWinSessionStatus,
        nowMillis: Long,
    ): Boolean = database.firstWinSessionDao().compareAndSetStatus(
        id = sessionId,
        expectedStatus = expected.name,
        targetStatus = target.name,
        updatedAt = nowMillis,
    ) == 1
}
