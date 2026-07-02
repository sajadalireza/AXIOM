package com.axiom.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Dungeon
import com.axiom.app.ui.theme.*

enum class DungeonCardSize { FULL, COMPACT }

@Composable
fun DungeonCard(
    dungeon: Dungeon,
    modifier: Modifier = Modifier,
    size: DungeonCardSize = DungeonCardSize.FULL,
    onEnter: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    // Determine rarity color using design system tokens
    val rarityColor = when (dungeon.rarity.lowercase()) {
        "normal" -> CommonGray
        "rare" -> RareBlue
        "epic" -> EpicPurple
        "legendary" -> LegendaryGold
        "mythic" -> PenaltyRed
        else -> CommonGray
    }

    // Pulsing effect for BOSS STAGE
    val infiniteTransition = rememberInfiniteTransition(label = "boss_pulse_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "boss_pulse_alpha"
    )

    val progress = dungeon.progressPercent.coerceIn(0f, 1f)

    val baseModifier = if (size == DungeonCardSize.COMPACT) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Box(
        modifier = baseModifier
            .fillMaxWidth()
            .soloLevelingCard(
                accentColor = BorderFaint,
                bevel = 12f,
                borderWidth = 1f,
                glowRadius = 0f,
                showSideNotches = false,
                backgroundColor = ShadowSurface
            )
            .clip(SoloLevelingBeveledShape(bevel = 12f, showSideNotches = false))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left rarity border
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(rarityColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (size == DungeonCardSize.FULL) {
                    // Name + RarityBadge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dungeon.name,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RarityBadge(rarity = dungeon.rarity)
                    }

                    // Description
                    if (dungeon.description.isNotBlank()) {
                        Text(
                            text = dungeon.description,
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    // Stage tracker dots: "● ● ○ ○ ○"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.dungeon_card_stages),
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = TextDim,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (i in 0 until dungeon.totalStages) {
                                val isCompleted = i < dungeon.completedStages
                                Text(
                                    text = "●",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 14.sp,
                                    color = if (isCompleted) SystemGreen else BorderFaint,
                                    modifier = Modifier.testTag("dot_${dungeon.id}_$i")
                                )
                            }
                        }
                    }

                    // Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { dungeon.progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = SystemGreen,
                            trackColor = BorderFaint,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dungeon_card_ready, (dungeon.progressPercent * 100).toInt()),
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = TextDim
                            )
                            Text(
                                text = stringResource(R.string.dungeon_card_stages_count, dungeon.completedStages, dungeon.totalStages),
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = TextDim
                            )
                        }
                    }

                    // Boss Stage Alert (If on boss stage)
                    if (dungeon.isOnBossStage) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(LegendaryGold.copy(alpha = 0.1f))
                                .border(1.dp, LegendaryGold.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .alpha(pulseAlpha),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.dungeon_card_boss_stage),
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = LegendaryGold
                            )
                        }
                    }

                    // ENTER DUNGEON Button
                    Button(
                        onClick = onEnter,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SystemGreen,
                            contentColor = VoidBlack
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("btn_enter_dungeon_${dungeon.id}")
                    ) {
                        Text(
                            text = if (dungeon.isCompleted) "VIEW DUNGEON RAID" else "ENTER DUNGEON",
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    // COMPACT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dungeon.name,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )

                        if (dungeon.isOnBossStage) {
                            Text(
                                text = "⚔ BOSS STAGE",
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = LegendaryGold.copy(alpha = pulseAlpha)
                            )
                        } else {
                            Text(
                                text = "Stage ${dungeon.completedStages}/${dungeon.totalStages}",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = TextDim
                            )
                        }
                    }

                    // Dungeon description if present
                    if (dungeon.description.isNotEmpty()) {
                        Text(
                            text = dungeon.description,
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(DimSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(SystemGreen)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DungeonCardPreview() {
    AwakenTheme {
        DungeonCard(
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
            onEnter = {}
        )
    }
}
