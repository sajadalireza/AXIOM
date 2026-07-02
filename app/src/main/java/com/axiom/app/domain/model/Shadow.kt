package com.axiom.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Shadow(
    val id: String,
    val name: String,
    val skillId: String,
    val rankLabel: String,
    val acquiredAt: Long,
    val skillCategory: String = "Mind"
)
