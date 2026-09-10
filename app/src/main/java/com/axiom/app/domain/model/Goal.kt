package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

/**
 * Canonical Goal domain model representing a desired real-world outcome.
 *
 * Per AXIOM Canonical Vocabulary (CANONICAL_VOCABULARY.md):
 * "A Goal represents the result the user is trying to achieve. It may be advanced
 * directly by Missions or through Projects."
 */
@Immutable
data class Goal(
    val id: String,
    val title: String,
    val description: String = "",
    val targetDate: Long? = null,
    val status: GoalStatus = GoalStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val progressPercent: Float = 0f
)

enum class GoalStatus {
    ACTIVE,
    COMPLETED,
    PAUSED,
    ARCHIVED
}
