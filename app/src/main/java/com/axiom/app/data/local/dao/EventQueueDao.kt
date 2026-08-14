package com.axiom.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.axiom.app.data.local.entity.EventQueueEntity

/**
 * WP-204 event-queue DAO — storage. WP-206 adds consent-aware drain/purge read-write ops
 * (delete-after-success, DECLINED purge). No schema change: these are pure queries over the
 * existing v18 `event_queue` table (no new columns, no status/retry fields).
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

    // ---- WP-206 consent-aware drain/purge (no schema change) ----

    /** PENDING analytics rows of the given types, oldest first (drain order). */
    @Query("SELECT * FROM event_queue WHERE status = 'PENDING' AND eventType IN (:types) ORDER BY createdAt ASC")
    suspend fun getPendingByTypes(types: List<String>): List<EventQueueEntity>

    /** Delete one row after a confirmed upload (§21 delete-after-success). */
    @Query("DELETE FROM event_queue WHERE eventId = :id")
    suspend fun deleteById(id: String)

    /** DECLINED purge — remove every queued analytics row of the given types (§13/§26). Returns rows deleted. */
    @Query("DELETE FROM event_queue WHERE eventType IN (:types)")
    suspend fun purgeByTypes(types: List<String>): Int

    /** PENDING analytics-row count of the given types (evidence/verification). */
    @Query("SELECT COUNT(*) FROM event_queue WHERE status = 'PENDING' AND eventType IN (:types)")
    suspend fun countPendingByTypes(types: List<String>): Int
}
