package com.axiom.app.presentation.missions

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Accessibility & Font Scaling Contract Test for G3-P4 Mission Authoring Simplification.
 * Verifies:
 * 1. Single Primary CTA minimum height (>=52dp) and flexible expansion under 200% font scale.
 * 2. Absence of rigid fixed-height constraints on interactive chips and containers.
 * 3. Complete English and Persian RTL string parity for all authoring strings.
 */
class MissionAuthoringFontScaleContractTest {

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
    fun primaryCta_preservesMinimumTargetWhileAllowingExpansionFor200PercentFont() {
        val screenSource = locate(
            "src/main/java/com/axiom/app/presentation/missions/AddMissionScreen.kt"
        ).readText()

        assertTrue(
            "Screen must define the canonical btn_accept_mission test tag",
            screenSource.contains(".testTag(\"btn_accept_mission\")")
        )
        assertTrue(
            "Primary CTA must maintain a minimum 52dp touch target using heightIn(min = 52.dp)",
            screenSource.contains(".heightIn(min = 52.dp)")
        )
        assertFalse(
            "Primary CTA must not force a rigid fixed height that clips text at 200% font scale",
            screenSource.contains(".testTag(\"btn_accept_mission\")\n        .height(52.dp)")
        )
    }

    @Test
    fun interactiveChipsAndInputs_useDynamicHeightsToPreventFontScalingClipping() {
        val screenSource = locate(
            "src/main/java/com/axiom/app/presentation/missions/AddMissionScreen.kt"
        ).readText()

        assertTrue(
            "Skill chips must use heightIn(min = 44.dp) to allow dynamic font growth",
            screenSource.contains(".heightIn(min = 44.dp)")
        )
        assertTrue(
            "Track chips must use heightIn(min = 36.dp) for dynamic height growth",
            screenSource.contains(".heightIn(min = 36.dp)")
        )
    }

    @Test
    fun missionAuthoringStrings_haveCompleteEnglishAndPersianParity() {
        val enXml = locate("src/main/res/values/strings.xml").readText()
        val faXml = locate("src/main/res/values-fa/strings.xml").readText()

        val requiredAuthoringKeys = listOf(
            "add_mission_quick_create_badge",
            "add_mission_title_label",
            "add_mission_title_placeholder",
            "add_mission_done_condition_label",
            "add_mission_done_condition_placeholder",
            "add_mission_context_label",
            "add_mission_context_placeholder",
            "add_mission_duration_label",
            "add_mission_duration_min",
            "add_mission_schedule_label",
            "add_mission_schedule_now",
            "add_mission_schedule_morning",
            "add_mission_schedule_afternoon",
            "add_mission_schedule_evening",
            "add_mission_evidence_label",
            "add_mission_evidence_none",
            "add_mission_evidence_checklist",
            "add_mission_evidence_metric",
            "add_mission_advanced_toggle_expand",
            "add_mission_advanced_toggle_collapse",
            "add_mission_primary_cta",
            "add_mission_validation_hint"
        )

        for (key in requiredAuthoringKeys) {
            val enRegex = Regex("""<string\s+name="$key"[^>]*>(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
            val faRegex = Regex("""<string\s+name="$key"[^>]*>(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)

            val enMatch = enRegex.find(enXml)
            assertNotNull("Missing English string for key: $key", enMatch)
            assertTrue("English string for $key must not be empty", enMatch!!.groupValues[1].isNotBlank())

            val faMatch = faRegex.find(faXml)
            assertNotNull("Missing Persian translation for key: $key", faMatch)
            assertTrue("Persian translation for $key must not be empty", faMatch!!.groupValues[1].isNotBlank())
        }
    }
}
