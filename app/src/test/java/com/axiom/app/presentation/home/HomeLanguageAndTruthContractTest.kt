package com.axiom.app.presentation.home

import com.axiom.app.R
import com.axiom.app.presentation.home.components.homeNextActionStrings
import com.axiom.app.ui.HomeNextAction
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-UIUX-02 repair regression guard for two confirmed blockers.
 *
 * 1. MIXED EN/FA LANGUAGE STATE — Home derived its Xion advisory text from the stored
 *    DataStore language while every other string resolved against the runtime UI locale.
 *    When those two sources diverged, Persian advisory text rendered inside an English
 *    Home (observed: "تکمیل کنید: Momentum Re-anchor: Core Goal Action").
 *    The advisory is now a language-neutral [HomeNextAction] descriptor that the
 *    presentation layer resolves from resources, under the same configuration that renders
 *    every other Home string.
 *
 * 2. TRUTHFUL TRACK VALUE — TodayStatusRow sliced the real mission track with `take(10)`,
 *    rendering "Recovery Protocol" as "Recovery P". Runtime strings are no longer cut
 *    before presentation; constrained space is handled by the Compose layout.
 */
class HomeLanguageAndTruthContractTest {

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

    private fun locateDir(relative: String): File {
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("../app/$relative"),
            File(System.getProperty("user.dir") ?: ".", relative),
            File(System.getProperty("user.dir") ?: ".", "app/$relative")
        )
        return candidates.firstOrNull { it.isDirectory }
            ?: fail("Required source directory not found: $relative").let { error("unreachable") }
    }

    private fun homeViewModel(): String = locate("src/main/java/com/axiom/app/ui/HomeViewModel.kt").readText()

    private fun xionCard(): String = locate(
        "src/main/java/com/axiom/app/presentation/home/components/XionInsightCard.kt"
    ).readText()

    private fun todayRow(): String = locate(
        "src/main/java/com/axiom/app/presentation/home/components/TodayStatusRow.kt"
    ).readText()

    // ─────────────────────────────────────────────────────────────────────────────
    // 1. Language state can no longer desynchronize the Home advisory
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * The advisory is resolved through a locale-independent mapping: the same action always
     * maps to the same resource, whatever language state happens to be stored.
     */
    @Test
    fun homeAdvisory_mapsToResourcesIndependentlyOfStoredLanguageState() {
        val firstMission = homeNextActionStrings(HomeNextAction.AddFirstMission)
        assertEquals(
            "Add-first-mission advisory must resolve to its own resource",
            R.string.home_next_action_add_first_mission,
            firstMission.resId
        )
        assertTrue(
            "Add-first-mission advisory carries no format arguments",
            firstMission.formatArgs.isEmpty()
        )

        val complete = homeNextActionStrings(HomeNextAction.CompleteMission("Momentum Re-anchor: Core Goal Action"))
        assertEquals(
            "Complete-mission advisory must resolve to its own resource",
            R.string.home_next_action_complete_mission,
            complete.resId
        )
        assertEquals(
            "The real mission title must be passed through untouched as the format argument",
            listOf("Momentum Re-anchor: Core Goal Action"),
            complete.formatArgs
        )

        val streak = homeNextActionStrings(HomeNextAction.BuildStreak)
        assertEquals(
            "Streak advisory must resolve to its own resource",
            R.string.home_next_action_build_streak,
            streak.resId
        )
        assertTrue("Streak advisory carries no format arguments", streak.formatArgs.isEmpty())

        assertTrue(
            "Distinct advisory states must never collapse onto one resource",
            setOf(firstMission.resId, complete.resId, streak.resId).size == 3
        )
    }

    /**
     * HomeViewModel must not build display text from the stored language, and the advisory
     * type itself must stay language-neutral.
     */
    @Test
    fun homeAdvisory_isNeverConstructedFromStoredLanguageState() {
        val viewModel = homeViewModel()

        assertFalse(
            "HomeViewModel must not read the DataStore language for Home presentation state",
            viewModel.contains("languageFlow")
        )
        assertFalse(
            "The previous Persian advisory literal must never return to HomeViewModel",
            viewModel.contains("تکمیل کنید")
        )
        assertFalse(
            "The previous English advisory literal must never return to HomeViewModel",
            viewModel.contains("\"Complete: ")
        )
        assertFalse(
            "The previous streak advisory literal must never return to HomeViewModel",
            viewModel.contains("Keep going — build your streak")
        )

        val advisoryType = viewModel.substringAfter("sealed interface HomeNextAction").substringBefore("}")
        assertTrue(
            "HomeNextAction must exist as a structured advisory descriptor",
            viewModel.contains("sealed interface HomeNextAction")
        )
        assertFalse(
            "The advisory descriptor must carry no display text of its own",
            Regex("\"[^\"]+\"").containsMatchIn(advisoryType)
        )
    }

    /** The rendered advisory must come from resources, not from any pre-localized payload. */
    @Test
    fun homeAdvisory_rendersThroughResourceResolution() {
        val card = xionCard()
        assertTrue(
            "Xion advisory must bind to the structured HomeNextAction state",
            card.contains("nextBestAction: HomeNextAction?")
        )
        assertTrue(
            "Xion advisory must resolve the Complete-mission resource",
            card.contains("R.string.home_next_action_complete_mission")
        )
        assertTrue(
            "Xion advisory must resolve the Add-first-mission resource",
            card.contains("R.string.home_next_action_add_first_mission")
        )
        assertTrue(
            "Xion advisory must resolve the streak resource",
            card.contains("R.string.home_next_action_build_streak")
        )
        assertFalse(
            "No localized advisory literal may be rendered directly",
            card.contains("تکمیل کنید")
        )
    }

    /** English Home can never render Persian advisory text; Persian Home stays Persian-only. */
    @Test
    fun homeAdvisoryResources_haveEnFaParityWithoutCrossLanguageLeak() {
        val enXml = locate("src/main/res/values/strings.xml").readText()
        val faXml = locate("src/main/res/values-fa/strings.xml").readText()

        val keys = listOf(
            "home_next_action_add_first_mission",
            "home_next_action_complete_mission",
            "home_next_action_build_streak"
        )
        val arabicScript = Regex("[\\u0600-\\u06FF]")
        val formatSpec = Regex("%[0-9]+\\$[A-Za-z]")

        for (key in keys) {
            val en = "name=\"$key\">([^<]+)</string>".toRegex().find(enXml)
            val fa = "name=\"$key\">([^<]+)</string>".toRegex().find(faXml)
            assertNotNull("English resource missing key: $key", en)
            assertNotNull("Persian resource missing key: $key", fa)

            val enValue = en!!.groupValues[1]
            val faValue = fa!!.groupValues[1]
            assertTrue("English $key must not be blank", enValue.isNotBlank())
            assertTrue("Persian $key must not be blank", faValue.isNotBlank())

            assertFalse(
                "English advisory resource '$key' must never contain Persian script: $enValue",
                arabicScript.containsMatchIn(enValue)
            )
            assertFalse(
                "Persian advisory resource '$key' must not leak Latin script: $faValue",
                Regex("[A-Za-z]").containsMatchIn(formatSpec.replace(faValue, ""))
            )
        }

        val enComplete = "name=\"home_next_action_complete_mission\">([^<]+)</string>"
            .toRegex().find(enXml)!!.groupValues[1]
        val faComplete = "name=\"home_next_action_complete_mission\">([^<]+)</string>"
            .toRegex().find(faXml)!!.groupValues[1]
        assertTrue("English complete advisory must keep the mission title placeholder", enComplete.contains("%1\$s"))
        assertTrue("Persian complete advisory must keep the mission title placeholder", faComplete.contains("%1\$s"))
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 2. Runtime values are never sliced before presentation
    // ─────────────────────────────────────────────────────────────────────────────

    @Test
    fun todayTrack_rendersTheTruthfulRuntimeValue() {
        val today = todayRow()

        assertFalse(
            "TodayStatusRow must not slice the runtime track value before presentation",
            today.contains(".take(10)")
        )
        assertFalse(
            "No character-level truncation of the runtime track may remain",
            Regex("""primaryTrack\s*\?\.\s*take""").containsMatchIn(today)
        )
        assertTrue(
            "TodayStatusRow must bind the track directly from real Home state",
            today.contains("state.topMissions.firstOrNull()?.track")
        )
        assertTrue(
            "The track card must wrap a long value rather than cut it",
            today.contains("maxLines = 2")
        )
        assertTrue(
            "Constrained space must be handled at the Compose layer through ellipsis",
            today.contains("overflow = TextOverflow.Ellipsis")
        )
    }

    /** No Home surface may regress to slicing a runtime string before rendering it. */
    @Test
    fun noHomeSurfaceSlicesRuntimeStringsBeforePresentation() {
        val homeDir = locateDir("src/main/java/com/axiom/app/presentation/home")
        // Bounded slicing of collections ("show the top N missions") is a legitimate operation.
        // Slicing a display scalar is not: the runtime string must reach the Text layer intact
        // and let the Compose layout handle constrained space.
        val collectionReceivers = setOf(
            "topMissions", "recentFeed", "base", "missions", "dungeons", "messages"
        )
        val takePattern = Regex("""([A-Za-z_][A-Za-z0-9_]*)\s*\??\.\s*take\(\s*\d+\s*\)""")
        val offenders = homeDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .flatMap { file ->
                takePattern.findAll(file.readText())
                    .map { it.groupValues[1] }
                    .filter { receiver -> receiver !in collectionReceivers }
                    .map { receiver -> "${file.name}: slices '$receiver'" }
            }
            .toList()
        assertTrue(
            "Home must not truncate runtime strings before presentation. Offenders: ${offenders.joinToString("; ")}",
            offenders.isEmpty()
        )
    }
}
