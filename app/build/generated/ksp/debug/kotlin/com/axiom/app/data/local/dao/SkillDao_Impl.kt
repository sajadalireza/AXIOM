package com.axiom.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.SkillEntity
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
public class SkillDao_Impl(
  __db: RoomDatabase,
) : SkillDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSkillEntity: EntityInsertAdapter<SkillEntity>

  private val __updateAdapterOfSkillEntity: EntityDeleteOrUpdateAdapter<SkillEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSkillEntity = object : EntityInsertAdapter<SkillEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `skills` (`id`,`name`,`category`,`currentXP`,`level`,`rankLabel`,`parentId`,`isUnlocked`,`xpToNextRank`,`rankProgressPercent`,`isShadowCandidate`,`rankColor`,`trackId`,`totalRawHours`,`totalEffectiveHours`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SkillEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.category)
        statement.bindLong(4, entity.currentXP)
        statement.bindLong(5, entity.level.toLong())
        statement.bindText(6, entity.rankLabel)
        val _tmpParentId: String? = entity.parentId
        if (_tmpParentId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpParentId)
        }
        val _tmp: Int = if (entity.isUnlocked) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindLong(9, entity.xpToNextRank)
        statement.bindDouble(10, entity.rankProgressPercent.toDouble())
        val _tmp_1: Int = if (entity.isShadowCandidate) 1 else 0
        statement.bindLong(11, _tmp_1.toLong())
        statement.bindLong(12, entity.rankColor)
        val _tmpTrackId: String? = entity.trackId
        if (_tmpTrackId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpTrackId)
        }
        statement.bindDouble(14, entity.totalRawHours)
        statement.bindDouble(15, entity.totalEffectiveHours)
      }
    }
    this.__updateAdapterOfSkillEntity = object : EntityDeleteOrUpdateAdapter<SkillEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `skills` SET `id` = ?,`name` = ?,`category` = ?,`currentXP` = ?,`level` = ?,`rankLabel` = ?,`parentId` = ?,`isUnlocked` = ?,`xpToNextRank` = ?,`rankProgressPercent` = ?,`isShadowCandidate` = ?,`rankColor` = ?,`trackId` = ?,`totalRawHours` = ?,`totalEffectiveHours` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: SkillEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.category)
        statement.bindLong(4, entity.currentXP)
        statement.bindLong(5, entity.level.toLong())
        statement.bindText(6, entity.rankLabel)
        val _tmpParentId: String? = entity.parentId
        if (_tmpParentId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpParentId)
        }
        val _tmp: Int = if (entity.isUnlocked) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindLong(9, entity.xpToNextRank)
        statement.bindDouble(10, entity.rankProgressPercent.toDouble())
        val _tmp_1: Int = if (entity.isShadowCandidate) 1 else 0
        statement.bindLong(11, _tmp_1.toLong())
        statement.bindLong(12, entity.rankColor)
        val _tmpTrackId: String? = entity.trackId
        if (_tmpTrackId == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpTrackId)
        }
        statement.bindDouble(14, entity.totalRawHours)
        statement.bindDouble(15, entity.totalEffectiveHours)
        statement.bindText(16, entity.id)
      }
    }
  }

  public override suspend fun insertSkill(skill: SkillEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfSkillEntity.insert(_connection, skill)
  }

  public override suspend fun updateSkill(skill: SkillEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfSkillEntity.handle(_connection, skill)
  }

  public override fun getAllSkillsFlow(): Flow<List<SkillEntity>> {
    val _sql: String = "SELECT * FROM skills ORDER BY category ASC, name ASC"
    return createFlow(__db, false, arrayOf("skills")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfCurrentXP: Int = getColumnIndexOrThrow(_stmt, "currentXP")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfRankLabel: Int = getColumnIndexOrThrow(_stmt, "rankLabel")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfIsUnlocked: Int = getColumnIndexOrThrow(_stmt, "isUnlocked")
        val _columnIndexOfXpToNextRank: Int = getColumnIndexOrThrow(_stmt, "xpToNextRank")
        val _columnIndexOfRankProgressPercent: Int = getColumnIndexOrThrow(_stmt,
            "rankProgressPercent")
        val _columnIndexOfIsShadowCandidate: Int = getColumnIndexOrThrow(_stmt, "isShadowCandidate")
        val _columnIndexOfRankColor: Int = getColumnIndexOrThrow(_stmt, "rankColor")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfTotalRawHours: Int = getColumnIndexOrThrow(_stmt, "totalRawHours")
        val _columnIndexOfTotalEffectiveHours: Int = getColumnIndexOrThrow(_stmt,
            "totalEffectiveHours")
        val _result: MutableList<SkillEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SkillEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpCurrentXP: Long
          _tmpCurrentXP = _stmt.getLong(_columnIndexOfCurrentXP)
          val _tmpLevel: Int
          _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          val _tmpRankLabel: String
          _tmpRankLabel = _stmt.getText(_columnIndexOfRankLabel)
          val _tmpParentId: String?
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null
          } else {
            _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          }
          val _tmpIsUnlocked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsUnlocked).toInt()
          _tmpIsUnlocked = _tmp != 0
          val _tmpXpToNextRank: Long
          _tmpXpToNextRank = _stmt.getLong(_columnIndexOfXpToNextRank)
          val _tmpRankProgressPercent: Float
          _tmpRankProgressPercent = _stmt.getDouble(_columnIndexOfRankProgressPercent).toFloat()
          val _tmpIsShadowCandidate: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsShadowCandidate).toInt()
          _tmpIsShadowCandidate = _tmp_1 != 0
          val _tmpRankColor: Long
          _tmpRankColor = _stmt.getLong(_columnIndexOfRankColor)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpTotalRawHours: Double
          _tmpTotalRawHours = _stmt.getDouble(_columnIndexOfTotalRawHours)
          val _tmpTotalEffectiveHours: Double
          _tmpTotalEffectiveHours = _stmt.getDouble(_columnIndexOfTotalEffectiveHours)
          _item =
              SkillEntity(_tmpId,_tmpName,_tmpCategory,_tmpCurrentXP,_tmpLevel,_tmpRankLabel,_tmpParentId,_tmpIsUnlocked,_tmpXpToNextRank,_tmpRankProgressPercent,_tmpIsShadowCandidate,_tmpRankColor,_tmpTrackId,_tmpTotalRawHours,_tmpTotalEffectiveHours)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSkillById(id: String): SkillEntity? {
    val _sql: String = "SELECT * FROM skills WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfCurrentXP: Int = getColumnIndexOrThrow(_stmt, "currentXP")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfRankLabel: Int = getColumnIndexOrThrow(_stmt, "rankLabel")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfIsUnlocked: Int = getColumnIndexOrThrow(_stmt, "isUnlocked")
        val _columnIndexOfXpToNextRank: Int = getColumnIndexOrThrow(_stmt, "xpToNextRank")
        val _columnIndexOfRankProgressPercent: Int = getColumnIndexOrThrow(_stmt,
            "rankProgressPercent")
        val _columnIndexOfIsShadowCandidate: Int = getColumnIndexOrThrow(_stmt, "isShadowCandidate")
        val _columnIndexOfRankColor: Int = getColumnIndexOrThrow(_stmt, "rankColor")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfTotalRawHours: Int = getColumnIndexOrThrow(_stmt, "totalRawHours")
        val _columnIndexOfTotalEffectiveHours: Int = getColumnIndexOrThrow(_stmt,
            "totalEffectiveHours")
        val _result: SkillEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpCurrentXP: Long
          _tmpCurrentXP = _stmt.getLong(_columnIndexOfCurrentXP)
          val _tmpLevel: Int
          _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          val _tmpRankLabel: String
          _tmpRankLabel = _stmt.getText(_columnIndexOfRankLabel)
          val _tmpParentId: String?
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null
          } else {
            _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          }
          val _tmpIsUnlocked: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsUnlocked).toInt()
          _tmpIsUnlocked = _tmp != 0
          val _tmpXpToNextRank: Long
          _tmpXpToNextRank = _stmt.getLong(_columnIndexOfXpToNextRank)
          val _tmpRankProgressPercent: Float
          _tmpRankProgressPercent = _stmt.getDouble(_columnIndexOfRankProgressPercent).toFloat()
          val _tmpIsShadowCandidate: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsShadowCandidate).toInt()
          _tmpIsShadowCandidate = _tmp_1 != 0
          val _tmpRankColor: Long
          _tmpRankColor = _stmt.getLong(_columnIndexOfRankColor)
          val _tmpTrackId: String?
          if (_stmt.isNull(_columnIndexOfTrackId)) {
            _tmpTrackId = null
          } else {
            _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          }
          val _tmpTotalRawHours: Double
          _tmpTotalRawHours = _stmt.getDouble(_columnIndexOfTotalRawHours)
          val _tmpTotalEffectiveHours: Double
          _tmpTotalEffectiveHours = _stmt.getDouble(_columnIndexOfTotalEffectiveHours)
          _result =
              SkillEntity(_tmpId,_tmpName,_tmpCategory,_tmpCurrentXP,_tmpLevel,_tmpRankLabel,_tmpParentId,_tmpIsUnlocked,_tmpXpToNextRank,_tmpRankProgressPercent,_tmpIsShadowCandidate,_tmpRankColor,_tmpTrackId,_tmpTotalRawHours,_tmpTotalEffectiveHours)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSkillById(id: String) {
    val _sql: String = "DELETE FROM skills WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
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
