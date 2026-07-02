package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.Shadow

@Entity(tableName = "shadows")
data class ShadowEntity(
    @PrimaryKey val id: String,
    val name: String,
    val skillId: String,
    val rankLabel: String,
    val acquiredAt: Long,
    val skillCategory: String = "Mind"
) {
    fun toDomain(): Shadow = Shadow(
        id = id,
        name = name,
        skillId = skillId,
        rankLabel = rankLabel,
        acquiredAt = acquiredAt,
        skillCategory = skillCategory
    )

    companion object {
        fun fromDomain(domain: Shadow): ShadowEntity = ShadowEntity(
            id = domain.id,
            name = domain.name,
            skillId = domain.skillId,
            rankLabel = domain.rankLabel,
            acquiredAt = domain.acquiredAt,
            skillCategory = domain.skillCategory
        )
    }
}
