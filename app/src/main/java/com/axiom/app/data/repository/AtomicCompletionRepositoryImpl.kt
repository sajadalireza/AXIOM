package com.axiom.app.data.repository

import androidx.room.withTransaction
import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.entity.CompletionReceiptEntity
import com.axiom.app.data.local.entity.DungeonEntity
import com.axiom.app.data.local.entity.EventQueueEntity
import com.axiom.app.data.local.entity.HunterEntity
import com.axiom.app.data.local.entity.MissionEntity
import com.axiom.app.data.local.entity.ShadowEntity
import com.axiom.app.data.local.entity.SkillEntity
import com.axiom.app.data.local.entity.StreakEntity
import com.axiom.app.domain.completion.AtomicCompletionOutcome
import com.axiom.app.domain.completion.AtomicCompletionWriteSequence
import com.axiom.app.domain.repository.AtomicCompletionCommand
import com.axiom.app.domain.repository.AtomicCompletionRepository
import com.axiom.app.domain.repository.AtomicCompletionResult
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WP-205 — production transactional boundary for mission completion.
 *
 * Wraps [AtomicCompletionWriteSequence] (the shared single-source-of-truth ordering object)
 * inside ONE [AxiomDatabase.withTransaction] over the real Room DAOs. This is the ONLY place
 * completion mutates Room, so completion is all-or-nothing: any throw rolls the whole set back
 * (partial commit == 0). The same sequence object is exercised by the JVM sqlite ACID proof,
 * so the shipped ordering/gating is exactly what the tests verify.
 */
