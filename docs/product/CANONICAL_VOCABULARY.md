# AXIOM Canonical Product Vocabulary v1.0

## Document Control

- Status: Canonical when merged to `main`
- Owner: Product Owner (`sajadalireza`)
- Version: `1.0`
- Effective date: `2026-08-06`
- Work Packet: `WP-002 — Vocabulary Lock`
- Decision record: `DECISION A — CONSTITUTION-PRESERVING MISSION`
- Decision issue: `https://github.com/sajadalireza/AXIOM/issues/6`
- Authorized baseline: `main@1c5d7a483a469740c13ab2ff06e0007ffa4d6fc5`
- Governing source: [AXIOM Product Constitution](PRODUCT_CONSTITUTION.md)
- Deprecation plan: [Vocabulary Deprecation Plan](VOCABULARY_DEPRECATION_PLAN.md)
- Change policy: A semantic change requires an explicit Product Owner Decision Record, version increment, and protected Pull Request.

## Authority and Scope

This document defines AXIOM product-domain vocabulary. It supplements the Product Constitution without amending it. The Product Constitution remains the higher-precedence source for AXIOM identity, positioning, Core Loop, and non-negotiable principles.

Definitions in this document are semantic contracts. A current class, table, field, route, resource key, event name, progression mechanic, or visual metaphor may represent a concept, but no implementation property independently defines that concept.

This document does not authorize a rename, schema migration, event migration, route migration, UI redesign, or runtime behavior change. Those changes require separately accepted Work Packets.

## Product Owner Decision Record

The Product Owner approved:

`DECISION A — CONSTITUTION-PRESERVING MISSION`

The existing Product Constitution remains unchanged. `Mission` preserves its Constitution-defined meaning as the smallest meaningful executable commitment that advances a Goal.

An earlier vocabulary proposal used `Mission` to mean a durable reason or purpose. That proposal is superseded for AXIOM product-domain terminology. It is not erased: its useful concept is retained under the canonical term `Purpose`.

Consequently:

- `Mission` must not mean a durable purpose statement.
- `Purpose` owns the durable reason, intent, or “why” concept.
- Historical artifacts may retain the superseded proposal as evidence, but must be marked superseded when cited for current product-domain decisions.

## Canonical Relationship Model

```text
Purpose
  explains why a Goal, Project, or course of action matters

Goal
  is the desired real-world outcome
  may be advanced directly by Missions
  may also be advanced through one or more Projects

Project
  is bounded, multi-step work that produces a named outcome or artifact
  may contain multiple Missions

Mission
  is the smallest meaningful executable commitment with a done condition
  may contain or require one or more Actions
  advances a Goal directly or through a Project

Action
  is an observable physical or digital step inside or supporting a Mission

Skill
  is a repeatable capability used and improved while executing Missions and Projects
```

Purpose does not replace Goal. Goal does not replace Project. Project does not replace Mission. Mission does not replace Action. Skill is not a work item; it is a capability that work may exercise or improve.

## Canonical Terms

### Goal

**Definition**

`A desired real-world outcome.`

A Goal represents the result the user is trying to achieve.

**Allowed product usage**

- Use `Goal` for a meaningful desired result in the user’s life or work.
- Explanatory copy may describe it as an `outcome` when the relationship remains clear.
- A Goal may be advanced by Missions directly or through Projects.

**Domain and persistence mapping**

- Canonical future domain type: `Goal`.
- Absence of a current `Goal` entity does not authorize another term to absorb its meaning.
- Goal progress must remain semantically distinct from XP, Skill progression, or Mission completion.

**Event naming contract**

New product events should use the canonical noun, for example:

- `goal_created`
- `goal_updated`
- `goal_progress_updated`
- `goal_completed`

**Prohibited ambiguity**

Do not use `Goal` for:

- a temporary practice-session target;
- a single execution step;
- a Mission completion condition;
- a Project;
- a durable purpose statement.

