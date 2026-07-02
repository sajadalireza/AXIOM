package com.axiom.app.domain.model

data class FinancialCheckpoint(
    val id: String,
    val monthIndex: Int,
    val targetAmount: Float,
    val currency: String
)

data class MonthlyIncomeEntry(
    val id: String,
    val monthIndex: Int,
    val actualAmount: Float
)
