package com.axiom.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.CustomKPIEntity
import com.axiom.app.`data`.local.entity.FinancialCheckpointEntity
import com.axiom.app.`data`.local.entity.HardTruthOrAffirmationEntity
import com.axiom.app.`data`.local.entity.IronRuleEntity
import com.axiom.app.`data`.local.entity.IronRuleViolationLogEntity
import com.axiom.app.`data`.local.entity.KeyRelationshipEntity
import com.axiom.app.`data`.local.entity.MajorMilestoneEntity
import com.axiom.app.`data`.local.entity.MonthlyIncomeEntryEntity
import com.axiom.app.`data`.local.entity.ScheduleBlockEntity
import com.axiom.app.`data`.local.entity.TrackEntity
import com.axiom.app.`data`.local.entity.WarriorProfileEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WarriorBlueprintDao_Impl(
  __db: RoomDatabase,
) : WarriorBlueprintDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWarriorProfileEntity: EntityInsertAdapter<WarriorProfileEntity>

  private val __insertAdapterOfTrackEntity: EntityInsertAdapter<TrackEntity>

  private val __insertAdapterOfScheduleBlockEntity: EntityInsertAdapter<ScheduleBlockEntity>

  private val __insertAdapterOfCustomKPIEntity: EntityInsertAdapter<CustomKPIEntity>

  private val __insertAdapterOfIronRuleEntity: EntityInsertAdapter<IronRuleEntity>

  private val __insertAdapterOfHardTruthOrAffirmationEntity:
      EntityInsertAdapter<HardTruthOrAffirmationEntity>

  private val __insertAdapterOfMajorMilestoneEntity: EntityInsertAdapter<MajorMilestoneEntity>

  private val __insertAdapterOfKeyRelationshipEntity: EntityInsertAdapter<KeyRelationshipEntity>

  private val __insertAdapterOfFinancialCheckpointEntity:
      EntityInsertAdapter<FinancialCheckpointEntity>

  private val __insertAdapterOfMonthlyIncomeEntryEntity:
      EntityInsertAdapter<MonthlyIncomeEntryEntity>

  private val __insertAdapterOfIronRuleViolationLogEntity:
      EntityInsertAdapter<IronRuleViolationLogEntity>

  private val __deleteAdapterOfTrackEntity: EntityDeleteOrUpdateAdapter<TrackEntity>

  private val __deleteAdapterOfScheduleBlockEntity: EntityDeleteOrUpdateAdapter<ScheduleBlockEntity>

  private val __deleteAdapterOfCustomKPIEntity: EntityDeleteOrUpdateAdapter<CustomKPIEntity>

  private val __deleteAdapterOfIronRuleEntity: EntityDeleteOrUpdateAdapter<IronRuleEntity>

  private val __deleteAdapterOfHardTruthOrAffirmationEntity:
      EntityDeleteOrUpdateAdapter<HardTruthOrAffirmationEntity>

  private val __deleteAdapterOfMajorMilestoneEntity:
      EntityDeleteOrUpdateAdapter<MajorMilestoneEntity>

  private val __deleteAdapterOfKeyRelationshipEntity:
      EntityDeleteOrUpdateAdapter<KeyRelationshipEntity>

  private val __deleteAdapterOfFinancialCheckpointEntity:
      EntityDeleteOrUpdateAdapter<FinancialCheckpointEntity>

  private val __deleteAdapterOfMonthlyIncomeEntryEntity:
      EntityDeleteOrUpdateAdapter<MonthlyIncomeEntryEntity>

  private val __deleteAdapterOfIronRuleViolationLogEntity:
      EntityDeleteOrUpdateAdapter<IronRuleViolationLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWarriorProfileEntity = object :
        EntityInsertAdapter<WarriorProfileEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `warrior_profiles` (`id`,`codename`,`oneLineThesis`,`rareProfileDescription`,`createdAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WarriorProfileEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.codename)
        statement.bindText(3, entity.oneLineThesis)
        statement.bindText(4, entity.rareProfileDescription)
        statement.bindLong(5, entity.createdAt)
      }
    }
    this.__insertAdapterOfTrackEntity = object : EntityInsertAdapter<TrackEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `tracks` (`id`,`name`,`color`,`icon`,`description`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TrackEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.color)
        statement.bindText(4, entity.icon)
        statement.bindText(5, entity.description)
      }
    }
    this.__insertAdapterOfScheduleBlockEntity = object : EntityInsertAdapter<ScheduleBlockEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `schedule_blocks` (`id`,`trackId`,`startTime`,`title`,`actionDescription`,`tag`,`recurrence`,`isNonNegotiable`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ScheduleBlockEntity) {
        statement.bindText(1, entity.id)
        val _tmpTrackId: String? = entity.trackId
        if (_tmpTrackId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTrackId)
        }
        statement.bindText(3, entity.startTime)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.actionDescription)
        statement.bindText(6, entity.tag)
        statement.bindText(7, entity.recurrence)
        val _tmp: Int = if (entity.isNonNegotiable) 1 else 0
        statement.bindLong(8, _tmp.toLong())
      }
    }
    this.__insertAdapterOfCustomKPIEntity = object : EntityInsertAdapter<CustomKPIEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `custom_kpis` (`id`,`trackId`,`name`,`targetValue`,`targetUnit`,`measurementHint`,`redFlagAction`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CustomKPIEntity) {
        statement.bindText(1, entity.id)
        val _tmpTrackId: String? = entity.trackId
        if (_tmpTrackId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpTrackId)
        }
        statement.bindText(3, entity.name)
        statement.bindDouble(4, entity.targetValue.toDouble())
        statement.bindText(5, entity.targetUnit)
        statement.bindText(6, entity.measurementHint)
        statement.bindText(7, entity.redFlagAction)
      }
    }
    this.__insertAdapterOfIronRuleEntity = object : EntityInsertAdapter<IronRuleEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `iron_rules` (`id`,`orderIndex`,`ruleText`,`isAutomatable`,`linkedSignalType`,`linkedKpiId`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: IronRuleEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.orderIndex.toLong())
        statement.bindText(3, entity.ruleText)
        val _tmp: Int = if (entity.isAutomatable) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindText(5, entity.linkedSignalType)
        val _tmpLinkedKpiId: String? = entity.linkedKpiId
        if (_tmpLinkedKpiId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpLinkedKpiId)
        }
      }
    }
    this.__insertAdapterOfHardTruthOrAffirmationEntity = object :
        EntityInsertAdapter<HardTruthOrAffirmationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `hard_truths_affirmations` (`id`,`type`,`text`,`orderIndex`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement,
          entity: HardTruthOrAffirmationEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.type)
        statement.bindText(3, entity.text)
        statement.bindLong(4, entity.orderIndex.toLong())
      }
    }
    this.__insertAdapterOfMajorMilestoneEntity = object :
        EntityInsertAdapter<MajorMilestoneEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `major_milestones` (`id`,`label`,`targetDate`,`description`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MajorMilestoneEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.label)
        statement.bindLong(3, entity.targetDate)
        statement.bindText(4, entity.description)
      }
    }
    this.__insertAdapterOfKeyRelationshipEntity = object :
        EntityInsertAdapter<KeyRelationshipEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `key_relationships` (`id`,`label`,`category`,`lastInteractionAt`,`preparedTalkingPoint`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: KeyRelationshipEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.label)
        statement.bindText(3, entity.category)
        val _tmpLastInteractionAt: Long? = entity.lastInteractionAt
        if (_tmpLastInteractionAt == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpLastInteractionAt)
        }
        statement.bindText(5, entity.preparedTalkingPoint)
      }
    }
    this.__insertAdapterOfFinancialCheckpointEntity = object :
        EntityInsertAdapter<FinancialCheckpointEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `financial_checkpoints` (`id`,`monthIndex`,`targetAmount`,`currency`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FinancialCheckpointEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.monthIndex.toLong())
        statement.bindDouble(3, entity.targetAmount.toDouble())
        statement.bindText(4, entity.currency)
      }
    }
    this.__insertAdapterOfMonthlyIncomeEntryEntity = object :
        EntityInsertAdapter<MonthlyIncomeEntryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `monthly_income_entries` (`id`,`monthIndex`,`actualAmount`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MonthlyIncomeEntryEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.monthIndex.toLong())
        statement.bindDouble(3, entity.actualAmount.toDouble())
      }
    }
    this.__insertAdapterOfIronRuleViolationLogEntity = object :
        EntityInsertAdapter<IronRuleViolationLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `iron_rule_violation_logs` (`id`,`ruleId`,`date`,`wasAutomaticallyDetected`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: IronRuleViolationLogEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.ruleId)
        statement.bindLong(3, entity.date)
        val _tmp: Int = if (entity.wasAutomaticallyDetected) 1 else 0
        statement.bindLong(4, _tmp.toLong())
      }
    }
    this.__deleteAdapterOfTrackEntity = object : EntityDeleteOrUpdateAdapter<TrackEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `tracks` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: TrackEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfScheduleBlockEntity = object :
        EntityDeleteOrUpdateAdapter<ScheduleBlockEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `schedule_blocks` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ScheduleBlockEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfCustomKPIEntity = object : EntityDeleteOrUpdateAdapter<CustomKPIEntity>()
        {
      protected override fun createQuery(): String = "DELETE FROM `custom_kpis` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: CustomKPIEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfIronRuleEntity = object : EntityDeleteOrUpdateAdapter<IronRuleEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `iron_rules` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: IronRuleEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfHardTruthOrAffirmationEntity = object :
        EntityDeleteOrUpdateAdapter<HardTruthOrAffirmationEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `hard_truths_affirmations` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement,
          entity: HardTruthOrAffirmationEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfMajorMilestoneEntity = object :
        EntityDeleteOrUpdateAdapter<MajorMilestoneEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `major_milestones` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MajorMilestoneEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfKeyRelationshipEntity = object :
        EntityDeleteOrUpdateAdapter<KeyRelationshipEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `key_relationships` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: KeyRelationshipEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfFinancialCheckpointEntity = object :
        EntityDeleteOrUpdateAdapter<FinancialCheckpointEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `financial_checkpoints` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FinancialCheckpointEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfMonthlyIncomeEntryEntity = object :
        EntityDeleteOrUpdateAdapter<MonthlyIncomeEntryEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `monthly_income_entries` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MonthlyIncomeEntryEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__deleteAdapterOfIronRuleViolationLogEntity = object :
        EntityDeleteOrUpdateAdapter<IronRuleViolationLogEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `iron_rule_violation_logs` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: IronRuleViolationLogEntity) {
        statement.bindText(1, entity.id)
      }
    }
  }

  public override suspend fun insertProfile(profile: WarriorProfileEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWarriorProfileEntity.insert(_connection, profile)
  }

  public override suspend fun insertTrack(track: TrackEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfTrackEntity.insert(_connection, track)
  }

  public override suspend fun insertScheduleBlock(block: ScheduleBlockEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfScheduleBlockEntity.insert(_connection, block)
  }

  public override suspend fun insertCustomKPI(kpi: CustomKPIEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfCustomKPIEntity.insert(_connection, kpi)
  }

  public override suspend fun insertIronRule(rule: IronRuleEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfIronRuleEntity.insert(_connection, rule)
  }

  public override suspend fun insertHardTruthOrAffirmation(entry: HardTruthOrAffirmationEntity):
      Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfHardTruthOrAffirmationEntity.insert(_connection, entry)
  }

  public override suspend fun insertMajorMilestone(milestone: MajorMilestoneEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMajorMilestoneEntity.insert(_connection, milestone)
  }

  public override suspend fun insertKeyRelationship(relation: KeyRelationshipEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfKeyRelationshipEntity.insert(_connection, relation)
  }

  public override suspend fun insertFinancialCheckpoint(checkpoint: FinancialCheckpointEntity): Unit
      = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFinancialCheckpointEntity.insert(_connection, checkpoint)
  }

  public override suspend fun insertMonthlyIncomeEntry(entry: MonthlyIncomeEntryEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMonthlyIncomeEntryEntity.insert(_connection, entry)
  }

  public override suspend fun insertIronRuleViolationLog(log: IronRuleViolationLogEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfIronRuleViolationLogEntity.insert(_connection, log)
  }

  public override suspend fun deleteTrack(track: TrackEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfTrackEntity.handle(_connection, track)
  }

  public override suspend fun deleteScheduleBlock(block: ScheduleBlockEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfScheduleBlockEntity.handle(_connection, block)
  }

  public override suspend fun deleteCustomKPI(kpi: CustomKPIEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfCustomKPIEntity.handle(_connection, kpi)
  }

  public override suspend fun deleteIronRule(rule: IronRuleEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfIronRuleEntity.handle(_connection, rule)
  }

  public override suspend fun deleteHardTruthOrAffirmation(entry: HardTruthOrAffirmationEntity):
      Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfHardTruthOrAffirmationEntity.handle(_connection, entry)
  }

  public override suspend fun deleteMajorMilestone(milestone: MajorMilestoneEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfMajorMilestoneEntity.handle(_connection, milestone)
  }

  public override suspend fun deleteKeyRelationship(relation: KeyRelationshipEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfKeyRelationshipEntity.handle(_connection, relation)
  }

  public override suspend fun deleteFinancialCheckpoint(checkpoint: FinancialCheckpointEntity): Unit
      = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfFinancialCheckpointEntity.handle(_connection, checkpoint)
  }

  public override suspend fun deleteMonthlyIncomeEntry(entry: MonthlyIncomeEntryEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfMonthlyIncomeEntryEntity.handle(_connection, entry)
  }

  public override suspend fun deleteIronRuleViolationLog(log: IronRuleViolationLogEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfIronRuleViolationLogEntity.handle(_connection, log)
  }

  public override suspend fun getProfile(id: String): WarriorProfileEntity? {
    val _sql: String = "SELECT * FROM warrior_profiles WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCodename: Int = getColumnIndexOrThrow(_stmt, "codename")
        val _columnIndexOfOneLineThesis: Int = getColumnIndexOrThrow(_stmt, "oneLineThesis")
        val _columnIndexOfRareProfileDescription: Int = getColumnIndexOrThrow(_stmt,
            "rareProfileDescription")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: WarriorProfileEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCodename: String
          _tmpCodename = _stmt.getText(_columnIndexOfCodename)
          val _tmpOneLineThesis: String
          _tmpOneLineThesis = _stmt.getText(_columnIndexOfOneLineThesis)
          val _tmpRareProfileDescription: String
          _tmpRareProfileDescription = _stmt.getText(_columnIndexOfRareProfileDescription)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              WarriorProfileEntity(_tmpId,_tmpCodename,_tmpOneLineThesis,_tmpRareProfileDescription,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProfileFlow(id: String): Flow<WarriorProfileEntity?> {
    val _sql: String = "SELECT * FROM warrior_profiles WHERE id = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("warrior_profiles")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCodename: Int = getColumnIndexOrThrow(_stmt, "codename")
        val _columnIndexOfOneLineThesis: Int = getColumnIndexOrThrow(_stmt, "oneLineThesis")
        val _columnIndexOfRareProfileDescription: Int = getColumnIndexOrThrow(_stmt,
            "rareProfileDescription")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: WarriorProfileEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCodename: String
          _tmpCodename = _stmt.getText(_columnIndexOfCodename)
          val _tmpOneLineThesis: String
          _tmpOneLineThesis = _stmt.getText(_columnIndexOfOneLineThesis)
          val _tmpRareProfileDescription: String
          _tmpRareProfileDescription = _stmt.getText(_columnIndexOfRareProfileDescription)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              WarriorProfileEntity(_tmpId,_tmpCodename,_tmpOneLineThesis,_tmpRareProfileDescription,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTracksFlow(): Flow<List<TrackEntity>> {
    val _sql: String = "SELECT * FROM tracks ORDER BY id ASC"
    return createFlow(__db, false, arrayOf("tracks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: MutableList<TrackEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TrackEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpIcon: String
          _tmpIcon = _stmt.getText(_columnIndexOfIcon)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          _item = TrackEntity(_tmpId,_tmpName,_tmpColor,_tmpIcon,_tmpDescription)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllTracks(): List<TrackEntity> {
    val _sql: String = "SELECT * FROM tracks ORDER BY id ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: MutableList<TrackEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TrackEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpIcon: String
          _tmpIcon = _stmt.getText(_columnIndexOfIcon)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          _item = TrackEntity(_tmpId,_tmpName,_tmpColor,_tmpIcon,_tmpDescription)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTrackById(id: String): TrackEntity? {
    val _sql: String = "SELECT * FROM tracks WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: TrackEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpIcon: String
          _tmpIcon = _stmt.getText(_columnIndexOfIcon)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          _result = TrackEntity(_tmpId,_tmpName,_tmpColor,_tmpIcon,_tmpDescription)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getScheduleBlocksFlow(): Flow<List<ScheduleBlockEntity>> {
    val _sql: String = "SELECT * FROM schedule_blocks ORDER BY startTime ASC"
    return createFlow(__db, false, arrayOf("schedule_blocks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfActionDescription: Int = getColumnIndexOrThrow(_stmt, "actionDescription")
        val _columnIndexOfTag: Int = getColumnIndexOrThrow(_stmt, "tag")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfIsNonNegotiable: Int = getColumnIndexOrThrow(_stmt, "isNonNegotiable")
        val _result: MutableList<ScheduleBlockEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ScheduleBlockEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpActionDescription: String
          _tmpActionDescription = _stmt.getText(_columnIndexOfActionDescription)
          val _tmpTag: String
          _tmpTag = _stmt.getText(_columnIndexOfTag)
          val _tmpRecurrence: String
          _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          val _tmpIsNonNegotiable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsNonNegotiable).toInt()
          _tmpIsNonNegotiable = _tmp != 0
          _item =
              ScheduleBlockEntity(_tmpId,_tmpTrackId,_tmpStartTime,_tmpTitle,_tmpActionDescription,_tmpTag,_tmpRecurrence,_tmpIsNonNegotiable)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllScheduleBlocks(): List<ScheduleBlockEntity> {
    val _sql: String = "SELECT * FROM schedule_blocks ORDER BY startTime ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfActionDescription: Int = getColumnIndexOrThrow(_stmt, "actionDescription")
        val _columnIndexOfTag: Int = getColumnIndexOrThrow(_stmt, "tag")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfIsNonNegotiable: Int = getColumnIndexOrThrow(_stmt, "isNonNegotiable")
        val _result: MutableList<ScheduleBlockEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ScheduleBlockEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpStartTime: String
          _tmpStartTime = _stmt.getText(_columnIndexOfStartTime)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpActionDescription: String
          _tmpActionDescription = _stmt.getText(_columnIndexOfActionDescription)
          val _tmpTag: String
          _tmpTag = _stmt.getText(_columnIndexOfTag)
          val _tmpRecurrence: String
          _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          val _tmpIsNonNegotiable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsNonNegotiable).toInt()
          _tmpIsNonNegotiable = _tmp != 0
          _item =
              ScheduleBlockEntity(_tmpId,_tmpTrackId,_tmpStartTime,_tmpTitle,_tmpActionDescription,_tmpTag,_tmpRecurrence,_tmpIsNonNegotiable)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCustomKPIsFlow(): Flow<List<CustomKPIEntity>> {
    val _sql: String = "SELECT * FROM custom_kpis ORDER BY id ASC"
    return createFlow(__db, false, arrayOf("custom_kpis")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetValue: Int = getColumnIndexOrThrow(_stmt, "targetValue")
        val _columnIndexOfTargetUnit: Int = getColumnIndexOrThrow(_stmt, "targetUnit")
        val _columnIndexOfMeasurementHint: Int = getColumnIndexOrThrow(_stmt, "measurementHint")
        val _columnIndexOfRedFlagAction: Int = getColumnIndexOrThrow(_stmt, "redFlagAction")
        val _result: MutableList<CustomKPIEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomKPIEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetValue: Float
          _tmpTargetValue = _stmt.getDouble(_columnIndexOfTargetValue).toFloat()
          val _tmpTargetUnit: String
          _tmpTargetUnit = _stmt.getText(_columnIndexOfTargetUnit)
          val _tmpMeasurementHint: String
          _tmpMeasurementHint = _stmt.getText(_columnIndexOfMeasurementHint)
          val _tmpRedFlagAction: String
          _tmpRedFlagAction = _stmt.getText(_columnIndexOfRedFlagAction)
          _item =
              CustomKPIEntity(_tmpId,_tmpTrackId,_tmpName,_tmpTargetValue,_tmpTargetUnit,_tmpMeasurementHint,_tmpRedFlagAction)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllCustomKPIs(): List<CustomKPIEntity> {
    val _sql: String = "SELECT * FROM custom_kpis ORDER BY id ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetValue: Int = getColumnIndexOrThrow(_stmt, "targetValue")
        val _columnIndexOfTargetUnit: Int = getColumnIndexOrThrow(_stmt, "targetUnit")
        val _columnIndexOfMeasurementHint: Int = getColumnIndexOrThrow(_stmt, "measurementHint")
        val _columnIndexOfRedFlagAction: Int = getColumnIndexOrThrow(_stmt, "redFlagAction")
        val _result: MutableList<CustomKPIEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomKPIEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetValue: Float
          _tmpTargetValue = _stmt.getDouble(_columnIndexOfTargetValue).toFloat()
          val _tmpTargetUnit: String
          _tmpTargetUnit = _stmt.getText(_columnIndexOfTargetUnit)
          val _tmpMeasurementHint: String
          _tmpMeasurementHint = _stmt.getText(_columnIndexOfMeasurementHint)
          val _tmpRedFlagAction: String
          _tmpRedFlagAction = _stmt.getText(_columnIndexOfRedFlagAction)
          _item =
              CustomKPIEntity(_tmpId,_tmpTrackId,_tmpName,_tmpTargetValue,_tmpTargetUnit,_tmpMeasurementHint,_tmpRedFlagAction)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getIronRulesFlow(): Flow<List<IronRuleEntity>> {
    val _sql: String = "SELECT * FROM iron_rules ORDER BY orderIndex ASC"
    return createFlow(__db, false, arrayOf("iron_rules")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _columnIndexOfRuleText: Int = getColumnIndexOrThrow(_stmt, "ruleText")
        val _columnIndexOfIsAutomatable: Int = getColumnIndexOrThrow(_stmt, "isAutomatable")
        val _columnIndexOfLinkedSignalType: Int = getColumnIndexOrThrow(_stmt, "linkedSignalType")
        val _columnIndexOfLinkedKpiId: Int = getColumnIndexOrThrow(_stmt, "linkedKpiId")
        val _result: MutableList<IronRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: IronRuleEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpRuleText: String
          _tmpRuleText = _stmt.getText(_columnIndexOfRuleText)
          val _tmpIsAutomatable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAutomatable).toInt()
          _tmpIsAutomatable = _tmp != 0
          val _tmpLinkedSignalType: String
          _tmpLinkedSignalType = _stmt.getText(_columnIndexOfLinkedSignalType)
          val _tmpLinkedKpiId: String?
          if (_stmt.isNull(_columnIndexOfLinkedKpiId)) {
            _tmpLinkedKpiId = null
          } else {
            _tmpLinkedKpiId = _stmt.getText(_columnIndexOfLinkedKpiId)
          }
          _item =
              IronRuleEntity(_tmpId,_tmpOrderIndex,_tmpRuleText,_tmpIsAutomatable,_tmpLinkedSignalType,_tmpLinkedKpiId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllIronRules(): List<IronRuleEntity> {
    val _sql: String = "SELECT * FROM iron_rules ORDER BY orderIndex ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _columnIndexOfRuleText: Int = getColumnIndexOrThrow(_stmt, "ruleText")
        val _columnIndexOfIsAutomatable: Int = getColumnIndexOrThrow(_stmt, "isAutomatable")
        val _columnIndexOfLinkedSignalType: Int = getColumnIndexOrThrow(_stmt, "linkedSignalType")
        val _columnIndexOfLinkedKpiId: Int = getColumnIndexOrThrow(_stmt, "linkedKpiId")
        val _result: MutableList<IronRuleEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: IronRuleEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          val _tmpRuleText: String
          _tmpRuleText = _stmt.getText(_columnIndexOfRuleText)
          val _tmpIsAutomatable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAutomatable).toInt()
          _tmpIsAutomatable = _tmp != 0
          val _tmpLinkedSignalType: String
          _tmpLinkedSignalType = _stmt.getText(_columnIndexOfLinkedSignalType)
          val _tmpLinkedKpiId: String?
          if (_stmt.isNull(_columnIndexOfLinkedKpiId)) {
            _tmpLinkedKpiId = null
          } else {
            _tmpLinkedKpiId = _stmt.getText(_columnIndexOfLinkedKpiId)
          }
          _item =
              IronRuleEntity(_tmpId,_tmpOrderIndex,_tmpRuleText,_tmpIsAutomatable,_tmpLinkedSignalType,_tmpLinkedKpiId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getHardTruthsOrAffirmationsFlow(): Flow<List<HardTruthOrAffirmationEntity>> {
    val _sql: String = "SELECT * FROM hard_truths_affirmations ORDER BY orderIndex ASC"
    return createFlow(__db, false, arrayOf("hard_truths_affirmations")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _result: MutableList<HardTruthOrAffirmationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HardTruthOrAffirmationEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          _item = HardTruthOrAffirmationEntity(_tmpId,_tmpType,_tmpText,_tmpOrderIndex)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllHardTruthsOrAffirmations(): List<HardTruthOrAffirmationEntity> {
    val _sql: String = "SELECT * FROM hard_truths_affirmations ORDER BY orderIndex ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfOrderIndex: Int = getColumnIndexOrThrow(_stmt, "orderIndex")
        val _result: MutableList<HardTruthOrAffirmationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HardTruthOrAffirmationEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          val _tmpOrderIndex: Int
          _tmpOrderIndex = _stmt.getLong(_columnIndexOfOrderIndex).toInt()
          _item = HardTruthOrAffirmationEntity(_tmpId,_tmpType,_tmpText,_tmpOrderIndex)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getMajorMilestonesFlow(): Flow<List<MajorMilestoneEntity>> {
    val _sql: String = "SELECT * FROM major_milestones ORDER BY targetDate ASC"
    return createFlow(__db, false, arrayOf("major_milestones")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfTargetDate: Int = getColumnIndexOrThrow(_stmt, "targetDate")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: MutableList<MajorMilestoneEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MajorMilestoneEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpTargetDate: Long
          _tmpTargetDate = _stmt.getLong(_columnIndexOfTargetDate)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          _item = MajorMilestoneEntity(_tmpId,_tmpLabel,_tmpTargetDate,_tmpDescription)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllMajorMilestones(): List<MajorMilestoneEntity> {
    val _sql: String = "SELECT * FROM major_milestones ORDER BY targetDate ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfTargetDate: Int = getColumnIndexOrThrow(_stmt, "targetDate")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: MutableList<MajorMilestoneEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MajorMilestoneEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpTargetDate: Long
          _tmpTargetDate = _stmt.getLong(_columnIndexOfTargetDate)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          _item = MajorMilestoneEntity(_tmpId,_tmpLabel,_tmpTargetDate,_tmpDescription)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getKeyRelationshipsFlow(): Flow<List<KeyRelationshipEntity>> {
    val _sql: String = "SELECT * FROM key_relationships ORDER BY label ASC"
    return createFlow(__db, false, arrayOf("key_relationships")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfLastInteractionAt: Int = getColumnIndexOrThrow(_stmt, "lastInteractionAt")
        val _columnIndexOfPreparedTalkingPoint: Int = getColumnIndexOrThrow(_stmt,
            "preparedTalkingPoint")
        val _result: MutableList<KeyRelationshipEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: KeyRelationshipEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpLastInteractionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastInteractionAt)) {
            _tmpLastInteractionAt = null
          } else {
            _tmpLastInteractionAt = _stmt.getLong(_columnIndexOfLastInteractionAt)
          }
          val _tmpPreparedTalkingPoint: String
          _tmpPreparedTalkingPoint = _stmt.getText(_columnIndexOfPreparedTalkingPoint)
          _item =
              KeyRelationshipEntity(_tmpId,_tmpLabel,_tmpCategory,_tmpLastInteractionAt,_tmpPreparedTalkingPoint)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllKeyRelationships(): List<KeyRelationshipEntity> {
    val _sql: String = "SELECT * FROM key_relationships ORDER BY label ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfLastInteractionAt: Int = getColumnIndexOrThrow(_stmt, "lastInteractionAt")
        val _columnIndexOfPreparedTalkingPoint: Int = getColumnIndexOrThrow(_stmt,
            "preparedTalkingPoint")
        val _result: MutableList<KeyRelationshipEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: KeyRelationshipEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpLastInteractionAt: Long?
          if (_stmt.isNull(_columnIndexOfLastInteractionAt)) {
            _tmpLastInteractionAt = null
          } else {
            _tmpLastInteractionAt = _stmt.getLong(_columnIndexOfLastInteractionAt)
          }
          val _tmpPreparedTalkingPoint: String
          _tmpPreparedTalkingPoint = _stmt.getText(_columnIndexOfPreparedTalkingPoint)
          _item =
              KeyRelationshipEntity(_tmpId,_tmpLabel,_tmpCategory,_tmpLastInteractionAt,_tmpPreparedTalkingPoint)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getFinancialCheckpointsFlow(): Flow<List<FinancialCheckpointEntity>> {
    val _sql: String = "SELECT * FROM financial_checkpoints ORDER BY monthIndex ASC"
    return createFlow(__db, false, arrayOf("financial_checkpoints")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMonthIndex: Int = getColumnIndexOrThrow(_stmt, "monthIndex")
        val _columnIndexOfTargetAmount: Int = getColumnIndexOrThrow(_stmt, "targetAmount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _result: MutableList<FinancialCheckpointEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FinancialCheckpointEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpMonthIndex: Int
          _tmpMonthIndex = _stmt.getLong(_columnIndexOfMonthIndex).toInt()
          val _tmpTargetAmount: Float
          _tmpTargetAmount = _stmt.getDouble(_columnIndexOfTargetAmount).toFloat()
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          _item = FinancialCheckpointEntity(_tmpId,_tmpMonthIndex,_tmpTargetAmount,_tmpCurrency)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllFinancialCheckpoints(): List<FinancialCheckpointEntity> {
    val _sql: String = "SELECT * FROM financial_checkpoints ORDER BY monthIndex ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMonthIndex: Int = getColumnIndexOrThrow(_stmt, "monthIndex")
        val _columnIndexOfTargetAmount: Int = getColumnIndexOrThrow(_stmt, "targetAmount")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _result: MutableList<FinancialCheckpointEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FinancialCheckpointEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpMonthIndex: Int
          _tmpMonthIndex = _stmt.getLong(_columnIndexOfMonthIndex).toInt()
          val _tmpTargetAmount: Float
          _tmpTargetAmount = _stmt.getDouble(_columnIndexOfTargetAmount).toFloat()
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          _item = FinancialCheckpointEntity(_tmpId,_tmpMonthIndex,_tmpTargetAmount,_tmpCurrency)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getMonthlyIncomeEntriesFlow(): Flow<List<MonthlyIncomeEntryEntity>> {
    val _sql: String = "SELECT * FROM monthly_income_entries ORDER BY monthIndex ASC"
    return createFlow(__db, false, arrayOf("monthly_income_entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMonthIndex: Int = getColumnIndexOrThrow(_stmt, "monthIndex")
        val _columnIndexOfActualAmount: Int = getColumnIndexOrThrow(_stmt, "actualAmount")
        val _result: MutableList<MonthlyIncomeEntryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MonthlyIncomeEntryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpMonthIndex: Int
          _tmpMonthIndex = _stmt.getLong(_columnIndexOfMonthIndex).toInt()
          val _tmpActualAmount: Float
          _tmpActualAmount = _stmt.getDouble(_columnIndexOfActualAmount).toFloat()
          _item = MonthlyIncomeEntryEntity(_tmpId,_tmpMonthIndex,_tmpActualAmount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllMonthlyIncomeEntries(): List<MonthlyIncomeEntryEntity> {
    val _sql: String = "SELECT * FROM monthly_income_entries ORDER BY monthIndex ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMonthIndex: Int = getColumnIndexOrThrow(_stmt, "monthIndex")
        val _columnIndexOfActualAmount: Int = getColumnIndexOrThrow(_stmt, "actualAmount")
        val _result: MutableList<MonthlyIncomeEntryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MonthlyIncomeEntryEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpMonthIndex: Int
          _tmpMonthIndex = _stmt.getLong(_columnIndexOfMonthIndex).toInt()
          val _tmpActualAmount: Float
          _tmpActualAmount = _stmt.getDouble(_columnIndexOfActualAmount).toFloat()
          _item = MonthlyIncomeEntryEntity(_tmpId,_tmpMonthIndex,_tmpActualAmount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getIronRuleViolationLogsFlow(): Flow<List<IronRuleViolationLogEntity>> {
    val _sql: String = "SELECT * FROM iron_rule_violation_logs ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("iron_rule_violation_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRuleId: Int = getColumnIndexOrThrow(_stmt, "ruleId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfWasAutomaticallyDetected: Int = getColumnIndexOrThrow(_stmt,
            "wasAutomaticallyDetected")
        val _result: MutableList<IronRuleViolationLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: IronRuleViolationLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRuleId: String
          _tmpRuleId = _stmt.getText(_columnIndexOfRuleId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpWasAutomaticallyDetected: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfWasAutomaticallyDetected).toInt()
          _tmpWasAutomaticallyDetected = _tmp != 0
          _item =
              IronRuleViolationLogEntity(_tmpId,_tmpRuleId,_tmpDate,_tmpWasAutomaticallyDetected)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllIronRuleViolationLogs(): List<IronRuleViolationLogEntity> {
    val _sql: String = "SELECT * FROM iron_rule_violation_logs ORDER BY date DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRuleId: Int = getColumnIndexOrThrow(_stmt, "ruleId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfWasAutomaticallyDetected: Int = getColumnIndexOrThrow(_stmt,
            "wasAutomaticallyDetected")
        val _result: MutableList<IronRuleViolationLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: IronRuleViolationLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpRuleId: String
          _tmpRuleId = _stmt.getText(_columnIndexOfRuleId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpWasAutomaticallyDetected: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfWasAutomaticallyDetected).toInt()
          _tmpWasAutomaticallyDetected = _tmp != 0
          _item =
              IronRuleViolationLogEntity(_tmpId,_tmpRuleId,_tmpDate,_tmpWasAutomaticallyDetected)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearTracks() {
    val _sql: String = "DELETE FROM tracks"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearScheduleBlocks() {
    val _sql: String = "DELETE FROM schedule_blocks"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearCustomKPIs() {
    val _sql: String = "DELETE FROM custom_kpis"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearIronRules() {
    val _sql: String = "DELETE FROM iron_rules"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearHardTruthsOrAffirmations() {
    val _sql: String = "DELETE FROM hard_truths_affirmations"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearMajorMilestones() {
    val _sql: String = "DELETE FROM major_milestones"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearKeyRelationships() {
    val _sql: String = "DELETE FROM key_relationships"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearFinancialCheckpoints() {
    val _sql: String = "DELETE FROM financial_checkpoints"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearMonthlyIncomeEntries() {
    val _sql: String = "DELETE FROM monthly_income_entries"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearIronRuleViolationLogs() {
    val _sql: String = "DELETE FROM iron_rule_violation_logs"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
