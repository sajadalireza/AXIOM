package com.axiom.app.domain.vocabulary

import com.axiom.app.domain.model.Dungeon
import com.axiom.app.domain.model.Project
import com.axiom.app.domain.model.toLegacyDungeon
import com.axiom.app.domain.model.toProject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * G3-P1 — Verifies bidirectional lossless adapter conversion between canonical [Project]
 * and legacy [Dungeon] domain models without schema alteration or data loss.
 */
class ProjectDungeonCompatibilityTest {

    @Test
    fun legacyDungeon_convertsToCanonicalProject_losslessly() {
        val dungeon = Dungeon(
            id = "dung-42",
            name = "Refactor Cache Architecture",
            description = "Migrate from LRU to tiered cache",
            rarity = "RARE",
            totalStages = 5,
            completedStages = 3,
            isBossDefeated = false,
            createdAt = 1700000000L,
            completedAt = null,
            stageDescriptions = "1:Spec,2:L1,3:L2,4:Bench,5:Deploy"
        )

        val project = dungeon.toProject()

        assertEquals("dung-42", project.id)
        assertEquals("Refactor Cache Architecture", project.name)
        assertEquals("Migrate from LRU to tiered cache", project.description)
        assertEquals("RARE", project.rarity)
        assertEquals(5, project.totalStages)
        assertEquals(3, project.completedStages)
        assertFalse(project.isCompleted)
        assertEquals(0.6f, project.progressPercent, 0.001f)
        assertEquals(1700000000L, project.createdAt)
        assertEquals("1:Spec,2:L1,3:L2,4:Bench,5:Deploy", project.stageDescriptions)
    }

    @Test
    fun canonicalProject_convertsToLegacyDungeon_losslessly() {
        val project = Project(
            id = "proj-101",
            name = "Distributed Consensus Engine",
            description = "Implement Raft replication",
            totalStages = 3,
            completedStages = 3,
            isCompleted = true,
            createdAt = 1710000000L,
            completedAt = 1710050000L,
            rarity = "EPIC",
            stageDescriptions = "Leader,Follower,Snapshot"
        )

        val dungeon = project.toLegacyDungeon()

        assertEquals("proj-101", dungeon.id)
        assertEquals("Distributed Consensus Engine", dungeon.name)
        assertEquals("Implement Raft replication", dungeon.description)
        assertEquals("EPIC", dungeon.rarity)
        assertEquals(3, dungeon.totalStages)
        assertEquals(3, dungeon.completedStages)
        assertTrue(dungeon.isBossDefeated)
        assertTrue(dungeon.isCompleted)
        assertEquals(1.0f, dungeon.progressPercent, 0.001f)
        assertEquals(1710000000L, dungeon.createdAt)
        assertEquals(1710050000L, dungeon.completedAt)
    }

    @Test
    fun roundTripConversion_preservesInvariants() {
        val originalDungeon = Dungeon(
            id = "d-roundtrip",
            name = "Security Hardening",
            description = "Eliminate legacy cryptographic primitives",
            rarity = "LEGENDARY",
            totalStages = 4,
            completedStages = 2,
            isBossDefeated = false,
            createdAt = 1720000000L,
            completedAt = null,
            stageDescriptions = "Audit,Migrate,Fuzz,Sign"
        )

        val roundTrip = originalDungeon.toProject().toLegacyDungeon()

        assertEquals(originalDungeon.id, roundTrip.id)
        assertEquals(originalDungeon.name, roundTrip.name)
        assertEquals(originalDungeon.description, roundTrip.description)
        assertEquals(originalDungeon.rarity, roundTrip.rarity)
        assertEquals(originalDungeon.totalStages, roundTrip.totalStages)
        assertEquals(originalDungeon.completedStages, roundTrip.completedStages)
        assertEquals(originalDungeon.isBossDefeated, roundTrip.isBossDefeated)
        assertEquals(originalDungeon.createdAt, roundTrip.createdAt)
        assertEquals(originalDungeon.completedAt, roundTrip.completedAt)
        assertEquals(originalDungeon.stageDescriptions, roundTrip.stageDescriptions)
    }
}
