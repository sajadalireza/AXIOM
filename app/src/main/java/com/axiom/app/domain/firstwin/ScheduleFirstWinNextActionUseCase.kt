package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.ScheduleBlock

/** WP-207 RED — durable schedule + lifecycle handoff with crash-safe retry semantics. */
class ScheduleFirstWinNextActionUseCase(
    private val missionStore: FirstWinMissionStore,
    private val scheduleStore: FirstWinScheduleStore,
    private val factsReader: FirstWinFactsReader,
    private val lifecycleController: FirstWinLifecycleController,
) {
    suspend operator fun invoke(
        setupComplete: Boolean,
        sessionId: String,
        startTime: String,
        recurrence: String,
        nowMillis: Long,
    ): ScheduleBlock = TODO("WP-207 RED")
}
