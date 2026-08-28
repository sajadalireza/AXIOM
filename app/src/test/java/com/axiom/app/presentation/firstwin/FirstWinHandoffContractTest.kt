package com.axiom.app.presentation.firstwin

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-207 RED contract for the unfinished tail of the First-Win vertical slice.
 *
 * NEXT must offer one primary finish action, durable lifecycle state must advance
 * through HANDOFF, and HOME may only be entered after the runtime reports the
 * terminal HOME position. This guard intentionally fails until that wiring exists.
 */
class FirstWinHandoffContractTest {
    private fun locate(relative: String): File {
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("../app/$relative"),
            File(System.getProperty("user.dir") ?: ".", relative),
            File(System.getProperty("user.dir") ?: ".", "app/$relative"),
        )
        return candidates.firstOrNull { it.isFile }
            ?: fail("Required source not found: $relative").let { error("unreachable") }
    }

    @Test
    fun nextToHandoffToHome_isExplicitlyWired() {
        val screen = locate("src/main/java/com/axiom/app/presentation/firstwin/FirstWinScreen.kt").readText()
        val runtime = locate("src/main/java/com/axiom/app/presentation/firstwin/FirstWinJourneyRuntime.kt").readText()
        val viewModel = locate("src/main/java/com/axiom/app/presentation/firstwin/FirstWinViewModel.kt").readText()
        val nav = locate("src/main/java/com/axiom/app/navigation/AwakenNavGraph.kt").readText()

        assertTrue("NEXT must expose the finish-for-now action", screen.contains("onFinishForNow"))
        assertTrue("HANDOFF must have a visible UI state", screen.contains("FirstWinPosition.HANDOFF"))
        assertTrue("FirstWinScreen must emit terminal handoff completion", screen.contains("onHandoffComplete"))
        assertTrue("Runtime must durably advance NEXT to HANDOFF", runtime.contains("finishForNow"))
        assertTrue("Runtime must durably advance HANDOFF to HOME", runtime.contains("completeHandoff"))
        assertTrue("ViewModel must expose finishForNow", viewModel.contains("fun finishForNow()"))
        assertTrue("ViewModel must expose completeHandoff", viewModel.contains("fun completeHandoff()"))
        assertTrue("NavGraph must consume First-Win completion", nav.contains("onHandoffComplete ="))
        assertTrue("First-Win completion must route to Home", nav.contains("navController.navigate(Screen.Home.route)"))
    }
}
