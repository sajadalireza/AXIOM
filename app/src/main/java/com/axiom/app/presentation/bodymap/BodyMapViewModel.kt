package com.axiom.app.presentation.bodymap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.engine.MuscleEngine
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.domain.model.WorkoutTemplate
import com.axiom.app.domain.repository.MuscleGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyMapViewModel @Inject constructor(
    private val muscleRepository: MuscleGroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BodyMapUiState>(BodyMapUiState.Loading)
    val uiState: StateFlow<BodyMapUiState> = _uiState.asStateFlow()

    private val _selectedMuscleId = MutableStateFlow<String?>(null)
    val selectedMuscleId: StateFlow<String?> = _selectedMuscleId.asStateFlow()

    init {
        viewModelScope.launch {
            muscleRepository.getAllMuscleGroups()
                .collect { list ->
                    if (list.isEmpty()) {
                        _uiState.value = BodyMapUiState.Empty
                    } else {
                        _uiState.value = BodyMapUiState.Success(list)
                    }
                }
        }
    }

    fun selectMuscle(id: String?) {
        _selectedMuscleId.value = id
    }

    fun recordWorkout(template: WorkoutTemplate, durationMinutes: Int) {
        val currentState = _uiState.value
        if (currentState is BodyMapUiState.Success) {
            viewModelScope.launch {
                val updatedList = MuscleEngine.applyWorkoutToMuscles(
                    muscleGroups = currentState.muscles,
                    template = template,
                    durationMinutes = durationMinutes,
                    now = System.currentTimeMillis()
                )
                updatedList.forEach { muscle ->
                    muscleRepository.insertMuscleGroup(muscle)
                }
            }
        }
    }

    fun recordCustomWorkout(muscleIds: List<String>, durationMinutes: Int) {
        val customWeights = muscleIds.associateWith { 1.0f }
        recordWorkout(WorkoutTemplate.Custom(customWeights), durationMinutes)
    }

    fun logTrainingSession(
        muscleId: String,
        hoursTrained: Float,
        goalSet: Boolean,
        gotFeedback: Boolean,
        pushedComfortZone: Boolean
    ) {
        val currentState = _uiState.value
        if (currentState is BodyMapUiState.Success) {
            viewModelScope.launch {
                val muscles = currentState.muscles
                val muscle = muscles.firstOrNull { it.id == muscleId }
                if (muscle != null) {
                    val scoreCount = (if (goalSet) 1 else 0) + (if (gotFeedback) 1 else 0) + (if (pushedComfortZone) 1 else 0)
                    val sessionQuality = scoreCount / 3.0
                    val newScore = com.axiom.app.domain.engine.MuscleRecoveryEngine.calculateStrengthGain(muscle.strengthScore, sessionQuality)
                    val updatedMuscle = muscle.copy(
                        strengthScore = newScore,
                        lastTrainedTimestamp = System.currentTimeMillis(),
                        freshnessPercent = com.axiom.app.domain.engine.MuscleRecoveryEngine.calculateFreshness(System.currentTimeMillis())
                    )
                    muscleRepository.updateMuscleGroup(updatedMuscle)
                }
            }
        }
    }
}

sealed interface BodyMapUiState {
    object Loading : BodyMapUiState
    object Empty : BodyMapUiState
    data class Success(val muscles: List<MuscleGroup>) : BodyMapUiState
}
