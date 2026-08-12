package com.axiom.app.core

object FeatureFlags {
    const val AI_FEATURES_ENABLED = true
    var FORCE_OFFLINE_MODE = false

    /**
     * WP-104 SEC-104-001: master switch for the direct client→Gemini egress path.
     * DISABLED BY DEFAULT and fail-closed: the normal production/default build must
     * not silently call Gemini directly. The direct BYO-key path is only reachable
     * when this is explicitly flipped to true for controlled testing. A full
     * server/App Check gateway migration is deferred to a later work packet.
     */
    const val DIRECT_GEMINI_EGRESS_ENABLED = false

    /** Master switch for the Premium purchase flow. Keep false during
     *  the free viral-growth phase — no real billing SDK is connected
     *  yet (see PremiumViewModel.kt). Flip to true only after Cafe
     *  Bazaar / Myket IAB SDK is wired to real purchase confirmation. */
    const val PREMIUM_PURCHASE_ENABLED = false
}
