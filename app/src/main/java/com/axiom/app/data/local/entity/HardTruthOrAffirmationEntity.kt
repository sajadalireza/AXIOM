package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.CalibrationType
import com.axiom.app.domain.model.HardTruthOrAffirmation

@Entity(tableName = "hard_truths_affirmations")
data class HardTruthOrAffirmationEntity(
    @PrimaryKey val id: String,
    val type: String, // Stored as "TRUTH" or "AFFIRMATION"
    val text: String,
    val orderIndex: Int
) {
    fun toDomain(): HardTruthOrAffirmation = HardTruthOrAffirmation(
        id = id,
        type = try { CalibrationType.valueOf(type) } catch (e: Exception) { CalibrationType.TRUTH },
        text = text,
        orderIndex = orderIndex
    )

    companion object {
        fun fromDomain(domain: HardTruthOrAffirmation): HardTruthOrAffirmationEntity = HardTruthOrAffirmationEntity(
            id = domain.id,
            type = domain.type.name,
            text = domain.text,
            orderIndex = domain.orderIndex
        )
    }
}
