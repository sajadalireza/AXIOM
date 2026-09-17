package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.core.localization.AxiomDateFormatter
import com.axiom.app.domain.model.Hunter
import com.axiom.app.ui.theme.*

/**
 * Layer 3 — PROGRESS: truthful G3 execution and hunter level progression.
 * Visual anatomy intentionally follows the V3 Pixel Master while keeping all
 * displayed values bound to real runtime state.
 */
@Composable
fun HomeProgressCard(
    hunter: Hunter,
    activeMissionsCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val progress = hunter.progressPercent.coerceIn(0f, 1f)
    val isFa = java.util.Locale.getDefault().language == "fa"
    val fontScale = LocalDensity.current.fontScale

    val levelStr = if (isFa) AxiomDateFormatter.toPersianDigits(hunter.level.toString()) else hunter.level.toString()
    val percentInt = (progress * 100).toInt()
    val percentStr = if (isFa) AxiomDateFormatter.toPersianDigits(percentInt.toString()) else percentInt.toString()
    val activeMissionsStr = if (isFa) AxiomDateFormatter.toPersianDigits(activeMissionsCount.toString()) else activeMissionsCount.toString()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("home_progress_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface.copy(alpha = 0.90f)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    colors.legendaryGold.copy(alpha = 0.48f),
                    colors.systemGreen.copy(alpha = 0.28f),
                    colors.borderFaint.copy(alpha = 0.16f)
                )
            )
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.bg_mountain_monolith),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.14f),
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                colors.shadowSurface.copy(alpha = 0.42f),
                                colors.shadowSurface.copy(alpha = 0.74f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_progress_header),
                        fontFamily = Outfit,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        letterSpacing = 0.4.sp
                    )
                    Text(
                        text = stringResource(R.string.home_progress_level_format, levelStr),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.legendaryGold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val ringSize = if (fontScale > 1.3f) 60.dp else 54.dp
                    Box(
                        modifier = Modifier.size(ringSize),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val stroke = 3.dp.toPx()
                            drawCircle(
                                color = colors.borderFaint.copy(alpha = 0.28f),
                                style = Stroke(width = stroke)
                            )
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(colors.legendaryGold, colors.systemGreen, colors.legendaryGold)
                                ),
                                startAngle = -90f,
                                sweepAngle = progress * 360f,
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = levelStr,
                                fontFamily = Outfit,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = stringResource(R.string.home_hunter_level_label),
                                fontFamily = JetBrainsMono,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.legendaryGold
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.home_progress_missions_label),
                                    fontFamily = Outfit,
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = stringResource(R.string.home_progress_missions_active, activeMissionsStr),
                                    fontFamily = Outfit,
                                    fontSize = if (fontScale > 1.3f) 12.sp else 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }
                            Text(
                                text = "$percentStr%",
                                fontFamily = Outfit,
                                fontSize = if (fontScale > 1.3f) 16.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.systemGreen
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(colors.dimSurface.copy(alpha = 0.85f))
                        ) {
                            if (progress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(colors.systemGreen, colors.legendaryGold)
                                            )
                                        )
                                )
                            }
                        }

                        Text(
                            text = stringResource(R.string.home_progress_level_label),
                            fontFamily = Outfit,
                            fontSize = 9.sp,
                            color = colors.textDim
                        )
                    }
                }
            }
        }
    }
}
