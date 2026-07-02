package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.FinancialCheckpoint
import com.axiom.app.domain.model.MonthlyIncomeEntry
import com.axiom.app.domain.repository.WarriorProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFinancialDataUseCase @Inject constructor(
    private val repository: WarriorProfileRepository
) {
    operator fun invoke(): Flow<FinancialData> {
        return combine(
            repository.getFinancialCheckpointsFlow(),
            repository.getMonthlyIncomeEntriesFlow()
        ) { checkpots, entries ->
            FinancialData(checkpots, entries)
        }
    }
}

data class FinancialData(
    val checkpoints: List<FinancialCheckpoint>,
    val entries: List<MonthlyIncomeEntry>
)
