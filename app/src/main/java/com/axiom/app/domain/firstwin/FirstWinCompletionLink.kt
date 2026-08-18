package com.axiom.app.domain.firstwin

/** WP-207 RED — bounds session correlation to actual First-Win completions only. */
object FirstWinCompletionLink {
    fun resolveSessionId(isFirstWin: Boolean, candidateSessionId: String?): String? =
        TODO("WP-207 RED")
}
