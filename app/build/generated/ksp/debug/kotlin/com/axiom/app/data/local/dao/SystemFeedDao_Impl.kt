package com.axiom.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.SystemFeedEntity
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
public class SystemFeedDao_Impl(
  __db: RoomDatabase,
) : SystemFeedDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSystemFeedEntity: EntityInsertAdapter<SystemFeedEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSystemFeedEntity = object : EntityInsertAdapter<SystemFeedEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `system_feed` (`id`,`message`,`type`,`xpGained`,`timestamp`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SystemFeedEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.message)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.xpGained.toLong())
        statement.bindLong(5, entity.timestamp)
      }
    }
  }

  public override suspend fun insertMessage(message: SystemFeedEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSystemFeedEntity.insert(_connection, message)
  }

  public override fun getFeed(limit: Int): Flow<List<SystemFeedEntity>> {
    val _sql: String = "SELECT * FROM system_feed ORDER BY timestamp DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("system_feed")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfMessage: Int = getColumnIndexOrThrow(_stmt, "message")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfXpGained: Int = getColumnIndexOrThrow(_stmt, "xpGained")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<SystemFeedEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SystemFeedEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpMessage: String
          _tmpMessage = _stmt.getText(_columnIndexOfMessage)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpXpGained: Int
          _tmpXpGained = _stmt.getLong(_columnIndexOfXpGained).toInt()
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item = SystemFeedEntity(_tmpId,_tmpMessage,_tmpType,_tmpXpGained,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldEntries(keepCount: Int) {
    val _sql: String =
        "DELETE FROM system_feed WHERE id NOT IN (SELECT id FROM system_feed ORDER BY timestamp DESC LIMIT ?)"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, keepCount.toLong())
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
