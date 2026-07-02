package com.axiom.app.domain.repository

import kotlinx.coroutines.flow.Flow

sealed class ActivationResult {
    object Success : ActivationResult()
    data class Error(val message: String) : ActivationResult()
}

interface ActivationRepository {
    fun isActivated(): Flow<Boolean>
    suspend fun loginWithEmailPassword(email: String, password: String): ActivationResult
    suspend fun signUpWithEmailPassword(email: String, password: String): ActivationResult
    suspend fun loginWithGoogle(idToken: String): ActivationResult
    fun hasSupabaseCredentials(): Boolean
    suspend fun reVerifyOnBoot(): Boolean
    suspend fun preRegisterLeague(email: String): ActivationResult

    /**
     * Silently establishes a Supabase anonymous session if none exists
     * yet. Safe to call on every app start — no-ops if a session
     * (anonymous or upgraded) is already present. Never blocks app
     * entry; failures are swallowed.
     */
    suspend fun ensureAnonymousSession()

    /**
     * Converts the current anonymous session into a real email account
     * in place, preserving the same user_id and League history. Falls
     * back to a normal sign-up if no anonymous session exists yet.
     */
    suspend fun upgradeAnonymousToEmail(email: String, password: String): ActivationResult
}
