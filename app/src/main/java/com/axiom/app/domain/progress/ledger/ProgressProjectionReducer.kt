package com.axiom.app.domain.progress.ledger

import com.axiom.app.domain.model.MasteryTier
import kotlin.math.max

/**
 * Pure, deterministic reducer for the Progress Ledger.
 *
 * Guarantees:
 * 1. Replayability: Replaying the identical sequence of [LedgerEntry] objects yields the identical [ProgressSnapshot].
 * 2. Order determinism: When reducing a collection, entries are ordered chronologically by [LedgerEntry.timestamp],
 *    with [LedgerEntry.id] breaking ties.
 * 3. Pillar Separation: State updates to Execution, Capability, and Outcome are decoupled.
 * 4. Progression Integrity: Rank advancement strictly requires Capability (effective hours) and Outcome (milestones)
 *    and cannot be unlocked by raw XP alone.
 */
object ProgressProjectionReducer {

    /**
     * Reduces a sequence of ledger entries into a consolidated [ProgressSnapshot].
     */
    fun reduce(
        entries: List<LedgerEntry>,
        initial: ProgressSnapshot = ProgressSnapshot.EMPTY
    ): ProgressSnapshot {
        if (entries.isEmpty()) return initial

        val sortedEntries = entries.sortedWith(
            compareBy<LedgerEntry> { it.timestamp }.thenBy { it.id }
        )

        return sortedEntries.fold(initial) { snapshot, entry ->
            step(snapshot, entry)
        }
    }

    /**
     * Applies a single [LedgerEntry] to the given [ProgressSnapshot], returning a new immutable snapshot.
     */
    fun step(current: ProgressSnapshot, entry: LedgerEntry): ProgressSnapshot {
        return when (entry) {
            is LedgerEntry.ExecutionEntry -> applyExecution(current, entry)
            is LedgerEntry.CapabilityEntry -> applyCapability(current, entry)
            is LedgerEntry.OutcomeEntry -> applyOutcome(current, entry)
        }
    }

    private fun applyExecution(
        current: ProgressSnapshot,
        entry: LedgerEntry.ExecutionEntry
    ): ProgressSnapshot {
        val exec = current.execution
        val newTotalCompletions = exec.totalCompletions + 1
        val newTotalFocusMinutes = exec.totalFocusMinutes + entry.durationMinutes
        val newTimedCompletions = if (entry.isTimedMission) {
            exec.timedCompletions + 1
        } else {
            exec.timedCompletions
        }

        val newAverageQuality = if (newTotalCompletions == 1) {
            entry.qualityScore
        } else {
            ((exec.averageQualityScore * exec.totalCompletions) + entry.qualityScore) / newTotalCompletions
        }

        val newTrackBreakdown = exec.trackBreakdown.toMutableMap().apply {
            this[entry.track] = (this[entry.track] ?: 0) + 1
        }

        val updatedExec = exec.copy(
            totalCompletions = newTotalCompletions,
            totalFocusMinutes = newTotalFocusMinutes,
            timedCompletions = newTimedCompletions,
            averageQualityScore = newAverageQuality,
            trackBreakdown = newTrackBreakdown
        )

        return current.copy(
            execution = updatedExec,
            totalEntriesCount = current.totalEntriesCount + 1,
            lastEntryTimestamp = max(current.lastEntryTimestamp, entry.timestamp)
        )
    }

