package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add columns to iron_rules
        db.execSQL("ALTER TABLE iron_rules ADD COLUMN linkedSignalType TEXT NOT NULL DEFAULT 'NONE'")
        db.execSQL("ALTER TABLE iron_rules ADD COLUMN linkedKpiId TEXT DEFAULT NULL")
        
        // Create iron_rule_violation_logs table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `iron_rule_violation_logs` (" +
            "`id` TEXT NOT NULL, " +
            "`ruleId` TEXT NOT NULL, " +
            "`date` INTEGER NOT NULL, " +
            "`wasAutomaticallyDetected` INTEGER NOT NULL, " +
            "PRIMARY KEY(`id`))"
        )
    }
}
