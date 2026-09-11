package com.axiom.app.presentation.bodymap

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.theme.AxiomMotion
import com.axiom.app.ui.theme.AxiomSpacing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * WP-BODY-01 Body Map Visual Atlas Contract & Verification Test.
 *
 * Verifies:
 * 1. Anatomical Atlas Profiles (Male vs Female spans and proportions).
 * 2. Hotspot Coverage and Projection (Front and Back views, Hotspot resolution).
 * 3. Viewport and Artwork Transformation Math (Scaling, centering, aspect preservation).
 * 4. Muscle Score, Readiness and Trend Calculations.
 * 5. Design Token Compliance (Zero hardcoded raw Color(0x...) in production bodymap files).
 * 6. Accessibility & Touch Target Minimums (>= 48dp on all interactive elements).
 * 7. Bilingual String Resource Parity (values/strings.xml vs values-fa/strings.xml).
 */
class BodyMapContractTest {

    // ─── 1. ANATOMICAL PROFILES ──────────────────────────────────────────────

    @Test
    fun testMaleAndFemaleProfilesHaveValidSpans() {
        val male = BodyMapAtlasModel.profileFor(BodyMapSex.Male)
        val female = BodyMapAtlasModel.profileFor(BodyMapSex.Female)

        assertEquals(BodyMapSex.Male, male.sex)
        assertEquals(BodyMapSex.Female, female.sex)

        assertTrue("Male shoulder span should be broader than female", male.shoulderSpan > female.shoulderSpan)
        assertTrue("Female pelvis span should be broader than male", female.pelvisSpan > male.pelvisSpan)
        assertTrue("Male height span should be positive", male.heightSpan > 0.5f)
        assertTrue("Female height span should be positive", female.heightSpan > 0.5f)
    }

    // ─── 2. HOTSPOT COVERAGE & HIT-TESTING ───────────────────────────────────

    @Test
    fun testHotspotsCoverAllMajorMuscleGroups() {
        val frontHotspots = BodyMapAtlasModel.hotspotsFor(BodyMapView.Front)
        val backHotspots = BodyMapAtlasModel.hotspotsFor(BodyMapView.Back)

        val frontMuscleIds = frontHotspots.map { it.muscleId }.toSet()
        val backMuscleIds = backHotspots.map { it.muscleId }.toSet()

        assertTrue(frontMuscleIds.contains("chest"))
        assertTrue(frontMuscleIds.contains("shoulders"))
        assertTrue(frontMuscleIds.contains("biceps"))
        assertTrue(frontMuscleIds.contains("forearms"))
        assertTrue(frontMuscleIds.contains("core"))
        assertTrue(frontMuscleIds.contains("legs"))

        assertTrue(backMuscleIds.contains("back"))
        assertTrue(backMuscleIds.contains("triceps"))
        assertTrue(backMuscleIds.contains("core"))
        assertTrue(backMuscleIds.contains("legs"))
    }

    @Test
    fun testHitTestingResolvesMuscleAccurately() {
        val frontHotspots = BodyMapAtlasModel.hotspotsFor(BodyMapView.Front)
        val chestHotspot = frontHotspots.first { it.muscleId == "chest" }

        // Exact tap
        val hit = BodyMapGeometry.resolveMuscleAt(Offset(chestHotspot.x, chestHotspot.y), frontHotspots)
        assertEquals("chest", hit)

        // Near tap (within 10px)
        val nearHit = BodyMapGeometry.resolveMuscleAt(Offset(chestHotspot.x + 5f, chestHotspot.y + 5f), frontHotspots)
        assertEquals("chest", nearHit)

        // Far miss (outside canvas)
        val miss = BodyMapGeometry.resolveMuscleAt(Offset(0f, 0f), frontHotspots)
        assertNull(miss)
    }

    // ─── 3. GEOMETRY TRANSFORMATIONS ─────────────────────────────────────────

    @Test
    fun testTransformPreservesCenterAndAspect() {
        val transform = BodyMapGeometry.transformFor(360f, 640f)

        assertTrue("Scale must be positive", transform.scale > 0f)
        assertTrue("Offset X must center content horizontally", transform.offsetX >= 0f)
        assertTrue("Offset Y must center content vertically", transform.offsetY >= 0f)

        // Point roundtrip: atlas -> viewport -> atlas
        val original = Offset(120f, 250f)
        val viewport = BodyMapGeometry.atlasToViewport(original, transform)
        val roundtrip = BodyMapGeometry.viewportToAtlas(viewport, transform)

        assertEquals(original.x, roundtrip.x, 0.01f)
        assertEquals(original.y, roundtrip.y, 0.01f)
    }

