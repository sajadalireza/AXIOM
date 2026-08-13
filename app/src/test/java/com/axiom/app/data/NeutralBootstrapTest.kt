package com.axiom.app.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * WP-202 fresh-install neutrality regression.
 *
 * Product Owner Decision 1: a fresh install may contain ONLY minimal non-personal state
 * — reference/catalog data (skills, muscle groups) and technical defaults. Personal seed
 * data on a fresh install must be exactly 0: no personal thesis, tracks/pillars, KPIs,
 * iron rules, hard truths, affirmations, personal schedules, milestones, named
 * relationships, or auto-completed setup/entitlement flags.
 *
 * RED expectation (personal bootstrap still in place): TESTS A, B, C, E FAIL because
 * [SeedDataHelper.seedDefaultProfileIfNeeded] seeds a personal Warrior profile, blueprint
 * collections, and fakes setupComplete/financial flags. TESTS D and F pass in both states
 * (reference catalogs present; existing-user data preserved).
 */
class NeutralBootstrapTest {

    private fun newHelper(
        prefs: FakeSeedPreferences,
        hunter: FakeHunterRepository = FakeHunterRepository(),
        skills: FakeSkillRepository = FakeSkillRepository(),
        muscles: FakeMuscleGroupRepository = FakeMuscleGroupRepository(),
        warrior: FakeWarriorProfileRepository = FakeWarriorProfileRepository()
    ) = SeedDataHelper(skills, muscles, warrior, hunter, prefs)

    // === TEST A — fresh install seeds NO hunter profile (personal identity = 0). ===
    @Test
    fun freshInstall_seedsNoHunterProfile() = runTest {
        val hunter = FakeHunterRepository()
        val helper = newHelper(FakeSeedPreferences(), hunter = hunter)

        helper.seedDefaultProfileIfNeeded()

        assertNull("Fresh bootstrap must not seed a hunter profile", hunter.profile)
    }

    // === TEST B — fresh install seeds NO personal blueprint collections. ===
    @Test
    fun freshInstall_seedsNoPersonalBlueprint() = runTest {
        val warrior = FakeWarriorProfileRepository()
        val helper = newHelper(FakeSeedPreferences(), warrior = warrior)

        helper.seedDefaultProfileIfNeeded()

        assertEquals("tracks", 0, warrior.getAllTracks().size)
        assertEquals("scheduleBlocks", 0, warrior.getAllScheduleBlocks().size)
        assertEquals("customKPIs", 0, warrior.getAllCustomKPIs().size)
        assertEquals("ironRules", 0, warrior.getAllIronRules().size)
        assertEquals("hardTruthsOrAffirmations", 0, warrior.getAllHardTruthsOrAffirmations().size)
        assertEquals("majorMilestones", 0, warrior.getAllMajorMilestones().size)
        assertEquals("keyRelationships", 0, warrior.getAllKeyRelationships().size)
    }

    // === TEST C — fresh install does NOT fake completion / entitlement flags. ===
    @Test
    fun freshInstall_doesNotFakeCompletionFlags() = runTest {
        val prefs = FakeSeedPreferences()
        val helper = newHelper(prefs)

        helper.seedDefaultProfileIfNeeded()

        assertFalse("setupComplete must be user-earned", prefs.setupComplete)
        assertFalse("firstMissionDone must be user-earned", prefs.firstMissionDone)
        assertFalse("blueprintSetupComplete must be user-earned", prefs.blueprintSetupComplete)
        assertFalse("financialModuleEnabled must default false", prefs.financialModuleEnabled)
    }

    // === TEST D — fresh install DOES seed neutral reference catalogs. ===
    @Test
    fun freshInstall_seedsReferenceCatalogs() = runTest {
        val skills = FakeSkillRepository()
        val muscles = FakeMuscleGroupRepository()
        val helper = newHelper(FakeSeedPreferences(), skills = skills, muscles = muscles)

        helper.seedDefaultProfileIfNeeded()

        assertEquals("skill catalog (6 parents + 18 children)", 24, skills.getAllSkills().first().size)
        assertEquals("muscle group catalog", 8, muscles.getAllMuscleGroups().first().size)
    }

    // === TEST E — fresh install saves NO personal WarriorProfile record. ===
    @Test
    fun freshInstall_seedsNoWarriorProfileRecord() = runTest {
        val warrior = FakeWarriorProfileRepository()
        val helper = newHelper(FakeSeedPreferences(), warrior = warrior)

        helper.seedDefaultProfileIfNeeded()

        assertNull("Fresh bootstrap must not seed the personal WarriorProfile", warrior.getProfile("default"))
    }

    // === TEST F — existing user's data is preserved (no wipe, no rollback). ===
    @Test
    fun existingUser_dataIsPreserved() = runTest {
        val prefs = FakeSeedPreferences(
            skillTreeSeeded = true,
            muscleGroupsSeeded = true,
            alirezaProfileSeeded = true
        )
        val warrior = FakeWarriorProfileRepository().apply {
            saveMajorMilestone(PersonalBlueprintTestFixture.MAJOR_MILESTONE)
            PersonalBlueprintTestFixture.KEY_RELATIONSHIPS.forEach { saveKeyRelationship(it) }
            setFinancialModuleEnabled(true)
        }

        val helper = newHelper(prefs, warrior = warrior)
        helper.seedDefaultProfileIfNeeded()

        assertEquals("existing milestones preserved", 1, warrior.getAllMajorMilestones().size)
        assertEquals("existing relationships preserved", 4, warrior.getAllKeyRelationships().size)
        assertEquals("existing financial toggle preserved", true, warrior.isFinancialModuleEnabledFlow().first())
    }
}
