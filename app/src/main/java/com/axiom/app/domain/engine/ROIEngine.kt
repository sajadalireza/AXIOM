package com.axiom.app.domain.engine

object ROIEngine {

    fun calculatePowerScore(
        marketDemand: Float,
        leverage: Float,
        complexity: Float,
        estimatedHours: Float
    ): Float {
        val base = (marketDemand * 0.3f) + (leverage * 0.3f) + (complexity * 0.4f)
        val divisor = if (estimatedHours > 0) Math.sqrt(estimatedHours.toDouble()).toFloat() else 1f
        return (base / divisor).coerceIn(1.0f, 10.0f)
    }

    fun classifyRarity(powerScore: Float): String {
        return when {
            powerScore < 2.5f -> "FOUNDATION"
            powerScore < 4.5f -> "COMPOUND"
            powerScore < 7.0f -> "CRITICAL"
            powerScore < 9.0f -> "SHIELD"
            else -> "DEPTH"
        }
    }

    fun toMissionSuggestion(powerScore: Float, baseXP: Int): MissionSuggestion {
        val successChance = (100 - (powerScore * 8f).toInt()).coerceIn(15, 95)
        val rewardPotential = when {
            powerScore < 3.0f -> "Low"
            powerScore < 5.5f -> "Medium"
            powerScore < 8.0f -> "High"
            else -> "EX-Rank"
        }
        return MissionSuggestion(
            successChance = successChance,
            rewardPotential = rewardPotential,
            estimatedXP = baseXP
        )
    }
}

data class MissionSuggestion(
    val successChance: Int,
    val rewardPotential: String,
    val estimatedXP: Int
)
