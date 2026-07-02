package com.axiom.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.MissionEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
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
public class MissionDao_Impl(
  __db: RoomDatabase,
) : MissionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMissionEntity: EntityInsertAdapter<MissionEntity>

  private val __deleteAdapterOfMissionEntity: EntityDeleteOrUpdateAdapter<MissionEntity>

  private val __updateAdapterOfMissionEntity: EntityDeleteOrUpdateAdapter<MissionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMissionEntity = object : EntityInsertAdapter<MissionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `missions` (`id`,`title`,`track`,`rarity`,`skillId`,`skillName`,`xpReward`,`powerScore`,`status`,`dungeonId`,`estimatedHours`,`actualHours`,`createdAt`,`completedAt`,`rarityColor`,`isInstantGate`,`description`,`trackId`,`scheduleBlockId`,`qualityScore`,`effectiveHours`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MissionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.track)
        statement.bindText(4, entity.rarity)
        statement.bindText(5, entity.skillId)
        statement.bindText(6, entity.skillName)
        statement.bindLong(7, entity.xpReward.toLong())
        statement.bindDouble(8, entity.powerScore.toDouble())
        statement.bindText(9, entity.status)
        val _tmpDungeonId: String? = entity.dungeonId
        if (_tmpDungeonId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpDungeonId)
        }
        statement.bindDouble(11, entity.estimatedHours.toDouble())
        val _tmpActualHours: Float? = entity.actualHours
        if (_tmpActualHours == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpActualHours.toDouble())
        }
        statement.bindLong(13, entity.createdAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpCompletedAt)
        }
        statement.bindLong(15, entity.rarityColor)
        val _tmp: Int = if (entity.isInstantGate) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        statement.bindText(17, entity.description)
        val _tmpTrackId: String? = entity.trackId
        if (_tmpTrackId == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpTrackId)
        }
        val _tmpScheduleBlockId: String? = entity.scheduleBlockId
        if (_tmpScheduleBlockId == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpScheduleBlockId)
        }
        statement.bindDouble(20, entity.qualityScore)
        statement.bindDouble(21, entity.effectiveHours)
      }
    }
    this.__deleteAdapterOfMissionEntity = object : EntityDeleteOrUpdateAdapter<MissionEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `missions` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MissionEntity) {
        statement.bindText(1, entity.id)
      }
    }
    this.__updateAdapterOfMissionEntity = object : EntityDeleteOrUpdateAdapter<MissionEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `missions` SET `id` = ?,`title` = ?,`track` = ?,`rarity` = ?,`skillId` = ?,`skillName` = ?,`xpReward` = ?,`powerScore` = ?,`status` = ?,`dungeonId` = ?,`estimatedHours` = ?,`actualHours` = ?,`createdAt` = ?,`completedAt` = ?,`rarityColor` = ?,`isInstantGate` = ?,`description` = ?,`trackId` = ?,`scheduleBlockId` = ?,`qualityScore` = ?,`effectiveHours` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MissionEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.track)
        statement.bindText(4, entity.rarity)
        statement.bindText(5, entity.skillId)
        statement.bindText(6, entity.skillName)
        statement.bindLong(7, entity.xpReward.toLong())
        statement.bindDouble(8, entity.powerScore.toDouble())
        statement.bindText(9, entity.status)
        val _tmpDungeonId: String? = entity.dungeonId
        if (_tmpDungeonId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpDungeonId)
        }
        statement.bindDouble(11, entity.estimatedHours.toDouble())
        val _tmpActualHours: Float? = entity.actualHours
        if (_tmpActualHours == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpActualHours.toDouble())
        }
        statement.bindLong(13, entity.createdAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpCompletedAt)
        }
        statement.bindLong(15, entity.rarityColor)
        val _tmp: Int = if (entity.isInstantGate) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        statement.bindText(17, entity.description)
        val _tmpTrackId: String? = entity.trackId
        if (_tmpTrackId == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpTrackId)
        }
        val _tmpScheduleBlockId: String? = entity.scheduleBlockId
        if (_tmpScheduleBlockId == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpScheduleBlockId)
        }
        statement.bindDouble(20, entity.qualityScore)
        statement.bindDouble(21, entity.effectiveHours)
        statement.bindText(22, entity.id)
      }
    }
  }

  public override suspend fun insertMission(mission: MissionEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMissionEntity.insert(_connection, mission)
  }

  public override suspend fun deleteMission(mission: MissionEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfMissionEntity.handle(_connection, mission)
  }

  public override suspend fun updateMission(mission: MissionEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfMissionEntity.handle(_connection, mission)
  }

  public override fun getAllMissionsFlow(): Flow<List<MissionEntity>> {
    val _sql: String = "SELECT * FROM missions ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("missions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfTrack: Int = getColumnIndexOrThrow(_stmt, "track")
        val _columnIndexOfRarity: Int = getColumnIndexOrThrow(_stmt, "rarity")
        val _columnIndexOfSkillId: Int = getColumnIndexOrThrow(_stmt, "skillId")
        val _columnIndexOfSkillName: Int = getColumnIndexOrThrow(_stmt, "skillName")
        val _columnIndexOfXpReward: Int = getColumnIndexOrThrow(_stmt, "xpReward")
        val _columnIndexOfPowerScore: Int = getColumnIndexOrThrow(_stmt, "powerScore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfDungeonId: Int = getColumnIndexOrThrow(_stmt, "dungeonId")
        val _columnIndexOfEstimatedHours: Int = getColumnIndexOrThrow(_stmt, "estimatedHours")
        val _columnIndexOfActualHours: Int = getColumnIndexOrThrow(_stmt, "actualHours")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRarityColor: Int = getColumnIndexOrThrow(_stmt, "rarityColor")
        val _columnIndexOfIsInstantGate: Int = getColumnIndexOrThrow(_stmt, "isInstantGate")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfScheduleBlockId: Int = getColumnIndexOrThrow(_stmt, "scheduleBlockId")
        val _columnIndexOfQualityScore: Int = getColumnIndexOrThrow(_stmt, "qualityScore")
        val _columnIndexOfEffectiveHours: Int = getColumnIndexOrThrow(_stmt, "effectiveHours")
        val _result: MutableList<MissionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MissionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpTrack: String
          _tmpTrack = _stmt.getText(_columnIndexOfTrack)
          val _tmpRarity: String
          _tmpRarity = _stmt.getText(_columnIndexOfRarity)
          val _tmpSkillId: String
          _tmpSkillId = _stmt.getText(_columnIndexOfSkillId)
          val _tmpSkillName: String
          _tmpSkillName = _stmt.getText(_columnIndexOfSkillName)
          val _tmpXpReward: Int
          _tmpXpReward = _stmt.getLong(_columnIndexOfXpReward).toInt()
          val _tmpPowerScore: Float
          _tmpPowerScore = _stmt.getDouble(_columnIndexOfPowerScore).toFloat()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpDungeonId: String?
          if (_stmt.isNull(_columnIndexOfDungeonId)) {
            _tmpDungeonId = null
          } else {
            _tmpDungeonId = _stmt.getText(_columnIndexOfDungeonId)
          }
          val _tmpEstimatedHours: Float
          _tmpEstimatedHours = _stmt.getDouble(_columnIndexOfEstimatedHours).toFloat()
          val _tmpActualHours: Float?
          if (_stmt.isNull(_columnIndexOfActualHours)) {
            _tmpActualHours = null
          } else {
            _tmpActualHours = _stmt.getDouble(_columnIndexOfActualHours).toFloat()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRarityColor: Long
          _tmpRarityColor = _stmt.getLong(_columnIndexOfRarityColor)
          val _tmpIsInstantGate: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsInstantGate).toInt()
          _tmpIsInstantGate = _tmp != 0
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpScheduleBlockId: String?
          if (_stmt.isNull(_columnIndexOfScheduleBlockId)) {
            _tmpScheduleBlockId = null
          } else {
            _tmpScheduleBlockId = _stmt.getText(_columnIndexOfScheduleBlockId)
          }
          val _tmpQualityScore: Double
          _tmpQualityScore = _stmt.getDouble(_columnIndexOfQualityScore)
          val _tmpEffectiveHours: Double
          _tmpEffectiveHours = _stmt.getDouble(_columnIndexOfEffectiveHours)
          _item =
              MissionEntity(_tmpId,_tmpTitle,_tmpTrack,_tmpRarity,_tmpSkillId,_tmpSkillName,_tmpXpReward,_tmpPowerScore,_tmpStatus,_tmpDungeonId,_tmpEstimatedHours,_tmpActualHours,_tmpCreatedAt,_tmpCompletedAt,_tmpRarityColor,_tmpIsInstantGate,_tmpDescription,_tmpTrackId,_tmpScheduleBlockId,_tmpQualityScore,_tmpEffectiveHours)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getActiveMissionsFlow(): Flow<List<MissionEntity>> {
    val _sql: String = "SELECT * FROM missions WHERE status = 'ACTIVE' ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("missions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfTrack: Int = getColumnIndexOrThrow(_stmt, "track")
        val _columnIndexOfRarity: Int = getColumnIndexOrThrow(_stmt, "rarity")
        val _columnIndexOfSkillId: Int = getColumnIndexOrThrow(_stmt, "skillId")
        val _columnIndexOfSkillName: Int = getColumnIndexOrThrow(_stmt, "skillName")
        val _columnIndexOfXpReward: Int = getColumnIndexOrThrow(_stmt, "xpReward")
        val _columnIndexOfPowerScore: Int = getColumnIndexOrThrow(_stmt, "powerScore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfDungeonId: Int = getColumnIndexOrThrow(_stmt, "dungeonId")
        val _columnIndexOfEstimatedHours: Int = getColumnIndexOrThrow(_stmt, "estimatedHours")
        val _columnIndexOfActualHours: Int = getColumnIndexOrThrow(_stmt, "actualHours")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRarityColor: Int = getColumnIndexOrThrow(_stmt, "rarityColor")
        val _columnIndexOfIsInstantGate: Int = getColumnIndexOrThrow(_stmt, "isInstantGate")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfScheduleBlockId: Int = getColumnIndexOrThrow(_stmt, "scheduleBlockId")
        val _columnIndexOfQualityScore: Int = getColumnIndexOrThrow(_stmt, "qualityScore")
        val _columnIndexOfEffectiveHours: Int = getColumnIndexOrThrow(_stmt, "effectiveHours")
        val _result: MutableList<MissionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MissionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpTrack: String
          _tmpTrack = _stmt.getText(_columnIndexOfTrack)
          val _tmpRarity: String
          _tmpRarity = _stmt.getText(_columnIndexOfRarity)
          val _tmpSkillId: String
          _tmpSkillId = _stmt.getText(_columnIndexOfSkillId)
          val _tmpSkillName: String
          _tmpSkillName = _stmt.getText(_columnIndexOfSkillName)
          val _tmpXpReward: Int
          _tmpXpReward = _stmt.getLong(_columnIndexOfXpReward).toInt()
          val _tmpPowerScore: Float
          _tmpPowerScore = _stmt.getDouble(_columnIndexOfPowerScore).toFloat()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpDungeonId: String?
          if (_stmt.isNull(_columnIndexOfDungeonId)) {
            _tmpDungeonId = null
          } else {
            _tmpDungeonId = _stmt.getText(_columnIndexOfDungeonId)
          }
          val _tmpEstimatedHours: Float
          _tmpEstimatedHours = _stmt.getDouble(_columnIndexOfEstimatedHours).toFloat()
          val _tmpActualHours: Float?
          if (_stmt.isNull(_columnIndexOfActualHours)) {
            _tmpActualHours = null
          } else {
            _tmpActualHours = _stmt.getDouble(_columnIndexOfActualHours).toFloat()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRarityColor: Long
          _tmpRarityColor = _stmt.getLong(_columnIndexOfRarityColor)
          val _tmpIsInstantGate: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsInstantGate).toInt()
          _tmpIsInstantGate = _tmp != 0
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpScheduleBlockId: String?
          if (_stmt.isNull(_columnIndexOfScheduleBlockId)) {
            _tmpScheduleBlockId = null
          } else {
            _tmpScheduleBlockId = _stmt.getText(_columnIndexOfScheduleBlockId)
          }
          val _tmpQualityScore: Double
          _tmpQualityScore = _stmt.getDouble(_columnIndexOfQualityScore)
          val _tmpEffectiveHours: Double
          _tmpEffectiveHours = _stmt.getDouble(_columnIndexOfEffectiveHours)
          _item =
              MissionEntity(_tmpId,_tmpTitle,_tmpTrack,_tmpRarity,_tmpSkillId,_tmpSkillName,_tmpXpReward,_tmpPowerScore,_tmpStatus,_tmpDungeonId,_tmpEstimatedHours,_tmpActualHours,_tmpCreatedAt,_tmpCompletedAt,_tmpRarityColor,_tmpIsInstantGate,_tmpDescription,_tmpTrackId,_tmpScheduleBlockId,_tmpQualityScore,_tmpEffectiveHours)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMissionById(id: String): MissionEntity? {
    val _sql: String = "SELECT * FROM missions WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfTrack: Int = getColumnIndexOrThrow(_stmt, "track")
        val _columnIndexOfRarity: Int = getColumnIndexOrThrow(_stmt, "rarity")
        val _columnIndexOfSkillId: Int = getColumnIndexOrThrow(_stmt, "skillId")
        val _columnIndexOfSkillName: Int = getColumnIndexOrThrow(_stmt, "skillName")
        val _columnIndexOfXpReward: Int = getColumnIndexOrThrow(_stmt, "xpReward")
        val _columnIndexOfPowerScore: Int = getColumnIndexOrThrow(_stmt, "powerScore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfDungeonId: Int = getColumnIndexOrThrow(_stmt, "dungeonId")
        val _columnIndexOfEstimatedHours: Int = getColumnIndexOrThrow(_stmt, "estimatedHours")
        val _columnIndexOfActualHours: Int = getColumnIndexOrThrow(_stmt, "actualHours")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRarityColor: Int = getColumnIndexOrThrow(_stmt, "rarityColor")
        val _columnIndexOfIsInstantGate: Int = getColumnIndexOrThrow(_stmt, "isInstantGate")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfScheduleBlockId: Int = getColumnIndexOrThrow(_stmt, "scheduleBlockId")
        val _columnIndexOfQualityScore: Int = getColumnIndexOrThrow(_stmt, "qualityScore")
        val _columnIndexOfEffectiveHours: Int = getColumnIndexOrThrow(_stmt, "effectiveHours")
        val _result: MissionEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpTrack: String
          _tmpTrack = _stmt.getText(_columnIndexOfTrack)
          val _tmpRarity: String
          _tmpRarity = _stmt.getText(_columnIndexOfRarity)
          val _tmpSkillId: String
          _tmpSkillId = _stmt.getText(_columnIndexOfSkillId)
          val _tmpSkillName: String
          _tmpSkillName = _stmt.getText(_columnIndexOfSkillName)
          val _tmpXpReward: Int
          _tmpXpReward = _stmt.getLong(_columnIndexOfXpReward).toInt()
          val _tmpPowerScore: Float
          _tmpPowerScore = _stmt.getDouble(_columnIndexOfPowerScore).toFloat()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpDungeonId: String?
          if (_stmt.isNull(_columnIndexOfDungeonId)) {
            _tmpDungeonId = null
          } else {
            _tmpDungeonId = _stmt.getText(_columnIndexOfDungeonId)
          }
          val _tmpEstimatedHours: Float
          _tmpEstimatedHours = _stmt.getDouble(_columnIndexOfEstimatedHours).toFloat()
          val _tmpActualHours: Float?
          if (_stmt.isNull(_columnIndexOfActualHours)) {
            _tmpActualHours = null
          } else {
            _tmpActualHours = _stmt.getDouble(_columnIndexOfActualHours).toFloat()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRarityColor: Long
          _tmpRarityColor = _stmt.getLong(_columnIndexOfRarityColor)
          val _tmpIsInstantGate: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsInstantGate).toInt()
          _tmpIsInstantGate = _tmp != 0
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpScheduleBlockId: String?
          if (_stmt.isNull(_columnIndexOfScheduleBlockId)) {
            _tmpScheduleBlockId = null
          } else {
            _tmpScheduleBlockId = _stmt.getText(_columnIndexOfScheduleBlockId)
          }
          val _tmpQualityScore: Double
          _tmpQualityScore = _stmt.getDouble(_columnIndexOfQualityScore)
          val _tmpEffectiveHours: Double
          _tmpEffectiveHours = _stmt.getDouble(_columnIndexOfEffectiveHours)
          _result =
              MissionEntity(_tmpId,_tmpTitle,_tmpTrack,_tmpRarity,_tmpSkillId,_tmpSkillName,_tmpXpReward,_tmpPowerScore,_tmpStatus,_tmpDungeonId,_tmpEstimatedHours,_tmpActualHours,_tmpCreatedAt,_tmpCompletedAt,_tmpRarityColor,_tmpIsInstantGate,_tmpDescription,_tmpTrackId,_tmpScheduleBlockId,_tmpQualityScore,_tmpEffectiveHours)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMissionsByScheduleBlockId(scheduleBlockId: String):
      List<MissionEntity> {
    val _sql: String = "SELECT * FROM missions WHERE scheduleBlockId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, scheduleBlockId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfTrack: Int = getColumnIndexOrThrow(_stmt, "track")
        val _columnIndexOfRarity: Int = getColumnIndexOrThrow(_stmt, "rarity")
        val _columnIndexOfSkillId: Int = getColumnIndexOrThrow(_stmt, "skillId")
        val _columnIndexOfSkillName: Int = getColumnIndexOrThrow(_stmt, "skillName")
        val _columnIndexOfXpReward: Int = getColumnIndexOrThrow(_stmt, "xpReward")
        val _columnIndexOfPowerScore: Int = getColumnIndexOrThrow(_stmt, "powerScore")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfDungeonId: Int = getColumnIndexOrThrow(_stmt, "dungeonId")
        val _columnIndexOfEstimatedHours: Int = getColumnIndexOrThrow(_stmt, "estimatedHours")
        val _columnIndexOfActualHours: Int = getColumnIndexOrThrow(_stmt, "actualHours")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfRarityColor: Int = getColumnIndexOrThrow(_stmt, "rarityColor")
        val _columnIndexOfIsInstantGate: Int = getColumnIndexOrThrow(_stmt, "isInstantGate")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfScheduleBlockId: Int = getColumnIndexOrThrow(_stmt, "scheduleBlockId")
        val _columnIndexOfQualityScore: Int = getColumnIndexOrThrow(_stmt, "qualityScore")
        val _columnIndexOfEffectiveHours: Int = getColumnIndexOrThrow(_stmt, "effectiveHours")
        val _result: MutableList<MissionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MissionEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpTrack: String
          _tmpTrack = _stmt.getText(_columnIndexOfTrack)
          val _tmpRarity: String
          _tmpRarity = _stmt.getText(_columnIndexOfRarity)
          val _tmpSkillId: String
          _tmpSkillId = _stmt.getText(_columnIndexOfSkillId)
          val _tmpSkillName: String
          _tmpSkillName = _stmt.getText(_columnIndexOfSkillName)
          val _tmpXpReward: Int
          _tmpXpReward = _stmt.getLong(_columnIndexOfXpReward).toInt()
          val _tmpPowerScore: Float
          _tmpPowerScore = _stmt.getDouble(_columnIndexOfPowerScore).toFloat()
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpDungeonId: String?
          if (_stmt.isNull(_columnIndexOfDungeonId)) {
            _tmpDungeonId = null
          } else {
            _tmpDungeonId = _stmt.getText(_columnIndexOfDungeonId)
          }
          val _tmpEstimatedHours: Float
          _tmpEstimatedHours = _stmt.getDouble(_columnIndexOfEstimatedHours).toFloat()
          val _tmpActualHours: Float?
          if (_stmt.isNull(_columnIndexOfActualHours)) {
            _tmpActualHours = null
          } else {
            _tmpActualHours = _stmt.getDouble(_columnIndexOfActualHours).toFloat()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpRarityColor: Long
          _tmpRarityColor = _stmt.getLong(_columnIndexOfRarityColor)
          val _tmpIsInstantGate: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsInstantGate).toInt()
          _tmpIsInstantGate = _tmp != 0
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpScheduleBlockId: String?
          if (_stmt.isNull(_columnIndexOfScheduleBlockId)) {
            _tmpScheduleBlockId = null
          } else {
            _tmpScheduleBlockId = _stmt.getText(_columnIndexOfScheduleBlockId)
          }
          val _tmpQualityScore: Double
          _tmpQualityScore = _stmt.getDouble(_columnIndexOfQualityScore)
          val _tmpEffectiveHours: Double
          _tmpEffectiveHours = _stmt.getDouble(_columnIndexOfEffectiveHours)
          _item =
              MissionEntity(_tmpId,_tmpTitle,_tmpTrack,_tmpRarity,_tmpSkillId,_tmpSkillName,_tmpXpReward,_tmpPowerScore,_tmpStatus,_tmpDungeonId,_tmpEstimatedHours,_tmpActualHours,_tmpCreatedAt,_tmpCompletedAt,_tmpRarityColor,_tmpIsInstantGate,_tmpDescription,_tmpTrackId,_tmpScheduleBlockId,_tmpQualityScore,_tmpEffectiveHours)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
