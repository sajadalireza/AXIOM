package com.axiom.app.core

object FeatureFlags {
    const val AI_FEATURES_ENABLED = true
    var FORCE_OFFLINE_MODE = false

    /** Master switch for the Premium purchase flow. Keep false during
     *  the free viral-growth phase — no real billing SDK is connected
     *  yet (see PremiumViewModel.kt). Flip to true only after Cafe
     *  Bazaar / Myket IAB SDK is wired to real purchase confirmation. */
    const val PREMIUM_PURCHASE_ENABLED = false
}
