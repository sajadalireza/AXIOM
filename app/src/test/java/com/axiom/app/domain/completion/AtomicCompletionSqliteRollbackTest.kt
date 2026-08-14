package com.axiom.app.domain.completion

import com.axiom.app.data.local.db.migrations.CompletionReceiptSchemaV18
import com.axiom.app.data.local.db.migrations.FirstWinSchemaV17
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager

/**
 * WP-205 RED — atomic-completion ATOMICITY proof on a real SQLite engine.
 *
 * Drives [AtomicCompletionWriteSequence.run] inside a JDBC transaction with JDBC-backed
 * lambdas over the shipped receipt/event/streak DDL ([FirstWinSchemaV17]/[CompletionReceiptSchemaV18])
 * plus a minimal missions/hunter fixture. Proves all-or-nothing: a throw mid-sequence rolls
 * back EVERY prior write (no partial completion), the success path writes the full rich set,
 * and a replay with an existing receipt re-awards nothing.
 *
 * RED until GREEN: run() currently throws NotImplementedError, so every case fails.
 *
 * §20 limitation carried forward: this exercises SQLite transaction semantics via sqlite-jdbc,
 * NOT Room's real withTransaction on a device (no connected-device CI job). The production
 * repository wraps this SAME sequence object inside database.withTransaction.
 */
