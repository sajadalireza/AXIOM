package com.axiom.app.presentation.firstwin

import com.axiom.app.domain.firstwin.FirstWinArea

enum class FirstWinDraftStep { AREA, ACTION }

data class FirstWinDraftState(
    val step: FirstWinDraftStep,
    val selectedArea: FirstWinArea?,
    val actionTitle: String,
) {
    val canContinueArea: Boolean get() = selectedArea != null
    val canCreateMission: Boolean get() = selectedArea != null && actionTitle.trim().length >= 3
}

/** WP-207 — pure pre-Mission draft reducer; no durable writes. */
object FirstWinDraftReducer {
    fun fresh(): FirstWinDraftState = FirstWinDraftState(
        step = FirstWinDraftStep.AREA,
        selectedArea = null,
        actionTitle = "",
    )

    fun selectArea(state: FirstWinDraftState, area: FirstWinArea): FirstWinDraftState =
        state.copy(selectedArea = area)

    fun continueFromArea(state: FirstWinDraftState): FirstWinDraftState =
        if (state.step == FirstWinDraftStep.AREA && state.selectedArea != null) {
            state.copy(step = FirstWinDraftStep.ACTION, actionTitle = "")
        } else {
            state
        }

    fun setActionTitle(state: FirstWinDraftState, title: String): FirstWinDraftState =
        if (state.step == FirstWinDraftStep.ACTION) state.copy(actionTitle = title) else state

    fun backToArea(state: FirstWinDraftState): FirstWinDraftState =
        state.copy(step = FirstWinDraftStep.AREA, actionTitle = "")
}
