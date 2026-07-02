package com.axiom.app.presentation.leagues

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.R
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.LeaguesUiState
import com.axiom.app.ui.LeaguesViewModel
import com.axiom.app.ui.components.ActiveFocusEngineLayout
import com.axiom.app.ui.theme.*

@Composable
fun MainLeaguesLayout(
    state: LeaguesUiState.Success,
    activeMission: Mission?,
    timerSeconds: Int,
    isTimerActive: Boolean,
    isTimerPaused: Boolean,
    isFastSyncEnabled: Boolean,
    viewModel: LeaguesViewModel,
    onNavigate: (String) -> Unit,
    axiomColors: AxiomColorScheme
) {
    // Determine league tier
    val points = state.userLP
    val isFa = java.util.Locale.getDefault().language == "fa"
    
    // Animated LP display
    val animatedPoints by animateIntAsState(
        targetValue = points,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "animatedPoints"
    )

    val tierTitle = when {
        points < 200 -> if (isFa) "پروتکل محافظ برنزی" else "BRONZE SHIELD PROTOCOL"
        points < 600 -> if (isFa) "فرمانروایی معیار نقره‌ای" else "SILVER CRITERION DIVISION"
        points < 1200 -> if (isFa) "درگاه صعود طلایی" else "GOLD ASCENSION GATEWAY"
        else -> if (isFa) "قلمرو پادشاه سایه (رتبه S)" else "SHADOW MONARCH DOMINION (S-RANK)"
    }
    val tierColor = when {
        points < 200 -> Color(0xFFCD7F32)
        points < 600 -> Color(0xFFC0C0C0)
        points < 1200 -> axiomColors.legendaryGold
        else -> axiomColors.systemGreen
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page title
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.league_title),
                    fontFamily = JetBrainsMono,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = axiomColors.textPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stringResource(R.string.league_subtitle),
                    fontFamily = Inter,
                    fontSize = 11.sp,
                    color = axiomColors.textDim
                )
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = BorderFaint, thickness = 1.dp)
            }
        }

        // Active Focus timer block
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (isTimerActive) axiomColors.systemGreen else BorderFaint,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(axiomColors.dimSurface)
                    .padding(16.dp)
            ) {
                if (isTimerActive && activeMission != null) {
                    ActiveFocusEngineLayout(
                        title = activeMission.title,
                        subtitle = if (isFa) "جایزه تخمینی: ۲۵+ امتیاز لیگ و ${activeMission.xpReward}+ تجربه" else "EST. REWARD: +25 LP & +${activeMission.xpReward} XP",
                        seconds = timerSeconds,
                        isFastSyncEnabled = isFastSyncEnabled,
                        isPaused = isTimerPaused,
                        onPauseToggle = { if (isTimerPaused) viewModel.resumeFocusProtocol() else viewModel.pauseFocusProtocol() },
                        onAbort = { viewModel.pauseOrAbortFocusProtocol(isBreach = false) }
                    )
                } else {
                    InactiveFocusLayout(
                        activeMissions = state.activeMissions,
                        isFastSyncEnabled = isFastSyncEnabled,
                        viewModel = viewModel,
                        axiomColors = axiomColors,
                        onNavigate = onNavigate
                    )
                }
            }
        }

        // League tier & current standings header
        item {
            val currentPoints = points
            val (lowerLimit, upperLimit, nextTierOpt) = when {
                currentPoints < 200 -> Triple(0, 200, "SILVER CRITERION DIVISION")
                currentPoints < 600 -> Triple(200, 600, "GOLD ASCENSION GATEWAY")
                currentPoints < 1200 -> Triple(600, 1200, "SHADOW MONARCH DOMINION (S-RANK)")
                else -> Triple(1200, 2000, "")
            }
            val progressFraction = if (upperLimit > lowerLimit) {
                ((currentPoints - lowerLimit).toFloat() / (upperLimit - lowerLimit)).coerceIn(0f, 1f)
            } else 1f
            val lpRemaining = upperLimit - currentPoints
            val progressText = if (nextTierOpt.isNotEmpty()) {
                val nextTierNameFa = when (nextTierOpt) {
                    "SILVER CRITERION DIVISION" -> "معیار نقره‌ای"
                    "GOLD ASCENSION GATEWAY" -> "صعود طلایی"
                    "SHADOW MONARCH DOMINION (S-RANK)" -> "فرمانروای سایه"
                    else -> nextTierOpt
                }
                if (isFa) "به $lpRemaining امتیاز لیگ نیاز دارید تا به رتبه $nextTierNameFa صعود کنید" else "NEED $lpRemaining LP FOR ASCENSION TO $nextTierOpt"
            } else {
                if (isFa) "به رتبه عالی پادشاه سایه‌ها دست یافتید" else "SUPREME SHADOW MONARCH RANK ATTAINED"
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, tierColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .background(tierColor.copy(alpha = 0.03f))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isFa) "◈ سطح رتبه بخش" else "◈ DIVISION RANK TIER",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = axiomColors.textDim
                        )
                        Text(
                            text = tierTitle,
                            fontFamily = JetBrainsMono,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tierColor
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(tierColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$animatedPoints LP",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = axiomColors.voidBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // High-fidelity progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(TextPrimary.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(tierColor.copy(alpha = 0.5f), tierColor)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Modern tick mark indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        (if (isFa) "برنز" else "BRONZE") to (currentPoints >= 0),
                        (if (isFa) "نقره (۲۰۰)" else "SILVER (200)") to (currentPoints >= 200),
                        (if (isFa) "طلا (۶۰۰)" else "GOLD (600)") to (currentPoints >= 600),
                        (if (isFa) "رتبه اس (۱.۲هزار)" else "S-RANK (1.2K)") to (currentPoints >= 1200)
                    ).forEach { (name, reached) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        if (reached) tierColor else TextPrimary.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = name,
                                fontFamily = JetBrainsMono,
                                fontSize = 7.sp,
                                fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
                                color = if (reached) tierColor else axiomColors.textDim
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderFaint, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFa) "◈ کالیبراسیون شناختی:" else "◈ COGNITIVE CALIBRATION:",
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = axiomColors.textDim
                    )
                    Text(
                        text = progressText,
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nextTierOpt.isNotEmpty()) tierColor else axiomColors.legendaryGold
                    )
                }
            }
        }

        if (!state.isLiveLeaderboard) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, axiomColors.textDim.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .background(axiomColors.dimSurface)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = axiomColors.textDim,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.league_demo_leaderboard_banner),
                            fontFamily = Inter,
                            fontSize = 10.sp,
                            color = axiomColors.textDim
                        )
                    }
                }
            }
        }

        // Gap to Next Rank Callout
        item {
            val userIndex = state.rivals.indexOfFirst { it.name.contains("(YOU)") }
            if (userIndex != -1) {
                val userRank = userIndex + 1
                if (userIndex > 0) {
                    val rivalAbove = state.rivals[userIndex - 1]
                    val gap = rivalAbove.points - state.userLP
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, axiomColors.systemGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = axiomColors.voidBlack)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = axiomColors.systemGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isFa) "◈ فاصله صعود شناختی" else "◈ ASCENSION CALIBRATION GAP",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = axiomColors.systemGreen
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(axiomColors.systemGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isFa) "رتبه #$userRank" else "RANK #$userRank",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = axiomColors.systemGreen
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.league_gap_behind, gap.toString(), rivalAbove.name),
                                fontFamily = Inter,
                                fontSize = 10.sp,
                                color = axiomColors.textPrimary,
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(TextPrimary.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                             ) {
                                val progressRatio = remember(state.userLP, rivalAbove.points) {
                                    val safeBottom = (rivalAbove.points - 300).coerceAtLeast(0)
                                    val total = rivalAbove.points - safeBottom
                                    val current = state.userLP - safeBottom
                                    if (total > 0) (current.toFloat() / total).coerceIn(0f, 1f) else 0.5f
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressRatio)
                                        .fillMaxHeight()
                                        .background(axiomColors.systemGreen, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, axiomColors.legendaryGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = axiomColors.dimSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = axiomColors.legendaryGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isFa) "◈ وضعیت ایستگاه پیوند" else "◈ NEXUS STANDINGS SUPREMACY",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = axiomColors.legendaryGold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(axiomColors.legendaryGold, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isFa) "قهرمان" else "CHAMPION",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = axiomColors.voidBlack
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (state.isLiveLeaderboard) {
                                    stringResource(R.string.league_global_standings_top)
                                } else {
                                    stringResource(R.string.league_local_standings_top)
                                },
                                fontFamily = Inter,
                                fontSize = 10.sp,
                                color = axiomColors.textPrimary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Live standings header
        item {
            Text(
                text = if (isFa) "◈ رتبه‌بندی محلی همگام‌ساز سطح" else "◈ TIER SYNC LOCAL STANDINGS",
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = axiomColors.systemGreen,
                letterSpacing = 0.5.sp
            )
        }

        // Standing items with Promotion / Demotion zones
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val promotionCutoff = (state.rivals.size * 0.2f).toInt().coerceAtLeast(1)
                val demotionCutoff = (state.rivals.size * 0.8f).toInt().coerceAtMost(state.rivals.size - 1)

                state.rivals.forEachIndexed { index, rival ->
                    if (index == 0 && state.rivals.size > 1) {
                        ZoneDividerItem(
                            title = if (isFa) "منطقه صعود (۲ نفر برتر) - مسیر اعتلا" else "PROMOTION ZONE (TOP 2) - NEXUS ASCENSION PATH",
                            description = stringResource(R.string.league_promotion_zone),
                            color = axiomColors.legendaryGold,
                            icon = Icons.Default.ArrowUpward
                        )
                    } else if (index == promotionCutoff && promotionCutoff < demotionCutoff) {
                        ZoneDividerItem(
                            title = if (isFa) "بخش وضعیت امن لیدربرد" else "SECURE STATUS INTEL GROUP",
                            description = stringResource(R.string.league_stable_zone),
                            color = axiomColors.systemGreen,
                            icon = Icons.Default.Remove
                        )
                    } else if (index == demotionCutoff && demotionCutoff > promotionCutoff) {
                        ZoneDividerItem(
                            title = if (isFa) "منطقه هشدار سقوط (احتمال تنزل رتبه)" else "RE-ACTIVATION WARNING SECTOR (DEMOTION RISK)",
                            description = stringResource(R.string.league_demotion_risk),
                            color = axiomColors.penaltyRed,
                            icon = Icons.Default.ArrowDownward
                        )
                    }

                    RivalHunterRow(
                        rank = index + 1,
                        rival = rival,
                        axiomColors = axiomColors,
                        isUser = rival.name.contains("(YOU)"),
                        zoneColor = when {
                            index < promotionCutoff -> axiomColors.legendaryGold
                            index < demotionCutoff -> axiomColors.systemGreen
                            else -> axiomColors.penaltyRed
                        },
                        isPreregistered = state.isPreregistered
                    )
                }
            }
        }
    }
}
