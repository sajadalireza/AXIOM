package com.axiom.app.presentation.onboarding

import com.axiom.app.domain.firstwin.FirstWinSessionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * WP-208 — First-Win launch policy kill switch and rollback test matrix.
 *
 * Verifies that:
 * 1. Treatment can be switched off without data loss or re-onboarding completed users.
 * 2. An interrupted mid-flow user falls back safely to the bounded legacy destination when treatment is off.
 * 3. A fresh user falls back to legacy onboarding when treatment is off.
 * 4. Resumption and fresh assignment work properly when treatment is active.
 * 5. Recovery and repair rules dominate all flags and session states.
 */
class FirstWinLaunchPolicyRollbackTest {

    private fun eligibility(setup: Boolean, hunter: Boolean, firstMission: Boolean, blueprint: Boolean): EligibilityResult =
        EligibilityStateMachine.evaluate(EligibilitySnapshot(setup, hunter, firstMission, blueprint))

    private fun resolve(
        setup: Boolean, hunter: Boolean, firstMission: Boolean, blueprint: Boolean,
        session: FirstWinSessionStatus?,
        sessionExists: Boolean = session != null,
        isTreatmentActive: Boolean,
    ): LaunchDestination =
        FirstWinLaunchPolicy.resolve(
            eligibility = eligibility(setup, hunter, firstMission, blueprint),
            firstWinSessionStatus = session,
            firstWinSessionExists = sessionExists,
            isTreatmentActive = isTreatmentActive,
        )

    // =========================================================================
    // MUST-ACCEPTANCE 1: Treatment switched off without data loss / no duplicate onboarding
    // =========================================================================

    @Test
    fun completedSession_routesHome_whetherTreatmentIsActiveOrKilled() {
        // Hunter present, setup complete, first mission done
        val destWhenActive = resolve(
            setup = true, hunter = true, firstMission = true, blueprint = true,
            session = FirstWinSessionStatus.COMPLETED,
            isTreatmentActive = true,
        )
        val destWhenKilled = resolve(
            setup = true, hunter = true, firstMission = true, blueprint = true,
            session = FirstWinSessionStatus.COMPLETED,
            isTreatmentActive = false,
        )

        assertEquals("Completed user must route HOME when treatment is active", LaunchDestination.HOME, destWhenActive)
        assertEquals("Completed user must STILL route HOME when treatment is killed (no duplicate onboarding)", LaunchDestination.HOME, destWhenKilled)
    }

    @Test
    fun completedSession_withStaleFirstMissionFlag_stillRoutesHome_evenWhenKilled() {
        // Even if preferences firstMissionDone flag hasn't flipped yet, Room v18 COMPLETED proof routes HOME
        val destWhenKilled = resolve(
            setup = true, hunter = true, firstMission = false, blueprint = false,
            session = FirstWinSessionStatus.COMPLETED,
            isTreatmentActive = false,
        )
        assertEquals("Completed session dominates stale flags even when treatment is killed", LaunchDestination.HOME, destWhenKilled)
    }

    // =========================================================================
    // MUST-ACCEPTANCE 2: Interrupted mid-flow user recovery
    // =========================================================================

    @Test
    fun inProgressSession_resumesFirstWinWhenActive_fallsBackToLegacyWhenKilled() {
        val inProgressStatuses = listOf(
            FirstWinSessionStatus.ACTIVE,
            FirstWinSessionStatus.REWARD_SEEN,
            FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
            FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE,
            null, // sessionExists=true with null or unrecognized status
        )

        for (status in inProgressStatuses) {
            val destWhenActive = resolve(
                setup = true, hunter = true, firstMission = false, blueprint = false,
                session = status,
                sessionExists = true,
                isTreatmentActive = true,
            )
            val destWhenKilled = resolve(
                setup = true, hunter = true, firstMission = false, blueprint = false,
                session = status,
                sessionExists = true,
                isTreatmentActive = false,
            )

            assertEquals("In-progress status $status must resume FIRST_WIN when treatment is active", LaunchDestination.FIRST_WIN, destWhenActive)
            assertEquals("In-progress status $status must fall back to ONBOARDING (legacy destination) when treatment is killed", LaunchDestination.ONBOARDING, destWhenKilled)
        }
    }

