package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * WP-205 Room v17 → v18 — enrich the existing `completion_receipt` into the canonical
 * rich First-Win completion receipt (design spec §7), authorized by PROMPT 41B.
 *
 * Adds nullable `sessionId`, `missionId`, `hunterXpAwarded`, `skillXpAwarded`,
 * `resultingStreak`, `completedAt` columns by replaying
 * [CompletionReceiptSchemaV18.STATEMENTS] — the single source of truth also exercised
 * by the JVM contract test.
 *
 * Strictly additive: only `ALTER TABLE ... ADD COLUMN`. No DROP, no DELETE, no table
 * rebuild, no row rewrite, no DataStore read, no backfill. Existing v17 rows (including
 * legacy metadata-only completion receipts) survive unchanged and receive NULL for the
 * new fields — historical XP / streak are never fabricated. The WP-204 UNIQUE
 * idempotency index on `idempotencyKey` is unaffected.
 */
val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        CompletionReceiptSchemaV18.STATEMENTS.forEach(db::execSQL)
    }
}
