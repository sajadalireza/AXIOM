package com.axiom.app.presentation.bodymap

import com.axiom.app.domain.model.MuscleGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BodyMapAtlasModelTest {
    @Test
    fun seededZeroMuscles_areClassifiedAsUnmeasuredWithoutPercentile() {
        val seededMuscles = listOf(
            MuscleGroup(id = "chest", displayName = "Chest"),
            MuscleGroup(id = "biceps", displayName = "Biceps"),
            MuscleGroup(id = "legs", displayName = "Legs")
        )

        assertFalse(BodyMapAtlasModel.hasMeasuredStrengthData(seededMuscles))
        assertTrue(bodyMapUiStateFor(seededMuscles) is BodyMapUiState.Unmeasured)
        assertEquals(null, BodyMapAtlasModel.topPercentFor(seededMuscles, selectedScore = 0))
    }

    @Test
    fun measuredTiedScores_doNotClaimAPercentile() {
        val measuredMuscles = listOf(
            MuscleGroup(id = "chest", displayName = "Chest", strengthScore = 40, lastTrainedTimestamp = 1L),
            MuscleGroup(id = "biceps", displayName = "Biceps", strengthScore = 40, lastTrainedTimestamp = 1L),
            MuscleGroup(id = "legs", displayName = "Legs", strengthScore = 40, lastTrainedTimestamp = 1L)
        )

        assertTrue(BodyMapAtlasModel.hasMeasuredStrengthData(measuredMuscles))
        assertTrue(bodyMapUiStateFor(measuredMuscles) is BodyMapUiState.Success)
        assertEquals(null, BodyMapAtlasModel.topPercentFor(measuredMuscles, selectedScore = 40))
    }

    @Test
    fun topPercentFor_assignsTheSameRankToEqualMeasuredScores() {
        val measuredMuscles = listOf(
            MuscleGroup(id = "chest", displayName = "Chest", strengthScore = 90),
            MuscleGroup(id = "biceps", displayName = "Biceps", strengthScore = 80),
            MuscleGroup(id = "triceps", displayName = "Triceps", strengthScore = 80),
            MuscleGroup(id = "legs", displayName = "Legs", strengthScore = 60)
        )

        assertEquals(50, BodyMapAtlasModel.topPercentFor(measuredMuscles, selectedScore = 80))
    }

    @Test
    fun mixedMeasuredAndSeededMuscles_excludeUnmeasuredZerosFromComparisons() {
        val mixedMuscles = listOf(
            MuscleGroup(id = "chest", displayName = "Chest", strengthScore = 80, lastTrainedTimestamp = 1L),
            MuscleGroup(id = "biceps", displayName = "Biceps"),
            MuscleGroup(id = "legs", displayName = "Legs", strengthScore = 60, lastTrainedTimestamp = 2L)
        )

        val chestSummary = BodyMapAtlasModel.selectedSummary(mixedMuscles, "chest")

        assertTrue(bodyMapUiStateFor(mixedMuscles) is BodyMapUiState.Success)
        assertEquals(listOf("chest", "legs"), BodyMapAtlasModel.measuredStrengthMuscles(mixedMuscles).map { it.id })
        assertEquals(null, BodyMapAtlasModel.selectedSummary(mixedMuscles, "biceps"))
        assertEquals(10, chestSummary?.deltaFromAverage)
        assertEquals(50, BodyMapAtlasModel.topPercentFor(mixedMuscles, selectedScore = 80))
        assertEquals(
            "legs",
            BodyMapAtlasModel.defaultSelectedFor(mixedMuscles, BodyMapDisplayMode.StrengthBalance)
        )
    }

    @Test
    fun strengthTrendDirection_reflectsDeltaSign() {
        assertEquals(StrengthTrendDirection.Up, strengthTrendDirection(4))
        assertEquals(StrengthTrendDirection.Flat, strengthTrendDirection(0))
        assertEquals(StrengthTrendDirection.Down, strengthTrendDirection(-4))
    }

    @Test
    fun profileFor_usesDifferentMaleAndFemaleProportions() {
        val male = BodyMapAtlasModel.profileFor(BodyMapSex.Male)
        val female = BodyMapAtlasModel.profileFor(BodyMapSex.Female)

        assertTrue(male.shoulderSpan > female.shoulderSpan)
        assertTrue(female.pelvisSpan > male.pelvisSpan)
        assertTrue(female.legHorizontalSpan < male.legHorizontalSpan)
        assertEquals(BodyMapSex.Male, male.sex)
        assertEquals(BodyMapSex.Female, female.sex)
    }

    @Test
    fun profileFor_matchesReferenceStatureAndArmSpan() {
        val male = BodyMapAtlasModel.profileFor(BodyMapSex.Male)
        val female = BodyMapAtlasModel.profileFor(BodyMapSex.Female)

        assertEquals(1.000f, male.heightSpan, 0.0001f)
        assertEquals(0.972f, female.heightSpan, 0.0001f)
        assertEquals(1.040f, male.armSpan, 0.0001f)
        assertEquals(0.800f, female.armSpan, 0.0001f)
        assertEquals(1.060f, male.handSpan, 0.0001f)
        assertEquals(0.87612f, female.handSpan, 0.0001f)
        assertEquals(1.000f, male.shoulderSpan, 0.0001f)
        assertEquals(0.732f, female.shoulderSpan, 0.0001f)
        assertEquals(0.917f, male.chestSpan, 0.0001f)
        assertEquals(0.806f, female.chestSpan, 0.0001f)
        assertEquals(1.084f, male.pelvisSpan, 0.0001f)
        assertEquals(1.244f, female.pelvisSpan, 0.0001f)
        assertEquals(0.946f, male.legHorizontalSpan, 0.0001f)
        assertEquals(0.718f, female.legHorizontalSpan, 0.0001f)
        assertEquals(1.091f, male.kneeSpan, 0.0001f)
        assertEquals(0.843f, female.kneeSpan, 0.0001f)
        assertEquals(0.926f, male.calfSpan, 0.0001f)
        assertEquals(0.704f, female.calfSpan, 0.0001f)
        assertEquals(0.904f, male.footSpan, 0.0001f)
        assertEquals(0.702f, female.footSpan, 0.0001f)
        assertEquals(1.000f, male.legSpan, 0.0001f)
        assertEquals(1.000f, female.legSpan, 0.0001f)
    }

    @Test
    fun selectedSummary_returnsScoreAndDeltaFromAverage() {
        val muscles = listOf(
            MuscleGroup(id = "chest", displayName = "Chest", strengthScore = 50, freshnessPercent = 80),
            MuscleGroup(id = "biceps", displayName = "Biceps", strengthScore = 82, freshnessPercent = 90),
            MuscleGroup(id = "legs", displayName = "Legs", strengthScore = 60, freshnessPercent = 70)
        )

        val summary = BodyMapAtlasModel.selectedSummary(muscles, "biceps")

        assertEquals("biceps", summary?.id)
        assertEquals("Arms", summary?.groupLabel)
        assertEquals(82, summary?.score)
        assertEquals(18, summary?.deltaFromAverage)
    }

    @Test
    fun involvementRoles_marksPrimaryAndSecondaryMuscles() {
        val roles = BodyMapAtlasModel.involvementRoles(
            primaryMuscleId = "chest",
            secondaryMuscleIds = listOf("shoulders", "triceps")
        )

        assertEquals(BodyMapRegionRole.Primary, roles["chest"])
        assertEquals(BodyMapRegionRole.Secondary, roles["shoulders"])
        assertEquals(BodyMapRegionRole.Secondary, roles["triceps"])
        assertEquals(BodyMapRegionRole.Neutral, roles["legs"])
    }

    @Test
    fun vectorHotspots_coverEverySelectableFrontAndBackRegion() {
        val front = BodyMapAtlasModel.hotspotsFor(BodyMapView.Front)
        val back = BodyMapAtlasModel.hotspotsFor(BodyMapView.Back)

        assertEquals(
            setOf("shoulders", "chest", "biceps", "forearms", "core", "legs"),
            front.map { it.muscleId }.toSet()
        )
        assertEquals(
            setOf("shoulders", "back", "triceps", "core", "legs"),
            back.map { it.muscleId }.toSet()
        )
        assertTrue(front.count { it.muscleId == "biceps" } >= 2)
        assertTrue(front.count { it.muscleId == "forearms" } >= 2)
        assertTrue(back.count { it.muscleId == "triceps" } >= 2)
        assertTrue(front.all { it.x in 0f..240f && it.y in 0f..640f })
        assertTrue(back.all { it.x in 0f..240f && it.y in 0f..640f })
    }

    @Test
    fun defaultSelectedFor_prefersArmsThenWeakestStrengthMuscle() {
        val musclesWithArms = listOf(
            MuscleGroup(id = "legs", displayName = "Legs", strengthScore = 20),
            MuscleGroup(id = "biceps", displayName = "Biceps", strengthScore = 90)
        )
        assertEquals("biceps", BodyMapAtlasModel.defaultSelectedFor(musclesWithArms, BodyMapDisplayMode.StrengthBalance))

        val musclesWithoutArms = listOf(
            MuscleGroup(id = "legs", displayName = "Legs", strengthScore = 20),
            MuscleGroup(id = "chest", displayName = "Chest", strengthScore = 70)
        )
        assertEquals("legs", BodyMapAtlasModel.defaultSelectedFor(musclesWithoutArms, BodyMapDisplayMode.StrengthBalance))
    }
}
