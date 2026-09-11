package com.axiom.app.domain.analytics

import com.axiom.app.core.CanonicalAnalyticsEvents
import com.axiom.app.domain.model.Mission
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

/**
 * Gate G4 Contract Tests — E3.1 Beta Instrumentation & Privacy Guardrails.
 *
 * Verifies:
 * 1. Strict segregation of assignment and exposure events.
 * 2. Zero PII or private free text (Goal title, mission text, reflections, health/finance).
 * 3. Strict allowlist enforcement across all canonical beta events.
 * 4. WMPU calculation correctness and deduplication within cycle weeks.
 * 5. Decision dashboard telemetry aggregation and gate threshold evaluation.
 * 6. Operational error boundary sanitization.
 */
class BetaInstrumentationContractTest {

    // ---------------------------------------------------------------------------------------------
    // 1. Assignment vs. Exposure Segregation
    // ---------------------------------------------------------------------------------------------

    @Test
    fun assignmentAndExposureEvents_areStrictlySegregated() {
        // Assignment carries treatment, template, cohort ring — NO screen or UI properties
        val assignment = AnalyticsPayloadPolicy.validate(
            CanonicalAnalyticsEvents.FIRST_WIN_ASSIGNED,
            mapOf(
                "treatment_id" to "VARIANT_A",
                "template_id" to "FIRST_WIN_ONBOARDING",
                "cohort_ring" to ReleaseRing.ALPHA.name
            )
        )
        assertTrue("Assignment event must be accepted", assignment is PayloadValidation.Accepted)
        val cleanAssignment = (assignment as PayloadValidation.Accepted).clean
        assertEquals("VARIANT_A", cleanAssignment["treatment_id"])
        assertEquals("FIRST_WIN_ONBOARDING", cleanAssignment["template_id"])
        assertEquals(ReleaseRing.ALPHA.name, cleanAssignment["cohort_ring"])
        assertFalse("Assignment must NOT carry screen_name", cleanAssignment.containsKey("screen_name"))

        // Exposure carries treatment, template, screen_name, cohort ring
        val exposure = AnalyticsPayloadPolicy.validate(
            CanonicalAnalyticsEvents.FIRST_WIN_EXPOSED,
            mapOf(
                "treatment_id" to "VARIANT_A",
                "template_id" to "FIRST_WIN_ONBOARDING",
                "screen_name" to "FirstMissionScreen",
                "cohort_ring" to ReleaseRing.ALPHA.name
            )
        )
        assertTrue("Exposure event must be accepted", exposure is PayloadValidation.Accepted)
        val cleanExposure = (exposure as PayloadValidation.Accepted).clean
        assertEquals("FirstMissionScreen", cleanExposure["screen_name"])

        // Passing screen_name into assignment is rejected (not allowlisted)
        val badAssignment = AnalyticsPayloadPolicy.validate(
            CanonicalAnalyticsEvents.FIRST_WIN_ASSIGNED,
            mapOf(
                "treatment_id" to "VARIANT_A",
                "screen_name" to "FirstMissionScreen"
            )
        )
        assertTrue("Assignment must reject screen_name", badAssignment is PayloadValidation.Rejected)
    }

    @Test
    fun experimentAssignmentAndExposure_haveDistinctSchemas() {
        val expAssigned = AnalyticsPayloadPolicy.validate(
            CanonicalAnalyticsEvents.EXPERIMENT_ASSIGNED,
            mapOf(
                "experiment_id" to "EXP_001",
                "variant_id" to "TREATMENT",
                "cohort_ring" to ReleaseRing.CLOSED_BETA.name
            )
        )
        assertTrue(expAssigned is PayloadValidation.Accepted)

        val expExposed = AnalyticsPayloadPolicy.validate(
            CanonicalAnalyticsEvents.EXPERIMENT_EXPOSED,
            mapOf(
                "experiment_id" to "EXP_001",
                "variant_id" to "TREATMENT",
                "screen_name" to "HomeScreen",
                "cohort_ring" to ReleaseRing.CLOSED_BETA.name
            )
        )
        assertTrue(expExposed is PayloadValidation.Accepted)
    }

