package com.axiom.app.domain.model

enum class LinkedSignalType {
    NONE, CUSTOM_KPI, SLEEP_TARGET, FIRST_TWO_HOURS
}

data class IronRule(
    val id: String,
    val orderIndex: Int,
    val ruleText: String,
    val isAutomatable: Boolean,
    val linkedSignalType: LinkedSignalType = LinkedSignalType.NONE,
    val linkedKpiId: String? = null
)
