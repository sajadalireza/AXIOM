package com.axiom.app.ci

import org.junit.Assert.fail
import org.junit.Test

/**
 * WP-105 CI RED-PROOF — TEMPORARY, MUST NEVER MERGE.
 *
 * This test exists only to prove the CI "Unit Tests" check turns the pull request
 * red on a deliberately failing unit test. It lives solely on the ephemeral
 * codex/wp-105-ci-red-proof branch and is deleted after evidence capture.
 */
class CiRedProofTest {
    @Test
    fun ci_red_proof_deliberate_failure() {
        fail("WP-105 deliberate CI red-proof failure — proves CI fails on a red test. Do not merge.")
    }
}
