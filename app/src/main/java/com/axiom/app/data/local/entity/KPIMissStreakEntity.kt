package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kpi_miss_streaks")
data class KPIMissStreakEntity(
    @PrimaryKey val kpiId: String,
    val missStreak: Int
)
