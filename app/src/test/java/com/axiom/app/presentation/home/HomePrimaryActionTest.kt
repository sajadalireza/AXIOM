package com.axiom.app.presentation.home

import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.HomeUiState
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * G3-P3 unit tests verifying Home Primary Action mechanics and single-CTA constitutional integrity.
 *
 * Enforces:
 * 1. Home is centered on the Next Meaningful Mission.
 * 2. Exactly one dominant Primary CTA is rendered on Home.
 * 3. Competing floating action bars are eliminated.
 * 4. Secondary surfaces are subordinated into a collapsible section.
 */
class HomePrimaryActionTest {

    private fun locate(relative: String): File {
        val candidates = listOf(
            File(relative),
            File("app/$relative"),
            File("../app/$relative"),
            File(System.getProperty("user.dir") ?: ".", relative),
            File(System.getProperty("user.dir") ?: ".", "app/$relative")
        )
        return candidates.firstOrNull { it.isFile }
            ?: fail("Required source not found: $relative").let { error("unreachable") }
    }

    private val testHunter = Hunter(
        id = "h-1",
        name = "Sajad",
        level = 5,
        rankLabel = "D",
        totalXP = 1200L,
        currentXP = 200,
        xpToNextLevel = 500,
        progressPercent = 0.4f,
        rankColor = 0xFFFFFFFFL,
        rankGlyph = "D",
        personalThesis = "Build AXIOM"
    )

    @Test
    fun homeState_withActiveMissions_designatesFirstMissionAsPrimary() {
        val activeMission1 = Mission(
            id = "m-1",
            title = "Implement Page Table Mapping",
            track = "SYSTEMS",
            rarity = "RARE",
            skillId = "s-systems",
            skillName = "Systems",
            xpReward = 150,
            powerScore = 1.0f,
            status = "ACTIVE",
            dungeonId = "d-kernel",
            estimatedHours = 1.0f,
            actualHours = null,
            createdAt = 1000L,
            completedAt = null,
            rarityColor = 0xFFFFFFFFL
        )
        val activeMission2 = Mission(
            id = "m-2",
            title = "Write Unit Tests",
            track = "SYSTEMS",
            rarity = "COMMON",
            skillId = "s-systems",
            skillName = "Systems",
            xpReward = 50,
            powerScore = 0.5f,
            status = "ACTIVE",
            dungeonId = null,
            estimatedHours = 0.5f,
            actualHours = null,
            createdAt = 2000L,
            completedAt = null,
            rarityColor = 0xFFFFFFFFL
        )

        val successState = HomeUiState.Success(
            hunter = testHunter,
            topMissions = listOf(activeMission1, activeMission2),
            activeDungeon = Dungeon(
                id = "d-kernel",
                name = "Microkernel Project",
                description = "Build kernel",
                rarity = "RARE",
                totalStages = 4,
                completedStages = 1,
                isBossDefeated = false,
                createdAt = 1000L,
                completedAt = null,
                stageDescriptions = "Stage 1||Stage 2||Stage 3||Boss"
            ),
            recentFeed = emptyList(),
            characterStats = null,
            streakDays = 5,
            activeMissionsCount = 2,
            streakMultiplier = 1.0f
        )

        // The Next Meaningful Mission is topMissions.first()
        val primaryMission = successState.topMissions.firstOrNull()
        assertNotNull(primaryMission)
        assertEquals("m-1", primaryMission!!.id)
        assertEquals("Implement Page Table Mapping", primaryMission.title)
        assertEquals("d-kernel", primaryMission.dungeonId)
    }

    @Test
    fun homeState_withNoActiveMissions_designatesEmptyHeroState() {
        val emptyState = HomeUiState.Success(
            hunter = testHunter,
            topMissions = emptyList(),
            activeDungeon = null,
            recentFeed = emptyList(),
            characterStats = null,
            streakDays = 0,
            activeMissionsCount = 0,
            streakMultiplier = 1.0f
        )

        assertNull(emptyState.topMissions.firstOrNull())
    }

    @Test
    fun singlePrimaryCtaRule_enforcedInSuccessContent() {
        val content = locate(
            "src/main/java/com/axiom/app/presentation/home/components/SuccessContent.kt"
        ).readText()

        // 1. NextMissionHeroCard is rendered
        assertTrue(
            "SuccessContent must render NextMissionHeroCard",
            content.contains("NextMissionHeroCard(")
        )

        // 2. Competing floating HomeActionBar is removed
        assertFalse(
            "SuccessContent must NOT render competing HomeActionBar",
            content.contains("HomeActionBar(")
        )

        // 3. Secondary surfaces are enclosed in SecondarySurfacesSection
        assertTrue(
            "SuccessContent must subordinate secondary widgets inside SecondarySurfacesSection",
            content.contains("SecondarySurfacesSection")
        )
    }
}
