package com.axiom.app.domain.repository

import com.axiom.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface WarriorProfileRepository {

    // === WarriorProfile ===
    fun getProfileFlow(id: String = "default"): Flow<WarriorProfile?>
    suspend fun getProfile(id: String = "default"): WarriorProfile?
    suspend fun saveProfile(profile: WarriorProfile)

    // === Track ===
    fun getTracksFlow(): Flow<List<Track>>
    suspend fun getAllTracks(): List<Track>
    suspend fun getTrackById(id: String): Track?
    suspend fun saveTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    suspend fun clearTracks()

    // === ScheduleBlock ===
    fun getScheduleBlocksFlow(): Flow<List<ScheduleBlock>>
    suspend fun getAllScheduleBlocks(): List<ScheduleBlock>
    suspend fun saveScheduleBlock(block: ScheduleBlock)
    suspend fun deleteScheduleBlock(block: ScheduleBlock)
    suspend fun clearScheduleBlocks()

    // === CustomKPI ===
    fun getCustomKPIsFlow(): Flow<List<CustomKPI>>
    suspend fun getAllCustomKPIs(): List<CustomKPI>
    suspend fun saveCustomKPI(kpi: CustomKPI)
    suspend fun deleteCustomKPI(kpi: CustomKPI)
    suspend fun clearCustomKPIs()

    // === IronRule ===
    fun getIronRulesFlow(): Flow<List<IronRule>>
    suspend fun getAllIronRules(): List<IronRule>
    suspend fun saveIronRule(rule: IronRule)
    suspend fun deleteIronRule(rule: IronRule)
    suspend fun clearIronRules()

    // === HardTruthOrAffirmation ===
    fun getHardTruthsOrAffirmationsFlow(): Flow<List<HardTruthOrAffirmation>>
    suspend fun getAllHardTruthsOrAffirmations(): List<HardTruthOrAffirmation>
    suspend fun saveHardTruthOrAffirmation(entry: HardTruthOrAffirmation)
    suspend fun deleteHardTruthOrAffirmation(entry: HardTruthOrAffirmation)
    suspend fun clearHardTruthsOrAffirmations()

    // === MajorMilestone ===
    fun getMajorMilestonesFlow(): Flow<List<MajorMilestone>>
    suspend fun getAllMajorMilestones(): List<MajorMilestone>
    suspend fun saveMajorMilestone(milestone: MajorMilestone)
    suspend fun deleteMajorMilestone(milestone: MajorMilestone)
    suspend fun clearMajorMilestones()

    // === KeyRelationship ===
    fun getKeyRelationshipsFlow(): Flow<List<KeyRelationship>>
    suspend fun getAllKeyRelationships(): List<KeyRelationship>
    suspend fun saveKeyRelationship(relation: KeyRelationship)
    suspend fun deleteKeyRelationship(relation: KeyRelationship)
    suspend fun clearKeyRelationships()

    // === FinancialCheckpoint ===
    fun getFinancialCheckpointsFlow(): Flow<List<FinancialCheckpoint>>
    suspend fun getAllFinancialCheckpoints(): List<FinancialCheckpoint>
    suspend fun saveFinancialCheckpoint(checkpoint: FinancialCheckpoint)
    suspend fun deleteFinancialCheckpoint(checkpoint: FinancialCheckpoint)
    suspend fun clearFinancialCheckpoints()

    // === MonthlyIncomeEntry ===
    fun getMonthlyIncomeEntriesFlow(): Flow<List<MonthlyIncomeEntry>>
    suspend fun getAllMonthlyIncomeEntries(): List<MonthlyIncomeEntry>
    suspend fun saveMonthlyIncomeEntry(entry: MonthlyIncomeEntry)
    suspend fun deleteMonthlyIncomeEntry(entry: MonthlyIncomeEntry)
    suspend fun clearMonthlyIncomeEntries()

    // === IronRuleViolationLog ===
    fun getIronRuleViolationLogsFlow(): Flow<List<IronRuleViolationLog>>
    suspend fun getAllIronRuleViolationLogs(): List<IronRuleViolationLog>
    suspend fun saveIronRuleViolationLog(log: IronRuleViolationLog)
    suspend fun deleteIronRuleViolationLog(log: IronRuleViolationLog)
    suspend fun clearIronRuleViolationLogs()

    // === System Configuration for Financial ===
    fun isFinancialModuleEnabledFlow(): Flow<Boolean>
    suspend fun setFinancialModuleEnabled(enabled: Boolean)
}
