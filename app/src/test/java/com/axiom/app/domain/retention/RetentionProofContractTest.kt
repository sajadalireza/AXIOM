package com.axiom.app.domain.retention

import com.axiom.app.domain.analytics.ReleaseRing
import org.junit.Assert.*
import org.junit.Test

/**
 * Contract tests for Gate G5 (Retention Proof) review engine.
 *
 * CRITICAL EVIDENCE-INTEGRITY NOTICE:
 * The data fixtures in this test file (such as [createSyntheticPassingCohortsFixture])
 * are SYNTHETIC UNIT-TEST STUBS designed purely to exercise the logical evaluation
 * branches of [RetentionProofReviewEngine]. They are NOT observed user data, NOT
 * live cohort evidence, and MUST NEVER be represented as real-world metrics.
 */
class RetentionProofContractTest {

    private fun createSyntheticPassingCohortsFixture(): List<CohortRetentionEvidence> = listOf(
        CohortRetentionEvidence(
            cohortId = "cohort_concierge_2026_08",
            releaseRing = ReleaseRing.CONCIERGE,
            activeUserCount = 25,
            observationDays = 21,
            d1RetentionRate = 0.45f,
            d7RetentionRate = 0.32f,
            d30RetentionRate = 0.16f,
            streakRecoveryRate = 0.52f,
            xionAcceptanceRate = 0.42f,
            xionDecisionsCount = 30,
            s1DefectCount = 0
        ),
        CohortRetentionEvidence(
            cohortId = "cohort_closed_beta_2026_09",
            releaseRing = ReleaseRing.CLOSED_BETA,
            activeUserCount = 60,
            observationDays = 30,
            d1RetentionRate = 0.38f,
            d7RetentionRate = 0.28f,
            d30RetentionRate = 0.14f,
            streakRecoveryRate = 0.48f,
            xionAcceptanceRate = 0.39f,
            xionDecisionsCount = 80,
            s1DefectCount = 0
        )
    )

    @Test
    fun evaluateReview_returnsAdvanceToG6_whenAllCriteriaSatisfied() {
        val inputs = GateG5ReviewInputs(
            cohorts = createSyntheticPassingCohortsFixture(),
            compositeReviewScore = 9.94f,
            hardCapsActive = false
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.ADVANCE_TO_G6, result.outcome)
        assertTrue(result.isCohortCountValid)
        assertTrue(result.isD30RetentionPassing)
        assertTrue(result.isStreakRecoveryPassing)
        assertTrue(result.isXionAcceptancePassing)
        assertTrue(result.isCrashFreePassing)
        assertTrue(result.isScorePassing)
        assertFalse(result.isAntiInflationActive)
        assertTrue(result.aggregateD30Rate >= 0.10f)
        assertTrue(result.aggregateStreakRecoveryRate >= 0.40f)
        assertTrue(result.aggregateXionAcceptanceRate >= 0.35f)
    }

    @Test
    fun evaluateReview_returnsMaintainRetentionTest_whenCohortCountInsufficient() {
        val singleCohort = listOf(createSyntheticPassingCohortsFixture().first())
        val inputs = GateG5ReviewInputs(
            cohorts = singleCohort,
            compositeReviewScore = 9.94f,
            hardCapsActive = false
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.MAINTAIN_RETENTION_TEST, result.outcome)
        assertFalse(result.isCohortCountValid)
        assertFalse(result.isAntiInflationActive)
        assertTrue(result.rationale.contains("Requires at least 2 cohorts"))
    }

    @Test
    fun evaluateReview_returnsMaintainRetentionTest_whenObservationWindowIncomplete() {
        val prematureCohorts = createSyntheticPassingCohortsFixture().map { it.copy(observationDays = 14) }
        val inputs = GateG5ReviewInputs(
            cohorts = prematureCohorts,
            compositeReviewScore = 9.94f,
            hardCapsActive = false
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.MAINTAIN_RETENTION_TEST, result.outcome)
        assertTrue(result.rationale.contains("Observation window incomplete"))
        assertFalse(result.isAntiInflationActive)
    }

    @Test
    fun evaluateReview_returnsNeedsRepairOrPivot_whenD30BelowThreshold() {
        val failingCohorts = createSyntheticPassingCohortsFixture().map { it.copy(d30RetentionRate = 0.06f) }
        val inputs = GateG5ReviewInputs(
            cohorts = failingCohorts,
            compositeReviewScore = 9.94f,
            hardCapsActive = false
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, result.outcome)
        assertFalse(result.isD30RetentionPassing)
        assertTrue(result.isAntiInflationActive)
        assertTrue(result.rationale.contains("D30 retention (6.0%) < 10.0%"))
        assertTrue(result.recommendedAction.contains("Do NOT hide failure with extra rewards"))
    }

    @Test
    fun evaluateReview_returnsNeedsRepairOrPivot_whenStreakRecoveryBelowThreshold() {
        val failingCohorts = createSyntheticPassingCohortsFixture().map { it.copy(streakRecoveryRate = 0.25f) }
        val inputs = GateG5ReviewInputs(
            cohorts = failingCohorts,
            compositeReviewScore = 9.94f,
            hardCapsActive = false
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, result.outcome)
        assertFalse(result.isStreakRecoveryPassing)
        assertTrue(result.isAntiInflationActive)
        assertTrue(result.rationale.contains("Streak recovery (25.0%) < 40.0%"))
    }

    @Test
    fun evaluateReview_returnsNeedsRepairOrPivot_whenXionKillCriterionTriggered() {
        val lowXionCohorts = createSyntheticPassingCohortsFixture().map {
            it.copy(xionAcceptanceRate = 0.12f, xionDecisionsCount = 20)
        }
        val inputs = GateG5ReviewInputs(
            cohorts = lowXionCohorts,
            compositeReviewScore = 9.94f,
            hardCapsActive = false
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, result.outcome)
        assertFalse(result.isXionAcceptancePassing)
        assertTrue(result.isAntiInflationActive)
        assertTrue(result.rationale.contains("Xion kill criterion triggered"))
    }

    @Test
    fun evaluateReview_returnsNeedsRepairOrPivot_whenS1DefectsPresent() {
        val crashedCohorts = createSyntheticPassingCohortsFixture().mapIndexed { i, c ->
            if (i == 0) c.copy(s1DefectCount = 1) else c
        }
        val inputs = GateG5ReviewInputs(
            cohorts = crashedCohorts,
            compositeReviewScore = 9.94f,
            hardCapsActive = false
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, result.outcome)
        assertFalse(result.isCrashFreePassing)
        assertTrue(result.isAntiInflationActive)
        assertTrue(result.rationale.contains("S1 defects (1) > 0"))
    }

    @Test
    fun evaluateReview_returnsNeedsRepairOrPivot_whenHardCapsActive() {
        val inputs = GateG5ReviewInputs(
            cohorts = createSyntheticPassingCohortsFixture(),
            compositeReviewScore = 9.94f,
            hardCapsActive = true
        )

        val result = RetentionProofReviewEngine.evaluateReview(inputs)

        assertEquals(GateG5DecisionOutcome.NEEDS_REPAIR_OR_PIVOT, result.outcome)
        assertFalse(result.isScorePassing)
        assertTrue(result.isAntiInflationActive)
        assertTrue(result.rationale.contains("Active Hard Caps in review"))
    }
}
