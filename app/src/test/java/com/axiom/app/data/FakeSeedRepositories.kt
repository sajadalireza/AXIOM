package com.axiom.app.data

import com.axiom.app.domain.model.*
import com.axiom.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * WP-202: pure-JVM hand-written fakes (no Robolectric/Mockito/MockK in this project).
 * They record exactly what [SeedDataHelper] writes so a fresh-install bootstrap can be
 * asserted deterministically. Only the members bootstrap touches carry real behaviour;
 * unused members are inert.
 */

class FakeSeedPreferences(
    skillTreeSeeded: Boolean = false,
    muscleGroupsSeeded: Boolean = false,
    alirezaProfileSeeded: Boolean = false
) : SeedPreferences {
    private val _skillTreeSeeded = MutableStateFlow(skillTreeSeeded)
    private val _muscleGroupsSeeded = MutableStateFlow(muscleGroupsSeeded)
    private val _alirezaProfileSeeded = MutableStateFlow(alirezaProfileSeeded)
    private val _setupComplete = MutableStateFlow(false)
    private val _firstMissionDone = MutableStateFlow(false)
    private val _blueprintSetupComplete = MutableStateFlow(false)
    private val _financialModuleEnabled = MutableStateFlow(false)

    override val skillTreeSeededFlow: Flow<Boolean> = _skillTreeSeeded
    override suspend fun setSkillTreeSeeded(value: Boolean) { _skillTreeSeeded.value = value }
    override val muscleGroupsSeededFlow: Flow<Boolean> = _muscleGroupsSeeded
    override suspend fun setMuscleGroupsSeeded(value: Boolean) { _muscleGroupsSeeded.value = value }
    override val alirezaProfileSeededFlow: Flow<Boolean> = _alirezaProfileSeeded
    override suspend fun setAlirezaProfileSeeded(value: Boolean) { _alirezaProfileSeeded.value = value }
    override val setupCompleteFlow: Flow<Boolean> = _setupComplete
    override suspend fun setSetupComplete() { _setupComplete.value = true }
    override val firstMissionDoneFlow: Flow<Boolean> = _firstMissionDone
    override suspend fun setFirstMissionDone(value: Boolean) { _firstMissionDone.value = value }
    override val blueprintSetupCompleteFlow: Flow<Boolean> = _blueprintSetupComplete
    override suspend fun setBlueprintSetupComplete(value: Boolean) { _blueprintSetupComplete.value = value }
    override val financialModuleEnabledFlow: Flow<Boolean> = _financialModuleEnabled
    override suspend fun setFinancialModuleEnabled(value: Boolean) { _financialModuleEnabled.value = value }

    // Direct synchronous accessors for assertions.
    val setupComplete: Boolean get() = _setupComplete.value
    val firstMissionDone: Boolean get() = _firstMissionDone.value
    val blueprintSetupComplete: Boolean get() = _blueprintSetupComplete.value
    val financialModuleEnabled: Boolean get() = _financialModuleEnabled.value
    val alirezaProfileSeeded: Boolean get() = _alirezaProfileSeeded.value
}

class FakeHunterRepository(seed: Hunter? = null) : HunterRepository {
    var profile: Hunter? = seed
    override fun getHunterProfile(): Flow<Hunter?> = MutableStateFlow(profile)
    override suspend fun getDirectHunterProfile(): Hunter? = profile
    override suspend fun updateHunterProfile(profile: Hunter) { this.profile = profile }
}

class FakeSkillRepository : SkillRepository {
    val skills = linkedMapOf<String, Skill>()
    override fun getAllSkills(): Flow<List<Skill>> = MutableStateFlow(skills.values.toList())
    override suspend fun getSkillById(id: String): Skill? = skills[id]
    override suspend fun insertSkill(skill: Skill) { skills[skill.id] = skill }
    override suspend fun updateSkill(skill: Skill) { skills[skill.id] = skill }
    override suspend fun deleteSkillById(id: String) { skills.remove(id) }
}

class FakeMuscleGroupRepository : MuscleGroupRepository {
    val groups = linkedMapOf<String, MuscleGroup>()
    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> = MutableStateFlow(groups.values.toList())
    override suspend fun getMuscleGroupById(id: String): MuscleGroup? = groups[id]
    override suspend fun insertMuscleGroup(muscle: MuscleGroup) { groups[muscle.id] = muscle }
    override suspend fun insertMuscleGroups(muscles: List<MuscleGroup>) { muscles.forEach { groups[it.id] = it } }
    override suspend fun updateMuscleGroup(muscle: MuscleGroup) { groups[muscle.id] = muscle }
}

class FakeWarriorProfileRepository : WarriorProfileRepository {
    var profile: WarriorProfile? = null
    val tracks = mutableListOf<Track>()
    val scheduleBlocks = mutableListOf<ScheduleBlock>()
    val customKPIs = mutableListOf<CustomKPI>()
    val ironRules = mutableListOf<IronRule>()
    val hardTruths = mutableListOf<HardTruthOrAffirmation>()
    val milestones = mutableListOf<MajorMilestone>()
    val relationships = mutableListOf<KeyRelationship>()
    val financialCheckpoints = mutableListOf<FinancialCheckpoint>()
    val monthlyIncome = mutableListOf<MonthlyIncomeEntry>()
    val violationLogs = mutableListOf<IronRuleViolationLog>()
    private val _financialEnabled = MutableStateFlow(false)
    val financialEnabled: Boolean get() = _financialEnabled.value

    override fun getProfileFlow(id: String): Flow<WarriorProfile?> = MutableStateFlow(profile)
    override suspend fun getProfile(id: String): WarriorProfile? = profile
    override suspend fun saveProfile(profile: WarriorProfile) { this.profile = profile }

