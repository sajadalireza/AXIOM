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

                // Mark onboarding done
                preferences.setFirstMissionDone(true)

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
