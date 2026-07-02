package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.MajorMilestone

@Entity(tableName = "major_milestones")
data class MajorMilestoneEntity(
    @PrimaryKey val id: String,
    val label: String,
    val targetDate: Long,
    val description: String
) {
    fun toDomain(): MajorMilestone = MajorMilestone(
        id = id,
        label = label,
        targetDate = targetDate,
        description = description
    )

    companion object {
        fun fromDomain(domain: MajorMilestone): MajorMilestoneEntity = MajorMilestoneEntity(
            id = domain.id,
            label = domain.label,
            targetDate = domain.targetDate,
            description = domain.description
        )
    }
}
