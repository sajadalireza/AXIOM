package com.axiom.app.domain.usecase

import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSkillsUseCase @Inject constructor(
    private val repository: SkillRepository
) {
    operator fun invoke(): Flow<List<Skill>> {
        return repository.getAllSkills()
    }
}
