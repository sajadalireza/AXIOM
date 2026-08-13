package com.axiom.app.data

import com.axiom.app.domain.model.*
import com.axiom.app.domain.repository.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Neutral fresh-install seeder (WP-202). Seeds only non-personal reference catalogs.
 *
 * [warriorProfileRepository] and [hunterRepository] are intentionally retained even though
 * the neutral bootstrap does not write to them: they are the observation surface for the
 * [com.axiom.app.data.NeutralBootstrapTest] neutrality regression, which asserts that a
 * fresh bootstrap leaves the hunter profile and every personal blueprint collection empty.
 * Any future re-introduction of personal seeding here trips that test.
 */
@Singleton
class SeedDataHelper @Inject constructor(
    private val skillRepository: SkillRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val warriorProfileRepository: WarriorProfileRepository,
    private val hunterRepository: HunterRepository,
    private val preferences: SeedPreferences
) {
    private fun cleanId(name: String): String {
        return name.lowercase().trim()
            .replace("&", "and")
            .replace("-", "_")
            .replace("/", "_")
            .replace("(", "")
            .replace(")", "")
            .replace("  ", " ")
            .replace(" ", "_")
            .replace("__", "_")
    }

    suspend fun seedSkillsIfNeeded() {
        val seeded = preferences.skillTreeSeededFlow.first()
        if (seeded) return

        // 1. Insert 6 parent skills (isUnlocked=true) — generic, domain-neutral names,
        // matching InitializeAxiomUseCase's starter skill set exactly (same names ->
        // same generated ids) so this doesn't create duplicate/orphaned skill rows.
        val deepWorkParent = createParentSkill("Deep Work", "Deep Work", "capability")
        val creativeOutputParent = createParentSkill("Creative Output", "Creative Output", "capability")
        val communicationParent = createParentSkill("Communication", "Communication", "capability")
        val commercialParent = createParentSkill("Income & Commercial Intelligence", "Income & Commercial Intelligence", "commercial_intelligence")
        val personalGrowthParent = createParentSkill("Personal Growth", "Personal Growth", "capability")
        val physicalParent = createParentSkill("Physical Mastery", "Physical Mastery", "capability")

        val parents = listOf(deepWorkParent, creativeOutputParent, communicationParent, commercialParent, personalGrowthParent, physicalParent)
        for (parent in parents) {
            skillRepository.insertSkill(parent)
        }

        // 2. Insert child skills (isUnlocked=false)
        val children = listOf(
            // Deep Work children
            createChildSkill("Focus Session Habit", "Deep Work", deepWorkParent.id, "capability"),
            createChildSkill("Time Blocking", "Deep Work", deepWorkParent.id, "capability"),
            createChildSkill("Distraction-Free Environment", "Deep Work", deepWorkParent.id, "capability"),

            // Creative Output children
            createChildSkill("Idea Journal", "Creative Output", creativeOutputParent.id, "capability"),
            createChildSkill("Portfolio Building", "Creative Output", creativeOutputParent.id, "capability"),
            createChildSkill("Public Sharing", "Creative Output", creativeOutputParent.id, "capability"),

            // Communication children
            createChildSkill("Written Communication", "Communication", communicationParent.id, "capability"),
            createChildSkill("Public Speaking", "Communication", communicationParent.id, "capability"),
            createChildSkill("Active Listening", "Communication", communicationParent.id, "capability"),

            // Income & Commercial Intelligence children
            createChildSkill("Problem Discovery Conversations", "Income & Commercial Intelligence", commercialParent.id, "commercial_intelligence"),
            createChildSkill("Consulting Delivery", "Income & Commercial Intelligence", commercialParent.id, "commercial_intelligence"),
            createChildSkill("Monthly Income Diagnostic", "Income & Commercial Intelligence", commercialParent.id, "commercial_intelligence"),

            // Personal Growth children
            createChildSkill("Goal Setting Review", "Personal Growth", personalGrowthParent.id, "capability"),
            createChildSkill("Habit Tracking", "Personal Growth", personalGrowthParent.id, "capability"),
            createChildSkill("Reflection Practice", "Personal Growth", personalGrowthParent.id, "capability"),

            // Physical Mastery children
            createChildSkill("Strength Training", "Physical Mastery", physicalParent.id, "capability"),
            createChildSkill("Sleep Quality", "Physical Mastery", physicalParent.id, "capability"),
            createChildSkill("Daily Hygiene", "Physical Mastery", physicalParent.id, "capability")
        )

        for (child in children) {
            skillRepository.insertSkill(child)
        }

        // Mark as seeded
        preferences.setSkillTreeSeeded(true)
    }

    private fun createParentSkill(name: String, category: String, trackId: String? = null): Skill {
        val skillId = "skill_${cleanId(name)}"
        return Skill(
            id = skillId,
            name = name,
            category = category,
            currentXP = 0L,
            level = 1,
            rankLabel = "E-Rank",
            parentId = null,
            isUnlocked = true,
            xpToNextRank = 100L,
            rankProgressPercent = 0.0f,
            isShadowCandidate = false,
            rankColor = 0xFF9E9E9E,
            trackId = trackId
        )
    }

    private fun createChildSkill(name: String, category: String, parentId: String, trackId: String? = null): Skill {
        val skillId = "skill_${cleanId(name)}"
        return Skill(
            id = skillId,
            name = name,
            category = category,
            currentXP = 0L,
            level = 1,
            rankLabel = "E-Rank",
            parentId = parentId,
            isUnlocked = true,
            xpToNextRank = 100L,
            rankProgressPercent = 0.0f,
            isShadowCandidate = false,
            rankColor = 0xFF9E9E9E,
            trackId = trackId
        )
    }

    suspend fun seedMuscleGroupsIfNeeded() {
        val seeded = preferences.muscleGroupsSeededFlow.first()
        if (seeded) return

        val muscleGroups = listOf(
            MuscleGroup("chest", "Chest", 0, null, 100),
            MuscleGroup("back", "Back", 0, null, 100),
            MuscleGroup("shoulders", "Shoulders", 0, null, 100),
            MuscleGroup("biceps", "Biceps", 0, null, 100),
            MuscleGroup("triceps", "Triceps", 0, null, 100),
            MuscleGroup("legs", "Legs", 0, null, 100),
            MuscleGroup("core", "Core", 0, null, 100),
            MuscleGroup("forearms", "Forearms", 0, null, 100)
        )

        muscleGroupRepository.insertMuscleGroups(muscleGroups)
        preferences.setMuscleGroupsSeeded(true)
    }

    /**
     * WP-202 (PO Decisions 1–5): fresh-install bootstrap. Seeds ONLY neutral reference
     * catalogs (skill taxonomy + muscle groups); it must never seed personal blueprint
     * content (thesis, tracks, KPIs, iron rules, hard truths, affirmations, schedules,
     * milestones, named relationships) nor fake user-earned flags (setupComplete,
     * financialModuleEnabled, blueprintSetupComplete, firstMissionDone).
     *
     * The Hunter profile and starter content are created later by the user-triggered
     * onboarding flow ([com.axiom.app.domain.usecase.InitializeAxiomUseCase]); seeding a
     * Hunter here would trip that use case's `existingProfile != null` early-return and
     * break the first-win starter missions/dungeon.
     *
     * Existing users are unaffected: each catalog seeder self-guards on its own
     * `*_SEEDED` flag, so an established install performs no writes and loses no data.
     *
     * The former personal payload now lives only in the `src/test` dev fixture
     * `PersonalBlueprintTestFixture` and never ships or auto-executes.
     */
    suspend fun seedReferenceCatalogsIfNeeded() {
        seedSkillsIfNeeded()
        seedMuscleGroupsIfNeeded()
    }
}
