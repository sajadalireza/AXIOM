package com.axiom.app.di

import android.content.Context
import androidx.room.Room
import com.axiom.app.data.local.AxiomDatabase
import com.axiom.app.data.local.dao.*
import com.axiom.app.data.repository.*
import com.axiom.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindHunterRepository(
        impl: HunterRepositoryImpl
    ): HunterRepository

    // WP-205 — single atomic-completion transactional boundary (owns database.withTransaction).
    @Binds
    @Singleton
    abstract fun bindAtomicCompletionRepository(
        impl: AtomicCompletionRepositoryImpl
    ): AtomicCompletionRepository

    @Binds
    @Singleton
    abstract fun bindFirstWinStateStore(
        impl: RoomFirstWinStateStore
    ): com.axiom.app.domain.firstwin.FirstWinStateStore

    @Binds
    @Singleton
    abstract fun bindFirstWinLifecycleStore(
        impl: RoomFirstWinLifecycleStore
    ): com.axiom.app.domain.firstwin.FirstWinLifecycleStore

    @Binds
    @Singleton
    abstract fun bindFirstWinMissionStore(
        impl: RoomFirstWinMissionStore
    ): com.axiom.app.domain.firstwin.FirstWinMissionStore

    @Binds
    @Singleton
    abstract fun bindMissionRepository(
        impl: MissionRepositoryImpl
    ): MissionRepository

    @Binds
    @Singleton
    abstract fun bindDungeonRepository(
        impl: DungeonRepositoryImpl
    ): DungeonRepository

    @Binds
    @Singleton
    abstract fun bindSkillRepository(
        impl: SkillRepositoryImpl
    ): SkillRepository

    @Binds
    @Singleton
    abstract fun bindShadowRepository(
        impl: ShadowRepositoryImpl
    ): ShadowRepository

    @Binds
    @Singleton
    abstract fun bindSystemFeedRepository(
        impl: SystemFeedRepositoryImpl
    ): SystemFeedRepository

    @Binds
    @Singleton
    abstract fun bindStreakRepository(
        impl: StreakRepositoryImpl
    ): StreakRepository

    @Binds
    @Singleton
    abstract fun bindActivationRepository(
        impl: ActivationRepositoryImpl
    ): ActivationRepository

    @Binds
    @Singleton
    abstract fun bindLeagueRepository(
        impl: LeagueRepositoryImpl
    ): LeagueRepository

    @Binds
    @Singleton
    abstract fun bindCloudSyncRepository(
        impl: CloudSyncRepositoryImpl
    ): CloudSyncRepository

    @Binds
    @Singleton
    abstract fun bindWarriorProfileRepository(
        impl: WarriorProfileRepositoryImpl
    ): WarriorProfileRepository

    @Binds
    @Singleton
    abstract fun bindMuscleGroupRepository(
        impl: MuscleGroupRepositoryImpl
    ): MuscleGroupRepository

    @Binds
    @Singleton
    abstract fun bindVitalsRepository(
        impl: VitalsRepositoryImpl
    ): VitalsRepository

    @Binds
    @Singleton
    abstract fun bindDailyHabitLogRepository(
        impl: DailyHabitLogRepositoryImpl
    ): DailyHabitLogRepository

    // WP-206 — analytics ports. Store (Room event_queue, analytics rows only) + the SINGLE
    // network egress authority (Supabase). Bound here so the drain worker and gateway resolve them.
    @Binds
    @Singleton
    abstract fun bindAnalyticsEventStore(
        impl: com.axiom.app.data.analytics.RoomAnalyticsEventStore
    ): com.axiom.app.domain.analytics.AnalyticsEventStore

    @Binds
    @Singleton
    abstract fun bindAnalyticsUploader(
        impl: com.axiom.app.data.analytics.SupabaseAnalyticsUploader
    ): com.axiom.app.domain.analytics.AnalyticsUploader

    companion object {
        /**
         * WP-202: adapt the concrete [com.axiom.app.data.local.AxiomPreferences] to the
         * narrow [com.axiom.app.data.SeedPreferences] seam consumed by
         * [com.axiom.app.data.SeedDataHelper]. AxiomPreferences already exposes every
         * member with matching signatures; this thin delegate lets unit tests substitute
         * a pure-JVM fake without touching AxiomPreferences itself.
         */
        @Provides
        @Singleton
        fun provideSeedPreferences(
            prefs: com.axiom.app.data.local.AxiomPreferences
        ): com.axiom.app.data.SeedPreferences = object : com.axiom.app.data.SeedPreferences {
            override val skillTreeSeededFlow get() = prefs.skillTreeSeededFlow
            override suspend fun setSkillTreeSeeded(value: Boolean) = prefs.setSkillTreeSeeded(value)
            override val muscleGroupsSeededFlow get() = prefs.muscleGroupsSeededFlow
            override suspend fun setMuscleGroupsSeeded(value: Boolean) = prefs.setMuscleGroupsSeeded(value)
            override val alirezaProfileSeededFlow get() = prefs.alirezaProfileSeededFlow
            override suspend fun setAlirezaProfileSeeded(value: Boolean) = prefs.setAlirezaProfileSeeded(value)
            override val setupCompleteFlow get() = prefs.setupCompleteFlow
            override suspend fun setSetupComplete() = prefs.setSetupComplete()
            override val firstMissionDoneFlow get() = prefs.firstMissionDoneFlow
            override suspend fun setFirstMissionDone(value: Boolean) = prefs.setFirstMissionDone(value)
            override val blueprintSetupCompleteFlow get() = prefs.blueprintSetupCompleteFlow
            override suspend fun setBlueprintSetupComplete(value: Boolean) = prefs.setBlueprintSetupComplete(value)
            override val financialModuleEnabledFlow get() = prefs.financialModuleEnabledFlow
            override suspend fun setFinancialModuleEnabled(value: Boolean) = prefs.setFinancialModuleEnabled(value)
        }

        @Provides
        @Singleton
        fun provideAxiomDatabase(
            @ApplicationContext context: Context
        ): AxiomDatabase {
            return Room.databaseBuilder(
                context,
                AxiomDatabase::class.java,
                "axiom.db"
            )
            .addMigrations(
                com.axiom.app.db.migrations.MIGRATION_1_6,
                com.axiom.app.db.migrations.MIGRATION_6_7,
                com.axiom.app.db.migrations.MIGRATION_7_8,
                com.axiom.app.db.migrations.MIGRATION_8_9,
                com.axiom.app.db.migrations.MIGRATION_9_10,
                com.axiom.app.db.migrations.MIGRATION_10_11,
                com.axiom.app.db.migrations.MIGRATION_11_12,
                com.axiom.app.db.migrations.MIGRATION_12_13,
                com.axiom.app.db.migrations.MIGRATION_13_14,
                com.axiom.app.db.migrations.MIGRATION_14_15,
                com.axiom.app.db.migrations.MIGRATION_15_16,
                com.axiom.app.db.migrations.MIGRATION_16_17,
                com.axiom.app.db.migrations.MIGRATION_17_18
            )
            // No fallbackToDestructiveMigration: an unresolved migration path
            // must crash loudly (Room throws IllegalStateException) instead of
            // silently wiping missions/streaks/financial data on-device.
            .build()
        }

        @Provides
        fun provideHunterDao(db: AxiomDatabase): HunterDao = db.hunterDao()

        @Provides
        fun provideMissionDao(db: AxiomDatabase): MissionDao = db.missionDao()

        @Provides
        fun provideDungeonDao(db: AxiomDatabase): DungeonDao = db.dungeonDao()

        @Provides
        fun provideSkillDao(db: AxiomDatabase): SkillDao = db.skillDao()

        @Provides
        fun provideShadowDao(db: AxiomDatabase): ShadowDao = db.shadowDao()

        @Provides
        fun provideStreakDao(db: AxiomDatabase): StreakDao = db.streakDao()

        @Provides
        fun provideSystemFeedDao(db: AxiomDatabase): SystemFeedDao = db.systemFeedDao()

        @Provides
        fun provideWarriorBlueprintDao(db: AxiomDatabase): com.axiom.app.data.local.dao.WarriorBlueprintDao = db.warriorBlueprintDao()

        @Provides
        fun provideMuscleGroupDao(db: AxiomDatabase): MuscleGroupDao = db.muscleGroupDao()

        @Provides
        fun provideVitalLogDao(db: AxiomDatabase): VitalLogDao = db.vitalLogDao()

        @Provides
        fun provideKPIProgressDao(db: AxiomDatabase): KPIProgressDao = db.kpiProgressDao()

        @Provides
        fun provideDailyHabitLogDao(db: AxiomDatabase): DailyHabitLogDao = db.dailyHabitLogDao()

        @Provides
        fun provideWeeklyReviewDao(db: AxiomDatabase): com.axiom.app.data.local.dao.WeeklyReviewDao = db.weeklyReviewDao()
    }
}
