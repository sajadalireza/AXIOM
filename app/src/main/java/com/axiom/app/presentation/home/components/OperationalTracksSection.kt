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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.navigation.Screen
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
                color = Color(0xFF8A9B90),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        // 3x2 Grid of launch items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Item 1: Dungeons
                QuickLaunchItem(
                    title = if (isFa) "سیاه‌چال‌ها" else "DUNGEONS",
                    iconRes = R.drawable.ic_nav_missions,
                    iconColor = colors.penaltyRed,
                    onClick = { onNavigate(Screen.Dungeons.route) },
                    modifier = Modifier.weight(1f)
                )
                // Item 2: Skills
                QuickLaunchItem(
                    title = if (isFa) "مهارت‌ها" else "SKILLS",
                    iconRes = R.drawable.ic_nav_skills,
                    iconColor = colors.systemGreen,
                    onClick = { onNavigate(Screen.SkillTree.route) },
                    modifier = Modifier.weight(1f)
                )
                // Item 3: Leagues
                QuickLaunchItem(
                    title = if (isFa) "لیگ‌ها" else "LEAGUES",
                    iconRes = R.drawable.ic_nav_leagues,
                    iconColor = colors.legendaryGold,
                    onClick = { onNavigate(Screen.Leagues.route) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Item 4: Check-in
                QuickLaunchItem(
                    title = if (isFa) "حضور و غیاب" else "CHECK-IN",
                    iconRes = R.drawable.ic_nav_habits,
                    iconColor = colors.uncommonTeal,
                    onClick = { onNavigate(Screen.DailyCheckin.route) },
                    modifier = Modifier.weight(1f)
                )
                // Item 5: Analytics
                QuickLaunchItem(
                    title = if (isFa) "تحلیل‌ها" else "ANALYTICS",
                    iconRes = R.drawable.ic_nav_system,
                    iconColor = colors.rareBlue,
                    onClick = { onNavigate(Screen.WeeklyAnalytics.route) },
                    modifier = Modifier.weight(1f)
                )
                // Item 6: Pro
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(82.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF211C10), colors.shadowSurface)
                            )
                        )
                        .border(1.dp, colors.legendaryGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { onNavigate(Screen.Premium.route) }
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            text = "👑",
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (isFa) "حرفه‌ای" else "PRO",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.legendaryGold
                        )
                    }
                }
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
            .clip(RoundedCornerShape(12.dp))
            .background(colors.shadowSurface)
            .border(1.dp, Color(0xFF232220), RoundedCornerShape(12.dp))
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
