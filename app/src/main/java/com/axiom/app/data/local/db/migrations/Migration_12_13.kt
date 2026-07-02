package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE missions ADD COLUMN qualityScore REAL NOT NULL DEFAULT 1.0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            db.execSQL("ALTER TABLE missions ADD COLUMN effectiveHours REAL NOT NULL DEFAULT 0.0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
