package com.axiom.app.domain.firstwin

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstWinLifecycleControllerTest {
    private class FakeStore : FirstWinStateStore, FirstWinLifecycleStore {
        val sessions = linkedMapOf<String, String>()
        val missions = mutableSetOf<String>()
        val receipts = mutableSetOf<String>()
        val schedules = mutableSetOf<String>()
        var casCalls = 0

        override suspend fun getSession(sessionId: String): FirstWinSessionRecord? =
            sessions[sessionId]?.let { FirstWinSessionRecord(sessionId, it) }
        override suspend fun missionExists(missionId: String) = missionId in missions
        override suspend fun completionReceiptExists(sessionId: String) = sessionId in receipts
        override suspend fun scheduleExists(scheduleId: String) = scheduleId in schedules

        override suspend fun ensureActiveSession(sessionId: String, nowMillis: Long) {
            sessions.putIfAbsent(sessionId, FirstWinSessionStatus.ACTIVE.name)
        }

        override suspend fun compareAndSetStatus(
            sessionId: String,
            expected: FirstWinSessionStatus,
            target: FirstWinSessionStatus,
            nowMillis: Long,
        ): Boolean {
            casCalls++
            if (sessions[sessionId] != expected.name) return false
            sessions[sessionId] = target.name
            return true
        }
    }

    private fun fixture(): Pair<FakeStore, FirstWinLifecycleController> {
        val store = FakeStore()
        return store to FirstWinLifecycleController(FirstWinFactsReader(store), store)
    }

    @Test fun ensureSession_isDeterministicAndIdempotent() = runTest {
        val (store, controller) = fixture()
        val first = controller.ensureSession("hunter-1", 100L)
        val second = controller.ensureSession("hunter-1", 200L)
        assertEquals(first, second)
        assertEquals(FirstWinIds.sessionId("hunter-1"), first)
        assertEquals(1, store.sessions.size)
        assertEquals(FirstWinSessionStatus.ACTIVE.name, store.sessions[first])
    }

    @Test fun rewardSeen_requiresDurableCompletionReceipt() = runTest {
        val (store, controller) = fixture()
        val id = controller.ensureSession("hunter-1", 1L)
        store.missions += FirstWinIds.primaryMissionId(id)
        val outcome = controller.markRewardSeen(true, id, 2L)
        assertEquals(FirstWinLifecycleOutcome.PRECONDITION_FAILED, outcome)
        assertEquals(FirstWinSessionStatus.ACTIVE.name, store.sessions[id])
        assertEquals(0, store.casCalls)
    }

    @Test fun rewardSeen_appliesOnce_afterReceipt() = runTest {
        val (store, controller) = fixture()
        val id = controller.ensureSession("hunter-1", 1L)
        store.missions += FirstWinIds.primaryMissionId(id)
        store.receipts += id
        assertEquals(FirstWinLifecycleOutcome.APPLIED, controller.markRewardSeen(true, id, 2L))
        assertEquals(FirstWinLifecycleOutcome.ALREADY_APPLIED, controller.markRewardSeen(true, id, 3L))
        assertEquals(FirstWinSessionStatus.REWARD_SEEN.name, store.sessions[id])
        assertEquals(1, store.casCalls)
    }

    @Test fun scheduledHandoff_requiresReceiptAndSchedule() = runTest {
        val (store, controller) = fixture()
        val id = controller.ensureSession("hunter-1", 1L)
        store.missions += FirstWinIds.primaryMissionId(id)
        store.receipts += id
        controller.markRewardSeen(true, id, 2L)

        assertEquals(
            FirstWinLifecycleOutcome.PRECONDITION_FAILED,
            controller.markHandoffWithSchedule(true, id, 3L),
        )
        store.schedules += FirstWinIds.nextScheduleId(id)
        assertEquals(
            FirstWinLifecycleOutcome.APPLIED,
            controller.markHandoffWithSchedule(true, id, 4L),
        )
        assertEquals(FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE.name, store.sessions[id])
    }

    @Test fun finishForNow_requiresReceipt_butNotSchedule() = runTest {
        val (store, controller) = fixture()
        val id = controller.ensureSession("hunter-1", 1L)
        store.missions += FirstWinIds.primaryMissionId(id)
        store.receipts += id
        controller.markRewardSeen(true, id, 2L)
        assertEquals(
            FirstWinLifecycleOutcome.APPLIED,
            controller.markHandoffWithoutSchedule(true, id, 3L),
        )
        assertFalse(store.schedules.contains(FirstWinIds.nextScheduleId(id)))
        assertEquals(FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE.name, store.sessions[id])
    }

    @Test fun completed_onlyAdvancesFromHandoff_andIsIdempotent() = runTest {
        val (store, controller) = fixture()
        val id = controller.ensureSession("hunter-1", 1L)
        store.missions += FirstWinIds.primaryMissionId(id)
        store.receipts += id

        assertEquals(FirstWinLifecycleOutcome.STATE_CONFLICT, controller.markCompleted(true, id, 2L))
        controller.markRewardSeen(true, id, 3L)
        controller.markHandoffWithoutSchedule(true, id, 4L)
        assertEquals(FirstWinLifecycleOutcome.APPLIED, controller.markCompleted(true, id, 5L))
        assertEquals(FirstWinLifecycleOutcome.ALREADY_APPLIED, controller.markCompleted(true, id, 6L))
        assertEquals(FirstWinSessionStatus.COMPLETED.name, store.sessions[id])
    }

    @Test fun advancedOrUnknownState_neverMovesBackward() = runTest {
        val (store, controller) = fixture()
        val id = FirstWinIds.sessionId("hunter-1")
        store.sessions[id] = FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE.name
        store.missions += FirstWinIds.primaryMissionId(id)
        store.receipts += id

        assertEquals(FirstWinLifecycleOutcome.STATE_CONFLICT, controller.markRewardSeen(true, id, 7L))
        assertEquals(FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE.name, store.sessions[id])

        store.sessions[id] = "UNKNOWN_FUTURE"
        assertEquals(FirstWinLifecycleOutcome.STATE_CONFLICT, controller.markCompleted(true, id, 8L))
        assertTrue(store.sessions[id] == "UNKNOWN_FUTURE")
    }
}
