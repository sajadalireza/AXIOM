package com.axiom.app.presentation.decisionfilter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.ui.theme.LegendaryGold
import com.axiom.app.ui.theme.TextPrimary
import com.axiom.app.ui.theme.TextSecondary
import com.axiom.app.ui.theme.TextDim
import com.axiom.app.ui.theme.BorderFaint
import com.axiom.app.ui.theme.VoidBlack
import com.axiom.app.ui.theme.PenaltyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecisionFilterScreen(
    onBack: () -> Unit
) {
    var opportunityText by remember { mutableStateOf("") }
    
    val questions = listOf(
        "Does this directly advance my core 4-year thesis?",
        "Is the economic value > $5,000 OR does it have high compounding potential?",
        "Do I have the bandwidth to execute this without sacrificing existing commitments?",
        "Is this an absolute, 'hell yes'? (If it's a maybe, it is a no).",
        "Does this fit my personal integrity rules and prevent distraction?"
    )

    // Store answers; null = unanswered, true = YES, false = NO
    val answers = remember { mutableStateListOf<Boolean?>(null, null, null, null, null) }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "DECISION PROTOCOL",
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
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            Text(
                text = "FILTER INCOMING OPPORTUNITIES RUTHLESSLY",
                color = LegendaryGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Act as your own impartial analyst. Strip away excitement; analyze the raw criteria of alignment.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // OPPORTUNITY TEXT FIELD
            OutlinedTextField(
                value = opportunityText,
                onValueChange = { opportunityText = it },
                label = { Text("WHAT IS THE OPPORTUNITY / REQUEST?", color = LegendaryGold, fontWeight = FontWeight.Bold) },
                placeholder = { Text("e.g. Freelance project doing backend work, new partnership pitch...", color = TextDim) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("decision_opportunity_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = LegendaryGold,
                    unfocusedBorderColor = BorderFaint.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // THE 5 CRITICAL QUESTIONS
            questions.forEachIndexed { i, q ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, BorderFaint.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = VoidBlack.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "CRITERIA #${i + 1}",
                            color = LegendaryGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = q,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // YES BUTTON
                            val isYes = answers[i] == true
                            Button(
                                onClick = { answers[i] = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("q_${i}_yes_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isYes) LegendaryGold else BorderFaint.copy(alpha = 0.1f),
                                    contentColor = if (isYes) VoidBlack else TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("YES", fontWeight = FontWeight.Bold)
                            }

                            // NO BUTTON
                            val isNo = answers[i] == false
                            Button(
                                onClick = { answers[i] = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("q_${i}_no_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNo) PenaltyRed else BorderFaint.copy(alpha = 0.1f),
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("NO", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DIAGNOSTIC VERDICT AND REASONING
            val allAnswered = answers.none { it == null }
            if (opportunityText.isNotBlank() && allAnswered) {
                val totalYesCount = answers.count { it == true }
                val isApproved = totalYesCount == 5

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            if (isApproved) LegendaryGold else PenaltyRed,
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isApproved) LegendaryGold.copy(alpha = 0.05f) else PenaltyRed.copy(alpha = 0.05f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = if (isApproved) "Success" else "Caution",
                                tint = if (isApproved) LegendaryGold else PenaltyRed,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isApproved) "EXECUTE PROTOCOL APPROVED" else "DISTRUST PROTOCOL ACTIONED",
                                color = if (isApproved) LegendaryGold else PenaltyRed,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isApproved) {
                                "VERDICT: This is highly aligned and viable. Execute immediately with complete dedication."
                            } else {
                                "VERDICT: Extreme caution. Highly likely to be a distraction. Rejecting the opportunity is advised."
                            },
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        if (!isApproved) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "FAILED INTEGRITY ALIGNMENTS:",
                                color = PenaltyRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            answers.forEachIndexed { idx, ans ->
                                if (ans == false) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Failed",
                                            tint = PenaltyRed,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = questions[idx],
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Notice to prompt user to fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderFaint.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Input opportunity title and answer all 5 criteria to run decision intelligence diagnostic.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
