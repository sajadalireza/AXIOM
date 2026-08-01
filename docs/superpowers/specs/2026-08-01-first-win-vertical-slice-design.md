# AXIOM First-Win Vertical Slice Design

**Status:** Approved with required amendments; locked for written-spec review
**Date:** 2026-08-01
**Authoritative checkout:** `/Users/sajadalireza/Downloads/Warrior/warrior_fixed`

## 1. Purpose

AXIOM's first product upgrade will validate one core hypothesis:

> A new user who can complete one meaningful real-world action, see a restrained reward, and schedule the next action in under 90 seconds is more likely to return than a user introduced first to AXIOM's full RPG system.

This slice replaces the new-user path only. It reuses the real Mission, Skill, Hunter, XP, and streak domains; adds durable typed analytics; and keeps the legacy onboarding route available for rollback.

## 2. Scope

### In scope

- Sticky assignment to the `first_win_v1` experiment.
- State-aware routing for new, resumed, completed, and established users.
- Setup, Area, Action, Do, Reward, Next, and Home handoff screens.
- A resumable First-Win state machine whose committed state is authoritative in Room.
- Anonymous Friendly profile and base-skill initialization without starter missions or dungeons.
- Real Mission creation, start, atomic completion, XP, streak, reward, and next-action scheduling.
- Domain- and database-level idempotency.
- Typed, consent-aware product analytics with a durable local queue.
- Separate operational-integrity reporting for crashes and migration failures.
- English, Persian, RTL, TalkBack, 200% font scale, high contrast, and Reduced Motion behavior.
- Unit, migration, integration, and Compose UI coverage for the complete flow.

### Out of scope

- The complete Today redesign.
- Three-tab navigation.
- Friendly/Warrior mode settings outside the default Friendly presentation used here.
- Progressive Unlock Engine.
- Account creation, backup, notification permission, billing, leagues, Body Map, Finance, or advanced AI coaching.
- Remote-config infrastructure beyond the experiment-assignment boundary required by this slice.
- Rewriting the existing Mission system or creating an onboarding-only task model.

## 3. Product Flow

```text
Setup (outside progress)
  -> Area                 Step 1 of 4
  -> Action               Step 2 of 4
  -> Do                   Step 3 of 4
  -> Reward               transition, not numbered
  -> Next                 Step 4 of 4
  -> Handoff              transition, not numbered
  -> existing Home
```

The progress indicator counts only user-decision/action screens. Setup is prerequisite configuration. Reward and Handoff are short transition screens and do not inflate the visible step count.

### Primary success path

1. Select language if not already configured.
2. Select one life area: Work, Study, Health, or Personal.
3. Select one contextual micro-action or enter a custom action.
4. Create and start a real Mission.
5. Complete the real-world action.
6. Commit completion, XP, streak, and the completion receipt atomically.
7. Show a short, skippable Xion reward.
8. Schedule one real next action.
9. Persist First-Win completion and hand off to the existing Home.

### Safe-exit branches

```text
ActionSelected
  -> ScheduledForLater
  -> AwaitingRealStart

RewardSeen
  -> CompletedWithoutScheduling
```

`ScheduledForLater` is not terminal and resumes at `AwaitingRealStart`. `CompletedWithoutScheduling` is terminal and persists `completionVersion`, but it records first value rather than activation because no next action was scheduled.

## 4. State Machine

```text
NotStarted
  -> LanguageSelected
  -> AreaSelected
  -> ActionSelected
  -> MissionCreated
  -> ActionStarted
  -> ActionCompleted
  -> RewardSeen
  -> NextActionScheduled
  -> Completed
```

### Transition rules

- A tap may update draft UI state but cannot advance committed state.
- Committed state advances only after the corresponding Room transaction succeeds.
- Room is authoritative for the highest committed First-Win state.
- DataStore is a small routing and recovery snapshot, not the source of truth for domain completion.
- On launch, Room reconciles DataStore. Room repairs stale DataStore state except for sticky experiment assignment, whose original DataStore assignment remains authoritative.
- A pre-commit draft may be lost after process death. This is acceptable and is not a resume failure.
- A committed transition must be replay-safe and return its prior result when called with the same idempotency key.

## 5. Routing And Eligibility

Profile existence alone never means onboarding is complete.

