package com.axiom.app.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * WP-205 (v18 receipt prerequisite) — Room v18 rich completion-receipt schema
 * contract (RED -> GREEN, byte-identical test between phases).
 *
 * Runs in the JVM `testDebugUnitTest` CI job. It validates the committed
 * Room-exported schema `18.json` — the artifact the `Room Schema` CI job
 * regenerates and pins — asserting the PRODUCT-OWNER-authorized v17 -> v18
 * enrichment (PROMPT 41B, DECISION 1/2):
 *
 *   A. database schema version is 18
 *   D. the existing `completion_receipt` table now carries the canonical rich
 *      First-Win completion fields (sessionId, missionId, hunterXpAwarded,
 *      skillXpAwarded, resultingStreak, completedAt)
 *   E. the WP-204 idempotency UNIQUE index on `idempotencyKey` still exists
 *   H. no second receipt table is introduced and the unrelated v17 tables remain
 *      (table set is unchanged; only `completion_receipt` is modified)
 *
 * The legacy metadata columns (receiptId, idempotencyKey, receiptType, createdAt)
 * must survive exactly. Rich columns must be NULLABLE so migrated legacy rows are
 * never assigned fabricated XP / streak values (DECISION 3).
 *
 * RED at v17: `18.json` does not exist yet -> this test fails.
 * GREEN at v18: version bumped, entity enriched, `18.json` exported+committed -> passes.
 *
 * DB-runtime behaviours (ALTER-based migration preservation, rich round-trip,
 * duplicate rejection) are proven on a real SQLite engine by
 * `MigrationV17V18SqliteTest`; this test pins the schema surface only.
 */
class SchemaV18ContractTest {

    private val schemaRel = "schemas/com.axiom.app.data.local.AxiomDatabase/18.json"

    private fun locateSchema(): File {
        val candidates = listOf(
            File(schemaRel),
            File("app/$schemaRel"),
            File("../app/$schemaRel"),
            File(System.getProperty("user.dir") ?: ".", schemaRel),
            File(System.getProperty("user.dir") ?: ".", "app/$schemaRel")
        )
        return candidates.firstOrNull { it.isFile }
            ?: fail(
                "Expected Room v18 exported schema not found. Looked in: " +
                    candidates.joinToString { it.absolutePath }
            ).let { error("unreachable") }
    }

    /** Extracts the `createSql` string Room exported for a given table. */
    private fun createSqlFor(text: String, table: String): String {
        val marker = "\"tableName\": \"$table\""
        val at = text.indexOf(marker)
        assertTrue("v18 schema must contain table `$table`", at >= 0)
        val sqlKey = text.indexOf("\"createSql\"", at)
        assertTrue("table `$table` must expose a createSql", sqlKey >= 0)
        val firstQuote = text.indexOf('"', text.indexOf(':', sqlKey) + 1)
        val endQuote = text.indexOf('"', firstQuote + 1).let {
            // createSql has no embedded quotes in Room output beyond the wrapping pair
            it
        }
        return text.substring(firstQuote + 1, endQuote)
    }

    @Test
    fun schemaVersionIs18() {
        val text = locateSchema().readText()
        assertTrue(
            "18.json must declare schema version 18",
            Regex("\"version\"\\s*:\\s*18").containsMatchIn(text)
        )
    }

    @Test
    fun completionReceiptRetainsLegacyMetadataColumns() {
        val sql = createSqlFor(locateSchema().readText(), "completion_receipt")
        for (col in listOf(
            "`receiptId` TEXT NOT NULL",
            "`idempotencyKey` TEXT NOT NULL",
            "`receiptType` TEXT NOT NULL",
            "`createdAt` INTEGER NOT NULL"
        )) {
            assertTrue("completion_receipt must retain legacy column: $col\nGOT: $sql", sql.contains(col))
        }
        assertTrue(
            "completion_receipt primary key must remain receiptId",
            sql.contains("PRIMARY KEY(`receiptId`)")
        )
    }

    @Test
    fun completionReceiptGainsNullableRichFields() {
        val sql = createSqlFor(locateSchema().readText(), "completion_receipt")
        // Rich fields must be NULLABLE (no NOT NULL) so legacy rows are not fabricated.
        for (col in listOf(
            "`sessionId` TEXT",
            "`missionId` TEXT",
            "`hunterXpAwarded` INTEGER",
            "`skillXpAwarded` INTEGER",
            "`resultingStreak` INTEGER",
            "`completedAt` INTEGER"
        )) {
            assertTrue("completion_receipt must add rich field `$col`\nGOT: $sql", sql.contains(col))
        }
        for (badCol in listOf(
            "`sessionId` TEXT NOT NULL",
            "`missionId` TEXT NOT NULL",
            "`hunterXpAwarded` INTEGER NOT NULL",
            "`skillXpAwarded` INTEGER NOT NULL",
            "`resultingStreak` INTEGER NOT NULL",
            "`completedAt` INTEGER NOT NULL"
        )) {
            assertTrue(
                "rich field must be NULLABLE (legacy rows must not be fabricated): $badCol",
                !sql.contains(badCol)
            )
        }
    }

    @Test
    fun idempotencyUniqueIndexStillEnforced() {
        val text = locateSchema().readText()
        assertTrue(
            "completion_receipt must keep its UNIQUE idempotency index (WP-204 guarantee)",
            text.contains("CREATE UNIQUE INDEX IF NOT EXISTS `index_completion_receipt_idempotencyKey`")
        )
    }

    @Test
    fun noSecondReceiptTableAndUnrelatedTablesUnchanged() {
        val text = locateSchema().readText()
        // DECISION 1: enrich the existing table, do NOT create a competing receipt table.
        assertTrue(
            "must NOT introduce a competing `first_win_completion_receipts` table",
            !text.contains("\"tableName\": \"first_win_completion_receipts\"")
        )
        // The full v17 table set must still be present (only completion_receipt is modified).
        val expectedTables = listOf(
            "hunter_profile", "skills", "missions", "dungeons", "shadows", "streak",
            "system_feed", "warrior_profiles", "tracks", "schedule_blocks", "custom_kpis",
            "iron_rules", "hard_truths_affirmations", "major_milestones", "key_relationships",
            "financial_checkpoints", "monthly_income_entries", "muscle_groups", "vital_logs",
            "kpi_progress", "kpi_miss_streaks", "iron_rule_violation_logs", "daily_habit_logs",
            "weekly_reviews", "first_win_session", "completion_receipt", "event_queue"
        )
        for (t in expectedTables) {
            assertTrue("v18 must retain table `$t`", text.contains("\"tableName\": \"$t\""))
        }
        // Count of tableName occurrences must equal the v17 count (27) — no table added/removed.
        val count = Regex("\"tableName\"\\s*:").findAll(text).count()
        assertEquals("v18 must have exactly the v17 table count (only completion_receipt modified)", 27, count)
    }
}
