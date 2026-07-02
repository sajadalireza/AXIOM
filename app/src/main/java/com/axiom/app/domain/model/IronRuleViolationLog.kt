package com.axiom.app.domain.model

data class IronRuleViolationLog(
    val id: String,
    val ruleId: String,
    val date: Long,
    val wasAutomaticallyDetected: Boolean
)
