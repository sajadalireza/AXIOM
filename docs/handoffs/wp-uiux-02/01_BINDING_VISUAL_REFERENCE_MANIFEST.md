# WP-UIUX-02 — BINDING VISUAL REFERENCE MANIFEST

**Work Packet:** WP-UIUX-02 (Slice 2: Home Screen Visual Redesign & Core Navigation)  
**Owning Gate:** Gate G3 — Core Loop & Data Truth (AX-009 Home and Today, AX-002 App shell and primary navigation)  
**Status:** Canonical Product Owner Binding Target  

---

## 1. Selected Target Reference

| File | SHA-256 | Description |
|---|---|---|
| `docs/handoffs/wp-uiux-02/references/01_home_target.jpg` | `57a4825f7c9ae4ec04e0e132255d4191bf9040f00a442c478523a5f7ceac5f2f` | Product Owner-selected visual target for the Home Screen & Core Navigation Dock |

*Provenance Note:* Recovered from the authorized Product Owner handoff collection (`axiom_home_mockup_1789171453974.jpg`). Historical archives, deprecated proposals, and non-Home exploratory designs are strictly excluded.

---

## 2. Binding Visual Hierarchy & Styling Rules

The reference image is authoritative for visual hierarchy, spacing, geometry, and styling. Dynamic data, entity logic, and interaction behavior derive strictly from canonical repository code and domain models.

### A. PO-Binding Visual Details
- **Palette & Mood:** Deep Emerald (`#0D1A14`, `#10B981`, `#00E599`) + restrained Champagne Gold (`#F0D590`) + Graphite (`#1A221E`). Dark Matte background (`#0A0E0C` / `#121614`).
- **Header Structure:** Compact, refined header containing Hunter profile avatar, name, rank badge capsule (`RECRUIT I`), thin linear XP progress bar (`10 / 100 XP`), and clean flame streak counter (`1 Day`).
- **Hero Mission Card (Dominant Center of Gravity):**
  - Positioned prominently as the immediate primary visual focus (Layer 1 — NOW).
  - Bold mission title typography.
  - Duration pill (`25 min`) and reward pill (`+230 XP`).
  - **Single Dominant Primary CTA:** Exactly one primary button (`"Start Mission"` / `"شروع مأموریت"`), styled with a large emerald gradient/glow.
- **Streak & Habit Module:** Clean flame icon with a 7-day dot timeline.
- **Core Navigation Dock:** Floating curved container (20dp rounded corners), subtle dark emerald border, monochromatic icons, and an animated soft emerald active pod indicator (`podWidth = 44.dp`), eliminating notification badge bombardment.
- **Typography & Polish:** Modern geometric sans (Outfit / Inter) with clean hierarchy. Eliminate bracket clutter `[ ]`, cyber-terminal pseudo-code, all-caps noise, and RPG dominance.

### B. Visual Inspiration Only (Not Canonical Domain Models)
- Mockup text strings (`"Protocol: Deep Focus"`, `"+230 XP"`, `"25 min"`) are illustrative mockups; live fields bind dynamically to actual `Mission` entities in Room.
- Decorative neon glow blurs are visual tokens; they must not compromise WCAG AAA text contrast, TalkBack labels, or performance.

### C. Current Canonical Product Behavior Preserved (No Feature Deletion)
- **5-Tab Navigation Scope Preservation:** Missions, Physical, Home, Shadows, Hunter preserved in order and route semantics.
- **Progressive Disclosure:** All real secondary Home capabilities remain fully accessible:
  - Layer 1 (NOW): Next Meaningful Mission, Active Mission state, contextual Xion presence.
  - Layer 2 (TODAY): Daily Outcomes, Next Best Action, eligible Daily Habit / Vitals, Program Countdown DatePicker.
  - Layer 3 (PROGRESS): XP / Level / Rank progress, streak & weekly challenges, muscle recovery & body status, weekly review overdue banner.
  - Layer 4 (SECONDARY): Operational Tracks, System Feed, low-priority existing tools.
- **Database & Data Invariants:** Room schema v18 intact (0 migrations), 0 changes to `domain/` or `data/` layers.
- **Accessibility & Parity:** Complete EN/FA parity, dynamic RTL mirroring, TalkBack semantic markup, 200% font scaling, $\ge 48\,\text{dp}$ touch targets, and a real Light Theme companion.
