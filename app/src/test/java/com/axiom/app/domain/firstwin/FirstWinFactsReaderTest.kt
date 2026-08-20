package com.axiom.app.domain.firstwin

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstWinFactsReaderTest {

    private class FakeStore(
        var session: FirstWinSessionRecord? = null,
        var missions: Set<String> = emptySet(),
        var receiptSessions: Set<String> = emptySet(),
        var schedules: Set<String> = emptySet(),
    ) : FirstWinStateStore {
        override suspend fun getSession(sessionId: String): FirstWinSessionRecord? =
            session?.takeIf { it.sessionId == sessionId }
        override suspend fun missionExists(missionId: String): Boolean = missionId in missions
        override suspend fun completionReceiptExists(sessionId: String): Boolean = sessionId in receiptSessions
        override suspend fun scheduleExists(scheduleId: String): Boolean = scheduleId in schedules
    }

    private val sessionId = "fw:hunter-1:session"
    private fun missionId() = FirstWinIds.primaryMissionId(sessionId)
    private fun scheduleId() = FirstWinIds.nextScheduleId(sessionId)

    @Test fun missingSession_neverFabricatesDownstreamFacts() = runTest {
        val store = FakeStore(
            missions = setOf(missionId()),
            receiptSessions = setOf(sessionId),
            schedules = setOf(scheduleId()),
        )
        val facts = FirstWinFactsReader(store).read(setupComplete = true, sessionId = sessionId)

        assertFalse(facts.sessionExists)
        assertNull(facts.sessionStatus)
        assertFalse(facts.primaryMissionExists)
        assertFalse(facts.completionReceiptExists)
        assertFalse(facts.nextScheduleExists)
    }

    @Test fun activeSession_readsMissionOnlyFromDeterministicId() = runTest {
        val store = FakeStore(
            session = FirstWinSessionRecord(sessionId, FirstWinSessionStatus.ACTIVE.name),
            missions = setOf("unrelated", missionId()),
        )
        val facts = FirstWinFactsReader(store).read(setupComplete = true, sessionId = sessionId)

        assertTrue(facts.sessionExists)
        assertEquals(FirstWinSessionStatus.ACTIVE, facts.sessionStatus)
        assertTrue(facts.primaryMissionExists)
        assertFalse(facts.completionReceiptExists)
        assertFalse(facts.nextScheduleExists)
    }

    @Test fun receiptAndSchedule_areCorrelatedToSameSession() = runTest {
        val store = FakeStore(
            session = FirstWinSessionRecord(sessionId, FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE.name),
            missions = setOf(missionId()),
            receiptSessions = setOf(sessionId),
            schedules = setOf(scheduleId()),
        )
        val facts = FirstWinFactsReader(store).read(setupComplete = true, sessionId = sessionId)

        assertTrue(facts.completionReceiptExists)
        assertTrue(facts.nextScheduleExists)
        assertEquals(FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE, facts.sessionStatus)
    }

    @Test fun unknownStoredStatus_failsClosedAsNull() = runTest {
        val store = FakeStore(
            session = FirstWinSessionRecord(sessionId, "FUTURE_OR_CORRUPT_STATUS"),
            missions = setOf(missionId()),
            receiptSessions = setOf(sessionId),
        )
        val facts = FirstWinFactsReader(store).read(setupComplete = true, sessionId = sessionId)

        assertTrue(facts.sessionExists)
        assertNull(facts.sessionStatus)
        assertEquals(FirstWinPosition.REWARD, FirstWinPositionReducer.reduce(facts))
    }

    @Test fun setupFlag_isPassedThroughWithoutBeingInferredFromRoom() = runTest {
        val store = FakeStore(session = FirstWinSessionRecord(sessionId, FirstWinSessionStatus.ACTIVE.name))
        val facts = FirstWinFactsReader(store).read(setupComplete = false, sessionId = sessionId)
        assertFalse(facts.setupComplete)
    }
}
