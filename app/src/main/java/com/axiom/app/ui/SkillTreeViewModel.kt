package com.axiom.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.model.Mission
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.usecase.AriseShadowUseCase
import com.axiom.app.domain.usecase.CreateSkillUseCase
import com.axiom.app.domain.usecase.GetSkillsUseCase
import com.axiom.app.domain.usecase.GetMissionsUseCase
import com.axiom.app.domain.repository.SystemFeedRepository
import com.axiom.app.domain.repository.SkillRepository
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.presentation.ceremony.CeremonyEngine
import com.axiom.app.presentation.ceremony.CeremonyEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

sealed interface SkillTreeUiState {
    object Loading : SkillTreeUiState
    data class Success(val skills: List<Skill>) : SkillTreeUiState
    data class Error(val message: String) : SkillTreeUiState
}

data class UpgradeAnim(
    val parentId: String?,
    val childId: String,
    val startTime: Long,
    val duration: Long = 1200L
)

@HiltViewModel
class SkillTreeViewModel @Inject constructor(
    private val getSkillsUseCase: GetSkillsUseCase,
    private val createSkillUseCase: CreateSkillUseCase,
    private val ariseShadowUseCase: AriseShadowUseCase,
    private val getMissionsUseCase: GetMissionsUseCase,
    private val ceremonyEngine: CeremonyEngine,
    val preferences: AxiomPreferences,
    private val feedRepository: SystemFeedRepository,
    private val skillRepository: SkillRepository,
    private val hunterRepository: HunterRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            val shown = preferences.briefingSkillTreeFlow.first()
            if (!shown) {
                delay(1500)
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = "Skill tree initialized. Accumulate XP through missions to unlock rank tiers. Reach B-Rank to manifest a Shadow.",
                        timestamp = System.currentTimeMillis()
                    )
                )
                preferences.setBriefingShown("skill_tree")
            }
        }
    }

    private val _selectedSkillId = MutableStateFlow<String?>(null)
    val selectedSkillId: StateFlow<String?> = _selectedSkillId.asStateFlow()

    private val _collapsedSkillIds = MutableStateFlow<Set<String>>(emptySet())
    val collapsedSkillIds: StateFlow<Set<String>> = _collapsedSkillIds.asStateFlow()

    fun toggleCollapseSkill(skillId: String) {
        val current = _collapsedSkillIds.value
        _collapsedSkillIds.value = if (current.contains(skillId)) {
            current - skillId
        } else {
            current + skillId
        }
    }

    val upgradeAnimationState = MutableStateFlow<UpgradeAnim?>(null)

    fun upgradeOrUnlockSkill(skillId: String) {
        viewModelScope.launch {
            val sState = skillsState.value
            if (sState !is SkillTreeUiState.Success) return@launch
            val skills = sState.skills
            val targetSkill = skills.firstOrNull { it.id == skillId } ?: return@launch
            
            // Check eligibility: if locked, requires parent to be unlocked
            if (!targetSkill.isUnlocked) {
                if (targetSkill.parentId != null) {
                    val parent = skills.firstOrNull { it.id == targetSkill.parentId }
                    if (parent == null || !parent.isUnlocked) return@launch
                }
            }

            // Start animation
            upgradeAnimationState.value = UpgradeAnim(
                parentId = targetSkill.parentId,
                childId = targetSkill.id,
                startTime = System.currentTimeMillis()
            )

            delay(1200)

            val isUnlocking = !targetSkill.isUnlocked
            val updatedSkill = if (isUnlocking) {
                targetSkill.copy(
                    isUnlocked = true,
                    level = 1,
                    currentXP = 0,
                    rankProgressPercent = 0.0f
                )
            } else {
                val nextLevel = targetSkill.level + 1
                val nextRankXP = nextLevel * 100L
                val rankLabel = XPEngine.calculateSkillRank(targetSkill.currentXP + targetSkill.xpToNextRank)
                val rankLabelWithSuffix = if (rankLabel.endsWith("-Rank")) rankLabel else "$rankLabel-Rank"
                val rankColor = XPEngine.getRankColor(rankLabel)
                
                targetSkill.copy(
                    level = nextLevel,
                    currentXP = 0,
                    rankLabel = rankLabelWithSuffix,
                    rankColor = rankColor,
                    xpToNextRank = nextRankXP,
                    rankProgressPercent = 0.0f,
                    isShadowCandidate = XPEngine.isShadowCandidate(rankLabel)
                )
            }
            
            skillRepository.updateSkill(updatedSkill)

            val isFa = java.util.Locale.getDefault().language == "fa"
            val msg = if (isFa) {
                if (isUnlocking) {
                    "◈ [ پروتکل سیستم ] ◈ گره '${updatedSkill.name.uppercase()}' با موفقیت بیدار شد."
                } else {
                    "◈ [ پروتکل سیستم ] ◈ گره '${updatedSkill.name.uppercase()}' به سطح ${updatedSkill.level} اورکلاک و تقویت شد."
                }
            } else {
                if (isUnlocking) {
                    "◈ [ SYSTEM PROTOCOL ] ◈ NODE '${updatedSkill.name.uppercase()}' SUCCESSFULLY AWAKENED."
                } else {
                    "◈ [ SYSTEM PROTOCOL ] ◈ NODE '${updatedSkill.name.uppercase()}' OVERCLOCKED TO LEVEL ${updatedSkill.level}."
                }
            }
            feedRepository.emitMessage(
                SystemMessage(
                    id = UUID.randomUUID().toString(),
                    message = msg,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Terminate animation
            upgradeAnimationState.value = null
        }
    }

    val skillsState: StateFlow<SkillTreeUiState> = getSkillsUseCase()
        .map { skills -> SkillTreeUiState.Success(skills) as SkillTreeUiState }
        .catch { e -> emit(SkillTreeUiState.Error(e.message ?: "An unexpected error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SkillTreeUiState.Loading
        )

    val missionsState: StateFlow<List<Mission>> = getMissionsUseCase(activeOnly = false)
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val equippedPassiveSkillId: StateFlow<String?> = preferences.equippedPassiveSkillIdFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun equipPassiveSkill(skillId: String?) {
        viewModelScope.launch {
            if (skillId == null) {
                preferences.setEquippedPassiveSkillId(null)
                val isFa = java.util.Locale.getDefault().language == "fa"
                val msg = if (isFa) {
                    "◈ [ رابط کاربری سیستم ] ◈ جایگاه حامی غیرفعال خالی شد."
                } else {
                    "◈ [ SYSTEM INTERFACE ] ◈ PASSIVE BOOST SLOT CLEARED."
                }
                feedRepository.emitMessage(
                    SystemMessage(
                        id = UUID.randomUUID().toString(),
                        message = msg,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } else {
                val sState = skillsState.value
                if (sState !is SkillTreeUiState.Success) return@launch
                val targetSkill = sState.skills.firstOrNull { it.id == skillId } ?: return@launch
                
                val rank = targetSkill.rankLabel.replace("-Rank", "").trim()
                val isAtLeastB = rank == "B" || rank == "A" || rank == "S" || rank == "S+" || rank == "Legendary"
                
                if (targetSkill.isUnlocked && isAtLeastB) {
                    preferences.setEquippedPassiveSkillId(skillId)
                    val isFa = java.util.Locale.getDefault().language == "fa"
                    val msg = if (isFa) {
                        "◈ [ رابط کاربری سیستم ] ◈ بوستر غیرفعال: '${targetSkill.name.uppercase()}' مجهز شد. ضریب امتیاز روزانه +۵٪ فعال گردید."
                    } else {
                        "◈ [ SYSTEM INTERFACE ] ◈ PASSIVE BOOST: '${targetSkill.name.uppercase()}' EQUIPPED. +5% DAILY XP MULTIPLIER ACTIVE."
                    }
                    feedRepository.emitMessage(
                        SystemMessage(
                            id = UUID.randomUUID().toString(),
                            message = msg,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    fun selectSkill(id: String) {
        _selectedSkillId.value = id
    }

    fun clearSelection() {
        _selectedSkillId.value = null
    }

    fun createSkill(name: String, category: String, parentId: String? = null, trackId: String? = null) {
        viewModelScope.launch {
            createSkillUseCase(name, category, parentId, trackId)
        }
    }

    fun editSkill(
        skillId: String,
        newName: String,
        newCategory: String,
        newParentId: String?,
        newLevel: Int,
        isUnlocked: Boolean
    ) {
        viewModelScope.launch {
            val sState = skillsState.value
            if (sState !is SkillTreeUiState.Success) return@launch
            val target = sState.skills.firstOrNull { it.id == skillId } ?: return@launch
            
            // Validate parent choice to prevent cycle
            var finalParentId = newParentId
            if (finalParentId == skillId) {
                finalParentId = null
            } else if (finalParentId != null) {
                var currentAncestorId = finalParentId
                val skillsMap = sState.skills.associateBy { it.id }
                var cycleDetected = false
                var iterations = 0
                while (currentAncestorId != null && iterations < 100) {
                    if (currentAncestorId == skillId) {
                        cycleDetected = true
                        break
                    }
                    currentAncestorId = skillsMap[currentAncestorId]?.parentId
                    iterations++
                }
                if (cycleDetected) {
                    finalParentId = null
                }
            }

            val rankSuffix = XPEngine.calculateSkillRank(newLevel * 100L)
            val rankLabel = if (rankSuffix.endsWith("-Rank")) rankSuffix else "$rankSuffix-Rank"
            val rankColor = XPEngine.getRankColor(rankSuffix)

            val updated = target.copy(
                name = newName,
                category = newCategory,
                parentId = finalParentId,
                level = newLevel,
                isUnlocked = isUnlocked,
                rankLabel = if (!isUnlocked) "E-Rank" else rankLabel,
                rankColor = if (!isUnlocked) 0xFF9E9E9E else rankColor,
                isShadowCandidate = isUnlocked && XPEngine.isShadowCandidate(rankSuffix)
            )
            skillRepository.updateSkill(updated)
        }
    }

    fun deleteSkill(skillId: String) {
        viewModelScope.launch {
            val sState = skillsState.value
            if (sState !is SkillTreeUiState.Success) return@launch
            val currentSkills = sState.skills
            val targetSkill = currentSkills.firstOrNull { it.id == skillId } ?: return@launch

            // Promote child nodes to be children of targetSkill's parentId
            val children = currentSkills.filter { it.parentId == skillId }
            children.forEach { child ->
                val updatedChild = child.copy(parentId = targetSkill.parentId)
                skillRepository.updateSkill(updatedChild)
            }

            skillRepository.deleteSkillById(skillId)
            
            if (_selectedSkillId.value == skillId) {
                _selectedSkillId.value = null
            }
        }
    }

    fun getMasteryAllocation(skillId: String, type: String): Int {
        return preferences.getMasteryPointsAllocated(skillId, type)
    }

    fun allocateMastery(skillId: String, type: String, maxPoints: Int) {
        viewModelScope.launch {
            val success = preferences.allocateMasteryPoint(skillId, type, maxPoints)
            if (success) {
                _selectedSkillId.value = _selectedSkillId.value
            }
        }
    }

    fun refundMastery(skillId: String, type: String) {
        viewModelScope.launch {
            val success = preferences.refundMasteryPoint(skillId, type)
            if (success) {
                _selectedSkillId.value = _selectedSkillId.value
            }
        }
    }

    fun getSkillPrestigeTier(skillId: String): Int {
        return preferences.getSkillPrestige(skillId)
    }

    fun prestigeSkill(skillId: String) {
        viewModelScope.launch {
            val sState = skillsState.value
            if (sState !is SkillTreeUiState.Success) return@launch
            val target = sState.skills.firstOrNull { it.id == skillId } ?: return@launch
            
            // Increment prestige tier persisted state
            preferences.incrementSkillPrestige(skillId)
            
            val updated = target.copy(
                level = 1,
                currentXP = 0,
                isUnlocked = true, // keeps unlocked as prestige reward
                rankLabel = "E-Rank",
                rankColor = 0xFF8A8AA0,
                xpToNextRank = 100L,
                rankProgressPercent = 0.0f,
                isShadowCandidate = false
            )
            skillRepository.updateSkill(updated)
            
            val isFa = java.util.Locale.getDefault().language == "fa"
            val msg = if (isFa) {
                "◈ [ ماتریکس صعود ] ◈ گره '${target.name.uppercase()}' صعود کرد! رتبه اعتبار کسب شد. کارایی کل سیستم تقویت یافت (+۲۵٪ ضریب امتیاز)."
            } else {
                "◈ [ ASCENSION MATRIX ] ◈ NODE '${target.name.uppercase()}' HAS ASCENDED! PRESTIGE TIER SECURED. ALL SYSTEM EFFICIENCIES BOOSTED (+25% MULTIPLIER)."
            }
            feedRepository.emitMessage(
                SystemMessage(
                    id = UUID.randomUUID().toString(),
                    message = msg,
                    timestamp = System.currentTimeMillis()
                )
            )
            
            _selectedSkillId.value = _selectedSkillId.value
        }
    }

    fun ariseShadow(skillId: String, shadowName: String) {
        viewModelScope.launch {
            val shadow = ariseShadowUseCase(skillId, shadowName)
            if (shadow != null) {
                ceremonyEngine.emit(
                    CeremonyEvent.ShadowAcquired(
                        skillName = shadow.name,
                        rankLabel = shadow.rankLabel
                    )
                )
            }
        }
    }
}