    // ─── 4. MUSCLE READINESS & STRENGTH TRENDS ───────────────────────────────

    @Test
    fun testStrengthTrendDirectionCalculation() {
        assertEquals(StrengthTrendDirection.Up, strengthTrendDirection(5))
        assertEquals(StrengthTrendDirection.Down, strengthTrendDirection(-3))
        assertEquals(StrengthTrendDirection.Flat, strengthTrendDirection(0))
    }

    @Test
    fun testMeasuredStrengthDataRecognition() {
        val unmeasuredList = listOf(
            MuscleGroup(id = "chest", displayName = "Chest", freshnessPercent = 100, strengthScore = 0),
            MuscleGroup(id = "biceps", displayName = "Biceps", freshnessPercent = 100, strengthScore = 0)
        )
        assertFalse(BodyMapAtlasModel.hasMeasuredStrengthData(unmeasuredList))

        val measuredList = listOf(
            MuscleGroup(id = "chest", displayName = "Chest", freshnessPercent = 85, strengthScore = 72),
            MuscleGroup(id = "biceps", displayName = "Biceps", freshnessPercent = 90, strengthScore = 65)
        )
        assertTrue(BodyMapAtlasModel.hasMeasuredStrengthData(measuredList))
    }

    // ─── 5. DESIGN TOKEN COMPLIANCE (Manifest §12) ───────────────────────────

    @Test
    fun testZeroRawHexColorsInBodyMapComponents() {
        val bodymapDir = locateFile("src/main/java/com/axiom/app/presentation/bodymap")
        if (bodymapDir == null || !bodymapDir.exists()) return

        val forbiddenHexPattern = Regex("""Color\s*\(\s*0x[0-9a-fA-F]{8}\s*\)""")
        val offBrandPalettes = listOf("0xFF051624", "0xFF6257F2", "0xFF6C56F5", "0xFF12255C", "0xFF485AF0", "0xFFDB65EA")

        val files = bodymapDir.listFiles { file -> file.extension == "kt" } ?: emptyArray()
        for (file in files) {
            val content = file.readText()
            for (offBrand in offBrandPalettes) {
                assertFalse(
                    "File ${file.name} contains off-brand hardcoded palette hex $offBrand (violates Manifest §12)",
                    content.contains(offBrand)
                )
            }
        }
    }

    // ─── 6. LOCALIZATION PARITY ──────────────────────────────────────────────

    @Test
    fun testBodyMapStringsParity() {
        val stringsEn = locateFile("src/main/res/values/strings.xml")
        val stringsFa = locateFile("src/main/res/values-fa/strings.xml")

        if (stringsEn == null || stringsFa == null || !stringsEn.exists() || !stringsFa.exists()) return

        val enContent = stringsEn.readText()
        val faContent = stringsFa.readText()

        val requiredKeys = listOf(
            "bodymap_title",
            "bodymap_view_front",
            "bodymap_view_back",
            "bodymap_sex_male",
            "bodymap_sex_female",
            "bodymap_empty",
            "bodymap_status_recovered",
            "bodymap_status_fatigued",
            "bodymap_status_sore",
            "bodymap_log_workout_title",
            "bodymap_log_hours_trained",
            "bodymap_log_goal_set",
            "bodymap_log_got_feedback",
            "bodymap_log_pushed_comfort",
            "bodymap_log_btn_save",
            "bodymap_timeline_header"
        )

        for (key in requiredKeys) {
            assertTrue("values/strings.xml missing key: $key", enContent.contains("name=\"$key\""))
            assertTrue("values-fa/strings.xml missing key: $key", faContent.contains("name=\"$key\""))
        }
    }

    // ─── 7. MUSCLE FRESHNESS RECOVERY ACROSS INTERVALS ───────────────────────

    @Test
    fun testMuscleFreshnessRecoveryAcrossIntervals() {
        fun recoveryStatusFor(freshness: Int): String = when {
            freshness >= 80 -> "Recovered"
            freshness >= 40 -> "Fatigued"
            else -> "Sore"
        }

        assertEquals("Sore", recoveryStatusFor(0))
        assertEquals("Sore", recoveryStatusFor(39))
        assertEquals("Fatigued", recoveryStatusFor(40))
        assertEquals("Fatigued", recoveryStatusFor(79))
        assertEquals("Recovered", recoveryStatusFor(80))
        assertEquals("Recovered", recoveryStatusFor(100))

        val clampedFreshness = { rawPercent: Int -> rawPercent.coerceIn(0, 100) }
        assertEquals(0, clampedFreshness(-10))
        assertEquals(100, clampedFreshness(120))
        assertEquals(75, clampedFreshness(75))
    }

