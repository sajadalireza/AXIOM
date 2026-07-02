package com.axiom.app.presentation.bodymap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.MuscleGroup
import com.axiom.app.ui.LocalizationUtils
import com.axiom.app.ui.theme.*
import java.util.Locale

@Composable
fun RecoveryTimelineBar(
    muscles: List<MuscleGroup>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isFa = Locale.getDefault().language == "fa"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isFa) "خط زمانی و وضعیت ریکاوری عضلانی" else "RECOVERY TIMELINE & MUSCLE FRESHNESS",
            color = LegendaryGold,
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(muscles) { muscle ->
                val freshness = muscle.freshnessPercent
                val (statusColor, statusLabel) = when {
                    freshness >= 90 -> Color(0xFF00FF7F) to (if (isFa) "ریکاوری کامل" else "Recovered")
                    freshness >= 60 -> Color(0xFFFFCC00) to (if (isFa) "خستگی متوسط" else "Fatigued")
                    else -> Color(0xFFFF3333) to (if (isFa) "کوفتگی شدید" else "Sore")
                }

                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(95.dp),
                    colors = CardDefaults.cardColors(containerColor = ShadowSurface),
                    border = BorderStroke(1.dp, BorderFaint)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = LocalizationUtils.getLocalizedSkillName(muscle.displayName, context),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusColor)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = statusLabel,
                                    fontSize = 10.sp,
                                    color = statusColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$freshness%",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                               )
                            }

                            // Linear indicator
                            LinearProgressIndicator(
                                progress = { freshness / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = statusColor,
                                trackColor = BorderFaint
                            )
                        }
                    }
                }
            }
        }
    }
}
