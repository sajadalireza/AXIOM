package com.axiom.app.presentation.bodymap

import com.axiom.app.domain.model.MuscleGroup
import kotlin.math.roundToInt

enum class BodyMapSex {
    Male,
    Female
}

enum class BodyMapView {
    Front,
    Back
}

enum class BodyMapDisplayMode {
    StrengthBalance,
    MusclesInvolved,
    RecoveryReadiness
}

enum class BodyMapRegionRole {
    Neutral,
    Primary,
    Secondary
}

enum class StrengthTrendDirection {
    Up,
    Flat,
    Down
}

fun strengthTrendDirection(delta: Int): StrengthTrendDirection =
    when {
        delta > 0 -> StrengthTrendDirection.Up
        delta < 0 -> StrengthTrendDirection.Down
        else -> StrengthTrendDirection.Flat
    }

data class BodyMapAnatomyProfile(
    val sex: BodyMapSex,
    val heightSpan: Float,
    val armSpan: Float,
    val handSpan: Float,
    val shoulderSpan: Float,
    val chestSpan: Float,
    val waistSpan: Float,
    val pelvisSpan: Float,
    val legHorizontalSpan: Float,
    val kneeSpan: Float,
    val calfSpan: Float,
    val footSpan: Float,
    val legSpan: Float
)

data class BodyMapHotspot(
    val muscleId: String,
    val x: Float,
    val y: Float
)

data class BodyMapSelectedSummary(
    val id: String,
    val groupLabel: String,
    val displayName: String,
    val score: Int,
    val freshness: Int,
    val deltaFromAverage: Int
)

object BodyMapAtlasModel {
    private val allMuscleIds = listOf(
        "shoulders",
        "chest",
        "back",
        "biceps",
        "triceps",
        "forearms",
        "core",
        "legs"
    )

    fun profileFor(sex: BodyMapSex): BodyMapAnatomyProfile =
        when (sex) {
            BodyMapSex.Male -> BodyMapAnatomyProfile(
                sex = sex,
                heightSpan = 1.00f,
                armSpan = 1.040f,
                handSpan = 1.060f,
                shoulderSpan = 1.000f,
                chestSpan = 0.917f,
                waistSpan = 0.93f,
                pelvisSpan = 1.084f,
                legHorizontalSpan = 0.946f,
                kneeSpan = 1.091f,
                calfSpan = 0.926f,
                footSpan = 0.904f,
                legSpan = 1.00f
            )
            BodyMapSex.Female -> BodyMapAnatomyProfile(
                sex = sex,
                heightSpan = 0.972f,
                armSpan = 0.800f,
                handSpan = 0.87612f,
                shoulderSpan = 0.732f,
                chestSpan = 0.806f,
                waistSpan = 0.87f,
                pelvisSpan = 1.244f,
                legHorizontalSpan = 0.718f,
                kneeSpan = 0.843f,
                calfSpan = 0.704f,
                footSpan = 0.702f,
                legSpan = 1.00f
            )
        }

    fun hotspotsFor(view: BodyMapView): List<BodyMapHotspot> =
        if (view == BodyMapView.Front) {
            listOf(
                BodyMapHotspot("shoulders", 58f, 122f),
                BodyMapHotspot("shoulders", 182f, 122f),
                BodyMapHotspot("chest", 92f, 137f),
                BodyMapHotspot("chest", 148f, 137f),
                BodyMapHotspot("biceps", 38f, 180f),
                BodyMapHotspot("biceps", 202f, 180f),
                BodyMapHotspot("forearms", 18f, 266f),
                BodyMapHotspot("forearms", 222f, 266f),
                BodyMapHotspot("core", 120f, 218f),
                BodyMapHotspot("legs", 87f, 350f),
                BodyMapHotspot("legs", 153f, 350f),
                BodyMapHotspot("legs", 88f, 470f),
                BodyMapHotspot("legs", 152f, 470f)
            )
        } else {
            listOf(
                BodyMapHotspot("shoulders", 82f, 116f),
                BodyMapHotspot("shoulders", 158f, 116f),
                BodyMapHotspot("back", 92f, 174f),
                BodyMapHotspot("back", 148f, 174f),
                BodyMapHotspot("triceps", 38f, 194f),
                BodyMapHotspot("triceps", 202f, 194f),
                BodyMapHotspot("core", 120f, 248f),
                BodyMapHotspot("legs", 87f, 350f),
                BodyMapHotspot("legs", 153f, 350f),
                BodyMapHotspot("legs", 88f, 470f),
                BodyMapHotspot("legs", 152f, 470f)
            )
        }

