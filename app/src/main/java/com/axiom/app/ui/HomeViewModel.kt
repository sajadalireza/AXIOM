package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.usecase.GetDungeonsUseCase
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.usecase.GetMissionsUseCase
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.repository.MuscleGroupRepository
import com.axiom.app.domain.model.MuscleGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

import com.axiom.app.domain.model.CharacterStats
import com.axiom.app.data.local.AxiomPreferences

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val hunter: Hunter,
        val topMissions: List<Mission>,
        val activeDungeon: Dungeon?,
        val recentFeed: List<SystemMessage>,
        val characterStats: CharacterStats?,
        val streakDays: Int,
        val activeMissionsCount: Int,
        val streakMultiplier: Float,
        val showPremiumNudge: Boolean = false,
        val dungeons: List<Dungeon> = emptyList(),
        // PRIORITY 2: null = hide card
        val nextBestAction: String? = null,
        val nextBestActionRoute: String? = null
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val getMissionsUseCase: GetMissionsUseCase,
    private val getDungeonsUseCase: GetDungeonsUseCase,
    private val systemFeedRepository: SystemFeedRepository,
    private val muscleRepository: MuscleGroupRepository,
    private val habitRepository: com.axiom.app.domain.repository.DailyHabitLogRepository,
    private val preferences: AxiomPreferences
) : ViewModel() {

    val lastReviewTimestampFlow: Flow<Long> = preferences.lastReviewTimestampFlow
    val vehicleProgramStartDateFlow: Flow<Long> = preferences.vehicleProgramStartDateFlow

    fun setVehicleProgramStartDate(timestampMillis: Long) {
        viewModelScope.launch { preferences.setVehicleProgramStartDate(timestampMillis) }
    }

    private val todayStr: String = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())

    val todayHabitLog: StateFlow<com.axiom.app.data.local.entity.DailyHabitLogEntity?> = habitRepository.getLogByDate(todayStr)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val muscleGroups: StateFlow<List<MuscleGroup>> = muscleRepository.getAllMuscleGroups()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            val shown = preferences.briefingHomeFlow.first()
            if (!shown) {
                delay(1500) // let the screen load first
                val isFa = preferences.languageFlow.first() == "fa"
                val welcomeMsg = if (isFa) {
                    "پروفایل هانتر با موفقیت ایجاد شد. هیچ مأموریت فعالی شناسایی نشد. اولین هدف خود را در بخش مأموریت‌ها ثبت کنید."
                } else {
                    "Hunter profile established. No active missions detected. Register your first objective via MISSIONS."
                }
                systemFeedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = welcomeMsg,
                        timestamp = System.currentTimeMillis()
                    )
                )
                preferences.setBriefingShown("home")
            }
        }
    }

    val homeState: StateFlow<HomeUiState> = combine(
        getHunterProfileUseCase(),
        getMissionsUseCase(activeOnly = true),
        getDungeonsUseCase(),
        systemFeedRepository.getSystemMessages(),
        combine(preferences.statsFlow, preferences.streakFlow, preferences.languageFlow) { stats, streak, lang -> Triple(stats, streak, lang) }
    ) { hunter, missions, dungeons, messages, statsStreakLang ->
        if (hunter == null) {
            HomeUiState.Loading
        } else {
            val (stats, streak, lang) = statsStreakLang
            val isFa = lang == "fa"
            val streakMultiplier = when {
                streak < 7  -> 1.0f
                streak < 14 -> 1.15f
                streak < 30 -> 1.30f
                else        -> 1.50f
            }
            val streakDays  = streak
            val hunterLevel = hunter.level
            val showNudge   = streakDays >= 7 || hunterLevel >= 15

            HomeUiState.Success(
                hunter = hunter,
                topMissions = missions.take(3),
                activeDungeon = dungeons.firstOrNull { !it.isCompleted },
                recentFeed = messages.sortedByDescending { it.timestamp }.take(5),
                characterStats = stats,
                streakDays = streak,
                activeMissionsCount = missions.size,
                streakMultiplier = streakMultiplier,
                showPremiumNudge = showNudge,
                dungeons = dungeons,
                nextBestAction = when {
                    missions.isEmpty() -> if (isFa) "اولین مأموریت خود را برای شروع اضافه کنید" else "Add your first mission to get started"
                    missions.none { it.status == "COMPLETED" } -> missions.firstOrNull()?.title?.let { if (isFa) "تکمیل کنید: $it" else "Complete: $it" }
                    streak < 3 -> if (isFa) "ادامه دهید — رکوردهای متوالی را به ثبت برسانید" else "Keep going — build your streak"
                    else -> null
                },
                nextBestActionRoute = when {
                    missions.isEmpty() -> "add_mission"
                    missions.none { it.status == "COMPLETED" } -> "missions"
                    streak < 3 -> "missions"
                    else -> null
                }
            )
        }
    }
    .catch { e ->
        emit(HomeUiState.Error(e.message ?: "An unexpected error occurred"))
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    val streakFreezeCount: StateFlow<Int> = preferences.streakFreezeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
}
