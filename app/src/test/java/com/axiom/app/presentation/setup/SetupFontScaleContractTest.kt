package com.axiom.app.presentation.setup

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-207 accessibility regression guard.
 *
 * The setup CTA is part of the First-Win critical path. A fixed 52dp height
 * clips the localized label at 200% font scale. The CTA must retain a 48dp+
 * target while being allowed to grow with wrapped text.
 */
class SetupFontScaleContractTest {
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
    fun setupContinue_allowsTextGrowthAtLargeFontScale() {
        val screen = locate(
            "src/main/java/com/axiom/app/presentation/setup/LanguageThemeSetupScreen.kt"
        ).readText()

        assertFalse(
            "Setup CTA must not force a fixed 52dp height",
            screen.contains(".height(52.dp)")
        )
        assertTrue(
            "Setup CTA must preserve a >=52dp minimum touch target while allowing growth",
            screen.contains(".heightIn(min = 52.dp)")
        )
        assertTrue(
            "Setup CTA label must be centered when it wraps",
            screen.contains("textAlign = TextAlign.Center")
        )
    }
}
