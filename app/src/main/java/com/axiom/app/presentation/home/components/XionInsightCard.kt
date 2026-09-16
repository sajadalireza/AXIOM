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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
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
    nextBestAction: String?,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val fontScale = LocalDensity.current.fontScale
    var isAcknowledged by remember { mutableStateOf(false) }

    if (isAcknowledged) return

    val advisoryText = nextBestAction?.ifBlank { null }
        ?: stringResource(R.string.home_xion_unavailable)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("xion_insight_card"),
        colors = CardDefaults.cardColors(containerColor = colors.shadowSurface),
        border = BorderStroke(1.dp, colors.borderFaint),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Luminous Orb + "XION" + "ADVISORY" tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Luminous glowing orb
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(colors.systemGreen)
                            .border(1.5.dp, colors.systemGreen.copy(alpha = 0.3f), CircleShape)
                    )
                    Text(
                        text = stringResource(R.string.home_xion_header),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.systemGreen,
                        letterSpacing = 1.sp
                    )
                }

                // Subordinate tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.dimSurface)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_xion_subordinate_tag),
                        fontFamily = Outfit,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textDim
                    )
                }
            }

            // Advisory Text: Truthful nextBestAction or honest neutral state
            Text(
                text = advisoryText,
                fontFamily = Outfit,
                fontSize = if (fontScale > 1.3f) 12.sp else 13.sp,
                fontWeight = FontWeight.Normal,
                color = colors.textSecondary,
                lineHeight = if (fontScale > 1.3f) 16.sp else 18.sp
            )

            // Subordinate Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_xion_dismiss),
                    fontFamily = Outfit,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textDim,
                    modifier = Modifier
                        .clickable { isAcknowledged = true }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