Use `Practice target` or `Session target` for a temporary deliberate-practice target.

### Mission

**Definition**

`The smallest meaningful executable commitment that advances a Goal and has a clear completion condition.`

A Mission:

- is executable;
- advances a Goal;
- has an observable done condition;
- may record evidence or reflection;
- may produce execution feedback or progression;
- is not a durable purpose statement.

**Allowed product usage**

- Use `Mission` for the product’s canonical executable commitment.
- `Session` may describe the bounded execution period in which a Mission is performed.
- `Habit` may describe a recurring behavioral pattern or recurrence source, not every Mission.
- Copy may use a plain-language verb phrase for the Mission title while the underlying concept remains Mission.

**Domain and persistence mapping**

- Current `Mission`, `MissionEntity`, `MissionRepository`, and Mission use cases are implementation representations of the canonical concept.
- Status, timestamps, estimated hours, actual hours, XP reward, persistence, or UI style are implementation properties. They do not independently define Mission.

**Event naming contract**

New or migrated product events should use canonical Mission semantics, for example:

- `mission_created`
- `mission_scheduled`
- `mission_started`
- `mission_completed`
- `mission_deferred`
- `mission_evidence_recorded`
- `mission_reflection_recorded`

**Prohibited ambiguity**

Do not use `Mission` for:

- Purpose or personal “why”;
- a broad multi-stage Project;
- a generic engineering task;
- an Action that lacks a meaningful independent done condition;
- every focus Session or Habit regardless of semantics.

### Project

**Definition**

`A bounded multi-step body of work that produces a named outcome or artifact.`

A Project may contain multiple Missions.

**Allowed product usage**

- Use `Project` for bounded multi-step work with a named deliverable or outcome.
- `Sprint` may describe a timeboxed execution mode or phase of a Project when that distinction is explicit.
- `Dungeon` may remain a specialized gamified presentation until a separately authorized compatibility migration is completed.

**Domain and persistence mapping**

- Canonical future domain type: `Project`.
- Current `Dungeon` types, tables, routes, and copy are potential legacy or specialized representations; they are not silently redefined or renamed by WP-002.

**Event naming contract**

- `project_created`
- `project_started`
- `project_progress_updated`
- `project_completed`
- `project_archived`

A timeboxed phase may use `project_sprint_started` or a separately defined `sprint_*` event only after `Sprint` has an explicit typed contract.

**Prohibited ambiguity**

Do not use `Project` for:

- a single Mission;
- an indefinite life area;
- a Skill;
- a decorative container without a named outcome or artifact.

### Skill

**Definition**

`A repeatable capability improved through deliberate practice.`

**Allowed product usage**

- Use `Skill` for a capability that can be practised, demonstrated, and improved.
- `Skill tree`, `Skill stack`, level, XP, mastery tier, parent-child relation, and unlock state may visualize or organize Skills when copy does not redefine the concept.

**Domain and persistence mapping**

- Current `Skill`, `SkillEntity`, repositories, use cases, and Skill Tree surfaces represent the canonical concept.
- XP, levels, effective hours, parent-child relations, and unlocks are progression mechanisms; the semantic definition remains independent of them.

**Event naming contract**

- `skill_created`
- `skill_practice_recorded`
- `skill_progress_updated`
- `skill_unlocked`
- `skill_mastery_tier_changed`

**Prohibited ambiguity**

Do not use `Skill` for:

- a Project or Mission;
- a category with no repeatable capability;
- a level, XP balance, badge, or progression mechanism by itself.

### Purpose

**Definition**

`The durable reason, intent, or “why” behind a Goal, Project, or course of action.`

**Allowed product usage**

- Use `Purpose` for durable intent or reason.
- Explanatory copy may use `Why` or `Intent` when it clearly maps to Purpose.
- Purpose may explain a Goal, Project, or course of action without becoming the executable item.

**Domain and persistence mapping**

