package com.axiom.app.domain.firstwin.control

/**
 * WP-208 — variant assignment for First-Win vertical slice.
 *
 * [TREATMENT]: receives the G2 First-Win vertical slice flow.
 * [CONTROL]: receives the bounded legacy onboarding and first-mission flow.
 */
enum class FirstWinVariant {
    TREATMENT,
    CONTROL,
}
