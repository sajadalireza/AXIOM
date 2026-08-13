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
 * This function currently mirrors that buggy authority (the [resolved] argument
 * is intentionally IGNORED) so a deterministic JVM test can pin the defect.
 * The GREEN repair makes [resolved] authoritative here.
 */
fun splashExitRoute(
    resolved: LaunchDestination,
    firstMissionDone: Boolean,
    blueprintSetupComplete: Boolean,
): String = when {
    // BUG: 'resolved' ignored; route recomputed from stale-at-launch flags.
    !firstMissionDone -> Screen.Onboarding.route
    !blueprintSetupComplete -> Screen.BlueprintWizard.route
    else -> Screen.Home.route
}
