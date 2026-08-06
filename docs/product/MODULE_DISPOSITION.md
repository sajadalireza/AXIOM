# AXIOM Module Disposition and Freeze Map v1.0

## 1. Document Control

- Status: Canonical when merged to `main`
- Owner: Product Owner (`sajadalireza`)
- Work Packet: `WP-003 — Module Freeze Map`
- Version: `1.0`
- Effective date: `2026-08-06`
- Issue: `https://github.com/sajadalireza/AXIOM/issues/8`
- Authorized baseline: `main@39d6da924a586e9aad85719f6441f4ed8bb2fd37`
- Machine-readable record: [MODULE_DISPOSITION.csv](MODULE_DISPOSITION.csv)
- CSV multivalue delimiter: ` | `
- Implementation authority: none
- Change policy: identity, disposition, Gate, owner, metric, or re-entry changes require a Product Owner decision and protected Pull Request.

## 2. Product Owner Decision

WP-003 assigns exactly one approved disposition to each canonical module: `KEEP`, `IMPROVE`, `HIDE`, `FREEZE`, `VALIDATE`, `UNLOCK_LATER`, or `RETIRE`.

Appendix B source labels such as `Keep + Improve`, `Hide / Unlock Later`, `Freeze / Validate`, `Build Now`, `Release Gate`, and `Rollback Only → Retire` are preserved in CSV rationale fields and normalized to one governance value. WP-003 records decisions only; it does not implement them.

## 3. Source Hierarchy and Provenance

1. Live repository at the authorized baseline.
2. `CANONICAL_REPOSITORY.md`.
3. `PRODUCT_CONSTITUTION.md`.
4. `CANONICAL_VOCABULARY.md`.
5. `EXECUTION_WORKFLOW.md`.
6. `UPGRADE_MASTER_PLAN.md`.
7. `VOCABULARY_DEPRECATION_PLAN.md`.
8. Latest accepted WP-002 state.
9. Product Owner-supplied full Upgrade Master Plan DOCX/PDF, Appendix B.
10. Product Owner-supplied 52-row module registry.
11. Live `Screen.kt`, `AwakenNavGraph.kt`, and `FeatureFlags.kt` read-back.

Appendix B defines `AX-001` through `AX-052`. The registry supplies implementation mapping. Live repository evidence corrects stale route claims but cannot create or remove module identities. Git comparison from recovered product baseline `7a8491f61eda4ba57016a452e057a5e7d84a5c5a` to the WP-003 baseline showed only README/governance-document changes, so the registry remains applicable to current product code.

## 4. Disposition Definitions

- `KEEP`: supports the current Core Loop/foundation and may remain, with bounded cleanup only.
- `IMPROVE`: belongs in the current direction but requires a later bounded repair.
- `HIDE`: later remove ordinary access while preserving code, data, aliases, recovery, and rollback.
- `FREEZE`: prohibit expansion; allow only security, integrity, compatibility, or evidence-preserving repair.
- `VALIDATE`: no expansion before a named cohort, metric, threshold, kill rule, and decision.
- `UNLOCK_LATER`: defer until the owning Gate and prerequisite evidence pass.
- `RETIRE`: reversible removal only after replacement, dependency, data, compatibility, backup, and rollback evidence.

## 5. Module Identity and Deduplication Rules

- Appendix B ID/name pairs are canonical.
- Screens, routes, classes, tables, resources, events, and flags are implementation elements, not automatic modules.
- Shared elements map through documented dependencies; they are not duplicated as new modules.
- WP-002 vocabulary applies: `Dungeon` is a legacy/specialized Project view; `Task` is not a canonical product entity; decorative routing containers are not Gates.
- Visual polish, file count, implementation size, or test concentration does not determine disposition.

## 6. Complete 52-Module Disposition Table

