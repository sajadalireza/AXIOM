package com.axiom.app.core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Game events that Xion should react to with a bubble message and/or mood change.
 *
 * Currently wired from AxiomViewModel:
 *  - MissionCompleted, RankUp, LevelUp  → completeMission()
 *  - StreakBroken, StreakMilestone      → checkOffDailyProtocol()
 *
 * Not yet wired (would need new tracking infrastructure, out of scope for this pass):
 *  - DailyLoginFirst — requires a "last app open" timestamp, which AxiomPreferences
 *    does not currently track. Leaving the case here so it's ready when that lands.
 */
sealed class XionEvent {
    data class MissionCompleted(val missionTitle: String, val rarity: String, val xpGained: Int) : XionEvent()
    data class RankUp(val oldRank: String, val newRank: String) : XionEvent()
    object StreakBroken : XionEvent()
    data class StreakMilestone(val days: Int) : XionEvent()
    object DailyLoginFirst : XionEvent()
    data class LevelUp(val newLevel: Int) : XionEvent()
}

@Singleton
class XionEventBus @Inject constructor() {
    // extraBufferCapacity so two events emitted in quick succession (e.g. MissionCompleted
    // immediately followed by RankUp) don't get dropped before a collector is ready.
    private val _events = MutableSharedFlow<XionEvent>(extraBufferCapacity = 8)
    val events = _events.asSharedFlow()

    suspend fun emit(event: XionEvent) = _events.emit(event)
}
