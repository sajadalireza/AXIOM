package com.axiom.app.domain.progress.ledger

/**
 * Anti-farming and progression integrity policy for the Progress Ledger.
 *
 * Enforces:
 * 1. Zero / Negative Duration Rejection: Prevents zero-second completion spam.
 * 2. Rapid Duplicate Suppression: Prevents rapid duplicate completions of the same mission within cooldown.
 * 3. Velocity Limits: Restricts burst execution rates.
 * 4. Realistic Capability Hours: Prevents impossible practice hour claims (> 24h per entry or <= 0).
 * 5. XP Saturation: Prevents automated XP farming attacks.
 * 6. Progression Decoupling: Validates that rank advancement cannot be achieved through raw XP alone.
 */
class ProgressAntiFarmingPolicy(
    val minDuplicateIntervalMs: Long = 60_000L, // 1 minute duplicate cooldown
    val maxCompletionsPerMinute: Int = 3,
    val maxEffectiveHoursPerEntry: Double = 24.0,
    val maxXpPerHour: Long = 15_000L
) {

    sealed interface AntiFarmingViolation {
        data class ZeroOrNegativeDuration(val durationMinutes: Double) : AntiFarmingViolation
        data class RapidDuplicateSubmission(val missionId: String, val intervalMs: Long) : AntiFarmingViolation
        data class VelocityLimitExceeded(val count: Int, val windowMs: Long) : AntiFarmingViolation
        data class InvalidCapabilityHours(val hours: Double) : AntiFarmingViolation
        data class XpSaturationExceeded(val currentWindowXp: Long, val maxXpAllowed: Long) : AntiFarmingViolation
    }

    sealed interface ValidationResult {
        object Valid : ValidationResult
        data class Rejected(val violation: AntiFarmingViolation, val reason: String) : ValidationResult
    }

    /**
     * Validates an incoming [LedgerEntry] against the existing history.
     */
    fun validate(entry: LedgerEntry, history: List<LedgerEntry>): ValidationResult {
        return when (entry) {
            is LedgerEntry.ExecutionEntry -> validateExecution(entry, history)
            is LedgerEntry.CapabilityEntry -> validateCapability(entry, history)
            is LedgerEntry.OutcomeEntry -> validateOutcome(entry, history)
        }
    }

    private fun validateExecution(
        entry: LedgerEntry.ExecutionEntry,
        history: List<LedgerEntry>
    ): ValidationResult {
        // 1. Zero or negative duration check
        if (entry.durationMinutes <= 0.0) {
            return ValidationResult.Rejected(
                violation = AntiFarmingViolation.ZeroOrNegativeDuration(entry.durationMinutes),
                reason = "Execution entry rejected: duration (${entry.durationMinutes}m) must be greater than zero."
            )
        }

        val executions = history.filterIsInstance<LedgerEntry.ExecutionEntry>()

        // 2. Rapid duplicate submission check
        val lastSameMission = executions
            .filter { it.missionId == entry.missionId }
            .maxByOrNull { it.timestamp }

        if (lastSameMission != null) {
            val interval = entry.timestamp - lastSameMission.timestamp
            if (interval in 0 until minDuplicateIntervalMs) {
                return ValidationResult.Rejected(
                    violation = AntiFarmingViolation.RapidDuplicateSubmission(entry.missionId, interval),
                    reason = "Execution entry rejected: rapid duplicate mission ${entry.missionId} submitted within ${interval}ms (< ${minDuplicateIntervalMs}ms)."
                )
            }
        }

        // 3. Velocity check (max completions in 1 minute window)
        val oneMinuteAgo = entry.timestamp - 60_000L
        val recentCompletions = executions.count { it.timestamp in oneMinuteAgo..entry.timestamp }
        if (recentCompletions >= maxCompletionsPerMinute) {
            return ValidationResult.Rejected(
                violation = AntiFarmingViolation.VelocityLimitExceeded(recentCompletions, 60_000L),
                reason = "Execution entry rejected: execution velocity exceeded ($recentCompletions completions in past 60s, max $maxCompletionsPerMinute)."
            )
        }

        return ValidationResult.Valid
    }

    private fun validateCapability(
        entry: LedgerEntry.CapabilityEntry,
        history: List<LedgerEntry>
    ): ValidationResult {
        // 1. Practice hours sanity check
        if (entry.effectiveHoursGained <= 0.0 || entry.effectiveHoursGained > maxEffectiveHoursPerEntry) {
            return ValidationResult.Rejected(
                violation = AntiFarmingViolation.InvalidCapabilityHours(entry.effectiveHoursGained),
                reason = "Capability entry rejected: effective hours (${entry.effectiveHoursGained}h) out of realistic bounds (0..$maxEffectiveHoursPerEntry)."
            )
        }

        // 2. XP saturation check
        val capabilities = history.filterIsInstance<LedgerEntry.CapabilityEntry>()
        val oneHourAgo = entry.timestamp - 3_600_000L
        val recentXpSum = capabilities
            .filter { it.timestamp in oneHourAgo..entry.timestamp }
            .sumOf { it.xpAwarded }

        if (recentXpSum + entry.xpAwarded > maxXpPerHour) {
            return ValidationResult.Rejected(
                violation = AntiFarmingViolation.XpSaturationExceeded(recentXpSum + entry.xpAwarded, maxXpPerHour),
                reason = "Capability entry rejected: XP saturation reached (${recentXpSum + entry.xpAwarded} XP in 1 hour, max $maxXpPerHour)."
            )
        }

        return ValidationResult.Valid
    }

    private fun validateOutcome(
        entry: LedgerEntry.OutcomeEntry,
        history: List<LedgerEntry>
    ): ValidationResult {
        // Basic sanity checks: progress delta must not be negative
        if (entry.progressDeltaPercent < 0f) {
            return ValidationResult.Rejected(
                violation = AntiFarmingViolation.ZeroOrNegativeDuration(entry.progressDeltaPercent.toDouble()),
                reason = "Outcome entry rejected: progressDeltaPercent must be non-negative."
            )
        }
        return ValidationResult.Valid
    }

    /**
     * Verifies that candidate rank advancement is supported by both Capability hours and Goal milestones,
     * enforcing the Product Constitution rule: "Goal Progress is more important than XP".
     */
    fun isRankAdvancementLegitimate(
        candidateRank: ProgressRank,
        effectiveHours: Double,
        milestonesAchieved: Int,
        rawXP: Long
    ): Boolean {
        return candidateRank.isEligible(effectiveHours, milestonesAchieved, rawXP)
    }
}
