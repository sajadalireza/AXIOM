package com.axiom.app.domain.usecase

import com.axiom.app.domain.repository.WarriorProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFinancialModuleEnabledUseCase @Inject constructor(
    private val repository: WarriorProfileRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isFinancialModuleEnabledFlow()
    }
}
