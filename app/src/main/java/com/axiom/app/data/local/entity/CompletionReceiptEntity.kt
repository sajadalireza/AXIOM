package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Completion receipt — durable proof that a logical First-Win completion event was
 * recorded exactly once. Duplicate prevention is DB-enforced via the UNIQUE index on
 * [idempotencyKey] (not a Kotlin-only check).
 *
 * WP-204 shipped this as a metadata-only idempotency receipt. WP-205 (PROMPT 41B,
 * PO-authorized Room v17 -> v18) enriches THIS table — deliberately not a second
 * `first_win_completion_receipts` table — into the canonical rich completion receipt
 * from the design spec (§7): it now records the exact XP award and resulting streak
 * so the completion is replay-safe and returns the exact result after a crash or a
 * duplicate request.
 *
 * Two receipt shapes coexist in ONE table, distinguished by nullability rather than a
 * second table:
 *  - LEGACY_METADATA_RECEIPT — pre-v18 rows: rich fields are NULL. The 17->18 migration
 *    must NOT fabricate historical XP / streak values for them (DECISION 3).
 *  - WP205_RICH_COMPLETION_RECEIPT — new First-Mission completions: the production write
 *    boundary supplies the complete rich result (all rich fields non-null). Completeness
 *    is an application-layer invariant, intentionally stricter than the permissive
 *    database-level nullability required for legacy compatibility.
 *
 * This does NOT create the generalized G3 progression ledger; it records the exact award
 * for this one logical First-Win completion only.
 */
@Entity(
    tableName = "completion_receipt",
    indices = [Index(value = ["idempotencyKey"], unique = true)]
)
data class CompletionReceiptEntity(
    @PrimaryKey val receiptId: String,
    val idempotencyKey: String,
    val receiptType: String,
    val createdAt: Long,
    // WP-205 v18 rich completion-receipt fields. NULLABLE for migrated legacy rows
    // (no fabricated history). New WP-205 rich receipts populate all of them.
    val sessionId: String? = null,
    val missionId: String? = null,
    val hunterXpAwarded: Int? = null,
    val skillXpAwarded: Long? = null,
    val resultingStreak: Int? = null,
    val completedAt: Long? = null
)