```kotlin
when {
    firstWinStore.completionVersion != null -> Home
    firstWinStore.hasStarted -> FirstWinRoute(resume = true)
    legacyProfileExists && !firstWinStore.isExperimentUser -> Home
    firstWinAssignmentPolicy.isEligibleForNewAssignment() -> FirstWinRoute(resume = false)
    else -> LegacyOnboarding
}
```

### Routing precedence

1. A completed First-Win user routes to Home.
2. An assigned user with an incomplete First-Win session resumes regardless of the anonymous profile created mid-flow.
3. An established legacy user who was never assigned bypasses First Win.
4. An eligible new user receives a sticky assignment and enters First Win.
5. A non-eligible new user enters legacy onboarding.

### Kill-switch behavior

- The standard kill switch stops only new assignment.
- Users already assigned continue their sticky variant and finish or resume safely.
- No remote-config refresh or network change may move an in-flight user to another variant.
- An emergency in-flight migration requires a new, versioned migration policy. It is not inferred from the normal kill switch.

## 6. Sticky Experiment Assignment

At first eligibility, `FirstWinStore` persists all of the following before navigation:

- `experimentId`
- `variant`
- `assignmentTimestamp`
- `eligibilityVersion`
- `firstWinSessionId`
- `hasStarted`
- `completionVersion`

Assignment is stable across process death, restart, offline mode, remote-config refresh, and app upgrade. `experiment_assigned` records assignment; `experiment_exposed` records actual visible treatment. They are intentionally different events.

## 7. Persistence Model

Room database version 17 adds the following durable structures through a non-destructive `16 -> 17` migration.

### `first_win_sessions`

- `sessionId` primary key
- `state`
- `area`
- `primaryMissionId`
- `nextMissionId`
- `experimentId`
- `experimentVariant`
- `assignmentTimestamp`
- `eligibilityVersion`
- `rewardSeenAt`
- `completionVersion`
- `createdAt`
- `updatedAt`

This table is authoritative for committed flow state and committed Area selection.

### Mission idempotency and scheduling fields

- `firstWinSessionId`
- `experimentVariant`
- `creationIdempotencyKey`
- `completionIdempotencyKey`
- `scheduleIdempotencyKey`
- `scheduledFor`
- `startedAt`

Nullable unique indexes protect `creationIdempotencyKey`, `completionIdempotencyKey`, and `scheduleIdempotencyKey`. `firstWinSessionId` is indexed for reconciliation.

### `first_win_completion_receipts`

- `completionIdempotencyKey` primary key
- `sessionId`
- `missionId`
- `hunterXpAwarded`
- `skillXpAwarded`
- `resultingStreak`
- `completedAt`

The receipt makes completion replay-safe and supplies the exact result after a crash or duplicate request.

### `analytics_event_queue`

- `eventId` primary key
- `eventName`
- `schemaVersion`
- `purpose`
- `anonymousSessionId`
- `firstWinSessionId`
- `experimentId`
- `variant`
- `propertiesJson`
- `createdAt`
- `attemptCount`
- `nextAttemptAt`

The queue contains no name, email, action text, or other direct personal identifier.

## 8. Domain Transactions And Idempotency

Every First-Win operation receives the stable `firstWinSessionId` and its operation-specific idempotency key.

### Create

```text
CreateMission(sessionId, creationIdempotencyKey)
  -> return the existing Mission when that key already exists
  -> otherwise create exactly one Mission and advance to MissionCreated
```

### Complete

```text
CompleteMission(missionId, completionIdempotencyKey)
  -> return the existing completion receipt when that key already exists
  -> otherwise update Mission, Hunter XP, Skill XP, canonical streak,
     completion receipt, First-Win state, and local analytics in one Room transaction
```

The existing Room `StreakEntity` becomes canonical for the completion transaction. Legacy DataStore streak values are reconciled from Room after commit so a process death cannot duplicate or lose the committed streak result.

Network submission, AI copy, sound, animation, cloud backup, and league submission run only after the transaction commits. Their failure cannot roll back or duplicate the durable completion.

### Schedule

```text
ScheduleNextAction(sessionId, scheduleIdempotencyKey)
  -> return the existing next Mission when that key already exists
  -> otherwise create exactly one scheduled Mission and advance to NextActionScheduled
```

## 9. Initialization Boundary

`EnsureCoreProfileUseCase` creates only what First Win needs:

