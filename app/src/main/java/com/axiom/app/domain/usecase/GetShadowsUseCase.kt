package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.repository.ShadowRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShadowsUseCase @Inject constructor(
    private val repository: ShadowRepository
) {
    operator fun invoke(): Flow<List<Shadow>> {
        return repository.getAllShadows()
    }
}
