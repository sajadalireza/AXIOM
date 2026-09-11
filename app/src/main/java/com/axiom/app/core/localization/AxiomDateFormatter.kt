package com.axiom.app.core.localization

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Canonical Date, Calendar, and Plural Localization Engine for AXIOM (G3-P6 / E2.6).
 *
 * Provides:
 * 1. Bidirectional conversion between Gregorian and Solar Hijri (Jalali) calendar systems.
 * 2. Localized month names and date strings for Persian (Farsi) and English.
 * 3. Persian numeral transliteration (0-9 -> ۰-۹).
 * 4. Localized relative and remaining time formatting (days, hours, minutes).
 */
object AxiomDateFormatter {

    data class JalaliDate(
        val year: Int,
        val month: Int,
        val day: Int
    )

    private val PERSIAN_MONTHS = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    private val ENGLISH_MONTHS_SHORT = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    private val ENGLISH_MONTHS_FULL = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /**
     * Converts any ASCII digits in [input] to Persian numerals (۰-۹).
     */
    fun toPersianDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(PERSIAN_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Astronomical conversion from Gregorian (year, month [1..12], day [1..31])
     * to Solar Hijri (Jalali) date.
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gyAdj = gy - 1600
        val gmAdj = gm - 1
        val gdAdj = gd - 1

        var gDayNo = 365 * gyAdj + (gyAdj + 3) / 4 - (gyAdj + 99) / 100 + (gyAdj + 399) / 400

        for (i in 0 until gmAdj) {
            gDayNo += gDaysInMonth[i]
        }
        if (gmAdj > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gdAdj

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0 until 11) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i + 1
                break
            }
            jDayNo -= jDaysInMonth[i]
        }
        val jd: Int
        if (jm == 0) {
            jm = 12
            jd = jDayNo + 1
        } else {
            jd = jDayNo + 1
        }

        return JalaliDate(year = jy, month = jm, day = jd)
    }

    /**
     * Formats an epoch timestamp in milliseconds to a localized date string.
     * When [isFa] is true, outputs Persian Solar Hijri with Persian numerals.
     */
    fun formatDate(
        epochMillis: Long,
        isFa: Boolean,
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        val cal = Calendar.getInstance(timeZone, Locale.ROOT).apply {
            timeInMillis = epochMillis
        }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)

        return if (isFa) {
            val jDate = gregorianToJalali(gy, gm, gd)
            val monthName = PERSIAN_MONTHS.getOrElse(jDate.month - 1) { "" }
            val raw = "${jDate.day} $monthName ${jDate.year}"
            toPersianDigits(raw)
        } else {
            val monthName = ENGLISH_MONTHS_SHORT.getOrElse(gm - 1) { "" }
            "$monthName $gd, $gy"
        }
    }

    /**
     * Formats a remaining duration into human-readable, localized tokens.
     */
    fun formatRemainingDuration(remainingMillis: Long, isFa: Boolean): String {
        if (remainingMillis <= 0L) {
            return if (isFa) "پایان یافته" else "Expired"
        }

        val totalMinutes = remainingMillis / (60 * 1000L)
        val totalHours = totalMinutes / 60
        val days = totalHours / 24
        val hours = totalHours % 24
        val minutes = totalMinutes % 60

        val text = when {
            days > 0 -> {
                if (isFa) {
                    "$days روز و $hours ساعت"
                } else {
                    if (days == 1L) "1 day $hours hrs" else "$days days $hours hrs"
                }
            }
            hours > 0 -> {
                if (isFa) {
                    "$hours ساعت و $minutes دقیقه"
                } else {
                    if (hours == 1L) "1 hr $minutes min" else "$hours hrs $minutes min"
                }
            }
            minutes > 0 -> {
                if (isFa) {
                    "$minutes دقیقه"
                } else {
                    if (minutes == 1L) "1 minute" else "$minutes minutes"
                }
            }
            else -> {
                if (isFa) "کمتر از ۱ دقیقه" else "< 1 minute"
            }
        }

        return if (isFa) toPersianDigits(text) else text
    }

    /**
     * Returns the localized Persian or English month name for a given 1-based month index.
     */
    fun getMonthName(month: Int, isFa: Boolean): String {
        return if (isFa) {
            PERSIAN_MONTHS.getOrElse(month - 1) { "" }
        } else {
            ENGLISH_MONTHS_FULL.getOrElse(month - 1) { "" }
        }
    }
}
