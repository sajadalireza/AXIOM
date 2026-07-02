package com.axiom.app.ui.components.xion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.ui.XionViewModel
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun XionDialogContent(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    activeCustomColor: Color?,
    onColorChange: (Color?) -> Unit,
    viewModel: XionViewModel
) {
    val colors      = LocalAxiomColors.current
    val haptic      = LocalHapticFeedback.current
    val systemColor = activeCustomColor ?: colors.systemGreen

    val snapshot        by viewModel.snapshot.collectAsStateWithLifecycle()
    val chatHistory     by viewModel.chatHistory.collectAsStateWithLifecycle()
    val isChatLoading   by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val language        by viewModel.languageState.collectAsStateWithLifecycle()
    val isFa = language == "fa" || java.util.Locale.getDefault().language == "fa"
    var isChatMode      by remember { mutableStateOf(false) }
    var chatInput       by remember { mutableStateOf("") }

    var terminalAiPending by remember { mutableStateOf(false) }

    var clickCount    by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }
    var isTerminalOpen by remember { mutableStateOf(false) }
    var terminalOutput by remember { mutableStateOf("") }

    val greetText            = stringResource(R.string.xion_companion_greet)
    var fullSpeechText       by remember { mutableStateOf(greetText) }
    var textTickerKey        by remember { mutableStateOf(0) }
    var glitchOffset         by remember { mutableStateOf(0.dp) }
    var glitchAlpha          by remember { mutableStateOf(1.0f) }
    var isGlitching          by remember { mutableStateOf(true) }

    LaunchedEffect(chatHistory) {
        if (terminalAiPending && chatHistory.isNotEmpty()) {
            val last = chatHistory.last()
            if (!last.first) {   // isUser=false → AI reply
                terminalOutput    = "[ SYSTEM RESPONSE ]\n${last.second}"
                terminalAiPending = false
            }
        }
    }

    LaunchedEffect(fullSpeechText, textTickerKey) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        isGlitching = true
        repeat(7) { idx ->
            glitchOffset = if (idx % 2 == 0) ((-4..4).random()).dp else 0.dp
            glitchAlpha  = if (idx % 3 == 0) 0.65f else 1.0f
            delay(50)
        }
        glitchOffset = 0.dp; glitchAlpha = 1.0f; isGlitching = false
    }

    val hologramT = rememberInfiniteTransition(label = "hologram")
    val scanlineProgress by hologramT.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "scanline"
    )

    var displayedText by remember(textTickerKey) { mutableStateOf("") }
    LaunchedEffect(fullSpeechText, textTickerKey) {
        val pool = listOf('$','#','@','%','&','*','?','!','X','1','0','◈','▲','▼','[',']','§','¢','★','⚡')
        val len  = fullSpeechText.length
        displayedText = ""
        for (i in 0..len) {
            val decrypted  = fullSpeechText.take(i)
            val scrambled  = (0 until min(3, len - i)).map { pool.random() }.joinToString("")
            displayedText  = decrypted + scrambled
            delay(16)
        }
        displayedText = fullSpeechText
    }

    fun playButtonFeedback() = haptic.performHapticFeedback(HapticFeedbackType.LongPress)

    val currentMood = remember(isTerminalOpen, isGlitching, fullSpeechText, snapshot) {
        when {
            isGlitching                                                   -> XionMood.GLITCHED
            isTerminalOpen                                                 -> XionMood.THINKING
            snapshot.inactiveDays >= 3                                    -> XionMood.WARNING
            snapshot.streakDays >= 30                                     -> XionMood.EXCITED
            fullSpeechText.contains("conquer",        ignoreCase = true) ||
            fullSpeechText.contains("success",        ignoreCase = true) ||
            fullSpeechText.contains("hello",          ignoreCase = true) ||
            fullSpeechText.contains("ready",          ignoreCase = true) ||
            fullSpeechText.contains("congratulations",ignoreCase = true) ||
            snapshot.completedTodayCount > 0                             -> XionMood.HAPPY
            fullSpeechText.contains("missed",  ignoreCase = true) ||
            fullSpeechText.contains("fail",    ignoreCase = true) ||
            fullSpeechText.contains("penalty", ignoreCase = true) ||
            fullSpeechText.contains("spectre", ignoreCase = true) ||
            snapshot.streakDays == 0                                      -> XionMood.SAD
            else                                                          -> XionMood.IDLE
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = glitchOffset)
                .graphicsLayer { alpha = glitchAlpha }
                .clip(RoundedCornerShape(12.dp))
                .background(colors.voidBlack)
                .border(1.5.dp, systemColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(systemColor.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(size.width / 2f, 0f),
                            radius = size.width * 0.7f
                        ),
                        radius = size.width * 0.7f,
                        center = Offset(size.width / 2f, 0f)
                    )
                }
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.xion_companion_title),
                        color = systemColor, fontFamily = JetBrainsMono,
                        fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(systemColor.copy(alpha = 0.12f))
                            .border(1.dp, systemColor.copy(alpha = 0.4f), CircleShape)
                            .clickable { playButtonFeedback(); onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = systemColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier.size(110.dp)
                        .background(colors.dimSurface, CircleShape)
                        .border(2.dp, systemColor.copy(alpha = 0.40f), CircleShape)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            val now = System.currentTimeMillis()
                            if (now - lastClickTime < 500) clickCount++ else clickCount = 1
                            lastClickTime = now
                            if (clickCount >= 5) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isTerminalOpen = !isTerminalOpen; clickCount = 0
                                if (isTerminalOpen) {
                                    terminalOutput = "AXIOM CORE CONSOLE TERMINAL v2.0\nSECURITY LEVEL: HUNTER_MAX\n\nType 'help' for command directory.\n"
                                    isChatMode = false
                                } else { fullSpeechText = greetText; textTickerKey++ }
                            } else {
                                if (clickCount == 1) {
                                    textTickerKey++
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(6.dp).border(1.dp, systemColor.copy(alpha = 0.20f), CircleShape))
                    XionLivingEyeAvatar(
                        modifier = Modifier.size(98.dp),
                        systemColor = systemColor,
                        isActiveSpeaking = displayedText != fullSpeechText,
                        textTickerKey = textTickerKey,
                        mood = currentMood
                    )
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).drawBehind {
                        val lineY = size.height * scanlineProgress
                        drawLine(color = systemColor, start = Offset(0f, lineY), end = Offset(size.width, lineY), strokeWidth = 2.dp.toPx())
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(Color.Transparent, systemColor.copy(alpha = 0.12f), Color.Transparent),
                                startY = maxOf(0f, lineY - 12.dp.toPx()),
                                endY   = minOf(size.height, lineY + 12.dp.toPx())
                            ),
                            topLeft = Offset(0f, maxOf(0f, lineY - 12.dp.toPx())),
                            size    = androidx.compose.ui.geometry.Size(size.width, minOf(size.height, lineY + 12.dp.toPx()) - maxOf(0f, lineY - 12.dp.toPx()))
                        )
                    })
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(
                        Brush.verticalGradient(listOf(systemColor.copy(alpha = 0.12f), Color.Transparent, systemColor.copy(alpha = 0.22f)))
                    ))
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.borderFaint))

                if (!isTerminalOpen) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors.shadowSurface)
                            .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                    ) {
                        listOf((if (isFa) "راهنما" else "GUIDE") to false, (if (isFa) "گفتگو" else "CHAT") to true).forEach { (label, isChat) ->
                            val isActive = isChatMode == isChat
                            Box(
                                modifier = Modifier.weight(1f)
                                    .background(if (isActive) systemColor.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { isChatMode = isChat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                textTickerKey++
                                Text(label, fontFamily = JetBrainsMono, fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) systemColor else colors.textDim)
                            }
                        }
                    }
                }

                when {
                    isTerminalOpen -> {
                        TerminalConsolePanel(
                            systemColor = systemColor,
                            onCommandEntered = { cmd ->
                                val s = snapshot
                                val trimmed = cmd.trim().lowercase()
                                val hackPrefixes = listOf("opensajadaghakishi", "setlevel ", "setrank ", "setstreak ", "addxp ", "godmode", "lockall", "lock")
                                val isHackCommand = hackPrefixes.any { trimmed == it || trimmed.startsWith(it) }

                                if (isHackCommand) {
                                    viewModel.executeDevHack(cmd) { result ->
                                        terminalOutput = result
                                    }
                                } else {
                                    when {
                                        trimmed == "help" -> {
                                            val isBypass = viewModel.devBypassFlow.value
                                            val devCommands = if (isBypass) """
                                              |
                                              |SECRET DEV COMMANDS:
                                              |  setlevel [1..100]  — force level modifier
                                              |  setrank [name]     — override title designation
                                              |  setstreak [days]   — set consistency streak
                                              |  addxp [amount]     — award instant resonance
                                              |  godmode            — max Sovereign potential
                                              |  lock               — close all developer overrides
                                            """.trimMargin() else ""

                                            terminalOutput = """
                                        AVAILABLE COMMANDS:
                                          profile      — hunter classification data
                                          streak       — streak status report
                                          missions     — active mission count
                                          xp           — XP and level progress
                                          ai [text]    — query dimensional intelligence
                                          system_red   — spectral override: red-alert
                                          system_gold  — spectral override: legendary gold
                                          system_purple— spectral override: epic purple
                                          system_green — revert to standard green
                                          overload     — core power cycle
                                          shutdown     — exit tactical console$devCommands
                                            """.trimIndent()
                                        }
                                        trimmed == "profile" -> {
                                            terminalOutput = """
                                        [ HUNTER PROFILE ]
                                        NAME     : ${s.hunterName.uppercase()}
                                        RANK     : ${s.rankLabel.uppercase()}
                                        STREAK   : ${s.streakDays} DAYS
                                        TOTAL XP : ${s.totalXP}
                                        STATUS   : ${if (s.inactiveDays >= 3) "⚠ INACTIVE — ${s.inactiveDays} DAYS OFFLINE" else "ACTIVE"}
                                            """.trimIndent()
                                        }
                                        trimmed == "streak" -> {
                                            val verdict = when {
                                                s.streakDays == 0  -> "⚠ NO ACTIVE STREAK. PENALTY RISK HIGH."
                                                s.inactiveDays >= 2 -> "⚠ ${s.inactiveDays} DAYS SINCE LAST PROTOCOL."
                                                s.streakDays >= 30 -> "★ ELITE STREAK — LEGENDARY CONSISTENCY."
                                                s.streakDays >= 7  -> "STREAK STABLE — MAINTAIN CADENCE."
                                                else               -> "STREAK ACTIVE — KEEP GOING."
                                            }
                                            terminalOutput = "STREAK STATUS: ${s.streakDays} DAYS\n$verdict"
                                        }
                                        trimmed == "missions" -> {
                                            terminalOutput = """
                                        MISSION STATUS:
                                          ACTIVE  : ${s.activeMissionCount}
                                          TODAY   : ${s.completedTodayCount} COMPLETED
                                        VERDICT : ${if (s.activeMissionCount == 0) "⚠ MISSION QUEUE EMPTY" else "PROTOCOLS IN PROGRESS"}
                                            """.trimIndent()
                                        }
                                        trimmed == "xp" -> {
                                            terminalOutput = "TOTAL XP BANKED: ${s.totalXP}\nRANK: ${s.rankLabel.uppercase()}\nLOG: Accumulation rate nominal."
                                        }
                                        trimmed.startsWith("ai ") -> {
                                            val query = trimmed.removePrefix("ai ").trim()
                                            if (query.isBlank()) {
                                                terminalOutput = "USAGE: ai [your question]\nEXAMPLE: ai how do I improve my streak?"
                                            } else {
                                                terminalAiPending = true
                                                terminalOutput    = "[ CONNECTING TO DIMENSIONAL INTERFACE... ]"
                                                viewModel.sendChat(query)
                                            }
                                        }
                                        trimmed == "system_red"    -> { onColorChange(colors.penaltyRed);    terminalOutput = "SPECTRAL OVERRIDE COMPLETED.\nCORE WAVE: PENALTY RED." }
                                        trimmed == "system_gold"   -> { onColorChange(colors.legendaryGold); terminalOutput = "SPECTRAL OVERRIDE COMPLETED.\nCORE WAVE: LEGENDARY GOLD." }
                                        trimmed == "system_purple" -> { onColorChange(colors.epicPurple);    terminalOutput = "SPECTRAL OVERRIDE COMPLETED.\nCORE WAVE: EPIC PURPLE." }
                                        trimmed == "system_green"  -> { onColorChange(null);                 terminalOutput = "SPECTRAL RESET SUCCESSFUL.\nCOGNITIVE GREEN RESTORED." }
                                        trimmed == "overload"      -> { terminalOutput = "WARNING: CORE VOLTAGE SPIKE!\nOVERLOAD DEPLOYED."; playButtonFeedback(); textTickerKey++ }
                                        trimmed == "shutdown"      -> { isTerminalOpen = false; terminalOutput = ""; fullSpeechText = greetText; textTickerKey++ }
                                        else -> { terminalOutput = "UNKNOWN: '$trimmed'\nType 'help' for command directory." }
                                    }
                                }
                            },
                            outputLog = terminalOutput,
                            onCloseTerminal = { isTerminalOpen = false; terminalOutput = ""; fullSpeechText = greetText; textTickerKey++ }
                        )
                    }

                    isChatMode -> {
                        val quickReplies = if (isFa) listOf(
                            "من چطورم؟", "ایده مأموریت بده.",
                            "امروز غایب بودم. الآن چیکار کنم؟", "به من انگیزه بده."
                        ) else listOf(
                            "How am I doing?", "Give me a mission idea.",
                            "I missed today. What now?", "Motivate me."
                        )
                        quickReplies.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { q ->
                                    Box(
                                        modifier = Modifier.weight(1f)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(systemColor.copy(alpha = 0.1f))
                                            .border(1.dp, systemColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                            .clickable(enabled = !isChatLoading) { viewModel.sendChat(q) }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(q, fontFamily = JetBrainsMono, fontSize = 9.sp,
                                            color = systemColor, lineHeight = 12.sp)
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 170.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            chatHistory.takeLast(5).forEach { (isUser, text) ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isUser) systemColor.copy(alpha = 0.12f) else colors.dimSurface)
                                        .border(1.dp,
                                            if (isUser) systemColor.copy(alpha = 0.4f) else colors.borderFaint,
                                            RoundedCornerShape(4.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text       = if (isUser) "> $text" else text,
                                        fontFamily = JetBrainsMono,
                                        fontSize   = 10.sp,
                                        color      = if (isUser) systemColor else colors.textSecondary,
                                        lineHeight  = 14.sp
                                    )
                                }
                            }
                            if (isChatLoading) {
                                Text(if (isFa) "[ در حال پردازش... ]" else "[ PROCESSING... ]", fontFamily = JetBrainsMono,
                                    fontSize = 10.sp, color = systemColor,
                                    modifier = Modifier.padding(4.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value         = chatInput,
                                onValueChange = { chatInput = it },
                                textStyle     = TextStyle(fontFamily = JetBrainsMono, fontSize = 11.sp, color = colors.textPrimary),
                                cursorBrush   = SolidColor(systemColor),
                                singleLine    = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = {
                                    if (chatInput.isNotBlank() && !isChatLoading) { viewModel.sendChat(chatInput); chatInput = "" }
                                }),
                                modifier = Modifier.weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(colors.dimSurface)
                                    .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                decorationBox = { inner ->
                                    if (chatInput.isEmpty()) Text(if (isFa) "از سیستم بپرسید..." else "Ask the system...",
                                        fontFamily = JetBrainsMono, fontSize = 11.sp, color = colors.textDim)
                                    inner()
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (chatInput.isNotBlank()) systemColor.copy(alpha = 0.2f) else colors.shadowSurface)
                                    .border(1.dp, if (chatInput.isNotBlank()) systemColor else colors.borderFaint, RoundedCornerShape(4.dp))
                                    .clickable(enabled = chatInput.isNotBlank() && !isChatLoading) {
                                        viewModel.sendChat(chatInput); chatInput = ""
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(if (isFa) "ارسال" else "SEND", fontFamily = JetBrainsMono, fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chatInput.isNotBlank()) systemColor else colors.textDim)
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.dimSurface)
                                .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                                .padding(12.dp)
                        ) {
                            Text(displayedText, color = colors.textSecondary,
                                fontFamily = JetBrainsMono, fontSize = 12.sp,
                                lineHeight = 18.sp, modifier = Modifier.fillMaxWidth())
                        }

                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val homeTip     = stringResource(R.string.xion_companion_home_tip)
                            val missionsTip = stringResource(R.string.xion_companion_missions_tip)
                            val dungeonsTip = stringResource(R.string.xion_companion_dungeons_tip)
                            val shadowsTip  = stringResource(R.string.xion_companion_shadows_tip)

                            ShortcutGuideButton(if (isFa) "◈ راهنمای صفحه: پنل اصلی خانه" else "◈ SCREEN GUIDE: HOME PANEL",    fullSpeechText == homeTip,     systemColor) { playButtonFeedback(); fullSpeechText = homeTip;     textTickerKey++ }
                            ShortcutGuideButton(if (isFa) "◈ پروتکل کوئست: لیست مأموریت‌ها" else "◈ QUEST PROTOCOL: MISSIONS LIST", fullSpeechText == missionsTip, systemColor) { playButtonFeedback(); fullSpeechText = missionsTip; textTickerKey++ }
                            ShortcutGuideButton(if (isFa) "◈ سیستم رید: فتح دانجن‌ها" else "◈ RAID SYSTEM: DUNGEONS CONQUER", fullSpeechText == dungeonsTip, systemColor) { playButtonFeedback(); fullSpeechText = dungeonsTip; textTickerKey++ }
                            ShortcutGuideButton(if (isFa) "◈ فرماندهی سپاه: آرشیو سایه‌ها" else "◈ ARMY COMMAND: SHADOW ARCHIVE",  fullSpeechText == shadowsTip,  systemColor) { playButtonFeedback(); fullSpeechText = shadowsTip;  textTickerKey++ }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutGuideButton(label: String, isActive: Boolean, systemColor: Color, onClick: () -> Unit) {
    val colors = LocalAxiomColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (isPressed) 0.96f else 1f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "btn_scale"
    )
    Row(
        modifier = Modifier.fillMaxWidth().graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(4.dp))
            .background(if (isActive) systemColor.copy(alpha = 0.15f) else colors.shadowSurface)
            .border(1.dp, if (isActive) systemColor else colors.borderFaint, RoundedCornerShape(4.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (isActive) systemColor else colors.textSecondary,
            fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TerminalConsolePanel(systemColor: Color, onCommandEntered: (String) -> Unit, outputLog: String, onCloseTerminal: () -> Unit) {
    val colors = LocalAxiomColors.current
    var inputVal by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(colors.voidBlack)
            .border(1.dp, systemColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(130.dp)
            .background(colors.dimSurface)
            .border(1.dp, colors.borderFaint, RoundedCornerShape(2.dp))
            .padding(8.dp)
        ) {
            Text(outputLog, color = systemColor, fontFamily = JetBrainsMono,
                fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.fillMaxSize())
        }
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(colors.dimSurface)
                .border(1.dp, colors.borderFaint, RoundedCornerShape(2.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("xion@axiom:~$ ", color = systemColor, fontFamily = JetBrainsMono,
                fontSize = 11.sp, fontWeight = FontWeight.Bold)
            BasicTextField(
                value = inputVal, onValueChange = { inputVal = it },
                textStyle = TextStyle(color = systemColor, fontFamily = JetBrainsMono, fontSize = 11.sp),
                cursorBrush = SolidColor(systemColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (inputVal.isNotBlank()) { onCommandEntered(inputVal); inputVal = "" } }),
                modifier = Modifier.weight(1f), singleLine = true
            )
            Box(
                modifier = Modifier.clip(RoundedCornerShape(2.dp))
                    .background(systemColor.copy(alpha = 0.15f))
                    .clickable { if (inputVal.isNotBlank()) { onCommandEntered(inputVal); inputVal = "" } }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) { Text("EXE", color = systemColor, fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
        }
        Text("Type 'shutdown' to resume manual HUD directives.", color = colors.textDim,
            fontFamily = JetBrainsMono, fontSize = 9.sp, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
    }
}
