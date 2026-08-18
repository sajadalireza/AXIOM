package com.axiom.app.domain.firstwin

/**
 * Bounds completion-receipt session correlation to actual First-Win completions only.
 * Legacy First-Win callers may omit the session id; generic missions can never claim one.
 */
object FirstWinCompletionLink {
    fun resolveSessionId(isFirstWin: Boolean, candidateSessionId: String?): String? {
        require(candidateSessionId == null || candidateSessionId.isNotBlank()) {
            "candidateSessionId must be null or non-blank"
        }
        return if (isFirstWin) candidateSessionId else null
    }
}
