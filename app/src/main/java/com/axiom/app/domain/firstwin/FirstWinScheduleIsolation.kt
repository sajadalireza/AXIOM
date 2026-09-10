package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.ScheduleBlock

const val FIRST_WIN_SCHEDULE_TAG = "FirstWin"

internal fun shouldGenerateMissionFromScheduleBlock(block: ScheduleBlock): Boolean =
    block.tag != FIRST_WIN_SCHEDULE_TAG
