package com.axiom.app.domain.progress.ledger

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * G3-P2 — Unit tests verifying deterministic replayability, pillar separation,
 * and progression rank decoupling for [ProgressProjectionReducer].
 */
class ProgressProjectionReducerTest {

    @Test
    fun reduce_withEmptyList_returnsEmptySnapshot() {
        val result = ProgressProjectionReducer.reduce(emptyList())
        assertEquals(ProgressSnapshot.EMPTY, result)
        assertEquals(0, result.totalEntriesCount)
        assertEquals(0L, result.totalRawXP)
        assertEquals(ProgressRank.INITIATE, result.progressRank)
    }

    @Test
    fun reduce_withExecutionEntries_updatesExecutionProjectionSeparately() {
        val entries = listOf(
            LedgerEntry.ExecutionEntry(
                id = "e-1",
                timestamp = 1000L,
                missionId = "m-101",
                title = "Study Kernel Memory",
                durationMinutes = 45.0,
                qualityScore = 1.0,
                isTimedMission = true,
                track = "SYSTEMS"
            ),
            LedgerEntry.ExecutionEntry(
                id = "e-2",
                timestamp = 2000L,
                missionId = "m-102",
                title = "Refactor Page Table",
                durationMinutes = 75.0,
                qualityScore = 0.8,
                isTimedMission = false,
                track = "SYSTEMS"
            ),
            LedgerEntry.ExecutionEntry(
                id = "e-3",
                timestamp = 3000L,
                missionId = "m-103",
                title = "Write Persian Docs",
                durationMinutes = 30.0,
                qualityScore = 0.9,
                isTimedMission = true,
                track = "DOCS"
            )
        )

        val snapshot = ProgressProjectionReducer.reduce(entries)

        // Execution pillar checks
        assertEquals(3, snapshot.execution.totalCompletions)
        assertEquals(150.0, snapshot.execution.totalFocusMinutes, 0.001)
        assertEquals(2.5, snapshot.execution.effectiveExecutionHours, 0.001)
        assertEquals(2, snapshot.execution.timedCompletions)
        assertEquals(0.9, snapshot.execution.averageQualityScore, 0.001)
        assertEquals(2, snapshot.execution.trackBreakdown["SYSTEMS"])
        assertEquals(1, snapshot.execution.trackBreakdown["DOCS"])

        // Pillar separation: Capability and Outcome must remain unaffected
        assertEquals(0.0, snapshot.capability.totalEffectiveHours, 0.001)
        assertEquals(0L, snapshot.capability.totalCapabilityXP)
        assertTrue(snapshot.capability.skills.isEmpty())

        assertEquals(0, snapshot.outcome.totalMilestonesAchieved)
        assertEquals(0, snapshot.outcome.completedProjectsCount)
        assertTrue(snapshot.outcome.goals.isEmpty())

        // Metadata checks
        assertEquals(3, snapshot.totalEntriesCount)
        assertEquals(3000L, snapshot.lastEntryTimestamp)
        assertEquals(ProgressRank.INITIATE, snapshot.progressRank)
    }

