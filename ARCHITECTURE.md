# AXIOM — Architecture Baseline (WP-106)

> **This document describes the architecture that CURRENTLY EXISTS in the codebase, not an
> idealized target.** It is a code-backed map: every structural claim points to a real
> file and symbol so it can be verified and kept honest. It deliberately records
> inconsistencies and technical debt as first-class facts rather than smoothing them over.
> It is **not** an architecture redesign and prescribes no refactor.

## Evidence legend

Each non-obvious claim is tagged:

- **[VERIFIED]** — read directly from source in this baseline.
- **[KNOWN DEBT]** — a real inconsistency/duplication/leak that exists today; documented, not fixed here.
- **[UNRESOLVED]** — behavior not fully determinable from static reading alone.
- **[DEFERRED]** — intentionally postponed to a later work packet (with pointer).

Code references use `path:symbol` (preferred) or `path:line`. Line numbers are a snapshot of
the WP-106 baseline commit and may drift; the symbol is the durable anchor.

---

## 1. Purpose & Scope

AXIOM (product/package name `com.axiom.app`; the repo/app is also branded "WARRIOR") is a
single-module native Android application: a gamified self-discipline / "hunter" progression
system (missions, XP, ranks, streaks, skill tree, physical vitals, weekly review). This
document is the architecture baseline: subsystem inventory, data-ownership map, critical-path
traces, and the security/AI/analytics/CI boundaries — each bound to concrete code.

Out of scope: product roadmap, visual design, and any change to product source. See
[`docs/product/`](docs/product/) for product intent and [`docs/governance/`](docs/governance/)
for the work-packet process.

---

## 2. System Overview

- **Shape:** one Gradle module (`:app`). No dynamic-feature or library modules. **[VERIFIED]**
- **UI:** 100% Jetpack Compose + Material 3, single-Activity (`MainActivity`), Compose
  Navigation. **[VERIFIED]** — `app/src/main/AndroidManifest.xml` declares exactly one
  launcher activity.
- **Data posture: local-first / offline-first.** The authoritative store is an on-device Room
  database (`axiom.db`) plus a DataStore preferences file. The network (Supabase) is
  **optional** — activation codes, league/leaderboard, cloud sync, blueprint extraction, and
  analytics all no-op gracefully when unconfigured. **[VERIFIED]**
- **No mandatory auth gate.** A fresh install routes through onboarding straight into the
  product; there is no login wall (§6). Supabase sessions default to *anonymous*
  (`AxiomPreferences` `is_anonymous_user` default `true`). **[VERIFIED]**
- **AI is BYO-key and default-disabled.** Direct Gemini egress is fail-closed off by default
  (§13). **[VERIFIED]**

---

## 3. Build & Runtime Baseline

Authoritative build facts live in the Gradle files and the pinned wrapper; this section only
summarizes and links (see [`DEVELOPMENT.md`](DEVELOPMENT.md) and
[`docs/BUILD_PROTOCOL.md`](docs/BUILD_PROTOCOL.md) for the operational detail — not duplicated here).

| Property | Value | Source |
|----------|-------|--------|
| Android Gradle Plugin | 9.1.1 | `DEVELOPMENT.md` / `gradle/` |
| Gradle distribution | 9.3.1 (SHA-256 pinned) | `gradle/wrapper/gradle-wrapper.properties` |
| Kotlin | 2.2.20 | build config |
| compileSdk / targetSdk / minSdk | 36 / 35 / 26 | `app/build.gradle.kts` |
| JDK | 17 (Temurin in CI) | `.github/workflows/ci.yml` |
| DI | Hilt (`@HiltAndroidApp`) | `app/AwakenApplication.kt` |
| Background work | WorkManager (Hilt-integrated) | `AwakenApplication.kt` |

Runtime entry: manifest `application` = `.AwakenApplication`, single launcher activity
`.MainActivity`; one foreground service `com.axiom.app.domain.focus.FocusTimerService`
(`foregroundServiceType="specialUse"`); `allowBackup="false"`; `supportsRtl="true"`. **[VERIFIED]**
Permissions requested: `POST_NOTIFICATIONS`, `INTERNET`, `WAKE_LOCK`, `VIBRATE`,
`FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`. **[VERIFIED]**

---

## 4. Module & Package Map

Single module `:app`, package root `com.axiom.app`. Layering is roughly Clean-ish
(presentation → domain → data) but not strictly enforced — some ViewModels reach past the
repository layer directly into DAOs/DataStore (see §10, §11). **[VERIFIED]**

