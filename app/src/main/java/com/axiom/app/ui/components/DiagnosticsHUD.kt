package com.axiom.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.app.core.AppInitDiagnostics
import com.axiom.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DiagnosticsHUD(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val logs by AppInitDiagnostics.logs.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val configStatus by AppInitDiagnostics.supabaseConfigStatus.collectAsStateWithLifecycle()
    val clientStatus by AppInitDiagnostics.supabaseClientStatus.collectAsStateWithLifecycle()
    val authStatus by AppInitDiagnostics.userAuthStatus.collectAsStateWithLifecycle()
    val dbStatus by AppInitDiagnostics.dbFetchStatus.collectAsStateWithLifecycle()

    // Automatically scroll to the end of logs when new logs arrive
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(8.dp)
                .border(1.dp, SystemGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = VoidBlack
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "[ WARRIOR DIAGNOSTICS ]",
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = SystemGreen
                        )
                    }
                    IconButton(
                        onClick = onDismissRequest,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = TextDim
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Diagnostics"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phase Checklist HUD
                Text(
                    text = "INITIALIZATION SEQUENCE STATUS",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DimSurface, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusRow(title = "1. Supabase Configurations", status = configStatus)
                    StatusRow(title = "2. Supabase Gateway Client", status = clientStatus)
                    StatusRow(title = "3. User Preferences & Auth State", status = authStatus)
                    StatusRow(title = "4. Local SQLite DB Validation", status = dbStatus)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logs Terminal Area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TERMINAL LOG OUTPUT",
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    
                    Row {
                        // Copy Logs
                        TextButton(
                            onClick = {
                                val fullLogs = logs.joinToString("\n")
                                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Warrior Init Diagnostics Logs", fullLogs)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(context, "Diagnostics logs copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = SystemGreen)
                        ) {
                            Text("COPY", fontFamily = JetBrainsMono, fontSize = 11.sp)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    if (logs.isEmpty()) {
                        Text(
                            text = "No log output recorded yet.",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(logs) { logLine ->
                                val color = when {
                                    logLine.contains("[CRITICAL_") || logLine.contains("❌") -> PenaltyRed
                                    logLine.contains("⚠️") || logLine.contains("[WARN") -> LegendaryGold
                                    logLine.contains("✅") || logLine.contains("SUCCESS") -> SystemGreen
                                    logLine.contains("🚀") || logLine.contains("SYSTEM") -> Color(0xFF00E5FF)
                                    else -> TextDim
                                }
                                Text(
                                    text = logLine,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    color = color,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SystemGreen,
                            contentColor = VoidBlack
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Acknowledge",
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusRow(
    title: String,
    status: AppInitDiagnostics.StepStatus
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            color = TextDim
        )
        
        val (text, color) = when (status) {
            AppInitDiagnostics.StepStatus.PENDING -> "PENDING" to Color.LightGray
            AppInitDiagnostics.StepStatus.RUNNING -> "RUNNING..." to LegendaryGold
            AppInitDiagnostics.StepStatus.SUCCESS -> "SUCCESS 🟩" to SystemGreen
            AppInitDiagnostics.StepStatus.FAILED -> "FAILED ❌" to PenaltyRed
        }

        Text(
            text = text,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = color
        )
    }
}
