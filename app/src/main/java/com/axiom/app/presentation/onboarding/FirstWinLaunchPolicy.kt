package com.axiom.app.presentation.onboarding

import com.axiom.app.domain.firstwin.FirstWinSessionStatus

/**
 * WP-207/WP-208 — pure First-Win launch policy with Kill Switch & Rollback control plane.
 *
 * Decides the final [LaunchDestination] by combining the existing WP-203
 * [EligibilityResult] with the durable First-Win session lifecycle status and the
 * WP-208 [isTreatmentActive] control gate.
 *
 * Precedence (fail-closed):
 *  1. [EligibilityState.HUNTER_RECOVERY]             -> existing destination (a lost Hunter recovers in place)
 *  2. [EligibilityState.INVALID] (setup/order repair)-> existing destination
 *  3. NEEDS_HUNTER + COMPLETED session                 -> existing destination
 *       (WP-203 invariant: a missing Hunter never routes directly Home)
 *  4. session status == [FirstWinSessionStatus.COMPLETED] -> HOME
 *       (WP-208 MUST-ACCEPTANCE: switching off treatment NEVER throws a completed user back
 *        into onboarding; durable Room completion dominates regardless of treatment flag)
 *  5. any other or unknown status on an existing session:
 *       - if [isTreatmentActive] == true             -> FIRST_WIN (resumes at exact position)
 *       - if [isTreatmentActive] == false            -> existing destination (bounded legacy fallback)
 *  6. no session && (NEEDS_HUNTER || NEEDS_FIRST_MISSION):
 *       - if [isTreatmentActive] == true             -> FIRST_WIN (fresh assignment)
 *       - if [isTreatmentActive] == false            -> existing destination (bounded legacy fallback: ONBOARDING)
 *  7. otherwise                                      -> existing destination (NEEDS_SETUP / NEEDS_BLUEPRINT / ESTABLISHED)
 *
 * Pure: no clock, no I/O, no coroutine timing — the same inputs always yield the
 * same destination.
 */
object FirstWinLaunchPolicy {

    fun resolve(
        eligibility: EligibilityResult,
        firstWinSessionStatus: FirstWinSessionStatus?,
        firstWinSessionExists: Boolean = firstWinSessionStatus != null,
        isTreatmentActive: Boolean = true,
    ): LaunchDestination {
        val state = eligibility.state

        // 1-2. Recovery and repair dominate: a missing Hunter or an inconsistent
        // ordering must recover in place and never skip into the First-Win flow.
        if (state == EligibilityState.HUNTER_RECOVERY) return eligibility.destination
        if (state == EligibilityState.INVALID) return eligibility.destination

        // 3. A completed First-Win session is strong durable progress evidence, but it
        // must never violate WP-203's missing-Hunter invariant. Recreate/recover the
        // prerequisite Hunter first rather than routing a null-Hunter state to Home.
        if (state == EligibilityState.NEEDS_HUNTER && firstWinSessionStatus == FirstWinSessionStatus.COMPLETED) {
            return eligibility.destination
        }

        // 4. A completed First-Win session is finished (Home).
        // WP-208 invariant: switching off treatment never throws a completed user back
        // into onboarding (No duplicate onboarding / zero data loss).
        if (firstWinSessionStatus == FirstWinSessionStatus.COMPLETED) {
            return LaunchDestination.HOME
        }

        // 5. An existing in-progress session (setup completed, not recovery/repair):
        // If treatment is active, resume First-Win. If treatment is disabled (killed/control),
        // fall back cleanly to the legacy destination without deleting session data.
        if (firstWinSessionExists || firstWinSessionStatus != null) {
            return if (isTreatmentActive) {
                LaunchDestination.FIRST_WIN
            } else {
                eligibility.destination
            }
        }

        // 6. No session — fresh First-Win assignment for first-win-eligible states if treatment is active.
        // If treatment is inactive/killed, fall back to legacy destination (ONBOARDING).
        if (state == EligibilityState.NEEDS_HUNTER || state == EligibilityState.NEEDS_FIRST_MISSION) {
            return if (isTreatmentActive) {
                LaunchDestination.FIRST_WIN
            } else {
                eligibility.destination
            }
        }

        // 7. Legacy setup / blueprint / established keep their existing destination.
        return eligibility.destination
    }
}
