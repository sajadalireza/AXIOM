package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.Skill

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val currentXP: Long,
    val level: Int,
    val rankLabel: String,
    val parentId: String?,
    val isUnlocked: Boolean,
    val xpToNextRank: Long,
    val rankProgressPercent: Float,
    val isShadowCandidate: Boolean,
    val rankColor: Long,
    val trackId: String? = null,
    val totalRawHours: Double = 0.0,
    val totalEffectiveHours: Double = 0.0
) {
    fun toDomain(): Skill = Skill(
        id = id,
        name = name,
        category = category,
        currentXP = currentXP,
        level = level,
        rankLabel = rankLabel,
        parentId = parentId,
        isUnlocked = isUnlocked,
        xpToNextRank = xpToNextRank,
        rankProgressPercent = rankProgressPercent,
        isShadowCandidate = isShadowCandidate,
        rankColor = rankColor,
        trackId = trackId,
        totalRawHours = totalRawHours,
        totalEffectiveHours = totalEffectiveHours
    )

    companion object {
        fun fromDomain(domain: Skill): SkillEntity = SkillEntity(
            id = domain.id,
            name = domain.name,
            category = domain.category,
            currentXP = domain.currentXP,
            level = domain.level,
            rankLabel = domain.rankLabel,
            parentId = domain.parentId,
            isUnlocked = domain.isUnlocked,
            xpToNextRank = domain.xpToNextRank,
            rankProgressPercent = domain.rankProgressPercent,
            isShadowCandidate = domain.isShadowCandidate,
            rankColor = domain.rankColor,
            trackId = domain.trackId,
            totalRawHours = domain.totalRawHours,
            totalEffectiveHours = domain.totalEffectiveHours
        )
    }
}
