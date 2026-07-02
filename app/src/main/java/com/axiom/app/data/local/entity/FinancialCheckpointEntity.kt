package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.FinancialCheckpoint

@Entity(tableName = "financial_checkpoints")
data class FinancialCheckpointEntity(
    @PrimaryKey val id: String,
    val monthIndex: Int,
    val targetAmount: Float,
    val currency: String
) {
    fun toDomain(): FinancialCheckpoint = FinancialCheckpoint(
        id = id,
        monthIndex = monthIndex,
        targetAmount = targetAmount,
        currency = currency
    )

    companion object {
        fun fromDomain(domain: FinancialCheckpoint): FinancialCheckpointEntity = FinancialCheckpointEntity(
            id = domain.id,
            monthIndex = domain.monthIndex,
            targetAmount = domain.targetAmount,
            currency = domain.currency
        )
    }
}
