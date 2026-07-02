package com.axiom.app.domain.usecase

import android.util.Log
import com.axiom.app.domain.model.FinancialCheckpoint
import com.axiom.app.domain.repository.WarriorProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveFinancialCheckpointUseCase @Inject constructor(
    private val repository: WarriorProfileRepository
) {
    suspend operator fun invoke(checkpoint: FinancialCheckpoint) = withContext(Dispatchers.IO) {
        try {
            // Local logging is safe (CrashReporter only captures Throwables)
            Log.d("SaveFinancialCheckpoint", "Saving checkpoint: id=${checkpoint.id}")
            repository.saveFinancialCheckpoint(checkpoint)
        } catch (e: Exception) {
            // CRITICAL: Throw a fully generic exception. Never interpolate amount, monthIndex, or currency.
            throw IllegalStateException("Failed to save financial checkpoint", e)
        }
    }
}
