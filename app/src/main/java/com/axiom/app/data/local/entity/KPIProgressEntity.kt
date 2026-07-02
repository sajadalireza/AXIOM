package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kpi_progress")
data class KPIProgressEntity(
    @PrimaryKey val id: String,
    val kpiId: String,
    val date: Long,
    val incrementValue: Float
)
