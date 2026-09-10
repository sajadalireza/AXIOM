package com.axiom.app.domain.firstwin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FirstWinCompletionLinkTest {
    @Test fun firstWin_withSession_linksReceipt() =
        assertEquals(
            "fw:hunter-1:session",
            FirstWinCompletionLink.resolveSessionId(true, "fw:hunter-1:session"),
        )

    @Test fun legacyFirstWin_withoutSession_remainsCompatible() =
        assertNull(FirstWinCompletionLink.resolveSessionId(true, null))

    @Test fun genericMission_neverAcceptsFirstWinSessionCandidate() =
        assertNull(FirstWinCompletionLink.resolveSessionId(false, "fw:hunter-1:session"))

    @Test(expected = IllegalArgumentException::class)
    fun blankCandidate_isRejected() {
        FirstWinCompletionLink.resolveSessionId(true, "   ")
    }
}
