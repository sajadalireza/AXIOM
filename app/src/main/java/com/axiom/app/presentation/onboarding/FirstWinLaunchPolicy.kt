package com.axiom.app.presentation.onboarding

import com.axiom.app.domain.firstwin.FirstWinSessionStatus

/**
 * WP-207 — pure First-Win launch policy.
 *
 * Decides the final [LaunchDestination] by combining the existing WP-203
 * [EligibilityResult] with the durable First-Win session lifecycle status. It
 * NEVER modifies the [EligibilityStateMachine]; it only inserts [LaunchDestination.FIRST_WIN]
 * where the slice's new-user path applies and routes completed sessions to Home.
 *
 * Precedence (fail-closed):
 *  1. [EligibilityState.HUNTER_RECOVERY]             -> existing destination (a lost Hunter recovers in place)
 *  2. [EligibilityState.INVALID] (setup/order repair)-> existing destination
 *  3. NEEDS_HUNTER + COMPLETED session                 -> existing destination
 *       (WP-203 invariant: a missing Hunter never routes directly Home)
 *  4. session status == [FirstWinSessionStatus.COMPLETED] -> HOME
 *  5. any other (non-null) session status            -> FIRST_WIN (resume)
 *  6. no session && NEEDS_HUNTER || NEEDS_FIRST_MISSION -> FIRST_WIN (fresh assignment)
 *  7. otherwise                                      -> existing destination (NEEDS_SETUP / NEEDS_BLUEPRINT / ESTABLISHED)
 *
 * Pure: no clock, no I/O, no coroutine timing — the same inputs always yield the
 * same destination.
 */
object FirstWinLaunchPolicy {

    fun resolve(
        eligibility: EligibilityResult,
        firstWinSessionStatus: FirstWinSessionStatus?,
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

        // 4-5. A First-Win session exists (setup completed, not in recovery/repair):
        // a completed session is done (Home); any other session resumes First-Win.
        when (firstWinSessionStatus) {
            FirstWinSessionStatus.COMPLETED -> return LaunchDestination.HOME
            null -> Unit
            else -> return LaunchDestination.FIRST_WIN
        }

        // 6. No session — fresh First-Win assignment for first-win-eligible states.
        if (state == EligibilityState.NEEDS_HUNTER || state == EligibilityState.NEEDS_FIRST_MISSION) {
            return LaunchDestination.FIRST_WIN
        }

        // 7. Legacy setup / blueprint / established keep their existing destination.
        return eligibility.destination
    }
}
