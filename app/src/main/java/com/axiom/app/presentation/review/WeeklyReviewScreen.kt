package com.axiom.app.presentation.review

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import com.axiom.app.domain.review.ObstacleCategory
import com.axiom.app.domain.review.WeeklyReviewEngine
import com.axiom.app.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun WeeklyReviewScreen(
    onBack: () -> Unit,
    viewModel: WeeklyReviewViewModel = hiltViewModel()
) {
    val completedMissions by viewModel.completedMissionsThisWeek.collectAsStateWithLifecycle()
    val snapshot by viewModel.progressSnapshot.collectAsStateWithLifecycle()
    val summaryText = remember(completedMissions, snapshot) {
        viewModel.createEvidenceSummary(completedMissions, snapshot)
    }

    var step by remember { mutableIntStateOf(1) }

    // State placeholders for each step
    var selectedObstacleCategory by remember { mutableStateOf<ObstacleCategory?>(null) }
    var wrongAssumption by remember { mutableStateOf("") }
    var criticFeedback by remember { mutableStateOf("") }
    var selectedDecisionType by remember { mutableStateOf("Tactics (Change Now)") }
    var primaryOutcome by remember { mutableStateOf("") }
    var journalText by remember { mutableStateOf("") }
    var usefulnessRating by remember { mutableIntStateOf(5) }

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
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LegendaryGold)
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
                    1 -> "PROGRESS RECOGNITION"
                    2 -> "OBSTACLE IDENTIFICATION"
                    3 -> "RUTHLESS CRITIC PROMPT"
                    4 -> "NEXT-WEEK COMMITMENT"
                    5 -> "WRITE ONE PAGE"
                    6 -> "COMMIT & EVALUATE"
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
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text(
                                "Truth-grounded progress audit. Zero vanity metrics, zero unearned inflation:",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Structured Metrics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, BorderFaint.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                    colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.6f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("WMPU STATUS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            if (snapshot.wmpuAchieved) "ACHIEVED" else "PENDING",
                                            color = if (snapshot.wmpuAchieved) SystemGreen else LegendaryGold,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, BorderFaint.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                    colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.6f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("GOAL ACTIONS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "${snapshot.goalContributingMissions} / ${snapshot.totalMissionsCompleted}",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, BorderFaint.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                    colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.6f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("EFFECTIVE HRS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            String.format(Locale.US, "%.1fh", snapshot.totalEffectiveHours),
                                            color = TextPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, LegendaryGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = VoidBlack.copy(alpha = 0.4f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text(
                                "Identify Friction & Check Assumptions",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Failure is data, not shame. Select the primary friction pattern and document broken assumptions:",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Obstacle Category Selector Chips
                            Text(
                                "Friction Category:",
                                color = LegendaryGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ObstacleCategory.entries.forEach { category ->
                                    val isSelected = selectedObstacleCategory == category
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 48.dp)
                                            .border(
                                                1.dp,
                                                if (isSelected) LegendaryGold else BorderFaint.copy(alpha = 0.3f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .background(
                                                if (isSelected) LegendaryGold.copy(alpha = 0.15f) else VoidBlack.copy(alpha = 0.3f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                selectedObstacleCategory = if (isSelected) null else category
                                            }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            category.titleEn,
                                            color = if (isSelected) LegendaryGold else TextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            category.titleFa,
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "What assumption or hypothesis turned out wrong?",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = wrongAssumption,
                                onValueChange = { wrongAssumption = it },
                                placeholder = { Text("e.g. Underestimated API integration complexity; need smaller daily slices...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp)
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
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .testTag("copy_prompt_button")
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
                                    .heightIn(min = 120.dp)
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
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text(
                                "Decide What Changes & Next-Week Commitment",
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
                            Spacer(modifier = Modifier.height(20.dp))
                            Column(Modifier.selectableGroup()) {
                                decisionTypes.forEach { type ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 48.dp)
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
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Single Primary Next-Week Outcome:",
                                color = LegendaryGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = primaryOutcome,
                                onValueChange = { primaryOutcome = it },
                                placeholder = { Text("e.g. Deliver working auth flow before Wednesday noon") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("step4_primary_outcome"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LegendaryGold,
                                    unfocusedBorderColor = BorderFaint.copy(alpha = 0.5f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
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
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Ready",
                                tint = LegendaryGold,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "COMMIT & EVALUATE",
                                color = LegendaryGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Your evidence and commitments will be locked into the historical archives.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Usefulness Rating (1..5)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BorderFaint.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "How useful was this review in grounding your next cycle?",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        (1..5).forEach { star ->
                                            IconButton(
                                                onClick = { usefulnessRating = star },
                                                modifier = Modifier
                                                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                                                    .testTag("usefulness_rating_$star")
                                            ) {
                                                Icon(
                                                    imageVector = if (star <= usefulnessRating) Icons.Default.Star else Icons.Outlined.Star,
                                                    contentDescription = "Rating $star",
                                                    tint = if (star <= usefulnessRating) LegendaryGold else BorderFaint
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Anti-Inflation Award Notice
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SystemGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(containerColor = VoidBlack.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "⬡ Review Reward: +${WeeklyReviewEngine.FLAT_REVIEW_XP} XP (Flat, anti-inflationary)",
                                        color = SystemGreen,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("wizard_back_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, TextPrimary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (step > 1) "Previous" else "Cancel", fontWeight = FontWeight.Bold)
                }

                val canGoNext = when (step) {
                    2 -> wrongAssumption.isNotBlank() || selectedObstacleCategory != null
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
                            val committedDecision = if (primaryOutcome.isNotBlank()) {
                                "$selectedDecisionType - $primaryOutcome"
                            } else {
                                selectedDecisionType
                            }
                            viewModel.submitReview(
                                summary = summaryText,
                                wrongAssumption = wrongAssumption,
                                criticFeedback = criticFeedback,
                                decisionType = committedDecision,
                                journalText = journalText,
                                obstacleCategory = selectedObstacleCategory,
                                usefulnessRating = usefulnessRating,
                                onComplete = onBack
                            )
                        }
                    },
                    enabled = canGoNext,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("wizard_next_button"),
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
