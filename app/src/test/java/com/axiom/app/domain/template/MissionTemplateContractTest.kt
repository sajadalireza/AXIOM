package com.axiom.app.domain.template

import org.junit.Assert.*
import org.junit.Test

/**
 * G5-P3 Mission Template Pack Contract Test.
 *
 * Verifies:
 * 1. Single Beachhead focus: all templates belong to SOFTWARE_SOLOPRENEUR.
 * 2. Uniqueness of template IDs.
 * 3. Bilingual content completeness (English & Persian) across titles, Done Conditions, triggers, and tips.
 * 4. Realistic duration boundaries (15 to 120 minutes).
 * 5. Category coverage across all 4 defined categories.
 * 6. Template adoption rate and usage stats calculation.
 */
class MissionTemplateContractTest {

    @Test
    fun allTemplates_belongToSoftwareSolopreneurBeachhead() {
        assertTrue("Template pack must contain at least 6 templates", MissionTemplatePack.TEMPLATES.size >= 6)
        for (template in MissionTemplatePack.TEMPLATES) {
            assertEquals(
                "All templates must belong to SOFTWARE_SOLOPRENEUR in G5-P3",
                Beachhead.SOFTWARE_SOLOPRENEUR,
                template.beachhead
            )
        }
    }

    @Test
    fun allTemplateIds_areUniqueAndNonBlank() {
        val ids = MissionTemplatePack.TEMPLATES.map { it.id }
        assertEquals("All template IDs must be unique", ids.distinct().size, ids.size)
        for (id in ids) {
            assertTrue("Template ID must not be blank", id.isNotBlank())
            assertTrue("Template ID must start with solopreneur_", id.startsWith("solopreneur_"))
        }
    }

    @Test
    fun allTemplates_haveCompleteBilingualMetadata() {
        for (t in MissionTemplatePack.TEMPLATES) {
            assertTrue("titleEn non-blank for ${t.id}", t.titleEn.isNotBlank())
            assertTrue("titleFa non-blank for ${t.id}", t.titleFa.isNotBlank())
            assertTrue("descriptionEn non-blank for ${t.id}", t.descriptionEn.isNotBlank())
            assertTrue("descriptionFa non-blank for ${t.id}", t.descriptionFa.isNotBlank())
            assertTrue("doneConditionEn non-blank for ${t.id}", t.doneConditionEn.isNotBlank())
            assertTrue("doneConditionFa non-blank for ${t.id}", t.doneConditionFa.isNotBlank())
            assertTrue("contextTriggerEn non-blank for ${t.id}", t.contextTriggerEn.isNotBlank())
            assertTrue("contextTriggerFa non-blank for ${t.id}", t.contextTriggerFa.isNotBlank())
            assertTrue("leverageTipEn non-blank for ${t.id}", t.leverageTipEn.isNotBlank())
            assertTrue("leverageTipFa non-blank for ${t.id}", t.leverageTipFa.isNotBlank())
        }
    }

    @Test
    fun allTemplates_haveRealisticFocusDurations() {
        for (t in MissionTemplatePack.TEMPLATES) {
            assertTrue(
                "Duration for ${t.id} must be between 15 and 120 minutes, was ${t.defaultDurationMinutes}",
                t.defaultDurationMinutes in 15..120
            )
        }
    }

    @Test
    fun allCategories_areRepresentedInPack() {
        for (category in TemplateCategory.entries) {
            val matching = MissionTemplatePack.getByCategory(category)
            assertTrue("Category $category must have at least 1 template", matching.isNotEmpty())
        }
    }

    @Test
    fun getById_retrievesExpectedTemplate() {
        val template = MissionTemplatePack.getById("solopreneur_customer_interview")
        assertNotNull("Expected template solopreneur_customer_interview to exist", template)
        assertEquals(TemplateCategory.CUSTOMER_DISCOVERY, template?.category)

        val nonExistent = MissionTemplatePack.getById("unknown_template_xyz")
        assertNull(nonExistent)
    }

    @Test
    fun templateUsageStats_computesCorrectAdoptionRate() {
        val emptyStats = TemplateUsageStats(0, 0)
        assertEquals(0, emptyStats.totalCreations)
        assertEquals(0f, emptyStats.templateAdoptionRate, 0.001f)

        val activeStats = TemplateUsageStats(templateAcceptedCount = 3, blankCreatedCount = 1)
        assertEquals(4, activeStats.totalCreations)
        assertEquals(0.75f, activeStats.templateAdoptionRate, 0.001f)

        val allTemplates = TemplateUsageStats(templateAcceptedCount = 5, blankCreatedCount = 0)
        assertEquals(1.0f, allTemplates.templateAdoptionRate, 0.001f)
    }
}
