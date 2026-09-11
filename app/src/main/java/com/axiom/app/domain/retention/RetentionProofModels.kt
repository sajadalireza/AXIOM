package com.axiom.app.domain.retention

import com.axiom.app.domain.analytics.ReleaseRing

/**
 * Domain models governing Phase D (Gate G5 Retention Proof) evaluation.
 *
 * Mandated thresholds per Roadmap Section 11 / G5 Gate Exit:
 * - Two cohorts with valid retention evidence (e.g. Concierge, Closed Beta)
 * - D30 Retention >= 10%
 * - Streak Recovery Rate >= 40%
 * - Xion Suggestion Acceptance Rate >= 35% (kill criterion if < 20% after >= 10 decisions)
 * - Zero S1 defects
 */

enum class GateG5DecisionOutcome {
    ADVANCE_TO_G6,
    MAINTAIN_RETENTION_TEST,
    NEEDS_REPAIR_OR_PIVOT
}

data class CohortRetentionEvidence(
    val cohortId: String,
    val releaseRing: ReleaseRing,
    val activeUserCount: Int,
    val observationDays: Int,
    val d1RetentionRate: Float,
    val d7RetentionRate: Float,
    val d30RetentionRate: Float,
    val streakRecoveryRate: Float,
    val xionAcceptanceRate: Float?,
    val xionDecisionsCount: Int,
    val s1DefectCount: Int
)

data class GateG5ReviewInputs(
    val cohorts: List<CohortRetentionEvidence>,
    val compositeReviewScore: Float,
    val hardCapsActive: Boolean
)

data class GateG5ReviewResult(
    val outcome: GateG5DecisionOutcome,
    val isCohortCountValid: Boolean,
    val isD30RetentionPassing: Boolean,
    val isStreakRecoveryPassing: Boolean,
    val isXionAcceptancePassing: Boolean,
    val isCrashFreePassing: Boolean,
    val isScorePassing: Boolean,
    val isAntiInflationActive: Boolean,
    val aggregateD30Rate: Float,
    val aggregateStreakRecoveryRate: Float,
    val aggregateXionAcceptanceRate: Float,
    val rationale: String,
    val recommendedAction: String
)
