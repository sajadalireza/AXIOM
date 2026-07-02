package com.axiom.app.domain.model

enum class MasteryTier(val minHours: Double, val label: String) {
    NOVICE(0.0, "Novice"),
    PRACTITIONER(50.0, "Practitioner"),
    PROFICIENT(300.0, "Proficient"),
    SKILLED(1000.0, "Skilled"),
    EXPERT(3000.0, "Expert"),
    MASTER(7000.0, "Master");

    companion object {
        fun fromHours(hours: Double): MasteryTier =
            entries.sortedByDescending { it.minHours }
                .first { hours >= it.minHours }
    }
}
