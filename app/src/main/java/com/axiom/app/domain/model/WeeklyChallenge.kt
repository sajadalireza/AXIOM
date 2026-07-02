package com.axiom.app.domain.model

data class WeeklyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val targetValue: Int,
    val currentValue: Int = 0,
    val isCompleted: Boolean = false
)

data class WeeklyProgress(
    val missionsDone: Int = 0,
    val streakBest: Int = 0,
    val rareDone: Int = 0,
    val allClaimed: Boolean = false
)
