package com.axiom.app.presentation.ceremony

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════════════
// CEREMONY EVENTS — sealed hierarchy for all ceremony types
// ═══════════════════════════════════════════════════════════════

sealed class CeremonyEvent {

    /** Hunter reaches a new level. Triggers LevelUpCeremony. */
    data class LevelUp(
        val newLevel: Int,
        val hunterName: String
    ) : CeremonyEvent()

    /** Hunter's rank tier changes (E→D, D→C, …, A→S). Triggers RankUpCeremony. */
    data class RankUp(
        val oldRank: String,
        val newRank: String
    ) : CeremonyEvent()

    /** A skill crosses Rank B and joins the Shadow Army. Triggers ShadowAcquisitionCeremony. */
    data class ShadowAcquired(
        val skillName: String,
        val rankLabel: String
    ) : CeremonyEvent()

    /** The final stage of a Campaign is cleared. Triggers CheckpointClearedCeremony. */
    data class CheckpointCleared(
        val campaignName: String,
        val bonusXP: Int
    ) : CeremonyEvent()

    /** Triggers BossDefeatedCeremony with dark theme and rumbling particles. */
    data class BossDefeated(
        val bossName: String,
        val bonusXP: Long
    ) : CeremonyEvent()

    /**
     * Streak was broken after being > 3 days.
     * Shorter streaks get a system feed message only — no ceremony.
     */
    data class StreakBroken(val lostStreak: Int) : CeremonyEvent()

    data class MissionComplete(
        val missionTitle: String,
        val rarity      : String,
        val xpGained    : Int,
        val rarityColor : Long
    ) : CeremonyEvent()

    data class FocusComplete(val lpGained: Int) : CeremonyEvent()

    /**
     * Streak crosses a milestone (7 / 14 / 30 days).
     * Triggers StreakMilestoneOverlay.
     */
    data class StreakMilestone(
        val streak: Int,
        val label: String          // "ACTIVE" | "REINFORCED" | "DOMINANT"
    ) : CeremonyEvent()

    data class StreakShieldUsed(val savedStreak: Int, val remainingShields: Int) : CeremonyEvent()

    data class WeeklyReviewComplete(val xpGained: Int) : CeremonyEvent()

    data class IronRuleBreached(val ruleText: String) : CeremonyEvent()

    /**
     * Variable-reward "System Anomaly" bonus rolled on mission completion.
     * tier is "MINOR" | "MAJOR" | "CRITICAL". Triggers a SystemAnomalyCeremony.
     */
    data class SystemAnomaly(val tier: String, val bonusXP: Int) : CeremonyEvent()
}

// ═══════════════════════════════════════════════════════════════
// CEREMONY ENGINE
//
// Manages a queue of CeremonyEvents so that if multiple ceremonies
// fire in rapid succession (e.g. mission complete → level up →
// rank up → shadow), they are shown one-by-one in order without
// any being dropped.
//
// Inject this singleton into ViewModels that trigger ceremonies,
// and into CeremonyHost (via CeremonyViewModel) to observe events.
// ═══════════════════════════════════════════════════════════════

@Singleton
class CeremonyEngine @Inject constructor() {

    private val _ceremonyEvent = MutableStateFlow<CeremonyEvent?>(null)

    /** Observed by CeremonyHost to render the active ceremony overlay. */
    val ceremonyEvent: StateFlow<CeremonyEvent?> = _ceremonyEvent.asStateFlow()

    /** Public Channel to queue incoming events. */
    val eventChannel = Channel<CeremonyEvent>(Channel.UNLIMITED)

    /** Channel to coordinate dismiss triggers. */
    val dismissChannel = Channel<Unit>(Channel.CONFLATED)

    /**
     * Schedules [event] for display via the unlimited event channel.
     */
    fun emit(event: CeremonyEvent) {
        eventChannel.trySend(event)
    }

    /**
     * Dismisses the current ceremony by signaling on the dismiss channel.
     */
    fun dismiss() {
        dismissChannel.trySend(Unit)
    }

    /**
     * Helper to set the active event programmatically inside the synchronization loop.
     */
    fun setActiveEvent(event: CeremonyEvent?) {
        _ceremonyEvent.value = event
    }
}
