package com.axiom.app.data

import kotlinx.coroutines.flow.Flow

/**
 * WP-202 seam: the narrow slice of preference state that bootstrap seeding reads and
 * writes. Extracted so [SeedDataHelper] can be exercised deterministically on the JVM
 * with a hand-written fake — the concrete [com.axiom.app.data.local.AxiomPreferences]
 * requires an Android Context + DataStore, which unit tests cannot construct.
 *
 * AxiomPreferences already implements every member; it is adapted to this interface via
 * an @Provides binding in AppModule (no changes to AxiomPreferences itself).
 */
interface SeedPreferences {
    val skillTreeSeededFlow: Flow<Boolean>
    suspend fun setSkillTreeSeeded(value: Boolean)

    val muscleGroupsSeededFlow: Flow<Boolean>
    suspend fun setMuscleGroupsSeeded(value: Boolean)

    val alirezaProfileSeededFlow: Flow<Boolean>
    suspend fun setAlirezaProfileSeeded(value: Boolean)

    val setupCompleteFlow: Flow<Boolean>
    suspend fun setSetupComplete()

    val firstMissionDoneFlow: Flow<Boolean>
    suspend fun setFirstMissionDone(value: Boolean)

    val blueprintSetupCompleteFlow: Flow<Boolean>
    suspend fun setBlueprintSetupComplete(value: Boolean)

    val financialModuleEnabledFlow: Flow<Boolean>
    suspend fun setFinancialModuleEnabled(value: Boolean)
}
