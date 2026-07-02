package com.axiom.app.domain.model

data class CustomKPI(
    val id: String,
    val trackId: String?,
    val name: String,
    val targetValue: Float,
    val targetUnit: String, // e.g. "per week", "per day", "minutes/day"
    val measurementHint: String,
    val redFlagAction: String
)
