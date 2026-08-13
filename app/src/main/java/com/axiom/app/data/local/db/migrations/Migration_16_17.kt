package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * WP-204 Room v16 → v17 — additive First-Win state.
 *
 * Creates the three new persistence-primitive tables (first_win_session,
 * completion_receipt, event_queue) and the two idempotency UNIQUE indexes by
 * replaying [FirstWinSchemaV17.STATEMENTS] — the single source of truth also
 * exercised by the JVM contract test.
 *
 * Strictly additive: no DROP, no DELETE, no row rewrite, no DataStore read,
 * no backfill of inferred completion. Existing v16 tables/rows are untouched,
 * so eligibility routing (WP-203) is unchanged by this migration.
 */
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        FirstWinSchemaV17.STATEMENTS.forEach(db::execSQL)
    }
}
