package com.axiom.app.domain.model

data class MajorMilestone(
    val id: String,
    val label: String,
    val targetDate: Long, // Epoch timestamp in milliseconds
    val description: String
)
