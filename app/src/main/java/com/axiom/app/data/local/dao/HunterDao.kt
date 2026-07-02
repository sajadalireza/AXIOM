package com.axiom.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.axiom.app.data.local.entity.HunterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HunterDao {
    @Query("SELECT * FROM hunter_profile LIMIT 1")
    fun getProfileFlow(): Flow<HunterEntity?>

    @Query("SELECT * FROM hunter_profile LIMIT 1")
    suspend fun getProfile(): HunterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProfile(profile: HunterEntity)
}
