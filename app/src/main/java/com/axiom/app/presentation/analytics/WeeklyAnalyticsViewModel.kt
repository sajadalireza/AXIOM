package com.axiom.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.local.dao.KPIProgressDao
import com.axiom.app.data.local.entity.KPIMissStreakEntity
import com.axiom.app.data.local.entity.KPIProgressEntity
import com.axiom.app.data.local.entity.VitalLogEntity
import com.axiom.app.data.local.entity.VitalType
import com.axiom.app.data.local.entity.DailyHabitLogEntity
import com.axiom.app.domain.model.CustomKPI
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.domain.repository.MuscleGroupRepository
import com.axiom.app.domain.repository.VitalsRepository
import com.axiom.app.domain.repository.WarriorProfileRepository
import com.axiom.app.domain.repository.DailyHabitLogRepository
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeeklyAnalyticsViewModel @Inject constructor(
    private val warriorRepository: WarriorProfileRepository,
    private val missionRepository: MissionRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val vitalsRepository: VitalsRepository,
    private val kpiProgressDao: KPIProgressDao,
    private val dailyHabitLogRepository: DailyHabitLogRepository,
    val ceremonyEngine: CeremonyEngine,
    val preferences: AxiomPreferences
) : ViewModel() {

    val streakFlow: StateFlow<Int> = preferences.streakFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _aiSummary = MutableStateFlow<String>("Generating summary...")
    val aiSummary: StateFlow<String> = _aiSummary

    init {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(completedMissions, last7DaysHabits) { _, _ -> }.collect {
                loadAiSummary()
            }
        }
    }

    fun loadAiSummary() {
        viewModelScope.launch {
            _aiSummary.value = "Generating summary..."
            val streakVal = preferences.streakFlow.first()
            val missions = completedMissions.value
            val habits = last7DaysHabits.value
            
            val limit = System.currentTimeMillis() - 7 * 86400000L
            val missionsLast7Days = missions.count {
                val compAt = it.completedAt ?: 0L
                compAt >= limit
            }
            
            val sleep = getSleepHoursPerDay(habits)
            val sleepHits = sleep.count { it >= 7f }
            
            val fallbackSummary = "You completed $missionsLast7Days missions, maintained a $streakVal-day streak, and hit your sleep KPI $sleepHits/7 days this week."
            
            try {
                val key = preferences.geminiApiKeyFlow.first()
                if (!key.isNullOrBlank()) {
                    val model = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = key)
                    val prompt = "Write a concise, motivational 1-line summary of a warrior's performance this week. Stats: Completed $missionsLast7Days missions, current streak: $streakVal days, hit sleep targets: $sleepHits/7 days. Keep it short and direct under 20 words. Do not include intro or outro, write in third person, starting with 'You completed...'"
                    val response = model.generateContent(prompt)
                    val text = response.text?.trim()
                    if (!text.isNullOrBlank()) {
                        _aiSummary.value = text
                        return@launch
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _aiSummary.value = fallbackSummary
        }
    }

    fun getFormattedDateRange(): String {
        val dates = getLast7DaysDates()
        if (dates.isEmpty()) return ""
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("MMM dd", Locale.US)
        return try {
            val firstDate = parser.parse(dates.first())
            val lastDate = parser.parse(dates.last())
            "${formatter.format(firstDate)} - ${formatter.format(lastDate)}"
        } catch (e: Exception) {
            "${dates.first()} - ${dates.last()}"
        }
    }

    fun getWeekDebriefTitle(): String {
        val isoWeek = getISOWeekNumber()
        val parts = isoWeek.split("-W")
        val weekNum = if (parts.size > 1) parts[1] else "CURRENT"
        return "WEEK $weekNum DEBRIEF — ${getFormattedDateRange()}"
    }

    fun calculatePerformanceScore(missionsLast7Days: Int, streak: Int, habits: List<DailyHabitLogEntity>): Int {
        val missionsPart = (missionsLast7Days / 7f).coerceIn(0f, 1f) * 40f
        val streakPart = (streak / 7f).coerceIn(0f, 1f) * 30f
        
        var hits = 0
        for (habit in habits) {
            if ((habit.sleepHours ?: 0f) >= 7.5f) hits++
            if (habit.waterGlasses >= 6) hits++
        }
        val habitsPart = if (habits.isNotEmpty()) {
            (hits / (habits.size * 2f)).coerceIn(0f, 1f) * 30f
        } else {
            0f
        }
        
        return (missionsPart + streakPart + habitsPart).toInt().coerceIn(0, 100)
    }

    fun getWeeklyWinsAndMisses(
        completedMissions: List<Mission>,
        habits: List<DailyHabitLogEntity>,
        kpis: List<CustomKPI>,
        progress: List<KPIProgressEntity>,
        missStreaks: List<KPIMissStreakEntity>,
        streak: Int
    ): Pair<List<String>, Pair<String, String>> {
        val wins = mutableListOf<String>()
        var miss = "No critical failures detected."
        var suggestion = "Maintain your current operational sequence."

        val limit = System.currentTimeMillis() - 7 * 86400000L
        val missionsLast7Days = completedMissions.count {
            val compAt = it.completedAt ?: 0L
            compAt >= limit
        }

        // 1. Mission win
        if (missionsLast7Days >= 5) {
            wins.add("Overwhelming output: completed $missionsLast7Days missions this week.")
        } else if (missionsLast7Days > 0) {
            wins.add("Active progress: completed $missionsLast7Days tactical protocols.")
        }

        // 2. Streak win
        if (streak >= 7) {
            wins.add("High momentum: secured a solid $streak-day focus streak.")
        } else if (streak > 0) {
            wins.add("Momentum preserved: active $streak-day workflow streak.")
        }

        // 3. Sleep / Water win
        val sleep = getSleepHoursPerDay(habits)
        val sleepHits = sleep.count { it >= 7.5f }
        if (sleepHits >= 5) {
            wins.add("Sleep optimization: hit rest KPIs on $sleepHits/7 days.")
        }

        val water = getWaterIntakePerDay(habits)
        val waterHits = water.count { it >= 6f }
        if (waterHits >= 5) {
            wins.add("Hydration discipline: water intake target met $waterHits/7 days.")
        }

        // Default wins if list is short
        if (wins.size < 3) wins.add("Protocol integrity maintained.")
        if (wins.size < 3) wins.add("Warrior profile stabilized.")
        if (wins.size < 3) wins.add("Strategic planning active.")

        val topWins = wins.take(3)

        // Now calculate misses
        val highestMissStreak = missStreaks.maxByOrNull { it.missStreak }
        val averageSleep = if (sleep.isNotEmpty()) sleep.average() else 8.0
        val averageWater = if (water.isNotEmpty()) water.average() else 8.0

        if (highestMissStreak != null && highestMissStreak.missStreak > 0) {
            val kpiName = kpis.find { it.id == highestMissStreak.kpiId }?.name ?: "Strategic KPI"
            miss = "Custom KPI failure: $kpiName is $highestMissStreak.missStreak week(s) behind."
            suggestion = "Dedicate your first 30 minutes of deep focus tomorrow exclusively to $kpiName."
        } else if (averageSleep < 6.5) {
            miss = "Sleep deprivation: averaged only ${String.format("%.1f", averageSleep)} hours of rest."
            suggestion = "Deploy a strict 10:00 PM digital screen shutdown protocol tonight."
        } else if (averageWater < 4.0) {
            miss = "Dehydration detected: averaged only ${String.format("%.1f", averageWater)} glasses of water."
            suggestion = "Keep a 1-liter thermos at your primary work station as a persistent cue."
        } else if (missionsLast7Days < 3) {
            miss = "Tactical stagnation: completed only $missionsLast7Days missions this week."
            suggestion = "Decompose your primary epic into 15-minute bite-sized micro-protocols."
        }

        return Pair(topWins, Pair(miss, suggestion))
    }

    fun getCompletedMissionsLast4Weeks(completedMissions: List<Mission>): List<Float> {
        val counts = FloatArray(4)
        val now = System.currentTimeMillis()
        val oneWeekMillis = 7 * 86400000L
        for (mission in completedMissions) {
            val compAt = mission.completedAt ?: continue
            val diff = now - compAt
            val weekIdx = (diff / oneWeekMillis).toInt()
            if (weekIdx in 0..3) {
                counts[3 - weekIdx] += 1f
            }
        }
        return counts.toList()
    }

    fun getXpGainedPerDay(completedMissions: List<Mission>): List<Float> {
        val dates = getLast7DaysDates()
        val xp = FloatArray(7)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (mission in completedMissions) {
            val completedTime = mission.completedAt ?: continue
            val dateStr = format.format(Date(completedTime))
            val idx = dates.indexOf(dateStr)
            if (idx != -1) {
                xp[idx] += mission.xpReward.toFloat()
            }
        }
        return xp.toList()
    }

    fun getMissionsCountPerDay(completedMissions: List<Mission>): List<Int> {
        val dates = getLast7DaysDates()
        val counts = IntArray(7)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (mission in completedMissions) {
            val completedTime = mission.completedAt ?: continue
            val dateStr = format.format(Date(completedTime))
            val idx = dates.indexOf(dateStr)
            if (idx != -1) {
                counts[idx] += 1
            }
        }
        return counts.toList()
    }

    val customKPIs: StateFlow<List<CustomKPI>> = warriorRepository.getCustomKPIsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedMissions: StateFlow<List<Mission>> = missionRepository.getAllMissions()
        .map { missions -> missions.filter { it.status == "COMPLETED" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val muscleGroups: StateFlow<List<MuscleGroup>> = muscleGroupRepository.getAllMuscleGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kpiProgress: StateFlow<List<KPIProgressEntity>> = kpiProgressDao.getAllProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kpiMissStreaks: StateFlow<List<KPIMissStreakEntity>> = kpiProgressDao.getAllMissStreaks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val waterLogs: StateFlow<List<VitalLogEntity>> = vitalsRepository.getWeeklyTrend(VitalType.WATER_ML)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepLogs: StateFlow<List<VitalLogEntity>> = vitalsRepository.getWeeklyTrend(VitalType.SLEEP_HOURS)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val energyLogs: StateFlow<List<VitalLogEntity>> = vitalsRepository.getWeeklyTrend(VitalType.ENERGY_SCORE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val last7DaysHabits: StateFlow<List<DailyHabitLogEntity>> = flow {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val startDateStr = dateFormat.format(cal.time)
        dailyHabitLogRepository.getLogsForLast7Days(startDateStr).collect { list ->
            emit(list)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getLast7DaysLabels(): List<String> {
        val labels = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("EEE", Locale.US)
        for (i in 0..6) {
            val dCal = cal.clone() as Calendar
            dCal.add(Calendar.DAY_OF_YEAR, -6 + i)
            labels.add(format.format(dCal.time))
        }
        return labels
    }

    fun getLast7DaysDates(): List<String> {
        val dates = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (i in 0..6) {
            val dCal = cal.clone() as Calendar
            dCal.add(Calendar.DAY_OF_YEAR, -6 + i)
            dates.add(format.format(dCal.time))
        }
        return dates
    }

    fun getEffectiveHoursPerDay(completedMissions: List<Mission>): List<Float> {
        val dates = getLast7DaysDates()
        val hours = FloatArray(7)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (mission in completedMissions) {
            val completedTime = mission.completedAt ?: continue
            val dateStr = format.format(Date(completedTime))
            val idx = dates.indexOf(dateStr)
            if (idx != -1) {
                val eff = if (mission.effectiveHours > 0.0) mission.effectiveHours else (mission.actualHours?.toDouble() ?: mission.estimatedHours.toDouble())
                hours[idx] += eff.toFloat()
            }
        }
        return hours.toList()
    }

    fun getAverageQualityPerDay(completedMissions: List<Mission>): List<Float> {
        val dates = getLast7DaysDates()
        val qualitySum = FloatArray(7)
        val count = IntArray(7)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (mission in completedMissions) {
            val completedTime = mission.completedAt ?: continue
            val dateStr = format.format(Date(completedTime))
            val idx = dates.indexOf(dateStr)
            if (idx != -1) {
                val q = if (mission.qualityScore >= 0.0) mission.qualityScore * 100.0 else 100.0
                qualitySum[idx] += q.toFloat()
                count[idx] += 1
            }
        }
        val avg = FloatArray(7)
        for (i in 0..6) {
            avg[i] = if (count[i] > 0) qualitySum[i] / count[i] else 0f
        }
        return avg.toList()
    }

    fun getWaterIntakePerDay(habits: List<DailyHabitLogEntity>): List<Float> {
        val dates = getLast7DaysDates()
        val water = FloatArray(7)
        for (i in 0..6) {
            val dateStr = dates[i]
            val log = habits.find { it.date == dateStr }
            water[i] = log?.waterGlasses?.toFloat() ?: 0f
        }
        return water.toList()
    }

    fun getSleepHoursPerDay(habits: List<DailyHabitLogEntity>): List<Float> {
        val dates = getLast7DaysDates()
        val sleep = FloatArray(7)
        for (i in 0..6) {
            val dateStr = dates[i]
            val log = habits.find { it.date == dateStr }
            sleep[i] = log?.sleepHours ?: 0f
        }
        return sleep.toList()
    }

    fun getMuscleTrainingIntensity(muscleId: String, completedMissions: List<Mission>, muscleGroups: List<MuscleGroup>): List<Float> {
        val dates = getLast7DaysDates()
        val intensities = FloatArray(7)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        val muscle = muscleGroups.find { it.id == muscleId }
        val lastTrainedStr = muscle?.lastTrainedTimestamp?.let { format.format(Date(it)) }

        for (i in 0..6) {
            val dateStr = dates[i]
            var intensity = 0.0f
            
            if (lastTrainedStr == dateStr) {
                intensity = 0.8f
            }
            
            for (mission in completedMissions) {
                val compAt = mission.completedAt ?: continue
                val mDate = format.format(Date(compAt))
                if (mDate == dateStr) {
                    val titleLower = mission.title.lowercase()
                    val trackLower = mission.track.lowercase()
                    val descLower = mission.description.lowercase()
                    
                    val isBodyWorkout = trackLower.contains("body") || trackLower.contains("fit") || trackLower.contains("gym") || titleLower.contains("workout")
                    
                    if (isBodyWorkout) {
                        intensity = intensity.coerceAtLeast(0.2f)
                    }
                    
                    val matchesMuscle = when (muscleId) {
                        "chest" -> titleLower.contains("chest") || titleLower.contains("push") || titleLower.contains("bench") || descLower.contains("chest")
                        "back" -> titleLower.contains("back") || titleLower.contains("pull") || titleLower.contains("deadlift") || descLower.contains("back")
                        "shoulders" -> titleLower.contains("shoulder") || titleLower.contains("press") || titleLower.contains("deltoid") || descLower.contains("shoulder")
                        "biceps" -> titleLower.contains("bicep") || titleLower.contains("curl") || descLower.contains("bicep")
                        "triceps" -> titleLower.contains("tricep") || titleLower.contains("dip") || descLower.contains("tricep")
                        "legs" -> titleLower.contains("leg") || titleLower.contains("squat") || titleLower.contains("quad") || titleLower.contains("calf") || descLower.contains("leg")
                        "core" -> titleLower.contains("core") || titleLower.contains("abs") || titleLower.contains("plank") || descLower.contains("core")
                        "forearms" -> titleLower.contains("forearm") || titleLower.contains("grip") || descLower.contains("forearm")
                        else -> false
                    }
                    if (matchesMuscle) {
                        intensity = intensity.coerceAtLeast(0.9f)
                    }
                }
            }
            intensities[i] = intensity
        }
        return intensities.toList()
    }

    fun getDisciplineBreakdown(completedMissions: List<Mission>): List<Pair<String, Float>> {
        val limit = System.currentTimeMillis() - 7 * 86400000L
        val map = mutableMapOf<String, Float>()
        for (mission in completedMissions) {
            val compAt = mission.completedAt ?: continue
            if (compAt >= limit) {
                val eff = if (mission.effectiveHours > 0.0) mission.effectiveHours else (mission.actualHours?.toDouble() ?: mission.estimatedHours.toDouble())
                map[mission.skillName] = (map[mission.skillName] ?: 0f) + eff.toFloat()
            }
        }
        return map.toList().sortedByDescending { it.second }
    }

    fun getISOWeekNumber(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return "$year-W$week"
    }

    fun isTodayReviewDay(scheduledDay: Int): Boolean {
        val cal = Calendar.getInstance()
        val currentDayIndex = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 5
        }
        return currentDayIndex == scheduledDay
    }

    private fun getStartOfWeekEpochMillis(): Long {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val now = System.currentTimeMillis()
        if (cal.timeInMillis > now) {
            cal.add(Calendar.WEEK_OF_YEAR, -1)
        }
        return cal.timeInMillis
    }

    fun getActualValueForKPI(
        kpi: CustomKPI,
        completedMissionsList: List<Mission>,
        musclesList: List<MuscleGroup>,
        progressList: List<KPIProgressEntity>
    ): Float {
        val startOfWeek = getStartOfWeekEpochMillis()
        if (kpi.trackId == null || kpi.trackId == "manual" || kpi.trackId.isEmpty()) {
            val currentWeekProgress = progressList.filter { it.kpiId == kpi.id && it.date >= startOfWeek }
            return currentWeekProgress.sumOf { it.incrementValue.toDouble() }.toFloat()
        }

        return when (kpi.trackId.lowercase()) {
            "water" -> {
                0f
            }
            else -> {
                val trackName = kpi.trackId
                val count = completedMissionsList.count { mission ->
                    val compAt = mission.completedAt ?: 0L
                    compAt >= startOfWeek && mission.status == "COMPLETED" && mission.track.contains(trackName, ignoreCase = true)
                }
                count.toFloat()
            }
        }
    }

    fun logManualKPI(kpiId: String, delta: Float) {
        viewModelScope.launch {
            val progressObj = KPIProgressEntity(
                id = UUID.randomUUID().toString(),
                kpiId = kpiId,
                date = System.currentTimeMillis(),
                incrementValue = delta
            )
            kpiProgressDao.insertProgress(progressObj)
        }
    }

    fun completeWeeklyReview(nextOutcomes: List<String>) {
        viewModelScope.launch {
            if (nextOutcomes.isNotEmpty()) preferences.setNextWeekOutcome1(nextOutcomes[0])
            if (nextOutcomes.size > 1) preferences.setNextWeekOutcome2(nextOutcomes[1])
            if (nextOutcomes.size > 2) preferences.setNextWeekOutcome3(nextOutcomes[2])

            val currentWeekStr = getISOWeekNumber()
            preferences.setLastReviewCompletedWeek(currentWeekStr)

            // Trigger the ceremony with 100 XP
            ceremonyEngine.emit(CeremonyEvent.WeeklyReviewComplete(100))
        }
    }
}
