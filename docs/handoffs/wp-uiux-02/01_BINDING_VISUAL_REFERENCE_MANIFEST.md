# WP-UIUX-02 — BINDING VISUAL REFERENCE MANIFEST

**Work Packet:** WP-UIUX-02 (Slice 2: Home Screen Visual Redesign & Core Navigation)

**Owning Gate:** Gate G3 — Core Loop & Data Truth (AX-009 Home and Today, AX-002 App shell and primary navigation)

**Status:** Product Owner Binding Target — corrected 2026-09-16

---

## 1. Product Owner Decision and Selected Target

The Product Owner explicitly rejected the earlier compact Home mockup built around `Customer Problem Interview / COMPLETE MISSION / 1 DAY STREAK` as the binding target. The Product Owner selected the **Mission-first Primary State** direction on 2026-09-16, and confirmed it as the **V3 Pixel Master** workflow.

`01_home_primary_state.png is the WP-UIUX-02 V3 Pixel Master.`

| File | SHA-256 | Role |
|---|---|---|
| `docs/handoffs/wp-uiux-02/references/01_home_primary_state.png` | `4b0b36c360563b9c6a7affbf7b41da4044a0d383ce80b296d932bddbded658b0` | **WP-UIUX-02 V3 Pixel Master** — Authoritative for visual hierarchy, component inventory/order, relative geometry, card proportions, corner radii, typography hierarchy, palette relationships, artwork weight, glow hierarchy, and bottom-dock composition. |
| `docs/handoffs/wp-uiux-02/references/rejected/00_superseded_compact_home_target.jpg` | `57a4825f7c9ae4ec04e0e132255d4191bf9040f00a442c478523a5f7ceac5f2f` | **REJECTED / historical only. Must not be used as a visual target.** |

### Authority and Precedence Rules
1. **Visual Authority**: `01_home_primary_state.png` controls visual layout, spacing, colors, typography scales, card geometry, and bottom-dock styling.
2. **Repository Truth Override**: Canonical repository governance (`PRODUCT_CONSTITUTION.md`, `MODULE_DISPOSITION.md`, `WIP_POLICY.md`) strictly overrides illustrative mockup text/data. Never copy mock values (e.g. `Lv. 12`, `1,240 XP`, fictional quotes/AI output) as product truth.
3. **Gate & Module-Disposition Firewall**: Visual elements implying AX-013 Dungeons, AX-016 Skill Tree, AX-018 Leagues, AX-026 Premium, or unactivated AX-015 Vitals are blocked from Home. Only the bounded exceptions (Daily Check-in, Weekly Analytics in secondary progressive disclosure) are permitted.
4. **Native Overlay Acceptance Method**: Emulator screenshots at comparable viewports must undergo direct overlay comparison against the Pixel Master. Deltas must be classified (Critical, Major, Minor, Justified) and repaired until zero unjustified Critical and zero unjustified Major deltas remain.

### Product Owner-confirmed composition
- AXIOM brand identity at the top; calm/cinematic rather than terminal-like.
- Compact user/Hunter identity with truthful Level/XP progress.
- **NEXT MEANINGFUL MISSION** is the dominant center of gravity.
- Exactly one dominant `Start Mission` / truthful `Continue Mission` CTA.
- Compact `Today` status row beneath the mission hero.
- Xion Insight is subordinate/advisory and never a competing CTA or truth authority.
- Weekly Progress is visible but subordinate to action.
- Floating five-tab navigation remains `Missions / Physical / Home / Shadows / Hunter` for this packet's scope-preservation contract.
- Home is the visual center of the dock. Use restrained Champagne Gold for identity/anchor emphasis and Deep Emerald/Cyan for action/state feedback. Do not copy illustrative mock data as facts.

---

## 2. Binding Visual Hierarchy

### Layer 1 — NOW
1. AXIOM identity / atmospheric header treatment.
2. Compact truthful Hunter/Profile + Level/XP state.
3. Next Meaningful Mission hero card.
4. Exactly one dominant primary mission CTA.

### Layer 2 — TODAY
Layer 2 = truthful G3-safe Today status cards.
AX-015 Daily Check-in remains ONLY the already-approved secondary link in progressive disclosure.
AX-015 Vitals are NOT activated by this packet.

### Layer 3 — XION / PROGRESS
- Xion Insight: subordinate; editable/rejectable/reportable where the canonical Decision Layer applies.
- Weekly Progress / streak / challenge/progress information: visually subordinate to real action.

### Layer 4 — SECONDARY / PROGRESSIVE DISCLOSURE
Preserve existing real secondary Home capabilities without allowing them to compete with the Next Meaningful Mission. Apply current Module Disposition and explicit packet decisions before exposing routes.

---

## 3. Module-Disposition Boundary for Home

The 2026-09-16 Product Owner decision for WP-UIUX-02 is binding:
- `AX-013 Dungeons` — **not reachable from Home** (`HIDE / G7`).
- `AX-016 Skill Tree` — **not reachable from Home** (`HIDE / G7`).
- `AX-018 Leagues` — **not reachable from Home** (`FREEZE / G7`).
- `AX-026 Premium / Entitlements` — **not reachable from Home** (`FREEZE / G6`).
- `AX-015 Daily Check-in` — may remain only as an existing secondary progressive-disclosure link under this packet's bounded PO exception.
- `AX-022 Weekly Analytics` — may remain only as an existing secondary progressive-disclosure link under this packet's bounded PO exception.

This exception does not activate G7/G4/G5, change `MODULE_DISPOSITION`, or authorize new feature behavior.

---

## 4. Visual Inspiration vs Product Truth

Illustrative mockup values such as a portrait/name, `Lv. 12`, `1,240 XP`, `3/4`, `Read 2 pages`, `5–10 min`, `+50 XP`, generated quotes, or generated Xion text are **not canonical product data**. Bind to real local models/state or show an honest empty/unknown state.

Supporting AXIOM boards/photos may be used only to refine component styling, spacing, icon language, emerald/gold/glass treatment, and cross-screen visual consistency. They may not override this Primary State composition or introduce unsupported features.

---

## 5. Non-Negotiable Product Invariants

- One primary CTA on Home.
- Mission-first hierarchy; Goal Progress remains more important than XP.
- No shame/punishment framing.
- Room v18, zero migrations in this packet.
- Zero `domain/` or `data/` behavior changes unless separately authorized.
- No analytics/privacy expansion and no client secrets.
- EN/FA parity, culturally appropriate Persian, RTL, TalkBack, 200% font scaling, >=48dp targets, reduced motion.
- Real Light Theme companion; not simple inversion.
- Local-first/offline behavior preserved.

---

## 6. Acceptance Evidence

The packet is not visually accepted merely because build/tests are green. Final acceptance requires:
1. fresh emulator screenshot at comparable viewport;
2. side-by-side comparison with `01_home_primary_state.png`;
3. explicit review of hierarchy, spacing, card geometry, CTA prominence, header/identity treatment, Today grouping, Xion/progress subordination, palette, and bottom dock;
4. zero unjustified Critical visual deltas;
5. zero unjustified Major visual deltas;
6. every material deviation justified by canonical behavior/accessibility/localization/device constraints;
7. canonical CI green on exact final PR head.
