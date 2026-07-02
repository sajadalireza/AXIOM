package com.axiom.app.domain.model

data class Track(
    val id: String,
    val name: String,
    val color: Long, // ARGB Long
    val icon: String,
    val description: String
)
