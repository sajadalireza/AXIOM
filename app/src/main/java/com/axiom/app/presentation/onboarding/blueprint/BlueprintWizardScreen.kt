package com.axiom.app.presentation.onboarding.blueprint

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.axiom.app.domain.model.IronRule
import com.axiom.app.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueprintWizardScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BlueprintWizardViewModel = hiltViewModel()
) {
    val colors = LocalAxiomColors.current
    var currentStep by remember { mutableStateOf(1) }

    // State bindings using read-only collectAsState
    val domainState by viewModel.selectedDomain.collectAsState()
    val thesisState by viewModel.oneLineThesis.collectAsState()
    val selectedTracksState by viewModel.selectedTracks.collectAsState()
    val rulesState by viewModel.ironRules.collectAsState()

    val isSaving by viewModel.isSaving.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("blueprint_onboarding_screen")
            .background(colors.voidBlack),
        topBar = {
            Column(modifier = Modifier.background(colors.shadowSurface)) {
                TopAppBar(
                    title = {
                        Text(
                            text = "BLUEPRINT OS",
                            style = TitleL.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp, fontFamily = FiraCode),
                            color = colors.legendaryGold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.shadowSurface)
                )
                PhaseProgressHeader(step = currentStep, colors = colors)
            }
        },
        bottomBar = {
            Surface(color = colors.shadowSurface, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep -= 1 },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, colors.borderFaint),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("wizard_back_button")
                        ) {
                            Text("BACK", fontFamily = FiraCode, color = colors.textPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    if (currentStep == 2 || currentStep == 3) {
                        TextButton(
                            onClick = { currentStep += 1 },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("skip_for_now_button")
                        ) {
                            Text("SKIP FOR NOW", fontFamily = FiraCode, color = colors.textDim)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep < 3) {
                                currentStep += 1
                            } else {
                                viewModel.completeOnboarding(onOnboardingComplete)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen),
                        shape = RoundedCornerShape(6.dp),
                        enabled = !isSaving,
                        modifier = Modifier.weight(1.5f).height(48.dp).testTag("wizard_next_button")
                    ) {
                        Text(
                            text = if (currentStep == 3) "LAUNCH WAR ROOM" else "NEXT ▸",
                            fontFamily = FiraCode,
                            color = colors.voidBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colors.voidBlack)
        ) {
            when (currentStep) {
                1 -> Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("SELECT PRIMARY LIFE DOMAIN", style = TitleL, color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    listOf("career" to "💼 Career Development", "finance" to "🪙 Wealth & Finance", "health" to "❤️ Physical Health", "relationships" to "🤝 Social Alliances").forEach { (id, label) ->
                        val isSel = domainState == id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) colors.systemGreen.copy(alpha = 0.2f) else colors.shadowSurface)
                                .border(1.dp, if (isSel) colors.systemGreen else colors.borderFaint, RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateSelectedDomain(id) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, style = TitleM, color = if (isSel) colors.systemGreen else colors.textPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("CHOOSE A THESIS TEMPLATE — THIS BECOMES YOUR PERSONAL THESIS", style = LabelS, color = colors.textDim)
                    val templates = com.axiom.app.data.BlueprintV51Data.THESIS_TEMPLATES[domainState] ?: emptyList()
                    templates.forEach { template ->
                        val isSelectedThesis = thesisState == template
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.oneLineThesis.value = template
                                currentStep = 2
                            },
                            colors = CardDefaults.cardColors(containerColor = if (isSelectedThesis) colors.systemGreen.copy(alpha = 0.15f) else colors.shadowSurface),
                            border = BorderStroke(0.5.dp, if (isSelectedThesis) colors.systemGreen else colors.borderFaint)
                        ) {
                            Text(template, style = LabelS, color = colors.textPrimary, modifier = Modifier.padding(14.dp))
                        }
                    }
                }
                2 -> Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("YOUR BATTLEFIELD TRACKS", style = TitleL, color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text("Target execution fields to activate in your dashboard.", style = LabelS, color = colors.textDim, textAlign = TextAlign.Center)
                    listOf("career" to "💼 CAREER", "finance" to "🪙 FINANCE", "health" to "❤️ HEALTH", "relationships" to "🤝 RELATIONSHIPS").forEach { (id, label) ->
                        val isSel = selectedTracksState.contains(id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) colors.systemGreen.copy(alpha = 0.15f) else colors.shadowSurface)
                                .border(1.dp, if (isSel) colors.systemGreen else colors.borderFaint, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.selectedTracks.value = if (isSel) selectedTracksState - id else selectedTracksState + id
                                }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, style = TitleM, color = if (isSel) colors.systemGreen else colors.textPrimary)
                            Checkbox(checked = isSel, onCheckedChange = {
                                viewModel.selectedTracks.value = if (isSel) selectedTracksState - id else selectedTracksState + id
                            }, colors = CheckboxDefaults.colors(checkedColor = colors.systemGreen))
                        }
                    }
                }
                3 -> Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("YOUR IRON COMMITMENTS", style = TitleL, color = colors.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    rulesState.forEachIndexed { index, rule ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(colors.shadowSurface).border(0.5.dp, colors.borderFaint, RoundedCornerShape(8.dp)).padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${index + 1}. ${rule.ruleText}", style = LabelS, color = colors.textPrimary, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.ironRules.value = rulesState.filter { it.id != rule.id } }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = colors.penaltyRed)
                            }
                        }
                    }
                    var customRuleText by remember { mutableStateOf("") }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customRuleText,
                            onValueChange = { customRuleText = it },
                            label = { Text("NEW RULE", fontFamily = FiraCode, fontSize = 9.sp) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.systemGreen)
                        )
                        Button(
                            onClick = {
                                if (customRuleText.isNotBlank()) {
                                    viewModel.addIronRule(customRuleText)
                                    customRuleText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.systemGreen),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("+", color = colors.voidBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhaseProgressHeader(step: Int, colors: com.axiom.app.ui.theme.AxiomColorScheme) {
    val phaseName = when (step) {
        1 -> "WHO ARE YOU?"
        2 -> "YOUR BATTLEFIELD"
        else -> "YOUR IRON RULES"
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PHASE $step OF 3: $phaseName", style = LabelS.copy(fontWeight = FontWeight.Bold, fontFamily = FiraCode), color = colors.legendaryGold, modifier = Modifier.testTag("phase_label"))
            Text("STEP $step OF 3", style = LabelS.copy(fontFamily = FiraCode), color = colors.textDim)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { step / 3f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = colors.systemGreen,
            trackColor = colors.borderFaint
        )
    }
}
