package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.SkillDao
import com.axiom.app.data.local.entity.SkillEntity
import com.axiom.app.domain.model.Skill
import com.axiom.app.domain.repository.SkillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRepositoryImpl @Inject constructor(
    private val skillDao: SkillDao
) : SkillRepository {
    override fun getAllSkills(): Flow<List<Skill>> =
        skillDao.getAllSkillsFlow().map { list -> list.map { it.toDomain().copy(isUnlocked = true) } }

    override suspend fun getSkillById(id: String): Skill? = withContext(Dispatchers.IO) {
        skillDao.getSkillById(id)?.toDomain()?.copy(isUnlocked = true)
    }

    override suspend fun insertSkill(skill: Skill) = withContext(Dispatchers.IO) {
        skillDao.insertSkill(SkillEntity.fromDomain(skill))
    }

    override suspend fun updateSkill(skill: Skill) = withContext(Dispatchers.IO) {
        skillDao.updateSkill(SkillEntity.fromDomain(skill))
    }

    override suspend fun deleteSkillById(id: String) = withContext(Dispatchers.IO) {
        skillDao.deleteSkillById(id)
    }
}
