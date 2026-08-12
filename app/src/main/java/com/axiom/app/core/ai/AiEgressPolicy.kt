package com.axiom.app.core.ai

import androidx.annotation.VisibleForTesting
import com.axiom.app.core.FeatureFlags

/**
 * WP-104 SEC-104-001 — explicit egress boundary between AXIOM product logic and the
 * direct Gemini client.
 *
 * Default behaviour is DISABLED and fail-closed: unless [FeatureFlags.DIRECT_GEMINI_EGRESS_ENABLED]
 * is explicitly true, no direct Gemini network request, key lookup, or prompt transmission
 * may occur. Callers must gate every direct-provider entry point through this policy so the
 * default/production build never silently egresses user data to Google.
 *
 * This is a bounded containment measure; a full server-gateway / Firebase App Check
 * migration is intentionally deferred.
 */
object AiEgressPolicy {

    // Single source of truth. Production always reads the compile-time flag; the seam
    // below exists ONLY so tests can exercise the allowed path without a second flag or
    // parallel security mechanism (WP-104 SEC-104-001 regression coverage).
    @Volatile
    private var allowedProvider: () -> Boolean = { FeatureFlags.DIRECT_GEMINI_EGRESS_ENABLED }

    /** True only when the direct BYO-key Gemini path is explicitly permitted. */
    fun isDirectGeminiAllowed(): Boolean = allowedProvider()

    /**
     * Fail-closed guard. Throws before any key lookup / network call when direct
     * egress is disabled, so a disabled build cannot transmit anything.
     */
    fun requireDirectGeminiAllowed() {
        if (!isDirectGeminiAllowed()) throw DirectAiEgressDisabledException()
    }

    /** Test-only override of the egress decision. Not for production use. */
    @VisibleForTesting
    fun setAllowedForTest(provider: () -> Boolean) {
        allowedProvider = provider
    }

    /** Restores the production decision (reads [FeatureFlags.DIRECT_GEMINI_EGRESS_ENABLED]). */
    @VisibleForTesting
    fun resetForTest() {
        allowedProvider = { FeatureFlags.DIRECT_GEMINI_EGRESS_ENABLED }
    }
}

/** Raised when a direct Gemini call is attempted while egress is disabled by policy. */
class DirectAiEgressDisabledException :
    Exception("Direct Gemini egress is disabled by AiEgressPolicy.")
