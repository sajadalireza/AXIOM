package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS vital_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                type TEXT NOT NULL,
                value REAL NOT NULL,
                loggedAt INTEGER NOT NULL
            )
        """.trimIndent())
        
        db.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_vital_logs_date_type ON vital_logs (date, type)
        """.trimIndent())
    }
}
