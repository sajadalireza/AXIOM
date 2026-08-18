package com.axiom.app.domain.firstwin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WP-207 RED — First-Win deterministic id-builder contract.
 *
 * Pins replay-safe / idempotent correlation keys for the First-Win mission and the
 * scheduled next action. FAILS until GREEN because [FirstWinIds] currently throws
 * [NotImplementedError].
 */
class FirstWinIdsTest {

    private val sessionA = "fw-session-0001"
    private val sessionB = "fw-session-0002"

    @Test fun primaryMissionId_deterministic() =
        assertEquals(FirstWinIds.primaryMissionId(sessionA), FirstWinIds.primaryMissionId(sessionA))

    @Test fun nextScheduleId_deterministic() =
        assertEquals(FirstWinIds.nextScheduleId(sessionA), FirstWinIds.nextScheduleId(sessionA))

    @Test fun primaryMissionId_distinctAcrossSessions() =
        assertNotEquals(FirstWinIds.primaryMissionId(sessionA), FirstWinIds.primaryMissionId(sessionB))

    @Test fun nextScheduleId_distinctAcrossSessions() =
        assertNotEquals(FirstWinIds.nextScheduleId(sessionA), FirstWinIds.nextScheduleId(sessionB))

    @Test fun missionIdDiffersFromScheduleId_sameSession() =
        assertNotEquals(FirstWinIds.primaryMissionId(sessionA), FirstWinIds.nextScheduleId(sessionA))

    @Test fun ids_areNonBlank() {
        assertTrue(FirstWinIds.primaryMissionId(sessionA).isNotBlank())
        assertTrue(FirstWinIds.nextScheduleId(sessionA).isNotBlank())
    }

    @Test fun ids_carrySessionId_forReconciliation() {
        // The id must remain reconcilable to its session (no opaque payload).
        assertTrue(FirstWinIds.primaryMissionId(sessionA).contains(sessionA))
        assertTrue(FirstWinIds.nextScheduleId(sessionA).contains(sessionA))
    }
}
