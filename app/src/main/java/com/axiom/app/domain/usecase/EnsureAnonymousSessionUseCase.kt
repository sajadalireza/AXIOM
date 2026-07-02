package com.axiom.app.domain.usecase

import com.axiom.app.domain.repository.ActivationRepository
import javax.inject.Inject

/**
 * Silently establishes a Supabase anonymous session on first launch.
 * - No-ops if a session (anonymous or upgraded) already exists.
 * - Delegates to ActivationRepository which handles the actual Supabase call.
 * - Never blocks the splash screen — always called fire-and-forget.
 * - Failures are swallowed: core app must work without a network session.
 */
class EnsureAnonymousSessionUseCase @Inject constructor(
    private val activationRepository: ActivationRepository
) {
    suspend operator fun invoke() {
        try {
            activationRepository.ensureAnonymousSession()
        } catch (e: Exception) {
            // Non-fatal. If the anonymous session can't be created
            // (no network, Supabase down), the app continues normally.
            // League submissions will simply no-op until a session exists.
            e.printStackTrace()
        }
    }
}
