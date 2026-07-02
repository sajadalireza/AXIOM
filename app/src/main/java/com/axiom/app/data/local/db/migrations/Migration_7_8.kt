package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS muscle_groups (
                id TEXT NOT NULL PRIMARY KEY,
                displayName TEXT NOT NULL,
                strengthXP INTEGER NOT NULL,
                recoveryWindowHours INTEGER NOT NULL,
                lastTrainedAt INTEGER,
                weeklyVolumeCount INTEGER NOT NULL
            )
        """.trimIndent())
    }
}
