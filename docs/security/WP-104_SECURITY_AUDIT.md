# WP-104 — Secret & Artifact Audit (Security Implementation)

Baseline: `main` at merge `e4ae339…` (app tree `23fe2f97…`). This document records the
WP-104 findings and their resolution. It contains **no secret values** — only finding
IDs, classes, and fingerprints.

## Findings & resolution

| ID | Sev | Finding | Status | Resolution |
|----|-----|---------|--------|------------|
| SEC-104-001 | S2 | Direct client→Gemini egress + user PII (name) in prompt, no boundary | RESOLVED (contained) | `AiEgressPolicy` default-disabled/fail-closed guard on every direct-Gemini entry point; `hunter.name` removed from outbound context |
| SEC-104-002 | S1* | Backend key baked into APK; build accepted `service_role` | RESOLVED | `SupabaseKeyPolicy` classifier + fail-closed build guard (`assertSupabaseClientKeySafe`); only `sb_publishable_*`/legacy `anon` (or empty) permitted; `service_role`/`sb_secret_`/malformed rejected at build |
| SEC-104-003 | S3 | Gemini key stored as plaintext DataStore | RESOLVED | `GeminiKeyStore`/`AndroidGeminiKeyStore` (AndroidKeyStore AES/GCM; ciphertext in `noBackupFilesDir`); one-time fail-safe migration removes legacy plaintext |
| SEC-104-004 | S3 | Debug HTTP `Level.BODY` logging exposed anon key/payloads | RESOLVED | `SupabaseClient` → `Level.BASIC` (debug) / `NONE` (release) + `redactHeader("Authorization"/"apikey")` |
| SEC-104-005 | S3 | `.gitignore` lacked credential-artifact patterns | RESOLVED | added `*.keystore`,`*.p12`,`*.pfx`,`*.pem`,`*.key`,`google-services.json`,`.env.*` (preserving `.env.example`) |

\* SEC-104-002 severity was conditional on whether a `service_role` key was ever injected; the build guard now makes that impossible for a client build.

## Product Owner decisions applied
- **Gemini (Decision A):** smallest bounded containment — direct path default-disabled, fail-closed, PII-free; no gateway built.
- **Supabase (Decision B):** client builds may contain only `sb_publishable_*` or legacy `anon`; build fails closed on `sb_secret_*`/`service_role`/unknown.

## Validation (JDK 17.0.20, Gradle 9.3.1)
`./gradlew --version | tasks | testDebugUnitTest | lintRelease | assembleDebug` — results in
`wp-104-local-evidence/impl/` (out-of-repo). Unit tests cover the Supabase key policy
(publishable/anon allowed; secret/service_role/malformed rejected; error redacted), the
default-disabled Gemini egress, and prompt name-exclusion. AndroidKeyStore round-trip and
migration are covered by an instrumentation test (`connectedDebugAndroidTest`).

## Residual risks / owner actions still required
- **RLS EXTERNAL VERIFICATION REQUIRED** — client-key safety ≠ database RLS safety. Confirm Row-Level Security is enforced on all Supabase tables before release. (Not verifiable from repo.)
- **Production Supabase key class: NOT VERIFIED** locally (no `.env` present). The build guard enforces client-safe classes at build time; owner must ensure the production build injects an `anon`/`sb_publishable_` key (rotate if a `service_role`/`sb_secret_` key was ever shipped).
- **Direct BYO Gemini architecture is temporary** — a future approved server boundary / Firebase App Check migration remains deferred.

No secret value is stored in this document or in the evidence package.
