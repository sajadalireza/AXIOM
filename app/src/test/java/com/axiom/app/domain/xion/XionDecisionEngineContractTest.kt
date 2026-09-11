package com.axiom.app.domain.xion

import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Skill
import org.junit.Assert.*
import org.junit.Test

class XionDecisionEngineContractTest {

    @Test
    fun findTemplateMatches_matchesSolopreneurKeywordsCorrectly() {
        val bugMatches = XionDecisionEngine.findTemplateMatches("Run smoke test and deploy to production")
        assertTrue("Expected smoke test deployment template match", bugMatches.any { it.id == "solopreneur_smoke_test_deployment" })

        val interviewMatches = XionDecisionEngine.findTemplateMatches("Conduct 5 user discovery interviews")
        assertTrue("Expected user interview template match", interviewMatches.any { it.id == "solopreneur_customer_interview" })

        val outreachMatches = XionDecisionEngine.findTemplateMatches("Targeted outreach to ICP prospects")
        assertTrue("Expected cold outreach template match", outreachMatches.any { it.id == "solopreneur_cold_outreach_sequence" })

        val funnelMatches = XionDecisionEngine.findTemplateMatches("Analyze conversion funnel dropoff and churn")
        assertTrue("Expected funnel audit template match", funnelMatches.any { it.id == "solopreneur_funnel_dropoff_audit" })
    }

    @Test
    fun templateToSuggestion_setsTemplateMatchedAndPhysicalDoneCondition() {
        val template = com.axiom.app.domain.template.MissionTemplatePack.TEMPLATES.first()
        val suggestion = XionDecisionEngine.templateToSuggestion(template, isPersian = false)

        assertTrue(suggestion.isTemplateMatched)
        assertEquals(template.id, suggestion.matchedTemplateId)
        assertTrue("Done condition must not be blank", suggestion.doneCondition.isNotBlank())
        assertEquals(template.doneConditionEn, suggestion.doneCondition)
        assertEquals(SuggestionStatus.PENDING, suggestion.status)
        assertFalse(suggestion.wasEdited)
    }

    @Test
    fun containsProhibitedContent_detectsTherapyFinancialAndTruthAuthorityTropes() {
        // Therapy
        assertTrue(XionDecisionEngine.containsProhibitedContent("This is a psychotherapy session to cure your anxiety disorder"))
        assertTrue(XionDecisionEngine.containsProhibitedContent("روان‌درمانی برای درمان افسردگی"))

        // Financial Advice
        assertTrue(XionDecisionEngine.containsProhibitedContent("Buy shares of this crypto investment with guaranteed profit"))
        assertTrue(XionDecisionEngine.containsProhibitedContent("سیگنال بورس با سود تضمینی"))

        // Truth Authority / Omniscience
        assertTrue(XionDecisionEngine.containsProhibitedContent("The all-knowing system declares the infallible oracle"))
        assertTrue(XionDecisionEngine.containsProhibitedContent("من دانای کل مطلق هستم و حقیقت را می‌دانم"))

        // Safe Software / Solopreneur execution
        assertFalse(XionDecisionEngine.containsProhibitedContent("Refactor repository module and write 5 unit tests"))
        assertFalse(XionDecisionEngine.containsProhibitedContent("بازبینی صفحه ثبت‌نام و افزودن آزمایش‌های خودکار"))
    }

    @Test
    fun sanitizeAndFilterSuggestions_replacesProhibitedItemsWithSafeTemplates() {
        val dirtySuggestions = listOf(
            XionSuggestion(
                title = "Therapy Session",
                description = "Diagnose mental health disorder and anxiety",
                skillName = "Psychology",
                estimatedHours = 1f,
                rarity = "COMMON",
                reasoning = "Cure mental distress",
                doneCondition = "Session completed"
            ),
            XionSuggestion(
                title = "Clean Unit Tests",
                description = "Add regression unit tests for authentication",
                skillName = "Engineering",
                estimatedHours = 1f,
                rarity = "COMMON",
                reasoning = "Improve test coverage",
                doneCondition = "Gradle test passes"
            )
        )

        val sanitized = XionDecisionEngine.sanitizeAndFilterSuggestions(dirtySuggestions)
        assertEquals(2, sanitized.size)
        // First item was replaced with safe beachhead template
        assertTrue(sanitized[0].isTemplateMatched)
        assertFalse(XionDecisionEngine.containsProhibitedContent(sanitized[0].description))
        // Second item was preserved
        assertEquals("Clean Unit Tests", sanitized[1].title)
    }

