package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.ScheduleBlock

/** WP-207 narrow idempotent write port for the existing Room v18 schedule_blocks table. */
interface FirstWinScheduleStore {
    suspend fun insertIfAbsent(block: ScheduleBlock): ScheduleBlock
}
