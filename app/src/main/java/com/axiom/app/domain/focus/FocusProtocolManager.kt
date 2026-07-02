package com.axiom.app.domain.focus

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.usecase.CompleteMissionUseCase
import com.axiom.app.domain.usecase.CompleteDungeonStageUseCase
import com.axiom.app.domain.usecase.DefeatBossUseCase
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import com.axiom.app.core.sound.SoundEngine
import com.axiom.app.core.sound.AwakenSound
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import com.axiom.app.domain.repository.LeagueRepository
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class FocusProtocolManager @Inject constructor(
    private val preferences: AxiomPreferences,
    private val completeMissionUseCase: CompleteMissionUseCase,
    private val completeDungeonStageUseCase: CompleteDungeonStageUseCase,
    private val defeatBossUseCase: DefeatBossUseCase,
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val ceremonyEngine: CeremonyEngine,
    private val leagueRepository: LeagueRepository,
    private val missionRepository: com.axiom.app.domain.repository.MissionRepository,
    private val muscleGroupRepository: com.axiom.app.domain.repository.MuscleGroupRepository,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _pendingWorkoutMinutes = MutableStateFlow<Int?>(null)
    val pendingWorkoutMinutes: StateFlow<Int?> = _pendingWorkoutMinutes.asStateFlow()

    private var lastFocusMinutes: Int = 25

    fun triggerWorkoutPrompt(minutes: Int) {
        _pendingWorkoutMinutes.value = minutes
    }

    fun dismissWorkoutPrompt() {
        _pendingWorkoutMinutes.value = null
    }

    fun submitWorkoutTemplate(template: com.axiom.app.domain.model.WorkoutTemplate) {
        val mins = _pendingWorkoutMinutes.value ?: 30
        _pendingWorkoutMinutes.value = null
        scope.launch {
            try {
                val muscles = muscleGroupRepository.getAllMuscleGroups().first()
                val now = System.currentTimeMillis()
                val updated = com.axiom.app.domain.engine.MuscleEngine.applyWorkoutToMuscles(muscles, template, mins, now)
                muscleGroupRepository.insertMuscleGroups(updated)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        scope.launch {
            try {
                val saved = preferences.activeTimerStateFlow.first()
                if (saved.isActive) {
                    val now = System.currentTimeMillis()
                    if (saved.isPaused) {
                        _isPaused.value = true
                        _isTimerActive.value = true
                        _activeFocusTitle.value = saved.title
                        _timerSecondsRemaining.value = saved.pausedRemainingSeconds
                        _isBreachDetected.value = false
                        
                        if (saved.missionId.isNotEmpty()) {
                            val mission = missionRepository.getMissionById(saved.missionId)
                            _activeFocusMission.value = mission
                        } else if (saved.dungeonId.isNotEmpty()) {
                            _activeFocusDungeonId.value = saved.dungeonId
                            _isBossStage.value = saved.isBoss
                        }
                    } else {
                        if (now >= saved.endTimeMillis) {
                            _activeFocusTitle.value = saved.title
                            if (saved.missionId.isNotEmpty()) {
                                val mission = missionRepository.getMissionById(saved.missionId)
                                _activeFocusMission.value = mission
                            } else if (saved.dungeonId.isNotEmpty()) {
                                _activeFocusDungeonId.value = saved.dungeonId
                                _isBossStage.value = saved.isBoss
                            }
                            onTimerSuccessfullyComplete()
                        } else {
                            _isPaused.value = false
                            _isTimerActive.value = true
                            _activeFocusTitle.value = saved.title
                            val secondsRemaining = ((saved.endTimeMillis - now) / 1000).toInt()
                            _timerSecondsRemaining.value = secondsRemaining
                            endTimeMillis = saved.endTimeMillis
                            _isBreachDetected.value = false

                            if (saved.missionId.isNotEmpty()) {
                                val mission = missionRepository.getMissionById(saved.missionId)
                                _activeFocusMission.value = mission
                            } else if (saved.dungeonId.isNotEmpty()) {
                                _activeFocusDungeonId.value = saved.dungeonId
                                _isBossStage.value = saved.isBoss
                            }

                            timerJob?.cancel()
                            timerJob = scope.launch {
                                while (_timerSecondsRemaining.value > 0) {
                                    delay(1000)
                                    if (!_isPaused.value) {
                                        val remaining = ((endTimeMillis - System.currentTimeMillis()) / 1000).toInt()
                                        _timerSecondsRemaining.value = remaining.coerceAtLeast(0)
                                        if (remaining % 5 == 0) {
                                            startBackgroundService()
                                        }
                                    }
                                }
                                onTimerSuccessfullyComplete()
                            }
                            startBackgroundService()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _activeFocusMission = MutableStateFlow<Mission?>(null)
    val activeFocusMission: StateFlow<Mission?> = _activeFocusMission.asStateFlow()

    private val _activeFocusDungeonId = MutableStateFlow<String?>(null)
    val activeFocusDungeonId: StateFlow<String?> = _activeFocusDungeonId.asStateFlow()

    private val _isBossStage = MutableStateFlow(false)
    val isBossStage: StateFlow<Boolean> = _isBossStage.asStateFlow()

    private val _activeFocusTitle = MutableStateFlow<String?>(null)
    val activeFocusTitle: StateFlow<String?> = _activeFocusTitle.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(0)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _isTimerActive = MutableStateFlow(false)
    val isTimerActive: StateFlow<Boolean> = _isTimerActive.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isBreachDetected = MutableStateFlow(false)
    val isBreachDetected: StateFlow<Boolean> = _isBreachDetected.asStateFlow()

    private val _fastTimeSyncEnabled = MutableStateFlow(false)
    val fastTimeSyncEnabled: StateFlow<Boolean> = _fastTimeSyncEnabled.asStateFlow()

    private var timerJob: Job? = null
    private var endTimeMillis = 0L
    private var pausedRemainingSeconds = 0

    fun toggleFastTimeSync() {
        _fastTimeSyncEnabled.value = !_fastTimeSyncEnabled.value
    }

    fun pauseFocusProtocol() {
        if (_isTimerActive.value && !_isPaused.value) {
            _isPaused.value = true
            pausedRemainingSeconds = _timerSecondsRemaining.value
            stopBackgroundService()
            scope.launch {
                try {
                    val current = preferences.activeTimerStateFlow.first()
                    preferences.saveActiveTimerState(
                        current.copy(
                            isPaused = true,
                            pausedRemainingSeconds = pausedRemainingSeconds
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun resumeFocusProtocol() {
        if (_isTimerActive.value && _isPaused.value) {
            _isPaused.value = false
            endTimeMillis = System.currentTimeMillis() + pausedRemainingSeconds * 1000L
            startBackgroundService()
            scope.launch {
                try {
                    val current = preferences.activeTimerStateFlow.first()
                    preferences.saveActiveTimerState(
                        current.copy(
                            isPaused = false,
                            endTimeMillis = endTimeMillis
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun startFocusProtocol(mission: Mission, durationMinutes: Int) {
        timerJob?.cancel()
        lastFocusMinutes = durationMinutes
        _isPaused.value = false
        _activeFocusMission.value = mission
        _activeFocusDungeonId.value = null
        _isBossStage.value = false
        _activeFocusTitle.value = mission.title
        
        val seconds = if (_fastTimeSyncEnabled.value) durationMinutes else durationMinutes * 60
        _timerSecondsRemaining.value = seconds
        endTimeMillis = System.currentTimeMillis() + seconds * 1000L
        _isTimerActive.value = true
        _isBreachDetected.value = false

        if (mission.status.uppercase() != "ACTIVE") {
            scope.launch {
                try {
                    missionRepository.updateMission(mission.copy(status = "ACTIVE"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        scope.launch {
            try {
                preferences.saveActiveTimerState(
                    com.axiom.app.data.local.ActiveTimerState(
                        isActive = true,
                        startTimeMillis = System.currentTimeMillis(),
                        endTimeMillis = endTimeMillis,
                        title = mission.title,
                        missionId = mission.id,
                        dungeonId = "",
                        isBoss = false,
                        isPaused = false,
                        pausedRemainingSeconds = 0
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        timerJob = scope.launch {
            while (_timerSecondsRemaining.value > 0) {
                delay(1000)
                if (!_isPaused.value) {
                    val remaining = ((endTimeMillis - System.currentTimeMillis()) / 1000).toInt()
                    _timerSecondsRemaining.value = remaining.coerceAtLeast(0)
                    if (remaining % 5 == 0) {
                        startBackgroundService()
                    }
                }
            }
            onTimerSuccessfullyComplete()
        }
        startBackgroundService()
    }

    fun startFocusProtocolForDungeon(dungeonId: String, isBoss: Boolean, dungeonName: String) {
        timerJob?.cancel()
        _isPaused.value = false
        _activeFocusMission.value = null
        _activeFocusDungeonId.value = dungeonId
        _isBossStage.value = isBoss
        val titleVal = if (isBoss) "BOSS: $dungeonName" else "$dungeonName - Stage"
        _activeFocusTitle.value = titleVal
        
        val durationMinutes = if (isBoss) 25 else 15
        lastFocusMinutes = durationMinutes
        val seconds = if (_fastTimeSyncEnabled.value) durationMinutes else durationMinutes * 60
        _timerSecondsRemaining.value = seconds
        endTimeMillis = System.currentTimeMillis() + seconds * 1000L
        _isTimerActive.value = true
        _isBreachDetected.value = false

        scope.launch {
            try {
                preferences.saveActiveTimerState(
                    com.axiom.app.data.local.ActiveTimerState(
                        isActive = true,
                        startTimeMillis = System.currentTimeMillis(),
                        endTimeMillis = endTimeMillis,
                        title = titleVal,
                        missionId = "",
                        dungeonId = dungeonId,
                        isBoss = isBoss,
                        isPaused = false,
                        pausedRemainingSeconds = 0
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        timerJob = scope.launch {
            while (_timerSecondsRemaining.value > 0) {
                delay(1000)
                if (!_isPaused.value) {
                    val remaining = ((endTimeMillis - System.currentTimeMillis()) / 1000).toInt()
                    _timerSecondsRemaining.value = remaining.coerceAtLeast(0)
                    if (remaining % 5 == 0) {
                        startBackgroundService()
                    }
                }
            }
            onTimerSuccessfullyComplete()
        }
        startBackgroundService()
    }

    private suspend fun applyBreachPenalty(lpLoss: Int) {
        preferences.addLeaguePoints(-lpLoss)
        _isBreachDetected.value = true
        try {
            val hunter = getHunterProfileUseCase().firstOrNull()
            if (hunter != null) {
                leagueRepository.submitScore(
                    rarity = "BREACH",
                    xp = -lpLoss,
                    hunterName = hunter.name,
                    hunterRank = hunter.rankLabel
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseOrAbortFocusProtocol(isBreach: Boolean) {
        timerJob?.cancel()
        timerJob = null
        _isTimerActive.value = false
        _isPaused.value = false
        stopBackgroundService()
        scope.launch {
            try {
                preferences.clearActiveTimerState()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (isBreach) {
            scope.launch {
                val isHardMode = preferences.hardModeEnabledFlow.first()
                val daysSince  = preferences.daysSinceFirstLaunchFlow.first()

                when {
                    // Hard Mode فعاله → penalty کامل
                    isHardMode -> applyBreachPenalty(lpLoss = 15)

                    // روز ۷+ بدون Hard Mode → penalty کوچیک (آموزشی)
                    daysSince >= 7 -> applyBreachPenalty(lpLoss = 5)

                    // روز اول هفته → فقط warning، بدون penalty
                    else -> {
                        _isBreachDetected.value = true
                        // LP کم نمیشه
                    }
                }
            }
        } else {
            _activeFocusMission.value = null
            _activeFocusDungeonId.value = null
            _isBossStage.value = false
            _activeFocusTitle.value = null
        }
    }

    fun confirmBreachDismissed() {
        _isBreachDetected.value = false
        _activeFocusMission.value = null
        _activeFocusDungeonId.value = null
        _isBossStage.value = false
        _activeFocusTitle.value = null
    }

    private fun onTimerSuccessfullyComplete() {
        val mission = _activeFocusMission.value
        val dungeonId = _activeFocusDungeonId.value
        val isBoss = _isBossStage.value

        _isTimerActive.value = false
        timerJob?.cancel()
        timerJob = null
        stopBackgroundService()
        scope.launch {
            try {
                preferences.clearActiveTimerState()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        scope.launch {
            _pendingWorkoutMinutes.value = lastFocusMinutes
            val isHardMode = preferences.hardModeEnabledFlow.first()
            val daysSince  = preferences.daysSinceFirstLaunchFlow.first()

            val focusBonus = when {
                isHardMode     -> 25  // Hard Mode → big reward
                daysSince >= 7 -> 20  // هفته دوم
                daysSince >= 4 -> 15  // نیمه دوم هفته اول
                else           -> 10  // روزهای اول
            }

            preferences.addLeaguePoints(focusBonus)
            ceremonyEngine.emit(CeremonyEvent.FocusComplete(lpGained = focusBonus))
            try {
                val hunter = getHunterProfileUseCase().firstOrNull()
                if (hunter != null) {
                    leagueRepository.submitScore(
                        rarity = "FOCUS",
                        xp = focusBonus,
                        hunterName = hunter.name,
                        hunterRank = hunter.rankLabel
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (mission != null) {
                val oldHunter = getHunterProfileUseCase().firstOrNull()
                val oldRank = oldHunter?.rankLabel ?: "E-Rank"
                val result = completeMissionUseCase(mission.id, 1.0f)
                preferences.checkOffDailyProtocol()

                if (result != null) {
                    SoundEngine.play(AwakenSound.MISSION_COMPLETE)
                    SoundEngine.play(AwakenSound.XP_PING)

                    ceremonyEngine.emit(
                        CeremonyEvent.MissionComplete(
                            missionTitle = mission.title,
                            rarity = mission.rarity,
                            xpGained = result.hunterXPGained,
                            rarityColor = mission.rarityColor
                        )
                    )

                    if (result.leveledUp) {
                        val newLevel = result.newLevel ?: ((oldHunter?.level ?: 1) + 1)
                        ceremonyEngine.emit(CeremonyEvent.LevelUp(newLevel, oldHunter?.name ?: "Hunter"))
                        SoundEngine.play(AwakenSound.LEVEL_UP)
                    }
                    if (result.rankChanged) {
                        ceremonyEngine.emit(CeremonyEvent.RankUp(oldRank, result.newRank ?: "D-Rank"))
                        SoundEngine.play(AwakenSound.RANK_UP)
                    }
                    if (result.shadowUnlocked != null) {
                        ceremonyEngine.emit(
                            CeremonyEvent.ShadowAcquired(
                                skillName = result.shadowUnlocked.name,
                                rankLabel = result.shadowUnlocked.rankLabel
                            )
                        )
                        SoundEngine.play(AwakenSound.SHADOW_MANIFEST)
                    }
                }
                _activeFocusMission.value = null
            } else if (dungeonId != null) {
                if (isBoss) {
                    val result = defeatBossUseCase(dungeonId)
                    if (result != null) {
                        ceremonyEngine.emit(CeremonyEvent.CheckpointCleared(result.first, result.second))
                        SoundEngine.play(AwakenSound.BOSS_DEFEATED)
                    }
                } else {
                    completeDungeonStageUseCase(dungeonId)
                    SoundEngine.play(AwakenSound.MISSION_COMPLETE)
                }
                _activeFocusDungeonId.value = null
                _isBossStage.value = false
            }
        }
    }

    private fun startBackgroundService() {
        try {
            val intent = Intent(context, FocusTimerService::class.java)
            context.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopBackgroundService() {
        try {
            val intent = Intent(context, FocusTimerService::class.java)
            context.stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
