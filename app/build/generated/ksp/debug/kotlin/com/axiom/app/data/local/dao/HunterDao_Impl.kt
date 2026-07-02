package com.axiom.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.HunterEntity
import javax.`annotation`.processing.Generated
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class HunterDao_Impl(
  __db: RoomDatabase,
) : HunterDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHunterEntity: EntityInsertAdapter<HunterEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHunterEntity = object : EntityInsertAdapter<HunterEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `hunter_profile` (`id`,`name`,`level`,`rankLabel`,`totalXP`,`currentXP`,`xpToNextLevel`,`progressPercent`,`rankColor`,`rankGlyph`,`personalThesis`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HunterEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.level.toLong())
        statement.bindText(4, entity.rankLabel)
        statement.bindLong(5, entity.totalXP)
        statement.bindLong(6, entity.currentXP.toLong())
        statement.bindLong(7, entity.xpToNextLevel.toLong())
        statement.bindDouble(8, entity.progressPercent.toDouble())
        statement.bindLong(9, entity.rankColor)
        statement.bindText(10, entity.rankGlyph)
        statement.bindText(11, entity.personalThesis)
      }
    }
  }

  public override suspend fun updateProfile(profile: HunterEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfHunterEntity.insert(_connection, profile)
  }

  public override fun getProfileFlow(): Flow<HunterEntity?> {
    val _sql: String = "SELECT * FROM hunter_profile LIMIT 1"
    return createFlow(__db, false, arrayOf("hunter_profile")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfRankLabel: Int = getColumnIndexOrThrow(_stmt, "rankLabel")
        val _columnIndexOfTotalXP: Int = getColumnIndexOrThrow(_stmt, "totalXP")
        val _columnIndexOfCurrentXP: Int = getColumnIndexOrThrow(_stmt, "currentXP")
        val _columnIndexOfXpToNextLevel: Int = getColumnIndexOrThrow(_stmt, "xpToNextLevel")
        val _columnIndexOfProgressPercent: Int = getColumnIndexOrThrow(_stmt, "progressPercent")
        val _columnIndexOfRankColor: Int = getColumnIndexOrThrow(_stmt, "rankColor")
        val _columnIndexOfRankGlyph: Int = getColumnIndexOrThrow(_stmt, "rankGlyph")
        val _columnIndexOfPersonalThesis: Int = getColumnIndexOrThrow(_stmt, "personalThesis")
        val _result: HunterEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLevel: Int
          _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          val _tmpRankLabel: String
          _tmpRankLabel = _stmt.getText(_columnIndexOfRankLabel)
          val _tmpTotalXP: Long
          _tmpTotalXP = _stmt.getLong(_columnIndexOfTotalXP)
          val _tmpCurrentXP: Int
          _tmpCurrentXP = _stmt.getLong(_columnIndexOfCurrentXP).toInt()
          val _tmpXpToNextLevel: Int
          _tmpXpToNextLevel = _stmt.getLong(_columnIndexOfXpToNextLevel).toInt()
          val _tmpProgressPercent: Float
          _tmpProgressPercent = _stmt.getDouble(_columnIndexOfProgressPercent).toFloat()
          val _tmpRankColor: Long
          _tmpRankColor = _stmt.getLong(_columnIndexOfRankColor)
          val _tmpRankGlyph: String
          _tmpRankGlyph = _stmt.getText(_columnIndexOfRankGlyph)
          val _tmpPersonalThesis: String
          _tmpPersonalThesis = _stmt.getText(_columnIndexOfPersonalThesis)
          _result =
              HunterEntity(_tmpId,_tmpName,_tmpLevel,_tmpRankLabel,_tmpTotalXP,_tmpCurrentXP,_tmpXpToNextLevel,_tmpProgressPercent,_tmpRankColor,_tmpRankGlyph,_tmpPersonalThesis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getProfile(): HunterEntity? {
    val _sql: String = "SELECT * FROM hunter_profile LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfRankLabel: Int = getColumnIndexOrThrow(_stmt, "rankLabel")
        val _columnIndexOfTotalXP: Int = getColumnIndexOrThrow(_stmt, "totalXP")
        val _columnIndexOfCurrentXP: Int = getColumnIndexOrThrow(_stmt, "currentXP")
        val _columnIndexOfXpToNextLevel: Int = getColumnIndexOrThrow(_stmt, "xpToNextLevel")
        val _columnIndexOfProgressPercent: Int = getColumnIndexOrThrow(_stmt, "progressPercent")
        val _columnIndexOfRankColor: Int = getColumnIndexOrThrow(_stmt, "rankColor")
        val _columnIndexOfRankGlyph: Int = getColumnIndexOrThrow(_stmt, "rankGlyph")
        val _columnIndexOfPersonalThesis: Int = getColumnIndexOrThrow(_stmt, "personalThesis")
        val _result: HunterEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpLevel: Int
          _tmpLevel = _stmt.getLong(_columnIndexOfLevel).toInt()
          val _tmpRankLabel: String
          _tmpRankLabel = _stmt.getText(_columnIndexOfRankLabel)
          val _tmpTotalXP: Long
          _tmpTotalXP = _stmt.getLong(_columnIndexOfTotalXP)
          val _tmpCurrentXP: Int
          _tmpCurrentXP = _stmt.getLong(_columnIndexOfCurrentXP).toInt()
          val _tmpXpToNextLevel: Int
          _tmpXpToNextLevel = _stmt.getLong(_columnIndexOfXpToNextLevel).toInt()
          val _tmpProgressPercent: Float
          _tmpProgressPercent = _stmt.getDouble(_columnIndexOfProgressPercent).toFloat()
          val _tmpRankColor: Long
          _tmpRankColor = _stmt.getLong(_columnIndexOfRankColor)
          val _tmpRankGlyph: String
          _tmpRankGlyph = _stmt.getText(_columnIndexOfRankGlyph)
          val _tmpPersonalThesis: String
          _tmpPersonalThesis = _stmt.getText(_columnIndexOfPersonalThesis)
          _result =
              HunterEntity(_tmpId,_tmpName,_tmpLevel,_tmpRankLabel,_tmpTotalXP,_tmpCurrentXP,_tmpXpToNextLevel,_tmpProgressPercent,_tmpRankColor,_tmpRankGlyph,_tmpPersonalThesis)
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
