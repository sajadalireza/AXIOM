package com.axiom.app.domain.firstwin

import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.repository.HunterRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * WP-207 minimal Hunter bootstrap. It intentionally owns no preferences, Dungeon,
 * Mission, streak-freeze, analytics, or starter-content dependency.
 */
@Singleton
class EnsureFirstWinHunterUseCase @Inject constructor(
    private val hunterRepository: HunterRepository,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(): Hunter = mutex.withLock {
        hunterRepository.getDirectHunterProfile()?.let { return@withLock it }

        val hunter = Hunter(
            id = UUID.randomUUID().toString(),
            name = "Hunter",
            level = 1,
            rankLabel = "E-Rank",
            totalXP = 0L,
            currentXP = 0,
            xpToNextLevel = 100,
            progressPercent = 0f,
            rankColor = 0xFF9E9E9E,
            rankGlyph = "E",
            personalThesis = "",
        )
        hunterRepository.updateHunterProfile(hunter)
        hunter
    }
}
