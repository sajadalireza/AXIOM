package com.axiom.app.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.axiom.app.data.local.AxiomDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AxiomDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To16() {
        // Create database with version 1
        var db = helper.createDatabase(TEST_DB, 1)

        // Insert initial data at version 1
        db.execSQL("INSERT INTO hunter_profile (id, name, level, rankLabel, totalXP, currentXP, xpToNextLevel, progressPercent, rankColor, rankGlyph, personalThesis) VALUES ('1', 'Test Warrior', 1, 'RECRUIT', 0, 0, 100, 0.0, 0, '', '')")

        db.close()

        // Run migrations sequentially up to version 16
        db = helper.runMigrationsAndValidate(
            TEST_DB,
            16,
            true,
            com.axiom.app.db.migrations.MIGRATION_1_6,
            com.axiom.app.db.migrations.MIGRATION_6_7,
            com.axiom.app.db.migrations.MIGRATION_7_8,
            com.axiom.app.db.migrations.MIGRATION_8_9,
            com.axiom.app.db.migrations.MIGRATION_9_10,
            com.axiom.app.db.migrations.MIGRATION_10_11,
            com.axiom.app.db.migrations.MIGRATION_11_12,
            com.axiom.app.db.migrations.MIGRATION_12_13,
            com.axiom.app.db.migrations.MIGRATION_13_14,
            com.axiom.app.db.migrations.MIGRATION_14_15,
            com.axiom.app.db.migrations.MIGRATION_15_16
        )

        // Check if data is retained
        val cursor = db.query("SELECT * FROM hunter_profile WHERE id='1'")
        assert(cursor.moveToFirst())
        val nameIndex = cursor.getColumnIndex("name")
        assert(cursor.getString(nameIndex) == "Test Warrior")
        cursor.close()
    }
}
