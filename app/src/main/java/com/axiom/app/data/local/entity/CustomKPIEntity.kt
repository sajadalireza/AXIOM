package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.CustomKPI

@Entity(tableName = "custom_kpis")
data class CustomKPIEntity(
    @PrimaryKey val id: String,
    val trackId: String?,
    val name: String,
    val targetValue: Float,
    val targetUnit: String,
    val measurementHint: String,
    val redFlagAction: String
) {
    fun toDomain(): CustomKPI = CustomKPI(
        id = id,
        trackId = trackId,
        name = name,
        targetValue = targetValue,
        targetUnit = targetUnit,
        measurementHint = measurementHint,
        redFlagAction = redFlagAction
    )

    companion object {
        fun fromDomain(domain: CustomKPI): CustomKPIEntity = CustomKPIEntity(
            id = domain.id,
            trackId = domain.trackId,
            name = domain.name,
            targetValue = domain.targetValue,
            targetUnit = domain.targetUnit,
            measurementHint = domain.measurementHint,
            redFlagAction = domain.redFlagAction
        )
    }
}
