package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

/**
 * Canonical Project domain model representing bounded, multi-step work
 * that produces a named outcome or artifact.
 *
 * Per AXIOM Canonical Vocabulary (CANONICAL_VOCABULARY.md):
 * "A Project is bounded multi-step work that produces a named outcome or artifact.
 * A Project may contain multiple Missions and advance a Goal."
 */
@Immutable
data class Project(
    val id: String,
    val name: String,
    val description: String = "",
    val totalStages: Int = 1,
    val completedStages: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val rarity: String = "COMMON",
    val stageDescriptions: String = "",
    val goalId: String? = null
) {
    val progressPercent: Float
        get() = if (totalStages > 0) completedStages.toFloat() / totalStages.toFloat() else 0f
}

/**
 * Lossless compatibility adapter converting a legacy [Dungeon] into canonical [Project].
 */
fun Dungeon.toProject(): Project = Project(
    id = this.id,
    name = this.name,
    description = this.description,
    totalStages = this.totalStages,
    completedStages = this.completedStages,
    isCompleted = this.isCompleted,
    createdAt = this.createdAt,
    completedAt = this.completedAt,
    rarity = this.rarity,
    stageDescriptions = this.stageDescriptions,
    goalId = null
)

/**
 * Lossless compatibility adapter converting a canonical [Project] back into legacy [Dungeon].
 */
fun Project.toLegacyDungeon(): Dungeon = Dungeon(
    id = this.id,
    name = this.name,
    description = this.description,
    rarity = this.rarity,
    totalStages = this.totalStages,
    completedStages = this.completedStages,
    isBossDefeated = this.isCompleted,
    createdAt = this.createdAt,
    completedAt = this.completedAt,
    stageDescriptions = this.stageDescriptions
)
