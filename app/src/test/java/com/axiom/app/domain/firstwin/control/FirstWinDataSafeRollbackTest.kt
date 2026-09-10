package com.axiom.app.domain.firstwin.control

import com.axiom.app.data.local.AxiomDatabase
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-208 — Data-Safe Rollback and Schema Integrity Guard.
 *
 * Verifies that:
 * 1. Room schema remains at version 18 freeze (no destructive schema changes).
 * 2. Room database does not introduce unverified migrations in WP-208.
 * 3. The control plane relies exclusively on DataStore and in-memory feature flags
 *    without touching, dropping, or truncating any Room transactional tables.
 * 4. Core onboarding preference flags remain isolated and protected.
 */
class FirstWinDataSafeRollbackTest {

    private fun locateFile(relative: String): File {
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("../app/$relative"),
            File(System.getProperty("user.dir") ?: ".", relative),
            File(System.getProperty("user.dir") ?: ".", "app/$relative"),
        )
        return candidates.firstOrNull { it.isFile }
            ?: fail("File $relative not found").let { error("unreachable") }
    }

    @Test
    fun roomDatabase_remainsAtVersion18() {
        val dbSource = locateFile("src/main/java/com/axiom/app/data/local/AxiomDatabase.kt").readText()

        assertTrue(
            "AxiomDatabase must remain at Room schema version 18",
            dbSource.contains("version = 18"),
        )
        assertFalse(
            "AxiomDatabase must not be bumped to version 19 in WP-208",
            dbSource.contains("version = 19"),
        )
    }

    @Test
    fun controlPlaneImpl_doesNotInteractWithRoomDirectly() {
        val implSource = locateFile("src/main/java/com/axiom/app/data/repository/FirstWinControlPlaneImpl.kt").readText()

        assertFalse(
            "FirstWinControlPlaneImpl must not inject AxiomDatabase",
            implSource.contains("AxiomDatabase"),
        )
        assertFalse(
            "FirstWinControlPlaneImpl must not perform SQL drop or truncate",
            implSource.contains("execSQL") || implSource.contains("delete") || implSource.contains("drop"),
        )
        assertTrue(
            "FirstWinControlPlaneImpl must use AxiomPreferences (DataStore)",
            implSource.contains("AxiomPreferences"),
        )
    }

    @Test
    fun preferenceKeys_areIsolatedAndNonDestructive() {
        val prefsSource = locateFile("src/main/java/com/axiom/app/data/local/AxiomPreferences.kt").readText()

        assertTrue(
            "AxiomPreferences must contain FIRST_WIN_VARIANT key",
            prefsSource.contains("first_win_variant"),
        )
        assertTrue(
            "AxiomPreferences must contain FIRST_WIN_ASSIGNED_ELIGIBILITY_VERSION key",
            prefsSource.contains("first_win_assigned_eligibility_version"),
        )
        assertTrue(
            "AxiomPreferences must contain FIRST_WIN_REMOTE_KILL_ACTIVE key",
            prefsSource.contains("first_win_remote_kill_active"),
        )
        // Ensure core flags are not mutated by control plane methods
        val setVariantMethod = prefsSource.substringAfter("setFirstWinVariantAssignment").substringBefore("}")
        assertFalse("setFirstWinVariantAssignment must not touch setup_complete", setVariantMethod.contains("SETUP_COMPLETE"))
        assertFalse("setFirstWinVariantAssignment must not touch first_mission_done", setVariantMethod.contains("FIRST_MISSION_DONE"))
        assertFalse("setFirstWinVariantAssignment must not touch blueprint_setup_complete", setVariantMethod.contains("BLUEPRINT_SETUP_COMPLETE"))
    }
}
