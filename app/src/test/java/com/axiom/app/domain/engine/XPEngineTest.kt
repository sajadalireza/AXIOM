package com.axiom.app.domain.engine

import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Skill
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class XPEngineTest {

    @Test
    fun testXpNeededForLevel() {
        assertEquals(100L, XPEngine.xpNeededForLevel(1))
        assertEquals(150L, XPEngine.xpNeededForLevel(2))
        assertEquals(200L, XPEngine.xpNeededForLevel(3))
        assertEquals(5950L, XPEngine.xpNeededForLevel(118))
    }

    @Test
    fun testCalculateSkillRank() {
        assertEquals("RECRUIT", XPEngine.calculateSkillRank(0))
        assertEquals("RECRUIT", XPEngine.calculateSkillRank(499))
        assertEquals("BUILDER", XPEngine.calculateSkillRank(500))
        assertEquals("OPERATOR", XPEngine.calculateSkillRank(1500))
        assertEquals("SPECIALIST", XPEngine.calculateSkillRank(3500))
        assertEquals("STRATEGIST", XPEngine.calculateSkillRank(7000))
        assertEquals("ARCHITECT", XPEngine.calculateSkillRank(12000))
        assertEquals("ARCHITECT", XPEngine.calculateSkillRank(100000))
    }

    @Test
    fun testCalculateHunterRank() {
        assertEquals("RECRUIT", XPEngine.calculateHunterRank(1))
        assertEquals("RECRUIT", XPEngine.calculateHunterRank(10))
        assertEquals("BUILDER", XPEngine.calculateHunterRank(11))
        assertEquals("BUILDER", XPEngine.calculateHunterRank(25))
        assertEquals("OPERATOR", XPEngine.calculateHunterRank(26))
        assertEquals("OPERATOR", XPEngine.calculateHunterRank(45))
        assertEquals("SPECIALIST", XPEngine.calculateHunterRank(46))
        assertEquals("SPECIALIST", XPEngine.calculateHunterRank(70))
        assertEquals("STRATEGIST", XPEngine.calculateHunterRank(71))
        assertEquals("STRATEGIST", XPEngine.calculateHunterRank(99))
        assertEquals("ARCHITECT", XPEngine.calculateHunterRank(100))
    }

    @Test
    fun testIsShadowCandidate() {
        assertTrue(XPEngine.isShadowCandidate("B"))
        assertTrue(XPEngine.isShadowCandidate("A"))
        assertTrue(XPEngine.isShadowCandidate("SPECIALIST"))
        assertTrue(XPEngine.isShadowCandidate("STRATEGIST"))
        assertTrue(XPEngine.isShadowCandidate("ARCHITECT"))
        assertTrue(XPEngine.isShadowCandidate("B-Rank"))
        assertTrue(XPEngine.isShadowCandidate("S-Rank"))
        assertTrue(XPEngine.isShadowCandidate("SPECIALIST-Rank"))
        assertTrue(!XPEngine.isShadowCandidate("E"))
        assertTrue(!XPEngine.isShadowCandidate("RECRUIT"))
        assertTrue(!XPEngine.isShadowCandidate("BUILDER"))
        assertTrue(!XPEngine.isShadowCandidate("OPERATOR"))
    }

    @Test
    fun testCalculateXPResult_NormalAndEdgeCases() {
        val mission = Mission(
            id = "test-mission",
            title = "Test Mission",
            track = "Combat",
            rarity = "COMMON",
            skillId = "test-skill",
            skillName = "Shadow step",
            xpReward = 100,
            powerScore = 1.0f,
            status = "PENDING",
            dungeonId = null,
            estimatedHours = 1.0f,
            actualHours = null,
            createdAt = System.currentTimeMillis(),
            completedAt = null,
            rarityColor = 0xFFFFFFFFL,
            description = "Description"
        )

        val hunter = Hunter(
            id = "test-hunter",
            name = "Jin-Woo",
            level = 1,
            rankLabel = "E-Rank",
            totalXP = 0L,
            currentXP = 0,
            xpToNextLevel = 100,
            progressPercent = 0.0f,
            rankColor = 0xFFFFFFFFL,
            rankGlyph = "E"
        )

        val skill = Skill(
            id = "test-skill",
            name = "Shadow step",
            category = "Agility",
            currentXP = 0L,
            level = 1,
            rankLabel = "E-Rank",
            parentId = null,
            isUnlocked = true,
            xpToNextRank = 500L,
            rankProgressPercent = 0.0f,
            isShadowCandidate = false,
            rankColor = 0xFFFFFFFFL
        )

        val result = XPEngine.calculateXPResult(
            mission = mission,
            hunter = hunter,
            skill = skill,
            streakMultiplier = 1.0f,
            shadows = emptyList()
        )

        // 100 Base * 1.0 (COMMON Rarity) * 1.0 (streak) * 1.0 (no shadows) = 100 Hunter XP code-gained
        assertEquals(100, result.hunterXPGained)
        assertEquals(100L, result.skillXPGained["test-skill"])
        assertTrue(result.skillLeveledUp["test-skill"] ?: false)

        // Hunter leveled up from 1 to 2 because level 1 requires 100 XP
        assertTrue(result.leveledUp)
        assertEquals(2, result.newLevel)
    }

    @Test
    fun testCalculateXPResult_MaxLevelConstraint() {
        val mission = Mission(
            id = "test-mission",
            title = "Boss Hunter",
            track = "Combat",
            rarity = "COMMON",
            skillId = "test-skill",
            skillName = "Annihilation",
            xpReward = 100,
            powerScore = 1.0f,
            status = "PENDING",
            dungeonId = null,
            estimatedHours = 1.0f,
            actualHours = null,
            createdAt = System.currentTimeMillis(),
            completedAt = null,
            rarityColor = 0xFFFFFFFFL,
            description = "Kill boss"
        )

        val hunter = Hunter(
            id = "level-100-hunter",
            name = "S-Rank Hunter",
            level = 100,
            rankLabel = "S-Rank",
            totalXP = 100000L,
            currentXP = 50,
            xpToNextLevel = 5000,
            progressPercent = 0.01f,
            rankColor = 0xFFFFD700L,
            rankGlyph = "S"
        )

        val skill = Skill(
            id = "test-skill",
            name = "Annihilation",
            category = "Combat",
            currentXP = 12000L,
            level = 10,
            rankLabel = "S-Rank",
            parentId = null,
            isUnlocked = true,
            xpToNextRank = 0L,
            rankProgressPercent = 1.0f,
            isShadowCandidate = true,
            rankColor = 0xFFFFD700L
        )

        val result = XPEngine.calculateXPResult(
            mission = mission,
            hunter = hunter,
            skill = skill,
            streakMultiplier = 1.2f,
            shadows = emptyList()
        )

        // Verify remaining hunter level is capped at 100
        assertTrue(!result.leveledUp)
        assertNull(result.newLevel)
    }
}