    @Test
    fun reduce_withCapabilityEntries_updatesCapabilityProjectionSeparately() {
        val entries = listOf(
            LedgerEntry.CapabilityEntry(
                id = "c-1",
                timestamp = 1000L,
                skillId = "s-os",
                skillName = "OS Engineering",
                effectiveHoursGained = 10.0,
                xpAwarded = 1000L
            ),
            LedgerEntry.CapabilityEntry(
                id = "c-2",
                timestamp = 2000L,
                skillId = "s-os",
                skillName = "OS Engineering",
                effectiveHoursGained = 45.0,
                xpAwarded = 4500L
            ),
            LedgerEntry.CapabilityEntry(
                id = "c-3",
                timestamp = 3000L,
                skillId = "s-arch",
                skillName = "System Architecture",
                effectiveHoursGained = 20.0,
                xpAwarded = 2000L
            )
        )

        val snapshot = ProgressProjectionReducer.reduce(entries)

        // Capability pillar checks
        assertEquals(75.0, snapshot.capability.totalEffectiveHours, 0.001)
        assertEquals(7500L, snapshot.capability.totalCapabilityXP)
        assertEquals(7500L, snapshot.totalRawXP)
        assertEquals("s-os", snapshot.capability.topSkillId)

        val osSkill = snapshot.capability.skills["s-os"]
        assertNotNull(osSkill)
        assertEquals(55.0, osSkill!!.effectiveHours, 0.001)
        assertEquals(5500L, osSkill.xpEarned)
        assertEquals(2, osSkill.practiceSessionsCount)
        assertEquals("Practitioner", osSkill.masteryTier)

        val archSkill = snapshot.capability.skills["s-arch"]
        assertNotNull(archSkill)
        assertEquals(20.0, archSkill!!.effectiveHours, 0.001)
        assertEquals(1, archSkill.practiceSessionsCount)
        assertEquals("Novice", archSkill.masteryTier)

        // Pillar separation: Execution and Outcome remain 0
        assertEquals(0, snapshot.execution.totalCompletions)
        assertEquals(0, snapshot.outcome.totalMilestonesAchieved)

        // Rank checks: Despite 75 hours and 7500 XP, with 0 outcome milestones, rank is INITIATE
        assertEquals(ProgressRank.INITIATE, snapshot.progressRank)
    }

    @Test
    fun reduce_withOutcomeEntries_updatesOutcomeProjectionSeparately() {
        val entries = listOf(
            LedgerEntry.OutcomeEntry(
                id = "o-1",
                timestamp = 1000L,
                goalId = "g-1",
                goalTitle = "Launch Microkernel",
                projectId = "p-1",
                stageCompleted = 1,
                progressDeltaPercent = 25f,
                isMilestoneAchieved = false
            ),
            LedgerEntry.OutcomeEntry(
                id = "o-2",
                timestamp = 2000L,
                goalId = "g-1",
                goalTitle = "Launch Microkernel",
                projectId = "p-1",
                stageCompleted = 2,
                progressDeltaPercent = 25f,
                isMilestoneAchieved = true
            ),
            LedgerEntry.OutcomeEntry(
                id = "o-3",
                timestamp = 3000L,
                goalId = "g-2",
                goalTitle = "Publish Compiler Book",
                projectId = null,
                stageCompleted = null,
                progressDeltaPercent = 10f,
                isMilestoneAchieved = true
            )
        )

        val snapshot = ProgressProjectionReducer.reduce(entries)

        // Outcome pillar checks
        assertEquals(2, snapshot.outcome.totalMilestonesAchieved)
        assertEquals(1, snapshot.outcome.completedProjectsCount)
        assertEquals(60f, snapshot.outcome.totalGoalProgressSum, 0.01f)

        val goal1 = snapshot.outcome.goals["g-1"]
        assertNotNull(goal1)
        assertEquals(50f, goal1!!.progressPercent, 0.01f)
        assertEquals(1, goal1.milestonesAchieved)
        assertEquals(2, goal1.completedStagesCount)
        assertFalse(goal1.isCompleted)

        val goal2 = snapshot.outcome.goals["g-2"]
        assertNotNull(goal2)
        assertEquals(10f, goal2!!.progressPercent, 0.01f)
        assertEquals(1, goal2.milestonesAchieved)

        // Pillar separation
        assertEquals(0, snapshot.execution.totalCompletions)
        assertEquals(0.0, snapshot.capability.totalEffectiveHours, 0.001)
    }

