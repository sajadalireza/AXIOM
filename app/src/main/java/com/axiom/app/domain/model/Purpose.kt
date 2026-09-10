package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

/**
 * Canonical Purpose domain model representing the durable reason, intent,
 * or "why" behind a Goal, Project, or course of action.
 *
 * Per AXIOM Canonical Vocabulary (CANONICAL_VOCABULARY.md):
 * "Purpose explains why a Goal, Project, or course of action matters.
 * Purpose does not replace Goal. Mission does not replace Purpose."
 */
@Immutable
data class Purpose(
    val statement: String,
    val coreMotivation: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
