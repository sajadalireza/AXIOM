package com.axiom.app.navigation

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-207 navigation registration guard.
 *
 * `Screen.FirstWin.route` already exists and Splash can map FIRST_WIN to it, but that
 * route is not safe to emit until AwakenNavGraph owns a real composable destination.
 */
class FirstWinDestinationRegistrationTest {
    private fun locateNavGraph(): File {
        val relative = "src/main/java/com/axiom/app/navigation/AwakenNavGraph.kt"
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("../app/$relative"),
            File(System.getProperty("user.dir") ?: ".", relative),
            File(System.getProperty("user.dir") ?: ".", "app/$relative"),
        )
        return candidates.firstOrNull { it.isFile }
            ?: fail(
                "AwakenNavGraph.kt not found. Looked in: " +
                    candidates.joinToString { it.absolutePath }
            ).let { error("unreachable") }
    }

    @Test
    fun firstWinRoute_isRegisteredToRealScreen() {
        val source = locateNavGraph().readText()

        assertTrue(
            "AwakenNavGraph must register Screen.FirstWin.route before Splash can emit FIRST_WIN",
            source.contains("composable(Screen.FirstWin.route)"),
        )
        assertTrue(
            "First-Win destination must render FirstWinScreen rather than a placeholder",
            source.contains("FirstWinScreen("),
        )
    }
}
