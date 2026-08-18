package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.ScheduleBlock
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleFirstWinNextActionUseCaseTest {
    private class FakeStore : FirstWinStateStore, FirstWinLifecycleStore, FirstWinMissionStore, FirstWinScheduleStore {
        val sessions = linkedMapOf<String, String>()
        val missions = linkedMapOf<String, Mission>()
        val receipts = mutableSetOf<String>()
        val schedules = linkedMapOf<String, ScheduleBlock>()

        override suspend fun getSession(sessionId: String): FirstWinSessionRecord? =
            sessions[sessionId]?.let { FirstWinSessionRecord(sessionId, it) }
        override suspend fun missionExists(missionId: String) = missionId in missions
        override suspend fun completionReceiptExists(sessionId: String) = sessionId in receipts
        override suspend fun scheduleExists(scheduleId: String) = scheduleId in schedules
        override suspend fun ensureActiveSession(sessionId: String, nowMillis: Long) {
            sessions.putIfAbsent(sessionId, FirstWinSessionStatus.ACTIVE.name)
        }
        override suspend fun compareAndSetStatus(
            sessionId: String, expected: FirstWinSessionStatus, target: FirstWinSessionStatus, nowMillis: Long,
        ): Boolean {
            if (sessions[sessionId] != expected.name) return false
            sessions[sessionId] = target.name
            return true
        }
        override suspend fun getById(missionId: String): Mission? = missions[missionId]
        override suspend fun insertIfAbsent(mission: Mission): Mission = missions.getOrPut(mission.id) { mission }
        override suspend fun insertIfAbsent(block: ScheduleBlock): ScheduleBlock = schedules.getOrPut(block.id) { block }
    }

    private val sessionId = "fw:hunter-1:session"
    private fun mission() = Mission(
        id = FirstWinIds.primaryMissionId(sessionId), title = "Review one page", track = "STUDY",
        rarity = "Normal", skillId = "skill_deep_work", skillName = "Deep Work", xpReward = 10,
        powerScore = 1f, status = "COMPLETED", dungeonId = null, estimatedHours = 2f / 60f,
        actualHours = 2f / 60f, createdAt = 1L, completedAt = 2L, rarityColor = 1L,
        trackId = "capability",
    )

    private fun fixture(): Pair<FakeStore, ScheduleFirstWinNextActionUseCase> {
        val store = FakeStore().apply {
            sessions[sessionId] = FirstWinSessionStatus.REWARD_SEEN.name
            missions[FirstWinIds.primaryMissionId(sessionId)] = mission()
        }
        val factsReader = FirstWinFactsReader(store)
        val lifecycle = FirstWinLifecycleController(factsReader, store)
        return store to ScheduleFirstWinNextActionUseCase(store, store, factsReader, lifecycle)
    }

    @Test fun missingReceipt_rejectsBeforeScheduleWrite() = runTest {
        val (store, useCase) = fixture()
        try {
            useCase(true, sessionId, "09:00", "DAILY", 10L)
            throw AssertionError("expected failure")
        } catch (_: IllegalStateException) { }
        assertTrue(store.schedules.isEmpty())
        assertEquals(FirstWinSessionStatus.REWARD_SEEN.name, store.sessions[sessionId])
    }

    @Test fun scheduleCommit_advancesToHandoffWithSchedule() = runTest {
        val (store, useCase) = fixture()
        store.receipts += sessionId
        val block = useCase(true, sessionId, "09:00", "DAILY", 10L)
        assertEquals(FirstWinIds.nextScheduleId(sessionId), block.id)
        assertEquals("09:00", block.startTime)
        assertEquals("DAILY", block.recurrence)
        assertEquals("Review one page", block.title)
        assertEquals(FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE.name, store.sessions[sessionId])
    }

    @Test fun retry_doesNotReplaceExistingSchedule() = runTest {
        val (store, useCase) = fixture()
        store.receipts += sessionId
        val first = useCase(true, sessionId, "09:00", "DAILY", 10L)
        val second = useCase(true, sessionId, "14:30", "MONDAY", 11L)
        assertEquals(first, second)
        assertEquals("09:00", second.startTime)
        assertEquals(1, store.schedules.size)
    }

    @Test fun crashAfterScheduleInsert_beforeStatusTransition_isRepairableOnRetry() = runTest {
        val (store, useCase) = fixture()
        store.receipts += sessionId
        store.schedules[FirstWinIds.nextScheduleId(sessionId)] = ScheduleBlock(
            id = FirstWinIds.nextScheduleId(sessionId), trackId = "capability", startTime = "08:30",
            title = "Review one page", actionDescription = "Review one page", tag = "FirstWin",
            recurrence = "DAILY", isNonNegotiable = false,
        )
        val result = useCase(true, sessionId, "12:00", "MONDAY", 20L)
        assertEquals("08:30", result.startTime)
        assertEquals(FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE.name, store.sessions[sessionId])
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidTime_isRejected() = runTest {
        val (store, useCase) = fixture()
        store.receipts += sessionId
        useCase(true, sessionId, "25:99", "DAILY", 10L)
    }
}
