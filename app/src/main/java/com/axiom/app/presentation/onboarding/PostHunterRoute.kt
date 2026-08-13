package com.axiom.app.presentation.onboarding

/**
 * WP-203 recovery-loop repair — post-Hunter-creation routing.
 *
 * After a Hunter is (re)created in the onboarding surface — whether in a
 * genuinely FRESH flow or a HUNTER_RECOVERY flow — the next destination must be
 * derived from an AUTHORITATIVE re-read of persisted eligibility, never from
 * lifecycle-collected UI defaults (`collectAsStateWithLifecycle(initialValue =
 * false)`), which can still read `false` at the moment the user advances and so
 * would replay First Mission / Blueprint for a completed recovery user.
 *
 * This mirrors the WP-202 splash-exit repair one layer deeper: the same
 * stale-initial-value antipattern lived in `AwakeningComplete.onBegin`, and the
 * fix is the same — re-read persisted truth and route from eligibility.
 */
enum class PostHunterRoute { HOME, FIRST_MISSION, BLUEPRINT, ONBOARDING, SETUP }

/**
 * Single post-Hunter route authority. [readSnapshot] is an authoritative,
 * terminal re-read of persisted eligibility facts (DataStore flags + Hunter
 * entity existence), acquired AFTER the Hunter-creation write completes. Reuses
 * the WP-203 [EligibilityStateMachine] — it introduces no second classifier.
 */
class PostHunterRouteResolver(
    private val readSnapshot: suspend () -> EligibilitySnapshot,
) {
    /**
     * GREEN: re-read authoritative persisted eligibility and map the resulting
     * [EligibilityState] to the next onboarding step. Lifecycle-collected UI
     * flags are never consulted here, so a completed recovery user resolves to
     * HOME (no First Mission / Blueprint replay) while a genuinely fresh user
     * still resolves to FIRST_MISSION — both from persisted truth, not mode.
     */
    suspend fun resolve(): PostHunterRoute {
        val result = EligibilityStateMachine.evaluate(readSnapshot())
        return when (result.state) {
            EligibilityState.ESTABLISHED -> PostHunterRoute.HOME
            EligibilityState.NEEDS_BLUEPRINT -> PostHunterRoute.BLUEPRINT
            EligibilityState.NEEDS_FIRST_MISSION -> PostHunterRoute.FIRST_MISSION
            EligibilityState.INVALID ->
                if (result.reason == InvalidReason.BLUEPRINT_BEFORE_FIRST_MISSION) {
                    // Impossible order: recover toward the first mission; the
                    // blueprint flag is preserved (never deleted) for later.
                    PostHunterRoute.FIRST_MISSION
                } else {
                    // Setup missing (should not occur post-Hunter): recover to setup.
                    PostHunterRoute.SETUP
                }
            EligibilityState.NEEDS_SETUP -> PostHunterRoute.SETUP
            // Hunter still absent right after a creation attempt ⇒ the repository
            // write did not land; stay in the onboarding/recovery surface rather
            // than proceed past a missing prerequisite.
            EligibilityState.NEEDS_HUNTER,
            EligibilityState.HUNTER_RECOVERY -> PostHunterRoute.ONBOARDING
        }
    }
}
