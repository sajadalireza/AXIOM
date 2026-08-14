package com.axiom.app.domain.completion

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.entity.StreakEntity
import com.axiom.app.data.repository.AtomicCompletionRepositoryImpl
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.repository.AtomicCompletionCommand
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.ZoneId

/**
 * WP-205 §18 — AUTHORED instrumented proof of the REAL Room `database.withTransaction`
 * boundary + the canonical streak reconciliation ([AtomicCompletionRepositoryImpl.resolveRoomStreak],
 * §8/§9). Exercises [AtomicCompletionRepositoryImpl] over an in-memory [AxiomDatabase] so the
 * production transaction owner (not a JDBC stand-in) is what runs.
 *
 * §18 HONESTY NOTE: AUTHORED YES / EXECUTED NO. There is no connected-device / emulator CI job
 * in this repo, so this instrumented test has NOT been executed. It is committed so the Room-level
 * behavior is pinned the moment a device job exists. Engine-level rollback + UNIQUE-dedupe are
 * separately PROVEN (EXECUTED YES) on real SQLite by AtomicCompletionSqliteRollbackTest /
 * AtomicCompletionRegressionTest. This file must NEVER be reported as PASS until run on a device.
 */
@RunWith(AndroidJUnit4::class)
class AtomicCompletionRoomInstrumentedTest {

    private lateinit var db: AxiomDatabase
    private lateinit var repo: AtomicCompletionRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AxiomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = AtomicCompletionRepositoryImpl(db)
    }

    @After
    fun tearDown() = db.close()

    private fun millisFor(date: LocalDate): Long =
        date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private fun hunter() = Hunter(
        id = "h1", name = "Test", level = 1, rankLabel = "E-Rank",
        totalXP = 0L, currentXP = 0, xpToNextLevel = 100, progressPercent = 0f,
        rankColor = 0L, rankGlyph = ""
    )

    private fun mission(id: String) = Mission(
        id = id, title = "t", track = "focus", rarity = "COMMON", skillId = "s1", skillName = "S",
        xpReward = 10, powerScore = 1f, status = "COMPLETED", dungeonId = null,
        estimatedHours = 0.5f, actualHours = 0.5f, createdAt = 0L, completedAt = 123L, rarityColor = 0L
    )

    private fun command(
        key: String, missionId: String, isFirstWin: Boolean, now: Long,
        baselineCurrent: Int = 0, baselineLongest: Int = 0, baselineLast: Long = 0L, xp: Int = 50
    ) = AtomicCompletionCommand(
        idempotencyKey = key, isFirstWin = isFirstWin, completedMission = mission(missionId),
        updatedHunter = hunter(), updatedSkills = emptyList(), unlockedShadow = null, updatedDungeon = null,
        streakBaselineCurrent = baselineCurrent, streakBaselineLongest = baselineLongest,
        streakLastActivityMillis = baselineLast, nowMillis = now,
        hunterXpAwarded = xp, skillXpAwarded = 0L, sessionId = null
    )

    // §8/§9 — no prior activity → streak initializes to 1.
    @Test
    fun streak_initializesToOne_whenNoPriorActivity() = runBlocking {
        val today = LocalDate.of(2026, 8, 14)
        val r = repo.commit(command("completion:mission:m1", "m1", isFirstWin = false, now = millisFor(today)))
        assertEquals(AtomicCompletionOutcome.AWARDED, r.outcome)
        assertEquals(1, r.resultingStreak)
        assertEquals(1, db.streakDao().getStreak().first()!!.currentStreak)
    }

    // §8 — completing the day after the last activity increments the streak by one.
    @Test
    fun streak_incrementsByOne_whenLastActivityWasYesterday() = runBlocking {
        val today = LocalDate.of(2026, 8, 14)
        db.streakDao().insertStreak(
            StreakEntity(id = "primary", currentStreak = 4, longestStreak = 4,
                lastActivityDate = today.minusDays(1).toString(), xpMultiplier = 1.0f, streakLabel = "")
        )
        val r = repo.commit(command("completion:mission:m1", "m1", isFirstWin = false, now = millisFor(today)))
        assertEquals(5, r.resultingStreak)
        assertEquals(5, r.resultingLongestStreak)
    }

    // §8 — a second completion on the SAME day does not increment the streak again.
    @Test
    fun streak_unchanged_whenAlreadyCompletedToday() = runBlocking {
        val today = LocalDate.of(2026, 8, 14)
        db.streakDao().insertStreak(
            StreakEntity(id = "primary", currentStreak = 7, longestStreak = 9,
                lastActivityDate = today.toString(), xpMultiplier = 1.15f, streakLabel = "")
        )
        val r = repo.commit(command("completion:mission:m2", "m2", isFirstWin = false, now = millisFor(today)))
        assertEquals(7, r.resultingStreak)
        assertEquals("longest preserved", 9, r.resultingLongestStreak)
    }

    // §8 — a gap of more than one day resets the streak to 1.
    @Test
    fun streak_resetsToOne_afterGap() = runBlocking {
        val today = LocalDate.of(2026, 8, 14)
        db.streakDao().insertStreak(
            StreakEntity(id = "primary", currentStreak = 12, longestStreak = 12,
                lastActivityDate = today.minusDays(5).toString(), xpMultiplier = 1.30f, streakLabel = "")
        )
        val r = repo.commit(command("completion:mission:m1", "m1", isFirstWin = false, now = millisFor(today)))
        assertEquals(1, r.resultingStreak)
        assertEquals("longest never regresses", 12, r.resultingLongestStreak)
    }

    // §9 — Room streak absent but DataStore baseline valid → bootstrap without zeroing the user.
    @Test
    fun streak_bootstrapsFromDataStoreBaseline_whenRoomEmpty() = runBlocking {
        val today = LocalDate.of(2026, 8, 14)
        val r = repo.commit(
            command(
                "completion:mission:m1", "m1", isFirstWin = false, now = millisFor(today),
                baselineCurrent = 6, baselineLongest = 8, baselineLast = millisFor(today.minusDays(1))
            )
        )
        assertEquals("yesterday baseline + 1", 7, r.resultingStreak)
        assertEquals(8, r.resultingLongestStreak)
    }

    // §6/§7 — first-win completion writes exactly one rich receipt + one event with exact XP/streak.
    @Test
    fun firstWin_writesRichReceiptAndEvent_exactlyOnce() = runBlocking {
        val today = LocalDate.of(2026, 8, 14)
        val key = "first-win:first-mission:h1"
        val r = repo.commit(command(key, "m1", isFirstWin = true, now = millisFor(today), xp = 77))
        assertEquals(AtomicCompletionOutcome.AWARDED, r.outcome)
        val receipt = db.completionReceiptDao().getByKey(key)
        assertNotNull(receipt)
        assertEquals(77, receipt!!.hunterXpAwarded)
        assertEquals(r.resultingStreak, receipt.resultingStreak)
        assertEquals("m1", receipt.missionId)
        assertEquals(1, db.eventQueueDao().getPending().size)

        // Replay is idempotent: no re-award, still one receipt/event.
        val replay = repo.commit(command(key, "m1", isFirstWin = true, now = millisFor(today), xp = 77))
        assertEquals(AtomicCompletionOutcome.ALREADY_RECORDED, replay.outcome)
        assertEquals(1, db.completionReceiptDao().getAll().size)
        assertEquals(1, db.eventQueueDao().getPending().size)
    }
}
