# AXIOM CI — Pull-Request Validation Baseline (WP-105)

Every pull request (and every push to `main`) runs `.github/workflows/ci.yml`. It
proves the code compiles, tests pass, lint is clean, a debug APK assembles, and the
Room schema is committed — **without any signing or backend secrets**.

For local environment setup (JDK, Android SDK, wrapper policy) see
[`DEVELOPMENT.md`](../DEVELOPMENT.md). This document is the CI-specific triage guide;
it intentionally does not duplicate setup instructions.

## Required checks

| Check name | Gradle command | What it guards |
|------------|----------------|----------------|
| `Unit Tests` | `./gradlew testDebugUnitTest --stacktrace --no-daemon` | JVM unit tests (incl. WP-104 security regressions) |
| `Lint` | `./gradlew lintRelease --stacktrace --no-daemon` | Android lint on the release variant |
| `Assemble Debug` | `./gradlew assembleDebug --stacktrace --no-daemon` | Debug APK compiles and links |
| `Room Schema` | `./gradlew :app:kspDebugKotlin` + `git status --porcelain -- app/schemas` | Committed Room schema matches generated output |

These four job names are the stable status contexts used for branch protection.

## Local reproduction

Run the exact CI commands locally before pushing:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"   # if not auto-discovered
./gradlew testDebugUnitTest --stacktrace --no-daemon
./gradlew lintRelease --stacktrace --no-daemon
./gradlew assembleDebug --stacktrace --no-daemon
./gradlew :app:kspDebugKotlin --stacktrace --no-daemon && git status --porcelain -- app/schemas
```

An empty `git status` for `app/schemas` means the schema check would pass.

## Requirements

- JDK 17 (Temurin in CI; AGP 9.1.1 baseline).
- Gradle via the checked-in wrapper only (distribution `9.3.1`, SHA-256 pinned).
- Android SDK `platforms;android-36` + `build-tools;36.0.0` (CI installs these via `sdkmanager`).
- **No signing keystore, no Supabase/Gemini/Google secrets.** The debug/test/lint
  paths tolerate empty config: an absent `SUPABASE_KEY` classifies as `EMPTY`
  (client-safe) and direct Gemini egress is disabled by default.

## Common failure categories

- **Unit Tests red:** a real product/test regression. Reproduce with the local
  command above; read the uploaded `unit-test-reports` artifact
  (`app/build/reports/tests/testDebugUnitTest`). Never mask with `continue-on-error`.
- **Lint red:** a new lint error on the release variant. Inspect the `lint-report`
  artifact (`lint-results-release.html`).
- **Assemble Debug red:** compilation/resource/link failure. Reproduce locally;
  check for a missing Android SDK component if it is environment-specific.
- **Room Schema red:** a Room entity/DAO/migration change regenerated the exported
  schema but the new `app/schemas/**` file was not committed. Remediation:

  ```bash
  ./gradlew :app:kspDebugKotlin --no-daemon
  git add app/schemas
  git commit -m "chore: update Room exported schema"
  ```

  Investigate before committing: a schema change without a matching
  `AxiomDatabase` version bump + migration is a data-safety bug, not a formatting nit.

## CI vs. release

- **CI (`ci.yml`)** — this workflow. Triggers on `pull_request` and `push: main`.
  Read-only permissions, no secrets, no signing. Its four checks gate merges.
- **Release (`release.yml`)** — separate. Triggers on `push: main` and manual
  dispatch. Consumes signing + backend secrets to build/sign release artifacts.
  It is **not** a PR check and is **not** a required merge gate. WP-105 does not
  modify its signing behavior.

## Inspecting a run

1. Open the PR → **Checks** tab, or the repo **Actions** tab → **CI**.
2. Pick the run for your head SHA; open the failing job.
3. Expand the failing step for the log; download artifacts from the run summary.
4. Reproduce locally with the matching command above.
