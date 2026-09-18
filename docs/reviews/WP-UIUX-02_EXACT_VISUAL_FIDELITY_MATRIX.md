# WP-UIUX-02 — Exact Visual Fidelity Matrix

**Work Packet:** WP-UIUX-02 — Home Screen Visual Redesign & Core Navigation
**Validation date:** 2026-09-18 (targeted repair cycle)
**Branch:** `codex/ui-ux-phase2-home-navigation`
**Reviewed head at repair start:** `c1f4a31edf4b761a42c099bf06dd753d75409b0f`
**Validated source-patch SHA-256:** `d0d4f95d578f22224e09dbf10e08a49b74ef2982555d1d666289215c61cdba15`
**Validated APK SHA-256:** `746d6f0f5290cc29e0872c93178961ebbcf697eeedd2480f49ca83c316e4e31d`
**Binding Pixel Master:** `docs/handoffs/wp-uiux-02/references/01_home_primary_state.png`
**Pixel Master SHA-256:** `4b0b36c360563b9c6a7affbf7b41da4044a0d383ce80b296d932bddbded658b0`

> Provenance rule: the validated source patch is the exact output of
> `git diff c1f4a31 -- app/src/main | shasum -a 256` from this working tree
> (`d0d4f95d…`). The evidence set below was captured from APK `746d6f0f…`, built from that
> exact source state. The previous revision of this document claimed PASS for captures
> 55–63; those captures were produced by an unverified capture flow and are now
> **invalidated** (see Section 2). All evidence in this revision was produced by the
> assertion-gated harness `tools/wp-uiux-02/capture_home_evidence.sh`.

## 1. Acceptance Contract

Binding hierarchy:

`Identity → Next Meaningful Mission → one primary CTA → Today → Xion → Progress → collapsed secondary disclosure → five-tab dock`.

Runtime data remains truthful and may differ from the illustrative Pixel Master. Device chrome/aspect-ratio differences and accessibility-specific responsive layouts are allowed only when explicitly justified.

## 2. Evidence Set (repair cycle)

### Invalidated previous evidence

