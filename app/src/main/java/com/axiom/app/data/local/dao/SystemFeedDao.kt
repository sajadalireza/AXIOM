package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.SystemFeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemFeedDao {

    @Query("SELECT * FROM system_feed ORDER BY timestamp DESC LIMIT :limit")
    fun getFeed(limit: Int = 50): Flow<List<SystemFeedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SystemFeedEntity)

    @Query("DELETE FROM system_feed WHERE id NOT IN (SELECT id FROM system_feed ORDER BY timestamp DESC LIMIT :keepCount)")
    suspend fun deleteOldEntries(keepCount: Int = 200)
}
