// RULE: every entity change MUST bump `version` AND add a Migration in db/migrations/. Never use fallbackToDestructiveMigration in production.
package com.axiom.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.axiom.app.data.local.dao.*
import com.axiom.app.data.local.entity.*

@Database(
    entities = [
        HunterEntity::class,
        SkillEntity::class,
        MissionEntity::class,
        DungeonEntity::class,
        ShadowEntity::class,
        StreakEntity::class,
        SystemFeedEntity::class,
        WarriorProfileEntity::class,
        TrackEntity::class,
        ScheduleBlockEntity::class,
        CustomKPIEntity::class,
        IronRuleEntity::class,
        HardTruthOrAffirmationEntity::class,
        MajorMilestoneEntity::class,
        KeyRelationshipEntity::class,
        FinancialCheckpointEntity::class,
        MonthlyIncomeEntryEntity::class,
        MuscleGroupEntity::class,
        VitalLogEntity::class,
        KPIProgressEntity::class,
        KPIMissStreakEntity::class,
        IronRuleViolationLogEntity::class,
        DailyHabitLogEntity::class,
        WeeklyReviewEntity::class
    ],
    version = 16,
    exportSchema = true
)
abstract class AxiomDatabase : RoomDatabase() {
    abstract fun hunterDao(): HunterDao
    abstract fun missionDao(): MissionDao
    abstract fun dungeonDao(): DungeonDao
    abstract fun skillDao(): SkillDao
    abstract fun shadowDao(): ShadowDao
    abstract fun streakDao(): StreakDao
    abstract fun systemFeedDao(): SystemFeedDao
    abstract fun warriorBlueprintDao(): WarriorBlueprintDao
    abstract fun muscleGroupDao(): MuscleGroupDao
    abstract fun vitalLogDao(): VitalLogDao
    abstract fun kpiProgressDao(): KPIProgressDao
    abstract fun dailyHabitLogDao(): DailyHabitLogDao
    abstract fun weeklyReviewDao(): com.axiom.app.data.local.dao.WeeklyReviewDao
}
