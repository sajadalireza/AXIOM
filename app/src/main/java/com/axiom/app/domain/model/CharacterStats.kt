package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class CharacterStats(
    val execution: Int,
    val focus: Int,
    val knowledge: Int,
    val business: Int,
    val fitness: Int,
    val creativity: Int
) {
    val strength: Int get() = fitness
    val intelligence: Int get() = knowledge
    val vitality: Int get() = focus
}

