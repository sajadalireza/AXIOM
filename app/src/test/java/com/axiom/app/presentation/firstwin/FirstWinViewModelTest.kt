package com.axiom.app.presentation.firstwin

import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinPosition
import com.axiom.app.domain.model.Mission
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FirstWinViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val sessionId = "fw:hunter-1:session"
    private fun mission() = Mission(
        id = "mission-1", title = "Read one page", track = "STUDY", rarity = "Normal",
        skillId = "skill_deep_work", skillName = "Deep Work", xpReward = 10, powerScore = 1f,
        status = "ACTIVE", dungeonId = null, estimatedHours = 2f / 60f, actualHours = null,
        createdAt = 1L, completedAt = null, rarityColor = 1L,
    )

    private class FakeRuntime(
        var openResult: FirstWinJourneySnapshot,
        var createResult: FirstWinJourneySnapshot = openResult,
        var completeResult: FirstWinJourneySnapshot = openResult,
        var rewardResult: FirstWinJourneySnapshot = openResult,
        var finishResult: FirstWinJourneySnapshot = openResult,
        var handoffResult: FirstWinJourneySnapshot = openResult,
    ) : FirstWinJourneyRuntime {
        var openCalls = 0
        var createCalls = 0
        var completeCalls = 0
        var rewardCalls = 0
        var finishCalls = 0
        var handoffCalls = 0
        var createFailure: Throwable? = null
        var completeFailure: Throwable? = null
        var rewardFailure: Throwable? = null
        var finishFailure: Throwable? = null
        var handoffFailure: Throwable? = null

        override suspend fun open(): FirstWinJourneySnapshot {
            openCalls++
            return openResult
        }

        override suspend fun createMission(
            sessionId: String,
            area: FirstWinArea,
            actionTitle: String,
        ): FirstWinJourneySnapshot {
            createCalls++
            createFailure?.let { throw it }
            return createResult
        }

        override suspend fun completeMission(
            sessionId: String,
            missionId: String,
        ): FirstWinJourneySnapshot {
            completeCalls++
            completeFailure?.let { throw it }
            return completeResult
        }

        override suspend fun acknowledgeReward(sessionId: String): FirstWinJourneySnapshot {
            rewardCalls++
            rewardFailure?.let { throw it }
            return rewardResult
        }

        override suspend fun finishForNow(sessionId: String): FirstWinJourneySnapshot {
            finishCalls++
            finishFailure?.let { throw it }
            return finishResult
        }

        override suspend fun completeHandoff(sessionId: String): FirstWinJourneySnapshot {
            handoffCalls++
            handoffFailure?.let { throw it }
            return handoffResult
        }
    }

    @Test fun start_loadsDurablePositionOnce() = runTest {
        val runtime = FakeRuntime(FirstWinJourneySnapshot(sessionId, FirstWinPosition.AREA, null))
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        assertEquals(1, runtime.openCalls)
        assertEquals(FirstWinPosition.AREA, vm.state.value.position)
        assertEquals(sessionId, vm.state.value.sessionId)
        assertFalse(vm.state.value.isLoading)
    }

    @Test fun draftEvents_reusePureDraftReducer() = runTest {
        val runtime = FakeRuntime(FirstWinJourneySnapshot(sessionId, FirstWinPosition.AREA, null))
        val vm = FirstWinViewModel(runtime)
        vm.selectArea(FirstWinArea.STUDY)
        vm.continueFromArea()
        vm.setActionTitle("Read one page")
        assertEquals(FirstWinDraftStep.ACTION, vm.state.value.draft.step)
        assertEquals(FirstWinArea.STUDY, vm.state.value.draft.selectedArea)
        assertTrue(vm.state.value.draft.canCreateMission)
        vm.backToArea()
        assertEquals(FirstWinDraftStep.AREA, vm.state.value.draft.step)
    }

    @Test fun createMission_usesRuntimeOnce_andRefreshesToDo() = runTest {
        val created = mission()
        val runtime = FakeRuntime(
            FirstWinJourneySnapshot(sessionId, FirstWinPosition.AREA, null),
            FirstWinJourneySnapshot(sessionId, FirstWinPosition.DO, created),
        )
        val vm = FirstWinViewModel(runtime)
        vm.selectArea(FirstWinArea.STUDY); vm.continueFromArea(); vm.setActionTitle("Read one page")
        vm.createMission(); vm.createMission(); advanceUntilIdle()
        assertEquals(1, runtime.createCalls)
        assertEquals(FirstWinPosition.DO, vm.state.value.position)
        assertEquals(created, vm.state.value.mission)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun createFailure_isGeneric_andPreservesDraft() = runTest {
        val runtime = FakeRuntime(FirstWinJourneySnapshot(sessionId, FirstWinPosition.AREA, null)).apply {
            createFailure = IllegalStateException("sensitive internal detail")
        }
        val vm = FirstWinViewModel(runtime)
        vm.selectArea(FirstWinArea.WORK); vm.continueFromArea(); vm.setActionTitle("Send one email")
        vm.createMission(); advanceUntilIdle()
        assertEquals(FirstWinUiError.CREATE_MISSION, vm.state.value.error)
        assertEquals("Send one email", vm.state.value.draft.actionTitle)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun resumeDo_keepsDurableMission() = runTest {
        val existing = mission()
        val runtime = FakeRuntime(FirstWinJourneySnapshot(sessionId, FirstWinPosition.DO, existing))
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        assertEquals(FirstWinPosition.DO, vm.state.value.position)
        assertEquals(existing, vm.state.value.mission)
    }

    @Test fun completeMission_usesRuntimeOnce_andRefreshesToReward() = runTest {
        val active = mission()
        val completed = active.copy(status = "COMPLETED", completedAt = 2L)
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.DO, active),
            completeResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.REWARD, completed),
        )
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.completeMission(); vm.completeMission(); advanceUntilIdle()
        assertEquals(1, runtime.completeCalls)
        assertEquals(FirstWinPosition.REWARD, vm.state.value.position)
        assertEquals(completed, vm.state.value.mission)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun completeFailure_isGeneric_andPreservesDurableDoState() = runTest {
        val active = mission()
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.DO, active),
        ).apply { completeFailure = IllegalStateException("sensitive internal detail") }
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.completeMission(); advanceUntilIdle()
        assertEquals(FirstWinUiError.COMPLETE_MISSION, vm.state.value.error)
        assertEquals(FirstWinPosition.DO, vm.state.value.position)
        assertEquals(active, vm.state.value.mission)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun continueFromReward_acknowledgesOnce_andRefreshesToNext() = runTest {
        val completed = mission().copy(status = "COMPLETED", completedAt = 2L)
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.REWARD, completed),
            rewardResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.NEXT, completed),
        )
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.continueFromReward(); vm.continueFromReward(); advanceUntilIdle()
        assertEquals(1, runtime.rewardCalls)
        assertEquals(FirstWinPosition.NEXT, vm.state.value.position)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun rewardFailure_isGeneric_andPreservesReward() = runTest {
        val completed = mission().copy(status = "COMPLETED", completedAt = 2L)
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.REWARD, completed),
        ).apply { rewardFailure = IllegalStateException("sensitive internal detail") }
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.continueFromReward(); advanceUntilIdle()
        assertEquals(FirstWinUiError.ACK_REWARD, vm.state.value.error)
        assertEquals(FirstWinPosition.REWARD, vm.state.value.position)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun finishForNow_usesRuntimeOnce_andRefreshesToHandoff() = runTest {
        val completed = mission().copy(status = "COMPLETED", completedAt = 2L)
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.NEXT, completed),
            finishResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.HANDOFF, completed),
        )
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.finishForNow(); vm.finishForNow(); advanceUntilIdle()
        assertEquals(1, runtime.finishCalls)
        assertEquals(FirstWinPosition.HANDOFF, vm.state.value.position)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun finishFailure_isGeneric_andPreservesNext() = runTest {
        val completed = mission().copy(status = "COMPLETED", completedAt = 2L)
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.NEXT, completed),
        ).apply { finishFailure = IllegalStateException("sensitive internal detail") }
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.finishForNow(); advanceUntilIdle()
        assertEquals(FirstWinUiError.FINISH_FOR_NOW, vm.state.value.error)
        assertEquals(FirstWinPosition.NEXT, vm.state.value.position)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun completeHandoff_usesRuntimeOnce_andRefreshesToHome() = runTest {
        val completed = mission().copy(status = "COMPLETED", completedAt = 2L)
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.HANDOFF, completed),
            handoffResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.HOME, completed),
        )
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.completeHandoff(); vm.completeHandoff(); advanceUntilIdle()
        assertEquals(1, runtime.handoffCalls)
        assertEquals(FirstWinPosition.HOME, vm.state.value.position)
        assertFalse(vm.state.value.isBusy)
    }

    @Test fun handoffFailure_isGeneric_andPreservesHandoff() = runTest {
        val completed = mission().copy(status = "COMPLETED", completedAt = 2L)
        val runtime = FakeRuntime(
            openResult = FirstWinJourneySnapshot(sessionId, FirstWinPosition.HANDOFF, completed),
        ).apply { handoffFailure = IllegalStateException("sensitive internal detail") }
        val vm = FirstWinViewModel(runtime)
        vm.start(); advanceUntilIdle()
        vm.completeHandoff(); advanceUntilIdle()
        assertEquals(FirstWinUiError.COMPLETE_HANDOFF, vm.state.value.error)
        assertEquals(FirstWinPosition.HANDOFF, vm.state.value.position)
        assertFalse(vm.state.value.isBusy)
    }
}
