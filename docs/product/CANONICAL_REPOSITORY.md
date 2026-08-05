# AXIOM Canonical Repository and Access Control

## Status

This document is proposed under `WP-000 — Establish Canonical Repository and Access Controls`.

The repository is the sole canonical repository **candidate** until WP-000 passes, this document is merged through Pull Request, one valid independent approval exists, all review conversations are resolved, and the default-branch ruleset remains independently verified.

Product development, WP-001, and direct changes to `main` remain prohibited until the WP-000 Gate decision is PASS.

## Canonical Repository Candidate

| Field | Value |
|---|---|
| Repository | `sajadalireza/AXIOM` |
| Repository URL | `https://github.com/sajadalireza/AXIOM` |
| Repository owner | `sajadalireza` |
| Visibility | `public` |
| Visibility decision | `PUBLIC-FOR-FREE-BRANCH-PROTECTION` |
| Default branch | `main` |
| Baseline commit | `7a8491f61eda4ba57016a452e057a5e7d84a5c5a` |
| Baseline tree | `62a7ab6324d76080041c8446dfc1bc08438555f2` |
| Baseline parent | `0acb161da6abdaa0c2592bac4368617135a50832` |
| Immutable baseline tag | `axiom-baseline-wp000r-7a8491f` |
| Historical branch | `ci/build-protocol` |
| Historical tags | `v4`, `v5`, `v6` |
| Governance issue | `https://github.com/sajadalireza/AXIOM/issues/1` |
| Documentation PR | `https://github.com/sajadalireza/AXIOM/pull/2` |

## Visibility Decision: PUBLIC-FOR-FREE-BRANCH-PROTECTION

The Product Owner changed repository visibility from `private` to `public` because the available GitHub Free configuration did not enforce repository rulesets while the repository was private, and a paid GitHub Pro, Team, or Enterprise plan was not currently available.

Public visibility was selected so that the WP-000 default-branch governance controls could be technically enforced without purchasing a paid plan.

This decision is reversible when a suitable paid plan becomes available and equivalent or stronger private-repository enforcement has been independently verified. Changing the repository back to `private` without paid enforcement would reactivate the default-branch governance blocker and must not occur outside an explicit Product Owner Gate.

Public visibility increases source-code exposure. It does not authorize disclosure of credentials, secrets, sensitive user data, private operational context, or proprietary external artifacts. Secret scanning and least-privilege controls remain mandatory.

## Source-of-Truth Order

Repository and product decisions must use the following order:

1. The live repository at the current canonical branch and commit.
2. The AXIOM Product Constitution.
3. The AXIOM Execution Workflow.
4. The AXIOM Upgrade Master Plan.
5. The latest valid Checkpoint Capsule.
6. Current primary-source documentation only when a versioned external fact is required.

A conflict between canonical sources must stop execution and be recorded. It must not be silently resolved.

## Required Access Model

- Repository ownership remains with `sajadalireza` unless a Product Owner Gate explicitly changes ownership.
- Administrative access is limited to the repository owner and explicitly authorized administrators.
- Contributors use the minimum repository permission required for their assigned packet.
- Changes to `main` are accepted only through reviewed Pull Requests.
- Direct pushes to `main` are prohibited and technically restricted by the active repository ruleset.
- Force pushes, non-fast-forward updates, and deletion of `main` are prohibited.
- The current ruleset has no bypass actors; the current owner cannot bypass it.
- Destructive repository operations require a dry run, backup or recovery reference, explicit authorization, and a tested rollback procedure.
- Credentials, tokens, private keys, sensitive user data, and private operational context must not be committed, copied into issues, or exposed in logs or analytics.
- WIP is limited to one active Work Packet.

## PR-Only Change Policy

Every change to `main` must satisfy all of the following:

1. Use a non-default branch scoped to the active Work Packet.
2. Reference the active GitHub issue and Acceptance Contract.
3. Separate behavior changes from refactors whenever practical.
4. Include evidence required by the active Acceptance Contract.
5. Receive at least one valid approval from an eligible independent reviewer.
6. Dismiss stale approvals when new commits are pushed.
7. Resolve all review conversations before merge.
8. Pass every required status check that is actually configured and operational.
9. Never report a build, test, lint, migration, runtime check, security scan, or CI status as passing without direct tool evidence.
10. Merge only after the packet score is at least `9.5`, all Must Acceptance items pass, required evidence is present, and no Hard Cap remains active.

The repository currently has no stable registered CI status context. No required status check is configured, and no CI PASS is claimed. Required CI checks may be added only after an actual workflow produces independently observed, stable check names.

## Verified Default-Branch Ruleset

| Field | Verified value |
|---|---|
| Ruleset name | `Protect main — WP-000` |
| Ruleset ID | `20441513` |
| Target | Default branch (`~DEFAULT_BRANCH`, currently `main`) |
| Enforcement | `active` |
| Bypass actors | none |
| Current owner bypass | `never` |

Verified active rules:

