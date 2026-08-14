package com.axiom.app.domain.analytics

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WP-206 RED — offline-drain reliability + consent gate (§21/§24, Hard Caps §39/§13).
 * Fails until GREEN because [AnalyticsDispatchEngine.drain] throws NotImplementedError.
 */
class AnalyticsDispatchEngineTest {

    private fun ev(id: String) = QueuedAnalyticsEvent(id, "idem-$id", "mission_completed", mapOf("rarity" to "RARE"))

    private class FakeConsent(var state: AnalyticsConsentState) : AnalyticsConsentSource {
        var reads = 0
        override suspend fun current(): AnalyticsConsentState { reads++; return state }
    }

    private class FakeStore(rows: List<QueuedAnalyticsEvent>) : AnalyticsEventStore {
        val remaining = rows.toMutableList()
        var purgeCalls = 0
        override suspend fun pendingAnalytics() = remaining.toList()
        override suspend fun delete(eventId: String) { remaining.removeAll { it.eventId == eventId } }
        override suspend fun purgeAnalytics(): Int { val n = remaining.size; remaining.clear(); purgeCalls++; return n }
    }

    private class FakeUploader(val succeed: Boolean = true) : AnalyticsUploader {
        val uploaded = mutableListOf<String>()
        override suspend fun upload(event: QueuedAnalyticsEvent): Boolean { uploaded.add(event.eventId); return succeed }
    }

    // §39 HARD CAP — UNKNOWN uploads nothing, deletes nothing.
    @Test fun unknown_zeroUpload_zeroDelete() = runBlocking {
        val store = FakeStore(listOf(ev("a"), ev("b")))
        val up = FakeUploader()
        val outcome = AnalyticsDispatchEngine.drain(FakeConsent(AnalyticsConsentState.UNKNOWN), store, up)
        assertEquals(DrainOutcome.NO_CONSENT, outcome)
        assertEquals(0, up.uploaded.size)
        assertEquals(2, store.remaining.size)
        assertEquals(0, store.purgeCalls)
    }

    // §13/§26 — DECLINED purges queued analytics and uploads nothing.
    @Test fun declined_purges_zeroUpload() = runBlocking {
        val store = FakeStore(listOf(ev("a"), ev("b")))
        val up = FakeUploader()
        val outcome = AnalyticsDispatchEngine.drain(FakeConsent(AnalyticsConsentState.DECLINED), store, up)
        assertEquals(DrainOutcome.PURGED_DECLINED, outcome)
        assertEquals(0, up.uploaded.size)
        assertEquals(1, store.purgeCalls)
        assertTrue(store.remaining.isEmpty())
    }

    // §14/§21 — GRANTED uploads each row then deletes it (delete AFTER success).
    @Test fun granted_uploadsAndDeletesEach() = runBlocking {
        val store = FakeStore(listOf(ev("a"), ev("b"), ev("c")))
        val up = FakeUploader(succeed = true)
        val outcome = AnalyticsDispatchEngine.drain(FakeConsent(AnalyticsConsentState.GRANTED), store, up)
        assertEquals(DrainOutcome.DRAINED, outcome)
        assertEquals(listOf("a", "b", "c"), up.uploaded)
        assertTrue(store.remaining.isEmpty())
    }

    // §37 — GRANTED but offline: failed upload keeps the row PENDING (no delete), retry later.
    @Test fun granted_offline_keepsRow_noDelete() = runBlocking {
        val store = FakeStore(listOf(ev("a"), ev("b")))
        val up = FakeUploader(succeed = false)
        val outcome = AnalyticsDispatchEngine.drain(FakeConsent(AnalyticsConsentState.GRANTED), store, up)
        assertEquals(DrainOutcome.RETRY_LATER, outcome)
        assertEquals(1, up.uploaded.size)          // stops at first failure
        assertEquals(2, store.remaining.size)       // nothing deleted
    }

    // §16 — mid-drain revocation halts further uploads.
    @Test fun granted_thenRevoked_midDrain_stops() = runBlocking {
        val store = FakeStore(listOf(ev("a"), ev("b"), ev("c")))
        val consent = FakeConsent(AnalyticsConsentState.GRANTED)
        val up = object : AnalyticsUploader {
            val uploaded = mutableListOf<String>()
            override suspend fun upload(event: QueuedAnalyticsEvent): Boolean {
                uploaded.add(event.eventId)
                consent.state = AnalyticsConsentState.DECLINED // user revokes after first upload
                return true
            }
        }
        val outcome = AnalyticsDispatchEngine.drain(consent, store, up)
        assertEquals(1, up.uploaded.size)          // only the first row went out
        assertTrue(outcome != DrainOutcome.DRAINED)
    }
}
