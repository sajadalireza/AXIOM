package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.MuscleGroup

@Entity(tableName = "muscle_groups")
data class MuscleGroupEntity(
    @PrimaryKey val id: String,           // e.g. "chest", "back"
    val displayName: String,
    val strengthScore: Int = 0,           // 0-100, grows with training
    val lastTrainedTimestamp: Long? = null,
    val freshnessPercent: Int = 100       // 0-100, decays over time
) {
    fun toDomain(): MuscleGroup = MuscleGroup(
        id = id,
        displayName = displayName,
        strengthScore = strengthScore,
        lastTrainedTimestamp = lastTrainedTimestamp,
        freshnessPercent = freshnessPercent
    )

    companion object {
        fun fromDomain(domain: MuscleGroup): MuscleGroupEntity = MuscleGroupEntity(
            id = domain.id,
            displayName = domain.displayName,
            strengthScore = domain.strengthScore,
            lastTrainedTimestamp = domain.lastTrainedTimestamp,
            freshnessPercent = domain.freshnessPercent
        )
    }
}
