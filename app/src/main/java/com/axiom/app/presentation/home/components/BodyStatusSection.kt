package com.axiom.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.theme.*

@Composable
fun BodyStatusSection(
    muscles: List<MuscleGroup>,
    onNavigateToBodyMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val now = System.currentTimeMillis()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Combat Readiness Widget (Circular Progress & Stats)
        CombatReadinessWidget(
            muscles = muscles,
            onNavigateToBodyMap = onNavigateToBodyMap,
            modifier = Modifier.fillMaxWidth()
        )

        // Muscle Recovery Tactical Mini-Grid
        if (muscles.isNotEmpty()) {
            Text(
                text = "BIOLOGICAL HARDWARE STATUS",
                fontFamily = FiraCode,
                fontSize = 11.sp,
                color = colors.textDim,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            val chunkedMuscles = muscles.chunked(3)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chunkedMuscles.forEach { rowMuscles ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowMuscles.forEach { muscle ->
                            val freshness = com.axiom.app.domain.engine.MuscleEngine.calculateFreshness(
                                muscle.lastTrainedAt,
                                muscle.recoveryWindowHours,
                                now
                            )
                            val freshnessColor = when {
                                freshness >= 80f -> colors.systemGreen
                                freshness >= 40f -> colors.leverageDepth
                                else -> colors.penaltyRed
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(colors.dimSurface)
                                    .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = muscle.displayName.uppercase(),
                                        fontFamily = FiraCode,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        maxLines = 1
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${freshness.toInt()}%",
                                            fontFamily = FiraCode,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = freshnessColor
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(freshnessColor)
                                        )
                                    }
                                }
                            }
                        }
                        if (rowMuscles.size < 3) {
                            repeat(3 - rowMuscles.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CombatReadinessWidget(
    muscles: List<MuscleGroup>,
    onNavigateToBodyMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFa = java.util.Locale.getDefault().language == "fa"
    val now = System.currentTimeMillis()

    val averageFreshness = if (muscles.isEmpty()) 100f else {
        muscles.map { com.axiom.app.domain.engine.MuscleEngine.calculateFreshness(it.lastTrainedAt, it.recoveryWindowHours, now) }.average().toFloat()
    }

    val freshestGroup = muscles.maxByOrNull { com.axiom.app.domain.engine.MuscleEngine.calculateFreshness(it.lastTrainedAt, it.recoveryWindowHours, now) }
    val fatiguedGroup = muscles.minByOrNull { com.axiom.app.domain.engine.MuscleEngine.calculateFreshness(it.lastTrainedAt, it.recoveryWindowHours, now) }

    val freshestName = freshestGroup?.displayName ?: (if (isFa) "عضلات" else "Muscles")
    val freshestFreshness = freshestGroup?.let { com.axiom.app.domain.engine.MuscleEngine.calculateFreshness(it.lastTrainedAt, it.recoveryWindowHours, now) } ?: 100f

    val fatiguedName = fatiguedGroup?.displayName ?: (if (isFa) "هیچ‌کدام" else "None")
    val fatiguedFreshness = fatiguedGroup?.let { com.axiom.app.domain.engine.MuscleEngine.calculateFreshness(it.lastTrainedAt, it.recoveryWindowHours, now) } ?: 100f

    val lastTrained = fatiguedGroup?.lastTrainedAt
    val hoursLeft = if (lastTrained != null) {
        val passed = (now - lastTrained).toFloat() / 3600000f
        (fatiguedGroup.recoveryWindowHours - passed).coerceAtLeast(0f)
    } else {
        0f
    }
    val daysLeft = kotlin.math.ceil(hoursLeft / 24f).toInt()

    val colors = LocalAxiomColors.current
    val systemGreen = colors.systemGreen
    val warnOrange = colors.leverageDepth
    val penaltyRed = colors.penaltyRed

    val readinessColor = when {
        averageFreshness >= 80f -> systemGreen
        averageFreshness >= 40f -> warnOrange
        else -> penaltyRed
    }

    val summarySentence = stringResource(
        R.string.home_combat_readiness_summary,
        freshestName,
        freshestFreshness.toInt(),
        fatiguedName,
        daysLeft,
        fatiguedFreshness.toInt()
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNavigateToBodyMap() }
            .testTag("combat_readiness_widget"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LegendaryGold.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { averageFreshness / 100f },
                    color = readinessColor,
                    trackColor = colors.voidBlack,
                    strokeWidth = 6.dp,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "${averageFreshness.toInt()}%",
                    color = colors.textPrimary,
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isFa) "آمادگی رزمی فیزیکی (COMBAT READINESS)" else "PHYSICAL COMBAT READINESS",
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Bold,
                    color = LegendaryGold,
                    fontSize = 11.sp
                )

                Text(
                    text = summarySentence,
                    fontFamily = Inter,
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isFa) "▶ ورود به اسکنر مکانیکی عضلات" else "▶ ACCESS MUSCLE CORE SCAN",
                        fontFamily = FiraCode,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = readinessColor
                    )
                }
            }
        }
    }
}
