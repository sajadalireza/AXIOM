package com.axiom.app.domain.review

import com.axiom.app.domain.model.Mission
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * G5-P2 Weekly Review Engine Contract Test.
 *
 * Verifies:
 * 1. Truth-grounded progress snapshot calculation (Goal alignment, effective hours, WMPU status).
 * 2. Zero-shame handling of low or zero completion weeks.
 * 3. Hours fallback hierarchy (effectiveHours -> actualHours -> estimatedHours).
 * 4. Obstacle category metadata completeness (bilingual English + Persian).
 * 5. Review evaluation completeness and progress correlation scoring.
 * 6. Anti-inflation invariant: flat non-inflated 50 XP review award.
 * 7. Privacy-safe hours bracket categorization for telemetry.
 */
class WeeklyReviewEngineContractTest {

    private val now = 1757600000000L // arbitrary fixed timestamp
    private val oneDayAgo = now - 86400000L
    private val twoDaysAgo = now - 2 * 86400000L
    private val eightDaysAgo = now - 8 * 86400000L

    private fun sampleMission(
        id: String,
        status: String = "COMPLETED",
        completedAt: Long? = oneDayAgo,
        goalId: String? = null,
        effectiveHours: Double = 0.0,
        actualHours: Float? = null,
        estimatedHours: Float = 1.0f
    ): Mission {
        return Mission(
            id = id,
            title = "Mission $id",
            track = "Build",
            rarity = "COMMON",
            skillId = "skill-1",
            skillName = "Deep Work",
            xpReward = 50,
            powerScore = 10f,
            status = status,
            dungeonId = goalId,
            trackId = goalId,
            estimatedHours = estimatedHours,
            actualHours = actualHours,
            createdAt = oneDayAgo - 3600000L,
            completedAt = completedAt,
            rarityColor = 0xFF4CAF50,
            effectiveHours = effectiveHours
        )
    }

    @Test
    fun buildProgressSnapshot_accuratelyIdentifiesGoalMissionsAndWmpu() {
        val missions = listOf(
            sampleMission("m1", completedAt = oneDayAgo, goalId = "g-revenue", effectiveHours = 2.5),
            sampleMission("m2", completedAt = twoDaysAgo, goalId = "g-revenue", effectiveHours = 1.5),
            sampleMission("m3", completedAt = oneDayAgo, goalId = null, effectiveHours = 1.0),
            // Mission completed >7 days ago (should be excluded)
            sampleMission("m4", completedAt = eightDaysAgo, goalId = "g-revenue", effectiveHours = 5.0),
            // Mission not completed
            sampleMission("m5", status = "AVAILABLE", completedAt = null, goalId = "g-revenue")
        )

        val snapshot = WeeklyReviewEngine.buildProgressSnapshot(
            missions = missions,
            currentStreak = 7,
            nowMillis = now
        )

        assertEquals(3, snapshot.totalMissionsCompleted)
        assertEquals(2, snapshot.goalContributingMissions)
        assertEquals(5.0f, snapshot.totalEffectiveHours, 0.01f)
        assertTrue("WMPU should be achieved with 2 completed goal-aligned missions", snapshot.wmpuAchieved)
        assertEquals(7, snapshot.activeStreak)
    }

    @Test
    fun buildProgressSnapshot_handlesZeroCompletionWeeksWithoutShame() {
        val missions = listOf(
            sampleMission("m1", status = "AVAILABLE", completedAt = null),
            sampleMission("m2", completedAt = eightDaysAgo) // outside cycle
        )

        val snapshot = WeeklyReviewEngine.buildProgressSnapshot(
            missions = missions,
            currentStreak = 0,
            nowMillis = now
        )

        assertEquals(0, snapshot.totalMissionsCompleted)
        assertEquals(0, snapshot.goalContributingMissions)
        assertEquals(0f, snapshot.totalEffectiveHours, 0.01f)
        assertFalse(snapshot.wmpuAchieved)
        assertEquals(0, snapshot.activeStreak)
    }

    @Test
    fun buildProgressSnapshot_usesEffectiveHoursFallback() {
        val missions = listOf(
            // Uses effectiveHours directly
            sampleMission("m1", effectiveHours = 3.0, actualHours = 2.0f, estimatedHours = 1.0f),
            // Fallback to actualHours
            sampleMission("m2", effectiveHours = 0.0, actualHours = 2.5f, estimatedHours = 1.0f),
            // Fallback to estimatedHours
            sampleMission("m3", effectiveHours = 0.0, actualHours = null, estimatedHours = 1.5f)
        )

        val snapshot = WeeklyReviewEngine.buildProgressSnapshot(missions, currentStreak = 3, nowMillis = now)
        assertEquals(7.0f, snapshot.totalEffectiveHours, 0.01f)
    }

