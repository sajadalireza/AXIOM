package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

enum class VitalType {
    WATER_ML, SLEEP_HOURS, TEETH_AM, TEETH_PM, ENERGY_SCORE
}

@Entity(
    tableName = "vital_logs",
    indices = [Index(value = ["date", "type"], unique = true)]
)
data class VitalLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // epoch day: System.currentTimeMillis() / 86400000L
    val type: String, // String representation of VitalType
    val value: Float,
    val loggedAt: Long = System.currentTimeMillis()
)