| Package | Responsibility | Notable members |
|---------|----------------|-----------------|
| `core/` | Cross-cutting: AI egress, security, notifications, sound, diagnostics, feature flags, analytics | `core/ai/*`, `core/security/*`, `core/notification/*`, `core/FeatureFlags.kt`, `core/AnalyticsLogger.kt`, `core/AppInitDiagnostics.kt`, `core/CrashReporter.kt` |
| `data/local/` | Room DB, DAOs, entities, DataStore | `data/local/AxiomDatabase.kt`, `data/local/dao/*` (13 DAOs), `data/local/entity/*` (24 entities), `data/local/AxiomPreferences.kt` |
| `data/local/db/migrations/` | **Real** Room migration bodies (MIGRATION_1_6 … 15_16) | `Migration_*.kt` |
| `db/migrations/` | **Thin aliases** delegating to `data.local.db.migrations.*`; these are what DI wires | `Migration_*.kt` — **[KNOWN DEBT]** dual migration packages (§8) |
| `data/remote/` | Supabase Retrofit service + DTOs | `data/remote/SupabaseClient.kt` |
| `data/repository/` | 14 repository implementations | `*Impl.kt` |
| `domain/repository/` | 14 repository interfaces | one per data concept |
| `domain/engine/` | Pure calculation | `XPEngine`, `MuscleEngine`, `MuscleRecoveryEngine`, `ROIEngine` |
| `domain/usecase/` | Orchestration (22 usecases) | `CompleteMissionUseCase`, `CheckStreakOnOpenUseCase`, `GrantDailyLoginBonusUseCase` |
| `domain/focus/` | Focus-timer service + manager | `FocusTimerService`, `FocusProtocolManager` |
| `domain/model/` | Domain models (27) | `Hunter`, `Mission`, `Skill`, `WarriorPersona` |
| `di/` | Hilt wiring | `di/AppModule.kt` (single module) |
| `navigation/` | Compose nav graph + route defs | `navigation/AwakenNavGraph.kt`, `navigation/Screen.kt` |
| `presentation/` & `ui/` | Compose screens + ViewModels | **[KNOWN DEBT]** screens/VMs are split across BOTH `presentation/*` and `ui/*` with no clean rule (e.g. the primary `MissionsViewModel` lives in `ui/`, most feature screens in `presentation/`) |
| `ui/theme/` | Design tokens, theme | `AwakenTheme`, `ThemeMode` |

---

## 5. Application Startup & Initialization

**`AwakenApplication.onCreate()`** (`app/AwakenApplication.kt`), all steps wrapped defensively
so one failure cannot abort startup: **[VERIFIED]**

1. `AppInitDiagnostics.init(this)` then `CrashReporter.init()`.
2. Manual `WorkManager.initialize(...)` using the injected `HiltWorkerFactory` (the default
   `androidx.startup` WorkManager initializer is **removed** in the manifest via
   `tools:node="remove"`, so Hilt owns worker construction). **[VERIFIED]**
3. `SoundEngine.init(this)` and `AxiomNotificationManager.createNotificationChannel(this)`.
4. On `Dispatchers.IO`: `preferences.migrateGeminiKeyIfNeeded()` (WP-104 SEC-104-003 plaintext→
   encrypted key migration, idempotent/fail-safe), then schedule the 21:00 streak reminder
   **only if** `firstMissionDoneFlow.first()` — a deliberate retention guard so a user who never
   finished onboarding is not sent a "streak == 0" fear notification the first evening. **[VERIFIED]**

**`MainActivity.onCreate()`** (`app/MainActivity.kt`): **[VERIFIED]**

1. `attachBaseContext` applies locale from a **`SharedPreferences("axiom_lang")`** read (key
   `lang`, default `en`) — a synchronous store used because DataStore is async and locale must
   be set before `super.attachBaseContext`. **[KNOWN DEBT]** language is thus persisted in two
   places (see §9/§18).
2. `AppInitDiagnostics.runStartupCheckSequence(...)`.
3. Requests `POST_NOTIFICATIONS` on API 33+. **[KNOWN DEBT]** the same permission is requested
   again from Compose (`MainScreen` `LaunchedEffect`) — duplicated ask.
