package com.axiom.app.presentation.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WP-203 — eligibility state machine contract (RED → GREEN).
 *
 * Exhaustive, deterministic, clock-free. Asserts the LOCKED contract (Decisions
 * A–F) over all 16 combinations of the four facts. Against the RED skeleton
 * (Hunter-blind, silently normalizing) the defect-row assertions FAIL; the
 * GREEN implementation makes all pass.
 */
class EligibilityStateMachineTest {

    private fun snap(s: Boolean, h: Boolean, f: Boolean, b: Boolean) =
        EligibilitySnapshot(setupComplete = s, hunterExists = h, firstMissionDone = f, blueprintSetupComplete = b)

    private fun state(s: Boolean, h: Boolean, f: Boolean, b: Boolean) =
        EligibilityStateMachine.evaluate(snap(s, h, f, b)).state

    // ---- Full 16-state matrix (§4 / §27) ----

    @Test fun row01_freshInstall_needsSetup() =
        assertEquals(EligibilityState.NEEDS_SETUP, state(false, false, false, false))

    @Test fun row02_downstreamWithoutSetup_invalid() =
        assertEquals(EligibilityState.INVALID, state(false, false, false, true))

    @Test fun row03_downstreamWithoutSetup_invalid() =
        assertEquals(EligibilityState.INVALID, state(false, false, true, false))

    @Test fun row04_downstreamWithoutSetup_invalid() =
        assertEquals(EligibilityState.INVALID, state(false, false, true, true))

    @Test fun row05_hunterWithoutSetup_invalid() =
        assertEquals(EligibilityState.INVALID, state(false, true, false, false))

    @Test fun row06_hunterWithoutSetup_invalid() =
        assertEquals(EligibilityState.INVALID, state(false, true, false, true))

    @Test fun row07_hunterWithoutSetup_invalid() =
        assertEquals(EligibilityState.INVALID, state(false, true, true, false))

    @Test fun row08_hunterWithoutSetup_invalid() =
        assertEquals(EligibilityState.INVALID, state(false, true, true, true))

    @Test fun row09_freshHunter_needsHunter() =
        assertEquals(EligibilityState.NEEDS_HUNTER, state(true, false, false, false))

    @Test fun row10_missingHunterWithBlueprint_recovery() =
        assertEquals(EligibilityState.HUNTER_RECOVERY, state(true, false, false, true))

    @Test fun row11_missingHunterWithFirstMission_recovery() =
        assertEquals(EligibilityState.HUNTER_RECOVERY, state(true, false, true, false))

    // Decision B — THE critical defect row: completed flags + missing Hunter.
    @Test fun row12_completedFlagsMissingHunter_recovery_notEstablished() {
        val result = EligibilityStateMachine.evaluate(snap(true, false, true, true))
        assertEquals(EligibilityState.HUNTER_RECOVERY, result.state)
        assertNotEquals(EligibilityState.ESTABLISHED, result.state)
        assertNotEquals(LaunchDestination.HOME, result.destination)
    }

    @Test fun row13_hunterNoFirstMission_needsFirstMission() =
        assertEquals(EligibilityState.NEEDS_FIRST_MISSION, state(true, true, false, false))

    @Test fun row14_blueprintBeforeFirstMission_invalid() =
        assertEquals(EligibilityState.INVALID, state(true, true, false, true))

    @Test fun row15_hunterFirstMissionNoBlueprint_needsBlueprint() =
        assertEquals(EligibilityState.NEEDS_BLUEPRINT, state(true, true, true, false))

    @Test fun row16_allComplete_established() =
        assertEquals(EligibilityState.ESTABLISHED, state(true, true, true, true))

    // ---- Decision A: hunterExists never implies completion ----

    @Test fun decisionA_hunterExistenceNeverImpliesEstablished_whenFlagsIncomplete() {
        // Hunter present but downstream incomplete must never be ESTABLISHED.
        assertNotEquals(EligibilityState.ESTABLISHED, state(true, true, false, false))
        assertNotEquals(EligibilityState.ESTABLISHED, state(true, true, true, false))
    }

    @Test fun decisionA_establishedRequiresHunterAndAllFlags() {
        assertEquals(EligibilityState.ESTABLISHED, state(true, true, true, true))
        // Same completion flags, Hunter gone → must NOT be ESTABLISHED.
        assertNotEquals(EligibilityState.ESTABLISHED, state(true, false, true, true))
    }

    // ---- Decision B: recovery routes away from Home, is marked recovery ----

