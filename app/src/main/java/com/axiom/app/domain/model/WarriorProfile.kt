package com.axiom.app.domain.model

data class WarriorProfile(
    val id: String = "default",
    val codename: String,
    val oneLineThesis: String,
    val rareProfileDescription: String,
    val createdAt: Long = System.currentTimeMillis()
)
