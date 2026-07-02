package com.axiom.app.domain.model

data class AIMissionSuggestion(
    val title: String,
    val description: String,
    val skillName: String,
    val estimatedHours: Float,
    val rarity: String,
    val reasoning: String
)
