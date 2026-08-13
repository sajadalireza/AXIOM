package com.axiom.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.axiom.app.data.local.entity.EventQueueEntity

/**
 * WP-204 event-queue DAO — STORAGE ONLY. IGNORE-on-conflict dedupes queued events
 * by [idempotencyKey] at the SQLite layer. No dispatch/consume/retry/worker here;
 * consumption semantics belong to WP-205/WP-206.
 */
@Dao
interface EventQueueDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: EventQueueEntity): Long

    @Query("SELECT * FROM event_queue WHERE eventId = :id LIMIT 1")
    suspend fun getById(id: String): EventQueueEntity?

    @Query("SELECT * FROM event_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPending(): List<EventQueueEntity>

    @Query("SELECT * FROM event_queue ORDER BY createdAt DESC")
    suspend fun getAll(): List<EventQueueEntity>
}
