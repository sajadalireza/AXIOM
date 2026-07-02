package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.usecase.CompleteMissionUseCase
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.usecase.GetMissionsUseCase
import com.axiom.app.domain.focus.FocusProtocolManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.axiom.app.domain.repository.LeagueRepository
import com.axiom.app.data.remote.LeagueScoreRow
import com.axiom.app.domain.repository.ActivationRepository
import com.axiom.app.domain.repository.ActivationResult

sealed interface LeaguesUiState {
    object Loading : LeaguesUiState
    data class Success(
        val activeMissions: List<Mission>,
        val hunter: Hunter,
        val userLP: Int,
        val isPreregistered: Boolean,
        val rivals: List<RivalHunter>,
        val isLiveLeaderboard: Boolean
    ) : LeaguesUiState
    data class Error(val message: String) : LeaguesUiState
}

data class RivalHunter(
    val name: String,
    val rype: String, // e.g. "Shadow Monarch", "Goliath", etc.
    val rankLabel: String,
    val points: Int,
    val status: String, // "LIVE FOCUS", "RESTING", "PATROLLING GATES"
    val pointsDelta: Int = 0,
    val rankDelta: Int = 0,
    // True for locally-simulated rivals shown when no real Supabase leaderboard
    // data exists yet (see isLiveLeaderboard on LeaguesUiState.Success). Must
    // stay visually distinguishable in the UI so a bot is never mistaken for
    // a real competitor.
    val isGhost: Boolean = false
)

