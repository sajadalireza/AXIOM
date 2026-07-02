package com.axiom.app.domain.model

enum class CalibrationType {
    TRUTH, AFFIRMATION
}

data class HardTruthOrAffirmation(
    val id: String,
    val type: CalibrationType,
    val text: String,
    val orderIndex: Int
)
