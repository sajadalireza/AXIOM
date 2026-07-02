package com.axiom.app.presentation.systemvoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.axiom.app.ui.ChatEntry
import com.axiom.app.ui.SystemVoiceViewModel
import com.axiom.app.ui.components.xion.XionChatList
import com.axiom.app.ui.components.xion.XionInputBar
import com.axiom.app.ui.components.xion.XionMessage
import com.axiom.app.ui.components.xion.XionMood
import com.axiom.app.domain.model.WarriorPersona
import androidx.compose.ui.res.stringResource
import com.axiom.app.R
import com.axiom.app.ui.components.ScanlineOverlay
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.components.ScreenHelpButton
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SystemVoiceScreen(
    onNavigate: (String) -> Unit = {},
    viewModel: SystemVoiceViewModel = hiltViewModel()
) {
    val hasApiKey   by viewModel.hasApiKey.collectAsStateWithLifecycle()
    val isLoading   by viewModel.isLoading.collectAsStateWithLifecycle()
    val chat        by viewModel.chat.collectAsStateWithLifecycle()
    val apiKeySaved by viewModel.apiKeySaved.collectAsStateWithLifecycle()
    val hunterLevel by viewModel.hunterLevel.collectAsStateWithLifecycle()
    val isDevBypass by viewModel.isDevBypass.collectAsStateWithLifecycle()

    var showSetupSheet by remember { mutableStateOf(false) }
    var inputText      by remember { mutableStateOf("") }
    val listState      = rememberLazyListState()
    val colors         = LocalAxiomColors.current

    LaunchedEffect(hasApiKey, hunterLevel, isDevBypass) {
        // AI features are locked and inaccessible. Automated key setup is disabled.
        // if (!hasApiKey && (hunterLevel >= 4 || isDevBypass)) showSetupSheet = true
    }
    LaunchedEffect(chat.size) {
        if (chat.isNotEmpty()) listState.animateScrollToItem(chat.size - 1)
    }
    LaunchedEffect(apiKeySaved) { if (apiKeySaved) showSetupSheet = false }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.voidBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── HEADER ───────────────────────────
            SystemVoiceHeader(
                hasApiKey = hasApiKey,
                onSetupKey = { showSetupSheet = true }
            )

            val voiceMode by viewModel.systemVoiceMode.collectAsStateWithLifecycle()

            if (hunterLevel < 4 && !isDevBypass) {
                com.axiom.app.ui.components.CyberLockedFeaturePanel(
                    title = stringResource(id = R.string.system_voice_locked_title),
                    lockedMessage = stringResource(id = R.string.system_voice_locked_msg),
                    requirementLabel = stringResource(id = R.string.system_voice_locked_req),
                    currentProgressLabel = stringResource(id = R.string.system_voice_locked_progress, hunterLevel),
                    progress = hunterLevel.toFloat() / 4f,
                    actionLabel = stringResource(id = R.string.system_voice_locked_action),
                    onActionClick = { onNavigate(com.axiom.app.navigation.Screen.Missions.route) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // System Voice Persona Mode Selector (Replaced with custom WarriorOperativeQueryDirectory mapping table)
                WarriorOperativeQueryDirectory(
                    selectedMode = voiceMode,
                    onSelectPersona = { viewModel.setSystemVoiceMode(it) },
                    onQuickFillQuery = { inputText = it }
                )

                var showGlossary by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    val shown = viewModel.preferences.briefingSystemVoiceFlow.first()
                    showGlossary = !shown
                }
                if (showGlossary) {
                    com.axiom.app.ui.components.GlossaryBriefingCard(
                        stringResId = com.axiom.app.R.string.glossary_system_voice,
                        onDismiss = {
                            showGlossary = false
                            scope.launch {
                                viewModel.preferences.setBriefingShown("system_voice")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // ── CHAT ─────────────────────────────
                val mappedMessages = chat.map { entry ->
                    XionMessage(
                        id = entry.id,
                        text = entry.text,
                        isUser = entry.isUser,
                        timestamp = entry.timestamp,
                        isStreaming = entry.isStreaming
                    )
                }

                XionChatList(
                    messages = mappedMessages,
                    systemColor = colors.systemGreen,
                    xionMood = if (isLoading) XionMood.THINKING else if (chat.lastOrNull()?.isStreaming == true) XionMood.EXCITED else XionMood.IDLE,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                )

                // ── QUICK ACTIONS ────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        stringResource(R.string.system_voice_action_briefing) to stringResource(R.string.system_voice_action_briefing_prompt),
                        stringResource(R.string.system_voice_action_suggest) to stringResource(R.string.system_voice_action_suggest_prompt),
                        stringResource(R.string.system_voice_action_analyze) to stringResource(R.string.system_voice_action_analyze_prompt)
                    ).forEach { (label, prompt) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                                .clickable(enabled = !isLoading) {
                                    viewModel.askSystem(prompt)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = if (hasApiKey) colors.textDim else colors.textDim.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                // ── INPUT ROW ────────────────────────
                val selectedPersona = WarriorPersona.fromId(voiceMode) ?: WarriorPersona.ACCOUNTABILITY_PARTNER
                XionInputBar(
                    value = inputText,
                    onValueChange = { inputText = it },
                    onSend = {
                        viewModel.askSystem(inputText)
                        inputText = ""
                    },
                    isChatLoading = isLoading,
                    selectedPersona = selectedPersona,
                    onPersonaSelected = { viewModel.setSystemVoiceMode(it.id) },
                    systemColor = colors.systemGreen,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )
            }
        }

        ScanlineOverlay()
    }

    if (showSetupSheet) {
        ApiKeySetupSheet(
            onDismiss = { showSetupSheet = false },
            onSave    = { key -> viewModel.saveApiKey(key) }
        )
    }
}

@Composable
private fun SystemVoiceHeader(hasApiKey: Boolean, onSetupKey: () -> Unit) {
    var blink by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { while (true) { delay(800); blink = !blink } }
    val colors = LocalAxiomColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.dimSurface)
            .border(BorderStroke(1.dp, colors.borderFaint))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                stringResource(R.string.system_voice_interface),
                fontFamily = JetBrainsMono,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.systemGreen
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when {
                                hasApiKey && blink -> colors.systemGreen
                                hasApiKey          -> colors.systemGreen.copy(alpha = 0.3f)
                                else               -> colors.textDim
                            }
                        )
                )
                Text(
                    text = if (hasApiKey) stringResource(R.string.system_voice_link_active)
                           else stringResource(R.string.system_voice_no_api_key),
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = if (hasApiKey) colors.textSecondary else colors.textDim
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!hasApiKey) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.borderFaint.copy(alpha = 0.10f))
                        .border(1.dp, colors.borderFaint, RoundedCornerShape(4.dp))
                        .clickable { onSetupKey() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        stringResource(R.string.system_voice_enable_live_ai),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textDim
                    )
                }
            }
            ScreenHelpButton(stringResId = R.string.glossary_system_voice)
        }
    }
}

