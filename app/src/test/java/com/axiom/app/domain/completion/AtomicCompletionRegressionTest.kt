package com.axiom.app.domain.completion

import com.axiom.app.data.local.db.migrations.CompletionReceiptSchemaV18
import com.axiom.app.data.local.db.migrations.FirstWinSchemaV17
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager

/**
 * WP-205 GREEN regression battery (§16 A–R) — real-engine coverage of the shipped
 * [AtomicCompletionWriteSequence] driven over the shipped receipt/event/streak DDL on a real
 * SQLite engine via sqlite-jdbc. This is DISTINCT from [AtomicCompletionSqliteRollbackTest]
 * (which pins success / rollback / replay); this file adds the remaining §16 rows that are
 * expressible at the storage + orchestration layer:
 *
 *  A  normalCompletion_nonFirstWin_writesEconomyButNoReceiptOrEvent
 *  B  firstMissionCompletion_writesFullRichSet
 *  C  sameCommandRetry_firstWin_reAwardsNothing
 *  D  duplicateReceiptInsert_isRejectedByUniqueIndex (concurrent-duplicate DB guard)
 *  E  failureMidSequence covered in sibling rollback test (kept distinct — §17)
 *  G  independentMissions_haveIndependentKeys_bothAward
 *  H  receipt_persistsExactXpAndStreak (§7 result contract)
 *  N  noDuplicateReceiptEventOrXp_onReplay
 *
 * The Room-specific streak reconciliation rule (§8/§9 resolveRoomStreak) and the true
 * `database.withTransaction` boundary are exercised by the authored instrumented test
 * (AUTHORED YES / EXECUTED NO — see AtomicCompletionRoomInstrumentedTest).
 */
class AtomicCompletionRegressionTest {

    private fun openDb(): Connection = DriverManager.getConnection("jdbc:sqlite::memory:")

    private fun buildSchema(c: Connection) {
        c.createStatement().use { s ->
            s.executeUpdate(
                "CREATE TABLE `hunter_profile` (`id` TEXT NOT NULL, `totalXP` INTEGER NOT NULL, " +
                    "`currentXP` INTEGER NOT NULL, PRIMARY KEY(`id`))"
            )
            s.executeUpdate(
                "CREATE TABLE `streak` (`id` TEXT NOT NULL, `currentStreak` INTEGER NOT NULL, " +
                    "`longestStreak` INTEGER NOT NULL, `lastActivityDate` TEXT, " +
                    "`xpMultiplier` REAL NOT NULL, `streakLabel` TEXT NOT NULL, PRIMARY KEY(`id`))"
            )
            s.executeUpdate(
                "CREATE TABLE `missions` (`id` TEXT NOT NULL, `status` TEXT NOT NULL, " +
                    "`completedAt` INTEGER, PRIMARY KEY(`id`))"
            )
            FirstWinSchemaV17.STATEMENTS.forEach(s::executeUpdate)
            CompletionReceiptSchemaV18.STATEMENTS.forEach(s::executeUpdate)
        }
    }

    private fun seed(c: Connection) {
        c.createStatement().use { s ->
            s.executeUpdate("INSERT INTO `hunter_profile` VALUES ('h1', 0, 0)")
            s.executeUpdate("INSERT INTO `missions` VALUES ('m1', 'PENDING', NULL)")
            s.executeUpdate("INSERT INTO `missions` VALUES ('m2', 'PENDING', NULL)")
        }
    }

    private fun rowCount(c: Connection, table: String, where: String = "1=1"): Int =
        c.createStatement().use { s ->
            s.executeQuery("SELECT COUNT(*) FROM `$table` WHERE $where").use { rs -> rs.next(); rs.getInt(1) }
        }

    private fun missionStatus(c: Connection, id: String): String =
        c.createStatement().use { s ->
            s.executeQuery("SELECT status FROM `missions` WHERE id='$id'").use { rs -> rs.next(); rs.getString(1) }
        }

