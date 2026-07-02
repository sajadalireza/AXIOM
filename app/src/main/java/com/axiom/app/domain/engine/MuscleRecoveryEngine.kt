package com.axiom.app.domain.engine

object MuscleRecoveryEngine {
    fun calculateFreshness(lastTrainedTimestamp: Long?): Int {
        if (lastTrainedTimestamp == null) return 100
        val hoursSince = (System.currentTimeMillis() - lastTrainedTimestamp) / 3_600_000.0
        return when {
            hoursSince < 24  -> 30   // still recovering, not ready
            hoursSince < 48  -> 70   // mostly recovered
            hoursSince < 96  -> 100  // fully fresh, optimal training window
            hoursSince < 168 -> 80   // still fine but window closing
            else -> 50               // neglected — losing progress, not "fresh," needs attention
        }
    }

    fun calculateStrengthGain(currentScore: Int, sessionQuality: Double): Int {
        val gain = (sessionQuality * 3).toInt().coerceIn(1, 4)
        return (currentScore + gain).coerceAtMost(100)
    }
}
