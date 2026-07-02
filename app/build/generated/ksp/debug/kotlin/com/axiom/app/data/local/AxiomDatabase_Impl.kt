package com.axiom.app.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.axiom.app.`data`.local.dao.DailyHabitLogDao
import com.axiom.app.`data`.local.dao.DailyHabitLogDao_Impl
import com.axiom.app.`data`.local.dao.DungeonDao
import com.axiom.app.`data`.local.dao.DungeonDao_Impl
import com.axiom.app.`data`.local.dao.HunterDao
import com.axiom.app.`data`.local.dao.HunterDao_Impl
import com.axiom.app.`data`.local.dao.KPIProgressDao
import com.axiom.app.`data`.local.dao.KPIProgressDao_Impl
import com.axiom.app.`data`.local.dao.MissionDao
import com.axiom.app.`data`.local.dao.MissionDao_Impl
import com.axiom.app.`data`.local.dao.MuscleGroupDao
import com.axiom.app.`data`.local.dao.MuscleGroupDao_Impl
import com.axiom.app.`data`.local.dao.ShadowDao
import com.axiom.app.`data`.local.dao.ShadowDao_Impl
import com.axiom.app.`data`.local.dao.SkillDao
import com.axiom.app.`data`.local.dao.SkillDao_Impl
import com.axiom.app.`data`.local.dao.StreakDao
import com.axiom.app.`data`.local.dao.StreakDao_Impl
import com.axiom.app.`data`.local.dao.SystemFeedDao
import com.axiom.app.`data`.local.dao.SystemFeedDao_Impl
import com.axiom.app.`data`.local.dao.VitalLogDao
import com.axiom.app.`data`.local.dao.VitalLogDao_Impl
import com.axiom.app.`data`.local.dao.WarriorBlueprintDao
import com.axiom.app.`data`.local.dao.WarriorBlueprintDao_Impl
import com.axiom.app.`data`.local.dao.WeeklyReviewDao
import com.axiom.app.`data`.local.dao.WeeklyReviewDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AxiomDatabase_Impl : AxiomDatabase() {
  private val _hunterDao: Lazy<HunterDao> = lazy {
    HunterDao_Impl(this)
  }

  private val _missionDao: Lazy<MissionDao> = lazy {
    MissionDao_Impl(this)
  }

  private val _dungeonDao: Lazy<DungeonDao> = lazy {
    DungeonDao_Impl(this)
  }

  private val _skillDao: Lazy<SkillDao> = lazy {
    SkillDao_Impl(this)
  }

  private val _shadowDao: Lazy<ShadowDao> = lazy {
    ShadowDao_Impl(this)
  }

  private val _streakDao: Lazy<StreakDao> = lazy {
    StreakDao_Impl(this)
  }

  private val _systemFeedDao: Lazy<SystemFeedDao> = lazy {
    SystemFeedDao_Impl(this)
  }

  private val _warriorBlueprintDao: Lazy<WarriorBlueprintDao> = lazy {
    WarriorBlueprintDao_Impl(this)
  }

  private val _muscleGroupDao: Lazy<MuscleGroupDao> = lazy {
    MuscleGroupDao_Impl(this)
  }

  private val _vitalLogDao: Lazy<VitalLogDao> = lazy {
    VitalLogDao_Impl(this)
  }

  private val _kPIProgressDao: Lazy<KPIProgressDao> = lazy {
    KPIProgressDao_Impl(this)
  }

  private val _dailyHabitLogDao: Lazy<DailyHabitLogDao> = lazy {
    DailyHabitLogDao_Impl(this)
  }

  private val _weeklyReviewDao: Lazy<WeeklyReviewDao> = lazy {
    WeeklyReviewDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(16,
        "00fb30ff6d0688d01b1e5fe2c40cc25b", "248579b30bf5b9419c3098e73b461d61") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `hunter_profile` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `level` INTEGER NOT NULL, `rankLabel` TEXT NOT NULL, `totalXP` INTEGER NOT NULL, `currentXP` INTEGER NOT NULL, `xpToNextLevel` INTEGER NOT NULL, `progressPercent` REAL NOT NULL, `rankColor` INTEGER NOT NULL, `rankGlyph` TEXT NOT NULL, `personalThesis` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `skills` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `currentXP` INTEGER NOT NULL, `level` INTEGER NOT NULL, `rankLabel` TEXT NOT NULL, `parentId` TEXT, `isUnlocked` INTEGER NOT NULL, `xpToNextRank` INTEGER NOT NULL, `rankProgressPercent` REAL NOT NULL, `isShadowCandidate` INTEGER NOT NULL, `rankColor` INTEGER NOT NULL, `trackId` TEXT, `totalRawHours` REAL NOT NULL, `totalEffectiveHours` REAL NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `missions` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `track` TEXT NOT NULL, `rarity` TEXT NOT NULL, `skillId` TEXT NOT NULL, `skillName` TEXT NOT NULL, `xpReward` INTEGER NOT NULL, `powerScore` REAL NOT NULL, `status` TEXT NOT NULL, `dungeonId` TEXT, `estimatedHours` REAL NOT NULL, `actualHours` REAL, `createdAt` INTEGER NOT NULL, `completedAt` INTEGER, `rarityColor` INTEGER NOT NULL, `isInstantGate` INTEGER NOT NULL, `description` TEXT NOT NULL, `trackId` TEXT, `scheduleBlockId` TEXT, `qualityScore` REAL NOT NULL, `effectiveHours` REAL NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `dungeons` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `rarity` TEXT NOT NULL, `totalStages` INTEGER NOT NULL, `completedStages` INTEGER NOT NULL, `isBossDefeated` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `completedAt` INTEGER, `stageDescriptions` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `shadows` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `skillId` TEXT NOT NULL, `rankLabel` TEXT NOT NULL, `acquiredAt` INTEGER NOT NULL, `skillCategory` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `streak` (`id` TEXT NOT NULL, `currentStreak` INTEGER NOT NULL, `longestStreak` INTEGER NOT NULL, `lastActivityDate` TEXT, `xpMultiplier` REAL NOT NULL, `streakLabel` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `system_feed` (`id` TEXT NOT NULL, `message` TEXT NOT NULL, `type` TEXT NOT NULL, `xpGained` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `warrior_profiles` (`id` TEXT NOT NULL, `codename` TEXT NOT NULL, `oneLineThesis` TEXT NOT NULL, `rareProfileDescription` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `tracks` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `color` INTEGER NOT NULL, `icon` TEXT NOT NULL, `description` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `schedule_blocks` (`id` TEXT NOT NULL, `trackId` TEXT, `startTime` TEXT NOT NULL, `title` TEXT NOT NULL, `actionDescription` TEXT NOT NULL, `tag` TEXT NOT NULL, `recurrence` TEXT NOT NULL, `isNonNegotiable` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `custom_kpis` (`id` TEXT NOT NULL, `trackId` TEXT, `name` TEXT NOT NULL, `targetValue` REAL NOT NULL, `targetUnit` TEXT NOT NULL, `measurementHint` TEXT NOT NULL, `redFlagAction` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `iron_rules` (`id` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, `ruleText` TEXT NOT NULL, `isAutomatable` INTEGER NOT NULL, `linkedSignalType` TEXT NOT NULL, `linkedKpiId` TEXT, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `hard_truths_affirmations` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `text` TEXT NOT NULL, `orderIndex` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `major_milestones` (`id` TEXT NOT NULL, `label` TEXT NOT NULL, `targetDate` INTEGER NOT NULL, `description` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `key_relationships` (`id` TEXT NOT NULL, `label` TEXT NOT NULL, `category` TEXT NOT NULL, `lastInteractionAt` INTEGER, `preparedTalkingPoint` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `financial_checkpoints` (`id` TEXT NOT NULL, `monthIndex` INTEGER NOT NULL, `targetAmount` REAL NOT NULL, `currency` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `monthly_income_entries` (`id` TEXT NOT NULL, `monthIndex` INTEGER NOT NULL, `actualAmount` REAL NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `muscle_groups` (`id` TEXT NOT NULL, `displayName` TEXT NOT NULL, `strengthScore` INTEGER NOT NULL, `lastTrainedTimestamp` INTEGER, `freshnessPercent` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `vital_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `type` TEXT NOT NULL, `value` REAL NOT NULL, `loggedAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_vital_logs_date_type` ON `vital_logs` (`date`, `type`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `kpi_progress` (`id` TEXT NOT NULL, `kpiId` TEXT NOT NULL, `date` INTEGER NOT NULL, `incrementValue` REAL NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `kpi_miss_streaks` (`kpiId` TEXT NOT NULL, `missStreak` INTEGER NOT NULL, PRIMARY KEY(`kpiId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `iron_rule_violation_logs` (`id` TEXT NOT NULL, `ruleId` TEXT NOT NULL, `date` INTEGER NOT NULL, `wasAutomaticallyDetected` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `daily_habit_logs` (`id` TEXT NOT NULL, `date` TEXT NOT NULL, `waterGlasses` INTEGER NOT NULL, `sleepHours` REAL, `sleepQuality` INTEGER, `teethMorning` INTEGER NOT NULL, `teethEvening` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `weekly_reviews` (`id` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `step1Summary` TEXT NOT NULL, `step2WrongAssumption` TEXT NOT NULL, `step3CriticFeedback` TEXT NOT NULL, `step4DecisionType` TEXT NOT NULL, `step5JournalText` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '00fb30ff6d0688d01b1e5fe2c40cc25b')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `hunter_profile`")
        connection.execSQL("DROP TABLE IF EXISTS `skills`")
        connection.execSQL("DROP TABLE IF EXISTS `missions`")
        connection.execSQL("DROP TABLE IF EXISTS `dungeons`")
        connection.execSQL("DROP TABLE IF EXISTS `shadows`")
        connection.execSQL("DROP TABLE IF EXISTS `streak`")
        connection.execSQL("DROP TABLE IF EXISTS `system_feed`")
        connection.execSQL("DROP TABLE IF EXISTS `warrior_profiles`")
        connection.execSQL("DROP TABLE IF EXISTS `tracks`")
        connection.execSQL("DROP TABLE IF EXISTS `schedule_blocks`")
        connection.execSQL("DROP TABLE IF EXISTS `custom_kpis`")
        connection.execSQL("DROP TABLE IF EXISTS `iron_rules`")
        connection.execSQL("DROP TABLE IF EXISTS `hard_truths_affirmations`")
        connection.execSQL("DROP TABLE IF EXISTS `major_milestones`")
        connection.execSQL("DROP TABLE IF EXISTS `key_relationships`")
        connection.execSQL("DROP TABLE IF EXISTS `financial_checkpoints`")
        connection.execSQL("DROP TABLE IF EXISTS `monthly_income_entries`")
        connection.execSQL("DROP TABLE IF EXISTS `muscle_groups`")
        connection.execSQL("DROP TABLE IF EXISTS `vital_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `kpi_progress`")
        connection.execSQL("DROP TABLE IF EXISTS `kpi_miss_streaks`")
        connection.execSQL("DROP TABLE IF EXISTS `iron_rule_violation_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `daily_habit_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `weekly_reviews`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsHunterProfile: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHunterProfile.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("level", TableInfo.Column("level", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("rankLabel", TableInfo.Column("rankLabel", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("totalXP", TableInfo.Column("totalXP", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("currentXP", TableInfo.Column("currentXP", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("xpToNextLevel", TableInfo.Column("xpToNextLevel", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("progressPercent", TableInfo.Column("progressPercent", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("rankColor", TableInfo.Column("rankColor", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("rankGlyph", TableInfo.Column("rankGlyph", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHunterProfile.put("personalThesis", TableInfo.Column("personalThesis", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHunterProfile: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHunterProfile: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHunterProfile: TableInfo = TableInfo("hunter_profile", _columnsHunterProfile,
            _foreignKeysHunterProfile, _indicesHunterProfile)
        val _existingHunterProfile: TableInfo = read(connection, "hunter_profile")
        if (!_infoHunterProfile.equals(_existingHunterProfile)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |hunter_profile(com.axiom.app.data.local.entity.HunterEntity).
              | Expected:
              |""".trimMargin() + _infoHunterProfile + """
              |
              | Found:
              |""".trimMargin() + _existingHunterProfile)
        }
        val _columnsSkills: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSkills.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("currentXP", TableInfo.Column("currentXP", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("level", TableInfo.Column("level", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("rankLabel", TableInfo.Column("rankLabel", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("parentId", TableInfo.Column("parentId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("isUnlocked", TableInfo.Column("isUnlocked", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("xpToNextRank", TableInfo.Column("xpToNextRank", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("rankProgressPercent", TableInfo.Column("rankProgressPercent", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("isShadowCandidate", TableInfo.Column("isShadowCandidate", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("rankColor", TableInfo.Column("rankColor", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("trackId", TableInfo.Column("trackId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("totalRawHours", TableInfo.Column("totalRawHours", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSkills.put("totalEffectiveHours", TableInfo.Column("totalEffectiveHours", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSkills: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSkills: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSkills: TableInfo = TableInfo("skills", _columnsSkills, _foreignKeysSkills,
            _indicesSkills)
        val _existingSkills: TableInfo = read(connection, "skills")
        if (!_infoSkills.equals(_existingSkills)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |skills(com.axiom.app.data.local.entity.SkillEntity).
              | Expected:
              |""".trimMargin() + _infoSkills + """
              |
              | Found:
              |""".trimMargin() + _existingSkills)
        }
        val _columnsMissions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMissions.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("track", TableInfo.Column("track", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("rarity", TableInfo.Column("rarity", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("skillId", TableInfo.Column("skillId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("skillName", TableInfo.Column("skillName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("xpReward", TableInfo.Column("xpReward", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("powerScore", TableInfo.Column("powerScore", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("dungeonId", TableInfo.Column("dungeonId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("estimatedHours", TableInfo.Column("estimatedHours", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("actualHours", TableInfo.Column("actualHours", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("rarityColor", TableInfo.Column("rarityColor", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("isInstantGate", TableInfo.Column("isInstantGate", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("trackId", TableInfo.Column("trackId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("scheduleBlockId", TableInfo.Column("scheduleBlockId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("qualityScore", TableInfo.Column("qualityScore", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMissions.put("effectiveHours", TableInfo.Column("effectiveHours", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMissions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMissions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMissions: TableInfo = TableInfo("missions", _columnsMissions, _foreignKeysMissions,
            _indicesMissions)
        val _existingMissions: TableInfo = read(connection, "missions")
        if (!_infoMissions.equals(_existingMissions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |missions(com.axiom.app.data.local.entity.MissionEntity).
              | Expected:
              |""".trimMargin() + _infoMissions + """
              |
              | Found:
              |""".trimMargin() + _existingMissions)
        }
        val _columnsDungeons: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDungeons.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("rarity", TableInfo.Column("rarity", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("totalStages", TableInfo.Column("totalStages", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("completedStages", TableInfo.Column("completedStages", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("isBossDefeated", TableInfo.Column("isBossDefeated", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDungeons.put("stageDescriptions", TableInfo.Column("stageDescriptions", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDungeons: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDungeons: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDungeons: TableInfo = TableInfo("dungeons", _columnsDungeons, _foreignKeysDungeons,
            _indicesDungeons)
        val _existingDungeons: TableInfo = read(connection, "dungeons")
        if (!_infoDungeons.equals(_existingDungeons)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |dungeons(com.axiom.app.data.local.entity.DungeonEntity).
              | Expected:
              |""".trimMargin() + _infoDungeons + """
              |
              | Found:
              |""".trimMargin() + _existingDungeons)
        }
        val _columnsShadows: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsShadows.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShadows.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShadows.put("skillId", TableInfo.Column("skillId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShadows.put("rankLabel", TableInfo.Column("rankLabel", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShadows.put("acquiredAt", TableInfo.Column("acquiredAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShadows.put("skillCategory", TableInfo.Column("skillCategory", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysShadows: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesShadows: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoShadows: TableInfo = TableInfo("shadows", _columnsShadows, _foreignKeysShadows,
            _indicesShadows)
        val _existingShadows: TableInfo = read(connection, "shadows")
        if (!_infoShadows.equals(_existingShadows)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |shadows(com.axiom.app.data.local.entity.ShadowEntity).
              | Expected:
              |""".trimMargin() + _infoShadows + """
              |
              | Found:
              |""".trimMargin() + _existingShadows)
        }
        val _columnsStreak: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStreak.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStreak.put("currentStreak", TableInfo.Column("currentStreak", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStreak.put("longestStreak", TableInfo.Column("longestStreak", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStreak.put("lastActivityDate", TableInfo.Column("lastActivityDate", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStreak.put("xpMultiplier", TableInfo.Column("xpMultiplier", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStreak.put("streakLabel", TableInfo.Column("streakLabel", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStreak: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStreak: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoStreak: TableInfo = TableInfo("streak", _columnsStreak, _foreignKeysStreak,
            _indicesStreak)
        val _existingStreak: TableInfo = read(connection, "streak")
        if (!_infoStreak.equals(_existingStreak)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |streak(com.axiom.app.data.local.entity.StreakEntity).
              | Expected:
              |""".trimMargin() + _infoStreak + """
              |
              | Found:
              |""".trimMargin() + _existingStreak)
        }
        val _columnsSystemFeed: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSystemFeed.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSystemFeed.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSystemFeed.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSystemFeed.put("xpGained", TableInfo.Column("xpGained", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSystemFeed.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSystemFeed: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSystemFeed: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSystemFeed: TableInfo = TableInfo("system_feed", _columnsSystemFeed,
            _foreignKeysSystemFeed, _indicesSystemFeed)
        val _existingSystemFeed: TableInfo = read(connection, "system_feed")
        if (!_infoSystemFeed.equals(_existingSystemFeed)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |system_feed(com.axiom.app.data.local.entity.SystemFeedEntity).
              | Expected:
              |""".trimMargin() + _infoSystemFeed + """
              |
              | Found:
              |""".trimMargin() + _existingSystemFeed)
        }
        val _columnsWarriorProfiles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWarriorProfiles.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWarriorProfiles.put("codename", TableInfo.Column("codename", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWarriorProfiles.put("oneLineThesis", TableInfo.Column("oneLineThesis", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWarriorProfiles.put("rareProfileDescription",
            TableInfo.Column("rareProfileDescription", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWarriorProfiles.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWarriorProfiles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWarriorProfiles: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWarriorProfiles: TableInfo = TableInfo("warrior_profiles", _columnsWarriorProfiles,
            _foreignKeysWarriorProfiles, _indicesWarriorProfiles)
        val _existingWarriorProfiles: TableInfo = read(connection, "warrior_profiles")
        if (!_infoWarriorProfiles.equals(_existingWarriorProfiles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |warrior_profiles(com.axiom.app.data.local.entity.WarriorProfileEntity).
              | Expected:
              |""".trimMargin() + _infoWarriorProfiles + """
              |
              | Found:
              |""".trimMargin() + _existingWarriorProfiles)
        }
        val _columnsTracks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTracks.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("color", TableInfo.Column("color", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("icon", TableInfo.Column("icon", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTracks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTracks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTracks: TableInfo = TableInfo("tracks", _columnsTracks, _foreignKeysTracks,
            _indicesTracks)
        val _existingTracks: TableInfo = read(connection, "tracks")
        if (!_infoTracks.equals(_existingTracks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |tracks(com.axiom.app.data.local.entity.TrackEntity).
              | Expected:
              |""".trimMargin() + _infoTracks + """
              |
              | Found:
              |""".trimMargin() + _existingTracks)
        }
        val _columnsScheduleBlocks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsScheduleBlocks.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleBlocks.put("trackId", TableInfo.Column("trackId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleBlocks.put("startTime", TableInfo.Column("startTime", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleBlocks.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleBlocks.put("actionDescription", TableInfo.Column("actionDescription",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleBlocks.put("tag", TableInfo.Column("tag", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleBlocks.put("recurrence", TableInfo.Column("recurrence", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsScheduleBlocks.put("isNonNegotiable", TableInfo.Column("isNonNegotiable", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysScheduleBlocks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesScheduleBlocks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoScheduleBlocks: TableInfo = TableInfo("schedule_blocks", _columnsScheduleBlocks,
            _foreignKeysScheduleBlocks, _indicesScheduleBlocks)
        val _existingScheduleBlocks: TableInfo = read(connection, "schedule_blocks")
        if (!_infoScheduleBlocks.equals(_existingScheduleBlocks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |schedule_blocks(com.axiom.app.data.local.entity.ScheduleBlockEntity).
              | Expected:
              |""".trimMargin() + _infoScheduleBlocks + """
              |
              | Found:
              |""".trimMargin() + _existingScheduleBlocks)
        }
        val _columnsCustomKpis: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCustomKpis.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomKpis.put("trackId", TableInfo.Column("trackId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomKpis.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomKpis.put("targetValue", TableInfo.Column("targetValue", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomKpis.put("targetUnit", TableInfo.Column("targetUnit", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomKpis.put("measurementHint", TableInfo.Column("measurementHint", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCustomKpis.put("redFlagAction", TableInfo.Column("redFlagAction", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCustomKpis: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCustomKpis: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCustomKpis: TableInfo = TableInfo("custom_kpis", _columnsCustomKpis,
            _foreignKeysCustomKpis, _indicesCustomKpis)
        val _existingCustomKpis: TableInfo = read(connection, "custom_kpis")
        if (!_infoCustomKpis.equals(_existingCustomKpis)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |custom_kpis(com.axiom.app.data.local.entity.CustomKPIEntity).
              | Expected:
              |""".trimMargin() + _infoCustomKpis + """
              |
              | Found:
              |""".trimMargin() + _existingCustomKpis)
        }
        val _columnsIronRules: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsIronRules.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRules.put("orderIndex", TableInfo.Column("orderIndex", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRules.put("ruleText", TableInfo.Column("ruleText", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRules.put("isAutomatable", TableInfo.Column("isAutomatable", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRules.put("linkedSignalType", TableInfo.Column("linkedSignalType", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRules.put("linkedKpiId", TableInfo.Column("linkedKpiId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysIronRules: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesIronRules: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoIronRules: TableInfo = TableInfo("iron_rules", _columnsIronRules,
            _foreignKeysIronRules, _indicesIronRules)
        val _existingIronRules: TableInfo = read(connection, "iron_rules")
        if (!_infoIronRules.equals(_existingIronRules)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |iron_rules(com.axiom.app.data.local.entity.IronRuleEntity).
              | Expected:
              |""".trimMargin() + _infoIronRules + """
              |
              | Found:
              |""".trimMargin() + _existingIronRules)
        }
        val _columnsHardTruthsAffirmations: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHardTruthsAffirmations.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHardTruthsAffirmations.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHardTruthsAffirmations.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHardTruthsAffirmations.put("orderIndex", TableInfo.Column("orderIndex", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHardTruthsAffirmations: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHardTruthsAffirmations: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHardTruthsAffirmations: TableInfo = TableInfo("hard_truths_affirmations",
            _columnsHardTruthsAffirmations, _foreignKeysHardTruthsAffirmations,
            _indicesHardTruthsAffirmations)
        val _existingHardTruthsAffirmations: TableInfo = read(connection,
            "hard_truths_affirmations")
        if (!_infoHardTruthsAffirmations.equals(_existingHardTruthsAffirmations)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |hard_truths_affirmations(com.axiom.app.data.local.entity.HardTruthOrAffirmationEntity).
              | Expected:
              |""".trimMargin() + _infoHardTruthsAffirmations + """
              |
              | Found:
              |""".trimMargin() + _existingHardTruthsAffirmations)
        }
        val _columnsMajorMilestones: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMajorMilestones.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMajorMilestones.put("label", TableInfo.Column("label", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMajorMilestones.put("targetDate", TableInfo.Column("targetDate", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMajorMilestones.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMajorMilestones: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMajorMilestones: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMajorMilestones: TableInfo = TableInfo("major_milestones", _columnsMajorMilestones,
            _foreignKeysMajorMilestones, _indicesMajorMilestones)
        val _existingMajorMilestones: TableInfo = read(connection, "major_milestones")
        if (!_infoMajorMilestones.equals(_existingMajorMilestones)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |major_milestones(com.axiom.app.data.local.entity.MajorMilestoneEntity).
              | Expected:
              |""".trimMargin() + _infoMajorMilestones + """
              |
              | Found:
              |""".trimMargin() + _existingMajorMilestones)
        }
        val _columnsKeyRelationships: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsKeyRelationships.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsKeyRelationships.put("label", TableInfo.Column("label", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsKeyRelationships.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsKeyRelationships.put("lastInteractionAt", TableInfo.Column("lastInteractionAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsKeyRelationships.put("preparedTalkingPoint",
            TableInfo.Column("preparedTalkingPoint", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysKeyRelationships: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesKeyRelationships: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoKeyRelationships: TableInfo = TableInfo("key_relationships",
            _columnsKeyRelationships, _foreignKeysKeyRelationships, _indicesKeyRelationships)
        val _existingKeyRelationships: TableInfo = read(connection, "key_relationships")
        if (!_infoKeyRelationships.equals(_existingKeyRelationships)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |key_relationships(com.axiom.app.data.local.entity.KeyRelationshipEntity).
              | Expected:
              |""".trimMargin() + _infoKeyRelationships + """
              |
              | Found:
              |""".trimMargin() + _existingKeyRelationships)
        }
        val _columnsFinancialCheckpoints: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFinancialCheckpoints.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFinancialCheckpoints.put("monthIndex", TableInfo.Column("monthIndex", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFinancialCheckpoints.put("targetAmount", TableInfo.Column("targetAmount", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFinancialCheckpoints.put("currency", TableInfo.Column("currency", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFinancialCheckpoints: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFinancialCheckpoints: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFinancialCheckpoints: TableInfo = TableInfo("financial_checkpoints",
            _columnsFinancialCheckpoints, _foreignKeysFinancialCheckpoints,
            _indicesFinancialCheckpoints)
        val _existingFinancialCheckpoints: TableInfo = read(connection, "financial_checkpoints")
        if (!_infoFinancialCheckpoints.equals(_existingFinancialCheckpoints)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |financial_checkpoints(com.axiom.app.data.local.entity.FinancialCheckpointEntity).
              | Expected:
              |""".trimMargin() + _infoFinancialCheckpoints + """
              |
              | Found:
              |""".trimMargin() + _existingFinancialCheckpoints)
        }
        val _columnsMonthlyIncomeEntries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMonthlyIncomeEntries.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMonthlyIncomeEntries.put("monthIndex", TableInfo.Column("monthIndex", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMonthlyIncomeEntries.put("actualAmount", TableInfo.Column("actualAmount", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMonthlyIncomeEntries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMonthlyIncomeEntries: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMonthlyIncomeEntries: TableInfo = TableInfo("monthly_income_entries",
            _columnsMonthlyIncomeEntries, _foreignKeysMonthlyIncomeEntries,
            _indicesMonthlyIncomeEntries)
        val _existingMonthlyIncomeEntries: TableInfo = read(connection, "monthly_income_entries")
        if (!_infoMonthlyIncomeEntries.equals(_existingMonthlyIncomeEntries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |monthly_income_entries(com.axiom.app.data.local.entity.MonthlyIncomeEntryEntity).
              | Expected:
              |""".trimMargin() + _infoMonthlyIncomeEntries + """
              |
              | Found:
              |""".trimMargin() + _existingMonthlyIncomeEntries)
        }
        val _columnsMuscleGroups: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMuscleGroups.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroups.put("displayName", TableInfo.Column("displayName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroups.put("strengthScore", TableInfo.Column("strengthScore", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroups.put("lastTrainedTimestamp", TableInfo.Column("lastTrainedTimestamp",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMuscleGroups.put("freshnessPercent", TableInfo.Column("freshnessPercent", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMuscleGroups: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMuscleGroups: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoMuscleGroups: TableInfo = TableInfo("muscle_groups", _columnsMuscleGroups,
            _foreignKeysMuscleGroups, _indicesMuscleGroups)
        val _existingMuscleGroups: TableInfo = read(connection, "muscle_groups")
        if (!_infoMuscleGroups.equals(_existingMuscleGroups)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |muscle_groups(com.axiom.app.data.local.entity.MuscleGroupEntity).
              | Expected:
              |""".trimMargin() + _infoMuscleGroups + """
              |
              | Found:
              |""".trimMargin() + _existingMuscleGroups)
        }
        val _columnsVitalLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsVitalLogs.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVitalLogs.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVitalLogs.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVitalLogs.put("value", TableInfo.Column("value", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVitalLogs.put("loggedAt", TableInfo.Column("loggedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVitalLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesVitalLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesVitalLogs.add(TableInfo.Index("index_vital_logs_date_type", true, listOf("date",
            "type"), listOf("ASC", "ASC")))
        val _infoVitalLogs: TableInfo = TableInfo("vital_logs", _columnsVitalLogs,
            _foreignKeysVitalLogs, _indicesVitalLogs)
        val _existingVitalLogs: TableInfo = read(connection, "vital_logs")
        if (!_infoVitalLogs.equals(_existingVitalLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |vital_logs(com.axiom.app.data.local.entity.VitalLogEntity).
              | Expected:
              |""".trimMargin() + _infoVitalLogs + """
              |
              | Found:
              |""".trimMargin() + _existingVitalLogs)
        }
        val _columnsKpiProgress: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsKpiProgress.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsKpiProgress.put("kpiId", TableInfo.Column("kpiId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsKpiProgress.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsKpiProgress.put("incrementValue", TableInfo.Column("incrementValue", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysKpiProgress: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesKpiProgress: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoKpiProgress: TableInfo = TableInfo("kpi_progress", _columnsKpiProgress,
            _foreignKeysKpiProgress, _indicesKpiProgress)
        val _existingKpiProgress: TableInfo = read(connection, "kpi_progress")
        if (!_infoKpiProgress.equals(_existingKpiProgress)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |kpi_progress(com.axiom.app.data.local.entity.KPIProgressEntity).
              | Expected:
              |""".trimMargin() + _infoKpiProgress + """
              |
              | Found:
              |""".trimMargin() + _existingKpiProgress)
        }
        val _columnsKpiMissStreaks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsKpiMissStreaks.put("kpiId", TableInfo.Column("kpiId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsKpiMissStreaks.put("missStreak", TableInfo.Column("missStreak", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysKpiMissStreaks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesKpiMissStreaks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoKpiMissStreaks: TableInfo = TableInfo("kpi_miss_streaks", _columnsKpiMissStreaks,
            _foreignKeysKpiMissStreaks, _indicesKpiMissStreaks)
        val _existingKpiMissStreaks: TableInfo = read(connection, "kpi_miss_streaks")
        if (!_infoKpiMissStreaks.equals(_existingKpiMissStreaks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |kpi_miss_streaks(com.axiom.app.data.local.entity.KPIMissStreakEntity).
              | Expected:
              |""".trimMargin() + _infoKpiMissStreaks + """
              |
              | Found:
              |""".trimMargin() + _existingKpiMissStreaks)
        }
        val _columnsIronRuleViolationLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsIronRuleViolationLogs.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRuleViolationLogs.put("ruleId", TableInfo.Column("ruleId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRuleViolationLogs.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIronRuleViolationLogs.put("wasAutomaticallyDetected",
            TableInfo.Column("wasAutomaticallyDetected", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysIronRuleViolationLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesIronRuleViolationLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoIronRuleViolationLogs: TableInfo = TableInfo("iron_rule_violation_logs",
            _columnsIronRuleViolationLogs, _foreignKeysIronRuleViolationLogs,
            _indicesIronRuleViolationLogs)
        val _existingIronRuleViolationLogs: TableInfo = read(connection, "iron_rule_violation_logs")
        if (!_infoIronRuleViolationLogs.equals(_existingIronRuleViolationLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |iron_rule_violation_logs(com.axiom.app.data.local.entity.IronRuleViolationLogEntity).
              | Expected:
              |""".trimMargin() + _infoIronRuleViolationLogs + """
              |
              | Found:
              |""".trimMargin() + _existingIronRuleViolationLogs)
        }
        val _columnsDailyHabitLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDailyHabitLogs.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyHabitLogs.put("date", TableInfo.Column("date", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyHabitLogs.put("waterGlasses", TableInfo.Column("waterGlasses", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyHabitLogs.put("sleepHours", TableInfo.Column("sleepHours", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyHabitLogs.put("sleepQuality", TableInfo.Column("sleepQuality", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyHabitLogs.put("teethMorning", TableInfo.Column("teethMorning", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyHabitLogs.put("teethEvening", TableInfo.Column("teethEvening", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDailyHabitLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDailyHabitLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDailyHabitLogs: TableInfo = TableInfo("daily_habit_logs", _columnsDailyHabitLogs,
            _foreignKeysDailyHabitLogs, _indicesDailyHabitLogs)
        val _existingDailyHabitLogs: TableInfo = read(connection, "daily_habit_logs")
        if (!_infoDailyHabitLogs.equals(_existingDailyHabitLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |daily_habit_logs(com.axiom.app.data.local.entity.DailyHabitLogEntity).
              | Expected:
              |""".trimMargin() + _infoDailyHabitLogs + """
              |
              | Found:
              |""".trimMargin() + _existingDailyHabitLogs)
        }
        val _columnsWeeklyReviews: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWeeklyReviews.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeeklyReviews.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeeklyReviews.put("step1Summary", TableInfo.Column("step1Summary", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeeklyReviews.put("step2WrongAssumption", TableInfo.Column("step2WrongAssumption",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeeklyReviews.put("step3CriticFeedback", TableInfo.Column("step3CriticFeedback",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeeklyReviews.put("step4DecisionType", TableInfo.Column("step4DecisionType", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeeklyReviews.put("step5JournalText", TableInfo.Column("step5JournalText", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWeeklyReviews: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWeeklyReviews: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWeeklyReviews: TableInfo = TableInfo("weekly_reviews", _columnsWeeklyReviews,
            _foreignKeysWeeklyReviews, _indicesWeeklyReviews)
        val _existingWeeklyReviews: TableInfo = read(connection, "weekly_reviews")
        if (!_infoWeeklyReviews.equals(_existingWeeklyReviews)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |weekly_reviews(com.axiom.app.data.local.entity.WeeklyReviewEntity).
              | Expected:
              |""".trimMargin() + _infoWeeklyReviews + """
              |
              | Found:
              |""".trimMargin() + _existingWeeklyReviews)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "hunter_profile", "skills",
        "missions", "dungeons", "shadows", "streak", "system_feed", "warrior_profiles", "tracks",
        "schedule_blocks", "custom_kpis", "iron_rules", "hard_truths_affirmations",
        "major_milestones", "key_relationships", "financial_checkpoints", "monthly_income_entries",
        "muscle_groups", "vital_logs", "kpi_progress", "kpi_miss_streaks",
        "iron_rule_violation_logs", "daily_habit_logs", "weekly_reviews")
  }

  public override fun clearAllTables() {
    super.performClear(false, "hunter_profile", "skills", "missions", "dungeons", "shadows",
        "streak", "system_feed", "warrior_profiles", "tracks", "schedule_blocks", "custom_kpis",
        "iron_rules", "hard_truths_affirmations", "major_milestones", "key_relationships",
        "financial_checkpoints", "monthly_income_entries", "muscle_groups", "vital_logs",
        "kpi_progress", "kpi_miss_streaks", "iron_rule_violation_logs", "daily_habit_logs",
        "weekly_reviews")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(HunterDao::class, HunterDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MissionDao::class, MissionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DungeonDao::class, DungeonDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SkillDao::class, SkillDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ShadowDao::class, ShadowDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(StreakDao::class, StreakDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SystemFeedDao::class, SystemFeedDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WarriorBlueprintDao::class,
        WarriorBlueprintDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MuscleGroupDao::class, MuscleGroupDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(VitalLogDao::class, VitalLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(KPIProgressDao::class, KPIProgressDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DailyHabitLogDao::class, DailyHabitLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WeeklyReviewDao::class, WeeklyReviewDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun hunterDao(): HunterDao = _hunterDao.value

  public override fun missionDao(): MissionDao = _missionDao.value

  public override fun dungeonDao(): DungeonDao = _dungeonDao.value

  public override fun skillDao(): SkillDao = _skillDao.value

  public override fun shadowDao(): ShadowDao = _shadowDao.value

  public override fun streakDao(): StreakDao = _streakDao.value

  public override fun systemFeedDao(): SystemFeedDao = _systemFeedDao.value

  public override fun warriorBlueprintDao(): WarriorBlueprintDao = _warriorBlueprintDao.value

  public override fun muscleGroupDao(): MuscleGroupDao = _muscleGroupDao.value

  public override fun vitalLogDao(): VitalLogDao = _vitalLogDao.value

  public override fun kpiProgressDao(): KPIProgressDao = _kPIProgressDao.value

  public override fun dailyHabitLogDao(): DailyHabitLogDao = _dailyHabitLogDao.value

  public override fun weeklyReviewDao(): WeeklyReviewDao = _weeklyReviewDao.value
}