- WP-002 does not require a persistent `Purpose` entity.
- Existing fields such as `personalThesis` may be candidates for a future Purpose migration, but are not declared equivalent without a bounded behavior and data review.

**Event naming contract**

If Purpose becomes instrumented:

- `purpose_defined`
- `purpose_updated`
- `purpose_linked`

**Prohibited ambiguity**

Do not use `Purpose` as:

- a Mission;
- a Mission completion condition;
- a measurable Goal merely because it is important;
- a generic description field.

### Action

**Definition**

`An observable physical or digital step performed inside or in support of a Mission.`

**Allowed product usage**

- Use `Action` for an observable step.
- An Action may be described as the next step inside a Mission.
- An Action does not need to become a separate persistent domain entity in WP-002.

**Domain and persistence mapping**

- No new `Action` entity is required by this vocabulary lock.
- Technical identifiers such as UI action handlers remain engineering terms unless exposed as product-domain copy.

**Event naming contract**

Only instrument Actions that are explicitly modeled and privacy-reviewed, for example:

- `action_recorded`
- `action_completed`

Prefer Mission-level events when no separate Action contract exists.

**Prohibited ambiguity**

Do not use `Action` for:

- a broad Mission;
- a Project;
- a CTA label merely because it is clickable;
- an unobservable intention.

### Task

**Definition**

`Not a canonical AXIOM product entity.`

**Allowed usage**

`Task` may remain in:

- engineering planning;
- issue tracking;
- implementation internals;
- technical documentation where it does not refer to a user-facing AXIOM domain entity.

**Domain and persistence mapping**

- No canonical product `Task` type, table, repository, route, resource family, or event family should be introduced.

**Event naming contract**

- Do not introduce product analytics events with `task_*` as a synonym for Mission.
- Engineering automation events may use `task` only when clearly outside the AXIOM product domain.

**Prohibited ambiguity**

Task must not become a competing product-facing synonym for Mission.

When a product-facing `Task` occurrence is found:

- replace it with `Mission` when it is a meaningful executable commitment with a done condition;
- replace it with `Action` when it is only an observable step inside or supporting a Mission.

### Gate

**Definition**

`An objective pass/fail condition evaluated through explicit evidence or Acceptance Criteria.`

**Allowed product and program usage**

- Execution-program Gates retain this canonical meaning.
- A future product Gate is allowed only when it represents an explicit condition with evidence and a pass/fail result.

**Domain and persistence mapping**

- Program-governance Gate records are not product-domain entities.
- UI routing containers, navigation labels, timed Missions, and decorative portals are not Gates merely because they use the word.

**Event naming contract**

Where an actual Gate is instrumented:

- `gate_evaluated`
- `gate_passed`
- `gate_failed`

Events must identify the Gate type and evidence contract without recording sensitive evidence content.

**Prohibited ambiguity**

Do not use `Gate` for:

- a navigation container;
- a tab group;
- a decorative portal;
- a timed Mission;
- a generic lock or unlock state without an explicit evidence-based condition.

### Protocol

**Definition**

`A repeatable sequence, procedure, or rule set.`

**Allowed product and engineering usage**

- Use `Protocol` for a repeatable operational sequence or rule set.
- `FocusProtocolManager` is semantically acceptable when it orchestrates a repeatable focus-timer procedure.
- A documented recovery, migration, experiment, or verification procedure may be a Protocol.

**Domain and persistence mapping**

- A Protocol may be implemented through services, managers, configuration, documentation, or orchestration.
- It does not require a single canonical persistence type.

**Event naming contract**

Only when a real Protocol lifecycle exists:

- `protocol_started`
- `protocol_step_completed`
- `protocol_completed`
- `protocol_aborted`

Prefer the more specific domain noun when the event is actually about a Mission, Project, or Session.

**Prohibited ambiguity**

Not every configuration screen, toggle group, questionnaire, completion form, header, subtitle, status message, or decorative system label is a Protocol.

## Context-Specific Presentation Terms

### Session

