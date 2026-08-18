package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WarriorBlueprintDao {

    // === WarriorProfile ===
    @Query("SELECT * FROM warrior_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String = "default"): WarriorProfileEntity?

    @Query("SELECT * FROM warrior_profiles WHERE id = :id LIMIT 1")
    fun getProfileFlow(id: String = "default"): Flow<WarriorProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: WarriorProfileEntity)


    // === Track ===
    @Query("SELECT * FROM tracks ORDER BY id ASC")
    fun getTracksFlow(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks ORDER BY id ASC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE id = :id LIMIT 1")
    suspend fun getTrackById(id: String): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("DELETE FROM tracks")
    suspend fun clearTracks()


    // === ScheduleBlock ===
    @Query("SELECT * FROM schedule_blocks ORDER BY startTime ASC")
    fun getScheduleBlocksFlow(): Flow<List<ScheduleBlockEntity>>

    @Query("SELECT * FROM schedule_blocks ORDER BY startTime ASC")
    suspend fun getAllScheduleBlocks(): List<ScheduleBlockEntity>

    @Query("SELECT * FROM schedule_blocks WHERE id = :id LIMIT 1")
    suspend fun getScheduleBlockById(id: String): ScheduleBlockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleBlock(block: ScheduleBlockEntity)

    @Delete
    suspend fun deleteScheduleBlock(block: ScheduleBlockEntity)

    @Query("DELETE FROM schedule_blocks")
    suspend fun clearScheduleBlocks()


    // === CustomKPI ===
    @Query("SELECT * FROM custom_kpis ORDER BY id ASC")
    fun getCustomKPIsFlow(): Flow<List<CustomKPIEntity>>

    @Query("SELECT * FROM custom_kpis ORDER BY id ASC")
    suspend fun getAllCustomKPIs(): List<CustomKPIEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomKPI(kpi: CustomKPIEntity)

    @Delete
    suspend fun deleteCustomKPI(kpi: CustomKPIEntity)

    @Query("DELETE FROM custom_kpis")
    suspend fun clearCustomKPIs()


    // === IronRule ===
    @Query("SELECT * FROM iron_rules ORDER BY orderIndex ASC")
    fun getIronRulesFlow(): Flow<List<IronRuleEntity>>

    @Query("SELECT * FROM iron_rules ORDER BY orderIndex ASC")
    suspend fun getAllIronRules(): List<IronRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIronRule(rule: IronRuleEntity)

    @Delete
    suspend fun deleteIronRule(rule: IronRuleEntity)

    @Query("DELETE FROM iron_rules")
    suspend fun clearIronRules()


    // === HardTruthOrAffirmation ===
    @Query("SELECT * FROM hard_truths_affirmations ORDER BY orderIndex ASC")
    fun getHardTruthsOrAffirmationsFlow(): Flow<List<HardTruthOrAffirmationEntity>>

    @Query("SELECT * FROM hard_truths_affirmations ORDER BY orderIndex ASC")
    suspend fun getAllHardTruthsOrAffirmations(): List<HardTruthOrAffirmationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHardTruthOrAffirmation(entry: HardTruthOrAffirmationEntity)

    @Delete
    suspend fun deleteHardTruthOrAffirmation(entry: HardTruthOrAffirmationEntity)

    @Query("DELETE FROM hard_truths_affirmations")
    suspend fun clearHardTruthsOrAffirmations()


    // === MajorMilestone ===
    @Query("SELECT * FROM major_milestones ORDER BY targetDate ASC")
    fun getMajorMilestonesFlow(): Flow<List<MajorMilestoneEntity>>

    @Query("SELECT * FROM major_milestones ORDER BY targetDate ASC")
    suspend fun getAllMajorMilestones(): List<MajorMilestoneEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMajorMilestone(milestone: MajorMilestoneEntity)

    @Delete
    suspend fun deleteMajorMilestone(milestone: MajorMilestoneEntity)

    @Query("DELETE FROM major_milestones")
    suspend fun clearMajorMilestones()


    // === KeyRelationship ===
    @Query("SELECT * FROM key_relationships ORDER BY label ASC")
    fun getKeyRelationshipsFlow(): Flow<List<KeyRelationshipEntity>>

    @Query("SELECT * FROM key_relationships ORDER BY label ASC")
    suspend fun getAllKeyRelationships(): List<KeyRelationshipEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyRelationship(relation: KeyRelationshipEntity)

    @Delete
    suspend fun deleteKeyRelationship(relation: KeyRelationshipEntity)

    @Query("DELETE FROM key_relationships")
    suspend fun clearKeyRelationships()


    // === FinancialCheckpoint ===
    @Query("SELECT * FROM financial_checkpoints ORDER BY monthIndex ASC")
    fun getFinancialCheckpointsFlow(): Flow<List<FinancialCheckpointEntity>>

    @Query("SELECT * FROM financial_checkpoints ORDER BY monthIndex ASC")
    suspend fun getAllFinancialCheckpoints(): List<FinancialCheckpointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialCheckpoint(checkpoint: FinancialCheckpointEntity)

    @Delete
    suspend fun deleteFinancialCheckpoint(checkpoint: FinancialCheckpointEntity)

    @Query("DELETE FROM financial_checkpoints")
    suspend fun clearFinancialCheckpoints()


    // === MonthlyIncomeEntry ===
    @Query("SELECT * FROM monthly_income_entries ORDER BY monthIndex ASC")
    fun getMonthlyIncomeEntriesFlow(): Flow<List<MonthlyIncomeEntryEntity>>

    @Query("SELECT * FROM monthly_income_entries ORDER BY monthIndex ASC")
    suspend fun getAllMonthlyIncomeEntries(): List<MonthlyIncomeEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyIncomeEntry(entry: MonthlyIncomeEntryEntity)

    @Delete
    suspend fun deleteMonthlyIncomeEntry(entry: MonthlyIncomeEntryEntity)

    @Query("DELETE FROM monthly_income_entries")
    suspend fun clearMonthlyIncomeEntries()


    // === IronRuleViolationLog ===
    @Query("SELECT * FROM iron_rule_violation_logs ORDER BY date DESC")
    fun getIronRuleViolationLogsFlow(): Flow<List<IronRuleViolationLogEntity>>

    @Query("SELECT * FROM iron_rule_violation_logs ORDER BY date DESC")
    suspend fun getAllIronRuleViolationLogs(): List<IronRuleViolationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIronRuleViolationLog(log: IronRuleViolationLogEntity)

    @Delete
    suspend fun deleteIronRuleViolationLog(log: IronRuleViolationLogEntity)

    @Query("DELETE FROM iron_rule_violation_logs")
    suspend fun clearIronRuleViolationLogs()
}
