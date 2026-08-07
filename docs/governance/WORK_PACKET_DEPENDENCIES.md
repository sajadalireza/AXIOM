# AXIOM Work Packet Dependencies

## Authority and scope

This document records the canonical G0 through G2 Work Packet dependency graph established by WP-004. It is a governance dependency map, not an authorization schedule.

The live repository, the current active controlled Issue, accepted predecessor records, canonical control documents, accepted Product Owner decisions, and verified GitHub Issue state remain authoritative. Completion of a predecessor never automatically authorizes or activates its successor.

Current-state rows below are synchronized to the WP-005 G0 Gate Review baseline. Live GitHub state plus explicit Product Owner transition records remain authoritative for current operational state.

## Canonical acyclic chain

```text
WP-000 (#1)
→ WP-001 (#4)
→ WP-002 (#6)
→ WP-003 (#8)
→ WP-004 (#15)
→ WP-005 (#16)
→ WP-101 (#17)
→ WP-102 (#18)
→ WP-103 (#19)
→ WP-104 (#20)
→ WP-105 (#21)
→ WP-106 (#22)
→ WP-107 (#23)
→ WP-201 (#24)
→ WP-202 (#25)
→ WP-203 (#26)
→ WP-204 (#27)
→ WP-205 (#28)
→ WP-206 (#29)
→ WP-207 (#30)
→ WP-208 (#31)
→ WP-209 (#32)
```

## Packet-by-packet dependency record

| Work Packet | Gate | Issue | Predecessor | Successor | Current state | WIP | Activation rule |
|---|---|---:|---|---|---|---:|---|
| WP-000 | G0 | #1 | none | WP-001 (#4) | `closed` | 0 | Historical completed packet; no reactivation. |
| WP-001 | G0 | #4 | WP-000 (#1) | WP-002 (#6) | `closed` | 0 | Historical completed packet; no reactivation. |
| WP-002 | G0 | #6 | WP-001 (#4) | WP-003 (#8) | `closed` | 0 | Historical completed packet; no reactivation. |
| WP-003 | G0 | #8 | WP-002 (#6) | WP-004 (#15) | `closed` | 0 | Historical completed packet; no reactivation. |
| WP-004 | G0 | #15 | WP-003 (#8) | WP-005 (#16) | `closed` | 0 | Historical completed packet; successor activation required explicit Product Owner handoff authorization. |
| WP-005 | G0 | #16 | WP-004 (#15) | WP-101 (#17) | `active` | 1 | Active only under the recorded Product Owner `DECISION K-R` authorization; its Gate result does not activate WP-101. |
| WP-101 | G1 | #17 | WP-005 (#16) | WP-102 (#18) | `planned` | 0 | Successful WP-005 G0 Gate decision plus separate explicit Product Owner activation. |
| WP-102 | G1 | #18 | WP-101 (#17) | WP-103 (#19) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-103 | G1 | #19 | WP-102 (#18) | WP-104 (#20) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-104 | G1 | #20 | WP-103 (#19) | WP-105 (#21) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-105 | G1 | #21 | WP-104 (#20) | WP-106 (#22) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-106 | G1 | #22 | WP-105 (#21) | WP-107 (#23) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-107 | G1 | #23 | WP-106 (#22) | WP-201 (#24) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-201 | G2 | #24 | WP-107 (#23) | WP-202 (#25) | `planned` | 0 | Successful WP-107 G1 Gate decision plus explicit Product Owner activation. |
| WP-202 | G2 | #25 | WP-201 (#24) | WP-203 (#26) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-203 | G2 | #26 | WP-202 (#25) | WP-204 (#27) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-204 | G2 | #27 | WP-203 (#26) | WP-205 (#28) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-205 | G2 | #28 | WP-204 (#27) | WP-206 (#29) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-206 | G2 | #29 | WP-205 (#28) | WP-207 (#30) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-207 | G2 | #30 | WP-206 (#29) | WP-208 (#31) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-208 | G2 | #31 | WP-207 (#30) | WP-209 (#32) | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |
| WP-209 | G2 | #32 | WP-208 (#31) | none | `planned` | 0 | Accepted and closed predecessor plus explicit Product Owner activation. |

## Integrity properties

### Acyclicity

The graph is a single directed chain with:

- `22` canonical Work Packet identities;
- `21` directed predecessor-to-successor edges;
- one root, `WP-000`;
- one terminal node, `WP-209`;
- no back edge;
- no cycle.

### Orphan prevention

Every canonical Work Packet:

- maps to exactly one GitHub Issue;
- has an owning Gate;
- has a predecessor except the root;
- has a successor except the terminal node;
- has a current canonical state;
- has an activation rule.

Issues `#10` through `#14` are permanent incident artifacts and are not graph nodes.

### WIP and activation

- Planned Issues consume no WIP.
- Authorized Issues consume no WIP.
- Active, blocked, review, and accepted controlled Issues consume WIP until formal closure or explicit Product Owner suspension or supersession.
- Completion or closure of a predecessor does not activate its successor.
- Every activation requires an explicit Product Owner decision and a WIP preflight satisfying `WIP_POLICY.md`.
- During normal execution, steady-state global WIP must equal exactly `1`.
- When the integration cannot transactionally close a predecessor and activate its successor, an explicit Product Owner decision may authorize the bounded handoff defined in `WIP_POLICY.md`: WIP must never exceed `1`, one transient WIP=`0` state is permitted only between the authorized predecessor closure and immediate successor activation, and final steady-state WIP must return to exactly `1`.
- No branch or Pull Request may be created for an inactive future Work Packet.

### Gate review dependencies

- `WP-101` requires a successful `WP-005` G0 Gate decision and a separate explicit Product Owner activation decision.
- `WP-201` requires a successful `WP-107` G1 Gate decision and a separate explicit Product Owner activation decision.
- `WP-209` reviews G2 after the accepted completion of `WP-201` through `WP-208`; its creation does not authorize the review to begin.

### G3 through G7

G3 through G7 are intentionally not decomposed into Work Packet Issues. Their Milestones remain Gate-level placeholders until a later Product Owner decision authorizes bounded decomposition.

## Change control

A change to identity, Issue mapping, Gate ownership, predecessor, successor, state, WIP rule, or activation rule requires:

1. live repository and Issue read-back;
2. an explicit Product Owner decision;
3. a bounded Work Packet or repair packet;
4. exact evidence and rollback;
5. protected Pull Request review before `main` changes.
