package com.axiom.app.domain.analytics

/**
 * Pure telemetry aggregator for Gate G4 Instrumented Beta Decision Dashboard.
 *
 * Computes core product truth metrics without PII:
 * 1. FMC Rate (First Mission Completion): Target >= 65%, stretch 80%.
 * 2. WMPU Rate (Weekly Meaningful Progress Unit): Target >= 25%, stretch 40%.
 * 3. Event Completeness Rate: Target >= 95%.
 * 4. Operational Integrity Rate: Target >= 99% with zero S1.
 */
object DecisionDashboardAggregator {

    const val MIN_FMC_THRESHOLD = 0.65f
    const val MIN_WMPU_THRESHOLD = 0.25f
    const val MIN_EVENT_COMPLETENESS_THRESHOLD = 0.95f
    const val MIN_OPERATIONAL_INTEGRITY_THRESHOLD = 0.99f

    data class TelemetryRawInputs(
        val firstWinExposures: Int,
        val firstWinCompletions: Int,
        val totalCohortActiveUsers: Int,
        val usersWithWmpu: Int,
        val recordedValidEvents: Int,
        val expectedTriggeredEvents: Int,
        val totalOperations: Int,
        val operationalErrors: Int,
        val cohortRing: ReleaseRing = ReleaseRing.ALPHA
    )

    data class DecisionDashboardSnapshot(
        val cohortRing: ReleaseRing,
        val firstMissionCompletionRate: Float,
        val wmpuRate: Float,
        val eventCompletenessRate: Float,
        val operationalIntegrityRate: Float,
        val isFmcPassing: Boolean,
        val isWmpuPassing: Boolean,
        val isCompletenessPassing: Boolean,
        val isIntegrityPassing: Boolean,
        val isGateG4CandidateReady: Boolean
    )

    fun computeSnapshot(inputs: TelemetryRawInputs): DecisionDashboardSnapshot {
        val fmcRate = if (inputs.firstWinExposures > 0) {
            (inputs.firstWinCompletions.toFloat() / inputs.firstWinExposures.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        val wmpuRate = if (inputs.totalCohortActiveUsers > 0) {
            (inputs.usersWithWmpu.toFloat() / inputs.totalCohortActiveUsers.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        val completenessRate = if (inputs.expectedTriggeredEvents > 0) {
            (inputs.recordedValidEvents.toFloat() / inputs.expectedTriggeredEvents.toFloat()).coerceIn(0f, 1f)
        } else {
            1f // Neutral default if no events expected yet
        }

        val integrityRate = if (inputs.totalOperations > 0) {
            ((inputs.totalOperations - inputs.operationalErrors).toFloat() / inputs.totalOperations.toFloat()).coerceIn(0f, 1f)
        } else {
            1f
        }

        val fmcPassing = fmcRate >= MIN_FMC_THRESHOLD
        val wmpuPassing = wmpuRate >= MIN_WMPU_THRESHOLD
        val completenessPassing = completenessRate >= MIN_EVENT_COMPLETENESS_THRESHOLD
        val integrityPassing = integrityRate >= MIN_OPERATIONAL_INTEGRITY_THRESHOLD

        val gateReady = fmcPassing && wmpuPassing && completenessPassing && integrityPassing

        return DecisionDashboardSnapshot(
            cohortRing = inputs.cohortRing,
            firstMissionCompletionRate = fmcRate,
            wmpuRate = wmpuRate,
            eventCompletenessRate = completenessRate,
            operationalIntegrityRate = integrityRate,
            isFmcPassing = fmcPassing,
            isWmpuPassing = wmpuPassing,
            isCompletenessPassing = completenessPassing,
            isIntegrityPassing = integrityPassing,
            isGateG4CandidateReady = gateReady
        )
    }
}
