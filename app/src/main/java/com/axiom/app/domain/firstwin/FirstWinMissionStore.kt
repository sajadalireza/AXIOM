package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Mission

/** WP-207 narrow idempotent Mission write port. */
interface FirstWinMissionStore {
    suspend fun insertIfAbsent(mission: Mission): Mission
}
