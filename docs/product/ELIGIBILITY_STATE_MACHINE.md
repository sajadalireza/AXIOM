# ELIGIBILITY STATE MACHINE (WP-203)

Contract-driven launch routing for fresh, resumed, completed, and established
users. Single source of truth that classifies a user into an explicit
eligibility state from four authoritative startup facts, then maps that state to
the existing `LaunchDestination` navigation authority (WP-201/WP-202 preserved).

- Source: `app/src/main/java/com/axiom/app/presentation/onboarding/EligibilityStateMachine.kt`
- Tests: `.../onboarding/EligibilityStateMachineTest.kt`,
  `.../navigation/EligibilityRoutingNavigationTest.kt`
- Runtime wiring: `SplashViewModel.resolveDestination()` (routing),
  `OnboardingViewModel.beginAwakening()` (streak-init guard).

## Authoritative facts

`EligibilitySnapshot` — recomputed on every launch, never persisted (persisted
eligibility is WP-204):

| Fact | Source | Meaning |
|------|--------|---------|
| `setupComplete` | DataStore `setup_complete` | Language/setup finished |
| `hunterExists` | `HunterRepository.getDirectHunterProfile() != null` | The Hunter ENTITY exists |
| `firstMissionDone` | DataStore `first_mission_done` | First mission completed |
| `blueprintSetupComplete` | DataStore `blueprint_setup_complete` | Blueprint wizard completed |

`hunterExists` is a **prerequisite/entity fact, never evidence of completion**
(Decision A). It is read as a first-class fact so a missing Hunter after earned
progress recovers rather than rendering Home with a null Hunter.

## States

`NEEDS_SETUP · NEEDS_HUNTER · HUNTER_RECOVERY · NEEDS_FIRST_MISSION ·
NEEDS_BLUEPRINT · ESTABLISHED · INVALID`

`INVALID` carries a bounded `InvalidReason`:
`NONE · DOWNSTREAM_WITHOUT_SETUP · HUNTER_WITHOUT_SETUP ·
BLUEPRINT_BEFORE_FIRST_MISSION`.

## Precedence (evaluation order)

1. **Setup** is prerequisite #1. Without it, nothing downstream is legitimate —
   route `SETUP`, keeping later flags (non-destructive, Decision E/F). Reason:
   `HUNTER_WITHOUT_SETUP` if a Hunter exists, else `DOWNSTREAM_WITHOUT_SETUP` if
   any earned flag is set, else `NONE` (genuinely fresh → `NEEDS_SETUP`).
2. **Hunter** is prerequisite #2. Setup done but no Hunter:
   - any earned progress (`firstMissionDone || blueprintSetupComplete`) →
     `HUNTER_RECOVERY` / `ONBOARDING`, `isRecovery = true` (Decision B) — never Home.
   - no earned progress → `NEEDS_HUNTER` / `ONBOARDING`, `isRecovery = false`
     (Decision C, genuinely fresh).
3. **Downstream** (setup + Hunter present): `NEEDS_FIRST_MISSION` →
   `BLUEPRINT_BEFORE_FIRST_MISSION` (INVALID, impossible order) → `NEEDS_BLUEPRINT`
   → `ESTABLISHED`.

A later completion never hides a missing earlier prerequisite.

## Full 16-state transition matrix

S=setupComplete · H=hunterExists · F=firstMissionDone · B=blueprintSetupComplete.

| # | S | H | F | B | State | Reason | Destination | isRecovery |
|---|---|---|---|---|-------|--------|-------------|-----------|
| 1 | F | F | F | F | NEEDS_SETUP | NONE | SETUP | false |
| 2 | F | F | F | T | INVALID | DOWNSTREAM_WITHOUT_SETUP | SETUP | false |
| 3 | F | F | T | F | INVALID | DOWNSTREAM_WITHOUT_SETUP | SETUP | false |
| 4 | F | F | T | T | INVALID | DOWNSTREAM_WITHOUT_SETUP | SETUP | false |
| 5 | F | T | F | F | INVALID | HUNTER_WITHOUT_SETUP | SETUP | false |
| 6 | F | T | F | T | INVALID | HUNTER_WITHOUT_SETUP | SETUP | false |
| 7 | F | T | T | F | INVALID | HUNTER_WITHOUT_SETUP | SETUP | false |
| 8 | F | T | T | T | INVALID | HUNTER_WITHOUT_SETUP | SETUP | false |
| 9 | T | F | F | F | NEEDS_HUNTER | NONE | ONBOARDING | false |
| 10 | T | F | F | T | HUNTER_RECOVERY | NONE | ONBOARDING | true |
| 11 | T | F | T | F | HUNTER_RECOVERY | NONE | ONBOARDING | true |
| 12 | T | F | T | T | **HUNTER_RECOVERY** | NONE | **ONBOARDING** | true |
| 13 | T | T | F | F | NEEDS_FIRST_MISSION | NONE | ONBOARDING | false |
| 14 | T | T | F | T | INVALID | BLUEPRINT_BEFORE_FIRST_MISSION | ONBOARDING | false |
| 15 | T | T | T | F | NEEDS_BLUEPRINT | NONE | BLUEPRINT_WIZARD | false |
| 16 | T | T | T | T | ESTABLISHED | NONE | HOME | false |

Row 12 is THE critical Decision-B case: completed flags with a missing Hunter
used to classify HOME (null-Hunter shimmer). It now recovers via ONBOARDING.

## Streak safety (Decision D)

`shouldInitializeStreak(snapshot) == !firstMissionDone && !blueprintSetupComplete`.

`OnboardingViewModel.beginAwakening()` reads the earned-progress facts **before**
initialization and only calls `preferences.setStreak(0)` when the guard is true.
A recovery/resumed user (any earned flag) keeps their streak; only a genuinely
fresh user is zeroed. `initializeAxiomUseCase` is already idempotent on the Hunter
and non-destructive, so it is safe to reuse for HUNTER_RECOVERY recreation.

## Non-goals / invariants (LOCKED)

- No new screens, no First-Win redesign, no UI polish.
- No Room schema change, no migration, no persisted eligibility (that is WP-204).
- Pure classification only: never mutates Room/DataStore, never resets progress,
  never fabricates completion or personal data (Decision F).
- `LaunchClassifier` and `splashExitRoute` are unchanged; the state machine maps
  onto the existing four `LaunchDestination` values.
