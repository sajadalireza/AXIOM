package com.axiom.app.data.repository

import com.axiom.app.data.local.dao.ShadowDao
import com.axiom.app.data.local.entity.ShadowEntity
import com.axiom.app.domain.model.Shadow
import com.axiom.app.domain.repository.ShadowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShadowRepositoryImpl @Inject constructor(
    private val shadowDao: ShadowDao
) : ShadowRepository {
    override fun getAllShadows(): Flow<List<Shadow>> =
        shadowDao.getAllShadowsFlow().map { list -> list.map { it.toDomain() } }

    override suspend fun insertShadow(shadow: Shadow) = withContext(Dispatchers.IO) {
        shadowDao.insertShadow(ShadowEntity.fromDomain(shadow))
    }
}