    private fun writeMission(c: Connection, id: String) =
        c.createStatement().use { it.executeUpdate("UPDATE `missions` SET status='COMPLETED', completedAt=123 WHERE id='$id'") }

    private fun writeHunter(c: Connection, xp: Int) =
        c.createStatement().use { it.executeUpdate("UPDATE `hunter_profile` SET totalXP=$xp, currentXP=$xp WHERE id='h1'") }

    private fun upsertStreak(c: Connection, current: Int) =
        c.createStatement().use {
            it.executeUpdate("INSERT OR REPLACE INTO `streak` VALUES ('primary', $current, $current, '2026-08-14', 1.0, '')")
        }

    private fun insertReceipt(c: Connection, key: String, xp: Int, streak: Int, mission: String) =
        c.prepareStatement(
            "INSERT INTO `completion_receipt` " +
                "(`receiptId`,`idempotencyKey`,`receiptType`,`createdAt`,`sessionId`,`missionId`," +
                "`hunterXpAwarded`,`skillXpAwarded`,`resultingStreak`,`completedAt`) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)"
        ).use { ps ->
            ps.setString(1, "r-$key"); ps.setString(2, key)
            ps.setString(3, "WP205_RICH_COMPLETION_RECEIPT"); ps.setLong(4, 111L)
            ps.setNull(5, java.sql.Types.VARCHAR)
            ps.setString(6, mission); ps.setInt(7, xp); ps.setLong(8, 20L)
            ps.setInt(9, streak); ps.setLong(10, 123L)
            ps.executeUpdate()
        }

    private fun insertEvent(c: Connection, key: String) =
        c.prepareStatement(
            "INSERT INTO `event_queue` " +
                "(`eventId`,`idempotencyKey`,`eventType`,`payload`,`status`,`createdAt`) VALUES (?,?,?,?,?,?)"
        ).use { ps ->
            ps.setString(1, "e-$key"); ps.setString(2, key); ps.setString(3, "FIRST_WIN_COMPLETION")
            ps.setString(4, "{}"); ps.setString(5, "PENDING"); ps.setLong(6, 112L)
            ps.executeUpdate()
        }

    /** Drives the shipped sequence inside a JDBC txn for one logical completion. */
    private fun runCompletion(
        c: Connection,
        key: String,
        missionId: String,
        isFirstWin: Boolean,
        hunterXp: Int,
        streak: Int,
    ): AtomicCompletionOutcome = runBlocking {
        c.autoCommit = false
        try {
            val o = AtomicCompletionWriteSequence.run(
                isFirstWin = isFirstWin,
                receiptAlreadyExists = { rowCount(c, "completion_receipt", "idempotencyKey='$key'") > 0 },
                writeMissionCompleted = { writeMission(c, missionId) },
                writeSkillAwards = { },
                writeHunterAward = { writeHunter(c, hunterXp) },
                upsertStreak = { upsertStreak(c, streak) },
                writeShadowIfUnlocked = { },
                writeDungeonIfPresent = { },
                insertRichReceipt = { insertReceipt(c, key, hunterXp, streak, missionId) },
                insertPendingEvent = { insertEvent(c, key) },
            )
            c.commit(); o
        } catch (e: Throwable) { c.rollback(); throw e }
    }

    // A — ordinary (non-first-win) completion writes the economy but no first-win receipt/event.
    @Test
    fun normalCompletion_nonFirstWin_writesEconomyButNoReceiptOrEvent() {
        openDb().use { c ->
            buildSchema(c); seed(c)
            val outcome = runCompletion(c, "completion:mission:m1", "m1", isFirstWin = false, hunterXp = 30, streak = 1)
            assertEquals(AtomicCompletionOutcome.AWARDED, outcome)
            assertEquals("COMPLETED", missionStatus(c, "m1"))
            assertEquals("streak written for every completion", 1, rowCount(c, "streak"))
            assertEquals("no first-win receipt for ordinary completion", 0, rowCount(c, "completion_receipt"))
            assertEquals("no first-win event for ordinary completion", 0, rowCount(c, "event_queue"))
        }
    }

