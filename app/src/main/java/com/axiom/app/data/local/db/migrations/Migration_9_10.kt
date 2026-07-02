package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS kpi_progress (
                id TEXT NOT NULL,
                kpiId TEXT NOT NULL,
                date INTEGER NOT NULL,
                incrementValue REAL NOT NULL,
                PRIMARY KEY(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS kpi_miss_streaks (
                kpiId TEXT NOT NULL,
                missStreak INTEGER NOT NULL,
                PRIMARY KEY(kpiId)
            )
        """.trimIndent())
    }
}
