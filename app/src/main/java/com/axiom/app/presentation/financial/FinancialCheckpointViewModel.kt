package com.axiom.app.presentation.financial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.app.domain.model.FinancialCheckpoint
import com.axiom.app.domain.model.MonthlyIncomeEntry
import com.axiom.app.domain.usecase.GetFinancialDataUseCase
import com.axiom.app.domain.usecase.SaveFinancialCheckpointUseCase
import com.axiom.app.domain.usecase.SaveMonthlyIncomeEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface FinancialCheckpointUiState {
    object Loading : FinancialCheckpointUiState
    data class Success(
        val checkpoints: List<FinancialCheckpoint>,
        val actualEntries: List<MonthlyIncomeEntry>,
        val currency: String
    ) : FinancialCheckpointUiState
}

@HiltViewModel
class FinancialCheckpointViewModel @Inject constructor(
    private val getFinancialDataUseCase: GetFinancialDataUseCase,
    private val saveFinancialCheckpointUseCase: SaveFinancialCheckpointUseCase,
    private val saveMonthlyIncomeEntryUseCase: SaveMonthlyIncomeEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FinancialCheckpointUiState>(FinancialCheckpointUiState.Loading)
    val uiState: StateFlow<FinancialCheckpointUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getFinancialDataUseCase()
                .onStart { _uiState.value = FinancialCheckpointUiState.Loading }
                .catch { _uiState.value = FinancialCheckpointUiState.Success(emptyList(), emptyList(), "$") }
                .collect { data ->
                    val defaultCurrency = data.checkpoints.firstOrNull()?.currency ?: "$"
                    _uiState.value = FinancialCheckpointUiState.Success(
                        checkpoints = data.checkpoints.sortedBy { it.monthIndex },
                        actualEntries = data.entries.sortedBy { it.monthIndex },
                        currency = defaultCurrency
                    )
                }
        }
    }

    fun updateTarget(monthIndex: Int, amount: Float, currency: String, existingCheckpoint: FinancialCheckpoint?) {
        viewModelScope.launch {
            val checkpoint = existingCheckpoint?.copy(targetAmount = amount, currency = currency)
                ?: FinancialCheckpoint(
                    id = UUID.randomUUID().toString(),
                    monthIndex = monthIndex,
                    targetAmount = amount,
                    currency = currency
                )
            saveFinancialCheckpointUseCase(checkpoint)
        }
    }

    fun updateIncome(monthIndex: Int, amount: Float, existingEntry: MonthlyIncomeEntry?) {
        viewModelScope.launch {
            val entry = existingEntry?.copy(actualAmount = amount)
                ?: MonthlyIncomeEntry(
                    id = UUID.randomUUID().toString(),
                    monthIndex = monthIndex,
                    actualAmount = amount
                )
            saveMonthlyIncomeEntryUseCase(entry)
        }
    }
}
