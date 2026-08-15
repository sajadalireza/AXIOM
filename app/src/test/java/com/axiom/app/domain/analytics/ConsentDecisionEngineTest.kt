package com.axiom.app.domain.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WP-206 RED — pure consent routing contract (§6, Decision A). Fails until GREEN because
 * [ConsentDecisionEngine] bodies throw NotImplementedError.
 */
class ConsentDecisionEngineTest {

    @Test fun unknown_holdsLocallyAndNeverUploads() {
        assertEquals(QueueDecision.HOLD, ConsentDecisionEngine.decide(AnalyticsConsentState.UNKNOWN))
        assertTrue(ConsentDecisionEngine.shouldEnqueue(AnalyticsConsentState.UNKNOWN))
        assertFalse(ConsentDecisionEngine.shouldUpload(AnalyticsConsentState.UNKNOWN))
        assertFalse(ConsentDecisionEngine.shouldPurge(AnalyticsConsentState.UNKNOWN))
    }

    @Test fun granted_isSendEligible() {
        assertEquals(QueueDecision.SEND_ELIGIBLE, ConsentDecisionEngine.decide(AnalyticsConsentState.GRANTED))
        assertTrue(ConsentDecisionEngine.shouldEnqueue(AnalyticsConsentState.GRANTED))
        assertTrue(ConsentDecisionEngine.shouldUpload(AnalyticsConsentState.GRANTED))
        assertFalse(ConsentDecisionEngine.shouldPurge(AnalyticsConsentState.GRANTED))
    }

    @Test fun declined_deletesAndDoesNotCollectOrUpload() {
        assertEquals(QueueDecision.DELETE, ConsentDecisionEngine.decide(AnalyticsConsentState.DECLINED))
        assertFalse(ConsentDecisionEngine.shouldEnqueue(AnalyticsConsentState.DECLINED))
        assertFalse(ConsentDecisionEngine.shouldUpload(AnalyticsConsentState.DECLINED))
        assertTrue(ConsentDecisionEngine.shouldPurge(AnalyticsConsentState.DECLINED))
    }
}
