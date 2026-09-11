package com.axiom.app.domain.analytics

import org.junit.Assert.*
import org.junit.Test

/**
 * Gate G4 Contract Tests — E3.2 Closed Alpha/Beta Protocol & Release Rings.
 *
 * Verifies:
 * 1. Release ring definitions, participant bounds, and minimum durations.
 * 2. Ring advancement gating and blocker identification.
 * 3. Weekly decision review evaluation against Gate G4 exit criteria.
 * 4. Anti-inflation invariant: failure strictly forbids reward inflation.
 */
class BetaRingProtocolContractTest {

    // ---------------------------------------------------------------------------------------------
    // 1. Release Ring Definitions and Bounds
    // ---------------------------------------------------------------------------------------------

    @Test
    fun releaseRings_haveCanonicalBoundsAndDurations() {
        val internal = ReleaseRingOrchestrator.RINGS[ReleaseRing.INTERNAL]!!
        assertEquals(3, internal.minUsers)
        assertEquals(5, internal.maxUsers)
        assertEquals(3, internal.minDurationDays)

        val alpha = ReleaseRingOrchestrator.RINGS[ReleaseRing.ALPHA]!!
        assertEquals(8, alpha.minUsers)
        assertEquals(12, alpha.maxUsers)
        assertEquals(7, alpha.minDurationDays)

        val concierge = ReleaseRingOrchestrator.RINGS[ReleaseRing.CONCIERGE]!!
        assertEquals(20, concierge.minUsers)
        assertEquals(30, concierge.maxUsers)
        assertEquals(21, concierge.minDurationDays)

        val closedBeta = ReleaseRingOrchestrator.RINGS[ReleaseRing.CLOSED_BETA]!!
        assertEquals(50, closedBeta.minUsers)
        assertEquals(100, closedBeta.maxUsers)
        assertEquals(30, closedBeta.minDurationDays)
    }

    // ---------------------------------------------------------------------------------------------
    // 2. Release Ring Advancement Gating
    // ---------------------------------------------------------------------------------------------

    @Test
    fun internalToAlpha_satisfyingCriteria_canAdvance() {
        val eval = ReleaseRingOrchestrator.evaluateRingAdvancement(
            currentRing = ReleaseRing.INTERNAL,
            activeUsers = 4,
            durationDays = 4,
            s1Defects = 0
        )
        assertTrue("Internal ring with 4 users and 0 S1 should advance", eval.canAdvance)
        assertEquals(ReleaseRing.ALPHA, eval.nextRing)
        assertTrue(eval.blockers.isEmpty())
    }

    @Test
    fun alphaToConcierge_requiresMinimumFmcAndTtfv() {
        // Failing FMC (<80%)
        val evalLowFmc = ReleaseRingOrchestrator.evaluateRingAdvancement(
            currentRing = ReleaseRing.ALPHA,
            activeUsers = 10,
            durationDays = 7,
            s1Defects = 0,
            fmcRate = 0.75f,
            ttfvMinutes = 2.0f
        )
        assertFalse("FMC < 80% must block Alpha advancement", evalLowFmc.canAdvance)
        assertTrue(evalLowFmc.blockers.any { it.contains("FMC rate") })

        // Failing TTFV (>=3.0 min)
        val evalHighTtfv = ReleaseRingOrchestrator.evaluateRingAdvancement(
            currentRing = ReleaseRing.ALPHA,
            activeUsers = 10,
            durationDays = 7,
            s1Defects = 0,
            fmcRate = 0.85f,
            ttfvMinutes = 3.5f
        )
        assertFalse("TTFV >= 3.0 min must block Alpha advancement", evalHighTtfv.canAdvance)
        assertTrue(evalHighTtfv.blockers.any { it.contains("TTFV") })

        // Passing all criteria
        val evalPassing = ReleaseRingOrchestrator.evaluateRingAdvancement(
            currentRing = ReleaseRing.ALPHA,
            activeUsers = 10,
            durationDays = 8,
            s1Defects = 0,
            fmcRate = 0.85f,
            ttfvMinutes = 2.1f
        )
        assertTrue("Alpha satisfying all criteria can advance to Concierge", evalPassing.canAdvance)
        assertEquals(ReleaseRing.CONCIERGE, evalPassing.nextRing)
    }

