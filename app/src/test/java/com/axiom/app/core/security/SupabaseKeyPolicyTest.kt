package com.axiom.app.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

/** WP-104 SEC-104-002 tests. Uses only synthetic fixtures — no real credential. */
class SupabaseKeyPolicyTest {

    private fun jwt(role: String): String {
        val enc = Base64.getUrlEncoder().withoutPadding()
        val h = enc.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray())
        val p = enc.encodeToString("""{"role":"$role","iss":"supabase"}""".toByteArray())
        return "$h.$p.c2lnbmF0dXJlX3BsYWNlaG9sZGVy"
    }

    @Test fun emptyAllowed() {
        assertEquals(SupabaseKeyPolicy.KeyClass.EMPTY, SupabaseKeyPolicy.classify(""))
        assertTrue(SupabaseKeyPolicy.isClientSafe(""))
        assertTrue(SupabaseKeyPolicy.isClientSafe(null))
    }

    @Test fun publishableAllowed() {
        assertEquals(SupabaseKeyPolicy.KeyClass.PUBLISHABLE, SupabaseKeyPolicy.classify("sb_publishable_ABC123def"))
        assertTrue(SupabaseKeyPolicy.isClientSafe("sb_publishable_ABC123def"))
    }

    @Test fun legacyAnonAllowed() {
        assertEquals(SupabaseKeyPolicy.KeyClass.LEGACY_ANON, SupabaseKeyPolicy.classify(jwt("anon")))
        assertTrue(SupabaseKeyPolicy.isClientSafe(jwt("anon")))
    }

    @Test fun secretRejected() {
        assertEquals(SupabaseKeyPolicy.KeyClass.SECRET, SupabaseKeyPolicy.classify("sb_secret_ABC123def"))
        assertFalse(SupabaseKeyPolicy.isClientSafe("sb_secret_ABC123def"))
    }

    @Test fun serviceRoleRejected() {
        assertEquals(SupabaseKeyPolicy.KeyClass.SERVICE_ROLE, SupabaseKeyPolicy.classify(jwt("service_role")))
        assertFalse(SupabaseKeyPolicy.isClientSafe(jwt("service_role")))
    }

    @Test fun malformedRejected() {
        assertEquals(SupabaseKeyPolicy.KeyClass.MALFORMED, SupabaseKeyPolicy.classify("not-a-key"))
        assertFalse(SupabaseKeyPolicy.isClientSafe("not-a-key"))
    }

    @Test fun unknownRoleRejected() {
        assertEquals(SupabaseKeyPolicy.KeyClass.UNKNOWN, SupabaseKeyPolicy.classify(jwt("editor")))
    }

    @Test fun errorTextNeverContainsKey() {
        val secret = "sb_secret_SUPERSENSITIVEVALUE999"
        val ex = runCatching { SupabaseKeyPolicy.assertClientSafe(secret) }.exceptionOrNull()
        assertTrue(ex is IllegalStateException)
        assertFalse(ex!!.message!!.contains("SUPERSENSITIVEVALUE999"))
        assertFalse(ex.message!!.contains(secret))
    }
}
