# Change Contract: WP-BODY-01 — Body Map Visual Atlas Integration & Hardening

**Document ID:** `CONTRACT-WP-BODY-01`  
**Date:** 2026-09-11  
**Author / Lead:** `sajadalireza` (W7 Solo Maintainer)  
**Parent Authorization:** `DECISION-2026-09-11-AX014`  
**Target Issue:** Issue #78 (`WP-BODY-01 — Body Map Visual Atlas Integration & Hardening`)  
**Status:** PUBLISHED & LOCKED  

---

## 1. Verified Baseline

- **Canonical Repository:** `https://github.com/sajadalireza/AXIOM`
- **Canonical Root:** `/Users/sajadalireza/Projects/AXIOM/axiom-canonical`
- **Verified Baseline Commit:** `3507f94c9d001b644fad7eab9875bd4f3ea5f838` (`origin/main`)
- **Working Branch:** `codex/wp-body-01-body-map-integration`
- **Global WIP Invariant:**
  - Issue `#78` is in `state:active` (WIP = 1).
  - Issue `#75` is in `state:suspended` (WIP = 0).
  - Sole WIP-consuming issue across the program is `#78`.

---

## 2. Invariants & Guardrails

1. **Native Vector / Canvas Implementation:** Body Atlas must use native Compose Canvas/vector paths (`BodySilhouetteCanvas.kt` and `BodyMapPaths.kt`). No static raster bitmap screenshots may be embedded as product atlas assets.
2. **Room Database Schema v18 Frozen:** Exactly 27 tables; zero schema migrations, zero table alterations. `NoWp207MigrationGuardTest` must pass.
3. **Offline & Local-First:** Complete functionality operates offline using local SQLite (`muscle_groups`) without remote server dependencies.
4. **Bilingual Parity & RTL:** 100% of user-facing strings localized in English (`values/strings.xml`) and Persian (`values-fa/strings.xml`). RTL layout direction supported natively.
5. **Accessibility (WCAG 2.1 AA):**
   - Minimum interactive touch target size $\ge 48\text{dp}$ for all interactive elements (tabs, muscle hotspots, flip toggle, buttons).
   - TalkBack semantic content descriptions for all muscle groups and recovery states.
   - Clean 200% font scaling with zero clipping or text cutoff.
   - Reduced-motion support for flip and bounce animations.
6. **Zero Sensitive / Medical Telemetry:** Zero free-text body notes, zero medical diagnosis or treatment claims, zero PII.
7. **Design System Tokens:** Canonical Cyber-Fantasy tokens (`LocalAxiomColors.current`, `AxiomSpacing`, `AxiomCard`). Zero raw `Color(0x...)` literals in touched production components.
8. **Rollback Strategy:** Governed by `RB_DISABLE_STAGE_RETAIN_DATA` (navigation/tab hiding toggle without SQLite data destruction).

---

## 3. Non-Goals

1. **Gate G6 / Monetization Proof:** Strictly **NOT AUTHORIZED**. No payment walls, no subscription logic, no pricing cards.
2. **Unrelated G7 Modules:** Strictly remain **FROZEN / HIDDEN**:
   - `AX-013 | Dungeons`
   - `AX-016 | Skill tree`
   - `AX-018 | Leagues`
   - `AX-024 | Decision filter`
3. **No External Sensor / Cloud Sync:** No integration with Google Fit, Health Connect, or external wearables in this packet.

---

## 4. Prior-Work Inspection & Authoritative Recovery Map

**Authoritative Donor:** `/Users/sajadalireza/Downloads/Warrior/warrior_fixed`  
**Governing Evidence Map:** `donor-recovery-evidence/WARRIOR_FIXED_RECOVERY_MANIFEST.md`  

