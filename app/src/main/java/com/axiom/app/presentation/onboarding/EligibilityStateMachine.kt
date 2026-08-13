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
 * Pure and deterministic: no clock, no I/O, no coroutine timing.
 *
 * NOTE (RED skeleton, WP-203): the [evaluate] body below intentionally
 * replicates the PRE-FIX production classifier — it ignores `hunterExists` and
 * silently normalizes impossible states. This makes the accompanying matrix
 * test fail on exactly the defect rows (10, 11, 12, invalid rows). The GREEN
 * commit replaces this body with the real contract logic.
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
        // RED skeleton: pre-fix behavior — Hunter-blind, silently normalizing.
        val destination = when {
            !snapshot.setupComplete -> LaunchDestination.SETUP
            !snapshot.firstMissionDone -> LaunchDestination.ONBOARDING
            !snapshot.blueprintSetupComplete -> LaunchDestination.BLUEPRINT_WIZARD
            else -> LaunchDestination.HOME
        }
        val state = when (destination) {
            LaunchDestination.SETUP -> EligibilityState.NEEDS_SETUP
            LaunchDestination.ONBOARDING -> EligibilityState.NEEDS_HUNTER
            LaunchDestination.BLUEPRINT_WIZARD -> EligibilityState.NEEDS_BLUEPRINT
            LaunchDestination.HOME -> EligibilityState.ESTABLISHED
        }
        return EligibilityResult(
            state = state,
            reason = InvalidReason.NONE,
            destination = destination,
            isRecovery = false,
        )
    }

    /**
     * Streak-init guard (Decision D): a genuinely fresh user has NO earned
     * downstream progress. Recovery/resumed users (any earned flag) must keep
     * their streak. Pure — driven by the earned-progress facts only, so it is
     * correct even after a Hunter has just been (re)created.
     *
     * RED skeleton: always true (mirrors the current unconditional setStreak(0)).
     */
    fun shouldInitializeStreak(snapshot: EligibilitySnapshot): Boolean = true
}
