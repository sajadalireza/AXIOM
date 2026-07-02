package com.axiom.app.presentation.ceremony

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.axiom.app.R
import com.axiom.app.data.local.AxiomPreferences
import com.axiom.app.domain.engine.XPEngine
import com.axiom.app.domain.repository.HunterRepository
import com.axiom.app.domain.usecase.CreateMissionUseCase
import com.axiom.app.ui.components.neonGlow
import com.axiom.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class CeremonyViewModel @Inject constructor(
    val ceremonyEngine: CeremonyEngine,
    val createMissionUseCase: CreateMissionUseCase,
    val hunterRepository: HunterRepository,
    val preferences: AxiomPreferences
) : ViewModel() {
    fun acceptPenalty(title: String, xp: Int) {
        viewModelScope.launch {
            createMissionUseCase(
                title = title,
                track = "Penalty Protocol",
                rarity = "COMMON",
                skillId = "",
                xpReward = xp,
                powerScore = 0.5f,
                estimatedHours = 1.0f
            )
        }
    }

    fun useStreakShield(lostStreak: Int) {
        viewModelScope.launch {
            if (preferences.consumeStreakFreeze()) {
                preferences.setStreak(lostStreak)
                val remaining = preferences.streakFreezeFlow.first()
                ceremonyEngine.emit(CeremonyEvent.StreakShieldUsed(lostStreak, remaining))
            }
        }
    }

    fun awardWeeklyReviewXP(xpReward: Int) {
        viewModelScope.launch {
            val hunter = hunterRepository.getDirectHunterProfile() ?: return@launch
            var newHunterXP    = hunter.currentXP + xpReward
            var newHunterLevel = hunter.level
            var nextLevelXP    = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            while (newHunterXP >= nextLevelXP && newHunterLevel < 100) {
                newHunterXP   -= nextLevelXP
                newHunterLevel++
                nextLevelXP    = XPEngine.xpNeededForLevel(newHunterLevel).toInt()
            }
            if (newHunterLevel >= 100) { newHunterLevel = 100; newHunterXP = 0 }

            val hunterRankLabel = XPEngine.calculateHunterRank(newHunterLevel)
            val hunterRankSuffix = if (hunterRankLabel.endsWith("-Rank")) hunterRankLabel else "$hunterRankLabel-Rank"
            hunterRepository.updateHunterProfile(
                hunter.copy(
                    level          = newHunterLevel,
                    rankLabel      = hunterRankSuffix,
                    totalXP        = hunter.totalXP + xpReward,
                    currentXP      = newHunterXP,
                    xpToNextLevel  = nextLevelXP,
                    progressPercent = if (newHunterLevel >= 100) 1f else newHunterXP.toFloat() / nextLevelXP,
                    rankColor      = XPEngine.getRankColor(hunterRankLabel),
                    rankGlyph      = XPEngine.getGlyphForRank(hunterRankLabel)
                )
            )
            preferences.addLeaguePoints(xpReward)
        }
    }
}

