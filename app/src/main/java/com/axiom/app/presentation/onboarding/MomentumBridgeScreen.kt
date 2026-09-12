package com.axiom.app.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.ui.components.AxiomPillButton
import com.axiom.app.ui.components.AxiomWordmarkHeader
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.Outfit

/**
 * Momentum Bridge screen reproducing exact PO reference 03_momentum_bridge.png:
 * - Top: AXIOM Wordmark
 * - Title: "Build momentum, one step at a time" with gold accent
 * - Subtitle: "AXIOM is designed to help you focus, act, and keep moving forward."
 * - Central Artwork: Floating stone stairway over water towards glowing mountain peak
 * - Bottom: "Continue  →" with gold border sheen + "You can change settings later." footer
 */
@Composable
fun MomentumBridgeScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Header & Text block
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AxiomWordmarkHeader(
                    modifier = Modifier.padding(top = 12.dp),
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Title: "Build momentum, one step at a time"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.momentum_title_1),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Light,
                        fontSize = 30.sp,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.momentum_title_2),
                        fontFamily = Outfit,
                        fontWeight = FontWeight.Normal,
                        fontSize = 30.sp,
                        color = Color(0xFFE5C88F),
                        textAlign = TextAlign.Center
                    )
                }

                // Subtitle
                Text(
                    text = stringResource(R.string.momentum_subtitle),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Center: Atmospheric Floating Stairway Painting
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.bg_momentum_stairway),
                    contentDescription = "Stairway to Momentum",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom: Continue CTA & Settings Notice
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AxiomPillButton(
                    text = stringResource(R.string.btn_continue),
                    onClick = onContinue,
                    borderColor = Color(0xFFE5C88F)
                )

                Text(
                    text = stringResource(R.string.momentum_footer),
                    fontFamily = Outfit,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = colors.textDim,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
