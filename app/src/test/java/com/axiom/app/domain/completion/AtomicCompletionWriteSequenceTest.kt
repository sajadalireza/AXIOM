package com.axiom.app.domain.completion

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WP-205 RED — atomic completion ORDER + gating + idempotency contract, pure JVM.
 *
 * Uses recording lambdas (no DB) to pin the exact write order and the first-win / idempotency
 * gating that [AtomicCompletionWriteSequence.run] must implement. Fails until GREEN because
 * the production body currently throws NotImplementedError.
 */
class AtomicCompletionWriteSequenceTest {

    private val order = mutableListOf<String>()

    private suspend fun run(
        isFirstWin: Boolean,
        receiptExists: Boolean,
    ): AtomicCompletionOutcome = AtomicCompletionWriteSequence.run(
        isFirstWin = isFirstWin,
        receiptAlreadyExists = { order.add("probe"); receiptExists },
        writeMissionCompleted = { order.add("mission") },
        writeSkillAwards = { order.add("skill") },
        writeHunterAward = { order.add("hunter") },
        upsertStreak = { order.add("streak") },
        writeShadowIfUnlocked = { order.add("shadow") },
        writeDungeonIfPresent = { order.add("dungeon") },
        insertRichReceipt = { order.add("receipt") },
        insertPendingEvent = { order.add("event") },
    )

    @Test
    fun firstWin_writesFullSetInCanonicalOrder() = runBlocking {
        val outcome = run(isFirstWin = true, receiptExists = false)
        assertEquals(AtomicCompletionOutcome.AWARDED, outcome)
        // probe first (idempotency), then economy writes, then receipt + event last.
        assertEquals(
            listOf("probe", "mission", "skill", "hunter", "streak", "shadow", "dungeon", "receipt", "event"),
            order,
        )
    }

    @Test
    fun firstWin_whenReceiptExists_shortCircuitsWithNoAward() = runBlocking {
        val outcome = run(isFirstWin = true, receiptExists = true)
        assertEquals(AtomicCompletionOutcome.ALREADY_RECORDED, outcome)
        // idempotency: probe ran, but NOTHING was written (no double award).
        assertTrue("probe must run", order.contains("probe"))
        assertFalse("no mission write on replay", order.contains("mission"))
        assertFalse("no hunter award on replay", order.contains("hunter"))
        assertFalse("no receipt re-insert on replay", order.contains("receipt"))
        assertFalse("no event re-enqueue on replay", order.contains("event"))
    }

    @Test
    fun nonFirstWin_runsAtomicEconomyButWritesNoReceiptOrEvent() = runBlocking {
        val outcome = run(isFirstWin = false, receiptExists = false)
        assertEquals(AtomicCompletionOutcome.AWARDED, outcome)
        assertTrue("economy still written", order.containsAll(listOf("mission", "skill", "hunter", "streak")))
        assertFalse("no first-win receipt for ordinary completion", order.contains("receipt"))
        assertFalse("no first-win event for ordinary completion", order.contains("event"))
    }

    @Test
    fun receiptAndEventAreLastTwoWrites_soAwardIsProvenBeforeReceipt() = runBlocking {
        run(isFirstWin = true, receiptExists = false)
        val n = order.size
        assertEquals("event", order[n - 1])
        assertEquals("receipt", order[n - 2])
    }
}
