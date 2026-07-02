package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.Hunter

@Entity(tableName = "hunter_profile")
data class HunterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val level: Int,
    val rankLabel: String,
    val totalXP: Long,
    val currentXP: Int,
    val xpToNextLevel: Int,
    val progressPercent: Float,
    val rankColor: Long,
    val rankGlyph: String,
    val personalThesis: String = ""
) {
    fun toDomain(): Hunter = Hunter(
        id = id,
        name = name,
        level = level,
        rankLabel = rankLabel,
        totalXP = totalXP,
        currentXP = currentXP,
        xpToNextLevel = xpToNextLevel,
        progressPercent = progressPercent,
        rankColor = rankColor,
        rankGlyph = rankGlyph,
        personalThesis = personalThesis
    )

    companion object {
        fun fromDomain(domain: Hunter): HunterEntity = HunterEntity(
            id = domain.id,
            name = domain.name,
            level = domain.level,
            rankLabel = domain.rankLabel,
            totalXP = domain.totalXP,
            currentXP = domain.currentXP,
            xpToNextLevel = domain.xpToNextLevel,
            progressPercent = domain.progressPercent,
            rankColor = domain.rankColor,
            rankGlyph = domain.rankGlyph,
            personalThesis = domain.personalThesis
        )
    }
}