4. `lifecycleScope.launch`: `checkStreakOnOpenUseCase()`, `vitalsViewModel.checkAppOpenVitals()`,
   `grantDailyLoginBonusUseCase()` (→ ceremony if bonus), `seedDataHelper.seedDefaultProfileIfNeeded()`,
   plus a skills/muscle-group fallback seed when a profile exists but skills are empty.
5. `setContent { AwakenTheme(themeMode) { MainScreen() } }` — `themeMode` collected from
   `preferences.themeModeFlow`.

`MainScreen` (`ui/MainScreen.kt`) creates the `NavController` and hosts `AwakenNavGraph` (§6).

---

## 6. Navigation Architecture

- **Host:** single `NavHost` inside `AwakenNavGraph` (`navigation/AwakenNavGraph.kt`), invoked
  from `MainScreen`. `startDestination = Screen.Splash.route` **always**. **[VERIFIED]**
- **First-destination decision** happens *inside* the `Splash` composable, from two DataStore
  flags (`AwakenNavViewModel` → `AxiomPreferences.firstMissionDoneFlow` /
  `blueprintSetupCompleteFlow`): **[VERIFIED]**

  ```
  nextRoute = when {
      !firstMissionDone       -> Screen.Onboarding
      !blueprintSetupComplete -> Screen.BlueprintWizard
      else                    -> Screen.Home
  }
  ```

- **Routes:** ~29 routes defined in `navigation/Screen.kt`, every one registered in
  `AwakenNavGraph` (no unmapped `Screen`). Args are URL-encoded into route templates
  (`Screen.encode`) and decoded via `Screen.decode`. Feature routes are grouped into nested
  `navigation(...)` subgraphs: `home_graph`, `missions_graph`, `physical_graph`,
  `shadows_graph`, `hunter_graph`. **[VERIFIED]**
- **No auth/login gate.** Onboarding progression is the only gating; activation is optional and
  reachable only from Profile. **[VERIFIED]**

**[KNOWN DEBT] navigation inconsistencies (all real, in code today):**

- `SystemVoice`, `WeeklyReview`, `DecisionFilter`, `Premium` are registered **both** as full
  NavHost composables (in `hunter_graph`) **and** rendered as `ModalBottomSheet` overlays in
  `MainScreen` keyed on `currentRoute` — navigating to one puts the destination on the back
  stack while the modal shows the same content (two live copies of one screen).
- `PlaceholderScreen(title)` in `AwakenNavGraph.kt` is defined but never referenced — dead code.
- The "NAVIGATION HIERARCHY DOCUMENTATION" comment header in `AwakenNavGraph.kt` is stale: it
  lists an `Archive` destination that has no `Screen.Archive`/composable, and mislabels
  `SystemVoice`.
- `CharacterStats` (`character_stats`) is registered but has no visible in-graph entry point.
- `MainScreen.safeNavigate` carries defensive `catch → popBackStack(Home)` fallback and
  `startsWith("dungeon"/"shadow")` tab-matching heuristics — evidence of prior nav crashes.

---

## 7. Dependency Injection & Ownership

- **Single Hilt module** `di/AppModule.kt` (`@InstallIn(SingletonComponent::class)`). It
  `@Binds` all 14 repository interfaces to their impls (`@Singleton`), and a `companion object`
  `@Provides` the `AxiomDatabase` (Room, name `"axiom.db"`, version 16) plus 13 DAOs. **[VERIFIED]**
- **[KNOWN DEBT]** `provideKPIProgressDao` and `provideWeeklyReviewDao` are `@Provides` **without**
  `@Singleton`, unlike the sibling DAO providers — a minor DI inconsistency (new wrapper per
  injection; the underlying DAO is cheap).
- No secrets are injected via DI; runtime config comes from `BuildConfig` (`SUPABASE_URL`,
  `SUPABASE_KEY`, `GEMINI_API_KEY`, …) populated from `.env`/`local.properties` at build time.

---

## 8. Persistence — Room

- **`AxiomDatabase`** (`data/local/AxiomDatabase.kt`): `@Database(version = 16, exportSchema = true)`,
  **24 entities**, 13 DAO accessors. DB file `axiom.db`. **[VERIFIED]**
- **Migration policy is data-safety-first:** `AppModule` registers explicit migrations
  `MIGRATION_1_6 … MIGRATION_15_16` and **deliberately omits** `fallbackToDestructiveMigration`
  — an unresolved migration path must crash loudly (Room `IllegalStateException`) rather than
  silently wipe missions/streaks/financial data. The class header enforces the rule "every
  entity change MUST bump `version` AND add a Migration." **[VERIFIED]** See [ADR-0003](docs/adr/0003-room-no-destructive-migration.md).
