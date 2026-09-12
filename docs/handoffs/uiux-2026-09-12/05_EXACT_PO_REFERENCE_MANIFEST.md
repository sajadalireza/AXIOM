# WP-UIUX-01 — Exact Product Owner Reference Manifest

Status: BINDING Product Owner visual targets for Issue #83.
These filenames supersede the defective/mislabeled references previously staged in the repository.

## Authority rule
- Live repository + canonical governance = behavior, capabilities, state, accessibility, privacy, localization.
- THESE EXACT IMAGES = visual composition/style target for the corresponding authorized screen.
- Do not infer new features from image text.
- Do not substitute a similar design.
- If a canonical behavior requires adaptation, preserve the visual hierarchy and record the deviation.

## Exact mapping

| Screen | Exact file | SHA-256 | Size | Product Owner target |
|---|---|---|---|---|
| Launch | `references/01_launch.png` | `2612d98fdef5b87b10baaec45090c52f23032f4e0ea4ae930bfcb642a0e6f97f` | `941x1672` | AXIOM / “Small steps. Real progress.” / Begin |
| Language Selection | `references/02_language_selection.png` | `6839cfa7d1558eecbd618727ea5d55d8923b0ac6a739e0d00933a0da16fc81c0` | `941x1672` | Cinematic language selection; English and Persian each have a visual representative. |
| Momentum Bridge | `references/03_momentum_bridge.png` | `be3b86ea538ba40a1ea842d171a2335dd34bdefd3ab28c25846996f6c2718138` | `941x1672` | “Build momentum, one step at a time” |
| First Win Step 1 | `references/04_first_win_step1.png` | `68a1a474eeb681812d184ec5503bfa9483a06ec1cff32636a69e5922501fc96d` | `941x1672` | “Where would one small win help most?” — 2×2 Work / Study / Health / Personal |
| First Win Step 2 | `references/05_first_win_step2.png` | `5dc86ffaf2ff543881d6a542ff19ce5a407da5df9be087188e13ed955afa6c5d` | `941x1672` | “Choose one small action” — cinematic mountain scene, input, 0/100 counter, Examples |
| First Win Step 3 | `references/06_first_win_step3.png` | `5d0b99b461e4eda627fe1ac46239c3123242402a2273d42c4e97845ab0f375c0` | `941x1672` | “Now do your small action.” — selected-action card |
| First Win Step 4 | `references/07_first_win_step4.png` | `52acb45d7246bbeba611ca469471b7435375e26d9453a7d4d1462e4720216573` | `986x1596` | “Great job! You completed your first action.” — completion summary |

## Fidelity acceptance
For each screen:
1. Capture emulator screenshot at comparable viewport.
2. Compare side-by-side with the exact file above.
3. Classify Critical / Major / Minor deltas.
4. Zero unjustified Critical or Major deltas is required.
5. Product Owner acceptance remains required even when automated tests are green.

## Important
The `provenance/` images are snapshots from the design-selection walkthrough. They may contain Control Room/Edit/share UI overlays and are NOT implementation targets. Use only `references/` as visual targets.
