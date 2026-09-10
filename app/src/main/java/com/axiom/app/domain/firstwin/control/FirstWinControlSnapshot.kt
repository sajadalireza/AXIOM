package com.axiom.app.domain.firstwin.control

/**
 * WP-208 — immutable snapshot of First-Win control plane state.
 */
data class FirstWinControlSnapshot(
    val eligibilityVersion: Int,
    val assignedVariant: FirstWinVariant,
    val isLocalKilled: Boolean,
    val isRemoteKilled: Boolean,
    val isTreatmentActive: Boolean,
    val assignmentTimestamp: Long,
)
