package com.axiom.app.db

import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * WP-204 Room v17 First-Win state — schema contract (RED→GREEN, byte-identical).
 *
 * Runs in the JVM `testDebugUnitTest` CI job (no Robolectric needed). It validates
 * the committed Room-exported schema `17.json` — the artifact the `Room Schema` CI
 * job regenerates and pins — asserting:
 *   A. database schema version is 17
 *   B/C/D. the three additive First-Win tables exist
 *   E/F. the completion-receipt and event-queue idempotency UNIQUE indexes exist
 *
 * RED at v16: `17.json` does not exist yet → this test fails.
 * GREEN at v17: version bumped, entities added, `17.json` exported+committed → passes.
 *
 * DB-runtime behaviours (duplicate rejection, legacy-row preservation) are proven by
 * the instrumented AndroidX Room migration tests; this test pins the schema surface.
 */
class SchemaV17ContractTest {

    private val schemaRel = "schemas/com.axiom.app.data.local.AxiomDatabase/17.json"

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
                "Expected Room v17 exported schema not found. Looked in: " +
                    candidates.joinToString { it.absolutePath }
            ).let { error("unreachable") }
    }

    @Test
    fun schemaVersionIs17() {
        val text = locateSchema().readText()
        assertTrue(
            "17.json must declare schema version 17",
            Regex("\"version\"\\s*:\\s*17").containsMatchIn(text)
        )
    }

    @Test
    fun firstWinTablesExist() {
        val text = locateSchema().readText()
        for (table in listOf("first_win_session", "completion_receipt", "event_queue")) {
            assertTrue(
                "v17 schema must contain table `$table`",
                text.contains("\"tableName\": \"$table\"")
            )
        }
    }

    @Test
    fun idempotencyUniqueIndexesExist() {
        val text = locateSchema().readText()
        assertTrue(
            "completion_receipt must have a UNIQUE index on idempotencyKey",
            text.contains("CREATE UNIQUE INDEX IF NOT EXISTS `index_completion_receipt_idempotencyKey`")
        )
        assertTrue(
            "event_queue must have a UNIQUE index on idempotencyKey",
            text.contains("CREATE UNIQUE INDEX IF NOT EXISTS `index_event_queue_idempotencyKey`")
        )
    }
}
