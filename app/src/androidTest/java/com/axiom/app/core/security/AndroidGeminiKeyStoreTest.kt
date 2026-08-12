package com.axiom.app.core.security

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * WP-104 SEC-104-003 — AndroidKeyStore round-trip + clear semantics.
 * Instrumentation test: requires an Android runtime (AndroidKeyStore), so it is not
 * part of the JVM `testDebugUnitTest` battery. Run via `connectedDebugAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class AndroidGeminiKeyStoreTest {

    private lateinit var store: GeminiKeyStore

    @Before fun setUp() {
        store = AndroidGeminiKeyStore(ApplicationProvider.getApplicationContext())
        store.clear()
    }

    @After fun tearDown() { store.clear() }

    @Test fun storeThenRetrieveRoundTrips() {
        assertFalse(store.hasKey())
        store.store("AIzaSyFAKE-TEST-VALUE-not-real")
        assertTrue(store.hasKey())
        assertEquals("AIzaSyFAKE-TEST-VALUE-not-real", store.retrieve())
    }

    @Test fun clearRemovesKey() {
        store.store("AIzaSyFAKE-TEST-VALUE-not-real")
        store.clear()
        assertFalse(store.hasKey())
        assertNull(store.retrieve())
    }

    @Test fun overwriteReplacesValue() {
        store.store("first-fake")
        store.store("second-fake")
        assertEquals("second-fake", store.retrieve())
    }
}