    // =========================================================================
    // MUST-ACCEPTANCE 3: Fresh user assignment vs Legacy fallback
    // =========================================================================

    @Test
    fun freshUser_routesFirstWinWhenActive_routesOnboardingWhenKilled() {
        // Setup complete, no hunter yet
        val freshNeedsHunterActive = resolve(
            setup = true, hunter = false, firstMission = false, blueprint = false,
            session = null,
            sessionExists = false,
            isTreatmentActive = true,
        )
        val freshNeedsHunterKilled = resolve(
            setup = true, hunter = false, firstMission = false, blueprint = false,
            session = null,
            sessionExists = false,
            isTreatmentActive = false,
        )

        assertEquals(LaunchDestination.FIRST_WIN, freshNeedsHunterActive)
        assertEquals(LaunchDestination.ONBOARDING, freshNeedsHunterKilled)

        // Setup complete, hunter exists, no first mission yet
        val freshNeedsMissionActive = resolve(
            setup = true, hunter = true, firstMission = false, blueprint = false,
            session = null,
            sessionExists = false,
            isTreatmentActive = true,
        )
        val freshNeedsMissionKilled = resolve(
            setup = true, hunter = true, firstMission = false, blueprint = false,
            session = null,
            sessionExists = false,
            isTreatmentActive = false,
        )

        assertEquals(LaunchDestination.FIRST_WIN, freshNeedsMissionActive)
        assertEquals(LaunchDestination.ONBOARDING, freshNeedsMissionKilled)
    }

    // =========================================================================
    // Invariant Precedence: Recovery and Repair dominate all flags
    // =========================================================================

    @Test
    fun hunterRecovery_alwaysRoutesOnboarding_regardlessOfTreatmentAndSession() {
        for (treatment in listOf(true, false)) {
            for (status in listOf(null, FirstWinSessionStatus.ACTIVE, FirstWinSessionStatus.COMPLETED)) {
                val dest = resolve(
                    setup = true, hunter = false, firstMission = true, blueprint = false,
                    session = status,
                    isTreatmentActive = treatment,
                )
                assertEquals("HUNTER_RECOVERY must always route ONBOARDING (treatment=$treatment, status=$status)", LaunchDestination.ONBOARDING, dest)
            }
        }
    }

    @Test
    fun invalidSetupRepair_alwaysRoutesSetup_regardlessOfTreatmentAndSession() {
        for (treatment in listOf(true, false)) {
            for (status in listOf(null, FirstWinSessionStatus.ACTIVE, FirstWinSessionStatus.COMPLETED)) {
                val dest = resolve(
                    setup = false, hunter = true, firstMission = false, blueprint = false,
                    session = status,
                    isTreatmentActive = treatment,
                )
                assertEquals("INVALID setup must route SETUP (treatment=$treatment, status=$status)", LaunchDestination.SETUP, dest)
            }
        }
    }

    @Test
    fun needsHunterWithCompletedSession_recoversHunterFirst_regardlessOfTreatment() {
        for (treatment in listOf(true, false)) {
            val dest = resolve(
                setup = true, hunter = false, firstMission = false, blueprint = false,
                session = FirstWinSessionStatus.COMPLETED,
                isTreatmentActive = treatment,
            )
            assertEquals("Missing Hunter invariant dominates completed session", LaunchDestination.ONBOARDING, dest)
        }
    }

    @Test
    fun establishedUser_routesHome_regardlessOfTreatment() {
        for (treatment in listOf(true, false)) {
            val dest = resolve(
                setup = true, hunter = true, firstMission = true, blueprint = true,
                session = null,
                sessionExists = false,
                isTreatmentActive = treatment,
            )
            assertEquals("Established user routes HOME", LaunchDestination.HOME, dest)
        }
    }
}
