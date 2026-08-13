package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * WP-204 completion receipt — durable proof that a logical First-Win completion
 * event was recorded exactly once. Duplicate prevention is DB-enforced via the
 * UNIQUE index on [idempotencyKey] (not a Kotlin-only check). Persistence
 * primitive only; the completion *decision* is deferred to WP-205.
 */
@Entity(
    tableName = "completion_receipt",
    indices = [Index(value = ["idempotencyKey"], unique = true)]
)
data class CompletionReceiptEntity(
    @PrimaryKey val receiptId: String,
    val idempotencyKey: String,
    val receiptType: String,
    val createdAt: Long
)
