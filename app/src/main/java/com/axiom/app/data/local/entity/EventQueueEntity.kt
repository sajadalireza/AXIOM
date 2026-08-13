package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * WP-204 event queue — durable pending-event storage suitable for later atomic /
 * outbox orchestration (WP-205/WP-206). STORAGE CONTRACT ONLY: WP-204 does not
 * dispatch, consume, retry, or deliver events. Duplicate queued events are
 * DB-prevented via the UNIQUE index on [idempotencyKey].
 */
@Entity(
    tableName = "event_queue",
    indices = [Index(value = ["idempotencyKey"], unique = true)]
)
data class EventQueueEntity(
    @PrimaryKey val eventId: String,
    val idempotencyKey: String,
    val eventType: String,
    val payload: String = "",
    val status: String = "PENDING",
    val createdAt: Long
)
