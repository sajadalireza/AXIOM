package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_habit_logs")
data class DailyHabitLogEntity(
    @PrimaryKey val id: String,           // UUID
    val date: String,                     // "yyyy-MM-dd"
    val waterGlasses: Int = 0,             // count, goal = 8
    val sleepHours: Float? = null,
    val sleepQuality: Int? = null,         // 1-5
    val teethMorning: Boolean = false,
    val teethEvening: Boolean = false
)
