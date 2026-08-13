package com.axiom.app.navigation

import com.axiom.app.presentation.onboarding.LaunchClassifier
import com.axiom.app.presentation.onboarding.LaunchDestination
import com.axiom.app.presentation.onboarding.LaunchInputs
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * WP-202 routing-repair regression (PROMPT 30) — device-found cold-relaunch defect.
 *
 * Deterministic and pure: asserts that the one-shot Splash-exit route is a function
 * of the authoritative [LaunchDestination] produced by [LaunchClassifier], and can
 * NEVER be overridden by separately-collected launch flags sitting at their
 * `collectAsStateWithLifecycle(initialValue = false)` initial values.
 *
 * No Thread.sleep, no wall-clock, no timing — the "stale flags" are passed
 * explicitly as `false` to model the exact device-found race window.
 *
 * RED (cd5f413): [splashExitRoute] ignores `resolved` and recomputes from the
 * stale flags, so an established completed user (resolver = HOME) is routed to
 * ONBOARDING → these HOME/SETUP/BLUEPRINT assertions FAIL.
 */
class SplashExitRouteRegressionTest {

    // §9 LOAD-BEARING: established completed user, resolver = HOME, but the
    // lifecycle-collected flags are still at their initial `false`. The exit
    // route MUST be Home — the authoritative resolution must not be replaced.
    @Test
    fun establishedColdRelaunch_resolvesHome_andRoutesHome_despiteStaleFalseFlags() {
        val resolved = LaunchClassifier.classify(
            LaunchInputs(setupComplete = true, firstMissionDone = true, blueprintSetupComplete = true)
        )
        assertEquals(LaunchDestination.HOME, resolved)
        assertEquals(
            "authoritative HOME must not be replaced by stale ONBOARDING",
            Screen.Home.route,
            splashExitRoute(resolved, firstMissionDone = false, blueprintSetupComplete = false)
        )
    }

    // §10 fresh-user guard: resolved ONBOARDING must reach Onboarding.
    @Test
    fun freshUser_resolvedOnboarding_routesOnboarding() {
        val resolved = LaunchClassifier.classify(
            LaunchInputs(setupComplete = true, firstMissionDone = false, blueprintSetupComplete = false)
        )
        assertEquals(LaunchDestination.ONBOARDING, resolved)
        assertEquals(
            Screen.Onboarding.route,
            splashExitRoute(resolved, firstMissionDone = false, blueprintSetupComplete = false)
        )
    }

    // §8 route mapping: every resolved destination maps to its own route,
    // independent of the (stale) collected flags.
    @Test
    fun resolvedSetup_routesSetup_regardlessOfFlags() {
        assertEquals(Screen.Setup.route, splashExitRoute(LaunchDestination.SETUP, false, false))
        assertEquals(Screen.Setup.route, splashExitRoute(LaunchDestination.SETUP, true, true))
    }

    @Test
    fun resolvedBlueprint_routesBlueprint_regardlessOfFlags() {
        assertEquals(
            Screen.BlueprintWizard.route,
            splashExitRoute(LaunchDestination.BLUEPRINT_WIZARD, firstMissionDone = false, blueprintSetupComplete = false)
        )
    }

    @Test
    fun resolvedHome_routesHome_regardlessOfFlags() {
        assertEquals(Screen.Home.route, splashExitRoute(LaunchDestination.HOME, false, false))
        assertEquals(Screen.Home.route, splashExitRoute(LaunchDestination.HOME, true, true))
    }
}
