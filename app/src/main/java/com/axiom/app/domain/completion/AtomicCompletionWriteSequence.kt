package com.axiom.app.domain.completion

/**
 * WP-205 — ordered, all-or-nothing write set for an atomic mission completion.
 *
 * Single source of truth: production calls [run] INSIDE `database.withTransaction { }`
 * with real Room DAO calls; the JVM sqlite proof calls it inside a JDBC transaction with
 * JDBC-backed lambdas. Same order, same gating — so the test exercises the shipped contract.
 *
 * Atomicity (rollback) is supplied by the ENCLOSING transaction, not here: if any lambda
 * throws, the enclosing withTransaction / JDBC txn rolls back every prior write. This object
 * only fixes the ORDER, the idempotency short-circuit, and the first-win gating.
 *
 * First-win gating: the rich completion_receipt + the pending event are written ONLY for the
 * first-win completion. Non-first-win completions still run atomically but write no receipt
 * (their own receipts are a later G3 concern). The rich receipt distinguishes a
 * WP205_RICH_COMPLETION_RECEIPT from a LEGACY_METADATA_RECEIPT by populating all rich fields.
 */
object AtomicCompletionWriteSequence {

    /**
     * @param isFirstWin true iff this is the hunter's first-win completion (receipt+event written)
     * @param receiptAlreadyExists idempotency probe against the UNIQUE idempotencyKey (first-win only)
     * @return [AtomicCompletionOutcome.ALREADY_RECORDED] if a first-win receipt already exists
     *         (no re-award), else [AtomicCompletionOutcome.AWARDED] after all writes.
     */
    suspend fun run(
        isFirstWin: Boolean,
        receiptAlreadyExists: suspend () -> Boolean,
        writeMissionCompleted: suspend () -> Unit,
        writeSkillAwards: suspend () -> Unit,
        writeHunterAward: suspend () -> Unit,
        upsertStreak: suspend () -> Unit,
        writeShadowIfUnlocked: suspend () -> Unit,
        writeDungeonIfPresent: suspend () -> Unit,
        insertRichReceipt: suspend () -> Unit,
        insertPendingEvent: suspend () -> Unit,
    ): AtomicCompletionOutcome {
        // IDEMPOTENCY GATE (first-win only): if a rich receipt already exists for this
        // logical completion, re-award NOTHING. Runs before any mutation so a duplicate
        // command performs zero writes (no mission transition, no XP, no streak, no second
        // receipt/event). Short-circuit relies on Kotlin && so a non-first-win completion
        // never even probes the receipt table.
        if (isFirstWin && receiptAlreadyExists()) return AtomicCompletionOutcome.ALREADY_RECORDED

        // ECONOMY WRITES — canonical order. Every step here is a Room write inside the
        // enclosing transaction; a throw in any of them rolls back all prior writes.
        writeMissionCompleted()
        writeSkillAwards()
        writeHunterAward()
        upsertStreak()
        writeShadowIfUnlocked()
        writeDungeonIfPresent()

        // FIRST-WIN-ONLY durable proof + causal event, written LAST so the award is
        // fully materialized before the receipt/event that attest to it. Non-first-win
        // completions run the same atomic economy but write no first-win receipt/event.
        if (isFirstWin) {
            insertRichReceipt()
            insertPendingEvent()
        }

        return AtomicCompletionOutcome.AWARDED
    }
}

/** Result of an atomic completion attempt. */
enum class AtomicCompletionOutcome {
    /** All writes performed (or would be, inside the enclosing txn). */
    AWARDED,

    /** A first-win receipt already existed for this idempotencyKey — nothing re-awarded. */
    ALREADY_RECORDED,
}