- Exported schemas live in `app/schemas/` and are integrity-checked in CI (§17).

**[KNOWN DEBT] dual migration packages.** Migration bodies exist in
`data/local/db/migrations/Migration_*.kt`, but DI wires
`com.axiom.app.db.migrations.MIGRATION_*`, whose files are **thin aliases**
(`val MIGRATION_15_16 = com.axiom.app.data.local.db.migrations.MIGRATION_15_16`). Both packages
are live; the split is redundant indirection, not dead code. Verified via
`grep "data.local.db.migrations"` (referenced only by the alias files + their own package) and
the DI import list in `AppModule.kt`.

---

## 9. Persistence — DataStore & Other Stores

**Three separate on-device stores exist beyond Room** **[KNOWN DEBT — store sprawl]**:

1. **Preferences DataStore `"axiom_prefs"`** (`data/local/AxiomPreferences.kt`, `@Singleton open class`)
   — the primary settings/scalars store. Exposes Flows and suspend mutators for: streak & stats
   (`streak`, `longest_streak`, `last_complete_timestamp`, `stat_*` → `statsFlow`), weekly
   progress, onboarding/briefing booleans, theme (`theme_mode`, default `SYSTEM`), language
   (`language`, default `en`), system-voice mode (default `COLD`), activation/premium/auth
   (`activated`, `is_premium`, `supabase_access_token`, `supabase_user_id`,
   `is_anonymous_user` **default true**, `user_email`), league points, focus-timer state
   (`activeTimerStateFlow`), blueprint/vitals config (water 2500ml, sleep 7.5h, energy floor 6),
   and weekly-review config. **[VERIFIED]**
2. **`SharedPreferences("axiom_lang")`** — locale bootstrap only (`MainActivity.attachBaseContext`,
   also read by `AxiomNotificationManager`). Duplicates the DataStore `language` value. **[KNOWN DEBT]**
3. **`SharedPreferences("axiom_skill_mastery")`** (`AxiomPreferences.kt`) — skill mastery/prestige
   points via imperative int get/set, outside both Room and the Flow model, outside any
   repository. **[KNOWN DEBT]**

**Gemini key is in none of the above.** It is encrypted by `AndroidGeminiKeyStore`
(`core/security/AndroidGeminiKeyStore.kt`): a non-exportable `AES/GCM/NoPadding` key in
`AndroidKeyStore`; ciphertext (12-byte IV + ct) written to `getNoBackupFilesDir`.
`AxiomPreferences.setGeminiApiKey()` encrypts and removes any legacy plaintext;
`migrateGeminiKeyIfNeeded()` verifies the encrypted round-trip before deleting plaintext
(fail-safe). **[VERIFIED]** See §13/§16.

---

## 10. State Source-of-Truth & Repository Ownership

14 repositories (interface in `domain/repository/`, impl in `data/repository/`). Most are a
clean 1:1 wrapper over one DAO/entity, but ownership is **not uniform** — several concepts are
split across stores or bypass the repository layer entirely. **[VERIFIED]**

| Repository | DAO(s) | Room table(s) | Other backing |
|------------|--------|---------------|---------------|
| `HunterRepository` | `HunterDao` | `hunter_profile` | — |
| `MissionRepository` | `MissionDao` | `missions` | — |
| `DungeonRepository` | `DungeonDao` | `dungeons` | — |
| `SkillRepository` | `SkillDao` | `skills` | — |
| `ShadowRepository` | `ShadowDao` | `shadows` | — |
| `MuscleGroupRepository` | `MuscleGroupDao` | `muscle_groups` | — |
| `DailyHabitLogRepository` | `DailyHabitLogDao` | `daily_habit_logs` | — |
| `VitalsRepository` | `VitalLogDao` | `vital_logs` | DataStore |
| `WarriorProfileRepository` | `WarriorBlueprintDao` (god-DAO, **11 tables**) | `warrior_profiles`, `tracks`, `schedule_blocks`, `custom_kpis`, `iron_rules`, `hard_truths_affirmations`, `major_milestones`, `key_relationships`, `financial_checkpoints`, `monthly_income_entries`, `iron_rule_violation_logs` | DataStore |
| `StreakRepository` | — | — (does **not** use `StreakEntity`/`streak`) | **DataStore only** |
| `SystemFeedRepository` | — | — (does **not** use `SystemFeedEntity`) | **In-memory only** (`MutableStateFlow`, cap 50) |
| `ActivationRepository` | — | — | DataStore + Supabase |
| `LeagueRepository` | — | — | DataStore + Supabase |
| `CloudSyncRepository` | 7 DAOs (whole DB) | reads/writes 7 tables for sync | DataStore + Supabase |

