package com.axiom.app.domain.firstwin.control

import android.content.Context
import android.content.ContextWrapper
import com.axiom.app.core.FeatureFlags
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.repository.FirstWinControlPlaneImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

private class TestContext : ContextWrapper(null) {
    override fun getApplicationContext(): Context = this
    override fun getFilesDir(): File = File(System.getProperty("java.io.tmpdir") ?: "/tmp")
    override fun getNoBackupFilesDir(): File = File(System.getProperty("java.io.tmpdir") ?: "/tmp")
}

private class FakeControlPreferences : AxiomPreferences(TestContext()) {
    var storedVariant: String? = null
    var storedVersion: Int? = null
    var storedTimestamp: Long? = null
    var storedRemoteKill: Boolean = false

    override val firstWinVariantFlow: Flow<String?>
        get() = flowOf(storedVariant)

    override val firstWinAssignedEligibilityVersionFlow: Flow<Int?>
        get() = flowOf(storedVersion)

    override val firstWinAssignmentTimestampFlow: Flow<Long?>
        get() = flowOf(storedTimestamp)

    override val firstWinRemoteKillFlow: Flow<Boolean>
        get() = flowOf(storedRemoteKill)

    override suspend fun setFirstWinVariantAssignment(
        variant: String,
        eligibilityVersion: Int,
        timestamp: Long,
    ) {
        storedVariant = variant
        storedVersion = eligibilityVersion
        storedTimestamp = timestamp
    }

    override suspend fun setFirstWinRemoteKill(active: Boolean) {
        storedRemoteKill = active
    }
}

class FirstWinControlPlaneTest {

    private lateinit var preferences: FakeControlPreferences
    private lateinit var controlPlane: FirstWinControlPlane

    @Before
    fun setUp() {
        FeatureFlags.FIRST_WIN_LOCAL_KILL = false
        preferences = FakeControlPreferences()
        controlPlane = FirstWinControlPlaneImpl(preferences)
    }

    @After
    fun tearDown() {
        FeatureFlags.FIRST_WIN_LOCAL_KILL = false
    }

    @Test
    fun freshUser_assignsDefaultTreatmentAndCurrentVersion() = runBlocking {
        val variant = controlPlane.getOrAssignVariant()

        assertEquals(FirstWinVariant.TREATMENT, variant)
        assertEquals("TREATMENT", preferences.storedVariant)
        assertEquals(FirstWinControlPlane.CURRENT_ELIGIBILITY_VERSION, preferences.storedVersion)
        assertNotNull(preferences.storedTimestamp)
        assertTrue(preferences.storedTimestamp!! > 0L)
    }

    @Test
    fun assignmentDrift_isStrictlyZeroAcrossRepeatedCalls() = runBlocking {
        val initialVariant = controlPlane.getOrAssignVariant()
        val initialTimestamp = preferences.storedTimestamp

        repeat(100) { iteration ->
            val repeatedVariant = controlPlane.getOrAssignVariant(defaultVariant = FirstWinVariant.CONTROL)
            assertEquals("Variant must not drift on iteration $iteration", initialVariant, repeatedVariant)
            assertEquals("Timestamp must not drift on iteration $iteration", initialTimestamp, preferences.storedTimestamp)
        }
    }

    @Test
    fun existingPersistedVariant_isPreservedWithoutOverwriting() = runBlocking {
        preferences.storedVariant = FirstWinVariant.CONTROL.name
        preferences.storedVersion = FirstWinControlPlane.CURRENT_ELIGIBILITY_VERSION
        preferences.storedTimestamp = 12345L

        val variant = controlPlane.getOrAssignVariant(defaultVariant = FirstWinVariant.TREATMENT)

        assertEquals(FirstWinVariant.CONTROL, variant)
        assertEquals(12345L, preferences.storedTimestamp)
    }

    @Test
    fun localKillSwitch_disablesTreatmentWithoutMutatingVariant() = runBlocking {
        controlPlane.getOrAssignVariant()
        assertTrue("Treatment should be active before local kill", controlPlane.isTreatmentActive())

        controlPlane.setLocalKill(true)

        assertTrue(controlPlane.isLocalKilled())
        assertTrue(controlPlane.isKillSwitchActive())
        assertFalse("Treatment must be inactive when local kill is true", controlPlane.isTreatmentActive())
        assertEquals("Stored variant must remain TREATMENT", "TREATMENT", preferences.storedVariant)

        controlPlane.setLocalKill(false)
        assertTrue("Treatment must be active again when local kill is released", controlPlane.isTreatmentActive())
    }

    @Test
    fun remoteKillSwitch_disablesTreatmentWithoutMutatingVariant() = runBlocking {
        controlPlane.getOrAssignVariant()
        assertTrue("Treatment should be active before remote kill", controlPlane.isTreatmentActive())

        controlPlane.setRemoteKill(true)

        assertTrue(controlPlane.isRemoteKilled())
        assertTrue(controlPlane.isKillSwitchActive())
        assertFalse("Treatment must be inactive when remote kill is true", controlPlane.isTreatmentActive())
        assertEquals("Stored variant must remain TREATMENT", "TREATMENT", preferences.storedVariant)

        controlPlane.setRemoteKill(false)
        assertTrue("Treatment must be active again when remote kill is released", controlPlane.isTreatmentActive())
    }

    @Test
    fun controlVariant_neverEvaluatesTreatmentActive() = runBlocking {
        preferences.storedVariant = FirstWinVariant.CONTROL.name
        preferences.storedVersion = FirstWinControlPlane.CURRENT_ELIGIBILITY_VERSION
        preferences.storedTimestamp = 1000L

        assertFalse("Control variant must not be treatment active", controlPlane.isTreatmentActive())
    }

    @Test
    fun snapshot_accuratelyReflectsControlPlaneState() = runBlocking {
        controlPlane.setRemoteKill(true)
        controlPlane.setLocalKill(false)

        val snapshot = controlPlane.getSnapshot()

        assertEquals(FirstWinControlPlane.CURRENT_ELIGIBILITY_VERSION, snapshot.eligibilityVersion)
        assertEquals(FirstWinVariant.TREATMENT, snapshot.assignedVariant)
        assertFalse(snapshot.isLocalKilled)
        assertTrue(snapshot.isRemoteKilled)
        assertFalse(snapshot.isTreatmentActive)
        assertTrue(snapshot.assignmentTimestamp > 0L)
    }
}
