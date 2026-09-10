package com.axiom.app.domain.vocabulary

import com.axiom.app.core.CanonicalAnalyticsEvents
import com.axiom.app.domain.model.Goal
import com.axiom.app.domain.model.GoalStatus
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Project
import com.axiom.app.domain.model.Purpose
import com.axiom.app.domain.model.Skill
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * G3-P1 — Static contract tests verifying canonical vocabulary alignment across
 * domain entities, string resources, and analytics events.
 *
 * Enforces AXIOM Canonical Vocabulary (CANONICAL_VOCABULARY.md) and
 * Deprecation Plan (VOCABULARY_DEPRECATION_PLAN.md).
 */
class CanonicalVocabularyContractTest {

    @Test
    fun canonicalDomainEntities_instantiateWithCorrectProperties() {
        val goal = Goal(
            id = "g-1",
            title = "Master Systems Engineering",
            description = "Complete core systems roadmap",
            status = GoalStatus.ACTIVE,
            progressPercent = 0.5f
        )
        assertEquals("g-1", goal.id)
        assertEquals(GoalStatus.ACTIVE, goal.status)
        assertEquals(0.5f, goal.progressPercent)

        val project = Project(
            id = "p-1",
            name = "Microkernel Implementation",
            description = "Build a minimal preemptive microkernel",
            totalStages = 4,
            completedStages = 2,
            isCompleted = false
        )
        assertEquals("p-1", project.id)
        assertEquals(0.5f, project.progressPercent)

        val purpose = Purpose(
            statement = "Advance open computing architectures",
            coreMotivation = "Empower independent developers"
        )
        assertEquals("Advance open computing architectures", purpose.statement)

        val mission = Mission(
            id = "m-1",
            title = "Write context switch routines",
            track = "CORE",
            rarity = "COMMON",
            skillId = "s-1",
            skillName = "Assembly",
            xpReward = 50,
            powerScore = 1.0f,
            status = "PENDING",
            dungeonId = "p-1",
            estimatedHours = 1.0f,
            actualHours = null,
            createdAt = 1000L,
            completedAt = null,
            rarityColor = 0L,
            isInstantGate = true
        )
        // Verify canonical helpers on Mission
        assertTrue(mission.isTimedMission)
        assertEquals("p-1", mission.projectId)
    }

    @Test
    fun stringResources_containNoProductFacingTaskSynonymsForMission() {
        val rootDir = File(System.getProperty("user.dir") ?: ".")
        val englishStrings = File(rootDir, "src/main/res/values/strings.xml")
        if (englishStrings.exists()) {
            val content = englishStrings.readText()
            // Verify stat_focus_desc and first_mission_desc do not use "task"
            val statFocusLine = content.lines().firstOrNull { it.contains("stat_focus_desc") }
            assertNotNull(statFocusLine)
            assertFalse("stat_focus_desc must not contain 'task'", statFocusLine!!.contains("task", ignoreCase = true))
            assertTrue("stat_focus_desc must use 'mission'", statFocusLine.contains("mission", ignoreCase = true))

            val firstMissionLine = content.lines().firstOrNull { it.contains("first_mission_desc") }
            assertNotNull(firstMissionLine)
            assertFalse("first_mission_desc must not contain 'task'", firstMissionLine!!.contains("task", ignoreCase = true))
            assertTrue("first_mission_desc must use 'mission'", firstMissionLine.contains("mission", ignoreCase = true))
        }
    }

    @Test
    fun canonicalAnalyticsEvents_followLowerSnakeCaseAndCanonicalPrefixes() {
        val events = listOf(
            CanonicalAnalyticsEvents.GOAL_CREATED,
            CanonicalAnalyticsEvents.GOAL_UPDATED,
            CanonicalAnalyticsEvents.GOAL_PROGRESS_UPDATED,
            CanonicalAnalyticsEvents.GOAL_COMPLETED,
            CanonicalAnalyticsEvents.MISSION_CREATED,
            CanonicalAnalyticsEvents.MISSION_SCHEDULED,
            CanonicalAnalyticsEvents.MISSION_STARTED,
            CanonicalAnalyticsEvents.MISSION_COMPLETED,
            CanonicalAnalyticsEvents.PROJECT_CREATED,
            CanonicalAnalyticsEvents.PROJECT_STARTED,
            CanonicalAnalyticsEvents.PROJECT_PROGRESS_UPDATED,
            CanonicalAnalyticsEvents.PROJECT_COMPLETED,
            CanonicalAnalyticsEvents.SKILL_CREATED,
            CanonicalAnalyticsEvents.SKILL_PRACTICE_RECORDED,
            CanonicalAnalyticsEvents.SKILL_PROGRESS_UPDATED
        )

        for (event in events) {
            // Must be lower snake case
            assertTrue("Event $event must be lower_snake_case", event.matches(Regex("^[a-z]+(_[a-z]+)+$")))
            // Must start with canonical domain noun
            assertTrue(
                "Event $event must start with goal_, mission_, project_, or skill_",
                event.startsWith("goal_") || event.startsWith("mission_") ||
                        event.startsWith("project_") || event.startsWith("skill_")
            )
        }

        // Test legacy alias resolution
        assertEquals(
            CanonicalAnalyticsEvents.PROJECT_CREATED,
            CanonicalAnalyticsEvents.resolveCanonicalName("dungeon_created")
        )
        assertEquals(
            CanonicalAnalyticsEvents.MISSION_CREATED,
            CanonicalAnalyticsEvents.resolveCanonicalName("task_created")
        )
        assertEquals(
            CanonicalAnalyticsEvents.MISSION_STARTED,
            CanonicalAnalyticsEvents.resolveCanonicalName("instant_gate_started")
        )
    }
}
