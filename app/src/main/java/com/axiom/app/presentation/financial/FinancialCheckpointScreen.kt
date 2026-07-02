package com.axiom.app.presentation.financial

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.R
import com.axiom.app.domain.model.FinancialCheckpoint
import com.axiom.app.domain.model.MonthlyIncomeEntry
import com.axiom.app.ui.theme.LocalAxiomColors
import com.axiom.app.ui.theme.JetBrainsMono
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialCheckpointScreen(
    onBack: () -> Unit,
    viewModel: FinancialCheckpointViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val axiomColors = LocalAxiomColors.current

    // Dialog state
    var selectedMonthForActual by remember { mutableStateOf<Int?>(null) }
    var selectedMonthForTarget by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.financial_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = axiomColors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("financial_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = axiomColors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = axiomColors.voidBlack
                )
            )
        },
        containerColor = axiomColors.voidBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(axiomColors.voidBlack)
        ) {
            when (val uiState = state) {
                is FinancialCheckpointUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = axiomColors.systemGreen,
                            modifier = Modifier.testTag("financial_loading")
                        )
                    }
                }
                is FinancialCheckpointUiState.Success -> {
                    val checkpoints = uiState.checkpoints
                    val actuals = uiState.actualEntries
                    val currency = uiState.currency

                    // Setup chart series
                    val targetEntries = (1..6).map { m ->
                        val checkpoint = checkpoints.find { it.monthIndex == m }
                        FloatEntry(m.toFloat(), checkpoint?.targetAmount ?: 0f)
                    }
                    val actualEntries = (1..6).map { m ->
                        val actual = actuals.find { it.monthIndex == m }
                        FloatEntry(m.toFloat(), actual?.actualAmount ?: 0f)
                    }

                    val chartModel = if (targetEntries.isNotEmpty() || actualEntries.isNotEmpty()) {
                        entryModelOf(targetEntries, actualEntries)
                    } else {
                        null
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title/Subtitle Hero header
                        item {
                            Column {
                                Text(
                                    text = "/ SYSTEM MONITOR /",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    color = axiomColors.legendaryGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.financial_subtitle),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = axiomColors.textSecondary
                                )
                            }
                        }

                        // Visualization - Compare Target vs Actuals
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, axiomColors.borderFaint, RoundedCornerShape(8.dp)),
                                color = axiomColors.shadowSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "◈ VISUAL RESONANCE MATRIX",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        color = axiomColors.systemGreen,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (chartModel != null) {
                                        Chart(
                                            chart = lineChart(),
                                            model = chartModel,
                                            startAxis = rememberStartAxis(),
                                            bottomAxis = rememberBottomAxis(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .testTag("financial_line_chart")
                                        )
                                    } else {
                                        Text(
                                            text = "[ NO PERFORMANCE MATRIX GENERATED ]",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 12.sp,
                                            color = axiomColors.textDim
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Metric legend
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(Color.Cyan, RoundedCornerShape(2.dp))
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = stringResource(R.string.financial_target_label),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = axiomColors.textSecondary
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(axiomColors.systemGreen, RoundedCornerShape(2.dp))
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = stringResource(R.string.financial_actual_label),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = axiomColors.textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Income Diagnostic (On Track vs Off Track, and Remediation alert)
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, axiomColors.borderFaint, RoundedCornerShape(8.dp)),
                                color = axiomColors.shadowSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "◈ " + stringResource(R.string.financial_income_diagnostic),
                                        fontFamily = JetBrainsMono,
                                        fontSize = 12.sp,
                                        color = axiomColors.legendaryGold,
                                        fontWeight = FontWeight.Bold
                                    )

                                    var hasOffTrack = false

                                    (1..6).forEach { m ->
                                        val targetAmt = checkpoints.find { it.monthIndex == m }?.targetAmount ?: 0f
                                        val actualAmt = actuals.find { it.monthIndex == m }?.actualAmount ?: 0f
                                        val isOnTrack = actualAmt >= targetAmt

                                        if (!isOnTrack && targetAmt > 0) {
                                            hasOffTrack = true
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(R.string.financial_month_label, m),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = axiomColors.textPrimary
                                            )

                                            if (isOnTrack) {
                                                Text(
                                                    text = stringResource(R.string.financial_diagnostic_on_track, m),
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = axiomColors.systemGreen
                                                )
                                            } else {
                                                Text(
                                                    text = stringResource(R.string.financial_diagnostic_off_track, m),
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.Red
                                                )
                                            }
                                        }
                                    }

                                    if (hasOffTrack) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Divider(color = axiomColors.borderFaint)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(R.string.financial_diagnostic_prompt_off_track),
                                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                                            color = axiomColors.legendaryGold,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Data Entry list: Month Cards
                        item {
                            Text(
                                text = "◈ MONTHLY TRACKING NODES",
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = axiomColors.textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items((1..6).toList()) { m ->
                            val checkpoint = checkpoints.find { it.monthIndex == m }
                            val actual = actuals.find { it.monthIndex == m }

                            val targetVal = checkpoint?.targetAmount ?: 0f
                            val actualVal = actual?.actualAmount ?: 0f

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, axiomColors.borderFaint, RoundedCornerShape(8.dp))
                                    .testTag("month_card_$m"),
                                color = axiomColors.shadowSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = stringResource(R.string.financial_month_label, m).uppercase(),
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = axiomColors.legendaryGold
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${stringResource(R.string.financial_target_label)}: $currency$targetVal",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = axiomColors.textSecondary
                                            )
                                            Text(
                                                text = "${stringResource(R.string.financial_actual_label)}: $currency$actualVal",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (actualVal >= targetVal) axiomColors.systemGreen else axiomColors.textPrimary
                                            )
                                        }
                                    }

                                    // Quick entry update actions
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Edit target target/currency button
                                        IconButton(
                                            onClick = { selectedMonthForTarget = m },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("edit_target_button_$m")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Target",
                                                tint = axiomColors.textSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        // Update actual income button
                                        Button(
                                            onClick = { selectedMonthForActual = m },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = axiomColors.systemGreen,
                                                contentColor = axiomColors.voidBlack
                                            ),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier
                                                .height(28.dp)
                                                .testTag("record_income_button_$m")
                                        ) {
                                            Text(
                                                text = "RECORD",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = JetBrainsMono
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Actual dialog entry input
                    selectedMonthForActual?.let { m ->
                        val existing = actuals.find { it.monthIndex == m }
                        var actText by remember { mutableStateOf(existing?.actualAmount?.toString() ?: "") }

                        Dialog(onDismissRequest = { selectedMonthForActual = null }) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .border(1.dp, axiomColors.borderFaint, RoundedCornerShape(8.dp)),
                                color = axiomColors.shadowSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.financial_update_actual),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = axiomColors.legendaryGold
                                    )

                                    OutlinedTextField(
                                        value = actText,
                                        onValueChange = { actText = it },
                                        label = { Text(stringResource(R.string.financial_actual_label)) },
                                        textStyle = MaterialTheme.typography.bodyLarge,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = axiomColors.systemGreen,
                                            unfocusedBorderColor = axiomColors.borderFaint,
                                            focusedLabelColor = axiomColors.systemGreen,
                                            unfocusedLabelColor = axiomColors.textSecondary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("actual_amount_input")
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        TextButton(
                                            onClick = { selectedMonthForActual = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("CANCEL", color = axiomColors.textSecondary)
                                        }

                                        Button(
                                            onClick = {
                                                val amt = actText.toFloatOrNull() ?: 0f
                                                viewModel.updateIncome(m, amt, existing)
                                                selectedMonthForActual = null
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = axiomColors.systemGreen,
                                                contentColor = axiomColors.voidBlack
                                            ),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("save_actual_button")
                                        ) {
                                            Text("SAVE", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Target / Currency dialog entry input
                    selectedMonthForTarget?.let { m ->
                        val existing = checkpoints.find { it.monthIndex == m }
                        var tarText by remember { mutableStateOf(existing?.targetAmount?.toString() ?: "") }
                        var curText by remember { mutableStateOf(existing?.currency ?: currency) }

                        Dialog(onDismissRequest = { selectedMonthForTarget = null }) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .border(1.dp, axiomColors.borderFaint, RoundedCornerShape(8.dp)),
                                color = axiomColors.shadowSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.financial_update_target),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = axiomColors.legendaryGold
                                    )

                                    OutlinedTextField(
                                        value = tarText,
                                        onValueChange = { tarText = it },
                                        label = { Text(stringResource(R.string.financial_target_label)) },
                                        textStyle = MaterialTheme.typography.bodyLarge,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = axiomColors.systemGreen,
                                            unfocusedBorderColor = axiomColors.borderFaint,
                                            focusedLabelColor = axiomColors.systemGreen,
                                            unfocusedLabelColor = axiomColors.textSecondary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("target_amount_input")
                                    )

                                    OutlinedTextField(
                                        value = curText,
                                        onValueChange = { curText = it },
                                        label = { Text(stringResource(R.string.financial_currency_label)) },
                                        textStyle = MaterialTheme.typography.bodyLarge,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = axiomColors.systemGreen,
                                            unfocusedBorderColor = axiomColors.borderFaint,
                                            focusedLabelColor = axiomColors.systemGreen,
                                            unfocusedLabelColor = axiomColors.textSecondary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("target_currency_input")
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        TextButton(
                                            onClick = { selectedMonthForTarget = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("CANCEL", color = axiomColors.textSecondary)
                                        }

                                        Button(
                                            onClick = {
                                                val amt = tarText.toFloatOrNull() ?: 0f
                                                viewModel.updateTarget(m, amt, curText, existing)
                                                selectedMonthForTarget = null
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = axiomColors.systemGreen,
                                                contentColor = axiomColors.voidBlack
                                            ),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("save_target_button")
                                        ) {
                                            Text("SAVE", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
