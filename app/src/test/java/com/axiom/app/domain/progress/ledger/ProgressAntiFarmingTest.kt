package com.axiom.app.domain.progress.ledger

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * G3-P2 — Unit tests verifying anti-farming rules and execution velocity limits.
 */
class ProgressAntiFarmingTest {

    private val policy = ProgressAntiFarmingPolicy(
        minDuplicateIntervalMs = 60_000L,
        maxCompletionsPerMinute = 3,
        maxEffectiveHoursPerEntry = 24.0,
        maxXpPerHour = 15_000L
    )

    @Test
    fun validate_zeroOrNegativeDuration_isRejected() {
        val zeroDurationEntry = LedgerEntry.ExecutionEntry(
            id = "e-zero",
            timestamp = 1000L,
            missionId = "m-farm",
            title = "Instant Click Spam",
            durationMinutes = 0.0
        )
        val negativeDurationEntry = LedgerEntry.ExecutionEntry(
            id = "e-neg",
            timestamp = 1000L,
            missionId = "m-farm",
            title = "Negative Duration Glitch",
            durationMinutes = -10.0
        )

        val resultZero = policy.validate(zeroDurationEntry, emptyList())
        assertTrue(resultZero is ProgressAntiFarmingPolicy.ValidationResult.Rejected)
        val rejZero = resultZero as ProgressAntiFarmingPolicy.ValidationResult.Rejected
        assertTrue(rejZero.violation is ProgressAntiFarmingPolicy.AntiFarmingViolation.ZeroOrNegativeDuration)

        val resultNeg = policy.validate(negativeDurationEntry, emptyList())
        assertTrue(resultNeg is ProgressAntiFarmingPolicy.ValidationResult.Rejected)
    }

    @Test
    fun validate_rapidDuplicateSubmission_isRejected() {
        val initialEntry = LedgerEntry.ExecutionEntry(
            id = "e-orig",
            timestamp = 100_000L,
            missionId = "m-dup-test",
            title = "Focused Reading",
            durationMinutes = 30.0
        )
        val history = listOf(initialEntry)

        // Duplicate submitted 20 seconds later (within 60s cooldown)
        val rapidDuplicate = LedgerEntry.ExecutionEntry(
            id = "e-dup-early",
            timestamp = 120_000L,
            missionId = "m-dup-test",
            title = "Focused Reading",
            durationMinutes = 30.0
        )
        val earlyResult = policy.validate(rapidDuplicate, history)
        assertTrue(earlyResult is ProgressAntiFarmingPolicy.ValidationResult.Rejected)
        val rej = earlyResult as ProgressAntiFarmingPolicy.ValidationResult.Rejected
        assertTrue(rej.violation is ProgressAntiFarmingPolicy.AntiFarmingViolation.RapidDuplicateSubmission)
        assertEquals("m-dup-test", (rej.violation as ProgressAntiFarmingPolicy.AntiFarmingViolation.RapidDuplicateSubmission).missionId)

        // Valid duplicate submitted 65 seconds later (> 60s cooldown)
        val validDelayedDuplicate = LedgerEntry.ExecutionEntry(
            id = "e-dup-valid",
            timestamp = 165_000L,
            missionId = "m-dup-test",
            title = "Focused Reading",
            durationMinutes = 30.0
        )
        val validResult = policy.validate(validDelayedDuplicate, history)
        assertTrue(validResult is ProgressAntiFarmingPolicy.ValidationResult.Valid)
    }

    @Test
    fun validate_velocityLimitExceeded_isRejected() {
        val baseTimestamp = 1_000_000L
        val history = listOf(
            LedgerEntry.ExecutionEntry(
                id = "e-1",
                timestamp = baseTimestamp + 10_000L,
                missionId = "m-1",
                title = "Mission 1",
                durationMinutes = 15.0
            ),
            LedgerEntry.ExecutionEntry(
                id = "e-2",
                timestamp = baseTimestamp + 20_000L,
                missionId = "m-2",
                title = "Mission 2",
                durationMinutes = 15.0
            ),
            LedgerEntry.ExecutionEntry(
                id = "e-3",
                timestamp = baseTimestamp + 30_000L,
                missionId = "m-3",
                title = "Mission 3",
                durationMinutes = 15.0
            )
        )

        // 4th completion within the same 60s window (max is 3)
        val burstEntry = LedgerEntry.ExecutionEntry(
            id = "e-4",
            timestamp = baseTimestamp + 40_000L,
            missionId = "m-4",
            title = "Mission 4",
            durationMinutes = 15.0
        )

        val result = policy.validate(burstEntry, history)
        assertTrue(result is ProgressAntiFarmingPolicy.ValidationResult.Rejected)
        val rej = result as ProgressAntiFarmingPolicy.ValidationResult.Rejected
        assertTrue(rej.violation is ProgressAntiFarmingPolicy.AntiFarmingViolation.VelocityLimitExceeded)
    }