@Composable
fun CeremonyHost(
    onNavigateToMissions: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CeremonyViewModel = hiltViewModel()
) {
    val currentColors = LocalAxiomColors.current
    val hapticFeedback = LocalHapticFeedback.current
    val event by viewModel.ceremonyEngine.ceremonyEvent.collectAsStateWithLifecycle()

    // ═══════════════════════════════════════════════════════════════
    // LAUNCHED EFFECT: SEQUENTIAL QUEUE CONSUMER WITH 200MS GAP & HAPTICS
    // ═══════════════════════════════════════════════════════════════
    LaunchedEffect(Unit) {
        for (e in viewModel.ceremonyEngine.eventChannel) {
            // 1. Determine Event Severity for Haptics
            val isHeavy = when (e) {
                is CeremonyEvent.LevelUp -> true
                is CeremonyEvent.RankUp -> true
                is CeremonyEvent.ShadowAcquired -> true
                is CeremonyEvent.BossDefeated -> true
                is CeremonyEvent.CheckpointCleared -> true
                is CeremonyEvent.WeeklyReviewComplete -> true
                is CeremonyEvent.StreakMilestone -> e.streak >= 7
                is CeremonyEvent.MissionComplete -> {
                    val rarity = e.rarity.uppercase()
                    rarity == "DEPTH" || rarity == "SHIELD" || rarity == "WEALTH_ENGINE" || rarity == "CRITICAL"
                }
                is CeremonyEvent.SystemAnomaly -> e.tier == "MAJOR" || e.tier == "CRITICAL"
                else -> false
            }

            if (isHeavy) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            } else {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }

            // 2. Play associated start sound if any
            val spec = getSpecForEvent(e, currentColors)
            spec.soundResId?.let { soundResId ->
                com.axiom.app.core.sound.SoundEngine.play(soundResId)
            }

            // 3. Mark active event and await dismiss trigger
            viewModel.ceremonyEngine.setActiveEvent(e)
            viewModel.ceremonyEngine.dismissChannel.receive()

            // 4. Clear and add 200ms gap
            viewModel.ceremonyEngine.setActiveEvent(null)
            delay(200)
        }
    }

    event?.let { e ->
        val spec = remember(e, currentColors) { getSpecForEvent(e, currentColors) }

        CeremonyContainer(
            spec = spec,
            onDismiss = { viewModel.ceremonyEngine.dismiss() },
            modifier = Modifier.fillMaxSize()
        ) {
            when (e) {
                is CeremonyEvent.MissionComplete -> {
                    MissionCompleteMini(
                        missionTitle = e.missionTitle,
                        rarity       = com.axiom.app.domain.model.LeverageTag.fromString(e.rarity),
                        xpGained     = e.xpGained,
                        onDismiss    = { viewModel.ceremonyEngine.dismiss() },
                        modifier     = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 80.dp)
                    )
                }
                is CeremonyEvent.LevelUp -> {
                    LevelUpCeremony(
                        newLevel = e.newLevel,
                        hunterName = e.hunterName,
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.RankUp -> {
                    RankUpCeremony(
                        oldRank = e.oldRank,
                        newRank = e.newRank,
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.ShadowAcquired -> {
                    ShadowAcquisitionCeremony(
                        skillName = e.skillName,
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.CheckpointCleared -> {
                    CheckpointClearedCeremony(
                        campaignName = e.campaignName,
                        bonusXP = e.bonusXP.toLong(),
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.BossDefeated -> {
                    BossDefeatedCeremony(
                        bossName = e.bossName,
                        bonusXP = e.bonusXP,
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.StreakBroken -> {
                    val shieldCount by viewModel.preferences.streakFreezeFlow.collectAsStateWithLifecycle(initialValue = 0)
                    StreakBrokenOverlay(
                        lostStreak = e.lostStreak,
                        shieldCount = shieldCount,
                        onUseShield = {
                            viewModel.useStreakShield(e.lostStreak)
                            viewModel.ceremonyEngine.dismiss()
                        },
                        onAcceptPenalty = { title, xp ->
                            viewModel.acceptPenalty(title, xp)
                        },
                        onDismissAndNavigateToMissions = {
                            viewModel.ceremonyEngine.dismiss()
                            onNavigateToMissions()
                        },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.StreakMilestone -> {
                    StreakMilestoneOverlay(
                        streakDays = e.streak,
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.StreakShieldUsed -> {
                    StreakShieldUsedOverlay(
                        savedStreak = e.savedStreak,
                        remainingShields = e.remainingShields,
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.FocusComplete -> {
                    // FocusComplete dismisses instantly
                    viewModel.ceremonyEngine.dismiss()
                }
                is CeremonyEvent.WeeklyReviewComplete -> {
                    WeeklyReviewCompleteOverlay(
                        xpGained = e.xpGained,
                        onConfirm = {
                            viewModel.awardWeeklyReviewXP(e.xpGained)
                            viewModel.ceremonyEngine.dismiss()
                        }
                    )
                }
                is CeremonyEvent.IronRuleBreached -> {
                    IronRuleBreachedOverlay(
                        ruleText = e.ruleText,
                        onDismiss = {
                            viewModel.ceremonyEngine.dismiss()
                        },
                        modifier = modifier
                    )
                }
                is CeremonyEvent.SystemAnomaly -> {
                    SystemAnomalyOverlay(
                        tier = e.tier,
                        bonusXP = e.bonusXP,
                        onDismiss = { viewModel.ceremonyEngine.dismiss() },
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun IronRuleBreachedOverlay(
    ruleText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAxiomColors.current.voidBlack.copy(alpha = 0.95f))
            .background(PenaltyRed.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.command_rule_breach_alert),
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = PenaltyRed,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "◈",
                fontFamily = JetBrainsMono,
                fontSize = 58.sp,
                color = PenaltyRed,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.neonGlow(PenaltyRed, intensity = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val isFa = stringResource(id = R.string.setup_lang_fa) == "فارسی"
            val message = if (isFa) {
                "[ فرماندهی ] نقض قانون:\n$ruleText"
            } else {
                "[ COMMAND ] Rule breached:\n$ruleText"
            }

            Text(
                text = message,
                fontFamily = JetBrainsMono,
                fontSize = 16.sp,
                color = PenaltyRed,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.neonGlow(PenaltyRed, intensity = 0.3f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PenaltyRed,
                    contentColor = VoidBlack
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .neonGlow(PenaltyRed, intensity = 0.3f)
            ) {
                Text(
                    text = stringResource(id = R.string.ceremony_resume_protocol),
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SystemAnomalyOverlay(
    tier: String,
    bonusXP: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAxiomColors.current
    val accent = when (tier) {
        "CRITICAL" -> colors.legendaryGold
        "MAJOR" -> colors.epicPurple
        else -> colors.systemGreen
    }
    val tierLabel = when (tier) {
        "CRITICAL" -> "CRITICAL ANOMALY"
        "MAJOR" -> "MAJOR ANOMALY"
        else -> "MINOR ANOMALY"
    }

    var overlayAlphaState by remember { mutableStateOf(0f) }
    var burstTrigger by remember { mutableStateOf(false) }
    var canDismissState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animate(0f, 0.92f, animationSpec = tween(250)) { v, _ -> overlayAlphaState = v }
        burstTrigger = true
        com.axiom.app.core.sound.SoundEngine.play(R.raw.shadow_manifest)
        delay(1400)
        canDismissState = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.voidBlack.copy(alpha = overlayAlphaState))
            .clickable(enabled = canDismissState) { onDismiss() }
            .testTag("system_anomaly_overlay"),
        contentAlignment = Alignment.Center
    ) {
        com.axiom.app.ui.components.CyberParticleBurst(
            trigger = burstTrigger,
            onAnimationComplete = { burstTrigger = false },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "◈",
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 80.sp,
                color = accent,
                modifier = Modifier.testTag("ceremony_glyph")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "[ $tierLabel ]",
                fontFamily = JetBrainsMono,
                fontSize = 15.sp,
                color = accent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("ceremony_title")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "+$bonusXP BONUS XP",
                fontFamily = Fraunces,
                fontSize = 30.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("ceremony_subtitle")
            )
        }

        if (canDismissState) {
            Text(
                text = stringResource(R.string.ceremony_tap_continue),
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = TextDim,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .testTag("ceremony_tap_continue")
            )
        }
    }
}

@Composable
fun WeeklyReviewCompleteOverlay(
    xpGained: Int,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .border(2.dp, com.axiom.app.ui.theme.LegendaryGold, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Weekly Review Completed",
                    color = com.axiom.app.ui.theme.LegendaryGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "مراسم مرور هفتگی با موفقیت به پایان رسید",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .background(com.axiom.app.ui.theme.LegendaryGold.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, com.axiom.app.ui.theme.LegendaryGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "+$xpGained XP",
                        color = com.axiom.app.ui.theme.LegendaryGold,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.axiom.app.ui.theme.LegendaryGold,
                        contentColor = VoidBlack
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Accept & Close",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

private fun getSpecForEvent(event: CeremonyEvent, themeColors: AxiomColorScheme): CeremonySpec {
    return when (event) {
        is CeremonyEvent.LevelUp -> CeremonySpec(
            title = "[ LEVEL UP ]",
            subtitle = "Hunter Level ${event.newLevel}",
            accentColor = themeColors.systemGreen,
            soundResId = R.raw.level_up,
            particleStyle = ParticleStyle.BURST,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.RankUp -> CeremonySpec(
            title = "[ RANK INCREASED ]",
            subtitle = "${event.oldRank}  →  ${event.newRank}",
            accentColor = themeColors.systemGreen,
            soundResId = R.raw.rank_up,
            particleStyle = ParticleStyle.BURST,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.ShadowAcquired -> CeremonySpec(
            title = "[ SHADOW ACQUIRED ]",
            subtitle = event.skillName,
            accentColor = themeColors.legendaryGold,
            soundResId = R.raw.shadow_manifest,
            particleStyle = ParticleStyle.RING,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.CheckpointCleared -> CeremonySpec(
            title = "[ CHECKPOINT CLEARED ]",
            subtitle = event.campaignName,
            accentColor = themeColors.systemGreen,
            soundResId = R.raw.boss_defeated,
            particleStyle = ParticleStyle.BURST,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.BossDefeated -> CeremonySpec(
            title = "[ BOSS DEFEATED ]",
            subtitle = event.bossName,
            accentColor = themeColors.penaltyRed,
            soundResId = R.raw.boss_defeated,
            particleStyle = ParticleStyle.BURST,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.StreakBroken -> CeremonySpec(
            title = "[ STREAK BROKEN ]",
            subtitle = "Lost streak of ${event.lostStreak} days",
            accentColor = themeColors.penaltyRed,
            soundResId = R.raw.system_alert,
            particleStyle = ParticleStyle.NONE,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.StreakMilestone -> CeremonySpec(
            title = "[ PROTOCOL ${event.label} ]",
            subtitle = "Day ${event.streak} streak achieved.",
            accentColor = themeColors.systemGreen,
            soundResId = R.raw.level_up,
            particleStyle = ParticleStyle.RAIN,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.StreakShieldUsed -> CeremonySpec(
            title = "[ SHIELD ACTIVATED ]",
            subtitle = "Saved streak: ${event.savedStreak}",
            accentColor = themeColors.systemGreen,
            soundResId = R.raw.system_alert,
            particleStyle = ParticleStyle.NONE,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.IronRuleBreached -> CeremonySpec(
            title = "[ RULE BREACHED ]",
            subtitle = event.ruleText,
            accentColor = themeColors.penaltyRed,
            soundResId = R.raw.system_alert,
            particleStyle = ParticleStyle.NONE,
            durationMs = 0,
            dismissible = true
        )
        is CeremonyEvent.MissionComplete -> {
            val tag = com.axiom.app.domain.model.LeverageTag.fromString(event.rarity)
            val tier = when (tag) {
                com.axiom.app.domain.model.LeverageTag.DEPTH, com.axiom.app.domain.model.LeverageTag.SHIELD, com.axiom.app.domain.model.LeverageTag.WEALTH_ENGINE -> 3
                com.axiom.app.domain.model.LeverageTag.CRITICAL, com.axiom.app.domain.model.LeverageTag.REVIEW, com.axiom.app.domain.model.LeverageTag.PROTECTED -> 2
                else -> 1
            }
            CeremonySpec(
                title = "[ PROTOCOL RESOLVED ]",
                subtitle = event.missionTitle,
                accentColor = tag.getColor(),
                soundResId = R.raw.mission_complete,
                particleStyle = if (tier == 3) ParticleStyle.RAIN else ParticleStyle.NONE,
                durationMs = when (tier) {
                    1 -> 1200
                    2 -> 2000
                    else -> 0
                },
                dismissible = tier == 3
            )
        }
        is CeremonyEvent.WeeklyReviewComplete -> CeremonySpec(
            title = "[ WEEKLY REVIEW COMPLETE ]",
            subtitle = "+${event.xpGained} XP",
            accentColor = themeColors.legendaryGold,
            soundResId = R.raw.level_up,
            particleStyle = ParticleStyle.BURST,
            durationMs = 0,
            dismissible = false
        )
        is CeremonyEvent.FocusComplete -> CeremonySpec(
            title = "Focus Complete",
            subtitle = "+${event.lpGained} LP",
            accentColor = themeColors.systemGreen,
            soundResId = null,
            particleStyle = ParticleStyle.NONE,
            durationMs = 1,
            dismissible = true
        )
        is CeremonyEvent.SystemAnomaly -> CeremonySpec(
            title = "[ SYSTEM ANOMALY ]",
            subtitle = "+${event.bonusXP} bonus XP",
            accentColor = when (event.tier) {
                "CRITICAL" -> themeColors.legendaryGold
                "MAJOR" -> themeColors.epicPurple
                else -> themeColors.systemGreen
            },
            soundResId = R.raw.shadow_manifest,
            particleStyle = ParticleStyle.BURST,
            durationMs = 0,
            dismissible = true
        )
    }
}
