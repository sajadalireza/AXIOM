package com.axiom.app.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.ui.HomeNextAction
import com.axiom.app.ui.theme.*

/**
 * Layer 3 — XION: Subordinate, advisory tactical insight card.
 *
 * Requirements:
 * - Visually subordinate to the Next Meaningful Mission primary CTA.
 * - Does NOT fabricate pretend-AI advice or boilerplate directives.
 * - Binds directly to real nextBestAction state, or displays an honest neutral unavailable state.
 * - Low-emphasis dismiss action.
 * - Scales responsively at 200% font scale while remaining subordinate.
 */
@Composable
fun XionInsightCard(
    nextBestAction: HomeNextAction?,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val fontScale = LocalDensity.current.fontScale
    var isAcknowledged by remember { mutableStateOf(false) }

    if (isAcknowledged) return

    val advisorySpec = nextBestAction?.let { homeNextActionStrings(it) }
    val advisoryText = advisorySpec?.let { spec ->
        if (spec.formatArgs.isEmpty()) {
            stringResource(spec.resId)
        } else {
            stringResource(spec.resId, *spec.formatArgs.toTypedArray())
        }
    } ?: stringResource(R.string.home_xion_unavailable)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("xion_insight_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface.copy(alpha = 0.92f)),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    colors.systemGreen.copy(alpha = 0.35f),
                    colors.borderFaint.copy(alpha = 0.2f)
                )
            )
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Luminous Radiant Orb matching Pixel Master Xion Insight
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                lerp(colors.systemGreen, colors.textPrimary, 0.18f),
                                lerp(colors.systemGreen, colors.dimSurface, 0.20f),
                                lerp(colors.shadowSurface, colors.systemGreen, 0.12f)
                            )
                        )
                    )
                    .border(1.dp, colors.systemGreen.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(colors.textPrimary.copy(alpha = 0.75f))
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_xion_header),
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.systemGreen,
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .clickable(
                                onClickLabel = stringResource(R.string.home_xion_dismiss)
                            ) { isAcknowledged = true },
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = stringResource(R.string.home_xion_dismiss),
                            fontFamily = Outfit,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textDim,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = advisoryText,
                    fontFamily = Outfit,
                    fontSize = if (fontScale > 1.3f) 12.sp else 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    lineHeight = 17.sp,
                    maxLines = if (fontScale > 1.3f) 3 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Resource specification for a Home advisory; the string itself resolves at render time. */
internal data class HomeNextActionStrings(
    val resId: Int,
    val formatArgs: List<String> = emptyList()
)

/**
 * Language-neutral mapping from advisory state to the resource that renders it.
 *
 * The mapping never consults any stored language state, so the advisory is always resolved
 * under the exact configuration that renders the rest of the screen.
 */
internal fun homeNextActionStrings(action: HomeNextAction): HomeNextActionStrings = when (action) {
    HomeNextAction.AddFirstMission ->
        HomeNextActionStrings(R.string.home_next_action_add_first_mission)
    is HomeNextAction.CompleteMission ->
        HomeNextActionStrings(R.string.home_next_action_complete_mission, listOf(action.missionTitle))
    HomeNextAction.BuildStreak ->
        HomeNextActionStrings(R.string.home_next_action_build_streak)
}
