package com.axiom.app.core.ai

import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.xion.SuggestionStatus
import com.axiom.app.domain.xion.XionDecisionEngine
import com.axiom.app.domain.xion.XionSuggestion
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stripped, privacy-safe context for gateway communication.
 * Invariant: ZERO PII. Personal names, identifiers, locations, and raw personal notes
 * are strictly forbidden from this data class.
 */
data class XionHunterContext(
    val level: Int,
    val rankLabel: String,
    val streakDays: Int,
    val unlockedSkills: List<String>
)

/**
 * Server-side gateway boundary contract for Xion AI decisions.
 * Decouples the client UI from specific model providers and enforces security invariants.
 */
interface XionGatewayBoundary {
    suspend fun generateDecision(
        goal: String,
        context: XionHunterContext,
        isPersian: Boolean
    ): Result<List<XionSuggestion>>
}

/**
 * Production implementation of XionGatewayBoundary.
 * - Respects AiEgressPolicy (fail-closed unless direct Gemini egress is explicitly allowed).
 * - Enforces zero client secrets in the APK.
 * - Injects strict anti-therapist, anti-financial-advice, anti-truth-authority system prompts.
 * - Sanitizes all output through XionDecisionEngine.
 */
@Singleton
class DefaultXionGatewayBoundary @Inject constructor(
    private val preferences: AxiomPreferences
) : XionGatewayBoundary {

    override suspend fun generateDecision(
        goal: String,
        context: XionHunterContext,
        isPersian: Boolean
    ): Result<List<XionSuggestion>> = runCatching {
        // Step 1: Enforce fail-closed egress security policy
        if (!AiEgressPolicy.isDirectGeminiAllowed()) {
            throw DirectAiEgressDisabledException()
        }

        // Step 2: Read user-supplied BYO key (zero bundled secrets)
        val apiKey = preferences.geminiApiKeyFlow.first()
            ?: throw NoApiKeyException()

        val langInstruction = if (isPersian) {
            "Generate title, description, reasoning, and doneCondition strictly in Persian (Farsi). Keep skillName matching the English list: ${context.unlockedSkills.joinToString(", ")}."
        } else {
            "Generate all fields strictly in English. Keep skillName matching the English list: ${context.unlockedSkills.joinToString(", ")}."
        }

        val systemInstruction = """
            You are XION — an executive tactical copilot for Software / Solopreneur execution.
            You break goals into high-leverage engineering, customer discovery, and distribution missions.
            BOUNDARY INVARIANTS:
            1. You are NOT a therapist, psychologist, or medical professional. Never offer mental health advice or diagnosis.
            2. You are NOT a financial advisor or investment guide. Never give stock, crypto, or financial speculation tips.
            3. You are NOT an omniscient or divine authority. Never claim absolute truth or cosmic insight.
            4. Every mission MUST have a concrete, physical, testable doneCondition (e.g. 'PR merged with 5 tests', '10 users interviewed with notes documented').
            $langInstruction
        """.trimIndent()

        val prompt = """
            Hunter Context:
            - Level: ${context.level}
            - Rank: ${context.rankLabel}
            - Streak: ${context.streakDays} days
            - Objective: "$goal"
            - Available Skills: ${context.unlockedSkills.joinToString(", ")}

            Generate exactly 3 progressive execution missions as a valid JSON array. No markdown fences, no explanatory intro.
            Schema for each array element:
            {
              "title": string (action-oriented, max 8 words),
              "description": string (one sentence explaining value),
              "skillName": string (must match one available skill),
              "estimatedHours": float (between 0.5 and 4.0),
              "rarity": string ("COMMON", "UNCOMMON", "RARE", or "EPIC"),
              "reasoning": string (one sentence tactical rationale),
              "doneCondition": string (specific physical verification condition)
            }
        """.trimIndent()

        val model = GenerativeModel(
            modelName = GEMINI_MODEL_NAME,
            apiKey = apiKey,
            systemInstruction = content { text(systemInstruction) }
        )

        val response = model.generateContent(prompt).text
            ?: throw IllegalStateException("Empty response from AI gateway")

        val cleanJson = response.trim()
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```").trim()

        val jsonArray = JSONArray(cleanJson)
        val rawSuggestions = mutableListOf<XionSuggestion>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            rawSuggestions.add(
                XionSuggestion(
                    title = obj.getString("title"),
                    description = obj.getString("description"),
                    skillName = obj.optString("skillName", context.unlockedSkills.firstOrNull() ?: "Engineering"),
                    estimatedHours = obj.optDouble("estimatedHours", 1.0).toFloat().coerceIn(0.25f, 8.0f),
                    rarity = obj.optString("rarity", "COMMON"),
                    reasoning = obj.optString("reasoning", "Progress toward core objective."),
                    doneCondition = obj.optString("doneCondition", "Deliverable verified and committed."),
                    isTemplateMatched = false,
                    status = SuggestionStatus.PENDING
                )
            )
        }

        // Pass through post-filtering guardrails
        XionDecisionEngine.sanitizeAndFilterSuggestions(rawSuggestions, isPersian)
    }
}