| ID | Canonical module | Disposition | Gate | Primary metric |
|---|---|---|---|---|
| AX-001 | Application bootstrap | IMPROVE | G1 | startup_success_rate |
| AX-002 | App shell and primary navigation | IMPROVE | G2 \| G3 | primary_action_clarity_rate |
| AX-003 | Language and theme setup | IMPROVE | G2 | setup_completion_rate |
| AX-004 | Splash and route guard | IMPROVE | G2 | route_guard_accuracy_rate |
| AX-005 | Legacy identity onboarding | RETIRE | G2 \| G4 | legacy_onboarding_exposure_rate |
| AX-006 | Legacy first mission | RETIRE | G2 | legacy_first_mission_exposure_rate |
| AX-007 | Blueprint and Main Quest | HIDE | G7 | ordinary_user_exposure_rate |
| AX-008 | First-Win vertical slice | IMPROVE | G2 | first_mission_completion_rate |
| AX-009 | Home and Today | IMPROVE | G3 | next_meaningful_mission_action_rate |
| AX-010 | Mission list | IMPROVE | G3 | meaningful_mission_selection_rate |
| AX-011 | Mission authoring | IMPROVE | G3 | mission_authoring_completion_rate |
| AX-012 | Mission execution and focus | IMPROVE | G2 \| G3 | atomic_mission_completion_integrity_rate |
| AX-013 | Dungeons | HIDE | G7 | ordinary_user_exposure_rate |
| AX-014 | Physical Body Map | FREEZE | G7 | ordinary_user_exposure_rate |
| AX-015 | Vitals and daily check-in | VALIDATE | G7 | validated_checkin_value_rate |
| AX-016 | Skill tree | HIDE | G7 | ordinary_user_exposure_rate |
| AX-017 | Shadows | VALIDATE | G7 | obstacle_model_validation_rate |
| AX-018 | Leagues | FREEZE | G7 | ordinary_user_exposure_rate |
| AX-019 | Hunter profile | IMPROVE | G2 \| G3 | minimal_profile_completion_rate |
| AX-020 | Activation and account upgrade | FREEZE | G6 | mandatory_path_exposure_rate |
| AX-021 | Weekly review | UNLOCK_LATER | G5 | weekly_review_completion_rate |
| AX-022 | Weekly analytics | UNLOCK_LATER | G4 \| G5 | weekly_analytics_actionability_rate |
| AX-023 | Financial checkpoint | VALIDATE | G6 | financial_checkpoint_validation_rate |
| AX-024 | Decision filter | HIDE | G7 | ordinary_user_exposure_rate |
| AX-025 | System Voice AI | IMPROVE | G1 \| G5 | safe_ai_suggestion_acceptance_rate |
| AX-026 | Premium and entitlements | FREEZE | G6 | paywall_exposure_before_value_rate |
| AX-027 | Xion companion | KEEP | G2 \| G3 \| G4 \| G5 | xion_contextual_acceptance_rate |
| AX-028 | Reward ceremonies | KEEP | G2 | non_blocking_reward_completion_rate |
| AX-029 | XP, rank, ROI and recovery engines | IMPROVE | G3 | progression_duplicate_ledger_rate |
| AX-030 | Streak and weekly challenge | UNLOCK_LATER | G5 | streak_recovery_rate |
| AX-031 | Feature flags and unlocks | IMPROVE | G1 \| G2 | assignment_consistency_rate |
| AX-032 | Sharing and referral | FREEZE | G7 | ordinary_user_exposure_rate |
| AX-033 | Notifications and reminders | UNLOCK_LATER | G5 | reminder_incremental_completion_lift |
| AX-034 | Sound, haptics and motion | IMPROVE | G3 | sensory_accessibility_compliance_rate |
| AX-035 | Theme, typography and semantic tokens | IMPROVE | G3 | semantic_token_coverage_rate |
| AX-036 | Shared Compose components | IMPROVE | G3 | critical_screen_component_reuse_rate |
| AX-037 | Localization and RTL | IMPROVE | G2 \| G3 | localization_release_gate_pass_rate |
| AX-038 | Accessibility | IMPROVE | G2 \| G3 | accessibility_release_gate_pass_rate |
| AX-039 | Room persistence and migrations | IMPROVE | G2 | migration_integrity_rate |
| AX-040 | Preferences and session state | IMPROVE | G2 | domain_truth_preference_violation_count |
| AX-041 | Domain models and use cases | IMPROVE | G2 \| G3 | canonical_domain_contract_coverage |
| AX-042 | Repositories and dependency injection | KEEP | G1 \| G2 \| G3 | repository_contract_coverage |
| AX-043 | Cloud sync and Supabase transport | FREEZE | G6 \| G7 | sync_data_loss_rate |
| AX-044 | Product analytics and experimentation | IMPROVE | G2 \| G3 \| G4 | activation_event_completeness_rate |
| AX-045 | Crash and startup diagnostics | IMPROVE | G1 \| G2 \| G3 \| G4 | crash_free_users_rate |
| AX-046 | Privacy, security and consent | IMPROVE | G1 \| G2 | unconsented_upload_count |
| AX-047 | Offline and resilience behavior | IMPROVE | G2 \| G3 | offline_journey_success_rate |
| AX-048 | Build, dependencies and release | IMPROVE | G1 | clean_clone_build_success_rate |
| AX-049 | Unit tests | IMPROVE | G1 \| G2 \| G3 | critical_domain_test_coverage |
| AX-050 | Instrumented, UI and migration tests | IMPROVE | G2 | required_instrumented_suite_pass_rate |
| AX-051 | Performance and observability | IMPROVE | G3 \| G4 | representative_performance_budget_pass_rate |
| AX-052 | Seed and blueprint data | IMPROVE | G2 | personal_seed_on_fresh_install_count |

