# WP-UIUX-02 — Exact Visual Fidelity Matrix

**Work Packet:** WP-UIUX-02 — Home Screen Visual Redesign & Core Navigation
**Validation date:** 2026-09-18
**Branch:** `codex/ui-ux-phase2-home-navigation`
**Base HEAD:** `5d85aeb72b4a798128316a5416e516ad6f2b15a0`
**Source patch SHA-256:** `39a8bf713783afdcd3cf640f5b56a90c77e15127b82be5ed94f0659117026e75`
**Validated APK SHA-256:** `87bde01fafb63aa73dc15c9218a021d10111a27a98c52d5eb4c49bbd22a5fd0b`
**Binding Pixel Master:** `docs/handoffs/wp-uiux-02/references/01_home_primary_state.png`
**Pixel Master SHA-256:** `4b0b36c360563b9c6a7affbf7b41da4044a0d383ce80b296d932bddbded658b0`

> Provenance note: this is **working-tree closure evidence**, not exact final PR-head evidence. The source changes are intentionally uncommitted. A final PR-head capture must be regenerated or provenance-matched after the Product Owner decides to commit.

## 1. Acceptance Contract

Binding hierarchy:

`Identity → Next Meaningful Mission → one primary CTA → Today → Xion → Progress → collapsed secondary disclosure → five-tab dock`.

Runtime data remains truthful and may differ from the illustrative Pixel Master. Device chrome/aspect-ratio differences and accessibility-specific responsive layouts are allowed only when explicitly justified.

## 2. Final Evidence Set

All final captures were produced from the validated APK above on the same Android emulator and current working-tree patch.

| Evidence | SHA-256 | State / purpose |
|---|---|---|
| `55_visual_repair_final_dark_en_100_top.png` | `2caef74763ea489fdbfc3e629ca2e89fd6b213734b8963f4e1f3f18d4b7dad7f` | Primary Dark / EN / 100% Home top |
| `56_visual_repair_final_dark_en_100_lower.png` | `a48da4d36d9540d89a09cf2b8ba7ee3e41bef40a19933e2024c39a7c80fe1b59` | Primary Dark / EN lower hierarchy |
| `57_visual_repair_final_dark_fa_rtl_100_top.png` | `322dcc9b2f6ab757c2d7a346679513975d261d3435f518639e555f5f46fce031` | Persian RTL / Dark / 100% top |
| `58_visual_repair_final_dark_fa_rtl_100_lower.png` | `f837b80076b424a1be804dd1bed77c3000232bf5584ff9fadbeab21d4fb62048` | Persian RTL lower hierarchy |
| `59_visual_repair_final_dark_en_200_top.png` | `59b29f504f53d5549eae25804e712a54c84f268776203a69f1837433b6af48a3` | Dark / EN / 200% font top |
| `60_visual_repair_final_dark_en_200_lower.png` | `9477cb08b5c625f419c6efcd3f08dbbdcedaead9640c32d97fc659cbb344d612` | Dark / EN / 200% lower |
| `61_visual_repair_final_dark_en_200_deep.png` | `2d75d731e5e62225d2063e1dff3b6e8c1d3e8e58542d1f1f86d72a610e8939d1` | 200% deep-scroll / secondary surfaces |
| `62_visual_repair_final_light_en_100_top.png` | `b522c3f036a505e61cf169bf4ad4216bddfc6f47cdf38ddf7c9f800072d93ad6` | Light / EN / 100% top |
| `63_visual_repair_final_light_en_100_lower.png` | `be03fd93abb83959eec5b83cc899f7e4cb733deab87e5687b36b5c30337eb246` | Light / EN lower hierarchy |
| `v3_vs_55_visual_repair_final.png` | `552cdb747403539d83913dd94ec1fc3c566adadca6c55b46ec0e052e09e4deed` | Final direct Pixel Master side-by-side |

The primary comparison uses the same V3 Pixel Master whose hash remains unchanged.

## 3. V3 Major Delta Closure

| ID | Original blocker | Final observation | Final classification |
|---|---|---|---|
| `V3-M01` | Hunter identity anatomy materially too generic / weak. | Header now restores strong AXIOM identity, profile anchor, Hunter/rank anatomy, and gold Level/XP hierarchy. No canonical portrait field is exposed by the current Home `Hunter` model, so an initial-based profile fallback is used rather than inventing a portrait. | **Resolved Major; residual justified Minor** |
| `V3-M02` | Mission hero too tall / text-heavy / weak grouping. | Hero is materially compressed, uses target-like mission label, icon/title/goal/metadata grouping, one dominant emerald CTA, and target-proportional rounded geometry. Runtime title/track/duration/XP remain truthful. | **Resolved** |
| `V3-M03` | Primary viewport did not establish Identity → Mission → Today → Xion → Progress together. | Dark EN 100% final capture establishes all five layers in the primary composition before secondary disclosure; Progress is visible before the dock. | **Resolved** |
| `V3-M04` | Bottom dock too flat; Home anchor weak. | Dock now has floating glass mass, larger radius, gold/emerald treatment, persistent five-tab identity at 100%, and a materially stronger central Home anchor. Existing canonical Home glyph is retained rather than copying the illustrative glyph. | **Resolved Major; residual justified Minor** |
| `V3-M05` | UI too flat; insufficient atmospheric art/depth/glow. | Mountain/celestial artwork now carries through the header/background and Progress card with controlled emerald/gold depth while preserving readable card contrast. | **Resolved** |

