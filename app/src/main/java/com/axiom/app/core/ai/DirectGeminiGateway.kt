package com.axiom.app.core.ai

/**
 * WP-104 SEC-104-001 — single choke point for the direct BYO-Gemini path.
 *
 * Every direct-provider call site must run its `GenerativeModel` construction and
 * `generateContent` invocation *inside* [withDirectGemini]. The provider lambda is
 * invoked ONLY when [AiEgressPolicy] permits direct egress AND a non-blank key is
 * present; otherwise the lambda is never touched and the caller falls back to a
 * safe local state. This makes an accidental ungated egress (the SEC-104-001
 * defect) fail closed by construction rather than relying on each caller to
 * remember an ad-hoc guard.
 */
object DirectGeminiGateway {

    /**
     * Runs [providerCall] only when direct Gemini egress is allowed and [apiKey] is
     * usable. Returns `null` (caller should fall back) when egress is disabled or no
     * key exists — in that case no key value is passed on and the provider is never
     * constructed or contacted.
     */
    suspend fun <T> withDirectGemini(
        apiKey: String?,
        providerCall: suspend (key: String) -> T
    ): T? {
        if (!AiEgressPolicy.isDirectGeminiAllowed()) return null
        val key = apiKey?.takeIf { it.isNotBlank() } ?: return null
        return providerCall(key)
    }
}
