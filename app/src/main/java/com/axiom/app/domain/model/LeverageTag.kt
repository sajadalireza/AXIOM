package com.axiom.app.domain.model

import androidx.compose.ui.graphics.Color

enum class LeverageTag(val colorHex: Long) {
    FOUNDATION(0xFF6E6E85),
    COMPOUND(0xFF188C68),
    CRITICAL(0xFF2569B8),
    SHIELD(0xFF6258C4),
    DEPTH(0xFFC97F0E),
    BATCH(0xFF8A8AA0),
    WEALTH_ENGINE(0xFFEF9F27),
    REVIEW(0xFF7F77DD),
    PROTECTED(0xFFFF5252),
    REST(0xFF4CAF50);

    fun getColor(): Color = Color(colorHex)

    companion object {
        fun fromString(str: String): LeverageTag {
            val clean = str.trim().uppercase()
            return when (clean) {
                "COMMON" -> FOUNDATION
                "UNCOMMON" -> COMPOUND
                "RARE" -> CRITICAL
                "EPIC" -> SHIELD
                "LEGENDARY" -> DEPTH
                else -> {
                    try {
                        valueOf(clean)
                    } catch (e: Exception) {
                        FOUNDATION
                    }
                }
            }
        }
    }
}
