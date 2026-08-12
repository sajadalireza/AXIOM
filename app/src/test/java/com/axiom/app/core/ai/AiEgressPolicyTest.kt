package com.axiom.app.core.ai

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** WP-104 SEC-104-001 tests. */
class AiEgressPolicyTest {

    @Test fun directGeminiDisabledByDefault() {
        // Default/production build must not permit direct Gemini egress.
        assertFalse(AiEgressPolicy.isDirectGeminiAllowed())
    }

    @Test fun requireFailsClosedWhenDisabled() {
        val ex = runCatching { AiEgressPolicy.requireDirectGeminiAllowed() }.exceptionOrNull()
        assertTrue(ex is DirectAiEgressDisabledException)
    }
}

/** WP-104 prompt-privacy minimization tests. */
class AiPromptContextTest {

    @Test fun contextExcludesNameAndIdentity() {
        val ctx = AiPromptContext.hunterContext(
            level = 12,
            rankLabel = "B-Class Specialist",
            streakDays = 7,
            currentXP = 340,
            xpToNextLevel = 500,
            inactiveDays = 1
        )
        // Non-identifying stats present…
        assertTrue(ctx.contains("Level 12"))
        assertTrue(ctx.contains("Streak: 7 days"))
        // …but no name/identity marker.
        assertFalse(ctx.contains("Hunter:"))   // old "Hunter: <name>" form removed
        assertFalse(ctx.contains("@"))
    }

    @Test fun arbitraryNameCannotEnterContext() {
        val ctx = AiPromptContext.hunterContext(
            level = 1, rankLabel = "E-Class Recruit", streakDays = 0,
            currentXP = 0, xpToNextLevel = 100, inactiveDays = 0
        )
        // The builder takes no name parameter, so a real name can never be embedded.
        assertFalse(ctx.contains("Alireza"))
    }
}