    @Test fun decisionB_recoveryIsMarkedRecovery_andRoutesOnboarding() {
        for (snapshot in listOf(
            snap(true, false, false, true),
            snap(true, false, true, false),
            snap(true, false, true, true),
        )) {
            val r = EligibilityStateMachine.evaluate(snapshot)
            assertEquals(EligibilityState.HUNTER_RECOVERY, r.state)
            assertTrue("recovery must be streak-safe flagged", r.isRecovery)
            assertEquals(LaunchDestination.ONBOARDING, r.destination)
        }
    }

    // ---- Decision C: fresh hunter creation is normal, not recovery ----

    @Test fun decisionC_freshHunter_isNotRecovery() {
        val r = EligibilityStateMachine.evaluate(snap(true, false, false, false))
        assertEquals(EligibilityState.NEEDS_HUNTER, r.state)
        assertFalse(r.isRecovery)
        assertEquals(LaunchDestination.ONBOARDING, r.destination)
    }

    // ---- Decision D: streak-init only for genuinely fresh ----

    @Test fun decisionD_streakInit_onlyForFreshNoEarnedProgress() {
        assertTrue(EligibilityStateMachine.shouldInitializeStreak(snap(true, false, false, false)))
        // Any earned downstream progress ⇒ preserve streak (recovery/resumed).
        assertFalse(EligibilityStateMachine.shouldInitializeStreak(snap(true, false, true, true)))
        assertFalse(EligibilityStateMachine.shouldInitializeStreak(snap(true, false, false, true)))
        assertFalse(EligibilityStateMachine.shouldInitializeStreak(snap(true, false, true, false)))
        assertFalse(EligibilityStateMachine.shouldInitializeStreak(snap(true, true, true, true)))
    }

    // ---- Decision E: impossible states are INVALID with bounded reason, non-destructive ----

    @Test fun decisionE_invalidReasons_areBoundedAndRecoverToEarliestPrerequisite() {
        // downstream without setup → recover toward SETUP, keep flags (not deleted here).
        EligibilityStateMachine.evaluate(snap(false, false, true, true)).let {
            assertEquals(EligibilityState.INVALID, it.state)
            assertEquals(InvalidReason.DOWNSTREAM_WITHOUT_SETUP, it.reason)
            assertEquals(LaunchDestination.SETUP, it.destination)
        }
        // hunter without setup → recover toward SETUP.
        EligibilityStateMachine.evaluate(snap(false, true, false, false)).let {
            assertEquals(EligibilityState.INVALID, it.state)
            assertEquals(InvalidReason.HUNTER_WITHOUT_SETUP, it.reason)
            assertEquals(LaunchDestination.SETUP, it.destination)
        }
        // blueprint before first mission → recover toward first mission (Onboarding), keep B.
        EligibilityStateMachine.evaluate(snap(true, true, false, true)).let {
            assertEquals(EligibilityState.INVALID, it.state)
            assertEquals(InvalidReason.BLUEPRINT_BEFORE_FIRST_MISSION, it.reason)
            assertEquals(LaunchDestination.ONBOARDING, it.destination)
        }
    }

    // ---- Determinism: same facts ⇒ same result, evaluated repeatedly ----

    @Test fun determinism_sameSnapshotAlwaysSameResult() {
        for (i in 0 until 16) {
            val s = (i and 1) != 0
            val h = (i and 2) != 0
            val f = (i and 4) != 0
            val b = (i and 8) != 0
            val first = EligibilityStateMachine.evaluate(snap(s, h, f, b))
            repeat(5) { assertEquals(first, EligibilityStateMachine.evaluate(snap(s, h, f, b))) }
        }
    }

    // ---- Precedence: a later completion never hides a missing earlier prerequisite ----

    @Test fun precedence_missingSetupDominatesAllDownstream() {
        // With setup=false, no downstream flag can produce a downstream/home destination.
        for (h in listOf(false, true)) for (f in listOf(false, true)) for (b in listOf(false, true)) {
            val dest = EligibilityStateMachine.evaluate(snap(false, h, f, b)).destination
            assertEquals("setup missing must dominate", LaunchDestination.SETUP, dest)
        }
    }

    @Test fun precedence_missingHunterNeverReachesHome() {
        for (f in listOf(false, true)) for (b in listOf(false, true)) {
            val dest = EligibilityStateMachine.evaluate(snap(true, false, f, b)).destination
            assertNotEquals("missing hunter must never route Home", LaunchDestination.HOME, dest)
        }
    }
}
