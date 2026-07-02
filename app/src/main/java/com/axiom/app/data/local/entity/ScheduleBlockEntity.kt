package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.ScheduleBlock

@Entity(tableName = "schedule_blocks")
data class ScheduleBlockEntity(
    @PrimaryKey val id: String,
    val trackId: String?,
    val startTime: String,
    val title: String,
    val actionDescription: String,
    val tag: String,
    val recurrence: String,
    val isNonNegotiable: Boolean
) {
    fun toDomain(): ScheduleBlock = ScheduleBlock(
        id = id,
        trackId = trackId,
        startTime = startTime,
        title = title,
        actionDescription = actionDescription,
        tag = tag,
        recurrence = recurrence,
        isNonNegotiable = isNonNegotiable
    )

    companion object {
        fun fromDomain(domain: ScheduleBlock): ScheduleBlockEntity = ScheduleBlockEntity(
            id = domain.id,
            trackId = domain.trackId,
            startTime = domain.startTime,
            title = domain.title,
            actionDescription = domain.actionDescription,
            tag = domain.tag,
            recurrence = domain.recurrence,
            isNonNegotiable = domain.isNonNegotiable
        )
    }
}
