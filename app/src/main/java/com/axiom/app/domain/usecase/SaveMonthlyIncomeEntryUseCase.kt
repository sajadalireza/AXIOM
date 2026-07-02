package com.axiom.app.domain.usecase

import android.util.Log
import com.axiom.app.domain.model.MonthlyIncomeEntry
import com.axiom.app.domain.repository.WarriorProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveMonthlyIncomeEntryUseCase @Inject constructor(
    private val repository: WarriorProfileRepository
) {
    suspend operator fun invoke(entry: MonthlyIncomeEntry) = withContext(Dispatchers.IO) {
        try {
            Log.d("SaveMonthlyIncomeEntry", "Saving monthly income entry: id=${entry.id}")
            repository.saveMonthlyIncomeEntry(entry)
        } catch (e: Exception) {
            // CRITICAL: Throw a fully generic exception. Never interpolate amount, monthIndex, or currency.
            throw IllegalStateException("Failed to save financial actual entry", e)
        }
    }
}
