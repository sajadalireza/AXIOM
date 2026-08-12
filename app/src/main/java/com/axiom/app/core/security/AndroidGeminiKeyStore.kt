package com.axiom.app.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * WP-104 SEC-104-003 — [GeminiKeyStore] backed by an AndroidKeyStore AES/GCM key.
 *
 * - The AES key is generated and held non-exportable inside the AndroidKeyStore
 *   (alias [KEY_ALIAS]); it never leaves the secure element / keystore.
 * - The Gemini credential is stored only as GCM ciphertext (12-byte IV prefix +
 *   ciphertext) inside [Context.getNoBackupFilesDir], so it is app-private and
 *   excluded from backups. Plaintext is never persisted or logged.
 * - Any crypto/IO failure fails closed (returns null / no-op) rather than throwing
 *   into the UI, and never surfaces the key value.
 */
class AndroidGeminiKeyStore(context: Context) : GeminiKeyStore {

    private val appContext = context.applicationContext
    private val blob: File get() = File(appContext.noBackupFilesDir, BLOB_NAME)

    override fun store(key: String) {
        val cipher = Cipher.getInstance(TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = cipher.iv
        val ct = cipher.doFinal(key.toByteArray(Charsets.UTF_8))
        blob.writeBytes(iv + ct)
    }

    override fun retrieve(): String? = try {
        if (!blob.exists()) null else {
            val bytes = blob.readBytes()
            if (bytes.size <= IV_LEN) null else {
                val iv = bytes.copyOfRange(0, IV_LEN)
                val ct = bytes.copyOfRange(IV_LEN, bytes.size)
                val cipher = Cipher.getInstance(TRANSFORM)
                cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_BITS, iv))
                String(cipher.doFinal(ct), Charsets.UTF_8)
            }
        }
    } catch (e: Exception) {
        null // fail closed
    }

    override fun hasKey(): Boolean = blob.exists()

    override fun clear() {
        runCatching { if (blob.exists()) blob.delete() }
    }

    private fun secretKey(): SecretKey {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (ks.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        gen.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return gen.generateKey()
    }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "axiom_gemini_key_v1"
        const val TRANSFORM = "AES/GCM/NoPadding"
        const val BLOB_NAME = "gemini_key.bin"
        const val IV_LEN = 12
        const val TAG_BITS = 128
    }
}
