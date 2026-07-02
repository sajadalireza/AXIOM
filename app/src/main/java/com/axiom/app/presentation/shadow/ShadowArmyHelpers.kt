package com.axiom.app.presentation.shadow

import com.axiom.app.domain.model.Shadow

/**
 * Sort options for the Shadow Army grid.
 * Kept out of the screen composable so the UI file stays focused on layout.
 */
enum class SortOption {
    POWER, DATE, CATEGORY
}

/**
 * Returns the lore/backstory text for a given shadow.
 * This is content/business logic and intentionally lives outside the screen
 * composable so [ShadowArmyScreen] only deals with rendering.
 */
fun getShadowStory(shadow: Shadow): String {
    return when (shadow.name.trim()) {
        "Research Scientist" -> "Born from the depth of complex analysis, this shadow once clouded the mind with analytical paralysis and doubt. Now, transformed into the Research Scientist, it serves as an unparalleled cognitive engine, mapping connections and mining insights from the void of chaos."
        "ML Engineer" -> "Once a barrier of complexity and algorithmic hesitation, this shadow has been subjugated into the ML Engineer. It builds high-throughput models of execution, optimizing processes and structuring raw data of life with mathematical precision."
        "Startup Advisor" -> "The embodiment of risk-aversion and fear of failure, now bound as the Startup Advisor. It analyzes markets, navigates critical pivot points, and offers calculated, high-leverage strategic guidance."
        "English Coach" -> "The voice of articulate translation and expression, once representing a fear of miscommunication. It has been refined into the English Coach, sharpening rhetoric and empowering linguistic fluency."
        "Market Intelligence" -> "Formed from uncertainty in competitive spaces. Subjugated into Market Intelligence, it continuously scans the horizons, providing clarity and foresight in times of market volatility."
        "Publishing Coach" -> "Once a fear of public judgment and creative vulnerability. This shadow now guides the distribution of ideas, maximizing reach and aligning narrative structures."
        "Accountability Partner" -> "The ultimate conqueror of procrastination. Born from lazy afternoons and broken promises, it now stands as an unwavering sentinel, demanding high-level discipline and daily focus."
        "Ruthless Critic" -> "The inner voice of hyper-criticism and self-doubt. By facing and subjugating this powerful entity, it has been bound as the Ruthless Critic, providing unvarnished, objective feedback to expose and eliminate weaknesses."
        else -> "A powerful virtual team operative born from the subjugation of personal limits. Aligned with ${shadow.skillCategory}, this shadow channels its formidable energy to amplify focus and accelerate mastery."
    }
}
