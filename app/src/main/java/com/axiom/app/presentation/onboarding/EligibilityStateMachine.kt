package com.axiom.app.presentation.onboarding

/**
 * WP-203 — First-Win eligibility state machine (contract-driven routing).
 *
 * Single source of truth for classifying a user into an explicit eligibility
 * state from four authoritative startup facts, then mapping that state to the
 * existing [LaunchDestination] navigation authority (WP-201/WP-202 preserved).
 *
 * Design contract (LOCKED product-owner decisions):
 *  - A: [EligibilitySnapshot.hunterExists] is a prerequisite/ENTITY fact, never
 *       evidence of completion. `hunterExists ⇒ completion` is FORBIDDEN.
 *  - B: completion flags present but Hunter missing ⇒ [HUNTER_RECOVERY]
 *       (never HOME, never destructive). User recreates the Hunter; earned
 *       progress is preserved; re-evaluation then normally yields ESTABLISHED.
 *  - C: setup done + Hunter absent + no downstream progress ⇒ [NEEDS_HUNTER].
 *  - D: recovery/resumed users must never be re-zeroed; streak-init is for
 *       genuinely fresh users only (handled at the write boundary via
 *       [shouldInitializeStreak]).
 *  - E: impossible-order combinations are classified [INVALID] with a bounded
 *       [InvalidReason] and recovered toward the earliest missing prerequisite
 *       WITHOUT deleting later earned flags.
 *  - F: no destructive repair — this layer only classifies and routes.
 *
 * Pure and deterministic: no clock, no I/O, no coroutine timing. This layer
 * only CLASSIFIES and maps to an existing route — it never mutates Room or
 * DataStore, never resets progress, never fabricates completion (Decision F).
 * No eligibility state is persisted (that belongs to WP-204); the result is
 * recomputed from authoritative facts on every launch.
 */
enum class EligibilityState {
    NEEDS_SETUP,
    NEEDS_HUNTER,
    HUNTER_RECOVERY,
    NEEDS_FIRST_MISSION,
    NEEDS_BLUEPRINT,
    ESTABLISHED,
    INVALID,
}

/** Bounded set of reasons an [EligibilityState.INVALID] classification can carry. */
enum class InvalidReason {
    NONE,
    DOWNSTREAM_WITHOUT_SETUP,
    HUNTER_WITHOUT_SETUP,
    BLUEPRINT_BEFORE_FIRST_MISSION,
}

/** The four authoritative startup facts. `hunterExists` is the WP-203 addition. */
data class EligibilitySnapshot(
    val setupComplete: Boolean,
    val hunterExists: Boolean,
    val firstMissionDone: Boolean,
    val blueprintSetupComplete: Boolean,
)

/**
 * Classification result. [destination] is the existing navigation authority the
 * state maps to; [isRecovery] marks a streak-safe re-entry (HUNTER_RECOVERY).
 */
data class EligibilityResult(
    val state: EligibilityState,
    val reason: InvalidReason,
    val destination: LaunchDestination,
    val isRecovery: Boolean,
)

object EligibilityStateMachine {

    fun evaluate(snapshot: EligibilitySnapshot): EligibilityResult {
        val (setup, hunter, firstMission, blueprint) = snapshot

        // Prerequisite 1 — setup. Nothing downstream is legitimate without it.
        // Later flags are NOT deleted (Decision E/F); we recover toward SETUP and
        // carry a bounded reason so the impossible order is explicit, not silent.
        if (!setup) {
            val reason = when {
                hunter -> InvalidReason.HUNTER_WITHOUT_SETUP
                firstMission || blueprint -> InvalidReason.DOWNSTREAM_WITHOUT_SETUP
                else -> InvalidReason.NONE
            }
            val state = if (reason == InvalidReason.NONE) {
                EligibilityState.NEEDS_SETUP
            } else {
                EligibilityState.INVALID
            }
            return EligibilityResult(state, reason, LaunchDestination.SETUP, isRecovery = false)
        }

        // Prerequisite 2 — Hunter entity. `hunterExists` is a prerequisite, never
        // evidence of completion (Decision A): a missing Hunter can never route Home.
        if (!hunter) {
            return if (firstMission || blueprint) {
                // Earned progress but Hunter gone (Decision B): explicit, streak-safe
                // recovery — never HOME, never destructive. Re-eval after recreation
                // normally yields ESTABLISHED.
                EligibilityResult(
                    EligibilityState.HUNTER_RECOVERY,
                    InvalidReason.NONE,
                    LaunchDestination.ONBOARDING,
                    isRecovery = true,
                )
            } else {
                // Genuinely fresh Hunter creation (Decision C): normal, not recovery.
                EligibilityResult(
                    EligibilityState.NEEDS_HUNTER,
                    InvalidReason.NONE,
                    LaunchDestination.ONBOARDING,
                    isRecovery = false,
                )
            }
        }

        // Setup + Hunter present — classify by downstream earned progress.
        return when {
            !firstMission && !blueprint -> EligibilityResult(
                EligibilityState.NEEDS_FIRST_MISSION,
                InvalidReason.NONE,
                LaunchDestination.ONBOARDING,
                isRecovery = false,
            )
            // Blueprint before first mission is an impossible order (Decision E):
            // recover toward the first mission, preserve the blueprint flag.
            !firstMission && blueprint -> EligibilityResult(
                EligibilityState.INVALID,
                InvalidReason.BLUEPRINT_BEFORE_FIRST_MISSION,
                LaunchDestination.ONBOARDING,
                isRecovery = false,
            )
            firstMission && !blueprint -> EligibilityResult(
                EligibilityState.NEEDS_BLUEPRINT,
                InvalidReason.NONE,
                LaunchDestination.BLUEPRINT_WIZARD,
                isRecovery = false,
            )
            else -> EligibilityResult(
                EligibilityState.ESTABLISHED,
                InvalidReason.NONE,
                LaunchDestination.HOME,
                isRecovery = false,
            )
        }
    }

    /**
     * Streak-init guard (Decision D): a genuinely fresh user has NO earned
     * downstream progress. Recovery/resumed users (any earned flag) must keep
     * their streak. Pure — driven by the earned-progress facts only, so it is
     * correct even after a Hunter has just been (re)created.
     */
    fun shouldInitializeStreak(snapshot: EligibilitySnapshot): Boolean =
        !snapshot.firstMissionDone && !snapshot.blueprintSetupComplete
}
