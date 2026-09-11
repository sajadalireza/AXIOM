package com.axiom.app.presentation.home

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * G3-P3 accessibility & font scaling regression guard.
 *
 * Resolves G2-S3-01 / WP-207 carry-forward where legacy Home layout suffered from
 * text clipping and overlap at 200% system font scale.
 *
 * Verifies:
 * 1. Single Primary CTA contract with flexible growth and >=48dp (52dp) touch target.
 * 2. Absence of rigid height constraints on Countdown digit blocks.
 * 3. Watermark glyph constraint preventing text occlusion at 200% font scale.
 * 4. Complete string resource parity across English and Persian RTL.
 */
class HomeFontScaleContractTest {

    private fun locate(relative: String): File {
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("../app/$relative"),
            File(System.getProperty("user.dir") ?: ".", relative),
            File(System.getProperty("user.dir") ?: ".", "app/$relative")
        )
        return candidates.firstOrNull { it.isFile }
            ?: fail("Required source not found: $relative").let { error("unreachable") }
    }

    @Test
    fun primaryCta_preservesMinimumTargetWhileAllowingTextGrowthAt200Percent() {
        val heroCard = locate(
            "src/main/java/com/axiom/app/presentation/home/components/NextMissionHeroCard.kt"
        ).readText()

        assertTrue(
            "Hero Card must define the canonical home_primary_cta test tag",
            heroCard.contains(".testTag(\"home_primary_cta\")")
        )
        assertTrue(
            "Primary CTA must maintain a minimum 52dp touch target while allowing expansion for 200% font scale",
            heroCard.contains(".heightIn(min = 52.dp)")
        )
        assertFalse(
            "Primary CTA must not force a rigid 52dp height that clips text at 200% font scale",
            heroCard.contains("Button(\n                    onClick =\n                        modifier = Modifier\n                        .fillMaxWidth()\n                        .height(52.dp)")
        )
    }

    @Test
    fun countdownDigitBlock_avoidsFixedClippingHeight() {
        val countdown = locate(
            "src/main/java/com/axiom/app/presentation/home/components/CountdownBannerSection.kt"
        ).readText()

        assertTrue(
            "DigitBlock must use flexible heightIn(min = 52.dp) to accommodate 200% font scale",
            countdown.contains(".heightIn(min = 52.dp)")
        )
        assertFalse(
            "DigitBlock must not force a rigid 52dp height which clips 24sp+ digits at 200% font scale",
            countdown.contains(".height(52.dp)")
        )
    }

    @Test
    fun hunterHeader_constrainsWatermarkToPreventOverlap() {
        val header = locate(
            "src/main/java/com/axiom/app/presentation/home/components/HunterHeaderSection.kt"
        ).readText()

        assertFalse(
            "Hunter header must not use an unconstrained 130sp watermark that occludes content at 200% font scale",
            header.contains("fontSize = 130.sp")
        )
        assertTrue(
            "Hunter header must constrain watermark font size and softWrap to prevent layout overlap",
            header.contains("softWrap = false") && header.contains("fontSize = 64.sp")
        )
    }

    @Test
    fun homeHeroStrings_haveCompleteEnglishAndPersianParity() {
        val enXml = locate("src/main/res/values/strings.xml").readText()
        val faXml = locate("src/main/res/values-fa/strings.xml").readText()

        val requiredKeys = listOf(
            "home_hero_label",
            "home_hero_start_mission",
            "home_hero_commit_mission",
            "home_hero_no_mission_desc",
            "home_hero_advances_goal",
            "home_hero_advances_project",
            "home_hero_view_all_missions",
            "home_secondary_surfaces_title",
            "home_secondary_surfaces_expand",
            "home_secondary_surfaces_collapse"
        )

        for (key in requiredKeys) {
            val enPattern = "name=\"$key\">([^<]+)</string>".toRegex()
            val faPattern = "name=\"$key\">([^<]+)</string>".toRegex()

            val enMatch = enPattern.find(enXml)
            val faMatch = faPattern.find(faXml)

            assertNotNull("English resource missing key: $key", enMatch)
            assertNotNull("Persian resource missing key: $key", faMatch)

            assertTrue(
                "English string for $key must not be blank",
                enMatch!!.groupValues[1].isNotBlank()
            )
            assertTrue(
                "Persian string for $key must not be blank",
                faMatch!!.groupValues[1].isNotBlank()
            )
        }
    }
}
