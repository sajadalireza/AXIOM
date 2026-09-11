package com.axiom.app.domain.progress.ledger

import androidx.compose.runtime.Immutable

/**
 * Projections derived deterministically from the immutable [LedgerEntry] stream.
 *
 * Per AXIOM Upgrade Master Plan & Execution Roadmap (E2.4):
 * Separates:
 *  1. [ExecutionProjection] — Execution actions and missions completed in the real world.
 *  2. [CapabilityProjection] — Deliberate practice and skill capabilities built.
 *  3. [OutcomeProjection] — Measurable progress toward real-world goals and projects.
 *
 * Core Principle: Progression and Rank are decoupled from raw XP. Goal and Outcome
 * progress remain primary over raw XP per Product Constitution ("Goal Progress از XP مهم‌تر است").
 */

@Immutable
data class ExecutionProjection(
    val totalCompletions: Int = 0,
    val totalFocusMinutes: Double = 0.0,
    val timedCompletions: Int = 0,
    val averageQualityScore: Double = 0.0,
    val trackBreakdown: Map<String, Int> = emptyMap()
) {
    val effectiveExecutionHours: Double
        get() = totalFocusMinutes / 60.0
}

@Immutable
data class SkillCapability(
    val skillId: String,
    val skillName: String,
    val effectiveHours: Double = 0.0,
    val xpEarned: Long = 0L,
    val practiceSessionsCount: Int = 0,
    val masteryTier: String = "NOVICE"
)

@Immutable
data class CapabilityProjection(
    val skills: Map<String, SkillCapability> = emptyMap(),
    val totalEffectiveHours: Double = 0.0,
    val totalCapabilityXP: Long = 0L,
    val topSkillId: String? = null
)

@Immutable
data class GoalOutcome(
    val goalId: String,
    val goalTitle: String,
    val progressPercent: Float = 0f,
    val milestonesAchieved: Int = 0,
    val completedStagesCount: Int = 0,
    val isCompleted: Boolean = false
)

@Immutable
data class OutcomeProjection(
    val goals: Map<String, GoalOutcome> = emptyMap(),
    val totalMilestonesAchieved: Int = 0,
    val completedProjectsCount: Int = 0,
    val totalGoalProgressSum: Float = 0f
)

/**
 * Canonical Progress Rank decoupled from raw XP.
 *
 * Advancing beyond [INITIATE] strictly requires:
 * 1. Deliberate Practice Capability ([requiredHours])
 * 2. Real-World Goal / Project Outcomes ([requiredMilestones])
 * 3. Base Execution ([requiredXP])
 *
 * Raw XP farming without genuine capability hours or outcome milestones cannot
 * bypass the rank gate.
 */
enum class ProgressRank(
    val titleEn: String,
    val titleFa: String,
    val requiredHours: Double,
    val requiredMilestones: Int,
    val requiredXP: Long
) {
    INITIATE(
        titleEn = "Initiate",
        titleFa = "آغازگر",
        requiredHours = 0.0,
        requiredMilestones = 0,
        requiredXP = 0L
    ),
    APPRENTICE(
        titleEn = "Apprentice",
        titleFa = "کارآموز",
        requiredHours = 5.0,
        requiredMilestones = 1,
        requiredXP = 500L
    ),
    PRACTITIONER(
        titleEn = "Practitioner",
        titleFa = "تمرین‌ورز",
        requiredHours = 25.0,
        requiredMilestones = 3,
        requiredXP = 2_500L
    ),
    BUILDER(
        titleEn = "Builder",
        titleFa = "سازنده",
        requiredHours = 75.0,
        requiredMilestones = 8,
        requiredXP = 10_000L
    ),
    ARCHITECT(
        titleEn = "Architect",
        titleFa = "معمار",
        requiredHours = 200.0,
        requiredMilestones = 20,
        requiredXP = 30_000L
    ),
    MASTER(
        titleEn = "Master",
        titleFa = "استاد",
        requiredHours = 500.0,
        requiredMilestones = 50,
        requiredXP = 100_000L
    );

    fun isEligible(effectiveHours: Double, milestonesAchieved: Int, rawXP: Long): Boolean {
        return effectiveHours >= requiredHours &&
                milestonesAchieved >= requiredMilestones &&
                rawXP >= requiredXP
    }

    companion object {
        fun fromProgress(
            effectiveHours: Double,
            milestonesAchieved: Int,
            rawXP: Long
        ): ProgressRank {
            val ranks = values().sortedByDescending { it.ordinal }
            for (rank in ranks) {
                if (rank.isEligible(effectiveHours, milestonesAchieved, rawXP)) {
                    return rank
                }
            }
            return INITIATE
        }
    }
}

/**
 * Consolidated immutable state containing the full projection of all three progress pillars.
 */
@Immutable
data class ProgressSnapshot(
    val execution: ExecutionProjection = ExecutionProjection(),
    val capability: CapabilityProjection = CapabilityProjection(),
    val outcome: OutcomeProjection = OutcomeProjection(),
    val progressRank: ProgressRank = ProgressRank.INITIATE,
    val totalRawXP: Long = 0L,
    val totalEntriesCount: Int = 0,
    val lastEntryTimestamp: Long = 0L,
    val antiFarmingViolationsCount: Int = 0
) {
    companion object {
        val EMPTY = ProgressSnapshot()
    }
}