- Anonymous Friendly profile. Where the current schema requires a non-null Hunter name, persist an internal anonymous sentinel and render it as localized `You`.
- First-launch timestamp.
- One neutral `skill_follow_through` starter skill needed to attach a real Mission. Area remains session context and is not converted into permanent profile taxonomy.
- Initial streak record if none exists.

It does not create starter missions, starter dungeons, RPG lore, notification prompts, account requirements, or advanced modules. The legacy initializer may delegate to the shared core initializer and retain its legacy-only seed behavior.

## 10. Consent And Analytics

### Consent states

| State | Product analytics behavior |
| --- | --- |
| `Unknown` | Keep temporary anonymous product events locally. Do not upload. |
| `Granted` | Queue new events and permit WorkManager batch upload. |
| `Declined` | Delete queued product-analytics events and store no new product-analytics events. |

Unknown events expire locally after seven days if consent remains unresolved. Updating consent records `analytics_consent_updated` only when product analytics is permitted to record that change; a local consent audit timestamp remains available regardless.

### Operational integrity separation

Crash integrity, database corruption, and migration failure are not product analytics. They use a separate operational channel, schema, retention policy, and identifier scope. They cannot contain action text or be joined to First-Win behavioral events through a personal identifier.

### Required typed events

- `experiment_assigned`
- `experiment_exposed`
- `onboarding_started`
- `onboarding_step_viewed`
- `onboarding_step_resumed`
- `onboarding_step_completed`
- `goal_selected`
- `first_action_created`
- `action_started`
- `action_completed`
- `first_value_reached`
- `first_reward_seen`
- `next_action_scheduled`
- `first_win_activated`
- `first_win_resumed`
- `first_win_abandoned`
- `first_win_safe_exit`
- `analytics_consent_updated`

### Event scope

- `experiment_assigned`: once per sticky assignment.
- `experiment_exposed`: once per sticky assignment, after the treatment UI is fully rendered and at least one frame has been visible.
- `onboarding_step_viewed`: once per step per First-Win session.
- `onboarding_step_resumed`: once when a previously committed step is revisited in a later app process/session.
- `goal_selected`: after successful Area Room commit, never on category tap.
- `first_value_reached`: after the atomic action-completion transaction commits.
- `first_win_activated`: after next-action scheduling commits and `completionVersion` is durably persisted.
- `first_win_abandoned`: explicit exit before first value, not ordinary backgrounding or process death.
- `first_win_safe_exit`: when the user intentionally schedules the first action for later.

Analytics enqueueing is part of the relevant local transaction where causality matters. Upload is always asynchronous and non-blocking.

## 11. Mobile Screen Contracts

### 11.1 Setup

Setup is outside visible progress.

- **Default:** Show AXIOM, the literal value proposition, English and Persian choices, Continue, and a secondary Privacy choices entry.
- **Loading:** Persist language and locale while preserving button dimensions and disabling conflicting controls.
- **Error:** Keep the selected language and show an inline retry near Continue.
- **Resume:** If Setup committed, skip it. Otherwise restore the last committed locale.
- **Offline:** Fully functional; no network dependency.
- **Double tap:** One in-flight setup commit and one navigation result.
- **Back:** Exit setup naturally. Preserve a committed locale.
- **Accessibility:** Announce language names rather than flags; use a single-select semantic group, RTL, 200% font scale, and 48dp targets.
- **Exposure:** `setup_viewed` after one visible frame. Experiment exposure does not occur here.

### 11.2 Area — Step 1 Of 4

The Area is initial context only and never becomes a permanent profile taxonomy.

- **Default:** Show Work, Study, Health, and Personal with no initial selection. Continue is disabled. No validation error appears.
- **Selected:** One category has a border, indicator, and selected semantics. Continue becomes enabled.
- **Loading:** Begins only after Continue. Keep CTA dimensions stable, show progress, and temporarily disable every category.
- **Error:** Preserve the draft selection. Show `We couldn't save this yet. Try again.` beside Continue.
- **Resume:** Select the last committed Area from Room. DataStore is only a repairable snapshot.
- **Offline:** The product UI is unchanged. Room commit works normally and analytics remains queued.
- **Double tap:** ViewModel mutex, domain idempotency, and a database constraint guarantee one Area commit.
- **Back:** Return to Setup when Setup is present; otherwise exit naturally. Do not show a confirmation. Preserve sticky assignment and locale.
- **Accessibility:** Announce `Study, Learn or review, selected`. Focus order is Title, supporting copy, Work, Study, Health, Personal, Continue.
- **Analytics:** `experiment_exposed` and `onboarding_step_viewed(step = "area")` follow their separate scopes. After successful Continue commit, enqueue `goal_selected(area = ...)` and `onboarding_step_completed(step = "area")`.

