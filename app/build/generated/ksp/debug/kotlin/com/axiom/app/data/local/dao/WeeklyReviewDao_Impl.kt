package com.axiom.app.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.axiom.app.`data`.local.entity.WeeklyReviewEntity
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
public class WeeklyReviewDao_Impl(
  __db: RoomDatabase,
) : WeeklyReviewDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWeeklyReviewEntity: EntityInsertAdapter<WeeklyReviewEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWeeklyReviewEntity = object : EntityInsertAdapter<WeeklyReviewEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `weekly_reviews` (`id`,`timestamp`,`step1Summary`,`step2WrongAssumption`,`step3CriticFeedback`,`step4DecisionType`,`step5JournalText`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WeeklyReviewEntity) {
        statement.bindText(1, entity.id)
        statement.bindLong(2, entity.timestamp)
        statement.bindText(3, entity.step1Summary)
        statement.bindText(4, entity.step2WrongAssumption)
        statement.bindText(5, entity.step3CriticFeedback)
        statement.bindText(6, entity.step4DecisionType)
        statement.bindText(7, entity.step5JournalText)
      }
    }
  }

  public override suspend fun insertReview(review: WeeklyReviewEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWeeklyReviewEntity.insert(_connection, review)
  }

  public override fun getAllReviews(): Flow<List<WeeklyReviewEntity>> {
    val _sql: String = "SELECT * FROM weekly_reviews ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("weekly_reviews")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfStep1Summary: Int = getColumnIndexOrThrow(_stmt, "step1Summary")
        val _columnIndexOfStep2WrongAssumption: Int = getColumnIndexOrThrow(_stmt,
            "step2WrongAssumption")
        val _columnIndexOfStep3CriticFeedback: Int = getColumnIndexOrThrow(_stmt,
            "step3CriticFeedback")
        val _columnIndexOfStep4DecisionType: Int = getColumnIndexOrThrow(_stmt, "step4DecisionType")
        val _columnIndexOfStep5JournalText: Int = getColumnIndexOrThrow(_stmt, "step5JournalText")
        val _result: MutableList<WeeklyReviewEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WeeklyReviewEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpStep1Summary: String
          _tmpStep1Summary = _stmt.getText(_columnIndexOfStep1Summary)
          val _tmpStep2WrongAssumption: String
          _tmpStep2WrongAssumption = _stmt.getText(_columnIndexOfStep2WrongAssumption)
          val _tmpStep3CriticFeedback: String
          _tmpStep3CriticFeedback = _stmt.getText(_columnIndexOfStep3CriticFeedback)
          val _tmpStep4DecisionType: String
          _tmpStep4DecisionType = _stmt.getText(_columnIndexOfStep4DecisionType)
          val _tmpStep5JournalText: String
          _tmpStep5JournalText = _stmt.getText(_columnIndexOfStep5JournalText)
          _item =
              WeeklyReviewEntity(_tmpId,_tmpTimestamp,_tmpStep1Summary,_tmpStep2WrongAssumption,_tmpStep3CriticFeedback,_tmpStep4DecisionType,_tmpStep5JournalText)
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
