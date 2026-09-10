package com.axiom.app.data.repository

import com.axiom.app.core.FeatureFlags
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.firstwin.control.FirstWinControlPlane
import com.axiom.app.domain.firstwin.control.FirstWinControlSnapshot
import com.axiom.app.domain.firstwin.control.FirstWinVariant
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirstWinControlPlaneImpl @Inject constructor(
    private val preferences: AxiomPreferences,
) : FirstWinControlPlane {

    override suspend fun isLocalKilled(): Boolean = FeatureFlags.FIRST_WIN_LOCAL_KILL

    override suspend fun isRemoteKilled(): Boolean = preferences.firstWinRemoteKillFlow.first()

    override suspend fun getOrAssignVariant(
        defaultVariant: FirstWinVariant,
    ): FirstWinVariant {
        val storedRaw = preferences.firstWinVariantFlow.first()
        val existing = storedRaw?.let { raw ->
            runCatching { FirstWinVariant.valueOf(raw) }.getOrNull()
        }
        if (existing != null) {
            return existing
        }

        // Fresh assignment: persist sticky variant, eligibility version, and timestamp
        val assigned = defaultVariant
        val now = System.currentTimeMillis()
        preferences.setFirstWinVariantAssignment(
            variant = assigned.name,
            eligibilityVersion = FirstWinControlPlane.CURRENT_ELIGIBILITY_VERSION,
            timestamp = now,
        )
        return assigned
    }

    override suspend fun isTreatmentActive(): Boolean {
        if (isKillSwitchActive()) return false
        val variant = getOrAssignVariant()
        return variant == FirstWinVariant.TREATMENT
    }

    override suspend fun getSnapshot(): FirstWinControlSnapshot {
        val variant = getOrAssignVariant()
        val version = preferences.firstWinAssignedEligibilityVersionFlow.first()
            ?: FirstWinControlPlane.CURRENT_ELIGIBILITY_VERSION
        val timestamp = preferences.firstWinAssignmentTimestampFlow.first() ?: 0L
        val localKill = isLocalKilled()
        val remoteKill = isRemoteKilled()
        val treatmentActive = !localKill && !remoteKill && (variant == FirstWinVariant.TREATMENT)

        return FirstWinControlSnapshot(
            eligibilityVersion = version,
            assignedVariant = variant,
            isLocalKilled = localKill,
            isRemoteKilled = remoteKill,
            isTreatmentActive = treatmentActive,
            assignmentTimestamp = timestamp,
        )
    }

    override suspend fun setRemoteKill(active: Boolean) {
        preferences.setFirstWinRemoteKill(active)
    }

    override fun setLocalKill(active: Boolean) {
        FeatureFlags.FIRST_WIN_LOCAL_KILL = active
    }
}
