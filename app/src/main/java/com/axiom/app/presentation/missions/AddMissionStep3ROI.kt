package com.axiom.app.presentation.missions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.ui.components.HolographicCard
import com.axiom.app.ui.components.RarityBadge
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.theme.*

@Composable
fun AddMissionStep3ROI(
    estimatedHoursStr: String,
    onEstimatedHoursChange: (String) -> Unit,
    marketDemand: Float,
    onMarketDemandChange: (Float) -> Unit,
    leverage: Float,
    onLeverageChange: (Float) -> Unit,
    complexity: Float,
    onComplexityChange: (Float) -> Unit,
    selectedRarityState: String?,
    onRaritySelect: (String?) -> Unit,
    liveRarity: String,
    currentPowerScore: Float,
    estimatedXP: Int,
    successChance: Int,
    modifier: Modifier = Modifier
) {
    val rarities = listOf("COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY")

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // TARGET RARITY SELECTOR
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.add_mission_target_rarity),
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = TextDim,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rarities.forEach { rarity ->
                    val isColorSelected = liveRarity.uppercase() == rarity
                    val color = rarityColorMap[rarity] ?: CommonGray
                    HolographicCard(
                        modifier = Modifier
                            .width(114.dp)
                            .height(44.dp)
                            .clickable {
                                onRaritySelect(rarity)
                                // Adjust slider presets backwards to trigger clicked rarity dynamically
                                when (rarity) {
                                    "COMMON" -> {
                                        onMarketDemandChange(1f)
                                        onLeverageChange(1f)
                                        onComplexityChange(1f)
                                    }
                                    "UNCOMMON" -> {
                                        onMarketDemandChange(3f)
                                        onLeverageChange(3f)
                                        onComplexityChange(3f)
                                    }
                                    "RARE" -> {
                                        onMarketDemandChange(5f)
                                        onLeverageChange(5f)
                                        onComplexityChange(5f)
                                    }
                                    "EPIC" -> {
                                        onMarketDemandChange(8f)
                                        onLeverageChange(8f)
                                        onComplexityChange(8f)
                                    }
                                    "LEGENDARY" -> {
                                        onMarketDemandChange(10f)
                                        onLeverageChange(10f)
                                        onComplexityChange(10f)
                                    }
                                }
                            }
                            .testTag("rarity_chip_$rarity"),
                        accentColor = if (isColorSelected) color else BorderFaint,
                        glowEnabled = isColorSelected
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = rarity,
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isColorSelected) color else TextDim
                            )
                        }
                    }
                }
            }
        }

        // ESTIMATED HOURS (TerminalTextField)
        TerminalTextField(
            value = estimatedHoursStr,
            onValueChange = onEstimatedHoursChange,
            label = "ESTIMATED HOURS",
            placeholder = { Text("E.g. 2.5", color = TextDim, fontFamily = JetBrainsMono, fontSize = 14.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("field_mission_hours")
        )

        // SLIDERS
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Slider 1: Market Demand
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.add_mission_market_value),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${marketDemand.toInt()}/10",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = SystemGreen
                    )
                }
                Slider(
                    value = marketDemand,
                    onValueChange = {
                        onMarketDemandChange(it)
                        onRaritySelect(null)
                    },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        activeTrackColor = SystemGreen,
                        thumbColor = SystemGreen,
                        inactiveTrackColor = BorderFaint
                    )
                )
            }

            // Slider 2: Growth Potential (Leverage)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.add_mission_growth),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${leverage.toInt()}/10",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = SystemGreen
                    )
                }
                Slider(
                    value = leverage,
                    onValueChange = {
                        onLeverageChange(it)
                        onRaritySelect(null)
                    },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        activeTrackColor = SystemGreen,
                        thumbColor = SystemGreen,
                        inactiveTrackColor = BorderFaint
                    )
                )
            }

            // Slider 3: Mental Load (Complexity)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.add_mission_mental_load),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${complexity.toInt()}/10",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = PenaltyRed
                    )
                }
                Slider(
                    value = complexity,
                    onValueChange = {
                        onComplexityChange(it)
                        onRaritySelect(null)
                    },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        activeTrackColor = PenaltyRed,
                        thumbColor = PenaltyRed,
                        inactiveTrackColor = BorderFaint
                    )
                )
            }
        }

        // Live Rarity Color Lookup
        val liveRarityUpper = liveRarity.uppercase()
        val targetRarityColor = rarityColorMap[liveRarityUpper] ?: CommonGray
        val animatedRarityColor by animateColorAsState(
            targetValue = targetRarityColor,
            animationSpec = tween(300),
            label = "live_rarity_color"
        )

        // Live Power Score Anim
        val animatedPowerScore by animateFloatAsState(
            targetValue = currentPowerScore,
            animationSpec = tween(400),
            label = "power_score_anim"
        )

        // Screen Entrance Scale
        var isLoaded by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isLoaded = true
        }
        val entranceScale by animateFloatAsState(
            targetValue = if (isLoaded) 1.0f else 0.95f,
            animationSpec = tween(400, easing = EaseOutCubic),
            label = "preview_entrance_scale"
        )

        // SYSTEM ANALYSIS PANEL (LIVE PREVIEW)
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = entranceScale
                    scaleY = entranceScale
                }
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(ShadowSurface)
                .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
        ) {
            // Left border: 3dp in rarityColor via Canvas to sit exactly inside
            Canvas(modifier = Modifier.matchParentSize()) {
                val canvasHeight = this.size.height
                drawRect(
                    color = animatedRarityColor,
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(3.dp.toPx(), canvasHeight)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 19.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RarityBadge(rarity = liveRarity)
                    Text(
                        text = "[+$estimatedXP XP]",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = SystemGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = stringResource(R.string.add_mission_power_score),
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = TextDim,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = String.format(java.util.Locale.US, "%.2f", animatedPowerScore),
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = LegendaryGold
                )

                Text(
                    text = stringResource(R.string.add_mission_success_chance, successChance),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
