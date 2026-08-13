package com.axiom.app.db

import com.axiom.app.data.local.db.migrations.FirstWinSchemaV17
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * WP-204 — JVM proof of the v16 → v17 migration on a real SQLite engine
 * (org.xerial:sqlite-jdbc), running inside the CI `testDebugUnitTest` channel.
 *
 * Instrumented `MigrationTest` (androidTest) is authored but not executed here —
 * CI has no connected-device job and this environment cannot run an emulator.
 * These JVM tests exercise the EXACT production DDL via [FirstWinSchemaV17.STATEMENTS]
 * (the same list [com.axiom.app.data.local.db.migrations.MIGRATION_16_17] replays),
 * so they prove the three MUST-ACCEPTANCE behaviours on the authoritative channel:
 *   1. existing v16 data is preserved across the migration,
 *   2. the migration applies (creating the new tables), and
 *   3. the UNIQUE indexes are DB-enforced against duplicates.
 */
class MigrationV16V17SqliteTest {

    private fun openDb(): Connection =
        DriverManager.getConnection("jdbc:sqlite::memory:")

    /** Representative subset of the v16 schema, DDL copied verbatim from 16.json. */
    private fun createV16Subset(c: Connection) {
        c.createStatement().use { s ->
            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `hunter_profile` (`id` TEXT NOT NULL, " +
                    "`name` TEXT NOT NULL, `level` INTEGER NOT NULL, `rankLabel` TEXT NOT NULL, " +
                    "`totalXP` INTEGER NOT NULL, `currentXP` INTEGER NOT NULL, " +
                    "`xpToNextLevel` INTEGER NOT NULL, `progressPercent` REAL NOT NULL, " +
                    "`rankColor` INTEGER NOT NULL, `rankGlyph` TEXT NOT NULL, " +
                    "`personalThesis` TEXT NOT NULL, PRIMARY KEY(`id`))"
            )
            s.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `streak` (`id` TEXT NOT NULL, " +
                    "`currentStreak` INTEGER NOT NULL, `longestStreak` INTEGER NOT NULL, " +
                    "`lastActivityDate` TEXT, `xpMultiplier` REAL NOT NULL, " +
                    "`streakLabel` TEXT NOT NULL, PRIMARY KEY(`id`))"
            )
        }
    }

    private fun applyV17Migration(c: Connection) {
        c.createStatement().use { s -> FirstWinSchemaV17.STATEMENTS.forEach(s::executeUpdate) }
    }

    private fun tableExists(c: Connection, name: String): Boolean {
        c.prepareStatement(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?"
        ).use { ps ->
            ps.setString(1, name)
            ps.executeQuery().use { rs -> return rs.next() }
        }
    }

    private fun rowCount(c: Connection, table: String): Int {
        c.createStatement().use { s ->
            s.executeQuery("SELECT COUNT(*) FROM `$table`").use { rs ->
                rs.next(); return rs.getInt(1)
            }
        }
    }

    @Test
    fun migrationPreservesExistingV16Data() {
        openDb().use { c ->
            createV16Subset(c)
            c.createStatement().use { s ->
                s.executeUpdate(
                    "INSERT INTO `hunter_profile` VALUES " +
                        "('h1','Alireza',7,'HUNTER',4200,120,300,0.4,-16777216,'H','Thesis')"
                )
                s.executeUpdate(
                    "INSERT INTO `streak` VALUES ('s1',9,15,'2026-08-14',1.5,'On fire')"
                )
            }

            applyV17Migration(c)

            // Existing rows survive, byte-for-byte.
            assertEquals(1, rowCount(c, "hunter_profile"))
            assertEquals(1, rowCount(c, "streak"))
            c.createStatement().use { s ->
                s.executeQuery("SELECT name, level, totalXP FROM `hunter_profile` WHERE id='h1'").use { rs ->
                    assertTrue(rs.next())
                    assertEquals("Alireza", rs.getString("name"))
                    assertEquals(7, rs.getInt("level"))
                    assertEquals(4200, rs.getInt("totalXP"))
                }
                s.executeQuery("SELECT currentStreak, streakLabel FROM `streak` WHERE id='s1'").use { rs ->
                    assertTrue(rs.next())
                    assertEquals(9, rs.getInt("currentStreak"))
                    assertEquals("On fire", rs.getString("streakLabel"))
                }
            }

            // New tables created and start EMPTY (no backfill of inferred completion).
            assertTrue(tableExists(c, "first_win_session"))
            assertTrue(tableExists(c, "completion_receipt"))
            assertTrue(tableExists(c, "event_queue"))
            assertEquals(0, rowCount(c, "first_win_session"))
            assertEquals(0, rowCount(c, "completion_receipt"))
            assertEquals(0, rowCount(c, "event_queue"))
        }
    }

