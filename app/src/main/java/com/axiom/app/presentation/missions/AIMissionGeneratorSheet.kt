package com.axiom.app.presentation.missions

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.domain.model.AIMissionSuggestion
import com.axiom.app.ui.AIMissionGeneratorViewModel
import com.axiom.app.ui.components.RarityBadge
import com.axiom.app.ui.components.TerminalTextField
import com.axiom.app.ui.theme.*
import com.axiom.app.R
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIMissionGeneratorSheet(
    onDismiss: () -> Unit,
    viewModel: AIMissionGeneratorViewModel = hiltViewModel()
) {
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val isLoading   by viewModel.isLoading.collectAsStateWithLifecycle()
    val hasApiKey   by viewModel.hasApiKey.collectAsStateWithLifecycle()
    val selected    by viewModel.selected.collectAsStateWithLifecycle()
    val totalXp     by viewModel.totalXp.collectAsStateWithLifecycle()
    val created     by viewModel.created.collectAsStateWithLifecycle()

    var goal by remember { mutableStateOf("") }

    // Dismiss automatically after missions are created
    LaunchedEffect(created) { if (created) { delay(800); onDismiss() } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ShadowSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── HEADER ────────────────────────────────────────
            Text(
                stringResource(R.string.ai_generate_sheet_title),
                fontFamily = JetBrainsMono,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SystemGreen
            )
            Text(
                stringResource(R.string.ai_generate_sheet_subtitle),
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = TextSecondary
            )

            // ── DECOUPLED INDEPENDENT WARNING ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(SystemGreen.copy(alpha = 0.08f))
                    .border(1.dp, SystemGreen.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.ai_offline_title),
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SystemGreen
                    )
                    Text(
                        text = stringResource(R.string.ai_offline_desc),
                        fontFamily = Inter,
                        fontSize = 11.sp,
                        color = SystemGreen.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    )
                }
            }

            // ── GOAL INPUT ────────────────────────────────────
            TerminalTextField(
                value = goal,
                onValueChange = { goal = it },
                label = stringResource(R.string.ai_generate_label_goal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                placeholder = {
                    Text(
                        stringResource(R.string.ai_generate_placeholder),
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextDim
                    )
                }
            )

            // ── GENERATE BUTTON ───────────────────────────────
            val canGenerate = goal.trim().length >= 3 && !isLoading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (canGenerate) SystemGreen.copy(alpha = 0.15f) else DimSurface
                    )
                    .border(
                        1.dp,
                        if (canGenerate) SystemGreen.copy(alpha = 0.5f) else BorderFaint,
                        RoundedCornerShape(4.dp)
                    )
                    .then(
                        if (canGenerate) Modifier.clickable { viewModel.generate(goal) }
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    BlinkingSystemText(stringResource(R.string.ai_generate_processing))
                } else {
                    Text(
                        stringResource(R.string.ai_generate_btn_missions),
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canGenerate) SystemGreen else TextDim
                    )
                }
            }

            // ── RESULTS ───────────────────────────────────────
            if (suggestions.isNotEmpty()) {
                Text(
                    stringResource(R.string.ai_generate_assigned),
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SystemGreen
                )
                suggestions.forEach { s ->
                    AIMissionCard(
                        suggestion = s,
                        isSelected = selected.contains(s.title),
                        onToggle   = { viewModel.toggleSelection(s) }
                    )
                }
            }

            // ── ACCEPT BUTTON ─────────────────────────────────
            if (selected.isNotEmpty()) {
                val xpText = if (totalXp > 0) "  +${totalXp} XP" else ""
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SystemGreen.copy(alpha = 0.15f))
                        .border(1.dp, SystemGreen, RoundedCornerShape(4.dp))
                        .clickable { viewModel.acceptSelected() },
                    contentAlignment = Alignment.Center
                ) {
                    if (created) {
                        Text(
                            stringResource(R.string.ai_generate_registered),
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SystemGreen
                        )
                    } else {
                        Text(
                            stringResource(R.string.ai_generate_accept) + xpText,
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SystemGreen
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AIMissionCard(
    suggestion: AIMissionSuggestion,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) SystemGreen.copy(alpha = 0.08f) else DimSurface)
            .border(
                1.dp,
                if (isSelected) SystemGreen.copy(alpha = 0.5f) else BorderFaint,
                RoundedCornerShape(4.dp)
            )
            .clickable { onToggle() }
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RarityBadge(rarity = suggestion.rarity)
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor   = SystemGreen,
                        uncheckedColor = BorderFaint
                    )
                )
            }
            Text(
                suggestion.title,
                fontFamily = JetBrainsMono,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "[ ${suggestion.skillName} ]",
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = SystemGreen
            )
            Text(
                "EST. ${suggestion.estimatedHours}h",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = TextDim
            )
            Text(
                suggestion.reasoning,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun BlinkingSystemText(text: String) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) { delay(500); visible = !visible }
    }
    if (visible) {
        Text(
            text,
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SystemGreen
        )
    }
}
