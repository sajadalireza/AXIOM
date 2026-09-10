# Gate G2 — Alpha Usability Testing Protocol & Measurement Kit

**Document Version:** 1.0.0  
**Target Slice:** First-Win Vertical Slice (WP-201 through WP-208)  
**Associated Issues:** #30 (WP-207), #31 (WP-208), #32 (WP-209)  
**Baseline Commit:** `3ce343c9f0ccf6a408889d90bfe9c94b77691c68`  
**Compliance Standard:** AXIOM Execution Workflow & Product Constitution (Zero Fabricated User Evidence)

---

## 1. Objective & Success Criteria

### Objective
Evaluate the First-Win user onboarding journey with 8 to 12 real target users to measure initial value comprehension, time-to-first-value (TTFV), unassisted completion rate, and accessibility under natural physical conditions.

### Must-Acceptance Thresholds
1. **Unassisted Completion Rate:** $\ge 80\%$ of participants complete the entire First-Win journey (`Setup -> Area -> Action -> Do -> Reward -> Next -> Handoff -> Home`) without moderator intervention.
2. **Time-To-First-Value (TTFV):**
   - Median TTFV: $< 3.0\text{ minutes}$ (180 seconds).
   - 80th Percentile TTFV: $< 5.0\text{ minutes}$ (300 seconds).
3. **Defect Thresholds:**
   - **S0 Defects (Blocker / Crash / Data Loss):** Exactly `0`.
   - **S1 Defects (Major Usability Blocker preventing unassisted progress):** Exactly `0`.
   - **S2 Defects (Minor friction / non-blocking confusion):** $\le 3$ with documented repair packets.

---

## 2. Participant Recruitment Profile

Target Cohort Size: **8 to 12 participants**

| Parameter | Specification | Rationale |
|---|---|---|
| **Target Persona** | Knowledge workers, creators, and students seeking habit or focus consistency. | Represents core target audience of AXIOM. |
| **Language & Locale** | Native Persian (Farsi) speakers; comfortable reading Persian UI. | Validates RTL layout, typography, and phrasing. |
| **Device Diversity** | 50% Android 12–14 standard phone, 25% small-screen / high-density device, 25% large phone. | Validates dynamic Compose layouts and scroll view behaviors. |
| **Accessibility Sub-Cohort** | At least 2 participants utilizing accessibility settings (one TalkBack/screen reader user, one 150%–200% font scale user). | Validates WCAG 2.1 AA, touch targets, and content accessibility. |
| **Prior Familiarity** | Zero prior exposure to AXIOM codebase or internal prototypes. | Simulates genuine first-launch cognitive conditions. |

---

## 3. Test Environment & Apparatus

1. **Hardware / Device:**
   - Physical Android device running Android 11+ with screen recording enabled.
   - For TalkBack sessions: TalkBack screen reader active, audio output recorded.
2. **Software Build:**
   - Clean debug or release APK built from canonical commit `3ce343c9f0ccf6a408889d90bfe9c94b77691c68`.
   - Initial device state: Fresh install or `adb shell pm clear com.axiom.app` to guarantee clean first launch.
3. **Observation Recording:**
   - Moderator observation log (timing, verbal feedback, touch hesitations).
   - Device video recording of screen interactions and touch indicators.

---

## 4. Moderator Protocol & Scripts

### Step 0: Pre-Session Briefing (2 minutes)
> *"Welcome, and thank you for taking the time to try AXIOM today. We are testing a new mobile application designed to help people build focus and accomplish meaningful personal missions.*
> 
> *Please keep in mind that we are testing the application, not you. There are no right or wrong answers. If you feel stuck, confused, or frustrated at any point, that is completely normal and represents valuable feedback for our design.*
> 
> *We ask you to 'think aloud' as much as possible—tell us what you are looking at, what you expect to happen, and why you choose to tap a button. We will record your screen and audio for research purposes. Whenever you are ready, please open the AXIOM app on the device in front of you."*

### Step 1: Journey Flow & Unassisted Observation
The user progresses through the 8 stages of the First-Win journey. The moderator observes passively without guiding or prompting:

```text
[Splash / Language Setup]
       │
       ▼
[Life Area Selection]
       │
       ▼
[First Action Selection]
       │
       ▼
[Action Execution (Do Step)]
       │
       ▼
[Reward & Reflection]
       │
       ▼
[Schedule Next Action]
       │
       ▼
[Handoff to Home]
       │
       ▼
[Home Dashboard]
```

### Moderator Intervention Rule
- **Level 0 (No Assistance):** User navigates and completes independently.
- **Level 1 (Clarifying Prompt):** Moderator asks: *"What are you thinking right now?"* or *"What do you see on the screen?"* (Does NOT count as assistance).
- **Level 2 (Directive Hint):** Moderator points out a button or explains a concept because user has been stuck for > 60 seconds. (Recorded as **Assisted Completion**; counts against unassisted completion metric).
- **Level 3 (Fatal Failure):** Application crashes, errors out, or user abandons session. (Recorded as **Incomplete / S0 or S1 defect**).

### Step 2: Post-Journey Debrief (3 minutes)
1. *"In your own words, what did you just accomplish in the app?"*
2. *"Did anything feel confusing, slow, or unexpected?"*
3. *"How easy or difficult was it to complete your first action?"*
4. *"Rate your overall experience from 1 (very difficult/confusing) to 5 (effortless/delightful)."*

---

## 5. Telemetry & Observation Logging Template

For each participant, complete the following scorecard:

```markdown
### Participant Session: P-[01..12]
- Date & Time: YYYY-MM-DD HH:MM
- Device Model: [e.g., Pixel 7, Galaxy S22]
- Android OS Version: [e.g., Android 14]
- Accessibility Configuration: [Default / 200% Font Scale / TalkBack Active]
- Moderator: [Name]

#### Timing & Milestones
- T0 (App Opened): 00:00
- T1 (Area Selected): __:__
- T2 (Action Selected): __:__
- T3 (Action Completed / Reward Screen): __:__  [TTFV = T3 - T0]
- T4 (Next Action Scheduled): __:__
- T5 (Home Landed): __:__  [Total Journey Duration = T5 - T0]

#### Metrics
- Unassisted Completion: [YES / NO]
- TTFV: __ seconds (Target: < 180s)
- Total Duration: __ seconds (Target: < 300s)
- Moderator Interventions: [0 / 1 / 2+]

#### Observations & Quotes
- Confusion points:
- Verbal comments:
- UX friction notes:
```

---

## 6. Findings Classification Matrix (Taxonomy)

| Severity | Definition | Gate Impact |
|---|---|---|
| **S0** | Blocker: Crash, data corruption, process termination, unrecoverable loop. | **GATE HARD STOP (FAIL)** |
| **S1** | Critical Usability: User cannot understand or proceed without explicit moderator guidance. | **GATE BLOCKER (Requires Repair Packet)** |
| **S2** | Minor Friction: User hesitated or expressed confusion, but completed unassisted. | Allowed ($\le 3$) with documented carry-forward. |
| **S3** | Cosmetic / Polish: Visual alignment, typography nuance, minor wording tweak. | Non-blocking (Backlog). |

---

## 7. Execution Status

- **Protocol & Measurement Instruments:** `COMPLETE & READY FOR LIVE EXECUTION`
- **Live Cohort Sessions:** `PENDING PHYSICAL RECRUITMENT & MODERATED SESSIONS`
- **Product Gate Status:** `BLOCKED ON LIVE COHORT EXECUTION` (Strict compliance with *"Never fabricate user evidence"*).
