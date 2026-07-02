package com.axiom.app.presentation.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.entity.DailyHabitLogEntity
import com.axiom.app.domain.repository.DailyHabitLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DailyCheckinViewModel @Inject constructor(
    private val repository: DailyHabitLogRepository
) : ViewModel() {

    private val todayStr: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private val _habitLog = MutableStateFlow<DailyHabitLogEntity?>(null)
    val habitLog: StateFlow<DailyHabitLogEntity?> = _habitLog.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getLogByDate(todayStr).collect { existingLog ->
                if (existingLog == null) {
                    val newLog = DailyHabitLogEntity(
                        id = UUID.randomUUID().toString(),
                        date = todayStr,
                        waterGlasses = 0,
                        sleepHours = null,
                        sleepQuality = null,
                        teethMorning = false,
                        teethEvening = false
                    )
                    repository.insertLog(newLog)
                    _habitLog.value = newLog
                } else {
                    _habitLog.value = existingLog
                }
            }
        }
    }

    fun updateWater(glasses: Int) {
        val current = _habitLog.value ?: return
        val updated = current.copy(waterGlasses = glasses.coerceIn(0, 8))
        viewModelScope.launch {
            repository.updateLog(updated)
        }
    }

    fun updateSleep(hours: Float?, quality: Int?) {
        val current = _habitLog.value ?: return
        val updated = current.copy(sleepHours = hours, sleepQuality = quality?.coerceIn(1, 5))
        viewModelScope.launch {
            repository.updateLog(updated)
        }
    }

    fun updateTeeth(morning: Boolean, evening: Boolean) {
        val current = _habitLog.value ?: return
        val updated = current.copy(teethMorning = morning, teethEvening = evening)
        viewModelScope.launch {
            repository.updateLog(updated)
        }
    }
}
