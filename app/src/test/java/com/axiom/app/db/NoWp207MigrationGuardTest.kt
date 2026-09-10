package com.axiom.app.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * WP-207 freeze guard — the First-Win slice MUST NOT touch the Room schema.
 *
 * Runs in the JVM `testDebugUnitTest` CI job against the committed Room-exported
 * `18.json`. It pins (GREEN-by-design, tripwire):
 *
 *   A. schema version stays 18
 *   B. the table count stays 27 (no table added/removed)
 *   C. `first_win_session` keeps EXACTLY its four WP-204 columns
 *      (sessionId, status, createdAt, updatedAt) and gains NO state-machine /
 *      experiment / scheduling columns
 *   D. `missions` gains NO first-win idempotency / scheduling columns
 *   E. `schedule_blocks` gains NO absolute-timestamp / idempotency columns
 *
 * Any future attempt to add a column bumps the schema and fails here. WP-207 derives
 * its state from the EXISTING typed facts; it must never require a migration.
 */
class NoWp207MigrationGuardTest {

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
        val endQuote = text.indexOf('"', firstQuote + 1)
        return text.substring(firstQuote + 1, endQuote)
    }

    /**
     * Every backtick-quoted column identifier in a createSql. Room's exported createSql
     * renders the table name as the `${TABLE_NAME}` placeholder, so placeholder tokens are
     * excluded here — otherwise a naive backtick scan would misreport the placeholder as a
     * column and break the exact-column assertion.
     */
    private fun identifiers(sql: String): Set<String> =
        Regex("`([^`]+)`")
            .findAll(sql)
            .map { it.groupValues[1] }
            .filterNot { it.startsWith("\${") }
            .toSet()

    @Test
    fun schemaVersionStays18() {
        val text = locateSchema().readText()
        assertTrue(
            "18.json must declare schema version 18 (no bump allowed for WP-207)",
            Regex("\"version\"\\s*:\\s*18").containsMatchIn(text)
        )
    }

    @Test
    fun tableCountStays27() {
        val text = locateSchema().readText()
        val count = Regex("\"tableName\"\\s*:").findAll(text).count()
        assertEquals("WP-207 must not add/remove any Room table", 27, count)
    }

    @Test
    fun firstWinSessionKeepsExactlyFourColumns() {
        val sql = createSqlFor(locateSchema().readText(), "first_win_session")
        val columns = identifiers(sql)
        assertEquals(
            "first_win_session must keep exactly its WP-204 columns",
            setOf("sessionId", "status", "createdAt", "updatedAt"),
            columns
        )
    }

    @Test
    fun firstWinSessionGainsNoStateMachineOrExperimentColumns() {
        val sql = createSqlFor(locateSchema().readText(), "first_win_session")
        val forbidden = setOf(
            "area", "state", "primaryMissionId", "nextMissionId",
            "experimentId", "experimentVariant", "assignmentTimestamp",
            "eligibilityVersion", "rewardSeenAt", "completionVersion",
            "firstWinSessionId", "scheduledFor", "startedAt",
            "creationIdempotencyKey", "completionIdempotencyKey", "scheduleIdempotencyKey"
        )
        val present = forbidden.intersect(identifiers(sql))
        assertTrue("first_win_session must not gain columns: $present", present.isEmpty())
    }

    @Test
    fun missionsGainNoFirstWinColumns() {
        val sql = createSqlFor(locateSchema().readText(), "missions")
        val forbidden = setOf(
            "firstWinSessionId", "creationIdempotencyKey", "completionIdempotencyKey",
            "scheduleIdempotencyKey", "scheduledFor", "startedAt", "area", "completionVersion"
        )
        val present = forbidden.intersect(identifiers(sql))
        assertFalse("missions must not gain first-win columns: $present", present.isNotEmpty())
    }

    @Test
    fun scheduleBlocksGainNoFirstWinColumns() {
        val sql = createSqlFor(locateSchema().readText(), "schedule_blocks")
        val forbidden = setOf(
            "scheduledFor", "scheduledDate", "scheduledAt", "dueAt",
            "scheduleIdempotencyKey", "firstWinSessionId", "completionVersion", "startedAt"
        )
        val present = forbidden.intersect(identifiers(sql))
        assertFalse("schedule_blocks must not gain first-win columns: $present", present.isNotEmpty())
    }
}
