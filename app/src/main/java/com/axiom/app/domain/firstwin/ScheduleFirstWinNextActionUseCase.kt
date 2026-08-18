package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.ScheduleBlock
import javax.inject.Inject

/**
 * Durable next-action scheduling using the existing Room-v18 ScheduleBlock plus a real Mission.
 * The schedule row and linked Mission are inserted before the session CAS, so a crash between
 * writes is repaired by idempotently re-entering this use case; existing rows are never replaced.
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

        val nextMission = missionStore.insertIfAbsent(
            mission.copy(
                id = FirstWinIds.nextMissionId(sessionId),
                status = "ACTIVE",
                dungeonId = null,
                actualHours = null,
                createdAt = nowMillis,
                completedAt = null,
                isInstantGate = false,
                scheduleBlockId = stored.id,
                qualityScore = 1.0,
                effectiveHours = 0.0,
            )
        )
        check(nextMission.scheduleBlockId == stored.id) {
            "Scheduled First-Win Mission is not linked to the durable schedule"
        }

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
