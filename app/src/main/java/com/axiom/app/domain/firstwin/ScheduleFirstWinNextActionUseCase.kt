package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.ScheduleBlock
import javax.inject.Inject

/**
 * Durable next-action scheduling using the existing Room-v18 recurring ScheduleBlock model.
 * The schedule row is inserted before the session CAS, so a crash in between is repaired by
 * idempotently re-entering this use case; the existing row is never replaced on retry.
 */
class ScheduleFirstWinNextActionUseCase @Inject constructor(
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
    ): ScheduleBlock {
        require(sessionId.isNotBlank()) { "sessionId must not be blank" }
        require(TIME_PATTERN.matches(startTime)) { "startTime must be HH:mm" }
        require(recurrence.isNotBlank()) { "recurrence must not be blank" }

        val facts = factsReader.read(setupComplete, sessionId)
        check(facts.sessionExists) { "First-Win session is missing" }
        check(facts.primaryMissionExists) { "First-Win mission is missing" }
        check(facts.completionReceiptExists) { "Completion receipt is required before scheduling" }
        check(
            facts.sessionStatus == FirstWinSessionStatus.REWARD_SEEN ||
                facts.sessionStatus == FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE
        ) { "First-Win session is not ready for scheduling: ${facts.sessionStatus}" }

        val mission = missionStore.getById(FirstWinIds.primaryMissionId(sessionId))
            ?: error("First-Win mission disappeared during scheduling")
        val candidate = ScheduleBlock(
            id = FirstWinIds.nextScheduleId(sessionId),
            trackId = mission.trackId,
            startTime = startTime,
            title = mission.title,
            actionDescription = mission.title,
            tag = "Foundation",
            recurrence = recurrence,
            isNonNegotiable = false,
        )
        val stored = scheduleStore.insertIfAbsent(candidate)

        when (lifecycleController.markHandoffWithSchedule(setupComplete, sessionId, nowMillis)) {
            FirstWinLifecycleOutcome.APPLIED,
            FirstWinLifecycleOutcome.ALREADY_APPLIED -> Unit
            FirstWinLifecycleOutcome.PRECONDITION_FAILED ->
                error("Schedule committed but durable First-Win prerequisites are missing")
            FirstWinLifecycleOutcome.STATE_CONFLICT ->
                error("Schedule committed but First-Win lifecycle state conflicted")
        }
        return stored
    }

    private companion object {
        val TIME_PATTERN = Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")
    }
}
