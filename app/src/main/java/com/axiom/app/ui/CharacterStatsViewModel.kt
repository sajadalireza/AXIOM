package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.CharacterStats
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CharacterStatsUiState {
    object Loading : CharacterStatsUiState
    data class Success(
        val stats: CharacterStats,
        val pointsAvailable: Int
    ) : CharacterStatsUiState
    data class Error(val message: String) : CharacterStatsUiState
}

@HiltViewModel
class CharacterStatsViewModel @Inject constructor(
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val preferences: AxiomPreferences
) : ViewModel() {

    val statsState: StateFlow<CharacterStatsUiState> = combine(
        getHunterProfileUseCase(),
        preferences.statsFlow
    ) { hunter, stats ->
        if (hunter == null) {
            CharacterStatsUiState.Loading
        } else {
            val totalStatsAllocated = stats.execution + stats.focus + stats.knowledge + stats.business + stats.fitness + stats.creativity
            val statPointsAvailable = (hunter.level * 3) - totalStatsAllocated + 60
            CharacterStatsUiState.Success(
                stats = stats,
                pointsAvailable = statPointsAvailable.coerceAtLeast(0)
            )
        }
    }
    .catch { e ->
        emit(CharacterStatsUiState.Error(e.message ?: "An unexpected error occurred"))
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharacterStatsUiState.Loading
    )

    fun allocateStat(statName: String) {
        viewModelScope.launch {
            val hunter = getHunterProfileUseCase().firstOrNull() ?: return@launch
            val stats = preferences.statsFlow.first()
            val totalStatsAllocated = stats.execution + stats.focus + stats.knowledge + stats.business + stats.fitness + stats.creativity
            val statPointsAvailable = (hunter.level * 3) - totalStatsAllocated + 60

            if (statPointsAvailable > 0) {
                preferences.increaseStat(statName)
            }
        }
    }
}
