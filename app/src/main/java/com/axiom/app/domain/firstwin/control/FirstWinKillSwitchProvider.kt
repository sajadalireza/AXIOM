package com.axiom.app.domain.firstwin.control

/**
 * WP-208 — contract governing local and remote kill switch boundaries.
 */
interface FirstWinKillSwitchProvider {
    suspend fun isLocalKilled(): Boolean
    suspend fun isRemoteKilled(): Boolean
    suspend fun isKillSwitchActive(): Boolean = isLocalKilled() || isRemoteKilled()
}
