package com.axiom.app.domain.repository

import com.axiom.app.domain.model.Hunter
import kotlinx.coroutines.flow.Flow

interface HunterRepository {
    fun getHunterProfile(): Flow<Hunter?>
    suspend fun getDirectHunterProfile(): Hunter?
    suspend fun updateHunterProfile(profile: Hunter)
}
