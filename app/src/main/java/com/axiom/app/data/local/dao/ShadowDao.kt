package com.axiom.app.data.local.dao

import androidx.room.*
import com.axiom.app.data.local.entity.ShadowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShadowDao {
    @Query("SELECT * FROM shadows ORDER BY acquiredAt DESC")
    fun getAllShadowsFlow(): Flow<List<ShadowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShadow(shadow: ShadowEntity)
}
