package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.core.XionEvent
import com.axiom.app.core.XionEventBus
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.focus.FocusProtocolManager
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.domain.model.*
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AxiomUiState {
    object Loading : AxiomUiState
    data class Success(
        val hunter: Hunter,
        val missions: List<Mission>,
        val skills: List<Skill>,
        val dungeons: List<Dungeon>,
        val shadows: List<Shadow>,
        val streak: Int,
        val lastCompleteTimestamp: Long,
        val stats: CharacterStats
    ) : AxiomUiState
    data class Error(val message: String) : AxiomUiState
}

@HiltViewModel
class AxiomViewModel @Inject constructor(
    private val repository: HunterRepository,
    private val initializeAxiomUseCase: InitializeAxiomUseCase,
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val getMissionsUseCase: GetMissionsUseCase,
    private val getSkillsUseCase: GetSkillsUseCase,
    private val getDungeonsUseCase: GetDungeonsUseCase,
    private val getShadowsUseCase: GetShadowsUseCase,
    private val completeMissionUseCase: CompleteMissionUseCase,
    private val createMissionUseCase: CreateMissionUseCase,
    private val createSkillUseCase: CreateSkillUseCase,
    private val createDungeonUseCase: CreateDungeonUseCase,
    private val ariseShadowUseCase: AriseShadowUseCase,
    val preferences: AxiomPreferences,
    private val xionEventBus: XionEventBus,
    private val focusProtocolManager: FocusProtocolManager,
    private val cloudSyncRepository: com.axiom.app.domain.repository.CloudSyncRepository
) : ViewModel() {

    init {
        // Seeding and onboarding initialization are managed during onboarding flow.
    }

    val uiState: StateFlow<AxiomUiState> = combine(
        getHunterProfileUseCase(),
        getMissionsUseCase(activeOnly = false),
        getSkillsUseCase(),
        getDungeonsUseCase(),
        getShadowsUseCase(),
        preferences.streakFlow,
        preferences.lastCompleteTimestampFlow,
        preferences.statsFlow
    ) { args: Array<Any?> ->
        val profile = args[0] as? Hunter
        @Suppress("UNCHECKED_CAST")
        val missions = args[1] as? List<Mission> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val skills = args[2] as? List<Skill> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val dungeons = args[3] as? List<Dungeon> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val shadows = args[4] as? List<Shadow> ?: emptyList()
        val streak = args[5] as? Int ?: 0
        val lastTime = args[6] as? Long ?: 0L
        val stats = args[7] as? CharacterStats ?: CharacterStats(10, 10, 10, 10, 10, 10)

        if (profile == null) {
            AxiomUiState.Loading
        } else {
            AxiomUiState.Success(
                hunter = profile,
                missions = missions,
                skills = skills,
                dungeons = dungeons,
                shadows = shadows,
                streak = streak,
                lastCompleteTimestamp = lastTime,
                stats = stats
            )
        }
    }
        .catch { e -> emit(AxiomUiState.Error(e.message ?: "An unexpected error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AxiomUiState.Loading
        )

    fun completeMission(missionId: String, actualHours: Float? = null) {
        viewModelScope.launch {
            // Capture pre-completion context for the event bus — the use case
            // mutates the mission/hunter, so title/rarity/oldRank must be read first.
            val before = uiState.value as? AxiomUiState.Success
            val missionBefore = before?.missions?.firstOrNull { it.id == missionId }
            val oldRank = before?.hunter?.rankLabel

            val result = completeMissionUseCase(missionId, actualHours)

            if (result != null && missionBefore != null) {
                xionEventBus.emit(
                    XionEvent.MissionCompleted(
                        missionTitle = missionBefore.title,
                        rarity = missionBefore.rarity,
                        xpGained = result.hunterXPGained
                    )
                )

                val trackLower = missionBefore.track.lowercase()
                val titleLower = missionBefore.title.lowercase()
                val isExercise = trackLower.contains("fit") || trackLower.contains("health") || trackLower.contains("body") || trackLower.contains("exercise") ||
                                 titleLower.contains("workout") || titleLower.contains("gym") || titleLower.contains("run") || titleLower.contains("lift") || titleLower.contains("training") || titleLower.contains("ورزش") || titleLower.contains("تمرین")
                if (isExercise) {
                    focusProtocolManager.triggerWorkoutPrompt(30)
                }

                // RankUp takes priority over a same-completion LevelUp —
                // a rank change is the more significant moment.
                if (result.rankChanged && result.newRank != null && oldRank != null) {
                    xionEventBus.emit(XionEvent.RankUp(oldRank = oldRank, newRank = result.newRank))
                } else if (result.leveledUp && result.newLevel != null) {
                    xionEventBus.emit(XionEvent.LevelUp(newLevel = result.newLevel))
                }
            }
            cloudSyncRepository.backupProgress()
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
            cloudSyncRepository.backupProgress()
        }
    }

    fun createSkill(name: String, category: String, parentId: String? = null, trackId: String? = null) {
        viewModelScope.launch {
            createSkillUseCase(name, category, parentId, trackId)
            cloudSyncRepository.backupProgress()
        }
    }

    fun createDungeon(name: String, description: String, rarity: String, totalStages: Int) {
        viewModelScope.launch {
            createDungeonUseCase(name, description, rarity, totalStages)
            cloudSyncRepository.backupProgress()
        }
    }

    fun ariseShadow(skillId: String, shadowName: String) {
        viewModelScope.launch {
            ariseShadowUseCase(skillId, shadowName)
            cloudSyncRepository.backupProgress()
        }
    }

    fun checkOffDailyProtocol() {
        viewModelScope.launch {
            // Read streak BEFORE the protocol mutates it, so we can detect a
            // break-then-restart (streak resets to 1 after being > 1).
            val streakBefore = preferences.streakFlow.first()
            val updated = preferences.checkOffDailyProtocol()
            if (updated) {
                val streakAfter = preferences.streakFlow.first()

                if (streakBefore > 1 && streakAfter == 1) {
                    xionEventBus.emit(XionEvent.StreakBroken)
                }

                if (streakAfter == 7 || streakAfter == 30 || streakAfter == 100) {
                    val lastShownMilestone = preferences.lastShownStreakMilestoneFlow.first()
                    if (lastShownMilestone < streakAfter) {
                        preferences.setLastShownStreakMilestone(streakAfter)
                        xionEventBus.emit(XionEvent.StreakMilestone(streakAfter))
                    }
                }

                // If checked off, award custom Daily Protocol completion rewards:
                // We add 50 XP with a multiplier calculated from streak!
                val currentProfile = getHunterProfileUseCase().first() ?: return@launch
                val multiplier = 1.0f + (streakAfter * 0.1f).coerceAtMost(1.0f)
                val baseXP = 50f
                val awardedXP = (baseXP * multiplier).toInt()

                var newHunterXP = currentProfile.currentXP + awardedXP
                var newHunterLevel = currentProfile.level
                var nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
                val totalXP = currentProfile.totalXP + awardedXP

                while (newHunterXP >= nextLevelXP && newHunterLevel < 100) {
                    newHunterXP -= nextLevelXP
                    newHunterLevel++
                    nextLevelXP = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
                }
                if (newHunterLevel >= 100) {
                    newHunterLevel = 100
                    newHunterXP = 0
                    nextLevelXP = XPEngine.xpNeededForLevel(100).toInt()
                }

                val hunterRank = XPEngine.calculateHunterRank(newHunterLevel)
                val hunterRankWithSuffix = if (hunterRank.endsWith("-Rank")) hunterRank else "$hunterRank-Rank"
                val hunterRankColor = XPEngine.getRankColor(hunterRank)
                val hunterRankGlyph = XPEngine.getGlyphForRank(hunterRank)

                val updatedHunter = currentProfile.copy(
                    level = newHunterLevel,
                    rankLabel = hunterRankWithSuffix,
                    totalXP = totalXP,
                    currentXP = newHunterXP,
                    xpToNextLevel = nextLevelXP,
                    progressPercent = if (newHunterLevel >= 100) 1.0f else newHunterXP.toFloat() / nextLevelXP.toFloat(),
                    rankColor = hunterRankColor,
                    rankGlyph = hunterRankGlyph
                )
                repository.updateHunterProfile(updatedHunter)

                if (newHunterLevel > currentProfile.level) {
                    xionEventBus.emit(XionEvent.LevelUp(newHunterLevel))
                }
            }
            cloudSyncRepository.backupProgress()
        }
    }

    fun allocateStat(statName: String) {
        viewModelScope.launch {
            val currentProfile = getHunterProfileUseCase().firstOrNull() ?: return@launch
            val stats = preferences.statsFlow.first()
            val totalStatsAllocated = stats.execution + stats.focus + stats.knowledge + stats.business + stats.fitness + stats.creativity
            val statPointsAvailable = (currentProfile.level * 3) - totalStatsAllocated + 60 // base stats starting at 10 (60 total)

            if (statPointsAvailable > 0) {
                preferences.increaseStat(statName)
                cloudSyncRepository.backupProgress()
            }
        }
    }

    private val _dailyLoginBonusGranted = MutableStateFlow(false)
    val dailyLoginBonusGranted: StateFlow<Boolean> = _dailyLoginBonusGranted.asStateFlow()

    fun notifyDailyLoginBonus() {
        viewModelScope.launch {
            _dailyLoginBonusGranted.value = true
            kotlinx.coroutines.delay(3500)
            _dailyLoginBonusGranted.value = false
        }
    }

    val weeklyProgress: StateFlow<com.axiom.app.domain.model.WeeklyProgress> =
        preferences.weeklyProgressFlow
            .stateIn(viewModelScope, SharingStarted.Lazily, com.axiom.app.domain.model.WeeklyProgress())

    fun claimWeeklyBonus() {
        viewModelScope.launch {
            if (preferences.claimWeeklyBonus()) {
                val hunter = getHunterProfileUseCase().first() ?: return@launch
                repository.updateHunterProfile(
                    hunter.copy(totalXP = hunter.totalXP + 150, currentXP = hunter.currentXP + 150)
                )
            }
        }
    }
}
