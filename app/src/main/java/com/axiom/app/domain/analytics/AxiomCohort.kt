package com.axiom.app.domain.analytics

/**
 * Release rings and cohort attribution per AXIOM Upgrade Master Plan & Gate G4.
 *
 * Release rings:
 * - INTERNAL: 3–5 internal users for fresh install, recovery, and crash-free verification.
 * - ALPHA: 8–12 users for First-Win usability, >=80% unassisted completion, TTFV <3 min.
 * - CONCIERGE: 20–30 users for 21 days with semi-manual support to validate value and problem truth.
 * - CLOSED_BETA: 50–100 users for 30–60 days without manual retention intervention.
 * - PRODUCTION: General availability ring post-G6 monetization proof.
 */
enum class ReleaseRing {
    INTERNAL,
    ALPHA,
    CONCIERGE,
    CLOSED_BETA,
    PRODUCTION
}

/**
 * Non-PII cohort and runtime build context for telemetry attribution.
 */
data class CohortContext(
    val ring: ReleaseRing = ReleaseRing.ALPHA,
    val appBuild: String = "canonical-beta-1.0.0",
    val schemaVersion: Int = 18
)
