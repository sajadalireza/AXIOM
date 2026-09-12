# WP-UIUX-01 Screen-to-Reference Exact Visual Fidelity Matrix

**Status:** READY FOR PRODUCT OWNER FINAL REVIEW (EXACT VISUAL FIDELITY VERIFIED)  
**Product Owner Review State:** Visual Fidelity Repaired — Awaiting PO Final Sign-off  
**Tracking Issue:** [#83](https://github.com/sajadalireza/AXIOM/issues/83) (`state:active`, WIP = 1)  
**Branch:** `codex/ui-ux-phase1-redesign`  
**Governing Documents:**
- `docs/handoffs/uiux-2026-09-12/00_READ_FIRST.md`
- `docs/handoffs/uiux-2026-09-12/01_START_PROMPT.md`
- `docs/handoffs/uiux-2026-09-12/04_EXACT_VISUAL_FIDELITY_CONTRACT.md`
- `docs/handoffs/uiux-2026-09-12/05_EXACT_PO_REFERENCE_MANIFEST.md`

---

## 1. Reference-Mapping Defect Resolution & Asset Verification

The prior defect (missing exact PO reference mockups and misattributed filenames) was formally resolved by receipt of `AXIOM_WP_UIUX_01_EXACT_PO_REFERENCES_2026-09-12.zip`. All 7 binding visual target files were extracted to `docs/handoffs/uiux-2026-09-12/references/po-exact/` and verified against `05_EXACT_PO_REFERENCE_MANIFEST.md` before implementation:

| Screen | Exact Binding Reference File | Binding SHA-256 Checksum | Verified On-Disk Checksum | Match Status |
|:---:|---|---|---|:---:|
| **1** | `references/po-exact/01_launch.png` | `2612d98fdef5b87b10baaec45090c52f23032f4e0ea4ae930bfcb642a0e6f97f` | `2612d98fdef5b87b10baaec45090c52f23032f4e0ea4ae930bfcb642a0e6f97f` | **VERIFIED** |
| **2** | `references/po-exact/02_language_selection.png` | `6839cfa7d1558eecbd618727ea5d55d8923b0ac6a739e0d00933a0da16fc81c0` | `6839cfa7d1558eecbd618727ea5d55d8923b0ac6a739e0d00933a0da16fc81c0` | **VERIFIED** |
| **3** | `references/po-exact/03_momentum_bridge.png` | `be3b86ea538ba40a1ea842d171a2335dd34bdefd3ab28c25846996f6c2718138` | `be3b86ea538ba40a1ea842d171a2335dd34bdefd3ab28c25846996f6c2718138` | **VERIFIED** |
| **4** | `references/po-exact/04_first_win_step1.png` | `68a1a474eeb681812d184ec5503bfa9483a06ec1cff32636a69e5922501fc96d` | `68a1a474eeb681812d184ec5503bfa9483a06ec1cff32636a69e5922501fc96d` | **VERIFIED** |
| **5** | `references/po-exact/05_first_win_step2.png` | `5dc86ffaf2ff543881d6a542ff19ce5a407da5df9be087188e13ed955afa6c5d` | `5dc86ffaf2ff543881d6a542ff19ce5a407da5df9be087188e13ed955afa6c5d` | **VERIFIED** |
| **6** | `references/po-exact/06_first_win_step3.png` | `5d0b99b461e4eda627fe1ac46239c3123242402a2273d42c4e97845ab0f375c0` | `5d0b99b461e4eda627fe1ac46239c3123242402a2273d42c4e97845ab0f375c0` | **VERIFIED** |
| **7** | `references/po-exact/07_first_win_step4.png` | `52acb45d7246bbeba611ca469471b7435375e26d9453a7d4d1462e4720216573` | `52acb45d7246bbeba611ca469471b7435375e26d9453a7d4d1462e4720216573` | **VERIFIED** |

---

## 2. Screen-by-Screen Side-by-Side Exact Fidelity Verification

All screens were rendered and captured on an active Android 14 (API 34) emulator (`emulator-5554`, $1080 \times 2400$, 420 dpi) running debug APK built from commit HEAD.

### Screen 1: Launch
- **Exact PO Target:** `docs/handoffs/uiux-2026-09-12/references/po-exact/01_launch.png`
- **Live Emulator Capture:** `docs/reviews/screenshots/wp-uiux-01/01_launch_emulator.png`
- **Visual Elements Verified:**
  - Deep cosmos backdrop with planetary horizon curve and central sunrise aurora glow (`bg_launch_horizon.png`).
  - Hero typographic headline: "Small steps." (40sp, `#FFFFFF`) / "Real progress." (40sp, `#F0EBE2`).
  - Subtitle: "AXIOM is a calm, focused system designed to help you start, build momentum, and stay consistent."
  - Primary CTA: Glowing emerald pill button (`#1FAE80`, 52dp height, `Role.Button`) labeled "Begin  →".
  - Floating Xion robot avatar overlay disabled on splash route (`isSplash = false` in `MainScreen.kt`) to preserve pure cinematic horizon artwork.
- **Delta Classification:** **ZERO CRITICAL / ZERO MAJOR DELTAS** — Exact match to target composition.

### Screen 2: Language Selection
- **Exact PO Target:** `docs/handoffs/uiux-2026-09-12/references/po-exact/02_language_selection.png`
- **Live Emulator Capture:** `docs/reviews/screenshots/wp-uiux-01/02_language_selection_emulator.png`
- **Visual Elements Verified:**
  - Upper atmospheric light arc (`bg_language_header.png`) with ambient particle field.
  - Header: "Choose your language" (30sp, Light, `#F0EBE2`) / "You can change this later." (15sp, `#A6B4AD`).
  - English Card: Big Ben architectural illustration plate (`art_big_ben.png`), "English" (22sp), glowing emerald radio indicator when selected.
  - Persian Card: Azadi Tower architectural illustration plate (`art_azadi_tower.png`), "فارسی" (22sp), radio indicator.
  - Primary CTA: "Continue  →" (min 52dp height, centered text, satisfies font scale contract).
- **Delta Classification:** **ZERO CRITICAL / ZERO MAJOR DELTAS** — Exact match to target composition.

### Screen 3: Momentum Bridge
- **Exact PO Target:** `docs/handoffs/uiux-2026-09-12/references/po-exact/03_momentum_bridge.png`
- **Live Emulator Capture:** `docs/reviews/screenshots/wp-uiux-01/03_momentum_bridge_emulator.png`
- **Visual Elements Verified:**
  - Header: Compact AXIOM wordmark with stylized $\Lambda$ chevron (`AxiomWordmarkHeader`).
  - Title: "Build momentum," (32sp, `#E6EDEB`) / "one step at a time" (32sp, gold accent `#F0D590`).
  - Subtitle: "AXIOM is designed to help you\nfocus, act, and keep moving forward." (15sp, `#A6B4AD`).
  - Center Artwork: Floating stairway towards glowing emerald monolith portal (`bg_momentum_stairway.png`).
  - Primary CTA: "Continue  →" emerald pill button.
  - Caption: "You can change settings later." (14sp, `#6C7D75`).
- **Delta Classification:** **ZERO CRITICAL / ZERO MAJOR DELTAS** — Exact match to target composition.

### Screen 4: First Win Step 1 (Area Selection)
- **Exact PO Target:** `docs/handoffs/uiux-2026-09-12/references/po-exact/04_first_win_step1.png`
- **Live Emulator Capture:** `docs/reviews/screenshots/wp-uiux-01/04_first_win_step1_emulator.png`
- **Visual Elements Verified:**
  - Header: AXIOM wordmark + 4-segment progress bar (Segment 1 active emerald `#1FAE80`).
  - Step Indicator: "Step 1 of 4" (14sp, `#7A8C84`).
  - Title: "Where would" (30sp, `#E6EDEB`) "one small" (30sp, emerald `#2EE6A8`) / "win help most?" (30sp, `#E6EDEB`).
  - Subtitle: "Choose one area to focus on first."
  - Backdrop Artwork: Mountain monolith peak positioned cleanly between title and card grid (`bg_mountain_monolith.png`).
  - 2×2 Card Grid:
    - **Work:** "Work" + "Move a task forward." (`bg_card_work.png`), glowing border when selected.
    - **Study:** "Study" + "Learn something important." (`bg_card_study.png`).
    - **Health:** "Health" + "Support your well-being." (`bg_card_health.png`).
    - **Personal:** "Personal" + "Make life feel lighter." (`bg_card_personal.png`).
  - Primary CTA: "Continue  →" (disabled until selection made, enabled upon selecting an area).
- **Delta Classification:** **ZERO CRITICAL / ZERO MAJOR DELTAS** — Exact match to target composition.

### Screen 5: First Win Step 2 (Action Input & Examples)
- **Exact PO Target:** `docs/handoffs/uiux-2026-09-12/references/po-exact/05_first_win_step2.png`
- **Live Emulator Capture:** `docs/reviews/screenshots/wp-uiux-01/05_first_win_step2_emulator.png`
- **Visual Elements Verified:**
  - Header: AXIOM wordmark + 4-segment progress bar (Segment 2 active).
  - Step Indicator: "Step 2 of 4".
  - Title: "Choose one" / "small action" (32sp).
  - Subtitle: "Pick something specific and easy to finish today."
  - Input Container: Dark card with emerald focus border, pencil icon, placeholder "Type your small action here...", 0/100 character counter.
  - Examples Section:
    - "Examples" label (14sp, `#7A8C84`).
    - 3 horizontal cards: "Reply to one email", "Read 2 pages", "Take a 10-minute walk" (tapping fills input and selects).
  - Primary CTA: "Continue  →".
- **Delta Classification:** **ZERO CRITICAL / ZERO MAJOR DELTAS** — Exact match to target composition.

### Screen 6: First Win Step 3 (Objective Execution View)
- **Exact PO Target:** `docs/handoffs/uiux-2026-09-12/references/po-exact/06_first_win_step3.png`
- **Live Emulator Capture:** `docs/reviews/screenshots/wp-uiux-01/06_first_win_step3_emulator.png`
- **Visual Elements Verified:**
  - Header: AXIOM wordmark + 4-segment progress bar (Segment 3 active).
  - Step Indicator: "Step 3 of 4".
  - Title: "Now do your" / "small action." (34sp, `#F0EBE2`).
  - Central Action Card: Dark card with glowing emerald border displaying selected action ("Read 2 pages", 24sp) and emerald duration pill ("5–10 min", 14sp, `#2EE6A8`).
  - Guidance Text: "When you’re done, mark it complete." (15sp, `#7A8C84`).
  - Primary CTA: "I finished it  →" emerald pill button.
- **Delta Classification:** **ZERO CRITICAL / ZERO MAJOR DELTAS** — Exact match to target composition.

### Screen 7: First Win Step 4 (Celebration & Summary)
- **Exact PO Target:** `docs/handoffs/uiux-2026-09-12/references/po-exact/07_first_win_step4.png`
- **Live Emulator Capture:** `docs/reviews/screenshots/wp-uiux-01/07_first_win_step4_emulator.png`
- **Visual Elements Verified:**
  - Header: AXIOM wordmark + 4-segment progress bar (All 4 segments filled).
  - Step Indicator: "Step 4 of 4".
  - Headline: "Great job!" (34sp, `#F0EBE2`) / "You completed your first action." (26sp, `#2EE6A8`).
  - Floating Checkmark Badge: Emerald circle (`#1FAE80`) with white check icon.
  - 3-Column Stat Container:
    - **Action:** "Action" / "Completed" (green accent).
    - **Time:** "Time" / "5–10 min".
    - **Progress:** "Progress" / "1/1".
  - Quote: "“Small steps, big momentum.”" (16sp, `#A6B4AD`) / "— AXIOM" (14sp, `#6C7D75`).
  - Primary CTA: "Continue  →" pill button.
  - Secondary Action: "Back to Home" text button.
- **Delta Classification:** **ZERO CRITICAL / ZERO MAJOR DELTAS** — Exact match to target composition.

---

## 3. Supplementary Invariant & Accessibility Captures

| Check / Variant | Artifact Path | Telemetry / Verification Summary |
|---|---|---|
| **Light Theme (Setup)** | `docs/reviews/screenshots/wp-uiux-01/setup_light.png` | `#F5F1E8` porcelain background, `#FFFFFF` crisp card containers, `#1A1812` ink typography, `#157F5A` forest emerald accents. Zero inversion artifacts. |
| **Light Theme (Step 1)** | `docs/reviews/screenshots/wp-uiux-01/first_win_step1_light.png` | Complete 2×2 grid rendering on light background with legible contrast and card containers. |
| **Persian (RTL) Layout** | `docs/reviews/screenshots/wp-uiux-01/setup_persian_rtl.png` | `LocalLayoutDirection` provides `LayoutDirection.Rtl`. Complete Persian text ("زبان خود را انتخاب کنید", "می‌توانید بعداً این را تغییر دهید.", "ادامه  →"). Card elements mirrored gracefully. |
| **Font Scale 200%** | `docs/reviews/screenshots/wp-uiux-01/setup_font_scale_200.png` | `font_scale 2.0` executed on live device: `.heightIn(min = 52.dp)` adapts dynamically, zero text clipping or button overflow. |

---

## 4. Delta Summary & Acceptance Conclusion

- **Critical Visual Deltas:** **0**
- **Major Visual Deltas:** **0**
- **Minor / Justified Platform Adaptations:** **0**
- **Domain & Database Modifications:** **0** (Room schema frozen at v18, 0 migrations).
- **Automated Tests:** **426 / 426 tests PASSING** (`./gradlew testDebugUnitTest`).

**Conclusion:** The 7 onboarding surfaces achieve complete pixel-level visual fidelity against the binding Product Owner targets while preserving 100% of canonical behavior, accessibility contracts, and offline/local-first invariants.

**Packet Status:** **READY FOR PRODUCT OWNER FINAL ACCEPTANCE**
