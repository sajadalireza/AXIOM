package com.axiom.app.presentation.firstwin

import com.axiom.app.domain.firstwin.CreateFirstWinMissionUseCase
import com.axiom.app.domain.firstwin.EnsureFirstWinHunterUseCase
import com.axiom.app.domain.firstwin.FirstWinArea
import com.axiom.app.domain.firstwin.FirstWinFactsReader
import com.axiom.app.domain.firstwin.FirstWinLifecycleController
import com.axiom.app.domain.firstwin.FirstWinMissionStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultFirstWinJourneyRuntime @Inject constructor(
    private val ensureHunter: EnsureFirstWinHunterUseCase,
    private val lifecycle: FirstWinLifecycleController,
    private val factsReader: FirstWinFactsReader,
    private val missionStore: FirstWinMissionStore,
    private val createMission: CreateFirstWinMissionUseCase,
) : FirstWinJourneyRuntime {
    override suspend fun open(): FirstWinJourneySnapshot = TODO("WP-207 RED")

    override suspend fun createMission(
        sessionId: String,
        area: FirstWinArea,
        actionTitle: String,
    ): FirstWinJourneySnapshot = TODO("WP-207 RED")
}
