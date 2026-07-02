package com.axiom.app.domain.repository

import com.axiom.app.domain.model.SystemMessage
import kotlinx.coroutines.flow.Flow

interface SystemFeedRepository {
    fun getSystemMessages(): Flow<List<SystemMessage>>
    suspend fun emitMessage(message: SystemMessage)
}
