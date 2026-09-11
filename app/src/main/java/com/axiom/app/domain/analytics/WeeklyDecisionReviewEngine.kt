package com.axiom.app.domain.analytics

/**
 * Pure domain engine executing weekly decision reviews for Gate G4 (Instrumented Beta).
 *
 * Enforces Gate G4 exit thresholds:
 * - D1 Retention >= 30%
 * - D7 Retention >= 20–25% (minimum 20%, target 25%)
 * - WMPU Rate >= 25% (target 40%)
 * - S1 Defects == 0 (zero tolerance)
 *
 * Core Governance Invariant:
 * "If thresholds fail, do not hide the failure with new rewards/content.
 * Create the smallest repair/pivot decision."
 */
object WeeklyDecisionReviewEngine {

    const val MIN_D1_RETENTION = 0.30f
    const val MIN_D7_RETENTION = 0.20f
    const val MIN_WMPU_RATE = 0.25f
    const val MAX_S1_DEFECTS = 0

    const val MIN_OBSERVATION_DAYS = 21
    const val MIN_COHORT_USERS = 20

    enum class DecisionOutcome {
        ADVANCE_TO_G5,
        MAINTAIN_CURRENT_RING,
        NEEDS_REPAIR_OR_PIVOT
    }

    data class DecisionReviewInputs(
        val d1RetentionRate: Float,
        val d7RetentionRate: Float,
        val wmpuRate: Float,
        val s1DefectCount: Int,
        val activeUsers: Int,
        val durationDays: Int,
        val cohortRing: ReleaseRing = ReleaseRing.CLOSED_BETA
    )

    data class DecisionReviewResult(
        val outcome: DecisionOutcome,
        val isD1Passing: Boolean,
        val isD7Passing: Boolean,
        val isWmpuPassing: Boolean,
        val isCrashFreePassing: Boolean,
        val isObservationComplete: Boolean,
        val isRewardInflationForbidden: Boolean,
        val rationale: String,
        val recommendedAction: String
    )

    fun evaluateReview(inputs: DecisionReviewInputs): DecisionReviewResult {
        val d1Pass = inputs.d1RetentionRate >= MIN_D1_RETENTION
        val d7Pass = inputs.d7RetentionRate >= MIN_D7_RETENTION
        val wmpuPass = inputs.wmpuRate >= MIN_WMPU_RATE
        val crashPass = inputs.s1DefectCount <= MAX_S1_DEFECTS
        val observationComplete = inputs.durationDays >= MIN_OBSERVATION_DAYS && inputs.activeUsers >= MIN_COHORT_USERS

        if (!observationComplete) {
            return DecisionReviewResult(
                outcome = DecisionOutcome.MAINTAIN_CURRENT_RING,
                isD1Passing = d1Pass,
                isD7Passing = d7Pass,
                isWmpuPassing = wmpuPass,
                isCrashFreePassing = crashPass,
                isObservationComplete = false,
                isRewardInflationForbidden = false,
                rationale = "Observation window or cohort size incomplete (${inputs.durationDays}/$MIN_OBSERVATION_DAYS days, ${inputs.activeUsers}/$MIN_COHORT_USERS users).",
                recommendedAction = "Continue observation in ${inputs.cohortRing.name} ring without premature intervention."
            )
        }

        val allPassing = d1Pass && d7Pass && wmpuPass && crashPass

        if (allPassing) {
            return DecisionReviewResult(
                outcome = DecisionOutcome.ADVANCE_TO_G5,
                isD1Passing = true,
                isD7Passing = true,
                isWmpuPassing = true,
                isCrashFreePassing = true,
                isObservationComplete = true,
                isRewardInflationForbidden = false,
                rationale = "All Gate G4 exit criteria satisfied: D1=${"%.1f".format(inputs.d1RetentionRate * 100)}%, D7=${"%.1f".format(inputs.d7RetentionRate * 100)}%, WMPU=${"%.1f".format(inputs.wmpuRate * 100)}%, zero S1 defects.",
                recommendedAction = "Authorize exit from Gate G4 and advance to Phase D (Gate G5 Retention Proof)."
            )
        }

        // Failure case: strictly enforce the Anti-Inflation Invariant
        val failureReasons = mutableListOf<String>()
        if (!d1Pass) failureReasons.add("D1 retention (${"%.1f".format(inputs.d1RetentionRate * 100)}%) < 30.0%")
        if (!d7Pass) failureReasons.add("D7 retention (${"%.1f".format(inputs.d7RetentionRate * 100)}%) < 20.0%")
        if (!wmpuPass) failureReasons.add("WMPU rate (${"%.1f".format(inputs.wmpuRate * 100)}%) < 25.0%")
        if (!crashPass) failureReasons.add("S1 defects (${inputs.s1DefectCount}) > 0")

        return DecisionReviewResult(
            outcome = DecisionOutcome.NEEDS_REPAIR_OR_PIVOT,
            isD1Passing = d1Pass,
            isD7Passing = d7Pass,
            isWmpuPassing = wmpuPass,
            isCrashFreePassing = crashPass,
            isObservationComplete = true,
            isRewardInflationForbidden = true,
            rationale = "Thresholds failed: ${failureReasons.joinToString("; ")}. Reward inflation is strictly forbidden.",
            recommendedAction = "Create the smallest vertical repair/pivot decision addressing root friction. Do NOT hide failure with extra XP or bonuses."
        )
    }
}
