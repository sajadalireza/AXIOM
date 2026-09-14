package com.axiom.app.presentation.home

import com.axiom.app.R
import com.axiom.app.presentation.home.components.muscleLabelResId
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    fun hunterHeader_preventsLayoutOverlapAndProtectsTypographyAt200Percent() {
        val header = locate(
            "src/main/java/com/axiom/app/presentation/home/components/HunterHeaderSection.kt"
        ).readText()

        assertFalse(
            "Hunter header must not use an unconstrained 130sp watermark that occludes content at 200% font scale",
            header.contains("fontSize = 130.sp")
        )
        assertTrue(
            "Hunter header name must prevent multi-line collision using TextOverflow.Ellipsis",
            header.contains("overflow = TextOverflow.Ellipsis")
        )
        assertTrue(
            "Hunter header must guarantee at least a 48dp touch target for accessibility",
            header.contains(".size(48.dp)")
        )
        assertTrue(
            "Hunter header must protect avatar clickability with content description",
            header.contains("home_hunter_profile_icon_cd")
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
            "home_secondary_surfaces_collapse",
            "home_body_status_title",
            "home_system_feed_title",
            "home_system_feed_empty",
            "home_active_mission_stage_boss",
            "home_active_mission_stage",
            "home_active_mission_quick_complete_cd",
            "home_vitals_teeth",
            "home_vitals_teeth_complete",
            "home_vitals_teeth_pending",
            "home_habit_water_format",
            "nav_home",
            "muscle_chest",
            "muscle_back",
            "muscle_shoulders",
            "muscle_biceps",
            "muscle_triceps",
            "muscle_legs",
            "muscle_core",
            "muscle_forearms"
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

    // ─── WP-UIUX-02 final Persian localization closure guards ────────────────

    /**
     * The Home dock label regressed to the literal mis-translation "کارت" ("card").
     * It must resolve to the culturally correct "خانه" and can never silently revert.
     */
    @Test
    fun persianNavHome_resolvesToHomeAndNeverRevertsToCard() {
        val faXml = locate("src/main/res/values-fa/strings.xml").readText()
        val match = "name=\"nav_home\">([^<]+)</string>".toRegex().find(faXml)

        assertNotNull("Persian nav_home resource must exist", match)
        val value = match!!.groupValues[1]
        assertEquals("Persian Home dock label must be the canonical 'خانه'", "خانه", value)
        assertFalse("Persian nav_home must not regress to 'کارت'", value.contains("کارت"))
    }

    /**
     * System-seeded muscle taxonomy must localize at the presentation layer:
     * every canonical [com.axiom.app.domain.model.MuscleGroup.id] resolves to a
     * localized string resource, unknown/custom IDs fall back to displayName, and
     * the Persian resources carry no Latin script. Room/domain/seed data is untouched.
     */
    @Test
    fun homeMuscleTaxonomy_resolvesToLocalizedPresentationLabels() {
        val expected = linkedMapOf(
            "chest" to R.string.muscle_chest,
            "back" to R.string.muscle_back,
            "shoulders" to R.string.muscle_shoulders,
            "biceps" to R.string.muscle_biceps,
            "triceps" to R.string.muscle_triceps,
            "legs" to R.string.muscle_legs,
            "core" to R.string.muscle_core,
            "forearms" to R.string.muscle_forearms
        )

        for ((id, resId) in expected) {
            assertEquals(
                "Canonical muscle id '$id' must resolve to its localized resource",
                resId,
                muscleLabelResId(id)
            )
        }

        assertNull(
            "Unknown/custom muscle ids must fall back to the stored displayName",
            muscleLabelResId("custom_quadriceps_xyz")
        )

        val faXml = locate("src/main/res/values-fa/strings.xml").readText()
        for (id in expected.keys) {
            val match = "name=\"muscle_$id\">([^<]+)</string>".toRegex().find(faXml)
            assertNotNull("Persian resource missing for muscle id '$id'", match)
            val value = match!!.groupValues[1]
            assertFalse(
                "Persian muscle label for '$id' must not leak Latin script: $value",
                Regex("[A-Za-z]").containsMatchIn(value)
            )
        }

        val bodyStatus = locate(
            "src/main/java/com/axiom/app/presentation/home/components/BodyStatusSection.kt"
        ).readText()
        assertTrue(
            "Home Body Status must route muscle names through the localized resolver",
            bodyStatus.contains("muscleLabelResId(muscle.id)")
        )
        assertFalse(
            "Home Body Status grid must not render the raw system seed name",
            bodyStatus.contains("text = muscle.displayName.uppercase()")
        )
        assertFalse(
            "Combat readiness summary must not render the raw system seed name",
            bodyStatus.contains("freshestGroup?.displayName") || bodyStatus.contains("fatiguedGroup?.displayName")
        )
    }

    /**
     * The Daily Habit water counter hardcoded the English abbreviation "gl" into
     * a Persian Home card. The unit must be resource-backed with EN/FA parity.
     */
    @Test
    fun homeWaterUnit_isLocalizedAndNoHardcodedEnglishAbbreviationRemains() {
        val enXml = locate("src/main/res/values/strings.xml").readText()
        val faXml = locate("src/main/res/values-fa/strings.xml").readText()

        val en = "name=\"home_habit_water_format\">([^<]+)</string>".toRegex().find(enXml)
        val fa = "name=\"home_habit_water_format\">([^<]+)</string>".toRegex().find(faXml)

        assertNotNull("English home_habit_water_format missing", en)
        assertNotNull("Persian home_habit_water_format missing", fa)

        val enValue = en!!.groupValues[1]
        val faValue = fa!!.groupValues[1]

        assertTrue("English water unit must read 'glasses', was: $enValue", enValue.contains("glasses"))
        assertTrue("Persian water unit must read 'لیوان', was: $faValue", faValue.contains("لیوان"))
        assertTrue("English water format must keep the %1\$d counter placeholder", enValue.contains("%1\$d"))
        assertTrue("Persian water format must keep the %1\$d counter placeholder", faValue.contains("%1\$d"))
        assertTrue("English water format must keep the %2\$d goal placeholder", enValue.contains("%2\$d"))
        assertTrue("Persian water format must keep the %2\$d goal placeholder", faValue.contains("%2\$d"))

        val habit = locate(
            "src/main/java/com/axiom/app/presentation/home/components/DailyHabitNudgeSection.kt"
        ).readText()
        assertFalse(
            "Home must not hardcode the English ' gl' water abbreviation",
            habit.contains("/ 8 gl")
        )
        assertTrue(
            "Home water counter must render the localized format resource",
            habit.contains("R.string.home_habit_water_format")
        )
    }
}
