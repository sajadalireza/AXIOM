package com.axiom.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * G3-P5 Design System Contract Test.
 *
 * Verifies:
 * 1. Monotonic scales of AxiomSpacing, AxiomRadius, AxiomBorder, AxiomElevation, AxiomMotion.
 * 2. Backward compatibility aliases (Spacing, Border, Elevation, Anim) match canonical tokens.
 * 3. Semantic status color roles (statusSuccess, statusWarning, statusError, statusInfo)
 *    are defined, specified, and distinct across dark and light palettes.
 * 4. Touched Home components contain zero hardcoded Color(0x...) literals.
 */
class DesignSystemContractTest {

    @Test
    fun testSpacingScaleIsStrictlyMonotonic() {
        assertTrue(AxiomSpacing.xxs < AxiomSpacing.xs)
        assertTrue(AxiomSpacing.xs < AxiomSpacing.s)
        assertTrue(AxiomSpacing.s < AxiomSpacing.sm)
        assertTrue(AxiomSpacing.sm < AxiomSpacing.m)
        assertTrue(AxiomSpacing.m < AxiomSpacing.l)
        assertTrue(AxiomSpacing.l < AxiomSpacing.xl)
        assertTrue(AxiomSpacing.xl < AxiomSpacing.xxl)
        assertTrue(AxiomSpacing.xxl < AxiomSpacing.xxxl)
    }

    @Test
    fun testRadiusScaleIsStrictlyMonotonic() {
        assertTrue(AxiomRadius.none < AxiomRadius.xs)
        assertTrue(AxiomRadius.xs < AxiomRadius.s)
        assertTrue(AxiomRadius.s < AxiomRadius.m)
        assertTrue(AxiomRadius.m < AxiomRadius.l)
        assertTrue(AxiomRadius.l < AxiomRadius.xl)
        assertTrue(AxiomRadius.xl < AxiomRadius.xxl)
        assertTrue(AxiomRadius.xxl < AxiomRadius.round)
    }

    @Test
    fun testBorderScaleIsStrictlyMonotonic() {
        assertTrue(AxiomBorder.none < AxiomBorder.hairline)
        assertTrue(AxiomBorder.hairline < AxiomBorder.thin)
        assertTrue(AxiomBorder.thin < AxiomBorder.medium)
        assertTrue(AxiomBorder.medium < AxiomBorder.thick)
        assertTrue(AxiomBorder.thick < AxiomBorder.heavy)
    }

    @Test
    fun testElevationScaleIsStrictlyMonotonic() {
        assertTrue(AxiomElevation.none < AxiomElevation.tiny)
        assertTrue(AxiomElevation.tiny < AxiomElevation.low)
        assertTrue(AxiomElevation.low < AxiomElevation.medium)
        assertTrue(AxiomElevation.medium < AxiomElevation.high)
    }

    @Test
    fun testMotionDurationsArePositiveAndOrdered() {
        assertTrue(AxiomMotion.fastMs < AxiomMotion.normalMs)
        assertTrue(AxiomMotion.normalMs < AxiomMotion.slowMs)
        assertTrue(AxiomMotion.slowMs < AxiomMotion.pulseMs)
        assertTrue(AxiomMotion.pulseMs < AxiomMotion.shimmerMs)
    }

    @Suppress("DEPRECATION")
    @Test
    fun testBackwardCompatibilityAliasesPreserveValues() {
        assertEquals(AxiomSpacing.xs, Spacing.xs)
        assertEquals(AxiomSpacing.s, Spacing.s)
        assertEquals(AxiomSpacing.m, Spacing.m)
        assertEquals(AxiomSpacing.l, Spacing.l)
        assertEquals(AxiomSpacing.xl, Spacing.xl)
        assertEquals(AxiomSpacing.xxl, Spacing.xxl)

        assertEquals(AxiomBorder.thin, Border.Thin)
        assertEquals(AxiomBorder.heavy, Border.Thick)

        assertEquals(AxiomElevation.none, Elevation.None)
        assertEquals(AxiomElevation.low, Elevation.Low)
        assertEquals(AxiomElevation.medium, Elevation.Medium)
        assertEquals(AxiomElevation.high, Elevation.High)

        assertEquals(AxiomMotion.fastMs, Anim.ShortDuration)
        assertEquals(AxiomMotion.normalMs, Anim.MediumDuration)
        assertEquals(AxiomMotion.slowMs, Anim.LongDuration)
    }

    @Test
    fun testSemanticStatusColorsAreDefinedAndDistinct() {
        // Dark palette
        assertTrue(AxiomDarkColors.statusSuccess.isSpecified)
        assertTrue(AxiomDarkColors.statusWarning.isSpecified)
        assertTrue(AxiomDarkColors.statusError.isSpecified)
        assertTrue(AxiomDarkColors.statusInfo.isSpecified)

        assertNotEquals(AxiomDarkColors.statusSuccess, AxiomDarkColors.statusError)
        assertNotEquals(AxiomDarkColors.statusWarning, AxiomDarkColors.statusInfo)
        assertNotEquals(AxiomDarkColors.statusSuccess, AxiomDarkColors.voidBlack)

        // Light palette
        assertTrue(AxiomLightColors.statusSuccess.isSpecified)
        assertTrue(AxiomLightColors.statusWarning.isSpecified)
        assertTrue(AxiomLightColors.statusError.isSpecified)
        assertTrue(AxiomLightColors.statusInfo.isSpecified)

        assertNotEquals(AxiomLightColors.statusSuccess, AxiomLightColors.statusError)
        assertNotEquals(AxiomLightColors.statusWarning, AxiomLightColors.statusInfo)
        assertNotEquals(AxiomLightColors.statusSuccess, AxiomLightColors.voidBlack)

        // Static aliases
        assertEquals(AxiomDarkColors.statusSuccess, StatusSuccess)
        assertEquals(AxiomDarkColors.statusWarning, StatusWarning)
        assertEquals(AxiomDarkColors.statusError, StatusError)
        assertEquals(AxiomDarkColors.statusInfo, StatusInfo)
    }

    @Test
    fun testNoRawColorLiteralsInTouchedHomeComponents() {
        val targetFiles = listOf(
            "HunterHeaderSection.kt",
            "CountdownBannerSection.kt",
            "OperationalTracksSection.kt",
            "NextMissionHeroCard.kt"
        )

        val baseDir = locateHomeComponentsDir()
        val hexColorRegex = Regex("""Color\s*\(\s*0x[0-9a-fA-F]+""")

        for (fileName in targetFiles) {
            val file = File(baseDir, fileName)
            assertTrue("Expected file $fileName to exist at ${file.absolutePath}", file.isFile)

            val lines = file.readLines()
            for ((index, line) in lines.withIndex()) {
                val match = hexColorRegex.find(line)
                if (match != null) {
                    fail("Found hardcoded color literal '${match.value}' in $fileName at line ${index + 1}: $line")
                }
            }
        }
    }

    private fun locateHomeComponentsDir(): File {
        val relPath = "app/src/main/java/com/axiom/app/presentation/home/components"
        val altPath = "src/main/java/com/axiom/app/presentation/home/components"
        val candidates = listOf(
            File(relPath),
            File(altPath),
            File(System.getProperty("user.dir") ?: ".", relPath),
            File(System.getProperty("user.dir") ?: ".", altPath)
        )
        val dir = candidates.firstOrNull { it.isDirectory }
        if (dir == null) {
            fail("Could not locate Home components directory. Checked: $candidates")
            throw IllegalStateException("Unreachable")
        }
        return dir
    }
}
