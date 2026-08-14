package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.engine.ROIEngine
import com.axiom.app.domain.model.*
import com.axiom.app.domain.usecase.CompleteMissionUseCase
import com.axiom.app.domain.usecase.CreateMissionUseCase
import com.axiom.app.domain.usecase.GetDungeonsUseCase
import com.axiom.app.domain.usecase.GetSkillsUseCase
import com.axiom.app.domain.usecase.GetMissionsUseCase
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.repository.MissionRepository
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.focus.FocusProtocolManager
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

sealed interface MissionsUiState {
    object Loading : MissionsUiState
    data class Success(
        val activeMissions: List<Mission>,
        val completedMissions: List<Mission>,
        val pendingMissions: List<Mission>,
        val skills: List<Skill>,
        val dungeons: List<Dungeon>
    ) : MissionsUiState
    data class Error(val message: String) : MissionsUiState
}

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val getMissionsUseCase: GetMissionsUseCase,
    private val getSkillsUseCase: GetSkillsUseCase,
    private val getDungeonsUseCase: GetDungeonsUseCase,
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val completeMissionUseCase: CompleteMissionUseCase,
    private val createMissionUseCase: CreateMissionUseCase,
    private val repository: MissionRepository,
    private val ceremonyEngine: CeremonyEngine,
    private val preferences: AxiomPreferences,
    private val feedRepository: SystemFeedRepository,
    private val focusProtocolManager: FocusProtocolManager
) : ViewModel() {

    // Forwarded surface — screens read/act through the ViewModel, not by reaching
    // into raw injected collaborators (was: public `preferences`/`focusProtocolManager`).
    val isFocusTimerActive: StateFlow<Boolean> = focusProtocolManager.isTimerActive
    val activeFocusMission: StateFlow<Mission?> = focusProtocolManager.activeFocusMission
    val activeFocusTitle: StateFlow<String?> = focusProtocolManager.activeFocusTitle

    fun startFocusProtocol(mission: Mission, durationMinutes: Int) {
        focusProtocolManager.startFocusProtocol(mission, durationMinutes)
    }

    suspend fun isMissionsBriefingShown(): Boolean = preferences.briefingMissionsFlow.first()

    fun markMissionsBriefingShown() {
        viewModelScope.launch { preferences.setBriefingShown("missions") }
    }

    init {
        viewModelScope.launch {
            val shown = preferences.briefingMissionsFlow.first()
            if (!shown) {
                delay(1500)
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = "Tap [ + ] to register your first mission. Power Score determines XP reward and rarity classification.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                preferences.setBriefingShown("missions")
            }
        }
    }

    private val _xpFloatEvent = MutableStateFlow<com.axiom.app.ui.components.XPFloatEvent?>(null)
    val xpFloatEvent: StateFlow<com.axiom.app.ui.components.XPFloatEvent?> = _xpFloatEvent.asStateFlow()

    private val _toastMessage = MutableStateFlow<Pair<String, Boolean>?>(null)
    val toastMessage: StateFlow<Pair<String, Boolean>?> = _toastMessage.asStateFlow()

    val missionsState: StateFlow<MissionsUiState> = combine(
        getMissionsUseCase(activeOnly = false),
        getSkillsUseCase(),
        getDungeonsUseCase()
    ) { missions, skills, dungeons ->
        val active = missions.filter { it.status.uppercase().trim() == "ACTIVE" }
        val completed = missions.filter { it.status.uppercase().trim() == "COMPLETED" }
        val pending = missions.filter { it.status.uppercase().trim() !in setOf("ACTIVE", "COMPLETED") }
        MissionsUiState.Success(
            activeMissions = active,
            completedMissions = completed,
            pendingMissions = pending,
            skills = skills,
            dungeons = dungeons
        ) as MissionsUiState
    }
        .catch { e -> emit(MissionsUiState.Error(e.message ?: "An unexpected error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MissionsUiState.Loading
        )

    fun completeMission(
        id: String,
        actualHours: Float?,
        goalSet: Boolean = true,
        gotFeedback: Boolean = true,
        pushedComfortZone: Boolean = true
    ) {
        viewModelScope.launch {
            val oldHunter = getHunterProfileUseCase().firstOrNull()
            val oldRank   = oldHunter?.rankLabel ?: "E-Rank"
            val mission   = repository.getMissionById(id)   // fetch before completing
            val result    = completeMissionUseCase(
                missionId = id,
                actualHours = actualHours,
                goalSet = goalSet,
                gotFeedback = gotFeedback,
                pushedComfortZone = pushedComfortZone
            )

            if (result != null) {
                // Streak is now owned by CompleteMissionUseCase (WP-205 canonical Room streak,
                // mirrored to DataStore post-commit). No separate checkOffDailyProtocol here —
                // that would double-count the daily streak transition.

                _xpFloatEvent.value = com.axiom.app.ui.components.XPFloatEvent(
                    xpValue          = result.hunterXPGained,
                    shadowMultiplier = result.shadowMultiplier
                )
                com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.MISSION_COMPLETE)
                com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.XP_PING)

                if (mission != null) {
                    val isFa = preferences.languageFlow.first() == "fa"
                    val msg    = SystemMessages.missionComplete(mission.rarity, isFa)
                    val isGold = mission.rarity.uppercase() in listOf("LEGENDARY", "EPIC", "DEPTH", "SHIELD", "WEALTH_ENGINE")
                    _toastMessage.value = Pair(msg, isGold)

                    val trackLower = mission.track.lowercase()
                    val titleLower = mission.title.lowercase()
                    val isExercise = trackLower.contains("fit") || trackLower.contains("health") || trackLower.contains("body") || trackLower.contains("exercise") ||
                                     titleLower.contains("workout") || titleLower.contains("gym") || titleLower.contains("run") || titleLower.contains("lift") || titleLower.contains("training") || titleLower.contains("ورزش") || titleLower.contains("تمرین")
                    if (isExercise) {
                        focusProtocolManager.triggerWorkoutPrompt(30)
                    }
                }

                // Emit MissionComplete first — queued before level/rank/shadow
                if (mission != null) {
                    ceremonyEngine.emit(CeremonyEvent.MissionComplete(
                        missionTitle = mission.title,
                        rarity       = mission.rarity,
                        xpGained     = result.hunterXPGained,
                        rarityColor  = mission.rarityColor
                    ))
                }

                if (result.leveledUp) {
                    val newLevel = result.newLevel ?: ((oldHunter?.level ?: 1) + 1)
                    ceremonyEngine.emit(CeremonyEvent.LevelUp(newLevel, oldHunter?.name ?: "Hunter"))
                    com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.LEVEL_UP)
                }
                if (result.rankChanged) {
                    ceremonyEngine.emit(CeremonyEvent.RankUp(oldRank, result.newRank ?: "D-Rank"))
                    com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.RANK_UP)
                }
                if (result.shadowUnlocked != null) {
                    ceremonyEngine.emit(CeremonyEvent.ShadowAcquired(
                        skillName = result.shadowUnlocked.name,
                        rankLabel = result.shadowUnlocked.rankLabel
                    ))
                    com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.SHADOW_MANIFEST)
                }
            }
        }
    }

    fun addMission(
        title: String,
        track: String,
        skillId: String,
        estimatedHours: Float,
        marketDemand: Float,
        leverage: Float,
        complexity: Float,
        dungeonId: String? = null,
        isInstantGate: Boolean = false,
        customRarity: String? = null,
        description: String = ""
    ) {
        viewModelScope.launch {
            val powerScore = ROIEngine.calculatePowerScore(marketDemand, leverage, complexity, estimatedHours)
            val rarity = customRarity ?: if (isInstantGate) "LEGENDARY" else ROIEngine.classifyRarity(powerScore).uppercase()
            val xpReward = (powerScore * 20f).toInt().coerceAtLeast(25)
            createMissionUseCase(
                title = title,
                track = track,
                rarity = rarity,
                skillId = skillId,
                xpReward = xpReward,
                powerScore = powerScore,
                estimatedHours = estimatedHours,
                dungeonId = dungeonId,
                isInstantGate = isInstantGate,
                description = description
            )
        }
    }

    fun addAndCompleteMission(
        title: String,
        track: String,
        skillId: String,
        estimatedHours: Float,
        marketDemand: Float,
        leverage: Float,
        complexity: Float,
        dungeonId: String? = null,
        isInstantGate: Boolean = false,
        customRarity: String? = null,
        description: String = "",
        actualHours: Float,
        goalSet: Boolean,
        gotFeedback: Boolean,
        pushedComfortZone: Boolean
    ) {
        viewModelScope.launch {
            val powerScore = ROIEngine.calculatePowerScore(marketDemand, leverage, complexity, estimatedHours)
            val rarity = customRarity ?: if (isInstantGate) "LEGENDARY" else ROIEngine.classifyRarity(powerScore).uppercase()
            val xpReward = (powerScore * 20f).toInt().coerceAtLeast(25)
            val missionId = createMissionUseCase(
                title = title,
                track = track,
                rarity = rarity,
                skillId = skillId,
                xpReward = xpReward,
                powerScore = powerScore,
                estimatedHours = estimatedHours,
                dungeonId = dungeonId,
                isInstantGate = isInstantGate,
                description = description
            )
            completeMission(
                id = missionId,
                actualHours = actualHours,
                goalSet = goalSet,
                gotFeedback = gotFeedback,
                pushedComfortZone = pushedComfortZone
            )
        }
    }

    fun createMission(
        title: String,
        track: String,
        rarity: String,
        skillId: String,
        xpReward: Int,
        powerScore: Float,
        estimatedHours: Float,
        dungeonId: String? = null
    ) {
        viewModelScope.launch {
            createMissionUseCase(
                title, track, rarity, skillId, xpReward, powerScore, estimatedHours, dungeonId
            )
        }
    }

    fun deferMission(id: String) {
        viewModelScope.launch {
            val mission = repository.getMissionById(id)
            if (mission != null) {
                if (mission.status.uppercase() == "ACTIVE") {
                    repository.updateMission(mission.copy(status = "DEFERRED"))
                } else {
                    repository.updateMission(mission.copy(status = "ACTIVE"))
                    focusProtocolManager.startFocusProtocol(mission, 25)
                }
            }
        }
    }

    fun deleteMission(id: String) {
        viewModelScope.launch {
            val mission = repository.getMissionById(id)
            if (mission != null) {
                repository.deleteMission(mission)
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun clearXpFloatEvent() {
        _xpFloatEvent.value = null
    }
}