    @Test
    fun validate_invalidCapabilityHours_isRejected() {
        val impossibleHoursEntry = LedgerEntry.CapabilityEntry(
            id = "c-unreal",
            timestamp = 1000L,
            skillId = "s-1",
            skillName = "Skill 1",
            effectiveHoursGained = 48.0, // Exceeds 24 hours per entry
            xpAwarded = 100L
        )

        val result = policy.validate(impossibleHoursEntry, emptyList())
        assertTrue(result is ProgressAntiFarmingPolicy.ValidationResult.Rejected)
        val rej = result as ProgressAntiFarmingPolicy.ValidationResult.Rejected
        assertTrue(rej.violation is ProgressAntiFarmingPolicy.AntiFarmingViolation.InvalidCapabilityHours)
    }

    @Test
    fun validate_xpSaturationExceeded_isRejected() {
        val baseTimestamp = 1_000_000L
        val history = listOf(
            LedgerEntry.CapabilityEntry(
                id = "c-1",
                timestamp = baseTimestamp + 10_000L,
                skillId = "s-1",
                skillName = "Skill 1",
                effectiveHoursGained = 2.0,
                xpAwarded = 10_000L
            )
        )

        // Adding 6,000 XP pushes the 1-hour rolling sum to 16,000 XP (> 15,000 limit)
        val saturatedEntry = LedgerEntry.CapabilityEntry(
            id = "c-2",
            timestamp = baseTimestamp + 50_000L,
            skillId = "s-1",
            skillName = "Skill 1",
            effectiveHoursGained = 1.0,
            xpAwarded = 6_000L
        )

        val result = policy.validate(saturatedEntry, history)
        assertTrue(result is ProgressAntiFarmingPolicy.ValidationResult.Rejected)
        val rej = result as ProgressAntiFarmingPolicy.ValidationResult.Rejected
        assertTrue(rej.violation is ProgressAntiFarmingPolicy.AntiFarmingViolation.XpSaturationExceeded)
    }

    @Test
    fun inMemoryProgressLedgerEngine_recordsViolations_andMaintainsIntegrity() = runBlocking {
        val engine = InMemoryProgressLedgerEngine(antiFarmingPolicy = policy)

        // Append valid execution
        val validEntry = LedgerEntry.ExecutionEntry(
            id = "e-valid-1",
            timestamp = 10_000L,
            missionId = "m-valid",
            title = "System Kernel Analysis",
            durationMinutes = 45.0
        )
        val appendResult1 = engine.append(validEntry)
        assertTrue(appendResult1 is ProgressAppendResult.Success)
        assertEquals(1, engine.getSnapshot().execution.totalCompletions)
        assertEquals(0, engine.getSnapshot().antiFarmingViolationsCount)

        // Attempt zero-duration mission
        val invalidZero = LedgerEntry.ExecutionEntry(
            id = "e-zero",
            timestamp = 15_000L,
            missionId = "m-spam",
            title = "Zero Mission",
            durationMinutes = 0.0
        )
        val appendResult2 = engine.append(invalidZero)
        assertTrue(appendResult2 is ProgressAppendResult.Rejected)
        assertEquals(1, engine.getSnapshot().execution.totalCompletions)
        assertEquals(1, engine.getSnapshot().antiFarmingViolationsCount)

        // Attempt rapid duplicate
        val invalidDup = LedgerEntry.ExecutionEntry(
            id = "e-dup",
            timestamp = 20_000L,
            missionId = "m-valid", // Duplicate within 10 seconds
            title = "System Kernel Analysis",
            durationMinutes = 45.0
        )
        val appendResult3 = engine.append(invalidDup)
        assertTrue(appendResult3 is ProgressAppendResult.Rejected)
        assertEquals(2, engine.getSnapshot().antiFarmingViolationsCount)

        // Replay with mixed dirty list
        val mixedEntries = listOf(
            validEntry,
            invalidZero,
            invalidDup,
            LedgerEntry.CapabilityEntry(
                id = "c-legit",
                timestamp = 80_000L,
                skillId = "s-os",
                skillName = "OS",
                effectiveHoursGained = 6.0,
                xpAwarded = 600L
            )
        )

        val replayedSnapshot = engine.replay(mixedEntries)
        assertEquals(1, replayedSnapshot.execution.totalCompletions)
        assertEquals(6.0, replayedSnapshot.capability.totalEffectiveHours, 0.001)
        assertEquals(2, replayedSnapshot.antiFarmingViolationsCount)
    }

    @Test
    fun isRankAdvancementLegitimate_enforcesDecouplingFromRawXP() {
        // High XP without hours or milestones cannot reach BUILDER
        assertFalse(
            policy.isRankAdvancementLegitimate(
                candidateRank = ProgressRank.BUILDER,
                effectiveHours = 0.0,
                milestonesAchieved = 0,
                rawXP = 500_000L
            )
        )

        // Sufficient hours (80.0), sufficient milestones (10), and sufficient XP (15,000) achieves BUILDER
        assertTrue(
            policy.isRankAdvancementLegitimate(
                candidateRank = ProgressRank.BUILDER,
                effectiveHours = 80.0,
                milestonesAchieved = 10,
                rawXP = 15_000L
            )
        )
    }
}
