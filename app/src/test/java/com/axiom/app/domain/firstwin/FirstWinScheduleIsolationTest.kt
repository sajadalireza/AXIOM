package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.ScheduleBlock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirstWinScheduleIsolationTest {
    private fun block(tag: String) = ScheduleBlock(
        id = "schedule-1",
        trackId = null,
        startTime = "09:00",
        title = "Review one page",
        actionDescription = "Review one page",
        tag = tag,
        recurrence = "DAILY",
        isNonNegotiable = false,
    )

    @Test fun firstWinOneShot_isNotMaterializedAgainByDailyGenerator() {
        assertFalse(shouldGenerateMissionFromScheduleBlock(block(FIRST_WIN_SCHEDULE_TAG)))
    }

    @Test fun legacySchedule_stillMaterializesNormally() {
        assertTrue(shouldGenerateMissionFromScheduleBlock(block("Foundation")))
    }
}