    // B — first-mission completion writes the full rich set.
    @Test
    fun firstMissionCompletion_writesFullRichSet() {
        openDb().use { c ->
            buildSchema(c); seed(c)
            val key = "first-win:first-mission:h1"
            val outcome = runCompletion(c, key, "m1", isFirstWin = true, hunterXp = 50, streak = 1)
            assertEquals(AtomicCompletionOutcome.AWARDED, outcome)
            assertEquals(1, rowCount(c, "completion_receipt"))
            assertEquals(1, rowCount(c, "event_queue"))
            assertEquals(1, rowCount(c, "streak"))
        }
    }

    // C + N — same-command retry re-awards nothing and leaves exactly one receipt/event.
    @Test
    fun sameCommandRetry_firstWin_reAwardsNothing() {
        openDb().use { c ->
            buildSchema(c); seed(c)
            val key = "first-win:first-mission:h1"
            runCompletion(c, key, "m1", isFirstWin = true, hunterXp = 50, streak = 1)
            val second = runCompletion(c, key, "m1", isFirstWin = true, hunterXp = 50, streak = 1)
            assertEquals(AtomicCompletionOutcome.ALREADY_RECORDED, second)
            assertEquals("still exactly one receipt", 1, rowCount(c, "completion_receipt"))
            assertEquals("still exactly one event", 1, rowCount(c, "event_queue"))
        }
    }

    // D — the UNIQUE idempotencyKey index is the DB-level guard for a concurrent duplicate that
    // slips past the app-level probe (both racers see no receipt, both try to insert).
    @Test
    fun duplicateReceiptInsert_isRejectedByUniqueIndex() {
        openDb().use { c ->
            buildSchema(c); seed(c)
            val key = "first-win:first-mission:h1"
            insertReceipt(c, key, 50, 1, "m1")
            var threw = false
            try { insertReceipt(c, key, 50, 1, "m1") } catch (e: Exception) { threw = true }
            assertTrue("duplicate idempotencyKey must be rejected by the UNIQUE index", threw)
            assertEquals(1, rowCount(c, "completion_receipt"))
        }
    }

    // G — different missions use independent keys, so both award independently.
    @Test
    fun independentMissions_haveIndependentKeys_bothAward() {
        openDb().use { c ->
            buildSchema(c); seed(c)
            val a = runCompletion(c, "completion:mission:m1", "m1", isFirstWin = false, hunterXp = 30, streak = 1)
            val b = runCompletion(c, "completion:mission:m2", "m2", isFirstWin = false, hunterXp = 30, streak = 1)
            assertEquals(AtomicCompletionOutcome.AWARDED, a)
            assertEquals(AtomicCompletionOutcome.AWARDED, b)
            assertEquals("COMPLETED", missionStatus(c, "m1"))
            assertEquals("COMPLETED", missionStatus(c, "m2"))
        }
    }

    // H — the rich receipt persists the exact XP and resulting streak (§7 result contract).
    @Test
    fun receipt_persistsExactXpAndStreak() {
        openDb().use { c ->
            buildSchema(c); seed(c)
            val key = "first-win:first-mission:h1"
            runCompletion(c, key, "m1", isFirstWin = true, hunterXp = 77, streak = 3)
            c.createStatement().use { s ->
                s.executeQuery(
                    "SELECT hunterXpAwarded, resultingStreak, missionId, receiptType, sessionId " +
                        "FROM completion_receipt WHERE idempotencyKey='$key'"
                ).use { rs ->
                    rs.next()
                    assertEquals(77, rs.getInt("hunterXpAwarded"))
                    assertEquals(3, rs.getInt("resultingStreak"))
                    assertEquals("m1", rs.getString("missionId"))
                    assertEquals("WP205_RICH_COMPLETION_RECEIPT", rs.getString("receiptType"))
                    rs.getString("sessionId"); assertTrue("sessionId NULL allowed this packet", rs.wasNull())
                }
            }
        }
    }
}
