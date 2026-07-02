package com.axiom.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.ui.theme.AwakenTheme

@Composable
fun DungeonCardCompact(
    dungeon: Dungeon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DungeonCard(
        dungeon = dungeon,
        modifier = modifier,
        size = DungeonCardSize.COMPACT,
        onClick = onClick
    )
}

@Preview
@Composable
fun DungeonCardCompactPreview() {
    AwakenTheme {
        DungeonCardCompact(
            dungeon = Dungeon(
                id = "test_dungeon",
                name = "C-Rank Gate: Instance #12",
                description = "Clear the lower floor of magical beasts to close the rift.",
                rarity = "rare",
                totalStages = 5,
                completedStages = 2,
                isBossDefeated = false,
                createdAt = System.currentTimeMillis(),
                completedAt = null,
                stageDescriptions = "Entrance||Lower Lobby||Central Hall||Throne Room||Boss Lair"
            ),
            onClick = {}
        )
    }
}