    @Test
    fun generateContextualInsight_producesBilingualActionableGuidance() {
        val dummyHunter = Hunter(
            id = "hunter_1",
            name = "TestHunter",
            level = 12,
            rankLabel = "C-Rank",
            totalXP = 5000L,
            currentXP = 500,
            xpToNextLevel = 1000,
            progressPercent = 0.5f,
            rankColor = 0xFFFFFFFF,
            rankGlyph = "⚔"
        )

        val enInsight = XionDecisionEngine.generateContextualInsight(dummyHunter, 7, "Build authentication API", false)
        assertNotNull(enInsight.insightEn)
        assertNotNull(enInsight.frictionLevel)

        val faInsight = XionDecisionEngine.generateContextualInsight(dummyHunter, 0, "Build complex enterprise multi-tenant system", true)
        assertEquals("HIGH", faInsight.frictionLevel)
        assertTrue(faInsight.insightFa.isNotBlank())
    }

    @Test
    fun buildHeuristicSuggestions_prioritizesTemplatesAndEnsuresDoneConditions() {
        val dummySkill = Skill(
            id = "sk_eng",
            name = "Software Architecture",
            category = "Engineering",
            currentXP = 100L,
            level = 2,
            rankLabel = "D-Rank",
            parentId = null,
            isUnlocked = true,
            xpToNextRank = 500L,
            rankProgressPercent = 0.2f,
            isShadowCandidate = false,
            rankColor = 0xFF8A8AA0
        )

        val suggestions = XionDecisionEngine.buildHeuristicSuggestions("Run smoke test and deploy to production", listOf(dummySkill), false)
        assertTrue("Expected at least 3 suggestions", suggestions.size >= 3)
        // Template-first: first item should be template matched
        assertTrue("First item should be template matched", suggestions.first().isTemplateMatched)
        // All suggestions must have non-empty physical done conditions
        for (s in suggestions) {
            assertTrue("Done condition cannot be blank: ${s.title}", s.doneCondition.isNotBlank())
        }
    }

    @Test
    fun calculateAcceptanceMetrics_evaluatesTargetAndKillCriterion() {
        // Zero decisions: rate is 0, kill criterion not triggered
        val zero = XionDecisionEngine.calculateAcceptanceMetrics(5, 0, 0, 0, 0)
        assertEquals(0f, zero.acceptanceRate, 0.001f)
        assertFalse(zero.killCriterionTriggered)

        // 4 accepted, 6 rejected -> 40% (Target >= 35% met, no kill criterion)
        val passed = XionDecisionEngine.calculateAcceptanceMetrics(10, 4, 6, 1, 0)
        assertEquals(0.40f, passed.acceptanceRate, 0.001f)
        assertFalse(passed.killCriterionTriggered)

        // 1 accepted, 9 rejected -> 10% (< 20% kill criterion triggered)
        val killed = XionDecisionEngine.calculateAcceptanceMetrics(10, 1, 9, 0, 0)
        assertEquals(0.10f, killed.acceptanceRate, 0.001f)
        assertTrue(killed.killCriterionTriggered)

        // 1 accepted, 3 rejected -> 25% (< 35% but total decisions = 4 < 10, so kill criterion NOT triggered yet)
        val earlyLow = XionDecisionEngine.calculateAcceptanceMetrics(4, 1, 3, 0, 0)
        assertEquals(0.25f, earlyLow.acceptanceRate, 0.001f)
        assertFalse(earlyLow.killCriterionTriggered)
    }
}
