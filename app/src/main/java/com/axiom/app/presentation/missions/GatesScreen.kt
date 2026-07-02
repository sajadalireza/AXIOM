package com.axiom.app.presentation.missions

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.presentation.dungeon.DungeonsScreen
import com.axiom.app.ui.components.ScanlineOverlay
import com.axiom.app.ui.components.ScreenHelpButton
import com.axiom.app.ui.components.SystemToast
import com.axiom.app.ui.theme.*

@Composable
fun GatesScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    var selectedTopTab by remember { mutableStateOf(0) } // 0 = Missions, 1 = Dungeons, 2 = Leagues
    
    // Local toast capability for pre-registration confirmation
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isToastGold by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Screen header (GATES SYSTEM UPLINK)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.nav_gates),
                        fontFamily = JetBrainsMono,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (java.util.Locale.getDefault().language == "fa") "[ درگاه‌های مسیریابی سیستم ]" else "[ SYSTEM ROUTING GATES ]",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = colors.systemGreen,
                        letterSpacing = 1.sp
                    )
                }
                
                ScreenHelpButton(stringResId = R.string.glossary_title)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Premium Cyberpunk Top Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp)
                    .background(colors.voidBlack)
                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    R.string.missions_title,
                    R.string.dungeons_title
                )
                
                tabs.forEachIndexed { index, titleResId ->
                    val isSelected = selectedTopTab == index
                    val tabBgColor by animateColorAsState(
                        targetValue = if (isSelected) colors.systemGreen.copy(alpha = 0.12f) else Color.Transparent,
                        label = "tab_bg"
                    )
                    val tabTextColor by animateColorAsState(
                        targetValue = if (isSelected) colors.systemGreen else colors.textDim,
                        label = "tab_text"
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(tabBgColor)
                            .clickable { selectedTopTab = index }
                            .testTag("top_tab_${index}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(titleResId),
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = tabTextColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body content depending on index
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTopTab) {
                    0 -> MissionsScreen(
                        onNavigate = onNavigate,
                        isEmbedded = true,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> DungeonsScreen(
                        onNavigate = onNavigate,
                        isEmbedded = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        ScanlineOverlay()

        SystemToast(
            message = toastMessage,
            isGold = isToastGold,
            onDismiss = { toastMessage = null },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )
    }
}

@Composable
fun LeaguesComingSoonScreen(
    onPreRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    var isRegistered by remember { mutableStateOf(false) }

    // Pulsing lock key animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lock_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            // Cyber glow behind the lock icon
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SystemGreen.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.width / 2
                    )
                )
            }
            
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked Feature",
                tint = if (isRegistered) colors.systemGreen else PenaltyRed,
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer(
                        scaleX = pulseScale,
                        scaleY = pulseScale
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.leagues_coming_soon),
            fontFamily = JetBrainsMono,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.leagues_coming_soon_desc),
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Grid showing planned league features
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                .background(ShadowSurface)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (java.util.Locale.getDefault().language == "fa") "◈ پروتکل‌های هسته لیگ:" else "◈ LEAGUE CORE PROTOCOLS:",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = colors.systemGreen,
                fontWeight = FontWeight.Bold
            )
            
            LeagueFeatureRow(
                title = stringResource(R.string.leagues_badge_bronze),
                desc = if (java.util.Locale.getDefault().language == "fa") "آغاز نبردهای محلی انضباط شخصی" else "Starting ground for self-discipline tracking."
            )
            LeagueFeatureRow(
                title = stringResource(R.string.leagues_badge_silver),
                desc = if (java.util.Locale.getDefault().language == "fa") "رقابت با ۵۰ هانتر هم‌تراز در طول هفته" else "Weekly brackets of 50 Hunters in concurrent ranks."
            )
            LeagueFeatureRow(
                title = stringResource(R.string.leagues_badge_gold),
                desc = if (java.util.Locale.getDefault().language == "fa") "صعود بر اساس امتیاز قدرت و بقا" else "High-stakes XP survival and power-score brackets."
            )
            LeagueFeatureRow(
                title = stringResource(R.string.leagues_badge_shadow_monarch),
                desc = if (java.util.Locale.getDefault().language == "fa") "جام رتبه‌بندی S-Rank جهانی و لقب همیشگی" else "Exclusive global S-Rank titles for top masters."
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Pre register button
        val preRegInteractionSource = remember { MutableInteractionSource() }
        val isPressed by preRegInteractionSource.collectIsPressedAsState()
        val buttonScale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1.0f,
            label = "btn_scale"
        )

        Button(
            onClick = {
                if (!isRegistered) {
                    isRegistered = true
                    onPreRegister()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRegistered) colors.systemGreen.copy(alpha = 0.2f) else colors.systemGreen,
                contentColor = if (isRegistered) colors.systemGreen else colors.voidBlack
            ),
            shape = RoundedCornerShape(4.dp),
            interactionSource = preRegInteractionSource,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .scale(buttonScale)
                .border(
                    width = 1.dp,
                    color = if (isRegistered) colors.systemGreen else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                )
                .testTag("btn_leagues_prereg")
        ) {
            Text(
                text = if (isRegistered) {
                    if (java.util.Locale.getDefault().language == "fa") "✓ اولویت اتصال فعال گردید" else "✓ PRIORITY SYNC LOCKED"
                } else {
                    stringResource(R.string.leagues_preregister_btn)
                },
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LeagueFeatureRow(
    title: String,
    desc: String
) {
    val colors = LocalAxiomColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = colors.systemGreen.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = desc,
                fontFamily = JetBrainsMono,
                fontSize = 10.sp,
                color = colors.textDim
            )
        }
    }
}
