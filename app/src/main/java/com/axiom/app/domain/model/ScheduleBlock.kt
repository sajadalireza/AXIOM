package com.axiom.app.domain.model

data class ScheduleBlock(
    val id: String,
    val trackId: String?,
    val startTime: String, // e.g. "08:30"
    val title: String,
    val actionDescription: String,
    val tag: String, // "Foundation", "Critical", "Shield", "Rest"
    val recurrence: String, // "DAILY" or days like "MONDAY,WEDNESDAY,FRIDAY"
    val isNonNegotiable: Boolean
)