    // ---------------------------------------------------------------------------------------------
    // 2. Zero PII & Private Free-Text Invariant
    // ---------------------------------------------------------------------------------------------

    @Test
    fun allSensitiveFields_areRejectedAcrossBetaEvents() {
        val sensitiveKeys = listOf(
            "title", "mission_title", "goal_title", "goalTitle",
            "reflection", "reflection_text", "note", "journal",
            "prompt", "freetext", "free_text", "user_text",
            "health", "health_data", "medical", "salary", "income", "finance",
            "hunter_name", "username", "token", "password"
        )

        for (key in sensitiveKeys) {
            val res1 = AnalyticsPayloadPolicy.validate("mission_completed", mapOf(key to "private data"))
            assertTrue("mission_completed must reject $key", res1 is PayloadValidation.Rejected)

            val res2 = AnalyticsPayloadPolicy.validate("wmpu_achieved", mapOf(key to "private data"))
            assertTrue("wmpu_achieved must reject $key", res2 is PayloadValidation.Rejected)

            val res3 = AnalyticsPayloadPolicy.validate("first_win_completed", mapOf(key to "private data"))
            assertTrue("first_win_completed must reject $key", res3 is PayloadValidation.Rejected)
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 3. WMPU Calculation Engine
    // ---------------------------------------------------------------------------------------------

    @Test
    fun wmpuEvaluation_requiresGoalAlignedMissionWithinSevenDays() {
        val now = System.currentTimeMillis()
        val eightDaysAgo = now - (8 * 86_400_000L)
        val twoDaysAgo = now - (2 * 86_400_000L)

        // 1. Mission without goal -> Does NOT qualify
        val nonGoalMission = Mission(
            id = "m1",
            title = "Random chore",
            track = "HEALTH",
            rarity = "COMMON",
            skillId = "s1",
            skillName = "Skill",
            xpReward = 30,
            powerScore = 1f,
            status = "COMPLETED",
            dungeonId = null,
            estimatedHours = 0.5f,
            actualHours = 0.5f,
            createdAt = twoDaysAgo,
            completedAt = twoDaysAgo,
            rarityColor = 0L,
            trackId = null
        )

        val eval1 = WmpuCalculationEngine.evaluate(
            missions = listOf(nonGoalMission),
            activeGoalsCount = 1,
            nowMillis = now
        )
        assertFalse("Mission without goal must not achieve WMPU", eval1.isAchieved)
        assertEquals(0, eval1.meaningfulMissionsCount)

        // 2. Goal-aligned mission but completed 8 days ago -> Does NOT qualify
        val oldGoalMission = nonGoalMission.copy(
            id = "m2",
            trackId = "goal_fitness",
            completedAt = eightDaysAgo
        )
        val eval2 = WmpuCalculationEngine.evaluate(
            missions = listOf(oldGoalMission),
            activeGoalsCount = 1,
            nowMillis = now
        )
        assertFalse("Goal mission older than 7 days must not achieve WMPU", eval2.isAchieved)

        // 3. Goal-aligned mission completed 2 days ago -> Achieves WMPU!
        val validGoalMission = nonGoalMission.copy(
            id = "m3",
            trackId = "goal_fitness",
            completedAt = twoDaysAgo
        )
        val eval3 = WmpuCalculationEngine.evaluate(
            missions = listOf(validGoalMission),
            activeGoalsCount = 1,
            nowMillis = now
        )
        assertTrue("Goal mission completed within 7 days must achieve WMPU", eval3.isAchieved)
        assertEquals(1, eval3.meaningfulMissionsCount)
    }

    @Test
    fun wmpuDeduplication_preventsMultipleEmissionsInSameCycleWeek() {
        val now = System.currentTimeMillis()
        val currentWeek = WmpuCalculationEngine.currentCycleWeek(now)

        val achievedEval = WmpuCalculationEngine.WmpuEvaluation(
            cycleWeek = currentWeek,
            isAchieved = true,
            meaningfulMissionsCount = 2,
            activeGoalsCount = 1
        )

        // First achievement in current cycle week -> should emit
        assertTrue(
            "New week must emit WMPU event",
            WmpuCalculationEngine.shouldEmitWmpuEvent(achievedEval, lastRecordedCycleWeek = null)
        )
        assertTrue(
            "Previous week must emit WMPU event for new week",
            WmpuCalculationEngine.shouldEmitWmpuEvent(achievedEval, lastRecordedCycleWeek = currentWeek - 1)
        )

        // Already recorded for this cycle week -> must NOT emit
        assertFalse(
            "Same week must NOT emit duplicate WMPU event",
            WmpuCalculationEngine.shouldEmitWmpuEvent(achievedEval, lastRecordedCycleWeek = currentWeek)
        )
    }

    // ---------------------------------------------------------------------------------------------
    // 4. Decision Dashboard Telemetry Aggregator
    // ---------------------------------------------------------------------------------------------

    @Test
    fun decisionDashboard_passingInputs_producesReadySnapshot() {
        val inputs = DecisionDashboardAggregator.TelemetryRawInputs(
            firstWinExposures = 10,
            firstWinCompletions = 8, // FMC = 80% >= 65%
            totalCohortActiveUsers = 100,
            usersWithWmpu = 35, // WMPU rate = 35% >= 25%
            recordedValidEvents = 98,
            expectedTriggeredEvents = 100, // Completeness = 98% >= 95%
            totalOperations = 1000,
            operationalErrors = 5, // Integrity = 99.5% >= 99%
            cohortRing = ReleaseRing.ALPHA
        )

        val snapshot = DecisionDashboardAggregator.computeSnapshot(inputs)

        assertEquals(0.80f, snapshot.firstMissionCompletionRate, 0.001f)
        assertEquals(0.35f, snapshot.wmpuRate, 0.001f)
        assertEquals(0.98f, snapshot.eventCompletenessRate, 0.001f)
        assertEquals(0.995f, snapshot.operationalIntegrityRate, 0.001f)

        assertTrue(snapshot.isFmcPassing)
        assertTrue(snapshot.isWmpuPassing)
        assertTrue(snapshot.isCompletenessPassing)
        assertTrue(snapshot.isIntegrityPassing)
        assertTrue("All thresholds pass -> candidate ready", snapshot.isGateG4CandidateReady)
    }

    @Test
    fun decisionDashboard_failingThresholds_blocksGateReadiness() {
        val lowFmcInputs = DecisionDashboardAggregator.TelemetryRawInputs(
            firstWinExposures = 10,
            firstWinCompletions = 5, // FMC = 50% < 65% (FAIL)
            totalCohortActiveUsers = 100,
            usersWithWmpu = 30,
            recordedValidEvents = 100,
            expectedTriggeredEvents = 100,
            totalOperations = 1000,
            operationalErrors = 0,
            cohortRing = ReleaseRing.ALPHA
        )

        val snapshot = DecisionDashboardAggregator.computeSnapshot(lowFmcInputs)
        assertFalse(snapshot.isFmcPassing)
        assertFalse("Low FMC must block Gate G4 candidate readiness", snapshot.isGateG4CandidateReady)
    }

    // ---------------------------------------------------------------------------------------------
    // 5. Operational Error Boundary Sanitization
    // ---------------------------------------------------------------------------------------------

    @Test
    fun operationalError_acceptsSanitizedCode_andRejectsStacktrace() {
        val sanitized = AnalyticsPayloadPolicy.validate(
            CanonicalAnalyticsEvents.OPERATIONAL_ERROR,
            mapOf(
                "component" to "AnalyticsDrainWorker",
                "error_category" to "NETWORK_TIMEOUT",
                "error_code" to "HTTP_504",
                "status" to "RETRY_SCHEDULED"
            )
        )
        assertTrue("Sanitized operational error must be accepted", sanitized is PayloadValidation.Accepted)

        // Raw stacktrace or path leakage is rejected
        val leakedError = AnalyticsPayloadPolicy.validate(
            CanonicalAnalyticsEvents.OPERATIONAL_ERROR,
            mapOf(
                "component" to "RoomDatabase",
                "stack_trace" to "/data/user/0/com.axiom.app/databases/axiom.db corrupted"
            )
        )
        assertTrue("Un-allowlisted stack_trace must be rejected", leakedError is PayloadValidation.Rejected)
    }
}
