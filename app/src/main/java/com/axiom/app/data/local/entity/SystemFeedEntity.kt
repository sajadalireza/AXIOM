package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_feed")
data class SystemFeedEntity(
    @PrimaryKey val id: String,
    val message: String,
    val type: String,
    val xpGained: Int,
    val timestamp: Long
)
