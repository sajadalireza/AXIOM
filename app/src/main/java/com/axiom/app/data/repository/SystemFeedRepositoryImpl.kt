package com.axiom.app.data.repository

import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.domain.repository.SystemFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemFeedRepositoryImpl @Inject constructor() : SystemFeedRepository {
    private val _messages = MutableStateFlow<List<SystemMessage>>(emptyList())

    override fun getSystemMessages(): Flow<List<SystemMessage>> = _messages.asStateFlow()

    override suspend fun emitMessage(message: SystemMessage) {
        val current = _messages.value.toMutableList()
        current.add(0, message)
        _messages.value = current.take(50)
    }
}