**[KNOWN DEBT] ownership hazards (see [ADR-0001](docs/adr/0001-state-source-of-truth.md)):**

- **Streak has two stores.** Live streak = DataStore (`streakFlow`); the `StreakEntity`/`streak`
  Room table is written/read **only** by `CloudSyncRepository` for snapshots. The two can diverge.
- **System feed has two stores.** Runtime feed = in-memory (lost on process death); the
  `SystemFeedEntity` table is touched **only** by cloud sync.
- **Three entities have no repository and are used straight from ViewModels:**
  `KPIProgressDao` (+`KPIMissStreakEntity`) → `WeeklyAnalyticsViewModel`; `WeeklyReviewDao`
  (`weekly_reviews`) → `ProfileViewModel` and `WeeklyReviewViewModel`. These bypass the
  repository abstraction the rest of the app uses.
- **`WarriorBlueprintDao` is a god-DAO** (11 tables under one repository) while every other
  domain gets 1:1 granularity — asymmetric.
- **Duplicated Supabase credential sanitization** (`sanitizeUrl`/`sanitizeKey`/
  `hasSupabaseCredentials`) is copy-pasted across `ActivationRepositoryImpl` and
  `LeagueRepositoryImpl` (and referenced in `CloudSyncRepositoryImpl`) with no shared helper.

---

## 11. Domain Layer — Engines & UseCases

- **Engines** (`domain/engine/`) are pure/stateless calculators: `XPEngine` (rarity/shadow/
  streak multipliers, level-up loops, rank/color/glyph, mastery tiers, instant-gate 3× window),
  `MuscleEngine`, `MuscleRecoveryEngine`, `ROIEngine`. **[VERIFIED]**
- **UseCases** (`domain/usecase/`, 22) orchestrate repositories + engines + preferences.

**[KNOWN DEBT] `CompleteMissionUseCase` is a fat orchestrator, not a thin usecase.** It holds
business logic that arguably belongs in the engine, and duplicates engine math:

- The **streak-multiplier tier thresholds** (1.0 / 1.15 / 1.30 / 1.50) and the **System-Anomaly
  variable-reward RNG** (`Random.nextFloat()` + `tryConsumeSystemAnomalySlot()` daily cap) live
  **inline in the usecase**, not in `XPEngine`.
- **Level-up loops are computed twice** — once inside `XPEngine.calculateXPResult` (to derive
  result flags) and again inline in the usecase (to compute persisted values); the engine's
  `testLevel` is discarded.
- Presentation concerns leak into the **ViewModel** (`ui/MissionsViewModel`): exercise-keyword
  detection, streak check-off gating, and ceremony/sound sequencing.

This is documented as reality; §12-A traces the full sequence.

---

## 12. Critical Journey Sequences

### A. Mission complete → XP → level/streak → persistence **[VERIFIED]**

```
MissionsScreen.onComplete  (also MissionDetailScreen / SuccessContent / FirstMissionViewModel / FocusProtocolManager)
  → ui/MissionsViewModel.completeMission(id)
      → domain/usecase/CompleteMissionUseCase.invoke(id)          [all core logic]
          → MissionRepository.getMissionById            (guard: already COMPLETED → return null)
          → XPEngine.calculateQualityScore / calculateEffectiveHours
          → MissionRepository.updateMission(status=COMPLETED, completedAt=now)   ── writes `missions`
          → HunterRepository.getDirectHunterProfile
          → AxiomPreferences.streakFlow.first()          (streak READ → multiplier tier)
          → XPEngine.calculateXPResult(...)              (rarity×shadow×streak, level-ups, shadow unlock)
          → Random anomaly roll + tryConsumeSystemAnomalySlot()   (bonus XP)
          → SkillRepository.updateSkill                  ── writes `skills`  (+ child auto-unlock ≥50h)
          → HunterRepository.updateHunterProfile         ── writes `hunter_profile` (@Insert REPLACE)
          → ShadowRepository (insert)                    ── writes `shadows`
          → AxiomPreferences.incrementWeeklyMissions / incrementWeeklyRare
          → DungeonRepository.updateDungeon (if mission.dungeonId != null)  ── writes `dungeons`
          → [fire-and-forget] SystemVoiceEngine AI reaction; LeagueRepository.submitScore(hunter.name)
          → AnalyticsLogger.log("mission_completed", {rarity, xp_gained, leveled_up})
      → (result != null) AxiomPreferences.checkOffDailyProtocol()   ── STREAK WRITE (DataStore)
      → ceremony/sound sequencing in the ViewModel
```

