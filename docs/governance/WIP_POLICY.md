# AXIOM Global WIP Policy

## Policy statement

AXIOM has a global WIP limit of exactly one controlled WIP-consuming Issue.

The limit applies across:

- `type:work-packet`;
- `type:gate-review`;
- `type:repair`.

A Gate Review or Repair Issue cannot run beside the current Work Packet merely because its type differs.

## State consumption rules

Only one state label may exist on a controlled Issue.

| State | WIP consumption | Meaning |
|---|---:|---|
| `state:planned` | 0 | Recorded for visibility; not authorized or active. |
| `state:authorized` | 0 | Explicitly authorized but not active. |
| `state:active` | 1 | Current executing controlled Issue. |
| `state:blocked` | 1 | Current Issue is blocked by a named dependency and retains WIP. |
| `state:review` | 1 | Work or artifact is under evidence-gated review and retains WIP. |
| `state:accepted` | 1 | Acceptance passed but formal closure is pending; WIP remains consumed. |
| `state:closed` | 0 | Formally closed after required evidence and decision. |
| `state:superseded` | 0 | Replaced only through an explicit Product Owner decision. |

A blocked Issue remains the current packet. Blocking does not release WIP and does not permit its successor to start.

## Canonical transition path

```text
planned
→ authorized
→ active
→ blocked or review
→ accepted
→ closed
```

Permitted controlled variations:

- `blocked → active` after the named blocker is cleared and verified;
- `active → review` after the implementation or artifact is complete;
- `review → active` when repair is required within the same bounded packet;
- any transition to `superseded` only through an explicit Product Owner decision.

State changes must replace the existing state label; multiple state labels are invalid.

## Creation, authorization, and activation

- Issue creation does not imply authorization.
- Authorization does not imply activation and consumes no WIP.
- Activation requires an explicit Product Owner decision.
- Before activation, read the live repository, current Issues, open Pull Requests, branch state, and global WIP query.
- The activation mutation must leave exactly one WIP-consuming controlled Issue.
- Acceptance does not authorize or activate the next Work Packet.
- Formal closure or explicit Product Owner suspension or supersession is required before WIP can transfer.

## Branch and Pull Request rule

No branch or Pull Request may be created for an inactive future packet.

A controlled branch or Pull Request is allowed only when:

1. the associated Work Packet, Gate Review, or Repair Issue is the current active WIP-consuming Issue;
2. the exact baseline and branch name are explicitly authorized;
3. the changed-file or GitHub-object boundary is explicit;
4. rollback is documented;
5. no other controlled Issue consumes WIP.

## Canonical queries

### Global WIP query

```text
is:issue is:open label:"state:active","state:blocked","state:review","state:accepted"
```

Expected current result:

```text
exactly 1
#15 only
```

### Planned backlog query

```text
is:issue is:open label:"state:planned"
```

Expected current result:

```text
17
#16 through #32
```

### Active Work Packet query

```text
is:issue is:open label:"type:work-packet" label:"state:active"
```

Expected current result: exactly `#15`.

### Active non-Work-Packet control

```text
is:issue is:open label:"state:active" -label:"type:work-packet"
```

Expected current result: `0`.

## Activation procedure

Before any packet becomes active:

1. Verify the Product Owner decision names the exact Issue, baseline, branch, objective, boundary, invariants, non-goals, impact, rollback, and stop conditions.
2. Run the global WIP query.
3. Verify the predecessor and required Gate decision.
4. Verify the candidate Issue has exactly one state label and is not already active.
5. Verify no unauthorized branch or Pull Request exists.
6. Apply the bounded state transition.
7. Re-run the global WIP and negative-control queries.
8. Stop immediately if the result is not exactly one.

No compensating mutation is permitted without a new Product Owner decision when stop-on-error is triggered.

## Blocked, review, and accepted work

- `blocked` retains WIP because responsibility, context, and rollback remain attached to the current Issue.
- `review` retains WIP because evidence, findings, and repair are part of the active packet.
- `accepted` retains WIP until formal closure and post-merge or post-decision read-back completes.
- A successor may remain planned or authorized but cannot become active while any of these states consumes WIP.

## Incident exclusions

Issues `#10` through `#14` are permanent closed/not-planned audit artifacts.

They:

- are not controlled Work Packet, Gate Review, or Repair Issues;
- never consume WIP;
- must remain unassigned and without labels or Milestones;
- cannot be reused to satisfy a missing Issue identity;
- cannot be reopened, reclassified, or repurposed.

## Evidence and integrity requirements

A WIP transition is valid only when evidence confirms:

- exactly one state label on each affected controlled Issue;
- exactly one global WIP-consuming Issue after activation;
- predecessor and Gate requirements are satisfied;
- future packets remain inactive;
- no unauthorized branch, Pull Request, Issue, comment, label, Milestone, Project, Ruleset, or repository file changed;
- CI, build, test, lint, migration, runtime, and security status are represented truthfully.

Any WIP result greater than or less than one during an active program phase is a Hard Cap and requires immediate stop and a bounded Product Owner decision.