## 7. Disposition Totals

| Disposition | Count |
|---|---:|
| `KEEP` | 3 |
| `IMPROVE` | 30 |
| `HIDE` | 4 |
| `FREEZE` | 6 |
| `VALIDATE` | 3 |
| `UNLOCK_LATER` | 4 |
| `RETIRE` | 2 |
| **Total** | **52** |

## 8. Gate Ownership Summary

All `52 / 52` modules have an owning Gate. Overlapping Gate ranges are counted in each applicable Gate.

| Gate | Modules mapped (overlapping ranges count in each Gate) |
|---|---:|
| `G1` | 8 |
| `G2` | 24 |
| `G3` | 20 |
| `G4` | 6 |
| `G5` | 6 |
| `G6` | 4 |
| `G7` | 10 |

WP-003 does not decompose G3–G7 beyond canonical source detail.

## 9. Metric Ownership Summary

- Accountable owner: `Product Owner (sajadalireza)` for `52 / 52` rows.
- Source execution lane recorded for `52 / 52` rows.
- Primary metric decision recorded for `52 / 52` rows.
- Active `KEEP`/`IMPROVE` rows with metrics: `33 / 33`.
- Deferred/hidden/frozen/validation/retirement rows with exposure, validation, integrity, or final-decision metrics: `19 / 19`.
- Runtime metrics authorized or emitted by WP-003: `0`.

Metrics are governance contracts, not claims that events or dashboards exist. Sensitive Goal, Purpose, Project, Mission, Action, evidence, reflection, account, health, or financial free text must not enter analytics payloads.

## 10. Route and Navigation Implications

All 29 live Screen routes map to canonical modules:

| Route | Canonical module mapping | Current implication |
|---|---|---|
| setup | AX-003 | Conditional mandatory first-launch setup |
| splash | AX-004 | Start destination and route guard |
| activation | AX-020 | Optional route from Profile; frozen from mandatory path |
| onboarding | AX-005 | Currently mandatory when firstMissionDone=false; planned retirement |
| awakening_complete | AX-005 \| AX-006 | Legacy handoff between onboarding and first mission |
| home | AX-009 | Primary tab |
| missions | AX-002 \| AX-010 | Primary shell destination; currently renders GatesScreen |
| add_mission | AX-011 | Mission authoring |
| add_mission/{skillId} | AX-011 \| AX-016 | Skill-linked Mission authoring |
| mission_detail/{id} | AX-012 | Mission execution and focus |
| dungeons | AX-013 | Deferred Project/Dungeon surface |
| dungeon_detail/{id} | AX-013 | Deferred Project/Dungeon detail |
| create_dungeon | AX-013 | Deferred Project/Dungeon authoring |
| skill_tree | AX-016 | Deferred capability visualization |
| shadow_army | AX-017 | Reachable primary tab; validation required |
| profile | AX-019 | Primary tab |
| character_stats | AX-019 \| AX-029 | Profile/progression detail |
| main_quest | AX-007 | Deep route with Purpose/Goal ambiguity |
| system_voice | AX-025 | AI route requiring security boundary improvement |
| premium | AX-026 | Frozen UI; purchase flag currently false |
| leagues | AX-018 | Deferred/frozen social comparison |
| first_mission | AX-006 | Current legacy mandatory flow for new users |
| blueprint_wizard | AX-007 \| AX-052 | Current conditional mandatory blueprint/seed flow |
| body_map | AX-014 | Primary tab; frozen Health Pack candidate |
| daily_checkin | AX-015 | Health/vitals validation surface |
| weekly_analytics | AX-022 | Deferred retention analytics |
| financial_checkpoint | AX-023 | Validation-only financial tracking |
| weekly_review | AX-021 | Deferred retention review |
| decision_filter | AX-024 | Hidden later secondary tool |

