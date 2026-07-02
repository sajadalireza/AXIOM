package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.IronRule
import com.axiom.app.domain.model.LinkedSignalType

@Entity(tableName = "iron_rules")
data class IronRuleEntity(
    @PrimaryKey val id: String,
    val orderIndex: Int,
    val ruleText: String,
    val isAutomatable: Boolean,
    val linkedSignalType: String = "NONE",
    val linkedKpiId: String? = null
) {
    fun toDomain(): IronRule = IronRule(
        id = id,
        orderIndex = orderIndex,
        ruleText = ruleText,
        isAutomatable = isAutomatable,
        linkedSignalType = try { LinkedSignalType.valueOf(linkedSignalType) } catch (e: Exception) { LinkedSignalType.NONE },
        linkedKpiId = linkedKpiId
    )

    companion object {
        fun fromDomain(domain: IronRule): IronRuleEntity = IronRuleEntity(
            id = domain.id,
            orderIndex = domain.orderIndex,
            ruleText = domain.ruleText,
            isAutomatable = domain.isAutomatable,
            linkedSignalType = domain.linkedSignalType.name,
            linkedKpiId = domain.linkedKpiId
        )
    }
}
