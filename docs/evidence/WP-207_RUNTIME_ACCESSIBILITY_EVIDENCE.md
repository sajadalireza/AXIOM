# WP-207 Runtime & Accessibility Evidence Index

## Status

`EVIDENCE UPDATE — WP-207 REMAINS OPEN / PR #48 REMAINS DRAFT`

This index records executed evidence for WP-207. It does **not** claim G2 closure, alpha-user acceptance, median Time-to-First-Value acceptance, or independent-review completion.

- Issue: `#30 — WP-207 — First-Win UI & Accessibility`
- Pull request: `#48`
- Code under test for the current large-font repair: `66a2926327d01e176ff5f98f45aed3c745ca7ff0`
- Parent First-Win runtime head: `ff1fadc6a7d3b75f299e841dfff0d99f3c6a9ca3`
- APK SHA-256: `905b6e36083f069a8146ab1d959c297013b393cf4d678765090d53e41a5c8f78`

## CI / RED → GREEN Evidence

### Launch-policy integration

- RED head: `8abb69c82ee179aabf4fecaee2ec734c16f9fdf6`
- RED CI run: `33210347242`
- Expected failure: Unit Tests failed because Splash did not yet apply the durable First-Win launch policy.
- Repair / GREEN head: `ff1fadc6a7d3b75f299e841dfff0d99f3c6a9ca3`
- GREEN CI run: `33210809065`
- Result: Unit Tests, Lint, Assemble Debug, and Room Schema all passed.

### Setup 200% font-scale repair

- RED head: `f6683d4691e7439055ebc979205d67e86f29d610`
- RED CI run: `33214902836`
- Executed result: Unit Tests `FAILURE`, Room Schema `SUCCESS`, Lint `SUCCESS`; the overall run later became `cancelled` after the repair push, but the load-bearing Unit Tests failure had already completed.
- Repair head: `66a2926327d01e176ff5f98f45aed3c745ca7ff0`
- GREEN CI run: `33215338915`
- Result: Unit Tests `SUCCESS`, Lint `SUCCESS`, Assemble Debug `SUCCESS`, Room Schema `SUCCESS`.

The repair removed the fixed 52dp Setup CTA height and retained a 52dp minimum while allowing two-line wrapped text with centered padding.

## Executed Runtime Evidence

### English functional slice — parent First-Win runtime head

Executed on `ff1fadc6a7d3b75f299e841dfff0d99f3c6a9ca3` using the CI-produced debug APK.

Observed:

- `Setup → Area → Action → Do → Reward → Next → Handoff → Home`
- real Mission creation and completion
- force-stop / restart resume at durable First-Win positions
- mission completion while emulator networking was actually unavailable (`Network is unreachable`)
- post-completion relaunch routed to Home rather than replaying First-Win
- Home reflected earned progress

### Persian RTL + 200% font + reduced motion — current repair head

Executed on `66a2926327d01e176ff5f98f45aed3c745ca7ff0` using the exact CI-produced APK.

Environment:

- language: Persian
- font scale: `2.0`
- window / transition / animator scales: `0 / 0 / 0`
- test AVD: `warrior_test`

Observed:

- repaired Setup CTA rendered as a multi-line control instead of clipping
- `Area → Action → Do → Reward → Next → Handoff` remained usable in Persian RTL at 200% font
- Handoff CTA opened Home
- after completion, cold relaunch produced no First-Win markers and eventually exposed Home-specific signals (`HUNTER` / rank-progress UI)
- no crash was observed during the 200% cold relaunch

A cold AVD Home render at 200% was visibly delayed on one run. The exact latency was **not instrumented**, so this observation is not promoted into a TTFV result.

## TalkBack Evidence

Behavioral TalkBack traversal was executed on parent head `ff1fadc6a7d3b75f299e841dfff0d99f3c6a9ca3`:

- Android Accessibility Suite TalkBack service enabled
- `accessibility_enabled=1`
- `touch_exploration_enabled=1`
- real TalkBack focus advanced across the First-Win Step 1 content
- focus reached Area choices and the primary `Continue` CTA
- double-tap activation advanced the journey to Step 2

The only source changes from that parent head through `66a2926327d01e176ff5f98f45aed3c745ca7ff0` are:

- `app/src/main/java/com/axiom/app/presentation/setup/LanguageThemeSetupScreen.kt`
- `app/src/test/java/com/axiom/app/presentation/setup/SetupFontScaleContractTest.kt`

No First-Win source changed in that interval. Therefore the parent-head TalkBack run is relevant non-regression evidence, but it is **not represented as an exact-head TalkBack runtime re-run**. Exact-head TalkBack re-execution remains a conservative open item.

## Controlled Visual / Video Artifacts

Binary evidence is retained as an external controlled packet, consistent with the repository's existing recovery-evidence storage model.

Controlled packet identity:

`wp-207-evidence/66a2926327d01e176ff5f98f45aed3c745ca7ff0/`

| Artifact | SHA-256 |
|---|---|
| `01-setup-fa-200pct-repaired.png` | `6494491fa61e78e1ed3dee735142fa08809303617695d7465abad4860d0d8731` |
| `02-first-win-area-fa-200pct.png` | `c4a9188a0d51e050b53875899b2248e6b5e5bc7a8ad21004d0513995f76bf42e` |
| `03-first-win-action-fa-200pct.png` | `5bda19aaff177ba7c137a65337d3fa0547fc5cab8ce7d500fcdf6d03ef959b8d` |
| `04-first-win-do-fa-200pct.png` | `4d638dbd045961bb63b0945ef007b96a45cedd3689687ca5146c2195170d2f83` |
| `05-first-win-reward-fa-200pct.png` | `e69b67c314da7db51878eea610bdbe60750d8be8a9b1d01969e37c64acd87c04` |
| `06-first-win-next-fa-200pct.png` | `ea56d116d43e4015f9321bed429b01ecc0e8d863304f55355aba9e9ca869dfff` |
| `07-first-win-handoff-fa-200pct.png` | `013ea28012dea2ae50bec8d5cbdfcf2195342782233d93827c5e12590dcac732` |
| `08-home-fa-200pct-out-of-scope.png` | `1f2100c6d01724380dcabe6cfb9c5c3304b07a50672a4ad49a1e57e47e583ebb` |
| `09-first-win-fa-200pct-runtime.mp4` | `a5ba909dbfe7876793fb75b7794216e75096aa90d232b1745ae70c1893007c9f` |
| `10-cold-relaunch-home-fa-200pct-out-of-scope.png` | `3b9178620773ed3a172a5cbd44921835993a5803208d2d097ecf523543510fec` |

Video metadata:

- duration: `88.827656 s`
- size: `440,957 bytes`

## Scope Boundary / Known Follow-up

The legacy Home screen shows substantial layout overlap at 200% font. WP-207 explicitly declares **Home redesign** a non-goal, so this evidence does not silently expand WP-207 into a Home redesign packet.

This also means no claim is made that AXIOM as a whole is 200%-font accessible. The bounded claim is only that the WP-207 Setup / First-Win journey through Handoff is usable at 200% after the current repair, and that Handoff reaches Home.

## Remaining G2 Gates

WP-207 must remain open until the remaining load-bearing evidence is satisfied:

- exact-head TalkBack re-execution (conservative open item)
- alpha-user completion: at least 80% without assistance, **or** an explicit repair packet
- measured median Time to First Value below three minutes
- four required independent reviews / repair loop
- final Checkpoint Capsule and weighted acceptance score >= 9.5 with no active Hard Cap

No merge or issue closure is authorized by this evidence index alone.
