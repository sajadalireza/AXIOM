package com.axiom.app.domain.model

sealed class WorkoutTemplate(
    val nameKey: String,
    val muscleWeights: Map<String, Float>
) {
    object Push : WorkoutTemplate(
        nameKey = "workout_push",
        muscleWeights = mapOf("chest" to 1.0f, "shoulders" to 0.8f, "triceps" to 0.7f)
    )

    object Pull : WorkoutTemplate(
        nameKey = "workout_pull",
        muscleWeights = mapOf("back" to 1.0f, "biceps" to 0.8f, "forearms" to 0.5f)
    )

    object Legs : WorkoutTemplate(
        nameKey = "workout_legs",
        muscleWeights = mapOf("quads" to 1.0f, "hamstrings" to 0.9f, "glutes" to 0.9f, "calves" to 0.6f)
    )

    object FullBody : WorkoutTemplate(
        nameKey = "workout_full_body",
        muscleWeights = mapOf(
            "chest" to 0.5f, "back" to 0.5f, "shoulders" to 0.5f, "biceps" to 0.5f, "triceps" to 0.5f,
            "forearms" to 0.5f, "core" to 0.5f, "glutes" to 0.5f, "quads" to 0.5f, "hamstrings" to 0.5f, "calves" to 0.5f
        )
    )

    object RunCardio : WorkoutTemplate(
        nameKey = "workout_run_cardio",
        muscleWeights = mapOf("quads" to 0.4f, "hamstrings" to 0.4f, "calves" to 0.6f, "core" to 0.3f)
    )

    class Custom(customWeights: Map<String, Float>) : WorkoutTemplate(
        nameKey = "workout_custom",
        muscleWeights = customWeights
    )
}
