package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE hunter_profile ADD COLUMN currentHp INTEGER NOT NULL DEFAULT 100")
        db.execSQL("ALTER TABLE hunter_profile ADD COLUMN maxHp INTEGER NOT NULL DEFAULT 100")
    }
}
