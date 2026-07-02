package com.axiom.app.domain.model

data class KeyRelationship(
    val id: String,
    val label: String,
    val category: String, // e.g. "Mentor", "Peer", "Buyer", "Professor"
    val lastInteractionAt: Long?,
    val preparedTalkingPoint: String
)
