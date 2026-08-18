package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateFirstWinMissionUseCaseTest {
    private class FakeMissionStore : FirstWinMissionStore {
        val rows = linkedMapOf<String, Mission>()
        var insertCalls = 0
        override suspend fun insertIfAbsent(mission: Mission): Mission {
            insertCalls++
            return rows.getOrPut(mission.id) { mission }
        }
    }

    private class FakeSkillRepository : SkillRepository {
        val skills = linkedMapOf<String, Skill>()
        override fun getAllSkills(): Flow<List<Skill>> = MutableStateFlow(skills.values.toList())
        override suspend fun getSkillById(id: String): Skill? = skills[id]
        override suspend fun insertSkill(skill: Skill) { skills[skill.id] = skill }
        override suspend fun updateSkill(skill: Skill) { skills[skill.id] = skill }
        override suspend fun deleteSkillById(id: String) { skills.remove(id) }
    }

    private fun skill(id: String, name: String) = Skill(
        id = id, name = name, category = "capability", currentXP = 0, level = 1,
        rankLabel = "E-Rank", parentId = null, isUnlocked = true, xpToNextRank = 100,
        rankProgressPercent = 0f, isShadowCandidate = false, rankColor = 1L,
    )

    private fun fixture(area: FirstWinArea = FirstWinArea.WORK): Pair<FakeMissionStore, CreateFirstWinMissionUseCase> {
        val store = FakeMissionStore()
        val skills = FakeSkillRepository().apply { skills[area.skillId] = skill(area.skillId, "Neutral Skill") }
        return store to CreateFirstWinMissionUseCase(store, skills)
    }

    @Test fun sameSession_doubleTap_returnsOneDeterministicMission() = runTest {
        val (store, useCase) = fixture()
        val first = useCase("fw:hunter-1:session", FirstWinArea.WORK, "  Review one page  ")
        val second = useCase("fw:hunter-1:session", FirstWinArea.WORK, "Different retry text")
        assertEquals(FirstWinIds.primaryMissionId("fw:hunter-1:session"), first.id)
        assertEquals(first, second)
        assertEquals(1, store.rows.size)
        assertEquals("Review one page", first.title)
    }

    @Test fun healthArea_usesExistingNeutralSkill_andStableTrack() = runTest {
        val (_, useCase) = fixture(FirstWinArea.HEALTH)
        val mission = useCase("fw:hunter-2:session", FirstWinArea.HEALTH, "Drink a glass of water")
        assertEquals(FirstWinArea.HEALTH.skillId, mission.skillId)
        assertEquals("HEALTH", mission.track)
        assertEquals("ACTIVE", mission.status)
        assertNull(mission.dungeonId)
        assertTrue("First-Win action must stay micro-sized", mission.estimatedHours <= (3f / 60f))
        assertTrue(mission.xpReward > 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun blankAction_isRejected() = runTest {
        val (_, useCase) = fixture()
        useCase("fw:hunter-1:session", FirstWinArea.WORK, "  ")
    }

    @Test(expected = IllegalStateException::class)
    fun missingNeutralSkill_failsClosed_withoutFabricatedSkill() = runTest {
        val store = FakeMissionStore()
        val useCase = CreateFirstWinMissionUseCase(store, FakeSkillRepository())
        useCase("fw:hunter-1:session", FirstWinArea.PERSONAL, "Write one sentence")
    }
}
