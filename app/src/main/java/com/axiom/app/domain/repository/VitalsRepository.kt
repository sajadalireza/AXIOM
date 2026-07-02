package com.axiom.app.domain.repository

import com.axiom.app.data.local.entity.VitalLogEntity
import com.axiom.app.data.local.entity.VitalType
import kotlinx.coroutines.flow.Flow

interface VitalsRepository {
    suspend fun logWater(ml: Float)
    suspend fun logSleep(hours: Float)
    suspend fun toggleTeeth(am: Boolean): Boolean // Returns new checked/unchecked state
    suspend fun logEnergy(score: Int)
    
    fun getTodayWaterMl(): Flow<Float>
    fun getTodaySleepHours(): Flow<Float>
    fun getTodayTeethAmLogged(): Flow<Boolean>
    fun getTodayTeethPmLogged(): Flow<Boolean>
    fun getTodayEnergyScore(): Flow<Int?>
    
    fun getWeeklyTrend(type: VitalType): Flow<List<VitalLogEntity>>
    suspend fun getLastNDaysLogs(type: VitalType, n: Int): List<VitalLogEntity>
}
