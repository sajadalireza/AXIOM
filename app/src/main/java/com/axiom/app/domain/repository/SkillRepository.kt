package com.axiom.app.domain.repository

import com.axiom.app.domain.model.Skill
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun getAllSkills(): Flow<List<Skill>>
    suspend fun getSkillById(id: String): Skill?
    suspend fun insertSkill(skill: Skill)
    suspend fun updateSkill(skill: Skill)
    suspend fun deleteSkillById(id: String)
}
