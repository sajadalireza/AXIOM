# WP-UIUX-02 — Exact Visual Fidelity Matrix

**Work Packet:** WP-UIUX-02 — Home Screen Visual Redesign & Core Navigation
**Validation date:** 2026-09-18
**Branch:** `codex/ui-ux-phase2-home-navigation`
**Source baseline:** `5d85aeb72b4a798128316a5416e516ad6f2b15a0`
**Pre-final-fix checkpoint commit:** `0d12d8a80ac51eb4b11b2c9d4569f79008071118`
**Validated source patch SHA-256:** `5e2c9f6708ef7e29e0675a37ac38460b591265abfc5a7b16f11ea21736dc3d56`
**Validated APK SHA-256:** `c1ef5b8197cea110211cb222720abc3cad700faf397d2410ce9503a5dd2e54c4`
**Binding Pixel Master:** `docs/handoffs/wp-uiux-02/references/01_home_primary_state.png`
**Pixel Master SHA-256:** `4b0b36c360563b9c6a7affbf7b41da4044a0d383ce80b296d932bddbded658b0`

> Provenance rule: the validated source state is defined by the source baseline plus the source-patch hash above, not by a self-referential commit hash embedded in this document. The final accessibility repair was captured from that exact source state and APK. After commit, exact-head equivalence is established by recomputing the same source diff hash; if it remains `5e2c9f...`, the committed source is byte-equivalent to the validated candidate and no visual recapture is required.

## 1. Acceptance Contract

Binding hierarchy:

`Identity → Next Meaningful Mission → one primary CTA → Today → Xion → Progress → collapsed secondary disclosure → five-tab dock`.

Runtime data remains truthful and may differ from the illustrative Pixel Master. Device chrome/aspect-ratio differences and accessibility-specific responsive layouts are allowed only when explicitly justified.

## 2. Final Evidence Set

All final captures below were regenerated from APK SHA-256 `c1ef5b8197cea110211cb222720abc3cad700faf397d2410ce9503a5dd2e54c4` on the Android emulator at 1080×2400.

| Evidence | SHA-256 | State / purpose |
|---|---|---|
| `55_visual_repair_final_dark_en_100_top.png` | `9200d7e15c874439a283879e4b0baff4fb476337b3b887fa34dd7df994ffa8d0` | Primary Dark / EN / 100% Home top |
| `56_visual_repair_final_dark_en_100_lower.png` | `df00607a61703017c99761404e06ed383c56212f121220a123b393e961277c63` | Primary Dark / EN lower hierarchy |
| `57_visual_repair_final_dark_fa_rtl_100_top.png` | `dc4db30a3b310aa63a88ac67e2fe680d334105c2700ad332c5f099a55b167dc8` | Persian RTL / Dark / 100% top |
| `58_visual_repair_final_dark_fa_rtl_100_lower.png` | `c928b89fbd8613b1c25a9f09eb813b15367b260233459d025de61a626d674680` | Persian RTL lower hierarchy |
| `59_visual_repair_final_dark_en_200_top.png` | `c94b7400acfd9baa944e0d1b989c3131fc87f103f282c254025cba2c1a912c4f` | Dark / EN / 200% font top |
| `60_visual_repair_final_dark_en_200_lower.png` | `1be7bdccfacd845112b6ee492ac5b10a6f13199caab77a51a59707294bf34549` | Dark / EN / 200% lower |
| `61_visual_repair_final_dark_en_200_deep.png` | `ec5fef28161b085f32de68e22c5fe68552201b46aaa6c7f31c69230c67ec3a8f` | 200% deep-scroll / secondary surfaces |
| `62_visual_repair_final_light_en_100_top.png` | `81aa180d251c45237435d3d225c2b06437ab218007b44a7d0c7793d1b9f88fc0` | Light / EN / 100% top |
| `63_visual_repair_final_light_en_100_lower.png` | `f746e2383540db6c54912f736728c2741787025af752f44bb46ae0bf54f2cf6f` | Light / EN lower hierarchy |
| `v3_vs_55_visual_repair_final.png` | `0eb9410c573d1c2008b61c1d68420eb8a642766279ae3ed14b2eb60277c332a6` | Final direct Pixel Master side-by-side |

