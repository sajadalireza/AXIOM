# AXIOM Vocabulary Deprecation Plan v1.0

## Document Control

- Status: Controlled migration plan when merged to `main`
- Owner: Product Owner (`sajadalireza`)
- Version: `1.0`
- Effective date: `2026-08-06`
- Work Packet: `WP-002 — Vocabulary Lock`
- Decision: `DECISION A — CONSTITUTION-PRESERVING MISSION`
- Issue: `https://github.com/sajadalireza/AXIOM/issues/6`
- Authorized baseline: `main@1c5d7a483a469740c13ab2ff06e0007ffa4d6fc5`
- Canonical definitions: [AXIOM Canonical Product Vocabulary](CANONICAL_VOCABULARY.md)
- Governing Constitution: [AXIOM Product Constitution](PRODUCT_CONSTITUTION.md)

## Purpose

This document inventories relevant vocabulary surfaces, classifies current terms, and defines phased, reversible migration plans. It does not execute a rename, schema migration, route migration, event migration, resource-key migration, UI redesign, or behavior change.

## Inspection Method and Evidence Boundary

WP-002 inspected the exact authorized baseline across:

- canonical product and execution documentation;
- Kotlin domain models, persistence entities, repositories, use cases, focus orchestration, onboarding, navigation, and product UI;
- English and Persian string resources;
- analytics infrastructure;
- unit and migration test trees.

The repository was not available in the connector’s code-search index. Therefore, this plan does not claim an automated exact count of every textual occurrence. It uses the complete repository tree plus direct read-back of semantic load-bearing files. Negative findings are stated narrowly: for example, no `Task`-named product domain type or route was found in the inspected tree, rather than claiming the word never appears anywhere.

A future report-only vocabulary scanner is proposed below to produce reproducible occurrence counts and prevent new debt.

## Classification Taxonomy

| Classification | Meaning |
|---|---|
| `CANONICAL` | The occurrence uses a canonical term with the approved semantic meaning. |
| `CONTEXT-SPECIFIC — ACCEPTABLE` | The occurrence uses a narrower presentation or engineering term whose scope is clear and does not replace the canonical term. |
| `LEGACY ALIAS` | The term represents or presents a canonical concept under an older name and requires a compatibility-aware migration plan. |
| `AMBIGUOUS OR MISLEADING` | The occurrence could map to multiple canonical concepts, uses a term decoratively, or implies the wrong semantic relationship. |
| `ENGINEERING-ONLY` | The term is valid in implementation, planning, component naming, or technical operations and is not a product-domain noun. |
| `PROGRAM-GOVERNANCE-ONLY` | The term belongs to the AXIOM upgrade execution program rather than the product domain. |

## Inventory Summary

