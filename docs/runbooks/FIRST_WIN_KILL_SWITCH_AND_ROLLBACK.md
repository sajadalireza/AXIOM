# AXIOM Runbook: First-Win Kill Switch and Rollback (WP-208)

## 1. Document Control
- **Document:** Operational Runbook — First-Win Kill Switch and Rollback
- **Version:** `1.0.0`
- **Gate:** G2 — First-Win Vertical Slice
- **Work Packet:** WP-208 (#31)
- **Status:** Canonical Operational Baseline
- **Author / Accountable Owner:** `sajadalireza`
- **Effective Date:** `2026-09-11`

---

## 2. Objective & Scope
This runbook defines the operational procedures, trigger thresholds, and rollback mechanics for disabling the First-Win vertical slice (`Screen.FirstWin.route`) and falling back safely to the bounded legacy onboarding flow (`Screen.Onboarding.route`).

### Non-Negotiable Invariants
1. **Zero Data Loss:** Disabling First-Win or reverting code must NEVER delete or corrupt Room v18 database tables (`first_win_session`, `completion_receipt`, `schedule_blocks`, `missions`, `hunters`).
2. **No Duplicate Onboarding:** Users who have already completed First-Win (`FirstWinSessionStatus.COMPLETED`) must remain routed to `LaunchDestination.HOME`. They must NEVER be re-routed through onboarding upon kill switch engagement.
3. **Zero Assignment Drift:** Under normal operation, assigned variants (`TREATMENT` vs `CONTROL`) remain strictly sticky in DataStore across all app restarts.
4. **Bounded Legacy Fallback:** When treatment is killed or assigned to `CONTROL`, eligible users are directed to `LaunchDestination.ONBOARDING` (the proven legacy onboarding and first-mission flow).

---

## 3. Control Plane Architecture

```
                  ┌──────────────────────────────────────────────────┐
                  │                 SplashViewModel                  │
                  └─────────────────────────┬────────────────────────┘
                                            │
                             Queries isTreatmentActive()
                                            │
                                            ▼
                  ┌──────────────────────────────────────────────────┐
                  │               FirstWinControlPlane               │
                  └───────────┬──────────────────────────┬───────────┘
                              │                          │
                   Evaluates Kill Switches     Reads/Assigns Sticky Variant
                              │                          │
              ┌───────────────┴───────────────┐          ▼
              │                               │   ┌──────────────────────────────┐
              ▼                               ▼   │      AxiomPreferences        │
   ┌───────────────────────┐     ┌────────────┐   │ - first_win_variant          │
   │   FeatureFlags        │     │ DataStore  │   │ - assigned_eligibility_ver=1 │
   │ FIRST_WIN_LOCAL_KILL  │     │ RemoteKill │   │ - assignment_timestamp       │
   └───────────────────────┘     └────────────┘   └──────────────────────────────┘
```

### Key Components
- **`FirstWinControlPlane`:** Central authority governing variant assignment (`getOrAssignVariant()`) and treatment status (`isTreatmentActive()`).
- **`ELIGIBILITY_VERSION = 1`:** Pinned eligibility contract version stored with the variant.
- **Local Kill Switch (`FeatureFlags.FIRST_WIN_LOCAL_KILL`):** In-code toggle for testing and emergency compile-time kill.
- **Remote Kill Switch (`AxiomPreferences.firstWinRemoteKillFlow`):** Persisted flag (`first_win_remote_kill_active`) enabling instant over-the-air suppression without client redeployment.

---

## 4. Rollback Trigger Criteria

Engage this runbook immediately if any of the following conditions occur during Alpha (WP-209) or subsequent rollout:

| Severity | Metric / Signal | Threshold | Action |
|---|---|---|---|
| **S1 — Blocker** | Crash-free sessions on First-Win screen | `< 99.0%` | **Immediate Level 1 Remote Kill** |
| **S1 — Blocker** | Database corruption or migration failure | `> 0` occurrences | **Immediate Level 1 Remote Kill + PR Revert** |
| **S2 — Major** | First-Mission Completion (FMC) | `< 65%` over 24h | **Engage Level 1 Kill for investigation** |
| **S2 — Major** | ANR rate on First-Win or Splash | `> 0.5%` | **Engage Level 1 Kill** |
| **S3 — Minor** | Persisted assignment drift detected | `> 0` occurrences | **Investigate; engage Level 2 if reproducible** |

---

## 5. Rollback Procedures

### Level 1: Remote Kill Switch Engagement (Instantaneous, Zero-Downtime)
*Use this when immediate mitigation is required without deploying a new app binary.*

1. **Activate Remote Kill Flag:**
   Update the remote configuration or trigger client preferences sync to set:
   ```kotlin
   first_win_remote_kill_active = true
   ```
2. **Runtime Effect:**
   - `FirstWinControlPlane.isTreatmentActive()` immediately returns `false`.
   - `FirstWinLaunchPolicy.resolve(...)` automatically diverts fresh users (`NEEDS_HUNTER`, `NEEDS_FIRST_MISSION`) to `LaunchDestination.ONBOARDING`.
   - Mid-flow users who were interrupted in First-Win fall back to `LaunchDestination.ONBOARDING` without crashing or losing their created Hunter or mission.
   - Completed users (`COMPLETED` session) continue routing directly to `LaunchDestination.HOME`.
   - **No Room data is deleted or mutated.**

---

### Level 2: Local Kill Switch & Hotfix Release (Emergency Patch)
*Use this when releasing an emergency patch binary to Cafe Bazaar / Myket / APK distribution.*

1. **Modify `FeatureFlags.kt`:**
   ```kotlin
   // Set local kill switch to true:
   var FIRST_WIN_LOCAL_KILL = true
   ```
2. **Build and Verify Release APK:**
   ```bash
   ./gradlew assembleRelease
   ```
3. **Deploy Patch Version:**
   - Tag release `v1.x.x-hotfix-firstwin-killed`.
   - Distribute update. All installations running this binary will bypass First-Win and use legacy onboarding.

---

### Level 3: Git PR Revert Runbook (Full Code Revert)
*Use this if the vertical slice code itself must be reverted from `main`.*

1. **Safety Pre-check:**
   - Room schema version is `18`. Reverting PR #48 or WP-208 commits leaves Room database tables intact (`first_win_session`, `completion_receipt`, `schedule_blocks`).
   - Android Room ignores unused tables unless a destructive migration is explicitly triggered. Since ADR-0003 prohibits destructive migrations, all historical data remains safe.
2. **Execute Revert:**
   ```bash
   git checkout -b revert/wp-208-first-win-control origin/main
   git revert -m 1 <PR_MERGE_COMMIT_HASH>
   git push origin revert/wp-208-first-win-control
   ```
3. **Open Emergency PR:**
   - Title: `fix: emergency revert of First-Win slice`
   - Ensure all 4 CI checks pass (Unit Tests, Lint, Assemble Debug, Room Schema).
   - Merge into `main`.

---

## 6. Post-Rollback Health & Verification Checklist

After executing Level 1, 2, or 3 rollback:

- [ ] **Fresh Install Test:** Install APK on clean device; verify app launches into `LanguageThemeSetupScreen` -> `OnboardingScreen` (legacy flow).
- [ ] **Completed User Test:** Install over existing device with completed First-Win; verify app routes directly to `HomeScreen` (Home tab) without replaying onboarding.
- [ ] **Interrupted User Test:** Install over device with in-progress First-Win session; verify app routes to `OnboardingScreen` / `FirstMissionScreen` with existing Hunter profile preserved.
- [ ] **Database Integrity Check:** Verify `sqlite3 /data/data/com.axiom.app/databases/axiom.db "PRAGMA integrity_check;"` returns `ok`.
- [ ] **Telemetry Verification:** Verify analytics events reflect `legacy_onboarding` and zero First-Win crashes.

---

## 7. Restoration / Recovery Procedure

Once root cause is identified and patched:
1. Validate fix via unit tests and test matrix.
2. In remote config / test harness, flip:
   ```kotlin
   first_win_remote_kill_active = false
   ```
3. Verify that fresh users resume entering `FirstWinScreen` and existing sticky assignments remain intact.