Live corrections:

1. `AX-005`: live onboarding proceeds through `awakening_complete`, not directly Home.
2. `AX-006`: live `first_mission` is mandatory while `firstMissionDone=false`, contrary to the registry note.
3. `AX-007`: live `blueprint_wizard` is mandatory while `blueprintSetupComplete=false`, contrary to the optional/deep registry note.
4. `AX-010`: `missions` currently renders `GatesScreen`, a WP-002 vocabulary collision.
5. `AX-008`: no dedicated First-Win route exists.

These are evidence and migration risks, not route-change authorization.

## 11. Feature-Flag Implications

Live flags:

- `AI_FEATURES_ENABLED=true`
- `FORCE_OFFLINE_MODE=false`
- `PREMIUM_PURCHASE_ENABLED=false`

No sticky First-Win assignment, eligibility version, central experiment system, module disposition registry, or general kill switch is proven. WP-003 adds no flag.

CSV policy codes:

- `RS_ACTIVE_PRESERVE`: preserve route; bounded follow-up only.
- `RS_REPLACE_WITH_LEGACY_FALLBACK`: state-matrix replacement with compatible fallback.
- `RS_ADD_G2_STICKY_ROUTE`: add only under G2 with persisted assignment.
- `RS_HIDE_DEFAULT_OFF_KEEP_ALIAS`: default-off later; retain alias/recovery.
- `RS_RETIRE_REDIRECT_KEEP_READER`: redirect later; keep old reader until removal.
- `RS_FREEZE_STABLE`: preserve route and prohibit expansion.
- `RS_VALIDATE_COHORT_ONLY`: controlled-cohort entry only.
- `RS_DEFER_KEEP_COMPAT`: defer exposure; preserve compatibility.
- `FS_*`: no WP-003 flag change; later default-safe rollout/kill policy.
- `RB_*`: restore prior route/reader/surface and revert protected PR without deleting data.

Exact flag-policy codes:

- `FS_AI_KILL_PLUS_SERVER_BOUNDARY`: Later AI packet must provide a kill switch and approved server boundary before expansion.
- `FS_BUILD_CONTROL_PLANE_WP208`: Create sticky assignment, eligibility version, module state, and kill semantics only in WP-208.
- `FS_DEFAULT_SAFE_MODULE_FLAG_LATER`: Later packet may add a persisted default-safe module flag with kill and recovery behavior.
- `FS_KEEP_PREMIUM_PURCHASE_FALSE`: Keep the existing purchase flag false until G6 purchase evidence and rollback pass.
- `FS_NO_CHANGE`: WP-003 changes no flag; later work must keep current behavior until accepted.
- `FS_SYNC_DEFAULT_OFF_WITH_BACKUP`: Keep sync default-off until backup, restore, consent, conflict, and rollback evidence pass.

Exact rollback-policy codes:

- `RB_DISABLE_HIDE_RESTORE_NAV`: Disable the hide treatment and restore prior navigation without deleting implementation or data.
- `RB_DISABLE_STAGE_RETAIN_DATA`: Disable the staged rollout and retain all existing data and compatibility readers.
- `RB_DISABLE_UNLOCK_RESTORE_NAV`: Disable staged unlock and restore prior navigation/availability.
- `RB_END_COHORT_RESTORE_SURFACE`: End the validation cohort and restore the prior surface.
- `RB_RESTORE_REDIRECT_READER_BACKUP`: Restore the legacy redirect/reader from backup; no destructive cleanup before observation passes.
- `RB_REVERT_PR_KEEP_IDS_DATA`: Revert the bounded implementation PR while preserving identifiers and data.

## 12. Dependency and Collision Analysis

- **Launch/First-Win:** `AX-004`, `005`, `006`, `007`, `008`, `052` overlap in eligibility, profile creation, first Mission, blueprint, and neutral bootstrap. Resolve in WP-201/202/203/208.
- **Mission/Project vocabulary:** `AX-002`, `010`, `011`, `012`, `013`, `016` share navigation/authoring and legacy Task/Gate/Protocol/Dungeon/Sprint terms.
- **Progression/data truth:** `AX-012`, `028`, `029`, `030`, `039`, `040`, `041`, `042` share completion, XP, streak, reward, profile, and recovery state.
- **Premature reachability:** `AX-013`–`018`, `023`, `024`, `026` expose G6/G7 surfaces before their evidence Gate.
- **AI/remote/privacy:** `AX-025`, `043`, `044`, `045`, `046` share credentials, transport, sensitive context, event delivery, and consent boundaries.
- **Design/accessibility:** `AX-034`–`038` must preserve Persian, RTL, TalkBack, focus, scaling, contrast, touch targets, reduced motion, and sound-off behavior.
- **Build/evidence:** `AX-048`–`051` govern whether later implementation claims are trustworthy. WP-003 reruns none of their checks.

