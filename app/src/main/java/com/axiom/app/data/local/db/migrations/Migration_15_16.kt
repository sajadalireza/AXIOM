package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE warrior_profiles ADD COLUMN trackOneName TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE warrior_profiles ADD COLUMN trackOneDetail TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE warrior_profiles ADD COLUMN trackTwoName TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE warrior_profiles ADD COLUMN trackTwoDetail TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE warrior_profiles ADD COLUMN countdownLabel TEXT NOT NULL DEFAULT 'MILESTONE COUNTDOWN'")
        db.execSQL("ALTER TABLE warrior_profiles ADD COLUMN countdownTargetDays INTEGER NOT NULL DEFAULT 180")
    }
}
