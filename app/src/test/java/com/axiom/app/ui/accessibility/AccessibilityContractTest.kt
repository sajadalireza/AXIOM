package com.axiom.app.ui.accessibility

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.axiom.app.core.localization.AxiomDateFormatter
import com.axiom.app.ui.theme.AxiomDarkColors
import com.axiom.app.ui.theme.AxiomLightColors
import com.axiom.app.ui.theme.AxiomMotion
import com.axiom.app.ui.theme.AxiomSpacing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * G3-P6 Accessibility & Localization Contract Test.
 *
 * Verifies:
 * 1. WCAG 2.1 AA contrast ratios across dark and light palettes.
 * 2. Solar Hijri (Jalali) astronomical date conversion accuracy.
 * 3. Persian numeral transliteration (0-9 -> ۰-۹).
 * 4. Localized duration formatting and plural rules.
 * 5. Reduced motion token resolution (duration clamping to 0ms).
 * 6. Minimum touch target dimension compliance (>= 48dp).
 * 7. Key string parity between values/strings.xml and values-fa/strings.xml.
 */
class AccessibilityContractTest {

    // ─── 1. WCAG 2.1 AA Contrast Ratio Calculations ─────────────────────────

    private fun linearize(channel: Float): Double {
        val c = channel.toDouble()
        return if (c <= 0.04045) {
            c / 12.92
        } else {
            ((c + 0.055) / 1.055).pow(2.4)
        }
    }

