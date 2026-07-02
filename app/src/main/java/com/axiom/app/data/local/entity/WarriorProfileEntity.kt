package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.WarriorProfile

@Entity(tableName = "warrior_profiles")
data class WarriorProfileEntity(
    @PrimaryKey val id: String,
    val codename: String,
    val oneLineThesis: String,
    val rareProfileDescription: String,
    val createdAt: Long
) {
    fun toDomain(): WarriorProfile = WarriorProfile(
        id = id,
        codename = codename,
        oneLineThesis = oneLineThesis,
        rareProfileDescription = rareProfileDescription,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(domain: WarriorProfile): WarriorProfileEntity = WarriorProfileEntity(
            id = domain.id,
            codename = domain.codename,
            oneLineThesis = domain.oneLineThesis,
            rareProfileDescription = domain.rareProfileDescription,
            createdAt = domain.createdAt
        )
    }
}
