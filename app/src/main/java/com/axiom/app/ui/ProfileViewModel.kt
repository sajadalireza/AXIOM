package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.usecase.GetMissionsUseCase
import com.axiom.app.domain.usecase.GetShadowsUseCase
import com.axiom.app.domain.usecase.GetDungeonsUseCase
import com.axiom.app.domain.usecase.GetSkillsUseCase
import com.axiom.app.data.local.dao.WeeklyReviewDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(
        val hunter: Hunter,
        val totalMissionsCompleted: Int,
        val totalMissionsActive: Int,
        val currentStreak: Int,
        val longestStreak: Int,
        val totalXPEarned: Long,
        val shadowCount: Int = 0,
        val profileImageUri: String? = null,
        val completedMissions: List<Mission> = emptyList(),
        val defeatedBosses: List<com.axiom.app.domain.model.Dungeon> = emptyList(),
        val weeklyReviews: List<com.axiom.app.data.local.entity.WeeklyReviewEntity> = emptyList(),
        val skills: List<com.axiom.app.domain.model.Skill> = emptyList(),
        val activePersona: String = "Research Scientist"
    ) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val getMissionsUseCase: GetMissionsUseCase,
    private val getShadowsUseCase: GetShadowsUseCase,
    private val getDungeonsUseCase: GetDungeonsUseCase,
    private val getSkillsUseCase: GetSkillsUseCase,
    private val weeklyReviewDao: WeeklyReviewDao,
    val preferences: AxiomPreferences
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        getHunterProfileUseCase(),
        getMissionsUseCase(activeOnly = false),
        getShadowsUseCase(),
        preferences.streakFlow,
        preferences.longestStreakFlow,
        preferences.profileImageUriFlow,
        getDungeonsUseCase(),
        getSkillsUseCase(),
        weeklyReviewDao.getAllReviews(),
        preferences.systemVoiceModeFlow
    ) { args: Array<Any?> ->
        val hunter = args[0] as? Hunter
        @Suppress("UNCHECKED_CAST")
        val missions = args[1] as? List<Mission> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val shadows = args[2] as? List<Shadow> ?: emptyList()
        val currentStreak = args[3] as? Int ?: 0
        val longestStreak = args[4] as? Int ?: 0
        val profileImageUri = args[5] as? String
        @Suppress("UNCHECKED_CAST")
        val dungeons = args[6] as? List<com.axiom.app.domain.model.Dungeon> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val skills = args[7] as? List<com.axiom.app.domain.model.Skill> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val reviews = args[8] as? List<com.axiom.app.data.local.entity.WeeklyReviewEntity> ?: emptyList()
        val voiceMode = args[9] as? String ?: "research_scientist"

        if (hunter == null) {
            ProfileUiState.Loading
        } else {
            val totalMissionsCompleted = missions.count { it.status.uppercase() == "COMPLETED" }
            val totalMissionsActive = missions.count { it.status.uppercase() == "ACTIVE" }
            val completedMissions = missions.filter { it.status.uppercase() == "COMPLETED" }
                .sortedByDescending { it.completedAt ?: 0L }
            val defeatedBosses = dungeons.filter { it.isBossDefeated }
                .sortedByDescending { it.completedAt ?: 0L }
            val activePersona = com.axiom.app.domain.model.WarriorPersona.fromId(voiceMode)?.personaName ?: "Research Scientist"

            ProfileUiState.Success(
                hunter = hunter,
                totalMissionsCompleted = totalMissionsCompleted,
                totalMissionsActive = totalMissionsActive,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalXPEarned = hunter.totalXP,
                shadowCount = shadows.size,
                profileImageUri = profileImageUri,
                completedMissions = completedMissions,
                defeatedBosses = defeatedBosses,
                weeklyReviews = reviews.sortedByDescending { it.timestamp },
                skills = skills,
                activePersona = activePersona
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState.Loading
    )

    fun setLanguage(lang: String) { viewModelScope.launch { preferences.setLanguage(lang) } }

    fun setHardMode(value: Boolean) {
        viewModelScope.launch {
            preferences.setHardModeEnabled(value)
        }
    }

    fun saveProfileImageUri(uri: String?) {
        viewModelScope.launch {
            preferences.setProfileImageUri(uri)
        }
    }
}
