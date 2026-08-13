package com.axiom.app.presentation.onboarding

/**
 * Single source of truth for the launch destination.
 *
 * WP-201: the launch route must be a pure function of authoritative startup
 * state. Profile existence is intentionally NOT an input — a freshly seeded
 * default profile row must never, on its own, classify a user as established.
 */
enum class LaunchDestination {
    SETUP,
    ONBOARDING,
    BLUEPRINT_WIZARD,
    HOME,
}

/**
 * Authoritative startup flags used to classify the launch destination.
 * These mirror the persisted DataStore flags (not their non-authoritative
 * pre-emission defaults).
 */
data class LaunchInputs(
    val setupComplete: Boolean,
    val firstMissionDone: Boolean,
    val blueprintSetupComplete: Boolean,
)

object LaunchClassifier {
    /**
     * Flag-based classification. Deterministic for a given [inputs] — the same
     * user state always maps to the same destination, regardless of timing.
     */
    fun classify(inputs: LaunchInputs): LaunchDestination = when {
        !inputs.setupComplete -> LaunchDestination.SETUP
        !inputs.firstMissionDone -> LaunchDestination.ONBOARDING
        !inputs.blueprintSetupComplete -> LaunchDestination.BLUEPRINT_WIZARD
        else -> LaunchDestination.HOME
    }
}

/**
 * Resolves the launch destination from authoritative startup state.
 *
 * WP-201: [resolve] awaits startup readiness BEFORE reading launch state, so
 * route evaluation always observes post-bootstrap state. The same user state
 * therefore maps to the same destination regardless of coroutine timing.
 */
class LaunchRouteResolver(
    private val awaitStartupReady: suspend () -> Unit,
    private val readState: suspend () -> LaunchInputs,
) {
    suspend fun resolve(): LaunchDestination {
        awaitStartupReady()
        val inputs = readState()
        return LaunchClassifier.classify(inputs)
    }
}
