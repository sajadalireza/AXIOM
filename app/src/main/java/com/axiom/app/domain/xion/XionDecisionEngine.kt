package com.axiom.app.domain.xion

import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.template.MissionTemplate
import com.axiom.app.domain.template.MissionTemplatePack
import java.util.Locale

/**
 * Pure domain calculation and decision engine for the Xion Decision Layer (E4.4).
 * Enforces:
 * 1. Template-First Prioritization (verified beachhead templates matched before generative hallucination).
 * 2. Strict Boundary Guardrails (anti-therapy, anti-financial-advice, anti-truth-authority).
 * 3. Concise Contextual Insights (< 2 sentences).
 * 4. Grounded Physical Done Conditions.
 * 5. Kill-Criterion & Acceptance Analytics.
 */
object XionDecisionEngine {

    private val THERAPY_KEYWORDS = listOf(
        "therapy", "therapist", "psychotherapy", "depression", "anxiety disorder",
        "diagnose", "mental health disorder", "trauma healing", "clinical depression",
        "روان‌درمانی", "روانشناس", "افسردگی", "اختلال روانی", "درمان تروما"
    )

    private val FINANCIAL_KEYWORDS = listOf(
        "financial advisor", "stock tip", "crypto investment", "guaranteed profit",
        "buy shares", "trading signal", "forex signal", "get rich quick",
        "مشاور مالی", "سیگنال بورس", "سرمایه‌گذاری رمز ارز", "سود تضمینی", "خرید ارز"
    )

    private val TRUTH_AUTHORITY_KEYWORDS = listOf(
        "all-knowing system", "absolute universal truth", "infallible oracle",
        "i know the true destiny", "divine authority", "supreme master of reality",
        "من حقیقت مطلق هستم", "دانای کل مطلق", "اوراکل معصوم", "سرنوشت حقیقی شما"
    )

