package com.axiom.app.presentation.firstwin

import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinPosition
import com.axiom.app.domain.model.Mission

data class FirstWinJourneySnapshot(
    val sessionId: String,
    val position: FirstWinPosition,
    val mission: Mission?,
)

interface FirstWinJourneyRuntime {
    suspend fun open(): FirstWinJourneySnapshot
    suspend fun createMission(
        sessionId: String,
        area: FirstWinArea,
        actionTitle: String,
    ): FirstWinJourneySnapshot
}
