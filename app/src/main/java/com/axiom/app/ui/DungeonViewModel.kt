package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.usecase.CompleteDungeonStageUseCase
import com.axiom.app.domain.usecase.CreateDungeonUseCase
import com.axiom.app.domain.usecase.DefeatBossUseCase
import com.axiom.app.domain.usecase.GetDungeonsUseCase
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.focus.FocusProtocolManager

sealed interface DungeonsUiState {
    object Loading : DungeonsUiState
    data class Success(val dungeons: List<Dungeon>, val hunterLevel: Int, val isDevBypass: Boolean = false) : DungeonsUiState
    data class Error(val message: String) : DungeonsUiState
}

@HiltViewModel
class DungeonViewModel @Inject constructor(
    private val getDungeonsUseCase: GetDungeonsUseCase,
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val completeDungeonStageUseCase: CompleteDungeonStageUseCase,
    private val defeatBossUseCase: DefeatBossUseCase,
    private val createDungeonUseCase: CreateDungeonUseCase,
    private val ceremonyEngine: CeremonyEngine,
    val preferences: AxiomPreferences,
    private val feedRepository: SystemFeedRepository,
    val focusProtocolManager: FocusProtocolManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            val shown = preferences.briefingDungeonsFlow.first()
            if (!shown) {
                delay(1500)
                val isFa = java.util.Locale.getDefault().language == "fa"
                val msg = if (isFa) {
                    "دانجن‌ها عملیات‌های چندمرحله‌ای هستند. تمام مراحل را تکمیل کرده و باس را شکست دهید تا جوایز ویژه XP دریافت نمایید."
                } else {
                    "Dungeons are multi-stage operations. Complete all stages and defeat the boss to claim bonus XP rewards."
                }
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = msg,
                        timestamp = System.currentTimeMillis()
                    )
                )
                preferences.setBriefingShown("dungeons")
            }
        }
    }

    val dungeonsState: StateFlow<DungeonsUiState> = combine(
        getDungeonsUseCase(),
        getHunterProfileUseCase(),
        preferences.devBypassFlow
    ) { dungeons, hunter, devBypass ->
        DungeonsUiState.Success(dungeons, hunter?.level ?: 1, devBypass) as DungeonsUiState
    }
        .catch { e -> emit(DungeonsUiState.Error(e.message ?: "An unexpected error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DungeonsUiState.Loading
        )

    private val _stageCompletionEvent = MutableSharedFlow<StageCompletionEvent>()
    val stageCompletionEvent = _stageCompletionEvent.asSharedFlow()

    sealed interface StageCompletionEvent {
        data class MidStageCleared(val message: String) : StageCompletionEvent
        data class BossDefeated(val bossName: String, val xpGained: Long) : StageCompletionEvent
    }

    fun completeStage(dungeonId: String) {
        viewModelScope.launch {
            val dungeons = when (val s = dungeonsState.value) {
                is DungeonsUiState.Success -> s.dungeons
                else -> getDungeonsUseCase().first()
            }
            val dungeon = dungeons.firstOrNull { it.id == dungeonId } ?: return@launch
            val nextCompleted = dungeon.completedStages + 1
            val wasLastStage = nextCompleted >= dungeon.totalStages

            completeDungeonStageUseCase(dungeonId)

            if (wasLastStage) {
                val result = defeatBossUseCase(dungeonId)
                if (result != null) {
                    ceremonyEngine.emit(CeremonyEvent.BossDefeated(result.first, result.second.toLong()))
                    com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.BOSS_DEFEATED)
                    _stageCompletionEvent.emit(StageCompletionEvent.BossDefeated(result.first, result.second.toLong()))
                } else {
                    _stageCompletionEvent.emit(StageCompletionEvent.BossDefeated(dungeon.name, 100L))
                }
            } else {
                _stageCompletionEvent.emit(StageCompletionEvent.MidStageCleared("STAGE CLEARED"))
            }
        }
    }

    fun defeatBoss(dungeonId: String) {
        viewModelScope.launch {
            val result = defeatBossUseCase(dungeonId)
            if (result != null) {
                ceremonyEngine.emit(CeremonyEvent.BossDefeated(result.first, result.second.toLong()))
                com.axiom.app.core.sound.SoundEngine.play(com.axiom.app.core.sound.AwakenSound.BOSS_DEFEATED)
            }
        }
    }

    fun createDungeon(name: String, description: String, rarity: String, totalStages: Int, stageDescriptions: String = "") {
        viewModelScope.launch {
            createDungeonUseCase(name, description, rarity, totalStages, stageDescriptions)
        }
    }
}
