package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_reviews")
data class WeeklyReviewEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val step1Summary: String,
    val step2WrongAssumption: String,
    val step3CriticFeedback: String,
    val step4DecisionType: String,
    val step5JournalText: String
)
