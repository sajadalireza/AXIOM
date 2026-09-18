package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.core.localization.AxiomDateFormatter
import com.axiom.app.ui.HomeUiState
import com.axiom.app.ui.theme.*

/**
 * Layer 2 — TODAY: Truthful G3-safe compact status cards.
 *
 * Conforms to canonical MODULE_DISPOSITION (Gate G3 Core Loop & Data Truth):
 * - Does NOT expose AX-015 biometric tracking or direct Daily Check-in.
 * - Does NOT use legacy fabricated DailyOutcomes defaults or fake inferred states ("Engaged", "Active Focus").
 * - Binds exclusively to 3 truthful G3 Home/Mission metrics:
 *   1. Active missions count
 *   2. Next meaningful action availability
 *   3. Real mission track domain
 * - Adapts responsively at 200% font scale to prevent any text clipping.
 */
@Composable
fun TodayStatusRow(
    state: HomeUiState.Success,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val fontScale = LocalDensity.current.fontScale
    val isFa = java.util.Locale.getDefault().language == "fa"

    val missionsCountStr = if (isFa) {
        AxiomDateFormatter.toPersianDigits(state.activeMissionsCount.toString())
    } else {
        state.activeMissionsCount.toString()
    }

    // Metric 1: Active Missions Count
    val activeLabel = stringResource(R.string.home_today_missions_title)
    val activeVal = stringResource(R.string.home_today_missions_count, missionsCountStr)
    val activeSub = stringResource(R.string.home_today_missions_sub)

    // Metric 2: Next Meaningful Action Availability
    val actionLabel = stringResource(R.string.home_today_action_title)
    val actionVal = if (state.nextBestAction != null) {
        stringResource(R.string.home_today_action_available)
    } else {
        stringResource(R.string.home_today_action_none)
    }
    val actionSub = if (state.nextBestAction != null) {
        stringResource(R.string.home_today_action_sub_ready)
    } else {
        stringResource(R.string.home_today_action_sub_standby)
    }

    // Metric 3: Real Mission Track — rendered in full; only the presentation layer may wrap it
    val trackLabel = stringResource(R.string.home_today_track_title)
    val primaryTrack = state.topMissions.firstOrNull()?.track
    val trackVal = primaryTrack ?: stringResource(R.string.home_today_track_none)
    val trackSub = if (primaryTrack != null) {
        stringResource(R.string.home_today_track_sub_primary)
    } else {
        stringResource(R.string.home_today_track_sub_none)
    }

    // Metric 4: Real Protocol Engagement Status
    val statusLabel = stringResource(R.string.home_today_protocol_title)
    val hasActiveMission = state.topMissions.isNotEmpty()
    val statusVal = if (hasActiveMission) {
        stringResource(R.string.home_today_status_engaged)
    } else {
        stringResource(R.string.home_today_status_standby)
    }
    val statusSub = stringResource(R.string.home_today_status_sub)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_status_section"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_today_header),
                fontFamily = Outfit,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                letterSpacing = 0.sp
            )
            Text(
                text = stringResource(R.string.home_today_subtitle, missionsCountStr),
                fontFamily = Outfit,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary
            )
        }

        if (fontScale > 1.3f) {
            // Adaptive Stacked Layout for High Font Scale (>=130% - 200%)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TodayAdaptiveRowCard(
                    title = activeLabel,
                    value = activeVal,
                    subValue = activeSub,
                    accentColor = colors.systemGreen
                )
                TodayAdaptiveRowCard(
                    title = actionLabel,
                    value = actionVal,
                    subValue = actionSub,
                    accentColor = if (state.nextBestAction != null) colors.legendaryGold else colors.borderFaint
                )
                TodayAdaptiveRowCard(
                    title = trackLabel,
                    value = trackVal,
                    subValue = trackSub,
                    accentColor = colors.borderFaint
                )
                TodayAdaptiveRowCard(
                    title = statusLabel,
                    value = statusVal,
                    subValue = statusSub,
                    accentColor = if (hasActiveMission) colors.systemGreen else colors.borderFaint
                )
            }
        } else {
            // 4 Compact Cards Row matching Pixel Master 4-slot visual rhythm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TodayMetricCard(
                    glyph = "✓",
                    title = activeLabel,
                    value = activeVal,
                    subValue = activeSub,
                    accentColor = colors.systemGreen,
                    modifier = Modifier.weight(1f)
                )
                TodayMetricCard(
                    glyph = "→",
                    title = actionLabel,
                    value = actionVal,
                    subValue = actionSub,
                    accentColor = if (state.nextBestAction != null) colors.legendaryGold else colors.borderFaint,
                    modifier = Modifier.weight(1f)
                )
                TodayMetricCard(
                    glyph = "◌",
                    title = trackLabel,
                    value = trackVal,
                    subValue = trackSub,
                    accentColor = colors.borderFaint,
                    modifier = Modifier.weight(1f)
                )
                TodayMetricCard(
                    glyph = "◎",
                    title = statusLabel,
                    value = statusVal,
                    subValue = statusSub,
                    accentColor = if (hasActiveMission) colors.systemGreen else colors.borderFaint,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TodayMetricCard(
    glyph: String,
    title: String,
    value: String,
    subValue: String,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val cardContentDesc = "$title: $value, $subValue"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colors.shadowSurface.copy(alpha = 0.92f),
            contentColor = colors.textPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = modifier
            .heightIn(min = 72.dp)
            .clearAndSetSemantics {
                contentDescription = cardContentDesc
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 5.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = glyph,
                    fontFamily = Outfit,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
                Text(
                    text = title,
                    fontFamily = JetBrainsMono,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textDim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                fontFamily = Outfit,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subValue,
                fontFamily = Outfit,
                fontSize = 8.sp,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TodayAdaptiveRowCard(
    title: String,
    value: String,
    subValue: String,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val cardContentDesc = "$title: $value, $subValue"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = colors.shadowSurface,
            contentColor = colors.textPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clearAndSetSemantics {
                contentDescription = cardContentDesc
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = title,
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textDim
                )
                Text(
                    text = subValue,
                    fontFamily = Outfit,
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Both sides share the row proportionally so a long truthful value wraps instead
            // of squeezing the label or overflowing the card at high font scale.
            Text(
                text = value,
                fontFamily = Outfit,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.End,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}
