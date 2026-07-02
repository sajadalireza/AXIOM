package com.axiom.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════
// AXIOM SHAPE SYSTEM
// Rule: max 8dp corner radius everywhere — no exceptions.
// Aesthetic: classified military OS, flat surfaces, no blobs.
// ═══════════════════════════════════════════════════════════════

val AwakenShapes = Shapes(
    // Small: input chips, badges, small tags
    small  = RoundedCornerShape(6.dp),

    // Medium: cards, bottom sheets, dialogs
    medium = RoundedCornerShape(13.dp),

    // Large: full-screen sheets
    large  = RoundedCornerShape(20.dp)
)

// ─── Convenience aliases for direct Canvas / Modifier use ───────

/** 6dp — used for badges, chips, inline tags */
val ShapeSmall = RoundedCornerShape(6.dp)

/** 13dp — used for cards, panels, toast containers */
val ShapeMedium = RoundedCornerShape(13.dp)

/** 20dp — used for full-width sheets / navigation bars */
val ShapeLarge  = RoundedCornerShape(20.dp)
