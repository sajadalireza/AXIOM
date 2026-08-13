package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WP-204 First-Win session state — durable lifecycle record for a First-Win
 * execution session. Persistence primitive only: this is NOT the canonical
 * eligibility/completion authority (WP-203 DataStore facts + Hunter existence
 * remain authoritative). Does not duplicate setup/first-mission/blueprint flags.
 */
@Entity(tableName = "first_win_session")
data class FirstWinSessionEntity(
    @PrimaryKey val sessionId: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)