## 13. Hide / Freeze / Retire Strategy

1. Preserve code, identifiers, data readers, route aliases, and recovery evidence.
2. Open the named follow-up packet with a bounded Acceptance Contract.
3. Use default-safe persisted assignment for runtime visibility changes.
4. Verify deep links, saved state, restart, offline, accessibility, privacy, and rollback.
5. Measure exposure/dependency use before deletion.
6. For `RETIRE`, require replacement adoption and ordinary exposure `0`.
7. Remove compatibility only after an explicit Product Owner decision and stable observation window.

## 14. Re-entry and Unlocking Rules

- `HIDE`: owning-Gate prerequisites, approved UX, exposure target, and route/flag rollback PASS.
- `FREEZE`: Core Loop evidence, security/integrity clearance, named metric threshold, compatibility, and rollback PASS.
- `VALIDATE`: pre-registered cohort/test, owner, metric threshold, kill criterion, and explicit decision.
- `UNLOCK_LATER`: owning Gate PASS plus prerequisite data/event truth and staged rollback.
- `RETIRE`: replacement accepted, exposure zero, dependencies/data/backup/recovery verified, final-removal decision.
- `KEEP`/`IMPROVE`: only the listed accepted follow-up packet may change behavior.

## 15. Parking Lot

- Neutral replacement for `GatesScreen`.
- Purpose versus Goal onboarding contract.
- Project/Dungeon/Sprint compatibility.
- Body Map/Vitals Health Pack and claim-safety validation.
- Opt-in Leagues model.
- Financial tracking versus advice boundary.
- System Voice versus contextual Xion boundary.
- Event dictionary, consent queue, historical aliases, and metric instrumentation.
- Central module flags, sticky assignment, and kill semantics.
- Exact G3–G7 packet decomposition; deferred to Product Owner-authorized WP-004.

## 16. Follow-up Work Packet Recommendations

Use canonical G1 packets WP-101–107 and G2 packets WP-201–209 where applicable. G3–G7 references remain Gate-level placeholders until WP-004 authorizes decomposition. CSV `follow_up_packet` records module-specific recommendations; no recommendation activates a packet.

## 17. Rollback Strategy

Before merge, close the PR and delete `docs/wp-003-module-freeze-map`. After merge, revert the WP-003 merge commit through a protected PR. WP-003 requires no runtime/data rollback because it changes only documentation. Future Hide/Retire work must define backup, aliases/adapters, default-safe flags, observation windows, and explicit recovery commands before destructive action.

## 18. Evidence Limitations

- No build, lint, unit, instrumented, migration, runtime, security, remote-service, performance, or CI check is executed by WP-003.
- The registry is dated 2026-08-01; applicability is supported by no product-code change plus live route/flag read-back.
- Stale reachability claims are explicitly corrected.
- Remote Supabase, AI, analytics, diagnostics, account, league, and sync behavior remains unverified where source evidence says unknown.
- Metrics are decisions, not existing telemetry claims.
- Owner waves are execution lanes; current accountability remains with `sajadalireza`.
- Source composite decisions remain visible in CSV rationale fields.

## 19. Acceptance Checklist

- [x] Authoritative source reconciled to `AX-001`–`AX-052`.
- [x] Exactly 52 identities and one disposition each.
- [x] Owner, Gate, metric, evidence, dependency, route/flag policy, re-entry, risk, and rollback fields populated.
- [x] All 29 live Screen routes mapped.
- [x] Constitution and WP-002 vocabulary preserved.
- [x] No implementation authorized.
- [ ] Branch-head CSV parse and Markdown/CSV equivalence verified.
- [ ] Exact two-file boundary verified.
- [ ] Ruleset, CI truth, thread state, and four reviews verified.
- [ ] Protected merge and post-merge read-back completed.
- [ ] Final score ≥ `9.5`, no Hard Cap, Issue closed after verification.
