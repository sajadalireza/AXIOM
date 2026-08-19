package com.axiom.app.presentation.firstwin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinPosition
import com.axiom.app.domain.model.Mission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class FirstWinUiError { LOAD, CREATE_MISSION }

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

    fun start(): Unit = TODO("WP-207 RED")
    fun selectArea(area: FirstWinArea): Unit = TODO("WP-207 RED")
    fun continueFromArea(): Unit = TODO("WP-207 RED")
    fun setActionTitle(title: String): Unit = TODO("WP-207 RED")
    fun backToArea(): Unit = TODO("WP-207 RED")
    fun createMission(): Unit = TODO("WP-207 RED")
}
