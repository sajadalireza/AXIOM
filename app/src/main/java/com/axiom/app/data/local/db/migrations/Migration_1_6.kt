package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_6 = object : Migration(1, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // No schema changes occurred between version 1 and 6
        // This migration is required to satisfy Room's upgrade path without data loss.
    }
}
