package com.axiom.app.domain.analytics

/**
 * Domain orchestrator governing the canonical release rings per AXIOM Upgrade Master Plan & Gate G4.
 *
 * Canonical Ring Progression:
 * 1. INTERNAL: 3–5 users for fresh install, recovery, wording, and zero S1.
 * 2. ALPHA: 8–12 target users for First-Win usability, >=80% unassisted completion, TTFV <3 min.
 * 3. CONCIERGE: 20–30 users for 21 days with semi-manual support to test value/problem truth.
 * 4. CLOSED_BETA: 50–100 users for 30–60 days without manual retention intervention.
 * 5. PRODUCTION: Post-G6 monetization and retention proof.
 */
object ReleaseRingOrchestrator {

    data class RingDefinition(
        val ring: ReleaseRing,
        val minUsers: Int,
        val maxUsers: Int,
        val minDurationDays: Int,
        val description: String
    )

    val RINGS: Map<ReleaseRing, RingDefinition> = mapOf(
        ReleaseRing.INTERNAL to RingDefinition(
            ring = ReleaseRing.INTERNAL,
            minUsers = 3,
            maxUsers = 5,
            minDurationDays = 3,
            description = "Fresh install, recovery, wording, and zero S1"
        ),
        ReleaseRing.ALPHA to RingDefinition(
            ring = ReleaseRing.ALPHA,
            minUsers = 8,
            maxUsers = 12,
            minDurationDays = 7,
            description = "First-Win usability, >=80% unassisted completion, TTFV <3 min"
        ),
        ReleaseRing.CONCIERGE to RingDefinition(
            ring = ReleaseRing.CONCIERGE,
            minUsers = 20,
            maxUsers = 30,
            minDurationDays = 21,
            description = "21 days semi-manual support to test value/problem truth"
        ),
        ReleaseRing.CLOSED_BETA to RingDefinition(
            ring = ReleaseRing.CLOSED_BETA,
            minUsers = 50,
            maxUsers = 100,
            minDurationDays = 30,
            description = "30–60 days natural retention and WMPU proof without intervention"
        ),
        ReleaseRing.PRODUCTION to RingDefinition(
            ring = ReleaseRing.PRODUCTION,
            minUsers = 100,
            maxUsers = Int.MAX_VALUE,
            minDurationDays = 60,
            description = "General availability post-G6 monetization proof"
        )
    )

    data class RingEvaluation(
        val currentRing: ReleaseRing,
        val nextRing: ReleaseRing?,
        val canAdvance: Boolean,
        val blockers: List<String>
    )

    /**
     * Evaluates whether a cohort meets all criteria to advance from [currentRing] to the next ring.
     */
    fun evaluateRingAdvancement(
        currentRing: ReleaseRing,
        activeUsers: Int,
        durationDays: Int,
        s1Defects: Int,
        fmcRate: Float = 1.0f,
        ttfvMinutes: Float = 2.0f
    ): RingEvaluation {
        val def = RINGS[currentRing] ?: return RingEvaluation(currentRing, null, false, listOf("Unknown ring"))
        val blockers = mutableListOf<String>()

        // Zero tolerance for S1 defects across all rings
        if (s1Defects > 0) {
            blockers.add("S1 defects present: $s1Defects (zero tolerance)")
        }

        // Participant count bounds
        if (activeUsers < def.minUsers) {
            blockers.add("Active users ($activeUsers) below minimum (${def.minUsers})")
        }

        // Duration check
        if (durationDays < def.minDurationDays) {
            blockers.add("Cohort duration ($durationDays days) below minimum (${def.minDurationDays} days)")
        }

        // Ring-specific criteria
        when (currentRing) {
            ReleaseRing.INTERNAL -> {
                // Internal requires clean recovery and zero S1
            }
            ReleaseRing.ALPHA -> {
                if (fmcRate < 0.80f) {
                    blockers.add("FMC rate (${"%.1f".format(fmcRate * 100)}%) below Alpha threshold (80.0%)")
                }
                if (ttfvMinutes >= 3.0f) {
                    blockers.add("TTFV (${"%.1f".format(ttfvMinutes)}m) must be under 3.0 minutes")
                }
            }
            ReleaseRing.CONCIERGE -> {
                // 21-day observation required
            }
            ReleaseRing.CLOSED_BETA -> {
                // 30-day minimum observation required
            }
            ReleaseRing.PRODUCTION -> {
                blockers.add("Already at production ring")
            }
        }

        val nextRing = when (currentRing) {
            ReleaseRing.INTERNAL -> ReleaseRing.ALPHA
            ReleaseRing.ALPHA -> ReleaseRing.CONCIERGE
            ReleaseRing.CONCIERGE -> ReleaseRing.CLOSED_BETA
            ReleaseRing.CLOSED_BETA -> ReleaseRing.PRODUCTION
            ReleaseRing.PRODUCTION -> null
        }

        return RingEvaluation(
            currentRing = currentRing,
            nextRing = nextRing,
            canAdvance = blockers.isEmpty() && nextRing != null,
            blockers = blockers
        )
    }
}