Key facts: `completedAt` is written by the usecase; **streak is READ in the usecase but WRITTEN
by the ViewModel** via `AxiomPreferences.checkOffDailyProtocol()` (DataStore) — the
`StreakRepository` interface is bypassed by this path. `HunterDao.updateProfile` is
`@Insert(onConflict=REPLACE)` on the single-row `hunter_profile` table, not `@Update`.

### B. Cold start → first screen **[VERIFIED]**
`AwakenApplication.onCreate` (init + conditional reminder) → `MainActivity.onCreate`
(locale, diagnostics, permission, open-checks, seed) → `MainScreen` → `AwakenNavGraph` Splash →
`when(firstMissionDone, blueprintSetupComplete)` → Onboarding | BlueprintWizard | Home. (§5, §6)

### C. Onboarding chain **[VERIFIED]**
`Onboarding → AwakeningComplete → (firstMissionDone ? Home : FirstMission) →
(blueprintSetupComplete ? Home : BlueprintWizard) → Home`, all pop-inclusive. `Setup` re-routes
via `Splash` + `Activity.recreate()` to apply locale.

### D. Weekly review submit **[VERIFIED]**
`WeeklyReviewScreen → presentation/review/WeeklyReviewViewModel.submitReview` builds a
`WeeklyReviewEntity` (5 steps incl. free-text journal) → `weeklyReviewDao.insertReview`
(`@Insert REPLACE` into `weekly_reviews`) + `preferences.setLastReviewTimestamp`. **No AI, no
network** — journal free-text stays in local Room only.

### E. Ask the SYSTEM (AI) **[VERIFIED]**
`SystemVoiceScreen → SystemVoiceEngine.askSystemStream/askSystem` → offline gate
(`!AiEgressPolicy.isDirectGeminiAllowed() || FORCE_OFFLINE_MODE || !hasApiKey()` → local
fallback) → else `getModel()` (which calls `requireDirectGeminiAllowed()` **before** key
lookup) → Gemini `generateContentStream`. Prompt context excludes name/identity (§13).

---

## 13. AI Egress Boundary (WP-104 SEC-104-001)

Direct client→Gemini egress is a **bounded, fail-closed, default-disabled** capability. See
[ADR-0002](docs/adr/0002-ai-egress-boundary.md) and
[`docs/security/WP-104_SECURITY_AUDIT.md`](docs/security/WP-104_SECURITY_AUDIT.md). **[VERIFIED]**

- **Master flag:** `core/FeatureFlags.kt` `DIRECT_GEMINI_EGRESS_ENABLED = false` (const, default
  build never egresses). `AI_FEATURES_ENABLED = true`, `FORCE_OFFLINE_MODE = false`.
- **Policy:** `core/ai/AiEgressPolicy` — `isDirectGeminiAllowed()` reads the flag;
  `requireDirectGeminiAllowed()` throws `DirectAiEgressDisabledException` **before any key
  lookup or network call**. A `@VisibleForTesting` seam allows tests to exercise the allowed
  path without a second production flag.
- **Choke point:** `core/ai/DirectGeminiGateway.withDirectGemini(apiKey) { … }` returns `null`
  (caller falls back) unless egress is allowed **and** a non-blank key exists — the provider is
  never constructed otherwise.
- **Call-site inventory (every `GenerativeModel`/`generateContent` site is gated):**
  - `core/ai/SystemVoiceEngine.getModel()` → calls `requireDirectGeminiAllowed()` first; covers
    all streaming/generate paths that route through it.
  - `SystemVoiceEngine.generateStructuredMissions` → explicit `requireDirectGeminiAllowed()`
    before constructing the model.
  - `presentation/analytics/WeeklyAnalyticsViewModel.loadAiSummary` → wrapped in
    `DirectGeminiGateway.withDirectGemini(...)`.
  - **Ungated production Gemini call sites: 0.** **[VERIFIED]**
