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

/** WP-207 RED — pure pre-Mission draft reducer; no durable writes. */
object FirstWinDraftReducer {
    fun fresh(): FirstWinDraftState = TODO("WP-207 RED")
    fun selectArea(state: FirstWinDraftState, area: FirstWinArea): FirstWinDraftState = TODO("WP-207 RED")
    fun continueFromArea(state: FirstWinDraftState): FirstWinDraftState = TODO("WP-207 RED")
    fun setActionTitle(state: FirstWinDraftState, title: String): FirstWinDraftState = TODO("WP-207 RED")
    fun backToArea(state: FirstWinDraftState): FirstWinDraftState = TODO("WP-207 RED")
}