- `55_visual_repair_final_dark_en_100_top.png` and
  `59_visual_repair_final_dark_en_200_top.png` contain the **Splash screen** ("Small steps.
  Real progress." + `Begin`), not Home. The previous revision's PASS claims for captures
  55–63 are withdrawn; the files remain on disk only as history.
- Defect prevention: `tools/wp-uiux-02/capture_home_evidence.sh` now (a) cold-starts the
  app, (b) waits until the UI tree proves Home is rendered — dock label + hero label in the
  expected language, (c) auto-rejects any frame containing Splash/Onboarding markers
  (`Begin` / `شروع`), and only then (d) captures, logging the full state (font scale, night
  mode, `axiom_lang` XML, DataStore `language`/`theme_mode`) plus the UI tree next to each
  image. Run log: `docs/reviews/evidence-logs/wp-uiux-02/capture_run_20260918_210237.log`.

### Final evidence (APK `746d6f0f…`, 1080×2400 emulator)

| Evidence | SHA-256 | State / purpose |
|---|---|---|
| `64_fixed_dark_en_100_top.png` | `59b2eded15f51cc52863afc4e0e74c6040ae62a88b5d8becae829c1aea5f19e4` | Dark / EN / 100% Home top — captured under the reported **desync state** (UI `axiom_lang=en`, DataStore `language=fa`) |
| `65_fixed_dark_en_100_lower.png` | `73ab3dc4c8080bbf1716c8136ef5e71ab2fd1061b06e093b70ee95f5c2affe5e` | Dark / EN lower hierarchy |
| `66_fixed_dark_fa_rtl_100_top.png` | `95b98ed783dd907bf3855dc2dbdabd64804992cbc86a7d096a1c0805ca396025` | Persian RTL / Dark / 100% top |
| `67_fixed_dark_fa_rtl_100_lower.png` | `648cbdd6b74882cef64bcf242d83ec1a65ef70fa16053d50be389d99af8f8bf5` | Persian RTL lower hierarchy |
| `68_fixed_dark_en_200_top.png` | `07d8b1f39c73ceec333ce18e8423ee8c586181662de0b3027ee9e2c43cf80860` | Dark / EN / 200% font top |
| `69_fixed_dark_en_200_lower.png` | `1e05b398ca1e7b5f7d370882a5aaa42bf6ed2dacf0418cdde03c5598395e03f3` | Dark / EN / 200% lower |
| `70_fixed_dark_en_200_deep.png` | `73fd871c1cdc1534f6efbfc1f8759d405cb430633a8cc482f7c4bfd048baf6f5` | 200% deep-scroll / secondary surfaces |
| `71_fixed_light_en_100_top.png` | `4337b901076433b56a5e179102ed0abab57634906397e6f91fdfe7a70b58e1f8` | Light / EN / 100% top |
| `72_fixed_light_en_100_lower.png` | `30e837d6eaef30ff060b0653d0858a83a187040242da97b74c874759460da548` | Light / EN lower hierarchy |
| `v3_vs_64_fixed_dark_en.png` | `bc0863d3adf183a18cf91633342b05952bd4ba8de245aa619cf0cafb48bf3d0d` | Direct Pixel Master side-by-side, rebuilt from the genuine Home top capture |

The comparison uses the unchanged V3 Pixel Master with SHA-256 `4b0b36c360563b9c6a7affbf7b41da4044a0d383ce80b296d932bddbded658b0` (re-verified this cycle).

## 3. Reviewed Blockers Closed in This Cycle

| Blocker | Root cause | Repair | Evidence |
|---|---|---|---|
| `BLOCKER-1` Mixed EN/FA language state | `HomeViewModel` built the Xion advisory string from the DataStore `languageFlow` while every other Home string resolved against the runtime UI locale (`axiom_lang`). When the two sources diverged, Persian advisory text rendered inside an English Home. | The advisory is now a language-neutral `HomeNextAction` descriptor (no display text), resolved from `values/` / `values-fa/` resources at render time under the same configuration that renders the rest of the screen. `HomeViewModel` no longer references `languageFlow`; the persisted briefing language now derives from the UI locale. | Capture 64 taken under the exact desync state; UI-tree shows `Complete: Momentum Re-anchor: Core Goal Action` (EN). Zero Arabic-script strings across all EN frames (7/7 dumps CLEAN). `HomeLanguageAndTruthContractTest` guards the mapping and absence of stored-language coupling. |
| `BLOCKER-2` Truthful track truncation | `TodayStatusRow` applied `take(10)` to the runtime track value, rendering `Recovery Protocol` as `Recovery P`. | Slicing removed; the truthful value reaches the Text layer intact and wraps (`maxLines = 2`, `TextOverflow.Ellipsis` only as a last-resort presentation affordance). | Captures 64/66 show the full `Recovery Protocol` in the TRACK card at EN and FA; `Recovery P` appears in no evidence dump. Guarded by `todayTrack_rendersTheTruthfulRuntimeValue` and `noHomeSurfaceSlicesRuntimeStringsBeforePresentation`. |
| `BLOCKER-3` 200% font-scale readability | Hero mission title/goal collapsed to one-line ellipsis at large font scale. | At `fontScale > 1.3` the hero stacks a full-width mission block (title up to 4 lines, goal up to 3) instead of a width-shared row; the track value wraps to 2 lines. The single dominant CTA keeps `.heightIn(min = 52.dp)`. | Captures 68/69/70: full title `Momentum Re-anchor: Core Goal Action`, full goal line, full track value, prominent CTA, no clipping/overlap. Guarded by `heroMissionContent_staysReadableAt200PercentFontScale`. |

## 4. V3 Major Delta Closure

| ID | Original blocker | Final observation | Final classification |
|---|---|---|---|
| `V3-M01` | Hunter identity anatomy materially too generic / weak. | Header restores strong AXIOM identity, profile anchor, Hunter/rank anatomy, and gold Level/XP hierarchy. No canonical portrait field is exposed by the current Home `Hunter` model, so an initial-based profile fallback is used rather than inventing a portrait. | **Resolved Major; residual justified Minor** |
| `V3-M02` | Mission hero too tall / text-heavy / weak grouping. | Hero is materially compressed, uses target-like mission label, icon/title/goal/metadata grouping, one dominant emerald CTA, and target-proportional rounded geometry. Runtime title/track/duration/XP remain truthful (see `BLOCKER-2`). | **Resolved** |
| `V3-M03` | Primary viewport did not establish Identity → Mission → Today → Xion → Progress together. | Dark EN 100% capture 64 establishes the mission-first hierarchy before secondary disclosure; Progress remains above the dock / progressive disclosure boundary. | **Resolved** |
| `V3-M04` | Bottom dock too flat; Home anchor weak. | Dock retains floating glass mass, larger radius, gold/emerald treatment, persistent five-tab identity at 100%, and a materially stronger central Home anchor. Existing canonical Home glyph is retained rather than copying the illustrative glyph. | **Resolved Major; residual justified Minor** |
| `V3-M05` | UI too flat; insufficient atmospheric art/depth/glow. | Mountain/celestial artwork carries through the header/background and Progress card with controlled emerald/gold depth while preserving readable card contrast. | **Resolved** |

### Residual justified differences

- Runtime name, mission, counts, XP and progress values are real state and intentionally differ from mock data (e.g., the Pixel Master's illustrative `Read 2 pages` vs. the device's real `Momentum Re-anchor: Core Goal Action`).
- No fabricated profile portrait was introduced; Home currently receives no canonical portrait/image field through `Hunter`.
- The existing canonical Home navigation glyph is retained.
- Android system chrome and 1080×2400 device composition differ from the Pixel Master device frame.
- 200% font mode intentionally changes density and nav-label behavior to preserve reachability and prevent clipping (inactive dock tabs are icon/semantics-only; only the selected label renders).
- At 200%, the Today card row stacks into a single column; this is the accessibility-specific responsive layout required to keep truthful values readable.

## 5. Companion-State Validation

### Dark EN — 100% (desync regression scenario)

**PASS.** Captures 64/65 were taken with `axiom_lang=en` while the DataStore still stored
`language=fa` — the exact desynchronization that produced the reviewed defect. The rendered
Home is entirely English, including the Xion advisory and the full `Recovery Protocol`
track value. UI-tree scan: 0 Arabic-script strings across all EN frames.

### Persian / RTL — Dark 100%

**PASS.** Capture 66/67 UI-tree verification:

- `مأموریت معنادار بعدی` renders in the hero;
- `ادامه مأموریت` is localized;
- `امروز` and `پیشرفت` render in Persian;
- Xion advisory renders the Persian prefix `تکمیل کنید:` with the real mission title as truthful data;
- central `خانه` remains centered in the dock; five-tab order preserved under RTL mirroring;
- translated hero / Today / Progress surfaces remain within the 1080px viewport.

### Dark EN — 200% font scale

**PASS with intentional responsive divergence.** Captures 68/69/70 show:

- full mission title at 4-line budget and full goal-connection line — no ellipsized title;
- full truthful track value (`Recovery Protocol`) wrapped across two lines;
- single dominant CTA `Continue Mission` at `.heightIn(min = 52.dp)`, fully reachable;
- Today cards in single-column responsive layout with all values readable;
- `XION` advisory readable across up to 3 lines;
- deep scroll reaches `PROGRESS` and `VITALS & PROTOCOLS` without clipping;
- inactive dock tabs are icon/semantics-only with all five tabs present and accessible.

Known residual (minor, pre-existing, outside the repaired surfaces): at 200% the Progress
ring's `LVL` caption sits tight against the adjacent progress bar; content remains readable
and no blocker was raised for it.

### Light EN — 100%

**PASS.** Captures 71/72 use the same geometry under the actual Light companion palette
(via `cmd uimode night no` with DataStore `theme_mode=SYSTEM`), captured by the same
assertion-gated flow.

## 6. Product / Architecture Invariants

Verified in the repaired source/runtime:

- exactly one dominant Home primary mission CTA (per rendered hero branch);
- primary CTA semantics remain truthful and route through the existing mission action;
- mission-first hierarchy preserved;
- five-tab membership/order preserved: Missions / Physical / Home / Shadows / Hunter;
- Xion remains advisory/subordinate, and the global floating Xion widget is suppressed only on Home to prevent duplicate Xion surfaces (no floating orb in captures 64–72);
- Progress precedes progressive disclosure;
- no AX-013 Dungeons, AX-016 Skill Tree, AX-018 Leagues, or AX-026 Premium exposure was added to Home;
- no blocked/frozen Home modules were resurrected; module disposition contract tests remain green;
- Daily Check-in / Weekly Analytics remain bounded secondary surfaces only;
- no domain/data/schema migration was introduced by this repair;
- no fake/invisible test-only UI was introduced.

## 7. Build / Test Evidence

Validated against the source patch `d0d4f95d…` (recipe in the provenance note):

- `git diff --check`: **PASS**
- focused Home tests (`com.axiom.app.presentation.home.*`): **PASS — 30 / 30**, including the new `HomeLanguageAndTruthContractTest` (6 tests) and the extended `HomeFontScaleContractTest`
- `testDebugUnitTest`: **PASS — 449 tests / 0 failures / 0 errors** (67 classes)
- `lintDebug`: **PASS — 0 errors, 333 warnings** (no warnings in any file touched by this repair)
- `assembleDebug`: **PASS**
- validated APK SHA-256: `746d6f0f5290cc29e0872c93178961ebbcf697eeedd2480f49ca83c316e4e31d`
- APK size: `33,740,516` bytes

The lint warnings are non-fatal existing project warnings; the gate contains zero lint errors.

## 8. Final Visual Decision

- Unresolved Critical visual deltas: **0**
- Unresolved unjustified Major visual deltas: **0**
- Residual differences: **justified / minor**
- Dark EN 100 visual gate (incl. desync regression scenario): **PASS**
- FA RTL 100 companion gate: **PASS**
- Dark EN 200 accessibility gate: **PASS**
- Light EN 100 companion gate: **PASS**
- WP-UIUX-02 visual repair slice: **VISUAL PASS**

## 9. Repository / Review Closure Boundary

This matrix closes the **visual + local technical evidence** for the source state identified by `d0d4f95d…`. It is not, by itself, merge authorization.

Repository closure requires:

1. commit the repair + regenerated evidence;
2. recompute the source-patch hash (`git diff c1f4a31 -- app/src/main | shasum -a 256`) from the committed head and require an exact match to `d0d4f95d578f22224e09dbf10e08a49b74ef2982555d1d666289215c61cdba15`;
3. run the required independent Reviews A/B/C/D on that exact committed head;
4. require canonical CI green on that exact final head;
5. obtain Product Owner final acceptance before merge.

Per the repair mandate, no commit, merge, push, rebase, or reset was performed in this
cycle; the validated source state is the current working tree.
