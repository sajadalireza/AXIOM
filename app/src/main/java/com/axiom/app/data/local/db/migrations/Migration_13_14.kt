package com.axiom.app.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS `weekly_reviews` (`id` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `step1Summary` TEXT NOT NULL, `step2WrongAssumption` TEXT NOT NULL, `step3CriticFeedback` TEXT NOT NULL, `step4DecisionType` TEXT NOT NULL, `step5JournalText` TEXT NOT NULL, PRIMARY KEY(`id`))")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
