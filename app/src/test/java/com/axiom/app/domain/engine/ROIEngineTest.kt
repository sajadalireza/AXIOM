package com.axiom.app.domain.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ROIEngineTest {

    @Test
    fun testCalculatePowerScore_Normal() {
        val score = ROIEngine.calculatePowerScore(
            marketDemand = 5f,
            leverage = 3f,
            complexity = 8f,
            estimatedHours = 4f
        )
        // base = 5 * 0.3 + 3 * 0.3 + 8 * 0.4 = 1.5 + 0.9 + 3.2 = 5.6
        // divisor = sqrt(4) = 2.0
        // scoreBeforeCoerce = 5.6 / 2 = 2.8
        assertEquals(2.8f, score, 0.001f)
    }

    @Test
    fun testCalculatePowerScore_CoercionBoundaries() {
        // High values capped at 10.0f
        val scoreMax = ROIEngine.calculatePowerScore(
            marketDemand = 100f,
            leverage = 100f,
            complexity = 100f,
            estimatedHours = 1f
        )
        assertEquals(10.0f, scoreMax)

        // Low values capped at 1.0f
        val scoreMin = ROIEngine.calculatePowerScore(
            marketDemand = 0.1f,
            leverage = 0.1f,
            complexity = 0.1f,
            estimatedHours = 1000f
        )
        assertEquals(1.0f, scoreMin)

        // Zero hours
        val scoreZeroHours = ROIEngine.calculatePowerScore(
            marketDemand = 1f,
            leverage = 1f,
            complexity = 1f,
            estimatedHours = 0f
        )
        // divisor is 1f
        // base = 0.3 + 0.3 + 0.4 = 1.0f
        assertEquals(1.0f, scoreZeroHours)
    }

    @Test
    fun testClassifyRarity() {
        assertEquals("FOUNDATION", ROIEngine.classifyRarity(1.0f))
        assertEquals("FOUNDATION", ROIEngine.classifyRarity(2.49f))
        assertEquals("COMPOUND", ROIEngine.classifyRarity(2.5f))
        assertEquals("COMPOUND", ROIEngine.classifyRarity(4.49f))
        assertEquals("CRITICAL", ROIEngine.classifyRarity(4.5f))
        assertEquals("CRITICAL", ROIEngine.classifyRarity(6.99f))
        assertEquals("SHIELD", ROIEngine.classifyRarity(7.0f))
        assertEquals("SHIELD", ROIEngine.classifyRarity(8.99f))
        assertEquals("DEPTH", ROIEngine.classifyRarity(9.0f))
        assertEquals("DEPTH", ROIEngine.classifyRarity(10.0f))
    }

    @Test
    fun testToMissionSuggestion() {
        val suggestion1 = ROIEngine.toMissionSuggestion(2.0f, 100)
        // successChance = 100 - (2 * 8) = 84
        assertEquals(84, suggestion1.successChance)
        assertEquals("Low", suggestion1.rewardPotential)
        assertEquals(100, suggestion1.estimatedXP)

        val suggestionMax = ROIEngine.toMissionSuggestion(10.0f, 500)
        // successChance coerced between 15 and 95
        assertEquals(20, suggestionMax.successChance)
        assertEquals("EX-Rank", suggestionMax.rewardPotential)
        assertEquals(500, suggestionMax.estimatedXP)
    }
}
