# AXIOM Canonical Repository and Access Control

## Status

This document is the governance record for `WP-000 — Establish Canonical Repository and Access Controls`.

The repository becomes the sole canonical AXIOM repository only after the WP-000 alignment repair is merged through the protected Pull Request flow, post-merge evidence is recorded in issue `#1`, and WP-000 is formally closed.

Product development and WP-001 remain prohibited until that closure decision is recorded.

## Canonical Repository

| Field | Value |
|---|---|
| Repository | `sajadalireza/AXIOM` |
| Repository URL | `https://github.com/sajadalireza/AXIOM` |
| Repository owner | `sajadalireza` |
| Visibility | `public` |
| Visibility decision | `PUBLIC-FOR-FREE-BRANCH-PROTECTION` |
| Default branch | `main` |
| Recovery baseline commit | `7a8491f61eda4ba57016a452e057a5e7d84a5c5a` |
| Recovery baseline tree | `62a7ab6324d76080041c8446dfc1bc08438555f2` |
| Recovery baseline parent | `0acb161da6abdaa0c2592bac4368617135a50832` |
| Immutable baseline tag | `axiom-baseline-wp000r-7a8491f` |
| Historical branch | `ci/build-protocol` |
| Historical tags | `v4`, `v5`, `v6` |
| Governance issue | `https://github.com/sajadalireza/AXIOM/issues/1` |
| Initial governance PR | `https://github.com/sajadalireza/AXIOM/pull/2` |

## Initial Governance Merge Record

PR `#2` successfully merged the first canonical repository governance documents.

- Approved source head: `dd17cb6b2a6d10b5f909cc08510c4410948cc464`
- Merge commit: `e097bc52afc189ff8647f0737f480f1265d2de79`
- Merge parents:
  - `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`
  - `dd17cb6b2a6d10b5f909cc08510c4410948cc464`
- Merge signature: verified by GitHub

The merged documents retained the earlier one-approval requirement. That historical state is preserved below and superseded by the explicit Product Owner exception.

## Visibility Decision: PUBLIC-FOR-FREE-BRANCH-PROTECTION

The Product Owner changed repository visibility from `private` to `public` because the available GitHub Free configuration did not enforce repository rulesets while the repository was private, and a paid GitHub Pro, Team, or Enterprise plan was not available.

Public visibility was selected so that WP-000 default-branch controls could be technically enforced. The decision is reversible only after equivalent or stronger private-repository enforcement is available and independently verified. Returning to `private` without that enforcement reactivates the governance blocker.

Public visibility increases source-code exposure. It does not authorize disclosure of credentials, secrets, sensitive user data, private operational context, or proprietary external artifacts.

## Source-of-Truth Order

1. The live repository at the current canonical branch and commit.
2. The AXIOM Product Constitution.
3. The AXIOM Execution Workflow.
4. The AXIOM Upgrade Master Plan.
5. The latest valid Checkpoint Capsule.
6. Current primary-source documentation when a versioned external fact is required.

A conflict between canonical sources must stop execution and be recorded.

## Required Access Model

- Repository ownership remains with `sajadalireza` unless a Product Owner Gate changes it.
- Administrative access is limited to explicitly authorized maintainers.
- Contributors use the minimum permission required for their active Work Packet.
- Changes to `main` enter only through Pull Requests.
- Direct pushes, force pushes, non-fast-forward updates, and deletion of `main` are prohibited by the active ruleset.
- The ruleset has no bypass actors and the owner cannot bypass it.
- Destructive repository operations require a dry run, backup or recovery reference, explicit authorization, and a rollback procedure.
- Credentials, tokens, private keys, sensitive user data, and private operational context must not be committed or exposed.
- WIP is limited to one active Work Packet.

## Historical Approval Policy

The initial WP-000 policy required at least one valid approval from an eligible independent reviewer. Ruleset `20441513` originally enforced required approving review count `1`.

This history is retained and must not be rewritten as though it never existed.

## SOLO-MAINTAINER-GOVERNANCE-EXCEPTION

The Product Owner approved a bounded exception because `sajadalireza` is currently the only maintainer with Write authority and cannot provide a valid approval for their own Pull Request.

Independent approval classification while this condition remains true:

`NOT APPLICABLE — SOLO MAINTAINER EXCEPTION`

The exception:

- is not an independent external review;
- applies only while `sajadalireza` is the sole maintainer with Write authority;
- requires four structured current-head governance review lenses;
- requires the PR diff, head SHA, changed-file boundary, applied ruleset state, and unresolved-thread state to be re-read before merge;
- must be removed and required approvals restored to at least `1` when a trusted second maintainer receives Write access or the repository moves to team governance.

## PR-Only Change Policy

Every change to `main` must:

