package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.repository.HunterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class EnsureFirstWinHunterUseCaseTest {
    private class FakeHunterRepository(seed: Hunter? = null) : HunterRepository {
        private val state = MutableStateFlow(seed)
        var writes = 0
        override fun getHunterProfile(): Flow<Hunter?> = state
        override suspend fun getDirectHunterProfile(): Hunter? = state.value
        override suspend fun updateHunterProfile(profile: Hunter) { writes++; state.value = profile }
    }

    private fun hunter(id: String) = Hunter(
        id = id, name = "Existing", level = 4, rankLabel = "D-Rank", totalXP = 400,
        currentXP = 50, xpToNextLevel = 500, progressPercent = .1f,
        rankColor = 1L, rankGlyph = "D", personalThesis = "keep",
    )

    @Test fun existingHunter_isReturnedUnchanged_withoutWrite() = runTest {
        val existing = hunter("existing")
        val repo = FakeHunterRepository(existing)
        val result = EnsureFirstWinHunterUseCase(repo)()
        assertSame(existing, result)
        assertEquals(0, repo.writes)
    }

    @Test fun missingHunter_createsOnlyMinimalNeutralProfile() = runTest {
        val repo = FakeHunterRepository()
        val result = EnsureFirstWinHunterUseCase(repo)()
        assertEquals(1, repo.writes)
        assertTrue(result.id.isNotBlank())
        assertTrue(result.name.isNotBlank())
        assertEquals(1, result.level)
        assertEquals("E-Rank", result.rankLabel)
        assertEquals(0L, result.totalXP)
        assertEquals(0, result.currentXP)
        assertEquals("", result.personalThesis)
    }

    @Test fun repeatedCall_reusesPersistedHunter() = runTest {
        val repo = FakeHunterRepository()
        val useCase = EnsureFirstWinHunterUseCase(repo)
        val first = useCase()
        val second = useCase()
        assertEquals(first.id, second.id)
        assertEquals(1, repo.writes)
    }
}
