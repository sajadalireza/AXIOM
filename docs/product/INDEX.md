# AXIOM Product Control Documents

## Status

This index is the authoritative entry point for AXIOM product and execution control documents.

- Repository: `sajadalireza/AXIOM`
- Owner: Product Owner (`sajadalireza`)
- Control-document baseline: `WP-002`
- Effective date: `2026-08-06`
- Change mechanism: GitHub Issue + protected Pull Request + Product Owner decision

## Canonical Control Documents

| Precedence | Document | Version | Date | Owner | Canonical source |
|---:|---|---|---|---|---|
| 0 | [Canonical Repository and Access Control](CANONICAL_REPOSITORY.md) | WP-000 final | 2026-08-05 | Product Owner | Live repository governance record established by WP-000 |
| 1 | [AXIOM Product Constitution](PRODUCT_CONSTITUTION.md) | 1.0 | 2026-08-05 | Product Owner | Section 3, `AXIOM Upgrade Master Plan v1.0`, supplied by the Product Owner |
| 2 | [AXIOM Canonical Product Vocabulary](CANONICAL_VOCABULARY.md) | 1.0 | 2026-08-06 | Product Owner | `DECISION A — CONSTITUTION-PRESERVING MISSION`, recorded in WP-002 Issue #6 |
| 3 | [AXIOM Execution Workflow](EXECUTION_WORKFLOW.md) | 1.0 | 2026-08-05 | Product Owner | `AXIOM_Execution_Workflow_v1.0.md`, supplied by the Product Owner |
| 4 | [AXIOM Upgrade Master Plan](UPGRADE_MASTER_PLAN.md) | 1.0 | 2026-08-05 | Product Owner | `AXIOM_Upgrade_Master_Plan_v1.0.md`, supplied by the Product Owner |

## Controlled Migration and Execution Plans

| Document | Version | Date | Owner | Authority boundary |
|---|---|---|---|---|
| [Vocabulary Deprecation Plan](VOCABULARY_DEPRECATION_PLAN.md) | 1.0 | 2026-08-06 | Product Owner | Inventories legacy and ambiguous terms and proposes reversible follow-up packets; it does not amend canonical definitions or authorize migrations by itself. |

## Precedence and Conflict Policy

1. `CANONICAL_REPOSITORY.md` governs repository identity, default-branch access, and change control.
2. `PRODUCT_CONSTITUTION.md` governs immutable product identity, value proposition, Core Loop, and non-negotiable product principles.
3. `CANONICAL_VOCABULARY.md` defines product-domain terms consistently with the Product Constitution and may not amend it.
4. `EXECUTION_WORKFLOW.md` governs how the upgrade program and Work Packets are executed.
5. `UPGRADE_MASTER_PLAN.md` governs the Gate roadmap, sequencing, metrics, and current upgrade plan.
6. Controlled migration plans apply canonical meaning to bounded changes but do not independently redefine the product or authorize implementation outside an accepted Work Packet.
7. The latest valid Checkpoint Capsule records execution state but does not silently amend these documents.

If two canonical documents conflict, execution must stop and the conflict must be resolved through an explicit Product Owner Decision Record. A lower-precedence document must not silently override a higher-precedence document.

No new product-facing term may silently duplicate, absorb, narrow, broaden, or redefine a term in `CANONICAL_VOCABULARY.md`.

## WP-002 Product Owner Decision Record

The Product Owner approved `DECISION A — CONSTITUTION-PRESERVING MISSION`.

- The Product Constitution remains unchanged.
- `Mission` retains the Constitution-defined executable-commitment meaning.
- The earlier Mission-as-purpose proposal is superseded for current AXIOM product-domain terminology.
- `Purpose` is the canonical durable reason, intent, or “why”.
- The historical proposal remains visible in `CANONICAL_VOCABULARY.md` rather than being erased.

Decision record: `https://github.com/sajadalireza/AXIOM/issues/6`

## Authority of Other Repository Documents

Research reports, audit files, specifications, prototypes, screenshots, build protocols, files under `docs/superpowers/`, and external PDF or DOCX artifacts are supporting or historical evidence. They are not authoritative control documents unless a later Product Owner Decision Record explicitly promotes them and updates this index.

A duplicate filename, copied excerpt, generated format, historical vocabulary proposal, implementation identifier, resource key, route, table, analytics event, or UI metaphor does not become authoritative by existence alone. Only the paths listed in this index are canonical control-document entry points or controlled plans.

## Ownership and Version Policy

- Document owner: Product Owner.
- Operational custodian: active AXIOM Execution Director for the current Work Packet.
- Every material change requires a linked GitHub Issue and protected Pull Request.
- Strategic changes to the Product Constitution require an explicit Product Owner Decision Record before editing the canonical text.
- Semantic vocabulary changes require an explicit Product Owner Decision Record and version increment to `CANONICAL_VOCABULARY.md`.
- Changes to meaning require a version increment.
- Typographical or formatting-only corrections may use a patch-version increment and must state that meaning is unchanged.
- Source provenance, effective date, and change rationale must remain visible in the modified document or linked Decision Record.
- Historical versions remain available through Git history; rewriting or force-updating history is prohibited.
- Controlled plans may propose follow-up Work Packets but may not activate more than one packet or bypass Acceptance Gates.
- When a trusted second maintainer receives Write access or team governance begins, required approving reviews must be restored to at least `1` as required by the repository governance record.

## Update Checklist

Before merging a control-document change:

1. Confirm the current canonical `main` commit.
2. Identify the governing source and document owner.
3. State whether the change is strategic, semantic, operational, or formatting-only.
4. Record the affected document version and effective date.
5. Verify every relative link from this index and the repository README.
6. Verify that no unlisted duplicate is presented as authoritative.
7. For vocabulary changes, verify the Product Constitution is unchanged unless an explicit amendment was authorized.
8. Verify canonical terms, context-specific terms, legacy aliases, engineering terms, and program-governance terms are not silently conflated.
9. Run the four current-head governance reviews.
10. Merge only through the protected Pull Request flow.
