package com.axiom.app.domain.usecase

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.local.entity.VitalType
import com.axiom.app.domain.repository.VitalsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BurnoutMonitorUseCase @Inject constructor(
    private val vitalsRepository: VitalsRepository,
    private val preferences: AxiomPreferences
) {
    suspend fun checkBurnout(): Boolean {
        // Fetch current 3 days of sleep and energy logs
        val sleepLogs = vitalsRepository.getLastNDaysLogs(VitalType.SLEEP_HOURS, 3)
        val energyLogs = vitalsRepository.getLastNDaysLogs(VitalType.ENERGY_SCORE, 3)

        // boundary cases: fewer than 3 days available yet
        if (sleepLogs.size < 3 || energyLogs.size < 3) {
            return false
        }

        val sleepAvg = sleepLogs.map { it.value }.average().toFloat()
        val energyAvg = energyLogs.map { it.value }.average().toFloat()

        val sleepTarget = preferences.sleepTargetFlow.first()
        val energyFloor = preferences.energyFloorFlow.first().toFloat()

        val sleepThreshold = sleepTarget - 1.5f

        // boundary case: averages exactly at the threshold is strictly <
        val isBurnout = sleepAvg < sleepThreshold || energyAvg < energyFloor

        if (isBurnout) {
            preferences.setBurnoutFlagActive(true)
        }
        return isBurnout
    }
}