### A. Ported / Reimplemented Assets & Code
| Donor Artifact / Component | Disposition | Action Taken & Hardening Applied |
|---|---|---|
| `BodyMapGeometry.kt` | **PORT & HARDEN** | Ported pure-Kotlin geometry transforms, male/female anatomical aspect scaling, hotspot detection, and trend direction resolution without Android dependencies. |
| `BodyMapAtlasModel.kt` | **PORT & HARDEN** | Ported anatomy profiles, body regions, hotspot models, and view modes. Zero Android framework coupling. |
| `BodyMapPaths.kt` | **HARDEN & PORT** | Ported full 240x640 anatomical vector paths (Head, Neck, Chest, Deltoids, Biceps, Forearms, Core, Quads, Calves, Trapezius, Lats, Lower Back, Glutes, Hamstrings) with symmetric aspect ratio scaling. |
| `BodySilhouetteCanvas.kt` | **HARDEN & RE-TOKENIZE** | Hardened renderer with vector fallback, decoupled dynamic highlight rendering from bitmap BFS loops, and re-tokenized 100% to `LocalAxiomColors.current`. |
| `BodyMapViewModel.kt` | **PRESERVE & HARDEN** | Sound MVI/MVVM pattern maintaining `muscleRepository.getAllMuscleGroups()` with exhaustive `BodyMapUiState` handling. |
| `MuscleStatusPanel.kt` | **HARDEN & RE-TOKENIZE** | Hardened bottom sheet detail panel with training session logging and $\ge 48\text{dp}$ touch targets. |
| `body_{m,f}_light.png` | **PORT** | Ported lightweight base silhouette bitmaps to `drawable-nodpi/` for hardware-accelerated ambient rendering. |
| Unit Test Suites | **PORT & HARDEN** | Ported and expanded `BodyMapGeometryTest.kt` (13 tests), `BodyMapAtlasModelTest.kt` (13 tests), and `BodyMapContractTest.kt` (6 tests). Total 32 tests. |

### B. Reference-Only Components & Dropped Feature Preservation
| Component | Donor Disposition | Canonical AXIOM Action |
|---|---|---|
| `BodyMapScreen.kt` | **REFERENCE ONLY** | Donor screen dropped 4 canonical AXIOM hub features. **REJECTED donor shell.** Reimplemented canonical 4-tab container preserving Recovery Timeline, Caliber Insights, 1RM Strength, and Daily Habits alongside the new Front/Back & Sex visual toggles. |
| `RecoveryTimelineBar.kt` | **PRESERVE** | Preserved canonical recovery timeline and muscle status cards. |
| `CaliberInsightsView.kt` | **PRESERVE** | Preserved canonical Caliber volume / density analytics tab. |
| `StrengthOneRMView.kt` | **PRESERVE** | Preserved canonical Brzycki/Epley 1RM strength calculator tab. |
| Training Session Logging | **PRESERVE** | Preserved workout / set logging directly from muscle detail panel. |

### C. Remediation of Known Donor Deficiencies
1. **Bitmap Retinting Jank Loop:** Donor re-ran an expensive 153,600-pixel BFS loop on every frame during bounce animations whenever selection changed. Remediated: Highlight overlay rendered dynamically via native Canvas overlay (< 0.1 ms); bitmap strength tinting offloaded to `Dispatchers.Default` and cached on `(muscles, sex)`.
2. **First-Frame UI Stall:** Heavy flood-fill decoding in composition caused first-frame jank. Remediated: Background decoding with instant native Compose Canvas vector paths fallback ensuring 0 ms stall.
3. **Draw-Time Allocations:** Donor instantiated Path objects and region lists during Canvas draw passes. Remediated: Hoisted all anatomical paths and atlas regions into static immutable singletons (`StaticFacePath`, `FrontAtlasRegions`, etc.). Zero heap allocations in draw loop.
4. **Hardcoded / Off-Brand Palettes:** Donor contained raw hex colors (`0xFF051624`, `0xFF6257F2`, `0xFF6C56F5`, `0xFF12255C`). Remediated: 100% tokenized to `LocalAxiomColors.current` (`systemGreen`, `legendaryGold`, `penaltyRed`, `rareBlue`, `dimSurface`, `shadowSurface`, `borderFaint`, `voidBlack`). Zero raw color literals in production code.
5. **Forbidden Transfers (§13):** Donor build scripts, static fonts, routing bypasses, personal seed data, and AI egress changes were completely rejected and untransferred.

