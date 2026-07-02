package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val name: String,
    val color: Long,
    val icon: String,
    val description: String
) {
    fun toDomain(): Track = Track(
        id = id,
        name = name,
        color = color,
        icon = icon,
        description = description
    )

    companion object {
        fun fromDomain(domain: Track): TrackEntity = TrackEntity(
            id = domain.id,
            name = domain.name,
            color = domain.color,
            icon = domain.icon,
            description = domain.description
        )
    }
}
