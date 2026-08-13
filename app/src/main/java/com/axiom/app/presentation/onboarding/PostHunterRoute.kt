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
 * stale-initial-value antipattern lives in `AwakeningComplete.onBegin`, and the
 * fix is the same — re-read persisted truth and route from eligibility.
 */
enum class PostHunterRoute { HOME, FIRST_MISSION, BLUEPRINT, ONBOARDING, SETUP }

/**
 * Single post-Hunter route authority. [readSnapshot] is an authoritative,
 * terminal re-read of persisted eligibility facts (DataStore flags + Hunter
 * entity existence), acquired AFTER the Hunter-creation write completes.
 */
class PostHunterRouteResolver(
    private val readSnapshot: suspend () -> EligibilitySnapshot,
) {
    /**
     * RED (pre-repair): reproduces `AwakeningComplete.onBegin` on `f6fca14` — the
     * post-Hunter route is chosen from stale lifecycle-collected downstream flags
     * (modeled as their `initialValue = false` defaults), NOT from an
     * authoritative re-read. A completed recovery user is therefore mis-routed to
     * FIRST_MISSION instead of HOME, replaying already-earned progress.
     */
    suspend fun resolve(): PostHunterRoute {
        val snapshot = readSnapshot()
        // DEFECT: downstream progress taken from stale lifecycle defaults, not the
        // authoritative snapshot — collectAsStateWithLifecycle(initialValue = false).
        val staleFirstMissionDone = false
        val staleBlueprintComplete = false
        return when {
            !snapshot.setupComplete -> PostHunterRoute.SETUP
            staleFirstMissionDone && staleBlueprintComplete -> PostHunterRoute.HOME
            staleFirstMissionDone -> PostHunterRoute.BLUEPRINT
            else -> PostHunterRoute.FIRST_MISSION
        }
    }
}