    @Test
    fun completionReceiptIdempotencyKeyIsDbEnforced() {
        openDb().use { c ->
            applyV17Migration(c)
            c.createStatement().use { s ->
                s.executeUpdate(
                    "INSERT INTO `completion_receipt` VALUES ('r1','key-A','SETUP',1000)"
                )
            }
            // Duplicate idempotencyKey via plain INSERT must be rejected by the UNIQUE index.
            try {
                c.createStatement().use { s ->
                    s.executeUpdate(
                        "INSERT INTO `completion_receipt` VALUES ('r2','key-A','SETUP',2000)"
                    )
                }
                fail("Expected UNIQUE constraint violation on duplicate idempotencyKey")
            } catch (e: SQLException) {
                assertTrue(e.message!!.contains("UNIQUE", ignoreCase = true))
            }
            // INSERT OR IGNORE (the DAO's OnConflictStrategy.IGNORE) leaves exactly one row.
            c.createStatement().use { s ->
                s.executeUpdate(
                    "INSERT OR IGNORE INTO `completion_receipt` VALUES ('r3','key-A','SETUP',3000)"
                )
            }
            assertEquals(1, rowCount(c, "completion_receipt"))
        }
    }

    @Test
    fun eventQueueIdempotencyKeyIsDbEnforced() {
        openDb().use { c ->
            applyV17Migration(c)
            c.createStatement().use { s ->
                s.executeUpdate(
                    "INSERT INTO `event_queue` VALUES ('e1','evt-A','FIRST_WIN','{}','PENDING',1000)"
                )
            }
            try {
                c.createStatement().use { s ->
                    s.executeUpdate(
                        "INSERT INTO `event_queue` VALUES ('e2','evt-A','FIRST_WIN','{}','PENDING',2000)"
                    )
                }
                fail("Expected UNIQUE constraint violation on duplicate idempotencyKey")
            } catch (e: SQLException) {
                assertTrue(e.message!!.contains("UNIQUE", ignoreCase = true))
            }
            c.createStatement().use { s ->
                s.executeUpdate(
                    "INSERT OR IGNORE INTO `event_queue` VALUES ('e3','evt-A','FIRST_WIN','{}','PENDING',3000)"
                )
            }
            assertEquals(1, rowCount(c, "event_queue"))
            // Distinct keys coexist.
            c.createStatement().use { s ->
                s.executeUpdate(
                    "INSERT INTO `event_queue` VALUES ('e4','evt-B','FIRST_WIN','{}','PENDING',4000)"
                )
            }
            assertEquals(2, rowCount(c, "event_queue"))
        }
    }

    @Test
    fun migrationIsPurelyAdditive() {
        // No statement may DROP, DELETE, ALTER-away, or otherwise mutate existing data.
        FirstWinSchemaV17.STATEMENTS.forEach { sql ->
            val upper = sql.uppercase()
            assertTrue("must be CREATE-only: $sql", upper.trimStart().startsWith("CREATE"))
            assertFalse("no DROP allowed: $sql", upper.contains("DROP"))
            assertFalse("no DELETE allowed: $sql", upper.contains("DELETE"))
            assertFalse("no destructive ALTER: $sql", upper.contains("ALTER"))
        }
    }
}
