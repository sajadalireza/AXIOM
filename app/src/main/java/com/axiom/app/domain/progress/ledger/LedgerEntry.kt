package com.axiom.app.domain.progress.ledger

import androidx.compose.runtime.Immutable

/**
 * Immutable Progress Ledger entry representing a durable event in the user's progress.
 *
 * Per AXIOM Upgrade Master Plan & Execution Roadmap (E2.4):
 * The Progress Ledger separates:
 *  1. [ExecutionEntry] — Actions and missions completed in the physical/digital world.
 *  2. [CapabilityEntry] — Deliberate practice and skill capabilities built.
 *  3. [OutcomeEntry] — Measurable progress toward real-world goals and projects.
 */
@Immutable
sealed interface LedgerEntry {
    val id: String
    val timestamp: Long

    /**
     * Execution Entry — records a completed mission or focus session.
     */
    @Immutable
    data class ExecutionEntry(
        override val id: String,
        override val timestamp: Long,
        val missionId: String,
        val title: String,
        val durationMinutes: Double,
        val qualityScore: Double = 1.0,
        val isTimedMission: Boolean = false,
        val track: String = "DEFAULT"
    ) : LedgerEntry

    /**
     * Capability Entry — records deliberate practice hours and capability built for a skill.
     */
    @Immutable
    data class CapabilityEntry(
        override val id: String,
        override val timestamp: Long,
        val skillId: String,
        val skillName: String,
        val effectiveHoursGained: Double,
        val xpAwarded: Long
    ) : LedgerEntry

    /**
     * Outcome Entry — records measurable progress advancing a Goal or Project.
     */
    @Immutable
    data class OutcomeEntry(
        override val id: String,
        override val timestamp: Long,
        val goalId: String? = null,
        val goalTitle: String? = null,
        val projectId: String? = null,
        val stageCompleted: Int? = null,
        val progressDeltaPercent: Float = 0f,
        val isMilestoneAchieved: Boolean = false
    ) : LedgerEntry
}
