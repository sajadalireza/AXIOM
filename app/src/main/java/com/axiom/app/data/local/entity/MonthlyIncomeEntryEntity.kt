package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.MonthlyIncomeEntry

@Entity(tableName = "monthly_income_entries")
data class MonthlyIncomeEntryEntity(
    @PrimaryKey val id: String,
    val monthIndex: Int,
    val actualAmount: Float
) {
    fun toDomain(): MonthlyIncomeEntry = MonthlyIncomeEntry(
        id = id,
        monthIndex = monthIndex,
        actualAmount = actualAmount
    )

    companion object {
        fun fromDomain(domain: MonthlyIncomeEntry): MonthlyIncomeEntryEntity = MonthlyIncomeEntryEntity(
            id = domain.id,
            monthIndex = domain.monthIndex,
            actualAmount = domain.actualAmount
        )
    }
}