1. Branch deletion is restricted.
2. Non-fast-forward updates and force pushes are blocked.
3. A Pull Request is required before merging.
4. Required approving review count is `1`.
5. Stale reviews are dismissed when new commits are pushed.
6. Review-thread resolution is required.
7. No required status check is configured because no stable CI check context currently exists.
8. Allowed merge methods are `merge`, `squash`, and `rebase`.

The ruleset must remain active through the final WP-000 merge and after the merge. Any change to its target, enforcement, bypass actors, review count, stale-review behavior, conversation-resolution requirement, deletion protection, or non-fast-forward protection requires a new evidence review.

## WP-000R Recovery Record

WP-000R restored the repository from committed Git history only. The dirty physical ZIP worktree was not imported.

Verified recovered identities:

- `refs/heads/main` → `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`
- `refs/heads/ci/build-protocol` → `28526de28d18689b624aadf3a29f38432f6debf1`
- `refs/tags/v4` → `51a9064c5996cb03797071c738f6e7e53bfb8aa1`
- `refs/tags/v5` → `d4dc15ad88564fcf3a80609ed1e3b3ff033e943f`
- `refs/tags/v6` → `5e78051af24b2f945d58cede43d9824d404dfc21`
- `refs/tags/axiom-baseline-wp000r-7a8491f` → `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`

Recovery references:

- Baseline tag: `https://github.com/sajadalireza/AXIOM/tree/axiom-baseline-wp000r-7a8491f`
- Baseline commit: `https://github.com/sajadalireza/AXIOM/commit/7a8491f61eda4ba57016a452e057a5e7d84a5c5a`
- Governance issue: `https://github.com/sajadalireza/AXIOM/issues/1`
- Evidence index: `docs/governance/WP-000R_RECOVERY_EVIDENCE.md`
- Controlled evidence package: `WP-000R-post-recovery-evidence.zip`
- Final decision record package: `WP-000R-post-review-final.zip`

## WP-000R Bundle-Hash Deviation Exception

The historical record retains:

- Original authorized bundle SHA-256: `7a541f50e29994f4cbb4b2af41df82ba83d43360a577c11591fac56f8fb76793`
- Execution bundle SHA-256: `2432841959cbc2c081778c77a29f0659a6bf28f2fc747aa23c2162ca0c5ccf84`
- Exact reachable-object manifest SHA-256: `c47a71106cb00d0fdb933e3d7025ceba952d1593aea59a39793b97578cf56a74`

The bundle was regenerated during recovery coordination, producing different bundle-container bytes while preserving the exact reachable Git objects. The Product Owner approved an object-equivalent source-identity exception for WP-000R only. This exception does not apply to future recovery packets.

WP-000R final result:

`PASS — OBJECT-EQUIVALENT RECOVERY VERIFIED`

- Packet completion score: `9.6 / 10`
- Process-execution quality note: `8.8 / 10`

## Reviewed Gitleaks Finding

| Field | Value |
|---|---|
| Rule | `generic-api-key` |
| File | `docs/BUILD_PROTOCOL.md` |
| Line | `166` |
| Commit | `28526de28d18689b624aadf3a29f38432f6debf1` |
| Fingerprint | `28526de28d18689b624aadf3a29f38432f6debf1:docs/BUILD_PROTOCOL.md:generic-api-key:166` |
| Classification | Non-functional, truncated documentation example |

The exception is limited to this exact fingerprint. General secret detection remains enabled and must not be bypassed.

## Recovery and Rollback

The immutable baseline tag is the primary rollback anchor for the authorized recovered baseline.

Before any destructive rollback:

1. Stop all writes and freeze active Work Packets.
2. Capture current remote refs and a new recovery bundle.
3. Verify the backup bundle and record its SHA-256.
4. Produce a dry-run comparison between current `main` and the immutable baseline tag.
5. Obtain explicit Product Owner authorization.
6. Use the smallest non-destructive recovery method available.
7. Do not force-update `main` unless a destructive recovery packet explicitly authorizes it.

Visibility rollback is separate from Git-history rollback. Returning to private visibility requires proof that equivalent ruleset enforcement remains available under a suitable paid plan. Without that proof, the repository must remain public or WP-000 governance becomes blocked again.

## Migration, Analytics, Privacy, and Product Impact

- Migration impact: none.
- Database or schema impact: none.
- Analytics impact: none.
- Application privacy behavior impact: none.
- Repository exposure impact: source code and committed documentation are publicly readable.
- Product code impact: none.
- UI or accessibility impact: none.
- Runtime behavior impact: none.
- Dependency impact: none.

## WP-000 Completion Gate

This repository becomes the sole canonical AXIOM repository only when:

- this document is approved and merged through Pull Request;
- issue `#1` records all required evidence;
- ruleset `20441513` remains active and independently verified;
- the PR-only policy is technically enforced;
- one valid independent approval exists for the current PR head;
- all review conversations are resolved;
- CI status absence is recorded truthfully and no nonexistent checks are claimed;
- four independent current-head reviews pass;
- the weighted WP-000 score is at least `9.5`;
- every Must Acceptance item passes; and
- no Hard Cap is active.

Until that Gate passes, `sajadalireza/AXIOM` remains the sole canonical repository candidate, not the fully declared canonical repository.
