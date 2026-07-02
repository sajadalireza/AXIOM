package com.axiom.app.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.StreakEntity
import javax.`annotation`.processing.Generated
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class StreakDao_Impl(
  __db: RoomDatabase,
) : StreakDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfStreakEntity: EntityInsertAdapter<StreakEntity>

  private val __updateAdapterOfStreakEntity: EntityDeleteOrUpdateAdapter<StreakEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfStreakEntity = object : EntityInsertAdapter<StreakEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `streak` (`id`,`currentStreak`,`longestStreak`,`lastActivityDate`,`xpMultiplier`,`streakLabel`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StreakEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.currentStreak.toLong())
        statement.bindLong(3, entity.longestStreak.toLong())
        val _tmpLastActivityDate: String? = entity.lastActivityDate
        if (_tmpLastActivityDate == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLastActivityDate)
        }
        statement.bindDouble(5, entity.xpMultiplier.toDouble())
        statement.bindText(6, entity.streakLabel)
      }
    }
    this.__updateAdapterOfStreakEntity = object : EntityDeleteOrUpdateAdapter<StreakEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `streak` SET `id` = ?,`currentStreak` = ?,`longestStreak` = ?,`lastActivityDate` = ?,`xpMultiplier` = ?,`streakLabel` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: StreakEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.currentStreak.toLong())
        statement.bindLong(3, entity.longestStreak.toLong())
        val _tmpLastActivityDate: String? = entity.lastActivityDate
        if (_tmpLastActivityDate == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLastActivityDate)
        }
        statement.bindDouble(5, entity.xpMultiplier.toDouble())
        statement.bindText(6, entity.streakLabel)
        statement.bindText(7, entity.id)
      }
    }
  }

  public override suspend fun insertStreak(streak: StreakEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfStreakEntity.insert(_connection, streak)
  }

  public override suspend fun updateStreak(streak: StreakEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfStreakEntity.handle(_connection, streak)
  }

  public override fun getStreak(): Flow<StreakEntity?> {
    val _sql: String = "SELECT * FROM streak LIMIT 1"
    return createFlow(__db, false, arrayOf("streak")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCurrentStreak: Int = getColumnIndexOrThrow(_stmt, "currentStreak")
        val _columnIndexOfLongestStreak: Int = getColumnIndexOrThrow(_stmt, "longestStreak")
        val _columnIndexOfLastActivityDate: Int = getColumnIndexOrThrow(_stmt, "lastActivityDate")
        val _columnIndexOfXpMultiplier: Int = getColumnIndexOrThrow(_stmt, "xpMultiplier")
        val _columnIndexOfStreakLabel: Int = getColumnIndexOrThrow(_stmt, "streakLabel")
        val _result: StreakEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpCurrentStreak: Int
          _tmpCurrentStreak = _stmt.getLong(_columnIndexOfCurrentStreak).toInt()
          val _tmpLongestStreak: Int
          _tmpLongestStreak = _stmt.getLong(_columnIndexOfLongestStreak).toInt()
          val _tmpLastActivityDate: String?
          if (_stmt.isNull(_columnIndexOfLastActivityDate)) {
            _tmpLastActivityDate = null
          } else {
            _tmpLastActivityDate = _stmt.getText(_columnIndexOfLastActivityDate)
          }
          val _tmpXpMultiplier: Float
          _tmpXpMultiplier = _stmt.getDouble(_columnIndexOfXpMultiplier).toFloat()
          val _tmpStreakLabel: String
          _tmpStreakLabel = _stmt.getText(_columnIndexOfStreakLabel)
          _result =
              StreakEntity(_tmpId,_tmpCurrentStreak,_tmpLongestStreak,_tmpLastActivityDate,_tmpXpMultiplier,_tmpStreakLabel)
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
