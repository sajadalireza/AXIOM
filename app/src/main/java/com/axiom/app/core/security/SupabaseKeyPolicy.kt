package com.axiom.app.core.security

import java.util.Base64

/**
 * WP-104 SEC-104-002 — fail-closed classifier for the Supabase client credential.
 *
 * Client-side AXIOM builds may only ship a PUBLIC client key (modern
 * `sb_publishable_*`, or a legacy JWT whose role is exactly `anon`). Elevated or
 * unrecognized non-empty key material (`sb_secret_*`, legacy `service_role` JWT,
 * malformed) is rejected. JWT payload decoding here is LOCAL CONFIGURATION
 * CLASSIFICATION ONLY — never authentication verification. The key value is never
 * logged or returned; only its class/fingerprint is safe to surface.
 */
object SupabaseKeyPolicy {

    enum class KeyClass(val allowedInClient: Boolean) {
        EMPTY(true),          // dev/default build with no Supabase backend
        PUBLISHABLE(true),    // sb_publishable_*
        LEGACY_ANON(true),    // legacy JWT role=anon
        SECRET(false),        // sb_secret_*
        SERVICE_ROLE(false),  // legacy JWT role=service_role
        UNKNOWN(false),       // JWT with unrecognized/absent role
        MALFORMED(false)      // non-empty, not a recognized key shape
    }

    private val ROLE = Regex("\"role\"\\s*:\\s*\"([^\"]+)\"")

    fun classify(rawKey: String?): KeyClass {
        val key = rawKey?.trim().orEmpty()
        if (key.isEmpty()) return KeyClass.EMPTY
        if (key.startsWith("sb_publishable_")) return KeyClass.PUBLISHABLE
        if (key.startsWith("sb_secret_")) return KeyClass.SECRET
        val parts = key.split(".")
        if (parts.size == 3) {
            val role = decodeRole(parts[1]) ?: return KeyClass.UNKNOWN
            return when (role) {
                "anon" -> KeyClass.LEGACY_ANON
                "service_role" -> KeyClass.SERVICE_ROLE
                else -> KeyClass.UNKNOWN
            }
        }
        return KeyClass.MALFORMED
    }

    fun isClientSafe(rawKey: String?): Boolean = classify(rawKey).allowedInClient

    /** Fail-closed assertion. Error text carries only the class — never the key. */
    fun assertClientSafe(rawKey: String?) {
        val cls = classify(rawKey)
        check(cls.allowedInClient) {
            "Unsafe Supabase client key rejected (class=$cls). " +
                "Only sb_publishable_* or legacy anon JWT may ship in an Android build."
        }
    }

    private fun decodeRole(payloadSegment: String): String? = try {
        val json = String(Base64.getUrlDecoder().decode(padded(payloadSegment)))
        ROLE.find(json)?.groupValues?.get(1)
    } catch (e: Exception) {
        null
    }

    private fun padded(s: String): String = when (s.length % 4) {
        2 -> "$s=="
        3 -> "$s="
        else -> s
    }
}
