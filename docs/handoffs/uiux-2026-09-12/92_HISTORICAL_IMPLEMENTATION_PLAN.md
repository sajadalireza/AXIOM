# Implementation Plan — AXIOM UI/UX Redesign (Phase 1: Core Surface Polish)

## Goal Description
Transform AXIOM's primary user interface from developer-prototype styling ("Developer Art") into the approved high-fidelity modern dark sci-fi consumer product experience evidenced in `UI_REDESIGN_PROPOSAL.md`.

This plan focuses on Phase 1: **Fixing critical visual glitches, eliminating typography chaos, harmonizing the color palette, and polishing the Home Screen and Body Map components**.

---

## User Review Required

> [!IMPORTANT]
> - **Zero Logic Mutation:** All underlying Room database entities, state flows, repositories, and calculation engines remain 100% untouched.
> - **Room Invariant:** Schema frozen at v18 (27 tables, 0 migrations).
> - **Accessibility Preservation:** Minimum $48\text{dp}$ touch target compliance and semantic labels will be maintained across all redesigned buttons.

---

## Proposed Changes

### Component 1: Critical Visual Glitches & Overlap Fixes

#### [MODIFY] [CompanionXionWidget.kt](file:///Users/sajadalireza/Projects/AXIOM/axiom-canonical/app/src/main/java/com/axiom/app/ui/components/CompanionXionWidget.kt)
- Fix the speech bubble clipping bug that caused `1\nmissi\nons` text wrapping by enforcing minimum readable width, single-line or whole-word wrapping (`overflow = TextOverflow.Ellipsis`), and proper padding.
- Prevent speech bubble from rendering when text is empty or truncated.
- Ensure the floating orb does not overlap interactive cards or bottom navigation targets.

#### [MODIFY] [MainHUD.kt](file:///Users/sajadalireza/Projects/AXIOM/axiom-canonical/app/src/main/java/com/axiom/app/ui/MainHUD.kt)
- Redesign the top status bar: replace the raw monospace ASCII look with a sleek profile row (Avatar, Hunter title, pill badge `RECRUIT I`, and a refined gradient emerald XP bar).
- Fix padding on the right edge so rank badges are not clipped against the screen margin.

#### [MODIFY] [CelebrationDialog.kt](file:///Users/sajadalireza/Projects/AXIOM/axiom-canonical/app/src/main/java/com/axiom/app/ui/ceremony/CelebrationDialog.kt)
- Fix the modal backdrop so background text (`NO ACTIVE MISSIONS`) does not collide or bleed through the foreground level-up announcement (`Hunter Level 2`).
- Increase scrim opacity and add a dark solid container background behind the dialog.

---

### Component 2: Home Screen Visual Modernization

#### [MODIFY] [HomeScreen.kt](file:///Users/sajadalireza/Projects/AXIOM/axiom-canonical/app/src/main/java/com/axiom/app/presentation/home/HomeScreen.kt)
- **Hero Mission Card:** Replace the flat bordered box with the approved modern card layout: bold title, time estimate (`25 min`), reward pill (`+230 XP`), and a prominent glowing emerald button (`COMPLETE MISSION` / `+ COMMIT TO A MISSION`).
- **Typography Unification:** Eliminate the outdated serif `HUNTER` font and redundant string `RECRUIT-RANK-RANK`; replace with unified geometric typography.
- **Streak & Momentum Card:** Replace the blurred orb with the clean amber flame icon and 7-day dot progress timeline matching the mockup.
- **Remove Bracket Noise:** Clean up micro-copy by removing bracket clutter (`[ NEXT MEANINGFUL MISSION ]` -> `Active Mission`, `[ VITALS & PROTOCOLS ]` -> `Vitals & Protocols`).

---

### Component 3: Navigation Bar & Notification De-cluttering

#### [MODIFY] [MainScreen.kt](file:///Users/sajadalireza/Projects/AXIOM/axiom-canonical/app/src/main/java/com/axiom/app/ui/MainScreen.kt)
- Suppress premature red alert notification badges on bottom navigation icons on clean install so users are not overwhelmed with artificial notifications on Day 1.
- Refine bottom bar icons with minimalist stroke aesthetics and soft green indicator dots for the active tab.

---

### Component 4: Body Map Palette Harmonization

#### [MODIFY] [BodyMapScreen.kt](file:///Users/sajadalireza/Projects/AXIOM/axiom-canonical/app/src/main/java/com/axiom/app/presentation/bodymap/BodyMapScreen.kt)
- Replace the high-visibility yellow toggle buttons (`Front/Back`, `Male/Female`) with sleek dark pills featuring subtle emerald active borders.
- Align muscle freshness cards (`Chest: 100% Recovered`, `Back: 95% Ready`) with refined mini progress indicators matching the approved mockup.

---

## Verification Plan

### Automated Tests
1. **Accessibility Contract Tests:**
   ```bash
   ./gradlew testDebugUnitTest --tests "com.axiom.app.ui.accessibility.AccessibilityContractTest"
   ```
2. **Room Schema & Invariant Tests:**
   ```bash
   ./gradlew testDebugUnitTest --tests "com.axiom.app.db.NoWp207MigrationGuardTest"
   ```
3. **Full Unit Test & Build Verification:**
   ```bash
   ./gradlew testDebugUnitTest assembleDebug
   ```

### Manual Visual Verification
- Deploy debug build to live Android emulator (API 34).
- Capture screenshots of the redesigned Home Screen and Body Map.
- Verify zero text clipping, zero widget collisions, and full visual alignment with `UI_REDESIGN_PROPOSAL.md`.
