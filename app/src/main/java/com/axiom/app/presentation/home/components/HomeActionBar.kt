package com.axiom.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.*

@Composable
fun HomeActionBar(
    onFocusClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onAddMissionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(colors.shadowSurface.copy(alpha = 0.95f))
            .border(1.dp, colors.borderFaint, RoundedCornerShape(24.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // FOCUS button
        ActionBarPillButton(
            iconText = "⏱",
            labelText = "FOCUS",
            onClick = onFocusClick,
            modifier = Modifier.testTag("action_bar_focus")
        )

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(colors.borderFaint)
        )

        // CHECK-IN button
        ActionBarPillButton(
            iconText = "✓",
            labelText = "CHECK-IN",
            onClick = onCheckInClick,
            modifier = Modifier.testTag("action_bar_checkin")
        )

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(colors.borderFaint)
        )

        // MISSION button
        ActionBarPillButton(
            iconText = "＋",
            labelText = "MISSION",
            onClick = onAddMissionClick,
            modifier = Modifier.testTag("action_bar_mission")
        )
    }
}

private fun Modifier.padding(symmetric: Int): Modifier = this.padding(horizontal = symmetric.dp, vertical = (symmetric / 2).dp)

@Composable
private fun ActionBarPillButton(
    iconText: String,
    labelText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = iconText,
            fontSize = 13.sp,
            color = colors.systemGreen
        )
        Text(
            text = labelText,
            fontFamily = FiraCode,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
    }
}