    private fun applyCapability(
        current: ProgressSnapshot,
        entry: LedgerEntry.CapabilityEntry
    ): ProgressSnapshot {
        val cap = current.capability
        val existingSkill = cap.skills[entry.skillId]
        val updatedEffectiveHours = (existingSkill?.effectiveHours ?: 0.0) + entry.effectiveHoursGained
        val updatedXP = (existingSkill?.xpEarned ?: 0L) + entry.xpAwarded
        val updatedPracticeCount = (existingSkill?.practiceSessionsCount ?: 0) + 1
        val updatedTier = MasteryTier.fromHours(updatedEffectiveHours).label

        val updatedSkill = SkillCapability(
            skillId = entry.skillId,
            skillName = entry.skillName,
            effectiveHours = updatedEffectiveHours,
            xpEarned = updatedXP,
            practiceSessionsCount = updatedPracticeCount,
            masteryTier = updatedTier
        )

        val updatedSkillsMap = cap.skills.toMutableMap().apply {
            this[entry.skillId] = updatedSkill
        }

        val newTotalEffectiveHours = cap.totalEffectiveHours + entry.effectiveHoursGained
        val newTotalCapabilityXP = cap.totalCapabilityXP + entry.xpAwarded
        val newTotalRawXP = current.totalRawXP + entry.xpAwarded
        val topSkill = updatedSkillsMap.maxByOrNull { it.value.effectiveHours }?.key

        val updatedCap = cap.copy(
            skills = updatedSkillsMap,
            totalEffectiveHours = newTotalEffectiveHours,
            totalCapabilityXP = newTotalCapabilityXP,
            topSkillId = topSkill
        )

        val updatedRank = ProgressRank.fromProgress(
            effectiveHours = newTotalEffectiveHours,
            milestonesAchieved = current.outcome.totalMilestonesAchieved,
            rawXP = newTotalRawXP
        )

        return current.copy(
            capability = updatedCap,
            totalRawXP = newTotalRawXP,
            progressRank = updatedRank,
            totalEntriesCount = current.totalEntriesCount + 1,
            lastEntryTimestamp = max(current.lastEntryTimestamp, entry.timestamp)
        )
    }

    private fun applyOutcome(
        current: ProgressSnapshot,
        entry: LedgerEntry.OutcomeEntry
    ): ProgressSnapshot {
        val outcome = current.outcome
        val goalsMap = outcome.goals.toMutableMap()
        var milestonesAchievedDelta = 0
        var completedProjectsDelta = 0

        if (entry.goalId != null) {
            val existingGoal = goalsMap[entry.goalId]
            val newMilestones = (existingGoal?.milestonesAchieved ?: 0) + if (entry.isMilestoneAchieved) 1 else 0
            val newStages = (existingGoal?.completedStagesCount ?: 0) + if (entry.stageCompleted != null) 1 else 0
            val newProgressPercent = ((existingGoal?.progressPercent ?: 0f) + entry.progressDeltaPercent).coerceIn(0f, 100f)
            val isGoalDone = newProgressPercent >= 100f

            goalsMap[entry.goalId] = GoalOutcome(
                goalId = entry.goalId,
                goalTitle = entry.goalTitle ?: existingGoal?.goalTitle ?: "Goal",
                progressPercent = newProgressPercent,
                milestonesAchieved = newMilestones,
                completedStagesCount = newStages,
                isCompleted = isGoalDone
            )
        }

        if (entry.isMilestoneAchieved) {
            milestonesAchievedDelta += 1
        }
        if (entry.projectId != null && entry.isMilestoneAchieved) {
            completedProjectsDelta += 1
        }

        val newTotalMilestones = outcome.totalMilestonesAchieved + milestonesAchievedDelta
        val newCompletedProjects = outcome.completedProjectsCount + completedProjectsDelta
        val newTotalGoalProgressSum = goalsMap.values.sumOf { it.progressPercent.toDouble() }.toFloat()

        val updatedOutcome = outcome.copy(
            goals = goalsMap,
            totalMilestonesAchieved = newTotalMilestones,
            completedProjectsCount = newCompletedProjects,
            totalGoalProgressSum = newTotalGoalProgressSum
        )

        val updatedRank = ProgressRank.fromProgress(
            effectiveHours = current.capability.totalEffectiveHours,
            milestonesAchieved = newTotalMilestones,
            rawXP = current.totalRawXP
        )

        return current.copy(
            outcome = updatedOutcome,
            progressRank = updatedRank,
            totalEntriesCount = current.totalEntriesCount + 1,
            lastEntryTimestamp = max(current.lastEntryTimestamp, entry.timestamp)
        )
    }
}
