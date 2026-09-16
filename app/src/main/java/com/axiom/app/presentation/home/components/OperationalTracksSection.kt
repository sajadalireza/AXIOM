package com.axiom.app.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.theme.AxiomBorder
import com.axiom.app.ui.theme.AxiomRadius
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.LocalAxiomColors

@Composable
fun OperationalTracksSection(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val isFa = java.util.Locale.getDefault().language == "fa"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Label header with indicator dot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(color = colors.systemGreen, shape = RoundedCornerShape(2.5.dp))
            )
            Text(
                text = if (isFa) "ماژول‌های عملیاتی" else "OPERATIONAL MODULES",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = colors.textSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        // Governance repair (WP-UIUX-02 / Issue #85 PO decision): Home must not expose
        // AX-013 Dungeons (HIDE / G7), AX-016 Skill Tree (HIDE / G7) or AX-018 Leagues
        // (FREEZE / G7). Their tiles and navigation callbacks were removed; canonical
        // MODULE_DISPOSITION is unchanged and the routes still exist elsewhere.
        // Retained under a bounded PO exception: AX-015 Daily Check-in and
        // AX-022 Weekly Analytics, as existing secondary progressive-disclosure links.
        // The remaining two tiles share one balanced row.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Item 1: Check-in
                QuickLaunchItem(
                    title = if (isFa) "حضور و غیاب" else "CHECK-IN",
                    iconRes = R.drawable.ic_nav_habits,
                    iconColor = colors.uncommonTeal,
                    onClick = { onNavigate(Screen.DailyCheckin.route) },
                    modifier = Modifier.weight(1f)
                )
                // Item 2: Analytics
                QuickLaunchItem(
                    title = if (isFa) "تحلیل‌ها" else "ANALYTICS",
                    iconRes = R.drawable.ic_nav_system,
                    iconColor = colors.rareBlue,
                    onClick = { onNavigate(Screen.WeeklyAnalytics.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickLaunchItem(
    title: String,
    iconRes: Int,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    Box(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(AxiomRadius.l))
            .background(colors.shadowSurface)
            .border(AxiomBorder.thin, colors.borderFaint, RoundedCornerShape(AxiomRadius.l))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(21.dp)
            )
            Text(
                text = title,
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary
            )
        }
    }
}
