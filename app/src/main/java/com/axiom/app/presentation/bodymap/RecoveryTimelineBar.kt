package com.axiom.app.presentation.bodymap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.theme.AxiomBorder
import com.axiom.app.ui.theme.AxiomRadius
import com.axiom.app.ui.theme.AxiomSpacing
import com.axiom.app.ui.theme.FiraCode
import com.axiom.app.ui.theme.Inter
import com.axiom.app.ui.theme.LocalAxiomColors

@Composable
fun RecoveryTimelineBar(
    muscles: List<MuscleGroup>,
    modifier: Modifier = Modifier,
    onMuscleClick: ((MuscleGroup) -> Unit)? = null
) {
    val context = LocalContext.current
    val colors = LocalAxiomColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AxiomSpacing.s)
    ) {
        Text(
            text = stringResource(R.string.bodymap_timeline_header),
            color = colors.legendaryGold,
            fontFamily = FiraCode,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth().testTag("recovery_timeline_row"),
            horizontalArrangement = Arrangement.spacedBy(AxiomSpacing.s),
            contentPadding = PaddingValues(vertical = AxiomSpacing.xs)
        ) {
            items(muscles, key = { it.id }) { muscle ->
                val freshness = muscle.freshnessPercent
                val (statusColor, statusLabel) = when {
                    freshness >= 80 -> colors.systemGreen to stringResource(R.string.bodymap_status_recovered)
                    freshness >= 40 -> colors.legendaryGold to stringResource(R.string.bodymap_status_fatigued)
                    else -> colors.penaltyRed to stringResource(R.string.bodymap_status_sore)
                }

                val localizedName = getLocalizedMuscleName(
                    muscleId = muscle.id,
                    fallbackDisplayName = muscle.displayName,
                    context = context
                )

                val cardDescription = stringResource(
                    R.string.bodymap_a11y_muscle_item,
                    localizedName,
                    freshness,
                    statusLabel
                )

                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(96.dp)
                        .semantics {
                            contentDescription = cardDescription
                        },
                    onClick = { onMuscleClick?.invoke(muscle) },
                    colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
                    border = BorderStroke(AxiomBorder.hairline, colors.borderFaint),
                    shape = RoundedCornerShape(AxiomRadius.m)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AxiomSpacing.s),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = localizedName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = Inter,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(AxiomRadius.xs))
                                    .background(statusColor)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(AxiomSpacing.xxs)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = statusLabel,
                                    fontSize = 10.sp,
                                    fontFamily = Inter,
                                    color = statusColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$freshness%",
                                    fontFamily = FiraCode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }

                            LinearProgressIndicator(
                                progress = { freshness / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(AxiomRadius.xs)),
                                color = statusColor,
                                trackColor = colors.borderFaint
                            )
                        }
                    }
                }
            }
        }
    }
}
