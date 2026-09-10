package com.axiom.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.axiom.app.data.local.entity.CompletionReceiptEntity

/**
 * WP-204 completion-receipt DAO. IGNORE-on-conflict: a duplicate [idempotencyKey]
 * insert is rejected by the UNIQUE index and leaves the existing row untouched
 * (row count stays 1). No orchestration — persistence surface only.
 */
@Dao
interface CompletionReceiptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(receipt: CompletionReceiptEntity): Long

    @Query("SELECT * FROM completion_receipt WHERE idempotencyKey = :key LIMIT 1")
    suspend fun getByKey(key: String): CompletionReceiptEntity?

    @Query("SELECT * FROM completion_receipt WHERE sessionId = :sessionId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getBySessionId(sessionId: String): CompletionReceiptEntity?

    @Query("SELECT * FROM completion_receipt ORDER BY createdAt DESC")
    suspend fun getAll(): List<CompletionReceiptEntity>
}
