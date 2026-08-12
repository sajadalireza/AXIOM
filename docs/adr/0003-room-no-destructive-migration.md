# ADR-0003 — No destructive Room migration fallback

- **Status:** Accepted (as-built)
- **Context ref:** `ARCHITECTURE.md` §8

## Context

`AxiomDatabase` (`axiom.db`) holds the user's irreplaceable on-device data: missions, streaks,
skill progression, and financial checkpoints. Room offers `fallbackToDestructiveMigration()`,
which silently drops and recreates tables when no migration path is found — trading a crash for
total local data loss.

## Decision (as-built)

- `AppModule.provideAxiomDatabase` registers explicit migrations `MIGRATION_1_6 … MIGRATION_15_16`
  and **deliberately does not** call `fallbackToDestructiveMigration`. An unresolved upgrade path
  therefore throws `IllegalStateException` (loud crash) rather than wiping data.
- Invariant (enforced by the `AxiomDatabase` header comment): every entity change MUST bump
  `version` AND add a matching `Migration`.
- CI enforces schema integrity: the **Room Schema** check regenerates schemas
  (`:app:kspDebugKotlin`) and fails on uncommitted drift in `app/schemas/` (see `docs/CI.md`).

## Consequences

- A missing/incorrect migration fails fast and visibly instead of destroying user data — the
  intended data-safety posture.
- Contributors must author a migration for every schema change; the CI drift check catches an
  uncommitted regenerated schema.
- **[KNOWN DEBT]** migration bodies live in `data/local/db/migrations/` while DI wires thin
  alias constants from `db/migrations/`; both packages are live (redundant indirection, not a
  correctness issue). See `ARCHITECTURE.md` §8.
