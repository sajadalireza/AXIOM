package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.HunterDao
import com.axiom.app.data.local.entity.HunterEntity
import com.axiom.app.domain.model.Hunter
import com.axiom.app.domain.repository.HunterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HunterRepositoryImpl @Inject constructor(
    private val hunterDao: HunterDao
) : HunterRepository {
    override fun getHunterProfile(): Flow<Hunter?> =
        hunterDao.getProfileFlow().map { it?.toDomain() }

    override suspend fun getDirectHunterProfile(): Hunter? = withContext(Dispatchers.IO) {
        hunterDao.getProfile()?.toDomain()
    }

    override suspend fun updateHunterProfile(profile: Hunter) = withContext(Dispatchers.IO) {
        hunterDao.updateProfile(HunterEntity.fromDomain(profile))
    }
}
