# AXIOM Upgrade Master Plan v1.0

## Document Control

- Status: Canonical control edition
- Owner: Product Owner (`sajadalireza`)
- Version: `1.0`
- Effective date: `2026-08-05`
- Strategy: Validation-Led Core Reconstruction
- Source: `AXIOM_Upgrade_Master_Plan_v1.0.md`, supplied by the Product Owner
- Full source artifact: Product Owner-supplied DOCX/PDF v1.0
- Change policy: Meaning changes require a Product Owner Decision Record, version increment, and protected Pull Request.

## Decision

Preserve AXIOM’s Personal Progression / Life OS identity, freeze non-core expansion, and rebuild the First-Win, Core Loop, Data Truth, Analytics, Security, and Release foundation through evidence gates.

## Gate Roadmap

- **G0 — Product & Scope Lock:** Product Constitution، Vocabulary، Beachhead hypothesis، Freeze list | Exit: سند در repo؛ تصمیم همه Moduleها؛ هیچ feature بدون Metric owner | Initial capacity: ۳–۵ روز
- **G1 — Repository Integrity:** Wrapper، clean Git، deterministic fonts، CI baseline، secrets audit | Exit: Clean clone build/test/lint؛ critical secret صفر؛ artifact tracked صفر | Initial capacity: ۵–۱۰ روز
- **G2 — First-Win Vertical Slice:** Route guard، session/state، Room v17، UI، atomic completion، consent queue | Exit: Fresh install → first value؛ restart/resume؛ rollback؛ test matrix سبز | Initial capacity: ۲–۴ هفته
- **G3 — Core Loop & Data Truth:** Goal/Mission canonical model، Home CTA، XP ledger، progress projections | Exit: Activation cohort ≥70٪؛ TTFV median <3m؛ FMC ≥65٪ | Initial capacity: ۳–۵ هفته
- **G4 — Instrumented Beta:** ۲۰–۳۰ نفر ۲۱ روز سپس ۵۰–۱۰۰ نفر، cohort dashboards | Exit: D1 ≥30٪؛ D7 ≥20–25٪؛ WMPU ≥25٪؛ crash-free قابل قبول | Initial capacity: ۴–۸ هفته
- **G5 — Retention Proof:** Flexible streak، recovery، weekly review، curated templates، Xion limited | Exit: دو cohort؛ D30 ≥10٪؛ streak recovery ≥40٪؛ Xion acceptance ≥35٪ | Initial capacity: ۶–۱۰ هفته
- **G6 — Monetization Proof:** Value test، paywall prototype، entitlement، billing، AI cost budget | Exit: Free-to-paid test 3–6٪؛ renewal signal؛ AI cost <15٪ target net revenue | Initial capacity: ۴–۸ هفته
- **G7 — Progressive Expansion:** Skill Tree، Dungeon، Body Map pack، creator packs، localization | Exit: هر expansion دارای cohort، owner، metric و positive unit economics | Initial capacity: مشروط

## Sprint Zero

- **SZ-01 Add Gradle Wrapper and pin distribution** — Clean clone works
- **SZ-02 Remove app/build from Git tracking** — tracked count=0
- **SZ-03 Remove CDN font task and invalid fallback** — offline build stable
- **SZ-04 Secret scan and rotate exposed candidates** — APK/repo secret zero
- **SZ-05 CI: unit+lint+assemble+schema check** — protected required checks
- **SZ-06 Write Product Constitution + glossary** — approved repo doc
- **SZ-07 ADR-001 Source of Truth** — Room/DataStore contract
- **SZ-08 ADR-002 New-user eligibility** — state matrix
- **SZ-09 Regression test for launch race** — fails before fix
- **SZ-10 Neutral bootstrap refactor plan** — no personal seed startup
- **SZ-11 First-Win migration/test plan** — schema and invariants
- **SZ-12 Event dictionary and consent contract** — typed schema
- **SZ-13 Create module freeze flags** — secondary routes inaccessible
- **SZ-14 Create Beta feedback protocol** — recruitment + consent + survey

## Core Rules

- Goal progress outranks XP
- Failure is information, not punishment
- One primary action per screen
- Room is transactional truth; DataStore is preference/sticky assignment
- No client secrets
- No expansion before retention evidence

## Critical Metrics

- **Activation:** ≥70٪ invited cohort
- **TTFV:** Median <3 دقیقه؛ 80٪ <5 دقیقه
- **FMC:** ≥65٪؛ مطلوب 80٪
- **Meaningful Mission Ratio:** >75٪
- **D1:** ≥30٪ beta invited
- **D7:** ≥20–25٪؛ Concierge مطلوب 35٪
- **D30:** ≥10٪ قبل monetization
- **WMPU Rate:** ≥25٪؛ مطلوب 40٪
- **Streak Recovery:** ≥40٪
- **Xion Acceptance:** ≥35٪؛ kill <20٪
- **AI Cost/WMPU:** <10–15٪ درآمد خالص هدف
- **Crash-free users:** هدف release-specific؛ S1 صفر

## Full Artifact and Authority

The Product Owner-supplied DOCX/PDF contains the full Product Constitution, 52-module matrix, Epic backlog, workflow, tests, event dictionary, beta protocol, risks, and quality review. This Markdown file is the canonical repository control entry point for the Upgrade Master Plan. External formats remain source artifacts and supporting evidence; they do not silently override this versioned repository path.
