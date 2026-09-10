package com.axiom.app.domain.firstwin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * WP-207 RED — First-Win position-derivation contract.
 *
 * Pins the exact fail-closed resume position derived from typed durable facts,
 * covering every committed step, every process-death window, and every
 * inconsistent (advanced-status-without-receipt) case. FAILS until GREEN because
 * [FirstWinPositionReducer.reduce] currently throws [NotImplementedError].
 */
class FirstWinPositionReducerTest {

    private fun facts(
        setupComplete: Boolean = true,
        sessionExists: Boolean = true,
        sessionStatus: FirstWinSessionStatus? = FirstWinSessionStatus.ACTIVE,
        primaryMissionExists: Boolean = false,
        completionReceiptExists: Boolean = false,
        nextScheduleExists: Boolean = false,
    ) = FirstWinFacts(
        setupComplete = setupComplete,
        sessionExists = sessionExists,
        sessionStatus = sessionStatus,
        primaryMissionExists = primaryMissionExists,
        completionReceiptExists = completionReceiptExists,
        nextScheduleExists = nextScheduleExists,
    )

    private fun reduce(
        setupComplete: Boolean = true,
        sessionExists: Boolean = true,
        sessionStatus: FirstWinSessionStatus? = FirstWinSessionStatus.ACTIVE,
        primaryMissionExists: Boolean = false,
        completionReceiptExists: Boolean = false,
        nextScheduleExists: Boolean = false,
    ) = FirstWinPositionReducer.reduce(
        facts(setupComplete, sessionExists, sessionStatus, primaryMissionExists, completionReceiptExists, nextScheduleExists)
    )

    // ---- 1. COMPLETED => HOME (the ONLY home condition) ----

    @Test fun completed_isHome() =
        assertEquals(FirstWinPosition.HOME, reduce(sessionStatus = FirstWinSessionStatus.COMPLETED))

    @Test fun completed_dominatesStaleSetupFlag() =
        assertEquals(
            FirstWinPosition.HOME,
            reduce(setupComplete = false, sessionExists = false, sessionStatus = FirstWinSessionStatus.COMPLETED)
        )

    @Test fun completed_isTheOnlyHomeCondition() {
        for (status in listOf<FirstWinSessionStatus?>(
            null,
            FirstWinSessionStatus.ACTIVE,
            FirstWinSessionStatus.REWARD_SEEN,
            FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
            FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE,
        )) {
            val pos = reduce(
                sessionStatus = status,
                primaryMissionExists = true,
                completionReceiptExists = true,
                nextScheduleExists = true,
            )
            assertNotEquals("status=$status must never yield HOME", FirstWinPosition.HOME, pos)
        }
    }

    // ---- 2. !setupComplete => SETUP (all non-COMPLETED cases) ----

    @Test fun setupIncomplete_isSetup() =
        assertEquals(FirstWinPosition.SETUP, reduce(setupComplete = false, sessionExists = false, sessionStatus = null))

    @Test fun setupGapDominatesDownstream() =
        assertEquals(
            FirstWinPosition.SETUP,
            reduce(setupComplete = false, primaryMissionExists = true, completionReceiptExists = true)
        )

    // ---- 3. setupComplete && !sessionExists => AREA (fresh eligible entry) ----

    @Test fun setupComplete_noSession_isArea() =
        assertEquals(FirstWinPosition.AREA, reduce(sessionExists = false, sessionStatus = null))

    // ---- 4. session exists && !primaryMissionExists => AREA ----

    @Test fun sessionExists_noMission_isArea() =
        assertEquals(FirstWinPosition.AREA, reduce(sessionExists = true, primaryMissionExists = false))

    // ---- 5. mission exists && !completionReceiptExists => DO (regardless of status) ----

    @Test fun missionExists_noReceipt_isDo() =
        assertEquals(FirstWinPosition.DO, reduce(primaryMissionExists = true, completionReceiptExists = false))

    @Test fun advancedRewardSeen_noReceipt_isDo_failClosed() =
        assertEquals(
            FirstWinPosition.DO,
            reduce(primaryMissionExists = true, sessionStatus = FirstWinSessionStatus.REWARD_SEEN)
        )

    @Test fun advancedHandoffWithSchedule_noReceipt_isDo_failClosed() =
        assertEquals(
            FirstWinPosition.DO,
            reduce(
                primaryMissionExists = true,
                sessionStatus = FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
                nextScheduleExists = true,
            )
        )

    @Test fun advancedHandoffWithoutSchedule_noReceipt_isDo_failClosed() =
        assertEquals(
            FirstWinPosition.DO,
            reduce(primaryMissionExists = true, sessionStatus = FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE)
        )

    @Test fun advancedStatus_noMission_isArea_failClosed() =
        assertEquals(
            FirstWinPosition.AREA,
            reduce(primaryMissionExists = false, sessionStatus = FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE)
        )

    // ---- 6. receipt exists && status ACTIVE or null => REWARD ----

    @Test fun receiptExists_activeStatus_isReward() =
        assertEquals(
            FirstWinPosition.REWARD,
            reduce(primaryMissionExists = true, completionReceiptExists = true, sessionStatus = FirstWinSessionStatus.ACTIVE)
        )

    @Test fun receiptExists_nullStatus_isReward() =
        assertEquals(
            FirstWinPosition.REWARD,
            reduce(primaryMissionExists = true, completionReceiptExists = true, sessionStatus = null)
        )

    // ---- 7. receipt exists && status REWARD_SEEN => NEXT ----

    @Test fun receiptExists_rewardSeen_isNext() =
        assertEquals(
            FirstWinPosition.NEXT,
            reduce(primaryMissionExists = true, completionReceiptExists = true, sessionStatus = FirstWinSessionStatus.REWARD_SEEN)
        )

    // ---- 8. receipt exists && HANDOFF_WITH_SCHEDULE (+ schedule row) => HANDOFF ----

    @Test fun receiptExists_handoffWithSchedule_plusSchedule_isHandoff() =
        assertEquals(
            FirstWinPosition.HANDOFF,
            reduce(
                primaryMissionExists = true,
                completionReceiptExists = true,
                sessionStatus = FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
                nextScheduleExists = true,
            )
        )

    @Test fun receiptExists_handoffWithSchedule_missingSchedule_isNext_failClosed() =
        assertEquals(
            FirstWinPosition.NEXT,
            reduce(
                primaryMissionExists = true,
                completionReceiptExists = true,
                sessionStatus = FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
                nextScheduleExists = false,
            )
        )

    // ---- 9. receipt exists && HANDOFF_WITHOUT_SCHEDULE => HANDOFF ----

    @Test fun receiptExists_handoffWithoutSchedule_isHandoff() =
        assertEquals(
            FirstWinPosition.HANDOFF,
            reduce(
                primaryMissionExists = true,
                completionReceiptExists = true,
                sessionStatus = FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE,
            )
        )

    // ---- Determinism ----

    @Test fun sameFacts_samePosition() {
        val f = facts(
            primaryMissionExists = true,
            completionReceiptExists = true,
            sessionStatus = FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
            nextScheduleExists = true,
        )
        repeat(5) { assertEquals(FirstWinPosition.HANDOFF, FirstWinPositionReducer.reduce(f)) }
    }
}
