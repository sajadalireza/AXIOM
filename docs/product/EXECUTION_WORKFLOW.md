# AXIOM Execution Workflow v1.0

## Document Control

- Status: Canonical control edition
- Owner: Product Owner (`sajadalireza`)
- Version: `1.0`
- Effective date: `2026-08-05`
- Source: `AXIOM_Execution_Workflow_v1.0.md`, supplied by the Product Owner
- Full source artifact: Product Owner-supplied DOCX/PDF v1.0
- Change policy: Meaning changes require a Product Owner Decision Record, version increment, and protected Pull Request.

## قانون مرکزی

- یک Gate، یک Epic فعال، یک Work Packet جاری.
- هر خروجی فقط با Score >= 9.5، بدون Hard Cap و با Evidence Package تحویل می‌شود.
- ترتیب: G0 Product Lock -> G1 Repo Integrity -> G2 First-Win -> G3 Core Loop -> G4 Beta -> G5 Retention -> G6 Monetization -> G7 Expansion.

## چرخه Work Packet

1. Evidence Intake
2. Baseline
3. Change Contract
4. Test Matrix
5. Vertical Slice
6. Implementation
7. Verification
8. Adversarial Review
9. Score & Repair
10. Rollout & Decision
11. Documentation

## PASS

Score >= 9.5 + no blocker + all Must acceptance passed + rollback + evidence.

## Work Packet اول

**WP-000: Canonical Repository & Access** — تعیین GitHub repo، branch و commit مبنا.

## Full Artifact and Authority

The Product Owner-supplied DOCX/PDF contains the complete Gate definitions, Work Packet queue from WP-000 through WP-209, rubric, Hard Caps, Git and Pull Request workflow, Evidence Package requirements, and execution templates. This Markdown file is the canonical repository control entry point for the Execution Workflow. External formats remain source artifacts and supporting evidence; they do not silently override this versioned repository path.

## Application Rule

The latest valid Checkpoint Capsule determines the current execution state. It may apply the Workflow to a specific Work Packet but may not silently amend this Workflow. Any conflict or proposed meaning change requires an explicit Product Owner Decision Record.