@HiltViewModel
class LeaguesViewModel @Inject constructor(
    private val getMissionsUseCase: GetMissionsUseCase,
    private val getHunterProfileUseCase: GetHunterProfileUseCase,
    private val completeMissionUseCase: CompleteMissionUseCase,
    private val preferences: AxiomPreferences,
    private val leagueRepository: LeagueRepository,
    private val focusProtocolManager: FocusProtocolManager,
    private val activationRepository: ActivationRepository
) : ViewModel() {

    private val _preregisterError = MutableStateFlow<String?>(null)
    val preregisterError: StateFlow<String?> = _preregisterError.asStateFlow()

    private val _isPreregistering = MutableStateFlow(false)
    val isPreregistering: StateFlow<Boolean> = _isPreregistering.asStateFlow()

    fun preRegisterForLeagues() {
        viewModelScope.launch {
            _isPreregistering.value = true
            _preregisterError.value = null
            
            val email = preferences.userEmailFlow.first() ?: ""
            if (email.isBlank()) {
                _preregisterError.value = "Activation required. No profile email has been registered yet."
                _isPreregistering.value = false
                return@launch
            }

            val result = activationRepository.preRegisterLeague(email)
            when (result) {
                is ActivationResult.Success -> {
                    preferences.setLeaguePreregistered(true)
                }
                is ActivationResult.Error -> {
                    _preregisterError.value = result.message
                }
            }
            _isPreregistering.value = false
        }
    }

    private val ghostArchetypes = listOf(
        Triple("Iron Shell",    "Defensive Crawler",   0.82f), // آهسته رشد میکنه
        Triple("Void Striker",  "Burst Protocol",      1.15f), // سریع ولی inconsistent
        Triple("Crimson Watch", "Endurance Type",      0.95f), // steady
        Triple("Ash Phantom",   "Silent Grinder",      1.05f), // کمی بالاتر از user
        Triple("Storm Caller",  "Aggressive Push",     1.20f), // challenge اصلی
        Triple("Echo Runner",   "Mirror Protocol",     0.90f)  // کمی پایینتر
    )

    private val _rivalsFlow = MutableStateFlow<List<RivalHunter>>(emptyList())
    val rivalsFlow: StateFlow<List<RivalHunter>> = _rivalsFlow.asStateFlow()

    val hardModeEnabledFlow = preferences.hardModeEnabledFlow
    val daysSinceFirstLaunchFlow = preferences.daysSinceFirstLaunchFlow

    private fun generateGhostRivals(userLP: Int, seed: String): List<RivalHunter> {
        val rng = java.util.Random(seed.hashCode().toLong())
        return ghostArchetypes.map { (name, rype, multiplier) ->
            val basePoints = (userLP * multiplier).toInt()
            val variance   = rng.nextInt(60) - 30 // کمی random (بین -30 تا +30)
            val statuses   = listOf(
                "ACTIVE PROTOCOL", "RESTING", "MISSION COMPLETE",
                "FOCUS LOCK", "STANDBY", "HUNTING"
            )
            RivalHunter(
                name      = name,
                rype      = rype,
                rankLabel = lpToRank(basePoints + variance),
                points    = (basePoints + variance).coerceAtLeast(10),
                status    = statuses[rng.nextInt(statuses.size)],
                isGhost   = true
            )
        }
    }

    private fun lpToRank(lp: Int) = when {
        lp < 200  -> "E-Rank"
        lp < 500  -> "D-Rank"
        lp < 900  -> "C-Rank"
        lp < 1400 -> "B-Rank"
        lp < 2000 -> "A-Rank"
        else      -> "S-Rank"
    }

    val activeFocusMission: StateFlow<Mission?> = focusProtocolManager.activeFocusMission
    val timerSecondsRemaining: StateFlow<Int> = focusProtocolManager.timerSecondsRemaining
    val isTimerActive: StateFlow<Boolean> = focusProtocolManager.isTimerActive
    val isTimerPaused: StateFlow<Boolean> = focusProtocolManager.isPaused
    val isBreachDetected: StateFlow<Boolean> = focusProtocolManager.isBreachDetected
    val fastTimeSyncEnabled: StateFlow<Boolean> = focusProtocolManager.fastTimeSyncEnabled

    private val _serverLeaderboard = MutableStateFlow<List<LeagueScoreRow>>(emptyList())
    val serverLeaderboard: StateFlow<List<LeagueScoreRow>> = _serverLeaderboard.asStateFlow()

    private val combinedLeaderboardFlow = _serverLeaderboard.map { serverList -> serverList }

    private val combinedFocusStateFlow = combine(
        focusProtocolManager.isTimerActive,
        focusProtocolManager.activeFocusMission
    ) { active, mission ->
        Pair(active, mission)
    }

    private val hunterWithConfigFlow = combine(
        getHunterProfileUseCase(),
        preferences.leaguePointsFlow,
        preferences.leaguePreregisteredFlow
    ) { hunter, points, prereg ->
        Triple(hunter, points, prereg)
    }

    val uiState: StateFlow<LeaguesUiState> = combine(
        getMissionsUseCase(activeOnly = true),
        hunterWithConfigFlow,
        combinedLeaderboardFlow,
        _rivalsFlow,
        combinedFocusStateFlow
    ) { activeMissions, hunterConfig, serverList, ghostRivals, focusPair ->
        val hunter = hunterConfig.first
        val points = hunterConfig.second
        val prereg = hunterConfig.third
        val isTimerActive = focusPair.first
        val activeFocusMission = focusPair.second

        if (hunter == null) {
            LeaguesUiState.Loading
        } else {
            // Map serverList if not empty, otherwise use generated ghostRivals
            val activeRivals = if (serverList.isNotEmpty()) {
                serverList.map { row ->
                    RivalHunter(
                        name = row.hunterName,
                        rype = "Global Challenger",
                        rankLabel = row.hunterRank,
                        points = row.totalScore.toInt(),
                        status = "READY FOR COMBAT",
                        pointsDelta = 0,
                        rankDelta = 0
                    )
                }
            } else {
                ghostRivals
            }

            // Force insert user themselves if they of course aren't in activeRivals, or update their entry.
            val hasUserInRivals = activeRivals.any { it.name.trim().lowercase() == hunter.name.trim().lowercase() || it.name.contains("(YOU)") }

            val userAsRival = RivalHunter(
                name = "${hunter.name} (YOU)",
                rype = "Awakened Protocol",
                rankLabel = hunter.rankLabel,
                points = points,
                status = if (isTimerActive) "LIVE FOCUS ON: ${activeFocusMission?.title}" else "IDLE",
                pointsDelta = 0,
                rankDelta = 0
            )

            val baseFullList = if (hasUserInRivals) {
                activeRivals.filterNot { it.name.trim().lowercase() == hunter.name.trim().lowercase() } + userAsRival
            } else {
                activeRivals + userAsRival
            }

            val fullList = baseFullList.sortedByDescending { it.points }

            LeaguesUiState.Success(
                activeMissions = activeMissions.filter { it.status == "ACTIVE" || it.status == "Active" },
                hunter = hunter,
                userLP = points,
                isPreregistered = prereg,
                rivals = fullList,
                isLiveLeaderboard = serverList.isNotEmpty()
            )
        }
    }.catch { e ->
        emit(LeaguesUiState.Error(e.message ?: "Sync channel connection failure."))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LeaguesUiState.Loading
    )

    fun fetchServerLeaderboard() {
        viewModelScope.launch {
            val list = leagueRepository.getLeaderboard()
            _serverLeaderboard.value = list

            // Reconcile user's local LP with server LP for trust/consistency
            try {
                val hunter = getHunterProfileUseCase().first()
                if (hunter != null && list.isNotEmpty()) {
                    val userRow = list.find { 
                        it.hunterName.trim().lowercase() == hunter.name.trim().lowercase() 
                    }
                    if (userRow != null) {
                        val serverLP = userRow.totalScore.toInt()
                        val localLP = preferences.leaguePointsFlow.first()
                        if (localLP != serverLP) {
                            val difference = serverLP - localLP
                            preferences.addLeaguePoints(difference)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        // هر بار LP کاربر تغییر کرد، rivals رو regenerate کن
        viewModelScope.launch {
            preferences.leaguePointsFlow.collect { userLP ->
                val hunterName = getHunterProfileUseCase().first()?.name ?: "Hunter"
                val freshRivals = generateGhostRivals(userLP, seed = hunterName)
                _rivalsFlow.value = freshRivals
            }
        }

        // Fluctuation loop: طبق archetype behavior
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                val userLP = preferences.leaguePointsFlow.first()
                val updated = _rivalsFlow.value.mapIndexed { i, rival ->
                    val (_, _, multiplier) = ghostArchetypes[i]
                    // هر ghost طبق multiplierش رشد میکنه
                    val growthRate = when {
                        multiplier > 1.1f -> (8..18).random()   // aggressive
                        multiplier > 0.95f -> (4..10).random()  // steady
                        else -> (1..6).random()                  // slow
                    }
                    val shouldGrow = Math.random() > 0.45
                    val statusOpts = listOf(
                        "ACTIVE PROTOCOL", "RESTING", "MISSION COMPLETE",
                        "FOCUS LOCK", "STANDBY", "HUNTING"
                    )
                    rival.copy(
                        points = if (shouldGrow) rival.points + growthRate else rival.points,
                        status = if (Math.random() > 0.75) statusOpts.random() else rival.status,
                        rankLabel = lpToRank(rival.points),
                        rankDelta = 0 // delta در sort مشخص میشه
                    )
                }
                // compute rank deltas
                val before = _rivalsFlow.value.sortedByDescending { it.points }
                val after  = updated.sortedByDescending { it.points }
                val withDelta = updated.map { rival ->
                    val oldRank = before.indexOfFirst { it.name == rival.name } + 1
                    val newRank = after.indexOfFirst  { it.name == rival.name } + 1
                    rival.copy(rankDelta = oldRank - newRank)
                }
                _rivalsFlow.value = withDelta
            }
        }

        fetchServerLeaderboard()
        viewModelScope.launch {
            while (true) {
                delay(60_000)   // هر ۱ دقیقه
                fetchServerLeaderboard()
            }
        }
    }

    fun toggleFastTimeSync() {
        focusProtocolManager.toggleFastTimeSync()
    }

    fun pauseFocusProtocol() {
        focusProtocolManager.pauseFocusProtocol()
    }

    fun resumeFocusProtocol() {
        focusProtocolManager.resumeFocusProtocol()
    }

    fun startFocusProtocol(mission: Mission, durationMinutes: Int) {
        focusProtocolManager.startFocusProtocol(mission, durationMinutes)
    }

    fun pauseOrAbortFocusProtocol(isBreach: Boolean) {
        focusProtocolManager.pauseOrAbortFocusProtocol(isBreach)
    }

    fun confirmBreachDismissed() {
        focusProtocolManager.confirmBreachDismissed()
    }

    fun setPreregistered(value: Boolean) {
        viewModelScope.launch {
            preferences.setLeaguePreregistered(value)
        }
    }
}
