package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.core.ai.SystemVoiceEngine
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.engine.ROIEngine
import com.axiom.app.domain.model.AIMissionSuggestion
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.usecase.CreateMissionUseCase
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.usecase.GetSkillsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIMissionGeneratorViewModel @Inject constructor(
    private val engine: SystemVoiceEngine,
    private val preferences: AxiomPreferences,
    private val getHunterProfile: GetHunterProfileUseCase,
    private val getSkills: GetSkillsUseCase,
    private val createMission: CreateMissionUseCase
) : ViewModel() {

    private val _suggestions = MutableStateFlow<List<AIMissionSuggestion>>(emptyList())
    private val _isLoading   = MutableStateFlow(false)
    private val _selected    = MutableStateFlow<Set<String>>(emptySet())
    private val _created     = MutableStateFlow(false)

    val suggestions: StateFlow<List<AIMissionSuggestion>> = _suggestions.asStateFlow()
    val isLoading: StateFlow<Boolean>     = _isLoading.asStateFlow()
    val selected: StateFlow<Set<String>>  = _selected.asStateFlow()
    val created: StateFlow<Boolean>       = _created.asStateFlow()

    val hasApiKey: StateFlow<Boolean> = preferences.geminiApiKeyFlow
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Estimated total XP for selected missions
    val totalXp: StateFlow<Int> = combine(_suggestions, _selected) { list, sel ->
        list.filter { sel.contains(it.title) }
            .sumOf { (it.estimatedHours * 100).toInt() }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun generate(goal: String) {
        viewModelScope.launch {
            val hunter = getHunterProfile().first() ?: return@launch
            val skills = getSkills().first()
            val streak = preferences.streakFlow.first()
            _isLoading.value = true
            _selected.value  = emptySet()
            _suggestions.value = engine.generateStructuredMissions(goal, skills, hunter, streak)
            _isLoading.value = false
        }
    }

    fun toggleSelection(suggestion: AIMissionSuggestion) {
        _selected.value = if (_selected.value.contains(suggestion.title))
            _selected.value - suggestion.title
        else
            _selected.value + suggestion.title
    }

    fun acceptSelected() {
        viewModelScope.launch {
            val hunter = getHunterProfile().first() ?: return@launch
            val skills = getSkills().first()
            _suggestions.value
                .filter { _selected.value.contains(it.title) }
                .forEach { s ->
                    val skill = skills.firstOrNull { it.name == s.skillName && it.isUnlocked }
                        ?: skills.firstOrNull { it.isUnlocked }
                        ?: return@forEach
                    val powerScore = ROIEngine.calculatePowerScore(
                        marketDemand   = 5f,
                        leverage       = 5f,
                        complexity     = 5f,
                        estimatedHours = s.estimatedHours
                    )
                    createMission(
                        title          = s.title,
                        track          = skill.category,
                        rarity         = s.rarity,
                        skillId        = skill.id,
                        xpReward       = (s.estimatedHours * 100).toInt(),
                        powerScore     = powerScore,
                        estimatedHours = s.estimatedHours,
                        description    = s.description
                    )
                }
            _created.value = true
        }
    }
}
