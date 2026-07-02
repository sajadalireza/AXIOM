package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.focus.FocusProtocolManager
import com.axiom.app.domain.repository.DailyHabitLogRepository
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.data.local.AxiomPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val focusProtocolManager: FocusProtocolManager,
    private val dailyHabitLogRepository: DailyHabitLogRepository,
    private val preferences: AxiomPreferences,
    private val feedRepository: SystemFeedRepository
) : ViewModel() {

    private val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val pendingDailyCheckinCount: StateFlow<Int> = dailyHabitLogRepository.getLogByDate(todayStr)
        .map { existingLog ->
            if (existingLog == null || (existingLog.waterGlasses == 0 && existingLog.sleepHours == null && !existingLog.teethMorning && !existingLog.teethEvening)) {
                1
            } else {
                0
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val overdueWeeklyReviewCount: StateFlow<Int> = combine(
        preferences.lastReviewCompletedWeekFlow,
        preferences.reviewDayOfWeekFlow
    ) { lastWeek, scheduledDay ->
        val currentWeek = getISOWeekNumber()
        val isNotCompleted = lastWeek != currentWeek
        val isOnOrAfterDay = isAfterOrOnReviewDay(scheduledDay)
        if (isNotCompleted && isOnOrAfterDay) 1 else 0
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val newSystemMessagesCount: StateFlow<Int> = feedRepository.getSystemMessages()
        .map { messages ->
            messages.size.coerceAtMost(9)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun getISOWeekNumber(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return "$year-W$week"
    }

    private fun isAfterOrOnReviewDay(scheduledDay: Int): Boolean {
        val cal = Calendar.getInstance()
        val currentDayIndex = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
        return currentDayIndex >= scheduledDay
    }
}

