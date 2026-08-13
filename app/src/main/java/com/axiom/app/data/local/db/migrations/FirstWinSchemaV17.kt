package com.axiom.app.data.local.db.migrations

/**
 * WP-204 — single source of truth for the additive v17 First-Win DDL.
 *
 * Both the runtime [MIGRATION_16_17] object and the JVM contract test
 * (`MigrationV16V17SqliteTest`) consume THIS list, so the test proves the exact
 * statements shipped in production — not a hand-copied paraphrase.
 *
 * Strictly additive: only CREATE TABLE / CREATE UNIQUE INDEX. No DROP, no DELETE,
 * no row rewrite, no DataStore read, no backfill of inferred completion. Existing
 * v16 tables/rows are untouched, so WP-203 eligibility routing is unchanged.
 *
 * DDL is kept structurally identical to Room's exported 17.json createSql
 * (same columns, types, NOT NULL, PK, and UNIQUE indexes), so Room's runtime
 * schema validation accepts the migrated database.
 */
object FirstWinSchemaV17 {
    val STATEMENTS: List<String> = listOf(
        "CREATE TABLE IF NOT EXISTS `first_win_session` " +
            "(`sessionId` TEXT NOT NULL, `status` TEXT NOT NULL, " +
            "`createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, " +
            "PRIMARY KEY(`sessionId`))",
        "CREATE TABLE IF NOT EXISTS `completion_receipt` " +
            "(`receiptId` TEXT NOT NULL, `idempotencyKey` TEXT NOT NULL, " +
            "`receiptType` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, " +
            "PRIMARY KEY(`receiptId`))",
        "CREATE UNIQUE INDEX IF NOT EXISTS `index_completion_receipt_idempotencyKey` " +
            "ON `completion_receipt` (`idempotencyKey`)",
        "CREATE TABLE IF NOT EXISTS `event_queue` " +
            "(`eventId` TEXT NOT NULL, `idempotencyKey` TEXT NOT NULL, " +
            "`eventType` TEXT NOT NULL, `payload` TEXT NOT NULL, " +
            "`status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, " +
            "PRIMARY KEY(`eventId`))",
        "CREATE UNIQUE INDEX IF NOT EXISTS `index_event_queue_idempotencyKey` " +
            "ON `event_queue` (`idempotencyKey`)"
    )
}
