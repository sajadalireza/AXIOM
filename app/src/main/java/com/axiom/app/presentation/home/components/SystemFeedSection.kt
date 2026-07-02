package com.axiom.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.SystemMessage
import com.axiom.app.ui.components.SystemMessageItem
import com.axiom.app.ui.theme.*

@Composable
fun SystemFeedSection(
    recentFeed: List<SystemMessage>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header
        Text(
            text = "SYSTEM INTELLIGENCE FEED",
            fontFamily = FiraCode,
            fontSize = 11.sp,
            color = colors.textDim,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        if (recentFeed.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.dimSurface)
                    .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO INTEL FEED DETECTED",
                    fontFamily = FiraCode,
                    fontSize = 11.sp,
                    color = colors.textDim,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Display top 3 latest system messages
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("system_feed_list")
            ) {
                recentFeed.take(3).forEach { msg ->
                    SystemMessageItem(
                        message = msg,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
