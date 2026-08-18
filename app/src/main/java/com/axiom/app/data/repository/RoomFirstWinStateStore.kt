package com.axiom.app.data.repository

import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.domain.firstwin.FirstWinSessionRecord
import com.axiom.app.domain.firstwin.FirstWinStateStore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WP-207 Room-v18 adapter for the narrow First-Win durable read port.
 * No transaction or mutation is owned here; each query reads an existing authoritative row.
 */
@Singleton
class RoomFirstWinStateStore @Inject constructor(
    private val database: AxiomDatabase,
) : FirstWinStateStore {
    override suspend fun getSession(sessionId: String): FirstWinSessionRecord? =
        database.firstWinSessionDao().getById(sessionId)?.let {
            FirstWinSessionRecord(sessionId = it.sessionId, rawStatus = it.status)
        }

    override suspend fun missionExists(missionId: String): Boolean =
        database.missionDao().getMissionById(missionId) != null

    override suspend fun completionReceiptExists(sessionId: String): Boolean =
        database.completionReceiptDao().getBySessionId(sessionId) != null

    override suspend fun scheduleExists(scheduleId: String): Boolean =
        database.warriorBlueprintDao().getScheduleBlockById(scheduleId) != null
}
