package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.repository.VitalsRepository
import com.axiom.app.domain.usecase.BurnoutMonitorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VitalsViewModel @Inject constructor(
    private val vitalsRepository: VitalsRepository,
    private val preferences: AxiomPreferences,
    private val burnoutMonitorUseCase: BurnoutMonitorUseCase
) : ViewModel() {

    val todayWater = vitalsRepository.getTodayWaterMl()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val todaySleep = vitalsRepository.getTodaySleepHours()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val todayTeethAm = vitalsRepository.getTodayTeethAmLogged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val todayTeethPm = vitalsRepository.getTodayTeethPmLogged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val todayEnergy = vitalsRepository.getTodayEnergyScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val waterTarget = preferences.waterTargetFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2500f)

    val sleepTarget = preferences.sleepTargetFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 7.5f)

    val energyFloor = preferences.energyFloorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    val burnoutActive = preferences.burnoutFlagActiveFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val burnoutSetAt = preferences.burnoutFlagSetAtFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _showEnergyPrompt = MutableStateFlow(false)
    val showEnergyPrompt: StateFlow<Boolean> = _showEnergyPrompt.asStateFlow()

    fun checkAppOpenVitals() {
        viewModelScope.launch {
            burnoutMonitorUseCase.checkBurnout()

            val entry = vitalsRepository.getTodayEnergyScore().first()
            if (entry == null) {
                val lastPrompt = preferences.lastEnergyPromptTimestampFlow.first()
                val now = System.currentTimeMillis()
                if (!preferences.isSameDay(lastPrompt, now)) {
                    _showEnergyPrompt.value = true
                }
            }
        }
    }

    fun dismissEnergyPrompt() {
        _showEnergyPrompt.value = false
    }

    fun logEnergyAndSavePrompt(score: Int) {
        viewModelScope.launch {
            vitalsRepository.logEnergy(score)
            preferences.setLastEnergyPromptTimestamp(System.currentTimeMillis())
            _showEnergyPrompt.value = false
        }
    }

    fun logWater(ml: Float) {
        viewModelScope.launch {
            vitalsRepository.logWater(ml)
        }
    }

    fun logSleep(hours: Float) {
        viewModelScope.launch {
            vitalsRepository.logSleep(hours)
        }
    }

    fun toggleTeeth(am: Boolean) {
        viewModelScope.launch {
            vitalsRepository.toggleTeeth(am)
        }
    }

    fun updateVitalsTargets(water: Float, sleep: Float, floor: Int) {
        viewModelScope.launch {
            preferences.setWaterTarget(water)
            preferences.setSleepTarget(sleep)
            preferences.setEnergyFloor(floor)
        }
    }

    fun acknowledgeBurnout() {
        viewModelScope.launch {
            preferences.setBurnoutFlagActive(false)
        }
    }
}
