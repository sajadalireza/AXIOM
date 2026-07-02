package com.axiom.app.domain.repository

import com.axiom.app.data.remote.LeagueScoreRow

interface LeagueRepository {
    suspend fun submitScore(rarity: String, xp: Int, hunterName: String, hunterRank: String): Boolean
    suspend fun getLeaderboard(): List<LeagueScoreRow>
}
