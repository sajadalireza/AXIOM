package com.axiom.app.db

import com.axiom.app.data.local.db.migrations.CompletionReceiptSchemaV18
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
 * WP-205 (PROMPT 41B) — JVM proof of the v17 -> v18 `completion_receipt` enrichment on
 * a real SQLite engine (org.xerial:sqlite-jdbc), running inside the CI
 * `testDebugUnitTest` channel (no connected-device job exists).
 *
 * These tests exercise the EXACT production DDL via [CompletionReceiptSchemaV18.STATEMENTS]
 * (the same list [com.axiom.app.data.local.db.migrations.MIGRATION_17_18] replays), layered
 * on the shipped v17 DDL [FirstWinSchemaV17.STATEMENTS], proving:
 *   - legacy metadata-only receipts survive the migration with NO fabricated XP/streak,
 *   - the rich receipt round-trips all canonical fields,
 *   - the WP-204 UNIQUE idempotency guarantee is intact after v18,
 *   - the migration is purely additive ALTER ... ADD COLUMN, and
 *   - the new rich columns are nullable in the migrated table.
 */
class MigrationV17V18SqliteTest {

    private fun openDb(): Connection = DriverManager.getConnection("jdbc:sqlite::memory:")

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

    private fun applyV17(c: Connection) =
        c.createStatement().use { s -> FirstWinSchemaV17.STATEMENTS.forEach(s::executeUpdate) }

    private fun applyV18(c: Connection) =
        c.createStatement().use { s -> CompletionReceiptSchemaV18.STATEMENTS.forEach(s::executeUpdate) }

    private fun rowCount(c: Connection, table: String): Int =
        c.createStatement().use { s ->
            s.executeQuery("SELECT COUNT(*) FROM `$table`").use { rs -> rs.next(); rs.getInt(1) }
        }

    /**
     * §11 — a real v17 fixture carrying a LEGACY metadata-only receipt survives the
     * migration byte-for-byte, and the six new rich columns are NULL (never fabricated).
     */
    @Test
    fun migrationPreservesLegacyMetadataReceiptWithoutFabrication() {
        openDb().use { c ->
            createV16Subset(c)
            applyV17(c)

            // Seed a legacy metadata-only completion receipt (v17 write shape: 4 cols).
            c.prepareStatement(
                "INSERT INTO `completion_receipt` " +
                    "(`receiptId`,`idempotencyKey`,`receiptType`,`createdAt`) VALUES (?,?,?,?)"
            ).use { ps ->
                ps.setString(1, "legacy-r1")
                ps.setString(2, "idem-legacy-1")
                ps.setString(3, "LEGACY_METADATA_RECEIPT")
                ps.setLong(4, 1_600_000_000_000L)
                ps.executeUpdate()
            }
            // An unrelated v17 table also carries a row, to prove it is untouched.
            c.prepareStatement(
                "INSERT INTO `event_queue` " +
                    "(`eventId`,`idempotencyKey`,`eventType`,`payload`,`status`,`createdAt`) " +
                    "VALUES (?,?,?,?,?,?)"
            ).use { ps ->
                ps.setString(1, "evt-1"); ps.setString(2, "evt-idem-1")
                ps.setString(3, "FIRST_WIN"); ps.setString(4, "{}")
                ps.setString(5, "PENDING"); ps.setLong(6, 1_600_000_000_001L)
                ps.executeUpdate()
            }

            applyV18(c)

            // Legacy row survives with EXACT metadata + all rich fields NULL.
            c.createStatement().use { s ->
                s.executeQuery(
                    "SELECT `receiptId`,`idempotencyKey`,`receiptType`,`createdAt`," +
                        "`sessionId`,`missionId`,`hunterXpAwarded`,`skillXpAwarded`," +
                        "`resultingStreak`,`completedAt` FROM `completion_receipt`"
                ).use { rs ->
                    assertTrue("legacy receipt row must survive", rs.next())
                    assertEquals("legacy-r1", rs.getString("receiptId"))
                    assertEquals("idem-legacy-1", rs.getString("idempotencyKey"))
                    assertEquals("LEGACY_METADATA_RECEIPT", rs.getString("receiptType"))
                    assertEquals(1_600_000_000_000L, rs.getLong("createdAt"))
                    rs.getString("sessionId"); assertTrue(rs.wasNull())
                    rs.getString("missionId"); assertTrue(rs.wasNull())
                    rs.getInt("hunterXpAwarded"); assertTrue("hunterXpAwarded not fabricated", rs.wasNull())
                    rs.getLong("skillXpAwarded"); assertTrue("skillXpAwarded not fabricated", rs.wasNull())
                    rs.getInt("resultingStreak"); assertTrue("resultingStreak not fabricated", rs.wasNull())
                    rs.getLong("completedAt"); assertTrue(rs.wasNull())
                    assertFalse("exactly one receipt row", rs.next())
                }
            }
            assertEquals("unrelated event_queue row untouched", 1, rowCount(c, "event_queue"))
        }
    }

