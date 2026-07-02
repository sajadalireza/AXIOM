package com.axiom.app.presentation.onboarding.blueprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.model.*
import com.axiom.app.domain.repository.WarriorProfileRepository
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.repository.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.axiom.app.BuildConfig

/**
 * Reachable only after the delight-first onboarding flow (codename entry ->
 * celebration -> real first mission) has already run. This wizard just
 * deepens the Hunter's profile: primary domain + personal thesis, battlefield
 * tracks, and iron rules. Codename, freeform thesis, and a "first mission"
 * are intentionally NOT collected here anymore — they're already real by
 * this point, collected via OnboardingScreen/FirstMissionScreen.
 */
@HiltViewModel
class BlueprintWizardViewModel @Inject constructor(
    private val repository: WarriorProfileRepository,
    private val hunterRepository: HunterRepository,
    private val preferences: AxiomPreferences,
    private val generateDailyMissionsUseCase: com.axiom.app.domain.usecase.GenerateDailyMissionsFromScheduleUseCase,
    private val missionRepository: MissionRepository
) : ViewModel() {

    // Step 1: WHO ARE YOU? (domain + thesis template -> written to Hunter.personalThesis)
    val selectedDomain = MutableStateFlow("career")
    val oneLineThesis = MutableStateFlow("Excellence is not an act, but a non-negotiable habit.")
    val rareProfileDescription = MutableStateFlow("Operative of relentless determination, balancing high-impact actions with tactical recovery.")

    // Step 2: YOUR BATTLEFIELD
    val selectedTracks = MutableStateFlow(listOf("career", "finance", "health"))

    // Step 3: YOUR IRON RULES
    val ironRules = MutableStateFlow(
        listOf(
            IronRule("ir_1", 1, "No digital screens in bed under any circumstances.", true),
            IronRule("ir_2", 2, "Execute highest leverage Conquest item before checking messages.", true),
            IronRule("ir_3", 3, "Friday review. No exceptions. 60 minutes audit.", true)
        )
    )

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isExtracting = MutableStateFlow(false)
    val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

    private val _extractionError = MutableStateFlow<String?>(null)
    val extractionError: StateFlow<String?> = _extractionError.asStateFlow()

    fun updateSelectedDomain(domain: String) {
        selectedDomain.value = domain
    }

    fun addIronRule(text: String) {
        val nextIndex = (ironRules.value.maxOfOrNull { it.orderIndex } ?: 0) + 1
        ironRules.value = ironRules.value + IronRule(UUID.randomUUID().toString(), nextIndex, text, true)
    }

    fun removeIronRule(id: String) {
        ironRules.value = ironRules.value.filter { it.id != id }
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                // The real Hunter (codename, first mission) already exists by this
                // point via the delight-first flow — reuse its name, and write this
                // step's chosen thesis template as the Hunter's real personal thesis.
                val hunter = hunterRepository.getDirectHunterProfile()
                if (hunter != null) {
                    hunterRepository.updateHunterProfile(hunter.copy(personalThesis = oneLineThesis.value))
                }

                // 1. Save Profile
                repository.saveProfile(
                    WarriorProfile(
                        id = "default",
                        codename = hunter?.name ?: "",
                        oneLineThesis = oneLineThesis.value,
                        rareProfileDescription = rareProfileDescription.value
                    )
                )

                // 2. Save Selected Tracks / Pillars
                repository.clearTracks()
                val allTracks = com.axiom.app.data.BlueprintV51Data.PILLARS
                val activeTracks = allTracks.filter { selectedTracks.value.contains(it.id) }
                if (activeTracks.isEmpty()) {
                    allTracks.forEach { repository.saveTrack(it) }
                } else {
                    activeTracks.forEach { repository.saveTrack(it) }
                }

                // 3. Save Schedule Blocks
                repository.clearScheduleBlocks()
                com.axiom.app.data.BlueprintV51Data.SCHEDULE_BLOCKS.forEach { repository.saveScheduleBlock(it) }

                // 4. Save KPIs
                repository.clearCustomKPIs()
                com.axiom.app.data.BlueprintV51Data.CUSTOM_KPIS.forEach { repository.saveCustomKPI(it) }

                // 5. Save Iron Rules
                repository.clearIronRules()
                repository.clearHardTruthsOrAffirmations()
                ironRules.value.take(5).forEach { repository.saveIronRule(it) }
                com.axiom.app.data.BlueprintV51Data.HARD_TRUTHS.forEach { repository.saveHardTruthOrAffirmation(it) }
                com.axiom.app.data.BlueprintV51Data.AFFIRMATIONS.forEach { repository.saveHardTruthOrAffirmation(it) }

                // 6. Generate Daily Missions
                try {
                    generateDailyMissionsUseCase()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                // Mark completion flag. firstMissionDone is intentionally NOT touched
                // here — it is owned exclusively by FirstMissionScreen's real
                // completion path, which already ran before this wizard is reachable.
                preferences.setBlueprintSetupComplete(true)
                preferences.setSetupComplete()

                _isSaving.value = false
                onSuccess()
            } catch (e: Exception) {
                _isSaving.value = false
                e.printStackTrace()
            }
        }
    }
}