    @Test
    fun evaluateReview_calculatesCompletenessScore() {
        val snapshot = ReviewProgressSnapshot(
            cycleWeek = 37,
            totalMissionsCompleted = 5,
            goalContributingMissions = 2,
            totalEffectiveHours = 6.5f,
            wmpuAchieved = true,
            activeStreak = 5
        )

        // All fields filled -> 1.0f
        val completeEval = WeeklyReviewEngine.evaluateReview(
            snapshot = snapshot,
            obstacleCategory = ObstacleCategory.TIME_SLIPPAGE,
            commitmentOutcome = "Refactor auth repository",
            usefulnessRating = 5
        )
        assertEquals(1.0f, completeEval.completenessScore, 0.01f)
        assertEquals(1.0f, completeEval.progressCorrelationScore, 0.01f)
        assertTrue(completeEval.isWmpuAchieved)
        assertEquals(50, completeEval.reviewXpAward)

        // Missing obstacle and rating out of range
        val partialEval = WeeklyReviewEngine.evaluateReview(
            snapshot = snapshot,
            obstacleCategory = null,
            commitmentOutcome = "Finish feature",
            usefulnessRating = 0
        )
        assertEquals(0.5f, partialEval.completenessScore, 0.01f)
    }

    @Test
    fun evaluateReview_scoresProgressCorrelationGradations() {
        val wmpuSnapshot = ReviewProgressSnapshot(37, 3, 2, 4f, wmpuAchieved = true, activeStreak = 3)
        val goalSnapshot = ReviewProgressSnapshot(37, 3, 1, 3f, wmpuAchieved = false, activeStreak = 3)
        val nonGoalSnapshot = ReviewProgressSnapshot(37, 3, 0, 3f, wmpuAchieved = false, activeStreak = 3)
        val zeroSnapshot = ReviewProgressSnapshot(37, 0, 0, 0f, wmpuAchieved = false, activeStreak = 0)

        val evalWmpu = WeeklyReviewEngine.evaluateReview(wmpuSnapshot, ObstacleCategory.CLARITY_GAP, "Target", 5)
        val evalGoal = WeeklyReviewEngine.evaluateReview(goalSnapshot, ObstacleCategory.CLARITY_GAP, "Target", 5)
        val evalNonGoal = WeeklyReviewEngine.evaluateReview(nonGoalSnapshot, ObstacleCategory.CLARITY_GAP, "Target", 5)
        val evalZero = WeeklyReviewEngine.evaluateReview(zeroSnapshot, ObstacleCategory.CLARITY_GAP, "Target", 5)

        assertEquals(1.0f, evalWmpu.progressCorrelationScore, 0.01f)
        assertEquals(0.8f, evalGoal.progressCorrelationScore, 0.01f)
        assertEquals(0.6f, evalNonGoal.progressCorrelationScore, 0.01f)
        assertEquals(0.4f, evalZero.progressCorrelationScore, 0.01f)
    }

    @Test
    fun antiInflation_enforcesFlatNonInflatedXpAward() {
        assertEquals(50, WeeklyReviewEngine.FLAT_REVIEW_XP)

        val snapshot = ReviewProgressSnapshot(37, 10, 5, 25f, true, 14)
        val eval = WeeklyReviewEngine.evaluateReview(
            snapshot = snapshot,
            obstacleCategory = ObstacleCategory.PRIORITY_DRIFT,
            commitmentOutcome = "Ship release",
            usefulnessRating = 5
        )
        assertEquals("Weekly review must award exactly flat 50 XP to prevent vanity inflation", 50, eval.reviewXpAward)
    }

    @Test
    fun categorizeHoursBracket_returnsCorrectBuckets() {
        assertEquals("0-2h", WeeklyReviewEngine.categorizeHoursBracket(0f))
        assertEquals("0-2h", WeeklyReviewEngine.categorizeHoursBracket(1.9f))
        assertEquals("2-5h", WeeklyReviewEngine.categorizeHoursBracket(2.0f))
        assertEquals("2-5h", WeeklyReviewEngine.categorizeHoursBracket(4.9f))
        assertEquals("5-10h", WeeklyReviewEngine.categorizeHoursBracket(5.0f))
        assertEquals("5-10h", WeeklyReviewEngine.categorizeHoursBracket(9.9f))
        assertEquals("10-20h", WeeklyReviewEngine.categorizeHoursBracket(10.0f))
        assertEquals("10-20h", WeeklyReviewEngine.categorizeHoursBracket(19.9f))
        assertEquals("20h+", WeeklyReviewEngine.categorizeHoursBracket(20.0f))
        assertEquals("20h+", WeeklyReviewEngine.categorizeHoursBracket(45.0f))
    }

    @Test
    fun obstacleCategories_allHaveEnglishAndPersianTitles() {
        assertTrue(ObstacleCategory.entries.isNotEmpty())
        for (category in ObstacleCategory.entries) {
            assertTrue("English title must not be blank for $category", category.titleEn.isNotBlank())
            assertTrue("Persian title must not be blank for $category", category.titleFa.isNotBlank())
        }
    }
}
