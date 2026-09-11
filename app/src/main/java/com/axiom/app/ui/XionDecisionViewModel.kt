package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.core.AnalyticsLogger
import com.axiom.app.core.CanonicalAnalyticsEvents
import com.axiom.app.core.ai.XionGatewayBoundary
import com.axiom.app.core.ai.XionHunterContext
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.engine.ROIEngine
import com.axiom.app.domain.model.MissionAuthoringPayload
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.usecase.CreateMissionUseCase
import com.axiom.app.domain.usecase.GetHunterProfileUseCase
import com.axiom.app.domain.usecase.GetSkillsUseCase
import com.axiom.app.domain.xion.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class XionDecisionViewModel @Inject constructor(
    private val gateway: XionGatewayBoundary,
    private val preferences: AxiomPreferences,
    private val getHunterProfile: GetHunterProfileUseCase,
    private val getSkills: GetSkillsUseCase,
    private val createMission: CreateMissionUseCase
) : ViewModel() {

    private val _goal = MutableStateFlow("")
    val goal: StateFlow<String> = _goal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _suggestions = MutableStateFlow<List<XionSuggestion>>(emptyList())
    val suggestions: StateFlow<List<XionSuggestion>> = _suggestions.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds: StateFlow<Set<String>> = _selectedIds.asStateFlow()

    private val _contextualInsight = MutableStateFlow<XionContextualInsight?>(null)
    val contextualInsight: StateFlow<XionContextualInsight?> = _contextualInsight.asStateFlow()

    private val _isOfflineFallback = MutableStateFlow(false)
    val isOfflineFallback: StateFlow<Boolean> = _isOfflineFallback.asStateFlow()

    private val _missionsCreated = MutableStateFlow(false)
    val missionsCreated: StateFlow<Boolean> = _missionsCreated.asStateFlow()

    val language: StateFlow<String> = preferences.languageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "en")

    val quotaStatus: StateFlow<XionQuotaStatus> = preferences.xionQuotaStatusFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), XionQuotaStatus())

    val acceptanceMetrics: StateFlow<XionAcceptanceMetrics> = preferences.xionAcceptanceMetricsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), XionAcceptanceMetrics())

    val totalSelectedXp: StateFlow<Int> = combine(_suggestions, _selectedIds) { list, sel ->
        list.filter { sel.contains(it.id) }
            .sumOf { (it.estimatedHours * 100).toInt().coerceAtLeast(25) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setGoal(newGoal: String) {
        _goal.value = newGoal
    }

    fun generateDecision() {
        val targetGoal = _goal.value.trim()
        if (targetGoal.length < 3) return

        viewModelScope.launch {
            _isLoading.value = true
            _selectedIds.value = emptySet()
            _missionsCreated.value = false

            val hunter = getHunterProfile().first()
            val skills = getSkills().first()
            val streak = preferences.streakFlow.first()
            val isFa = language.value == "fa"

            // 1. Contextual insight
            if (hunter != null) {
                _contextualInsight.value = XionDecisionEngine.generateContextualInsight(
                    hunter = hunter,
                    streakDays = streak,
                    goal = targetGoal,
                    isPersian = isFa
                )
            }

            // 2. Template-First matching
            val templateMatches = XionDecisionEngine.findTemplateMatches(targetGoal)
            val templateFirstSuggestions = templateMatches.take(2).map {
                XionDecisionEngine.templateToSuggestion(it, isFa)
            }

            // 3. Quota check & Gateway invocation
            val quotaAvailable = preferences.consumeXionDailyQuota(dailyLimit = 5)
            val generatedSuggestions = mutableListOf<XionSuggestion>()
            var usedOffline = false

            if (quotaAvailable && hunter != null) {
                val context = XionHunterContext(
                    level = hunter.level,
                    rankLabel = hunter.rankLabel,
                    streakDays = streak,
                    unlockedSkills = skills.filter { it.isUnlocked }.map { it.name }.ifEmpty { listOf("Engineering") }
                )
                val result = gateway.generateDecision(targetGoal, context, isFa)
                if (result.isSuccess) {
                    val rawGenerated = result.getOrNull() ?: emptyList()
                    generatedSuggestions.addAll(rawGenerated)
                    usedOffline = false
                } else {
                    usedOffline = true
                }
            } else {
                usedOffline = true
            }

            // 4. Combine template-first and generated suggestions
            val combined = mutableListOf<XionSuggestion>()
            // Always place template-matched items first
            combined.addAll(templateFirstSuggestions)

            if (usedOffline || generatedSuggestions.isEmpty()) {
                val heuristicRemaining = XionDecisionEngine.buildHeuristicSuggestions(targetGoal, skills, isFa)
                for (h in heuristicRemaining) {
                    if (combined.none { it.title.equals(h.title, ignoreCase = true) }) {
                        combined.add(h)
                    }
                    if (combined.size >= 3) break
                }
            } else {
                for (g in generatedSuggestions) {
                    if (combined.none { it.title.equals(g.title, ignoreCase = true) }) {
                        combined.add(g)
                    }
                    if (combined.size >= 3) break
                }
            }

            val sanitizedList = XionDecisionEngine.sanitizeAndFilterSuggestions(combined, isFa)
            _suggestions.value = sanitizedList
            _isOfflineFallback.value = usedOffline
            _isLoading.value = false

            // Telemetry & DataStore tracking
            preferences.recordXionExposed(sanitizedList.size)
            val hasTemplateFirst = sanitizedList.any { it.isTemplateMatched }
            val sourceStr = if (usedOffline) "offline_heuristic" else "gateway_generated"
            val ring = preferences.releaseRingFlow.first()
            AnalyticsLogger.log(
                CanonicalAnalyticsEvents.XION_SUGGESTION_EXPOSED,
                mapOf(
                    "source" to sourceStr,
                    "template_first" to hasTemplateFirst,
                    "count" to sanitizedList.size,
                    "cohort_ring" to ring
                )
            )
        }
    }

    fun toggleSelection(id: String) {
        _selectedIds.value = if (_selectedIds.value.contains(id)) {
            _selectedIds.value - id
        } else {
            _selectedIds.value + id
        }
    }

    fun editSuggestion(id: String, newTitle: String, newHours: Float, newDoneCondition: String) {
        _suggestions.value = _suggestions.value.map { item ->
            if (item.id == id) {
                item.copy(
                    title = newTitle.trim(),
                    estimatedHours = newHours.coerceIn(0.25f, 12.0f),
                    doneCondition = newDoneCondition.trim(),
                    wasEdited = true
                )
            } else {
                item
            }
        }
        viewModelScope.launch {
            val ring = preferences.releaseRingFlow.first()
            AnalyticsLogger.log(
                CanonicalAnalyticsEvents.XION_SUGGESTION_EDITED,
                mapOf(
                    "field_edited" to "content",
                    "cohort_ring" to ring
                )
            )
        }
    }

    fun rejectSuggestion(id: String, reason: XionRejectionReason) {
        val target = _suggestions.value.firstOrNull { it.id == id } ?: return
        _selectedIds.value = _selectedIds.value - id
        _suggestions.value = _suggestions.value.map { item ->
            if (item.id == id) item.copy(status = SuggestionStatus.REJECTED) else item
        }

        viewModelScope.launch {
            preferences.recordXionSuggestionRejected()
            val ring = preferences.releaseRingFlow.first()
            AnalyticsLogger.log(
                CanonicalAnalyticsEvents.XION_SUGGESTION_REJECTED,
                mapOf(
                    "rejection_reason" to reason.name.lowercase(),
                    "template_matched" to target.isTemplateMatched,
                    "cohort_ring" to ring
                )
            )
        }
    }

    fun reportSuggestion(id: String, category: XionReportCategory) {
        _selectedIds.value = _selectedIds.value - id
        _suggestions.value = _suggestions.value.map { item ->
            if (item.id == id) item.copy(status = SuggestionStatus.REPORTED, isReported = true) else item
        }

        viewModelScope.launch {
            preferences.recordXionSuggestionReported()
            val ring = preferences.releaseRingFlow.first()
            AnalyticsLogger.log(
                CanonicalAnalyticsEvents.XION_SUGGESTION_REPORTED,
                mapOf(
                    "report_category" to category.name.lowercase(),
                    "cohort_ring" to ring
                )
            )
        }
    }

    fun acceptSelected() {
        val sel = _selectedIds.value
        if (sel.isEmpty()) return

        viewModelScope.launch {
            val skills = getSkills().first()
            val chosen = _suggestions.value.filter { sel.contains(it.id) && it.status != SuggestionStatus.REJECTED && it.status != SuggestionStatus.REPORTED }
            val ring = preferences.releaseRingFlow.first()

            for (item in chosen) {
                val skill = skills.firstOrNull { it.name.equals(item.skillName, ignoreCase = true) && it.isUnlocked }
                    ?: skills.firstOrNull { it.isUnlocked }
                    ?: continue

                val payload = MissionAuthoringPayload(
                    title = item.title,
                    doneCondition = item.doneCondition,
                    skillId = skill.id,
                    track = skill.category,
                    durationMinutes = (item.estimatedHours * 60).toInt().coerceAtLeast(15),
                    customRarity = item.rarity,
                    notes = item.reasoning
                )

                createMission(payload)
                preferences.recordXionSuggestionAccepted(wasEdited = item.wasEdited)

                AnalyticsLogger.log(
                    CanonicalAnalyticsEvents.XION_SUGGESTION_ACCEPTED,
                    mapOf(
                        "template_matched" to item.isTemplateMatched,
                        "was_edited" to item.wasEdited,
                        "rarity" to item.rarity,
                        "cohort_ring" to ring
                    )
                )
            }

            _suggestions.value = _suggestions.value.map { item ->
                if (sel.contains(item.id)) item.copy(status = SuggestionStatus.ACCEPTED) else item
            }
            _missionsCreated.value = true
        }
    }
}
