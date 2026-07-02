package com.axiom.app.presentation.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.presentation.shadow.ShadowArmyScreen
import com.axiom.app.presentation.profile.CharacterStatsScreen
import com.axiom.app.ui.components.ScanlineOverlay
import com.axiom.app.ui.theme.*

@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ArchiveTab.ShadowArmy) }
    val colors = LocalAxiomColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP CHIP TOGGLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(colors.shadowSurface, RoundedCornerShape(4.dp))
                    .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Shadow Army Chip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (selectedTab == ArchiveTab.ShadowArmy) colors.systemGreen else Color.Transparent)
                        .clickable { selectedTab = ArchiveTab.ShadowArmy }
                        .testTag("chip_shadow_army"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.archive_shadow_army),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (selectedTab == ArchiveTab.ShadowArmy) colors.voidBlack else colors.textSecondary
                    )
                }

                // Stats Chip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (selectedTab == ArchiveTab.Stats) colors.systemGreen else Color.Transparent)
                        .clickable { selectedTab = ArchiveTab.Stats }
                        .testTag("chip_character_stats"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.archive_character_metrics),
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (selectedTab == ArchiveTab.Stats) colors.voidBlack else colors.textSecondary
                    )
                }
            }

            // INLINE SCREEN RENDERER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    ArchiveTab.ShadowArmy -> {
                        ShadowArmyScreen()
                    }
                    ArchiveTab.Stats -> {
                        CharacterStatsScreen()
                    }
                }
            }
        }
        ScanlineOverlay()
    }
}

enum class ArchiveTab {
    ShadowArmy, Stats
}