- **Prompt privacy:** outbound context is built by `core/ai/AiPromptContext.hunterContext(...)`
  from numeric stats only (level, rank label, streak, XP, inactive days). Hunter **name/identity
  is deliberately excluded**. The analytics summary prompt uses aggregate counts only.
- **Model id:** single source `GEMINI_MODEL_NAME = "gemini-flash-latest"` (a maintained alias).
- **[DEFERRED]** a full server-gateway / Firebase App Check migration is intentionally postponed
  to a later work packet; the current BYO-key direct path is a temporary containment.

There is also a **server-side** AI path (`SupabaseClient.extractBlueprint` →
`functions/v1/extract-blueprint` Edge Function) that does not use the direct Gemini client and
is outside the `AiEgressPolicy` scope. **[VERIFIED]**

---

## 14. Analytics & Telemetry Boundary

`core/AnalyticsLogger` (`object`, `log(eventName, properties)`): **[VERIFIED]**

- **Transport:** fire-and-forget raw `Thread{}`, POST to `${SUPABASE_URL}/rest/v1/analytics_events`
  with `apikey` + `Authorization: Bearer <SUPABASE_KEY>`, `Prefer: return=minimal`, 3s timeouts;
  failures only reach logcat. **No queue, no retry.**
- **No-op guard:** returns early if `SUPABASE_URL`/`SUPABASE_KEY` are blank or the URL contains
  `"your-project"` — an unconfigured build silently sends nothing.
- **Payload:** `event_name`, `app_version` (`BuildConfig.VERSION_NAME`), `properties` (jsonb,
  every value `.toString()`). No user id, no device id, no client timestamp.
- **Events fired:** `mission_completed` `{rarity, xp_gained, leveled_up}`;
  `onboarding_completed` (no props); `ai_call` `{method, success, reason?}` (`reason` =
  exception class name or `"blank_response"` — **never prompt/response content**);
  `streak_shield_used` / `streak_broken` `{streak_length}`.
- **PII verdict: none via AnalyticsLogger.** Only enum-like constants leave; no name, journal,
  free text, or secret.

**The one identity egress** is a *separate* channel: `LeagueRepositoryImpl.submitScore` sends
`hunterName = hunter.name` + rank to the Supabase leaderboard table (fire-and-forget from
`CompleteMissionUseCase`), gated on Supabase credentials + a bearer token. Weekly-review journal
free-text is **never** transmitted — it stays in local Room (`weekly_reviews`). **[VERIFIED]**

---

## 15. Remote / Supabase Boundary

`data/remote/SupabaseClient` — Retrofit + Moshi + OkHttp, per-base-URL service cache. **[VERIFIED]**
Endpoints: activation-code read/update, GoTrue auth (email/password login+signup, anonymous
signup, anonymous→email upgrade, id-token/Google login, user verify), league RPCs
(`submit_mission_score`, `get_current_season_leaderboard`, waitlist), `user_progress`
read/upsert (cloud sync), and the `extract-blueprint` Edge Function. Analytics posts directly
(not via this service). Sessions default to anonymous.

**Release assurances that remain owner responsibilities (do NOT treat as verified here):**

- **Production Supabase key class: NOT VERIFIED.** The build guard (`SupabaseKeyPolicy`,
  §16) enforces a client-safe class at build time, but the actual production-injected key is
  not verifiable from the repo.
- **Supabase RLS (Row-Level Security): NOT VERIFIED.** Client-key safety ≠ database RLS.
  RLS enforcement on all tables must be confirmed externally before release.

---

## 16. Security Boundaries

Implemented by WP-104; see [`docs/security/WP-104_SECURITY_AUDIT.md`](docs/security/WP-104_SECURITY_AUDIT.md). **[VERIFIED]**

- **AI egress:** default-disabled, fail-closed (§13).
- **Supabase client key:** `core/security/SupabaseKeyPolicy` classifies keys
  (`classify`/`isClientSafe`/`assertClientSafe`) — permitting only `sb_publishable_*` / legacy
  `anon` / empty. A **mirrored build-time guard** `assertSupabaseClientKeySafe()` in
  `app/build.gradle.kts` (invoked during configuration; build scripts cannot import app code)
  **fails the build** on `service_role` / `sb_secret_*` / malformed keys.
