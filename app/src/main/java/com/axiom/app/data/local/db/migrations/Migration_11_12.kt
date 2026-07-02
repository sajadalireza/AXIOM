package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // No schema changes occurred between version 11 and 12, or the schemas are identical.
        // This migration is required to satisfy Room's upgrade path.
    }
}
