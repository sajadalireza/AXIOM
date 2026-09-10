package com.axiom.app.domain.firstwin.control

/**
 * WP-208 — central control plane for First-Win rollout, variant assignment,
 * and kill boundaries under Gate G2.
 *
 * Invariants:
 *  - [CURRENT_ELIGIBILITY_VERSION] = 1 (pinned eligibility version).
 *  - Sticky assignment: once a variant is assigned, it is durably persisted in
 *    DataStore; subsequent calls return the same variant (assignment drift = 0).
 *  - Kill boundary: if either local or remote kill is active, [isTreatmentActive]
 *    evaluates to false.
 */
interface FirstWinControlPlane : FirstWinKillSwitchProvider {

    companion object {
        const val CURRENT_ELIGIBILITY_VERSION = 1
    }

    /**
     * Obtains the sticky variant for this installation. If no variant has been
     * assigned yet, assigns the default variant ([defaultVariant]), stores it
     * alongside [CURRENT_ELIGIBILITY_VERSION] and the assignment timestamp, and returns it.
     */
    suspend fun getOrAssignVariant(defaultVariant: FirstWinVariant = FirstWinVariant.TREATMENT): FirstWinVariant

    /**
     * Determines whether the First-Win treatment is actively enabled and authorized
     * to render for the user. Evaluates to true ONLY when:
     *   1. The local kill switch is NOT active.
     *   2. The remote kill switch is NOT active.
     *   3. The assigned variant is [FirstWinVariant.TREATMENT].
     */
    suspend fun isTreatmentActive(): Boolean

    /**
     * Captures a point-in-time snapshot of the control plane state.
     */
    suspend fun getSnapshot(): FirstWinControlSnapshot

    /**
     * Dynamically updates the remote kill switch state (e.g. from remote config or test harness).
     */
    suspend fun setRemoteKill(active: Boolean)

    /**
     * Locally updates the build-level kill switch override.
     */
    fun setLocalKill(active: Boolean)
}
