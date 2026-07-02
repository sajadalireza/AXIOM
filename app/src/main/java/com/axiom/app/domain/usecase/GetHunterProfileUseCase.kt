package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.repository.HunterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHunterProfileUseCase @Inject constructor(
    private val repository: HunterRepository
) {
    operator fun invoke(): Flow<Hunter?> {
        return repository.getHunterProfile()
    }
}
