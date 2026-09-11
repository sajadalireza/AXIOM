package com.axiom.app.presentation.missions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.domain.xion.*
import com.axiom.app.ui.XionDecisionViewModel
import com.axiom.app.ui.components.RarityBadge
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XionDecisionSheet(
    onDismiss: () -> Unit,
    viewModel: XionDecisionViewModel = hiltViewModel()
) {
    val goal by viewModel.goal.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedIds.collectAsStateWithLifecycle()
    val insight by viewModel.contextualInsight.collectAsStateWithLifecycle()
    val quota by viewModel.quotaStatus.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOfflineFallback.collectAsStateWithLifecycle()
    val created by viewModel.missionsCreated.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val totalXp by viewModel.totalSelectedXp.collectAsStateWithLifecycle()

    val isFa = language == "fa"

    // Dialog states for editing, rejecting, and reporting
    var editingSuggestion by remember { mutableStateOf<XionSuggestion?>(null) }
    var rejectingSuggestionId by remember { mutableStateOf<String?>(null) }
    var reportingSuggestionId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(created) {
        if (created) {
            delay(900)
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ShadowSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. HEADER & CLOSE ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (isFa) "لایه تصمیم‌گیری زایان (کوپایلوت شناختی)" else "XION DECISION LAYER",
                        fontFamily = JetBrainsMono,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen
                    )
                    Text(
                        text = if (isFa) "شکستن اهداف به مأموریت‌های ملموس و اتصال به الگوها" else "Cognitive copilot & template-first mission decomposition",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextDim
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp).testTag("btn_close_xion_sheet")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDim)
                }
            }

            // ── 2. QUOTA STATUS CARD ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (quota.isExhausted) PenaltyRed.copy(alpha = 0.08f) else SystemGreen.copy(alpha = 0.06f))
                    .border(
                        1.dp,
                        if (quota.isExhausted) PenaltyRed.copy(alpha = 0.3f) else SystemGreen.copy(alpha = 0.25f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFa) {
                            if (quota.isExhausted) "سهمیه هوش مصنوعی امروز تکمیل شد (استفاده از الگوهای محلی)"
                            else "سهمیه روزانه زایان: ${quota.remainingToday} از ${quota.dailyLimit} باقی‌مانده"
                        } else {
                            if (quota.isExhausted) "Daily AI Quota Reached (Using Verified Offline Catalog)"
                            else "⚡ DAILY QUOTA: ${quota.remainingToday} / ${quota.dailyLimit} REMAINING"
                        },
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (quota.isExhausted) PenaltyRed else SystemGreen
                    )

                    if (isOffline) {
                        Text(
                            text = "[ OFFLINE ]",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            color = LegendaryGold
                        )
                    }
                }
            }

            // ── 3. GOAL INPUT & PRESET CHIPS ─────────────────────────────────────
            TerminalTextField(
                value = goal,
                onValueChange = { viewModel.setGoal(it) },
                label = if (isFa) "هدف اجرایی نرم‌افزار / سوله‌پرنر" else "TARGET EXECUTION OBJECTIVE",
                placeholder = {
                    Text(
                        text = if (isFa) "مثلاً: پیاده‌سازی احراز هویت یا بازبینی صفحه قیمت‌گذاری..."
                        else "e.g. Ship authentication flow or audit pricing page...",
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextDim
                    )
                },
                modifier = Modifier.fillMaxWidth().testTag("field_xion_goal"),
                singleLine = false
            )

            // Preset quick chips
            val presets = if (isFa) {
                listOf("رفع باگ حیاتی", "مصاحبه با ۵ کاربر", "بازبینی صفحه قیمت‌گذاری", "ریفکتور ماژول پایگاه داده")
            } else {
                listOf("Ship critical bug fix", "5 User discovery interviews", "Audit pricing page", "Refactor database queries")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DimSurface)
                            .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                            .clickable { viewModel.setGoal(preset) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = preset,
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // ── 4. ACTIVATE / SYNTHESIZE BUTTON ──────────────────────────────────
            val canActivate = goal.trim().length >= 3 && !isLoading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (canActivate) SystemGreen.copy(alpha = 0.15f) else DimSurface)
                    .border(
                        1.dp,
                        if (canActivate) SystemGreen else BorderFaint,
                        RoundedCornerShape(6.dp)
                    )
                    .then(
                        if (canActivate) Modifier.clickable { viewModel.generateDecision() }
                        else Modifier
                    )
                    .testTag("btn_activate_xion"),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    Text(
                        text = if (isFa) "[ در حال تحلیل مسیر بهینه شناختی... ]" else "[ SYNTHESIZING OPTIMAL PATH... ]",
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen
                    )
                } else {
                    Text(
                        text = if (isFa) "⚡ فعال‌سازی کوپایلوت شناختی (الگومحور)" else "⚡ ACTIVATE COGNITIVE COPILOT",
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canActivate) SystemGreen else TextDim
                    )
                }
            }

            // ── 5. CONTEXTUAL INSIGHT CARD ───────────────────────────────────────
            insight?.let { ins ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(RareBlue.copy(alpha = 0.08f))
                        .border(1.dp, RareBlue.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isFa) "💡 بینش شناختی زایان" else "💡 XION CONTEXTUAL INSIGHT",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RareBlue
                            )
                            Text(
                                text = "[ ${ins.frictionLevel} FRICTION ]",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = when (ins.frictionLevel) {
                                    "HIGH" -> PenaltyRed
                                    "OPTIMAL" -> SystemGreen
                                    else -> RareBlue
                                }
                            )
                        }
                        Text(
                            text = if (isFa) ins.insightFa else ins.insightEn,
                            fontFamily = Inter,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 17.sp
                        )
                        val tip = if (isFa) ins.leverageTipFa else ins.leverageTipEn
                        if (!tip.isNullOrBlank()) {
                            Text(
                                text = "➜ $tip",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = LegendaryGold,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // ── 6. SUGGESTIONS LIST ──────────────────────────────────────────────
            val activeSuggestions = suggestions.filter {
                it.status != SuggestionStatus.REJECTED && it.status != SuggestionStatus.REPORTED
            }

            if (activeSuggestions.isNotEmpty()) {
                Text(
                    text = if (isFa) "مأموریت‌های پیشنهادی زایان (قابل ویرایش و اعتبارسنجی)" else "TAILORED RECOMMENDATIONS",
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SystemGreen
                )

                activeSuggestions.forEach { item ->
                    XionSuggestionCard(
                        suggestion = item,
                        isSelected = selectedIds.contains(item.id),
                        onToggle = { viewModel.toggleSelection(item.id) },
                        onEdit = { editingSuggestion = item },
                        onReject = { rejectingSuggestionId = item.id },
                        onReport = { reportingSuggestionId = item.id },
                        isPersian = isFa
                    )
                }
            }

            // ── 7. COMMIT & REGISTER BUTTON ──────────────────────────────────────
            if (selectedIds.isNotEmpty()) {
                val xpBonus = if (totalXp > 0) " (+$totalXp XP)" else ""
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SystemGreen.copy(alpha = 0.2f))
                        .border(1.dp, SystemGreen, RoundedCornerShape(6.dp))
                        .clickable { viewModel.acceptSelected() }
                        .testTag("btn_accept_xion_suggestions"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (created) {
                            if (isFa) "ثبت شد در سیستم!" else "MISSIONS REGISTERED!"
                        } else {
                            if (isFa) "تایید و ثبت مأموریت‌های منتخب$xpBonus" else "CONFIRM & REGISTER MISSIONS$xpBonus"
                        },
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }

    // ── DIALOG 1: EDIT SUGGESTION ────────────────────────────────────────────
    editingSuggestion?.let { target ->
        var editTitle by remember { mutableStateOf(target.title) }
        var editHours by remember { mutableStateOf(target.estimatedHours.toString()) }
        var editDoneCondition by remember { mutableStateOf(target.doneCondition) }

        Dialog(onDismissRequest = { editingSuggestion = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShadowSurface)
                    .border(1.dp, BorderFaint, RoundedCornerShape(8.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isFa) "ویرایش مأموریت پیشنهادی" else "EDIT SUGGESTION",
                        fontFamily = JetBrainsMono,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen
                    )

                    TerminalTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = if (isFa) "عنوان مأموریت" else "TITLE",
                        modifier = Modifier.fillMaxWidth()
                    )

                    TerminalTextField(
                        value = editHours,
                        onValueChange = { editHours = it },
                        label = if (isFa) "تخمین زمان (ساعت)" else "ESTIMATED HOURS",
                        modifier = Modifier.fillMaxWidth()
                    )

                    TerminalTextField(
                        value = editDoneCondition,
                        onValueChange = { editDoneCondition = it },
                        label = if (isFa) "شرط اتمام فیزیکی" else "PHYSICAL DONE CONDITION",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { editingSuggestion = null }) {
                            Text("Cancel", color = TextDim, fontFamily = JetBrainsMono)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val hoursParsed = editHours.toFloatOrNull() ?: target.estimatedHours
                                viewModel.editSuggestion(target.id, editTitle, hoursParsed, editDoneCondition)
                                editingSuggestion = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SystemGreen, contentColor = VoidBlack),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Save", fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ── DIALOG 2: REJECT REASON PICKER ───────────────────────────────────────
    rejectingSuggestionId?.let { targetId ->
        Dialog(onDismissRequest = { rejectingSuggestionId = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShadowSurface)
                    .border(1.dp, BorderFaint, RoundedCornerShape(8.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isFa) "دلیل رد این پیشنهاد چیست؟" else "REJECTION FEEDBACK",
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PenaltyRed
                    )
                    Text(
                        text = if (isFa) "بازخورد شما به بهبود الگوریتم انتخاب الگو کمک می‌کند."
                        else "Your input refines template-first precision and avoids noise.",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextDim
                    )

                    XionRejectionReason.values().forEach { reason ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(DimSurface)
                                .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                                .clickable {
                                    viewModel.rejectSuggestion(targetId, reason)
                                    rejectingSuggestionId = null
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (isFa) reason.labelFa else reason.labelEn,
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // ── DIALOG 3: REPORT BOUNDARY VIOLATION ───────────────────────────────────
    reportingSuggestionId?.let { targetId ->
        Dialog(onDismissRequest = { reportingSuggestionId = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShadowSurface)
                    .border(1.dp, PenaltyRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isFa) "گزارش نقض خط‌مشی زایان" else "REPORT POLICY VIOLATION",
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PenaltyRed
                    )
                    Text(
                        text = if (isFa) "زایان مجاز به ارائه مشاوره درمانی، مالی یا ادعای دانای کل نیست."
                        else "Xion is strictly restricted from medical, therapy, financial, or infallible authority claims.",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextDim
                    )

                    XionReportCategory.values().forEach { cat ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(DimSurface)
                                .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                                .clickable {
                                    viewModel.reportSuggestion(targetId, cat)
                                    reportingSuggestionId = null
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (isFa) cat.labelFa else cat.labelEn,
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = PenaltyRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun XionSuggestionCard(
    suggestion: XionSuggestion,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onReject: () -> Unit,
    onReport: () -> Unit,
    isPersian: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) SystemGreen.copy(alpha = 0.08f) else DimSurface)
            .border(
                1.dp,
                if (isSelected) SystemGreen.copy(alpha = 0.6f) else BorderFaint,
                RoundedCornerShape(6.dp)
            )
            .clickable { onToggle() }
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RarityBadge(rarity = suggestion.rarity)
                    if (suggestion.isTemplateMatched) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(LegendaryGold.copy(alpha = 0.15f))
                                .border(1.dp, LegendaryGold.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ TEMPLATE",
                                fontFamily = JetBrainsMono,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LegendaryGold
                            )
                        }
                    }
                    if (suggestion.wasEdited) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(RareBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "EDITED",
                                fontFamily = JetBrainsMono,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = RareBlue
                            )
                        }
                    }
                }

                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SystemGreen,
                        uncheckedColor = BorderFaint
                    ),
                    modifier = Modifier.size(48.dp)
                )
            }

            // Title
            Text(
                text = suggestion.title,
                fontFamily = JetBrainsMono,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Metadata & Description
            Text(
                text = "[ ${suggestion.skillName} ] • EST. ${suggestion.estimatedHours}h",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = SystemGreen
            )

            Text(
                text = suggestion.description,
                fontFamily = Inter,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            // Physical Done Condition
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(VoidBlack)
                    .border(1.dp, BorderFaint, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (isPersian) "شرط اتمام فیزیکی:" else "PHYSICAL DONE CONDITION:",
                        fontFamily = JetBrainsMono,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen
                    )
                    Text(
                        text = suggestion.doneCondition,
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            }

            // Rationale
            Text(
                text = "➜ ${suggestion.reasoning}",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                color = TextDim
            )

            // Action Row: Edit, Reject, Report
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onReject,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Reject",
                        tint = TextDim,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onReport,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Report",
                        tint = PenaltyRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
