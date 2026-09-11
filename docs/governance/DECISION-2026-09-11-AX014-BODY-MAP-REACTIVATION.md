# Gate Decision: Bounded Reactivation of AX-014 (Physical Body Map)

**Decision ID:** `DECISION-2026-09-11-AX014`  
**Date:** 2026-09-11  
**Authority:** Product Owner (`sajadalireza`)  
**Target Module:** `AX-014 | Physical Body Map`  
**Module Disposition Amendment:** `FREEZE (G7)` $\to$ `BOUNDED_REACTIVATION (WP-BODY-01)`  

---

## 1. Context & Executive Justification

Engineering implementation for Phase D (Gate G5 Retention Proof) across all four work packets (`G5-P1` through `G5-P4`) is complete, merged into canonical `main` (`d591a5e`), and verified with a 100% CI pass rate.

However, Gate G5 cannot be closed because real-world longitudinal retention evidence (21-day Concierge cohort and 30-day Closed Beta cohort observation) requires physical calendar time in the field. Under AXIOM's strict non-negotiable governance:
- **Unit-test fixtures are NOT product evidence.**
- **Synthetic cohort values must never be represented as observed user data.**
- **Issue #75 remains open in `state:blocked`.**

To prevent an uncontrolled idle engineering state without violating the global WIP limit (`WIP = 1`) or fabricating cohort telemetry, the Product Owner authorizes:
1. Formal governance amendment to `docs/governance/WIP_POLICY.md` adding `state:suspended` for externally blocked work;
2. Bounded early reactivation of **`AX-014 | Physical Body Map`** for one dedicated core hardening work packet (`WP-BODY-01`).

---

## 2. Authorized Scope for `WP-BODY-01`

Reactivation of AX-014 is strictly confined to:
1. **Existing Code Consolidation:** Reuse and harden existing canonical implementation files (`BodyMapPaths.kt`, `BodyMapScreen.kt`, `BodyMapViewModel.kt`, `BodySilhouetteCanvas.kt`, `BodyStatusSection.kt`).
2. **Visual Reconstruction Integration:** Align visual styling with `/Users/sajadalireza/Projects/AXIOM/warrior_visual_reconstruction/boards/02_body_atlas_board.png`.
3. **Design System Consolidation:** Refactor legacy color literals and deprecated spacing to canonical Cyber-Fantasy tokens (`LocalAxiomColors.current`, `AxiomSpacing`, `AxiomCard`).
4. **Accessibility & RTL Hardening:** Ensure all interactive touch targets (muscle hotspots, front/back flip toggles, tabs) meet WCAG 2.1 AA $\ge 48\text{dp}$, support TalkBack semantic content descriptions, accommodate 200% font scaling, and support native Persian RTL typography.
5. **Contract Test Suite:** Author `BodyMapContractTest.kt` in `app/src/test/java/com/axiom/app/presentation/bodymap/` (closing the current 0-test defect).
6. **Bounded Runtime Verification:** Verify compilation, lint, and debug assembly with 0 regressions.

---

## 3. Strict Non-Goals & Invariants

This decision does **NOT**:
1. **No Authorization of Phase E / Gate G6:** Monetization, payment value tests, and billing entitlements remain **STRICTLY NOT AUTHORIZED**.
2. **All Unrelated G7 Modules Remain FROZEN / HIDDEN:**
   - `AX-013 | Dungeons`: HIDE
   - `AX-015 | Vitals and daily check-in`: VALIDATE (Cohort only)
   - `AX-016 | Skill tree`: HIDE
   - `AX-017 | Shadows`: VALIDATE
   - `AX-018 | Leagues`: FREEZE
   - `AX-024 | Decision filter`: HIDE
   - All other creator, marketplace, or cloud sync modules remain frozen.
3. **Room Database Schema Freeze:** Room database schema remains strictly pinned at version 18. Zero migrations, zero table additions, zero column alterations (`NoWp207MigrationGuardTest` must pass).
4. **No Medical Diagnosis or Health Claims:** Body Map represents visual biological fatigue and recovery status only; it strictly forbids medical diagnosis, psychiatric claims, or therapeutic promises.
5. **Zero Sensitive / Free-Text Telemetry:** Telemetry is restricted to allowlisted primitive interaction events; no free-text notes, health details, or PII.
6. **Offline & Local-First:** Complete functionality is maintained offline without remote server dependencies.
7. **Rollback Mechanism:** Governed by `RB_DISABLE_STAGE_RETAIN_DATA` (Feature flag/navigation toggle hiding the Body Map tab without data destruction).

---

## 4. Controlled Handoff & WIP Governance Lifecycle

The transition from blocked Gate G5 to `WP-BODY-01` must strictly satisfy:

```text
#75 state:blocked (WIP = 1)
→ explicit Product Owner suspension
→ #75 state:suspended (WIP = 0)
→ immediate activation of WP-BODY-01
→ WP-BODY-01 state:active (WIP = 1)
```

### Post-Successor Completion Lifecycle
When `WP-BODY-01` implementation and acceptance are complete:
- **Scenario A (Live G5 cohort blocker has cleared):**
  `WP-BODY-01 accepted` $\to$ close Body packet $\to$ immediately reactivate #75 to `state:active` $\to$ steady-state WIP returns to exactly 1.
- **Scenario B (Live G5 cohort blocker has NOT cleared):**
  Keep `WP-BODY-01` in `state:accepted` consuming WIP=1 until an explicitly authorized immediate successor handoff exists. **It must NOT be closed leaving the program at WIP=0 for days or weeks.**
