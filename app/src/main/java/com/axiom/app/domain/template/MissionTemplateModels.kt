package com.axiom.app.domain.template

/**
 * Single Beachhead focus for E4.3: Software / Solopreneur.
 */
enum class Beachhead(val titleEn: String, val titleFa: String) {
    SOFTWARE_SOLOPRENEUR(
        titleEn = "Software / Solopreneur",
        titleFa = "توسعه‌دهنده مستقل نرم‌افزار"
    )
}

/**
 * Functional category for mission templates.
 */
enum class TemplateCategory(val titleEn: String, val titleFa: String) {
    PRODUCT_ENGINEERING(
        titleEn = "Product & Engineering",
        titleFa = "محصول و مهندسی"
    ),
    CUSTOMER_DISCOVERY(
        titleEn = "Customer Discovery",
        titleFa = "کشف مشتری و مصاحبه"
    ),
    DISTRIBUTION_SALES(
        titleEn = "Distribution & Growth",
        titleFa = "توزیع و رشد"
    ),
    OPERATIONS_METRICS(
        titleEn = "Operations & Metrics",
        titleFa = "عملیات و شاخص‌ها"
    )
}

/**
 * Human-authored, concrete mission template designed for high leverage.
 */
data class MissionTemplate(
    val id: String,
    val beachhead: Beachhead,
    val category: TemplateCategory,
    val titleEn: String,
    val titleFa: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val doneConditionEn: String,
    val doneConditionFa: String,
    val contextTriggerEn: String,
    val contextTriggerFa: String,
    val defaultDurationMinutes: Int,
    val recommendedTrack: String,
    val recommendedSkillName: String,
    val leverageTipEn: String,
    val leverageTipFa: String
)

/**
 * Statistics comparing template adoption versus blank mission creation.
 */
data class TemplateUsageStats(
    val templateAcceptedCount: Int,
    val blankCreatedCount: Int
) {
    val totalCreations: Int
        get() = templateAcceptedCount + blankCreatedCount

    val templateAdoptionRate: Float
        get() = if (totalCreations > 0) templateAcceptedCount.toFloat() / totalCreations else 0f
}