`Session` is acceptable for a bounded period of execution, focus, or deliberate practice. It is not a universal replacement for Mission.

A Mission may be executed in one Session, across multiple Sessions, or without a dedicated timed Session. A Session may record work against a Mission or Skill, but must not silently redefine the work item.

### Habit

`Habit` is acceptable for a recurring behavior or recurrence pattern. It is not a universal replacement for Mission.

A Habit may generate recurring Missions or describe a repeated behavior. A one-time Mission must not be presented as a Habit solely because the current screen groups them together.

### Sprint

`Sprint` is acceptable when it explicitly means a timeboxed execution period or phase. It must not silently replace every Project or remain the only name for a general multi-step work entity.

### Dungeon

`Dungeon` is a legacy or specialized gamified presentation candidate for Project. It may remain for compatibility until a dedicated Project migration packet determines the model, UX, persistence, route, and analytics strategy.

## UI Copy Contract

- Product-facing nouns must follow this vocabulary.
- Plain-language verb phrases are encouraged for titles and CTAs, but explanatory labels must not create a competing domain model.
- Context-specific terms must identify their scope. For example, `Focus Session` is clearer than using `Session` as the universal work entity.
- New copy must not use `Task` as a synonym for Mission.
- New copy must not use `Gate` or `Protocol` decoratively.
- English and Persian localization must preserve the same semantic distinction; translation must not collapse Goal, Purpose, Project, Mission, Action, or Skill into one generic term.

## Domain-Type Contract

- Domain type names should use canonical terms when the type represents the canonical concept.
- Existing legacy names remain compatible until a separately accepted migration packet changes them.
- New domain types must not duplicate a canonical concept under a new noun.
- A specialized type must document its relationship to the canonical type instead of silently replacing it.

## Analytics Event Contract

Future event dictionary work must:

1. use `lower_snake_case` event names;
2. start with the canonical domain noun when the event concerns that concept;
3. describe an observable lifecycle or state transition;
4. avoid decorative aliases in event names;
5. avoid sensitive user text, evidence content, reflection content, Purpose text, Goal text, Mission title, or other private free text in event properties;
6. preserve compatibility through an explicit versioned event-alias plan when renaming existing events;
7. keep Goal progress, Mission completion, Skill progression, Project progress, and XP events analytically distinct.

WP-002 defines this naming contract but does not rename or emit any event.

## Resource-Key and Navigation Contract

- New user-facing resource keys should use canonical semantics.
- Existing legacy resource keys may remain during compatibility migrations even when displayed copy changes.
- Resource-key names do not define the product-domain meaning.
- Routes must not be renamed without a deep-link, saved-state, analytics, test, and rollback review.
- A legacy route may map to a canonical screen during a transition, but the alias and removal criteria must be explicit.

## Conflict Rule

No new product-facing term may silently duplicate, absorb, narrow, broaden, or redefine a canonical term.

When a proposed term overlaps a canonical definition:

1. stop implementation;
2. identify the canonical term and affected surfaces;
3. classify the new term as context-specific, specialized, legacy, or conflicting;
4. obtain a Product Owner Decision Record if semantic meaning would change;
5. update this document and the deprecation plan through a protected Pull Request before implementation.

Implementation precedent, visual style, historical copy, analytics naming, or database naming cannot silently override this rule.

## Verification Questions

Before approving product copy, a type, a route, a resource, or an event, reviewers must be able to answer:

1. Is this a desired outcome (`Goal`), bounded multi-step work (`Project`), executable commitment (`Mission`), observable step (`Action`), durable why (`Purpose`), or repeatable capability (`Skill`)?
2. Is a context-specific term being used only within its declared scope?
3. Does the name create a competing synonym?
4. Would a user, analyst, engineer, and reviewer map the occurrence to the same canonical concept?
5. Is a legacy identifier being preserved for compatibility rather than mistaken for current semantics?

If any answer is unclear, the occurrence is ambiguous and must enter the deprecation inventory before expansion.