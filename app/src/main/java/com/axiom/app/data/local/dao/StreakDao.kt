package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {

    @Query("SELECT * FROM streak LIMIT 1")
    fun getStreak(): Flow<StreakEntity?>

    /**
     * One-shot suspend read of the singleton streak row. Unlike [getStreak], this is dispatched
     * onto the caller's coroutine (and, inside `database.withTransaction {}`, onto the transaction
     * thread) rather than the invalidation-tracker Flow executor — the correct pattern for a
     * read-modify-write performed inside a transaction. Used by the atomic completion streak
     * reconciliation so the in-txn read cannot stall on a separate query dispatcher.
     */
    @Query("SELECT * FROM streak LIMIT 1")
    suspend fun getStreakOnce(): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)

    @Update
    suspend fun updateStreak(streak: StreakEntity)
}
