package com.axiom.app.presentation.onboarding

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-208 integration contract guard: Splash must inject and consult [FirstWinControlPlane]
 * to determine [isTreatmentActive] before resolving the one-shot launch destination.
 */
class SplashFirstWinControlPlaneContractTest {

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
    fun splashViewModel_injectsAndAppliesFirstWinControlPlane() {
        val source = locateSplash().readText()

        assertTrue(
            "SplashViewModel must inject FirstWinControlPlane",
            source.contains("FirstWinControlPlane"),
        )
        assertTrue(
            "SplashViewModel must query isTreatmentActive() from control plane",
            source.contains("firstWinControlPlane.isTreatmentActive()"),
        )
        assertTrue(
            "SplashViewModel must pass isTreatmentActive to FirstWinLaunchPolicy.resolve",
            source.contains("isTreatmentActive = isTreatmentActive"),
        )
    }
}
