package com.axiom.app.presentation.firstwin

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.firstwin.CreateFirstWinMissionUseCase
import com.axiom.app.domain.firstwin.EnsureFirstWinHunterUseCase
import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinFactsReader
import com.axiom.app.domain.firstwin.FirstWinIds
import com.axiom.app.domain.firstwin.FirstWinLifecycleController
import com.axiom.app.domain.firstwin.FirstWinMissionStore
import com.axiom.app.domain.firstwin.FirstWinPositionReducer
import com.axiom.app.domain.usecase.CompleteMissionUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class DefaultFirstWinJourneyRuntime @Inject constructor(
    private val preferences: AxiomPreferences,
    private val ensureHunter: EnsureFirstWinHunterUseCase,
    private val lifecycle: FirstWinLifecycleController,
    private val factsReader: FirstWinFactsReader,
    private val missionStore: FirstWinMissionStore,
    private val createFirstWinMission: CreateFirstWinMissionUseCase,
    private val completeMissionUseCase: CompleteMissionUseCase,
) : FirstWinJourneyRuntime {
    override suspend fun open(): FirstWinJourneySnapshot {
        val hunter = ensureHunter()
        val sessionId = lifecycle.ensureSession(hunter.id, System.currentTimeMillis())
        return readSnapshot(sessionId)
    }

    override suspend fun createMission(
        sessionId: String,
        area: FirstWinArea,
        actionTitle: String,
    ): FirstWinJourneySnapshot {
        createFirstWinMission(sessionId, area, actionTitle)
        return readSnapshot(sessionId)
    }

    override suspend fun completeMission(
        sessionId: String,
        missionId: String,
    ): FirstWinJourneySnapshot = TODO("WP-207 RED")

    private suspend fun readSnapshot(sessionId: String): FirstWinJourneySnapshot {
        val facts = factsReader.read(
            setupComplete = preferences.setupCompleteFlow.first(),
            sessionId = sessionId,
        )
        val mission = if (facts.primaryMissionExists) {
            missionStore.getById(FirstWinIds.primaryMissionId(sessionId))
        } else {
            null
        }
        return FirstWinJourneySnapshot(
            sessionId = sessionId,
            position = FirstWinPositionReducer.reduce(facts),
            mission = mission,
        )
    }
}