| ID | Current surface | Current term | Classification | Canonical mapping or replacement |
|---|---|---|---|---|
| V-001 | `docs/product/PRODUCT_CONSTITUTION.md` | Goal | `CANONICAL` | Goal |
| V-002 | `docs/product/PRODUCT_CONSTITUTION.md` | Mission | `CANONICAL` | Mission |
| V-003 | `domain/model/Mission.kt`, `MissionEntity.kt`, Mission repositories and use cases | Mission | `CANONICAL` | Mission; implementation properties do not define semantics |
| V-004 | `navigation/Screen.kt` routes `missions`, `add_mission`, `mission_detail` | Mission | `CANONICAL` | Mission; route compatibility must be preserved |
| V-005 | English/Persian Mission surfaces that describe a bounded focus execution period | Session | `CONTEXT-SPECIFIC — ACCEPTABLE` | Focus Session or Practice Session, linked to a Mission or Skill |
| V-006 | `strings.xml` and `values-fa/strings.xml` grouping broad Mission surfaces as habits | Habit | `AMBIGUOUS OR MISLEADING` | Mission for executable commitments; Habit only for recurring behavior or recurrence source |
| V-007 | No `Task`-named product type or route found in the inspected tree; issue and engineering planning language may use Task | Task | `ENGINEERING-ONLY` | Mission or Action if future product-facing usage appears |
| V-008 | `AddMissionStep4Protocols.kt` deliberate-practice question and `CompleteMissionUseCase` parameter `goalSet` | Goal | `AMBIGUOUS OR MISLEADING` | Practice target or Session target |
| V-009 | `MainQuestScreen.kt`, `main_quest_*` resources, and `personalThesis` storage | Quest / Mission / reason / personal thesis | `AMBIGUOUS OR MISLEADING` | Purpose or Goal after an explicit onboarding intent split |
| V-010 | `domain/model/Dungeon.kt`, Dungeon persistence, repositories, use cases, routes, and UI | Dungeon | `LEGACY ALIAS` | Project, or an explicitly specialized gamified Project view |
| V-011 | Dungeon UI copy in English and Persian | Sprint | `LEGACY ALIAS` and sometimes `CONTEXT-SPECIFIC — ACCEPTABLE` | Project generally; Sprint only for an explicit timeboxed phase |
| V-012 | `domain/model/Skill.kt`, `SkillEntity.kt`, Skill repositories/use cases, Skill Tree | Skill | `CANONICAL` | Skill |
| V-013 | `GatesScreen.kt` and `nav_gates` routing container | Gate | `AMBIGUOUS OR MISLEADING` | Missions & Projects, Work, or another neutral container selected by a bounded UX packet |
| V-014 | `Mission.isInstantGate`, persistence field, creation flow, and `instant_gate_protocol_*` copy | Gate | `AMBIGUOUS OR MISLEADING` | Timed Mission or Focus Mission |
| V-015 | `EXECUTION_WORKFLOW.md`, Gate roadmap, Acceptance Gates | Gate | `PROGRAM-GOVERNANCE-ONLY` | Gate |
| V-016 | `FocusProtocolManager.kt` timer orchestration | Protocol | `CONTEXT-SPECIFIC — ACCEPTABLE` | Focus Protocol |
| V-017 | `AddMissionStep4Protocols.kt` as a configuration and completion form | Protocol | `AMBIGUOUS OR MISLEADING` | Mission options, Practice settings, or Completion settings by subsection |
| V-018 | decorative `SYSTEM PROTOCOL`, app subtitles, status copy, and generic protocol labels in resources | Protocol | `AMBIGUOUS OR MISLEADING` | Specific noun such as Session, Settings, Workflow, Status, or Procedure |
| V-019 | No explicit canonical Purpose model; likely overlap with `personalThesis` and onboarding reason copy | Purpose | `CANONICAL` definition, current implementation unresolved | Purpose after behavior and data review |
| V-020 | Component names such as `HomeActionBar.kt` and callback/action terminology | Action | `ENGINEERING-ONLY` | Action only when exposed as an observable product-domain step |
| V-021 | `AnalyticsLogger.kt` accepts unrestricted string event names | Mission, Goal, Project, Skill and aliases in analytics | `AMBIGUOUS OR MISLEADING` risk | Typed canonical event dictionary with versioned aliases |
| V-022 | Resource keys such as `nav_gates`, `missions_*`, `dungeons_*`, `instant_gate_protocol_*` | legacy vocabulary in identifiers | `LEGACY ALIAS` | Preserve keys initially; migrate only with compatibility evidence |
| V-023 | Unit and migration tests | vocabulary consistency coverage | `AMBIGUOUS OR MISLEADING` gap | Add report-only vocabulary check, then bounded CI enforcement |
| V-024 | `docs/product/EXECUTION_WORKFLOW.md` references Work Packet tasks and Gates | Task / Gate | `PROGRAM-GOVERNANCE-ONLY` or `ENGINEERING-ONLY` | No product-domain migration required |

Paths in the table are abbreviated where the repository prefix is clear. Detailed records below identify the load-bearing files and migration boundary.

## Canonical and Acceptable Records

### V-001 and V-002 — Constitution Goal and Mission

**Surfaces**

- `docs/product/PRODUCT_CONSTITUTION.md`
- Core Loop, positioning, immutable identity, and non-negotiable principles

**Classification**

- `Goal`: `CANONICAL`
- `Mission`: `CANONICAL`

**Rationale**

The Constitution explicitly transforms real Goals into small executable Missions and uses `Real goals. Small missions. Visible progress.` as a brand line. WP-002 preserves this meaning without editing the Constitution.

**Migration**

None in WP-002. Future documents and product changes must conform.

### V-003 and V-004 — Mission Domain, Persistence, Use Cases, and Routes

