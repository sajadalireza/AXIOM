package com.axiom.app.domain.model

data class MuscleGroup(
    val id: String,                  // "chest", "back", etc.
    val displayName: String,
    val strengthScore: Int = 0,           // 0-100, grows with training
    val lastTrainedTimestamp: Long? = null,
    val freshnessPercent: Int = 100       // 0-100, decays over time
) {
    // Compatibility getters for legacy screens:
    val strengthXP: Long get() = strengthScore * 100L
    val recoveryWindowHours: Int get() = 48
    val lastTrainedAt: Long? get() = lastTrainedTimestamp
    val weeklyVolumeCount: Int get() = if (lastTrainedTimestamp != null) 1 else 0
}
