package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.KeyRelationship

@Entity(tableName = "key_relationships")
data class KeyRelationshipEntity(
    @PrimaryKey val id: String,
    val label: String,
    val category: String,
    val lastInteractionAt: Long?,
    val preparedTalkingPoint: String
) {
    fun toDomain(): KeyRelationship = KeyRelationship(
        id = id,
        label = label,
        category = category,
        lastInteractionAt = lastInteractionAt,
        preparedTalkingPoint = preparedTalkingPoint
    )

    companion object {
        fun fromDomain(domain: KeyRelationship): KeyRelationshipEntity = KeyRelationshipEntity(
            id = domain.id,
            label = domain.label,
            category = domain.category,
            lastInteractionAt = domain.lastInteractionAt,
            preparedTalkingPoint = domain.preparedTalkingPoint
        )
    }
}