1. Use a non-default branch scoped to the active Work Packet.
2. Reference the active issue and Acceptance Contract.
3. Separate behavior changes from refactors whenever practical.
4. Include required evidence.
5. Use the approval policy applicable to the current governance model:
   - solo maintainer: `NOT APPLICABLE — SOLO MAINTAINER EXCEPTION`, with four structured current-head reviews;
   - two or more trusted maintainers or team governance: at least one valid independent approval.
6. Dismiss stale reviews when new commits are pushed.
7. Resolve all review conversations before merge.
8. Pass every required status check that is actually configured and operational.
9. Never claim a build, test, lint, migration, runtime check, security scan, or CI status passed without direct evidence.
10. Merge only after the packet score is at least `9.5`, all Must Acceptance items pass, required evidence is present, and no Hard Cap is active.

No stable CI status context currently exists. No required status check is configured and no CI PASS is claimed.

## Verified Default-Branch Ruleset

| Field | Current verified value |
|---|---|
| Ruleset name | `Protect main — WP-000` |
| Ruleset ID | `20441513` |
| Target | Default branch (`~DEFAULT_BRANCH`, currently `main`) |
| Enforcement | `active` |
| Required approving review count | `0` |
| Pull Request required | yes |
| Dismiss stale reviews | enabled |
| Require review-thread resolution | enabled |
| Branch deletion | restricted |
| Non-fast-forward / force push | blocked |
| Required status checks | none configured |
| Bypass actors | none |
| Current owner bypass | `never` |
| Allowed merge methods | `merge`, `squash`, `rebase` |

Any change to these controls requires a new evidence review. Approval count must be restored to at least `1` when the solo-maintainer condition ends.

## WP-000R Recovery Record

WP-000R restored committed Git history only. The dirty physical ZIP worktree was not imported.

Verified recovered identities:

- `refs/heads/main` → `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`
- `refs/heads/ci/build-protocol` → `28526de28d18689b624aadf3a29f38432f6debf1`
- `refs/tags/v4` → `51a9064c5996cb03797071c738f6e7e53bfb8aa1`
- `refs/tags/v5` → `d4dc15ad88564fcf3a80609ed1e3b3ff033e943f`
- `refs/tags/v6` → `5e78051af24b2f945d58cede43d9824d404dfc21`
- `refs/tags/axiom-baseline-wp000r-7a8491f` → `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`

## WP-000R Bundle-Hash Deviation Exception

- Original authorized bundle SHA-256: `7a541f50e29994f4cbb4b2af41df82ba83d43360a577c11591fac56f8fb76793`
- Execution bundle SHA-256: `2432841959cbc2c081778c77a29f0659a6bf28f2fc747aa23c2162ca0c5ccf84`
- Exact reachable-object manifest SHA-256: `c47a71106cb00d0fdb933e3d7025ceba952d1593aea59a39793b97578cf56a74`

The Product Owner accepted exact object-equivalent source identity for WP-000R only. Future recovery packets retain strict byte-identical bundle-hash enforcement.

WP-000R result: `PASS — OBJECT-EQUIVALENT RECOVERY VERIFIED`

## Reviewed Gitleaks Finding

- Rule: `generic-api-key`
- File: `docs/BUILD_PROTOCOL.md`
- Line: `166`
- Commit: `28526de28d18689b624aadf3a29f38432f6debf1`
- Fingerprint: `28526de28d18689b624aadf3a29f38432f6debf1:docs/BUILD_PROTOCOL.md:generic-api-key:166`
- Classification: non-functional, truncated documentation example

The exception is limited to this exact fingerprint. General secret detection remains enabled.

## Recovery and Rollback

The immutable baseline tag is the primary recovery anchor. Before destructive rollback, freeze writes, capture current refs and a verified bundle, produce a dry-run comparison, obtain Product Owner authorization, and use the smallest non-destructive recovery method available.

Visibility rollback is separate from Git-history rollback. Returning to private requires proof that equivalent ruleset enforcement remains available.

## Impact

- Migration: none.
- Database or schema: none.
- Analytics: none.
- Application privacy behavior: none.
- Product code: none.
- UI or accessibility: none.
- Runtime behavior: none.
- Dependencies: none.
- Repository exposure: source code and committed documentation are publicly readable.

## WP-000 Completion Gate

WP-000 passes only when:

- the current governance documents are merged through a protected Pull Request;
- issue `#1` records the superseding solo-maintainer decision and final evidence;
- ruleset `20441513` remains active and applied to `main`;
- Pull Requests, deletion restriction, non-fast-forward protection, stale-review dismissal, conversation resolution, and no-bypass controls remain active;
- required approval is either satisfied under team governance or classified `NOT APPLICABLE — SOLO MAINTAINER EXCEPTION` while the bounded condition remains true;
- all review conversations are resolved;
- no nonexistent CI checks are claimed;
- four current-head governance reviews pass;
- the weighted score is at least `9.5`;
- every Must Acceptance item passes; and
- no Hard Cap is active.