**Surfaces**

- `app/src/main/java/com/axiom/app/domain/model/Mission.kt`
- `app/src/main/java/com/axiom/app/data/local/entity/MissionEntity.kt`
- `app/src/main/java/com/axiom/app/domain/repository/MissionRepository.kt`
- `app/src/main/java/com/axiom/app/data/repository/MissionRepositoryImpl.kt`
- `app/src/main/java/com/axiom/app/domain/usecase/CreateMissionUseCase.kt`
- `app/src/main/java/com/axiom/app/domain/usecase/CompleteMissionUseCase.kt`
- `app/src/main/java/com/axiom/app/navigation/Screen.kt`

**Classification**

`CANONICAL`

**Rationale**

The current model is executable, completable, time-bounded, persistent, and linked to Skill progression. These are compatible implementation properties for Mission, but the properties do not independently define the term.

**Compatibility rule**

Current table, route, repository, and identifier names remain unchanged. Any future structural change still requires migration, deep-link, saved-state, analytics, and rollback evidence.

### V-005 — Session as a Bounded Execution Period

**Surfaces**

- English and Persian Mission screen copy
- focus timer and completion flows
- Skill deliberate-practice logging

**Classification**

`CONTEXT-SPECIFIC — ACCEPTABLE`

**Canonical relationship**

A Session is a bounded execution or practice period. It may execute a Mission or record practice against a Skill. It does not replace Mission as the work entity.

**Guardrail**

Prefer scoped labels such as `Focus Session` or `Practice Session`. Do not rename all Mission surfaces to Session or infer one Mission equals exactly one Session.

### V-012 — Skill

**Surfaces**

- `app/src/main/java/com/axiom/app/domain/model/Skill.kt`
- `app/src/main/java/com/axiom/app/data/local/entity/SkillEntity.kt`
- Skill repositories and use cases
- Skill Tree presentation

**Classification**

`CANONICAL`

**Rationale**

The implementation models a repeatable capability with practice-based progression. XP, levels, effective hours, hierarchy, and unlocks are mechanisms, not the semantic definition.

### V-015 — Program Gate

**Surfaces**

- `docs/product/EXECUTION_WORKFLOW.md`
- `docs/product/UPGRADE_MASTER_PLAN.md`
- Work Packet Acceptance Gates and evidence decisions

**Classification**

`PROGRAM-GOVERNANCE-ONLY`

**Rationale**

These Gates are objective pass/fail conditions evaluated through evidence and Acceptance Criteria. No product copy migration is required.

### V-016 — Focus Protocol

**Surface**

- `app/src/main/java/com/axiom/app/domain/focus/FocusProtocolManager.kt`

**Classification**

`CONTEXT-SPECIFIC — ACCEPTABLE`

**Rationale**

The manager orchestrates a repeatable timer procedure including start, pause, resume, persistence, recovery, and completion. This is a valid Protocol usage.

## Deprecated and Ambiguous Records

Each record below is planning-only. No migration is executed by WP-002.

### D-001 — Habit Used as a Universal Mission Label

- **Current surface:** `app/src/main/res/values/strings.xml`, `app/src/main/res/values-fa/strings.xml`, Mission list and creation copy.
- **Current term:** `Habit`, including broad labels equivalent to “Habits & Sessions” and CTAs that imply every Mission is a continuing habit.
- **Classification:** `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** `Mission` for an executable commitment; `Habit` only when a recurring behavior or recurrence source is actually modeled.
- **Semantic rationale:** A one-time Mission and a recurring Habit are not interchangeable. Habit describes recurrence; Mission describes the meaningful completable commitment.
- **Affected layers:** English copy, Persian copy, accessibility labels, screenshots, product analytics interpretation, UX documentation.
- **Migration risk:** Medium. Copy-only changes appear low-risk but may expose missing recurrence behavior or alter user expectations.
- **Compatibility concern:** Resource keys may remain legacy even after copy changes; translation parity and screenshot tests must remain stable.
- **Proposed follow-up Work Packet:** `WP-TBD — Mission and Habit Presentation Boundary`.
- **Rollback approach:** Revert copy commit through a protected PR; preserve resource keys during the first phase.

### D-002 — Session Used as the Universal Work Entity

- **Current surface:** Mission navigation and list copy that presents the entire entity as Session rather than distinguishing the execution period.
- **Current term:** `Session`.
- **Classification:** `CONTEXT-SPECIFIC — ACCEPTABLE` only when scoped; otherwise `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** `Mission` for the work entity; `Focus Session` or `Practice Session` for the execution period.
- **Semantic rationale:** One Mission may span multiple Sessions, and a Session may log Skill practice without creating a distinct Mission.
- **Affected layers:** UI copy, onboarding explanations, completion dialog, timer copy, analytics dimensions, support documentation.
- **Migration risk:** Medium because current screens appear to merge Habit, Session, and Mission mental models.
- **Compatibility concern:** Existing persisted Mission records and routes must remain unchanged; only semantic copy changes should occur first.
- **Proposed follow-up Work Packet:** `WP-TBD — Mission and Session UX Model`.
- **Rollback approach:** Restore prior localized copy while keeping canonical documents and data unchanged.

