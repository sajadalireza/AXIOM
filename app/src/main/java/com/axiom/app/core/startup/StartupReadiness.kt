package com.axiom.app.core.startup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authoritative startup-readiness signal.
 *
 * WP-201: launch routing must not read startup state until bootstrap/seeding
 * has settled. This is a state signal, NOT a clock — it decouples route
 * correctness from Splash animation timing. The owner (MainActivity) calls
 * [markReady] once (in a finally) after the seeding coroutine finishes, which
 * guarantees liveness without any fixed delay or timeout.
 */
@Singleton
class StartupReadiness @Inject constructor() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    /** Idempotent; safe to call from a finally block. */
    fun markReady() {
        _isReady.value = true
    }

    /** Suspends until startup state is authoritative. Returns immediately if already ready. */
    suspend fun await() {
        isReady.first { it }
    }
}
