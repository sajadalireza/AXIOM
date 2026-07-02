package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create warrior_profiles
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS warrior_profiles (
                id TEXT NOT NULL PRIMARY KEY,
                codename TEXT NOT NULL,
                oneLineThesis TEXT NOT NULL,
                rareProfileDescription TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
        """.trimIndent())

        // Create tracks
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tracks (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                color INTEGER NOT NULL,
                icon TEXT NOT NULL,
                description TEXT NOT NULL
            )
        """.trimIndent())

        // Create schedule_blocks
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS schedule_blocks (
                id TEXT NOT NULL PRIMARY KEY,
                trackId TEXT,
                startTime TEXT NOT NULL,
                title TEXT NOT NULL,
                actionDescription TEXT NOT NULL,
                tag TEXT NOT NULL,
                recurrence TEXT NOT NULL,
                isNonNegotiable INTEGER NOT NULL
            )
        """.trimIndent())

        // Create custom_kpis
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS custom_kpis (
                id TEXT NOT NULL PRIMARY KEY,
                trackId TEXT,
                name TEXT NOT NULL,
                targetValue REAL NOT NULL,
                targetUnit TEXT NOT NULL,
                measurementHint TEXT NOT NULL,
                redFlagAction TEXT NOT NULL
            )
        """.trimIndent())

        // Create iron_rules
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS iron_rules (
                id TEXT NOT NULL PRIMARY KEY,
                orderIndex INTEGER NOT NULL,
                ruleText TEXT NOT NULL,
                isAutomatable INTEGER NOT NULL
            )
        """.trimIndent())

        // Create hard_truths_affirmations
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS hard_truths_affirmations (
                id TEXT NOT NULL PRIMARY KEY,
                type TEXT NOT NULL,
                text TEXT NOT NULL,
                orderIndex INTEGER NOT NULL
            )
        """.trimIndent())

        // Create major_milestones
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS major_milestones (
                id TEXT NOT NULL PRIMARY KEY,
                label TEXT NOT NULL,
                targetDate INTEGER NOT NULL,
                description TEXT NOT NULL
            )
        """.trimIndent())

        // Create key_relationships
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS key_relationships (
                id TEXT NOT NULL PRIMARY KEY,
                label TEXT NOT NULL,
                category TEXT NOT NULL,
                lastInteractionAt INTEGER,
                preparedTalkingPoint TEXT NOT NULL
            )
        """.trimIndent())

        // Create financial_checkpoints
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS financial_checkpoints (
                id TEXT NOT NULL PRIMARY KEY,
                monthIndex INTEGER NOT NULL,
                targetAmount REAL NOT NULL,
                currency TEXT NOT NULL
            )
        """.trimIndent())

        // Create monthly_income_entries
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS monthly_income_entries (
                id TEXT NOT NULL PRIMARY KEY,
                monthIndex INTEGER NOT NULL,
                actualAmount REAL NOT NULL
            )
        """.trimIndent())

        // Add columns to missions table safely
        try {
            db.execSQL("ALTER TABLE missions ADD COLUMN trackId TEXT")
        } catch (e: Exception) {
            // Already column exists
        }
        try {
            db.execSQL("ALTER TABLE missions ADD COLUMN scheduleBlockId TEXT")
        } catch (e: Exception) {
            // Already column exists
        }
        try {
            db.execSQL("ALTER TABLE skills ADD COLUMN trackId TEXT")
        } catch (e: Exception) {
            // Already column exists
        }
    }
}