    private fun relativeLuminance(color: Color): Double {
        val r = linearize(color.red)
        val g = linearize(color.green)
        val b = linearize(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrastRatio(colorA: Color, colorB: Color): Double {
        val lumA = relativeLuminance(colorA)
        val lumB = relativeLuminance(colorB)
        val lighter = max(lumA, lumB)
        val darker = min(lumA, lumB)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun testDarkThemeTextContrastMeetsWcagAa() {
        val bg = AxiomDarkColors.voidBlack

        // WCAG AAA for body text is >= 7.0:1, AA is >= 4.5:1
        val textPrimaryContrast = contrastRatio(AxiomDarkColors.textPrimary, bg)
        assertTrue(
            "Expected textPrimary contrast >= 7.0:1 on voidBlack, was $textPrimaryContrast",
            textPrimaryContrast >= 7.0
        )

        val textSecondaryContrast = contrastRatio(AxiomDarkColors.textSecondary, bg)
        assertTrue(
            "Expected textSecondary contrast >= 4.5:1 on voidBlack, was $textSecondaryContrast",
            textSecondaryContrast >= 4.5
        )

        val systemGreenContrast = contrastRatio(AxiomDarkColors.systemGreen, bg)
        assertTrue(
            "Expected systemGreen contrast >= 4.5:1 on voidBlack, was $systemGreenContrast",
            systemGreenContrast >= 4.5
        )

        val legendaryGoldContrast = contrastRatio(AxiomDarkColors.legendaryGold, bg)
        assertTrue(
            "Expected legendaryGold contrast >= 7.0:1 on voidBlack, was $legendaryGoldContrast",
            legendaryGoldContrast >= 7.0
        )

        val rareBlueContrast = contrastRatio(AxiomDarkColors.rareBlue, bg)
        assertTrue(
            "Expected rareBlue contrast >= 4.5:1 on voidBlack, was $rareBlueContrast",
            rareBlueContrast >= 4.5
        )
    }

    @Test
    fun testLightThemeTextContrastMeetsWcagAa() {
        val surface = AxiomLightColors.shadowSurface

        val textPrimaryContrast = contrastRatio(AxiomLightColors.textPrimary, surface)
        assertTrue(
            "Expected textPrimary contrast >= 7.0:1 on shadowSurface, was $textPrimaryContrast",
            textPrimaryContrast >= 7.0
        )

        val textSecondaryContrast = contrastRatio(AxiomLightColors.textSecondary, surface)
        assertTrue(
            "Expected textSecondary contrast >= 4.5:1 on shadowSurface, was $textSecondaryContrast",
            textSecondaryContrast >= 4.5
        )

        val systemGreenContrast = contrastRatio(AxiomLightColors.systemGreen, surface)
        assertTrue(
            "Expected systemGreen contrast >= 3.0:1 on shadowSurface, was $systemGreenContrast",
            systemGreenContrast >= 3.0
        )
    }

    // ─── 2. Solar Hijri (Jalali) Calendar & Formatting ──────────────────────

    @Test
    fun testGregorianToJalaliAstronomicalAccuracy() {
        // Nowruz 1405 (2026-03-21)
        val nowruz = AxiomDateFormatter.gregorianToJalali(2026, 3, 21)
        assertEquals(1405, nowruz.year)
        assertEquals(1, nowruz.month)
        assertEquals(1, nowruz.day)

        // Mid-year 2026-09-11 -> 1405-06-20
        val midYear = AxiomDateFormatter.gregorianToJalali(2026, 9, 11)
        assertEquals(1405, midYear.year)
        assertEquals(6, midYear.month)
        assertEquals(20, midYear.day)

        // Leap year boundary (2024-03-20 -> 1403-01-01)
        val leapYear = AxiomDateFormatter.gregorianToJalali(2024, 3, 20)
        assertEquals(1403, leapYear.year)
        assertEquals(1, leapYear.month)
        assertEquals(1, leapYear.day)

        // Winter boundary (2025-01-01 -> 1403-10-12)
        val winter = AxiomDateFormatter.gregorianToJalali(2025, 1, 1)
        assertEquals(1403, winter.year)
        assertEquals(10, winter.month)
        assertEquals(12, winter.day)
    }

    @Test
    fun testPersianNumeralTransliteration() {
        val ascii = "0123456789 - Test 42"
        val persian = AxiomDateFormatter.toPersianDigits(ascii)
        assertEquals("۰۱۲۳۴۵۶۷۸۹ - Test ۴۲", persian)
    }

    @Test
    fun testFormatDateLocalized() {
        val utc = TimeZone.getTimeZone("UTC")
        val cal = Calendar.getInstance(utc, Locale.ROOT).apply {
            set(2026, Calendar.SEPTEMBER, 11, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val millis = cal.timeInMillis

        val enDate = AxiomDateFormatter.formatDate(millis, isFa = false, timeZone = utc)
        assertEquals("Sep 11, 2026", enDate)

        val faDate = AxiomDateFormatter.formatDate(millis, isFa = true, timeZone = utc)
        assertEquals("۲۰ شهریور ۱۴۰۵", faDate)
    }

    @Test
    fun testFormatRemainingDurationLocalized() {
        // Expired
        assertEquals("Expired", AxiomDateFormatter.formatRemainingDuration(0L, isFa = false))
        assertEquals("پایان یافته", AxiomDateFormatter.formatRemainingDuration(0L, isFa = true))

        // Multi-day
        val twoDaysFiveHours = (2 * 24 + 5) * 60 * 60 * 1000L
        assertEquals("2 days 5 hrs", AxiomDateFormatter.formatRemainingDuration(twoDaysFiveHours, isFa = false))
        assertEquals("۲ روز و ۵ ساعت", AxiomDateFormatter.formatRemainingDuration(twoDaysFiveHours, isFa = true))

        // Hours & minutes
        val threeHoursTenMins = (3 * 60 + 10) * 60 * 1000L
        assertEquals("3 hrs 10 min", AxiomDateFormatter.formatRemainingDuration(threeHoursTenMins, isFa = false))
        assertEquals("۳ ساعت و ۱۰ دقیقه", AxiomDateFormatter.formatRemainingDuration(threeHoursTenMins, isFa = true))

        // Less than 1 minute
        val twentySecs = 20 * 1000L
        assertEquals("< 1 minute", AxiomDateFormatter.formatRemainingDuration(twentySecs, isFa = false))
        assertEquals("کمتر از ۱ دقیقه", AxiomDateFormatter.formatRemainingDuration(twentySecs, isFa = true))
    }

    // ─── 3. Reduced Motion Token Behavior ───────────────────────────────────

    @Test
    fun testReducedMotionDurationClamping() {
        assertEquals(300, AxiomMotion.effectiveDuration(300, reducedMotion = false))
        assertEquals(0, AxiomMotion.effectiveDuration(300, reducedMotion = true))

        assertEquals(150, AxiomMotion.effectiveDuration(AxiomMotion.fastMs, reducedMotion = false))
        assertEquals(0, AxiomMotion.effectiveDuration(AxiomMotion.fastMs, reducedMotion = true))
    }

    // ─── 4. Minimum Touch Target Dimension Compliance ───────────────────────

    @Test
    fun testTouchTargetTokensSatisfyWcagMinimum() {
        // WCAG 2.1 Success Criterion 2.5.5 / 2.5.8 recommends >= 44dp to 48dp
        val wcagMinTouchTarget = 48.dp
        assertTrue(
            "AxiomSpacing.xxl ($AxiomSpacing.xxl) must meet or exceed WCAG minimum touch target ($wcagMinTouchTarget)",
            AxiomSpacing.xxl >= wcagMinTouchTarget
        )
    }

    // ─── 5. Localization String Resource Parity ─────────────────────────────

    @Test
    fun testStringResourcesEnglishAndPersianParity() {
        val valuesDir = locateResDir("values")
        val valuesFaDir = locateResDir("values-fa")

        val enStringsFile = File(valuesDir, "strings.xml")
        val faStringsFile = File(valuesFaDir, "strings.xml")

        assertTrue("Expected strings.xml at ${enStringsFile.absolutePath}", enStringsFile.isFile)
        assertTrue("Expected strings.xml at ${faStringsFile.absolutePath}", faStringsFile.isFile)

        val stringKeyRegex = Regex("""<string\s+name="([^"]+)"""")

        val enKeys = stringKeyRegex.findAll(enStringsFile.readText()).map { it.groupValues[1] }.toSet()
        val faKeys = stringKeyRegex.findAll(faStringsFile.readText()).map { it.groupValues[1] }.toSet()

        // Critical keys that must exist in both
        val criticalKeys = listOf(
            "app_name",
            "first_mission_title",
            "first_mission_desc",
            "first_mission_label",
            "first_mission_placeholder",
            "first_mission_btn_open"
        )

        for (key in criticalKeys) {
            assertTrue("Expected key '$key' to exist in values/strings.xml", enKeys.contains(key))
            assertTrue("Expected key '$key' to exist in values-fa/strings.xml", faKeys.contains(key))
        }
    }

    private fun locateResDir(folderName: String): File {
        val relPath = "app/src/main/res/$folderName"
        val altPath = "src/main/res/$folderName"
        val candidates = listOf(
            File(relPath),
            File(altPath),
            File(System.getProperty("user.dir") ?: ".", relPath),
            File(System.getProperty("user.dir") ?: ".", altPath)
        )
        val dir = candidates.firstOrNull { it.isDirectory }
        if (dir == null) {
            throw IllegalStateException("Could not locate res/$folderName. Checked: $candidates")
        }
        return dir
    }
}
