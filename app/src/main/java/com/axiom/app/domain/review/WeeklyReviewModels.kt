package com.axiom.app.domain.review

enum class ObstacleCategory(val titleEn: String, val titleFa: String) {
    TIME_SLIPPAGE(
        titleEn = "Time Underestimation",
        titleFa = "تخمین نادرست زمان"
    ),
    PRIORITY_DRIFT(
        titleEn = "Priority Drift",
        titleFa = "انحراف از اولویت اصلی"
    ),
    ENERGY_DEPLETION(
        titleEn = "Energy & Fatigue Limits",
        titleFa = "کاهش سطح انرژی و خستگی"
    ),
    EXTERNAL_BLOCKER(
        titleEn = "External Dependencies",
        titleFa = "موانع و وابستگی‌های بیرونی"
    ),
    CLARITY_GAP(
        titleEn = "Ambiguous Next Step",
        titleFa = "ابهام در اقدام بعدی"
    )
}

data class ReviewProgressSnapshot(
    val cycleWeek: Int,
    val totalMissionsCompleted: Int,
    val goalContributingMissions: Int,
    val totalEffectiveHours: Float,
    val wmpuAchieved: Boolean,
    val activeStreak: Int
)

data class NextWeekCommitment(
    val primaryOutcome: String,
    val implementationTrigger: String,
    val targetCadenceDays: Int = 5
)

data class WeeklyReviewEvaluation(
    val completenessScore: Float, // 0.0 to 1.0
    val progressCorrelationScore: Float, // 0.0 to 1.0
    val isWmpuAchieved: Boolean,
    val reviewXpAward: Int = 50 // Flat non-inflated award
)
