package com.axiom.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.ShadowEntity
import javax.`annotation`.processing.Generated
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
public class ShadowDao_Impl(
  __db: RoomDatabase,
) : ShadowDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfShadowEntity: EntityInsertAdapter<ShadowEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfShadowEntity = object : EntityInsertAdapter<ShadowEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `shadows` (`id`,`name`,`skillId`,`rankLabel`,`acquiredAt`,`skillCategory`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ShadowEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.skillId)
        statement.bindText(4, entity.rankLabel)
        statement.bindLong(5, entity.acquiredAt)
        statement.bindText(6, entity.skillCategory)
      }
    }
  }

  public override suspend fun insertShadow(shadow: ShadowEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfShadowEntity.insert(_connection, shadow)
  }

  public override fun getAllShadowsFlow(): Flow<List<ShadowEntity>> {
    val _sql: String = "SELECT * FROM shadows ORDER BY acquiredAt DESC"
    return createFlow(__db, false, arrayOf("shadows")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSkillId: Int = getColumnIndexOrThrow(_stmt, "skillId")
        val _columnIndexOfRankLabel: Int = getColumnIndexOrThrow(_stmt, "rankLabel")
        val _columnIndexOfAcquiredAt: Int = getColumnIndexOrThrow(_stmt, "acquiredAt")
        val _columnIndexOfSkillCategory: Int = getColumnIndexOrThrow(_stmt, "skillCategory")
        val _result: MutableList<ShadowEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShadowEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSkillId: String
          _tmpSkillId = _stmt.getText(_columnIndexOfSkillId)
          val _tmpRankLabel: String
          _tmpRankLabel = _stmt.getText(_columnIndexOfRankLabel)
          val _tmpAcquiredAt: Long
          _tmpAcquiredAt = _stmt.getLong(_columnIndexOfAcquiredAt)
          val _tmpSkillCategory: String
          _tmpSkillCategory = _stmt.getText(_columnIndexOfSkillCategory)
          _item =
              ShadowEntity(_tmpId,_tmpName,_tmpSkillId,_tmpRankLabel,_tmpAcquiredAt,_tmpSkillCategory)
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
