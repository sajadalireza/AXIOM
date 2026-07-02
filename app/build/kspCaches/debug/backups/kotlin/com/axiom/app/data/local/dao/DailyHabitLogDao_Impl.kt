package com.axiom.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.DailyHabitLogEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
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
public class DailyHabitLogDao_Impl(
  __db: RoomDatabase,
) : DailyHabitLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDailyHabitLogEntity: EntityInsertAdapter<DailyHabitLogEntity>

  private val __updateAdapterOfDailyHabitLogEntity: EntityDeleteOrUpdateAdapter<DailyHabitLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDailyHabitLogEntity = object : EntityInsertAdapter<DailyHabitLogEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `daily_habit_logs` (`id`,`date`,`waterGlasses`,`sleepHours`,`sleepQuality`,`teethMorning`,`teethEvening`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DailyHabitLogEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.date)
        statement.bindLong(3, entity.waterGlasses.toLong())
        val _tmpSleepHours: Float? = entity.sleepHours
        if (_tmpSleepHours == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpSleepHours.toDouble())
        }
        val _tmpSleepQuality: Int? = entity.sleepQuality
        if (_tmpSleepQuality == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpSleepQuality.toLong())
        }
        val _tmp: Int = if (entity.teethMorning) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmp_1: Int = if (entity.teethEvening) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
      }
    }
    this.__updateAdapterOfDailyHabitLogEntity = object :
        EntityDeleteOrUpdateAdapter<DailyHabitLogEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `daily_habit_logs` SET `id` = ?,`date` = ?,`waterGlasses` = ?,`sleepHours` = ?,`sleepQuality` = ?,`teethMorning` = ?,`teethEvening` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DailyHabitLogEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.date)
        statement.bindLong(3, entity.waterGlasses.toLong())
        val _tmpSleepHours: Float? = entity.sleepHours
        if (_tmpSleepHours == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpSleepHours.toDouble())
        }
        val _tmpSleepQuality: Int? = entity.sleepQuality
        if (_tmpSleepQuality == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpSleepQuality.toLong())
        }
        val _tmp: Int = if (entity.teethMorning) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmp_1: Int = if (entity.teethEvening) 1 else 0
        statement.bindLong(7, _tmp_1.toLong())
        statement.bindText(8, entity.id)
      }
    }
  }

  public override suspend fun insertLog(log: DailyHabitLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfDailyHabitLogEntity.insert(_connection, log)
  }

  public override suspend fun updateLog(log: DailyHabitLogEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfDailyHabitLogEntity.handle(_connection, log)
  }

  public override fun getLogByDate(date: String): Flow<DailyHabitLogEntity?> {
    val _sql: String = "SELECT * FROM daily_habit_logs WHERE date = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("daily_habit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, date)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfWaterGlasses: Int = getColumnIndexOrThrow(_stmt, "waterGlasses")
        val _columnIndexOfSleepHours: Int = getColumnIndexOrThrow(_stmt, "sleepHours")
        val _columnIndexOfSleepQuality: Int = getColumnIndexOrThrow(_stmt, "sleepQuality")
        val _columnIndexOfTeethMorning: Int = getColumnIndexOrThrow(_stmt, "teethMorning")
        val _columnIndexOfTeethEvening: Int = getColumnIndexOrThrow(_stmt, "teethEvening")
        val _result: DailyHabitLogEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpWaterGlasses: Int
          _tmpWaterGlasses = _stmt.getLong(_columnIndexOfWaterGlasses).toInt()
          val _tmpSleepHours: Float?
          if (_stmt.isNull(_columnIndexOfSleepHours)) {
            _tmpSleepHours = null
          } else {
            _tmpSleepHours = _stmt.getDouble(_columnIndexOfSleepHours).toFloat()
          }
          val _tmpSleepQuality: Int?
          if (_stmt.isNull(_columnIndexOfSleepQuality)) {
            _tmpSleepQuality = null
          } else {
            _tmpSleepQuality = _stmt.getLong(_columnIndexOfSleepQuality).toInt()
          }
          val _tmpTeethMorning: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTeethMorning).toInt()
          _tmpTeethMorning = _tmp != 0
          val _tmpTeethEvening: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfTeethEvening).toInt()
          _tmpTeethEvening = _tmp_1 != 0
          _result =
              DailyHabitLogEntity(_tmpId,_tmpDate,_tmpWaterGlasses,_tmpSleepHours,_tmpSleepQuality,_tmpTeethMorning,_tmpTeethEvening)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLogByDateDirect(date: String): DailyHabitLogEntity? {
    val _sql: String = "SELECT * FROM daily_habit_logs WHERE date = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, date)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfWaterGlasses: Int = getColumnIndexOrThrow(_stmt, "waterGlasses")
        val _columnIndexOfSleepHours: Int = getColumnIndexOrThrow(_stmt, "sleepHours")
        val _columnIndexOfSleepQuality: Int = getColumnIndexOrThrow(_stmt, "sleepQuality")
        val _columnIndexOfTeethMorning: Int = getColumnIndexOrThrow(_stmt, "teethMorning")
        val _columnIndexOfTeethEvening: Int = getColumnIndexOrThrow(_stmt, "teethEvening")
        val _result: DailyHabitLogEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpWaterGlasses: Int
          _tmpWaterGlasses = _stmt.getLong(_columnIndexOfWaterGlasses).toInt()
          val _tmpSleepHours: Float?
          if (_stmt.isNull(_columnIndexOfSleepHours)) {
            _tmpSleepHours = null
          } else {
            _tmpSleepHours = _stmt.getDouble(_columnIndexOfSleepHours).toFloat()
          }
          val _tmpSleepQuality: Int?
          if (_stmt.isNull(_columnIndexOfSleepQuality)) {
            _tmpSleepQuality = null
          } else {
            _tmpSleepQuality = _stmt.getLong(_columnIndexOfSleepQuality).toInt()
          }
          val _tmpTeethMorning: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTeethMorning).toInt()
          _tmpTeethMorning = _tmp != 0
          val _tmpTeethEvening: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfTeethEvening).toInt()
          _tmpTeethEvening = _tmp_1 != 0
          _result =
              DailyHabitLogEntity(_tmpId,_tmpDate,_tmpWaterGlasses,_tmpSleepHours,_tmpSleepQuality,_tmpTeethMorning,_tmpTeethEvening)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getLogsForLast7Days(startDate: String): Flow<List<DailyHabitLogEntity>> {
    val _sql: String = "SELECT * FROM daily_habit_logs WHERE date >= ? ORDER BY date ASC"
    return createFlow(__db, false, arrayOf("daily_habit_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, startDate)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfWaterGlasses: Int = getColumnIndexOrThrow(_stmt, "waterGlasses")
        val _columnIndexOfSleepHours: Int = getColumnIndexOrThrow(_stmt, "sleepHours")
        val _columnIndexOfSleepQuality: Int = getColumnIndexOrThrow(_stmt, "sleepQuality")
        val _columnIndexOfTeethMorning: Int = getColumnIndexOrThrow(_stmt, "teethMorning")
        val _columnIndexOfTeethEvening: Int = getColumnIndexOrThrow(_stmt, "teethEvening")
        val _result: MutableList<DailyHabitLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyHabitLogEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpWaterGlasses: Int
          _tmpWaterGlasses = _stmt.getLong(_columnIndexOfWaterGlasses).toInt()
          val _tmpSleepHours: Float?
          if (_stmt.isNull(_columnIndexOfSleepHours)) {
            _tmpSleepHours = null
          } else {
            _tmpSleepHours = _stmt.getDouble(_columnIndexOfSleepHours).toFloat()
          }
          val _tmpSleepQuality: Int?
          if (_stmt.isNull(_columnIndexOfSleepQuality)) {
            _tmpSleepQuality = null
          } else {
            _tmpSleepQuality = _stmt.getLong(_columnIndexOfSleepQuality).toInt()
          }
          val _tmpTeethMorning: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfTeethMorning).toInt()
          _tmpTeethMorning = _tmp != 0
          val _tmpTeethEvening: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfTeethEvening).toInt()
          _tmpTeethEvening = _tmp_1 != 0
          _item =
              DailyHabitLogEntity(_tmpId,_tmpDate,_tmpWaterGlasses,_tmpSleepHours,_tmpSleepQuality,_tmpTeethMorning,_tmpTeethEvening)
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