    @Test
    fun reduce_isDeterministicAndReplayEquivalent_regardlessOfInputOrder() {
        val rand = Random(42)
        val entries = mutableListOf<LedgerEntry>()

        for (i in 0 until 100) {
            val ts = rand.nextLong(100_000L, 500_000L)
            when (i % 3) {
                0 -> entries.add(
                    LedgerEntry.ExecutionEntry(
                        id = "exec-$i",
                        timestamp = ts,
                        missionId = "m-$i",
                        title = "Mission $i",
                        durationMinutes = rand.nextDouble(10.0, 60.0),
                        qualityScore = rand.nextDouble(0.7, 1.0),
                        isTimedMission = i % 2 == 0
                    )
                )
                1 -> entries.add(
                    LedgerEntry.CapabilityEntry(
                        id = "cap-$i",
                        timestamp = ts,
                        skillId = "skill-${i % 5}",
                        skillName = "Skill ${i % 5}",
                        effectiveHoursGained = rand.nextDouble(0.5, 3.0),
                        xpAwarded = rand.nextLong(50, 300)
                    )
                )
                2 -> entries.add(
                    LedgerEntry.OutcomeEntry(
                        id = "out-$i",
                        timestamp = ts,
                        goalId = "goal-${i % 3}",
                        goalTitle = "Goal ${i % 3}",
                        projectId = if (i % 4 == 0) "proj-$i" else null,
                        stageCompleted = if (i % 4 == 0) 1 else null,
                        progressDeltaPercent = (rand.nextFloat() * 10f),
                        isMilestoneAchieved = i % 5 == 0
                    )
                )
            }
        }

        val snapshotOriginal = ProgressProjectionReducer.reduce(entries)
        val snapshotShuffled1 = ProgressProjectionReducer.reduce(entries.shuffled(Random(1)))
        val snapshotShuffled2 = ProgressProjectionReducer.reduce(entries.shuffled(Random(99)))

        // Strict deterministic equality
        assertEquals(snapshotOriginal, snapshotShuffled1)
        assertEquals(snapshotOriginal, snapshotShuffled2)
        assertEquals(100, snapshotOriginal.totalEntriesCount)
    }

    @Test
    fun rankProgression_isStrictlyDecoupledFromRawXP() {
        // Scenario 1: Extreme raw XP farming (1,000,000 XP) with zero practice hours and zero milestones
        val excessiveXpEntry = LedgerEntry.CapabilityEntry(
            id = "farm-1",
            timestamp = 1000L,
            skillId = "s-farm",
            skillName = "Farmed Skill",
            effectiveHoursGained = 0.0, // No real deliberate practice hours
            xpAwarded = 1_000_000L
        )
        val farmedSnapshot = ProgressProjectionReducer.reduce(listOf(excessiveXpEntry))

        assertEquals(1_000_000L, farmedSnapshot.totalRawXP)
        // Must NOT advance past INITIATE because effective hours and milestones are 0
        assertEquals(ProgressRank.INITIATE, farmedSnapshot.progressRank)

        // Scenario 2: High practice hours (100h) and high XP (20,000 XP) but 0 Goal milestones
        val hoursOnlyEntries = listOf(
            LedgerEntry.CapabilityEntry(
                id = "hours-1",
                timestamp = 2000L,
                skillId = "s-deep",
                skillName = "Deep Study",
                effectiveHoursGained = 100.0,
                xpAwarded = 20_000L
            )
        )
        val hoursOnlySnapshot = ProgressProjectionReducer.reduce(hoursOnlyEntries)
        assertEquals(100.0, hoursOnlySnapshot.capability.totalEffectiveHours, 0.001)
        // Still INITIATE because 0 outcome milestones achieved
        assertEquals(ProgressRank.INITIATE, hoursOnlySnapshot.progressRank)

        // Scenario 3: Real multi-pillar progression reaching BUILDER:
        // Requires >= 75 hours, >= 8 milestones, >= 10,000 XP
        val multiPillarEntries = listOf(
            LedgerEntry.CapabilityEntry(
                id = "legit-cap",
                timestamp = 3000L,
                skillId = "s-legit",
                skillName = "Genuine Engineering",
                effectiveHoursGained = 80.0,
                xpAwarded = 12_000L
            )
        ) + (1..10).map { i ->
            LedgerEntry.OutcomeEntry(
                id = "legit-out-$i",
                timestamp = 3100L + i,
                goalId = "g-builder",
                goalTitle = "Complete Foundation Project",
                isMilestoneAchieved = true,
                progressDeltaPercent = 10f
            )
        }

        val builderSnapshot = ProgressProjectionReducer.reduce(multiPillarEntries)
        assertEquals(ProgressRank.BUILDER, builderSnapshot.progressRank)
        assertEquals(10, builderSnapshot.outcome.totalMilestonesAchieved)
        assertEquals(80.0, builderSnapshot.capability.totalEffectiveHours, 0.001)
    }
}