### D-003 — Practice Goal Used for a Temporary Session Target

- **Current surface:** `app/src/main/java/com/axiom/app/presentation/missions/AddMissionStep4Protocols.kt`; `goalSet` parameter in `CompleteMissionUseCase.kt`.
- **Current term:** `Goal` in the question “Did you have a specific goal for this session?” and the `goalSet` implementation identifier.
- **Classification:** `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** UI: `Practice target` or `Session target`; implementation identifier: `practiceTargetSet` in a separately authorized refactor.
- **Semantic rationale:** A temporary deliberate-practice target is not the desired real-world outcome represented by canonical Goal.
- **Affected layers:** English/Persian copy, use-case parameters, tests, analytics properties if added, accessibility labels.
- **Migration risk:** Low for copy; medium for identifier refactor because call sites and tests must be updated together.
- **Compatibility concern:** No schema change appears required, but serialized or analytics property names must be checked before identifier migration.
- **Proposed follow-up Work Packet:** `WP-TBD — Deliberate Practice Target Vocabulary`.
- **Rollback approach:** Revert the bounded copy/refactor commit; retain boolean behavior and persisted data unchanged.

### D-004 — Main Quest and Personal Thesis Collapse Purpose and Goal

- **Current surface:** `app/src/main/java/com/axiom/app/presentation/onboarding/MainQuestScreen.kt`, `main_quest_*` resources, `Hunter.personalThesis` or corresponding persistence.
- **Current term:** `Main Quest`, `core operational mission`, reason for activation, and `personalThesis`.
- **Classification:** `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** `Purpose` for the durable why; `Goal` for a desired real-world outcome. The screen may need one concept or an explicit two-step split after product validation.
- **Semantic rationale:** The current prompt mixes reason, intent, thesis, mission, and outcome. Storing one string does not prove which canonical concept it represents.
- **Affected layers:** Onboarding UI, persistence field, repository mapping, restore behavior, copy, tests, analytics, migration policy, privacy because the text may be sensitive free-form intent.
- **Migration risk:** High. A field rename without semantic and data review could misclassify existing user text.
- **Compatibility concern:** Existing `personalThesis` data must remain readable; Purpose/Goal text must never be added to analytics payloads.
- **Proposed follow-up Work Packet:** `WP-TBD — Onboarding Purpose and Goal Contract`.
- **Rollback approach:** Preserve the old field as source-of-truth until a versioned migration is proven; feature-flag new interpretation and revert UI mapping if validation fails.

### D-005 — Dungeon as a Project Alias

- **Current surface:** `Dungeon.kt`, `DungeonEntity.kt`, DAO, repositories, use cases, screens, components, routes, migration history, and tests.
- **Current term:** `Dungeon`.
- **Classification:** `LEGACY ALIAS`.
- **Canonical replacement:** `Project`, or a documented `Dungeon` presentation subtype/view of Project if gamification remains.
- **Semantic rationale:** The model is bounded multi-step work with named stages and completion, which maps most closely to Project. The fantasy noun is a presentation metaphor, not the canonical product entity.
- **Affected layers:** Domain model, Room schema, migrations, repository interfaces, use cases, routes, saved state, UI, localization, accessibility, analytics, tests, backup/restore, deep links.
- **Migration risk:** High.
- **Compatibility concern:** Existing rows, route names, persisted IDs, user data, analytics history, and external links must remain readable. A table or route rename cannot be bundled with copy cleanup.
- **Proposed follow-up Work Packet:** `WP-TBD — Project Model and Dungeon Compatibility Layer`.
- **Rollback approach:** Introduce additive Project adapters and legacy aliases first; retain Dungeon storage and routes until dual-read verification passes; rollback by disabling canonical adapters without rewriting data.

