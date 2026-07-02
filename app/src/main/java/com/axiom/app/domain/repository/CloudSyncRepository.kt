package com.axiom.app.domain.repository

interface CloudSyncRepository {
    suspend fun backupProgress(): Boolean
    suspend fun restoreProgress(): Boolean
}