    override fun getTracksFlow(): Flow<List<Track>> = MutableStateFlow(tracks.toList())
    override suspend fun getAllTracks(): List<Track> = tracks.toList()
    override suspend fun getTrackById(id: String): Track? = tracks.firstOrNull { it.id == id }
    override suspend fun saveTrack(track: Track) { tracks.add(track) }
    override suspend fun deleteTrack(track: Track) { tracks.remove(track) }
    override suspend fun clearTracks() { tracks.clear() }

    override fun getScheduleBlocksFlow(): Flow<List<ScheduleBlock>> = MutableStateFlow(scheduleBlocks.toList())
    override suspend fun getAllScheduleBlocks(): List<ScheduleBlock> = scheduleBlocks.toList()
    override suspend fun saveScheduleBlock(block: ScheduleBlock) { scheduleBlocks.add(block) }
    override suspend fun deleteScheduleBlock(block: ScheduleBlock) { scheduleBlocks.remove(block) }
    override suspend fun clearScheduleBlocks() { scheduleBlocks.clear() }

    override fun getCustomKPIsFlow(): Flow<List<CustomKPI>> = MutableStateFlow(customKPIs.toList())
    override suspend fun getAllCustomKPIs(): List<CustomKPI> = customKPIs.toList()
    override suspend fun saveCustomKPI(kpi: CustomKPI) { customKPIs.add(kpi) }
    override suspend fun deleteCustomKPI(kpi: CustomKPI) { customKPIs.remove(kpi) }
    override suspend fun clearCustomKPIs() { customKPIs.clear() }

    override fun getIronRulesFlow(): Flow<List<IronRule>> = MutableStateFlow(ironRules.toList())
    override suspend fun getAllIronRules(): List<IronRule> = ironRules.toList()
    override suspend fun saveIronRule(rule: IronRule) { ironRules.add(rule) }
    override suspend fun deleteIronRule(rule: IronRule) { ironRules.remove(rule) }
    override suspend fun clearIronRules() { ironRules.clear() }

    override fun getHardTruthsOrAffirmationsFlow(): Flow<List<HardTruthOrAffirmation>> = MutableStateFlow(hardTruths.toList())
    override suspend fun getAllHardTruthsOrAffirmations(): List<HardTruthOrAffirmation> = hardTruths.toList()
    override suspend fun saveHardTruthOrAffirmation(entry: HardTruthOrAffirmation) { hardTruths.add(entry) }
    override suspend fun deleteHardTruthOrAffirmation(entry: HardTruthOrAffirmation) { hardTruths.remove(entry) }
    override suspend fun clearHardTruthsOrAffirmations() { hardTruths.clear() }

    override fun getMajorMilestonesFlow(): Flow<List<MajorMilestone>> = MutableStateFlow(milestones.toList())
    override suspend fun getAllMajorMilestones(): List<MajorMilestone> = milestones.toList()
    override suspend fun saveMajorMilestone(milestone: MajorMilestone) { milestones.add(milestone) }
    override suspend fun deleteMajorMilestone(milestone: MajorMilestone) { milestones.remove(milestone) }
    override suspend fun clearMajorMilestones() { milestones.clear() }

    override fun getKeyRelationshipsFlow(): Flow<List<KeyRelationship>> = MutableStateFlow(relationships.toList())
    override suspend fun getAllKeyRelationships(): List<KeyRelationship> = relationships.toList()
    override suspend fun saveKeyRelationship(relation: KeyRelationship) { relationships.add(relation) }
    override suspend fun deleteKeyRelationship(relation: KeyRelationship) { relationships.remove(relation) }
    override suspend fun clearKeyRelationships() { relationships.clear() }

    override fun getFinancialCheckpointsFlow(): Flow<List<FinancialCheckpoint>> = MutableStateFlow(financialCheckpoints.toList())
    override suspend fun getAllFinancialCheckpoints(): List<FinancialCheckpoint> = financialCheckpoints.toList()
    override suspend fun saveFinancialCheckpoint(checkpoint: FinancialCheckpoint) { financialCheckpoints.add(checkpoint) }
    override suspend fun deleteFinancialCheckpoint(checkpoint: FinancialCheckpoint) { financialCheckpoints.remove(checkpoint) }
    override suspend fun clearFinancialCheckpoints() { financialCheckpoints.clear() }

    override fun getMonthlyIncomeEntriesFlow(): Flow<List<MonthlyIncomeEntry>> = MutableStateFlow(monthlyIncome.toList())
    override suspend fun getAllMonthlyIncomeEntries(): List<MonthlyIncomeEntry> = monthlyIncome.toList()
    override suspend fun saveMonthlyIncomeEntry(entry: MonthlyIncomeEntry) { monthlyIncome.add(entry) }
    override suspend fun deleteMonthlyIncomeEntry(entry: MonthlyIncomeEntry) { monthlyIncome.remove(entry) }
    override suspend fun clearMonthlyIncomeEntries() { monthlyIncome.clear() }

    override fun getIronRuleViolationLogsFlow(): Flow<List<IronRuleViolationLog>> = MutableStateFlow(violationLogs.toList())
    override suspend fun getAllIronRuleViolationLogs(): List<IronRuleViolationLog> = violationLogs.toList()
    override suspend fun saveIronRuleViolationLog(log: IronRuleViolationLog) { violationLogs.add(log) }
    override suspend fun deleteIronRuleViolationLog(log: IronRuleViolationLog) { violationLogs.remove(log) }
    override suspend fun clearIronRuleViolationLogs() { violationLogs.clear() }

    override fun isFinancialModuleEnabledFlow(): Flow<Boolean> = _financialEnabled
    override suspend fun setFinancialModuleEnabled(enabled: Boolean) { _financialEnabled.value = enabled }
}
