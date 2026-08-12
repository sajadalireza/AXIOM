# ADR-0001 — State source-of-truth per data concept

- **Status:** Accepted (as-built; documented in WP-106, not introduced by it)
- **Context ref:** `ARCHITECTURE.md` §9, §10, §12-A

## Context

AXIOM is local-first. On-device state is spread across four stores: Room (`axiom.db`),
Preferences DataStore (`axiom_prefs`), and two `SharedPreferences` files (`axiom_lang`,
`axiom_skill_mastery`). Different concepts landed in different stores as the app grew.

## Decision (as-built)

- **Relational / list-shaped domain data lives in Room:** missions, skills, dungeons, shadows,
  the warrior-blueprint graph (11 tables via `WarriorBlueprintDao`), habit logs, vitals, KPI
  progress, weekly reviews.
- **Single-valued scalars, flags, and settings live in DataStore** (`AxiomPreferences`): theme,
  language, onboarding/briefing flags, character `stat_*`, and the **live streak counters**.
- **Locale bootstrap** reads `SharedPreferences("axiom_lang")` synchronously in
  `attachBaseContext` (DataStore is async and locale must be set pre-inflate).
- **Gemini API key** is never in a plain store — it is encrypted via `AndroidGeminiKeyStore`.

## Consequences

- Fast, offline reads; no network dependency for core loops.
- **[KNOWN DEBT]** the boundary is crossed for some concepts:
  - **Streak** — live source is DataStore (`streakFlow`, written by
    `AxiomPreferences.checkOffDailyProtocol()` from the ViewModel); the `StreakEntity`/`streak`
    Room table is written only by `CloudSyncRepository`. The two stores can diverge.
  - **System feed** — live source is an in-memory `MutableStateFlow` (cap 50, lost on process
    death); the `SystemFeedEntity` table is used only by cloud sync.
  - **KPI progress / weekly review** entities are read/written directly from ViewModels,
    bypassing the repository layer.
  - **Language** is duplicated (DataStore `language` + `axiom_lang`); **skill mastery** lives in
    a third `SharedPreferences` file outside the Flow/repository model.
- Consolidation is a future decision; this ADR records the current split so callers know which
  store is authoritative for each concept (DataStore wins for live streak/stats).
