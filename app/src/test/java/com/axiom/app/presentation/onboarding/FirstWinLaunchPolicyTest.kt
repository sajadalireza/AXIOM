package com.axiom.app.presentation.onboarding

import com.axiom.app.domain.firstwin.FirstWinSessionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * WP-207 — First-Win launch policy contract.
 *
 * Exercises [FirstWinLaunchPolicy] over the real [EligibilityStateMachine] output,
 * pinning the precedence and especially the recovery precedence: a lost Hunter
 * (HUNTER_RECOVERY) and any INVALID repair must keep their existing destination
 * regardless of session state, while a completed First-Win session routes Home and
 * a non-completed session resumes First-Win.
 */
class FirstWinLaunchPolicyTest {

    private fun eligibility(setup: Boolean, hunter: Boolean, firstMission: Boolean, blueprint: Boolean): EligibilityResult =
        EligibilityStateMachine.evaluate(EligibilitySnapshot(setup, hunter, firstMission, blueprint))

    private fun resolve(
        setup: Boolean, hunter: Boolean, firstMission: Boolean, blueprint: Boolean,
        session: FirstWinSessionStatus?,
    ): LaunchDestination =
        FirstWinLaunchPolicy.resolve(eligibility(setup, hunter, firstMission, blueprint), session)

    // ---- Recovery precedence: HUNTER_RECOVERY dominates every session state ----

    @Test fun hunterRecovery_staysOnboardingRegardlessOfSession() {
        for (session in listOf<FirstWinSessionStatus?>(
            null,
            FirstWinSessionStatus.ACTIVE,
            FirstWinSessionStatus.REWARD_SEEN,
            FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
            FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE,
            FirstWinSessionStatus.COMPLETED,
        )) {
            assertEquals(
                "hunter recovery must stay Onboarding for session=$session",
                LaunchDestination.ONBOARDING,
                resolve(setup = true, hunter = false, firstMission = true, blueprint = false, session = session)
            )
        }
    }

    // ---- Repair precedence: INVALID keeps its existing destination ----

    @Test fun invalidSetupRepair_staysSetupRegardlessOfSession() {
        for (session in listOf<FirstWinSessionStatus?>(
            null,
            FirstWinSessionStatus.ACTIVE,
            FirstWinSessionStatus.COMPLETED,
        )) {
            assertEquals(
                "setup repair must stay Setup for session=$session",
                LaunchDestination.SETUP,
                resolve(setup = false, hunter = false, firstMission = true, blueprint = false, session = session)
            )
        }
    }

    @Test fun invalidBlueprintBeforeFirstMission_staysOnboardingRegardlessOfSession() {
        for (session in listOf<FirstWinSessionStatus?>(
            null,
            FirstWinSessionStatus.ACTIVE,
            FirstWinSessionStatus.COMPLETED,
        )) {
            assertEquals(
                "blueprint-before-first-mission must stay Onboarding for session=$session",
                LaunchDestination.ONBOARDING,
                resolve(setup = true, hunter = true, firstMission = false, blueprint = true, session = session)
            )
        }
    }

    // ---- COMPLETED session => HOME (overrides the legacy blueprint state) ----

    @Test fun completedSession_isHome_overridesNeedsBlueprint() =
        assertEquals(
            LaunchDestination.HOME,
            resolve(setup = true, hunter = true, firstMission = true, blueprint = false, session = FirstWinSessionStatus.COMPLETED)
        )

    @Test fun completedSession_missingHunter_neverRoutesHome() =
        assertEquals(
            LaunchDestination.ONBOARDING,
            resolve(setup = true, hunter = false, firstMission = false, blueprint = false, session = FirstWinSessionStatus.COMPLETED)
        )

    @Test fun completedSession_withHunter_isHome_evenIfDataStoreFirstMissionMirrorIsStale() =
        assertEquals(
            LaunchDestination.HOME,
            resolve(setup = true, hunter = true, firstMission = false, blueprint = false, session = FirstWinSessionStatus.COMPLETED)
        )

    // ---- Non-completed session => FIRST_WIN (resume) ----

    @Test fun nonCompletedSession_isFirstWin() {
        for (session in listOf(
            FirstWinSessionStatus.ACTIVE,
            FirstWinSessionStatus.REWARD_SEEN,
            FirstWinSessionStatus.HANDOFF_WITH_SCHEDULE,
            FirstWinSessionStatus.HANDOFF_WITHOUT_SCHEDULE,
        )) {
            assertEquals(
                "non-completed session must resume First-Win for session=$session",
                LaunchDestination.FIRST_WIN,
                resolve(setup = true, hunter = false, firstMission = false, blueprint = false, session = session)
            )
        }
    }

    @Test fun nonCompletedSession_dominatesLegacyBlueprintState() =
        assertEquals(
            LaunchDestination.FIRST_WIN,
            resolve(setup = true, hunter = true, firstMission = true, blueprint = false, session = FirstWinSessionStatus.ACTIVE)
        )

    // ---- No session + first-win-eligible => FIRST_WIN (fresh assignment) ----


    @Test fun existingSession_withUnknownStatus_failClosedToFirstWin_beforeBlueprint() {
        val eligibility = eligibility(true, true, true, false)
        assertEquals(
            LaunchDestination.FIRST_WIN,
            FirstWinLaunchPolicy.resolve(eligibility, firstWinSessionStatus = null, firstWinSessionExists = true),
        )
    }

    @Test fun establishedUser_withUnknownExistingSession_failClosedToFirstWin() {
        val eligibility = eligibility(true, true, true, true)
        assertEquals(
            LaunchDestination.FIRST_WIN,
            FirstWinLaunchPolicy.resolve(eligibility, firstWinSessionStatus = null, firstWinSessionExists = true),
        )
    }

    @Test fun noSession_needsHunter_isFirstWin() =
        assertEquals(
            LaunchDestination.FIRST_WIN,
            resolve(setup = true, hunter = false, firstMission = false, blueprint = false, session = null)
        )

    @Test fun noSession_needsFirstMission_isFirstWin() =
        assertEquals(
            LaunchDestination.FIRST_WIN,
            resolve(setup = true, hunter = true, firstMission = false, blueprint = false, session = null)
        )

    // ---- No session + legacy => existing destination ----

    @Test fun noSession_needsSetup_staysSetup() =
        assertEquals(
            LaunchDestination.SETUP,
            resolve(setup = false, hunter = false, firstMission = false, blueprint = false, session = null)
        )

    @Test fun noSession_needsBlueprint_staysBlueprintWizard() =
        assertEquals(
            LaunchDestination.BLUEPRINT_WIZARD,
            resolve(setup = true, hunter = true, firstMission = true, blueprint = false, session = null)
        )

    @Test fun noSession_established_staysHome() =
        assertEquals(
            LaunchDestination.HOME,
            resolve(setup = true, hunter = true, firstMission = true, blueprint = true, session = null)
        )

    // ---- Determinism ----

    @Test fun sameInputs_sameDestination() {
        repeat(5) {
            assertEquals(
                LaunchDestination.HOME,
                resolve(setup = true, hunter = true, firstMission = true, blueprint = false, session = FirstWinSessionStatus.COMPLETED)
            )
        }
    }
}
