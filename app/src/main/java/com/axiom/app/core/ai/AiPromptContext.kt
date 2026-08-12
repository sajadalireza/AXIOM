package com.axiom.app.core.ai

/**
 * WP-104 SEC-104-001 / prompt privacy minimization.
 *
 * Pure, testable builder for the non-identifying context string sent to Gemini.
 * Deliberately excludes the user's name and any direct identity field — only
 * gameplay stats (level, rank, streak, XP, inactivity) may leave the device.
 */
object AiPromptContext {

    /**
     * Builds the outbound hunter-context line. Contains NO name/email/phone or other
     * account identifier — only non-identifying progression stats.
     */
    fun hunterContext(
        level: Int,
        rankLabel: String,
        streakDays: Int,
        currentXP: Int,
        xpToNextLevel: Int,
        inactiveDays: Int
    ): String =
        "Hunter | Level $level | Rank $rankLabel | " +
            "Streak: $streakDays days | XP: $currentXP/$xpToNextLevel | " +
            "Days since last protocol submission: $inactiveDays days"
}
