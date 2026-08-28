package com.axiom.app.presentation.onboarding

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-207 integration guard: Splash must actually consume the durable First-Win
 * lifecycle when selecting its one-shot launch destination. A standalone policy
 * object is insufficient if the real launch resolver bypasses it.
 */
class SplashFirstWinIntegrationContractTest {
    private fun locateSplash(): File {
        val relative = "src/main/java/com/axiom/app/presentation/onboarding/SplashScreen.kt"
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("../app/$relative"),
            File(System.getProperty("user.dir") ?: ".", relative),
            File(System.getProperty("user.dir") ?: ".", "app/$relative"),
        )
        return candidates.firstOrNull { it.isFile }
            ?: fail("SplashScreen.kt not found").let { error("unreachable") }
    }

    @Test
    fun splashResolver_appliesDurableFirstWinPolicy() {
        val source = locateSplash().readText()

        assertTrue(
            "Splash must resolve the deterministic First-Win session id for the current Hunter",
            source.contains("FirstWinIds.sessionId"),
        )
        assertTrue(
            "Splash must read durable First-Win facts before choosing the destination",
            source.contains("firstWinFactsReader.read"),
        )
        assertTrue(
            "Splash must apply FirstWinLaunchPolicy to the eligibility result and durable session",
            source.contains("FirstWinLaunchPolicy.resolve"),
        )
        assertFalse(
            "Splash must not bypass FirstWinLaunchPolicy by returning EligibilityStateMachine directly",
            source.contains("return EligibilityStateMachine.evaluate(snapshot).destination"),
        )
    }
}