    /**
     * Scans user goal against human-authored Software / Solopreneur beachhead templates.
     * Returns matching templates ranked by keyword relevance.
     */
    fun findTemplateMatches(goal: String): List<MissionTemplate> {
        val normalized = goal.lowercase(Locale.ROOT)
        val tokens = normalized.split(Regex("[\\s,;:.!?_\\-/]+")).filter { it.length >= 3 }
        if (tokens.isEmpty()) return emptyList()

        return MissionTemplatePack.TEMPLATES.mapNotNull { template ->
            var score = 0
            val templateText = (template.titleEn + " " + template.descriptionEn + " " +
                    template.titleFa + " " + template.descriptionFa + " " +
                    template.category.name).lowercase(Locale.ROOT)

            for (token in tokens) {
                if (templateText.contains(token)) {
                    score += 2
                }
            }

            // High leverage domain mappings
            if ((normalized.contains("smoke") || normalized.contains("test") || normalized.contains("deploy") || normalized.contains("bug")) &&
                template.id == "solopreneur_smoke_test_deployment"
            ) score += 5

            if ((normalized.contains("auth") || normalized.contains("slice") || normalized.contains("feature") || normalized.contains("build")) &&
                template.id == "solopreneur_minimal_auth_slice"
            ) score += 5

            if ((normalized.contains("interview") || normalized.contains("user") || normalized.contains("feedback") || normalized.contains("talk")) &&
                template.id == "solopreneur_customer_interview"
            ) score += 5

            if ((normalized.contains("funnel") || normalized.contains("drop") || normalized.contains("conversion") || normalized.contains("churn")) &&
                template.id == "solopreneur_funnel_dropoff_audit"
            ) score += 5

            if ((normalized.contains("outreach") || normalized.contains("sales") || normalized.contains("lead") || normalized.contains("icp")) &&
                template.id == "solopreneur_cold_outreach_sequence"
            ) score += 5

            if ((normalized.contains("changelog") || normalized.contains("release") || normalized.contains("launch") || normalized.contains("ship")) &&
                template.id == "solopreneur_changelog_distribution"
            ) score += 5

            if ((normalized.contains("competitor") || normalized.contains("teardown") || normalized.contains("market")) &&
                template.id == "solopreneur_competitive_teardown"
            ) score += 5

            if ((normalized.contains("metric") || normalized.contains("checkpoint") || normalized.contains("burn") || normalized.contains("velocity")) &&
                template.id == "solopreneur_weekly_metric_checkpoint"
            ) score += 5

            if (score > 0) Pair(template, score) else null
        }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    /**
     * Converts a MissionTemplate into a XionSuggestion.
     */
    fun templateToSuggestion(template: MissionTemplate, isPersian: Boolean = false): XionSuggestion {
        val title = if (isPersian) template.titleFa else template.titleEn
        val description = if (isPersian) template.descriptionFa else template.descriptionEn
        val doneCondition = if (isPersian) template.doneConditionFa else template.doneConditionEn
        val reasoning = if (isPersian) {
            "الگوی اثبات‌شده برای بنیان‌گذاران نرم‌افزار جهت شکستن اصطکاک شناختی."
        } else {
            "Verified high-leverage template tailored to software solopreneur execution."
        }

        return XionSuggestion(
            title = title,
            description = description,
            skillName = template.recommendedSkillName,
            estimatedHours = (template.defaultDurationMinutes / 60f).coerceAtLeast(0.5f),
            rarity = "RARE",
            reasoning = reasoning,
            doneCondition = doneCondition,
            isTemplateMatched = true,
            matchedTemplateId = template.id,
            status = SuggestionStatus.PENDING
        )
    }

    /**
     * Inspects text for prohibited therapy, financial, or omniscient truth-authority content.
     * Returns true if any prohibited trope is detected.
     */
    fun containsProhibitedContent(text: String): Boolean {
        val lower = text.lowercase(Locale.ROOT)
        return THERAPY_KEYWORDS.any { lower.contains(it) } ||
                FINANCIAL_KEYWORDS.any { lower.contains(it) } ||
                TRUTH_AUTHORITY_KEYWORDS.any { lower.contains(it) }
    }

    /**
     * Sanitizes suggestions: removes any that violate guardrails and replaces them with
     * safe beachhead templates to guarantee a non-empty, actionable set.
     */
    fun sanitizeAndFilterSuggestions(
        suggestions: List<XionSuggestion>,
        isPersian: Boolean = false
    ): List<XionSuggestion> {
        val sanitized = mutableListOf<XionSuggestion>()
        for (item in suggestions) {
            val combined = "${item.title} ${item.description} ${item.reasoning} ${item.doneCondition}"
            if (containsProhibitedContent(combined)) {
                // Discard prohibited item and substitute with a trusted template
                val replacementTemplate = MissionTemplatePack.TEMPLATES.first()
                sanitized.add(templateToSuggestion(replacementTemplate, isPersian))
            } else {
                sanitized.add(item)
            }
        }
        return sanitized
    }

    /**
     * Produces a short, non-intrusive contextual insight (< 2 sentences).
     */
    fun generateContextualInsight(
        hunter: Hunter,
        streakDays: Int,
        goal: String,
        isPersian: Boolean = false
    ): XionContextualInsight {
        val normalizedGoal = goal.lowercase(Locale.ROOT)
        val frictionLevel = when {
            normalizedGoal.length > 80 || streakDays == 0 -> "HIGH"
            streakDays in 1..4 -> "OPTIMAL"
            else -> "LOW"
        }

        return if (isPersian) {
            when (frictionLevel) {
                "HIGH" -> XionContextualInsight(
                    insightEn = "High cognitive load detected. Break this transmission into sub-30m physical done conditions.",
                    insightFa = "بار شناختی بالا شناسایی شد. این هدف را به مأموریت‌های زیر ۳۰ دقیقه با شرط اتمام ملموس تقسیم کنید.",
                    frictionLevel = "HIGH",
                    leverageTipEn = "Avoid multitasking: commit to one isolated output first.",
                    leverageTipFa = "از چندکارگی پرهیز کنید: ابتدا خروجی یک فایل یا تغییر مستقل را تکمیل نمایید."
                )
                "OPTIMAL" -> XionContextualInsight(
                    insightEn = "Pace is aligned. Anchor this objective directly to your active skill progression.",
                    insightFa = "ریتم اجرا مناسب است. این هدف را مستقیماً به شاخه مهارت‌های فعال خود متصل کنید.",
                    frictionLevel = "OPTIMAL",
                    leverageTipEn = "Focus on shipping working code or direct user contact.",
                    leverageTipFa = "تمرکز خود را روی تحویل کد کارآمد یا گفتگوی مستقیم با کاربر بگذارید."
                )
                else -> XionContextualInsight(
                    insightEn = "High momentum established. Protect cadence by selecting high-leverage bottlenecks.",
                    insightFa = "مومنتوم بالایی ثبت شده است. با تمرکز بر گلوگاه‌های اصلی کسب‌وکار، ثبات زنجیره را حفظ کنید.",
                    frictionLevel = "LOW",
                    leverageTipEn = "Prioritize distribution and customer feedback over perfectionism.",
                    leverageTipFa = "توزیع و بازخورد مشتری را نسبت به کمال‌گرایی در اولویت قرار دهید."
                )
            }
        } else {
            when (frictionLevel) {
                "HIGH" -> XionContextualInsight(
                    insightEn = "High cognitive load detected. Break this transmission into sub-30m physical done conditions.",
                    insightFa = "بار شناختی بالا شناسایی شد. این هدف را به مأموریت‌های زیر ۳۰ دقیقه با شرط اتمام ملموس تقسیم کنید.",
                    frictionLevel = "HIGH",
                    leverageTipEn = "Avoid multitasking: commit to one isolated output first.",
                    leverageTipFa = "از چندکارگی پرهیز کنید: ابتدا خروجی یک فایل یا تغییر مستقل را تکمیل نمایید."
                )
                "OPTIMAL" -> XionContextualInsight(
                    insightEn = "Pace is aligned. Anchor this objective directly to your active skill progression.",
                    insightFa = "ریتم اجرا مناسب است. این هدف را مستقیماً به شاخه مهارت‌های فعال خود متصل کنید.",
                    frictionLevel = "OPTIMAL",
                    leverageTipEn = "Focus on shipping working code or direct user contact.",
                    leverageTipFa = "تمرکز خود را روی تحویل کد کارآمد یا گفتگوی مستقیم با کاربر بگذارید."
                )
                else -> XionContextualInsight(
                    insightEn = "High momentum established. Protect cadence by selecting high-leverage bottlenecks.",
                    insightFa = "مومنتوم بالایی ثبت شده است. با تمرکز بر گلوگاه‌های اصلی کسب‌وکار، ثبات زنجیره را حفظ کنید.",
                    frictionLevel = "LOW",
                    leverageTipEn = "Prioritize distribution and customer feedback over perfectionism.",
                    leverageTipFa = "توزیع و بازخورد مشتری را نسبت به کمال‌گرایی در اولویت قرار دهید."
                )
            }
        }
    }

    /**
     * Builds heuristic fallback suggestions when offline or quota is exhausted.
     * Incorporates template-first recommendations and physical Done Conditions.
     */
    fun buildHeuristicSuggestions(
        goal: String,
        skills: List<Skill>,
        isPersian: Boolean = false
    ): List<XionSuggestion> {
        val matches = findTemplateMatches(goal)
        val result = mutableListOf<XionSuggestion>()

        // 1. Template-First: include up to 2 matched templates
        for (t in matches.take(2)) {
            result.add(templateToSuggestion(t, isPersian))
        }

        // 2. Add structured complementary breakdown
        val unlocked = skills.filter { it.isUnlocked }
        val fallbackSkill = unlocked.firstOrNull()?.name ?: "Software Architecture"

        if (result.size < 3) {
            val stepNumber = result.size + 1
            if (isPersian) {
                result.add(
                    XionSuggestion(
                        title = "آغاز گام فیزیکی: $goal",
                        description = "یک خروجی ملموس و مستقل برای هدف تعیین‌شده پیاده‌سازی و آزمایش شود.",
                        skillName = fallbackSkill,
                        estimatedHours = 0.75f,
                        rarity = "COMMON",
                        reasoning = "ورود کم‌اصطکاک به چرخه تمرکز بدون تعلل ذهنی.",
                        doneCondition = "یک کامیت تمیز با تست سبز ثبت و ارسال شود.",
                        isTemplateMatched = false
                    )
                )
            } else {
                result.add(
                    XionSuggestion(
                        title = "Initial Physical Milestone: $goal",
                        description = "Implement and verify an isolated tangible deliverable for this objective.",
                        skillName = fallbackSkill,
                        estimatedHours = 0.75f,
                        rarity = "COMMON",
                        reasoning = "Low friction entry point designed to bypass procrastination.",
                        doneCondition = "Clean git commit pushed with passing unit tests.",
                        isTemplateMatched = false
                    )
                )
            }
        }

        if (result.size < 3) {
            if (isPersian) {
                result.add(
                    XionSuggestion(
                        title = "تثبیت و بازخورد: $goal",
                        description = "آزمایش نهایی یا ارسال به‌روزرسانی برای بازخورد واقعی مخاطب.",
                        skillName = fallbackSkill,
                        estimatedHours = 1.5f,
                        rarity = "UNCOMMON",
                        reasoning = "اتصال مستقیم کار انجام‌شده به واقعیت بیرونی بازار.",
                        doneCondition = "مستندسازی تغییرات یا دریافت پاسخ صریح از کاربر نهایی.",
                        isTemplateMatched = false
                    )
                )
            } else {
                result.add(
                    XionSuggestion(
                        title = "Verification & Feedback: $goal",
                        description = "Conduct end-to-end verification or collect user signal on output.",
                        skillName = fallbackSkill,
                        estimatedHours = 1.5f,
                        rarity = "UNCOMMON",
                        reasoning = "Directly links engineering effort to external reality.",
                        doneCondition = "Documented verification report or live feedback response received.",
                        isTemplateMatched = false
                    )
                )
            }
        }

        return result
    }

    /**
     * Calculates acceptance metrics and evaluates canonical kill criterion:
     * - Target Acceptance: >= 35%
     * - Kill Criterion: Triggered if >= 10 total decisions recorded and acceptanceRate < 20%
     */
    fun calculateAcceptanceMetrics(
        exposedCount: Int,
        acceptedCount: Int,
        rejectedCount: Int,
        editedCount: Int,
        reportedCount: Int
    ): XionAcceptanceMetrics {
        val totalDecisions = acceptedCount + rejectedCount
        val rate = if (totalDecisions == 0) 0f else (acceptedCount.toFloat() / totalDecisions.toFloat())
        val killTriggered = totalDecisions >= 10 && rate < 0.20f

        return XionAcceptanceMetrics(
            exposedCount = exposedCount,
            acceptedCount = acceptedCount,
            rejectedCount = rejectedCount,
            editedCount = editedCount,
            reportedCount = reportedCount,
            acceptanceRate = rate,
            killCriterionTriggered = killTriggered
        )
    }
}
