package com.axiom.app.domain.xion

import java.util.UUID

/**
 * Lifecycle status of an individual Xion suggestion.
 */
enum class SuggestionStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    REPORTED
}

/**
 * Structured recommendation produced by the Xion Decision Layer.
 * Invariant: Every suggestion includes a concrete, testable physical Done Condition.
 */
data class XionSuggestion(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val skillName: String,
    val estimatedHours: Float,
    val rarity: String,
    val reasoning: String,
    val doneCondition: String,
    val isTemplateMatched: Boolean = false,
    val matchedTemplateId: String? = null,
    val wasEdited: Boolean = false,
    val isReported: Boolean = false,
    val status: SuggestionStatus = SuggestionStatus.PENDING
)

/**
 * Short, non-intrusive contextual insight (< 2 sentences).
 * Emphasizes physical execution over motivational therapy.
 */
data class XionContextualInsight(
    val insightEn: String,
    val insightFa: String,
    val frictionLevel: String, // "LOW", "OPTIMAL", "HIGH"
    val leverageTipEn: String? = null,
    val leverageTipFa: String? = null
)

/**
 * Cost & quota control state for Xion AI calls.
 * Ensures usage stays strictly bounded within budget.
 */
data class XionQuotaStatus(
    val dailyLimit: Int = 5,
    val usedToday: Int = 0,
    val remainingToday: Int = 5,
    val isExhausted: Boolean = remainingToday <= 0
)

/**
 * User feedback reasons for rejecting a Xion suggestion.
 * Used for acceptance rate computation and model alignment.
 */
enum class XionRejectionReason(val labelEn: String, val labelFa: String) {
    NOT_RELEVANT("Not relevant to goal", "مرتبط با هدف نیست"),
    TOO_VAGUE("Too vague / lacks done condition", "بسیار مبهم / فاقد شرط اتمام فیزیکی"),
    UNREALISTIC_TIME("Time estimate is unrealistic", "تخمین زمان غیرواقعی است"),
    DIFFERENT_FOCUS("Different priority right now", "اولویت متفاوتی در حال حاضر دارم"),
    OTHER("Other", "سایر")
}

/**
 * Categories for reporting boundary violations.
 * Strictly enforces anti-therapist, anti-financial-advisor, and anti-truth-authority policies.
 */
enum class XionReportCategory(val labelEn: String, val labelFa: String) {
    INAPPROPRIATE_COUNSELING("Psychological therapy or medical advice", "مشاوره روان‌شناختی یا پزشکی نامناسب"),
    FINANCIAL_SPECULATION("Financial counseling or market speculation", "مشاوره مالی یا سیگنال اقتصادی"),
    TRUTH_AUTHORITY_CLAIM("Omniscient or divine truth authority", "ادعای مرجعیت مطلق حقیقت یا دانای کل"),
    HALLUCINATION("Nonsense or hallucinated instructions", "دستورالعمل‌های ساختگی یا توهم")
}

/**
 * Aggregate acceptance metrics for Xion Decision Layer.
 * Canonical Acceptance Target: >= 35%.
 * Kill Criterion: Triggered if >= 10 decisions recorded and acceptanceRate < 20%.
 */
data class XionAcceptanceMetrics(
    val exposedCount: Int = 0,
    val acceptedCount: Int = 0,
    val rejectedCount: Int = 0,
    val editedCount: Int = 0,
    val reportedCount: Int = 0,
    val acceptanceRate: Float = 0f,
    val killCriterionTriggered: Boolean = false
)

/**
 * Provenance of the decision recommendations.
 */
enum class DecisionSource {
    TEMPLATE_FIRST,
    GATEWAY_GENERATED,
    OFFLINE_HEURISTIC
}