### D-006 — Sprint Used as a Universal Dungeon/Project Name

- **Current surface:** English and Persian Dungeon resources and UI labels, including `SPRINTS`, create Sprint, active Sprint, Sprint checkpoints, and Sprint completion.
- **Current term:** `Sprint`.
- **Classification:** `LEGACY ALIAS` when it names the general entity; `CONTEXT-SPECIFIC — ACCEPTABLE` only for a real timeboxed phase.
- **Canonical replacement:** `Project` for the general entity; retain `Sprint` only after defining duration, lifecycle, and relationship to Project.
- **Semantic rationale:** A Project can last beyond one Sprint, and a Sprint is an execution method rather than the universal multi-step work object.
- **Affected layers:** English/Persian resources, UI hierarchy, navigation, analytics labels, screenshots, help content.
- **Migration risk:** Medium for copy, high if coupled to Dungeon schema or route changes.
- **Compatibility concern:** Do not rename resource keys, routes, or storage in the copy packet. Translation must preserve Project/Sprint distinction.
- **Proposed follow-up Work Packet:** `WP-TBD — Project and Sprint Presentation Contract`.
- **Rollback approach:** Revert display copy independently; retain all legacy identifiers.

### D-007 — Gate as a Navigation Container

- **Current surface:** `app/src/main/java/com/axiom/app/presentation/missions/GatesScreen.kt`, `nav_gates`, the Missions/Dungeons top-level container, and routing subtitles.
- **Current term:** `Gate`, including “SYSTEM ROUTING GATES”.
- **Classification:** `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** A neutral container such as `Missions & Projects`, `Work`, or `Execution`; final copy requires a bounded information-architecture decision.
- **Semantic rationale:** A navigation container is not an evidence-evaluated pass/fail condition.
- **Affected layers:** navigation labels, screen/class name, resource keys, routes if any, analytics screen names, accessibility, screenshots, help copy.
- **Migration risk:** Medium. Copy is low-risk, but class/route/event changes can break navigation and historical analytics.
- **Compatibility concern:** Keep legacy class and route identifiers initially; map new display copy to the existing route.
- **Proposed follow-up Work Packet:** `WP-TBD — Navigation Gate Vocabulary Cleanup`.
- **Rollback approach:** Restore the prior display label without changing navigation graph or persisted state.

### D-008 — Instant Gate as a Timed Mission

- **Current surface:** `Mission.isInstantGate`, `MissionEntity.isInstantGate`, `CreateMissionUseCase`, Mission creation UI, and `instant_gate_protocol_*` resources.
- **Current term:** `Instant Gate` and `Instant Gate Protocol`.
- **Classification:** `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** `Timed Mission`, `Focus Mission`, or a more specific Mission subtype selected by behavior review.
- **Semantic rationale:** The flag changes rarity/color and timing behavior; it does not represent an evidence-based pass/fail Gate.
- **Affected layers:** domain field, Room column, mapping, creation use case, UI, resources, tests, analytics, migrations.
- **Migration risk:** High if the field or column is renamed; low-to-medium for display copy.
- **Compatibility concern:** Existing persisted boolean values must retain behavior. Column and serialized names should remain until an additive migration is tested.
- **Proposed follow-up Work Packet:** `WP-TBD — Timed Mission Compatibility Rename`.
- **Rollback approach:** First change display copy only. Later use dual-name adapters and retain the legacy column; rollback by restoring adapter mapping.

### D-009 — Protocol as a Configuration Form

