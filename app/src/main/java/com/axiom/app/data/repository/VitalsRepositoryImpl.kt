package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.VitalLogDao
import com.axiom.app.data.local.entity.VitalLogEntity
import com.axiom.app.data.local.entity.VitalType
import com.axiom.app.domain.repository.VitalsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalsRepositoryImpl @Inject constructor(
    private val vitalLogDao: VitalLogDao,
    private val preferences: com.axiom.app.data.local.AxiomPreferences
) : VitalsRepository {

    private fun getTodayIndex(): Long {
        return System.currentTimeMillis() / 86_400_000L
    }

    private suspend fun checkAndTriggerStreak() {
        val today = getTodayIndex()
        val water = vitalLogDao.getVitalLog(today, VitalType.WATER_ML.name)?.value ?: 0f
        val sleep = vitalLogDao.getVitalLog(today, VitalType.SLEEP_HOURS.name)?.value ?: 0f
        val teethAm = (vitalLogDao.getVitalLog(today, VitalType.TEETH_AM.name)?.value ?: 0f) > 0f
        val teethPm = (vitalLogDao.getVitalLog(today, VitalType.TEETH_PM.name)?.value ?: 0f) > 0f
        val energy = vitalLogDao.getVitalLog(today, VitalType.ENERGY_SCORE.name)?.value ?: 0f
        
        if (water > 0f && sleep > 0f && teethAm && teethPm && energy >= 1f) {
            preferences.checkOffDailyProtocol()
        }
    }

    override suspend fun logWater(ml: Float) = withContext(Dispatchers.IO) {
        val today = getTodayIndex()
        val existing = vitalLogDao.getVitalLog(today, VitalType.WATER_ML.name)
        val currentVal = existing?.value ?: 0f
        vitalLogDao.insertVitalLog(
            VitalLogEntity(
                id = existing?.id ?: 0,
                date = today,
                type = VitalType.WATER_ML.name,
                value = currentVal + ml
            )
        )
        checkAndTriggerStreak()
    }

    override suspend fun logSleep(hours: Float) = withContext(Dispatchers.IO) {
        val today = getTodayIndex()
        val existing = vitalLogDao.getVitalLog(today, VitalType.SLEEP_HOURS.name)
        vitalLogDao.insertVitalLog(
            VitalLogEntity(
                id = existing?.id ?: 0,
                date = today,
                type = VitalType.SLEEP_HOURS.name,
                value = hours
            )
        )
        checkAndTriggerStreak()
    }

    override suspend fun toggleTeeth(am: Boolean): Boolean = withContext(Dispatchers.IO) {
        val today = getTodayIndex()
        val type = if (am) VitalType.TEETH_AM else VitalType.TEETH_PM
        val existing = vitalLogDao.getVitalLog(today, type.name)
        val newValue = if (existing != null && existing.value > 0f) 0f else 1f
        vitalLogDao.insertVitalLog(
            VitalLogEntity(
                id = existing?.id ?: 0,
                date = today,
                type = type.name,
                value = newValue
            )
        )
        checkAndTriggerStreak()
        newValue > 0f
    }

    override suspend fun logEnergy(score: Int) = withContext(Dispatchers.IO) {
        val today = getTodayIndex()
        val existing = vitalLogDao.getVitalLog(today, VitalType.ENERGY_SCORE.name)
        vitalLogDao.insertVitalLog(
            VitalLogEntity(
                id = existing?.id ?: 0,
                date = today,
                type = VitalType.ENERGY_SCORE.name,
                value = score.toFloat()
            )
        )
        checkAndTriggerStreak()
    }

    override fun getTodayWaterMl(): Flow<Float> {
        return vitalLogDao.getVitalLogFlow(getTodayIndex(), VitalType.WATER_ML.name).map { it?.value ?: 0f }
    }

    override fun getTodaySleepHours(): Flow<Float> {
        return vitalLogDao.getVitalLogFlow(getTodayIndex(), VitalType.SLEEP_HOURS.name).map { it?.value ?: 0f }
    }

    override fun getTodayTeethAmLogged(): Flow<Boolean> {
        return vitalLogDao.getVitalLogFlow(getTodayIndex(), VitalType.TEETH_AM.name).map { (it?.value ?: 0f) > 0f }
    }

    override fun getTodayTeethPmLogged(): Flow<Boolean> {
        return vitalLogDao.getVitalLogFlow(getTodayIndex(), VitalType.TEETH_PM.name).map { (it?.value ?: 0f) > 0f }
    }

    override fun getTodayEnergyScore(): Flow<Int?> {
        return vitalLogDao.getVitalLogFlow(getTodayIndex(), VitalType.ENERGY_SCORE.name).map { it?.value?.toInt() }
    }

    override fun getWeeklyTrend(type: VitalType): Flow<List<VitalLogEntity>> {
        val today = getTodayIndex()
        val start = today - 6
        return vitalLogDao.getTrendFlow(type.name, start, today)
    }

    override suspend fun getLastNDaysLogs(type: VitalType, n: Int): List<VitalLogEntity> = withContext(Dispatchers.IO) {
        val today = getTodayIndex()
        val start = today - (n - 1)
        vitalLogDao.getRecentLogs(type.name, start)
    }
}
