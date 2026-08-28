package com.axiom.app.presentation.firstwin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinPosition
import com.axiom.app.domain.model.Mission
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FirstWinUiError {
    LOAD,
    CREATE_MISSION,
    COMPLETE_MISSION,
    ACK_REWARD,
    FINISH_FOR_NOW,
    COMPLETE_HANDOFF,
}

data class FirstWinEntryUiState(
    val isLoading: Boolean = false,
    val isBusy: Boolean = false,
    val sessionId: String? = null,
    val position: FirstWinPosition? = null,
    val mission: Mission? = null,
    val draft: FirstWinDraftState = FirstWinDraftReducer.fresh(),
    val error: FirstWinUiError? = null,
)

@HiltViewModel
class FirstWinViewModel @Inject constructor(
    private val runtime: FirstWinJourneyRuntime,
) : ViewModel() {
    private val _state = MutableStateFlow(FirstWinEntryUiState())
    val state: StateFlow<FirstWinEntryUiState> = _state.asStateFlow()

    fun start() {
        val current = _state.value
        if (current.isLoading || current.sessionId != null) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val snapshot = runtime.open()
                applySnapshot(snapshot)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
                _state.update { it.copy(isLoading = false, error = FirstWinUiError.LOAD) }
            }
        }
    }

    fun selectArea(area: FirstWinArea) {
        if (_state.value.isBusy) return
        _state.update { it.copy(draft = FirstWinDraftReducer.selectArea(it.draft, area)) }
    }

    fun continueFromArea() {
        if (_state.value.isBusy) return
        _state.update { it.copy(draft = FirstWinDraftReducer.continueFromArea(it.draft)) }
    }

    fun setActionTitle(title: String) {
        if (_state.value.isBusy) return
        _state.update { it.copy(draft = FirstWinDraftReducer.setActionTitle(it.draft, title)) }
    }

    fun backToArea() {
        if (_state.value.isBusy) return
        _state.update { it.copy(draft = FirstWinDraftReducer.backToArea(it.draft)) }
    }

    fun createMission() {
        val current = _state.value
        val area = current.draft.selectedArea ?: return
        if (
            current.isBusy ||
            (current.position != null && current.position != FirstWinPosition.AREA) ||
            !current.draft.canCreateMission
        ) return
        val actionTitle = current.draft.actionTitle

        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                val sessionId = current.sessionId ?: runtime.open().sessionId
                applySnapshot(runtime.createMission(sessionId, area, actionTitle))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
                _state.update {
                    it.copy(isBusy = false, error = FirstWinUiError.CREATE_MISSION)
                }
            }
        }
    }

    fun completeMission() {
        val current = _state.value
        val sessionId = current.sessionId ?: return
        val mission = current.mission ?: return
        if (current.isBusy || current.position != FirstWinPosition.DO) return

        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                applySnapshot(runtime.completeMission(sessionId, mission.id))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
                _state.update {
                    it.copy(isBusy = false, error = FirstWinUiError.COMPLETE_MISSION)
                }
            }
        }
    }

    fun continueFromReward() {
        val current = _state.value
        val sessionId = current.sessionId ?: return
        if (current.isBusy || current.position != FirstWinPosition.REWARD) return

        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                applySnapshot(runtime.acknowledgeReward(sessionId))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
                _state.update { it.copy(isBusy = false, error = FirstWinUiError.ACK_REWARD) }
            }
        }
    }

    fun finishForNow() {
        val current = _state.value
        val sessionId = current.sessionId ?: return
        if (current.isBusy || current.position != FirstWinPosition.NEXT) return

        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                applySnapshot(runtime.finishForNow(sessionId))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
                _state.update { it.copy(isBusy = false, error = FirstWinUiError.FINISH_FOR_NOW) }
            }
        }
    }

    fun completeHandoff() {
        val current = _state.value
        val sessionId = current.sessionId ?: return
        if (current.isBusy || current.position != FirstWinPosition.HANDOFF) return

        _state.update { it.copy(isBusy = true, error = null) }
        viewModelScope.launch {
            try {
                applySnapshot(runtime.completeHandoff(sessionId))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Throwable) {
                _state.update { it.copy(isBusy = false, error = FirstWinUiError.COMPLETE_HANDOFF) }
            }
        }
    }

    private fun applySnapshot(snapshot: FirstWinJourneySnapshot) {
        _state.update {
            it.copy(
                isLoading = false,
                isBusy = false,
                sessionId = snapshot.sessionId,
                position = snapshot.position,
                mission = snapshot.mission,
                error = null,
            )
        }
    }
}
