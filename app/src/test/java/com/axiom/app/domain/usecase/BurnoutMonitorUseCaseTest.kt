package com.axiom.app.domain.usecase

import android.content.Context
import android.content.ContextWrapper
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.local.entity.VitalLogEntity
import com.axiom.app.data.local.entity.VitalType
import com.axiom.app.domain.repository.VitalsRepository
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeVitalsRepository : VitalsRepository {
    var sleepLogs: List<VitalLogEntity> = emptyList()
    var energyLogs: List<VitalLogEntity> = emptyList()

    override suspend fun logWater(ml: Float) {}
    override suspend fun logSleep(hours: Float) {}
    override suspend fun toggleTeeth(am: Boolean): Boolean = true
    override suspend fun logEnergy(score: Int) {}

    override fun getTodayWaterMl(): Flow<Float> = flowOf(0f)
    override fun getTodaySleepHours(): Flow<Float> = flowOf(0f)
    override fun getTodayTeethAmLogged(): Flow<Boolean> = flowOf(false)
    override fun getTodayTeethPmLogged(): Flow<Boolean> = flowOf(false)
    override fun getTodayEnergyScore(): Flow<Int?> = flowOf(null)

    override fun getWeeklyTrend(type: VitalType): Flow<List<VitalLogEntity>> = flowOf(emptyList())

    override suspend fun getLastNDaysLogs(type: VitalType, n: Int): List<VitalLogEntity> {
        return if (type == VitalType.SLEEP_HOURS) {
            sleepLogs.takeLast(n)
        } else {
            energyLogs.takeLast(n)
        }
    }
}

class FakeContext : ContextWrapper(null) {
    override fun getApplicationContext(): Context = this
    override fun getFilesDir(): File = File(System.getProperty("java.io.tmpdir") ?: "/tmp")
    override fun getNoBackupFilesDir(): File = File(System.getProperty("java.io.tmpdir") ?: "/tmp")
}

class FakeAxiomPreferences : AxiomPreferences(FakeContext()) {
    var sleepTarget: Float = 7.5f
    var energyFloor: Int = 6
    var burnoutActive: Boolean = false

    override val sleepTargetFlow: Flow<Float>
        get() = flowOf(sleepTarget)

    override val energyFloorFlow: Flow<Int>
        get() = flowOf(energyFloor)

    override val burnoutFlagActiveFlow: Flow<Boolean>
        get() = flowOf(burnoutActive)

    override suspend fun setBurnoutFlagActive(value: Boolean) {
        burnoutActive = value
    }
}

class BurnoutMonitorUseCaseTest {

    @Test
    fun testFewerThan3DaysAvailableYet() = runBlocking {
        val vitalsRepository = FakeVitalsRepository()
        val preferences = FakeAxiomPreferences()
        val useCase = BurnoutMonitorUseCase(vitalsRepository, preferences)

        // Only 2 days of logs
        vitalsRepository.sleepLogs = listOf(
            VitalLogEntity(id = 1, date = 1, type = VitalType.SLEEP_HOURS.name, value = 4.0f),
            VitalLogEntity(id = 2, date = 2, type = VitalType.SLEEP_HOURS.name, value = 4.0f)
        )
        vitalsRepository.energyLogs = listOf(
            VitalLogEntity(id = 3, date = 1, type = VitalType.ENERGY_SCORE.name, value = 3.0f),
            VitalLogEntity(id = 4, date = 2, type = VitalType.ENERGY_SCORE.name, value = 3.0f)
        )

        val result = useCase.checkBurnout()
        assertFalse("Burnout should not trigger since we have fewer than 3 days of logs", result)
        assertFalse(preferences.burnoutActive)
    }

    @Test
    fun testExactly3DaysUnderSleepThreshold() = runBlocking {
        val vitalsRepository = FakeVitalsRepository()
        val preferences = FakeAxiomPreferences()
        val useCase = BurnoutMonitorUseCase(vitalsRepository, preferences)

        // Sleep target = 7.5, sleepThreshold = 7.5 - 1.5 = 6.0
        // Sleep average in log = (5.5 + 5.5 + 5.5) / 3 = 5.5 (< 6.0)
        vitalsRepository.sleepLogs = listOf(
            VitalLogEntity(id = 1, date = 1, type = VitalType.SLEEP_HOURS.name, value = 5.5f),
            VitalLogEntity(id = 2, date = 2, type = VitalType.SLEEP_HOURS.name, value = 5.5f),
            VitalLogEntity(id = 3, date = 3, type = VitalType.SLEEP_HOURS.name, value = 5.5f)
        )
        vitalsRepository.energyLogs = listOf(
            VitalLogEntity(id = 4, date = 1, type = VitalType.ENERGY_SCORE.name, value = 8.0f),
            VitalLogEntity(id = 5, date = 2, type = VitalType.ENERGY_SCORE.name, value = 8.0f),
            VitalLogEntity(id = 6, date = 3, type = VitalType.ENERGY_SCORE.name, value = 8.0f)
        )

        val result = useCase.checkBurnout()
        assertTrue("Burnout should trigger when sleep average is < target - 1.5", result)
        assertTrue(preferences.burnoutActive)
    }

