# AXIOM Issue Hierarchy

## Status and authority

This document defines the GitHub-native Gate, Issue, label, ownership, and integrity hierarchy established by `WP-004 — Issue Hierarchy & Milestones`.

Source order:

1. Live GitHub Issues, Labels, and Milestones.
2. Issue `#15` and its Acceptance Contract.
3. `docs/product/CANONICAL_REPOSITORY.md`.
4. `docs/product/PRODUCT_CONSTITUTION.md`.
5. `docs/product/CANONICAL_VOCABULARY.md`.
6. `docs/product/EXECUTION_WORKFLOW.md`.
7. `docs/product/UPGRADE_MASTER_PLAN.md`.
8. `docs/product/MODULE_DISPOSITION.md`.
9. `docs/product/MODULE_DISPOSITION.csv`.
10. `docs/product/VOCABULARY_DEPRECATION_PLAN.md`.
11. The canonical Prompt Playbook.
12. Accepted Product Owner decisions and verified evidence.

A conflict between canonical sources stops execution. This document does not amend the Product Constitution, canonical vocabulary, module dispositions, or Gate roadmap.

## Gate hierarchy and Milestones

| Gate | Milestone title | Purpose |
|---|---|---|
| G0 | G0 — Product & Scope Lock | Lock canonical repository identity, Product Constitution, vocabulary, module disposition, execution hierarchy, and G0 Gate evidence. |
| G1 | G1 — Repository Integrity | Establish reproducible, secure, and auditable repository and build integrity. |
| G2 | G2 — First-Win Vertical Slice | Deliver and verify the First-Win vertical slice with data integrity, consent, accessibility, and rollback. |
| G3 | G3 — Core Loop & Data Truth | Gate-level placeholder for Core Loop and Data Truth work. No Work Packet decomposition is authorized. |
| G4 | G4 — Instrumented Beta | Gate-level placeholder for Instrumented Beta work. No Work Packet decomposition is authorized. |
| G5 | G5 — Retention Proof | Gate-level placeholder for Retention Proof work. No Work Packet decomposition is authorized. |
| G6 | G6 — Monetization Proof | Gate-level placeholder for Monetization Proof work. No Work Packet decomposition is authorized. |
| G7 | G7 — Progressive Expansion | Gate-level placeholder for Progressive Expansion work. No Work Packet decomposition is authorized. |

G3 through G7 remain Gate-level placeholders only. Creating a Milestone or Issue does not authorize implementation or reactivate a frozen, hidden, validation-only, deferred, or retired module.

## Controlled Issue types

| Label | Meaning |
|---|---|
| `type:work-packet` | Controlled Work Packet. |
| `type:gate-review` | Evidence-gated Gate review. |
| `type:decision` | Product Owner or governance decision. |
| `type:repair` | Bounded repair packet. |
| `type:evidence` | Evidence collection or verification. |
| `type:risk` | Tracked product, technical, or governance risk. |

Work Packet, Gate Review, and Repair Issues are subject to the global WIP policy. Decision, Evidence, and Risk Issues do not independently authorize implementation.

## Program label taxonomy

### Gate labels

| Label | Meaning |
|---|---|
| `gate:G0` | Owning Gate: G0 — Product & Scope Lock. |
| `gate:G1` | Owning Gate: G1 — Repository Integrity. |
| `gate:G2` | Owning Gate: G2 — First-Win Vertical Slice. |
| `gate:G3` | Owning Gate: G3 — Core Loop & Data Truth. |
| `gate:G4` | Owning Gate: G4 — Instrumented Beta. |
| `gate:G5` | Owning Gate: G5 — Retention Proof. |
| `gate:G6` | Owning Gate: G6 — Monetization Proof. |
| `gate:G7` | Owning Gate: G7 — Progressive Expansion. |

### State labels

Only one state label may be present on a controlled Issue.

| Label | Meaning |
|---|---|
| `state:planned` | Planned; not authorized or active. |
| `state:authorized` | Explicitly authorized; not active and not WIP-consuming. |
| `state:active` | Current WIP-consuming Issue. |
| `state:blocked` | Current WIP-consuming Issue blocked by a named dependency. |
| `state:review` | Implementation or artifact complete and under review; still WIP-consuming. |
| `state:accepted` | Acceptance passed and closure is pending; still WIP-consuming. |
| `state:closed` | Completed and formally closed. |
| `state:superseded` | Replaced by a later controlled Product Owner decision. |

Canonical progression:

```text
planned
→ authorized
→ active
→ blocked or review
→ accepted
→ closed
```

`blocked` may return to `active` after its named blocker is cleared. `superseded` may occur only through an explicit Product Owner decision.

### Severity labels

| Label | Meaning |
|---|---|
| `severity:S0` | Critical integrity, security, or irreversible-loss risk. |
| `severity:S1` | High-severity release or Gate blocker. |
| `severity:S2` | Material but bounded defect. |
| `severity:S3` | Low-severity improvement or documentation concern. |

### Evidence labels

| Label | Meaning |
|---|---|
| `evidence:required` | Required evidence has not yet been completed. |
| `evidence:partial` | Some required evidence exists but is incomplete. |
| `evidence:verified` | Required evidence has been independently verified. |
| `evidence:missing` | Required evidence is absent or unavailable. |

