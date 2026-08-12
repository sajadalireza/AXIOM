package com.axiom.app.core.security

/**
 * WP-104 SEC-104-003 — secure at-rest storage for the user-provided (BYO) Gemini key.
 *
 * The plaintext key must never be persisted; only authenticated ciphertext encrypted
 * with an AndroidKeyStore-backed AES/GCM key may be written, into app-private,
 * non-backed-up storage. All operations fail closed.
 */
interface GeminiKeyStore {
    /** Encrypts and persists [key]. Overwrites any existing value. */
    fun store(key: String)

    /** Returns the decrypted key, or null if absent or on any failure (fail closed). */
    fun retrieve(): String?

    /** True if an encrypted key blob is present. */
    fun hasKey(): Boolean

    /** Removes the encrypted key blob. */
    fun clear()
}