    @Test
    fun testExactly3DaysUnderEnergyFloor() = runBlocking {
        val vitalsRepository = FakeVitalsRepository()
        val preferences = FakeAxiomPreferences()
        val useCase = BurnoutMonitorUseCase(vitalsRepository, preferences)

        // Sleep target = 7.5, sleepThreshold = 6.0. Sleep avg = 7.0 (healthy)
        // Energy floor = 6. Energy avg = (5.0 + 5.0 + 5.0) / 3 = 5.0 (< 6.0)
        vitalsRepository.sleepLogs = listOf(
            VitalLogEntity(id = 1, date = 1, type = VitalType.SLEEP_HOURS.name, value = 7.0f),
            VitalLogEntity(id = 2, date = 2, type = VitalType.SLEEP_HOURS.name, value = 7.0f),
            VitalLogEntity(id = 3, date = 3, type = VitalType.SLEEP_HOURS.name, value = 7.0f)
        )
        vitalsRepository.energyLogs = listOf(
            VitalLogEntity(id = 4, date = 1, type = VitalType.ENERGY_SCORE.name, value = 5.0f),
            VitalLogEntity(id = 5, date = 2, type = VitalType.ENERGY_SCORE.name, value = 5.0f),
            VitalLogEntity(id = 6, date = 3, type = VitalType.ENERGY_SCORE.name, value = 5.0f)
        )

        val result = useCase.checkBurnout()
        assertTrue("Burnout should trigger when energy average is < floor", result)
        assertTrue(preferences.burnoutActive)
    }

    @Test
    fun testAveragesExactlyAtThreshold() = runBlocking {
        val vitalsRepository = FakeVitalsRepository()
        val preferences = FakeAxiomPreferences()
        val useCase = BurnoutMonitorUseCase(vitalsRepository, preferences)

        // Sleep target = 7.5, sleepThreshold = 6.0. Sleep avg = 6.0 (exactly at threshold)
        // Energy floor = 6. Energy avg = 6.0 (exactly at threshold)
        // Conditions are strictly LESS THAN (<), so this should not trigger burnout.
        vitalsRepository.sleepLogs = listOf(
            VitalLogEntity(id = 1, date = 1, type = VitalType.SLEEP_HOURS.name, value = 6.0f),
            VitalLogEntity(id = 2, date = 2, type = VitalType.SLEEP_HOURS.name, value = 6.0f),
            VitalLogEntity(id = 3, date = 3, type = VitalType.SLEEP_HOURS.name, value = 6.0f)
        )
        vitalsRepository.energyLogs = listOf(
            VitalLogEntity(id = 4, date = 1, type = VitalType.ENERGY_SCORE.name, value = 6.0f),
            VitalLogEntity(id = 5, date = 2, type = VitalType.ENERGY_SCORE.name, value = 6.0f),
            VitalLogEntity(id = 6, date = 3, type = VitalType.ENERGY_SCORE.name, value = 6.0f)
        )

        val result = useCase.checkBurnout()
        assertFalse("Burnout should not trigger when averages are exactly on the thresholds", result)
        assertFalse(preferences.burnoutActive)
    }

    @Test
    fun testHealthyAverages() = runBlocking {
        val vitalsRepository = FakeVitalsRepository()
        val preferences = FakeAxiomPreferences()
        val useCase = BurnoutMonitorUseCase(vitalsRepository, preferences)

        vitalsRepository.sleepLogs = listOf(
            VitalLogEntity(id = 1, date = 1, type = VitalType.SLEEP_HOURS.name, value = 8.0f),
            VitalLogEntity(id = 2, date = 2, type = VitalType.SLEEP_HOURS.name, value = 7.5f),
            VitalLogEntity(id = 3, date = 3, type = VitalType.SLEEP_HOURS.name, value = 8.0f)
        )
        vitalsRepository.energyLogs = listOf(
            VitalLogEntity(id = 4, date = 1, type = VitalType.ENERGY_SCORE.name, value = 8.0f),
            VitalLogEntity(id = 5, date = 2, type = VitalType.ENERGY_SCORE.name, value = 9.0f),
            VitalLogEntity(id = 6, date = 3, type = VitalType.ENERGY_SCORE.name, value = 7.0f)
        )

        val result = useCase.checkBurnout()
        assertFalse("Burnout should not trigger when vitals are healthy", result)
        assertFalse(preferences.burnoutActive)
    }
}
