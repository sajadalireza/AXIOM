package com.axiom.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.theme.AwakenTheme

@Composable
fun MissionCardCompact(
    mission: Mission,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dungeonName: String? = null,
    stageLabel: String? = null
) {
    MissionCard(
        mission = mission,
        onClick = onClick,
        modifier = modifier,
        cardSize = CardSize.COMPACT,
        dungeonName = dungeonName,
        stageLabel = stageLabel
    )
}

@Preview
@Composable
fun MissionCardCompactDelegatedPreview() {
    AwakenTheme {
        MissionCardCompact(
            mission = Mission(
                id = "test_id",
                title = "Defeat the Hobgoblin Shaman",
                track = "strength",
                rarity = "EPIC",
                skillId = "strength_shaman",
                skillName = "Rage Strike",
                xpReward = 120,
                powerScore = 45.5f,
                status = "ACTIVE",
                dungeonId = null,
                estimatedHours = 2f,
                actualHours = null,
                createdAt = System.currentTimeMillis() - 120000L,
                completedAt = null,
                rarityColor = 0xFFFF007FL,
                isInstantGate = false,
                description = "Locate and eliminate the shaman leading the raid squad."
            ),
            onClick = {}
        )
    }
}
