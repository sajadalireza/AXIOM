package com.axiom.app.domain.repository

import com.axiom.app.domain.model.Shadow
import kotlinx.coroutines.flow.Flow

interface ShadowRepository {
    fun getAllShadows(): Flow<List<Shadow>>
    suspend fun insertShadow(shadow: Shadow)
}
