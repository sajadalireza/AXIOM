package com.axiom.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axiom.app.domain.model.IronRuleViolationLog

@Entity(tableName = "iron_rule_violation_logs")
data class IronRuleViolationLogEntity(
    @PrimaryKey val id: String,
    val ruleId: String,
    val date: Long,
    val wasAutomaticallyDetected: Boolean
) {
    fun toDomain(): IronRuleViolationLog = IronRuleViolationLog(
        id = id,
        ruleId = ruleId,
        date = date,
        wasAutomaticallyDetected = wasAutomaticallyDetected
    )

    companion object {
        fun fromDomain(domain: IronRuleViolationLog): IronRuleViolationLogEntity = IronRuleViolationLogEntity(
            id = domain.id,
            ruleId = domain.ruleId,
            date = domain.date,
            wasAutomaticallyDetected = domain.wasAutomaticallyDetected
        )
    }
}