Category taps change draft UI state only. A process death before Continue may discard that draft without being reported as a resume failure.

### 11.3 Action — Step 2 Of 4

- **Default:** Show three contextual micro-actions and one custom-action field. No suggestion is committed until the primary CTA.
- **Loading:** Create the real Mission using the stable creation key; lock suggestions and input while preserving layout.
- **Error:** Preserve the selected suggestion or custom text and retry with the same key.
- **Resume:** Reconcile by `firstWinSessionId`. If Mission creation committed, show the committed Mission and advance to Do.
- **Offline:** Mission creation is fully local.
- **Double tap:** One Mission is returned for repeated creation requests.
- **Back:** Before custom text or Mission commit, return to Area. If valuable custom text exists, confirm before discarding it. After Mission commit, use the safe-exit sheet.
- **Accessibility:** Suggestions are a radio group. Custom input has a visible label, error semantics, IME Done, and a 48dp touch target.
- **Exposure:** `onboarding_step_viewed(step = "action")` after one visible frame. `first_action_created` and step completion enqueue only after Mission commit.

### 11.4 Do — Step 3 Of 4

- **Default:** Show one action, its area, an optional lightweight timer, `Start now`, and `Schedule instead`. After start commits, the primary CTA becomes `I did it`.
- **Loading:** Start or complete using the stable operation key. Keep button dimensions fixed and disable conflicting actions.
- **Error:** Preserve action state. A completion retry reuses `completionIdempotencyKey` and returns an existing receipt when present.
- **Resume:** `MissionCreated` resumes ready to start; `ActionStarted` resumes active; `ActionCompleted` skips directly to Reward.
- **Offline:** Start, completion, XP, streak, and receipt remain fully functional.
- **Double tap:** The UI mutex and Room transaction prevent duplicate XP, streak, completion, or receipt writes.
- **Back:** Before start, return to Action. After start, show Keep going, Schedule instead, and Exit. Exit never awards XP or marks completion.
- **Accessibility:** Do not announce every timer second. Announce only start, pause, safe exit, and completion state changes.
- **Exposure:** `action_started` after start commit. `action_completed` and `first_value_reached` after the atomic completion transaction.

### 11.5 Reward — Transition

- **Default:** Show a two-to-three-second Xion response, a visible progress mark, `Set the next step`, and a skippable animation.
- **Loading:** Read the durable completion receipt. The visual may wait briefly without delaying navigation controls.
- **Error:** Fall back to static success copy derived from the receipt. AI, sound, or animation failure is invisible to product completion.
- **Resume:** If completion committed but reward was not seen, show it once. If reward was already seen, resume Next without replay.
- **Offline:** Fully functional with static/local content.
- **Double tap:** Acknowledge reward once and advance once.
- **Back:** Advance to Next. Completion cannot be reversed and the ceremony is not replayed.
- **Accessibility:** Reduced Motion shows the final static state. Announce the complete reward meaning once in plain language.
- **Exposure:** `first_reward_seen` only when reward content is actually visible.

### 11.6 Next — Step 4 Of 4

- **Default:** Offer Later today, Tomorrow morning, and Choose a time. `Finish for now` remains a lower-emphasis command.
- **Loading:** Create the scheduled next Mission and finalize completion using one stable schedule key.
- **Error:** Preserve the chosen time and retry with the same key.
- **Resume:** Query by `scheduleIdempotencyKey`. If scheduling committed, restore the exact next Mission and finish.
- **Offline:** Scheduling is local and fully functional.
- **Double tap:** One next Mission and one activation result are produced.
- **Back:** Return to a static Reward summary without replay. `Finish for now` produces `CompletedWithoutScheduling`, not activation.
- **Accessibility:** Announce full localized date, time, and timezone. Restore focus to the selected time after picker dismissal.
- **Exposure:** `next_action_scheduled` after schedule commit; `first_win_activated` after durable `completionVersion` persistence.

### 11.7 Handoff — Transition

