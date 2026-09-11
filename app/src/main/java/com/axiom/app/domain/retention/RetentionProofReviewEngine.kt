package com.axiom.app.domain.retention

import java.util.Locale

/**
 * Pure domain review engine for Gate G5 (Retention Proof).
 *
 * Implements canonical roadmap Section 11 & G5 Gate Exit:
 * - Two cohorts with valid retention evidence
 * - D30 >= 10% before monetization
 * - Streak Recovery >= 40%
 * - Xion Acceptance >= 35% if Xion is active (kill criterion if < 20% after >= 10 decisions)
 * - Zero S1 defects
 * - Gate score >= 9.5, no Hard Cap
 *
 * Enforces Anti-Inflation Invariant:
 * "If thresholds fail, do not hide the failure with new rewards/content.
 * Create the smallest repair/pivot decision."
 */
object RetentionProofReviewEngine {

    const val MIN_COHORTS = 2
    const val MIN_OBSERVATION_DAYS = 21
    const val MIN_D30_RETENTION = 0.10f
    const val MIN_STREAK_RECOVERY = 0.40f
    const val MIN_XION_ACCEPTANCE = 0.35f
    const val KILL_CRITERION_XION_ACCEPTANCE = 0.20f
    const val MIN_XION_DECISIONS_FOR_KILL = 10
    const val MAX_S1_DEFECTS = 0
    const val MIN_COMPOSITE_SCORE = 9.50f

