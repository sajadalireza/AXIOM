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
 * Pins the fresh-install neutrality contract for [SeedDataHelper.seedReferenceCatalogsIfNeeded]:
 * it seeds reference catalogs (skills, muscle groups) and nothing else. Personal seed data
 * on a fresh install must be exactly 0 — no hunter profile, WarriorProfile, tracks/pillars,
 * KPIs, iron rules, hard truths, affirmations, schedules, milestones, named relationships,
 * or auto-completed setup/entitlement flags.
 *
 * TESTS A, B, C, E assert the absence of personal seed data; TESTS D and F assert the
 * present-and-preserved invariants (reference catalogs seeded; existing-user data intact).
 * (These four were the RED-failing assertions before the bootstrap was neutralized.)
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

        helper.seedReferenceCatalogsIfNeeded()

        assertNull("Fresh bootstrap must not seed a hunter profile", hunter.profile)
    }

    // === TEST B — fresh install seeds NO personal blueprint collections. ===
    @Test
    fun freshInstall_seedsNoPersonalBlueprint() = runTest {
        val warrior = FakeWarriorProfileRepository()
        val helper = newHelper(FakeSeedPreferences(), warrior = warrior)

        helper.seedReferenceCatalogsIfNeeded()

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

        helper.seedReferenceCatalogsIfNeeded()

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

        helper.seedReferenceCatalogsIfNeeded()

        assertEquals("skill catalog (6 parents + 18 children)", 24, skills.getAllSkills().first().size)
        assertEquals("muscle group catalog", 8, muscles.getAllMuscleGroups().first().size)
    }

    // === TEST E — fresh install saves NO personal WarriorProfile record. ===
    @Test
    fun freshInstall_seedsNoWarriorProfileRecord() = runTest {
        val warrior = FakeWarriorProfileRepository()
        val helper = newHelper(FakeSeedPreferences(), warrior = warrior)

        helper.seedReferenceCatalogsIfNeeded()

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
        helper.seedReferenceCatalogsIfNeeded()

        assertEquals("existing milestones preserved", 1, warrior.getAllMajorMilestones().size)
        assertEquals("existing relationships preserved", 4, warrior.getAllKeyRelationships().size)
        assertEquals("existing financial toggle preserved", true, warrior.isFinancialModuleEnabledFlow().first())
    }

    // === TEST G — personal data survives an ACTIVE catalog seed (non-tautological upgrade path). ===
    // TEST F pre-seeds the catalog flags, so its seeders early-return and the whole call is a
    // no-op ("data preserved after a no-op" is trivially true). This test instead leaves the
    // catalog flags FALSE so BOTH seeders actually execute against a store that already holds
    // personal blueprint data — the real upgrade path — and asserts the catalogs get seeded
    // WHILE the personal data is left untouched. This is the assertion that would actually fail
    // if the neutral bootstrap ever wrote to (or wiped) the personal repositories.
    @Test
    fun existingUser_personalDataSurvivesActiveCatalogSeed() = runTest {
        val prefs = FakeSeedPreferences() // skillTreeSeeded=false, muscleGroupsSeeded=false → seeders run
        val skills = FakeSkillRepository()
        val muscles = FakeMuscleGroupRepository()
        val warrior = FakeWarriorProfileRepository().apply {
            saveMajorMilestone(PersonalBlueprintTestFixture.MAJOR_MILESTONE)
            PersonalBlueprintTestFixture.KEY_RELATIONSHIPS.forEach { saveKeyRelationship(it) }
            setFinancialModuleEnabled(true)
        }

        val helper = newHelper(prefs, skills = skills, muscles = muscles, warrior = warrior)
        helper.seedReferenceCatalogsIfNeeded()

        // Seeders actually ran (proves this is NOT a no-op like TEST F)...
        assertEquals("catalog actively seeded", 24, skills.getAllSkills().first().size)
        assertEquals("muscle catalog actively seeded", 8, muscles.getAllMuscleGroups().first().size)
        // ...yet personal blueprint data is left completely intact.
        assertEquals("existing milestones preserved", 1, warrior.getAllMajorMilestones().size)
        assertEquals("existing relationships preserved", 4, warrior.getAllKeyRelationships().size)
        assertEquals("existing financial toggle preserved", true, warrior.isFinancialModuleEnabledFlow().first())
    }
}