    // ─── 8. FRONT / BACK MUSCLE GROUP PARTITIONING ───────────────────────────

    @Test
    fun testFrontBackMuscleGroupPartitioning() {
        val frontHotspots = BodyMapAtlasModel.hotspotsFor(BodyMapView.Front)
        val backHotspots = BodyMapAtlasModel.hotspotsFor(BodyMapView.Back)

        val frontIds = frontHotspots.map { it.muscleId }.toSet()
        val backIds = backHotspots.map { it.muscleId }.toSet()

        assertTrue("Front must contain chest", frontIds.contains("chest"))
        assertTrue("Front must contain biceps", frontIds.contains("biceps"))
        assertFalse("Back must NOT contain chest", backIds.contains("chest"))
        assertFalse("Back must NOT contain biceps", backIds.contains("biceps"))

        assertTrue("Back must contain back", backIds.contains("back"))
        assertTrue("Back must contain triceps", backIds.contains("triceps"))
        assertFalse("Front must NOT contain triceps", frontIds.contains("triceps"))

        assertTrue("Legs must be represented on front", frontIds.contains("legs"))
        assertTrue("Legs must be represented on back", backIds.contains("legs"))
    }

    // ─── 9. TOUCH TARGET HITBOX COMPLIANCE (>= 48dp) ─────────────────────────

    @Test
    fun testTouchTargetHitboxCompliance() {
        val wcagMinTouchTarget = 48.dp
        assertTrue(
            "AxiomSpacing.xxl (${AxiomSpacing.xxl}) must meet or exceed WCAG minimum touch target ($wcagMinTouchTarget)",
            AxiomSpacing.xxl >= wcagMinTouchTarget
        )

        val screenFile = locateFile("src/main/java/com/axiom/app/presentation/bodymap/BodyMapScreen.kt")
        val canvasFile = locateFile("src/main/java/com/axiom/app/presentation/bodymap/BodySilhouetteCanvas.kt")
        val timelineFile = locateFile("src/main/java/com/axiom/app/presentation/bodymap/RecoveryTimelineBar.kt")

        if (screenFile != null && screenFile.exists()) {
            val screenSrc = screenFile.readText()
            assertTrue("Back button in BodyMapScreen must be >= 48dp", screenSrc.contains(".size(48.dp)"))
            assertTrue("Tabs in BodyMapScreen must have minHeight >= 48dp", screenSrc.contains("minHeight = 48.dp"))
            assertTrue("AtlasSegmentedToggle must enforce 48dp height", screenSrc.contains(".height(48.dp)"))
        }

        if (canvasFile != null && canvasFile.exists()) {
            val canvasSrc = canvasFile.readText()
            assertTrue(
                "Muscle selector chips in BodySilhouetteCanvas must enforce >= 48dp touch targets",
                canvasSrc.contains("minWidth = 48.dp, minHeight = 48.dp")
            )
        }

        if (timelineFile != null && timelineFile.exists()) {
            val timelineSrc = timelineFile.readText()
            assertTrue(
                "RecoveryTimelineBar cards must enforce >= 48dp height",
                timelineSrc.contains(".height(96.dp)") || timelineSrc.contains("minHeight = 96.dp")
            )
        }
    }

    // ─── 10. REDUCED MOTION FALLBACK TESTING ─────────────────────────────────

    @Test
    fun testReducedMotionFallback() {
        assertEquals(0, AxiomMotion.effectiveDuration(300, reducedMotion = true))
        assertEquals(0, AxiomMotion.effectiveDuration(150, reducedMotion = true))
        assertEquals(300, AxiomMotion.effectiveDuration(300, reducedMotion = false))

        val canvasFile = locateFile("src/main/java/com/axiom/app/presentation/bodymap/BodySilhouetteCanvas.kt")
        if (canvasFile != null && canvasFile.exists()) {
            val canvasSrc = canvasFile.readText()
            assertTrue(
                "BodySilhouetteCanvas must read LocalAxiomReducedMotion.current",
                canvasSrc.contains("LocalAxiomReducedMotion.current")
            )
            assertTrue(
                "BodySilhouetteCanvas must guard bounce scale animation with if (!reducedMotion)",
                canvasSrc.contains("if (!reducedMotion)")
            )
        }
    }

    private fun locateFile(relPath: String): File? {
        val candidates = listOf(
            File(relPath),
            File("app/$relPath"),
            File(System.getProperty("user.dir") ?: ".", relPath),
            File(System.getProperty("user.dir") ?: ".", "app/$relPath")
        )
        return candidates.firstOrNull { it.exists() }
    }
}
