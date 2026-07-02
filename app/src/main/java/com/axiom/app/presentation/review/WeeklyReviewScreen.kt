package com.axiom.app.presentation.review

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.ui.theme.LegendaryGold
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.TextPrimary
import com.axiom.app.ui.theme.TextSecondary
import com.axiom.app.ui.theme.BorderFaint
import com.axiom.app.ui.theme.VoidBlack
import com.axiom.app.ui.theme.SystemGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun WeeklyReviewScreen(
    onBack: () -> Unit,
    viewModel: WeeklyReviewViewModel = hiltViewModel()
) {
    val completedMissions by viewModel.completedMissionsThisWeek.collectAsStateWithLifecycle()
    val summaryText = remember(completedMissions) {
        viewModel.createEvidenceSummary(completedMissions)
    }

    var step by remember { mutableStateOf(1) }

    // State placeholders for each step
    var wrongAssumption by remember { mutableStateOf("") }
    var criticFeedback by remember { mutableStateOf("") }
    var selectedDecisionType by remember { mutableStateOf("Tactics (Change Now)") }
    var journalText by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current
    var isCopiedNotificationShown by remember { mutableStateOf(false) }

    val decisionTypes = listOf(
        "Tactics (Change Now)",
        "Strategy (Need 2+ months evidence)",
        "Core thesis (Rare, only with overwhelming evidence)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "WEEKLY REVIEW RITUAL", 
                        color = LegendaryGold, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LegendaryGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // STEP NOTATOR / PROGRESS DOTS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..6).forEach { i ->
                    val isCurrentOrCompleted = i <= step
                    val color = if (isCurrentOrCompleted) LegendaryGold else BorderFaint.copy(alpha = 0.5f)
                    val weight = if (i == step) FontWeight.ExtraBold else FontWeight.Medium
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .height(4.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                }
            }
            
            Text(
                text = "STEP $step OF 6: " + when(step) {
                    1 -> "GATHER EVIDENCE"
                    2 -> "CHECK ASSUMPTIONS"
                    3 -> "RUTHLESS CRITIC PROMPT"
                    4 -> "DECIDE WHAT CHANGES"
                    5 -> "WRITE ONE PAGE"
                    6 -> "COMMIT"
                    else -> ""
                },
                color = LegendaryGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.5.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // ANIMATED CONTENT BODY BASED ON STEP
            Box(modifier = Modifier.weight(1f)) {
                when (step) {
                    1 -> {
                        Column {
                            Text(
                                "Tallying outputs, tracking disciplines, and auditing absolute metrics logged in the past 7 days:",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .border(1.dp, LegendaryGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = VoidBlack.copy(alpha = 0.4f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = summaryText,
                                        color = SystemGreen,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        Column {
                            Text(
                                "What assumption turned out wrong this week?",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Review your hypotheses and call out any biases or failed expectations.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = wrongAssumption,
                                onValueChange = { wrongAssumption = it },
                                placeholder = { Text("e.g. Assumed the client call would yield immediate signup, but they need 2 reviews...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .testTag("step2_wrong_assumption"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LegendaryGold,
                                    unfocusedBorderColor = BorderFaint.copy(alpha = 0.5f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                    3 -> {
                        val ruthlessPrompt = "Given this data:\n$summaryText\n\nwhat is the most important thing I am not facing right now, and what one thing should I change?"
                        Column {
                            Text(
                                "Run the diagnostic with your external AI system to strip away denial.",
                                color = TextPrimary,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .border(0.5.dp, BorderFaint.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Prompt text for external AI tool:",
                                        color = LegendaryGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = ruthlessPrompt,
                                        color = TextSecondary,
                                        maxLines = 4,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(ruthlessPrompt))
                                            isCopiedNotificationShown = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LegendaryGold,
                                            contentColor = VoidBlack
                                        ),
                                        modifier = Modifier.testTag("copy_prompt_button")
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Copy Prompt to Clipboard", fontWeight = FontWeight.Bold)
                                    }
                                    if (isCopiedNotificationShown) {
                                        Text(
                                            "Prompt successfully copied!",
                                            color = SystemGreen,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                "Paste back the response / critical verdict below:",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = criticFeedback,
                                onValueChange = { criticFeedback = it },
                                placeholder = { Text("Paste response here...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .testTag("step3_critic_feedback"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LegendaryGold,
                                    unfocusedBorderColor = BorderFaint.copy(alpha = 0.5f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                    4 -> {
                        Column {
                            Text(
                                "Decide What Level of Change is Needed:",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Be precise. Tactical tweaks are fast; thesis refactoring demands overwhelming evidence.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Column(Modifier.selectableGroup()) {
                                decisionTypes.forEach { type ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = (type == selectedDecisionType),
                                                onClick = { selectedDecisionType = type }
                                            )
                                            .border(
                                                1.dp,
                                                if (type == selectedDecisionType) LegendaryGold else BorderFaint.copy(alpha = 0.2f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = (type == selectedDecisionType),
                                            onClick = { selectedDecisionType = type },
                                            colors = RadioButtonDefaults.colors(selectedColor = LegendaryGold)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = type,
                                            color = if (type == selectedDecisionType) LegendaryGold else TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                    5 -> {
                        Column {
                            Text(
                                "Write One Page",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Journal freely about your discoveries, personal state, or upcoming trajectory.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = journalText,
                                onValueChange = { journalText = it },
                                placeholder = { Text("How has this week truly felt? What should change...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .testTag("step5_journal_text"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LegendaryGold,
                                    unfocusedBorderColor = BorderFaint.copy(alpha = 0.5f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                    6 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Ready",
                                    tint = LegendaryGold,
                                    modifier = Modifier.size(72.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "THE PROTOCOL IS COMPLETE",
                                    color = LegendaryGold,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "By committing, your outcomes and evidence are locked into your historical archives. Ready to enter next week with zero delusion?",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTTOM WIZARD CONTROLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (step > 1) {
                            step--
                            isCopiedNotificationShown = false
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.testTag("wizard_back_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, TextPrimary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (step > 1) "Previous" else "Cancel", fontWeight = FontWeight.Bold)
                }

                val canGoNext = when (step) {
                    2 -> wrongAssumption.isNotBlank()
                    3 -> criticFeedback.isNotBlank()
                    5 -> journalText.isNotBlank()
                    else -> true
                }

                Button(
                    onClick = {
                        if (step < 6) {
                            step++
                            isCopiedNotificationShown = false
                        } else {
                            viewModel.submitReview(
                                summary = summaryText,
                                wrongAssumption = wrongAssumption,
                                criticFeedback = criticFeedback,
                                decisionType = selectedDecisionType,
                                journalText = journalText,
                                onComplete = onBack
                            )
                        }
                    },
                    enabled = canGoNext,
                    modifier = Modifier.testTag("wizard_next_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LegendaryGold,
                        contentColor = VoidBlack,
                        disabledContainerColor = LegendaryGold.copy(alpha = 0.3f),
                        disabledContentColor = VoidBlack.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (step == 6) "Commit Protocol" else "Next", 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