- **Current surface:** `app/src/main/java/com/axiom/app/presentation/missions/AddMissionStep4Protocols.kt`.
- **Current term:** `Protocols` for a screen containing Dungeon assignment, a timed-Mission toggle, completion logging, and deliberate-practice questions.
- **Classification:** `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** `Mission options`, `Practice settings`, and `Completion settings` by subsection.
- **Semantic rationale:** A mixed configuration form is not one repeatable procedure or rule set.
- **Affected layers:** class/component name, headings, resources, tests, accessibility, analytics screen/step names.
- **Migration risk:** Low for headings; medium for Kotlin identifier rename and analytics continuity.
- **Compatibility concern:** Preserve navigation step order and state keys; avoid combining copy and behavior refactors.
- **Proposed follow-up Work Packet:** `WP-TBD — Mission Creation Step Vocabulary`.
- **Rollback approach:** Revert headings or component rename while retaining form behavior and state.

### D-010 — Decorative Protocol Copy

- **Current surface:** app subtitle, status messages, headers, system feed copy, league copy, onboarding copy, and other English/Persian resources using `Protocol` as atmosphere.
- **Current term:** `Protocol`.
- **Classification:** `AMBIGUOUS OR MISLEADING` unless an actual repeatable sequence or rule set is named.
- **Canonical replacement:** the specific concept: `Session`, `Workflow`, `Settings`, `Status`, `Procedure`, `Rule`, `Mission`, or no noun.
- **Semantic rationale:** Decorative repetition reduces the term’s precision and makes real Protocols indistinguishable from cosmetic labels.
- **Affected layers:** copy, localization, accessibility, screenshots, product comprehension, analytics screen labels.
- **Migration risk:** Medium due to large occurrence count and localization review, despite no runtime behavior change.
- **Compatibility concern:** Perform by bounded screen clusters, not mass replacement. Persian copy must preserve natural, culturally appropriate language rather than literal substitution.
- **Proposed follow-up Work Packet:** `WP-TBD — Protocol Copy Rationalization`.
- **Rollback approach:** Revert one screen cluster at a time; do not rename code identifiers or resources in the same packet.

### D-011 — Product-Facing Task

- **Current surface:** No `Task`-named product type or route was found in the inspected tree. Future copy, templates, analytics, or feature work remains at risk.
- **Current term:** `Task`.
- **Classification:** `ENGINEERING-ONLY` when used for issues, Work Packet planning, or implementation internals; product-facing use would be `AMBIGUOUS OR MISLEADING`.
- **Canonical replacement:** `Mission` for a meaningful completable commitment; `Action` for a smaller observable step.
- **Semantic rationale:** Task would create a competing product noun and weaken the Constitution’s Mission language.
- **Affected layers:** future copy, templates, domain types, analytics, help content, AI prompts.
- **Migration risk:** Low while prevented; potentially high if a parallel Task model is introduced.
- **Compatibility concern:** Engineering documentation must remain allowed and must not be falsely flagged as product copy.
- **Proposed follow-up Work Packet:** `WP-TBD — Vocabulary Consistency Lint`.
- **Rollback approach:** Report-only scanner first; revert a blocking rule if false positives disrupt engineering paths while preserving the product-facing policy.

### D-012 — Analytics Event Vocabulary

- **Current surface:** `app/src/main/java/com/axiom/app/core/AnalyticsLogger.kt` and all call sites.
- **Current term:** unrestricted `eventName: String`; possible embedded Mission, Session, Habit, Dungeon, Sprint, Gate, Protocol, Goal, or Task vocabulary.
- **Classification:** `AMBIGUOUS OR MISLEADING` risk.
- **Canonical replacement:** versioned typed event dictionary using canonical nouns and explicit context-specific event families.
- **Semantic rationale:** Stringly typed events can fragment one concept across aliases and make Goal progress, Mission completion, Project progress, Skill progression, and XP analytically indistinguishable.
- **Affected layers:** analytics call sites, backend table consumers, dashboards, cohorts, documentation, tests, consent review, retention queries.
- **Migration risk:** High. Renaming events can split time series and invalidate historical dashboards.
- **Compatibility concern:** Emit old and new names only under a time-bounded, privacy-reviewed dual-write plan or provide a warehouse mapping view. Never include private Goal, Purpose, Mission, evidence, or reflection free text.
- **Proposed follow-up Work Packet:** `WP-TBD — Typed Analytics Event Dictionary and Alias Bridge`.
- **Rollback approach:** Keep legacy emission path available behind a compatibility switch; rollback dashboards to alias-normalized views.

### D-013 — Legacy Resource Keys and Navigation Identifiers

- **Current surface:** resource families such as `missions_*`, `dungeons_*`, `nav_gates`, `instant_gate_protocol_*`; routes and Kotlin identifiers in `Screen.kt` and related screens.
- **Current term:** legacy vocabulary embedded in stable identifiers.
- **Classification:** `LEGACY ALIAS`.
- **Canonical replacement:** canonical displayed copy first; identifier migration only when compatibility value exceeds risk.
- **Semantic rationale:** Identifiers may preserve compatibility while product semantics improve. Renaming an identifier is not required merely because display copy changes.
- **Affected layers:** resources, localization, navigation, deep links, saved state, analytics screen names, tests, screenshots, build tooling.
- **Migration risk:** Medium to high.
- **Compatibility concern:** Resource and route aliases may need multi-release support. Existing deep links and saved states must remain valid.
- **Proposed follow-up Work Packet:** `WP-TBD — Resource and Route Vocabulary Compatibility`.
- **Rollback approach:** Preserve old identifiers and map them to canonical copy; remove aliases only after explicit usage and upgrade evidence.

### D-014 — Vocabulary Consistency Test Gap

- **Current surface:** `app/src/test` and `app/src/androidTest`.
- **Current term:** no vocabulary consistency test or lint contract.
- **Classification:** `AMBIGUOUS OR MISLEADING` evidence gap.
- **Canonical replacement:** deterministic report-only scanner followed by bounded CI enforcement.
- **Semantic rationale:** Documentation alone cannot prevent new product-facing aliases or detect drift between English, Persian, code, routes, resources, and events.
- **Affected layers:** repository scripts, CI, tests, allowlist, documentation review.
- **Migration risk:** Low in report-only mode; medium when made blocking because false positives can stop unrelated work.
- **Compatibility concern:** Engineering-only and program-governance paths require explicit allowlists. Historical identifiers must be distinguished from new product-facing debt.
- **Proposed follow-up Work Packet:** `WP-TBD — Vocabulary Consistency Lint`.
- **Rollback approach:** downgrade the check from blocking to advisory; retain generated reports and canonical definitions.

## Phased Migration Plan

### Phase 0 — Vocabulary Lock

**Packet:** WP-002

- Merge canonical definitions and this inventory.
- Do not change product code, schema, resources, routes, events, tests, dependencies, or configuration.
- Establish source-of-truth links in `docs/product/INDEX.md`.

**Exit evidence**

- Three-file boundary verified.
- Product Constitution unchanged.
- Four reviews pass.
- Protected merge and post-merge read-back pass.

### Phase 1 — Reproducible Detection, Advisory Only

- Add a repository vocabulary scanner.
- Produce exact path, line, term, layer, and provisional classification reports.
- Establish an allowlist for engineering-only and program-governance-only occurrences.
- Check English/Persian semantic pairing.
- Do not fail CI initially.

**Rollback**

Remove the CI invocation while retaining the report script for local use.

### Phase 2 — Low-Risk Product Copy Corrections

Use separate bounded packets for:

- practice `Goal` to `Practice target`;
- universal Habit/Session copy to scoped Mission/Focus Session language;
- navigation Gate display copy;
- overbroad Protocol headings;
- onboarding Purpose/Goal clarification only after its semantic contract is approved.

Do not rename identifiers, routes, fields, tables, or events in these packets.

**Rollback**

Revert each screen-cluster copy commit independently.

### Phase 3 — Compatibility Aliases and Typed Contracts

- Define typed analytics events and historical alias mapping.
- Introduce canonical resource constants while retaining old keys where needed.
- Add route aliases before any route removal.
- Add domain adapters for Project while retaining Dungeon persistence.

**Rollback**

Disable canonical adapters or new emissions while retaining legacy read/write paths.

### Phase 4 — Domain and Persistence Migration

Only after behavior, compatibility, and migration contracts pass:

- decide whether Dungeon becomes Project or a specialized Project view;
- migrate schemas additively;
- migrate fields such as `isInstantGate` only with dual-read evidence;
- split Purpose and Goal data if onboarding validation requires it.

**Rollback**

Use backward-compatible schemas, backup/export evidence, dual-read validation, and explicit down/restore procedures. Never remove legacy fields in the same release that introduces the new model.

### Phase 5 — Legacy Removal

Remove an alias only when:

- supported app versions no longer require it;
- deep-link and saved-state usage is below an approved threshold;
- analytics history is normalized;
- migration verification and rollback rehearsal pass;
- Product Owner approves the removal Gate.

## Proposed Vocabulary Consistency Check

A future packet should add a deterministic script such as:

`tools/check_vocabulary.py`

### Initial scan scope

- `app/src/main/**/*.kt`
- `app/src/main/res/values*/strings.xml`
- navigation routes and screen names
- analytics event literals and property keys
- product documentation
- tests and fixtures

### Rules

1. Reject new product-facing `Task` as a synonym for Mission.
2. Flag `Goal` near practice/session qualifiers unless allowlisted as `Practice target` migration debt.
3. Flag new decorative `Gate` and `Protocol` occurrences.
4. Flag new `Dungeon` or universal `Sprint` product nouns outside compatibility allowlists.
5. Flag Mission-as-purpose definitions.
6. Require canonical noun prefixes for new analytics events.
7. Compare English and Persian resource families for semantic category drift.
8. Permit engineering-only component names, issue language, coroutine or scheduler tasks, and program-governance Gates through path and rule-specific allowlists.
9. Treat existing legacy identifiers as baseline debt; fail only on net-new unapproved debt after the baseline report is accepted.

### Output

The check should emit machine-readable JSON and a concise Markdown report containing:

- path;
- line;
- matched term;
- layer;
- proposed classification;
- rule ID;
- allowlist status;
- canonical replacement guidance.

### Enforcement rollout

1. local/report-only;
2. non-blocking CI artifact;
3. blocking only for net-new high-confidence violations;
4. broader enforcement after false-positive review and Product Owner approval.

## Proposed Follow-up Work Packets

IDs must be assigned against the canonical Work Packet queue before activation. WIP remains one.

| Proposed packet | Smallest bounded outcome | Must not combine with |
|---|---|---|
| `WP-TBD — Vocabulary Consistency Lint` | Report-only deterministic inventory and allowlist | Product copy or schema changes |
| `WP-TBD — Deliberate Practice Target Vocabulary` | Replace practice-session Goal copy and identifiers without behavior change | Goal model creation |
| `WP-TBD — Mission and Habit Presentation Boundary` | Correct one bounded screen cluster and both localizations | Recurrence engine redesign |
| `WP-TBD — Mission and Session UX Model` | Define and apply the Session/Mission relationship to one flow | Timer refactor or data migration |
| `WP-TBD — Onboarding Purpose and Goal Contract` | Decide whether onboarding captures Purpose, Goal, or two explicit fields | Unreviewed persistence rename |
| `WP-TBD — Navigation Gate Vocabulary Cleanup` | Replace the Gate container display label while preserving routes | Navigation architecture rewrite |
| `WP-TBD — Mission Creation Step Vocabulary` | Replace overbroad Protocol headings in one form | Form behavior redesign |
| `WP-TBD — Protocol Copy Rationalization` | Correct decorative Protocol copy by one screen cluster | Mass resource-key rename |
| `WP-TBD — Typed Analytics Event Dictionary and Alias Bridge` | Typed schema plus historical alias strategy | Dashboard deletion or privacy expansion |
| `WP-TBD — Project Model and Dungeon Compatibility Layer` | Add canonical Project contract and compatibility adapter | Immediate table/route removal |
| `WP-TBD — Project and Sprint Presentation Contract` | Define when Sprint is a timeboxed Project phase | Full Project migration |
| `WP-TBD — Timed Mission Compatibility Rename` | Correct Instant Gate semantics with data compatibility | Unrelated Mission behavior changes |
| `WP-TBD — Resource and Route Vocabulary Compatibility` | Versioned alias and removal criteria | UI redesign |

## Global Rollback Rule

No deprecation migration may depend on destructive replacement as its first step.

Every follow-up packet must state:

- the pre-change identifier and behavior;
- additive compatibility mechanism;
- data backup or branch strategy where applicable;
- observed verification evidence;
- rollback command or protected revert path;
- alias removal criteria.

If semantic evidence conflicts with the canonical vocabulary, implementation stops and a Product Owner Decision Record is required.