- **Gemini key at rest:** `AndroidGeminiKeyStore` (AndroidKeyStore AES/GCM, ciphertext in
  `noBackupFilesDir`); one-time fail-safe migration removes legacy plaintext (§9).
- **Network logging:** `SupabaseClient` uses `Level.BASIC` (debug) / `NONE` (release) and
  `redactHeader("Authorization")` + `redactHeader("apikey")` — credentials never logged.
- **Manifest posture:** `allowBackup="false"`; all `provider`/`service` components
  `exported="false"`; only `MainActivity` exported (launcher).
- **This document contains no secret values** — only classes, flags, and fingerprints.

---

## 17. CI / Release Boundary

- **CI (`.github/workflows/ci.yml`)** — secret-free PR gate, 4 required checks (Unit Tests,
  Lint, Assemble Debug, Room Schema), `pull_request` + `push:main`, `permissions: contents:
  read`, SHA-pinned actions, concurrency cancel-in-progress. Triage runbook:
  [`docs/CI.md`](docs/CI.md). **[VERIFIED]**
- **Release (`.github/workflows/release.yml`)** — `push:[main,master]` + `workflow_dispatch`,
  `contents: write`, consumes signing + backend secrets (`KEYSTORE_BASE64`, `SUPABASE_*`,
  `GEMINI_API_KEY`, `GOOGLE_WEB_CLIENT_ID`). It is **not** a PR check and not a merge gate.
- **[KNOWN OPERATIONAL DEBT]** The release workflow is **fail-closed on missing signing
  material**: without a valid keystore secret the signing step fails, so `push:main` shows a
  red release job by design when secrets are absent. This is expected containment (no unsigned
  artifact ships), not a CI regression. Secondary debt: release.yml pins actions by floating
  tag (`@v4`/`@v3`) rather than SHA, unlike ci.yml.

---

## 18. Known Debt & Deferred Work (register)

Consolidated from the sections above — all present in code at this baseline; none fixed by WP-106.

| # | Item | Kind | Ref |
|---|------|------|-----|
| 1 | Dual Room migration packages (`db/migrations` aliases → `data/local/db/migrations`) | Redundant indirection | §8 |
| 2 | Streak stored in DataStore (live) **and** `StreakEntity` Room (cloud-sync only) | Split ownership | §10, §12-A |
| 3 | System feed in-memory (live) **and** `SystemFeedEntity` Room (cloud-sync only) | Split ownership | §10 |
| 4 | `KPIProgressDao` / `WeeklyReviewDao` used directly from ViewModels (no repository) | Layer bypass | §10 |
| 5 | `WarriorBlueprintDao` god-DAO owns 11 tables | Asymmetric granularity | §10 |
| 6 | Duplicated Supabase sanitize logic across 2-3 impls | Copy-paste | §10 |
| 7 | Language persisted in `SharedPreferences("axiom_lang")` **and** DataStore `language` | Store duplication | §5, §9 |
| 8 | Skill mastery/prestige in a 3rd store (`SharedPreferences("axiom_skill_mastery")`) | Store sprawl | §9 |
| 9 | `CompleteMissionUseCase` fat orchestrator; level-up math duplicated with `XPEngine` | Logic leak / duplication | §11, §12-A |
| 10 | Business/ceremony logic in `MissionsViewModel` | Logic leak | §11 |
| 11 | Screens/VMs split across `presentation/*` and `ui/*` with no rule | Package inconsistency | §4 |
| 12 | 4 routes rendered as both NavHost composable and ModalBottomSheet | Nav duplication | §6 |
| 13 | `PlaceholderScreen` dead code; stale nav-doc header (`Archive`); orphaned `CharacterStats` | Dead/stale | §6 |
| 14 | Duplicated `POST_NOTIFICATIONS` permission request | Redundant | §5 |
| 15 | `KPIProgressDao`/`WeeklyReviewDao` providers not `@Singleton` | DI inconsistency | §7 |
| 16 | Production Supabase key class — **NOT VERIFIED** | Owner release action | §15 |
| 17 | Supabase RLS — **NOT VERIFIED** | Owner release action | §15 |
| 18 | Release workflow fail-closed on missing signing; actions tag-pinned | Operational debt | §17 |
| 19 | Server-gateway / App Check AI migration | **[DEFERRED]** | §13 |

**Baseline commit:** this document reflects `codex/wp-106-architecture-baseline` at the WP-106
worktree HEAD. Line numbers are a snapshot; symbols are the durable anchors.

