package com.axiom.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.VitalLogEntity
import javax.`annotation`.processing.Generated
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
public class VitalLogDao_Impl(
  __db: RoomDatabase,
) : VitalLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfVitalLogEntity: EntityInsertAdapter<VitalLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfVitalLogEntity = object : EntityInsertAdapter<VitalLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `vital_logs` (`id`,`date`,`type`,`value`,`loggedAt`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: VitalLogEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.date)
        statement.bindText(3, entity.type)
        statement.bindDouble(4, entity.value.toDouble())
        statement.bindLong(5, entity.loggedAt)
      }
    }
  }

  public override suspend fun insertVitalLog(vitalLog: VitalLogEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfVitalLogEntity.insert(_connection, vitalLog)
  }

  public override fun getVitalLogFlow(date: Long, type: String): Flow<VitalLogEntity?> {
    val _sql: String = "SELECT * FROM vital_logs WHERE date = ? AND type = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("vital_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, date)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfLoggedAt: Int = getColumnIndexOrThrow(_stmt, "loggedAt")
        val _result: VitalLogEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpValue: Float
          _tmpValue = _stmt.getDouble(_columnIndexOfValue).toFloat()
          val _tmpLoggedAt: Long
          _tmpLoggedAt = _stmt.getLong(_columnIndexOfLoggedAt)
          _result = VitalLogEntity(_tmpId,_tmpDate,_tmpType,_tmpValue,_tmpLoggedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getVitalLog(date: Long, type: String): VitalLogEntity? {
    val _sql: String = "SELECT * FROM vital_logs WHERE date = ? AND type = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, date)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfLoggedAt: Int = getColumnIndexOrThrow(_stmt, "loggedAt")
        val _result: VitalLogEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpValue: Float
          _tmpValue = _stmt.getDouble(_columnIndexOfValue).toFloat()
          val _tmpLoggedAt: Long
          _tmpLoggedAt = _stmt.getLong(_columnIndexOfLoggedAt)
          _result = VitalLogEntity(_tmpId,_tmpDate,_tmpType,_tmpValue,_tmpLoggedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTrendFlow(
    type: String,
    startDate: Long,
    endDate: Long,
  ): Flow<List<VitalLogEntity>> {
    val _sql: String =
        "SELECT * FROM vital_logs WHERE type = ? AND date >= ? AND date <= ? ORDER BY date ASC"
    return createFlow(__db, false, arrayOf("vital_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfLoggedAt: Int = getColumnIndexOrThrow(_stmt, "loggedAt")
        val _result: MutableList<VitalLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VitalLogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpValue: Float
          _tmpValue = _stmt.getDouble(_columnIndexOfValue).toFloat()
          val _tmpLoggedAt: Long
          _tmpLoggedAt = _stmt.getLong(_columnIndexOfLoggedAt)
          _item = VitalLogEntity(_tmpId,_tmpDate,_tmpType,_tmpValue,_tmpLoggedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentLogs(type: String, startDate: Long): List<VitalLogEntity> {
    val _sql: String = "SELECT * FROM vital_logs WHERE type = ? AND date >= ? ORDER BY date ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfLoggedAt: Int = getColumnIndexOrThrow(_stmt, "loggedAt")
        val _result: MutableList<VitalLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VitalLogEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpValue: Float
          _tmpValue = _stmt.getDouble(_columnIndexOfValue).toFloat()
          val _tmpLoggedAt: Long
          _tmpLoggedAt = _stmt.getLong(_columnIndexOfLoggedAt)
          _item = VitalLogEntity(_tmpId,_tmpDate,_tmpType,_tmpValue,_tmpLoggedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteVitalLog(date: Long, type: String) {
    val _sql: String = "DELETE FROM vital_logs WHERE date = ? AND type = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, date)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
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