class AtomicCompletionSqliteRollbackTest {

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
        }
    }

    private fun rowCount(c: Connection, table: String, where: String = "1=1"): Int =
        c.createStatement().use { s ->
            s.executeQuery("SELECT COUNT(*) FROM `$table` WHERE $where").use { rs -> rs.next(); rs.getInt(1) }
        }

    private fun missionStatus(c: Connection): String =
        c.createStatement().use { s ->
            s.executeQuery("SELECT status FROM `missions` WHERE id='m1'").use { rs -> rs.next(); rs.getString(1) }
        }

    /** DAO-equivalent JDBC write lambdas mirroring the production DAO calls. */
    private fun writeMission(c: Connection) =
        c.createStatement().use { it.executeUpdate("UPDATE `missions` SET status='COMPLETED', completedAt=123 WHERE id='m1'") }

    private fun writeHunter(c: Connection) =
        c.createStatement().use { it.executeUpdate("UPDATE `hunter_profile` SET totalXP=50, currentXP=50 WHERE id='h1'") }

    private fun upsertStreak(c: Connection) =
        c.createStatement().use {
            it.executeUpdate("INSERT OR REPLACE INTO `streak` VALUES ('s1', 1, 1, '2026-08-14', 1.0, 'Kindling')")
        }

    private fun insertReceipt(c: Connection, key: String) =
        c.prepareStatement(
            "INSERT INTO `completion_receipt` " +
                "(`receiptId`,`idempotencyKey`,`receiptType`,`createdAt`,`sessionId`,`missionId`," +
                "`hunterXpAwarded`,`skillXpAwarded`,`resultingStreak`,`completedAt`) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)"
        ).use { ps ->
            ps.setString(1, "r-$key"); ps.setString(2, key)
            ps.setString(3, "WP205_RICH_COMPLETION_RECEIPT"); ps.setLong(4, 111L)
            ps.setNull(5, java.sql.Types.VARCHAR)      // sessionId NULL this packet
            ps.setString(6, "m1"); ps.setInt(7, 50); ps.setLong(8, 20L)
            ps.setInt(9, 1); ps.setLong(10, 123L)
            ps.executeUpdate()
        }

    private fun insertEvent(c: Connection, key: String) =
        c.prepareStatement(
            "INSERT INTO `event_queue` " +
                "(`eventId`,`idempotencyKey`,`eventType`,`payload`,`status`,`createdAt`) VALUES (?,?,?,?,?,?)"
        ).use { ps ->
            ps.setString(1, "e-$key"); ps.setString(2, key); ps.setString(3, "FIRST_WIN")
            ps.setString(4, "{}"); ps.setString(5, "PENDING"); ps.setLong(6, 112L)
            ps.executeUpdate()
        }

    private val KEY = "first-win:first-mission:h1"

    @Test
    fun successPath_writesFullRichSetAtomically() = runBlocking {
        openDb().use { c ->
            buildSchema(c); seed(c)
            c.autoCommit = false
            val outcome = try {
                val o = AtomicCompletionWriteSequence.run(
                    isFirstWin = true,
                    receiptAlreadyExists = { rowCount(c, "completion_receipt", "idempotencyKey='$KEY'") > 0 },
                    writeMissionCompleted = { writeMission(c) },
                    writeSkillAwards = { /* no skill table in this fixture */ },
                    writeHunterAward = { writeHunter(c) },
                    upsertStreak = { upsertStreak(c) },
                    writeShadowIfUnlocked = { },
                    writeDungeonIfPresent = { },
                    insertRichReceipt = { insertReceipt(c, KEY) },
                    insertPendingEvent = { insertEvent(c, KEY) },
                )
                c.commit(); o
            } catch (e: Throwable) { c.rollback(); throw e }

            assertEquals(AtomicCompletionOutcome.AWARDED, outcome)
            assertEquals("COMPLETED", missionStatus(c))
            assertEquals(1, rowCount(c, "completion_receipt"))
            assertEquals(1, rowCount(c, "event_queue"))
            assertEquals("streak upserted (Room authority)", 1, rowCount(c, "streak"))
        }
    }

    @Test
    fun failureMidSequence_rollsBackEveryPriorWrite() = runBlocking {
        openDb().use { c ->
            buildSchema(c); seed(c)
            c.autoCommit = false
            try {
                AtomicCompletionWriteSequence.run(
                    isFirstWin = true,
                    receiptAlreadyExists = { false },
                    writeMissionCompleted = { writeMission(c) },
                    writeSkillAwards = { },
                    writeHunterAward = { writeHunter(c) },
                    upsertStreak = { upsertStreak(c) },
                    writeShadowIfUnlocked = { },
                    writeDungeonIfPresent = { },
                    insertRichReceipt = { insertReceipt(c, KEY) },
                    insertPendingEvent = { throw RuntimeException("boom — event write failed") },
                )
                c.commit()
                fail("sequence should have thrown before commit")
            } catch (e: RuntimeException) {
                c.rollback()
            }

            // Fresh connection would show the same, but same conn post-rollback proves it:
            assertEquals("mission NOT completed after rollback", "PENDING", missionStatus(c))
            assertEquals("no receipt after rollback", 0, rowCount(c, "completion_receipt"))
            assertEquals("no event after rollback", 0, rowCount(c, "event_queue"))
            assertEquals("no streak after rollback", 0, rowCount(c, "streak"))
            assertEquals("hunter XP untouched after rollback", 0,
                c.createStatement().use { s ->
                    s.executeQuery("SELECT totalXP FROM hunter_profile WHERE id='h1'").use { rs -> rs.next(); rs.getInt(1) }
                })
        }
    }

    @Test
    fun replayWithExistingReceipt_reAwardsNothing() = runBlocking {
        openDb().use { c ->
            buildSchema(c); seed(c)
            // Pre-existing first-win receipt (prior committed completion).
            insertReceipt(c, KEY)
            c.autoCommit = false
            val outcome = AtomicCompletionWriteSequence.run(
                isFirstWin = true,
                receiptAlreadyExists = { rowCount(c, "completion_receipt", "idempotencyKey='$KEY'") > 0 },
                writeMissionCompleted = { writeMission(c) },
                writeSkillAwards = { },
                writeHunterAward = { writeHunter(c) },
                upsertStreak = { upsertStreak(c) },
                writeShadowIfUnlocked = { },
                writeDungeonIfPresent = { },
                insertRichReceipt = { insertReceipt(c, KEY) },
                insertPendingEvent = { insertEvent(c, KEY) },
            )
            c.commit()

            assertEquals(AtomicCompletionOutcome.ALREADY_RECORDED, outcome)
            assertEquals("still exactly one receipt", 1, rowCount(c, "completion_receipt"))
            assertEquals("no award: hunter untouched", 0,
                c.createStatement().use { s ->
                    s.executeQuery("SELECT totalXP FROM hunter_profile WHERE id='h1'").use { rs -> rs.next(); rs.getInt(1) }
                })
            assertFalse("no event enqueued on replay", rowCount(c, "event_queue") > 0)
        }
    }
}
