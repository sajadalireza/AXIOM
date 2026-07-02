package com.axiom.app.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.axiom.app.domain.model.WeeklyChallenge
import com.axiom.app.ui.components.WeeklyChallengeCard

@Composable
fun WeeklyChallengeSection(
    challenges: List<WeeklyChallenge>,
    allClaimed: Boolean,
    onClaimBonus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        WeeklyChallengeCard(
            challenges = challenges,
            allClaimed = allClaimed,
            onClaimBonus = onClaimBonus,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
