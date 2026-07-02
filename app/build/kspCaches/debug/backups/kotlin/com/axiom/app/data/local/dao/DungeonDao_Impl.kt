package com.axiom.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.DungeonEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class DungeonDao_Impl(
  __db: RoomDatabase,
) : DungeonDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDungeonEntity: EntityInsertAdapter<DungeonEntity>

  private val __updateAdapterOfDungeonEntity: EntityDeleteOrUpdateAdapter<DungeonEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDungeonEntity = object : EntityInsertAdapter<DungeonEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `dungeons` (`id`,`name`,`description`,`rarity`,`totalStages`,`completedStages`,`isBossDefeated`,`createdAt`,`completedAt`,`stageDescriptions`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DungeonEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.rarity)
        statement.bindLong(5, entity.totalStages.toLong())
        statement.bindLong(6, entity.completedStages.toLong())
        val _tmp: Int = if (entity.isBossDefeated) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.createdAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCompletedAt)
        }
        statement.bindText(10, entity.stageDescriptions)
      }
    }
    this.__updateAdapterOfDungeonEntity = object : EntityDeleteOrUpdateAdapter<DungeonEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `dungeons` SET `id` = ?,`name` = ?,`description` = ?,`rarity` = ?,`totalStages` = ?,`completedStages` = ?,`isBossDefeated` = ?,`createdAt` = ?,`completedAt` = ?,`stageDescriptions` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DungeonEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.rarity)
        statement.bindLong(5, entity.totalStages.toLong())
        statement.bindLong(6, entity.completedStages.toLong())
        val _tmp: Int = if (entity.isBossDefeated) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.createdAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCompletedAt)
        }
        statement.bindText(10, entity.stageDescriptions)
        statement.bindText(11, entity.id)
      }
    }
  }

  public override suspend fun insertDungeon(dungeon: DungeonEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfDungeonEntity.insert(_connection, dungeon)
  }

  public override suspend fun updateDungeon(dungeon: DungeonEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfDungeonEntity.handle(_connection, dungeon)
  }

  public override fun getAllDungeonsFlow(): Flow<List<DungeonEntity>> {
    val _sql: String = "SELECT * FROM dungeons ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("dungeons")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRarity: Int = getColumnIndexOrThrow(_stmt, "rarity")
        val _columnIndexOfTotalStages: Int = getColumnIndexOrThrow(_stmt, "totalStages")
        val _columnIndexOfCompletedStages: Int = getColumnIndexOrThrow(_stmt, "completedStages")
        val _columnIndexOfIsBossDefeated: Int = getColumnIndexOrThrow(_stmt, "isBossDefeated")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfStageDescriptions: Int = getColumnIndexOrThrow(_stmt, "stageDescriptions")
        val _result: MutableList<DungeonEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DungeonEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRarity: String
          _tmpRarity = _stmt.getText(_columnIndexOfRarity)
          val _tmpTotalStages: Int
          _tmpTotalStages = _stmt.getLong(_columnIndexOfTotalStages).toInt()
          val _tmpCompletedStages: Int
          _tmpCompletedStages = _stmt.getLong(_columnIndexOfCompletedStages).toInt()
          val _tmpIsBossDefeated: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBossDefeated).toInt()
          _tmpIsBossDefeated = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpStageDescriptions: String
          _tmpStageDescriptions = _stmt.getText(_columnIndexOfStageDescriptions)
          _item =
              DungeonEntity(_tmpId,_tmpName,_tmpDescription,_tmpRarity,_tmpTotalStages,_tmpCompletedStages,_tmpIsBossDefeated,_tmpCreatedAt,_tmpCompletedAt,_tmpStageDescriptions)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDungeonById(id: String): DungeonEntity? {
    val _sql: String = "SELECT * FROM dungeons WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRarity: Int = getColumnIndexOrThrow(_stmt, "rarity")
        val _columnIndexOfTotalStages: Int = getColumnIndexOrThrow(_stmt, "totalStages")
        val _columnIndexOfCompletedStages: Int = getColumnIndexOrThrow(_stmt, "completedStages")
        val _columnIndexOfIsBossDefeated: Int = getColumnIndexOrThrow(_stmt, "isBossDefeated")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfStageDescriptions: Int = getColumnIndexOrThrow(_stmt, "stageDescriptions")
        val _result: DungeonEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpRarity: String
          _tmpRarity = _stmt.getText(_columnIndexOfRarity)
          val _tmpTotalStages: Int
          _tmpTotalStages = _stmt.getLong(_columnIndexOfTotalStages).toInt()
          val _tmpCompletedStages: Int
          _tmpCompletedStages = _stmt.getLong(_columnIndexOfCompletedStages).toInt()
          val _tmpIsBossDefeated: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsBossDefeated).toInt()
          _tmpIsBossDefeated = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpStageDescriptions: String
          _tmpStageDescriptions = _stmt.getText(_columnIndexOfStageDescriptions)
          _result =
              DungeonEntity(_tmpId,_tmpName,_tmpDescription,_tmpRarity,_tmpTotalStages,_tmpCompletedStages,_tmpIsBossDefeated,_tmpCreatedAt,_tmpCompletedAt,_tmpStageDescriptions)
        } else {
          _result = null
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
