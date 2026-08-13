package com.axiom.app.navigation

import com.axiom.app.presentation.onboarding.LaunchDestination

/**
 * Single source of truth for the one-shot Splash-exit navigation route.
 *
 * WP-202 routing repair (PROMPT 30): the destination produced by
 * [com.axiom.app.presentation.onboarding.LaunchRouteResolver] must be the sole
 * authority for where Splash navigates on exit.
 *
 * ⚠ DEVICE-FOUND DEFECT (cd5f413) — reproduced verbatim below:
 * On cold relaunch of a completed, established user the resolver correctly
 * resolves [LaunchDestination.HOME], but [AwakenNavGraph] discarded that result
 * and recomputed the exit route from `firstMissionDone` / `blueprintSetupComplete`
 * collected via `collectAsStateWithLifecycle(initialValue = false)`. When the
 * splash-exit fires before those flows emit the persisted `true`, the stale
 * initial `false` values classify the completed user as ONBOARDING, and the
 * one-shot navigation locks that wrong destination — re-onboarding the user.
 *
 * GREEN repair (WP-202 PROMPT 30): [resolved] is now the SOLE authority for the
 * one-shot Splash-exit route. The [firstMissionDone] / [blueprintSetupComplete]
 * flags — collected at the NavGraph level via
 * `collectAsStateWithLifecycle(initialValue = false)` — are accepted for call-site
 * honesty (they are the stale-at-launch values that used to override the route)
 * but are deliberately NOT consulted: the resolver's [LaunchDestination] can never
 * again be replaced by their initial `false` values. This closes the WP-201
 * launch-race at the Splash → NavGraph handoff.
 */
@Suppress("UNUSED_PARAMETER")
fun splashExitRoute(
    resolved: LaunchDestination,
    firstMissionDone: Boolean,
    blueprintSetupComplete: Boolean,
): String = when (resolved) {
    LaunchDestination.SETUP -> Screen.Setup.route
    LaunchDestination.ONBOARDING -> Screen.Onboarding.route
    LaunchDestination.BLUEPRINT_WIZARD -> Screen.BlueprintWizard.route
    LaunchDestination.HOME -> Screen.Home.route
}