    fun selectedSummary(
        muscles: List<MuscleGroup>,
        selectedMuscleId: String?
    ): BodyMapSelectedSummary? {
        val measuredMuscles = measuredStrengthMuscles(muscles)
        val muscle = if (selectedMuscleId != null) {
            measuredMuscles.firstOrNull { it.id == selectedMuscleId } ?: return null
        } else {
            measuredMuscles.maxByOrNull { it.strengthScore } ?: return null
        }
        val average = measuredMuscles.map { it.strengthScore }.average()
        return BodyMapSelectedSummary(
            id = muscle.id,
            groupLabel = groupLabelFor(muscle.id),
            displayName = muscle.displayName,
            score = muscle.strengthScore,
            freshness = muscle.freshnessPercent,
            deltaFromAverage = (muscle.strengthScore - average).roundToInt()
        )
    }

    fun isStrengthMeasured(muscle: MuscleGroup): Boolean =
        muscle.lastTrainedTimestamp != null || muscle.strengthScore != 0

    fun measuredStrengthMuscles(muscles: List<MuscleGroup>): List<MuscleGroup> =
        muscles.filter(::isStrengthMeasured)

    fun hasMeasuredStrengthData(muscles: List<MuscleGroup>): Boolean =
        measuredStrengthMuscles(muscles).isNotEmpty()

    fun topPercentFor(
        muscles: List<MuscleGroup>,
        selectedScore: Int
    ): Int? {
        val measuredMuscles = measuredStrengthMuscles(muscles)
        if (measuredMuscles.size < 2 || measuredMuscles.map { it.strengthScore }.distinct().size < 2) {
            return null
        }
        val sharedRank = measuredMuscles.count { it.strengthScore > selectedScore } + 1
        return ((sharedRank * 100f) / measuredMuscles.size)
            .toInt()
            .coerceIn(1, 100)
    }

    fun defaultSelectedFor(
        muscles: List<MuscleGroup>,
        mode: BodyMapDisplayMode
    ): String? =
        when (mode) {
            BodyMapDisplayMode.StrengthBalance -> {
                val measuredMuscles = measuredStrengthMuscles(muscles)
                measuredMuscles.firstOrNull { it.id == "biceps" }?.id
                    ?: measuredMuscles.minByOrNull { it.strengthScore }?.id
                    ?: muscles.firstOrNull { it.id == "biceps" }?.id
                    ?: muscles.firstOrNull()?.id
            }
            BodyMapDisplayMode.MusclesInvolved ->
                muscles.firstOrNull { it.id == "chest" }?.id
                    ?: muscles.maxByOrNull { it.strengthScore }?.id
            BodyMapDisplayMode.RecoveryReadiness ->
                muscles.minByOrNull { it.freshnessPercent }?.id
        }

    fun involvementRoles(
        primaryMuscleId: String,
        secondaryMuscleIds: List<String>
    ): Map<String, BodyMapRegionRole> =
        allMuscleIds.associateWith { id ->
            when {
                id == primaryMuscleId -> BodyMapRegionRole.Primary
                id in secondaryMuscleIds -> BodyMapRegionRole.Secondary
                else -> BodyMapRegionRole.Neutral
            }
        }

    fun secondaryForPrimary(primaryMuscleId: String): List<String> =
        when (primaryMuscleId) {
            "chest" -> listOf("shoulders", "triceps")
            "back" -> listOf("biceps", "forearms")
            "shoulders" -> listOf("chest", "triceps")
            "biceps" -> listOf("back", "forearms")
            "triceps" -> listOf("chest", "shoulders")
            "forearms" -> listOf("biceps", "back")
            "core" -> listOf("legs", "back")
            "legs" -> listOf("core")
            else -> emptyList()
        }

    fun groupLabelFor(muscleId: String): String =
        when (muscleId) {
            "biceps", "triceps", "forearms" -> "Arms"
            "chest" -> "Chest"
            "back" -> "Back"
            "shoulders" -> "Shoulders"
            "core" -> "Core"
            "legs" -> "Legs"
            else -> "Muscle"
        }
}
