package com.axiom.app.presentation.home

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-UIUX-02 Home module-disposition governance guard (Issue #85, repair round).
 *
 * Product Owner decision recorded for this packet:
 *
 * - AX-013 Dungeons (HIDE / G7), AX-016 Skill Tree (HIDE / G7) and AX-018 Leagues
 *   (FREEZE / G7) must not be reachable from Home.
 * - AX-015 Daily Check-in (VALIDATE / G7) and AX-022 Weekly Analytics
 *   (UNLOCK_LATER / G4|G5) may remain as existing secondary progressive-disclosure
 *   links for this packet only.
 * - This is not Gate activation, feature expansion, or a change to MODULE_DISPOSITION.
 *
 * This guard derives the restricted route set from the canonical
 * `docs/product/MODULE_DISPOSITION.csv` (every module disposed `HIDE` or `FREEZE`, not a
 * hand-maintained Premium-only denylist), then proves the rendered Home surface exposes
 * none of them through navigation. It also proves the modules and their route definitions
 * still exist outside Home, i.e. the repair bounded Home exposure rather than deleting
 * capability. Canonical disposition documents are read-only inputs here.
 */
class HomeModuleDispositionContractTest {

    private data class ModuleRow(
        val id: String,
        val name: String,
        val disposition: String,
        val routes: List<String>
    )

    // ─────────────────────────────────────────────────────────────────────────────
    // Repository file resolution (unit tests may run from the module or repo root)
    // ─────────────────────────────────────────────────────────────────────────────

    private fun locate(relative: String): File =
        locatePath(relative, directories = false)
            ?: fail("Required repository file not found: $relative").let { error("unreachable") }

    private fun locateOrNull(relative: String): File? = locatePath(relative, directories = false)

    private fun locateDir(relative: String): File =
        locatePath(relative, directories = true)
            ?: fail("Required repository directory not found: $relative")
                .let { error("unreachable") }

    private fun locatePath(relative: String, directories: Boolean): File? {
        fun matches(candidate: File) = if (directories) candidate.isDirectory else candidate.isFile

        val direct = listOf(
            File(relative),
            File("app/$relative"),
            File("../$relative"),
            File("../../$relative")
        )
        direct.firstOrNull { matches(it) }?.let { return it }

        var current: File? = File(System.getProperty("user.dir") ?: ".").absoluteFile
        var depth = 0
        while (current != null && depth < 8) {
            val dir: File = current
            val candidate = File(dir, relative)
            if (matches(candidate)) return candidate
            current = dir.parentFile
            depth++
        }
        return null
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Canonical disposition input
    // ─────────────────────────────────────────────────────────────────────────────

    /** Minimal RFC-4180-style split; the disposition CSV quotes fields containing commas. */
    private fun parseCsvLine(line: String): List<String> {
        val out = mutableListOf<String>()
        val field = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' && inQuotes && i + 1 < line.length && line[i + 1] == '"' -> {
                    field.append('"')
                    i++
                }
                c == '"' -> inQuotes = !inQuotes
                c == ',' && !inQuotes -> {
                    out.add(field.toString())
                    field.setLength(0)
                }
                else -> field.append(c)
            }
            i++
        }
        out.add(field.toString())
        return out
    }

    private fun canonicalDisposition(): List<ModuleRow> {
        val file = locate("docs/product/MODULE_DISPOSITION.csv")
        val lines = file.readLines().filter { it.isNotBlank() }
        val header = parseCsvLine(lines.first())
        val idCol = header.indexOf("module_id")
        val nameCol = header.indexOf("canonical_module_name")
        val dispositionCol = header.indexOf("disposition")
        val routesCol = header.indexOf("current_routes")
        assertTrue("canonical disposition CSV must expose module_id", idCol >= 0)
        assertTrue("canonical disposition CSV must expose canonical_module_name", nameCol >= 0)
        assertTrue("canonical disposition CSV must expose disposition", dispositionCol >= 0)
        assertTrue("canonical disposition CSV must expose current_routes", routesCol >= 0)

        val rows = lines.drop(1).mapNotNull { line ->
            val cells = parseCsvLine(line)
            if (cells.size <= maxOf(dispositionCol, routesCol)) return@mapNotNull null
            ModuleRow(
                id = cells[idCol].trim(),
                name = cells[nameCol].trim(),
                disposition = cells[dispositionCol].trim().uppercase(),
                routes = cells[routesCol].split("|").map { it.trim() }.filter { it.isNotEmpty() }
            )
        }
        assertTrue("canonical disposition must parse at least 50 modules", rows.size >= 50)
        return rows
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Declared route surface (parsed from the canonical Screen declaration)
    // ─────────────────────────────────────────────────────────────────────────────

    private val screenDeclaration =
        Regex("""(?:object|data class)\s+(\w+)[^\n]*?:\s*Screen\("([^"]*)"\)""")

    /** normalized route string -> declared Screen names owning it. */
    private fun declaredRoutes(): Map<String, List<String>> {
        val source = locate("src/main/java/com/axiom/app/navigation/Screen.kt").readText()
        val normalized = source.replace(Regex("""\$\{encode\((\w+)\)\}"""), "{$1}")
        return screenDeclaration.findAll(normalized)
            .groupBy({ it.groupValues[2] }, { it.groupValues[1] })
    }

    /**
     * Route strings owned by modules canonically disposed `HIDE` or `FREEZE`, restricted to
     * routes that are actually declared in [com.axiom.app.navigation.Screen] (canonical
     * non-route entries such as `system share intent` are not navigable surfaces).
     */
    private fun restrictedRouteStrings(): Set<String> {
        val declared = declaredRoutes().keys
        return canonicalDisposition()
            .filter { it.disposition == "HIDE" || it.disposition == "FREEZE" }
            .flatMap { it.routes }
            .filter { it in declared }
            .toSet()
    }

    /**
     * Evidence-linked allowlist. AX-014 Physical Body Map is disposed `FREEZE` in canonical
     * disposition but carries a standing bounded Product Owner reactivation
     * (`DECISION-2026-09-11-AX014-BODY-MAP-REACTIVATION.md`) and its canonical
     * `navigation_reachability` is `PRIMARY_TAB`; Home's Body Status section linking into the
     * Body Map primary tab is therefore not a hidden-module exposure. The allowlist is
     * self-expiring: it only applies while that decision document is present and records AX-014.
     */
    private fun allowlistedRouteStrings(): Set<String> {
        val decision = locateOrNull(
            "docs/governance/DECISION-2026-09-11-AX014-BODY-MAP-REACTIVATION.md"
        ) ?: return emptySet()
        val recordsReactivation = decision.readText().contains("AX-014")
        return if (recordsReactivation) setOf("body_map") else emptySet()
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Home surface inventory
    // ─────────────────────────────────────────────────────────────────────────────

    private val navigationCall = Regex("""(?:onNavigate|safeNavigate)\(\s*"([^"]*)"\s*""")

    /**
     * Files that own or directly feed the rendered Home surface: the Home component package
     * plus the Home state provider whose `nextBestActionRoute` is rendered clickable on Home.
     */
    private fun homeSurfaceSourceFiles(): List<File> {
        val components = locateDir("src/main/java/com/axiom/app/presentation/home")
        val files = components.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
        val homeStateProvider = locateOrNull("src/main/java/com/axiom/app/ui/HomeViewModel.kt")
        val bottomNav = locateOrNull("src/main/java/com/axiom/app/ui/components/AwakenBottomNavBar.kt")
        return (files + listOfNotNull(homeStateProvider, bottomNav)).sortedBy { it.path }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Contract
    // ─────────────────────────────────────────────────────────────────────────────

    /** Detects every navigation reference to a restricted module inside one Home source file. */
    private fun detectRestrictedExposure(
        fileName: String,
        text: String,
        restricted: Set<String>,
        routeOwners: Map<String, List<String>>
    ): List<String> {
        val violations = mutableListOf<String>()
        for (route in restricted) {
            for (owner in routeOwners[route].orEmpty()) {
                if (text.contains("Screen.$owner.route")) {
                    violations += "$fileName references Screen.$owner.route ('$route')"
                }
            }
            for (match in navigationCall.findAll(text)) {
                if (match.groupValues[1] == route) {
                    violations += "$fileName navigates to raw route '$route'"
                }
            }
        }
        return violations
    }

    @Test
    fun homeSurfaceExposesNoHiddenOrFrozenModuleEntryPoint() {
        val restricted = restrictedRouteStrings() - allowlistedRouteStrings()
        assertTrue(
            "guard must derive restricted modules from canonical MODULE_DISPOSITION.csv",
            restricted.isNotEmpty()
        )

        val routeOwners = declaredRoutes()
        val scanned = homeSurfaceSourceFiles()
        assertTrue(
            "guard must actually scan the Home surface sources",
            scanned.any { it.name == "OperationalTracksSection.kt" } && scanned.size >= 10
        )

        val violations = scanned.flatMap { file ->
            detectRestrictedExposure(file.name, file.readText(), restricted, routeOwners)
        }

        assertTrue(
            "Home must not expose HIDE/FREEZE module entry points " +
                "(PO decision on Issue #85). Violations: ${violations.joinToString("; ")}",
            violations.isEmpty()
        )
    }

    /**
     * Negative control: the detector above must actually flag a known violation. Without this the
     * real-repository assertion could pass vacuously (e.g. an empty restricted set or a
     * mis-resolved route owner), which is exactly the failure mode a governance guard must not have.
     */
    @Test
    fun exposureDetectorFlagsKnownRestrictedRouteInSyntheticSource() {
        val restricted = restrictedRouteStrings() - allowlistedRouteStrings()
        val routeOwners = declaredRoutes()

        val synthetic = """
            // pre-repair shape of OperationalTracksSection.kt
            QuickLaunchItem(
                title = if (isFa) "لیگ‌ها" else "LEAGUES",
                iconRes = R.drawable.ic_nav_leagues,
                onClick = { onNavigate(Screen.Leagues.route) },
                modifier = Modifier.weight(1f)
            )
        """.trimIndent()

        val detected = detectRestrictedExposure(
            "SyntheticHomeSource.kt", synthetic, restricted, routeOwners
        )
        assertTrue(
            "detector must flag the pre-repair Leagues Home tile",
            detected.any { it.contains("Screen.Leagues.route") }
        )
        assertTrue(
            "detector must also flag raw literal navigation to a restricted route",
            detectRestrictedExposure(
                "SyntheticHomeSource.kt",
                "onNavigate(\"skill_tree\")",
                restricted,
                routeOwners
            ).isNotEmpty()
        )
    }

    @Test
    fun operationalTracksRendersExactlyTheTwoPoExceptedSecondaryLinks() {
        val source = locate(
            "src/main/java/com/axiom/app/presentation/home/components/OperationalTracksSection.kt"
        ).readText()

        assertTrue(
            "Operational Tracks must retain the AX-015 Daily Check-in secondary link",
            source.contains("onNavigate(Screen.DailyCheckin.route)")
        )
        assertTrue(
            "Operational Tracks must retain the AX-022 Weekly Analytics secondary link",
            source.contains("onNavigate(Screen.WeeklyAnalytics.route)")
        )

        val renderedTiles = Regex("""(?<!fun )QuickLaunchItem\(""").findAll(source).count()
        assertEquals(
            "Operational Tracks must render exactly two retained tiles, with no hidden " +
                "or blank placeholders left behind by the governance repair",
            2,
            renderedTiles
        )

        val balancedSlots = Regex("""Modifier\.weight\(1f\)""").findAll(source).count()
        assertEquals(
            "both retained tiles must share one balanced row (equal weight, no empty slot)",
            2,
            balancedSlots
        )
    }

    @Test
    fun restrictedModuleRoutesRemainDeclaredOutsideHome() {
        val declared = declaredRoutes().keys
        val restricted = restrictedRouteStrings()
        assertTrue("restricted route set must not be empty", restricted.isNotEmpty())
        for (route in restricted.sorted()) {
            assertTrue(
                "Home governance repair must remove Home exposure only — route definition " +
                    "'$route' must remain declared outside Home",
                route in declared
            )
        }
    }

    @Test
    fun primaryTabShellRemainsExactlyFiveCanonicalTabs() {
        val source = locate("src/main/java/com/axiom/app/ui/components/AwakenBottomNavBar.kt").readText()
        val tabBlock = source.substringAfter("val tabs = remember {").substringBefore("}")

        val canonicalOrder = listOf(
            "Screen.Missions",
            "Screen.BodyMap",
            "Screen.Home",
            "Screen.ShadowArmy",
            "Screen.Profile"
        )
        var previousIndex = -1
        for (tab in canonicalOrder) {
            val at = tabBlock.indexOf(tab)
            assertTrue("rendered dock source must declare the $tab primary tab", at > previousIndex)
            previousIndex = at
        }

        val tabCount = Regex("""Screen\.\w+""").findAll(tabBlock).count()
        assertEquals(
            "rendered dock must keep exactly five primary tabs",
            canonicalOrder.size,
            tabCount
        )
    }

    @Test
    fun laterGateSurfacesAreNotOrdinarilyRenderedOnHome() {
        val successContent = locate(
            "src/main/java/com/axiom/app/presentation/home/components/SuccessContent.kt"
        ).readText()

        val prohibitedSurfaces = listOf(
            "WeeklyReviewSection" to "AX-021 Weekly Review (G5)",
            "weekly_review_overdue" to "AX-021 Weekly Review banner (G5)",
            "MomentumStreakSection" to "AX-030 Momentum/Streak (G5)",
            "WeeklyChallengeSection" to "AX-030 Weekly Challenges (G5)",
            "BodyStatusSection" to "AX-014 Body Status recovery (G7)",
            "VitalsRow" to "AX-015 Vitals (G7)",
            "DailyHabitNudgeSection" to "AX-015 Habit nudge (G7)",
            "DailyOutcomesSection" to "Legacy fabricated daily outcomes"
        )

        for ((symbol, description) in prohibitedSurfaces) {
            assertTrue(
                "Home must not ordinarily render $description",
                !successContent.contains(symbol)
            )
        }
    }

    @Test
    fun focusActiveChipDoesNotNavigateToLeagues() {
        val mainScreen = locate("src/main/java/com/axiom/app/ui/MainScreen.kt").readText()
        val chipBlock = mainScreen.substringAfter("FocusActiveChip(").substringBefore("AwakenBottomNavBar")
        assertTrue(
            "FocusActiveChip on Home shell must not navigate to Screen.Leagues.route (AX-018 FREEZE)",
            !chipBlock.contains("Screen.Leagues.route")
        )
    }

    @Test
    fun homeScreenHasExactlyOneDominantPrimaryCta() {
        val heroCard = locate(
            "src/main/java/com/axiom/app/presentation/home/components/NextMissionHeroCard.kt"
        ).readText()
        val primaryCtaTags = Regex("""testTag\("home_primary_cta"\)""").findAll(heroCard).count()
        assertEquals(
            "NextMeaningfulMission hero card must host the home_primary_cta testTag",
            2,
            primaryCtaTags
        )

        val successContent = locate(
            "src/main/java/com/axiom/app/presentation/home/components/SuccessContent.kt"
        ).readText()
        val competingCtas = listOf(
            "burnout_ack_button",
            "weekly_review_start",
            "claim_bonus_button"
        )
        for (cta in competingCtas) {
            assertTrue(
                "SuccessContent must not contain competing primary CTA: $cta",
                !successContent.contains(cta)
            )
        }
    }

    @Test
    fun todayStatusRowBindsToRealG3State() {
        val todayRow = locate(
            "src/main/java/com/axiom/app/presentation/home/components/TodayStatusRow.kt"
        ).readText()
        assertTrue(
            "TodayStatusRow must bind to real G3 Home state",
            todayRow.contains("state.activeMissionsCount")
        )
        assertTrue(
            "TodayStatusRow must not access AX-015 Vitals or SharedPreferences",
            !todayRow.contains("getSharedPreferences") && !todayRow.contains("Vitals")
        )
    }

    @Test
    fun homeRenderedShellCannotReachForbiddenModules() {
        val forbiddenRoutes = listOf(
            "Screen.Dungeons.route",
            "Screen.SkillTree.route",
            "Screen.Leagues.route",
            "Screen.Premium.route"
        )

        // 1. All Home presentation and dock source files
        val homeSources = homeSurfaceSourceFiles()
        for (file in homeSources) {
            val text = file.readText()
            for (forbidden in forbiddenRoutes) {
                assertTrue(
                    "${file.name} must not reach $forbidden",
                    !text.contains(forbidden)
                )
            }
        }

        // 2. MainScreen Home shell (FocusActiveChip and BottomBar invocation)
        val mainScreen = locate("src/main/java/com/axiom/app/ui/MainScreen.kt").readText()
        val bottomBarBlock = mainScreen.substringAfter("bottomBar = {").substringBefore("AwakenNavGraph")
        for (forbidden in forbiddenRoutes) {
            assertTrue(
                "MainScreen Home bottomBar shell must not reach $forbidden",
                !bottomBarBlock.contains(forbidden)
            )
        }
    }
}