An evidence label records evidence state; it does not replace actual logs, artifacts, read-backs, runtime observations, or user evidence.

### Decision labels

| Label | Meaning |
|---|---|
| `decision:required` | Product Owner decision is required. |
| `decision:approved` | Decision approved and recorded. |
| `decision:rejected` | Proposed decision rejected. |
| `decision:superseded` | Earlier decision replaced by a later record. |

## Canonical title conventions

| Issue type | Convention | Example |
|---|---|---|
| Work Packet | `WP-NNN — Canonical Title` | `WP-101 — Gradle Wrapper` |
| Gate Review | `WP-NNN — Gate Review Title` or `Gx Gate Review — Scope` | `WP-107 — G1 Reproducibility Review` |
| Product Owner Decision | `PRODUCT OWNER DECISION — BOUNDED SUBJECT` | `PRODUCT OWNER DECISION — CREATE PLANNED G0–G2 ISSUES` |
| Repair Packet | `RP-NNN — Bounded Repair Title` | `RP-001 — Repair WIP Classification` |
| Evidence | `EVIDENCE — WP-NNN — Subject` | `EVIDENCE — WP-004 — WIP Queries` |
| Risk | `RISK — Severity — Subject` | `RISK — S1 — Ruleset Weakening` |

Titles identify records; they do not change state. State is controlled by labels and explicit Product Owner decisions.

## Accountable owner requirements

Every controlled Work Packet, Gate Review, Decision, Repair, Evidence, or Risk Issue must identify:

- one accountable owner;
- one owning Gate when the record belongs to a Gate;
- predecessor and dependencies when applicable;
- objective and exact scope;
- non-goals;
- Must Acceptance Criteria or decision criteria;
- evidence requirements;
- rollback or resolution path.

Assignment supports accountability but does not authorize or activate work.

## Canonical Work Packet mapping

| Work Packet | Issue | Gate | Canonical state |
|---|---:|---|---|
| WP-000 | #1 | G0 | closed |
| WP-001 | #4 | G0 | closed |
| WP-002 | #6 | G0 | closed |
| WP-003 | #8 | G0 | closed |
| WP-004 | #15 | G0 | active |
| WP-005 | #16 | G0 | planned |
| WP-101 | #17 | G1 | planned |
| WP-102 | #18 | G1 | planned |
| WP-103 | #19 | G1 | planned |
| WP-104 | #20 | G1 | planned |
| WP-105 | #21 | G1 | planned |
| WP-106 | #22 | G1 | planned |
| WP-107 | #23 | G1 | planned |
| WP-201 | #24 | G2 | planned |
| WP-202 | #25 | G2 | planned |
| WP-203 | #26 | G2 | planned |
| WP-204 | #27 | G2 | planned |
| WP-205 | #28 | G2 | planned |
| WP-206 | #29 | G2 | planned |
| WP-207 | #30 | G2 | planned |
| WP-208 | #31 | G2 | planned |
| WP-209 | #32 | G2 | planned |

This mapping is complete for the authorized G0 through G2 decomposition. No G3 through G7 Work Packet identity is authorized by this document.

## Permanent incident exclusions

Issues `#10` through `#14` are immutable audit artifacts from `INCIDENT-WP004-PREINSPECTION-WRITES`.

They:

- are not Work Packets;
- consume no WIP;
- must remain `closed/not_planned`;
- remain unassigned;
- remain without program labels or Milestones;
- must not be reused, reopened, relabeled, repurposed, or reconciled into the canonical mapping.

## Creation, authorization, and activation

These are separate controls:

1. **Creation** records a planned Issue and consumes no WIP.
2. **Authorization** requires an explicit Product Owner decision and still consumes no WIP.
3. **Activation** requires an explicit Product Owner decision, replacement of the single state label with a WIP-consuming state, and a successful WIP preflight.
4. **Acceptance** does not authorize or activate the successor.
5. **Closure** is formal and evidence-gated.

Creating an Issue, branch name, Milestone, template, predecessor link, or successor link does not authorize or activate a Work Packet.

## Dashboard and integrity queries

### Global WIP

```text
is:issue is:open label:"state:active","state:blocked","state:review","state:accepted"
```

Expected current result: exactly one Issue, `#15`.

### Active Work Packet control

```text
is:issue is:open label:"type:work-packet" label:"state:active"
```

Expected current result: exactly `#15`.

### Active non-Work-Packet control

```text
is:issue is:open label:"state:active" -label:"type:work-packet"
```

Expected current result: `0`.

### Planned backlog

```text
is:issue is:open label:"state:planned"
```

Expected current result: `17`, Issues `#16` through `#32`.

### Historical completed Work Packets

```text
is:issue is:closed label:"type:work-packet" label:"state:closed" label:"gate:G0"
```

Expected current result: Issues `#1`, `#4`, `#6`, and `#8`.

### Integrity review

Before any activation, verify:

- exactly one state label exists on every controlled Issue;
- every canonical Work Packet is assigned to one owning Gate and Milestone;
- Issues `#10` through `#14` remain excluded;
- no G3 through G7 Work Packet Issue exists;
- the dependency graph in `WORK_PACKET_DEPENDENCIES.md` is complete and acyclic;
- the global WIP query returns exactly one current Issue.
