package com.axiom.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.axiom.app.data.local.entity.FirstWinSessionEntity

/**
 * WP-204 First-Win session DAO. Persistence surface only; not the eligibility
 * authority (WP-203 routing unchanged).
 */
@Dao
interface FirstWinSessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: FirstWinSessionEntity): Long

    @Query("SELECT * FROM first_win_session WHERE sessionId = :id LIMIT 1")
    suspend fun getById(id: String): FirstWinSessionEntity?

    @Query("SELECT * FROM first_win_session ORDER BY createdAt DESC")
    suspend fun getAll(): List<FirstWinSessionEntity>
}
