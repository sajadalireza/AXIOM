# WP-000R Recovery Evidence Package Index

## Packet Result

`PASS — OBJECT-EQUIVALENT RECOVERY VERIFIED`

- Packet completion score: `9.6 / 10`
- Process-execution quality note: `8.8 / 10`
- Active Hard Caps: none

## Controlled Package Artifacts

| Artifact | SHA-256 | Storage model |
|---|---|---|
| `WP-000R-post-recovery-evidence.zip` | `485e70dc4ea8f9e11fb144820ec56145cc0cd7b67e5a47fdbf61466e1b792c35` | External controlled packet artifact |
| `WP-000R-post-review-final.zip` | `db2746e57647a47aedabd8a4e64561c271e36ff3f2e665ab4f06e328759df6c2` | External controlled final-decision artifact |

The connected GitHub integration cannot upload binary issue attachments. This repository-native index provides durable evidence linkage, identifiers, checksums, and verification targets.

## Recovery Source Identity

- Original authorized bundle SHA-256: `7a541f50e29994f4cbb4b2af41df82ba83d43360a577c11591fac56f8fb76793`
- Execution bundle SHA-256: `2432841959cbc2c081778c77a29f0659a6bf28f2fc747aa23c2162ca0c5ccf84`
- Exact reachable-object manifest SHA-256: `c47a71106cb00d0fdb933e3d7025ceba952d1593aea59a39793b97578cf56a74`

The bundle was regenerated during recovery coordination. The Product Owner approved an object-equivalent source-identity exception for WP-000R only. This exception does not apply to future recovery packets.

## Verified Remote Baseline

| Identity | Value |
|---|---|
| Repository | `sajadalireza/AXIOM` |
| Visibility during recovery | `private` |
| Current visibility | `public` |
| Visibility decision | `PUBLIC-FOR-FREE-BRANCH-PROTECTION` |
| Default branch | `main` |
| Commit | `7a8491f61eda4ba57016a452e057a5e7d84a5c5a` |
| Tree | `62a7ab6324d76080041c8446dfc1bc08438555f2` |
| Parent | `0acb161da6abdaa0c2592bac4368617135a50832` |
| Immutable baseline tag | `axiom-baseline-wp000r-7a8491f` |

The visibility change occurred after WP-000R recovery and did not alter recovered Git objects, commit identities, refs, tags, or the baseline tree. Public visibility was selected because paid private-repository ruleset enforcement was unavailable. Returning to private visibility without equivalent paid enforcement would reactivate the governance blocker.

## Verified Refs

- `refs/heads/main` → `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`
- `refs/heads/ci/build-protocol` → `28526de28d18689b624aadf3a29f38432f6debf1`
- `refs/tags/v4` → `51a9064c5996cb03797071c738f6e7e53bfb8aa1`
- `refs/tags/v5` → `d4dc15ad88564fcf3a80609ed1e3b3ff033e943f`
- `refs/tags/v6` → `5e78051af24b2f945d58cede43d9824d404dfc21`
- `refs/tags/axiom-baseline-wp000r-7a8491f` → `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`

## WP-000 Initial Governance Merge

PR `#2` merged the initial governance documents.

- Approved source head: `dd17cb6b2a6d10b5f909cc08510c4410948cc464`
- Merge commit: `e097bc52afc189ff8647f0737f480f1265d2de79`
- Merge parents:
  - `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`
  - `dd17cb6b2a6d10b5f909cc08510c4410948cc464`
- Merge signature: verified by GitHub

The merged documents reflected the earlier governance state in which ruleset `20441513` required one approval. That history remains part of the evidence record.

## Historical WP-000 Ruleset State

Before the solo-maintainer exception was applied:

- Ruleset ID: `20441513`
- Enforcement: `active`
- Required approving review count: `1`
- Pull Request required: yes
- Dismiss stale reviews: enabled
- Review-thread resolution: required
- Branch deletion: restricted
- Non-fast-forward and force pushes: blocked
- Bypass actors: none
- Current owner bypass: `never`
- Required status checks: none configured

This historical state is not concealed or rewritten.

## SOLO-MAINTAINER-GOVERNANCE-EXCEPTION

The Product Owner approved a bounded exception because `sajadalireza` is the only maintainer with Write authority and cannot approve their own Pull Request.

Independent approval is classified:

`NOT APPLICABLE — SOLO MAINTAINER EXCEPTION`

This exception is not an independent external review. It applies only while `sajadalireza` is the sole maintainer with Write authority. It must be removed and required approvals restored to at least `1` when a trusted second maintainer receives Write access or the repository moves to team governance.

Four structured current-head governance review lenses, exact diff and head verification, applied-ruleset verification, and unresolved-thread verification remain mandatory.

## Current WP-000 Ruleset Evidence

| Field | Current verified value |
|---|---|
| Ruleset name | `Protect main — WP-000` |
| Ruleset ID | `20441513` |
| Target | Default branch (`main`) |
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

No CI PASS is claimed. No required status check is configured because no stable CI check context has been independently observed.

## Security Review Record

- Scanner: Gitleaks full-history scan
- Finding count: one reviewed finding
- Rule: `generic-api-key`
- File: `docs/BUILD_PROTOCOL.md`
- Line: `166`
- Commit: `28526de28d18689b624aadf3a29f38432f6debf1`
- Fingerprint: `28526de28d18689b624aadf3a29f38432f6debf1:docs/BUILD_PROTOCOL.md:generic-api-key:166`
- Classification: non-functional, truncated documentation example
- Acceptance scope: exact fingerprint only
- General secret detection: remains enabled

Public visibility increases the importance of continued secret scanning. The exact false-positive exception must not be broadened.

## Recovery Invariants Verified

- Dirty ZIP worktree content was not imported.
- Original commit identities and parent relationships were preserved.
- Original tags and historical branch were preserved.
- No synthetic root or reconstructed commits were created.
- No history rewrite or force push was introduced.
- Remote commit, tree, parent graph, and reachable objects were independently verified.
- Immutable rollback tag exists.
- The later visibility and governance changes did not modify the recovered object graph.

## Repository Links

- Repository: https://github.com/sajadalireza/AXIOM
- Ruleset: https://github.com/sajadalireza/AXIOM/rules/20441513
- Baseline tag: https://github.com/sajadalireza/AXIOM/tree/axiom-baseline-wp000r-7a8491f
- Baseline commit: https://github.com/sajadalireza/AXIOM/commit/7a8491f61eda4ba57016a452e057a5e7d84a5c5a
- WP-000 issue: https://github.com/sajadalireza/AXIOM/issues/1
- WP-000 initial governance PR: https://github.com/sajadalireza/AXIOM/pull/2

## Retention Rule

Both bundle hashes, both controlled package hashes, the reachable-object manifest hash, the WP-000R Product Owner exception, the visibility decision, the historical one-approval state, the solo-maintainer exception, the current ruleset state, and the reviewed Gitleaks fingerprint must remain permanently in the governance evidence record.
