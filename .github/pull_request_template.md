# Controlled Pull Request

## Work Packet identity

- Work Packet ID:
- Tracking Issue:
- Owning Gate:
- Accountable owner:

## Exact repository state

- Authorized baseline branch and SHA:
- Current head branch and SHA:
- Merge base SHA:
- Target branch:

## Objective

<!-- State the smallest outcome authorized by the active Acceptance Contract. -->

## Scope

<!-- Describe the exact behavioral, documentation, or governance slice. -->

## Non-goals

<!-- State what this Pull Request deliberately does not change. -->

## Exact changed-file boundary

| Path | Added / modified / deleted | Authorized by |
|---|---|---|
|  |  |  |

- Expected changed-file count:
- Observed changed-file count:
- Unauthorized paths observed:

## Dependency and WIP impact

- Predecessor:
- Successor:
- Current WIP-consuming Issue:
- WIP query result:
- Future Work Packet activation performed: `no`

## Tests and verification actually executed

| Command or check | Environment | Result | Evidence link or log |
|---|---|---|---|
|  |  |  |  |

Do not list a check as passed unless its actual output was observed.

## CI, build, lint, migration, runtime, and status state

- CI workflows observed:
- Status contexts observed:
- Build:
- Tests:
- Lint:
- Migration:
- Runtime:
- Security scan:
- Any item not run:

## Evidence package

- Issue evidence:
- Commit and diff evidence:
- Runtime or user evidence:
- Logs and artifacts:
- Checkpoint Capsule:

## Impact assessment

### Migration impact

### Analytics impact

### Privacy impact

### Security and secret handling

- Secret or credential values included: `no`
- Sensitive personal text included: `no`
- Redaction or fingerprint method used:

## Rollback

- Before merge:
- After merge:
- Data or schema recovery:
- Rollback evidence:

## Four exact-head reviews

| Review | Result | Evidence and findings |
|---|---|---|
| A — Correctness and canonical-source fidelity |  |  |
| B — Architecture and security |  |  |
| C — Product, UX, and lean scope |  |  |
| D — Evidence and operability |  |  |

## Score and Hard Caps

- Weighted score:
- Rubric:
- Active Hard Caps:
- Must Acceptance result:

## Threads and unresolved findings

- Unresolved review threads:
- S0 findings:
- S1 findings:
- Other unresolved findings:
- Required repair packets:

## Mandatory attestations

- [ ] I have not claimed `DONE`, `PASS`, or acceptance without direct evidence, satisfied Must Acceptance items, a weighted score of at least 9.5, and no active Hard Cap.
- [ ] CI, build, test, lint, migration, runtime, status, and security results above are truthful; unexecuted checks are explicitly marked not run.
- [ ] This Pull Request was created from a non-default Work Packet branch; no direct mutation of `main` occurred.
- [ ] The diff contains only the authorized changed-file boundary and no unauthorized file.
- [ ] No secret, token, credential, private goal, reflection text, health or financial detail, or other sensitive personal data is disclosed.
- [ ] Rollback and recovery paths are documented and proportionate to the change.
- [ ] The active WIP query still returns exactly one controlled Issue.
- [ ] No future Work Packet was authorized or activated by this Pull Request.
- [ ] All required review conversations must be resolved before merge.