@Singleton
class AtomicCompletionRepositoryImpl @Inject constructor(
    private val database: AxiomDatabase
) : AtomicCompletionRepository {

    override suspend fun commit(command: AtomicCompletionCommand): AtomicCompletionResult {
        // Streak values resolved INSIDE the txn (canonical Room read-modify-write, §8). Seeded
        // to the baseline so an ALREADY_RECORDED short-circuit returns the baseline unchanged.
        var resultingStreak = command.streakBaselineCurrent
        var resultingLongest = command.streakBaselineLongest
        var resultingLastActivityMillis = command.streakLastActivityMillis

        val completedMissionEntity = MissionEntity.fromDomain(command.completedMission)
        val hunterEntity = command.updatedHunter?.let { HunterEntity.fromDomain(it) }
        val skillEntities = command.updatedSkills.map { SkillEntity.fromDomain(it) }
        val shadowEntity = command.unlockedShadow?.let { ShadowEntity.fromDomain(it) }
        val dungeonEntity = command.updatedDungeon?.let { DungeonEntity.fromDomain(it) }

        val outcome = database.withTransaction {
            AtomicCompletionWriteSequence.run(
                isFirstWin = command.isFirstWin,
                receiptAlreadyExists = {
                    database.completionReceiptDao().getByKey(command.idempotencyKey) != null
                },
                writeMissionCompleted = {
                    database.missionDao().updateMission(completedMissionEntity)
                },
                writeSkillAwards = {
                    skillEntities.forEach { database.skillDao().updateSkill(it) }
                },
                writeHunterAward = {
                    hunterEntity?.let { database.hunterDao().updateProfile(it) }
                },
                upsertStreak = {
                    val computed = resolveRoomStreak(command)
                    resultingStreak = computed.currentStreak
                    resultingLongest = computed.longestStreak
                    resultingLastActivityMillis = command.nowMillis
                    database.streakDao().insertStreak(computed)
                },
                writeShadowIfUnlocked = {
                    shadowEntity?.let { database.shadowDao().insertShadow(it) }
                },
                writeDungeonIfPresent = {
                    dungeonEntity?.let { database.dungeonDao().updateDungeon(it) }
                },
                insertRichReceipt = {
                    database.completionReceiptDao().insert(
                        CompletionReceiptEntity(
                            receiptId = UUID.randomUUID().toString(),
                            idempotencyKey = command.idempotencyKey,
                            receiptType = RECEIPT_TYPE_RICH,
                            createdAt = command.nowMillis,
                            sessionId = command.sessionId,
                            missionId = command.completedMission.id,
                            hunterXpAwarded = command.hunterXpAwarded,
                            skillXpAwarded = command.skillXpAwarded,
                            resultingStreak = resultingStreak,
                            completedAt = command.completedMission.completedAt
                        )
                    )
                },
                insertPendingEvent = {
                    // WP-206 Decision D — skip the first-win causal analytics row when consent was
                    // DECLINED at snapshot time. This is the ONLY analytics-consent branch inside the
                    // transaction and it reads an IMMUTABLE command field (no DataStore read here), so
                    // the ACID boundary is unchanged. Core writes (mission/hunter/skill/streak/receipt)
                    // always commit; only the optional telemetry row is gated.
                    if (command.analyticsCollectionAllowed) {
                        database.eventQueueDao().insert(
                            EventQueueEntity(
                                eventId = UUID.randomUUID().toString(),
                                idempotencyKey = command.idempotencyKey,
                                eventType = EVENT_TYPE_FIRST_WIN,
                                payload = "{\"missionId\":\"${command.completedMission.id}\"," +
                                    "\"hunterXpAwarded\":${command.hunterXpAwarded}}",
                                status = "PENDING",
                                createdAt = command.nowMillis
                            )
                        )
                    }
                }
            )
        }

        return AtomicCompletionResult(
            outcome = outcome,
            resultingStreak = resultingStreak,
            resultingLongestStreak = resultingLongest,
            lastActivityMillis = resultingLastActivityMillis
        )
    }

    /**
     * Canonical Room streak transition. Reconciles the current Room row with the DataStore
     * baseline (§9 legacy bootstrap — take the higher streak / latest activity, never zero an
     * existing user), then applies the daily First-Win rule (same-day → unchanged, yesterday →
     * +1, gap/first → 1). Preserves the existing per-day increment economics; only the durable
     * authority moves from DataStore to Room. Reuses any existing row's id so LIMIT-1 stays a
     * true singleton.
     */
    private suspend fun resolveRoomStreak(command: AtomicCompletionCommand): StreakEntity {
        val existing = database.streakDao().getStreakOnce()
        val today = command.nowMillis.toLocalDate()

        val roomDate = existing?.lastActivityDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val baselineDate = command.streakLastActivityMillis.takeIf { it > 0L }?.toLocalDate()
        val lastActivityDate = listOfNotNull(roomDate, baselineDate).maxOrNull()

        val baseCurrent = maxOf(existing?.currentStreak ?: 0, command.streakBaselineCurrent)
        val baseLongest = maxOf(existing?.longestStreak ?: 0, command.streakBaselineLongest)

        val newStreak = when {
            lastActivityDate == null -> 1
            lastActivityDate == today -> baseCurrent.coerceAtLeast(1)
            lastActivityDate == today.minusDays(1) -> baseCurrent + 1
            else -> 1
        }
        val newLongest = maxOf(baseLongest, newStreak)

        return StreakEntity(
            id = existing?.id ?: STREAK_SINGLETON_ID,
            currentStreak = newStreak,
            longestStreak = newLongest,
            lastActivityDate = today.toString(),
            xpMultiplier = streakMultiplier(newStreak),
            streakLabel = existing?.streakLabel ?: ""
        )
    }

    private fun streakMultiplier(streak: Int): Float = when {
        streak < 7 -> 1.0f
        streak < 14 -> 1.15f
        streak < 30 -> 1.30f
        else -> 1.50f
    }

    private fun Long.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

    companion object {
        const val RECEIPT_TYPE_RICH = "WP205_RICH_COMPLETION_RECEIPT"
        const val EVENT_TYPE_FIRST_WIN = "FIRST_WIN_COMPLETION"
        const val STREAK_SINGLETON_ID = "primary"
    }
}