The primary comparison uses the unchanged V3 Pixel Master with SHA-256 `4b0b36c360563b9c6a7affbf7b41da4044a0d383ce80b296d932bddbded658b0`.

## 3. V3 Major Delta Closure

| ID | Original blocker | Final observation | Final classification |
|---|---|---|---|
| `V3-M01` | Hunter identity anatomy materially too generic / weak. | Header restores strong AXIOM identity, profile anchor, Hunter/rank anatomy, and gold Level/XP hierarchy. No canonical portrait field is exposed by the current Home `Hunter` model, so an initial-based profile fallback is used rather than inventing a portrait. | **Resolved Major; residual justified Minor** |
| `V3-M02` | Mission hero too tall / text-heavy / weak grouping. | Hero is materially compressed, uses target-like mission label, icon/title/goal/metadata grouping, one dominant emerald CTA, and target-proportional rounded geometry. Runtime title/track/duration/XP remain truthful. | **Resolved** |
| `V3-M03` | Primary viewport did not establish Identity → Mission → Today → Xion → Progress together. | Dark EN 100% final captures establish the mission-first hierarchy before secondary disclosure; Progress remains above the dock / progressive disclosure boundary. | **Resolved** |
| `V3-M04` | Bottom dock too flat; Home anchor weak. | Dock now has floating glass mass, larger radius, gold/emerald treatment, persistent five-tab identity at 100%, and a materially stronger central Home anchor. Existing canonical Home glyph is retained rather than copying the illustrative glyph. | **Resolved Major; residual justified Minor** |
| `V3-M05` | UI too flat; insufficient atmospheric art/depth/glow. | Mountain/celestial artwork carries through the header/background and Progress card with controlled emerald/gold depth while preserving readable card contrast. | **Resolved** |

### Residual justified differences

- Runtime name, mission, counts, XP and progress values are real state and intentionally differ from mock data.
- No fabricated profile portrait was introduced; Home currently receives no canonical portrait/image field through `Hunter`.
- The existing canonical Home navigation glyph is retained.
- Android system chrome and 1080×2400 device composition differ from the Pixel Master device frame.
- 200% font mode intentionally changes density and nav-label behavior to preserve reachability and prevent clipping.

## 4. Companion-State Validation

### Dark EN — 100%

**PASS.** Captures 55/56 show the repaired mission-first hierarchy, one primary CTA, subordinate Xion, Progress before secondary disclosure, atmospheric treatment, and the strengthened dock.

### Persian / RTL — Dark 100%

**PASS.** Captures 57/58 were regenerated after the final accessibility repair. A premature first FA frame was rejected and recaptured after the UI settled.

Runtime UI-tree verification confirms genuine RTL/localized rendering:

- `مأموریت معنادار بعدی` renders in the hero;
- `ادامه مأموریت` is localized;
- `امروز` and `پیشرفت` render in Persian;
- central `خانه` remains centered in the dock;
- Home accessibility content description is `خانه`;
- translated hero / Today / Progress surfaces remain within the 1080px viewport.

### Dark EN — 200% font scale

**PASS with intentional responsive divergence.** Four accessibility defects were found across the repair cycle and closed before final acceptance:

1. all five dock labels rendered at 200% and clipped; inactive tabs are now icon/semantics-only while only the active tab label is visually rendered;
2. `SHOW DETAILS` broke vertically letter-by-letter; at high font scale that redundant secondary label is suppressed while the accessible disclosure title remains;
3. the primary mission CTA had regressed to a 48dp minimum; it is restored to flexible `.heightIn(min = 52.dp)` for both active and empty mission states;
4. the Hunter profile target lost part of its accessibility contract; the 54dp outer target now carries `home_hunter_profile_icon_cd`, while the inner avatar is restored to 48dp.

