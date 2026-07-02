package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.Mission

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val track: String,
    val rarity: String,
    val skillId: String,
    val skillName: String,
    val xpReward: Int,
    val powerScore: Float,
    val status: String,
    val dungeonId: String?,
    val estimatedHours: Float,
    val actualHours: Float?,
    val createdAt: Long,
    val completedAt: Long?,
    val rarityColor: Long,
    val isInstantGate: Boolean = false,
    val description: String = "",
    val trackId: String? = null,
    val scheduleBlockId: String? = null,
    val qualityScore: Double = 1.0,
    val effectiveHours: Double = 0.0
) {
    fun toDomain(): Mission = Mission(
        id = id,
        title = title,
        track = track,
        rarity = rarity,
        skillId = skillId,
        skillName = skillName,
        xpReward = xpReward,
        powerScore = powerScore,
        status = status,
        dungeonId = dungeonId,
        estimatedHours = estimatedHours,
        actualHours = actualHours,
        createdAt = createdAt,
        completedAt = completedAt,
        rarityColor = rarityColor,
        isInstantGate = isInstantGate,
        description = description,
        trackId = trackId,
        scheduleBlockId = scheduleBlockId,
        qualityScore = qualityScore,
        effectiveHours = effectiveHours
    )

    companion object {
        fun fromDomain(domain: Mission): MissionEntity = MissionEntity(
            id = domain.id,
            title = domain.title,
            track = domain.track,
            rarity = domain.rarity,
            skillId = domain.skillId,
            skillName = domain.skillName,
            xpReward = domain.xpReward,
            powerScore = domain.powerScore,
            status = domain.status,
            dungeonId = domain.dungeonId,
            estimatedHours = domain.estimatedHours,
            actualHours = domain.actualHours,
            createdAt = domain.createdAt,
            completedAt = domain.completedAt,
            rarityColor = domain.rarityColor,
            isInstantGate = domain.isInstantGate,
            description = domain.description,
            trackId = domain.trackId,
            scheduleBlockId = domain.scheduleBlockId,
            qualityScore = domain.qualityScore,
            effectiveHours = domain.effectiveHours
        )
    }
}
