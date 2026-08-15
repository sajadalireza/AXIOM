package com.axiom.app.domain.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WP-206 RED — payload allowlist (§8, PRIMARY) + sensitive-key blacklist (§9, defense-in-depth) +
 * static leak proof (§29/§40 Hard Cap: reflection text / mission title / free text never in an
 * analytics payload). Fails until GREEN because [AnalyticsPayloadPolicy.validate] throws.
 */
class AnalyticsPayloadPolicyTest {

    private fun reject(name: String, props: Map<String, Any?>): PayloadValidation.Rejected {
        val r = AnalyticsPayloadPolicy.validate(name, props)
        assertTrue("expected Rejected for $name/$props, got $r", r is PayloadValidation.Rejected)
        return r as PayloadValidation.Rejected
    }

    @Test fun accepts_wellFormedMissionCompleted_stringifiesValues() {
        val r = AnalyticsPayloadPolicy.validate(
            "mission_completed",
            mapOf("rarity" to "RARE", "xp_gained" to 40, "leveled_up" to true)
        )
        assertTrue(r is PayloadValidation.Accepted)
        val clean = (r as PayloadValidation.Accepted).clean
        assertEquals("RARE", clean["rarity"])
        assertEquals("40", clean["xp_gained"])
        assertEquals("true", clean["leveled_up"])
    }

    @Test fun accepts_onboardingCompleted_emptyProps() {
        assertTrue(AnalyticsPayloadPolicy.validate("onboarding_completed", emptyMap()) is PayloadValidation.Accepted)
    }

    @Test fun rejects_unknownEvent_failClosed() {
        assertEquals("unknown_event", reject("marketing_super_event", emptyMap()).reason)
    }

    @Test fun rejects_nonAllowlistedKey_evenIfInnocuous() {
        assertEquals("not_allowlisted", reject("streak_broken", mapOf("streak_length" to 3, "device_id" to "x")).reason)
    }

    // §29/§40 Hard Cap — every sensitive alias MUST be rejected regardless of event.
    @Test fun rejects_missionTitle() { assertTrue(reject("mission_completed", mapOf("title" to "Do 50 pushups")).reason.isNotEmpty()) }
    @Test fun rejects_missionTitle_alias() { assertTrue(reject("mission_completed", mapOf("missionTitle" to "x")).reason.isNotEmpty()) }
    @Test fun rejects_reflection() { assertTrue(reject("mission_completed", mapOf("reflection" to "I felt strong today")).reason.isNotEmpty()) }
    @Test fun rejects_reflectionText() { assertTrue(reject("mission_completed", mapOf("reflectionText" to "x")).reason.isNotEmpty()) }
    @Test fun rejects_journal() { assertTrue(reject("mission_completed", mapOf("journal" to "x")).reason.isNotEmpty()) }
    @Test fun rejects_note() { assertTrue(reject("mission_completed", mapOf("note" to "x")).reason.isNotEmpty()) }
    @Test fun rejects_prompt() { assertTrue(reject("ai_call", mapOf("prompt" to "system prompt leak")).reason.isNotEmpty()) }
    @Test fun rejects_freeText() { assertTrue(reject("ai_call", mapOf("freeText" to "x")).reason.isNotEmpty()) }
    @Test fun rejects_secretToken() { assertTrue(reject("ai_call", mapOf("token" to "Bearer abc")).reason.isNotEmpty()) }
    @Test fun rejects_identityName() { assertTrue(reject("mission_completed", mapOf("hunter_name" to "Ali")).reason.isNotEmpty()) }

    @Test fun blacklist_isCaseInsensitive() {
        assertTrue(reject("mission_completed", mapOf("REFLECTION" to "x")).reason.isNotEmpty())
        assertTrue(reject("mission_completed", mapOf("Title" to "x")).reason.isNotEmpty())
    }

    @Test fun firstWinCausalPayload_carriesOnlyMissionIdAndXp() {
        val r = AnalyticsPayloadPolicy.validate(
            "FIRST_WIN_COMPLETION",
            mapOf("missionId" to "m-1", "hunterXpAwarded" to 100)
        )
        assertTrue(r is PayloadValidation.Accepted)
        assertEquals(setOf("missionId", "hunterXpAwarded"), (r as PayloadValidation.Accepted).clean.keys)
    }

    @Test fun catalog_classifiesAllAnalyticsTypes() {
        assertEquals(
            setOf("mission_completed", "onboarding_completed", "ai_call", "streak_shield_used", "streak_broken", "FIRST_WIN_COMPLETION"),
            AnalyticsPayloadPolicy.ANALYTICS_EVENT_TYPES
        )
    }
}
