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
    suspend fun completeMission(
        sessionId: String,
        missionId: String,
    ): FirstWinJourneySnapshot
    suspend fun acknowledgeReward(sessionId: String): FirstWinJourneySnapshot
    suspend fun finishForNow(sessionId: String): FirstWinJourneySnapshot
    suspend fun completeHandoff(sessionId: String): FirstWinJourneySnapshot
}
