package com.axiom.app.domain.engine

import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.domain.model.WorkoutTemplate
import kotlin.math.max
import kotlin.math.min

object MuscleEngine {

    private const val XP_SCALE_FACTOR = 2.0f

    fun calculateFreshness(lastTrainedAt: Long?, recoveryWindowHours: Int, now: Long): Float {
        if (lastTrainedAt == null) return 100f
        val elapsedMs = now - lastTrainedAt
        if (elapsedMs <= 0) return 0f
        val elapsedHours = elapsedMs / 3600000f
        val freshness = (elapsedHours / recoveryWindowHours) * 100f
        return freshness.coerceIn(0f, 100f)
    }

    fun freshnessBand(freshness: Float): String {
        return when {
            freshness < 50f -> "FATIGUED"
            freshness < 80f -> "RECOVERING"
            else -> "FRESH"
        }
    }

    fun calculateMuscleRank(strengthXP: Long): String {
        return XPEngine.calculateSkillRank(strengthXP)
    }

    fun applyWorkoutToMuscles(
        muscleGroups: List<MuscleGroup>,
        template: WorkoutTemplate,
        durationMinutes: Int,
        now: Long
    ): List<MuscleGroup> {
        val weights = template.muscleWeights
        return muscleGroups.map { muscle ->
            val weight = weights[muscle.id]
            if (weight != null && weight > 0f) {
                val scoreGain = MuscleRecoveryEngine.calculateStrengthGain(muscle.strengthScore, weight.toDouble()) - muscle.strengthScore
                muscle.copy(
                    lastTrainedTimestamp = now,
                    strengthScore = (muscle.strengthScore + scoreGain).coerceIn(0, 100),
                    freshnessPercent = MuscleRecoveryEngine.calculateFreshness(now)
                )
            } else {
                muscle
            }
        }
    }
}
