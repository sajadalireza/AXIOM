package com.axiom.app.ui.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiom.app.domain.model.Mission
import com.axiom.app.ui.theme.JetBrainsMono
import com.axiom.app.ui.theme.LocalAxiomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableMissionCard(
    mission: Mission,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val c = LocalAxiomColors.current
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Only ACTIVE missions are swipe-completable
    if (mission.status.uppercase() != "ACTIVE") {
        content()
        return
    }

    var hasShownHint by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (!hasShownHint) {
            // Briefly slide left 20dp and back over 200ms
            offsetX.animateTo(
                targetValue = -20f,
                animationSpec = tween(durationMillis = 100, easing = EaseOutQuad)
            )
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 100, easing = EaseInQuad)
            )
            hasShownHint = true
        }
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    val vibrator = context.getSystemService(Vibrator::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 30, 20), -1))
                    }
                    onComplete()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    showDeleteDialog = true
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { it * 0.35f }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val isComplete = direction == SwipeToDismissBoxValue.StartToEnd
            val isDelete = direction == SwipeToDismissBoxValue.EndToStart
            val progress = dismissState.progress
            val bgColor by animateColorAsState(
                targetValue = when {
                    isComplete -> c.systemGreen.copy(alpha = (progress * 1.5f).coerceIn(0f, 1f))
                    isDelete   -> c.penaltyRed.copy(alpha = (progress * 1.5f).coerceIn(0f, 1f))
                    else       -> Color.Transparent
                },
                label = "swipe_bg"
            )
            Box(
                modifier = Modifier.fillMaxSize().background(bgColor).padding(horizontal = 20.dp),
                contentAlignment = if (isComplete) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = if (isComplete) Icons.Default.Check else Icons.Default.Delete,
                    contentDescription = null,
                    tint = c.voidBlack,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        content = {
            Box(modifier = Modifier.offset(x = offsetX.value.dp)) {
                content()
            }
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = c.dimSurface,
            title = {
                Text("[ ABORT MISSION ]", color = c.penaltyRed, fontFamily = JetBrainsMono,
                     fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            },
            text = {
                Text("Mission \"${mission.title}\" will be permanently deleted.",
                     color = c.textSecondary, fontFamily = JetBrainsMono, fontSize = 12.sp)
            },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("[ CONFIRM ]", color = c.penaltyRed, fontFamily = JetBrainsMono)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("[ CANCEL ]", color = c.textDim, fontFamily = JetBrainsMono)
                }
            }
        )
    }
}
