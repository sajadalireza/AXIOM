package com.axiom.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.SystemGreen

@Composable
fun ScreenHelpButton(
    stringResId: Int,
    modifier: Modifier = Modifier
) {
    var showHelp by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // The ? icon button — small, top-right corner of caller
        IconButton(
            onClick = { showHelp = true },
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "?",
                fontFamily = JetBrainsMono,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SystemGreen.copy(alpha = 0.7f)
            )
        }

        // Bottom sheet style popup using Dialog
        if (showHelp) {
            Dialog(onDismissRequest = { showHelp = false }) {
                GlossaryBriefingCard(
                    stringResId = stringResId,
                    onDismiss = { showHelp = false },
                    visible = true
                )
            }
        }
    }
}