Post-fix verification:
- `HomeFontScaleContractTest`: **PASS**;
- full `testDebugUnitTest`: **442 tests / 0 failures / 0 errors / 0 skipped**;
- inactive nav tabs retain accessibility content descriptions;
- only visible `HOME` label is rendered at high font scale;
- `NEXT MEANINGFUL MISSION` fits at `[118,863][975,990]`;
- `TODAY` fits at `[42,1688][283,1802]`;
- deep-scroll `XION` and `PROGRESS` remain reachable;
- `VITALS & PROTOCOLS` fits at `[152,1689][877,1815]`;
- `SHOW DETAILS` is absent from the high-scale render.

### Light EN — 100%

**PASS.** Captures 62/63 use the same geometry under the actual Light companion palette. A full-frame 1×1 downsample sanity check gives approximately:

- Dark top average RGB: `15 / 29 / 26`;
- Light top average RGB: `221 / 227 / 220`.

This confirms the Light evidence is a genuine light companion state rather than a stale Dark screenshot.

## 5. Product / Architecture Invariants

Verified in the repaired source/runtime:

- exactly one dominant Home primary mission CTA;
- primary CTA semantics remain truthful and route through the existing mission action;
- mission-first hierarchy preserved;
- five-tab membership/order preserved: Missions / Physical / Home / Shadows / Hunter;
- Xion remains advisory/subordinate, and the global floating Xion widget is suppressed only on Home to prevent duplicate Xion surfaces;
- Progress precedes progressive disclosure;
- no AX-013 Dungeons, AX-016 Skill Tree, AX-018 Leagues, or AX-026 Premium exposure was added to Home;
- Daily Check-in / Weekly Analytics remain bounded secondary surfaces only;
- no domain/data/schema migration was introduced by this visual repair;
- no fake/invisible test-only UI was introduced.

## 6. Build / Test Evidence

Validated against the final source candidate identified by source-patch SHA-256 `5e2c9f6708ef7e29e0675a37ac38460b591265abfc5a7b16f11ea21736dc3d56`:

- `git diff --check`: **PASS**
- `HomeFontScaleContractTest`: **PASS**
- `testDebugUnitTest`: **PASS — 442 / 442**
- `lintDebug`: **PASS — 0 errors, 333 warnings**
- `assembleDebug`: **PASS**
- validated APK SHA-256: `c1ef5b8197cea110211cb222720abc3cad700faf397d2410ce9503a5dd2e54c4`
- APK size: `33,738,948` bytes

The lint warnings are non-fatal existing project warnings; the final gate contains zero lint errors.

## 7. Final Visual Decision

- Unresolved Critical visual deltas: **0**
- Unresolved unjustified Major visual deltas: **0**
- Residual differences: **justified / minor**
- Dark EN 100 visual gate: **PASS**
- FA RTL 100 companion gate: **PASS**
- Dark EN 200 accessibility gate: **PASS**
- Light EN 100 companion gate: **PASS**
- WP-UIUX-02 visual repair slice: **VISUAL PASS**

## 8. Repository / Review Closure Boundary

This matrix closes the **visual + local technical evidence** for the source state identified by `5e2c9f...`. It is not, by itself, merge authorization.

Repository closure requires:

1. commit the final accessibility repair + regenerated evidence;
2. recompute the source-patch hash from the committed head and require an exact match to `5e2c9f6708ef7e29e0675a37ac38460b591265abfc5a7b16f11ea21736dc3d56`;
3. run the required independent Reviews A/B/C/D on that exact committed head;
4. require canonical CI green on that exact final head;
5. obtain Product Owner final acceptance before merge.

Independent-review infrastructure was unavailable during this local closure attempt: the configured Codex reviewer runner rejected its model because the local Codex version is too old, and Claude Code authentication was expired. No review score or approval is fabricated from those failed tool runs.

Until Reviews A/B/C/D and canonical CI complete: **visual blocker closed; merge authorization pending.**
