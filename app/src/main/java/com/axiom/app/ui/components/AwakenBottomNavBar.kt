package com.axiom.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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
            .padding(start = 18.dp, end = 18.dp, bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val fontScale = LocalDensity.current.fontScale
        val navHeight = if (fontScale > 1.3f) 88.dp else 80.dp

        val dockShape = RoundedCornerShape(30.dp)
        val isDark = colors.voidBlack == AxiomDarkColors.voidBlack

        val dockBackgroundBrush = if (isDark) {
            Brush.verticalGradient(
                listOf(
                    lerp(colors.shadowSurface, colors.systemGreen, 0.08f).copy(alpha = 0.94f),
                    lerp(colors.dimSurface, colors.voidBlack, 0.50f).copy(alpha = 0.97f)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    lerp(colors.shadowSurface, colors.systemGreen, 0.04f).copy(alpha = 0.95f),
                    lerp(colors.shadowSurface, colors.dimSurface, 0.60f).copy(alpha = 0.98f)
                )
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(navHeight)
                .background(dockBackgroundBrush, shape = dockShape)
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                colors.legendaryGold.copy(alpha = 0.35f),
                                colors.systemGreen.copy(alpha = 0.20f),
                                colors.borderFaint.copy(alpha = 0.15f)
                            )
                        )
                    ),
                    shape = dockShape
                )
                .clip(dockShape)
        ) {
            val totalWidth = maxWidth
            val tabWidth = totalWidth / tabs.size
            val podWidth = 48.dp

            // Linear horizontal top highlight (1dp) inner glow accent line with subtle gold/emerald tone
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            colors.legendaryGold.copy(alpha = 0.35f),
                            colors.systemGreen.copy(alpha = 0.25f),
                            colors.legendaryGold.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(28.dp.toPx(), 0f),
                    end = Offset(size.width - 28.dp.toPx(), 0f),
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

            // Active indicator color: legendaryGold for Home, systemGreen for other tabs
            val activePodColor = if (tabs.getOrNull(selectedIndex)?.screen == Screen.Home) {
                colors.legendaryGold
            } else {
                colors.systemGreen
            }

            // Active indicator: Uses absolute BottomStart alignment with LTR LayoutDirection for perfect layout positioning in both LTR & RTL
            CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides LayoutDirection.Ltr) {
                Canvas(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset { IntOffset(animatedXPx.roundToInt(), -with(density) { 3.dp.roundToPx() }) }
                        .size(width = 48.dp, height = 3.dp)
                        .graphicsLayer {
                            scaleX = scaleAnim.value
                        }
                ) {
                    drawRoundRect(
                        color = activePodColor,
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
                    val isHome = tab.screen == Screen.Home

                    // Selected Home uses colors.legendaryGold accent; other selected tabs use systemGreen
                    val selectedColor = if (isHome) colors.legendaryGold else colors.systemGreen

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) selectedColor else colors.textDim,
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
                            .heightIn(min = 48.dp)
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
                                // Elevated center Home anchor with radial emerald fill and dual gold/emerald border
                                if (isHome) {
                                    val homeAnchorBackground = if (isDark) {
                                        Brush.radialGradient(
                                            listOf(
                                                lerp(colors.shadowSurface, colors.systemGreen, if (isSelected) 0.38f else 0.24f),
                                                lerp(colors.shadowSurface, colors.systemGreen, if (isSelected) 0.20f else 0.12f),
                                                lerp(colors.voidBlack, colors.systemGreen, 0.05f)
                                            )
                                        )
                                    } else {
                                        Brush.radialGradient(
                                            listOf(
                                                lerp(colors.shadowSurface, colors.systemGreen, if (isSelected) 0.22f else 0.10f),
                                                lerp(colors.shadowSurface, colors.systemGreen, if (isSelected) 0.12f else 0.05f),
                                                colors.dimSurface.copy(alpha = 0.85f)
                                            )
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) colors.legendaryGold.copy(alpha = 0.85f) else colors.legendaryGold.copy(alpha = 0.40f),
                                                shape = CircleShape
                                            )
                                            .padding(1.5.dp)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) colors.systemGreen.copy(alpha = 0.70f) else colors.systemGreen.copy(alpha = 0.35f),
                                                shape = CircleShape
                                            )
                                            .background(homeAnchorBackground, CircleShape)
                                    )
                                }

                                Icon(
                                    painter = painterResource(id = tab.iconRes),
                                    contentDescription = stringResource(tab.labelRes),
                                    tint = iconColor,
                                    modifier = Modifier.size(if (isHome) 28.dp else 21.dp)
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
                                visible = if (fontScale > 1.3f) isSelected else true,
                                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                            ) {
                                Text(
                                    text = stringResource(tab.labelRes),
                                    fontFamily = Outfit,
                                    fontSize = if (fontScale > 1.3f) 9.sp else 10.sp,
                                    color = if (isSelected) selectedColor else colors.textDim,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
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
