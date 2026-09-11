package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.usecase.CompleteMissionUseCase
import com.axiom.app.domain.usecase.CreateMissionUseCase
import com.axiom.app.domain.usecase.GetSkillsUseCase
import com.axiom.app.ui.components.XPFloatEvent
import com.axiom.app.presentation.ceremony.CeremonyEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstMissionViewModel @Inject constructor(
    private val createMissionUseCase: CreateMissionUseCase,
    private val completeMissionUseCase: CompleteMissionUseCase,
    private val preferences: AxiomPreferences,
    private val getSkillsUseCase: GetSkillsUseCase,
    private val ceremonyEngine: CeremonyEngine
) : ViewModel() {

    private val _done = MutableStateFlow(false)
    val done: StateFlow<Boolean> = _done.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _xpFloatEvent = MutableStateFlow<XPFloatEvent?>(null)
    val xpFloatEvent: StateFlow<XPFloatEvent?> = _xpFloatEvent.asStateFlow()

    private var isSubmitting = false

    init {
        // Gate G4: emit Assignment event upon First-Win initialization
        viewModelScope.launch {
            val ring = preferences.releaseRingFlow.first().name
            val variant = preferences.firstWinVariantFlow.first() ?: "CONTROL_DEFAULT"
            com.axiom.app.core.AnalyticsLogger.log(
                com.axiom.app.core.CanonicalAnalyticsEvents.FIRST_WIN_ASSIGNED,
                mapOf(
                    "treatment_id" to variant,
                    "template_id" to "FIRST_WIN_ONBOARDING",
                    "cohort_ring" to ring
                )
            )
        }
    }

    /**
     * Gate G4: emit Exposure event when FirstMissionScreen renders.
     * Assignment and exposure remain strictly segregated.
     */
    fun onScreenExposed() {
        viewModelScope.launch {
            val ring = preferences.releaseRingFlow.first().name
            val variant = preferences.firstWinVariantFlow.first() ?: "CONTROL_DEFAULT"
            com.axiom.app.core.AnalyticsLogger.log(
                com.axiom.app.core.CanonicalAnalyticsEvents.FIRST_WIN_EXPOSED,
                mapOf(
                    "treatment_id" to variant,
                    "template_id" to "FIRST_WIN_ONBOARDING",
                    "screen_name" to "FirstMissionScreen",
                    "cohort_ring" to ring
                )
            )
        }
    }

    fun createAndStart(title: String) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.length < 3) return
        if (isSubmitting || _loading.value || _done.value) return
        isSubmitting = true
        _loading.value = true
        viewModelScope.launch {
            try {
                // Get first skill if available
                val skills = getSkillsUseCase().first()
                val firstSkillId = skills.firstOrNull()?.id ?: "skill_health"

                // Create the mission
                val missionId = createMissionUseCase(
                    title = trimmedTitle,
                    estimatedHours = 0.5f,
                    skillId = firstSkillId,
                    rarity = "COMMON"
                )

                // Complete it immediately so the very first action in the app
                // produces a real reward — this is the critical first-session moment.
                val xpResult = completeMissionUseCase(missionId, actualHours = 0.5f)

                // WP-205: first-mission-done is now flipped inside CompleteMissionUseCase PHASE C
                // (gated by the first-win receipt), so completion + onboarding flag are one atomic
                // authority. No separate setFirstMissionDone here.
                val ring = preferences.releaseRingFlow.first().name
                val variant = preferences.firstWinVariantFlow.first() ?: "CONTROL_DEFAULT"

                com.axiom.app.core.AnalyticsLogger.log(
                    com.axiom.app.core.CanonicalAnalyticsEvents.FIRST_WIN_COMPLETED,
                    mapOf(
                        "treatment_id" to variant,
                        "template_id" to "FIRST_WIN_ONBOARDING",
                        "duration_seconds" to "1",
                        "cohort_ring" to ring
                    )
                )

                com.axiom.app.core.AnalyticsLogger.log(
                    "onboarding_completed",
                    mapOf("cohort_ring" to ring)
                )

                // Feed the existing XP float animation with the real XP gained
                if (xpResult != null) {
                    _xpFloatEvent.value = XPFloatEvent(
                        xpValue = xpResult.hunterXPGained,
                        shadowMultiplier = 1.0f
                    )
                }

                // Experience delay simulating Gate opening sequence —
                // also gives the XP float animation time to play before navigating away
                delay(1200)
                _done.value = true
            } catch (e: Exception) {
                isSubmitting = false
            } finally {
                _loading.value = false
            }
        }
    }

    fun onXPAnimationComplete() {
        _xpFloatEvent.value = null
    }
}
