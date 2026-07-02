package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.usecase.AriseShadowUseCase
import com.axiom.app.domain.usecase.GetShadowsUseCase
import com.axiom.app.domain.usecase.GetSkillsUseCase
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.data.local.AxiomPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

import com.axiom.app.domain.usecase.GetHunterProfileUseCase

import com.axiom.app.domain.usecase.GetMissionsUseCase
import com.axiom.app.domain.model.Mission

sealed interface ShadowArmyUiState {
    object Loading : ShadowArmyUiState
    data class Success(
        val shadows: List<Shadow>,
        val candidates: List<Skill>,
        val hunterLevel: Int,
        val isDevBypass: Boolean = false,
        val missions: List<Mission> = emptyList()
    ) : ShadowArmyUiState
    data class Error(val message: String) : ShadowArmyUiState
}

@HiltViewModel
class ShadowViewModel @Inject constructor(
    private val getShadowsUseCase: GetShadowsUseCase,
    private val getSkillsUseCase: GetSkillsUseCase,
    private val ariseShadowUseCase: AriseShadowUseCase,
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val getMissionsUseCase: GetMissionsUseCase,
    val preferences: AxiomPreferences,
    private val feedRepository: SystemFeedRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            val shown = preferences.briefingShadowFlow.first()
            if (!shown) {
                delay(1500)
                val isFa = java.util.Locale.getDefault().language == "fa"
                val msg = if (isFa) {
                    "تیم هوشمند مجازی آنلاین شد. هر مأمور فعال +۵٪ امتیاز اضافی برای اهداف همسو اختصاص می‌دهد. حداکثر امتیاز اضافی: +۵۰٪."
                } else {
                    "Virtual Team online. Each active Operative grants +5% XP focus bonus for aligned sessions in their category. Max limit bonus: +50%."
                }
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = msg,
                        timestamp = System.currentTimeMillis()
                    )
                )
                preferences.setBriefingShown("shadow")
            }
        }
    }

    val shadowsState: StateFlow<ShadowArmyUiState> = combine(
        getShadowsUseCase(),
        getSkillsUseCase(),
        getHunterProfileUseCase(),
        preferences.devBypassFlow,
        getMissionsUseCase()
    ) { shadows, skills, hunter, devBypass, missions ->
        val candidates = skills.filter { it.isShadowCandidate }
        ShadowArmyUiState.Success(
            shadows = shadows,
            candidates = candidates,
            hunterLevel = hunter?.level ?: 1,
            isDevBypass = devBypass,
            missions = missions
        ) as ShadowArmyUiState
    }
    .catch { e -> emit(ShadowArmyUiState.Error(e.message ?: "Could not load Shadow Army.")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShadowArmyUiState.Loading
    )

    fun ariseShadow(skillId: String, shadowName: String) {
        viewModelScope.launch {
            ariseShadowUseCase(skillId, shadowName)
        }
    }
}
