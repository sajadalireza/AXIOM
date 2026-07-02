package com.axiom.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.MuscleGroupEntity
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
public class MuscleGroupDao_Impl(
  __db: RoomDatabase,
) : MuscleGroupDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMuscleGroupEntity: EntityInsertAdapter<MuscleGroupEntity>

  private val __updateAdapterOfMuscleGroupEntity: EntityDeleteOrUpdateAdapter<MuscleGroupEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMuscleGroupEntity = object : EntityInsertAdapter<MuscleGroupEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `muscle_groups` (`id`,`displayName`,`strengthScore`,`lastTrainedTimestamp`,`freshnessPercent`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MuscleGroupEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.displayName)
        statement.bindLong(3, entity.strengthScore.toLong())
        val _tmpLastTrainedTimestamp: Long? = entity.lastTrainedTimestamp
        if (_tmpLastTrainedTimestamp == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpLastTrainedTimestamp)
        }
        statement.bindLong(5, entity.freshnessPercent.toLong())
      }
    }
    this.__updateAdapterOfMuscleGroupEntity = object :
        EntityDeleteOrUpdateAdapter<MuscleGroupEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `muscle_groups` SET `id` = ?,`displayName` = ?,`strengthScore` = ?,`lastTrainedTimestamp` = ?,`freshnessPercent` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MuscleGroupEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.displayName)
        statement.bindLong(3, entity.strengthScore.toLong())
        val _tmpLastTrainedTimestamp: Long? = entity.lastTrainedTimestamp
        if (_tmpLastTrainedTimestamp == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpLastTrainedTimestamp)
        }
        statement.bindLong(5, entity.freshnessPercent.toLong())
        statement.bindText(6, entity.id)
      }
    }
  }

  public override suspend fun insertMuscleGroup(muscle: MuscleGroupEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMuscleGroupEntity.insert(_connection, muscle)
  }

  public override suspend fun insertMuscleGroups(muscles: List<MuscleGroupEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfMuscleGroupEntity.insert(_connection, muscles)
  }

  public override suspend fun updateMuscleGroup(muscle: MuscleGroupEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfMuscleGroupEntity.handle(_connection, muscle)
  }

  public override fun getAllMuscleGroupsFlow(): Flow<List<MuscleGroupEntity>> {
    val _sql: String = "SELECT * FROM muscle_groups"
    return createFlow(__db, false, arrayOf("muscle_groups")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfStrengthScore: Int = getColumnIndexOrThrow(_stmt, "strengthScore")
        val _columnIndexOfLastTrainedTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "lastTrainedTimestamp")
        val _columnIndexOfFreshnessPercent: Int = getColumnIndexOrThrow(_stmt, "freshnessPercent")
        val _result: MutableList<MuscleGroupEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MuscleGroupEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpStrengthScore: Int
          _tmpStrengthScore = _stmt.getLong(_columnIndexOfStrengthScore).toInt()
          val _tmpLastTrainedTimestamp: Long?
          if (_stmt.isNull(_columnIndexOfLastTrainedTimestamp)) {
            _tmpLastTrainedTimestamp = null
          } else {
            _tmpLastTrainedTimestamp = _stmt.getLong(_columnIndexOfLastTrainedTimestamp)
          }
          val _tmpFreshnessPercent: Int
          _tmpFreshnessPercent = _stmt.getLong(_columnIndexOfFreshnessPercent).toInt()
          _item =
              MuscleGroupEntity(_tmpId,_tmpDisplayName,_tmpStrengthScore,_tmpLastTrainedTimestamp,_tmpFreshnessPercent)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMuscleGroupById(id: String): MuscleGroupEntity? {
    val _sql: String = "SELECT * FROM muscle_groups WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDisplayName: Int = getColumnIndexOrThrow(_stmt, "displayName")
        val _columnIndexOfStrengthScore: Int = getColumnIndexOrThrow(_stmt, "strengthScore")
        val _columnIndexOfLastTrainedTimestamp: Int = getColumnIndexOrThrow(_stmt,
            "lastTrainedTimestamp")
        val _columnIndexOfFreshnessPercent: Int = getColumnIndexOrThrow(_stmt, "freshnessPercent")
        val _result: MuscleGroupEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpDisplayName: String
          _tmpDisplayName = _stmt.getText(_columnIndexOfDisplayName)
          val _tmpStrengthScore: Int
          _tmpStrengthScore = _stmt.getLong(_columnIndexOfStrengthScore).toInt()
          val _tmpLastTrainedTimestamp: Long?
          if (_stmt.isNull(_columnIndexOfLastTrainedTimestamp)) {
            _tmpLastTrainedTimestamp = null
          } else {
            _tmpLastTrainedTimestamp = _stmt.getLong(_columnIndexOfLastTrainedTimestamp)
          }
          val _tmpFreshnessPercent: Int
          _tmpFreshnessPercent = _stmt.getLong(_columnIndexOfFreshnessPercent).toInt()
          _result =
              MuscleGroupEntity(_tmpId,_tmpDisplayName,_tmpStrengthScore,_tmpLastTrainedTimestamp,_tmpFreshnessPercent)
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