### Residual justified differences

- Runtime name, mission, counts, XP and progress values are real state and intentionally differ from mock data.
- No fabricated profile portrait was introduced; Home currently receives no canonical portrait/image field through `Hunter`.
- The existing canonical Home navigation glyph is retained.
- Android system chrome and 1080×2400 device composition differ from the Pixel Master device frame.
- 200% font mode intentionally changes density and nav-label behavior to maintain reachability and avoid clipping.

## 4. Companion-State Validation

### Dark EN — 100%

**PASS.** Final captures 55/56 establish the complete mission-first hierarchy with one primary CTA, subordinate Xion, Progress before secondary disclosure, atmospheric treatment, and the strengthened dock.

### Persian / RTL — Dark 100%

**PASS.** Final captures 57/58 were recaptured after repair. Runtime UI-tree verification confirms RTL mirroring rather than simple text substitution:

- `مأموریت معنادار بعدی` is positioned on the RTL side of the hero.
- `امروز` is aligned to the right edge.
- the first Today metric begins from the right.
- central `خانه` remains centered in the dock.
- translated CTA and Progress strings remain within screen bounds.

### Dark EN — 200% font scale

**PASS with intentional responsive divergence.** Two defects were found during validation and repaired before the final capture set:

1. all five dock labels were rendering at 200% and clipping; at high font scale, inactive tabs are now icon/semantics-only and only the active tab label is rendered;
2. `SHOW DETAILS` broke vertically letter-by-letter in the collapsed secondary surface; at high font scale, that redundant secondary label is suppressed while the accessible toggle/title remains.

Post-fix UI-tree evidence confirms:
- inactive tabs retain accessibility content descriptions;
- only visible `HOME` label is rendered at high font scale;
- `VITALS & PROTOCOLS` fits within `[152,1644][877,1770]`;
- `SHOW DETAILS` is absent from the high-scale render;
- the content remains scroll-reachable through Xion, Progress and secondary disclosure.

### Light EN — 100%

**PASS.** Final captures 62/63 use the same geometry under the real Light companion palette. Theme switching was verified independently with a 1×1 full-frame downsample sanity check:
- Dark top average RGB ≈ `25 / 36 / 30`
- Light top average RGB ≈ `222 / 227 / 221`

This confirms the Light capture is a genuine light companion state rather than a stale Dark screenshot.

## 5. Product / Architecture Invariants

Verified in the repaired source/runtime:

- exactly one dominant Home primary mission CTA;
- mission-first hierarchy preserved;
- five-tab membership/order preserved: Missions / Physical / Home / Shadows / Hunter;
- Xion remains advisory/subordinate;
- Progress precedes progressive disclosure;
- no AX-013 Dungeons, AX-016 Skill Tree, AX-018 Leagues, or AX-026 Premium exposure was added to Home;
- Daily Check-in / Weekly Analytics remain bounded secondary surfaces only;
- no domain/data/schema migration was introduced by this visual repair.

## 6. Build / Test Evidence

- `git diff --check`: **PASS**
- `assembleDebug`: **PASS** after the final accessibility repair
- `HomeModuleDispositionContractTest`: **PASS** after final source changes
- validated APK SHA-256: `87bde01fafb63aa73dc15c9218a021d10111a27a98c52d5eb4c49bbd22a5fd0b`

Existing compiler warnings are outside this visual slice; no new compile error is present.

## 7. Final Visual Decision

- Unresolved Critical visual deltas: **0**
- Unresolved unjustified Major visual deltas: **0**
- Residual differences: **justified / minor**
- Dark EN 100 visual gate: **PASS**
- FA RTL 100 companion gate: **PASS**
- Dark EN 200 accessibility gate: **PASS**
- Light EN 100 companion gate: **PASS**
- WP-UIUX-02 visual repair slice: **VISUAL PASS**

## 8. What Is Not Yet Closed

This document does **not** claim final PR merge authorization because the validated repair remains an uncommitted working tree on base HEAD `5d85aeb...`.

Before marking the Work Packet fully DONE / merge-authorized:

1. Product Owner accepts the final repaired direction;
2. commit the intended source/evidence set;
3. verify the exact committed PR head matches this working-tree patch (or recapture if it differs);
4. rerun the required independent Reviews A/B/C/D on that exact PR head;
5. require canonical CI green on that exact final head.

Until then: **visual blocker is closed; repository/PR closure remains pending.**
