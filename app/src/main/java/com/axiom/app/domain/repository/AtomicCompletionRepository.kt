package com.axiom.app.domain.repository

import com.axiom.app.domain.completion.AtomicCompletionOutcome
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.model.Skill

/**
 * WP-205 — the single transactional boundary for a mission completion.
 *
 * The implementation owns exactly one `database.withTransaction { }` and, inside it,
 * drives [com.axiom.app.domain.completion.AtomicCompletionWriteSequence] with real Room
 * DAO writes. Every core write (mission transition, hunter XP/level/rank, skill XP,
 * canonical Room streak, first-win rich receipt, first-win causal event) commits or rolls
 * back together — there is no repository-to-repository pseudo transaction and no partial
 * completion.
 *
 * The use case computes ALL awarded values first (pure compute + DataStore reads) and hands
 * this repository a fully-resolved [AtomicCompletionCommand]; the ONLY read-modify-write the
 * repository performs itself is the canonical Room streak (§8), which must be resolved inside
 * the transaction to stay atomic with the rest of the completion.
 */
interface AtomicCompletionRepository {
    suspend fun commit(command: AtomicCompletionCommand): AtomicCompletionResult
}

/**
 * Fully-resolved intent for one atomic completion. XP / level / rank / skill progression are
 * pre-computed by the use case (WP-205 changes transactionality, NOT reward economics), so the
 * entities here already carry their post-award values.
 *
 * @param idempotencyKey stable, deterministic key for this logical completion. First Mission =
 *   `first-win:first-mission:{hunterId}`; generic completions use a stable mission-specific key
 *   (`completion:mission:{missionId}`) so a retry of the SAME completion collides and re-awards
 *   nothing, while different missions stay independent.
 * @param isFirstWin true only for the First-Win completion — gates the rich receipt + causal event.
 * @param streakBaselineCurrent / streakBaselineLongest / streakLastActivityMillis DataStore streak
 *   snapshot read in PHASE A, used to bootstrap/reconcile the canonical Room streak (§9) without
 *   ever zeroing an existing user.
 * @param analyticsCollectionAllowed WP-206 Decision D — an IMMUTABLE consent snapshot read BEFORE
 *   the atomic transaction. When false (consent DECLINED), the first-win causal analytics row is
 *   NOT enqueued inside the transaction. This preserves the ACID boundary exactly (no DataStore
 *   read inside `withTransaction`); Room and DataStore consent are NOT claimed to be atomic — a
 *   post-commit purge (AnalyticsGateway) remains the backstop for a race between snapshot and commit.
 */
data class AtomicCompletionCommand(
    val idempotencyKey: String,
    val isFirstWin: Boolean,
    val completedMission: Mission,
    val updatedHunter: Hunter?,
    val updatedSkills: List<Skill>,
    val unlockedShadow: Shadow?,
    val updatedDungeon: Dungeon?,
    val streakBaselineCurrent: Int,
    val streakBaselineLongest: Int,
    val streakLastActivityMillis: Long,
    val nowMillis: Long,
    val hunterXpAwarded: Int,
    val skillXpAwarded: Long,
    val sessionId: String?,
    val analyticsCollectionAllowed: Boolean = true
)

/**
 * Result of an atomic completion. [resultingStreak] / [resultingLongestStreak] /
 * [lastActivityMillis] are the canonical Room-authoritative streak values AFTER the commit; the
 * use case mirrors them into DataStore post-commit (a mirror failure never invalidates the Room
 * truth — §10). On [AtomicCompletionOutcome.ALREADY_RECORDED] the streak fields echo the baseline
 * unchanged (no re-award).
 */
data class AtomicCompletionResult(
    val outcome: AtomicCompletionOutcome,
    val resultingStreak: Int,
    val resultingLongestStreak: Int,
    val lastActivityMillis: Long
)