---

## 5. Affected Files & Boundaries

### Product Code
- `app/src/main/java/com/axiom/app/presentation/bodymap/BodyMapPaths.kt`
- `app/src/main/java/com/axiom/app/presentation/bodymap/BodySilhouetteCanvas.kt`
- `app/src/main/java/com/axiom/app/presentation/bodymap/BodyMapScreen.kt`
- `app/src/main/java/com/axiom/app/presentation/bodymap/BodyMapViewModel.kt`
- `app/src/main/java/com/axiom/app/presentation/bodymap/RecoveryTimelineBar.kt`
- `app/src/main/java/com/axiom/app/presentation/bodymap/MuscleStatusPanel.kt`
- `app/src/main/java/com/axiom/app/presentation/bodymap/CaliberInsightsView.kt`
- `app/src/main/java/com/axiom/app/presentation/bodymap/StrengthOneRMView.kt`
- `app/src/main/java/com/axiom/app/presentation/home/components/BodyStatusSection.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-fa/strings.xml`

### Test Code
- `app/src/test/java/com/axiom/app/presentation/bodymap/BodyMapContractTest.kt` [NEW]

---

## 6. Migration Impact

- **Room Schema Version:** 18 (Unchanged).
- **Migration Count:** 0.
- **Table Count:** 27 (Unchanged).

---

## 7. Analytics & Privacy Impact

- Zero PII collected or transmitted.
- Zero free-text body or medical data logged.
- Only standard, anonymous client navigation lifecycle events permitted.

---

## 8. Rollback Path (`RB_DISABLE_STAGE_RETAIN_DATA`)

If a production defect arises:
1. The `Screen.BodyMap` route in `AwakenNavGraph.kt` and the `tab_physical` item in `AwakenBottomNavBar.kt` can be conditionally hidden or disabled via configuration flag.
2. All underlying user data in the `muscle_groups` SQLite table remains preserved and undamaged.

---

## 9. Acceptance Contract

- **AC-1:** Native Compose Canvas vector body atlas renders front and back views with smooth transition.
- **AC-2:** All interactive controls (tabs, front/back selector, muscle selector, log buttons) meet WCAG 2.1 AA $\ge 48\text{dp}$ touch target guidelines.
- **AC-3:** 100% bilingual parity (English and Persian) with native RTL layout support.
- **AC-4:** Full TalkBack accessibility descriptions for all muscles and recovery states.
- **AC-5:** 200% font scaling supported with zero text clipping.
- **AC-6:** 0 raw color literals (`Color(0x...)`) in touched components; all resolve to `LocalAxiomColors.current`.
- **AC-7:** 100% unit-test pass rate, including `BodyMapContractTest` and `NoWp207MigrationGuardTest`.
- **AC-8:** `lintDebug` reports 0 errors; `assembleDebug` builds successfully.
- **AC-9:** Four independent adversarial reviews score $\ge 9.50/10.0$ with 0 hard caps.

---

## 10. Test Plan

1. **Automated Unit & Contract Tests:**
   - Author `BodyMapContractTest.kt`:
     - Test muscle freshness & recovery calculations across time intervals.
     - Test Front/Back muscle group partitioning.
     - Test touch target hitbox compliance ($\ge 48\text{dp}$).
     - Test string resource key parity between `values/strings.xml` and `values-fa/strings.xml`.
     - Test token compliance (zero raw hex literals in touched files).
     - Test reduced motion fallback.
   - Run: `./gradlew testDebugUnitTest --tests "com.axiom.app.presentation.bodymap.*" --tests "com.axiom.app.db.NoWp207MigrationGuardTest"`
   - Run full regression: `./gradlew testDebugUnitTest`
2. **Static Analysis & Compilation:**
   - `./gradlew lintDebug`
   - `./gradlew assembleDebug`