    /** §12 — a WP205 rich completion receipt round-trips every canonical field. */
    @Test
    fun richReceiptRoundTripsAllCanonicalFields() {
        openDb().use { c ->
            createV16Subset(c)
            applyV17(c)
            applyV18(c)

            c.prepareStatement(
                "INSERT INTO `completion_receipt` " +
                    "(`receiptId`,`idempotencyKey`,`receiptType`,`createdAt`," +
                    "`sessionId`,`missionId`,`hunterXpAwarded`,`skillXpAwarded`," +
                    "`resultingStreak`,`completedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"
            ).use { ps ->
                ps.setString(1, "rich-r1")
                ps.setString(2, "idem-rich-1")
                ps.setString(3, "WP205_RICH_COMPLETION_RECEIPT")
                ps.setLong(4, 1_700_000_000_000L)
                ps.setString(5, "sess-1")
                ps.setString(6, "mission-1")
                ps.setInt(7, 50)
                ps.setLong(8, 20L)
                ps.setInt(9, 1)
                ps.setLong(10, 1_700_000_000_500L)
                ps.executeUpdate()
            }

            c.createStatement().use { s ->
                s.executeQuery(
                    "SELECT * FROM `completion_receipt` WHERE `receiptId` = 'rich-r1'"
                ).use { rs ->
                    assertTrue(rs.next())
                    assertEquals("idem-rich-1", rs.getString("idempotencyKey"))
                    assertEquals("WP205_RICH_COMPLETION_RECEIPT", rs.getString("receiptType"))
                    assertEquals("sess-1", rs.getString("sessionId"))
                    assertEquals("mission-1", rs.getString("missionId"))
                    assertEquals(50, rs.getInt("hunterXpAwarded"))
                    assertEquals(20L, rs.getLong("skillXpAwarded"))
                    assertEquals(1, rs.getInt("resultingStreak"))
                    assertEquals(1_700_000_000_500L, rs.getLong("completedAt"))
                }
            }
        }
    }

    /** §13 — the WP-204 UNIQUE idempotency guarantee is intact after v18. */
    @Test
    fun idempotencyUniqueIndexStillEnforcedAfterV18() {
        openDb().use { c ->
            createV16Subset(c)
            applyV17(c)
            applyV18(c)

            fun insert(id: String) = c.prepareStatement(
                "INSERT INTO `completion_receipt` " +
                    "(`receiptId`,`idempotencyKey`,`receiptType`,`createdAt`) VALUES (?,?,?,?)"
            ).use { ps ->
                ps.setString(1, id); ps.setString(2, "dup-key")
                ps.setString(3, "WP205_RICH_COMPLETION_RECEIPT"); ps.setLong(4, 1L)
                ps.executeUpdate()
            }

            insert("first")
            try {
                insert("second")
                fail("duplicate idempotencyKey must be rejected by UNIQUE index")
            } catch (e: SQLException) {
                assertTrue(
                    "expected UNIQUE constraint violation, got: ${e.message}",
                    (e.message ?: "").contains("UNIQUE", ignoreCase = true)
                )
            }
            assertEquals("duplicate key must leave exactly one row", 1, rowCount(c, "completion_receipt"))
        }
    }

    /** §5/§6 — the migration is strictly additive: only ALTER ... ADD COLUMN, no rebuild. */
    @Test
    fun migrationIsPurelyAdditiveAddColumn() {
        assertEquals(6, CompletionReceiptSchemaV18.STATEMENTS.size)
        CompletionReceiptSchemaV18.STATEMENTS.forEach { sql ->
            val u = sql.uppercase()
            assertTrue("must ALTER TABLE completion_receipt: $sql",
                u.startsWith("ALTER TABLE `COMPLETION_RECEIPT` ADD COLUMN"))
            assertFalse("no DROP: $sql", u.contains("DROP"))
            assertFalse("no DELETE: $sql", u.contains("DELETE"))
            assertFalse("no NOT NULL (would fabricate/fail legacy rows): $sql", u.contains("NOT NULL"))
            assertFalse("no DEFAULT (no fabricated values): $sql", u.contains("DEFAULT"))
        }
    }

    /** §6 — every new rich column is nullable in the migrated table (notnull = 0). */
    @Test
    fun richColumnsAreNullableInMigratedTable() {
        openDb().use { c ->
            createV16Subset(c)
            applyV17(c)
            applyV18(c)

            val nullable = mutableMapOf<String, Boolean>()
            c.createStatement().use { s ->
                s.executeQuery("PRAGMA table_info(`completion_receipt`)").use { rs ->
                    while (rs.next()) {
                        nullable[rs.getString("name")] = rs.getInt("notnull") == 0
                    }
                }
            }
            listOf(
                "sessionId", "missionId", "hunterXpAwarded",
                "skillXpAwarded", "resultingStreak", "completedAt"
            ).forEach { col ->
                assertEquals("rich column `$col` must exist and be nullable", true, nullable[col])
            }
            // Legacy metadata columns remain NOT NULL.
            assertEquals(false, nullable["receiptId"])
            assertEquals(false, nullable["idempotencyKey"])
        }
    }
}
