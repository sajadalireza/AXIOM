# AXIOM Canonical Repository and Access Control

## Status

This document is proposed under `WP-000 — Establish Canonical Repository and Access Controls`.

The repository is the sole canonical repository **candidate** until WP-000 passes, this document is merged through Pull Request, and the required default-branch controls are independently verified.

Product development, WP-001, and direct changes to `main` remain prohibited until the WP-000 Gate decision is PASS.

## Canonical Repository Candidate

| Field | Value |
|---|---|
| Repository | `sajadalireza/AXIOM` |
| Repository URL | `https://github.com/sajadalireza/AXIOM` |
| Repository owner | `sajadalireza` |
| Visibility | `private` |
| Default branch | `main` |
| Baseline commit | `7a8491f61eda4ba57016a452e057a5e7d84a5c5a` |
| Baseline tree | `62a7ab6324d76080041c8446dfc1bc08438555f2` |
| Baseline parent | `0acb161da6abdaa0c2592bac4368617135a50832` |
| Immutable baseline tag | `axiom-baseline-wp000r-7a8491f` |
| Historical branch | `ci/build-protocol` |
| Historical tags | `v4`, `v5`, `v6` |
| Governance issue | `https://github.com/sajadalireza/AXIOM/issues/1` |

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
- Direct pushes to `main` are prohibited by policy and must be technically restricted through branch protection or a repository ruleset.
- Force pushes and deletion of `main` are prohibited.
- Destructive repository operations require a dry run, backup or recovery reference, explicit authorization, and a tested rollback procedure.
- Credentials, tokens, private keys, sensitive user data, and private operational context must not be committed, copied into issues, or exposed in logs or analytics.
- WIP is limited to one active Work Packet.

## PR-Only Change Policy

Every change to `main` must satisfy all of the following:

1. Use a non-default branch scoped to the active Work Packet.
2. Reference the active GitHub issue and Acceptance Contract.
3. Separate behavior changes from refactors whenever practical.
4. Include evidence required by the active Acceptance Contract.
5. Receive at least one approval from an eligible reviewer.
6. Dismiss stale approvals when new commits are pushed.
7. Resolve all review conversations before merge.
8. Pass every required status check that is actually configured and operational.
9. Never report a build, test, lint, migration, runtime check, security scan, or CI status as passing without direct tool evidence.
10. Merge only after the packet score is at least `9.5`, all Must Acceptance items pass, required evidence is present, and no Hard Cap remains active.

The repository currently has no registered CI status contexts. Required CI checks must not be configured or claimed until a real workflow produces stable check names and successful evidence.

## Current Default-Branch State

At the time this document was proposed:

- `main` resolves to `7a8491f61eda4ba57016a452e057a5e7d84a5c5a`.
- GitHub reports `protected: false`.
- Branch-protection enforcement is disabled.
- Required status-check contexts are empty.
- No CI status is registered for the authorized baseline commit.

This state is not sufficient for WP-000 completion.

## Manual Branch-Protection Configuration Checklist

The connected GitHub tool cannot configure branch protection. An administrator must configure `main` through GitHub repository settings and capture independent evidence of the resulting state.

Repository path:

`Settings` → `Branches` or `Rules` → create or edit a protection rule/ruleset targeting `main`

Required configuration:

- [ ] Require a Pull Request before merging.
- [ ] Require at least one approving review.
- [ ] Dismiss stale Pull Request approvals when new commits are pushed.
- [ ] Require conversation resolution before merging.
- [ ] Block force pushes.
- [ ] Block branch deletion.
- [ ] Restrict direct pushes or bypass access to explicitly authorized administrators only.
- [ ] Apply the rule to administrators where supported, unless a documented emergency-break-glass policy is approved.
- [ ] Do not add required status checks yet; no actual CI check context has been independently observed.
- [ ] After a real CI workflow exists, add only the exact stable check names observed in successful GitHub evidence.
- [ ] Re-fetch the `main` branch and protection/ruleset state after configuration.
- [ ] Attach screenshots or API evidence to issue `#1`.

Verification must show that `main` is protected and that PR review requirements, force-push prevention, branch-deletion prevention, and direct-push restrictions are active.

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
- Governance issue and evidence index: `https://github.com/sajadalireza/AXIOM/issues/1`
- Controlled evidence package: `WP-000R-post-recovery-evidence.zip`
- Final decision record package: `WP-000R-post-review-final.zip`

The controlled packages are external packet artifacts. Their filenames, checksums, and evidence tables must remain in the WP-000R evidence record.

## WP-000R Bundle-Hash Deviation Exception

The historical record must retain both hashes:

- Original authorized bundle SHA-256: `7a541f50e29994f4cbb4b2af41df82ba83d43360a577c11591fac56f8fb76793`
- Execution bundle SHA-256: `2432841959cbc2c081778c77a29f0659a6bf28f2fc747aa23c2162ca0c5ccf84`
- Exact reachable-object manifest SHA-256: `c47a71106cb00d0fdb933e3d7025ceba952d1593aea59a39793b97578cf56a74`

The bundle was regenerated during recovery coordination, producing different bundle-container bytes while preserving the exact reachable Git objects. The Product Owner approved an object-equivalent source-identity exception for WP-000R only.

This exception does not remove or rewrite the deviation. It does not apply to any future recovery packet. Future recovery packets retain strict byte-identical bundle-hash enforcement unless a new packet-specific Product Owner decision explicitly states otherwise.

WP-000R final result:

`PASS — OBJECT-EQUIVALENT RECOVERY VERIFIED`

- Packet completion score: `9.6 / 10`
- Process-execution quality note: `8.8 / 10`

## Reviewed Gitleaks Finding

The full-history Gitleaks scan reported one reviewed finding:

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

Read-only verification:

```bash
git ls-remote --heads --tags https://github.com/sajadalireza/AXIOM.git
git show --no-patch --format=fuller axiom-baseline-wp000r-7a8491f
git rev-parse axiom-baseline-wp000r-7a8491f^{tree}
```

Before any destructive rollback:

1. Stop all writes and freeze active Work Packets.
2. Capture current remote refs and a new recovery bundle.
3. Verify the backup bundle and record its SHA-256.
4. Produce a dry-run comparison between current `main` and the immutable baseline tag.
5. Obtain explicit Product Owner authorization.
6. Use the smallest non-destructive recovery method available.
7. Do not force-update `main` unless a destructive recovery packet explicitly authorizes it.

The original verified recovery bundle and WP-000R evidence packages remain controlled recovery artifacts.

## Migration, Analytics, Privacy, and Product Impact

- Migration impact: none.
- Database or schema impact: none.
- Analytics impact: none.
- Privacy behavior impact: none.
- Product code impact: none.
- UI or accessibility impact: none.
- Runtime behavior impact: none.
- Dependency impact: none.

## WP-000 Completion Gate

This repository becomes the sole canonical AXIOM repository only when:

- this document is approved and merged through Pull Request;
- issue `#1` records all required evidence;
- `main` is independently verified as protected;
- the PR-only policy is technically enforced;
- required review controls are active;
- CI status absence is recorded truthfully and no nonexistent checks are claimed;
- four independent reviews pass;
- the weighted WP-000 score is at least `9.5`;
- every Must Acceptance item passes; and
- no Hard Cap is active.

Until that Gate passes, `sajadalireza/AXIOM` remains the sole canonical repository candidate, not the fully declared canonical repository.
