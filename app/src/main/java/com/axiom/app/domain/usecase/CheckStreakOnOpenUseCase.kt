package com.axiom.app.domain.usecase

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class CheckStreakOnOpenUseCase @Inject constructor(
    private val preferences: AxiomPreferences,
    private val ceremonyEngine: CeremonyEngine,
    private val feedRepository: SystemFeedRepository
) {
    suspend operator fun invoke() {
        preferences.resetWeeklyIfNeeded()
        val currentStreak  = preferences.streakFlow.first()
        preferences.setWeeklyStreakBest(currentStreak)
        val lastComplete   = preferences.lastCompleteTimestampFlow.first()
        val now            = System.currentTimeMillis()

        if (lastComplete == 0L || currentStreak == 0) return

        if (!isSameDay(lastComplete, now) && !isYesterday(lastComplete, now)) {
            if (currentStreak >= 1 && preferences.consumeStreakFreeze()) {
                val remaining = preferences.streakFreezeFlow.first()
                ceremonyEngine.emit(CeremonyEvent.StreakShieldUsed(currentStreak, remaining))
                com.axiom.app.core.AnalyticsLogger.log("streak_shield_used", mapOf("streak_length" to currentStreak))
                feedRepository.emitMessage(
                    SystemMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        message = "⬡ Streak Shield activated. $currentStreak-day streak preserved.",
                        timestamp = now
                    )
                )
                return  // streak survives — do NOT reset
            }
            preferences.setStreak(0)
            if (currentStreak >= 1) {
                ceremonyEngine.emit(CeremonyEvent.StreakBroken(currentStreak))
                com.axiom.app.core.AnalyticsLogger.log("streak_broken", mapOf("streak_length" to currentStreak))
            }
            return
        }

        // Streak intact — check milestones (7, 14, 21, 30, 60, 90, 180, 365)
        val milestones  = listOf(7, 14, 21, 30, 60, 90, 180, 365)
        val lastShown   = preferences.lastShownStreakMilestoneFlow.first()
        val newMilestone = milestones
            .filter { it <= currentStreak && it > lastShown }
            .maxOrNull()

        // Guard: only fire once per milestone level
        if (newMilestone != null) {
            preferences.setLastShownStreakMilestone(newMilestone)   // set BEFORE emit
            val label = when (newMilestone) {
                7    -> "CONSECRATION PROTOCOL"
                14   -> "DOMINANCE PROTOCOL"
                21   -> "RESONANCE PROTOCOL"
                30   -> "ASCENSION PROTOCOL"
                60   -> "TRANSCENDENCE PROTOCOL"
                90   -> "IMMORTAL PROTOCOL"
                180  -> "OVERLORD PROTOCOL"
                else -> "CHRONOS PROTOCOL"
            }
            ceremonyEngine.emit(CeremonyEvent.StreakMilestone(currentStreak, label))
            preferences.awardStreakFreeze()

            if (newMilestone >= 7) {
                val isFa = java.util.Locale.getDefault().language == "fa"
                val labelFa = when (newMilestone) {
                    7    -> "پروتکل تعهد"
                    14   -> "پروتکل تسلط"
                    21   -> "پروتکل هماهنگی"
                    30   -> "پروتکل صعود"
                    60   -> "پروتکل تعالی"
                    90   -> "پروتکل جاودانگی"
                    180  -> "پروتکل فرمانروا"
                    else -> "پروتکل زمان"
                }
                val finalMsg = if (isFa) {
                    "⬡ پروتکل زنجیره $labelFa به دست آمد. زنجیره $currentStreak روزه شما هم‌اکنون دارایی باارزشی تحت حفاظت سیستم است."
                } else {
                    "⬡ Streak Protocol $label achieved. Your $currentStreak-day streak is now an asset worth protecting."
                }
                feedRepository.emitMessage(SystemMessage(
                    id        = java.util.UUID.randomUUID().toString(),
                    message   = finalMsg,
                    timestamp = now
                ))
            }
        }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        if (t1 == 0L || t2 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(t1: Long, t2: Long): Boolean {
        if (t1 == 0L || t2 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        cal1.add(Calendar.DAY_OF_YEAR, 1)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
