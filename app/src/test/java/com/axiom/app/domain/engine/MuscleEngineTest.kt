package com.axiom.app.domain.engine

import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.domain.model.WorkoutTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MuscleEngineTest {

    @Test
    fun testCalculateFreshness() {
        val now = System.currentTimeMillis()
        
        // 1. Never trained
        val neverTrained = MuscleEngine.calculateFreshness(null, 48, now)
        assertEquals(100f, neverTrained, 0.01f)

        // 2. Just trained
        val justTrained = MuscleEngine.calculateFreshness(now, 48, now)
        assertEquals(0f, justTrained, 0.01f)

        // 3. Halfway recovered
        val hours24Ago = now - (24 * 3600000L)
        val halfRecovered = MuscleEngine.calculateFreshness(hours24Ago, 48, now)
        assertEquals(50f, halfRecovered, 0.01f)

        // 4. Over-recovered (caps at 100)
        val hours100Ago = now - (100 * 3600000L)
        val overRecovered = MuscleEngine.calculateFreshness(hours100Ago, 48, now)
        assertEquals(100f, overRecovered, 0.01f)
    }

    @Test
    fun testFreshnessBand() {
        assertEquals("FATIGUED", MuscleEngine.freshnessBand(35f))
        assertEquals("RECOVERING", MuscleEngine.freshnessBand(60f))
        assertEquals("FRESH", MuscleEngine.freshnessBand(85f))
    }

    @Test
    fun testApplyWorkoutToMuscles() {
        val initialMuscles = listOf(
            MuscleGroup(
                id = "chest",
                displayName = "Chest",
                strengthScore = 0,
                lastTrainedTimestamp = null,
                freshnessPercent = 100
            ),
            MuscleGroup(
                id = "back",
                displayName = "Back",
                strengthScore = 0,
                lastTrainedTimestamp = null,
                freshnessPercent = 100
            )
        )

        val now = System.currentTimeMillis()
        // Push template targets Chest, not Back
        val updated = MuscleEngine.applyWorkoutToMuscles(initialMuscles, WorkoutTemplate.Push, 45, now)

        val chest = updated.first { it.id == "chest" }
        val back = updated.first { it.id == "back" }

        // Chest expects modification
        assertEquals(now, chest.lastTrainedAt)
        assertEquals(1, chest.weeklyVolumeCount)
        assertTrue(chest.strengthXP > 0L)

        // Back expects no modification
        assertEquals(null, back.lastTrainedAt)
        assertEquals(0, back.weeklyVolumeCount)
        assertEquals(0L, back.strengthXP)
    }
}
