# ADR-0002 — Fail-closed direct-Gemini egress boundary

- **Status:** Accepted (as-built; WP-104 SEC-104-001, documented in WP-106)
- **Context ref:** `ARCHITECTURE.md` §13; `docs/security/WP-104_SECURITY_AUDIT.md`

## Context

The app can talk to Google Gemini directly with a user-supplied (BYO) key for the "SYSTEM voice"
coach, structured mission generation, and the weekly AI summary. A direct client→provider path
risks silently egressing user data (including identity) to Google. WP-104 found one ungated call
site (`WeeklyAnalyticsViewModel`) and closed it.

## Decision (as-built)

Every direct-Gemini entry point is gated so a default build performs **zero** provider
construction or network contact:

- `core/FeatureFlags.DIRECT_GEMINI_EGRESS_ENABLED = false` (default; the master switch).
- `core/ai/AiEgressPolicy.requireDirectGeminiAllowed()` throws
  `DirectAiEgressDisabledException` **before any key lookup or network call**.
- `core/ai/DirectGeminiGateway.withDirectGemini(key) { … }` is the single choke point — the
  provider lambda runs only when egress is allowed **and** a non-blank key exists, else returns
  `null` and the caller falls back to local content.
- Outbound prompt context (`AiPromptContext.hunterContext`) is numeric stats only; **hunter
  name/identity is excluded**.

## Consequences

- **Fail-closed by construction:** an accidental new call site that forgets to gate still cannot
  egress unless it also flips the compile-time flag. Verified: **0 ungated production call sites**.
- The default/production build is offline-safe for AI; features degrade to local fallback text.
- **[DEFERRED]** the direct BYO-key path is a temporary containment; a server-gateway / Firebase
  App Check migration is postponed to a later work packet.
- The Supabase `functions/v1/extract-blueprint` Edge Function is a separate, server-side AI path
  outside this policy's scope.