- **Default:** For activated users, confirm the scheduled next action and offer `Go to Home`. For `CompletedWithoutScheduling`, confirm first value without claiming a next action exists. This is a compatibility bridge, not a Today redesign.
- **Loading:** Route once to the existing Home.
- **Error:** Stay on Handoff and offer navigation retry without changing completion.
- **Resume:** Any non-null `completionVersion` routes to Home rather than reopening First Win.
- **Offline:** No behavior change.
- **Double tap:** Navigation is single-top and idempotent.
- **Back:** Resolve to Home after completion.
- **Accessibility:** Announce completion once and include the scheduled time only when one exists; focus lands on the primary Home action.
- **Exposure:** No required product-analytics event. Activation, when applicable, was already recorded after the scheduling commit.

The existing Home receives one dismissible First-Win context banner. Its wider redesign remains out of scope.

## 12. Visual And Interaction Language

- Friendly Mode is the default First-Win presentation.
- Use the existing light Axiom palette as a base, with system green for the single primary CTA and restrained blue/gold accents for information and reward.
- Use Outfit for interface text, Fraunces for brief editorial headings, and JetBrains Mono only for timer/numeric readouts.
- Show one primary CTA per screen.
- Use no more than three visual hierarchy levels.
- Controls keep stable dimensions during loading.
- Minimum touch target is 48dp.
- Meaning is never encoded by color alone.
- Reward motion is short, skippable, and replaced by a static state under Reduced Motion.
- Xion supports the action and reward; it does not judge, threaten, shame, or delay value.

## 13. Error And Recovery Policy

- Preserve user input on every recoverable error.
- Place errors near the action that failed.
- Do not surface analytics, upload, AI, sound, or animation failures as product-flow errors.
- Reconcile Room before rendering a resumed step.
- Repair stale DataStore snapshots after reconciliation.
- Treat process death as normal recovery, never abandonment.
- Record abandonment only for explicit exit before first value.
- Never route an in-flight user to Home merely because an anonymous profile exists.

## 14. Testing Strategy

### Unit tests

- Every valid and invalid state transition.
- Route-guard precedence.
- Sticky assignment across config refresh and restart.
- Area draft-versus-commit behavior.
- Event scope and deduplication.
- Consent transitions and queue deletion.
- Create, complete, and schedule idempotency.
- Completion receipt replay.
- DataStore reconciliation from Room.

### Room and migration tests

- Non-destructive `16 -> 17` migration.
- Existing Hunter, Mission, Skill, streak, and user data remain intact.
- Unique idempotency indexes reject duplicates.
- Atomic rollback leaves Mission, XP, streak, receipt, state, and analytics mutually consistent.

### Compose UI tests

- Full English success flow.
- Full Persian RTL success flow.
- Area default, selected, loading, error, resume, offline, and double-tap states.
- Process death after every committed state.
- Back behavior before and after valuable input or Mission creation.
- Safe-exit path.
- Completed-without-scheduling path.
- TalkBack semantics and focus order.
- 200% font scaling with no clipping or overlap.
- Reduced Motion reward.

### Integration tests

- Real Mission, Hunter XP, Skill XP, streak, and scheduling persistence.
- Duplicate create, complete, and schedule requests return prior results.
- Analytics remains local while consent is Unknown.
- Grant uploads queued events; decline deletes them and blocks new product events.
- Offline completion and later analytics upload.

## 15. Acceptance Criteria

- A fresh user can reach first value in under 90 seconds in moderated testing.
- Setup, account, name, notifications, and RPG lore do not block first value.
- Area shows `Step 1 of 4`, Action shows `Step 2 of 4`, Do shows `Step 3 of 4`, and Next shows `Step 4 of 4`.
- Reward and Handoff have no numbered progress label.
- No preselected Area exists and Continue is disabled until selection.
- Area taps are draft-only; `goal_selected` occurs only after Room commit.
- A process death at any committed state resumes correctly.
- Profile creation mid-flow cannot bypass First Win.
- Duplicate taps cannot duplicate Missions, XP, streak, rewards, or schedules.
- Completion, XP, streak, receipt, state, and causal local analytics commit atomically.
- Existing established users bypass First Win.
- The standard kill switch affects new assignment only.
- Product analytics never uploads without Granted consent.
- English, Persian, RTL, TalkBack, 200% font scale, offline, and Reduced Motion paths pass.
- The legacy onboarding route remains available for rollback.

## 16. Design Decision

Proceed with the instrumented vertical slice. Do not begin the Today, navigation, unlock, social, billing, Body Map, Finance, or advanced AI workstreams until this slice produces trustworthy activation and first-value evidence.
