package com.axiom.app.presentation.onboarding

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * WP-203 recovery-loop repair — production-bound regression for the post-Hunter
 * routing authority ([PostHunterRouteResolver]) that `AwakenNavGraph` uses after
 * a Hunter is (re)created.
 *
 * RED expectation (on `f6fca14`): the post-Hunter route is derived from stale
 * lifecycle defaults, so a completed recovery user is mis-routed to FIRST_MISSION
 * instead of HOME (replaying earned progress). These assertions fail until the
 * resolver re-reads authoritative persisted eligibility.
 *
 * Deterministic: pure snapshot suppliers under [runTest]; no wall-clock sleeps.
 */
class PostHunterRecoveryRoutingTest {

    private fun resolver(snapshot: EligibilitySnapshot) =
        PostHunterRouteResolver(readSnapshot = { snapshot })

    // ---- C: completed recovery — Hunter recreated, all flags preserved → HOME ----
    @Test
    fun completedRecovery_afterHunterRecreation_routesHome() = runTest {
        val afterRecreation = EligibilitySnapshot(
            setupComplete = true, hunterExists = true,
            firstMissionDone = true, blueprintSetupComplete = true,
        )
        assertEquals(PostHunterRoute.HOME, resolver(afterRecreation).resolve())
    }

    // ---- D/E: completed recovery never replays First Mission or Blueprint ----
    @Test
    fun completedRecovery_neverReplaysFirstMissionOrBlueprint() = runTest {
        val r = resolver(EligibilitySnapshot(true, true, true, true)).resolve()
        assertNotEquals(PostHunterRoute.FIRST_MISSION, r)
        assertNotEquals(PostHunterRoute.BLUEPRINT, r)
    }

    // ---- A: fresh Hunter creation → First Mission (must not over-correct to Home) ----
    @Test
    fun freshHunter_afterCreation_routesFirstMission() = runTest {
        assertEquals(
            PostHunterRoute.FIRST_MISSION,
            resolver(EligibilitySnapshot(true, true, false, false)).resolve(),
        )
    }

    // ---- B: resumed blueprint — Hunter recreated, mission done, blueprint not → BLUEPRINT ----
    @Test
    fun resumedBlueprint_afterHunterRecreation_routesBlueprint() = runTest {
        assertEquals(
            PostHunterRoute.BLUEPRINT,
            resolver(EligibilitySnapshot(true, true, true, false)).resolve(),
        )
    }

    // ---- authority: authoritative persisted truth, not stale initialValue=false ----
    @Test
    fun postHunterRoute_usesAuthoritativeState_notStaleDefaults() = runTest {
        assertEquals(
            PostHunterRoute.HOME,
            resolver(EligibilitySnapshot(true, true, true, true)).resolve(),
        )
    }

    // ---- F: streak safety — completed recovery never re-initializes streak ----
    @Test
    fun completedRecovery_doesNotInitializeStreak() {
        assertFalse(
            EligibilityStateMachine.shouldInitializeStreak(
                EligibilitySnapshot(true, true, true, true)
            )
        )
        assertFalse(
            EligibilityStateMachine.shouldInitializeStreak(
                EligibilitySnapshot(true, false, true, true)
            )
        )
    }
}