    fun evaluateReview(inputs: GateG5ReviewInputs): GateG5ReviewResult {
        val cohortCountValid = inputs.cohorts.size >= MIN_COHORTS
        val observationComplete = inputs.cohorts.all { it.observationDays >= MIN_OBSERVATION_DAYS }

        val totalUsers = inputs.cohorts.sumOf { it.activeUserCount }.coerceAtLeast(1)

        // Weighted D30 Retention
        val weightedD30 = inputs.cohorts.map { it.d30RetentionRate * it.activeUserCount }.sum() / totalUsers
        val isD30Passing = weightedD30 >= MIN_D30_RETENTION

        // Weighted Streak Recovery
        val weightedStreakRecovery = inputs.cohorts.map { it.streakRecoveryRate * it.activeUserCount }.sum() / totalUsers
        val isStreakRecoveryPassing = weightedStreakRecovery >= MIN_STREAK_RECOVERY

        // Aggregate Xion Acceptance
        val xionCohorts = inputs.cohorts.filter { it.xionAcceptanceRate != null }
        val totalXionDecisions = xionCohorts.sumOf { it.xionDecisionsCount }
        val aggregateXionRate = if (totalXionDecisions > 0) {
            xionCohorts.sumOf { (it.xionAcceptanceRate ?: 0f) * it.xionDecisionsCount.toDouble() }.toFloat() / totalXionDecisions
        } else {
            0f
        }

        val isXionKillCriterionTriggered = totalXionDecisions >= MIN_XION_DECISIONS_FOR_KILL &&
                aggregateXionRate < KILL_CRITERION_XION_ACCEPTANCE

        val isXionAcceptancePassing = if (xionCohorts.isEmpty()) {
            true // Xion not active in tested cohorts
        } else {
            aggregateXionRate >= MIN_XION_ACCEPTANCE && !isXionKillCriterionTriggered
        }

        // Crash stability
        val totalS1Defects = inputs.cohorts.sumOf { it.s1DefectCount }
        val isCrashFreePassing = totalS1Defects <= MAX_S1_DEFECTS

        // Gate Score and Hard Caps
        val isScorePassing = inputs.compositeReviewScore >= MIN_COMPOSITE_SCORE && !inputs.hardCapsActive

        // Check if observation is prematurely judged
        if (!cohortCountValid || !observationComplete) {
            val pendingReason = if (!cohortCountValid) {
                "Requires at least $MIN_COHORTS cohorts with retention evidence (found ${inputs.cohorts.size})."
            } else {
                "Observation window incomplete (requires >= $MIN_OBSERVATION_DAYS days per cohort)."
            }
            return GateG5ReviewResult(
                outcome = GateG5DecisionOutcome.MAINTAIN_RETENTION_TEST,
                isCohortCountValid = cohortCountValid,
                isD30RetentionPassing = isD30Passing,
                isStreakRecoveryPassing = isStreakRecoveryPassing,
                isXionAcceptancePassing = isXionAcceptancePassing,
                isCrashFreePassing = isCrashFreePassing,
                isScorePassing = isScorePassing,
                isAntiInflationActive = false,
                aggregateD30Rate = weightedD30,
                aggregateStreakRecoveryRate = weightedStreakRecovery,
                aggregateXionAcceptanceRate = aggregateXionRate,
                rationale = pendingReason,
                recommendedAction = "Continue closed retention cohorts without premature monetization or expansion."
            )
        }

        val allPassing = isD30Passing &&
                isStreakRecoveryPassing &&
                isXionAcceptancePassing &&
                isCrashFreePassing &&
                isScorePassing

        if (allPassing) {
            return GateG5ReviewResult(
                outcome = GateG5DecisionOutcome.ADVANCE_TO_G6,
                isCohortCountValid = true,
                isD30RetentionPassing = true,
                isStreakRecoveryPassing = true,
                isXionAcceptancePassing = true,
                isCrashFreePassing = true,
                isScorePassing = true,
                isAntiInflationActive = false,
                aggregateD30Rate = weightedD30,
                aggregateStreakRecoveryRate = weightedStreakRecovery,
                aggregateXionAcceptanceRate = aggregateXionRate,
                rationale = String.format(
                    Locale.US,
                    "All Gate G5 retention criteria satisfied: %d cohorts, D30=%.1f%% (>=10%%), StreakRecovery=%.1f%% (>=40%%), XionAcceptance=%.1f%% (>=35%%), 0 S1 defects, Score=%.2f/10.0.",
                    inputs.cohorts.size,
                    weightedD30 * 100,
                    weightedStreakRecovery * 100,
                    aggregateXionRate * 100,
                    inputs.compositeReviewScore
                ),
                recommendedAction = "Authorize exit from Phase D (Gate G5 Retention Proof) and unlock Phase E (Gate G6 Monetization Proof)."
            )
        }

        // Failure handling enforcing anti-inflation
        val failures = mutableListOf<String>()
        if (!isD30Passing) failures.add(String.format(Locale.US, "D30 retention (%.1f%%) < 10.0%%", weightedD30 * 100))
        if (!isStreakRecoveryPassing) failures.add(String.format(Locale.US, "Streak recovery (%.1f%%) < 40.0%%", weightedStreakRecovery * 100))
        if (!isXionAcceptancePassing) {
            if (isXionKillCriterionTriggered) {
                failures.add(String.format(Locale.US, "Xion kill criterion triggered: acceptance (%.1f%%) < 20.0%% after %d decisions", aggregateXionRate * 100, totalXionDecisions))
            } else {
                failures.add(String.format(Locale.US, "Xion acceptance (%.1f%%) < 35.0%%", aggregateXionRate * 100))
            }
        }
        if (!isCrashFreePassing) failures.add("S1 defects ($totalS1Defects) > 0")
        if (!isScorePassing) {
            if (inputs.hardCapsActive) failures.add("Active Hard Caps in review")
            else failures.add(String.format(Locale.US, "Composite score (%.2f) < 9.50", inputs.compositeReviewScore))
        }

        return GateG5ReviewResult(
            outcome = GateG5DecisionOutcome.NEEDS_REPAIR_OR_PIVOT,
            isCohortCountValid = true,
            isD30RetentionPassing = isD30Passing,
            isStreakRecoveryPassing = isStreakRecoveryPassing,
            isXionAcceptancePassing = isXionAcceptancePassing,
            isCrashFreePassing = isCrashFreePassing,
            isScorePassing = isScorePassing,
            isAntiInflationActive = true,
            aggregateD30Rate = weightedD30,
            aggregateStreakRecoveryRate = weightedStreakRecovery,
            aggregateXionAcceptanceRate = aggregateXionRate,
            rationale = "Gate G5 retention thresholds failed: ${failures.joinToString("; ")}. Reward inflation is strictly forbidden.",
            recommendedAction = "Create the smallest vertical repair/pivot decision addressing root retention friction. Do NOT hide failure with extra rewards or artificial streaks."
        )
    }
}
