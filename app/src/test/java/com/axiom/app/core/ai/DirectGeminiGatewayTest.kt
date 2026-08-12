package com.axiom.app.core.ai

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WP-104 SEC-104-001 regression — proves the shared [DirectGeminiGateway] boundary
 * that the analytics call path (and any future direct caller) runs through actually
 * fail-closes the provider. Unlike the prior policy-object-only test, this exercises
 * the real invocation seam: it counts provider invocations and asserts ZERO when
 * egress is disabled. If someone removes the gate, `providerInvocations` becomes 1
 * in the disabled case and this test fails.
 */
class DirectGeminiGatewayTest {

    @After fun tearDown() = AiEgressPolicy.resetForTest()

    @Test fun disabled_neverInvokesProvider_evenWithKey() = runBlocking {
        AiEgressPolicy.setAllowedForTest { false }
        var providerInvocations = 0
        val result = DirectGeminiGateway.withDirectGemini("AIzaSyFAKE-not-real") { key ->
            providerInvocations++
            "SHOULD-NOT-HAPPEN:$key"
        }
        assertEquals("provider must not be contacted while egress is disabled", 0, providerInvocations)
        assertNull(result)
    }

    @Test fun disabled_isTheProductionDefault() = runBlocking {
        // No override: reads FeatureFlags.DIRECT_GEMINI_EGRESS_ENABLED (false by default).
        var providerInvocations = 0
        val result = DirectGeminiGateway.withDirectGemini("AIzaSyFAKE-not-real") { _ ->
            providerInvocations++; "x"
        }
        assertEquals(0, providerInvocations)
        assertNull(result)
    }

    @Test fun enabled_reachesProviderWithFake_noLiveCall() = runBlocking {
        AiEgressPolicy.setAllowedForTest { true }
        var providerInvocations = 0
        var seenKey: String? = null
        val result = DirectGeminiGateway.withDirectGemini("AIzaSyFAKE-not-real") { key ->
            providerInvocations++
            seenKey = key
            "fake-summary"
        }
        assertEquals(1, providerInvocations)
        assertEquals("AIzaSyFAKE-not-real", seenKey)
        assertEquals("fake-summary", result)
    }

    @Test fun enabled_butBlankKey_neverInvokesProvider() = runBlocking {
        AiEgressPolicy.setAllowedForTest { true }
        var providerInvocations = 0
        val result = DirectGeminiGateway.withDirectGemini("   ") { _ ->
            providerInvocations++; "x"
        }
        assertEquals(0, providerInvocations)
        assertNull(result)
    }

    @Test fun requireGuard_throwsWhenDisabled_passesWhenEnabled() {
        AiEgressPolicy.setAllowedForTest { false }
        var threw = false
        try { AiEgressPolicy.requireDirectGeminiAllowed() } catch (e: DirectAiEgressDisabledException) { threw = true }
        assertTrue(threw)
        AiEgressPolicy.setAllowedForTest { true }
        AiEgressPolicy.requireDirectGeminiAllowed() // no throw
    }
}
