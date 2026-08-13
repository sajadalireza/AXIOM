package com.axiom.app.presentation.onboarding

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * WP-201 launch-race regression.
 *
 * RED expectation: the launch route must be a deterministic function of
 * authoritative startup state — never of coroutine/seed timing — and a freshly
 * seeded default profile must never be treated as an established user.
 *
 * Tests are deterministic: ordering is driven by an explicit [CompletableDeferred]
 * barrier under [runTest]'s test dispatcher, with no real-time sleeps.
 */
class LaunchRaceRegressionTest {

    /** Mutable startup state mirroring SeedDataHelper.seedDefaultProfileIfNeeded writes. */
    private class FakeStartupState {
        var setupComplete = false
        var firstMissionDone = false
        var blueprintSetupComplete = false

        /** Mirrors the default-profile seed: setup done, but onboarding NOT complete. */
        fun applyDefaultProfileSeed() {
            setupComplete = true
            firstMissionDone = false
            blueprintSetupComplete = false
        }

        fun snapshot() = LaunchInputs(setupComplete, firstMissionDone, blueprintSetupComplete)
    }

    // §11 — a freshly seeded default profile is never classified as established.
    @Test
    fun freshlySeededDefaultProfileIsNeverClassifiedAsEstablished() {
        val fresh = FakeStartupState().apply { applyDefaultProfileSeed() }
        val dest = LaunchClassifier.classify(fresh.snapshot())
        assertNotEquals(LaunchDestination.HOME, dest)
        assertEquals(LaunchDestination.ONBOARDING, dest)
    }

    // §13 — same user state ⇒ same route regardless of seed-vs-read ordering.
    @Test
    fun launchRouteDoesNotDependOnBootstrapTiming() = runTest {
        assertEquals(
            "same fresh user must route identically regardless of bootstrap timing",
            resolveWithOrdering(seedBeforeRead = true),
            resolveWithOrdering(seedBeforeRead = false)
        )
    }

    // Core race: route evaluation that begins before seeding completes must still
    // wait for authoritative startup readiness and route the fresh user correctly.
    @Test
    fun freshUserRoutesToOnboardingEvenWhenEvaluationStartsBeforeSeeding() = runTest {
        val dest = resolveWithOrdering(seedBeforeRead = false)
        assertEquals(LaunchDestination.ONBOARDING, dest)
    }

    /**
     * Drives [LaunchRouteResolver] with an explicit ordering barrier.
     *  - seedBeforeRead = true : seeding completes, then the route is resolved.
     *  - seedBeforeRead = false: route resolution begins first; seeding completes
     *    only after startup readiness is signalled. A correct resolver awaits
     *    readiness, so it observes the post-seed state either way.
     */
    private suspend fun resolveWithOrdering(seedBeforeRead: Boolean): LaunchDestination = coroutineScope {
        val state = FakeStartupState() // fresh: nothing seeded yet
        val ready = CompletableDeferred<Unit>()
        val resolver = LaunchRouteResolver(
            awaitStartupReady = { ready.await() },
            readState = { state.snapshot() }
        )
        if (seedBeforeRead) {
            state.applyDefaultProfileSeed()
            ready.complete(Unit)
            resolver.resolve()
        } else {
            val deferred = async { resolver.resolve() }
            yield() // let resolve() start (and, when fixed, park on awaitStartupReady)
            state.applyDefaultProfileSeed()
            ready.complete(Unit)
            deferred.await()
        }
    }
}