@Composable
private fun SystemBubble(text: String) {
    var displayed by remember(text) { mutableStateOf("") }
    val colors = LocalAxiomColors.current
    LaunchedEffect(text) {
        text.forEach { ch -> displayed += ch; delay(16) }
    }
    Column(
        modifier = Modifier.fillMaxWidth(0.88f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = displayed,
            fontFamily = JetBrainsMono,
            fontSize = 13.sp,
            color = colors.textPrimary,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    val colors = LocalAxiomColors.current
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Text(
            "> $text",
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun LoadingBubble() {
    var dots by remember { mutableStateOf("") }
    val colors = LocalAxiomColors.current
    LaunchedEffect(Unit) {
        while (true) {
            delay(400)
            dots = when (dots) { "" -> "." ; "." -> ".." ; ".." -> "..." ; else -> "" }
        }
    }
    Text(
        "[ SYSTEM ] $dots",
        fontFamily = JetBrainsMono,
        fontSize = 13.sp,
        color = colors.systemGreen.copy(alpha = 0.7f)
    )
}

// ─── API KEY SETUP SHEET ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeySetupSheet(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var key     by remember { mutableStateOf("") }
    var showKey by remember { mutableStateOf(false) }
    val colors  = LocalAxiomColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.shadowSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.system_voice_key_required),
                fontFamily = JetBrainsMono,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.systemGreen
            )
            Text(
                stringResource(R.string.system_voice_key_desc),
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = colors.textSecondary,
                lineHeight = 17.sp
            )
            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
            Text(
                text = stringResource(R.string.system_voice_key_get),
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = colors.systemGreen,
                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline),
                modifier = Modifier
                    .clickable {
                        try {
                            uriHandler.openUri("https://aistudio.google.com/app/apikey")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    .padding(vertical = 4.dp)
            )

            // Key input using existing OutlinedTextField with elegant Styling
            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.system_voice_key_label), fontFamily = JetBrainsMono, fontSize = 10.sp, color = colors.textDim)
                },
                placeholder = {
                    Text(
                        stringResource(R.string.system_voice_paste_here),
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        color = colors.textDim
                    )
                },
                visualTransformation = if (showKey) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    TextButton(onClick = { showKey = !showKey }) {
                        Text(
                            if (showKey) stringResource(R.string.system_voice_hide) else stringResource(R.string.system_voice_show),
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = colors.textDim
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = colors.systemGreen,
                    unfocusedBorderColor = colors.borderFaint,
                    focusedTextColor     = colors.textPrimary,
                    unfocusedTextColor   = colors.textPrimary,
                    cursorColor          = colors.systemGreen
                )
            )

            val canSave = key.length > 10
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (canSave) colors.systemGreen.copy(alpha = 0.15f) else colors.dimSurface)
                    .border(
                        1.dp,
                        if (canSave) colors.systemGreen.copy(alpha = 0.5f) else colors.borderFaint,
                        RoundedCornerShape(4.dp)
                    )
                    .then(if (canSave) Modifier.clickable { onSave(key) } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.system_voice_activate),
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canSave) colors.systemGreen else colors.textDim
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun WarriorOperativeQueryDirectory(
    selectedMode: String,
    onSelectPersona: (String) -> Unit,
    onQuickFillQuery: (String) -> Unit
) {
    val colors = LocalAxiomColors.current
    var isExpanded by remember { mutableStateOf(true) }

    val mappings = remember {
        listOf(
            PersonaQueryRow(
                id = "research_scientist",
                name = "Research Scientist",
                faName = "محقق علمی",
                role = "Yeast Bio & Genome-Scale Models",
                faQuestions = "مدل‌سازی متابولیک، شبیه‌سازی FBA، کادون‌ها، کوبراپای",
                suggestedPrompt = "چگونه می‌توان مدل‌های در مقیاس ژنوم (GEMs) را برای سویه‌های مخمر کالیبره کرد؟"
            ),
            PersonaQueryRow(
                id = "ml_engineer",
                name = "ML Engineer",
                faName = "مهندس یادگیری ماشین",
                role = "Production & GNN Scaling",
                faQuestions = "پایپ‌لاین‌های پایتورچ، داکر، شبکه‌های GNN، ساختار داده بیو",
                suggestedPrompt = "بهترین الگو برای پردازش و توکنایز کردن دنباله ژنی مخمر جهت آموزش مدل ESM-2 چیست؟"
            ),
            PersonaQueryRow(
                id = "startup_advisor",
                name = "Startup Advisor",
                faName = "مشاور استارتاپ",
                role = "Biotech Pricing & USD Consulting",
                faQuestions = "قراردادهای مشاوره دلاری، کشف کلاینت خارجی، مدل تجاری",
                suggestedPrompt = "چگونه ساختار ارزش مشاوره فنی بیوتک را به صورت دلاری (USD) تنظیم کنم؟"
            ),
            PersonaQueryRow(
                id = "english_coach",
                name = "English Coach",
                faName = "مربی زبان انگلیسی",
                role = "Outreach Polish & Presentation",
                faQuestions = "اصلاح ایمیل‌های رسمی، تصحیح متن، نگارش پروپوزال",
                suggestedPrompt = "ایمیل سرد من برای ارتباط با آزمایشگاه بین‌المللی Strain Engineering را تصحیح کن."
            ),
            PersonaQueryRow(
                id = "market_intel",
                name = "Market Intelligence",
                faName = "تحلیل بازار",
                role = "Biotech Industry Demands",
                faQuestions = "چالش‌های صنعتی رآکتورها در اروپا، ترند فرآوری آنزیم",
                suggestedPrompt = "گران‌ترین مشکلات فنی کارخانجات تخمیر صنعتی در آلمان و هلند چه مواردی هستند؟"
            ),
            PersonaQueryRow(
                id = "publishing_coach",
                name = "Publishing Coach",
                faName = "هدایت نشر علمی",
                role = "Academic Preprints & Strategy",
                faQuestions = "برنامه‌ریزی نگارش مقاله، رفع داوری، انتخاب ژورنال برتر",
                suggestedPrompt = "برای پاسخ به ایراد داور درباره کارایی ترمیناتور مخمر چه شبیه‌سازی تکمیلی لازم است؟"
            ),
            PersonaQueryRow(
                id = "accountability_partner",
                name = "Accountability Partner",
                faName = "ناظر نظم و خواب",
                role = "Enforcing Schedule & KPIs",
                faQuestions = "پایبندی به بازه‌های تمرکز، رفع بی‌خوابی مفرط، مانیتور پیشرفت",
                suggestedPrompt = "امروز کارهای غیرمرتبط انجام دادم. چطور بهره‌وری کارهای سخت را برگردانم؟"
            ),
            PersonaQueryRow(
                id = "ruthless_critic",
                name = "Ruthless Critic",
                faName = "منتقد صریح",
                role = "Reality-Checking & Excuses",
                faQuestions = "واقع‌بینی مالی محض، ابهام‌زدایی از اقدامات خیالی، رفع تنبلی",
                suggestedPrompt = "برنامه‌ریزی فردا را ببین؛ کجای کار زیاده‌روی است و اقدامات واقعی کجاست؟"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(colors.dimSurface, shape = RoundedCornerShape(4.dp))
            .border(1.dp, colors.borderFaint, shape = RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🛠️ WAR ROOM OPERATIVE DIRECTORY",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.systemGreen
                )
                Text(
                    text = if (isExpanded) "[-]" else "[+]",
                    fontFamily = JetBrainsMono,
                    fontSize = 9.sp,
                    color = colors.textDim
                )
            }
            Text(
                text = "کدام پرسونا برای کدام سؤال؟",
                fontFamily = Inter,
                fontSize = 10.sp,
                color = colors.textDim
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = colors.borderFaint, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            mappings.forEach { row ->
                val isSelected = selectedMode.equals(row.id, ignoreCase = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) colors.systemGreen.copy(alpha = 0.08f) else Color.Transparent)
                        .clickable {
                            onSelectPersona(row.id)
                            onQuickFillQuery(row.suggestedPrompt)
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.45f)) {
                        Text(
                            text = row.name,
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colors.systemGreen else colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = row.role,
                            fontFamily = Inter,
                            fontSize = 8.sp,
                            color = colors.textDim
                        )
                    }
                    Column(
                        modifier = Modifier.weight(0.55f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = row.faQuestions,
                            fontFamily = Inter,
                            fontSize = 10.sp,
                            color = if (isSelected) colors.systemGreen.copy(alpha = 0.9f) else colors.textSecondary,
                            textAlign = TextAlign.Right
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "💡 نمونه: " + row.suggestedPrompt,
                            fontFamily = Inter,
                            fontSize = 8.sp,
                            color = colors.textDim,
                            textAlign = TextAlign.Right
                        )
                    }
                }
                HorizontalDivider(color = colors.borderFaint.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

private data class PersonaQueryRow(
    val id: String,
    val name: String,
    val faName: String,
    val role: String,
    val faQuestions: String,
    val suggestedPrompt: String
)
