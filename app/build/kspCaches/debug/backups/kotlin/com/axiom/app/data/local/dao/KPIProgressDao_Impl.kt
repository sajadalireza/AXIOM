package com.axiom.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.KPIMissStreakEntity
import com.axiom.app.`data`.local.entity.KPIProgressEntity
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
public class KPIProgressDao_Impl(
  __db: RoomDatabase,
) : KPIProgressDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfKPIProgressEntity: EntityInsertAdapter<KPIProgressEntity>

  private val __insertAdapterOfKPIMissStreakEntity: EntityInsertAdapter<KPIMissStreakEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfKPIProgressEntity = object : EntityInsertAdapter<KPIProgressEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `kpi_progress` (`id`,`kpiId`,`date`,`incrementValue`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: KPIProgressEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.kpiId)
        statement.bindLong(3, entity.date)
        statement.bindDouble(4, entity.incrementValue.toDouble())
      }
    }
    this.__insertAdapterOfKPIMissStreakEntity = object : EntityInsertAdapter<KPIMissStreakEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `kpi_miss_streaks` (`kpiId`,`missStreak`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: KPIMissStreakEntity) {
        statement.bindText(1, entity.kpiId)
        statement.bindLong(2, entity.missStreak.toLong())
      }
    }
  }

  public override suspend fun insertProgress(progress: KPIProgressEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfKPIProgressEntity.insert(_connection, progress)
  }

  public override suspend fun insertMissStreak(streak: KPIMissStreakEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfKPIMissStreakEntity.insert(_connection, streak)
  }

  public override fun getProgressForKPI(kpiId: String): Flow<List<KPIProgressEntity>> {
    val _sql: String = "SELECT * FROM kpi_progress WHERE kpiId = ?"
    return createFlow(__db, false, arrayOf("kpi_progress")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, kpiId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfKpiId: Int = getColumnIndexOrThrow(_stmt, "kpiId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfIncrementValue: Int = getColumnIndexOrThrow(_stmt, "incrementValue")
        val _result: MutableList<KPIProgressEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: KPIProgressEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpKpiId: String
          _tmpKpiId = _stmt.getText(_columnIndexOfKpiId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpIncrementValue: Float
          _tmpIncrementValue = _stmt.getDouble(_columnIndexOfIncrementValue).toFloat()
          _item = KPIProgressEntity(_tmpId,_tmpKpiId,_tmpDate,_tmpIncrementValue)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllProgress(): Flow<List<KPIProgressEntity>> {
    val _sql: String = "SELECT * FROM kpi_progress"
    return createFlow(__db, false, arrayOf("kpi_progress")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfKpiId: Int = getColumnIndexOrThrow(_stmt, "kpiId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfIncrementValue: Int = getColumnIndexOrThrow(_stmt, "incrementValue")
        val _result: MutableList<KPIProgressEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: KPIProgressEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpKpiId: String
          _tmpKpiId = _stmt.getText(_columnIndexOfKpiId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpIncrementValue: Float
          _tmpIncrementValue = _stmt.getDouble(_columnIndexOfIncrementValue).toFloat()
          _item = KPIProgressEntity(_tmpId,_tmpKpiId,_tmpDate,_tmpIncrementValue)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getMissStreakForKPI(kpiId: String): Flow<KPIMissStreakEntity?> {
    val _sql: String = "SELECT * FROM kpi_miss_streaks WHERE kpiId = ?"
    return createFlow(__db, false, arrayOf("kpi_miss_streaks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, kpiId)
        val _columnIndexOfKpiId: Int = getColumnIndexOrThrow(_stmt, "kpiId")
        val _columnIndexOfMissStreak: Int = getColumnIndexOrThrow(_stmt, "missStreak")
        val _result: KPIMissStreakEntity?
        if (_stmt.step()) {
          val _tmpKpiId: String
          _tmpKpiId = _stmt.getText(_columnIndexOfKpiId)
          val _tmpMissStreak: Int
          _tmpMissStreak = _stmt.getLong(_columnIndexOfMissStreak).toInt()
          _result = KPIMissStreakEntity(_tmpKpiId,_tmpMissStreak)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirectMissStreakForKPI(kpiId: String): KPIMissStreakEntity? {
    val _sql: String = "SELECT * FROM kpi_miss_streaks WHERE kpiId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, kpiId)
        val _columnIndexOfKpiId: Int = getColumnIndexOrThrow(_stmt, "kpiId")
        val _columnIndexOfMissStreak: Int = getColumnIndexOrThrow(_stmt, "missStreak")
        val _result: KPIMissStreakEntity?
        if (_stmt.step()) {
          val _tmpKpiId: String
          _tmpKpiId = _stmt.getText(_columnIndexOfKpiId)
          val _tmpMissStreak: Int
          _tmpMissStreak = _stmt.getLong(_columnIndexOfMissStreak).toInt()
          _result = KPIMissStreakEntity(_tmpKpiId,_tmpMissStreak)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllMissStreaks(): Flow<List<KPIMissStreakEntity>> {
    val _sql: String = "SELECT * FROM kpi_miss_streaks"
    return createFlow(__db, false, arrayOf("kpi_miss_streaks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfKpiId: Int = getColumnIndexOrThrow(_stmt, "kpiId")
        val _columnIndexOfMissStreak: Int = getColumnIndexOrThrow(_stmt, "missStreak")
        val _result: MutableList<KPIMissStreakEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: KPIMissStreakEntity
          val _tmpKpiId: String
          _tmpKpiId = _stmt.getText(_columnIndexOfKpiId)
          val _tmpMissStreak: Int
          _tmpMissStreak = _stmt.getLong(_columnIndexOfMissStreak).toInt()
          _item = KPIMissStreakEntity(_tmpKpiId,_tmpMissStreak)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteProgressForKPI(kpiId: String) {
    val _sql: String = "DELETE FROM kpi_progress WHERE kpiId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, kpiId)
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
