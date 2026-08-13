package com.axiom.app.navigation

import com.axiom.app.presentation.onboarding.EligibilitySnapshot
import com.axiom.app.presentation.onboarding.EligibilityState
import com.axiom.app.presentation.onboarding.EligibilityStateMachine
import com.axiom.app.presentation.onboarding.LaunchDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * WP-203 — end-to-end launch routing: EligibilityStateMachine → splashExitRoute.
 *
 * Deterministic and pure (no clock, no coroutine, no I/O). Models exactly what
 * [com.axiom.app.presentation.onboarding.SplashViewModel.resolveDestination]
 * does at runtime — evaluate the four-fact snapshot, take `.destination`, then
 * map it through the single-authority [splashExitRoute] (WP-202). Asserts the
 * navigation-visible half of the LOCKED contract (Decisions A–C, precedence).
 *
 * The `firstMissionDone` / `blueprintSetupComplete` args to [splashExitRoute]
 * are passed as their WP-202 stale-at-launch `false` values to prove they can
 * never override the resolved eligibility destination.
 */
class EligibilityRoutingNavigationTest {

    private fun route(s: Boolean, h: Boolean, f: Boolean, b: Boolean): String {
        val resolved = EligibilityStateMachine.evaluate(
            EligibilitySnapshot(
                setupComplete = s, hunterExists = h, firstMissionDone = f, blueprintSetupComplete = b,
            )
        ).destination
        // WP-202: stale flags (false) must not override the resolved destination.
        return splashExitRoute(resolved, firstMissionDone = false, blueprintSetupComplete = false)
    }

    // ---- Decision B (CRITICAL): completed flags + missing Hunter → recovery, never Home ----

    @Test
    fun missingHunterWithCompletedFlags_routesOnboarding_neverHome() {
        // (S=T, H=F, F=T, B=T) — the device-found null-Hunter Home shimmer case.
        assertEquals(Screen.Onboarding.route, route(true, false, true, true))
        assertNotEquals(Screen.Home.route, route(true, false, true, true))
    }

    @Test
    fun missingHunterWithAnyEarnedProgress_alwaysRoutesOnboarding() {
        assertEquals(Screen.Onboarding.route, route(true, false, false, true))
        assertEquals(Screen.Onboarding.route, route(true, false, true, false))
        assertEquals(Screen.Onboarding.route, route(true, false, true, true))
    }

    // ---- Precedence: a missing Hunter can NEVER reach Home, whatever the flags ----

    @Test
    fun missingHunter_neverRoutesHome_acrossAllFlagCombos() {
        for (f in listOf(false, true)) for (b in listOf(false, true)) {
            assertNotEquals(
                "missing hunter must never route Home",
                Screen.Home.route,
                route(true, false, f, b),
            )
        }
    }

    // ---- Established: setup + hunter + all flags → Home ----

    @Test
    fun establishedUser_routesHome() {
        val resolved = EligibilityStateMachine.evaluate(
            EligibilitySnapshot(true, true, true, true)
        )
        assertEquals(EligibilityState.ESTABLISHED, resolved.state)
        assertEquals(Screen.Home.route, route(true, true, true, true))
    }

    // ---- Fresh: setup + no hunter + no progress → Onboarding (Decision C) ----

    @Test
    fun freshUser_routesOnboarding() {
        assertEquals(Screen.Onboarding.route, route(true, false, false, false))
    }

    // ---- Missing setup dominates all downstream (precedence) → Setup ----

    @Test
    fun missingSetup_alwaysRoutesSetup() {
        for (h in listOf(false, true)) for (f in listOf(false, true)) for (b in listOf(false, true)) {
            assertEquals("setup missing must dominate", Screen.Setup.route, route(false, h, f, b))
        }
    }

    // ---- Intermediate progress maps to its own screen ----

    @Test
    fun hunterNoFirstMission_routesOnboarding() {
        assertEquals(Screen.Onboarding.route, route(true, true, false, false))
    }

    @Test
    fun hunterFirstMissionNoBlueprint_routesBlueprintWizard() {
        assertEquals(Screen.BlueprintWizard.route, route(true, true, true, false))
    }
}