    @Test
    fun anyS1Defect_blocksAdvancementAcrossAllRings() {
        for (ring in listOf(ReleaseRing.INTERNAL, ReleaseRing.ALPHA, ReleaseRing.CONCIERGE, ReleaseRing.CLOSED_BETA)) {
            val eval = ReleaseRingOrchestrator.evaluateRingAdvancement(
                currentRing = ring,
                activeUsers = 100,
                durationDays = 60,
                s1Defects = 1
            )
            assertFalse("S1 defect must block $ring advancement", eval.canAdvance)
            assertTrue(eval.blockers.any { it.contains("zero tolerance") })
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 3. Weekly Decision Review Engine & Anti-Inflation Invariant
    // ---------------------------------------------------------------------------------------------

    @Test
    fun weeklyDecisionReview_incompleteObservation_maintainsCurrentRing() {
        val incomplete = WeeklyDecisionReviewEngine.evaluateReview(
            WeeklyDecisionReviewEngine.DecisionReviewInputs(
                d1RetentionRate = 0.40f,
                d7RetentionRate = 0.25f,
                wmpuRate = 0.30f,
                s1DefectCount = 0,
                activeUsers = 15, // < 20 users
                durationDays = 14 // < 21 days
            )
        )
        assertEquals(WeeklyDecisionReviewEngine.DecisionOutcome.MAINTAIN_CURRENT_RING, incomplete.outcome)
        assertFalse(incomplete.isObservationComplete)
        assertFalse(incomplete.isRewardInflationForbidden)
    }

    @Test
    fun weeklyDecisionReview_passingAllG4Criteria_authorizesAdvancementToG5() {
        val passing = WeeklyDecisionReviewEngine.evaluateReview(
            WeeklyDecisionReviewEngine.DecisionReviewInputs(
                d1RetentionRate = 0.35f, // >= 30%
                d7RetentionRate = 0.22f, // >= 20%
                wmpuRate = 0.28f, // >= 25%
                s1DefectCount = 0,
                activeUsers = 50,
                durationDays = 25
            )
        )
        assertEquals(WeeklyDecisionReviewEngine.DecisionOutcome.ADVANCE_TO_G5, passing.outcome)
        assertTrue(passing.isD1Passing)
        assertTrue(passing.isD7Passing)
        assertTrue(passing.isWmpuPassing)
        assertTrue(passing.isCrashFreePassing)
        assertFalse(passing.isRewardInflationForbidden)
    }

    @Test
    fun weeklyDecisionReview_failingRetentionOrWmpu_forbidsRewardInflation() {
        // Case 1: D1 fails
        val failingD1 = WeeklyDecisionReviewEngine.evaluateReview(
            WeeklyDecisionReviewEngine.DecisionReviewInputs(
                d1RetentionRate = 0.24f, // < 30% FAIL
                d7RetentionRate = 0.22f,
                wmpuRate = 0.30f,
                s1DefectCount = 0,
                activeUsers = 50,
                durationDays = 30
            )
        )
        assertEquals(WeeklyDecisionReviewEngine.DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, failingD1.outcome)
        assertTrue("Failing review must strictly forbid reward inflation", failingD1.isRewardInflationForbidden)

        // Case 2: WMPU fails
        val failingWmpu = WeeklyDecisionReviewEngine.evaluateReview(
            WeeklyDecisionReviewEngine.DecisionReviewInputs(
                d1RetentionRate = 0.35f,
                d7RetentionRate = 0.22f,
                wmpuRate = 0.18f, // < 25% FAIL
                s1DefectCount = 0,
                activeUsers = 50,
                durationDays = 30
            )
        )
        assertEquals(WeeklyDecisionReviewEngine.DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, failingWmpu.outcome)
        assertTrue("Failing WMPU must forbid reward inflation", failingWmpu.isRewardInflationForbidden)
    }

    @Test
    fun weeklyDecisionReview_s1Defect_triggersRepairOrPivot() {
        val s1Crash = WeeklyDecisionReviewEngine.evaluateReview(
            WeeklyDecisionReviewEngine.DecisionReviewInputs(
                d1RetentionRate = 0.40f,
                d7RetentionRate = 0.30f,
                wmpuRate = 0.35f,
                s1DefectCount = 1, // S1 FAIL
                activeUsers = 50,
                durationDays = 30
            )
        )
        assertEquals(WeeklyDecisionReviewEngine.DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, s1Crash.outcome)
        assertFalse(s1Crash.isCrashFreePassing)
    }
}
