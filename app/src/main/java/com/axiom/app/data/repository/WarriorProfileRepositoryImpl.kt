package com.axiom.app.data.repository

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.data.local.dao.WarriorBlueprintDao
import com.axiom.app.data.local.entity.*
import com.axiom.app.domain.model.*
import com.axiom.app.domain.repository.WarriorProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WarriorProfileRepositoryImpl @Inject constructor(
    private val blueprintDao: WarriorBlueprintDao,
    private val preferences: AxiomPreferences
) : WarriorProfileRepository {

    // === WarriorProfile ===
    override fun getProfileFlow(id: String): Flow<WarriorProfile?> =
        blueprintDao.getProfileFlow(id).map { it?.toDomain() }

    override suspend fun getProfile(id: String): WarriorProfile? = withContext(Dispatchers.IO) {
        blueprintDao.getProfile(id)?.toDomain()
    }

    override suspend fun saveProfile(profile: WarriorProfile) = withContext(Dispatchers.IO) {
        blueprintDao.insertProfile(WarriorProfileEntity.fromDomain(profile))
    }


    // === Track ===
    override fun getTracksFlow(): Flow<List<Track>> =
        blueprintDao.getTracksFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllTracks(): List<Track> = withContext(Dispatchers.IO) {
        blueprintDao.getAllTracks().map { it.toDomain() }
    }

    override suspend fun getTrackById(id: String): Track? = withContext(Dispatchers.IO) {
        blueprintDao.getTrackById(id)?.toDomain()
    }

    override suspend fun saveTrack(track: Track) = withContext(Dispatchers.IO) {
        blueprintDao.insertTrack(TrackEntity.fromDomain(track))
    }

    override suspend fun deleteTrack(track: Track) = withContext(Dispatchers.IO) {
        blueprintDao.deleteTrack(TrackEntity.fromDomain(track))
    }

    override suspend fun clearTracks() = withContext(Dispatchers.IO) {
        blueprintDao.clearTracks()
    }


    // === ScheduleBlock ===
    override fun getScheduleBlocksFlow(): Flow<List<ScheduleBlock>> =
        blueprintDao.getScheduleBlocksFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllScheduleBlocks(): List<ScheduleBlock> = withContext(Dispatchers.IO) {
        blueprintDao.getAllScheduleBlocks().map { it.toDomain() }
    }

    override suspend fun saveScheduleBlock(block: ScheduleBlock) = withContext(Dispatchers.IO) {
        blueprintDao.insertScheduleBlock(ScheduleBlockEntity.fromDomain(block))
    }

    override suspend fun deleteScheduleBlock(block: ScheduleBlock) = withContext(Dispatchers.IO) {
        blueprintDao.deleteScheduleBlock(ScheduleBlockEntity.fromDomain(block))
    }

    override suspend fun clearScheduleBlocks() = withContext(Dispatchers.IO) {
        blueprintDao.clearScheduleBlocks()
    }


    // === CustomKPI ===
    override fun getCustomKPIsFlow(): Flow<List<CustomKPI>> =
        blueprintDao.getCustomKPIsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllCustomKPIs(): List<CustomKPI> = withContext(Dispatchers.IO) {
        blueprintDao.getAllCustomKPIs().map { it.toDomain() }
    }

    override suspend fun saveCustomKPI(kpi: CustomKPI) = withContext(Dispatchers.IO) {
        blueprintDao.insertCustomKPI(CustomKPIEntity.fromDomain(kpi))
    }

    override suspend fun deleteCustomKPI(kpi: CustomKPI) = withContext(Dispatchers.IO) {
        blueprintDao.deleteCustomKPI(CustomKPIEntity.fromDomain(kpi))
    }

    override suspend fun clearCustomKPIs() = withContext(Dispatchers.IO) {
        blueprintDao.clearCustomKPIs()
    }


    // === IronRule ===
    override fun getIronRulesFlow(): Flow<List<IronRule>> =
        blueprintDao.getIronRulesFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllIronRules(): List<IronRule> = withContext(Dispatchers.IO) {
        blueprintDao.getAllIronRules().map { it.toDomain() }
    }

    override suspend fun saveIronRule(rule: IronRule) = withContext(Dispatchers.IO) {
        blueprintDao.insertIronRule(IronRuleEntity.fromDomain(rule))
    }

    override suspend fun deleteIronRule(rule: IronRule) = withContext(Dispatchers.IO) {
        blueprintDao.deleteIronRule(IronRuleEntity.fromDomain(rule))
    }

    override suspend fun clearIronRules() = withContext(Dispatchers.IO) {
        blueprintDao.clearIronRules()
    }


    // === HardTruthOrAffirmation ===
    override fun getHardTruthsOrAffirmationsFlow(): Flow<List<HardTruthOrAffirmation>> =
        blueprintDao.getHardTruthsOrAffirmationsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllHardTruthsOrAffirmations(): List<HardTruthOrAffirmation> = withContext(Dispatchers.IO) {
        blueprintDao.getAllHardTruthsOrAffirmations().map { it.toDomain() }
    }

    override suspend fun saveHardTruthOrAffirmation(entry: HardTruthOrAffirmation) = withContext(Dispatchers.IO) {
        blueprintDao.insertHardTruthOrAffirmation(HardTruthOrAffirmationEntity.fromDomain(entry))
    }

    override suspend fun deleteHardTruthOrAffirmation(entry: HardTruthOrAffirmation) = withContext(Dispatchers.IO) {
        blueprintDao.deleteHardTruthOrAffirmation(HardTruthOrAffirmationEntity.fromDomain(entry))
    }

    override suspend fun clearHardTruthsOrAffirmations() = withContext(Dispatchers.IO) {
        blueprintDao.clearHardTruthsOrAffirmations()
    }


    // === MajorMilestone ===
    override fun getMajorMilestonesFlow(): Flow<List<MajorMilestone>> =
        blueprintDao.getMajorMilestonesFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllMajorMilestones(): List<MajorMilestone> = withContext(Dispatchers.IO) {
        blueprintDao.getAllMajorMilestones().map { it.toDomain() }
    }

    override suspend fun saveMajorMilestone(milestone: MajorMilestone) = withContext(Dispatchers.IO) {
        blueprintDao.insertMajorMilestone(MajorMilestoneEntity.fromDomain(milestone))
    }

    override suspend fun deleteMajorMilestone(milestone: MajorMilestone) = withContext(Dispatchers.IO) {
        blueprintDao.deleteMajorMilestone(MajorMilestoneEntity.fromDomain(milestone))
    }

    override suspend fun clearMajorMilestones() = withContext(Dispatchers.IO) {
        blueprintDao.clearMajorMilestones()
    }


    // === KeyRelationship ===
    override fun getKeyRelationshipsFlow(): Flow<List<KeyRelationship>> =
        blueprintDao.getKeyRelationshipsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllKeyRelationships(): List<KeyRelationship> = withContext(Dispatchers.IO) {
        blueprintDao.getAllKeyRelationships().map { it.toDomain() }
    }

    override suspend fun saveKeyRelationship(relation: KeyRelationship) = withContext(Dispatchers.IO) {
        blueprintDao.insertKeyRelationship(KeyRelationshipEntity.fromDomain(relation))
    }

    override suspend fun deleteKeyRelationship(relation: KeyRelationship) = withContext(Dispatchers.IO) {
        blueprintDao.deleteKeyRelationship(KeyRelationshipEntity.fromDomain(relation))
    }

    override suspend fun clearKeyRelationships() = withContext(Dispatchers.IO) {
        blueprintDao.clearKeyRelationships()
    }


    // === FinancialCheckpoint ===
    override fun getFinancialCheckpointsFlow(): Flow<List<FinancialCheckpoint>> =
        blueprintDao.getFinancialCheckpointsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllFinancialCheckpoints(): List<FinancialCheckpoint> = withContext(Dispatchers.IO) {
        blueprintDao.getAllFinancialCheckpoints().map { it.toDomain() }
    }

    override suspend fun saveFinancialCheckpoint(checkpoint: FinancialCheckpoint) = withContext(Dispatchers.IO) {
        blueprintDao.insertFinancialCheckpoint(FinancialCheckpointEntity.fromDomain(checkpoint))
    }

    override suspend fun deleteFinancialCheckpoint(checkpoint: FinancialCheckpoint) = withContext(Dispatchers.IO) {
        blueprintDao.deleteFinancialCheckpoint(FinancialCheckpointEntity.fromDomain(checkpoint))
    }

    override suspend fun clearFinancialCheckpoints() = withContext(Dispatchers.IO) {
        blueprintDao.clearFinancialCheckpoints()
    }


    // === MonthlyIncomeEntry ===
    override fun getMonthlyIncomeEntriesFlow(): Flow<List<MonthlyIncomeEntry>> =
        blueprintDao.getMonthlyIncomeEntriesFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllMonthlyIncomeEntries(): List<MonthlyIncomeEntry> = withContext(Dispatchers.IO) {
        blueprintDao.getAllMonthlyIncomeEntries().map { it.toDomain() }
    }

    override suspend fun saveMonthlyIncomeEntry(entry: MonthlyIncomeEntry) = withContext(Dispatchers.IO) {
        blueprintDao.insertMonthlyIncomeEntry(MonthlyIncomeEntryEntity.fromDomain(entry))
    }

    override suspend fun deleteMonthlyIncomeEntry(entry: MonthlyIncomeEntry) = withContext(Dispatchers.IO) {
        blueprintDao.deleteMonthlyIncomeEntry(MonthlyIncomeEntryEntity.fromDomain(entry))
    }

    override suspend fun clearMonthlyIncomeEntries() = withContext(Dispatchers.IO) {
        blueprintDao.clearMonthlyIncomeEntries()
    }


    // === IronRuleViolationLog ===
    override fun getIronRuleViolationLogsFlow(): Flow<List<IronRuleViolationLog>> =
        blueprintDao.getIronRuleViolationLogsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun getAllIronRuleViolationLogs(): List<IronRuleViolationLog> = withContext(Dispatchers.IO) {
        blueprintDao.getAllIronRuleViolationLogs().map { it.toDomain() }
    }

    override suspend fun saveIronRuleViolationLog(log: IronRuleViolationLog) = withContext(Dispatchers.IO) {
        blueprintDao.insertIronRuleViolationLog(IronRuleViolationLogEntity.fromDomain(log))
    }

    override suspend fun deleteIronRuleViolationLog(log: IronRuleViolationLog) = withContext(Dispatchers.IO) {
        blueprintDao.deleteIronRuleViolationLog(IronRuleViolationLogEntity.fromDomain(log))
    }

    override suspend fun clearIronRuleViolationLogs() = withContext(Dispatchers.IO) {
        blueprintDao.clearIronRuleViolationLogs()
    }

    // === System Configuration for Financial ===
    override fun isFinancialModuleEnabledFlow(): Flow<Boolean> =
        preferences.financialModuleEnabledFlow

    override suspend fun setFinancialModuleEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        preferences.setFinancialModuleEnabled(enabled)
    }
}
