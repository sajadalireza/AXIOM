package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Mission(
    val id: String,
    val title: String,
    val track: String,
    val rarity: String,
    val skillId: String,
    val skillName: String,
    val xpReward: Int,
    val powerScore: Float,
    val status: String,
    val dungeonId: String?,
    val estimatedHours: Float,
    val actualHours: Float?,
    val createdAt: Long,
    val completedAt: Long?,
    val rarityColor: Long,
    @Deprecated("Legacy alias for timed mission per CANONICAL_VOCABULARY.md. Use isTimedMission.")
    val isInstantGate: Boolean = false,
    val description: String = "",
    val trackId: String? = null,
    val scheduleBlockId: String? = null,
    val qualityScore: Double = 1.0,
    val effectiveHours: Double = 0.0
) {
    /**
     * Canonical property indicating whether this is a timed / focus mission.
     */
    val isTimedMission: Boolean
        get() = isInstantGate

    /**
     * Canonical reference to the associated [Project] (maps to legacy [dungeonId]).
     */
    val projectId: String?
        get() = dungeonId

    /**
     * Canonical reference to the associated [Goal] (maps to trackId or projectId).
     */
    val goalId: String?
        get() = trackId ?: projectId
}
