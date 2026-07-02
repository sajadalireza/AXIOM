package com.axiom.app.domain.model

data class XPResult(
    val missionId: String,
    val hunterXPGained: Int,
    val skillXPGained: Map<String, Long>,
    val skillLeveledUp: Map<String, Boolean>,
    val shadowUnlocked: Shadow?,
    val leveledUp: Boolean,
    val newLevel: Int?,
    val rankChanged: Boolean,
    val newRank: String?,
    val shadowMultiplier: Float = 1.0f
)
