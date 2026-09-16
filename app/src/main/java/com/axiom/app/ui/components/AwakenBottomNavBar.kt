package com.axiom.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import java.util.Locale
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.annotation.DrawableRes
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.navigation.Screen
import com.axiom.app.ui.theme.*
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

@Composable
fun AwakenBottomNavBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    skillTreeUnlocked: Boolean = false,   // PRIORITY 3: Day 3 gate
    pendingCheckinCount: Int = 0,
    overdueWeeklyReviewCount: Int = 0,
    newSystemMessagesCount: Int = 0
) {
    val colors = LocalAxiomColors.current

    // 5 high-fidelity tab definitions: [⚔ MISSIONS]  [🏋 PHYSICAL]  [🏠 HOME]  [💀 SHADOWS]  [👤 HUNTER]
    val tabs = remember {
        listOf(
            NavBarTab(Screen.Missions, R.drawable.ic_nav_missions, R.string.nav_missions, "tab_missions"),
            NavBarTab(Screen.BodyMap, R.drawable.ic_nav_physical, R.string.nav_physical_condition, "tab_physical"),
            NavBarTab(Screen.Home, R.drawable.ic_nav_home, R.string.nav_home, "tab_home"),
            NavBarTab(Screen.ShadowArmy, R.drawable.ic_nav_shadows, R.string.nav_shadows, "tab_shadow_army"),
            NavBarTab(Screen.Profile, R.drawable.ic_nav_habits, R.string.nav_hunter, "tab_profile")
        )
    }

    // Identify current selected index dynamically mapping route to current enabled tabs list
    val selectedIndex = remember(currentRoute, tabs) {
        val index = tabs.indexOfFirst { tab ->
            currentRoute == tab.screen.route ||
            (tab.screen == Screen.Missions && currentRoute == Screen.Missions.route) ||
            (tab.screen == Screen.BodyMap && (currentRoute == Screen.BodyMap.route || currentRoute == Screen.DailyCheckin.route)) ||
            (tab.screen == Screen.Home && currentRoute == Screen.Home.route) ||
            (tab.screen == Screen.ShadowArmy && (currentRoute == Screen.ShadowArmy.route || currentRoute?.startsWith("shadow") == true)) ||
            (tab.screen == Screen.Profile && (currentRoute == Screen.Profile.route || currentRoute == Screen.CharacterStats.route))
        }
        if (index != -1) index else 2 // default to Center HOME tab
    }

    // Outer-bleed spacing and container aligning matching edge-to-edge system standards
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val fontScale = LocalDensity.current.fontScale
        val navHeight = if (fontScale > 1.3f) 82.dp else 72.dp

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(navHeight)
                .background(colors.shadowSurface.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                .border(1.dp, colors.borderFaint, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
        ) {
            val totalWidth = maxWidth
            val tabWidth = totalWidth / tabs.size
            val podWidth = 40.dp

            // Linear horizontal top highlight (1dp) inner glow accent line
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    color = colors.borderFaint.copy(alpha = 0.4f),
                    start = Offset(24.dp.toPx(), 0f),
                    end = Offset(size.width - 24.dp.toPx(), 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Animate scale bounce target on tab change selection
            var lastSelectedIndex by remember { mutableStateOf(selectedIndex) }
            val scaleAnim = remember { Animatable(1f) }

            LaunchedEffect(selectedIndex) {
                if (selectedIndex != lastSelectedIndex) {
                    lastSelectedIndex = selectedIndex
                    scaleAnim.animateTo(
                        targetValue = 1.15f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
                    scaleAnim.animateTo(
                        targetValue = 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            }

            // High performance sliding transition target positioning matching 60FPS expectations with RTL support
            val isRtl = androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl
            val targetX = if (isRtl) totalWidth - tabWidth * (selectedIndex + 1) + (tabWidth - podWidth) / 2 else tabWidth * selectedIndex + (tabWidth - podWidth) / 2
            val density = LocalDensity.current
            val targetXPx = with(density) { targetX.toPx() }
            val animatedXPx by animateFloatAsState(
                targetValue = targetXPx,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "nav_pod_x"
            )

            // Active indicator: Uses absolute BottomStart alignment with LTR LayoutDirection for perfect layout positioning in both LTR & RTL
            CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Ltr) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset { IntOffset(animatedXPx.roundToInt(), -with(density) { 3.dp.roundToPx() }) }
                        .size(width = 40.dp, height = 3.dp)
                        .graphicsLayer {
                            scaleX = scaleAnim.value
                        }
                ) {
                    drawRoundRect(
                        color = colors.systemGreen,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                    )
                }
            }

            // Interactive Row layout on overlay
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selectedIndex
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) colors.systemGreen else colors.textDim,
                        animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing),
                        label = "tab_icon_color"
                    )

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "press_scale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .scale(scale)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = LocalIndication.current,
                                onClick = { onNavigate(tab.screen) }
                            )
                            .testTag(tab.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // For Home tab: circular emerald halo when selected
                                if (tab.screen == Screen.Home && isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(colors.systemGreen.copy(alpha = 0.15f))
                                            .border(1.dp, colors.systemGreen.copy(alpha = 0.40f), RoundedCornerShape(18.dp))
                                    )
                                }

                                Icon(
                                    painter = painterResource(id = tab.iconRes),
                                    contentDescription = stringResource(tab.labelRes),
                                    tint = iconColor,
                                    modifier = Modifier.size(24.dp)
                                )

                                // Refined numeric badge overlay: no notification bombardment on Home
                                val badgeCount = when (tab.screen) {
                                    Screen.BodyMap -> pendingCheckinCount
                                    Screen.Home -> 0
                                    Screen.Profile -> overdueWeeklyReviewCount
                                    else -> 0
                                }

                                if (badgeCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 8.dp, y = (-5).dp)
                                            .background(colors.penaltyRed, RoundedCornerShape(8.dp))
                                            .border(1.dp, colors.shadowSurface, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = badgeCount.toString(),
                                            fontFamily = Outfit,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                            ) {
                                Text(
                                    text = stringResource(tab.labelRes),
                                    fontFamily = Outfit,
                                    fontSize = if (fontScale > 1.3f) 9.sp else 10.sp,
                                    color = colors.systemGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(top = if (fontScale > 1.3f) 1.dp else 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class NavBarTab(
    val screen: Screen,
    @DrawableRes val iconRes: Int,
    val labelRes: Int,
    val testTag: String
)
